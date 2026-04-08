package com.example.smartclass.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.smartclass.ui.theme.PrimaryBlue
import com.example.smartclass.util.AuthManager
import com.example.smartclass.util.UserRole
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var users by remember { mutableStateOf<List<Map<String, Any?>>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var showRoleDialog by remember { mutableStateOf(false) }
    var selectedUser by remember { mutableStateOf<Map<String, Any?>?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        loadUsers { loadedUsers ->
            users = loadedUsers
            isLoading = false
        }
    }

    val filteredUsers = users.filter { user ->
        val name = user["fullName"] as? String ?: user["firstName"] as? String ?: ""
        val email = user["email"] as? String ?: ""
        val role = user["role"] as? String ?: ""
        name.contains(searchQuery, ignoreCase = true) ||
        email.contains(searchQuery, ignoreCase = true) ||
        role.contains(searchQuery, ignoreCase = true)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Админ-панель", fontWeight = FontWeight.Bold)
                        Text(
                            text = "Пользователи: ${users.size}",
                            style = MaterialTheme.typography.labelMedium,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        isLoading = true
                        scope.launch {
                            loadUsers { loadedUsers ->
                                users = loadedUsers
                                isLoading = false
                            }
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Обновить",
                            tint = Color.White
                        )
                    }
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
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
                .padding(horizontal = 16.dp)
        ) {
            // Поиск
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                placeholder = { Text("Поиск по имени, email, роли...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null)
                },
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(filteredUsers) { user ->
                        UserCard(
                            user = user,
                            onRoleClick = {
                                selectedUser = user
                                showRoleDialog = true
                            },
                            onBlockClick = {
                                scope.launch {
                                    val userId = user["id"] as String
                                    val isBlocked = user["isBlocked"] as Boolean
                                    val result = AuthManager.setBlocked(userId, !isBlocked)
                                    if (result.isSuccess) {
                                        loadUsers { users = it }
                                    } else {
                                        Toast.makeText(context, "Ошибка: ${result.exceptionOrNull()?.message}", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            },
                            onDeleteClick = {
                                scope.launch {
                                    val userId = user["id"] as String
                                    val result = AuthManager.deleteUser(userId)
                                    if (result.isSuccess) {
                                        loadUsers { users = it }
                                        Toast.makeText(context, "Пользователь удалён", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "Ошибка: ${result.exceptionOrNull()?.message}", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        )
                    }

                    if (filteredUsers.isEmpty() && searchQuery.isNotEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.SearchOff,
                                        contentDescription = null,
                                        modifier = Modifier.size(48.dp),
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("Ничего не найдено")
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Диалог смены роли
    if (showRoleDialog && selectedUser != null) {
        val currentUser = selectedUser
        RoleChangeDialog(
            user = currentUser!!,
            onDismiss = {
                showRoleDialog = false
            },
            onRoleChanged = { newRole ->
                scope.launch {
                    val userId = currentUser["id"] as String
                    val result = AuthManager.changeUserRole(userId, newRole)
                    if (result.isSuccess) {
                        loadUsers { users = it }
                        Toast.makeText(context, "Роль изменена", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Ошибка: ${result.exceptionOrNull()?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
                showRoleDialog = false
            }
        )
    }
}

private suspend fun loadUsers(callback: (List<Map<String, Any?>>) -> Unit) {
    val users = AuthManager.getAllUsers()
    callback(users)
}

@Composable
fun UserCard(
    user: Map<String, Any?>,
    onRoleClick: () -> Unit,
    onBlockClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val name = user["fullName"] as? String
        ?: (user["firstName"] as? String ?: "Без имени")
    val email = user["email"] as? String ?: "Без email"
    val role = UserRole.valueOf(user["role"] as? String ?: "STUDENT")
    val isBlocked = user["isBlocked"] as? Boolean ?: false
    val grade = user["grade"] as? Int

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
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
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Аватар
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                color = when (role) {
                                    UserRole.ADMIN -> Color(0xFFF44336)
                                    UserRole.TEACHER -> Color(0xFF2196F3)
                                    UserRole.STUDENT -> Color(0xFF4CAF50)
                                },
                                shape = RoundedCornerShape(10.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Column {
                        Text(
                            text = name,
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = email,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (grade != null && role == UserRole.STUDENT) {
                            Text(
                                text = "$grade класс",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                // Бейдж роли
                AssistChip(
                    onClick = onRoleClick,
                    label = {
                        Text(
                            text = when (role) {
                                UserRole.ADMIN -> "Админ"
                                UserRole.TEACHER -> "Учитель"
                                UserRole.STUDENT -> "Ученик"
                            },
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = when (role) {
                                UserRole.ADMIN -> Icons.Default.AdminPanelSettings
                                UserRole.TEACHER -> Icons.Default.School
                                UserRole.STUDENT -> Icons.Default.Person
                            },
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = when (role) {
                            UserRole.ADMIN -> Color(0xFFF44336).copy(alpha = 0.15f)
                            UserRole.TEACHER -> Color(0xFF2196F3).copy(alpha = 0.15f)
                            UserRole.STUDENT -> Color(0xFF4CAF50).copy(alpha = 0.15f)
                        }
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Кнопки действий
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Блокировка/разблокировка
                OutlinedButton(
                    onClick = onBlockClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = if (isBlocked) Icons.Default.LockOpen else Icons.Default.Lock,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isBlocked) "Разблокировать" else "Заблокировать")
                }

                // Удаление
                OutlinedButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Удалить")
                }
            }

            // Индикатор блокировки
            if (isBlocked) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "Пользователь заблокирован",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
fun RoleChangeDialog(
    user: Map<String, Any?>,
    onDismiss: () -> Unit,
    onRoleChanged: (UserRole) -> Unit
) {
    val currentRole = UserRole.valueOf(user["role"] as? String ?: "STUDENT")
    var selectedRole by remember { mutableStateOf(currentRole) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Изменить роль")
        },
        text = {
            Column {
                val name = user["fullName"] as? String ?: user["firstName"] as? String ?: "Пользователь"
                Text("Выберите роль для: $name")
                Spacer(modifier = Modifier.height(16.dp))
                UserRole.entries.forEach { role ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedRole = role }
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedRole == role,
                            onClick = { selectedRole = role }
                        )
                        Text(
                            text = when (role) {
                                UserRole.ADMIN -> "Администратор"
                                UserRole.TEACHER -> "Учитель"
                                UserRole.STUDENT -> "Ученик"
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onRoleChanged(selectedRole) }) {
                Text("Сохранить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
}
