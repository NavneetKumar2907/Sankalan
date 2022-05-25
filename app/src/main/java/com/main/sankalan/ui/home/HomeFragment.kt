package com.main.sankalan.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.main.sankalan.adapter.EventListAdapter
import com.main.sankalan.data.Events
import com.main.sankalan.data.RegistrationSuccess
import com.main.sankalan.data.TeamMembers
import com.main.sankalan.databinding.FragmentHomeBinding
import com.main.sankalan.dialogfragments.RegistrationSelection
import com.main.sankalan.interfaces.SelectedEventClickListener
import com.main.sankalan.model.MainViewModel


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
            recycleEventList.adapter =  EventListAdapter(it, this)

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