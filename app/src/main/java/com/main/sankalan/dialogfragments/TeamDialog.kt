package com.main.sankalan.dialogfragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.main.sankalan.R
import com.main.sankalan.data.RegistrationSuccess
import com.main.sankalan.data.TeamMembers
import com.main.sankalan.interfaces.SelectedEventClickListener
import com.main.sankalan.model.MainViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * Team Dialog For Edit by admin.
 */
class TeamDialog(private val teamReg: SelectedEventClickListener, private val teamSize:Int=0) : DialogFragment() {

    // Team Member Edit text
    private lateinit var member1: TextView
    private lateinit var member2: EditText
    private lateinit var member3: EditText
    private lateinit var member4: EditText
    private lateinit var teamName: EditText
    private lateinit var progressBar:ProgressBar


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.register_team, null)
    }

    override fun onViewCreated(teamView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(teamView, savedInstanceState)
        //Setup view
        member1 = teamView.findViewById(R.id.member1)
        member2 = teamView.findViewById(R.id.member2)
        member3 = teamView.findViewById(R.id.member3)
        member4 = teamView.findViewById(R.id.member4)
        teamName = teamView.findViewById(R.id.teamName)
        progressBar = teamView.findViewById(R.id.progressBarTeam)
        member1.text = activityViewModels<MainViewModel>().value.userData.value?.email
        teamView.findViewById<TextView>(R.id.heading).text = "You can Have $teamSize Members."

        if(teamSize == 2){
            member3.visibility = View.GONE
            member4.visibility = View.GONE
        }
        teamView.findViewById<Button>(R.id.submit_team)//On Submit CLicked
            .setOnClickListener {
                var count = 0
                val mem1Email = member1.text.toString().lowercase()
                val mem2Email = member2.text.toString().lowercase()
                val mem3Email = member3.text.toString().lowercase()
                val mem4Email = member4.text.toString().lowercase()
                val teamNameText = teamName.text.toString()
                //To Check 2 and 4 member register only.
                if (mem2Email.isNotEmpty() && member2.error == null) {
                    count += 1
                }
                if (mem3Email.isNotEmpty() && member3.error == null) {
                    count += 1
                }
                if (mem4Email.isNotEmpty() && member4.error == null) {
                    count += 1
                }

                if (count == 1 || count == 3) {
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Sure Register?")
                        .setMessage("Once Register you can not go back from the coming adventure.")
                        .setNegativeButton(resources.getString(R.string.cancel)) { dialog, which ->
                            // Respond to negative button press
                            dialog.cancel()
                        }
                        .setPositiveButton("Ok") { dialog, which ->
                            // Respond to positive button press
                            progressBar.visibility=View.VISIBLE
                            GlobalScope.launch {
                                val res: RegistrationSuccess = teamReg.Registration(
                                    TeamMembers(
                                        mem1Email,
                                        mem2Email,
                                        mem3Email,
                                        mem4Email
                                    )//Team Members,
                                    , teamNameText//Team Name.
                                )
                                Log.w("res", "$res")
                                    Log.w("res inside loop", "$res")



                                    if (res.success != null) {
                                        Handler(Looper.getMainLooper()).post {
                                            progressBar.visibility=View.GONE
                                            Toast.makeText(
                                                context,
                                                getString(res.success),
                                                Toast.LENGTH_SHORT
                                            )
                                                .show()
                                            getDialog()?.dismiss()
                                        }

                                    }
                                    if (res.failed != null) {
                                        Handler(Looper.getMainLooper()).post {
                                            progressBar.visibility=View.GONE
                                            Toast.makeText(context, res.failed, Toast.LENGTH_SHORT)
                                                .show()
                                        }

                                    }

                            }//End Coroutines

                        }
                        .show()//End Alert Dialog


                } else {
                    Toast.makeText(
                        context,
                        "You can Have ${teamSize} Members.",
                        Toast.LENGTH_SHORT
                    ).show()
                }


            }
        teamView.findViewById<Button>(R.id.cancel_team)
            .setOnClickListener {
                dialog?.dismiss()
            }


        member2.addTextChangedListener {
            try {
                if (!isValidEmail(it.toString())) {
                    member2.error = requireActivity().getString(R.string.invalid_email)
                }
            } catch (e: Exception) {
                Log.w("error", e.message.toString())
            }
        }
        member3.addTextChangedListener {
            try {
                if (!isValidEmail(it.toString())) {
                    member2.error = requireActivity().getString(R.string.invalid_email)
                }
            } catch (e: Exception) {
                Log.w("error", e.message.toString())
            }
        }
        member4.addTextChangedListener {
            try {
                if (!isValidEmail(it.toString())) {
                    member2.error = requireActivity().getString(R.string.invalid_email)
                }
            } catch (e: Exception) {
                Log.w("error", e.message.toString())
            }
        }


    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

}
