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
import com.example.ingresosgastosapp.Data.Gasto
import com.example.ingresosgastosapp.R

class GastosAdapter( private var gastos: List<Gasto>, private var categorias: List<String>, context: Context):
    RecyclerView.Adapter<GastosAdapter.GastosViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GastosViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_agregar_gasto, parent, false)
        return GastosViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: GastosViewHolder,
        position: Int
    ) {

        val gasto = gastos[position]
        holder.monto.setText(gasto.monto.toString())
        holder.descripcion.setText(gasto.descripcion)
        val context = holder.itemView.context
        val categoriaAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, categorias)
        categoriaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        holder.categoria.adapter = categoriaAdapter

        val catIndex = categorias.indexOf(gasto.categoria)
        if (catIndex >= 0) holder.categoria.setSelection(catIndex)


        holder.monto.isEnabled = false
        holder.descripcion.isEnabled = false
        holder.categoria.isEnabled = false
        holder.btnGuardar.visibility = View.GONE
    }


    override fun getItemCount(): Int = gastos.size

    class GastosViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val monto: EditText = itemView.findViewById(R.id.etMontoGasto)
        val descripcion: EditText = itemView.findViewById(R.id.etDescripcionGasto)
        val categoria : Spinner = itemView.findViewById(R.id.spinnerCategoriaGasto)
        val btnGuardar: Button = itemView.findViewById(R.id.btnGuardarGasto)
    }

    fun refreshData(newGastos: List<Gasto>){
        gastos = newGastos
        notifyDataSetChanged()
    }
}