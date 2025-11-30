package com.example.metalops.ui.agente.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.metalops.core.ui.components.AppHeader
import com.example.metalops.data.model.Client
import com.example.metalops.data.remote.ClientesRepository
import com.example.metalops.ui.agente.theme.MetalOpsTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ClientesScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val repository = remember { ClientesRepository() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var clients by remember { mutableStateOf<List<Client>>(emptyList()) }

    // Búsqueda
    var searchQuery by remember { mutableStateOf("") }

    // Cliente seleccionado para modal de detalle
    var selectedClient by remember { mutableStateOf<Client?>(null) }

    // Modal de registro
    var showRegisterModal by remember { mutableStateOf(false) }

    // Cargar clientes al entrar
    LaunchedEffect(Unit) {
        loadClients(
            repository = repository,
            onResult = { clients = it },
            onError = { errorMessage = it },
            onLoadingChange = { isLoading = it }
        )
    }

    // Filtrar por búsqueda
    val filteredClients by remember(clients, searchQuery) {
        mutableStateOf(
            if (searchQuery.isBlank()) {
                clients
            } else {
                clients.filter { client ->
                    client.name.contains(searchQuery, ignoreCase = true) ||
                            client.company.contains(searchQuery, ignoreCase = true) ||
                            client.document.contains(searchQuery, ignoreCase = true)
                }
            }
        )
    }

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
            // Header compartido (MetalOps + logo)
            AppHeader()

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Clientes",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )

            Text(
                text = "Busca, registra y gestiona tus clientes.",
                style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // ---------------- BÚSQUEDA ARRIBA ----------------
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Buscar cliente") },
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Buscar"
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primaryBlue,
                    cursorColor = primaryBlue
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // ---------------- BOTÓN REGISTRAR CLIENTE ----------------
            Button(
                onClick = { showRegisterModal = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = primaryBlue
                )
            ) {
                Icon(
                    imageVector = Icons.Default.PersonAdd,
                    contentDescription = "Registrar cliente",
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(text = "Registrar cliente")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Listado de clientes",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )

            Spacer(modifier = Modifier.height(8.dp))

            // ---------------- LISTA DE CLIENTES ----------------
            if (filteredClients.isEmpty() && !isLoading) {
                Text(
                    text = "No hay clientes registrados.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredClients) { client ->
                        ClientCard(
                            client = client,
                            primaryBlue = primaryBlue,
                            onClick = { selectedClient = client }
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

        // Modal detalle cliente
        selectedClient?.let { client ->
            ClientDetailModal(
                client = client,
                onDismiss = { selectedClient = null }
            )
        }

        // Modal registro cliente
        if (showRegisterModal) {
            RegisterClientModal(
                onDismiss = { showRegisterModal = false },
                onClientSaved = {
                    // Recargar lista después de guardar
                    scope.launch {
                        loadClients(
                            repository = repository,
                            onResult = { clients = it },
                            onError = { errorMessage = it },
                            onLoadingChange = { isLoading = it }
                        )
                    }
                },
                repository = repository,
                scope = scope,
                primaryBlue = primaryBlue
            )
        }
    }
}

/* ----------------- FUNCIONES AUXILIARES ----------------- */

private suspend fun loadClients(
    repository: ClientesRepository,
    onResult: (List<Client>) -> Unit,
    onError: (String?) -> Unit,
    onLoadingChange: (Boolean) -> Unit
) {
    onLoadingChange(true)
    onError(null)
    try {
        val result = repository.getClients()
        onResult(result)
    } catch (e: Exception) {
        e.printStackTrace()
        onError(e.message ?: "Error al obtener clientes")
    } finally {
        onLoadingChange(false)
    }
}

/* ----------------- UI DE LAS CARDS ----------------- */

@Composable
private fun ClientCard(
    client: Client,
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
            modifier = Modifier
                .padding(12.dp)
        ) {
            Text(
                text = client.name,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = client.company,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.Gray
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Doc: ${client.document}",
                style = MaterialTheme.typography.bodySmall
            )
            if (client.email.isNotBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = client.email,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = primaryBlue
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/* ----------------- MODAL DETALLE CLIENTE ----------------- */

@Composable
private fun ClientDetailModal(
    client: Client,
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
            modifier = Modifier.fillMaxWidth(0.9f),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .clickable(enabled = false) {} // para que el click no cierre el modal
            ) {
                Text(
                    text = client.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = client.company,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color.Gray
                    )
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Documento: ${client.document}",
                    style = MaterialTheme.typography.bodyMedium
                )
                if (client.email.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Correo: ${client.email}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                if (client.phone.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Teléfono: ${client.phone}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

/* ----------------- MODAL REGISTRO CLIENTE ----------------- */

@Composable
private fun RegisterClientModal(
    onDismiss: () -> Unit,
    onClientSaved: () -> Unit,
    repository: ClientesRepository,
    scope: CoroutineScope,
    primaryBlue: Color
) {
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var company by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var document by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x80000000))
            .clickable { if (!isSaving) onDismiss() },
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(0.9f),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .clickable(enabled = false) {}
            ) {
                Text(
                    text = "Registrar cliente",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre del cliente") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = primaryBlue,
                        cursorColor = primaryBlue
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = company,
                    onValueChange = { company = it },
                    label = { Text("Empresa") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = primaryBlue,
                        cursorColor = primaryBlue
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Correo") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = primaryBlue,
                        cursorColor = primaryBlue
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Teléfono") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = primaryBlue,
                        cursorColor = primaryBlue
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = document,
                    onValueChange = { document = it },
                    label = { Text("Documento (DNI / RUC)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = primaryBlue,
                        cursorColor = primaryBlue
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (name.isBlank() || company.isBlank()) {
                            Toast.makeText(
                                context,
                                "Nombre y empresa son obligatorios",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@Button
                        }

                        scope.launch {
                            isSaving = true
                            val result = repository.addClient(
                                name = name,
                                company = company,
                                email = email,
                                phone = phone,
                                document = document
                            )

                            result.onSuccess {
                                Toast.makeText(
                                    context,
                                    "Cliente registrado correctamente",
                                    Toast.LENGTH_SHORT
                                ).show()
                                isSaving = false
                                onClientSaved()
                                onDismiss()
                            }

                            result.onFailure {
                                Toast.makeText(
                                    context,
                                    it.message ?: "Error al registrar cliente",
                                    Toast.LENGTH_LONG
                                ).show()
                                isSaving = false
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    enabled = !isSaving,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = primaryBlue
                    )
                ) {
                    Text(text = if (isSaving) "Guardando..." else "Guardar cliente")
                }
            }
        }
    }
}

/* ----------------- PREVIEW ----------------- */

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ClientesScreenPreview() {
    MetalOpsTheme {
        val navController = rememberNavController()
        ClientesScreen(navController = navController)
    }
}