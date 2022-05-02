package com.example.sankalan.ui.profile

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import com.example.sankalan.MainViewModel
import com.example.sankalan.R
import com.example.sankalan.databinding.FragmentMyProfileBinding
import com.example.sankalan.ui.login.data.LoggedInUser


class MyProfileFragment : Fragment() {
    lateinit var popEditView: View
    lateinit var myprofileBinding: FragmentMyProfileBinding

    //ViewModel

    private val mainV: MainViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        popEditView = inflater.inflate(R.layout.edit_my_profile, null)
        myprofileBinding = FragmentMyProfileBinding.inflate(inflater)
        return myprofileBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val name = myprofileBinding.fullName
        val college = myprofileBinding.collegeNameText
        val course = myprofileBinding.courseName
        val year = myprofileBinding.yearText
        val mobile = myprofileBinding.mobileNoText
        val email = myprofileBinding.emailText
        val editButton = myprofileBinding.editProfileButton
        mainV.userData.observe(viewLifecycleOwner, Observer {
            name.text = it.name
            college.text = it.institute
            course.text = it.course
            year.text = it.year.toString()
            mobile.text = it.mobile
            email.text = it.email
        })
        val dim = LinearLayout.LayoutParams.MATCH_PARENT
        val editPopUp = PopupWindow(popEditView, dim, dim, true)
        editButton.setOnClickListener {
            //Edit Profile Popup
            editPopUp.showAtLocation(view, Gravity.CENTER, 0, 0)
        }
        val name_edit = popEditView.findViewById<EditText>(R.id.edit_username)
        val college_edit = popEditView.findViewById<EditText>(R.id.edit_college_name)
        val course_edit = popEditView.findViewById<EditText>(R.id.edit_course_name)
        val year_edit = popEditView.findViewById<EditText>(R.id.edit_course_year)
        val mobile_edit = popEditView.findViewById<EditText>(R.id.edit_mobile_no)
        val update = popEditView.findViewById<Button>(R.id.saveBtnTask)

        update.setOnClickListener {
            try {
                val userEditNew = LoggedInUser(
                    name = name_edit.text.toString(),
                    mobile = mobile_edit.text.toString(),
                    course = course_edit.text.toString(),
                    institute = college_edit.text.toString(),
                    year = year_edit.text.toString().toInt()
                )
                mainV.editUserDetail(userEditNew)

            } catch (e: Exception) {
                Log.w("Error", e.message.toString())
                Toast.makeText(context, "Error in saving data!!", Toast.LENGTH_SHORT).show()
            } finally {
                editPopUp.dismiss()
            }
        }

    }
}