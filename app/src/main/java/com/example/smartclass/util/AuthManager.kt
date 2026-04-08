package com.example.smartclass.util

import android.util.Log
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

private const val TAG = "AuthManager"

/**
 * Роли пользователей
 */
enum class UserRole {
    STUDENT,  // Ученик
    TEACHER   // Учитель
}

/**
 * Менеджер для управления авторизацией пользователей
 */
object AuthManager {

    val auth: FirebaseAuth = FirebaseManager.auth
    private val firestore: FirebaseFirestore = FirebaseManager.firestore

    /**
     * Регистрация пользователя по email и паролю с ролью
     */
    suspend fun signUp(
        email: String,
        password: String,
        role: UserRole = UserRole.STUDENT,
        grade: Int = 7,
        firstName: String = "",
        lastName: String = ""
    ): Result<AuthResult> {
        Log.d(TAG, "signUp: начало для $email, role=$role, grade=$grade, firstName=$firstName, lastName=$lastName")
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val userId = result.user?.uid ?: "unknown"

            // Сохраняем данные пользователя в Firestore
            val userData = hashMapOf(
                "email" to email,
                "role" to role.name,
                "grade" to grade,
                "firstName" to firstName,
                "lastName" to lastName,
                "fullName" to "$lastName $firstName".trim(),
                "createdAt" to System.currentTimeMillis(),
                "userId" to userId
            )
            firestore.collection("users").document(userId).set(userData).await()

            Log.d(TAG, "signUp: успешно, userId=$userId, role=$role, grade=$grade")
            Result.success(result)
        } catch (e: Exception) {
            Log.e(TAG, "signUp: ошибка - ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Вход пользователя по email и паролю
     */
    suspend fun signIn(email: String, password: String): Result<AuthResult> {
        Log.d(TAG, "signIn: начало для $email")
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val userId = result.user?.uid ?: "unknown"
            Log.d(TAG, "signIn: успешно, userId=$userId")
            Result.success(result)
        } catch (e: Exception) {
            Log.e(TAG, "signIn: ошибка - ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Выход из аккаунта
     */
    fun signOut() {
        val currentEmail = auth.currentUser?.email ?: "unknown"
        Log.d(TAG, "signOut: выход пользователя $currentEmail")
        auth.signOut()
        Log.d(TAG, "signOut: успешно")
    }

    /**
     * Восстановление пароля
     */
    suspend fun resetPassword(email: String): Result<Unit> {
        Log.d(TAG, "resetPassword: запрос для $email")
        return try {
            auth.sendPasswordResetEmail(email).await()
            Log.d(TAG, "resetPassword: письмо отправлено")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "resetPassword: ошибка - ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Проверка, авторизован ли пользователь
     */
    fun isSignedIn(): Boolean {
        val signedIn = FirebaseManager.isSignedIn
        val email = FirebaseManager.currentUser?.email
        Log.d(TAG, "isSignedIn: $signedIn, email=$email")
        return signedIn
    }

    /**
     * Получить текущую роль пользователя
     */
    suspend fun getCurrentUserRole(): UserRole? {
        val userId = auth.currentUser?.uid ?: return null
        return try {
            val document = firestore.collection("users").document(userId).get().await()
            val roleName = document.getString("role")
            roleName?.let { UserRole.valueOf(it) }
        } catch (e: Exception) {
            Log.e(TAG, "getUserRole: ошибка - ${e.message}", e)
            null
        }
    }

    /**
     * Получить класс текущего пользователя (для ученика)
     */
    suspend fun getCurrentUserGrade(): Int? {
        val userId = auth.currentUser?.uid ?: return null
        return try {
            val document = firestore.collection("users").document(userId).get().await()
            document.getLong("grade")?.toInt() ?: 7
        } catch (e: Exception) {
            Log.e(TAG, "getUserGrade: ошибка - ${e.message}", e)
            7
        }
    }

    /**
     * Получить полное имя текущего пользователя
     */
    suspend fun getCurrentUserName(): String {
        val userId = auth.currentUser?.uid ?: return "Пользователь"
        return try {
            val document = firestore.collection("users").document(userId).get().await()
            val fullName = document.getString("fullName")
            val firstName = document.getString("firstName")
            val email = document.getString("email")
            
            fullName ?: firstName ?: email ?: "Пользователь"
        } catch (e: Exception) {
            Log.e(TAG, "getUserName: ошибка - ${e.message}", e)
            "Пользователь"
        }
    }

    /**
     * Получить имя учителя по ID
     */
    suspend fun getTeacherName(teacherId: String): String {
        return try {
            val document = firestore.collection("users").document(teacherId).get().await()
            val fullName = document.getString("fullName")
            val firstName = document.getString("firstName")
            val email = document.getString("email")
            
            fullName ?: firstName ?: email ?: "Учитель"
        } catch (e: Exception) {
            Log.e(TAG, "getTeacherName: ошибка - ${e.message}", e)
            "Учитель"
        }
    }

    /**
     * Обновить имя и фамилию пользователя
     */
    suspend fun updateUserName(firstName: String, lastName: String): Result<Unit> {
        val userId = auth.currentUser?.uid ?: return Result.failure(Exception("Пользователь не авторизован"))
        Log.d(TAG, "updateUserName: обновление для userId=$userId, firstName=$firstName, lastName=$lastName")
        return try {
            val userData = mapOf(
                "firstName" to firstName,
                "lastName" to lastName,
                "fullName" to "$lastName $firstName".trim()
            )
            firestore.collection("users").document(userId).update(userData).await()
            Log.d(TAG, "updateUserName: успешно обновлено")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "updateUserName: ошибка - ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Сохранить роль пользователя (для тестирования)
     */
    suspend fun setUserRole(role: UserRole) {
        val userId = auth.currentUser?.uid ?: return
        try {
            firestore.collection("users").document(userId)
                .update("role", role.name).await()
            Log.d(TAG, "setUserRole: роль установлена = $role")
        } catch (e: Exception) {
            Log.e(TAG, "setUserRole: ошибка - ${e.message}", e)
        }
    }

    /**
     * Сохранить класс пользователя (для ученика)
     */
    suspend fun setUserGrade(grade: Int) {
        val userId = auth.currentUser?.uid ?: return
        try {
            firestore.collection("users").document(userId)
                .update("grade", grade).await()
            Log.d(TAG, "setUserGrade: класс установлен = $grade")
        } catch (e: Exception) {
            Log.e(TAG, "setUserGrade: ошибка - ${e.message}", e)
        }
    }
}
