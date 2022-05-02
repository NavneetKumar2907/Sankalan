package com.example.sankalan.dialogfragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.example.sankalan.MainViewModel
import com.example.sankalan.R
import com.example.sankalan.data.TeamMembers
import com.example.sankalan.interfaces.SelectedEventClickListener


class TeamDialog(val teamReg: SelectedEventClickListener) : DialogFragment() {

    // Team Member Edit text
    private lateinit var member1: TextView
    private lateinit var member2: EditText
    private lateinit var member3: EditText
    private lateinit var member4: EditText


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.register_team, null)
    }

    override fun onViewCreated(teamView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(teamView, savedInstanceState)
        member1 = teamView.findViewById(R.id.member1)
        member2 = teamView.findViewById(R.id.member2)
        member3 = teamView.findViewById(R.id.member3)
        member4 = teamView.findViewById(R.id.member4)
        member1.text = activityViewModels<MainViewModel>().value.userData.value?.email

        teamView.findViewById<Button>(R.id.submit_team)
            .setOnClickListener {
                var count = 0
                val mem1Email = member1.text.toString()
                val mem2Email = member2.text.toString()
                val mem3Email = member3.text.toString()
                val mem4Email = member4.text.toString()
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
                    teamReg.Registration(
                        TeamMembers(
                            mem1Email,
                            mem2Email,
                            mem3Email,
                            mem4Email
                        )
                    )
                } else {
                    Toast.makeText(
                        context,
                        "You can Have Two or Four Members.",
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

    fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

}
