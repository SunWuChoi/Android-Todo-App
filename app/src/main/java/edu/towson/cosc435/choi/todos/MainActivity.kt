package edu.towson.cosc435.choi.todos

import android.app.Activity
import android.util.Log
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.get
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import com.google.gson.Gson
import edu.towson.cosc435.choi.todos.interfaces.IController
import edu.towson.cosc435.choi.todos.models.Todo
import kotlinx.android.synthetic.main.activity_main.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity(), IController{

    @RequiresApi(Build.VERSION_CODES.O)

    var todos: MutableList<Todo> = (1..10).map { Todo("Title" + it, "content number" + it,false,LocalDateTime.now().format(
        DateTimeFormatter.ofPattern("M/d/y H:m:ss")),"3/23/2020", it) }.toMutableList()

    val adapter = ListAdapter(todos, this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        addTodoAbtn.setOnClickListener {
            launchNewTodoActivity()
        }

        recyclerView.layoutManager = GridLayoutManager(this, 1)

        recyclerView.adapter = adapter

        val callbacks = ItemTouchHelperCallback(adapter)
        val touchHelper = ItemTouchHelper(callbacks)
        touchHelper.attachToRecyclerView(recyclerView)

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
                            //Log.v("New Todo",todo.toString())
                            todos?.add(todo)
                            //logAllTodo(todos)
                            adapter.notifyDataSetChanged()
                        }

                    }
                    EDIT_TODO_REQUEST_CODE -> {
                        val json: String? = data?.getStringExtra(NewTodoActivity.TODO_EXTRA_KEY)
                        if(json != null){
                            val todo = Gson().fromJson<Todo>(json, Todo::class.java)
                            //Log.v("New Todo",todo.toString())

                            var pos = 0
                            for( i in todos){
                                if(i.id == todo.id){
                                    break
                                }
                                pos++
                            }


                            todos?.removeAt(pos)
                            todos?.add(pos,todo)
                            //logAllTodo(todos)
                            adapter.notifyDataSetChanged()
                        }
                    }
                }
            }
            Activity.RESULT_CANCELED -> {
                Toast.makeText(this,"User canceled. No Todo added",Toast.LENGTH_SHORT).show()
            }
        }
    }


    fun launchNewTodoActivity() {
        val intent = Intent(this, NewTodoActivity::class.java)
        //System.out.println(todos.lastIndex)
        var id = 0
        if(!todos.isEmpty()){
            id = todos.last().id
        }
        intent.putExtra(ID_CODE,id)
        startActivityForResult(intent, NEW_TODO_REQUEST_CODE)
    }

    fun logAllTodo(list: MutableList<Todo>?){
        if (list != null) {
            for(item in list){
                Log.v("New Todo",item.toString())
            }
        }
    }

    override fun updateTodo(idx: Int) {
        val todo = todos?.get(idx)
        if(todo != null){
            todos?.set(idx, todo.copy(todo.title.toUpperCase()))
        }
        adapter.notifyItemChanged(idx)
    }

    override fun removeTodo(idx: Int) {
        todos?.removeAt(idx)
        adapter.notifyItemChanged(idx)
    }


    companion object {
        val NEW_TODO_REQUEST_CODE = 1
        val EDIT_TODO_REQUEST_CODE = 2
        val ID_CODE = "ID"
    }
}
