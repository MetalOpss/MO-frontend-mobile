package com.example.metalops.ui.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.metalops.ui.auth.LoginScreen
import com.example.metalops.ui.auth.NewPasswordScreen
import com.example.metalops.ui.auth.ResetPasswordScreen

// Rutas de autenticación (sin Register)
sealed class AuthRoutes(val route: String) {
    object Login : AuthRoutes("login")
    object ResetPassword : AuthRoutes("reset_password")
    object NewPassword : AuthRoutes("new_password")
}

fun NavGraphBuilder.authNavGraph(
    navController: NavHostController,
    onLoginSuccess: (String) -> Unit // Planner / Agente / Operario
) {
    navigation(
        startDestination = AuthRoutes.Login.route,
        route = "auth"
    ) {

        // ---------- LOGIN ----------
        composable(AuthRoutes.Login.route) {
            LoginScreen(
                onLoginClick = { email, _, role ->
                    // 1) normalizamos el rol que viene del LoginScreen
                    val roleNormalized = role
                        .trim()
                        .replace("\"", "")
                        .lowercase()

                    // 2) correo fijo para admin (CAMBIA ESTO por tu correo de admin)
                    val adminEmail = "admin@metalops.com" // <-- tu correo admin aquí

                    // 3) si es admin POR ROL O POR CORREO → se va directo a su dashboard
                    val isAdminByRole = roleNormalized == "admin"
                    val isAdminByEmail = email.equals(adminEmail, ignoreCase = true)

                    if (isAdminByRole || isAdminByEmail) {
                        // 🔹 Ruta REAL del dashboard de admin
                        navController.navigate("admin_dashboard") {
                            // limpiamos el stack de auth para que no vuelva al login con back
                            popUpTo("auth") { inclusive = true }
                            launchSingleTop = true
                        }
                    } else {
                        // 🔹 Todo lo demás sigue como siempre
                        onLoginSuccess(role)
                    }
                },
                onForgotPasswordClick = {
                    navController.navigate(AuthRoutes.ResetPassword.route)
                }
            )
        }

        // ---------- RESET PASSWORD ----------
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

        // ---------- NEW PASSWORD ----------
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