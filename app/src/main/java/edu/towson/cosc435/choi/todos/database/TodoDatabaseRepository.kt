package edu.towson.cosc435.choi.todos.database

import android.content.Context
import androidx.room.Room
import edu.towson.cosc435.choi.todos.interfaces.ITodoRepository
import edu.towson.cosc435.choi.todos.models.Todo
import edu.towson.cosc435.choi.todos.network.TodoApi
import kotlinx.coroutines.delay
import java.lang.Exception

class TodoDatabaseRepository(ctx: Context) : ITodoRepository {
    private val todoList: MutableList<Todo> = mutableListOf()
    private val db: TodoDatabase = Room.databaseBuilder(
        ctx,
        TodoDatabase::class.java,
        "todos.db"
    ).build()

    override fun GetTodos(): List<Todo> {
        if(todoList.size == 0){
            refreshTodoList()
        }
        return todoList
    }

    override fun GetTodo(pos: Int): Todo {
        return todoList[pos]
    }

    override suspend fun AddTodo(todo: Todo) {
        //delay(2000)
        db.todoDao().addTodo(todo)
        refreshTodoList()
    }

    override suspend fun UpdateTodo(pos: Int, todo: Todo) {
        //delay(2000)
        db.todoDao().updateTodo(todo)
        refreshTodoList()
    }

    override suspend fun DeleteTodo(todo: Todo) {
        //delay(2000)
        db.todoDao().deleteTodo(todo)
        refreshTodoList()
    }

    override fun GetCount(): Int {
        return todoList.size
    }

    fun refreshTodoList() {
        todoList.clear()
        val todos = db.todoDao().getAllTodos()
        todoList.addAll(todos)
    }
}