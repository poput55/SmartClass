package com.example.smartclass.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smartclass.ui.theme.PrimaryBlue
import com.example.smartclass.ui.theme.SmartClassTheme
import com.example.smartclass.viewmodel.TeacherState
import com.example.smartclass.viewmodel.TeacherStats
import com.example.smartclass.viewmodel.TeacherViewModel

data class ReportItem(
    val label: String,
    val value: Int,
    val total: Int,
    val color: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeacherReportsScreen(
    onNavigateBack: () -> Unit,
    viewModel: TeacherViewModel = viewModel(factory = TeacherViewModel.Factory())
) {
    val teacherState by viewModel.teacherState.collectAsState()
    val allStudents by viewModel.allStudents.collectAsState()

    val stats = when (val state = teacherState) {
        is TeacherState.Success -> state.stats
        else -> TeacherStats()
    }
    
    val subjects = when (val state = teacherState) {
        is TeacherState.Success -> state.subjects
        else -> emptyList()
    }
    
    val activeHomeworks = when (val state = teacherState) {
        is TeacherState.Success -> state.activeHomeworks
        else -> emptyList()
    }

    // Реальная статистика по ДЗ
    val now = System.currentTimeMillis()
    val totalSubmissions = activeHomeworks.sumOf { it.submittedCount }
    val totalPossibleSubmissions = activeHomeworks.sumOf { it.totalCount }
    val overdueCount = activeHomeworks.count { it.dueDate < now && it.submittedCount < it.totalCount }
    
    // Процент выполнения
    val completionRate = if (totalPossibleSubmissions > 0) {
        (totalSubmissions.toFloat() / totalPossibleSubmissions * 100).toInt()
    } else 0
    
    val homeworkStats = when (val state = teacherState) {
        is TeacherState.Success -> listOf(
            ReportItem("Сдано работ", totalSubmissions, totalPossibleSubmissions.coerceAtLeast(1), Color(0xFF43e97b)),
            ReportItem("Просрочено", overdueCount, activeHomeworks.size.coerceAtLeast(1), Color(0xFFf5576c)),
            ReportItem("Выполнение", completionRate, 100, Color(0xFF667eea))
        )
        else -> emptyList()
    }

    // Успеваемость по классам (считаем по количеству сдавших ДЗ)
    val gradeStats = when (val state = teacherState) {
        is TeacherState.Success -> {
            subjects.map { subject ->
                val studentsInGrade = allStudents.filter { "${it.grade}" == subject.grade }
                val homeworksForGrade = activeHomeworks.filter { it.grade == subject.grade }
                val totalSubmitted = homeworksForGrade.sumOf { it.submittedCount }
                val totalStudents = studentsInGrade.size
                
                // Процент сдавших ДЗ в классе
                val completionPercent = if (totalStudents > 0 && homeworksForGrade.isNotEmpty()) {
                    (totalSubmitted.toFloat() / (totalStudents * homeworksForGrade.size) * 100).toInt().coerceIn(0, 100)
                } else 0
                
                subject.grade to completionPercent
            }
        }
        else -> emptyList()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Отчёты", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryBlue
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Общая статистика
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = PrimaryBlue.copy(alpha = 0.1f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Text(
                            text = "Общая успеваемость",
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            SummaryStatItem("Выполнение ДЗ", "$completionRate%", Color(0xFF667eea))
                            SummaryStatItem("Активных ДЗ", "${activeHomeworks.size}", Color(0xFF43e97b))
                            SummaryStatItem("Учеников", "${allStudents.size}", Color(0xFFf093fb))
                        }
                    }
                }
            }

            // Статистика по ДЗ
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Text(
                            text = "Статистика по ДЗ",
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        homeworkStats.forEach { stat ->
                            StatRow(
                                label = stat.label,
                                value = stat.value,
                                total = stat.total,
                                color = stat.color
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }

            // По классам
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Text(
                            text = "Успеваемость по классам",
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        gradeStats.forEach { (grade, score) ->
                            GradeStatRow(grade = grade, score = score)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }

            // Топ учеников
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        Text(
                            text = "Топ учеников",
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.titleLarge
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        val topStudents = allStudents
                            .sortedByDescending { it.averageScore }
                            .take(3)
                        
                        if (topStudents.isEmpty()) {
                            Text("Нет данных об учениках", color = Color.Gray)
                        } else {
                            topStudents.forEachIndexed { index, student ->
                                TopStudentItem("${index + 1}", student.name, student.grade, student.averageScore.toInt())
                                if (index < topStudents.size - 1) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun SummaryStatItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.headlineMedium,
            color = color
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = Color.Gray
        )
    }
}

@Composable
fun StatRow(label: String, value: Int, total: Int, color: Color) {
    val progress = value.toFloat() / total

    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Gray
            )
            Text(
                text = "$value/$total",
                fontWeight = FontWeight.SemiBold,
                color = color
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth(),
            color = color,
            trackColor = Color.LightGray
        )
    }
}

@Composable
fun GradeStatRow(grade: String, score: Int) {
    val color = when {
        score >= 80 -> Color(0xFF43e97b)
        score >= 60 -> Color(0xFFf093fb)
        else -> Color(0xFFf5576c)
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = grade,
            fontWeight = FontWeight.Medium,
            style = MaterialTheme.typography.bodyLarge
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            LinearProgressIndicator(
                progress = { score / 100f },
                modifier = Modifier.width(100.dp),
                color = color,
                trackColor = Color.LightGray
            )
            Text(
                text = "$score%",
                fontWeight = FontWeight.Bold,
                color = color,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun TopStudentItem(rank: String, name: String, grade: String, score: Int) {
    val medalColor = when (rank) {
        "1" -> Color(0xFFFFD700) // Золото
        "2" -> Color(0xFFC0C0C0) // Серебро
        "3" -> Color(0xFFCD7F32) // Бронза
        else -> Color.Gray
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "#$rank",
                    fontWeight = FontWeight.Bold,
                    color = medalColor
                )
            }
            Column {
                Text(
                    text = name,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = grade,
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray
                )
            }
        }
        Text(
            text = "$score%",
            fontWeight = FontWeight.Bold,
            color = Color(0xFF43e97b)
        )
    }
}

@Preview
@Composable
fun TeacherReportsScreenPreview() {
    SmartClassTheme {
        TeacherReportsScreen(onNavigateBack = {})
    }
}
