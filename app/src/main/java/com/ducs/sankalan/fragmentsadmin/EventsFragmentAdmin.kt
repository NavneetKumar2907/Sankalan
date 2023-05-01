package com.ducs.sankalan.fragmentsadmin

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.ducs.sankalan.R
import com.ducs.sankalan.activities.adminViewModel
import com.ducs.sankalan.adapter.AdminEventAdapter
import com.ducs.sankalan.data.DeleteResult
import com.ducs.sankalan.data.Events
import com.ducs.sankalan.data.Upload
import com.ducs.sankalan.databinding.FragmentEventsAdminBinding
import com.ducs.sankalan.dialogfragments.AddEvent
import com.ducs.sankalan.interfaces.EventInterfaceListeners
import com.ducs.sankalan.model.AdminViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.launch


class EventsFragment : Fragment(), EventInterfaceListeners {

    val model: AdminViewModel by activityViewModels()
    private lateinit var AdminEventBinding: FragmentEventsAdminBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        AdminEventBinding = FragmentEventsAdminBinding.inflate(layoutInflater)
        return AdminEventBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        AdminEventBinding.addEvent.setOnClickListener {
            AddEvent().show(requireActivity().supportFragmentManager, "Add Event")
        }
        AdminEventBinding.deleteAll.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Sure Delete All Event from databse?")
                .setMessage("It will delete All the Events from Database and its will not recover.")
                .setNegativeButton(resources.getString(R.string.cancel)) { dialog, which ->
                    // Respond to negative button press
                    dialog.cancel()
                }
                .setPositiveButton("Ok") { dialog, which ->
                    // Respond to positive button press
                    adminViewModel.viewModelScope.launch {
                        val res = deleteAll()
                        Handler(Looper.getMainLooper()).post {
                            if (res.failed != null) {
                                toas(res.failed)
                            } else {
                                toas(getString(res.success!!))
                            }
                        }

                    }
                }
                .show()


        }
        AdminEventBinding.adminEvents.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            setHasFixedSize(true)
        }
        model.getEvent().observe(viewLifecycleOwner, Observer {
            Log.w("List", "${it}")
            AdminEventBinding.adminEvents.adapter = AdminEventAdapter(it, this)
        })
    }

    override suspend fun delete(eventName: String): DeleteResult {
        val def = CompletableDeferred<DeleteResult>()

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Sure Delete $eventName from Database?")
            .setMessage("It will delete $eventName  from database and its will not recover.")
            .setNegativeButton(resources.getString(R.string.cancel)) { dialog, which ->
                // Respond to negative button press
                dialog.cancel()
            }
            .setPositiveButton("Ok") { dialog, which ->
                // Respond to positive button press
                model.viewModelScope.launch {
                    def.complete(model.deleteEvent(eventName = eventName))
                }
            }
            .show()
        return def.await()

    }

    override suspend fun edit(events: Events, eventName: String): Upload {
        return model.editEvent(events, eventName = eventName)
    }

    override suspend fun deleteAll(): DeleteResult {
        return model.deleteAllEvent()
    }

    fun toas(msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()

    }


}