package com.example.sankalan.ui.home

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.media.Image
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sankalan.MainViewModel
import com.example.sankalan.R
import com.example.sankalan.adapter.EventListAdapter
import com.example.sankalan.data.Events
import com.example.sankalan.data.RegistrationSuccess
import com.example.sankalan.data.TeamMembers
import com.example.sankalan.databinding.FragmentHomeBinding
import com.example.sankalan.interfaces.SelectedEventClickListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class HomeFragment : Fragment(), SelectedEventClickListener {

    private var _binding: FragmentHomeBinding? = null
     var selectedEvent:Events?=null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var recycleEventList: RecyclerView
    private lateinit var adapter: EventListAdapter
    private val homeViewModel: MainViewModel by activityViewModels()
    private lateinit var popUpSelectedEventView: View //Selected PopUp
    private lateinit var popUpMemberView:View

    //Slected Event View
    private lateinit var event_name_slected:TextView
    private lateinit var event_timing_slected:TextView
    private lateinit var event_venue_slected:TextView
    private lateinit var event_description_slected:TextView
    private lateinit var event_rule_slected:TextView
    private lateinit var event_poster_selected:ImageView
    private lateinit var event_coordinator_selected:TextView
    private lateinit var event_register_selected:Button

    // Team member layout view
    private lateinit var member1:TextView
    private lateinit var member2:TextView
    private lateinit var member3:TextView
    private lateinit var member4:TextView
    private lateinit var submit:Button
    private lateinit var addMember:Button
    private lateinit var subMember:Button

    private val dim = LinearLayout.LayoutParams.MATCH_PARENT
    lateinit var res:RegistrationSuccess

    lateinit var popUpSelectedWindow:PopupWindow
    lateinit var teamWindow:PopupWindow




    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        popUpSelectedEventView = inflater.inflate(R.layout.selected_event,container,false)
        popUpMemberView = inflater.inflate(R.layout.register_team,container,false)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        val root: View = binding.root

        recycleEventList = binding.recyclerViewHome
        recycleEventList.setHasFixedSize(true)
        recycleEventList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        homeViewModel.getEvent().observe(viewLifecycleOwner, Observer {
            adapter = EventListAdapter(it,this)
            recycleEventList.adapter = adapter
        })

        // Linknig popupselected views
        event_name_slected = popUpSelectedEventView.findViewById(R.id.event_name_selected_event)
        event_description_slected = popUpSelectedEventView.findViewById(R.id.about_event)
        event_rule_slected = popUpSelectedEventView.findViewById(R.id.rules_selected_event)
        event_timing_slected = popUpSelectedEventView.findViewById(R.id.timing_selected_event)
        event_poster_selected = popUpSelectedEventView.findViewById(R.id.poster_selected_events)
        event_venue_slected = popUpSelectedEventView.findViewById(R.id.venue_selected_events)
        event_coordinator_selected = popUpSelectedEventView.findViewById(R.id.contact_person)
        event_register_selected =  popUpSelectedEventView.findViewById<Button>(R.id.register_selected_event)

        //team member views
        member1 = popUpMemberView.findViewById(R.id.member1)
        member2 = popUpMemberView.findViewById(R.id.member2)
        member3 = popUpMemberView.findViewById(R.id.member3)
        member4 = popUpMemberView.findViewById(R.id.member4)

        addMember = popUpMemberView.findViewById(R.id.add_member)
        subMember = popUpMemberView.findViewById(R.id.sub_member)
        submit = popUpMemberView.findViewById(R.id.submit_team)

        //user verified or not
        Firebase.auth.addAuthStateListener{
            event_register_selected.isEnabled = it.currentUser!!.isEmailVerified
            if(it.currentUser!!.isEmailVerified){
                //Verified
                event_register_selected.text = getString(R.string.register)
            }else{

                event_register_selected.text = getString(R.string.not_verfied_button_string)
            }
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        event_register_selected.setOnClickListener {
            popUpSelectedWindow.dismiss()
            if(selectedEvent!!.Team){
                //Team Registr
                teamWindow = PopupWindow(popUpMemberView, dim,dim,true)
                teamWindow.showAtLocation(this.getView(), Gravity.CENTER, 0,0)
            }else{
                //Individual registration
                homeViewModel.viewModelScope.launch {
                    val res =homeViewModel.registerForEvent(selectedEvent!!.EventName)
                    if(res.succes!=null){
                        //sucess registration
                        Toast.makeText(context, getString(res.succes), Toast.LENGTH_SHORT).show()
                    }
                    if(res.failed!=null){
                        Toast.makeText(context,res.failed, Toast.LENGTH_SHORT).show()

                    }
                }
            }

        }


        addMember.setOnClickListener {
            member3.visibility = View.VISIBLE
            member4.visibility = View.VISIBLE
            it.isEnabled = false
            it.visibility = View.GONE
            subMember.isEnabled = true
            subMember.visibility = View.VISIBLE
        }
        subMember.setOnClickListener {
            member3.visibility = View.GONE
            member4.visibility = View.GONE
            it.isEnabled = false
            it.visibility = View.GONE
            addMember.isEnabled = true
            addMember.visibility = View.VISIBLE

        }
        submit.setOnClickListener {
            val memb1email = member1.text.toString()
            val memb2email = member2.text.toString()
            val memb3email = member3.text.toString()
            val memb4email = member4.text.toString()
            //Check validation and registration of emails
            val mem = TeamMembers(memb1email,memb2email,memb3email,memb4email)
            homeViewModel.viewModelScope.launch {
                val res = homeViewModel.registerForEvent(selectedEvent!!.EventName, team = true,members = mem)
                if(res.succes!=null){
                    Toast.makeText(context, getString(res.succes), Toast.LENGTH_SHORT).show()
                }
                if(res.failed!=null){
                    Toast.makeText(context,res.failed, Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun selectedEvent(position: Int, poster:Drawable) {
        selectedEvent = homeViewModel.getEvent().value?.get(position) //Event Details
        //Setting up all the view with selected events
        selectedEvent.apply {
            event_name_slected.text = this?.EventName
            event_description_slected.text = this?.Description
            //event_rule_slected.text = this?.eventRule
            event_timing_slected.text = this?.Time
            event_venue_slected.text = this?.Venue
            event_coordinator_selected.text = this?.Coordinator
            event_poster_selected.setImageDrawable(poster)
        }
        popUpSelectedWindow = PopupWindow(popUpSelectedEventView,dim,dim,true)
        popUpSelectedWindow.showAtLocation(this.view,Gravity.CENTER,0,0)
    }

}