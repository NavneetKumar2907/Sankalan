package com.example.sankalan.dialogfragments

import android.app.Dialog
import android.content.DialogInterface
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.sankalan.R
import com.example.sankalan.data.Events
import com.example.sankalan.interfaces.SelectedEventClickListener
import com.example.sankalan.ui.home.HomeFragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

// Dialog Fragment for Event Registration
class RegistrationSelection(
    val selectedEvent: Events,
    val regListener: SelectedEventClickListener
) :
    DialogFragment() {


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it, R.style.forgot_background)
            val inflater = requireActivity().layoutInflater
            val registerView = inflater.inflate(R.layout.selected_event, null)
            // Setup View
            registerView.apply {
                findViewById<ImageView>(R.id.poster_selected_events).setImageBitmap(selectedEvent.image_drawable)
                findViewById<TextView>(R.id.event_name_selected_event).text =
                    selectedEvent.EventName
                findViewById<TextView>(R.id.timing_selected_event).text = selectedEvent.Time
                findViewById<TextView>(R.id.venue_selected_events).text = selectedEvent.Venue
                findViewById<TextView>(R.id.contact_person).text = selectedEvent.Coordinator
                findViewById<TextView>(R.id.about_event).text = selectedEvent.Description
//                    findViewById<TextView>(R.id.rules_selected_event).text = selectedEvent.rules
            }
            val register: Button = registerView.findViewById(R.id.register_selected_event)
            try {
                //user verified or not
                Firebase.auth.addAuthStateListener {
                    register.isEnabled = it.currentUser!!.isEmailVerified
                   try {
                       if (it.currentUser!!.isEmailVerified) {
                           //Verified
                           register.text = requireActivity().getString(R.string.register)
                       } else {
                           register.setBackgroundColor(requireActivity().getColor(R.color.gray)) //Ignore error
                           register.text = context?.getString(R.string.not_verfied_string)
                       }
                   }catch (e:Exception){
                       Log.w("Error",e.message.toString())
                   }
                }

            } catch (e: Exception) {
                Log.w("Error", e.message.toString())
            }
            register.setOnClickListener {
                if (selectedEvent.Team) {
                    // Team Registration
                    TeamDialog(regListener).show(
                        requireActivity().supportFragmentManager,
                        "Team"
                    )
                } else {
                    // Individual Registration
                    regListener.Registration()
                }
            }

            builder.setView(registerView)
                .setNegativeButton(
                    R.string.cancel,
                    DialogInterface.OnClickListener { dialog, id ->
                        dialog.cancel()
                    })
            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

}