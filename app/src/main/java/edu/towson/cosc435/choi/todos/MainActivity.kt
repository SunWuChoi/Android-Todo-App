package edu.towson.cosc435.choi.todos

import android.app.Activity
import android.util.Log
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.gson.Gson
import edu.towson.cosc435.choi.todos.interfaces.ITodoRepository
import edu.towson.cosc435.choi.todos.models.Todo
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_newtodo.*
import kotlin.math.log

class MainActivity : AppCompatActivity() {

    lateinit var todos: ITodoRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        todos = TodoRepository()

        launchnewtodo_btn.setOnClickListener {
            launchNewTodoActivity()
        }

        logAll_btn.setOnClickListener{
            logAllTodo(todos.getAll())
        }

    }

    fun launchNewTodoActivity() {
        val intent = Intent(this, NewTodoActivity::class.java)
        startActivityForResult(intent, NEW_TODO_REQUEST_CODE)
    }

    fun logAllTodo(list: List<Todo>){
        for(item in list){
            Log.v("New Todo",item.toString())
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(resultCode){
            Activity.RESULT_OK -> {
                when(requestCode){
                    NEW_TODO_REQUEST_CODE -> {
                        val json: String? = data?.getStringExtra(NewTodoActivity.TODO_EXTRA_KEY)
                        if(json != null){
                            val todo = Gson().fromJson<Todo>(json, Todo::class.java)
                            todos.addTodo(todo)
                            Log.v("New Todo",todo.toString())
                        }

                    }
                }
            }
            Activity.RESULT_CANCELED -> {
                Toast.makeText(this,"User canceled. No Todo added",Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        val NEW_TODO_REQUEST_CODE = 1
    }
}
