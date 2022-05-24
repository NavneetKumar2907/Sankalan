package com.sankalan.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.sankalan.R
import com.sankalan.data.Teams
import com.sankalan.interfaces.TeamEditListener

class TeamAdapter(
    val data: ArrayList<Teams>,
    val listner: TeamEditListener? = null,
    val admin: Boolean = false,
    val con: Context? = null
) : RecyclerView.Adapter<TeamAdapter.TeamViewHolder>() {

    class TeamViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        /**
         * View Holder
         */
        val profilePicture: ImageView = v.findViewById(R.id.developer_profile_image)
        val github: Button = v.findViewById(R.id.developer_github)
        val linked: Button = v.findViewById(R.id.developer_linked_in)
        val instagram: Button = v.findViewById(R.id.developer_instagram)
        val name: TextView = v.findViewById(R.id.name_team)
        val positionTeam: TextView = v.findViewById(R.id.position_team)
        val edit: Button = v.findViewById(R.id.edit_team_admin)
        val phone: Button = v.findViewById(R.id.developer_phone_no)

        val divider: View = v.findViewById<View>(R.id.divider_gone)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.team_custom_layout, null)
        return TeamViewHolder(v)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: TeamViewHolder, position: Int) {
        //Setting Image with url
        val phone: Uri = Uri.parse("tel:" + data[position].phone)
        holder.name.text = data.get(position).name
        holder.profilePicture.setImageBitmap(data[position].imageBitmap)
        if (data[position].position.isNotEmpty()) {
            //For Panelist
            holder.positionTeam.visibility = View.VISIBLE
            holder.positionTeam.text = data[position].position
            holder.instagram.visibility = View.GONE
            holder.github.visibility = View.GONE
            holder.phone.visibility = View.VISIBLE
            holder.divider.visibility = View.GONE
        }

        holder.github.setOnClickListener {
            it.context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(data[position].github)))
        }

        holder.instagram.setOnClickListener {
            it.context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(data[position].instagram)
                )
            )
        }

        holder.linked.setOnClickListener {
            it.context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(data[position].linkedin)))
        }

        holder.phone.setOnClickListener {
            try {
                // It can be improved better.
                if (listner != null)
                    startActivity(
                        (listner as Fragment).requireContext(),
                        Intent(Intent.ACTION_DIAL, phone),
                        null
                    )
                else {
                    startActivity(con!!, Intent(Intent.ACTION_DIAL, phone), null)

                }

            } catch (e: Exception) {
                if (listner != null)
                    startActivity(
                        (listner as Fragment).requireContext(),
                        Intent(Intent.ACTION_DIAL, phone),
                        null
                    )
                else {
                    Toast.makeText(con!!, e.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        if (admin) {
            //admin Custom
            holder.apply {
                try {
                    name.setTextColor(R.color.black)
                    positionTeam.setTextColor(R.color.black)
                } catch (e: Exception) {
                    Log.w("Error", e.message.toString())
                }
            }
            holder.edit.visibility = View.VISIBLE
            holder.edit.setOnClickListener {
                //Open Edit Dialog
                listner?.openEdit(
                    data[holder.adapterPosition],
                    pos = data[holder.adapterPosition].position.isNotEmpty()
                )
            }
        }
    }

    override fun getItemCount(): Int = data.size
}