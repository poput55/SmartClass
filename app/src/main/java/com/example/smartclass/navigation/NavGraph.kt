package com.example.smartclass.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.smartclass.screens.AdminScreen
import com.example.smartclass.screens.AuthScreen
import com.example.smartclass.screens.CreateHomeworkScreen
import com.example.smartclass.screens.FormulaScreen
import com.example.smartclass.screens.HomeScreen
import com.example.smartclass.screens.HomeworkListScreen
import com.example.smartclass.screens.LessonScreen
import com.example.smartclass.screens.PracticeScreen
import com.example.smartclass.screens.ProfileScreen
import com.example.smartclass.screens.ProgressScreen
import com.example.smartclass.screens.QuizScreen
import com.example.smartclass.screens.SplashScreen
import com.example.smartclass.screens.StudentTestScreen
import com.example.smartclass.screens.StudentsListScreen
import com.example.smartclass.screens.TeacherHomeScreen
import com.example.smartclass.screens.TeacherReportsScreen
import com.example.smartclass.screens.TopicsScreen
import com.example.smartclass.data.Homework
import com.example.smartclass.data.StudentAttempt
import com.example.smartclass.viewmodel.AlgebraViewModel
import com.example.smartclass.viewmodel.HomeworkViewModel
import com.google.firebase.firestore.FirebaseFirestore

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Home : Screen("home")
    object TeacherHome : Screen("teacher-home")
    object Admin : Screen("admin")
    object Topics : Screen("topics")
    object Lesson : Screen("lesson")
    object Practice : Screen("practice")
    object Quiz : Screen("quiz")
    object Formulas : Screen("formulas")
    object Progress : Screen("progress")
    object Auth : Screen("auth")
    object Profile : Screen("profile")
    object CreateHomework : Screen("create-homework")
    object HomeworkList : Screen("homework-list")
    object StudentsList : Screen("students-list")
    object TeacherReports : Screen("teacher-reports")
    object StudentTest : Screen("student-test/{homeworkId}") {
        fun createRoute(homeworkId: String) = "student-test/$homeworkId"
    }
}

@Composable
fun NavGraph(
    navController: NavHostController,
    viewModel: AlgebraViewModel = viewModel()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateToHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Splash.route) {
                            inclusive = true
                        }
                    }
                },
                onNavigateToTeacherHome = {
                    navController.navigate(Screen.TeacherHome.route) {
                        popUpTo(Screen.Splash.route) {
                            inclusive = true
                        }
                    }
                },
                onNavigateToAuth = {
                    navController.navigate(Screen.Auth.route) {
                        popUpTo(Screen.Splash.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(Screen.Auth.route) {
            AuthScreen(
                onAuthSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Auth.route) {
                            inclusive = true
                        }
                    }
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Profile Screen
        composable(Screen.Profile.route) {
            ProfileScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToAdmin = { navController.navigate(Screen.Admin.route) },
                onLogout = {
                    navController.navigate(Screen.Auth.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }


        // Home Screen
        composable(Screen.Home.route) { backStackEntry ->
            val homeworkViewModel: HomeworkViewModel = viewModel(backStackEntry)
            HomeScreen(
                viewModel = viewModel,
                homeworkViewModel = homeworkViewModel,
                onNavigateToTopics = { navController.navigate(Screen.Topics.route) },
                onNavigateToFormulas = { navController.navigate(Screen.Formulas.route) },
                onNavigateToQuiz = {
                    viewModel.startQuiz()
                    navController.navigate(Screen.Quiz.route)
                },
                onNavigateToProgress = { navController.navigate(Screen.Progress.route) },
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                onNavigateToCreateHomework = { navController.navigate(Screen.CreateHomework.route) },
                onNavigateToHomeworkList = { navController.navigate(Screen.HomeworkList.route) },
                onNavigateToAdmin = { navController.navigate(Screen.Admin.route) }
            )
        }

        // Teacher Home Screen (для учителя)
        composable(Screen.TeacherHome.route) {
            TeacherHomeScreen(
                onNavigateToCreateHomework = { navController.navigate(Screen.CreateHomework.route) },
                onNavigateToCreateQuiz = { navController.navigate(Screen.CreateHomework.route) },
                onNavigateToStudents = { navController.navigate(Screen.StudentsList.route) },
                onNavigateToReports = { navController.navigate(Screen.TeacherReports.route) },
                onNavigateToProfile = { navController.navigate(Screen.Profile.route) },
                onSubjectClick = { },
                onHomeworkClick = { }
            )
        }

        // Admin Screen (для администратора)
        composable(Screen.Admin.route) {
            AdminScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Students List Screen (список учеников)
        composable(Screen.StudentsList.route) {
            StudentsListScreen(
                onNavigateBack = { navController.popBackStack() },
                showAllStudents = true
            )
        }

        // Teacher Reports Screen (отчёты учителя)
        composable(Screen.TeacherReports.route) {
            TeacherReportsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Create Homework Screen (для учителя)
        composable(Screen.CreateHomework.route) {
            CreateHomeworkScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Homework List Screen (для ученика/учителя)
        composable(Screen.HomeworkList.route) { backStackEntry ->
            val homeworkViewModel: HomeworkViewModel = viewModel(backStackEntry)
            HomeworkListScreen(
                onNavigateBack = { navController.popBackStack() },
                onOpenTest = { homework ->
                    navController.navigate(Screen.StudentTest.createRoute(homework.id))
                },
                viewModel = homeworkViewModel
            )
        }

        // Student Test Screen (прохождение теста)
        composable(Screen.StudentTest.route) { backStackEntry ->
            val homeworkId = backStackEntry.arguments?.getString("homeworkId") ?: return@composable
            val homeworkViewModel: HomeworkViewModel = viewModel(backStackEntry)
            
            // Загружаем Homework из Firestore по ID
            var homework by remember { mutableStateOf<Homework?>(null) }
            
            LaunchedEffect(homeworkId) {
                FirebaseFirestore.getInstance()
                    .collection("homework")
                    .document(homeworkId)
                    .get()
                    .addOnSuccessListener { document ->
                        homework = document.toObject(Homework::class.java)
                    }
                    .addOnFailureListener {
                        navController.popBackStack()
                    }
            }
            
            homework?.let { hw ->
                StudentTestScreen(
                    homework = hw,
                    onNavigateBack = { navController.popBackStack() },
                    onSubmitResult = { attempt: StudentAttempt ->
                        homeworkViewModel.submitTestResult(homeworkId, attempt)
                        navController.popBackStack()
                    }
                )
            } ?: run {
                // Показываем индикатор загрузки
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }

        composable(Screen.Topics.route) {
            TopicsScreen(
                viewModel = viewModel,
                onTopicSelected = { navController.navigate(Screen.Lesson.route) },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Lesson.route) {
            LessonScreen(
                viewModel = viewModel,
                onNavigateToPractice = { navController.navigate(Screen.Practice.route) },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Practice.route) {
            PracticeScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Quiz.route) {
            QuizScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Formulas.route) {
            FormulaScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Progress.route) {
            ProgressScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
