package com.example.sankalan.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sankalan.R
import com.example.sankalan.data.LoggedInUserView
import com.example.sankalan.data.RegisteredEvents

/**
 * Registered Event Adapter.
 */
class AdminRegEventAdapter(
    private val dataset: List<RegisteredEvents>,
    private val userDataSet: ArrayList<LoggedInUserView>?
) : RecyclerView.Adapter<AdminRegEventAdapter.AdminRegViewHolder>() {
    class AdminRegViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        /**
         * View Holder.
         */
        val teamName: TextView = v.findViewById(R.id.teamName_admin)
        val mem1: TextView = v.findViewById(R.id.member1_admin)
        val mem2: TextView = v.findViewById(R.id.member2_admin)
        val mem3: TextView = v.findViewById(R.id.member3_admin)
        val mem4: TextView = v.findViewById(R.id.member4_admin)
        val nameIndividual: TextView = v.findViewById(R.id.individual_name)
        val individualVisibility: LinearLayout = v.findViewById(R.id.individual)
        val teamVisibility: LinearLayout = v.findViewById(R.id.team)
        val individualPhone: TextView = v.findViewById(R.id.individual_phone)
        val individualEmail: TextView = v.findViewById(R.id.individual_email)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminRegViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.admin_registered_event_custom, null)
        return AdminRegViewHolder(v)
    }

    override fun onBindViewHolder(holder: AdminRegViewHolder, position: Int) {
        val item = dataset[position]
        holder.apply {

            if (item.individual.isNotEmpty()) {
                // If Event Is Team wise or Individual.

                individualVisibility.visibility = View.VISIBLE
                teamVisibility.visibility = View.GONE

                val res = userDataSet?.filter {
                    it.uid == item.individual
                }?.get(0)

                nameIndividual.text = res?.name
                individualPhone.text = res?.mobile
                individualEmail.text = res?.email
            } else {
                individualVisibility.visibility = View.GONE
                teamVisibility.visibility = View.VISIBLE


                try {
                    //Setting member1 text
                    mem1.text = userDataSet?.filter {
                        it.email == item.members.member1
                    }?.get(0)?.name
                } catch (e: Exception) {
                    Log.w("Error", e.message.toString())
                    mem1.visibility = View.GONE
                }

                try {
                    //Setting member2 text
                    mem2.text = userDataSet?.filter {
                        it.email == item.members.member2
                    }?.get(0)?.name
                } catch (e: Exception) {
                    Log.w("Error", e.message.toString())
                    mem2.visibility = View.GONE
                }
                try {
                    //Setting member3 text
                    mem3.text = userDataSet?.filter {
                        it.email == item.members.member3
                    }?.get(0)?.name

                } catch (e: Exception) {
                    Log.w("Error", e.message.toString())
                    mem3.visibility = View.GONE
                }
                try {
                    //Setting member4 text
                    mem4.text = userDataSet?.filter {
                        it.email == item.members.member4
                    }?.get(0)?.name
                } catch (e: Exception) {
                    Log.w("Error", e.message.toString())
                    mem4.visibility = View.GONE
                }

                teamName.text = item.teamName //Team Name
            }
        }
    }

    override fun getItemCount(): Int = dataset.size
}