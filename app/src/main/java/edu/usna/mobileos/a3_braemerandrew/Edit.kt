package edu.usna.mobileos.a3_braemerandrew

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText

class Edit : AppCompatActivity() {
    lateinit var titleBox : EditText
    lateinit var descriptionBox : EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
        titleBox  = findViewById(R.id.titleBox)
        descriptionBox = findViewById(R.id.descriptionBox)
        val currentEdit = intent.getSerializableExtra("toDo") as ToDo
        titleBox.setText(currentEdit.title)
        descriptionBox.setText(currentEdit.description)
        findViewById<Button>(R.id.goBackBut).setOnClickListener{
            var newTitle : String = titleBox.text.toString()
            var newDescription : String = descriptionBox.text.toString()
            var newToDo = ToDo(newTitle,newDescription,currentEdit.created)
            val resultIntent = Intent()
            resultIntent.putExtra("returned ToDo", newToDo)
            setResult(RESULT_OK, resultIntent)
            finish()
        }

    }


}