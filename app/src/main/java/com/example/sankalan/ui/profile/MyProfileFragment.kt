package com.example.sankalan.ui.profile

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.sankalan.MainViewModel
import com.example.sankalan.R
import com.example.sankalan.ui.login.data.LoggedInUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase



class MyProfileFragment : Fragment() {

    private val mainV :MainViewModel by activityViewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_profile, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val name = view.findViewById<TextView>(R.id.full_name)
        val college = view.findViewById<TextView>(R.id.college_name_text)
        val course = view.findViewById<TextView>(R.id.course_name)
        val year = view.findViewById<TextView>(R.id.year_text)
        val mobile = view.findViewById<TextView>(R.id.mobile_no_text)
        val email = view.findViewById<TextView>(R.id.email_text)
        mainV.userData.observe(viewLifecycleOwner, Observer {
            name.text = it.name
            college.text = it.institute
            course.text = it.course
            year.text = it.year.toString()
            mobile.text = it.mobile
            email.text = it.email
        })

    }
}