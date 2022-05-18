package com.example.sankalan.ui.developers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import com.example.sankalan.databinding.FragmentDeveloperBinding
import com.example.sankalan.model.MainViewModel

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

        //View Model Observer of Developer List
        developerViewModel.liveDeveloperTeam.observe(viewLifecycleOwner, Observer {
            DeveloperBinding.developerList.adapter = TeamAdapter(it, con = requireContext())
        })
        //Panel List
        DeveloperBinding.panelList.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)
            val snap = LinearSnapHelper()
            snap.attachToRecyclerView(this)
        }
        //Panel List Observer
        developerViewModel.livePanelTeam.observe(viewLifecycleOwner, Observer {
            DeveloperBinding.panelList.adapter = TeamAdapter(it, con = requireContext())
        })
    }


}