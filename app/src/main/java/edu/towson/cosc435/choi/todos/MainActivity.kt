package edu.towson.cosc435.choi.todos

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import edu.towson.cosc435.choi.todos.database.TodoDatabaseRepository
import edu.towson.cosc435.choi.todos.interfaces.ITodoRepository
import edu.towson.cosc435.choi.todos.interfaces.ITodoController
import edu.towson.cosc435.choi.todos.models.Todo
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), ITodoController {
    override val coroutineContext: CoroutineContext
        get() = lifecycleScope.coroutineContext

    override suspend fun toggleCompleted(pos: Int) {
        val todo = todosRepo.GetTodo(pos)
        val newTodo = todo.copy(completed = !todo.completed)
        try {
            withContext(Dispatchers.IO) {
                todosRepo.UpdateTodo(pos, newTodo)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error: ${e.message}")
            Toast.makeText(this, "Failed to update song", Toast.LENGTH_SHORT).show()
            throw e
        }
    }

    override fun editTodo(pos: Int) {
        val todo = todosRepo.GetTodo(pos)
        val intent = Intent(this, NewTodoActivity::class.java)
        intent.putExtra(NewTodoActivity.TODO, Gson().toJson(todo))
        startActivityForResult(intent, REQUEST_CODE)
    }

    override suspend fun deleteTodo(pos: Int) {
        val current = todosRepo.GetTodo(pos)
            try {
                withContext(Dispatchers.IO) {
                    todosRepo.DeleteTodo(current)
                }
            } catch (e: Exception){
                Log.e(TAG, "Error ${e.message}")
                Toast.makeText(this, "Failed to delete song", Toast.LENGTH_SHORT).show()
                throw e
            }
    }

    override lateinit var todosRepo: ITodoRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        todosRepo = TodoDatabaseRepository(this)

        add_todo_btn.setOnClickListener { launchNewTodoActivity() }
        recyclerView.adapter = TodoAdapter(this)
        recyclerView.layoutManager = LinearLayoutManager(this)

    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            update()
        }
    }

    private fun launchNewTodoActivity() {
        val intent = Intent(this, NewTodoActivity::class.java)
        startActivityForResult(intent,
            REQUEST_CODE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(requestCode) {
            REQUEST_CODE -> {
                when(resultCode) {
                    Activity.RESULT_OK -> {
                        val todo = Gson().fromJson<Todo>(data?.getStringExtra(NewTodoActivity.TODO), Todo::class.java)
                        lifecycleScope.launch {
                            addNewTodo(todo)
                            update()
                        }
                    }
                    1 -> {
                        val todo = Gson().fromJson<Todo>(data?.getStringExtra(NewTodoActivity.TODO), Todo::class.java)
                        lifecycleScope.launch {
                            try {
                                withContext(Dispatchers.IO) {
                                    todosRepo.UpdateTodo(0, todo)
                                }
                            } catch (e: Exception) {
                                Log.e(TAG, "Error: ${e.message}")
                                Toast.makeText(this@MainActivity, "Failed to update song", Toast.LENGTH_SHORT).show()
                                throw e
                            }
                        }
                    }
                }
            }
        }
    }
    override suspend fun addNewTodo(todo: Todo) {
        try{
            withContext(Dispatchers.IO){
                todosRepo.AddTodo(todo)
            }
        } catch (e : Exception){
            Log.e(TAG, "Error: ${e.message}")
            Toast.makeText(this, "Failed to add new todo", Toast.LENGTH_SHORT).show()
            throw e
        }
    }

    override suspend fun update(){
        try{
            withContext(Dispatchers.IO){
                todosRepo.GetTodos()
            }
        } catch (e : Exception){
            Log.e(TAG, "Error: ${e.message}")
            Toast.makeText(this, "Failed to get list", Toast.LENGTH_SHORT).show()
            throw e
        }
        recyclerView.adapter?.notifyDataSetChanged()
    }


    companion object {
        val REQUEST_CODE = 1
        val TAG = MainActivity::class.java.simpleName
    }
}