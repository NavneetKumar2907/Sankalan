package com.example.sankalan.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.sankalan.R
import com.example.sankalan.data.Events
import com.example.sankalan.interfaces.SelectedEventClickListener

class EventListAdapter(private val dataset: List<Events>, val listener:SelectedEventClickListener):
    RecyclerView.Adapter<EventListAdapter.EventViewHolder>() {

    class EventViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        val container:RelativeLayout = view.findViewById(R.id.container)
        val title: TextView = view.findViewById(R.id.event_title_card)
        val poster: ImageView=view.findViewById(R.id.event_poster_card)
        val type: TextView=view.findViewById(R.id.event_type_card)
        val nos: TextView=view.findViewById(R.id.event_nos_card)
        val venue: TextView=view.findViewById(R.id.event_venue_card)
        val timing: TextView=view.findViewById(R.id.event_timing_card)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.event_card_layout,parent,false)
        return EventViewHolder(v)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val item = dataset[position]

        holder.title.text = item.eventName
        holder.type.text = item.Type
        holder.nos.text =item.Team
        val time = "${item.timeHour} : ${item.timeMinute}"
        holder.timing.text = time
        holder.venue.text = item.Venue
        if(item.image_drawable!=null){
            holder.poster.setImageBitmap(item.image_drawable)
        }

        if(item.Team == "Team"){
            holder.container.setBackgroundResource(R.drawable.gradient1)
        }else{
            holder.container.setBackgroundResource(R.drawable.gradient2)
        }

        holder.container.setOnClickListener {
            //Start Popup
            listener.selectedEvent(holder.adapterPosition)

        }
    }
    override fun getItemCount() = dataset.size

}