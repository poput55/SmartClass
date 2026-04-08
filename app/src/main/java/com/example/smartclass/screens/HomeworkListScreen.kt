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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smartclass.data.Homework
import com.example.smartclass.ui.theme.PrimaryBlue
import com.example.smartclass.ui.theme.SmartClassTheme
import com.example.smartclass.util.AuthManager
import com.example.smartclass.viewmodel.HomeworkState
import com.example.smartclass.viewmodel.HomeworkViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeworkListScreen(
    onNavigateBack: () -> Unit,
    onOpenTest: (Homework) -> Unit = {},
    viewModel: HomeworkViewModel = viewModel(factory = HomeworkViewModel.Factory())
) {
    val homeworkState by viewModel.homeworkState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Домашнее задание", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Outlined.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Обновить")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryBlue
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = homeworkState) {
                is HomeworkState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is HomeworkState.Success -> {
                    if (state.homeworkList.isEmpty()) {
                        EmptyHomeworkList(modifier = Modifier.fillMaxSize())
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(
                                items = state.homeworkList,
                                key = { homework -> homework.id.ifEmpty { homework.title + homework.teacherId } }
                            ) { homework ->
                                HomeworkCard(
                                    homework = homework,
                                    onClick = {
                                        if (homework.type == com.example.smartclass.data.HomeworkType.QUIZ) {
                                            onOpenTest(homework)
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
                is HomeworkState.Error -> {
                    ErrorHomeworkList(
                        message = state.message,
                        onRetry = { viewModel.refresh() },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyHomeworkList(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Assignment,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = Color.Gray
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Нет домашних заданий",
            style = MaterialTheme.typography.titleLarge,
            color = Color.Gray,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Учитель ещё не добавил задания",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
    }
}

@Composable
fun ErrorHomeworkList(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Error,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = Color.Red
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Ошибка загрузки",
            style = MaterialTheme.typography.titleLarge,
            color = Color.Red,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text("Повторить")
        }
    }
}

@Composable
fun HomeworkCard(
    homework: Homework,
    onClick: () -> Unit = {}
) {
    val isOverdue = homework.dueDate < System.currentTimeMillis()
    var teacherName by remember { mutableStateOf(homework.teacherName) }
    
    // Получаем последнюю попытку ученика (если есть)
    val userAttempt = homework.attempts
        .filter { it.studentId == AuthManager.auth.currentUser?.uid }
        .maxByOrNull { it.completedAt }

    // Загружаем актуальное имя учителя, если есть teacherId
    LaunchedEffect(homework.id, homework.teacherId) {
        if (homework.teacherId.isNotEmpty()) {
            teacherName = AuthManager.getTeacherName(homework.teacherId)
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isOverdue && !homework.isCompleted) {
                MaterialTheme.colorScheme.errorContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
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
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    // Иконка типа ДЗ
                    Icon(
                        imageVector = if (homework.type == com.example.smartclass.data.HomeworkType.QUIZ) {
                            Icons.Default.Checklist
                        } else {
                            Icons.Default.Description
                        },
                        contentDescription = if (homework.type == com.example.smartclass.data.HomeworkType.QUIZ) "Тест" else "Текстовое",
                        tint = if (homework.type == com.example.smartclass.data.HomeworkType.QUIZ) {
                            PrimaryBlue
                        } else {
                            Color.Gray
                        },
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = homework.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }
                if (userAttempt != null) {
                    // Показываем результат теста
                    val percentage = userAttempt.percentage.toInt()
                    val passed = percentage >= 70
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (passed) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (passed) Icons.Default.CheckCircle else Icons.Default.Cancel,
                                contentDescription = null,
                                tint = if (passed) Color(0xFF4CAF50) else Color.Red,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "$percentage%",
                                fontWeight = FontWeight.Bold,
                                color = if (passed) Color(0xFF4CAF50) else Color.Red,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                } else if (homework.isCompleted) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Выполнено",
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = homework.description,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Color.Gray
                    )
                    Text(
                        text = "${homework.grade} класс",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.Gray
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Book,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Color.Gray
                    )
                    Text(
                        text = homework.topic,
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.Gray,
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Color.Gray
                    )
                    Text(
                        text = teacherName,
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.Gray
                    )
                }

                Text(
                    text = "До: ${SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date(homework.dueDate))}",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isOverdue) Color.Red else PrimaryBlue,
                    fontWeight = FontWeight.Medium
                )
            }
            
            // Индикатор количества вопросов для теста
            if (homework.type == com.example.smartclass.data.HomeworkType.QUIZ) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.QuestionAnswer,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = PrimaryBlue
                    )
                    Text(
                        text = "${homework.questions.size} вопросов",
                        style = MaterialTheme.typography.labelSmall,
                        color = PrimaryBlue
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun HomeworkListScreenPreview() {
    SmartClassTheme {
        HomeworkListScreen(onNavigateBack = {})
    }
}

@Preview
@Composable
fun HomeworkCardPreview() {
    SmartClassTheme {
        HomeworkCard(
            homework = Homework(
                id = "1",
                title = "Решить уравнения",
                description = "Страница 45, номера 1-10. Решить линейные уравнения и записать ответ.",
                grade = 7,
                topic = "Линейные уравнения",
                dueDate = System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000,
                teacherId = "teacher123",
                teacherName = "teacher@example.com",
                isCompleted = false
            )
        )
    }
}
