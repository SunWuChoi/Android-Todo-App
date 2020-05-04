package edu.towson.cosc435.choi.todos.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.util.*

@Entity
data class Todo (
    @ColumnInfo(name = "content")
    var content: String,
    @ColumnInfo(name = "completed")
    var completed: Boolean,
    @ColumnInfo(name = "title")
    var title: String,
    @ColumnInfo(name = "create_date")
    var create_date: String,
    @PrimaryKey
    @ColumnInfo(name = "id")
    var id: String,
    @ColumnInfo(name = "icon_url")
    val iconUrl: String
) {
    override fun toString(): String {
        return String.format("New Todo Log\n"+"Title       : " + title + "\nContent     : " + content + "\nis completed: " + completed + "\nDate created: " + create_date)
    }
}
