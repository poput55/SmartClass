package com.example.smartclass.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.smartclass.ui.components.*
import com.example.smartclass.ui.theme.*
import com.example.smartclass.viewmodel.AlgebraViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonScreen(
    viewModel: AlgebraViewModel,
    onNavigateToPractice: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val topic = viewModel.currentTopic
    val currentLesson = topic?.lessons?.getOrNull(viewModel.currentLessonIndex)

    if (topic == null || currentLesson == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Урок не выбран")
        }
        return
    }

    var expandedExample by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(topic.id, currentLesson.id) {
        println("DEBUG: LessonScreen - Topic ID: ${topic.id}, Lesson ID: ${currentLesson.id}")
        println("DEBUG: Topic: ${topic.title}, Lesson: ${currentLesson.title}")
    }

    Scaffold(
        topBar = {
            AlgebraTopBar(
                title = topic.title,
                onNavigateBack = onNavigateBack
            )
        },
        bottomBar = {
            LessonBottomBar(
                currentIndex = viewModel.currentLessonIndex,
                totalLessons = topic.lessons.size,
                onPrevious = {
                    viewModel.previousLesson()
                },
                onNext = {
                    viewModel.completeCurrentLesson()
                    viewModel.nextLesson()
                },
                onPractice = {
                    viewModel.completeCurrentLesson()
                    viewModel.startQuiz(topic.id)
                    onNavigateToPractice()
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Lesson Progress
            LinearProgressIndicator(
                progress = { (viewModel.currentLessonIndex + 1).toFloat() / topic.lessons.size },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp),
                color = PrimaryBlue
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Урок ${viewModel.currentLessonIndex + 1} из ${topic.lessons.size}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Lesson Title
            Text(
                text = currentLesson.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Lesson Content
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Text(
                    text = currentLesson.content,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(16.dp),
                    lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.5
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Examples Section
            Text(
                text = "Примеры",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            currentLesson.examples.forEachIndexed { index, example ->
                ExampleCard(
                    example = example,
                    exampleNumber = index + 1,
                    isExpanded = expandedExample == index,
                    onToggle = {
                        expandedExample = if (expandedExample == index) null else index
                    }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Key Points Section
            if (currentLesson.keyPoints.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Ключевые моменты",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = SuccessGreen.copy(alpha = 0.1f)
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        currentLesson.keyPoints.forEach { point ->
                            Row(
                                modifier = Modifier.padding(vertical = 4.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = SuccessGreen,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = point,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
private fun ExampleCard(
    example: com.example.smartclass.data.Example,
    exampleNumber: Int,
    isExpanded: Boolean,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = PrimaryBlue.copy(alpha = 0.05f)
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Пример $exampleNumber",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlue
                )
                IconButton(onClick = onToggle) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (isExpanded) "Свернуть" else "Развернуть"
                    )
                }
            }

            Text(
                text = example.problem,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )

            if (isExpanded) {
                Spacer(modifier = Modifier.height(16.dp))

                Divider()

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Шаги решения:",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                example.steps.forEachIndexed { index, step ->
                    StepCard(stepNumber = index + 1, content = step)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = SuccessGreen.copy(alpha = 0.1f),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = SuccessGreen
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Ответ: ${example.solution}",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = SuccessGreen
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LessonBottomBar(
    currentIndex: Int,
    totalLessons: Int,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onPractice: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = onPrevious,
                enabled = currentIndex > 0
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Предыдущий")
            }

            if (currentIndex == totalLessons - 1) {
                Button(
                    onClick = onPractice,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = SuccessGreen
                    )
                ) {
                    Text("Практика")
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.Default.Quiz, contentDescription = null)
                }
            } else {
                Button(onClick = onNext) {
                    Text("Следующий")
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                }
            }
        }
    }
}
