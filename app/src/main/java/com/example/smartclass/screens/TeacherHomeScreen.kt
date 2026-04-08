package com.example.smartclass.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smartclass.data.ActiveHomeworkPreview
import com.example.smartclass.data.TeacherSubject
import com.example.smartclass.ui.theme.PrimaryBlue
import com.example.smartclass.ui.theme.SmartClassTheme
import com.example.smartclass.viewmodel.TeacherState
import com.example.smartclass.viewmodel.TeacherViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherHomeScreen(
    onNavigateToCreateHomework: () -> Unit,
    onNavigateToCreateQuiz: () -> Unit,
    onNavigateToStudents: () -> Unit,
    onNavigateToReports: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onSubjectClick: (TeacherSubject) -> Unit = {},
    onHomeworkClick: (String) -> Unit = {},
    viewModel: TeacherViewModel = viewModel(factory = TeacherViewModel.Factory())
) {
    val teacherState by viewModel.teacherState.collectAsState()
    
    val subjects = when (val state = teacherState) {
        is TeacherState.Success -> state.subjects
        else -> emptyList()
    }
    
    val activeHomeworks = when (val state = teacherState) {
        is TeacherState.Success -> state.activeHomeworks
        else -> emptyList()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Панель учителя", fontWeight = FontWeight.Bold)
                        val teacherName = com.example.smartclass.util.AuthManager.auth.currentUser?.email ?: "Учитель"
                        Text(
                            text = teacherName,
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Обновить",
                            tint = Color.White
                        )
                    }
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Профиль",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryBlue
                )
            )
        }
    ) { paddingValues ->
        when (val state = teacherState) {
            is TeacherState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is TeacherState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Ошибка: ${state.message}", color = Color.Red)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.refresh() }) {
                            Text("Повторить")
                        }
                    }
                }
            }
            is TeacherState.Success -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
            // Секция: Мои предметы
            item {
                Text(
                    text = "Мои предметы",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))

                subjects.forEach { subject ->
                    SubjectCard(
                        subject = subject,
                        onClick = { onSubjectClick(subject) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            // Секция: Активные ДЗ
            item {
                Text(
                    text = "Активные ДЗ",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))

                activeHomeworks.forEach { homework ->
                    ActiveHomeworkCard(
                        homework = homework,
                        onClick = { onHomeworkClick(homework.id) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            // Быстрые действия
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Быстрые действия",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))

                QuickActionButton(
                    icon = Icons.Default.AddCircle,
                    text = "Создать ДЗ",
                    gradientColors = listOf(Color(0xFF667eea), Color(0xFF764ba2)),
                    onClick = onNavigateToCreateHomework
                )
                Spacer(modifier = Modifier.height(8.dp))

                QuickActionButton(
                    icon = Icons.Default.Quiz,
                    text = "Создать тест",
                    gradientColors = listOf(Color(0xFFf093fb), Color(0xFFf5576c)),
                    onClick = onNavigateToCreateQuiz
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    QuickActionButton(
                        icon = Icons.Default.People,
                        text = "Ученики",
                        gradientColors = listOf(Color(0xFF4facfe), Color(0xFF00f2fe)),
                        onClick = onNavigateToStudents,
                        modifier = Modifier.weight(1f)
                    )
                    QuickActionButton(
                        icon = Icons.Default.BarChart,
                        text = "Отчёты",
                        gradientColors = listOf(Color(0xFF43e97b), Color(0xFF38f9d7)),
                        onClick = onNavigateToReports,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
                }
            }
        }
    }
}

@Composable
fun SubjectCard(
    subject: TeacherSubject,
    onClick: () -> Unit
) {
    val color = try {
        Color(android.graphics.Color.parseColor(subject.color))
    } catch (e: Exception) {
        PrimaryBlue
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Цветной индикатор
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(color, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MenuBook,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Column {
                    Text(
                        text = "${subject.name} • ${subject.grade}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.People,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${subject.studentsCount} уч.",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Assignment,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${subject.activeHomeworks} ДЗ",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ActiveHomeworkCard(
    homework: ActiveHomeworkPreview,
    onClick: () -> Unit
) {
    val isOverdue = homework.dueDate < System.currentTimeMillis()
    val progress = if (homework.totalCount > 0) {
        homework.submittedCount.toFloat() / homework.totalCount
    } else 0f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isOverdue) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (homework.isQuiz) Icons.Default.Quiz else Icons.Default.Assignment,
                        contentDescription = null,
                        tint = if (homework.isQuiz) Color(0xFF667eea) else Color(0xFF764ba2),
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = homework.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Класс: ${homework.grade}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "До: ${SimpleDateFormat("dd.MM", Locale.getDefault()).format(Date(homework.dueDate))}",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isOverdue) MaterialTheme.colorScheme.error else PrimaryBlue,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Прогресс сдачи
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Сдали: ${homework.submittedCount}/${homework.totalCount}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (progress >= 0.5) Color(0xFF43e97b) else Color(0xFFf5576c)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth(),
                    color = if (progress >= 0.5) Color(0xFF43e97b) else Color(0xFFf5576c),
                    trackColor = MaterialTheme.colorScheme.outlineVariant
                )
            }
        }
    }
}

@Composable
fun QuickActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String,
    gradientColors: List<Color>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(gradientColors)
                )
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = text,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Preview
@Composable
fun TeacherHomeScreenPreview() {
    SmartClassTheme {
        TeacherHomeScreen(
            onNavigateToCreateHomework = {},
            onNavigateToCreateQuiz = {},
            onNavigateToStudents = {},
            onNavigateToReports = {},
            onNavigateToProfile = {},
            onSubjectClick = {},
            onHomeworkClick = {}
        )
    }
}
