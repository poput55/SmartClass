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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.smartclass.ui.components.*
import com.example.smartclass.ui.theme.*
import com.example.smartclass.viewmodel.AlgebraViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PracticeScreen(
    viewModel: AlgebraViewModel,
    onNavigateBack: () -> Unit
) {
    val questions = viewModel.quizQuestions
    val currentQuestion = questions.getOrNull(viewModel.currentQuestionIndex)

    Scaffold(
        topBar = {
            AlgebraTopBar(
                title = "Практика",
                onNavigateBack = {
                    viewModel.resetQuiz()
                    onNavigateBack()
                }
            )
        }
    ) { paddingValues ->
        if (viewModel.quizCompleted) {
            QuizResultContent(
                correctAnswers = viewModel.correctAnswers,
                totalQuestions = questions.size,
                onRetry = { viewModel.startQuiz(viewModel.currentTopic?.id) },
                onFinish = {
                    viewModel.resetQuiz()
                    onNavigateBack()
                },
                modifier = Modifier.padding(paddingValues)
            )
        } else if (currentQuestion != null) {
            QuestionContent(
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

@Composable
private fun QuestionContent(
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
        // Progress
        LinearProgressIndicator(
            progress = { questionNumber.toFloat() / totalQuestions },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = PrimaryBlue
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Вопрос $questionNumber из $totalQuestions",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Question
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

        // Options
        question.options.forEachIndexed { index, option ->
            AnswerOption(
                text = option,
                isSelected = selectedAnswer == index,
                isCorrect = if (showAnswer) index == question.correctAnswer else null,
                showResult = showAnswer,
                onClick = { onAnswerSelected(index) }
            )
        }

        // Explanation
        if (showAnswer) {
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (selectedAnswer == question.correctAnswer)
                        SuccessGreen.copy(alpha = 0.1f)
                    else
                        ErrorRed.copy(alpha = 0.1f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (selectedAnswer == question.correctAnswer)
                                Icons.Default.CheckCircle
                            else
                                Icons.Default.Info,
                            contentDescription = null,
                            tint = if (selectedAnswer == question.correctAnswer)
                                SuccessGreen
                            else
                                SecondaryOrange
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (selectedAnswer == question.correctAnswer)
                                "Правильоно!"
                            else
                                "Объяснение",
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

        // Action Button
        Button(
            onClick = if (showAnswer) onNextQuestion else onCheckAnswer,
            modifier = Modifier.fillMaxWidth(),
            enabled = selectedAnswer != null,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (showAnswer) SuccessGreen else PrimaryBlue
            )
        ) {
            Text(
                text = if (showAnswer) "Next Question" else "Check Answer",
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Composable
private fun QuizResultContent(
    correctAnswers: Int,
    totalQuestions: Int,
    onRetry: () -> Unit,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    val percentage = (correctAnswers.toFloat() / totalQuestions * 100).toInt()
    val isPassed = percentage >= 60

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = if (isPassed) Icons.Default.EmojiEvents else Icons.Default.Refresh,
            contentDescription = null,
            modifier = Modifier.size(100.dp),
            tint = if (isPassed) SecondaryOrange else PrimaryBlue
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = if (isPassed) "Хорошая работа!" else "Практикуйся по больше!",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "You got $correctAnswers out of $totalQuestions correct",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "$percentage%",
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Bold,
            color = if (isPassed) SuccessGreen else ErrorRed
        )

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(
                onClick = onRetry,
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Заново")
            }

            Button(
                onClick = onFinish,
                modifier = Modifier.weight(1f)
            ) {
                Text("Закончить")
                Spacer(modifier = Modifier.width(8.dp))
                Icon(Icons.Default.Check, contentDescription = null)
            }
        }
    }
}