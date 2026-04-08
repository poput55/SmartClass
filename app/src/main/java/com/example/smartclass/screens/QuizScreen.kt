package com.example.smartclass.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.smartclass.ui.components.*
import com.example.smartclass.ui.theme.*
import com.example.smartclass.viewmodel.AlgebraViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    viewModel: AlgebraViewModel,
    onNavigateBack: () -> Unit
) {
    val questions = viewModel.quizQuestions
    val currentQuestion = questions.getOrNull(viewModel.currentQuestionIndex)

    Scaffold(
        topBar = {
            AlgebraTopBar(
                title = "Тест",
                onNavigateBack = {
                    viewModel.resetQuiz()
                    onNavigateBack()
                }
            )
        }
    ) { paddingValues ->
        when {
            questions.isEmpty() -> {
                // Экран начала теста
                QuizSetupContent(
                    onStartQuiz = { viewModel.startQuiz() },
                    modifier = Modifier.padding(paddingValues)
                )
            }
            viewModel.quizCompleted -> {
                // Экран результатов
                QuizResultsContent(
                    correctAnswers = viewModel.correctAnswers,
                    totalQuestions = questions.size,
                    onRetry = { viewModel.startQuiz() },
                    onFinish = {
                        viewModel.resetQuiz()
                        onNavigateBack()
                    },
                    modifier = Modifier.padding(paddingValues)
                )
            }
            currentQuestion != null -> {
                // Экран вопроса
                QuizQuestionContent(
                    question = currentQuestion,
                    questionNumber = viewModel.currentQuestionIndex + 1,
                    totalQuestions = questions.size,
                    selectedAnswer = viewModel.selectedAnswer,
                    showAnswer = viewModel.showAnswer,
                    onAnswerSelected = { viewModel.selectAnswer(it) },
                    onCheckAnswer = { viewModel.checkAnswer() },
                    onNextQuestion = { viewModel.nextQuestion() },
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

@Composable
private fun QuizSetupContent(
    onStartQuiz: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Quiz,
            contentDescription = null,
            modifier = Modifier.size(100.dp),
            tint = PrimaryBlue
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Проверка знаний",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Ответьте на 10 вопросов по всем темам алгебры",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = PrimaryBlue.copy(alpha = 0.1f)
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                QuizInfoRow(icon = Icons.Default.Timer, text = "Нет ограничения по времени")
                Spacer(modifier = Modifier.height(8.dp))
                QuizInfoRow(icon = Icons.Default.QuestionAnswer, text = "10 вопросов")
                Spacer(modifier = Modifier.height(8.dp))
                QuizInfoRow(icon = Icons.Default.School, text = "Все оценки включены")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onStartQuiz,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Icon(Icons.Default.PlayArrow, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Начать тест", style = MaterialTheme.typography.titleMedium)
        }
    }
}

@Composable
private fun QuizInfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = PrimaryBlue,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun QuizQuestionContent(
    question: com.example.smartclass.data.Question,
    questionNumber: Int,
    totalQuestions: Int,
    selectedAnswer: Int?,
    showAnswer: Boolean,
    onAnswerSelected: (Int) -> Unit,
    onCheckAnswer: () -> Unit,
    onNextQuestion: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Progress indicator
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Вопрос $questionNumber/$totalQuestions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Surface(
                shape = MaterialTheme.shapes.small,
                color = when (question.difficulty) {
                    com.example.smartclass.data.Difficulty.EASY -> SuccessGreen
                    com.example.smartclass.data.Difficulty.MEDIUM -> SecondaryOrange
                    com.example.smartclass.data.Difficulty.HARD -> ErrorRed
                }.copy(alpha = 0.2f)
            ) {
                Text(
                    text = when (question.difficulty) {
                        com.example.smartclass.data.Difficulty.EASY -> "Лёгкий"
                        com.example.smartclass.data.Difficulty.MEDIUM -> "Средний"
                        com.example.smartclass.data.Difficulty.HARD -> "Сложный"
                        else -> question.difficulty.name
                    },
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = when (question.difficulty) {
                        com.example.smartclass.data.Difficulty.EASY -> SuccessGreen
                        com.example.smartclass.data.Difficulty.MEDIUM -> SecondaryOrange
                        com.example.smartclass.data.Difficulty.HARD -> ErrorRed
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        LinearProgressIndicator(
            progress = { questionNumber.toFloat() / totalQuestions },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = PrimaryBlue
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Question card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = PrimaryBlue.copy(alpha = 0.1f)
            )
        ) {
            Text(
                text = question.question,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(20.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Answer options
        question.options.forEachIndexed { index, option ->
            AnswerOption(
                text = option,
                isSelected = selectedAnswer == index,
                isCorrect = if (showAnswer) index == question.correctAnswer else null,
                showResult = showAnswer,
                onClick = { onAnswerSelected(index) }
            )
        }

        // Explanation when answer is shown
        if (showAnswer) {
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (selectedAnswer == question.correctAnswer)
                        SuccessGreen.copy(alpha = 0.1f)
                    else
                        SecondaryOrange.copy(alpha = 0.1f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = null,
                            tint = SecondaryOrange
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Объяснение",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = question.explanation,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Action button
        Button(
            onClick = if (showAnswer) onNextQuestion else onCheckAnswer,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            enabled = selectedAnswer != null,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (showAnswer) SuccessGreen else PrimaryBlue
            )
        ) {
            Text(
                text = if (showAnswer) {
                    if (questionNumber == totalQuestions) "Завершить тест" else "Следующий вопрос"
                } else "Проверить ответ",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                imageVector = if (showAnswer) Icons.Default.ArrowForward else Icons.Default.Check,
                contentDescription = null
            )
        }
    }
}

@Composable
private fun QuizResultsContent(
    correctAnswers: Int,
    totalQuestions: Int,
    onRetry: () -> Unit,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    val percentage = (correctAnswers.toFloat() / totalQuestions * 100).toInt()
    val grade = when {
        percentage >= 90 -> "5"
        percentage >= 80 -> "4"
        percentage >= 70 -> "3"
        percentage >= 60 -> "2"
        else -> "1"
    }
    val isPassed = percentage >= 60

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Trophy or retry icon
        Icon(
            imageVector = if (isPassed) Icons.Default.EmojiEvents else Icons.Default.SentimentDissatisfied,
            contentDescription = null,
            modifier = Modifier.size(120.dp),
            tint = if (isPassed) SecondaryOrange else PrimaryBlue
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = if (isPassed) "Тест сдан!" else "Попробуйте ещё раз",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Score card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (isPassed) SuccessGreen.copy(alpha = 0.1f) else ErrorRed.copy(alpha = 0.1f)
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Ваш результат",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "$correctAnswers / $totalQuestions",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$percentage%",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isPassed) SuccessGreen else ErrorRed
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Surface(
                        shape = MaterialTheme.shapes.medium,
                        color = if (isPassed) SuccessGreen else ErrorRed
                    ) {
                        Text(
                            text = "Оценка: $grade",
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = androidx.compose.ui.graphics.Color.White
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Action buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(
                onClick = onRetry,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Заново")
            }

            Button(
                onClick = onFinish,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
            ) {
                Text("Домой")
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.Default.Home, contentDescription = null)
            }
        }
    }
}