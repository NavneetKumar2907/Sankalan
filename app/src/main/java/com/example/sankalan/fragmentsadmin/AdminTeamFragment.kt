package com.example.sankalan.fragmentsadmin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sankalan.data.Teams
import com.example.sankalan.databinding.FragmentAdminTeamBinding
import com.example.sankalan.dialogfragments.EditTeam
import com.example.sankalan.interfaces.TeamEditListener
import com.example.sankalan.model.AdminViewModel
import com.example.sankalan.ui.developers.TeamAdapter


class AdminTeamFragment : Fragment(), TeamEditListener {

    lateinit var TeamBinding : FragmentAdminTeamBinding
    private val TeamModel:AdminViewModel by activityViewModels()
    val listner:TeamEditListener = this

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        TeamBinding = FragmentAdminTeamBinding.inflate(layoutInflater)
        return TeamBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        TeamBinding.addDeveloperMember.setOnClickListener {
            //Add Dveloper Member
            openEdit(Teams())
        }
        TeamBinding.addPanelTeam.setOnClickListener {
            //Add Panel Team
            openEdit(pos = true, data = Teams())
        }
        TeamBinding.adminDeveloperTeam.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)
        }
        TeamBinding.adminPanelTeam.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(true)
        }
        TeamModel.DeveloperTeam.observe(viewLifecycleOwner, Observer {
            TeamBinding.adminDeveloperTeam.adapter = TeamAdapter(it,admin = true, listner = listner)
        })
        TeamModel.livePanelTeam.observe(viewLifecycleOwner, Observer {
            TeamBinding.adminPanelTeam.adapter = TeamAdapter(it,admin = true, listner = listner)
        })
    }

    override fun openEdit(data:Teams, pos:Boolean) {
        EditTeam(data,pos = pos).show(requireActivity().supportFragmentManager,"Edit Team")
    }

}