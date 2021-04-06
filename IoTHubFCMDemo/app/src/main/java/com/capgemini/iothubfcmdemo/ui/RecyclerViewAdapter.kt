package com.capgemini.iothubfcmdemo.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.capgemini.iothubfcmdemo.R

class RecyclerViewAdapter(private val dataSet: ArrayList<Map<String, String>>) : RecyclerView.Adapter<NotificationViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.display_item, parent, false)
        return NotificationViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.titleText.text = dataSet[position]["title"]
        holder.bodyText.text = dataSet[position]["body"]
        holder.mIdText.text = dataSet[position]["mId"]
        holder.temp.text = "${dataSet[position]["temp"]} °C"
    }

    override fun getItemCount(): Int = dataSet.size
}