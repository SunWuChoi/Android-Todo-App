package edu.towson.cosc435.choi.todos.database

import androidx.room.*
import edu.towson.cosc435.choi.todos.models.Todo
import java.util.*

@Dao
interface TodoDao {
    @Insert
    fun addTodo(todo: Todo)

    @Update
    fun updateTodo(todo: Todo)

    @Delete
    fun deleteTodo(todo: Todo)

    @Query("select content, completed, title, create_date, id, icon_url from Todo")
    fun getAllTodos() : List<Todo>
}

class UUIDConverter {
    @TypeConverter
    fun fromString(uuid: String): UUID {
        return UUID.fromString(uuid)
    }

    @TypeConverter
    fun toString(uuid: UUID): String {
        return uuid.toString()
    }
}

@Database(entities = arrayOf(Todo::class), version = 2, exportSchema = false)
@TypeConverters(UUIDConverter::class)
abstract class TodoDatabase : RoomDatabase() {
    abstract fun todoDao(): TodoDao
}