package edu.towson.cosc435.choi.todos.interfaces

import edu.towson.cosc435.choi.todos.models.Todo

interface ITodoRepository {
    fun getCount(): Int
    fun getTodo(idx: Int): Todo
    fun getAll(): List<Todo>
    fun remove(todo: Todo)
    fun replace(idx: Int, todo: Todo)
    fun addTodo(todo: Todo)
}