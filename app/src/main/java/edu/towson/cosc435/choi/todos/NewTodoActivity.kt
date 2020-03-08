package edu.towson.cosc435.choi.todos

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import edu.towson.cosc435.choi.todos.models.Todo
import kotlinx.android.synthetic.main.activity_newtodo.*
import java.lang.Exception
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import com.google.gson.Gson


class NewTodoActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_newtodo)

        save_btn.setOnClickListener(this)

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onClick(view: View?) {
        when(view?.id){
            R.id.save_btn -> {
                val intent = Intent()
                val title = title_et.editableText.toString()
                val contents = contentsinput_et.editableText.toString()
                val isCompleted = completed_cb.isChecked
                val dateCreated = LocalDateTime.now().format(DateTimeFormatter.ofPattern("M/d/y H:m:ss"))
                try{
                    val todo = Todo(title, contents, isCompleted, dateCreated )
                    val json = Gson().toJson(todo)
                    intent.putExtra(TODO_EXTRA_KEY, json)
                    setResult(Activity.RESULT_OK,intent)
                    finish()
                }catch(ex: Exception){
                    Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    companion object{
        val TODO_EXTRA_KEY = "TODO"
    }

}
