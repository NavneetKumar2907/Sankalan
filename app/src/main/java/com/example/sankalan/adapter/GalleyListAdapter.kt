package com.example.sankalan.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.sankalan.R

class GalleryListAdapter : RecyclerView.Adapter<GalleryListAdapter.GalleryViewHolder>(){

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just an Image object.

    class GalleryViewHolder(private val v: View) : RecyclerView.ViewHolder(v) {
        val imageView: ImageView = v.findViewById(R.id.image)
        /**
         * Create new views (invoked by the gallery_layout)
         */

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {


        // create a new view
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.gallery_layout, parent, false)
        return GalleryViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        val item = dataset[position]
        holder.imageView.setImageResource(item.imageResourceId)

    }

    override fun getItemCount(): Int {
        return dataset.size
    }