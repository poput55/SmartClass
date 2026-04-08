package com.example.smartclass.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.smartclass.data.Homework
import com.example.smartclass.data.HomeworkType
import com.example.smartclass.data.QuizQuestion
import com.example.smartclass.data.StudentAttempt
import com.example.smartclass.util.AuthManager
import com.example.smartclass.util.UserRole
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

private const val TAG = "HomeworkViewModel"

sealed class HomeworkState {
    object Loading : HomeworkState()
    data class Success(val homeworkList: List<Homework>) : HomeworkState()
    data class Error(val message: String) : HomeworkState()
}

class HomeworkViewModel : ViewModel() {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val homeworkCollection = firestore.collection("homework")

    private val _homeworkState = MutableStateFlow<HomeworkState>(HomeworkState.Loading)
    val homeworkState: StateFlow<HomeworkState> = _homeworkState.asStateFlow()

    private val _userRole = MutableStateFlow<UserRole?>(null)
    val userRole: StateFlow<UserRole?> = _userRole.asStateFlow()

    private val _userGrade = MutableStateFlow<Int?>(null)
    val userGrade: StateFlow<Int?> = _userGrade.asStateFlow()

    init {
        loadUserData()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            val role = AuthManager.getCurrentUserRole()
            val grade = AuthManager.getCurrentUserGrade()
            _userRole.value = role
            _userGrade.value = grade
            Log.d(TAG, "Роль пользователя: $role, Класс: $grade")

            // Загружаем ДЗ в зависимости от роли
            role?.let { loadHomework(it, grade) }
        }
    }

    private fun loadHomework(role: UserRole, userGrade: Int?) {
        viewModelScope.launch {
            _homeworkState.value = HomeworkState.Loading
            try {
                val userId = AuthManager.auth.currentUser?.uid ?: return@launch
                val snapshots = when (role) {
                    UserRole.TEACHER -> {
                        // Учитель видит свои созданные ДЗ
                        homeworkCollection.whereEqualTo("teacherId", userId)
                            .get().await()
                    }
                    UserRole.STUDENT -> {
                        // Ученик видит все ДЗ для его класса
                        homeworkCollection.whereEqualTo("grade", userGrade ?: 7)
                            .get().await()
                    }
                }

                val homeworkList = snapshots.toObjects(Homework::class.java)
                _homeworkState.value = HomeworkState.Success(homeworkList)
                Log.d(TAG, "Загружено ДЗ: ${homeworkList.size}")
            } catch (e: Exception) {
                Log.e(TAG, "Ошибка загрузки ДЗ: ${e.message}", e)
                _homeworkState.value = HomeworkState.Error(e.message ?: "Ошибка загрузки")
            }
        }
    }

    fun createHomework(
        title: String,
        description: String,
        grade: Int,
        topic: String,
        dueDate: Long,
        type: HomeworkType = HomeworkType.TEXT,
        questions: List<QuizQuestion> = emptyList()
    ) {
        viewModelScope.launch {
            try {
                val userId = AuthManager.auth.currentUser?.uid ?: return@launch
                val teacherName = AuthManager.getCurrentUserName()

                val homework = Homework(
                    title = title,
                    description = description,
                    grade = grade,
                    topic = topic,
                    dueDate = dueDate,
                    teacherId = userId,
                    teacherName = teacherName,
                    studentGrade = grade,
                    type = type,
                    questions = questions
                )

                // Создаём документ и получаем его ID
                val documentRef = homeworkCollection.document()
                val homeworkWithId = homework.copy(id = documentRef.id)
                documentRef.set(homeworkWithId).await()

                Log.d(TAG, "ДЗ создано: $title с id=${documentRef.id}")

                // Обновляем список
                loadHomework(UserRole.TEACHER, grade)
            } catch (e: Exception) {
                Log.e(TAG, "Ошибка создания ДЗ: ${e.message}", e)
            }
        }
    }

    /**
     * Отправка результата теста учеником
     */
    fun submitTestResult(homeworkId: String, attempt: StudentAttempt) {
        viewModelScope.launch {
            try {
                val homeworkDoc = homeworkCollection.document(homeworkId)
                val homeworkSnapshot = homeworkDoc.get().await()
                val homework = homeworkSnapshot.toObject(Homework::class.java) ?: return@launch

                // Обновляем список попыток
                val updatedAttempts = homework.attempts.toMutableList().apply {
                    // Удаляем предыдущую попытку этого ученика, если есть
                    removeAll { it.studentId == attempt.studentId }
                    add(attempt)
                }

                // Определяем, выполнено ли ДЗ (70%+ правильных ответов)
                val isCompleted = attempt.percentage >= 70f

                homeworkDoc.update(
                    mapOf(
                        "attempts" to updatedAttempts,
                        "isCompleted" to isCompleted
                    )
                ).await()

                Log.d(TAG, "Результат теста отправлен: ${attempt.score}/${attempt.totalQuestions}")

                // Обновляем локальный список
                refresh()
            } catch (e: Exception) {
                Log.e(TAG, "Ошибка отправки результата: ${e.message}", e)
            }
        }
    }

    /**
     * Отметка ДЗ как выполненное (для текстовых заданий)
     */
    fun markAsCompleted(homeworkId: String, completed: Boolean) {
        viewModelScope.launch {
            try {
                homeworkCollection.document(homeworkId)
                    .update("isCompleted", completed)
                    .await()
                Log.d(TAG, "ДЗ отмечено как ${if (completed) "выполненное" else "невыполненное"}: $homeworkId")
                refresh()
            } catch (e: Exception) {
                Log.e(TAG, "Ошибка обновления статуса: ${e.message}", e)
            }
        }
    }

    fun deleteHomework(homeworkId: String) {
        viewModelScope.launch {
            try {
                homeworkCollection.document(homeworkId).delete().await()
                Log.d(TAG, "ДЗ удалено: $homeworkId")

                val role = _userRole.value ?: return@launch
                val grade = _userGrade.value ?: return@launch
                loadHomework(role, grade)
            } catch (e: Exception) {
                Log.e(TAG, "Ошибка удаления ДЗ: ${e.message}", e)
            }
        }
    }

    fun refresh() {
        val role = _userRole.value ?: return
        val grade = _userGrade.value ?: return
        loadHomework(role, grade)
    }

    // Стандартный Factory для создания ViewModel без параметров
    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HomeworkViewModel::class.java)) {
                return HomeworkViewModel() as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
