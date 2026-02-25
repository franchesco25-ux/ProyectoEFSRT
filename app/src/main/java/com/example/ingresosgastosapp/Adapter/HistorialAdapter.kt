package com.example.ingresosgastosapp.Adapter

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ingresosgastosapp.Data.TipoTransaccion
import com.example.ingresosgastosapp.Data.TransaccionItem
import com.example.ingresosgastosapp.R
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
        val btnOpciones: ImageButton = itemView.findViewById(R.id.btnOpciones)
        val iconContainer: FrameLayout = itemView.findViewById(R.id.iconContainer)
        val ivTipoIcon: ImageView = itemView.findViewById(R.id.ivTipoIcon)
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

        val context = holder.itemView.context
        val prefs = context.getSharedPreferences("user_prefs", android.content.Context.MODE_PRIVATE)
        val monedaPref = prefs.getString("MONEDA_PRINCIPAL", "USD ($)")
        val currencySymbol = if (monedaPref != null && monedaPref.contains("(")) monedaPref.substringAfter("(").replace(")", "") else "$"

        // Configurar iconos y colores por categoría
        val iconResYColor = getIconoYCategoria(transaccion.categoria, transaccion.tipo)
        holder.ivTipoIcon.setImageResource(iconResYColor.first)
        
        try {
            val color = Color.parseColor(iconResYColor.second)
            holder.ivTipoIcon.setColorFilter(color, PorterDuff.Mode.SRC_IN)
            holder.iconContainer.backgroundTintList = ColorStateList.valueOf(Color.argb(33, Color.red(color), Color.green(color), Color.blue(color)))
        } catch (e: Exception) {
            holder.ivTipoIcon.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN)
        }

        // Configurar colores según el tipo
        when (transaccion.tipo) {
            TipoTransaccion.INGRESO -> {
                holder.tvTipo.text = "INGRESO"
                holder.tvTipo.setTextColor(Color.parseColor("#28A745"))
                holder.tvMonto.text = "+ $currencySymbol ${String.format("%,.2f", transaccion.monto)}"
                holder.tvMonto.setTextColor(Color.parseColor("#28A745"))
            }
            TipoTransaccion.GASTO -> {
                holder.tvTipo.text = "GASTO"
                holder.tvTipo.setTextColor(Color.parseColor("#DC3545"))
                holder.tvMonto.text = "- $currencySymbol ${String.format("%,.2f", transaccion.monto)}"
                holder.tvMonto.setTextColor(Color.parseColor("#DC3545"))

                holder.tvMonto.text = "+ $currencySymbol ${String.format("%,.2f", transaccion.monto)}"
                holder.tvMonto.setTextColor(Color.parseColor("#0DF259"))
            }
            TipoTransaccion.GASTO -> {
                holder.tvTipo.text = "GASTO"
                holder.tvMonto.text = "- $currencySymbol ${String.format("%,.2f", transaccion.monto)}"
                holder.tvMonto.setTextColor(Color.parseColor("#FF5252"))

            }
        }

        holder.btnOpciones.setOnClickListener { view ->
            val popup = PopupMenu(context, view)
            popup.inflate(R.menu.menu_opciones_transaccion)
            popup.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.action_editar -> {
                        onEditClick(transaccion)
                        true
                    }
                    R.id.action_eliminar -> {
                        onDeleteClick(transaccion)
                        true
                    }
                    else -> false
                }
            }
            popup.show()
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

    private fun getIconoYCategoria(categoria: String, tipo: TipoTransaccion): Pair<Int, String> {
        if (tipo == TipoTransaccion.INGRESO) {
            return Pair(R.drawable.ic_wallet, "#0DF259") // Green wallet for income
        }
        
        return when (categoria.lowercase()) {
            "comida" -> Pair(R.drawable.ic_meta_comida, "#F97316") // Orange
            "transporte" -> Pair(R.drawable.ic_meta_auto, "#3B82F6") // Blue
            "salud" -> Pair(R.drawable.ic_meta_hospital, "#E11D48") // Red
            "entretenimiento" -> Pair(R.drawable.ic_meta_musica, "#A855F7") // Purple
            "educación", "educacion" -> Pair(R.drawable.ic_meta_libro, "#0DF259") // Green
            "servicios" -> Pair(R.drawable.ic_meta_herramientas, "#EAB308") // Yellow
            "ropa" -> Pair(R.drawable.ic_meta_compras, "#EC4899") // Pink
            else -> Pair(R.drawable.ic_history, "#6B7280") // Grey for others
        }
    }
}

