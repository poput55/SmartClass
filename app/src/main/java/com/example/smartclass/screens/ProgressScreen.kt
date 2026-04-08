package com.example.smartclass.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.smartclass.data.AlgebraTopics
import com.example.smartclass.ui.components.*
import com.example.smartclass.ui.theme.*
import com.example.smartclass.viewmodel.AlgebraViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressScreen(
    viewModel: AlgebraViewModel,
    onNavigateBack: () -> Unit
) {
    Scaffold(
        topBar = {
            AlgebraTopBar(
                title = "Мой прогресс",
                onNavigateBack = onNavigateBack
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                OverallProgressCard(
                    totalProgress = viewModel.getProgress(),
                    completedTopics = viewModel.completedTopics.size,
                    totalTopics = AlgebraTopics.topics.size
                )
            }

            // Grade-wise Progress
            item {
                Text(
                    text = "Прогресс по классам",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            items(listOf(7, 8, 9)) { grade ->
                GradeProgressCard(
                    grade = grade,
                    progress = viewModel.getGradeProgress(grade),
                    completedTopics = AlgebraTopics.getTopicsByGrade(grade)
                        .count { it.id in viewModel.completedTopics },
                    totalTopics = AlgebraTopics.getTopicsByGrade(grade).size
                )
            }

            // Completed Topics
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Завершенные темы",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            if (viewModel.completedTopics.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Пока нет завершенных тем",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                            )
                            Text(
                                text = "Начните обучение, чтобы отслеживать свой прогресс!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
            } else {
                items(
                    AlgebraTopics.topics.filter { it.id in viewModel.completedTopics }
                ) { topic ->
                    CompletedTopicCard(topic = topic)
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun OverallProgressCard(
    totalProgress: Float,
    completedTopics: Int,
    totalTopics: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = PrimaryBlue
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Общий прогресс",
                style = MaterialTheme.typography.titleMedium,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = { totalProgress },
                    modifier = Modifier.size(120.dp),
                    color = Color.White,
                    trackColor = Color.White.copy(alpha = 0.3f),
                    strokeWidth = 12.dp
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${(totalProgress * 100).toInt()}%",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "$completedTopics из $totalTopics тем завершено",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.9f)
            )
        }
    }
}

@Composable
private fun GradeProgressCard(
    grade: Int,
    progress: Float,
    completedTopics: Int,
    totalTopics: Int
) {
    val gradeColor = when (grade) {
        7 -> Grade7Color
        8 -> Grade8Color
        else -> Grade9Color
    }

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.size(60.dp),
                    color = gradeColor,
                    trackColor = gradeColor.copy(alpha = 0.2f),
                    strokeWidth = 6.dp
                )
                Text(
                    text = "${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Класс $grade",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$completedTopics из $totalTopics тем",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            if (progress == 1f) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Завершено",
                        tint = SuccessGreen,
                        modifier = Modifier.size(32.dp)
                    )
            }
        }
    }
}

@Composable
private fun CompletedTopicCard(
    topic: com.example.smartclass.data.Topic
) {
    val gradeColor = when (topic.grade) {
        7 -> Grade7Color
        8 -> Grade8Color
        else -> Grade9Color
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = SuccessGreen.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = SuccessGreen,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = topic.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Класс ${topic.grade}",
                    style = MaterialTheme.typography.bodySmall,
                    color = gradeColor
                )
            }
        }
    }
}

