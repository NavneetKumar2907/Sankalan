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
import org.w3c.dom.Text

class AdminRegEventAdapter(val dataset: List<RegisteredEvents>, val userDataSet: ArrayList<LoggedInUserView>?):RecyclerView.Adapter<AdminRegEventAdapter.AdminRegViewHolder>() {
    class AdminRegViewHolder(v: View):RecyclerView.ViewHolder(v){
        val teamName = v.findViewById<TextView>(R.id.teamName_admin)
        val mem1 = v.findViewById<TextView>(R.id.member1_admin)
        val mem2 = v.findViewById<TextView>(R.id.member2_admin)
        val mem3 = v.findViewById<TextView>(R.id.member3_admin)
        val mem4 = v.findViewById<TextView>(R.id.member4_admin)
        val nameIndividual = v.findViewById<TextView>(R.id.individual_name)
        val individualVisibility = v.findViewById<LinearLayout>(R.id.individual)
        val teamVisibility = v.findViewById<LinearLayout>(R.id.team)
        val individualPhone = v.findViewById<TextView>(R.id.individual_phone)
        val individualEmail = v.findViewById<TextView>(R.id.individual_email)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminRegViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.admin_registered_event_custom, null)
        return AdminRegViewHolder(v)
    }

    override fun onBindViewHolder(holder: AdminRegViewHolder, position: Int) {
        val item = dataset[position]
        holder.apply {

            if(item.individual.isNotEmpty()){
                individualVisibility.visibility = View.VISIBLE
                teamVisibility.visibility = View.GONE
                val res = userDataSet?.filter {
                    it.uid == item.individual
                }?.get(0)

                nameIndividual.text = res?.name
                individualPhone.text = res?.mobile
                individualEmail.text = res?.email
            }else{
                individualVisibility.visibility = View.GONE
                teamVisibility.visibility = View.VISIBLE

               try {
                   mem1.text = userDataSet?.filter {
                       it.email == item.members.member1
                   }?.get(0)?.name
               }catch (e:Exception){
                   Log.w("Error",e.message.toString())
                   mem1.visibility = View.GONE
               }

                try {
                    mem2.text = userDataSet?.filter {
                        it.email == item.members.member2
                    }?.get(0)?.name
                }catch (e:Exception){
                    Log.w("Error",e.message.toString())
                    mem2.visibility = View.GONE
                }
                try {
                    mem3.text = userDataSet?.filter {
                        it.email == item.members.member3
                    }?.get(0)?.name

                }catch (e:Exception){
                    Log.w("Error",e.message.toString())
                    mem3.visibility = View.GONE
                }
                try {
                    mem4.text = userDataSet?.filter {
                        it.email == item.members.member4
                    }?.get(0)?.name
                }catch (e:Exception){
                    Log.w("Error",e.message.toString())
                    mem4.visibility = View.GONE
                }

                teamName.text = item.teamName
            }
        }
    }

    override fun getItemCount(): Int = dataset.size
}