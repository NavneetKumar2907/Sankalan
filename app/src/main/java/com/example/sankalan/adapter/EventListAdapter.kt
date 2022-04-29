package com.example.sankalan.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.view.Gravity

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sankalan.MainViewModel
import com.example.sankalan.R
import com.example.sankalan.data.Events
import com.example.sankalan.interfaces.SelectedEventClickListener
import java.util.concurrent.Executors

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

        holder.title.text = item.EventName
        holder.type.text = item.Type
        holder.nos.text =if (item.Team) "Team" else "Individual"
        holder.timing.text = item.Time
        holder.venue.text = item.Venue

        //Setting Image with url
        val executer = Executors.newSingleThreadExecutor()

        val handler = Handler(Looper.getMainLooper())
        var img: Bitmap?= null
        executer.execute {
            try {
                val `in` = java.net.URL(dataset.get(position).Image).openStream()
                img = BitmapFactory.decodeStream(`in`)
                handler.post {
                    holder.poster.setImageBitmap(img)
                }
            }
            catch (e:Exception){
                e.printStackTrace()
            }
        }
        if(item.Team){
            holder.container.setBackgroundResource(R.drawable.gradient1)
        }else{
            holder.container.setBackgroundResource(R.drawable.gradient2)
        }

        holder.container.setOnClickListener {
            //Start Popup
            listener.selectedEvent(holder.adapterPosition,holder.poster.drawable)

        }
    }
    override fun getItemCount() = dataset.size

}