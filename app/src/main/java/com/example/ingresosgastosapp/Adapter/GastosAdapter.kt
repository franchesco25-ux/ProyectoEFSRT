package com.example.ingresosgastosapp.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Locale
import com.example.ingresosgastosapp.Data.Gastos
import com.example.ingresosgastosapp.R
import com.google.android.material.button.MaterialButton

class ListGastosAdapter(
    private val onEditClick: (Gastos) -> Unit,
    private val onDeleteClick: (Gastos) -> Unit
) : RecyclerView.Adapter<ListGastosAdapter.MyViewHolder>() {

    private var gastosList = emptyList<Gastos>()

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val id_txt: TextView = itemView.findViewById(R.id.idGasto_txt)
        val descripcion_txt: TextView = itemView.findViewById(R.id.descripcionGasto_txt)
        val monto_txt: TextView = itemView.findViewById(R.id.montoGasto_txt)
        val categoria_txt: TextView = itemView.findViewById(R.id.categoriaGasto_txt)
        val fecha_txt: TextView = itemView.findViewById(R.id.fechaGasto_txt)
        val btnEditar: MaterialButton = itemView.findViewById(R.id.btnEditarGasto)
        val btnEliminar: MaterialButton = itemView.findViewById(R.id.btnEliminarGasto)
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
        holder.monto_txt.text = "-S/ ${String.format("%.2f", currentGasto.monto)}"
        holder.categoria_txt.text = currentGasto.categoria
        holder.fecha_txt.text = formatFecha(currentGasto.fecha)

        holder.btnEditar.setOnClickListener {
            onEditClick(currentGasto)
        }

        holder.btnEliminar.setOnClickListener {
            onDeleteClick(currentGasto)
        }
    }

    fun setData(gastos: List<Gastos>) {
        this.gastosList = gastos
        notifyDataSetChanged()
    }

    private fun formatFecha(fecha: String): String {
        return try {
            val parte = fecha.split("T")[0]
            val input = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val output = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            output.format(input.parse(parte)!!)
        } catch (e: Exception) {
            fecha
        }
    }
}