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
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.sankalan.R
import com.example.sankalan.activities.MainActivity
import com.example.sankalan.data.LoggedInUserView
import com.example.sankalan.databinding.FragmentSignUpBinding
import com.example.sankalan.ui.login.model.AuthenticationViewModel

/**
 * Signup Class
 */

class SignUpFragment : Fragment() {



    lateinit var signupBinding:FragmentSignUpBinding //For UI
    private val signupViewmodel:AuthenticationViewModel by activityViewModels() //Authentication ViewModel
    lateinit var data:LoggedInUserView // User Data
    private var navController: NavController? = null //For Navigation



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

        // Intializing Views
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

        //Navigation COntroller
        navController = Navigation.findNavController(view)

        // Mobile Text Change Listener
        mobile.addTextChangedListener {
            if (it.toString().length != 10){
                mobile.error = getString(R.string.invalid_mobile)
            }
        }
        //Year text Change Listener
        year.addTextChangedListener {
            try{
                if(it.toString().toLong()!in 1..4){
                    year.error = getString(R.string.invalid_course_year)
                }
            }catch (e:Exception){
                year.error = getString(R.string.invalid_course_year)
            }
        }
        //Email Text Change Listener
        email.addTextChangedListener {
            signupViewmodel.onLoginDataChange(email = it.toString(), password = password.text.toString())
        }
        //Password Text Change Listener
        password.addTextChangedListener {
            signupViewmodel.onLoginDataChange(email = email.text.toString(), password = it.toString())
        }

        //Signup form observer
        signupViewmodel.loginForm.observe(viewLifecycleOwner, Observer {
            signUpButton.isEnabled = it.isValid

            if(it.emailError!=null){
                email.error = getString(it.emailError)
            }
            if(it.passError!=null){
                password.error = getString(it.passError)
            }

        })
        //SignUp Result Observer
        signupViewmodel.result_login.observe(viewLifecycleOwner, Observer {
            if(it.success!=null){
                startActivity(Intent(activity, MainActivity::class.java))
                activity?.finish()
            }
            if(it.failed!=null){
                Toast.makeText(context,it.failed,Toast.LENGTH_SHORT).show()
                loading.visibility = View.GONE
            }
        })

        //Login listener
        loginHere.setOnClickListener {
            navController?.navigate(R.id.action_signUpFragment_to_loginFragment)
        }

        //Signup Button listener
        signUpButton.setOnClickListener {
            if(name.text.isNullOrBlank() ||
                course.text.isNullOrBlank() ||
                institute.text.isNullOrBlank() ||
                year.text.isNullOrBlank()||
                mobile.text.isNullOrBlank()||
                email.text.isNullOrBlank()||
                password.text.isNullOrBlank()||year.error!=null){
                Toast.makeText(context,"Require ALl Fields Correct.",Toast.LENGTH_SHORT).show()
            }else{
                data = LoggedInUserView(name = name.text.toString(),
                    course = course.text.toString(),
                    institute = institute.text.toString(),
                    year = year.text.toString().toInt(),
                    mobile = mobile.text.toString(),
                    isVerified = false,
                    email = email.text.toString()
                )
                try{
                    loading.visibility = View.VISIBLE

                    signupViewmodel.signUp(email = email.text.toString(), password = password.text.toString(),data = data)
                }catch (e:Exception){
                    loading.visibility = View.INVISIBLE
                }
            }
        }

    }

}