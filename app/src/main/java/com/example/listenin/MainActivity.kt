package com.example.listenin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.listenin.ui.navigation.AppNavHost
import com.example.listenin.ui.theme.ListenInTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ListenInTheme {
                AppNavHost()
            }
        }
    }
}
