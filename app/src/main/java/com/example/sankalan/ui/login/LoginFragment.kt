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


// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"


class LoginFragment : Fragment(), View.OnClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    //Variables
    lateinit var LoginFragmentBinding: FragmentLoginBinding
    lateinit var authViewModel: AuthenticationViewModel
    lateinit var popUpForgotPassWord: PopupWindow
    private var navController: NavController? = null
    lateinit var popupView: View


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        popupView = inflater.inflate(R.layout.forgot_password, null)
        val width = LinearLayout.LayoutParams.MATCH_PARENT
        val height = LinearLayout.LayoutParams.MATCH_PARENT
        // Inflate the layout for this fragment
        LoginFragmentBinding = FragmentLoginBinding.inflate(inflater)
        popUpForgotPassWord = PopupWindow(popupView, width, height, true)
        return LoginFragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Views
        val emailEdit = LoginFragmentBinding.EmailAddress
        val passEdit = LoginFragmentBinding.editTextPassword
        val loginButton = LoginFragmentBinding.login
        val registerText = LoginFragmentBinding.registerHere
        val loading = LoginFragmentBinding.loading
        val forgotPassword = LoginFragmentBinding.forgotPassword
        navController = Navigation.findNavController(view)

        //ViewModel
        authViewModel = ViewModelProvider(
            this,
            AuthenticationViewModelFactory()
        ).get(AuthenticationViewModel::class.java)

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
        //result observer
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
        registerText.setOnClickListener {
            // signup
            // change fragment
            onClick(it)
        }
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

    private fun ForgotPassWordPopUp() {

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment LoginFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            LoginFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onClick(p0: View?) {
        navController?.navigate(R.id.action_loginFragment_to_signUpFragment)
    }
}