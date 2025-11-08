package com.example.metalops

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.remember
import androidx.navigation.compose.rememberNavController
import com.example.metalops.core.session.SessionManager
import com.example.metalops.ui.navigation.MetalOpsApp
import com.example.metalops.ui.navigation.RootNavGraph

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val sessionManager = remember { SessionManager(applicationContext) }
            MaterialTheme {
                Surface {
                    val navController = rememberNavController()
                    RootNavGraph(navController = navController, sessionManager = sessionManager)
                }
            }
        }
    }
}
