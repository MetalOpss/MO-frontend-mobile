MetalOps – Plataforma Móvil para Gestión Operativa Industrial

MetalOps es una aplicación móvil multiplataforma desarrollada en Android (Kotlin + Jetpack Compose) que permite gestionar operaciones industriales mediante flujos personalizados para 4 roles principales:
Admin, Planner, Agente y Operario.

El sistema facilita la creación, asignación, seguimiento y cierre de Órdenes de Trabajo (OT), así como la comunicación entre equipos operativos.
Está construido bajo el patrón MVVM, usa Firebase Authentication, Firestore, animaciones modernas, componentes reutilizables y navegación estructurada.

👨‍💻 Desarrollado por

Alexander Alcocer Flores – Tecsup Arequipa

Alexander Vásquez Montes – Tecsup Arequipa

🔐 Cuentas de acceso por rol (testing)
Rol	Correo	Contraseña
Agente	gabo@metalops.com	gabo123
Planificador	alcocer@metalops.com	alcocer123
Operario	alex@metalops.com	alex123
Admin	admin@metalops.com	admin123
📱 Características principales
🧩 1. Autenticación y Roles

Login con Firebase Authentication

Redirección automática por rol

Persistencia de sesión

Manejo de token e información del usuario

🛠️ 2. Gestión de Órdenes de Trabajo (OT)

Crear OT (Agente / Planner)

Asignación a operarios

Visualización detallada de cada OT

Línea de tiempo del ciclo de vida

Actualización de estados

Historial completo por rol

🧾 3. Módulos por rol
Admin

Gestión de usuarios

Roles

Información general del sistema

Planner

Asignación de OT

Calendario y planificación

Filtros por estados

Notificaciones por nuevas asignaciones

Agente

Creación de OT

Subida simulada de archivos adjuntos

Selección de clientes mediante menú desplegable

Seguimiento de sus órdenes

Operario

OT asignadas

Cambio de estado en tiempo real

Notificaciones simuladas (Leídas / No leídas)

Visualización de detalles técnicos

🧰 Tecnologías empleadas

Kotlin

Android Jetpack Compose

Firebase Authentication

Firestore (Base de datos NoSQL)

ViewModel + StateFlow

Navegación Compose

Coroutines

Material 3 UI

Animaciones Compose

Arquitectura MVVM

📂 Estructura del proyecto
MO-frontend-mobile/
│
├── core/
│   ├── navigation/
│   ├── ui/components/
│   └── utils/
│
├── data/
│   ├── model/
│   ├── repository/
│   └── remote/
│
├── ui/
│   ├── admin/
│   ├── planner/
│   ├── agente/
│   └── operario/
│
└── resources/
    ├── drawable/
    └── icons/

▶️ Cómo ejecutar

Clonar el repositorio:

git clone https://github.com/MetalOpss/MO-frontend-mobile.git


Abrir en Android Studio

Sincronizar Gradle

Ejecutar en dispositivo físico o emulador API 26+

📝 Estado actual

App 100% funcional para exposición

Roles y navegación listos

Notificaciones simuladas

Flujo completo de OT

Perfíl operario, planner y agente rediseñados

Historial y línea de tiempo funcional

📌 Próximas mejoras

Integración real con Firebase Storage

Dashboard general

Estadísticas por rol

Generación PDF de OT

Soporte para modo offline
