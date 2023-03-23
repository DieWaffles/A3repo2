package edu.usna.mobileos.a3_braemerandrew

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.io.Serializable
import java.util.*

class MainActivity : AppCompatActivity(), ToDoListener {
    val toDoList = ArrayList<ToDo>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toDoListView: RecyclerView = findViewById(R.id.toDoList)
        val toDoAdapter = ToDoAdapter(toDoList, this)
        toDoListView.adapter = toDoAdapter
    }

    override fun onItemClick(title: String) {
        TODO("Not yet implemented")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.addOpt -> {
                TODO("ADD NEW THING TO LIST IN A NEW ACTIVITY")
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onContextItemSelected(menuItem: MenuItem): Boolean {
        val position = menuItem.groupId
        val toDoItem = toDoList[position]
        return when(menuItem.itemId){
            R.id.show -> TODO("IMPLEMENT SHOW ITEM")
            R.id.edit -> TODO("EDIT ITEM")
            R.id.delete -> TODO("delete item")
            else -> super.onContextItemSelected(menuItem)
        }
    }
}

data class ToDo(val title: String, val description: String, val created: Calendar) : Serializable


class ToDoAdapter(val data: ArrayList<ToDo>, val listener: ToDoListener) : RecyclerView.Adapter<TextItemViewHolder>(){

    override fun getItemCount() = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TextItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater
            .inflate(R.layout.item_layout, parent, false)
        return TextItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: TextItemViewHolder, position: Int) {
        holder.onBind(data[position], listener)
    }

}

class TextItemViewHolder(v: View): RecyclerView.ViewHolder(v), View.OnCreateContextMenuListener{
    val textView: TextView = v.findViewById(R.id.itemTextView)
    fun onBind(toDoItem: ToDo, listener: ToDoListener){
        textView.text = toDoItem.title
        textView.setOnClickListener{ listener.onItemClick(toDoItem)}
        textView.setOnCreateContextMenuListener(this)
    }
    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?){
        menu?.add(adapterPosition, R.id.show, 1, "Show")
        menu?.add(adapterPosition, R.id.edit, 2, "Edit")
        menu?.add(adapterPosition, R.id.delete, 3, "Delete")
    }
}

interface ToDoListener{
    fun onItemClick(todoItem: ToDo)
}





