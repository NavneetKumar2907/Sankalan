package com.sankalan.fragmentsadmin

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.print.PrintHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.sankalan.adapter.AdminRegEventAdapter
import com.sankalan.data.RegisteredEvents
import com.sankalan.databinding.FragmentRegisteredEventAdminBinding
import com.sankalan.model.AdminViewModel


class RegisteredEventAdminFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private lateinit var registeredEvents: FragmentRegisteredEventAdminBinding
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
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            setHasFixedSize(true)
        }

        // Event list
        regViewModel.getEvent().observe(viewLifecycleOwner, Observer {
            for (e in it) {
                eventNames.add(e.eventName)
            }
            val spinnerArrayAdapter =
                ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, eventNames)
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            registeredEvents.eventRegisteredEventSpinner.adapter = spinnerArrayAdapter
        })

        registeredEvents.eventRegisteredEventSpinner.onItemSelectedListener = this



        regViewModel.regEvent.observe(viewLifecycleOwner, Observer {
            uniqueAllRegEvent = it.distinctBy {
                if (it.teamName.isEmpty()) it.individual else it.teamName
            }
        })

        registeredEvents.printRegEvent.setOnClickListener {
            var passValue = ""
            try {
                passValue = registeredEvents.eventRegisteredEventSpinner.selectedItem.toString()
                printPDF(passValue)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Empty Selection!!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun loadAdapter(listRegEvent: List<RegisteredEvents>) {

        registeredEvents.registeredCount.text = listRegEvent.size.toString()
        registeredEvents.registeredEventsList.adapter =
            AdminRegEventAdapter(listRegEvent, regViewModel.userData.value)
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

        val filRes = uniqueAllRegEvent.filter {
            it.eventName == eventNames[p2]
        }

        Log.w("filter", filRes.toString())
        loadAdapter(filRes)


    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }

    fun printPDF(eventName: String) {
        try {
            val reycleViewBitmap = registeredEvents.registeredEventsList
            if (reycleViewBitmap.adapter == null) {
                Toast.makeText(requireContext(), "Empty!!", Toast.LENGTH_SHORT).show()
                return
            }
            reycleViewBitmap.isDrawingCacheEnabled = true
            val screen: Bitmap = Bitmap.createBitmap(reycleViewBitmap.getDrawingCache())
            reycleViewBitmap.isDrawingCacheEnabled = false

            val canvas = Canvas(screen)
            val paint = Paint()
            paint.setColor(Color.BLACK) // Text Color
            paint.textSize = 36F
            paint.isUnderlineText = true

            val centreX = screen.width / 2

            // some more settings...

            // some more settings...
            canvas.drawBitmap(screen, 0F, 0F, paint)

            canvas.drawText(eventName, centreX.toFloat(), 25F, paint)

            activity?.also { context ->
                PrintHelper(context).apply {
                    scaleMode = PrintHelper.SCALE_MODE_FIT
                }.also { printHelper ->
                    printHelper.printBitmap(registeredEvents.eventRegisteredEventSpinner.selectedItem.toString(), screen)
                }
            }
        } catch (e: Exception) {
            Log.w("Error", e.message.toString())
        }

    }
}