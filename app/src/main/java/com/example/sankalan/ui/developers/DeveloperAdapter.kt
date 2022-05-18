package com.example.sankalan.ui.developers

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sankalan.R
import com.example.sankalan.data.Teams
import com.example.sankalan.interfaces.TeamEditListener

class TeamAdapter(val data:ArrayList<Teams>, val listner:TeamEditListener?=null, val admin:Boolean = false):RecyclerView.Adapter<TeamAdapter.TeamViewHolder>() {
    class TeamViewHolder(v:View):RecyclerView.ViewHolder(v){
        val profilePicture = v.findViewById<ImageView>(R.id.developer_profile_image)
        val github = v.findViewById<Button>(R.id.developer_github)
        val linked = v.findViewById<Button>(R.id.developer_linked_in)
        val instagram = v.findViewById<Button>(R.id.developer_instagram)
        val name = v.findViewById<TextView>(R.id.name_team)
        val positionTeam = v.findViewById<TextView>(R.id.position_team)
        val edit = v.findViewById<Button>(R.id.edit_team_admin)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamViewHolder {
        val v =  LayoutInflater.from(parent.context).inflate(R.layout.team_custom_layout, null)
        return TeamViewHolder(v)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: TeamViewHolder, position: Int) {
        //Setting Image with url
        holder.name.text = data.get(position).name
        holder.profilePicture.setImageBitmap(data[position].imageBitmap)
        if(data[position].position.isNotEmpty()){
            //For Panelist
            holder.positionTeam.visibility = View.VISIBLE
            holder.positionTeam.text = data[position].position
        }
        holder.github.setOnClickListener {
            it.context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(data[position].github)))
        }
        holder.instagram.setOnClickListener {
            it.context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(data[position].instagram)))
        }
        holder.linked.setOnClickListener {
            it.context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(data[position].linkedin)))
        }
        if(admin){
            //admin Custom
            holder.apply {
                try {
                    name.setTextColor(R.color.black)
                    positionTeam.setTextColor(R.color.black)
                }catch (e:Exception){
                    Log.w("Error",e.message.toString())
                }
            }
            holder.edit.visibility = View.VISIBLE
            holder.edit.setOnClickListener {
                //Open Edit Dialog
                listner?.openEdit(data[holder.adapterPosition])
            }
        }
    }

    override fun getItemCount(): Int = data.size
}