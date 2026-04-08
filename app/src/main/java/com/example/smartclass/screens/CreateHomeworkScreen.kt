package com.example.smartclass.screens

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smartclass.data.HomeworkType
import com.example.smartclass.data.QuizQuestion
import com.example.smartclass.ui.theme.PrimaryBlue
import com.example.smartclass.ui.theme.SmartClassTheme
import com.example.smartclass.viewmodel.HomeworkViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateHomeworkScreen(
    onNavigateBack: () -> Unit,
    viewModel: HomeworkViewModel = viewModel(factory = HomeworkViewModel.Factory())
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedGrade by remember { mutableStateOf(7) }
    var topic by remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf<Long?>(null) }
    
    // Переключатель типа ДЗ
    var homeworkType by remember { mutableStateOf(HomeworkType.TEXT) }
    
    // Вопросы для теста
    var questions by remember { mutableStateOf(listOf<QuizQuestion>()) }

    val calendar = Calendar.getInstance()
    val datePickerDialog = DatePickerDialog(
        context,
        { _, year, month, day ->
            calendar.set(year, month, day)
            dueDate = calendar.timeInMillis
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Создать ДЗ", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Переключатель типа ДЗ
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1A237E).copy(alpha = 0.1f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Тип задания",
                        fontWeight = FontWeight.Medium,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = homeworkType == HomeworkType.TEXT,
                            onClick = { homeworkType = HomeworkType.TEXT },
                            label = { Text("Текстовое") },
                            leadingIcon = if (homeworkType == HomeworkType.TEXT) {
                                { Icon(Icons.Default.Description, contentDescription = null, Modifier.size(18.dp)) }
                            } else null,
                            modifier = Modifier.weight(1f)
                        )
                        FilterChip(
                            selected = homeworkType == HomeworkType.QUIZ,
                            onClick = { homeworkType = HomeworkType.QUIZ },
                            label = { Text("Тест") },
                            leadingIcon = if (homeworkType == HomeworkType.QUIZ) {
                                { Icon(Icons.Default.Checklist, contentDescription = null, Modifier.size(18.dp)) }
                            } else null,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Название ДЗ
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Название задания") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                leadingIcon = {
                    Icon(Icons.Default.Title, contentDescription = null)
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                singleLine = true
            )

            // Описание / Инструкция
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text(if (homeworkType == HomeworkType.QUIZ) "Инструкция для теста" else "Описание") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                shape = RoundedCornerShape(12.dp),
                leadingIcon = {
                    Icon(Icons.Default.Description, contentDescription = null)
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                ),
                maxLines = 4
            )

            // Класс
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1A237E).copy(alpha = 0.1f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Класс",
                        fontWeight = FontWeight.Medium,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(7, 8, 9).forEach { grade ->
                            FilterChip(
                                selected = selectedGrade == grade,
                                onClick = { selectedGrade = grade },
                                label = { Text("$grade класс") },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Тема
            OutlinedTextField(
                value = topic,
                onValueChange = { topic = it },
                label = { Text("Тема") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                leadingIcon = {
                    Icon(Icons.Default.Book, contentDescription = null)
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                singleLine = true
            )

            // Срок сдачи
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { datePickerDialog.show() },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF1A237E).copy(alpha = 0.1f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = PrimaryBlue
                        )
                        Column {
                            Text(
                                text = "Срок сдачи",
                                fontWeight = FontWeight.Medium,
                                color = Color.Gray
                            )
                            Text(
                                text = dueDate?.let {
                                    SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date(it))
                                } ?: "Не выбран",
                                color = if (dueDate == null) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = Color.Gray
                    )
                }
            }

            // Редактор вопросов для теста
            if (homeworkType == HomeworkType.QUIZ) {
                QuizEditorSection(
                    questions = questions,
                    onQuestionsChange = { questions = it }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Кнопка создания
            Button(
                onClick = {
                    if (title.isBlank()) {
                        Toast.makeText(context, "Введите название", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    if (homeworkType == HomeworkType.QUIZ && questions.isEmpty()) {
                        Toast.makeText(context, "Добавьте хотя бы один вопрос", Toast.LENGTH_SHORT).show()
                        return@Button
                    }
                    
                    viewModel.createHomework(
                        title = title,
                        description = description,
                        grade = selectedGrade,
                        topic = topic,
                        dueDate = dueDate ?: System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000,
                        type = homeworkType,
                        questions = questions
                    )
                    Toast.makeText(context, "ДЗ создано", Toast.LENGTH_SHORT).show()
                    onNavigateBack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                enabled = title.isNotBlank() && (homeworkType == HomeworkType.TEXT || questions.isNotEmpty()),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryBlue
                )
            ) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Создать ДЗ", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun QuizEditorSection(
    questions: List<QuizQuestion>,
    onQuestionsChange: (List<QuizQuestion>) -> Unit
) {
    var expandedQuestions by remember { mutableStateOf(questions.map { false }) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A237E).copy(alpha = 0.05f)
        )
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
                Text(
                    text = "Вопросы теста",
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TextButton(onClick = {
                        onQuestionsChange(questions + QuizQuestion(
                            id = System.currentTimeMillis().toString(),
                            text = "",
                            options = listOf("", "", "", ""),
                            correctAnswer = 0,
                            explanation = ""
                        ))
                        expandedQuestions = expandedQuestions + true
                    }) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Вопрос")
                    }
                }
            }
            
            if (questions.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    questions.forEachIndexed { index, question ->
                        QuestionEditor(
                            question = question,
                            index = index,
                            isExpanded = expandedQuestions.getOrNull(index) ?: false,
                            onQuestionChange = { updatedQuestion ->
                                onQuestionsChange(questions.mapIndexed { i, q ->
                                    if (i == index) updatedQuestion else q
                                })
                            },
                            onDelete = {
                                onQuestionsChange(questions.filterIndexed { i, _ -> i != index })
                                expandedQuestions = expandedQuestions.filterIndexed { i, _ -> i != index }
                            },
                            onToggleExpand = {
                                expandedQuestions = expandedQuestions.mapIndexed { i, expanded ->
                                    if (i == index) !expanded else expanded
                                }
                            }
                        )
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Нет вопросов. Добавьте хотя бы один вопрос для теста.",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun QuestionEditor(
    question: QuizQuestion,
    index: Int,
    isExpanded: Boolean,
    onQuestionChange: (QuizQuestion) -> Unit,
    onDelete: () -> Unit,
    onToggleExpand: () -> Unit
) {
    var localText by remember(question.text) { mutableStateOf(question.text) }
    var localOptions by remember(question.options) { mutableStateOf(question.options.toMutableList()) }
    var localCorrectAnswer by remember(question.correctAnswer) { mutableStateOf(question.correctAnswer) }
    var localExplanation by remember(question.explanation) { mutableStateOf(question.explanation) }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
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
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(PrimaryBlue, RoundedCornerShape(4.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${index + 1}",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                    Text(
                        text = "Вопрос ${index + 1}",
                        fontWeight = FontWeight.Medium
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(onClick = onToggleExpand) {
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = if (isExpanded) "Свернуть" else "Развернуть",
                            tint = Color.Gray
                        )
                    }
                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Удалить",
                            tint = Color.Red
                        )
                    }
                }
            }
            
            if (isExpanded) {
                Spacer(modifier = Modifier.height(12.dp))
                
                // Текст вопроса
                OutlinedTextField(
                    value = localText,
                    onValueChange = {
                        localText = it
                        onQuestionChange(question.copy(text = it))
                    },
                    label = { Text("Текст вопроса") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = false,
                    maxLines = 3
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Варианты ответов
                Text(
                    text = "Варианты ответов",
                    fontWeight = FontWeight.Medium,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(8.dp))
                
                localOptions.forEachIndexed { optionIndex, optionText ->
                    var localOptionText by remember(optionText) { mutableStateOf(optionText) }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = localCorrectAnswer == optionIndex,
                            onClick = {
                                localCorrectAnswer = optionIndex
                                onQuestionChange(question.copy(correctAnswer = optionIndex))
                            },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = Color.Green
                            )
                        )
                        OutlinedTextField(
                            value = localOptionText,
                            onValueChange = {
                                localOptionText = it
                                localOptions = localOptions.toMutableList().apply {
                                    this[optionIndex] = it
                                }
                                onQuestionChange(question.copy(options = localOptions.toList()))
                            },
                            label = { Text("Вариант ${optionIndex + 1}") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = if (localCorrectAnswer == optionIndex) Color.Green else Color.Gray,
                                unfocusedBorderColor = if (localCorrectAnswer == optionIndex) Color.Green else Color.Gray
                            )
                        )
                    }
                }
                
                Text(
                    text = "Отметьте зелёным правильный ответ",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // Объяснение
                OutlinedTextField(
                    value = localExplanation,
                    onValueChange = {
                        localExplanation = it
                        onQuestionChange(question.copy(explanation = it))
                    },
                    label = { Text("Объяснение (необязательно)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    singleLine = false,
                    maxLines = 2
                )
            } else {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = question.text.ifEmpty { "Без текста" },
                    maxLines = 2,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Правильный ответ: ${question.options.getOrNull(question.correctAnswer)?.ifEmpty { "Не указан" } ?: "Не указан"}",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color.Green
                )
            }
        }
    }
}

@Preview
@Composable
fun CreateHomeworkScreenPreview() {
    SmartClassTheme {
        CreateHomeworkScreen(onNavigateBack = {})
    }
}
