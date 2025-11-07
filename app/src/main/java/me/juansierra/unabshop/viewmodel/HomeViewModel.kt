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
//Esto es lo de listar productos para el homescreen, de aqui se importa para simplificar las cosas en HomeScreen
class HomeViewModel : ViewModel() {
    private val repository = FirestoreRepository()

    private val _uiState = MutableStateFlow(HomeUiState()) //Un poco de ViewModel de la tarea anterior
    val uiState: StateFlow<HomeUiState> = _uiState

    init {
        cargarProductos()
    }

    fun cargarProductos() { //funcion para cargar productos que luego sera llamada desde home screen
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

    fun agregarProducto(producto: Producto) {//funcion para agregar productos que luego tambien sera llamada desde home screen
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            repository.agregarProducto(producto).fold(
                onSuccess = {
                    cargarProductos() // recargar la lista
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

    fun actualizarProducto(producto: Producto) { //para que la lista se valla actualizando, tambien llamada desde home screen
        viewModelScope.launch {
            repository.actualizarProducto(producto).fold(
                onSuccess = { cargarProductos() },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(error = exception.message)
                }
            )
        }
    }

    fun eliminarProducto(id: String) { //funcion para eliminar productos de la lista, a este punto este archivo es un callcenter
        viewModelScope.launch {
            repository.eliminarProducto(id).fold(
                onSuccess = { cargarProductos() },
                onFailure = { exception ->
                    _uiState.value = _uiState.value.copy(error = exception.message)
                }
            )
        }
    }

    fun buscarProductos(query: String) {//ultima funcion, buscar productos de la lista, funcion suscrita al plan de llamadas de home screen
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