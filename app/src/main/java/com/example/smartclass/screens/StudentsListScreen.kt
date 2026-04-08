package com.example.smartclass.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smartclass.data.StudentItem
import com.example.smartclass.ui.theme.PrimaryBlue
import com.example.smartclass.ui.theme.SmartClassTheme
import com.example.smartclass.viewmodel.TeacherViewModel
import com.example.smartclass.viewmodel.TeacherState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentsListScreen(
    onNavigateBack: () -> Unit,
    showAllStudents: Boolean = true,  // Показывать всех или только классов учителя
    viewModel: TeacherViewModel = viewModel(factory = TeacherViewModel.Factory())
) {
    val teacherState by viewModel.teacherState.collectAsState()
    val allStudents by viewModel.allStudents.collectAsState()
    
    val displayStudents: List<StudentItem> = when (val state = teacherState) {
        is TeacherState.Success -> {
            if (showAllStudents) {
                allStudents
            } else {
                state.students
            }
        }
        else -> emptyList()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ученики", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
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
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Статистика
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = PrimaryBlue.copy(alpha = 0.1f)
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                StatItem("Всего", displayStudents.size.toString())
                                VerticalDivider(modifier = Modifier.height(40.dp), color = Color.LightGray)
                                val grade7Count = displayStudents.count { it.grade == "7" }
                                val grade8Count = displayStudents.count { it.grade == "8" }
                                val grade9Count = displayStudents.count { it.grade == "9" }
                                StatItem("7 класс", "$grade7Count", Color(0xFF667eea))
                                VerticalDivider(modifier = Modifier.height(40.dp), color = Color.LightGray)
                                StatItem("8 класс", "$grade8Count", Color(0xFF43e97b))
                                VerticalDivider(modifier = Modifier.height(40.dp), color = Color.LightGray)
                                StatItem("9 класс", "$grade9Count", Color(0xFFf093fb))
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Список учеников
                    if (displayStudents.isEmpty()) {
                        item {
                            EmptyState()
                        }
                    } else {
                        items(displayStudents) { student ->
                            StudentCard(student = student)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.PeopleOutline,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = Color.Gray
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Нет учеников",
            style = MaterialTheme.typography.titleLarge,
            color = Color.Gray,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Ученики ещё не добавлены в систему",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )
    }
}

@Composable
fun StatItem(label: String, value: String, color: Color = PrimaryBlue) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleLarge,
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
fun StudentCard(student: StudentItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Аватар
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(PrimaryBlue.copy(alpha = 0.1f), RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = student.name.first().toString(),
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlue
                )
            }

            Column {
                Text(
                    text = student.name,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "${student.grade} класс",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}

@Preview
@Composable
fun StudentsListScreenPreview() {
    SmartClassTheme {
        StudentsListScreen(onNavigateBack = {})
    }
}
