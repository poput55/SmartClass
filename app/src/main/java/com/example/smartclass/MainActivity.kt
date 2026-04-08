package com.example.smartclass

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.smartclass.navigation.NavGraph
import com.example.smartclass.ui.theme.SmartClassTheme
import com.example.smartclass.viewmodel.AlgebraViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SmartClassTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val viewModel: AlgebraViewModel = viewModel(
                        factory = AlgebraViewModel.Factory(this)
                    )
                    NavGraph(navController = navController, viewModel = viewModel)
                }
            }
        }
    }
}
