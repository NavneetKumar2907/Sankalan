package com.example.sankalan.fragmentsadmin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sankalan.R
import com.example.sankalan.adapter.AdminUserAdapter
import com.example.sankalan.databinding.FragmentUsersAdminBinding
import com.example.sankalan.model.AdminViewModel


class UsersFragment : Fragment() {
    private lateinit var bindingUser :FragmentUsersAdminBinding
    private val userModel:AdminViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        bindingUser = FragmentUsersAdminBinding.inflate(layoutInflater)
        return bindingUser.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindingUser.userList.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            setHasFixedSize(true)
        }
        userModel.userData.observe(viewLifecycleOwner, Observer {
            bindingUser.userList.adapter = AdminUserAdapter(it)
            bindingUser.registrationCount.text = it.size.toString()
        })


    }


}