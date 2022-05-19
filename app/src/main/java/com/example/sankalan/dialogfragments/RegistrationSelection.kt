package com.example.sankalan.dialogfragments

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.sankalan.R
import com.example.sankalan.data.Events
import com.example.sankalan.interfaces.SelectedEventClickListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 *  Dialog Fragment for Event Registration
 */
class RegistrationSelection(
    private val selectedEvent: Events,
    private val regListener: SelectedEventClickListener
) :
    DialogFragment() {


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        return activity?.let { it ->
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it, R.style.forgot_background)
            val inflater = requireActivity().layoutInflater
            val registerView = inflater.inflate(R.layout.selected_event, null)
            // Setup View
            registerView.apply {
                if(selectedEvent.Coordinator.isEmpty()){
                    findViewById<TextView>(R.id.coordinator_fixed_text).visibility = View.GONE
                }
                findViewById<ImageView>(R.id.poster_selected_events).setImageBitmap(selectedEvent.image_drawable)
                findViewById<TextView>(R.id.event_name_selected_event).text =
                    selectedEvent.eventName
                val time = "${selectedEvent.timeHour} : ${selectedEvent.timeMinute}0 AM"
                findViewById<TextView>(R.id.timing_selected_event).text = time
                findViewById<TextView>(R.id.venue_selected_events).text = selectedEvent.Venue
                findViewById<TextView>(R.id.contact_person).text = selectedEvent.Coordinator
                findViewById<TextView>(R.id.about_event).text = selectedEvent.Description
                findViewById<TextView>(R.id.rules_selected_event).text = selectedEvent.rules
                findViewById<TextView>(R.id.selected_event_team).text = selectedEvent.Team
                findViewById<TextView>(R.id.selected_event_type).text = selectedEvent.Type
            }
            val register: Button = registerView.findViewById(R.id.register_selected_event)
            try {
                //user verified or not
                Firebase.auth.addAuthStateListener {
                    register.isEnabled = it.currentUser!!.isEmailVerified

                    if (it.currentUser!!.isEmailVerified) {
                        //Verified
                        try {
                            register.text = resources.getString(R.string.register)
                        } catch (e: Exception) {
                            Log.w("Error", e.message.toString())
                        }
                    } else {
                        //Not Verified.
                        try {
                            register.setBackgroundColor(resources.getColor(R.color.gray))//Ignore error
                        } catch (e: Exception) {
                            Log.w("Errror", e.message.toString())
                        }

                        register.text = context?.getString(R.string.not_verfied_string)
                    }

                }

            } catch (e: Exception) {
                Log.w("Error", e.message.toString())
            }
            //Register Button Listener.
            register.setOnClickListener {
                if (selectedEvent.Team == "Team") {
                    // Team Registration
                    TeamDialog(regListener,selectedEvent.eventName).show(
                        requireActivity().supportFragmentManager,
                        "Team"
                    )
                    dialog?.dismiss()
                } else {
                    // Individual Registration

                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Sure Register?")
                        .setMessage("Once Register you can not go back from the coming adventure.")
                        .setNegativeButton(resources.getString(R.string.cancel)) { dialog, _ ->
                            // Respond to negative button press
                            dialog.cancel()
                        }//End Negative
                        .setPositiveButton("Ok") { _, _ ->
                            // Respond to positive button press
                            GlobalScope.launch {
                                //Coroutine
                                val res = regListener.Registration()
                                Handler(Looper.getMainLooper()).post {
                                    if (res.success != null) {
                                        Toast.makeText(
                                            context,
                                            getString(res.success),
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                    }
                                    if (res.failed != null) {
                                        Toast.makeText(context, res.failed, Toast.LENGTH_SHORT)
                                            .show()
                                    }
                                }//End Handler
                            }//End Coroutine
                        }.show()
                }
            }//End Listener

            //Setting Up Builder View
            builder.setView(registerView)
                .setNegativeButton(
                    Html.fromHtml("<font color='#FFFFFF'>Cancel</font>"),
                    DialogInterface.OnClickListener { dialog, id ->
                        dialog.cancel()
                    })//End Negative
            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

}