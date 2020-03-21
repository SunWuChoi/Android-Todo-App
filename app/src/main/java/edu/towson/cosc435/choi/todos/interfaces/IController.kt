package edu.towson.cosc435.choi.todos.interfaces

interface IController {
    fun updateTodo(idx: Int)
    fun removeTodo(idx: Int)
}