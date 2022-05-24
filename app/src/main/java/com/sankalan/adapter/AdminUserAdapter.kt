package com.sankalan.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.sankalan.R
import com.sankalan.data.LoggedInUserView

/**
 * ADmin User List Adapter.
 */
class AdminUserAdapter(private val userList: ArrayList<LoggedInUserView>) :
    RecyclerView.Adapter<AdminUserAdapter.AdminViewHolder>() {
    class AdminViewHolder(val v: View) : RecyclerView.ViewHolder(v) {
        /**
         * View Holder.
         */
        val name: TextView = v.findViewById(R.id.admin_user_name)
        val mobile: TextView = v.findViewById(R.id.admin_user_mobile)
        val course: TextView = v.findViewById(R.id.admin_user_course)
        val institute: TextView = v.findViewById(R.id.admin_user_institute)
        val email: TextView = v.findViewById(R.id.admin_user_email)
        val year: TextView = v.findViewById(R.id.admin_user_year)
        val isVerified: TextView = v.findViewById(R.id.admin_user_isVerified)
        val uid: TextView = v.findViewById(R.id.admin_user_uid)
        val edit: Button = v.findViewById(R.id.edit_admin_user)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.user_custom_admin, null)
        return AdminViewHolder(v)
    }

    override fun onBindViewHolder(holder: AdminViewHolder, position: Int) {
        holder.apply {
            //Setting Up Values.
            name.text = userList[position].name
            mobile.text = userList[position].mobile
            course.text = userList[position].course
            institute.text = userList[position].institute
            email.text = userList[position].email
            year.text = userList[position].year.toString()
            if (userList[position].isVerified) {
                isVerified.text = "Yes"
                isVerified.setTextColor(
                    ContextCompat.getColor(
                        holder.v.context,
                        R.color.glow_green
                    )
                )
            } else {
                isVerified.text = "NO"
                isVerified.setTextColor(
                    ContextCompat.getColor(
                        holder.v.context,
                        android.R.color.holo_red_dark
                    )
                )
            }
            uid.text = userList[position].uid
        }
    }

    override fun getItemCount(): Int = userList.size
}