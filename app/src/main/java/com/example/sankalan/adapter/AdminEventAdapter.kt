package com.example.sankalan.adapter

import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.RecyclerView
import com.example.sankalan.R
import com.example.sankalan.activities.adminViewModel
import com.example.sankalan.data.Events
import com.example.sankalan.dialogfragments.AddEvent
import com.example.sankalan.interfaces.EventInterfaceListeners
import kotlinx.coroutines.launch

class AdminEventAdapter(val eventList:ArrayList<Events>, val listener:EventInterfaceListeners):RecyclerView.Adapter<AdminEventAdapter.EventAdminViewHolder>() {
    class EventAdminViewHolder(v:View):RecyclerView.ViewHolder(v){
        val eventName = v.findViewById<TextView>(R.id.admin_user_name)
        val eventType = v.findViewById<TextView>(R.id.admin_user_mobile)
        val eventTeam = v.findViewById<TextView>(R.id.admin_user_course)
        val eventVenue = v.findViewById<TextView>(R.id.admin_user_institute)
        val eventTime = v.findViewById<TextView>(R.id.admin_user_year)
        val eventCoordinator = v.findViewById<TextView>(R.id.admin_user_isVerified)
        val eventDescription = v.findViewById<TextView>(R.id.admin_user_email)
        val eventRules = v.findViewById<TextView>(R.id.admin_user_uid)
        val editEvent = v.findViewById<Button>(R.id.edit_admin_user)
        val deleteEvent = v.findViewById<Button>(R.id.delete_admin_user)
        val eventPicture = v.findViewById<ImageView>(R.id.eventPicture)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventAdminViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.admin_event_list_custom, null)
        return EventAdminViewHolder(v)
    }

    override fun onBindViewHolder(holder: EventAdminViewHolder, position: Int) {

            eventList[position].apply {
                Log.w("Value","$this")
                holder.eventName.text = this.eventName
                holder.eventType.text = this.Type
                holder.eventTeam.text = this.Team
                holder.eventVenue.text = this.Venue
                val time = "${this.timeHour} : ${this.timeMinute}"
                holder.eventTime.text = time
                holder.eventCoordinator.text = this.Coordinator
                holder.eventDescription.text = this.Description
                holder.eventRules.text = this.rules
                if(image_drawable!=null){
                    holder.eventPicture.setImageBitmap(image_drawable)
                }
        }
        holder.editEvent.setOnClickListener {
            AddEvent(this.eventList[holder.adapterPosition]).show((listener as Fragment).requireActivity().supportFragmentManager,"Add Event" )
        }
        holder.deleteEvent.setOnClickListener {
            adminViewModel.viewModelScope.launch {
                val res = listener.delete(eventName = eventList[holder.adapterPosition].eventName)
                Handler(Looper.getMainLooper()).post {
                    if(res.failed!=null){
                        toas(res.failed)
                    }else{
                        (listener as Fragment).context?.getString(res.success!!)
                            ?.let { it1 -> toas(it1) }
                    }
                }
            }
        }

    }
    fun toas(msg:String){
        Toast.makeText((listener as Fragment).requireContext(),msg, Toast.LENGTH_SHORT).show()
    }

    override fun getItemCount(): Int = eventList.size
}