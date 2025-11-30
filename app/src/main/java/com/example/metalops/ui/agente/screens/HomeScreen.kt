package com.example.metalops.ui.agente.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.metalops.core.session.SessionManager
import com.example.metalops.core.ui.components.AppHeader
import com.example.metalops.core.ui.components.FabMenu
import com.example.metalops.core.ui.components.Task
import com.example.metalops.data.remote.AgenteTasksRepository
import com.example.metalops.ui.agente.navigation.Destinations
import com.example.metalops.ui.agente.theme.MetalOpsTheme
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun HomeScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val repository = remember { AgenteTasksRepository() }
    val scope = rememberCoroutineScope()

    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }

    var selectedTab by remember { mutableStateOf("Pendientes") }
    var tasks by remember { mutableStateOf<List<Task>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    var userName by remember { mutableStateOf("") }
    var selectedTask by remember { mutableStateOf<Task?>(null) }

    // Cargar nombre del usuario
    LaunchedEffect(Unit) {
        userName = sessionManager.getUserName() ?: ""
    }

    // Cargar tareas desde Firebase
    LaunchedEffect(Unit) {
        loadTasks(
            repository = repository,
            onResult = { tasks = it },
            onError = { errorMessage = it },
            onLoadingChange = { isLoading = it }
        )
    }

    // Filtrado para la lista principal
    val filteredTasks by remember(tasks, selectedTab) {
        mutableStateOf(filterTasksByStatus(tasks, selectedTab))
    }

    // Fecha actual
    val todayText = remember {
        val formatter = DateTimeFormatter.ofPattern("d 'de' MMMM yyyy", Locale("es", "PE"))
        LocalDate.now().format(formatter)
    }

    HomeScreenContent(
        modifier = modifier,
        userName = userName,
        todayText = todayText,
        selectedTab = selectedTab,
        onTabSelected = { selectedTab = it },
        allTasks = tasks,
        tasks = filteredTasks,
        isLoading = isLoading,
        errorMessage = errorMessage,
        onRetry = {
            scope.launch {
                loadTasks(
                    repository = repository,
                    onResult = { tasks = it },
                    onError = { errorMessage = it },
                    onLoadingChange = { isLoading = it }
                )
            }
        },
        onTaskClick = { selectedTask = it },
        selectedTask = selectedTask,
        onDismissModal = { selectedTask = null },
        onNotificacionesClick = {
            navController.navigate(Destinations.NOTIFICACIONES)
        },
        onPerfilClick = {
            navController.navigate(Destinations.PERFIL)
        },
        onConfiguracionClick = {
            navController.navigate(Destinations.CONFIGURACION)
        }
    )
}

@Composable
private fun HomeScreenContent(
    modifier: Modifier = Modifier,
    userName: String,
    todayText: String,
    selectedTab: String,
    onTabSelected: (String) -> Unit,
    allTasks: List<Task>,
    tasks: List<Task>,
    isLoading: Boolean,
    errorMessage: String?,
    onRetry: () -> Unit,
    onTaskClick: (Task) -> Unit,
    selectedTask: Task?,
    onDismissModal: () -> Unit,
    onNotificacionesClick: () -> Unit,
    onPerfilClick: () -> Unit,
    onConfiguracionClick: () -> Unit
) {
    val primaryBlue = Color(0xFF1976D2)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp)
    ) {

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header
            AppHeader()

            Spacer(modifier = Modifier.height(8.dp))

            // Bienvenida + fecha
            Text(
                text = "Bienvenido${if (userName.isNotBlank()) ", $userName" else ""}",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = todayText,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color.Gray
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // -------- Stats horizontales: Tareas hoy / Urgentes / Completadas --------
            StatsSummaryRow(allTasks = allTasks)

            Spacer(modifier = Modifier.height(16.dp))

            // Error Firebase
            if (errorMessage != null) {
                Text(
                    text = errorMessage,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.clickable { onRetry() }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // -------- Filtros Pendientes / Urgentes / Completadas --------
            TasksFilterRow(
                selectedTab = selectedTab,
                onTabSelected = onTabSelected,
                primaryBlue = primaryBlue
            )

            Spacer(modifier = Modifier.height(12.dp))

            // -------- Lista de tareas debajo de los filtros --------
            if (tasks.isEmpty() && !isLoading) {
                Text(
                    text = "No hay tareas para esta categoría.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(tasks) { task ->
                        TaskListCard(
                            task = task,
                            primaryBlue = primaryBlue,
                            onClick = { onTaskClick(task) }
                        )
                    }
                }
            }
        }

        // Loader
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x33000000)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        // FAB
        FabMenu(
            modifier = Modifier.align(Alignment.BottomEnd),
            onNotificacionesClick = onNotificacionesClick,
            onPerfilClick = onPerfilClick,
            onConfiguracionClick = onConfiguracionClick
        )

        // Modal detalle tarea
        if (selectedTask != null) {
            TaskDetailModal(
                task = selectedTask,
                onDismiss = onDismissModal
            )
        }
    }
}

/* ------------------------ STATS HORIZONTALES ------------------------ */

@Composable
private fun StatsSummaryRow(
    allTasks: List<Task>
) {
    val primaryBlue = Color(0xFF1976D2)

    val totalToday = allTasks.size
    val urgentCount = allTasks.count { it.status.equals("urgente", ignoreCase = true) }
    val completedCount = allTasks.count { it.status.equals("completada", ignoreCase = true) }

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            StatCard(
                title = "Órdenes asignadas",
                value = totalToday.toString(),
                background = primaryBlue,
                textColor = Color.White
            )
        }
        item {
            StatCard(
                title = "Urgentes",
                value = urgentCount.toString(),
                background = Color.White,
                textColor = Color.Black,
                border = true
            )
        }
        item {
            StatCard(
                title = "Completadas",
                value = completedCount.toString(),
                background = Color.White,
                textColor = Color.Black,
                border = true
            )
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    background: Color,
    textColor: Color,
    border: Boolean = false
) {
    Card(
        modifier = Modifier
            .width(150.dp)
            .height(80.dp),
        shape = CardDefaults.shape,
        colors = CardDefaults.cardColors(
            containerColor = background
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (border) 0.dp else 4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = if (background == Color.White) Color.Gray else Color.White
                )
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = textColor
                )
            )
        }
    }
}

/* ------------------------ FILTROS + LISTA ------------------------ */

@Composable
private fun TasksFilterRow(
    selectedTab: String,
    onTabSelected: (String) -> Unit,
    primaryBlue: Color
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        FilterChipMetalOps(
            text = "Pendientes",
            isSelected = selectedTab == "Pendientes",
            onClick = { onTabSelected("Pendientes") },
            primaryBlue = primaryBlue
        )
        FilterChipMetalOps(
            text = "Urgentes",
            isSelected = selectedTab == "Urgentes",
            onClick = { onTabSelected("Urgentes") },
            primaryBlue = primaryBlue
        )
        FilterChipMetalOps(
            text = "Completadas",
            isSelected = selectedTab == "Completadas",
            onClick = { onTabSelected("Completadas") },
            primaryBlue = primaryBlue
        )
    }
}

@Composable
private fun FilterChipMetalOps(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    primaryBlue: Color
) {
    Box(
        modifier = Modifier
            .height(36.dp)
            .background(
                color = if (isSelected) primaryBlue else Color.Transparent,
                shape = CardDefaults.shape
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = if (isSelected) Color.White else Color.Gray,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
            )
        )
    }
}

@Composable
private fun TaskListCard(
    task: Task,
    primaryBlue: Color,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = task.time,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color.Gray
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = task.name,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = task.description,
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .background(
                        primaryBlue.copy(alpha = 0.08f),
                        shape = CardDefaults.shape
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = task.status,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = primaryBlue,
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }
    }
}

/* ------------------------ MODAL DETALLE ------------------------ */

@Composable
private fun TaskDetailModal(
    task: Task,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x80000000))
            .clickable { onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .clickable(enabled = false) {},
            ) {
                Text(
                    text = task.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = task.time,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color.Gray
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Estado: ${task.status}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }
        }
    }
}

/* ------------------------ LÓGICA AUXILIAR ------------------------ */

private suspend fun loadTasks(
    repository: AgenteTasksRepository,
    onResult: (List<Task>) -> Unit,
    onError: (String?) -> Unit,
    onLoadingChange: (Boolean) -> Unit
) {
    onLoadingChange(true)
    onError(null)

    try {
        val result = repository.getTasks()
        onResult(result)
    } catch (e: Exception) {
        e.printStackTrace()
        onError(e.message ?: "Error al obtener las tareas")
    } finally {
        onLoadingChange(false)
    }
}

private fun filterTasksByStatus(
    tasks: List<Task>,
    selectedTab: String
): List<Task> {
    return when (selectedTab) {
        "Pendientes" ->
            tasks.filter { it.status.equals("pendiente", ignoreCase = true) }

        "Urgentes" ->
            tasks.filter { it.status.equals("urgente", ignoreCase = true) }

        "Completadas" ->
            tasks.filter { it.status.equals("completada", ignoreCase = true) }

        else -> tasks
    }
}

/* ------------------------ PREVIEW ------------------------ */

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun HomeScreenPreview() {
    MetalOpsTheme {
        val fakeTasks = listOf(
            Task(
                time = "2025-11-27",
                name = "Cambio de filtros",
                description = "Realizar cambio de filtros de aire.",
                status = "pendiente"
            ),
            Task(
                time = "2025-11-27",
                name = "Reparación de fuga de aceite",
                description = "Detener la fuga en la línea principal del compresor.",
                status = "urgente"
            ),
            Task(
                time = "2025-11-26",
                name = "Inspección motor secundario",
                description = "Inspección realizada y sin anomalías.",
                status = "completada"
            )
        )

        HomeScreenContent(
            userName = "Alexander",
            todayText = "27 de noviembre 2025",
            selectedTab = "Pendientes",
            onTabSelected = {},
            allTasks = fakeTasks,
            tasks = fakeTasks,
            isLoading = false,
            errorMessage = null,
            onRetry = {},
            onTaskClick = {},
            selectedTask = null,
            onDismissModal = {},
            onNotificacionesClick = {},
            onPerfilClick = {},
            onConfiguracionClick = {}
        )
    }
}
