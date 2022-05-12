package com.example.sankalan.dialogfragments

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
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
                    selectedEvent.eventName
                val time = "${selectedEvent.timeHour} : ${selectedEvent.timeMinute}"
                findViewById<TextView>(R.id.timing_selected_event).text = time
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

                       if (it.currentUser!!.isEmailVerified) {
                           //Verified
                           try{
                               register.text = resources.getString(R.string.register)
                           }catch (e:Exception){
                               Log.w("Error",e.message.toString())
                           }
                       } else {

                           try{
                               register.setBackgroundColor(resources.getColor(R.color.gray))//Ignore error
                           }catch (e:Exception){
                               Log.w("Errror",e.message.toString())
                           }

                           register.text = context?.getString(R.string.not_verfied_string)
                       }

                }

            } catch (e: Exception) {
                Log.w("Error", e.message.toString())
            }
            register.setOnClickListener {
                if (selectedEvent.Team=="Team") {
                    // Team Registration
                    TeamDialog(regListener).show(
                        requireActivity().supportFragmentManager,
                        "Team"
                    )
                    dialog?.dismiss()
                } else {
                    // Individual Registration

                            MaterialAlertDialogBuilder(requireContext())
                                .setTitle("Sure Register?")
                                .setMessage("Once Register you can not go back from the coming adventure.")
                                .setNegativeButton(resources.getString(R.string.cancel)) { dialog, which ->
                                    // Respond to negative button press
                                    dialog.cancel()
                                }
                                .setPositiveButton("Ok") { dialog, which ->
                                    // Respond to positive button press
                                    GlobalScope.launch {
                                    val res = regListener.Registration()
                                    Handler(Looper.getMainLooper()).post {
                                        if (res.succes != null) {
                                            Toast.makeText(context, getString(res.succes), Toast.LENGTH_SHORT)
                                                .show()

                                        }
                                        if (res.failed != null) {
                                            Toast.makeText(context, res.failed, Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                }
                        }.show()
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