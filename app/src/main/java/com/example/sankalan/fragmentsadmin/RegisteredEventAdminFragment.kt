package com.example.sankalan.fragmentsadmin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.sankalan.R


/**
 * A simple [Fragment] subclass.
 * Use the [RegisteredEventAdminFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RegisteredEventAdminFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_registered_event_admin, container, false)
    }


}