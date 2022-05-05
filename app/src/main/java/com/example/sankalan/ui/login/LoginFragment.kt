package com.example.sankalan.ui.login


import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.sankalan.R
import com.example.sankalan.activities.AdminActivity
import com.example.sankalan.activities.MainActivity
import com.example.sankalan.databinding.FragmentLoginBinding
import com.example.sankalan.ui.login.model.AuthenticationViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

/**
 * Login Class
 * Authentication of User.
 */


class LoginFragment : Fragment(), View.OnClickListener {


    lateinit var LoginFragmentBinding: FragmentLoginBinding // For UI
    val authViewModel: AuthenticationViewModel by activityViewModels() // ViewModel For Authentication
    private var navController: NavController? = null //For Navigation


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
            if (it.failed != null) {
                Toast.makeText(context, it.failed, Toast.LENGTH_SHORT).show()
                loading.visibility = View.GONE
            }
            if (it.success != null) {
                if(Firebase.auth.currentUser?.email =="admin@sankalan.com"){
                    startActivity(Intent(activity, AdminActivity::class.java))
                }else{
                    startActivity(Intent(activity, MainActivity::class.java))
                }
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
            onClick(it)
        }
        //Forgot Password Listener
        forgotPassword.setOnClickListener {
            ForgotPassWordFragment().show(
                requireActivity().supportFragmentManager,
                "Forgot Password"
            )
        }

    }

    // Interface Override for click listener inside login and signup fragment
    override fun onClick(p0: View?) {
        navController?.navigate(R.id.action_loginFragment_to_signUpFragment)
    }


    //Forgot Password Dialog Fragment
    class ForgotPassWordFragment : DialogFragment() {
        lateinit var emailForgot: EditText

        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
            return activity?.let {
                // Use the Builder class for convenient dialog construction
                val builder = AlertDialog.Builder(it,R.style.forgot_background)
                val inflater = requireActivity().layoutInflater
                val popupView: View = inflater.inflate(R.layout.forgot_password, null)
                emailForgot = popupView.findViewById(R.id.emailAddressForgot)
                builder.setView(popupView)
                    .setPositiveButton(R.string.confirmEmail,
                        DialogInterface.OnClickListener { dialog, id ->
                            if (isValid(email = emailForgot.text.toString())) {
                                Toast.makeText(
                                    context,
                                    "Reset Link Sent to registered Email.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                Firebase.auth.sendPasswordResetEmail(emailForgot.text.toString())
                            } else {
                                Toast.makeText(context, "Invalid Email.", Toast.LENGTH_SHORT).show()
                            }
                        })
                    .setNegativeButton(R.string.cancel,
                        DialogInterface.OnClickListener { dialog, id ->
                            dialog.dismiss()
                        })
                // Create the AlertDialog object and return it
                builder.create()
            } ?: throw IllegalStateException("Activity cannot be null")
        }

        private fun isValid(email: String): Boolean {
            if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                return true
            }
            return false
        }
    }
}