package com.example.ingresosgastosapp.Adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ingresosgastosapp.Data.TipoTransaccion
import com.example.ingresosgastosapp.Data.TransaccionItem
import com.example.ingresosgastosapp.R
import com.google.android.material.button.MaterialButton
import java.text.SimpleDateFormat
import java.util.*

class HistorialAdapter(
    private val onEditClick: (TransaccionItem) -> Unit,
    private val onDeleteClick: (TransaccionItem) -> Unit
) : RecyclerView.Adapter<HistorialAdapter.TransaccionViewHolder>() {

    private var transacciones = listOf<TransaccionItem>()

    class TransaccionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTipo: TextView = itemView.findViewById(R.id.tvTipoTransaccion)
        val tvDescripcion: TextView = itemView.findViewById(R.id.tvDescripcionHistorial)
        val tvCategoria: TextView = itemView.findViewById(R.id.tvCategoriaHistorial)
        val tvFecha: TextView = itemView.findViewById(R.id.tvFechaHistorial)
        val tvMonto: TextView = itemView.findViewById(R.id.tvMontoHistorial)
        val btnEditar: MaterialButton = itemView.findViewById(R.id.btnEditarTransaccion)
        val btnEliminar: MaterialButton = itemView.findViewById(R.id.btnEliminarTransaccion)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransaccionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_historial_transaccion, parent, false)
        return TransaccionViewHolder(view)
    }

    override fun getItemCount(): Int = transacciones.size

    override fun onBindViewHolder(holder: TransaccionViewHolder, position: Int) {
        val transaccion = transacciones[position]

        holder.tvDescripcion.text = transaccion.descripcion
        holder.tvCategoria.text = transaccion.categoria
        holder.tvFecha.text = formatFecha(transaccion.fecha)

        // Configurar colores según el tipo
        when (transaccion.tipo) {
            TipoTransaccion.INGRESO -> {
                holder.tvTipo.text = "INGRESO"
                holder.tvTipo.setTextColor(Color.parseColor("#28A745"))
                holder.tvMonto.text = "+ S/. ${String.format("%.2f", transaccion.monto)}"
                holder.tvMonto.setTextColor(Color.parseColor("#28A745"))
            }
            TipoTransaccion.GASTO -> {
                holder.tvTipo.text = "GASTO"
                holder.tvTipo.setTextColor(Color.parseColor("#DC3545"))
                holder.tvMonto.text = "- S/. ${String.format("%.2f", transaccion.monto)}"
                holder.tvMonto.setTextColor(Color.parseColor("#DC3545"))
            }
        }

        holder.btnEditar.setOnClickListener {
            onEditClick(transaccion)
        }

        holder.btnEliminar.setOnClickListener {
            onDeleteClick(transaccion)
        }
    }

    fun setData(nuevasTransacciones: List<TransaccionItem>) {
        this.transacciones = nuevasTransacciones.sortedByDescending { item ->
            try {
                val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault())
                sdf.parse(item.fecha)?.time ?: 0L
            } catch (e: Exception) {
                try {
                    val sdf2 = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    sdf2.parse(item.fecha)?.time ?: 0L
                } catch (e2: Exception) {
                    0L
                }
            }
        }
        notifyDataSetChanged()
    }

    private fun formatFecha(fecha: String): String {
        return try {
            val inputFormat = if (fecha.contains("T")) {
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault())
            } else {
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            }
            val date = inputFormat.parse(fecha)
            val outputFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            outputFormat.format(date!!)
        } catch (e: Exception) {
            fecha
        }
    }
}
