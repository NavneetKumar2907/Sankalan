package com.ducs.sankalan.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ducs.sankalan.R

class ScoreAdapter(val data:ArrayList<String>):RecyclerView.Adapter<ScoreAdapter.ScoreViewHolder>() {

    class ScoreViewHolder(v:View):RecyclerView.ViewHolder(v){
        val srNo:TextView = v.findViewById(R.id.serial_no)
        val resulname:TextView = v.findViewById(R.id.result_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScoreViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.customscore, null)
        return ScoreViewHolder(v)
    }

    override fun onBindViewHolder(holder: ScoreViewHolder, position: Int) {
        holder.apply {
            srNo.text = (position+1).toString()
            resulname.text = data[position]
        }
    }

    override fun getItemCount(): Int=data.size
}