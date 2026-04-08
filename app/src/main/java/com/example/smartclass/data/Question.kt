package com.example.smartclass.data

import java.io.Serializable

/**
 * Вопрос для теста в домашнем задании
 */
data class QuizQuestion(
    val id: String = "",
    val text: String = "",                    // Текст вопроса
    val options: List<String> = emptyList(), // Варианты ответов (4 варианта)
    val correctAnswer: Int = 0,               // Индекс правильного ответа (0-3)
    val explanation: String = ""              // Объяснение правильного ответа
) : Serializable {
    companion object {
        private const val serialVersionUID = 1L
    }
}
