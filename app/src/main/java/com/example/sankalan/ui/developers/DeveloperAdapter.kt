package com.example.sankalan.ui.developers

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sankalan.R

class TeamAdapter(val data:ArrayList<Teams>):RecyclerView.Adapter<TeamAdapter.TeamViewHolder>() {
    class TeamViewHolder(v:View):RecyclerView.ViewHolder(v){
        val profilePicture = v.findViewById<ImageView>(R.id.developer_profile_image)
        val github = v.findViewById<Button>(R.id.developer_github)
        val linked = v.findViewById<Button>(R.id.developer_linked_in)
        val instagram = v.findViewById<Button>(R.id.developer_instagram)
        val name = v.findViewById<TextView>(R.id.name_team)
        val position = v.findViewById<TextView>(R.id.position_team)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TeamViewHolder {
        val v =  LayoutInflater.from(parent.context).inflate(R.layout.team_custom_layout, null)
        return TeamViewHolder(v)
    }

    override fun onBindViewHolder(holder: TeamViewHolder, position: Int) {
        //Setting Image with url
        holder.name.text = data.get(position).name
        holder.profilePicture.setImageBitmap(data[position].imageBitmap)
        if(data[position].position.isNotEmpty()){
            //For Panelist
            holder.position.visibility = View.VISIBLE
            holder.position.text = data[position].position
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
    }

    override fun getItemCount(): Int = data.size
}