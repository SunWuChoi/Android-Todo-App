package edu.towson.cosc435.choi.todos

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import edu.towson.cosc435.choi.todos.models.Todo
import kotlinx.android.synthetic.main.activity_newtodo.*
import java.lang.Exception
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import com.google.gson.Gson
import android.app.DatePickerDialog
import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.android.synthetic.main.recyclerview_item.*
import java.text.SimpleDateFormat
import java.util.*


class NewTodoActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_newtodo)

        val json: String? = intent?.getStringExtra(TODO_EXTRA_KEY)

        if(json != null){
            val todo = Gson().fromJson<Todo>(json, Todo::class.java)
            title_et.setText(todo.title)
            contentsinput_et.setText(todo.contents)
            completed_cb.isChecked = todo.isCompleted
            duedatenewTv.setText(todo.dueDate)
        }




        save_btn.setOnClickListener(this)

        duedatenewTv.setOnClickListener{
            clickDatePicker(it)
        }


    }

    @RequiresApi(Build.VERSION_CODES.O)

    override fun onClick(view: View?) {
        when(view?.id){
            R.id.save_btn -> {
                var id = intent.getIntExtra(MainActivity.ID_CODE,-1)
                var dateCreated = LocalDateTime.now().format(DateTimeFormatter.ofPattern("M/d/y H:m:ss"))

                if(id == -1){
                    id = Gson().fromJson<Todo>(intent?.getStringExtra(TODO_EXTRA_KEY), Todo::class.java).id - 1
                    dateCreated = Gson().fromJson<Todo>(intent?.getStringExtra(TODO_EXTRA_KEY), Todo::class.java).dateCreated
                }
                val intent = Intent()
                val title = title_et.editableText.toString()
                val contents = contentsinput_et.editableText.toString()
                val isCompleted = completed_cb.isChecked

                val duedate = duedatenewTv.editableText.toString()
                try{
                    val todo = Todo(title, contents, isCompleted, dateCreated, duedate, id+1 )
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

    fun clickDatePicker(view: View){
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val dpd = DatePickerDialog(this, DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
            // Display Selected date in Toast
            //Toast.makeText(this, """$dayOfMonth - ${monthOfYear + 1} - $year""", Toast.LENGTH_LONG).show()
            duedatenewTv.setText("$monthOfYear/$dayOfMonth/$year")
        }, year, month, day)
        dpd.show()
    }


    companion object{
        val TODO_EXTRA_KEY = "TODO"
    }

}
