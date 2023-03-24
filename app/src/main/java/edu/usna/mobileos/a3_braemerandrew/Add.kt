package edu.usna.mobileos.a3_braemerandrew

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import java.util.*

class Add : AppCompatActivity() {
    lateinit var addTitle : EditText
    lateinit var addDescription : EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)
        addTitle = findViewById(R.id.addTitle)
        addDescription = findViewById(R.id.addDescription)
        findViewById<Button>(R.id.addNewButt).setOnClickListener{
            var newToDo = ToDo(addTitle.text.toString(),addDescription.text.toString(), Calendar.getInstance())
            val resultIntent = Intent()
            resultIntent.putExtra("returned ToDo", newToDo)
            setResult(RESULT_OK, resultIntent)
            finish()
        }
    }
}