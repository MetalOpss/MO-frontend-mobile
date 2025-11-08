package com.example.metalops.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.metalops.ui.auth.LoginScreen
import com.example.metalops.ui.auth.NewPasswordScreen
import com.example.metalops.ui.auth.ResetPasswordScreen
import com.example.metalops.ui.auth.RegisterScreen

// Rutas de autenticación
sealed class AuthRoutes(val route: String) {
    object Login : AuthRoutes("login")
    object ResetPassword : AuthRoutes("reset_password")
    object NewPassword : AuthRoutes("new_password")
    object Register : AuthRoutes("register")
}

fun NavGraphBuilder.authNavGraph(
    navController: NavHostController,
    onLoginSuccess: (String) -> Unit // role: "Planner", "Operario", "Agente"
) {
    navigation(
        startDestination = AuthRoutes.Login.route,
        route = "auth"
    ) {
        // LOGIN (NO toques LoginScreen: no requiere onRegisterClick)
        composable(AuthRoutes.Login.route) {
            LoginScreen(
                onLoginClick = { _, _, role ->
                    onLoginSuccess(role)
                },
                onForgotPasswordClick = {
                    navController.navigate(AuthRoutes.ResetPassword.route)
                }
            )
        }

        // REGISTER (ajustado a tu firma)
        composable(AuthRoutes.Register.route) {
            RegisterScreen(
                onRegisterSubmit = { name, role, email, password ->
                    // TODO: aquí puedes llamar a tu API de registro si quieres.
                    // Tras registrar, regresa a Login.
                    navController.navigate(AuthRoutes.Login.route) {
                        popUpTo("auth") { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onBackToLogin = {
                    // Volver a Login sin nombres de parámetros (compatibilidad)
                    navController.popBackStack(AuthRoutes.Login.route, false)
                }
            )
        }

        // RESET PASSWORD
        composable(AuthRoutes.ResetPassword.route) {
            ResetPasswordScreen(
                onSendCodeClick = { _ ->
                    navController.navigate(AuthRoutes.NewPassword.route)
                },
                onCancelClick = {
                    navController.popBackStack()
                }
            )
        }

        // NEW PASSWORD
        composable(AuthRoutes.NewPassword.route) {
            NewPasswordScreen(
                onSendCodeClick = { _, _ ->
                    navController.navigate(AuthRoutes.Login.route) {
                        popUpTo("auth") { inclusive = false }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}
