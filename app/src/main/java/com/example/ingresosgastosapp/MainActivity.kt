package com.example.ingresosgastosapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.ingresosgastosapp.Data.BalanceViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var tvBalance: TextView
    private lateinit var btnAgregarIngreso: Button
    private lateinit var btnAgregarGasto: Button
    private lateinit var btnHistorial: Button
    private lateinit var btnExportarReportes: Button

    private val balanceViewModel: BalanceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvBalance = findViewById(R.id.tvBalance)
        btnAgregarIngreso = findViewById(R.id.btnAgregarIngreso)
        btnAgregarGasto = findViewById(R.id.btnAgregarGasto)
        btnHistorial = findViewById(R.id.btnHistorial)
        btnExportarReportes = findViewById(R.id.btnExportarReportes)

        // 👀 Observa el balance en tiempo real
        balanceViewModel.balance.observe(this, Observer { balance ->
            val total = balance?.total ?: 0.0
            tvBalance.text = "Balance: S/ %.2f".format(total)
        })

        btnAgregarIngreso.setOnClickListener {
            val intent = Intent(this, PruebaActivity::class.java)
            startActivity(intent)
        }

        btnAgregarGasto.setOnClickListener {
            val intent = Intent(this, GastosActivity::class.java)
            startActivity(intent)
        }

        btnHistorial.setOnClickListener {
            val intent = Intent(this, HistorialGastosActivity::class.java)
            startActivity(intent)
        }

        btnExportarReportes.setOnClickListener {
            Toast.makeText(this, "Función Exportar Reportes próximamente", Toast.LENGTH_SHORT).show()
        }
    }
}