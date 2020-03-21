package edu.towson.cosc435.choi.todos.models

data class Todo (
    val title: String,
    val contents: String,
    var isCompleted: Boolean,
    //val Image: String,
    val dateCreated: String,
    val dueDate: String,
    val id: Int
) {
    override fun toString(): String {
        return String.format("New Todo Log\n"+"Title       : " + title + "\nContent     : " + contents + "\nis completed: " + isCompleted + "\nDate created: " + dateCreated)
    }
}