package com.example.smartclass.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.smartclass.ui.theme.*
import com.example.smartclass.util.UserRole
import com.example.smartclass.viewmodel.AuthViewModel
import com.example.smartclass.viewmodel.AuthState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    onAuthSuccess: () -> Unit,
    onNavigateBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val viewModel: AuthViewModel = viewModel()

    val authState by viewModel.authState.collectAsState()
    val isSignInMode by viewModel.isSignInMode.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var selectedRole by remember { mutableStateOf(UserRole.STUDENT) }
    var selectedGrade by remember { mutableStateOf(7) }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var adminTapCount by remember { mutableStateOf(0) }
    var showAdminCode by remember { mutableStateOf(false) }
    var adminCodeInput by remember { mutableStateOf("") }

    // Обработка состояний авторизации
    LaunchedEffect(authState) {
        when (val state = authState) {
            is AuthState.Success -> {
                Toast.makeText(
                    context,
                    if (isSignInMode) "Вход выполнен!" else "Регистрация успешна!",
                    Toast.LENGTH_SHORT
                ).show()
                onAuthSuccess()
            }
            is AuthState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                isLoading = false
            }
            is AuthState.Loading -> isLoading = true
            is AuthState.Idle -> isLoading = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Functions,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "SmartClass",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.clickable {
                                adminTapCount++
                                if (adminTapCount >= 10) {
                                    showAdminCode = true
                                    adminTapCount = 0
                                }
                            }
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                AnimatedContent(
                    targetState = isSignInMode,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(200)) togetherWith
                                fadeOut(animationSpec = tween(200))
                    },
                    label = "title"
                ) { signIn ->
                    Text(
                        text = if (signIn) "Вход в аккаунт" else "Создание аккаунта",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = if (isSignInMode)
                        "Введите данные для продолжения" else
                        "Заполните форму для начала обучения",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }

            // Карточка формы
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Поля имени и фамилии (только для регистрации)
                    if (!isSignInMode) {
                        NameInputField(firstName, { firstName = it }, "Имя", Icons.Default.Person)
                        NameInputField(lastName, { lastName = it }, "Фамилия", Icons.Default.PersonOutline)
                    }

                    // Email поле с валидацией
                    val emailError by remember(email) {
                        derivedStateOf {
                            if (email.isNotEmpty() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                                "Некорректный email"
                            } else null
                        }
                    }

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Email,
                                contentDescription = null,
                                tint = if (emailError != null) Color(0xFFEF5350) else PrimaryBlue
                            )
                        },
                        trailingIcon = {
                            if (email.isNotEmpty()) {
                                IconButton(onClick = { email = "" }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Очистить",
                                        tint = Color.Gray,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(androidx.compose.ui.focus.FocusDirection.Down) }
                        ),
                        singleLine = true,
                        isError = emailError != null,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryBlue,
                            unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f),
                            errorBorderColor = Color(0xFFEF5350)
                        )
                    )

                    // Password поле с индикатором силы
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Пароль") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = PrimaryBlue
                            )
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = if (passwordVisible) "Скрыть" else "Показать",
                                    tint = Color.Gray
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                                if (email.isNotBlank() && password.isNotBlank()) {
                                    if (isSignInMode) viewModel.signIn(email, password)
                                    else viewModel.signUp(email, password)
                                }
                            }
                        ),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryBlue,
                            unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f)
                        )
                    )

                    // Выбор роли (только для регистрации)
                    if (!isSignInMode) {
                        Text(
                            text = "Выберите роль:",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilterChip(
                                selected = selectedRole == UserRole.STUDENT,
                                onClick = { selectedRole = UserRole.STUDENT },
                                label = { Text("Ученик") },
                                leadingIcon = if (selectedRole == UserRole.STUDENT) {
                                    {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                } else null,
                                modifier = Modifier.weight(1f)
                            )
                            FilterChip(
                                selected = selectedRole == UserRole.TEACHER,
                                onClick = { selectedRole = UserRole.TEACHER },
                                label = { Text("Учитель") },
                                leadingIcon = if (selectedRole == UserRole.TEACHER) {
                                    {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                } else null,
                                modifier = Modifier.weight(1f)
                            )
                        }

                        // ADMIN (скрытый, появляется после ввода кода)
                        if (selectedRole == UserRole.ADMIN) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                            ) {
                                FilterChip(
                                    selected = true,
                                    onClick = { /* неактивен */ },
                                    label = { Text("Администратор") },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.AdminPanelSettings,
                                            contentDescription = null,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = Color(0xFF6750A4).copy(alpha = 0.2f),
                                        selectedLabelColor = Color(0xFF6750A4),
                                        selectedLeadingIconColor = Color(0xFF6750A4)
                                    )
                                )
                            }
                        }

                        // Выбор класса (только для ученика)
                        if (selectedRole == UserRole.STUDENT) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "Выберите класс:",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                )
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
                    }

                    // Кнопка входа/регистрации
                    Button(
                        onClick = {
                            if (!isSignInMode && (firstName.isBlank() || lastName.isBlank())) {
                                Toast.makeText(context, "Введите имя и фамилию", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            if (email.isNotBlank() && password.isNotBlank()) {
                                if (isSignInMode) viewModel.signIn(email, password)
                                else viewModel.signUp(email, password, selectedRole, selectedGrade, firstName, lastName)
                            } else {
                                Toast.makeText(context, "Заполните все поля", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !isLoading && email.isNotBlank() && password.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryBlue
                        )
                    ) {
                        AnimatedContent(
                            targetState = isLoading,
                            label = "buttonContent"
                        ) { loading ->
                            if (loading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = if (isSignInMode) Icons.Default.CheckCircle else Icons.Default.Person,
                                        contentDescription = null,
                                        modifier = Modifier.size(20.dp)
                                    )
                                    Text(
                                        text = if (isSignInMode) "Войти" else "Зарегистрироваться",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }

                    // Восстановление пароля
                    AnimatedVisibility(
                        visible = isSignInMode,
                        enter = expandVertically() + fadeIn(),
                        exit = shrinkVertically() + fadeOut()
                    ) {
                        TextButton(
                            onClick = {
                                if (email.isNotBlank()) {
                                    viewModel.resetPassword(email)
                                    Toast.makeText(context, "Инструкции отправлены на email", Toast.LENGTH_LONG).show()
                                } else {
                                    Toast.makeText(context, "Введите email для восстановления", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Забыли пароль?",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = PrimaryBlue
                                )
                            }
                        }
                    }
                }
            }

            // 🔀 Переключатель режима
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isSignInMode) "Нет аккаунта? " else "Уже есть аккаунт? ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                TextButton(onClick = {
                    viewModel.toggleAuthMode()
                }) {
                    Text(
                        text = if (isSignInMode) "Зарегистрироваться" else "Войти",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue
                    )
                }
            }

        }
    }

    // Скрытый диалог для активации админа (секретный код: "admin2024")
    // Активация: 10 тапов по заголовку "SmartClass" в шапке
    if (showAdminCode) {
        AlertDialog(
            onDismissRequest = { showAdminCode = false },
            title = { Text("Код доступа") },
            text = {
                OutlinedTextField(
                    value = adminCodeInput,
                    onValueChange = { adminCodeInput = it },
                    label = { Text("Введите код") },
                    singleLine = true
                )
            },
            confirmButton = {
                Button(onClick = {
                    if (adminCodeInput == "admin2024") {
                        selectedRole = UserRole.ADMIN
                        showAdminCode = false
                        adminCodeInput = ""
                        Toast.makeText(context, "Режим администратора активирован", Toast.LENGTH_SHORT).show()
                    }
                }) {
                    Text("Подтвердить")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAdminCode = false }) {
                    Text("Отмена")
                }
            }
        )
    }
}

@Composable
private fun NameInputField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        leadingIcon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = PrimaryBlue,
                modifier = Modifier.size(20.dp)
            )
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next
        ),
        singleLine = true,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PrimaryBlue,
            unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f)
        )
    )
}

@Preview(device = "spec:parent=pixel_5,orientation=portrait", showBackground = true)
@Composable
fun AuthScreenPreview() {
    SmartClassTheme {
        AuthScreen(onAuthSuccess = {})
    }
}