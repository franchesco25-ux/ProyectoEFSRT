package com.example.ingresosgastosapp.Adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ingresosgastosapp.R
import java.text.SimpleDateFormat
import java.util.Locale

data class TransaccionReciente(
    val id: Int,
    val descripcion: String,
    val monto: Double,
    val categoria: String,
    val fecha: String,
    val esIngreso: Boolean
)

class TransaccionRecienteAdapter(
    private var items: List<TransaccionReciente> = emptyList()
) : RecyclerView.Adapter<TransaccionRecienteAdapter.ViewHolder>() {

    fun submitList(newItems: List<TransaccionReciente>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaccion_reciente, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ivTipoIcon: ImageView = itemView.findViewById(R.id.ivTipoIcon)
        private val tvDescripcion: TextView = itemView.findViewById(R.id.tvDescripcion)
        private val tvCategoria: TextView = itemView.findViewById(R.id.tvCategoria)
        private val tvMonto: TextView = itemView.findViewById(R.id.tvMonto)

        fun bind(item: TransaccionReciente) {
            tvDescripcion.text = item.descripcion.ifBlank { item.categoria }

            val fechaFormateada = formatFecha(item.fecha)
            tvCategoria.text = "${item.categoria} · $fechaFormateada"

            val prefs = itemView.context.getSharedPreferences("user_prefs",
                android.content.Context.MODE_PRIVATE)
            val monedaPref = prefs.getString("MONEDA_PRINCIPAL", "USD ($)")
            val sym = if (monedaPref != null && monedaPref.contains("("))
                monedaPref.substringAfter("(").replace(")", "") else "$"

            if (item.esIngreso) {
                tvMonto.text = "+$sym%,.2f".format(item.monto)
                tvMonto.setTextColor(Color.parseColor("#0df259"))
                ivTipoIcon.setImageResource(R.drawable.ic_attach_money)
                ivTipoIcon.setColorFilter(Color.parseColor("#0df259"))
            } else {
                tvMonto.text = "-$sym%,.2f".format(item.monto)
                tvMonto.setTextColor(Color.parseColor("#ef4444"))
                ivTipoIcon.setImageResource(R.drawable.ic_shopping_cart)
                ivTipoIcon.setColorFilter(Color.parseColor("#ef4444"))
            }
        }

        private fun formatFecha(fecha: String): String {
            return try {
                val inputFormat = if (fecha.contains("T")) {
                    SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault())
                } else {
                    SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                }
                val date = inputFormat.parse(fecha)
                val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                outputFormat.format(date!!)
            } catch (e: Exception) {
                fecha
            }
        }
    }
}

