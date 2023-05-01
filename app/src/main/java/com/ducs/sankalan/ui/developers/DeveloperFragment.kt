package com.ducs.sankalan.ui.developers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import com.ducs.sankalan.adapter.TeamAdapter
import com.ducs.sankalan.databinding.FragmentDeveloperBinding
import com.ducs.sankalan.model.MainViewModel

class DeveloperFragment : Fragment() {

    private lateinit var DeveloperBinding: FragmentDeveloperBinding
    val developerViewModel: MainViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        DeveloperBinding = FragmentDeveloperBinding.inflate(inflater)
        return DeveloperBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Developer List
        DeveloperBinding.developerList.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)
            val snap = LinearSnapHelper()
            snap.attachToRecyclerView(this)
        }
        //Panel List
        DeveloperBinding.panelList.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)
            val snap = LinearSnapHelper()
            snap.attachToRecyclerView(this)
        }

        //View Model Observer of Developer List
        developerViewModel.liveDeveloperTeam.observe(viewLifecycleOwner) {
            DeveloperBinding.developerList.adapter = TeamAdapter(it, con = requireContext())
        }

        //Panel List Observer
        developerViewModel.livePanelTeam.observe(viewLifecycleOwner) {
            DeveloperBinding.panelList.adapter = TeamAdapter(it, con = requireContext())
        }
    }


}