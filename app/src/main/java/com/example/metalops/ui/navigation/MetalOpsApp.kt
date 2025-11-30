package com.example.metalops

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.rememberNavController
import com.example.metalops.core.session.SessionManager
import com.example.metalops.ui.navigation.RootNavGraph
import com.example.metalops.ui.navigation.RootRoute

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MetalOpsApp() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val sessionManager = SessionManager(context)

    val userRole by sessionManager.userRole.collectAsState(initial = null)

    val startDestination = when (userRole) {
        "Agente"   -> RootRoute.Agente.route
        "Planner"  -> RootRoute.Planner.route
        "Operario" -> RootRoute.Operario.route
        else       -> RootRoute.Auth.route
    }

    MaterialTheme {
        Surface {
            RootNavGraph(
                navController = navController,
                startDestination = startDestination,
                sessionManager = sessionManager
            )
        }
    }
}
