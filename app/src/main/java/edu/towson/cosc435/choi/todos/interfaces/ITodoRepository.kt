package edu.towson.cosc435.choi.todos.interfaces

import edu.towson.cosc435.choi.todos.models.Todo
import kotlinx.coroutines.CoroutineScope
import java.util.*

interface ITodoRepository {
    fun GetTodos(): List<Todo>
    fun GetTodo(pos: Int): Todo
    suspend fun AddTodo(todo: Todo)
    suspend fun UpdateTodo(pos: Int, todo: Todo)
    suspend fun DeleteTodo(todo: Todo)
    fun GetCount(): Int
}
