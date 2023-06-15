package com.shaluambasta.noteapp.adapter

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.shaluambasta.noteapp.MainActivity
import com.shaluambasta.noteapp.R
import com.shaluambasta.noteapp.db.DBOpenHelper
import com.shaluambasta.noteapp.model.NoteModel
import com.shaluambasta.noteapp.utils.DialogBox
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class NoteAdapter(

    private val context: Context,
    private val dataSet: List<NoteModel>

) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {
    val dbOpenHelper = DBOpenHelper(context)

    inner class NoteViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val textTitle: TextView = view.findViewById(R.id.text_title)
        val pending: TextView = view.findViewById(R.id.pending)
        val textDescription: TextView = view.findViewById(R.id.text_description)
        val completeButton: ImageButton = view.findViewById(R.id.completeButton)
        val btnEdit: RelativeLayout = view.findViewById(R.id.btn_edit)
        val btnDelete: ImageView  = view.findViewById(R.id.btn_delete)

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.main_recycler_single_item, parent, false)

        return NoteViewHolder(adapterLayout)
    }

    private var datesss: Date? = null
    private var dateCompareOne: Date? = null
    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {

        val dialog = DialogBox()
        val item = dataSet[position]
        val dateFormat: DateFormat = SimpleDateFormat("HH:mm aaa")
        val date = Date()
        val dateformatted: String = dateFormat.format(date)
        Log.e("currrent time",dateformatted)
        datesss = parseDate(dateformatted);
        dateCompareOne = parseDate(item.description);


        holder.textTitle.text = item.title
        holder.textDescription.text = item.description

        if (item.complete=="true"){
            holder.completeButton.setImageResource(R.drawable.baseline_check_box_24)
            val color = Color.parseColor("#A14AAD") // Replace with your desired color code
            holder.completeButton.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
            holder.textTitle.setPaintFlags( holder.textTitle.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)
        }else {
            if(dateCompareOne?.before(datesss) == true){
                holder.textTitle.setTextColor(context.resources.getColor(R.color.red))
                holder.pending.setVisibility(View.VISIBLE)
            }else{
                holder.pending.setVisibility(View.GONE)
            }
            holder.completeButton.setImageResource(R.drawable.baseline_check_box_outline_blank_24)
        }
//        holder.btnEdit.setOnClickListener {
//            dialog.editDialog(context, item)
//        }
holder.completeButton.setOnClickListener{
    holder.textTitle.setPaintFlags( holder.textTitle.getPaintFlags() or Paint.STRIKE_THRU_TEXT_FLAG)

    updateData(item)
    holder.completeButton.setImageResource(R.drawable.baseline_check_box_24)
    val color = Color.parseColor("#A14AAD") // Replace with your desired color code
    holder.completeButton.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
    holder.pending.setVisibility(View.GONE)
    holder.textTitle.setTextColor(context.resources.getColor(R.color.black))

}
        holder.btnDelete.setOnClickListener {
            val context: Context = ContextThemeWrapper(context, com.shaluambasta.noteapp.R.style.AppTheme2)
            val builder = MaterialAlertDialogBuilder(context)

            builder.setTitle("Warning")

            builder.setMessage("Do you want to delete 'Classfication',this action can't be undone. ")


            //performing positive action
            builder.setPositiveButton("Yes"){dialogInterface, which ->
                val dbOpenHelper = DBOpenHelper(context)

                Log.d(TAG, item.id.toString())
                dbOpenHelper.deleteNote(item.id.toString())

                Toast.makeText(context, "Deleted!", Toast.LENGTH_SHORT).show()
                notifyDataSetChanged();


                val intent = Intent(context, MainActivity::class.java)
             //   intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                context.startActivity(intent)
                (context as Activity).finish()


            }

            //performing negative action
            builder.setNegativeButton("No"){dialogInterface, which ->
               // Toast.makeText(context,"clicked No",Toast.LENGTH_LONG).show()
            }
            // Create the AlertDialog
            val alertDialog: AlertDialog = builder.create()
            // Set other dialog properties
            alertDialog.setCancelable(false)
            alertDialog.show()
            // dialog.deleteDialog(context, item)
        }
    }
    val inputFormat = "HH:mm aaa"

    var inputParser = SimpleDateFormat(inputFormat, Locale.US)


    private fun parseDate(date: String): Date? {
        return try {
            inputParser.parse(date)
        } catch (e: ParseException) {
            Date(0)
        }
    }

    private fun updateData(item: NoteModel) {



            dbOpenHelper.updateNote(
                item.id.toString(),
                item.title,
                item.description,"true"
            )



    }


    override fun getItemCount(): Int {
        return dataSet.size
    }


}