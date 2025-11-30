package com.example.metalops.data.model

data class AgentNotification(
    val id: String = "",
    val title: String = "",
    val body: String = "",
    val isRead: Boolean = false,
    val createdAt: String = "",
    val agentId: String = ""
)
