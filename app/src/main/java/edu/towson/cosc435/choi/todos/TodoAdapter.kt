package edu.towson.cosc435.choi.todos

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.towson.cosc435.choi.todos.interfaces.ITodoController
import edu.towson.cosc435.choi.todos.models.Todo

import kotlinx.android.synthetic.main.todo_listitem_layout.view.*
import kotlinx.coroutines.launch
import java.lang.Exception

class TodoAdapter(val controller: ITodoController) : RecyclerView.Adapter<TodoViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.todo_cardview_layout, parent, false)
        val vh = TodoViewHolder(view)

        view.checkbox.setOnClickListener {
            val position = vh.adapterPosition

            controller.launch {
                try {
                    controller.toggleCompleted(position)
                    this@TodoAdapter.notifyItemChanged(position)
                } catch (e: Exception) {

                }
            }

            this@TodoAdapter.notifyDataSetChanged()
        }

        view.setOnClickListener {
            val position = vh.adapterPosition
            controller.editTodo(position)
        }

        view.setOnLongClickListener {
            val position = vh.adapterPosition

            controller.launch {
                try{
                    controller.deleteTodo(position)
                    this@TodoAdapter.notifyItemRemoved(position)
                } catch (e: Exception){

                }
            }

            true
        }

        return vh
    }

    override fun getItemCount(): Int {
        return controller.todosRepo.GetCount()
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        val todo = controller.todosRepo.GetTodo(position)
        val tv = holder.itemView.todo_title
        updateStrikethrough(todo, tv)
        holder.itemView.todo_title.text = todo.title
        holder.itemView.todo_text.text = todo.content
        holder.itemView.checkbox.isChecked = todo.completed
        holder.itemView.todo_create_date.text = todo.create_date.toString()
    }

    private fun updateStrikethrough(todo: Todo, tv: TextView) {
        if(todo.completed) {
            tv.paintFlags = tv.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
        } else {
            tv.paintFlags = tv.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
        }
    }
}

class TodoViewHolder(view: View) : RecyclerView.ViewHolder(view)