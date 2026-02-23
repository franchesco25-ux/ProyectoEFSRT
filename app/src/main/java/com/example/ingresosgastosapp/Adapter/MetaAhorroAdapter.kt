package com.example.ingresosgastosapp.Adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ingresosgastosapp.Data.MetaAhorro
import com.example.ingresosgastosapp.R
import java.text.NumberFormat
import java.util.Locale

class MetaAhorroAdapter(
    private val onItemClick: (MetaAhorro) -> Unit
) : ListAdapter<MetaAhorro, MetaAhorroAdapter.MetaViewHolder>(DiffCallback) {

    companion object DiffCallback : DiffUtil.ItemCallback<MetaAhorro>() {
        override fun areItemsTheSame(oldItem: MetaAhorro, newItem: MetaAhorro) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: MetaAhorro, newItem: MetaAhorro) = oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MetaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_meta_ahorro, parent, false)
        return MetaViewHolder(view)
    }

    override fun onBindViewHolder(holder: MetaViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class MetaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvNombre: TextView = itemView.findViewById(R.id.tv_nombre)
        private val tvMetaObjetivo: TextView = itemView.findViewById(R.id.tv_meta_objetivo)
        private val tvEstado: TextView = itemView.findViewById(R.id.tv_estado)
        private val tvMontoActual: TextView = itemView.findViewById(R.id.tv_monto_actual)
        private val tvPorcentaje: TextView = itemView.findViewById(R.id.tv_porcentaje)
        private val progressBar: ProgressBar = itemView.findViewById(R.id.progress_bar)
        private val tvFaltante: TextView = itemView.findViewById(R.id.tv_faltante)
        private val iconBg: View = itemView.findViewById(R.id.icon_bg)
        private val ivIcon: ImageView = itemView.findViewById(R.id.iv_icon)

        fun bind(meta: MetaAhorro) {
            val nf = NumberFormat.getNumberInstance(Locale.US).apply {
                minimumFractionDigits = 2
                maximumFractionDigits = 2
            }

            tvNombre.text = meta.nombre
            tvMetaObjetivo.text = if (meta.completada) "Meta Alcanzada: S/.${nf.format(meta.montoObjetivo)}" else "Meta: S/.${nf.format(meta.montoObjetivo)}"
            tvMontoActual.text = "S/.${nf.format(meta.montoActual)}"

            val porcentaje = if (meta.montoObjetivo > 0) ((meta.montoActual / meta.montoObjetivo) * 100).toInt().coerceAtMost(100) else 0
            tvPorcentaje.text = "${porcentaje}%"
            progressBar.progress = porcentaje

            val faltante = (meta.montoObjetivo - meta.montoActual).coerceAtLeast(0.0)

            // Color por meta
            try {
                val metaColor = Color.parseColor(meta.color)
                iconBg.backgroundTintList = android.content.res.ColorStateList.valueOf(Color.argb(26, Color.red(metaColor), Color.green(metaColor), Color.blue(metaColor)))
                ivIcon.imageTintList = android.content.res.ColorStateList.valueOf(metaColor)
                tvPorcentaje.setTextColor(metaColor)
            } catch (_: Exception) { }

            // Load custom icon
            try {
                val resId = itemView.context.resources.getIdentifier(meta.icono, "drawable", itemView.context.packageName)
                if (resId != 0) {
                    ivIcon.setImageResource(resId)
                }
            } catch (_: Exception) { }

            if (meta.completada || porcentaje >= 100) {
                tvEstado.text = "COMPLETADO"
                tvEstado.setTextColor(Color.parseColor("#0DF259"))
                tvEstado.backgroundTintList = android.content.res.ColorStateList.valueOf(Color.parseColor("#1A0DF259"))
                tvFaltante.visibility = View.GONE
                tvMontoActual.setTextColor(Color.parseColor("#0DF259"))
            } else {
                tvEstado.text = if (porcentaje > 0) "PROGRESO" else "ACTIVO"
                tvEstado.setTextColor(Color.parseColor("#0DF259"))
                tvEstado.backgroundTintList = android.content.res.ColorStateList.valueOf(Color.parseColor("#1A0DF259"))
                tvFaltante.visibility = View.VISIBLE
                tvFaltante.text = "Faltan S/.${nf.format(faltante)} para completar"
                tvMontoActual.setTextColor(Color.WHITE)
            }

            itemView.setOnClickListener { onItemClick(meta) }
        }
    }
}
