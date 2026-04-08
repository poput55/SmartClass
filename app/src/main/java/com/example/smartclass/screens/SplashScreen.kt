package com.example.smartclass.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.smartclass.R
import com.example.smartclass.util.AuthManager
import com.example.smartclass.util.UserRole
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToTeacherHome: () -> Unit,
    onNavigateToAuth: () -> Unit
) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.math)
    )

    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = 1,
        speed = 1.2f
    )

    LaunchedEffect(progress) {
        if (progress == 1f) {
            delay(300)
            // Проверка авторизации и роли
            if (AuthManager.isSignedIn()) {
                // Проверка блокировки
                if (AuthManager.isCurrentUserBlocked()) {
                    Log.d("SplashScreen", "Пользователь заблокирован -> Auth")
                    AuthManager.signOut()
                    onNavigateToAuth()
                    return@LaunchedEffect
                }
                val userRole = AuthManager.getCurrentUserRole()
                Log.d("SplashScreen", "userRole = $userRole")
                when (userRole) {
                    UserRole.ADMIN -> {
                        Log.d("SplashScreen", "Навигация на Home (админ)")
                        onNavigateToHome()
                    }
                    UserRole.TEACHER -> {
                        Log.d("SplashScreen", "Навигация на TeacherHome")
                        onNavigateToTeacherHome()
                    }
                    UserRole.STUDENT, null -> {
                        Log.d("SplashScreen", "Навигация на Home (студент или null)")
                        onNavigateToHome()
                    }
                }
            } else {
                Log.d("SplashScreen", "Не авторизован -> Auth")
                onNavigateToAuth()
            }

        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier.size(220.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "SmartClass",
                color = Color.Black,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
@Preview(
    name = "spalsh",
    showBackground = true,
    device = Devices.PIXEL_4
    )
@Composable()
    fun SplashScreenView(){
        SplashScreen(
            onNavigateToHome = {},
            onNavigateToTeacherHome = {},
            onNavigateToAuth = {}
        )
    }
