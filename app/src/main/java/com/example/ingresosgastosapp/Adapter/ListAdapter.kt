package com.example.ingresosgastosapp.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ingresosgastosapp.Data.Ingresos
import com.example.ingresosgastosapp.R
import com.google.android.material.button.MaterialButton

class ListAdapter(
    private val onEditClick: (Ingresos) -> Unit,
    private val onDeleteClick: (Ingresos) -> Unit
) : RecyclerView.Adapter<ListAdapter.MyViewHolder>() {

    private var ingresosList = emptyList<Ingresos>()

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val id_txt: TextView = itemView.findViewById(R.id.id_txt)
        val descripcion_txt: TextView = itemView.findViewById(R.id.descripcion_txt)
        val monto_txt: TextView = itemView.findViewById(R.id.monto_txt)
        val categoria_txt: TextView = itemView.findViewById(R.id.categoria_txt)
        val fecha_txt: TextView = itemView.findViewById(R.id.fecha_txt)
        val btnEditar: MaterialButton = itemView.findViewById(R.id.btnEditarIngreso)
        val btnEliminar: MaterialButton = itemView.findViewById(R.id.btnEliminarIngreso)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.custom_row, parent, false)
        )
    }

    override fun getItemCount() = ingresosList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentIngreso = ingresosList[position]
        holder.id_txt.text = currentIngreso.id.toString()
        holder.descripcion_txt.text = currentIngreso.descripcion
        holder.monto_txt.text = "S/. ${String.format("%.2f", currentIngreso.monto)}"
        holder.categoria_txt.text = currentIngreso.categoria
        holder.fecha_txt.text = currentIngreso.fecha

        holder.btnEditar.setOnClickListener {
            onEditClick(currentIngreso)
        }

        holder.btnEliminar.setOnClickListener {
            onDeleteClick(currentIngreso)
        }
    }

    fun setData(ingresos: List<Ingresos>) {
        this.ingresosList = ingresos
        notifyDataSetChanged()
    }
}