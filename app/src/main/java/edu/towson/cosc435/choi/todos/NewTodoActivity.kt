package edu.towson.cosc435.choi.todos

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.gson.Gson
import edu.towson.cosc435.choi.todos.models.Todo
import kotlinx.android.synthetic.main.activity_new_todo.*
import java.util.*
import kotlin.random.Random

class NewTodoActivity : AppCompatActivity() {
    lateinit var todo: Todo

    companion object {
        val TAG = NewTodoActivity::class.java.simpleName
        val TODO = "todo"
    }

    private fun makeTodo() : Todo {
        if(todo.id.equals("")) {
            return Todo(
                content = text_edit_text.editableText.toString(),
                title = title_edit_text.editableText.toString(),
                completed = newtodo_completed_checkbox.isChecked,
                id = UUID.randomUUID().toString(),
                create_date = Date().toString(),
                iconUrl = "https://api.adorable.io/avatars/".plus((1..20).shuffled().first())
            )
        } else {
            return Todo(
                content = text_edit_text.editableText.toString(),
                title = title_edit_text.editableText.toString(),
                completed = newtodo_completed_checkbox.isChecked,
                id = todo.id,
                create_date = todo.create_date,
                iconUrl = todo.iconUrl
            )
        }
    }

    private fun makeEmptyTodo() : Todo {
        return Todo(
            content = text_edit_text.editableText.toString(),
            title = title_edit_text.editableText.toString(),
            completed = newtodo_completed_checkbox.isChecked,
            id = "",
            create_date = "",
            iconUrl = ""
        )
    }

    private fun saveTodo() {
        var flag = false
        if(todo.id.equals("")){
            flag = true
        }

        val todo = makeTodo()
        val intent = Intent()
        intent.putExtra(TODO, Gson().toJson(todo))



        if(flag) {
            setResult(Activity.RESULT_OK, intent)
        } else {
            setResult(1, intent)
        }

        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_todo)

        val json: String? = intent?.getStringExtra(TODO)

        if(json != null){
            todo = Gson().fromJson<Todo>(json, Todo::class.java)
            title_edit_text.setText(todo.title)
            text_edit_text.setText(todo.content)
            newtodo_completed_checkbox.isChecked = todo.completed
        } else {
            todo = makeEmptyTodo()
        }


        save_btn.setOnClickListener { saveTodo() }
    }

}

