package com.example.sankalan.ui.login


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.sankalan.R
import com.example.sankalan.activities.MainActivity
import com.example.sankalan.databinding.FragmentLoginBinding
import com.example.sankalan.ui.login.model.AuthenticationViewModel
import com.example.sankalan.ui.login.model.AuthenticationViewModelFactory
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase





class LoginFragment : Fragment(), View.OnClickListener {



    lateinit var LoginFragmentBinding: FragmentLoginBinding // For UI
    lateinit var authViewModel: AuthenticationViewModel // ViewModel For Authentication
    lateinit var popUpForgotPassWord: PopupWindow
    private var navController: NavController? = null //For Navigation
    lateinit var popupView: View


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        LoginFragmentBinding = FragmentLoginBinding.inflate(inflater)
        return LoginFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Views Variables

        val emailEdit = LoginFragmentBinding.EmailAddress //Entered Email Address
        val passEdit = LoginFragmentBinding.editTextPassword //Entered PassWord
        val loginButton = LoginFragmentBinding.login // Login Button
        val registerText = LoginFragmentBinding.registerHere // Register Text
        val loading = LoginFragmentBinding.loading //Loading Progress Bar
        val forgotPassword = LoginFragmentBinding.forgotPassword //Forgot Password Text
        navController = Navigation.findNavController(view) //Navigation Controller

        //Initializing ViewModel
        authViewModel = ViewModelProvider(
            this,
            AuthenticationViewModelFactory()
        ).get(AuthenticationViewModel::class.java)

        // View Model  Status of login
        authViewModel.loginForm.observe(viewLifecycleOwner, Observer {
            loginButton.isEnabled = it.isValid

            if (it.emailError != null) {
                emailEdit.error = getString(it.emailError)
            }
            if (it.passError != null) {
                passEdit.error = getString(it.passError)
            }
        })

        //Change in text Listeners
        emailEdit.addTextChangedListener {
            authViewModel.onLoginDataChange(
                email = it.toString(),
                password = passEdit.text.toString()
            )
        }
        passEdit.addTextChangedListener {
            authViewModel.onLoginDataChange(
                email = emailEdit.text.toString(),
                password = it.toString()
            )
        }

        //View Model Login observer
        authViewModel.result_login.observe(viewLifecycleOwner, Observer {
            if(it.failed!=null){
                Toast.makeText(context,it.failed,Toast.LENGTH_SHORT).show()
                loading.visibility = View.GONE
            }
            if (it.success != null) {
                startActivity(Intent(activity, MainActivity::class.java))
                activity?.finish()
            }
        })

        // Login Button Listener
        loginButton.setOnClickListener {
            loading.visibility = View.VISIBLE
            try {
                authViewModel.login(
                    email = emailEdit.text.toString(),
                    password = passEdit.text.toString()
                )
            } catch (e: Exception) {
                Log.w("Error in Text string", "Empty String.")
                loading.visibility = View.GONE
            }
        }
        // Register Listener
        registerText.setOnClickListener {
            // signup
            // change fragment
            onClick(it)
        }
        //Forgot Password Listener
        forgotPassword.setOnClickListener {
            popUpForgotPassWord.showAtLocation(getView(), Gravity.CENTER, 0, 0)
        }
        val confirmEmail = popupView.findViewById<Button>(R.id.emailConfirm)
        val forgotEmail = popupView.findViewById<EditText>(R.id.emailAddressForgot)
        forgotEmail.addTextChangedListener {
            if (!authViewModel.isValidEmail(it.toString())) {
                forgotEmail.error = getString(R.string.invalid_email)
            }
        }
        confirmEmail.setOnClickListener {
            //firebase forgot Password
            val emailForgot = forgotEmail.text.toString()
            if (emailForgot.isNotEmpty() && authViewModel.isValidEmail(emailForgot)) {
                //
                Firebase.auth.sendPasswordResetEmail(emailForgot)
                Toast.makeText(context,"Email Reset Link Send to Email Check Your Mail!!.",Toast.LENGTH_SHORT).show()
                popUpForgotPassWord.dismiss()

            } else {
                Toast.makeText(context, "Email Not Valid", Toast.LENGTH_SHORT).show()
            }
            popUpForgotPassWord.dismiss()
        }

    }

    // Interface Override for click listener inside login and signup fragment
    override fun onClick(p0: View?) {
        navController?.navigate(R.id.action_loginFragment_to_signUpFragment)
    }
}