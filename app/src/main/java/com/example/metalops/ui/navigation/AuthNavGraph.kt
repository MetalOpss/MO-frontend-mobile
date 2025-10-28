package com.example.metalops.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.metalops.ui.auth.LoginScreen
import com.example.metalops.ui.auth.NewPasswordScreen
import com.example.metalops.ui.auth.ResetPasswordScreen
import com.example.metalops.ui.auth.RegisterScreen // 🔹 nuevo

// Rutas de autenticación
sealed class AuthRoutes(val route: String) {
    object Login : AuthRoutes("login")
    object ResetPassword : AuthRoutes("reset_password")
    object NewPassword : AuthRoutes("new_password")
    object Register : AuthRoutes("register") // 🔹 nuevo
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
                    onLoginSuccess(role)
                },
                onForgotPasswordClick = {
                    navController.navigate(AuthRoutes.ResetPassword.route)
                },
                onRegisterClick = { // 🔹 nuevo
                    navController.navigate(AuthRoutes.Register.route)
                }
            )
        }

        // Pantalla de Registro 🔹 nuevo
        composable(AuthRoutes.Register.route) {
            RegisterScreen(
                onRegisterSubmit = { name, role, email, password ->
                    // Aquí iría la llamada real a backend para crear cuenta.
                    // Por ahora: después de registrarse lo mandamos directo al login
                    // y le podrías rellenar automáticamente los campos si quisieras.
                    navController.navigate(AuthRoutes.Login.route) {
                        popUpTo("auth") { inclusive = false }
                    }
                },
                onBackToLogin = {
                    navController.popBackStack() // vuelve al login
                }
            )
        }

        // Pantalla de Restablecer Contraseña
        composable(AuthRoutes.ResetPassword.route) {
            ResetPasswordScreen(
                onSendCodeClick = { email ->
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
                    navController.navigate(AuthRoutes.Login.route) {
                        popUpTo("auth") { inclusive = false }
                    }
                }
            )
        }
    }
}
