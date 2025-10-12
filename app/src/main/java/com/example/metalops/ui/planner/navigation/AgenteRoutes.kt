package com.example.metalops.ui.planner.navigation

sealed class AgenteRoutes(val route: String) {
    object Home : AgenteRoutes("home")
    object Clientes : AgenteRoutes("clientes")
    object Ordenes : AgenteRoutes("ots")

    object RegistrarCliente : AgenteRoutes("registrar_cliente")
    object EditarCliente : AgenteRoutes("editar_cliente")

    object Perfil : AgenteRoutes("perfil")
    object Notificaciones : AgenteRoutes("notificaciones")
    object Configuracion : AgenteRoutes("configuracion")

    object InfoBuild : AgenteRoutes("info_build")
    object InfoTeam : AgenteRoutes("info_team")
    object Contacto : AgenteRoutes("contacto")

    object CrearOTPaso1 : AgenteRoutes("crear_ot_paso1")
    object CrearOTPaso2 : AgenteRoutes("crear_ot_paso2")
    object CrearOTPaso3 : AgenteRoutes("crear_ot_paso3")
    object CrearOTPaso3_1 : AgenteRoutes("crear_ot_paso3_1")
    object CrearOTPaso4 : AgenteRoutes("crear_ot_paso4")
    object CrearOTPaso5 : AgenteRoutes("crear_ot_paso5")
}
