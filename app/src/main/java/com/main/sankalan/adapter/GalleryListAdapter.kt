package com.main.sankalan.adapter

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.main.sankalan.R

/**
 * Gallery List Adapter.
 */

class GalleryListAdapter(private val imageList: ArrayList<Bitmap>) :
    RecyclerView.Adapter<GalleryListAdapter.GalleryAdapter>() {

    class GalleryAdapter(v: View) : RecyclerView.ViewHolder(v) {
        val galleryimage: ImageView = v.findViewById(R.id.image_gallery_card)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryAdapter {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.gallery_layout, parent, false)
        return GalleryAdapter(v)
    }

    override fun onBindViewHolder(holder: GalleryAdapter, position: Int) {

        holder.galleryimage.setImageBitmap(imageList[position])
    }

    override fun getItemCount(): Int = imageList.size
}