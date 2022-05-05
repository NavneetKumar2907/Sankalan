package com.example.sankalan.fragmentsadmin

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
import com.example.sankalan.activities.adminViewModel
import com.example.sankalan.adapter.AdminEventAdapter
import com.example.sankalan.data.DeleteResult
import com.example.sankalan.data.Events
import com.example.sankalan.data.Upload
import com.example.sankalan.databinding.FragmentEventsAdminBinding
import com.example.sankalan.dialogfragments.AddEvent
import com.example.sankalan.interfaces.EventInterfaceListeners
import com.example.sankalan.model.AdminViewModel
import kotlinx.coroutines.launch


class EventsFragment : Fragment(), EventInterfaceListeners {

    val model: AdminViewModel by activityViewModels()
    lateinit var AdminEventBinding: FragmentEventsAdminBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        AdminEventBinding = FragmentEventsAdminBinding.inflate(layoutInflater)
        return AdminEventBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        AdminEventBinding.addEvent.setOnClickListener {
            AddEvent().show(requireActivity().supportFragmentManager,"Add Event")
        }
        AdminEventBinding.deleteAll.setOnClickListener {

            adminViewModel.viewModelScope.launch {
                val res = deleteAll()
                Handler(Looper.getMainLooper()).post {
                    if(res.failed!=null){
                        toas(res.failed)
                    }else{
                        toas(getString(res.success!!))
                    }
                }

            }
        }
        AdminEventBinding.adminEvents.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            setHasFixedSize(true)
        }
        model.getEvent().observe(viewLifecycleOwner, Observer {
            Log.w("List","${it}")
            AdminEventBinding.adminEvents.adapter = AdminEventAdapter(it,this)
        })
    }

    override suspend fun delete(eventName: String): DeleteResult {
        return model.deleteEvent(eventName = eventName)
    }

    override suspend fun edit(events: Events, eventName: String): Upload {
        return model.editEvent(events, eventName = eventName)
    }

    override suspend fun deleteAll(): DeleteResult {
        return model.deleteAllEvent()
    }
    fun toas(msg:String){
        Toast.makeText(context,msg, Toast.LENGTH_SHORT).show()

    }


}