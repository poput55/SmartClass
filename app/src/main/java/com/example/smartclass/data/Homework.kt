package com.example.smartclass.data

import com.google.firebase.firestore.ServerTimestamp
import java.io.Serializable
import java.util.Date

/**
 * Тип домашнего задания
 */
enum class HomeworkType {
    TEXT,      // Текстовое описание
    QUIZ       // Тест с вопросами
}

/**
 * Результат прохождения теста учеником
 */
data class StudentAttempt(
    val studentId: String = "",
    val studentName: String = "",
    val answers: List<Int> = emptyList(),        // Индексы выбранных ответов
    val score: Int = 0,                          // Количество правильных ответов
    val totalQuestions: Int = 0,                 // Всего вопросов
    val completedAt: Long = 0L,                  // Время завершения
    val percentage: Float = 0f                   // Процент правильных ответов
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}

/**
 * Домашнее задание
 */
data class Homework(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val grade: Int = 7,                          // Класс, для которого предназначено ДЗ
    val topic: String = "",
    val dueDate: Long = 0L,
    val teacherId: String = "",
    val teacherName: String = "",
    @ServerTimestamp
    val createdAt: Date? = null,
    val isCompleted: Boolean = false,
    val studentGrade: Int = 0,                   // Класс ученика (для фильтрации ДЗ у учеников)
    val type: HomeworkType = HomeworkType.TEXT,  // Тип ДЗ
    val questions: List<QuizQuestion> = emptyList(), // Вопросы для теста
    val attempts: List<StudentAttempt> = emptyList() // Попытки учеников
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
