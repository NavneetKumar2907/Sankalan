package com.example.sankalan.fragmentsadmin

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sankalan.R
import com.example.sankalan.data.Events

class AdminEventAdapter(val eventList:ArrayList<Events>):RecyclerView.Adapter<AdminEventAdapter.EventAdminViewHolder>() {
    class EventAdminViewHolder(v:View):RecyclerView.ViewHolder(v){
        val eventName = v.findViewById<TextView>(R.id.eventName)
        val eventType = v.findViewById<TextView>(R.id.eventType)
        val eventTeam = v.findViewById<TextView>(R.id.eventTeam)
        val eventVenue = v.findViewById<TextView>(R.id.eventVenue)
        val eventTime = v.findViewById<TextView>(R.id.eventTime)
        val eventCoordinator = v.findViewById<TextView>(R.id.eventCoordinator)
        val eventDescription = v.findViewById<TextView>(R.id.eventDescription)
        val eventRules = v.findViewById<TextView>(R.id.eventRules)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventAdminViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: EventAdminViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int = eventList.size
}