package com.example.metalops.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun NotificationList(
    initialItems: List<NotificationModel>,
    filter: TabSelection,
    onToggleRead: (Int) -> Unit,
    onOpen: (Int) -> Unit
) {
    var items by remember { mutableStateOf(initialItems) }

    // keep internal list synced if initialItems change
    LaunchedEffect(initialItems) { items = initialItems }

    val filtered = when (filter) {
        TabSelection.READ -> items.filter { it.read }
        TabSelection.UNREAD -> items.filter { !it.read }
    }

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier.padding(top = 8.dp)
    ) {
        items(filtered) { n ->
            NotificationItemDetailed(
                item = n,
                modifier = Modifier.fillMaxWidth(),
                onToggleRead = { id ->
                    // update internal state
                    items = items.map { if (it.id == id) it.copy(read = !it.read) else it }
                    onToggleRead(id)
                },
                onOpen = { id -> onOpen(id) }
            )
        }
    }
}
