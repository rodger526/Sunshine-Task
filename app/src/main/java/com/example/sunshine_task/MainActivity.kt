package com.example.sunshine_task

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sunshine_task.ui.home.HomeScreen
import com.example.sunshine_task.ui.theme.SunshinetaskTheme
import com.example.sunshine_task.ui.theme.viewmodel.TaskViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SunshinetaskTheme {
                val viewModel: TaskViewModel = viewModel()
                HomeScreen(viewModel = viewModel)
            }
        }
    }
}
