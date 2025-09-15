package com.example.metalops

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.metalops.ui.BottomBar
import com.example.metalops.ui.HomeScreen
import com.example.metalops.ui.theme.MetalOpsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MetalOpsTheme {
                Scaffold(
                    bottomBar = { BottomBar() } // Barra inferior
                ) { innerPadding ->
                    HomeScreen(Modifier.padding(innerPadding)) // Contenido principal
                }
            }
        }
    }
}
