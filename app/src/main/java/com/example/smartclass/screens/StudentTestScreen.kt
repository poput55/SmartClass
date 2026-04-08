package com.example.smartclass.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.smartclass.data.Homework
import com.example.smartclass.data.HomeworkType
import com.example.smartclass.data.QuizQuestion
import com.example.smartclass.data.StudentAttempt
import com.example.smartclass.ui.theme.PrimaryBlue
import com.example.smartclass.ui.theme.SmartClassTheme
import com.example.smartclass.util.AuthManager
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentTestScreen(
    homework: Homework,
    onNavigateBack: () -> Unit,
    onSubmitResult: (StudentAttempt) -> Unit
) {
    val context = LocalContext.current
    var currentQuestionIndex by remember { mutableStateOf(0) }
    var selectedAnswers by remember { mutableStateOf(listOf<Int?>()) }
    var showResults by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    
    val questions = homework.questions
    val totalQuestions = questions.size
    
    // Инициализация выбранных ответов
    if (selectedAnswers.isEmpty() && totalQuestions > 0) {
        selectedAnswers = List(totalQuestions) { null }
    }
    
    // Подсчёт результатов
    fun calculateResults(): StudentAttempt {
        val answers = selectedAnswers.map { it ?: 0 }
        var correctCount = 0
        questions.forEachIndexed { index, question ->
            if (answers[index] == question.correctAnswer) {
                correctCount++
            }
        }
        val percentage = if (totalQuestions > 0) (correctCount.toFloat() / totalQuestions) * 100 else 0f
        
        return StudentAttempt(
            studentId = AuthManager.auth.currentUser?.uid ?: "",
            studentName = AuthManager.auth.currentUser?.email ?: "Ученик",
            answers = answers,
            score = correctCount,
            totalQuestions = totalQuestions,
            completedAt = System.currentTimeMillis(),
            percentage = percentage
        )
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Column {
                        Text("Тест", fontWeight = FontWeight.Bold)
                        Text(
                            text = "${currentQuestionIndex + 1} из $totalQuestions",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { showDialog = true }) {
                        Icon(
                            imageVector = Icons.Outlined.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryBlue
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Индикатор прогресса
            LinearProgressIndicator(
                progress = (currentQuestionIndex + 1).toFloat() / totalQuestions,
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                trackColor = PrimaryBlue.copy(alpha = 0.3f)
            )
            
            if (!showResults) {
                // Экран с вопросами
                val currentQuestion = questions[currentQuestionIndex]
                
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Карточка вопроса
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp)
                        ) {
                            Text(
                                text = currentQuestion.text,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                            
                            if (currentQuestion.explanation.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    color = PrimaryBlue.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(12.dp),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Info,
                                            contentDescription = null,
                                            tint = PrimaryBlue,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Text(
                                            text = currentQuestion.explanation,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = PrimaryBlue
                                        )
                                    }
                                }
                            }
                        }
                    }
                    
                    // Варианты ответов
                    Text(
                        text = "Выберите правильный ответ:",
                        fontWeight = FontWeight.Medium,
                        color = Color.Gray
                    )
                    
                    currentQuestion.options.forEachIndexed { optionIndex, optionText ->
                        val isSelected = selectedAnswers[currentQuestionIndex] == optionIndex
                        val backgroundColor = when {
                            isSelected -> PrimaryBlue.copy(alpha = 0.15f)
                            else -> Color.White
                        }
                        val borderColor = when {
                            isSelected -> PrimaryBlue
                            else -> Color.LightGray
                        }
                        
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedAnswers = selectedAnswers.mapIndexed { i, answer ->
                                        if (i == currentQuestionIndex) optionIndex else answer
                                    }
                                },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = backgroundColor
                            ),
                            border = CardDefaults.outlinedCardBorder().copy(
                                width = 2.dp,
                                brush = androidx.compose.ui.graphics.SolidColor(borderColor)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = isSelected,
                                    onClick = {
                                        selectedAnswers = selectedAnswers.mapIndexed { i, answer ->
                                            if (i == currentQuestionIndex) optionIndex else answer
                                        }
                                    },
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = PrimaryBlue
                                    )
                                )
                                Text(
                                    text = optionText,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Кнопки навигации
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Кнопка "Назад"
                        OutlinedButton(
                            onClick = {
                                if (currentQuestionIndex > 0) {
                                    currentQuestionIndex--
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            enabled = currentQuestionIndex > 0,
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = PrimaryBlue
                            )
                        ) {
                            Icon(Icons.Default.ChevronLeft, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Назад")
                        }
                        
                        // Кнопка "Далее" / "Завершить"
                        if (currentQuestionIndex < totalQuestions - 1) {
                            Button(
                                onClick = {
                                    currentQuestionIndex++
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = PrimaryBlue
                                )
                            ) {
                                Text("Далее")
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.Default.ChevronRight, contentDescription = null)
                            }
                        } else {
                            Button(
                                onClick = { showResults = true },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF4CAF50)
                                )
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Завершить")
                            }
                        }
                    }
                }
            } else {
                // Экран результатов
                val result = calculateResults()
                val percentage = result.percentage
                val passedPercentage = percentage >= 70
                
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    // Иконка результата
                    Icon(
                        imageVector = if (passedPercentage) Icons.Default.EmojiEvents else Icons.Default.SentimentDissatisfied,
                        contentDescription = null,
                        modifier = Modifier.size(100.dp),
                        tint = if (passedPercentage) Color(0xFFFFD700) else Color.Gray
                    )
                    
                    // Процент
                    Text(
                        text = "${percentage.toInt()}%",
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Bold,
                        color = if (passedPercentage) Color(0xFF4CAF50) else Color.Red
                    )
                    
                    Text(
                        text = if (passedPercentage) "Тест сдан!" else "Тест не сдан",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = if (passedPercentage) Color(0xFF4CAF50) else Color.Red
                    )
                    
                    Text(
                        text = "Правильных ответов: ${result.score} из $totalQuestions",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Gray
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Детализация по вопросам
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "Результаты по вопросам",
                                fontWeight = FontWeight.SemiBold,
                                style = MaterialTheme.typography.titleMedium
                            )
                            
                            Spacer(modifier = Modifier.height(12.dp))

                            Column(
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                questions.forEachIndexed { index, question ->
                                    val userAnswer = result.answers[index]
                                    val isCorrect = userAnswer == question.correctAnswer
                                    
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (isCorrect) 
                                                Color(0xFFE8F5E9) 
                                            else 
                                                Color(0xFFFFEBEE)
                                        )
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(12.dp)
                                        ) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(
                                                    text = "Вопрос ${index + 1}",
                                                    fontWeight = FontWeight.Medium
                                                )
                                                Icon(
                                                    imageVector = if (isCorrect) Icons.Default.CheckCircle else Icons.Default.Cancel,
                                                    contentDescription = null,
                                                    tint = if (isCorrect) Color(0xFF4CAF50) else Color.Red,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                            
                                            Spacer(modifier = Modifier.height(8.dp))
                                            
                                            Text(
                                                text = question.text,
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                            
                                            Spacer(modifier = Modifier.height(8.dp))
                                            
                                            Text(
                                                text = "Ваш ответ: ${question.options.getOrNull(userAnswer) ?: "Не выбран"}",
                                                style = MaterialTheme.typography.labelMedium,
                                                color = if (isCorrect) Color(0xFF4CAF50) else Color.Red
                                            )
                                            
                                            if (!isCorrect) {
                                                Text(
                                                    text = "Правильный ответ: ${question.options.getOrNull(question.correctAnswer)}",
                                                    style = MaterialTheme.typography.labelMedium,
                                                    color = Color(0xFF4CAF50)
                                                )
                                            }
                                            
                                            if (question.explanation.isNotEmpty()) {
                                                Spacer(modifier = Modifier.height(8.dp))
                                                Surface(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    color = PrimaryBlue.copy(alpha = 0.1f),
                                                    shape = RoundedCornerShape(6.dp)
                                                ) {
                                                    Row(
                                                        modifier = Modifier.padding(8.dp),
                                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Default.Info,
                                                            contentDescription = null,
                                                            tint = PrimaryBlue,
                                                            modifier = Modifier.size(16.dp)
                                                        )
                                                        Text(
                                                            text = question.explanation,
                                                            style = MaterialTheme.typography.labelSmall,
                                                            color = PrimaryBlue
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    // Кнопка отправки результата
                    Button(
                        onClick = {
                            val attempt = calculateResults()
                            onSubmitResult(attempt)
                            Toast.makeText(context, "Результат отправлен!", Toast.LENGTH_SHORT).show()
                            onNavigateBack()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryBlue
                        )
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Отправить результат", fontWeight = FontWeight.SemiBold)
                    }
                    
                    OutlinedButton(
                        onClick = { showResults = false },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = PrimaryBlue
                        )
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Пройти заново")
                    }
                }
            }
        }
        
        // Диалог подтверждения выхода
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Выйти из теста?") },
                text = { Text("Все несохранённые ответы будут потеряны") },
                confirmButton = {
                    TextButton(onClick = {
                        showDialog = false
                        onNavigateBack()
                    }) {
                        Text("Выйти", color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Отмена")
                    }
                }
            )
        }
    }
}

@Preview
@Composable
fun StudentTestScreenPreview() {
    SmartClassTheme {
        val sampleQuestions = listOf(
            QuizQuestion(
                id = "1",
                text = "Чему равен корень из 144?",
                options = listOf("10", "11", "12", "13"),
                correctAnswer = 2,
                explanation = "12 × 12 = 144"
            ),
            QuizQuestion(
                id = "2",
                text = "Решите уравнение: 2x + 5 = 15",
                options = listOf("x = 5", "x = 10", "x = 7.5", "x = 2.5"),
                correctAnswer = 0,
                explanation = "2x = 10, значит x = 5"
            )
        )
        val sampleHomework = Homework(
            id = "1",
            title = "Тест по алгебре",
            description = "Проверка знаний",
            grade = 7,
            topic = "Квадратные корни",
            dueDate = System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000,
            teacherId = "teacher1",
            teacherName = "Иванов И.И.",
            type = HomeworkType.QUIZ,
            questions = sampleQuestions
        )
        StudentTestScreen(
            homework = sampleHomework,
            onNavigateBack = {},
            onSubmitResult = {}
        )
    }
}
