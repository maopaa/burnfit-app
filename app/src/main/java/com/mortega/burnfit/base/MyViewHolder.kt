package com.mortega.burnfit.base

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.mortega.burnfit.R
import kotlinx.android.synthetic.main.inflater_item.view.*

class MyViewHolder (itemView: View?): RecyclerView.ViewHolder(itemView!!) {

    internal var nameTraining: TextView? = itemView?.findViewById(R.id.nameTraining)
    internal var time: TextView? = itemView?.findViewById(R.id.timeText)
    internal var cal: TextView? = itemView?.findViewById(R.id.infoCal)
}