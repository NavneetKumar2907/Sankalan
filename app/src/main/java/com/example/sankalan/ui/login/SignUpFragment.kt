package com.example.sankalan.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.sankalan.R
import com.example.sankalan.activities.MainActivity
import com.example.sankalan.databinding.FragmentSignUpBinding
import com.example.sankalan.ui.login.data.LoggedInUser
import com.example.sankalan.ui.login.model.AuthenticationViewModel
import com.example.sankalan.ui.login.model.AuthenticationViewModelFactory

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SignUpFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SignUpFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null


    //variable
    lateinit var signupBinding:FragmentSignUpBinding
    lateinit var signupViewmodel:AuthenticationViewModel
    lateinit var data:LoggedInUser
    private var navController: NavController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        signupViewmodel = ViewModelProvider(this,AuthenticationViewModelFactory()).get(AuthenticationViewModel::class.java)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        signupBinding = FragmentSignUpBinding.inflate(inflater)
        return signupBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val name = signupBinding.FullName
        val mobile = signupBinding.mobileNum
        val institute = signupBinding.CollegeName
        val year = signupBinding.courseYear
        val course = signupBinding.CourseName
        val email = signupBinding.enterEmail
        val password = signupBinding.createPassword
        val loginHere = signupBinding.loginHere
        val signUpButton = view.findViewById<Button>(R.id.signUp)
        val loading = view.findViewById<ProgressBar>(R.id.loading)
        //viewmodel = ViewModelProvider(this,AuthenticationViewModelFactory()).get(AuthenticationViewModel::class.java)

        navController = Navigation.findNavController(view)

        mobile.addTextChangedListener {
            if (it.toString().length != 10){
                mobile.error = getString(R.string.invalid_mobile)
            }
        }
        year.addTextChangedListener {
            try{
                if(it.toString().toLong()!in 1..4){
                    year.error = getString(R.string.invalid_course_year)
                }
            }catch (e:Exception){
                year.error = getString(R.string.invalid_course_year)
            }
        }
        email.addTextChangedListener {
            signupViewmodel.onLoginDataChange(email = it.toString(), password = password.text.toString())
        }
        password.addTextChangedListener {
            signupViewmodel.onLoginDataChange(email = email.text.toString(), password = it.toString())
        }

        signupViewmodel.loginForm.observe(viewLifecycleOwner, Observer {
            signUpButton.isEnabled = it.isValid

            if(it.emailError!=null){
                email.error = getString(it.emailError)
            }
            if(it.passError!=null){
                password.error = getString(it.passError)
            }

        })
        signupViewmodel.user.observe(viewLifecycleOwner, Observer {
            if(it!=null){
                startActivity(Intent(activity, MainActivity::class.java))
                activity?.finish()
            }
        })
        loginHere.setOnClickListener {
            navController?.navigate(R.id.action_signUpFragment_to_loginFragment)
        }
        signUpButton.setOnClickListener {
            if(name.text.isNullOrBlank() ||
                course.text.isNullOrBlank() ||
                institute.text.isNullOrBlank() ||
                year.text.isNullOrBlank()||
                mobile.text.isNullOrBlank()||
                email.text.isNullOrBlank()||
                    password.text.isNullOrBlank()){
                Toast.makeText(context,"Require ALl Fields",Toast.LENGTH_SHORT).show()
            }else{
                data = LoggedInUser(name = name.text.toString(),
                course = course.text.toString(),
                institute = institute.text.toString(),
                year = year.text.toString().toLong(),
                mobile = mobile.text.toString())
                try{
                    loading.visibility = View.VISIBLE

                    signupViewmodel.signUp(email = email.text.toString(), password = password.text.toString(),data = data)
                }catch (e:Exception){
                    loading.visibility = View.INVISIBLE
                }
            }
        }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SignUpFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SignUpFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}