package com.example.smartclass.data

/**
 * Предмет/класс учителя
 */
data class TeacherSubject(
    val id: String = "",
    val name: String = "",           // Название предмета
    val grade: String = "",          // Класс (например, "7А", "8Б")
    val studentsCount: Int = 0,      // Количество учеников
    val activeHomeworks: Int = 0,    // Активные ДЗ
    val color: String = ""           // Цвет для UI (hex)
)

/**
 * Активное домашнее задание для превью
 */
data class ActiveHomeworkPreview(
    val id: String = "",
    val title: String = "",
    val grade: String = "",          // Класс
    val dueDate: Long = 0L,          // Срок сдачи
    val submittedCount: Int = 0,     // Сколько сдали
    val totalCount: Int = 0,         // Всего учеников
    val isQuiz: Boolean = false      // Тест ли это
)

/**
 * Уведомление для учителя
 */
data class TeacherNotification(
    val id: String = "",
    val type: NotificationType = NotificationType.INFO,
    val title: String = "",
    val message: String = "",
    val timestamp: Long = 0L,
    val isRead: Boolean = false
)

enum class NotificationType {
    INFO,       // Информация
    SUCCESS,    // Успех (кто-то сдал)
    WARNING,    // Предупреждение (просрочено)
    ERROR       // Ошибка
}

/**
 * Ученик
 */
data class StudentItem(
    val id: String = "",
    val name: String = "",
    val grade: String = "",
    val email: String = "",
    val averageScore: Float = 0f
)
