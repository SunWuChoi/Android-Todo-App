package edu.towson.cosc435.choi.todos

import edu.towson.cosc435.choi.todos.interfaces.ITodoRepository
import edu.towson.cosc435.choi.todos.models.Todo

class TodoRepository : ITodoRepository{

    private var todos: MutableList<Todo> = mutableListOf()

    override fun getCount(): Int {
        return todos.size
    }

    override fun getTodo(idx: Int): Todo {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun remove(todo: Todo) {
        todos.remove(todo)
    }

    override fun replace(idx: Int, todo: Todo) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun addTodo(todo: Todo) {
        todos.add(todo)
    }

    override fun getAll(): List<Todo> {
        return todos
    }

}