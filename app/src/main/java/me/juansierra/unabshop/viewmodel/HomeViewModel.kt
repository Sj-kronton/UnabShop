package me.juansierra.unabshop.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import me.juansierra.unabshop.data.FirestoreRepository
import me.juansierra.unabshop.data.Producto

data class HomeUiState(
    val productos: List<Producto> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = ""
)

class HomeViewModel : ViewModel() {
    private val repository = FirestoreRepository()

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        cargarProductos()
    }

    fun cargarProductos() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            repository.obtenerProductos().fold(
                onSuccess = { productos ->
                    _uiState.value = _uiState.value.copy(
                        productos = productos,
                        isLoading = false
                    )
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        error = exception.message ?: "Error desconocido",
                        isLoading = false
                    )
                }
            )
        }
    }

    fun agregarProducto(producto: Producto) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            repository.agregarProducto(producto).fold(
                onSuccess = {
                    cargarProductos() // Recargar la lista
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(
                        error = exception.message,
                        isLoading = false
                    )
                }
            )
        }
    }

    fun actualizarProducto(producto: Producto) {
        viewModelScope.launch {
            repository.actualizarProducto(producto).fold(
                onSuccess = { cargarProductos() },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(error = exception.message)
                }
            )
        }
    }

    fun eliminarProducto(id: String) {
        viewModelScope.launch {
            repository.eliminarProducto(id).fold(
                onSuccess = { cargarProductos() },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(error = exception.message)
                }
            )
        }
    }

    fun buscarProductos(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)

        if (query.isEmpty()) {
            cargarProductos()
            return
        }

        viewModelScope.launch {
            repository.buscarProductos(query).fold(
                onSuccess = { productos ->
                    _uiState.value = _uiState.value.copy(productos = productos)
                },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(error = exception.message)
                }
            )
        }
    }
}