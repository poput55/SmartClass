package com.example.smartclass.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.smartclass.data.ActiveHomeworkPreview
import com.example.smartclass.data.Homework
import com.example.smartclass.data.StudentItem
import com.example.smartclass.data.TeacherSubject
import com.example.smartclass.util.AuthManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

private const val TAG = "TeacherViewModel"

/**
 * Состояние данных учителя
 */
sealed class TeacherState {
    object Loading : TeacherState()
    data class Success(
        val subjects: List<TeacherSubject> = emptyList(),
        val activeHomeworks: List<ActiveHomeworkPreview> = emptyList(),
        val students: List<StudentItem> = emptyList(),
        val stats: TeacherStats = TeacherStats()
    ) : TeacherState()
    data class Error(val message: String) : TeacherState()
}

/**
 * Статистика учителя
 */
data class TeacherStats(
    val totalStudents: Int = 0,
    val totalHomeworks: Int = 0,
    val totalQuizzes: Int = 0,
    val averageScore: Float = 0f,
    val overdueCount: Int = 0,
    val submittedOnTime: Int = 0,
    val lateSubmissions: Int = 0
)

class TeacherViewModel : ViewModel() {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val homeworkCollection = firestore.collection("homework")
    private val usersCollection = firestore.collection("users")

    private val _teacherState = MutableStateFlow<TeacherState>(TeacherState.Loading)
    val teacherState: StateFlow<TeacherState> = _teacherState.asStateFlow()

    // Все ученики (для отображения в списке)
    private val _allStudents = MutableStateFlow<List<StudentItem>>(emptyList())
    val allStudents: StateFlow<List<StudentItem>> = _allStudents.asStateFlow()

    private var homeworkListener: ListenerRegistration? = null

    init {
        loadTeacherData()
        setupRealtimeListener()
    }

    /**
     * Настройка прослушивания изменений в ДЗ в реальном времени
     */
    private fun setupRealtimeListener() {
        val teacherId = AuthManager.auth.currentUser?.uid ?: return
        
        homeworkListener = homeworkCollection
            .whereEqualTo("teacherId", teacherId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Ошибка listener: ${error.message}", error)
                    return@addSnapshotListener
                }
                
                // При изменении ДЗ - обновляем данные
                snapshot?.let {
                    Log.d(TAG, "ДЗ изменились, обновляем...")
                    viewModelScope.launch {
                        loadActiveHomeworks(teacherId)
                    }
                }
            }
    }

    /**
     * Загрузка данных учителя
     */
    private fun loadTeacherData() {
        viewModelScope.launch {
            _teacherState.value = TeacherState.Loading
            try {
                val teacherId = AuthManager.auth.currentUser?.uid ?: run {
                    _teacherState.value = TeacherState.Error("Пользователь не авторизован")
                    return@launch
                }

                // Загружаем всех учеников
                val allStudents = loadAllStudents()
                _allStudents.value = allStudents

                // Загружаем предметы (классы) учителя
                val subjects = loadTeacherSubjects(teacherId)

                // Загружаем активные ДЗ
                val activeHomeworks = loadActiveHomeworks(teacherId)

                // Загружаем учеников для классов учителя
                val students = loadStudents(subjects)

                // Считаем статистику
                val stats = calculateStats(subjects, activeHomeworks, students)

                _teacherState.value = TeacherState.Success(
                    subjects = subjects,
                    activeHomeworks = activeHomeworks,
                    students = students,
                    stats = stats
                )

                Log.d(TAG, "Данные учителя загружены: ${subjects.size} предметов, ${activeHomeworks.size} ДЗ, ${allStudents.size} учеников")
            } catch (e: Exception) {
                Log.e(TAG, "Ошибка загрузки данных: ${e.message}", e)
                _teacherState.value = TeacherState.Error(e.message ?: "Ошибка загрузки")
            }
        }
    }

    /**
     * Загрузка предметов учителя
     */
    private suspend fun loadTeacherSubjects(teacherId: String): List<TeacherSubject> {
        return try {
            // Получаем все ДЗ учителя
            val snapshot = homeworkCollection
                .whereEqualTo("teacherId", teacherId)
                .get()
                .await()

            val homeworks = snapshot.toObjects(Homework::class.java)
            
            // Загружаем всех учеников чтобы получить все классы
            val allStudents = loadAllStudents()
            
            // Получаем все уникальные классы из учеников
            val allGrades = allStudents.map { "${it.grade}" }.toSet()
            
            // Группируем ДЗ по классам
            val homeworksByGrade = homeworks.groupBy { "${it.grade}" }

            // Создаём список предметов для всех классов где есть ученики
            allGrades.map { grade ->
                val gradeHomeworks = homeworksByGrade[grade] ?: emptyList()
                val studentsInGrade = allStudents.count { "${it.grade}" == grade }
                val activeHomeworksCount = gradeHomeworks.count { !it.isCompleted }
                
                TeacherSubject(
                    id = grade,
                    name = "Алгебра",
                    grade = grade,
                    studentsCount = studentsInGrade,
                    activeHomeworks = activeHomeworksCount,
                    color = getGradeColor(grade.toIntOrNull() ?: 7)
                )
            }.sortedBy { it.grade }
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка загрузки предметов: ${e.message}", e)
            emptyList()
        }
    }

    /**
     * Загрузка активных ДЗ
     */
    private suspend fun loadActiveHomeworks(teacherId: String): List<ActiveHomeworkPreview> {
        return try {
            // Получаем все ДЗ учителя, кроме выполненных
            val snapshot = homeworkCollection
                .whereEqualTo("teacherId", teacherId)
                .get()
                .await()

            val homeworks = snapshot.toObjects(Homework::class.java)

            // Фильтруем только активные (не выполненные)
            val activeHomeworksList = homeworks.filter { !it.isCompleted }

            // Загружаем всех учеников для подсчёта
            val allStudents = loadAllStudents()

            val activeHomeworks = activeHomeworksList.map { hw ->
                // Считаем количество учеников в классе этого ДЗ
                val studentsInGrade = allStudents.count { "${it.grade}" == "${hw.grade}" }

                ActiveHomeworkPreview(
                    id = hw.id,
                    title = hw.title,
                    grade = "${hw.grade}",
                    dueDate = hw.dueDate,
                    submittedCount = hw.attempts.size,
                    totalCount = studentsInGrade,
                    isQuiz = hw.type == com.example.smartclass.data.HomeworkType.QUIZ
                )
            }.sortedBy { it.dueDate }
            
            // Обновляем состояние в реальном времени
            val currentState = _teacherState.value
            if (currentState is TeacherState.Success) {
                _teacherState.value = currentState.copy(
                    activeHomeworks = activeHomeworks
                )
            }
            
            activeHomeworks
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка загрузки ДЗ: ${e.message}", e)
            emptyList()
        }
    }

    /**
     * Загрузка всех учеников
     */
    private suspend fun loadAllStudents(): List<StudentItem> {
        return try {
            // Загружаем всех учеников
            val usersSnapshot = usersCollection
                .whereEqualTo("role", "STUDENT")
                .get()
                .await()

            // Загружаем все ДЗ для расчёта среднего балла
            val allHomeworks = homeworkCollection.get().await()
                .toObjects(Homework::class.java)

            usersSnapshot.documents.mapNotNull { doc ->
                val grade = doc.getLong("grade")?.toInt() ?: 7
                val studentId = doc.id

                // Считаем средний процент по всем попыткам ученика
                val attempts = allHomeworks
                    .filter { hw -> hw.attempts.any { it.studentId == studentId } }
                    .flatMap { it.attempts }
                    .filter { it.studentId == studentId }

                val averageScore = if (attempts.isNotEmpty()) {
                    attempts.map { it.percentage }.average().toFloat()
                } else 0f

                StudentItem(
                    id = doc.id,
                    name = doc.getString("fullName") ?: doc.getString("firstName") ?: "Без имени",
                    grade = "$grade",
                    email = doc.getString("email") ?: "",
                    averageScore = averageScore
                )
            }.sortedWith(compareBy({ it.grade }, { it.name }))
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка загрузки всех учеников: ${e.message}", e)
            emptyList()
        }
    }

    /**
     * Загрузка учеников
     */
    private suspend fun loadStudents(subjects: List<TeacherSubject>): List<StudentItem> {
        return try {
            // Получаем всех пользователей с ролью STUDENT
            val snapshot = usersCollection
                .whereEqualTo("role", "STUDENT")
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                val grade = doc.getLong("grade")?.toInt() ?: 7
                // Показываем учеников только тех классов, которые ведёт учитель
                if (subjects.any { it.grade == "$grade" }) {
                    StudentItem(
                        id = doc.id,
                        name = doc.getString("fullName") ?: doc.getString("firstName") ?: "Без имени",
                        grade = "$grade",
                        email = doc.getString("email") ?: "",
                        averageScore = 0f // TODO: рассчитать средний балл
                    )
                } else null
            }.sortedWith(compareBy({ it.grade }, { it.name }))
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка загрузки учеников: ${e.message}", e)
            emptyList()
        }
    }

    /**
     * Расчёт статистики
     */
    private fun calculateStats(
        subjects: List<TeacherSubject>,
        homeworks: List<ActiveHomeworkPreview>,
        students: List<StudentItem>
    ): TeacherStats {
        val now = System.currentTimeMillis()
        val overdue = homeworks.count { it.dueDate < now && it.submittedCount < it.totalCount }
        val onTime = homeworks.sumOf { it.submittedCount }
        val totalStudents = students.size

        // Считаем просроченные сдачи (сдано после дедлайна)
        val lateSubmissions = homeworks
            .filter { it.dueDate < now }
            .sumOf { it.submittedCount }

        return TeacherStats(
            totalStudents = totalStudents,
            totalHomeworks = homeworks.size,
            totalQuizzes = homeworks.count { it.isQuiz },
            averageScore = students.map { it.averageScore }.average().toFloat(),
            overdueCount = overdue,
            submittedOnTime = onTime,
            lateSubmissions = lateSubmissions
        )
    }

    /**
     * Получить цвет для класса
     */
    private fun getGradeColor(grade: Int): String {
        return when (grade) {
            7 -> "#FF6B6B"
            8 -> "#4ECDC4"
            9 -> "#45B7D1"
            10 -> "#96CEB4"
            11 -> "#FFEAA7"
            else -> "#667eea"
        }
    }

    /**
     * Обновить данные
     */
    fun refresh() {
        loadTeacherData()
    }

    /**
     * Получить учеников для конкретного класса
     */
    fun getStudentsForGrade(grade: String): List<StudentItem> {
        return when (val state = _teacherState.value) {
            is TeacherState.Success -> state.students.filter { it.grade == grade }
            else -> emptyList()
        }
    }

    override fun onCleared() {
        super.onCleared()
        homeworkListener?.remove()
    }

    /**
     * Factory для создания ViewModel
     */
    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(TeacherViewModel::class.java)) {
                return TeacherViewModel() as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
