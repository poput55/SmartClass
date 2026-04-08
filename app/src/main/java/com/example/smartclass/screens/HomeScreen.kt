package com.example.smartclass.screens

import android.app.Activity
import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smartclass.ui.components.*
import com.example.smartclass.ui.theme.*
import com.example.smartclass.util.AuthManager
import com.example.smartclass.util.UserRole
import com.example.smartclass.viewmodel.AlgebraViewModel
import com.example.smartclass.viewmodel.HomeworkViewModel
import kotlinx.coroutines.launch



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: AlgebraViewModel,
    homeworkViewModel: HomeworkViewModel = viewModel(factory = HomeworkViewModel.Factory()),
    onNavigateToTopics: () -> Unit,
    onNavigateToFormulas: () -> Unit,
    onNavigateToQuiz: () -> Unit,
    onNavigateToProgress: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToCreateHomework: () -> Unit,
    onNavigateToHomeworkList: () -> Unit,
    onNavigateToAdmin: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var userRole by remember { mutableStateOf<UserRole?>(null) }
    var showProfileIcon by remember { mutableStateOf(AuthManager.isSignedIn()) }

    LaunchedEffect(Unit) {
        userRole = AuthManager.getCurrentUserRole()
        android.util.Log.d("HomeScreen", "userRole = $userRole")
    }

    Scaffold(
        topBar = {
            AlgebraTopBar(
                title = "Изучение алгебры",
                actions = {
                    if (showProfileIcon) {
                        IconButton(
                            onClick = onNavigateToProfile,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Профиль",
                                tint = Color.White,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Welcome Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(
                    containerColor = PrimaryBlue
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    Text(
                        text = "Добро пожаловать в SmartClass!",
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Освойте концепции алгебры для классов 7-9",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Progress Overview
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        ProgressStat(
                            label = "Класс 7",
                            progress = viewModel.getGradeProgress(7),
                            color = Grade7Color
                        )
                        ProgressStat(
                            label = "Класс 8",
                            progress = viewModel.getGradeProgress(8),
                            color = Grade8Color
                        )
                        ProgressStat(
                            label = "Класс 9",
                            progress = viewModel.getGradeProgress(9),
                            color = Grade9Color
                        )
                    }
                }
            }



            // Menu Cards
            Text(
                text = "Варианты обучения",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            // Домашнее задание (для учителя и ученика)
            when (userRole) {
                UserRole.TEACHER -> {
                    MenuCard(
                        title = "Домашнее задание",
                        subtitle = "Создать ДЗ для учеников",
                        icon = Icons.Default.AddCircle,
                        gradientColors = listOf(Color(0xFF7C4DFF), Color(0xFF536DFE)),
                        onClick = onNavigateToCreateHomework
                    )
                }
                UserRole.STUDENT -> {
                    MenuCard(
                        title = "Домашнее задание",
                        subtitle = "Просмотреть задания от учителя",
                        icon = Icons.Default.Assignment,
                        gradientColors = listOf(Color(0xFF7C4DFF), Color(0xFF536DFE)),
                        onClick = onNavigateToHomeworkList
                    )
                }
                else -> {
                    // Если роль не определена, показываем все основные кнопки
                }
            }

            // Карточка админ-панели (только для админов)
            if (userRole == UserRole.ADMIN) {
                MenuCard(
                    title = "Админ-панель",
                    subtitle = "Управление пользователями",
                    icon = Icons.Default.AdminPanelSettings,
                    gradientColors = listOf(Color(0xFF6750A4), Color(0xFF9A4D8E)),
                    onClick = onNavigateToAdmin
                )
            }

            MenuCard(
                title = "Темы и уроки",
                subtitle = "Изучайте концепции алгебры шаг за шагом",
                icon = Icons.Default.MenuBook,
                gradientColors = listOf(Grade7Color, Grade8Color),
                onClick = onNavigateToTopics
            )

            MenuCard(
                title = "Справочник формул",
                subtitle = "Быстрый доступ ко всем формулам",
                icon =Icons.Default.Functions,
                gradientColors = listOf(SecondaryOrange, Color(0xFFFF5722)),
                onClick = onNavigateToFormulas
            )

            MenuCard(
                title = "Тест на практику",
                subtitle = "Проверьте свои знания",
                icon = Icons.Default.Quiz,
                gradientColors = listOf(Grade9Color, Color(0xFFE91E63)),
                onClick = {
                    viewModel.startQuiz()
                    onNavigateToQuiz()
                }
            )

            MenuCard(
                title = "Мой прогресс",
                subtitle = "Отслеживайте свой путь обучения",
                icon = Icons.Default.QueryBuilder,
                gradientColors = listOf(SuccessGreen, Color(0xFF00BCD4)),
                onClick = onNavigateToProgress
            )
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun ProgressStat(
    label: String,
    progress: Float,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                progress = { progress },
                modifier = Modifier.size(60.dp),
                color = color,
                trackColor = Color.White.copy(alpha = 0.3f),
                strokeWidth = 6.dp
            )
            Text(
                text = "${(progress * 100).toInt()}%",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.9f)
        )
    }
}

@Preview(
    name = "Home Screen",
    showBackground = true,
    device = Devices.PIXEL_4
)
@Composable
fun HomeScreenPreview() {
    val context = LocalContext.current

    val previewPrefs = remember {
        context.getSharedPreferences(
            "preview_prefs",
            Context.MODE_PRIVATE
        )
    }

    val previewViewModel = remember {
        AlgebraViewModel(
            savedStateHandle = SavedStateHandle(),
            sharedPreferences = previewPrefs
        )
    }

    SmartClassTheme {
        HomeScreen(
            viewModel = previewViewModel,
            onNavigateToTopics = {},
            onNavigateToFormulas = {},
            onNavigateToQuiz = {},
            onNavigateToProgress = {},
            onNavigateToProfile = {},
            onNavigateToCreateHomework = {},
            onNavigateToHomeworkList = {}
        )
    }
}
