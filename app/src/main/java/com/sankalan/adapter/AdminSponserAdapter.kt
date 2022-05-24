package com.sankalan.adapter

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.sankalan.R

class AdminSponserAdapter(val dataset:ArrayList<Bitmap>):RecyclerView.Adapter<AdminSponserAdapter.AdminSponserViewHolder>() {
    class AdminSponserViewHolder(v: View):RecyclerView.ViewHolder(v){
        val add = v.findViewById<Button>(R.id.add_sponsers)
        val delete = v.findViewById<Button>(R.id.delete_sponser)
        val sponser_picture = v.findViewById<ImageView>(R.id.sponser_picture)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminSponserViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.admin_sponsers_custom,null)
        return AdminSponserViewHolder(v)
    }

    override fun onBindViewHolder(holder: AdminSponserViewHolder, position: Int) {
        holder.add.setOnClickListener {

        }

        holder.delete.setOnClickListener {

        }

        holder.sponser_picture.setImageBitmap(dataset[position])
    }

    override fun getItemCount(): Int = dataset.size
}