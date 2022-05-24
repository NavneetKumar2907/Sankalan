package com.sankalan.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sankalan.R
import com.sankalan.data.RegisteredEvents

/**
 * My Events Adapter.
 */
class MyEventAdapter(private val dataset: List<RegisteredEvents>) :
    RecyclerView.Adapter<MyEventAdapter.EventViewHolder>() {

    class EventViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        /**
         * View Holder.
         */
        val myEventTitle: TextView = view.findViewById(R.id.my_event_title_card)
        val teamMember1: TextView = view.findViewById(R.id.my_event_member1_card)
        val teamMember2: TextView = view.findViewById(R.id.my_event_member2_card)
        val teamMember3: TextView = view.findViewById(R.id.my_event_member3_card)
        val teamMember4: TextView = view.findViewById(R.id.my_event_member4_card)
        val teamName: TextView = view.findViewById(R.id.team_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.my_event_card_layout, parent, false)
        return EventViewHolder(v)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val item = dataset[position]

        holder.myEventTitle.text = item.eventName
        holder.teamMember1.text = item.members.member1
        holder.teamMember2.text = item.members.member2
        holder.teamMember3.text = item.members.member3
        holder.teamMember4.text = item.members.member4
        holder.teamName.text = item.teamName

        if (item.individual.isNotEmpty()) {
            holder.teamName.text = "IndiVidual Event."
        }

    }

    override fun getItemCount() = dataset.size

}