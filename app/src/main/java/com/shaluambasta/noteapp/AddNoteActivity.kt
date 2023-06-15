package com.shaluambasta.noteapp

import android.app.TimePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.shaluambasta.noteapp.db.DBOpenHelper
import java.time.LocalTime


class AddNoteActivity : AppCompatActivity() {


    private lateinit var etTitle: TextInputLayout
    private lateinit var back: ImageView
    private lateinit var etDescription: TextInputLayout
    private lateinit var fabSend: AppCompatButton
    private lateinit var cancel: AppCompatButton
    private val dbOpenHelper = DBOpenHelper(this)
    private var dueTime: LocalTime? = null
    private lateinit var timeInputEditText: TextInputEditText

    var autoCompleteTextView: AutoCompleteTextView? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_note)
        getSupportActionBar()?.hide();

        etTitle = findViewById(R.id.et_title)
        back = findViewById(R.id.back)
        etDescription = findViewById(R.id.et_description)
        timeInputEditText = findViewById(R.id.timeInputEditText)
        fabSend = findViewById(R.id.fab_send)
        cancel = findViewById(R.id.cancel)

back.setOnClickListener {
    finish()
}
        timeInputEditText.setOnClickListener {
            openTimePicker()
        }





        fabSend.setOnClickListener {
            fabSendData()
        }
        cancel.setOnClickListener {
            finish()
        }

        val subjects = arrayOf("AM", "PM")
        val adapter = ArrayAdapter<String>(this, R.layout.dropdown_item, subjects)
        autoCompleteTextView = findViewById(R.id.autoCompleteTextview);
        autoCompleteTextView?.setAdapter(adapter)
        autoCompleteTextView?.setOnItemClickListener(OnItemClickListener { parent, view, position, id ->
            Toast.makeText(
                applicationContext,
                "" + autoCompleteTextView?.getText().toString(),
                Toast.LENGTH_SHORT
            ).show()
        })

    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun openTimePicker() {
        if(dueTime == null)
            dueTime = LocalTime.now()
        val listener = TimePickerDialog.OnTimeSetListener{ _, selectedHour, selectedMinute ->
            dueTime = LocalTime.of(selectedHour, selectedMinute)
            updateTimeButtonText()
        }
        val dialog = TimePickerDialog(this, listener, dueTime!!.hour, dueTime!!.minute, false)
        dialog.setTitle("Task Due")
        dialog.show()

    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateTimeButtonText() {
        etDescription.editText?.text= Editable.Factory.getInstance().newEditable(String.format("%02d:%02d",dueTime!!.hour,dueTime!!.minute))
    }

    private fun fabSendData() {

        if (etTitle.editText?.text.toString().isEmpty()) {
            etTitle.error = "Please enter your Title"
            etTitle.requestFocus()
            return
        }

        if (etDescription.editText?.text.toString().isEmpty()) {
            etDescription.error = "Please enter your Time"
            etDescription.requestFocus()
            return
        }

        if (notEmpty()) {
            dbOpenHelper.addNote(
                etTitle.editText?.text.toString(),
                etDescription.editText?.text.toString()+ " "+autoCompleteTextView?.getText().toString(),"false"
            )
            Toast.makeText(this, "Added", Toast.LENGTH_SHORT).show()
            val intentToMainActivity = Intent(this, MainActivity::class.java)
            startActivity(intentToMainActivity)
            finish()
        }

    }


    private fun notEmpty(): Boolean {
        return (etTitle.editText?.text.toString().isNotEmpty()
                && etDescription.editText?.text.toString().isNotEmpty())
    }

}