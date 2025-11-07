package me.juansierra.unabshop.data

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

data class Producto(
    val id: String = "",
    val nombre: String = "",
    val descripcion: String = "",
    val precio: Double = 0.0,
    val stock: Int = 0,
    val imagen: String = ""
)

class FirestoreRepository {
    private val db = FirebaseFirestore.getInstance() // <- Cambiado
    private val productosCollection = db.collection("productos")

    // CREATE
    suspend fun agregarProducto(producto: Producto): Result<String> {
        return try {
            val docRef = productosCollection.add(producto).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // READ - Todos los productos
    suspend fun obtenerProductos(): Result<List<Producto>> {
        return try {
            val snapshot = productosCollection.get().await()
            val productos = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Producto::class.java)?.copy(id = doc.id)
            }
            Result.success(productos)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // READ - Un producto
    suspend fun obtenerProductoPorId(id: String): Result<Producto?> {
        return try {
            val doc = productosCollection.document(id).get().await()
            val producto = doc.toObject(Producto::class.java)?.copy(id = doc.id)
            Result.success(producto)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // UPDATE
    suspend fun actualizarProducto(producto: Producto): Result<Unit> {
        return try {
            productosCollection.document(producto.id)
                .set(producto)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // DELETE
    suspend fun eliminarProducto(id: String): Result<Unit> {
        return try {
            productosCollection.document(id).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // BÚSQUEDA
    suspend fun buscarProductos(query: String): Result<List<Producto>> {
        return try {
            val snapshot = productosCollection
                .orderBy("nombre")
                .startAt(query)
                .endAt(query + "\uf8ff")
                .get()
                .await()
            val productos = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Producto::class.java)?.copy(id = doc.id)
            }
            Result.success(productos)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}