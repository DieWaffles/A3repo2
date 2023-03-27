package edu.usna.mobileos.a3_braemerandrew

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.DialogInterface.OnMultiChoiceClickListener
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable
import java.time.Month
import java.util.*
import java.util.Arrays.copyOf
import java.util.Calendar.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity(), ToDoListener {
    private var toDoList = ArrayList<ToDo>()
    private var requestCode = 5
    private lateinit var toDoItemCurrent : ToDo
    lateinit var toDoAdapter : ToDoAdapter
    private var toDoItemPos : Int = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toDoListView: RecyclerView = findViewById(R.id.toDoList)
        toDoList = getDataFromFile()
        toDoAdapter = ToDoAdapter(toDoList, this)
        toDoListView.adapter = toDoAdapter
    }

    override fun onItemClick(toDoItem: ToDo) {
        return
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.options_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.addOpt -> {
                val intent = Intent(baseContext, Add::class.java)
                startActivityForResult(intent,requestCode)
                return true
            }
            R.id.deleteSome -> {
                var tempList = ArrayList<String>()
                for(item in toDoList)
                    tempList.add(item.title)
                val stringArray = tempList.toTypedArray()
                val boolArray = BooleanArray(stringArray.size)
                val delSome = AlertDialog.Builder(this)
                delSome.setTitle("Check to Delete")
                    .setMultiChoiceItems(stringArray,boolArray){ dialog, whichButton, isChecked ->
                        boolArray[whichButton] = isChecked
                    }
                    .setPositiveButton("Confirm"){ delSome, whichButton->
                        var tList = ArrayList<ToDo>()
                        for(i in stringArray.indices) {
                            if (!boolArray[i]){
                                val tempToDo = toDoList[i]
                                tList.add(tempToDo)
                                Log.e("toDoDeleted","${tempToDo.title}")
                            }
                        }
                        toDoList.clear()
                        for(i in tList.indices){
                            toDoList.add(tList[i])
                        }
                        refreshDataSet()
                        delSome.dismiss()
                    }
                    .setNegativeButton("Cancel"){ delSome, whichButton->
                        delSome.dismiss()
                    }
                refreshDataSet()
                delSome.show()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onContextItemSelected(menuItem: MenuItem): Boolean {
        toDoItemPos = menuItem.groupId
        toDoItemCurrent = toDoList[toDoItemPos]
        return when(menuItem.itemId){
            R.id.show -> {
                val dateString : String = toDoItemCurrent.created.get(MONTH).toString() + '/' + toDoItemCurrent.created.get(
                    DAY_OF_MONTH).toString() + '/' + toDoItemCurrent.created.get(YEAR).toString()
                val showString : String = toDoItemCurrent.description + " " + dateString
                val showDialog = AlertDialog.Builder(this)
                showDialog.setTitle(toDoItemCurrent.title)
                    .setMessage(showString)
                    .setPositiveButton("Return to List") { showDialog, whichButton->
                        showDialog.dismiss()
                    }
                showDialog.show()
                return true
            }
            R.id.edit -> {
                val intent = Intent(baseContext, Edit::class.java)
                intent.putExtra("toDo",toDoItemCurrent)
                startActivityForResult(intent,requestCode)
                return true
            }
            R.id.delete -> {
                val showDialog = AlertDialog.Builder(this)
                showDialog.setTitle(toDoItemCurrent.title)
                    .setMessage("Delete?")
                    .setPositiveButton("Confirm") { showDialog, whichButton->
                        toDoList.remove(toDoItemCurrent)
                        refreshDataSet()
                        showDialog.dismiss()
                    }
                    .setNegativeButton("Cancel"){ showDialog, whichButton->
                        showDialog.dismiss()
                    }
                showDialog.show()
                return true
            }
            else -> super.onContextItemSelected(menuItem)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if(requestCode == this.requestCode){
            if(resultCode == RESULT_OK){
                val response = data?.getSerializableExtra("returned ToDo") as ToDo
                if(toDoItemPos != -1) {
                    toDoList[toDoItemPos].title = response.title
                    toDoList[toDoItemPos].description = response.description
                    toDoAdapter
                    toDoItemPos = -1
                    refreshDataSet()
                }
                else {
                    toDoList.add(response)
                    refreshDataSet()
                }
            }
        }
    }

    private fun refreshDataSet(){
        toDoAdapter.notifyDataSetChanged()
    }

    override fun onPause(){
        saveObjectToFile("List", toDoList)
        refreshDataSet()
        super.onPause()
    }

    override fun onStop(){
        saveObjectToFile("List",toDoList)
        refreshDataSet()
        super.onStop()
    }

    private fun saveObjectToFile(fileName: String, obj: ArrayList<ToDo>){
        try {
            ObjectOutputStream(openFileOutput(fileName, MODE_PRIVATE)).use {
                it.writeObject(obj)
                it.flush()
            }
        }
        catch (e: IOException){
            Log.e("IT472", "IOException writing file $fileName")
        }
    }

    private fun getDataFromFile() : ArrayList<ToDo> {
        try{
            ObjectInputStream(openFileInput("List")).use{
                return it.readObject() as ArrayList<ToDo>
            }
        } catch(e: IOException){
            Log.e("IT472","IOException reading file List")
            var empty = ArrayList<ToDo>()
            return empty
        }
    }

}

data class ToDo(var title: String, var description: String, val created: Calendar) : Serializable


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







