package com.example.sankalan.adapter

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.sankalan.R

class AdminGalleryAdapter(val dataset:ArrayList<Bitmap>):RecyclerView.Adapter<AdminGalleryAdapter.AdminViewHolder>() {
    class AdminViewHolder(v:View):RecyclerView.ViewHolder(v){
        val image = v.findViewById<ImageView>(R.id.upload_image_view)
        val add: Button = v.findViewById(R.id.change_image)
        val delete: Button = v.findViewById(R.id.delete_image)

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdminGalleryAdapter.AdminViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.custom_gallery_admin, null)
        return AdminViewHolder(v)
    }

    override fun onBindViewHolder(holder: AdminGalleryAdapter.AdminViewHolder, position: Int) {
       val item = dataset[position]
        holder.image.setImageBitmap(item)
        holder.add.setOnClickListener {

        }

        holder.delete.setOnClickListener {
            //Alert Dialog
        }
    }

    override fun getItemCount(): Int = dataset.size
}