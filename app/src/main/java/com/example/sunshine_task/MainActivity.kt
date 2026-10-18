package com.example.sunshine_task

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.sunshine_task.ui.navigation.AppNavGraph
import com.example.sunshine_task.ui.theme.SunshinetaskTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SunshinetaskTheme {
                AppNavGraph()
            }
        }
    }
}