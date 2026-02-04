package com.example.ingresosgastosapp.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ingresosgastosapp.Data.Gastos
import com.example.ingresosgastosapp.R
import java.text.SimpleDateFormat
import java.util.Locale

class GastosRecientesAdapter(
    private val onItemClick: ((Gastos) -> Unit)? = null
) : ListAdapter<Gastos, GastosRecientesAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_gasto_reciente, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), onItemClick)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDescripcion: TextView = itemView.findViewById(R.id.tv_descripcion)
        private val tvFecha: TextView = itemView.findViewById(R.id.tv_fecha)
        private val tvMonto: TextView = itemView.findViewById(R.id.tv_monto)
        private val iconGasto: ImageView = itemView.findViewById(R.id.icon_gasto)

        fun bind(gasto: Gastos, onItemClick: ((Gastos) -> Unit)?) {
            tvDescripcion.text = gasto.descripcion.ifBlank { gasto.categoria }
            tvMonto.text = "-S/ %.2f".format(gasto.monto)
            tvFecha.text = formatFecha(gasto.fecha)
            itemView.setOnClickListener { onItemClick?.invoke(gasto) }
        }

        private fun formatFecha(fecha: String): String {
            return try {
                val parte = fecha.split("T")[0]
                val input = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val output = SimpleDateFormat("dd MMM, yyyy", Locale.getDefault())
                output.format(input.parse(parte)!!)
            } catch (e: Exception) {
                "Hoy"
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Gastos>() {
        override fun areItemsTheSame(old: Gastos, new: Gastos) = old.id == new.id
        override fun areContentsTheSame(old: Gastos, new: Gastos) = old == new
    }
}
