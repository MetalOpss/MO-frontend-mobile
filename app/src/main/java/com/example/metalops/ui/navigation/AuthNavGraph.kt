package com.example.metalops.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.metalops.ui.auth.LoginScreen
import com.example.metalops.ui.auth.NewPasswordScreen
import com.example.metalops.ui.auth.ResetPasswordScreen

// Rutas de autenticación
sealed class AuthRoutes(val route: String) {
    object Login : AuthRoutes("login")
    object ResetPassword : AuthRoutes("reset_password")
    object NewPassword : AuthRoutes("new_password")
}

fun NavGraphBuilder.authNavGraph(
    navController: NavHostController,
    onLoginSuccess: (String) -> Unit // role: "Planner", "Operario", "Agente"
) {
    navigation(
        startDestination = AuthRoutes.Login.route,
        route = "auth"
    ) {
        // Pantalla de Login
        composable(AuthRoutes.Login.route) {
            LoginScreen(
                onLoginClick = { email, password, role ->
                    // Aquí podrías validar las credenciales con tu backend
                    // Por ahora, asumimos que el login es exitoso
                    onLoginSuccess(role)
                },
                onForgotPasswordClick = {
                    navController.navigate(AuthRoutes.ResetPassword.route)
                }
            )
        }

        // Pantalla de Restablecer Contraseña
        composable(AuthRoutes.ResetPassword.route) {
            ResetPasswordScreen(
                onSendCodeClick = { email ->
                    // Aquí enviarías el código al email
                    navController.navigate(AuthRoutes.NewPassword.route)
                },
                onCancelClick = {
                    navController.popBackStack()
                }
            )
        }

        // Pantalla de Nueva Contraseña
        composable(AuthRoutes.NewPassword.route) {
            NewPasswordScreen(
                onSendCodeClick = { newPassword, confirmPassword ->
                    // Aquí actualizarías la contraseña
                    // Si es exitoso, volver al login
                    navController.navigate(AuthRoutes.Login.route) {
                        popUpTo("auth") { inclusive = false }
                    }
                }
            )
        }
    }
}