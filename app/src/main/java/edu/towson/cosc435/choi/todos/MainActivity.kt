package edu.towson.cosc435.choi.todos

import android.app.Activity
import android.app.AlertDialog
import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import edu.towson.cosc435.choi.todos.database.TodoDatabaseRepository
import edu.towson.cosc435.choi.todos.interfaces.ITodoRepository
import edu.towson.cosc435.choi.todos.interfaces.ITodoController
import edu.towson.cosc435.choi.todos.models.Todo
import edu.towson.cosc435.choi.todos.network.ITodoApi
import edu.towson.cosc435.choi.todos.network.TodoApi
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
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
    private lateinit var todoApi: ITodoApi
    private var cached = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        todoApi = TodoApi(this)

        todosRepo = TodoDatabaseRepository(this)

        lifecycleScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    val todoListAPI = fetchTodos()
                    val todoListDB = todosRepo.GetTodos()
                    todoListAPI.forEach { todoApi ->
                        if (todoListDB.firstOrNull { todoDB -> todoDB.id == todoApi.id } == null) {
                            todosRepo.AddTodo(todoApi)
                        }
                    }
                }
                update()
            } catch (e: Exception) {
                Log.e(TAG, "Error: ${e.message}")
                Toast.makeText(this@MainActivity, "Failed to fetch todos", Toast.LENGTH_SHORT)
                    .show()
                throw e
            }
        }

        val scheduler = getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
        val jobInfo = JobInfo.Builder(JOB_ID, ComponentName(this, TodosService::class.java))
        jobInfo.setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
        jobInfo.setMinimumLatency(10 * 1000)

        if(!cached) {
            scheduler.schedule(jobInfo.build())
            //System.out.println("cached all images")
            cached = true
        }

            MessageQueue.Channel.observe(this, { success ->
                Log.d(TAG, "Downloaded Icons from todosService: $success")
                NotificationManagerCompat.from(this).cancel(TodosService.NOTIF_ID)
                launch(Dispatchers.IO) {
                    (todosRepo as TodoDatabaseRepository).refreshTodoList()
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@MainActivity, "Image download complete", Toast.LENGTH_SHORT).show()
                        recyclerView.adapter?.notifyDataSetChanged()
                    }
                }
            })



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
                                Toast.makeText(this@MainActivity, "Failed to update todo", Toast.LENGTH_SHORT).show()
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

    override suspend fun fetchTodos(): List<Todo> {
        return todoApi.fetchTodos().await()
    }

    override suspend fun fetchIcon(url: String): Bitmap {
        return todoApi.fetchIcon(url).await()
    }

    override suspend fun checkCache(icon: String): Bitmap? {
        val file = File(cacheDir, icon)
        if(file.exists()) {
            val input = file.inputStream()
            return BitmapFactory.decodeStream(input)
        } else {
            return null
        }
    }

    override suspend fun cacheIcon(filename: String, icon: Bitmap) {
        val file = File(cacheDir, filename)
        val output = file.outputStream()
        icon.compress(Bitmap.CompressFormat.JPEG, 100, output)
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
        val JOB_ID = 1
        val TAG = MainActivity::class.java.simpleName
    }
}