package edu.towson.cosc435.choi.todos.models

data class Todo (
    val Title: String,
    val Contents: String,
    val IsCompleted: Boolean,
    //val Image: String,
    val DateCreated: String
) {
    override fun toString(): String {
        return String.format("New Todo Log\n"+"Title       : " + Title + "\nContent     : " + Contents + "\nis completed: " + IsCompleted + "\nDate created: " + DateCreated)
    }
}