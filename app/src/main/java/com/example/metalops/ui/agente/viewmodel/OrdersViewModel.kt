package com.example.metalops.ui.agente.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.metalops.data.model.Order
import com.example.metalops.data.remote.OrdersRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class OrdersViewModel(
    private val repository: OrdersRepository = OrdersRepository()
) : ViewModel() {

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders

    fun loadOrders(agentId: String) {
        viewModelScope.launch {
            val data = repository.getOrdersByAgent(agentId)
            _orders.value = data
        }
    }
}
