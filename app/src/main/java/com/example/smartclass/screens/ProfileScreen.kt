package com.example.smartclass.screens

import android.R
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.smartclass.ui.theme.*
import com.example.smartclass.util.AuthManager
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    onNavigateToAdmin: () -> Unit = {},
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()
    val currentUser = AuthManager.auth.currentUser

    var showLogoutDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val email = currentUser?.email ?: "Неизвестно"
    val userId = currentUser?.uid?.take(8)?.plus("...") ?: "Неизвестно"
    val isEmailVerified = currentUser?.isEmailVerified ?: false
    val creationTime = currentUser?.metadata?.creationTimestamp?.let {
        SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date(it))
    } ?: "Неизвестно"

    // Загружаем имя и фамилию
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val name = AuthManager.getCurrentUserName()
        // Пробуем распарсить fullName на firstName и lastName
        val parts = name.split(" ")
        if (parts.size >= 2) {
            lastName = parts.first()
            firstName = parts.drop(1).joinToString(" ")
        } else {
            firstName = name
            lastName = ""
        }
        fullName = name
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Профиль",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Outlined.ArrowBack,
                            contentDescription = "Назад",
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Аватар пользователя
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                // Avatar Card
                Card(
                    modifier = Modifier.size(100.dp),
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(
                        containerColor = PrimaryBlue.copy(alpha = 0.1f)
                    ),
                    border = BorderStroke(2.dp, PrimaryBlue.copy(alpha = 0.3f))
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Аватар",
                            tint = PrimaryBlue,
                            modifier = Modifier.size(50.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Имя и фамилия пользователя
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = fullName ?: "Загрузка...",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            maxLines = 2
                        )
                        Text(
                            text = email,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray,
                            textAlign = TextAlign.Center,
                            maxLines = 1
                        )
                    }
                }

                // Кнопка редактирования
                TextButton(
                    onClick = { showEditDialog = true }
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = PrimaryBlue
                        )
                        Text(
                            text = "Редактировать",
                            style = MaterialTheme.typography.labelLarge,
                            color = PrimaryBlue,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Статус верификации
                if (isEmailVerified) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF4CAF50).copy(alpha = 0.1f),
                        modifier = Modifier.padding(horizontal = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF4CAF50),
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "Email подтверждён",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF4CAF50),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Карточка информации о профиле
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
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Заголовок секции
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = PrimaryBlue,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "Информация о профиле",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A237E)
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color.Gray.copy(alpha = 0.2f))

                    // Элементы информации
                    ProfileInfoItem(
                        label = "Email",
                        value = email,
                        icon = Icons.Default.Email,
                        onCopy = { /* TODO: копировать email */ }
                    )
                    ProfileInfoItem(
                        label = "ID пользователя",
                        value = userId,
                        icon = Icons.Default.Fingerprint,
                        onCopy = { /* TODO: копировать ID */ }
                    )
                    ProfileInfoItem(
                        label = "Дата регистрации",
                        value = creationTime,
                        icon = Icons.Default.CalendarToday
                    )

                    // Статус аккаунта
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = if (isEmailVerified) Color(0xFF4CAF50) else Color(0xFFFF9800),
                            modifier = Modifier.size(24.dp)
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Статус аккаунта",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                            Text(
                                text = if (isEmailVerified) "Активен" else "Требует подтверждения",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium,
                                color = if (isEmailVerified) Color(0xFF4CAF50) else Color(0xFFFF9800)
                            )
                        }
                        if (!isEmailVerified) {
                            TextButton(onClick = { /* TODO: отправить повторное письмо */ }) {
                                Text(
                                    text = "Подтвердить",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = PrimaryBlue,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
            // Кнопка выхода
            Button(
                onClick = { showLogoutDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFF44336)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Выйти из аккаунта",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            // Кнопка админ-панели (только для админов)
            var isAdmin by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) {
                isAdmin = AuthManager.getCurrentUserRole() == com.example.smartclass.util.UserRole.ADMIN
            }
            if (isAdmin) {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(
                    onClick = onNavigateToAdmin,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF6750A4)
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        width = 2.dp,
                        brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF6750A4))
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.AdminPanelSettings,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Админ-панель",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = Color.Gray.copy(alpha = 0.2f))
            Text(
                text = "∑ Решай • ∫ Изучай • ∞ Развивайся",
                style = MaterialTheme.typography.labelSmall,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Light
            )
        }
    }

    // 🔄 Диалог подтверждения выхода
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = null,
                    tint = Color(0xFFF44336),
                    modifier = Modifier.size(28.dp)
                )
            },
            title = { Text("Выход из аккаунта", fontWeight = FontWeight.Bold) },
            text = { Text("Вы уверены, что хотите выйти из аккаунта?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        AuthManager.signOut()
                        Toast.makeText(context, "Выход выполнен", Toast.LENGTH_SHORT).show()
                        showLogoutDialog = false
                        onLogout()
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFFF44336))
                ) {
                    Text("Выйти", fontWeight = FontWeight.Medium)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Отмена", fontWeight = FontWeight.Medium)
                }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = MaterialTheme.colorScheme.surface
        )
    }

    // Диалог редактирования имени
    if (showEditDialog) {
        EditNameDialog(
            firstName = firstName,
            lastName = lastName,
            onFirstNameChange = { firstName = it },
            onLastNameChange = { lastName = it },
            onDismiss = { showEditDialog = false },
            onSave = {
                isLoading = true
                scope.launch {
                    val result = AuthManager.updateUserName(firstName, lastName)
                    if (result.isSuccess) {
                        Toast.makeText(context, "Имя обновлено", Toast.LENGTH_SHORT).show()
                        fullName = "$lastName $firstName".trim()
                        showEditDialog = false
                    } else {
                        Toast.makeText(context, "Ошибка: ${result.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
                    }
                    isLoading = false
                }
            },
            isLoading = isLoading
        )
    }
}

// ─────────────────────────────────────────────────────────────
// Компонент элемента информации профиля
// ─────────────────────────────────────────────────────────────
@Composable
fun ProfileInfoItem(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    valueColor: Color = Color.Unspecified,
    onCopy: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = PrimaryBlue,
            modifier = Modifier.size(24.dp)
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = if (valueColor == Color.Unspecified) MaterialTheme.colorScheme.onSurface else valueColor,
                maxLines = 1
            )
        }

        // Кнопка копирования (если передан onCopy)
        if (onCopy != null) {
            IconButton(
                onClick = onCopy,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "Копировать",
                    tint = Color.Gray,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Preview(device = "spec:parent=pixel_5,orientation=portrait", showBackground = true)
@Composable
fun ProfileScreenPreview() {
    SmartClassTheme {
        ProfileScreen(
            onNavigateBack = {},
            onLogout = {}
        )
    }
}

// ─────────────────────────────────────────────────────────────
// Диалог редактирования имени
// ─────────────────────────────────────────────────────────────
@Composable
fun EditNameDialog(
    firstName: String,
    lastName: String,
    onFirstNameChange: (String) -> Unit,
    onLastNameChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onSave: () -> Unit,
    isLoading: Boolean
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = null,
                tint = PrimaryBlue,
                modifier = Modifier.size(28.dp)
            )
        },
        title = {
            Text(
                text = "Редактировать имя",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Имя
                OutlinedTextField(
                    value = firstName,
                    onValueChange = onFirstNameChange,
                    label = { Text("Имя") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = PrimaryBlue
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Next
                    ),
                    singleLine = true,
                    enabled = !isLoading,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryBlue,
                        unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f)
                    )
                )

                // Фамилия
                OutlinedTextField(
                    value = lastName,
                    onValueChange = onLastNameChange,
                    label = { Text("Фамилия") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.PersonOutline,
                            contentDescription = null,
                            tint = PrimaryBlue
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                    singleLine = true,
                    enabled = !isLoading,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryBlue,
                        unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f)
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onSave,
                enabled = !isLoading && firstName.isNotBlank() && lastName.isNotBlank(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryBlue
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Сохранить")
                }
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isLoading
            ) {
                Text("Отмена")
            }
        },
        shape = RoundedCornerShape(16.dp),
        containerColor = MaterialTheme.colorScheme.surface
    )
}

@Preview
@Composable
fun EditNameDialogPreview() {
    SmartClassTheme {
        EditNameDialog(
            firstName = "Иван",
            lastName = "Иванов",
            onFirstNameChange = {},
            onLastNameChange = {},
            onDismiss = {},
            onSave = {},
            isLoading = false
        )
    }
}