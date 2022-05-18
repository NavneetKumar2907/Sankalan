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

/**
 * Event List ADmin ADapter.
 */
class AdminEventAdapter(
    private val eventList: ArrayList<Events>,
    private val listener: EventInterfaceListeners
) : RecyclerView.Adapter<AdminEventAdapter.EventAdminViewHolder>() {

    class EventAdminViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        /**
         * Holder CLass
         */

        //Setting Up Views.
        val eventName: TextView = v.findViewById(R.id.admin_user_name)
        val eventType: TextView = v.findViewById(R.id.admin_user_mobile)
        val eventTeam: TextView = v.findViewById(R.id.admin_user_course)
        val eventVenue: TextView = v.findViewById(R.id.admin_user_institute)
        val eventTime: TextView = v.findViewById(R.id.admin_user_year)
        val eventCoordinator: TextView = v.findViewById(R.id.admin_user_isVerified)
        val eventDescription: TextView = v.findViewById(R.id.admin_user_email)
        val eventRules: TextView = v.findViewById(R.id.admin_user_uid)
        val editEvent: Button = v.findViewById(R.id.edit_admin_user)
        val deleteEvent: Button = v.findViewById(R.id.delete_admin_user)
        val eventPicture: ImageView = v.findViewById(R.id.eventPicture)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventAdminViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.admin_event_list_custom, null)
        return EventAdminViewHolder(v) //Infalting Views
    }

    override fun onBindViewHolder(holder: EventAdminViewHolder, position: Int) {

        eventList[position].apply {
            //Setting Up Values
            Log.w("Value", "$this")
            holder.eventName.text = this.eventName
            holder.eventType.text = this.Type
            holder.eventTeam.text = this.Team
            holder.eventVenue.text = this.Venue
            val time = "${this.timeHour} : ${this.timeMinute}"
            holder.eventTime.text = time
            holder.eventCoordinator.text = this.Coordinator
            holder.eventDescription.text = this.Description
            holder.eventRules.text = this.rules
            if (image_drawable != null) {
                holder.eventPicture.setImageBitmap(image_drawable)
            }
        }
        holder.editEvent.setOnClickListener {
            //Adding Event
            AddEvent(this.eventList[holder.adapterPosition]).show(
                (listener as Fragment).requireActivity().supportFragmentManager,
                "Add Event"
            )
        }
        //Deleting Event
        holder.deleteEvent.setOnClickListener {

            adminViewModel.viewModelScope.launch {
                val res = listener.delete(eventName = eventList[holder.adapterPosition].eventName)
                Handler(Looper.getMainLooper()).post {
                    if (res.failed != null) {
                        toas(res.failed)
                    } else {
                        (listener as Fragment).context?.getString(res.success!!)
                            ?.let { it1 -> toas(it1) }
                    }//End Else
                }//End Handler
            }//End Coroutine
        }//End Listner

    }

    private fun toas(msg: String) {
        Toast.makeText((listener as Fragment).requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    override fun getItemCount(): Int = eventList.size
}