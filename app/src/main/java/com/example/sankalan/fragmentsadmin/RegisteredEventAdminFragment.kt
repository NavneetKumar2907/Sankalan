package com.example.sankalan.fragmentsadmin

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sankalan.adapter.AdminRegEventAdapter
import com.example.sankalan.data.Events
import com.example.sankalan.data.RegisteredEvents
import com.example.sankalan.databinding.FragmentRegisteredEventAdminBinding
import com.example.sankalan.model.AdminViewModel


class RegisteredEventAdminFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private lateinit var registeredEvents:FragmentRegisteredEventAdminBinding
    private val regViewModel by activityViewModels<AdminViewModel>()

    val eventNames = arrayListOf<String>()
    var uniqueAllRegEvent = listOf<RegisteredEvents>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        registeredEvents = FragmentRegisteredEventAdminBinding.inflate(layoutInflater)
        return registeredEvents.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registeredEvents.registeredEventsList.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            setHasFixedSize(true)
        }

        // Event list
        regViewModel.getEvent().observe(viewLifecycleOwner, Observer {
            for(e in it){
                eventNames.add(e.eventName)
            }
            val spinnerArrayAdapter = ArrayAdapter(requireContext(),android.R.layout.simple_spinner_item, eventNames)
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            registeredEvents.eventRegisteredEventSpinner.adapter = spinnerArrayAdapter
        })

        registeredEvents.eventRegisteredEventSpinner.onItemSelectedListener = this



        regViewModel.regEvent.observe(viewLifecycleOwner, Observer {
            uniqueAllRegEvent = it.distinctBy {
                if(it.teamName.isEmpty()) it.individual else it.teamName
            }
        })
    }
    fun loadAdapter(listRegEvent: List<RegisteredEvents>){

        registeredEvents.registeredCount.text = listRegEvent.size.toString()
        registeredEvents.registeredEventsList.adapter = AdminRegEventAdapter(listRegEvent, regViewModel.userData.value)
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

        val filRes = uniqueAllRegEvent.filter {
            it.eventName==eventNames[p2]
        }

        Log.w("filter",filRes.toString())
        loadAdapter( filRes)


    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }

}