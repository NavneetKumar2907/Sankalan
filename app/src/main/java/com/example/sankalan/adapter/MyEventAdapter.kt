


package com.example.sankalan.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.sankalan.R
import com.example.sankalan.data.Events
import com.example.sankalan.data.MyEvents
import org.w3c.dom.Text
import java.util.concurrent.Executors

class MyEventAdapter(private val dataset: List<MyEvents>):
    RecyclerView.Adapter<MyEventAdapter.EventViewHolder>() {

    class EventViewHolder(private val view: View): RecyclerView.ViewHolder(view) {
        val my_event_container:RelativeLayout = view.findViewById(R.id.my_event_container)
        val my_event_title: TextView = view.findViewById(R.id.my_event_title_card)
        val teamMember1: TextView = view.findViewById(R.id.my_event_member1_card)
        val teamMember2: TextView=view.findViewById(R.id.my_event_member2_card)
        val teamMember3: TextView=view.findViewById(R.id.my_event_member3_card)
        val teamMember4: TextView=view.findViewById(R.id.my_event_member4_card)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.my_event_card_layout,parent,false)
        return EventViewHolder(v)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val item = dataset[position]

        holder.my_event_title.text = item.myEventName
        holder.teamMember1.text = item.teamMember1
        holder.teamMember2.text = item.teamMember2
        holder.teamMember3.text = item.teamMember3
        holder.teamMember4.text = item.teamMember4



    }
    override fun getItemCount() = dataset.size

}