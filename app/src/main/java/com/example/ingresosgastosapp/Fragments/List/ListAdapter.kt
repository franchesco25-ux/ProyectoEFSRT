package com.example.ingresosgastosapp.Fragments.List

import android.R.attr.text
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ingresosgastosapp.Data.Ingresos
import com.example.ingresosgastosapp.R

class ListAdapter: RecyclerView.Adapter<ListAdapter.MyViewHolder>() {

    private var userList = emptyList<Ingresos>()

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val id_txt: TextView = itemView.findViewById(R.id.id_txt)
        val firstName: TextView = itemView.findViewById(R.id.firstName_txt)
        val lastName: TextView = itemView.findViewById(R.id.lastName_txt)
        val age_txt: TextView = itemView.findViewById(R.id.age_txt)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.custom_row, parent, false))
    }

    override fun getItemCount(): Int = userList.size

    override fun onBindViewHolder(
        holder: MyViewHolder,
        position: Int
    ) {
        val currentItem = userList[position]
        holder.id_txt.text = currentItem.id.toString()
        holder.firstName.text = currentItem.firstName
        holder.lastName.text = currentItem.lastName
        holder.age_txt.text = currentItem.age_txt.toString()
    }

    fun setData(user: List<Ingresos>){
        this.userList = user
        notifyDataSetChanged()
    }

}