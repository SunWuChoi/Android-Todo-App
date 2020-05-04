package edu.towson.cosc435.choi.todos.interfaces

import android.graphics.Bitmap
import edu.towson.cosc435.choi.todos.models.Todo
import kotlinx.coroutines.CoroutineScope

interface ITodoController : CoroutineScope{
    val todosRepo: ITodoRepository
    suspend fun toggleCompleted(pos: Int)
    fun editTodo(pos: Int)
    suspend fun deleteTodo(pos: Int)
    suspend fun addNewTodo(todo: Todo)
    suspend fun update()

    suspend fun fetchTodos(): List<Todo>
    suspend fun fetchIcon(url: String): Bitmap
    suspend fun checkCache(icon: String): Bitmap?
    suspend fun cacheIcon(filename: String, icon: Bitmap)


}