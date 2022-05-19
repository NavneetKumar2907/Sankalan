package com.example.sankalan.ui.sponsers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sankalan.R

class SponserAdapter(val data:ArrayList<Sponsers>):RecyclerView.Adapter<SponserAdapter.SponserViewHolder>() {
    class SponserViewHolder(v: View):RecyclerView.ViewHolder(v){
        val type = v.findViewById<TextView>(R.id.sponsers_type)
        val image = v.findViewById<ImageView>(R.id.sponsers_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SponserViewHolder {
        val v =  LayoutInflater.from(parent.context).inflate(R.layout.custom_sponsers, null)
        return SponserViewHolder(v)
    }

    override fun onBindViewHolder(holder: SponserViewHolder, position: Int) {
        holder.type.text = data[position].type
        holder.image.setImageBitmap(data[position].imageBitmap)
    }

    override fun getItemCount(): Int= data.size
}