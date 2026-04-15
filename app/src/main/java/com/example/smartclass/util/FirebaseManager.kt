package com.example.smartclass.util

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore

private const val TAG = "FirebaseManager"


object FirebaseManager {

    val database: FirebaseDatabase by lazy {
        FirebaseDatabase.getInstance().apply {
            setPersistenceEnabled(true)
            Log.d(TAG, "Firebase Database инициализирован, persistence enabled")
        }
    }

    val auth: FirebaseAuth by lazy {
        FirebaseAuth.getInstance().apply {
            Log.d(TAG, "Firebase Auth инициализирован")
        }
    }

    val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance().apply {
            Log.d(TAG, "Firebase Firestore инициализирован")
        }
    }

    val currentUser get() = auth.currentUser

    val isSignedIn: Boolean
        get() = currentUser != null

    init {
        Log.d(TAG, "FirebaseManager создан")
    }
}
