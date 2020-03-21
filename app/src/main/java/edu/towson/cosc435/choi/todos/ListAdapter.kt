package edu.towson.cosc435.choi.todos

import android.app.Activity
import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.recyclerview.widget.ItemTouchHelper
import com.google.gson.Gson
import edu.towson.cosc435.choi.todos.interfaces.IController
import edu.towson.cosc435.choi.todos.models.Todo
import kotlinx.android.synthetic.main.activity_newtodo.*
import java.lang.Exception
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import androidx.core.app.ActivityCompat.startActivityForResult
import kotlinx.android.synthetic.main.recyclerview_item.view.*


interface ItemTouchAdapter {
    fun onMove(from: Int, to: Int)
    fun onDismiss(pos: Int)
}

class ListAdapter(val todos: MutableList<Todo>?, val controller: IController) : RecyclerView.Adapter<TodoViewHolder>(), ItemTouchAdapter{
    override fun onMove(from: Int, to: Int) {
        if (from < to) {
            for(i in from until to) {
                Collections.swap(todos, i, i + 1);
            }
        } else {
            for(i in from downTo to) {
                Collections.swap(todos, i, i - 1);
            }
        }
        notifyItemMoved(from, to);
    }

    override fun onDismiss(pos: Int) {
        todos?.removeAt(pos)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_item, parent, false)
        val holder = TodoViewHolder(view)
        holder.itemView.setOnClickListener {
            val position = holder.adapterPosition
            controller.updateTodo(position)
            notifyItemChanged(position)
        }
        return holder
    }

    override fun getItemCount(): Int {
        return todos?.size ?: 0
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val todo = todos!!.get(position)
        if (todo != null) {
            holder.bind(todo)
        }

        holder.itemView.setOnLongClickListener {
            todos?.removeAt(position)
            notifyDataSetChanged()
            true
        }

        holder.itemView.isCompletedCb.setOnClickListener{
            todos?.get(position).isCompleted = todos?.get(position).isCompleted != true
        }

        holder.itemView.setOnClickListener {

            val intent = Intent(it.context, NewTodoActivity::class.java)

            val title = todo.title
            val contents = todo.contents
            val isCompleted = todo.isCompleted
            val dateCreated = todo.dateCreated
            val duedate = todo.dueDate
            val id = todo.id
            try{
                val todo = Todo(title, contents, isCompleted, dateCreated, duedate, id )
                val json = Gson().toJson(todo)
                intent.putExtra(TODO_EXTRA_KEY, json)

            }catch(ex: Exception){
                Toast.makeText(it.context, "Error", Toast.LENGTH_SHORT).show()
            }

            (it.context as Activity).startActivityForResult(intent, EDIT_TODO_REQUEST_CODE)

        }
    }


    companion object{
        val ID_CODE = "ID"
        val TODO_EXTRA_KEY = "TODO"
        val EDIT_TODO_REQUEST_CODE = 2
    }


}

class TodoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

    val title: TextView = itemView.findViewById(R.id.titleTv)
    val contents: TextView = itemView.findViewById(R.id.contentsTv)
    val isCompleted: CheckBox = itemView.findViewById(R.id.isCompletedCb)
    val duedate: TextView = itemView.findViewById(R.id.duedateTv)
    val date: TextView = itemView.findViewById(R.id.dateTv)

    fun bind(todo: Todo) {
        title.text = todo.title
        contents.text = todo.contents
        isCompleted.isChecked = todo.isCompleted
        duedate.text = todo.dueDate
        date.text = todo.dateCreated
    }

}

class ItemTouchHelperCallback(val adapter: ItemTouchAdapter) : ItemTouchHelper.Callback(){
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        adapter.onDismiss(viewHolder.adapterPosition)
    }

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        return makeMovementFlags(ItemTouchHelper.UP or ItemTouchHelper.DOWN, ItemTouchHelper.START or ItemTouchHelper.END)
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        adapter.onMove(viewHolder.adapterPosition, target.adapterPosition)
        return true
    }


}