package com.example.sankalan.model

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sankalan.data.Events
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.net.URL
import java.util.concurrent.Executors

class AdminViewModel: ViewModel() {
    private val database = FirebaseDatabase.getInstance() // Database Instance
    private val databaseUser =
        database.getReference("Users")
    private val image_ref = Firebase.storage.reference //Storage refrence
    private val databaseEvent = database.getReference("Events") // Event Listener Reference

    private val teams = database.getReference("Teams")
    private val developerTeamReference = teams.child("Developers")
    private val panelTeamReference = teams.child("Panel")
    private val sponserReference = database.getReference("Sponsers")

    //event list value listener
    private val eventListener = object : ValueEventListener {
        /**
         * Event list change listener.
         */
        override fun onDataChange(snapshot: DataSnapshot) {
            if (snapshot.exists()) {
                val list = arrayListOf<Events>()
                for (childEventname in snapshot.children) {
                    val res = childEventname.getValue<Events>()!!
                    //Setting Image with url
                    val executer = Executors.newSingleThreadExecutor()
                    val handler = Handler(Looper.getMainLooper())
                    var image: Bitmap?

                    executer.execute {
                        try {
                            val `in` = URL(res.Image).openStream()
                            image = BitmapFactory.decodeStream(`in`)
                            res.image_drawable = image
                        } catch (e: Exception) {
                            Log.w("Image Error", e.message.toString() + res.Image)
                        } finally {
                            handler.post {
                                list.add(res)
                                eventList.value = list
                            }
                        }
                    }
                }


            } else {
                Log.w("Error", "NOT found data.")
            }
        }

        override fun onCancelled(error: DatabaseError) {
            Log.w("Error", error.details)
        }

    }

    // Event Live Data
    private val eventList: MutableLiveData<ArrayList<Events>> by lazy {
        MutableLiveData<ArrayList<Events>>().also {
            loadEvent()
        }
    }

    // Getter of Event List
    fun getEvent(): MutableLiveData<ArrayList<Events>> {
        return eventList
    }

    private fun loadEvent() {
        /**
         * Load Events.
         */
        databaseEvent.addValueEventListener(eventListener)
    }
    fun editEvent(event:Events, eventName:String=""){
        if(eventName.isNotEmpty()){
            databaseEvent.child(eventName).removeValue()
        }
        databaseEvent.child(event.EventName).setValue(event)
    }


}