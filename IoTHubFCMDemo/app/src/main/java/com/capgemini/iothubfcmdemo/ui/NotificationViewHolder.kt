package com.capgemini.iothubfcmdemo.ui

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.capgemini.iothubfcmdemo.R

class NotificationViewHolder(recyclerView: View) : RecyclerView.ViewHolder(recyclerView) {
    var titleText: TextView = recyclerView.findViewById(R.id.title)
    var bodyText: TextView = recyclerView.findViewById(R.id.body)
    var mIdText: TextView = recyclerView.findViewById(R.id.messageId)
    var temp: TextView = recyclerView.findViewById(R.id.temp)

}