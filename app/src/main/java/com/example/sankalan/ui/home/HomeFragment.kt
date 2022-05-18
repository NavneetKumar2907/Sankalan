package com.example.sankalan.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sankalan.adapter.EventListAdapter
import com.example.sankalan.data.Events
import com.example.sankalan.data.RegistrationSuccess
import com.example.sankalan.data.TeamMembers
import com.example.sankalan.databinding.FragmentHomeBinding
import com.example.sankalan.dialogfragments.RegistrationSelection
import com.example.sankalan.interfaces.SelectedEventClickListener
import com.example.sankalan.model.MainViewModel


/**
 * Selection
 * Registration
 * Other Features
 * User Verfied Or Not
 */
class HomeFragment : Fragment(), SelectedEventClickListener {

    private var _binding: FragmentHomeBinding? = null
    private var selectedEvent: Events? = null


    private val binding get() = _binding!!
    private lateinit var recycleEventList: RecyclerView
    private lateinit var adapter: EventListAdapter
    private val homeViewModel: MainViewModel by activityViewModels()


    lateinit var res: RegistrationSuccess


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        val root: View = binding.root

        // Event List Recycle View SetUp

        recycleEventList = binding.recyclerViewHome
        recycleEventList.setHasFixedSize(true)
        recycleEventList.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        // View Model Observer for getting events
        homeViewModel.getEvent().observe(viewLifecycleOwner, Observer {
            adapter = EventListAdapter(it, this)
            recycleEventList.adapter = adapter
        })

        return root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // Selected Event from Adapter
    override fun selectedEvent(position: Int) {
        selectedEvent = homeViewModel.getEvent().value?.get(position) //Event Details
        //Show Registration dialog
        RegistrationSelection(
            selectedEvent!!,
            this
        ).show(requireActivity().supportFragmentManager, "Registration")
    }

    override suspend fun Registration(team: TeamMembers, teamName: String): RegistrationSuccess {

        return homeViewModel.registerForEvent(
            selectedEvent!!.eventName,
            team = selectedEvent!!.Team == "Team",
            team,
            teamName
        )
    }

}