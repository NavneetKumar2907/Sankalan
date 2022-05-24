package com.sankalan.dialogfragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.sankalan.R
import com.sankalan.data.Events
import com.sankalan.databinding.SelectedEventBinding
import com.sankalan.interfaces.SelectedEventClickListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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

    private lateinit var regBinding: SelectedEventBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        regBinding = SelectedEventBinding.inflate(inflater)
        return regBinding.root
    }

    override fun onStart() {
        super.onStart()
        val width = ViewGroup.LayoutParams.MATCH_PARENT
        val height = ViewGroup.LayoutParams.WRAP_CONTENT
        dialog!!.window!!.setLayout(width, height)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        regBinding.apply {
            contactPerson.text = selectedEvent.Coordinator
            posterSelectedEvents.setImageBitmap(selectedEvent.image_drawable)
            eventNameSelectedEvent.text = selectedEvent.eventName
            venueSelectedEvents.text = selectedEvent.Venue
            var time =
                "${selectedEvent.timeHour % 12} : " + if (selectedEvent.timeMinute == 0) selectedEvent.timeMinute.toString() + "0" else selectedEvent.timeMinute.toString()
            time = if (selectedEvent.timeHour >= 12) "$time PM" else "$time AM"

            timingSelectedEvent.text = time
            aboutEvent.text = selectedEvent.Description
            selectedEventType.text = selectedEvent.Type
            selectedEventTeam.text = selectedEvent.Team
            if (selectedEvent.Team != "Team") {
                selectedEventTeam.setCompoundDrawablesWithIntrinsicBounds(
                    R.drawable.ic_person,
                    0,
                    0,
                    0
                )
            }

            //Click Listener
            registerSelectedEvent.setOnClickListener {
                if (selectedEvent.Team == "Team") {
                    // Team Registration
                    TeamDialog(regListener, selectedEvent.teamSize).show(
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

                            //Coroutine
                            GlobalScope.launch {
                                val res = regListener.Registration()

                                if (res.success != null) {
                                    Handler(Looper.getMainLooper()).post {
                                        Toast.makeText(
                                            context,
                                            getString(res.success),
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                        dialog?.dismiss()
                                    }
                                }
                                if (res.failed != null) {
                                    Handler(Looper.getMainLooper()).post {
                                        Toast.makeText(context, res.failed, Toast.LENGTH_SHORT)
                                            .show()
                                    }
                                }
                                //End Handler
                            }


                        }.show()
                }
            }
            cancelSelected.setOnClickListener {
                dialog?.cancel()
            }
        }


    }


}