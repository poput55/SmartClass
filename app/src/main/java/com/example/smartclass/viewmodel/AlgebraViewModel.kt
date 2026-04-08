package com.example.smartclass.viewmodel

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.smartclass.data.*

open class AlgebraViewModel(
    private val savedStateHandle: SavedStateHandle,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    companion object {
        private const val KEY_SELECTED_GRADE = "selected_grade"
        private const val KEY_CURRENT_TOPIC_ID = "current_topic_id"
        private const val KEY_CURRENT_LESSON_INDEX = "current_lesson_index"
        private const val KEY_QUIZ_QUESTIONS = "quiz_questions"
        private const val KEY_CURRENT_QUESTION_INDEX = "current_question_index"
        private const val KEY_SELECTED_ANSWER = "selected_answer"
        private const val KEY_SHOW_ANSWER = "show_answer"
        private const val KEY_CORRECT_ANSWERS = "correct_answers"
        private const val KEY_QUIZ_COMPLETED = "quiz_completed"
        private const val KEY_COMPLETED_TOPICS = "completed_topics"
        private const val KEY_COMPLETED_LESSONS = "completed_lessons"
    }

    var selectedGrade by mutableStateOf(sharedPreferences.getInt(KEY_SELECTED_GRADE, 7))
        private set

    // Current topic being viewed - persisted in SavedStateHandle
    var currentTopic by mutableStateOf<Topic?>(
        savedStateHandle.get<Int>(KEY_CURRENT_TOPIC_ID)?.let { topicId ->
            AlgebraTopics.getTopicById(topicId)
        }
    )
        private set

    // Current lesson index - persisted in SavedStateHandle
    var currentLessonIndex by mutableStateOf(savedStateHandle.get<Int>(KEY_CURRENT_LESSON_INDEX) ?: 0)
        private set

    // Quiz state - persisted in SavedStateHandle
    var quizQuestions by mutableStateOf<List<Question>>(
        savedStateHandle.get<List<Int>>(KEY_QUIZ_QUESTIONS)?.mapNotNull { questionId ->
            Questions.questions.find { it.id == questionId }
        } ?: emptyList()
    )
        private set

    var currentQuestionIndex by mutableStateOf(savedStateHandle.get<Int>(KEY_CURRENT_QUESTION_INDEX) ?: 0)
        private set

    var selectedAnswer by mutableStateOf<Int?>(savedStateHandle.get<Int>(KEY_SELECTED_ANSWER))
        private set

    var showAnswer by mutableStateOf(savedStateHandle.get<Boolean>(KEY_SHOW_ANSWER) ?: false)
        private set

    var correctAnswers by mutableStateOf(savedStateHandle.get<Int>(KEY_CORRECT_ANSWERS) ?: 0)
        private set

    var quizCompleted by mutableStateOf(savedStateHandle.get<Boolean>(KEY_QUIZ_COMPLETED) ?: false)
        private set

    // Progress tracking - persisted in SharedPreferences
    var completedTopics by mutableStateOf<Set<Int>>(
        sharedPreferences.getStringSet(KEY_COMPLETED_TOPICS, emptySet())?.map { it.toInt() }?.toSet() ?: emptySet()
    )
        private set

    var completedLessons by mutableStateOf<Map<Int, Set<Int>>>(
        sharedPreferences.getString(KEY_COMPLETED_LESSONS, "{}")?.let { json ->
            try {
                parseJsonToMap(json)
            } catch (e: Exception) {
                println("ERROR parsing lessons JSON: $e")
                emptyMap()
            }
        } ?: emptyMap()
    )
        private set

    // Functions
    fun selectGrade(grade: Int) {
        selectedGrade = grade
        sharedPreferences.edit().putInt(KEY_SELECTED_GRADE, grade).apply()
    }

    fun selectTopic(topic: Topic) {
        currentTopic = topic
        currentLessonIndex = 0
    }

    fun nextLesson() {
        currentTopic?.let { topic ->
            if (currentLessonIndex < topic.lessons.size - 1) {
                val currentLesson = topic.lessons[currentLessonIndex]
                markLessonComplete(topic.id, currentLesson.id)

                currentLessonIndex++
            }
        }
    }

    fun previousLesson() {
        if (currentLessonIndex > 0) {
            currentLessonIndex--
        }
    }

    fun completeCurrentLesson() {
        currentTopic?.let { topic ->
            val currentLesson = topic.lessons.getOrNull(currentLessonIndex)
            currentLesson?.let { lesson ->
                markLessonComplete(topic.id, lesson.id)
            }
        }
    }

    fun markLessonComplete(topicId: Int, lessonId: Int) {
        // DEBUG: Добавим логирование
        println("DEBUG: markLessonComplete called with topicId=$topicId, lessonId=$lessonId")

        // Получаем текущие завершенные уроки для этой темы
        val currentLessons = completedLessons[topicId]?.toMutableSet() ?: mutableSetOf()
        println("DEBUG: Current lessons for topic $topicId: $currentLessons")

        // Добавляем новый урок
        currentLessons.add(lessonId)
        println("DEBUG: Added lesson $lessonId. Now lessons: $currentLessons")

        // Обновляем completedLessons
        val newCompletedLessons = completedLessons.toMutableMap()
        newCompletedLessons[topicId] = currentLessons
        completedLessons = newCompletedLessons
        println("DEBUG: Updated completedLessons: $newCompletedLessons")

        // Получаем тему чтобы проверить сколько всего уроков
        val topic = AlgebraTopics.getTopicById(topicId)
        println("DEBUG: Found topic: ${topic?.title}")
        println("DEBUG: Topic lessons count: ${topic?.lessons?.size}")

        // Проверяем завершены ли все уроки
        if (topic != null && currentLessons.size == topic.lessons.size) {
            println("DEBUG: All lessons completed for topic $topicId!")

            // Добавляем тему в завершенные
            val newCompletedTopics = completedTopics.toMutableSet()
            newCompletedTopics.add(topicId)
            completedTopics = newCompletedTopics

            println("DEBUG: Added to completedTopics. Now: $newCompletedTopics")

            // Сохраняем в SharedPreferences
            sharedPreferences.edit().putStringSet(
                KEY_COMPLETED_TOPICS,
                newCompletedTopics.map { it.toString() }.toSet()
            ).apply()
        } else {
            println("DEBUG: Not all lessons completed. Current: ${currentLessons.size}, Total: ${topic?.lessons?.size}")
        }

        // Сохраняем завершенные уроки в более надежном формате
        val lessonsJson = buildJsonString(newCompletedLessons)

        sharedPreferences.edit().putString(KEY_COMPLETED_LESSONS, lessonsJson).apply()
        println("DEBUG: Saved lessons JSON: $lessonsJson")
    }

    // Вспомогательная функция для построения JSON строки
    private fun buildJsonString(map: Map<Int, Set<Int>>): String {
        return map.entries.joinToString(", ") { (key, value) ->
            "\"$key\":[${value.joinToString(",") { it.toString() }}]"
        }.let { "{$it}" }
    }

    // Также обновите загрузку completedLessons для нового формата JSON

    private fun parseJsonToMap(json: String): Map<Int, Set<Int>> {
        val result = mutableMapOf<Int, Set<Int>>()

        // Удаляем фигурные скобки
        val content = json.removeSurrounding("{", "}")
        if (content.isBlank()) return emptyMap()

        // Разбираем по запятым на уровне верхнего объекта
        val entries = content.split("\",")

        entries.forEach { entry ->
            // Добавляем обратно кавычку если была удалена
            val fixedEntry = if (!entry.endsWith("\"")) "$entry\"" else entry

            // Ищем двоеточие для разделения ключа и значения
            val colonIndex = fixedEntry.indexOf(':')
            if (colonIndex != -1) {
                val keyStr = fixedEntry.substring(0, colonIndex).trim().removeSurrounding("\"")
                val valueStr = fixedEntry.substring(colonIndex + 1).trim()

                try {
                    val key = keyStr.toInt()

                    // Обрабатываем значение как массив
                    val values = valueStr.removeSurrounding("[", "]")
                        .split(",")
                        .filter { it.isNotBlank() }
                        .map { it.trim().toInt() }
                        .toSet()

                    result[key] = values
                } catch (e: Exception) {
                    println("ERROR parsing entry: key='$keyStr', value='$valueStr'")
                }
            }
        }

        return result
    }

    fun startQuiz(topicId: Int? = null) {
        quizQuestions = if (topicId != null) {
            Questions.getQuestionsByTopic(topicId).shuffled().take(5)
        } else {
            Questions.getRandomQuestions(10)
        }
        currentQuestionIndex = 0
        selectedAnswer = null
        showAnswer = false
        correctAnswers = 0
        quizCompleted = false
    }

    fun selectAnswer(answerIndex: Int) {
        if (!showAnswer) {
            selectedAnswer = answerIndex
        }
    }

    fun checkAnswer() {
        showAnswer = true
        if (selectedAnswer == quizQuestions[currentQuestionIndex].correctAnswer) {
            correctAnswers++
        }
    }

    fun nextQuestion() {
        if (currentQuestionIndex < quizQuestions.size - 1) {
            currentQuestionIndex++
            selectedAnswer = null
            showAnswer = false
        } else {
            quizCompleted = true
        }
    }

    fun resetQuiz() {
        quizQuestions = emptyList()
        currentQuestionIndex = 0
        selectedAnswer = null
        showAnswer = false
        correctAnswers = 0
        quizCompleted = false
    }

    fun getTopicsForCurrentGrade(): List<Topic> {
        return AlgebraTopics.getTopicsByGrade(selectedGrade)
    }

    fun getFormulasForCurrentGrade(): List<Formula> {
        return Formulas.getFormulasByGrade(selectedGrade)
    }

    fun getProgress(): Float {
        val totalTopics = AlgebraTopics.topics.size
        return if (totalTopics > 0) {
            completedTopics.size.toFloat() / totalTopics
        } else 0f
    }

    fun getGradeProgress(grade: Int): Float {
        val gradeTopics = AlgebraTopics.getTopicsByGrade(grade)
        val completedGradeTopics = gradeTopics.count { it.id in completedTopics }
        return if (gradeTopics.isNotEmpty()) {
            completedGradeTopics.toFloat() / gradeTopics.size
        } else 0f
    }

    class Factory(private val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AlgebraViewModel::class.java)) {
                val sharedPreferences = context.getSharedPreferences("algebra_app_prefs", Context.MODE_PRIVATE)
                @Suppress("UNCHECKED_CAST")
                return AlgebraViewModel(SavedStateHandle(), sharedPreferences) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
    // Добавьте эту функцию для проверки завершенности урока
    fun isLessonCompleted(topicId: Int, lessonId: Int): Boolean {
        return completedLessons[topicId]?.contains(lessonId) ?: false
    }

    // Также добавьте функцию для получения информации о теме
    fun getTopicInfo(topicId: Int): String {
        val topic = AlgebraTopics.getTopicById(topicId)
        return if (topic != null) {
            "Topic: ${topic.title}, Lessons: ${topic.lessons.size}, " +
                    "Completed: ${completedLessons[topicId]?.size ?: 0}"
        } else {
            "Topic not found: $topicId"
        }
    }
}
