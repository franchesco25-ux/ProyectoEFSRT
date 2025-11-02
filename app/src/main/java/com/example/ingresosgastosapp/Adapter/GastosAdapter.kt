package com.example.ingresosgastosapp.Adapter

import android.content.Context
import android.widget.ArrayAdapter;
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ingresosgastosapp.Data.Gastos
import com.example.ingresosgastosapp.R

class ListGastosAdapter : RecyclerView.Adapter<ListGastosAdapter.MyViewHolder>() {

    private var gastosList = emptyList<Gastos>()

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val id_txt: TextView = itemView.findViewById(R.id.idGasto_txt)
        val descripcion_txt: TextView = itemView.findViewById(R.id.descripcionGasto_txt)
        val monto_txt: TextView = itemView.findViewById(R.id.montoGasto_txt)
        val categoria_txt: TextView = itemView.findViewById(R.id.categoriaGasto_txt)
        val fecha_txt: TextView = itemView.findViewById(R.id.fechaGasto_txt)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.custom_row_gastos, parent, false)
        )
    }

    override fun getItemCount() = gastosList.size

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentGasto = gastosList[position]
        holder.id_txt.text = currentGasto.id.toString()
        holder.descripcion_txt.text = currentGasto.descripcion
        holder.monto_txt.text = "S/. ${currentGasto.monto}"
        holder.categoria_txt.text = currentGasto.categoria
        holder.fecha_txt.text = currentGasto.fecha
    }

    fun setData(gastos: List<Gastos>) {
        this.gastosList = gastos
        notifyDataSetChanged()
    }
}