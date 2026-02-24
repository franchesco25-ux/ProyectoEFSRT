package com.example.ingresosgastosapp

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ingresosgastosapp.Adapter.HistorialAdapter
import com.example.ingresosgastosapp.Data.GastosViewModel
import com.example.ingresosgastosapp.Data.TipoTransaccion
import com.example.ingresosgastosapp.Data.TransaccionItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HistorialGastosActivity : BaseActivity() {

    private lateinit var rvHistorial: RecyclerView
    private lateinit var tvTotalMes: TextView

    private val gastosViewModel: GastosViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historial_gastos)

        // 1. Inicialización de Vistas
        tvTotalMes = findViewById(R.id.tvTotalMes)
        rvHistorial = findViewById(R.id.rvGastosHistorial)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)

        // 2. Configuración del Adapter
        val adapter = HistorialAdapter(
            onEditClick = { transaccion ->
                val intent = Intent(this, EditarGastoActivity::class.java)
                intent.putExtra("GASTO_ID", transaccion.id)
                startActivity(intent)
            },
            onDeleteClick = { transaccion ->
                confirmarEliminar(transaccion)
            }
        )

        rvHistorial.layoutManager = LinearLayoutManager(this)
        rvHistorial.adapter = adapter

        // 3. Observar datos reales de Gastos
        gastosViewModel.readAllData.observe(this) { listaGastos ->
            val transacciones = listaGastos.map {
                TransaccionItem(
                    it.id,
                    it.descripcion,
                    it.monto,
                    it.categoria,
                    it.fecha,
                    TipoTransaccion.GASTO
                )
            }
            adapter.setData(transacciones)

            // Calcular Total Real del Mes (Suma de gastos)
            val total = listaGastos.sumOf { it.monto }
            val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
            val monedaPref = prefs.getString("MONEDA_PRINCIPAL", "USD ($)")
            val currencySymbol = if (monedaPref != null && monedaPref.contains("(")) monedaPref.substringAfter("(").replace(")", "") else "$"

            tvTotalMes.text = "-$currencySymbol %,.2f".format(total)
        }

        // 4. Configuración de Clics
        btnBack.setOnClickListener { finish() }

        // 5. Configuración de Bottom Navigation personalizada
        setupCustomBottomNav("")
    }

    private fun confirmarEliminar(transaccion: TransaccionItem) {
        AlertDialog.Builder(this, R.style.AlertDialogTheme)
            .setTitle("Eliminar Gasto")
            .setMessage("¿Estás seguro de que deseas eliminar este gasto?")
            .setPositiveButton("Eliminar") { _, _ ->
                lifecycleScope.launch {
                    val gasto = gastosViewModel.getGastoById(transaccion.id)
                    if (gasto != null) {
                        gastosViewModel.deleteGastoWithBalance(gasto)
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@HistorialGastosActivity, "Gasto eliminado y balance restaurado", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}

