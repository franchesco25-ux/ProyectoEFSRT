package com.example.ingresosgastosapp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ingresosgastosapp.Adapter.GastosAdapter
import com.example.ingresosgastosapp.DataBase.GastosDatabaseHelper
import com.example.ingresosgastosapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var tvBalance: TextView
    private lateinit var btnAgregarIngreso: Button
    private lateinit var btnAgregarGasto: Button
    private lateinit var btnHistorial: Button
    private lateinit var btnExportarReportes: Button



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        tvBalance = findViewById(R.id.tvBalance)
        btnAgregarIngreso = findViewById(R.id.btnAgregarIngreso) //componente btm
        btnAgregarGasto = findViewById(R.id.btnAgregarGasto) //cmpnte btm
        btnHistorial = findViewById(R.id.btnHistorial) //cmpnte btm
        btnExportarReportes = findViewById(R.id.btnExportarReportes) //cmpnte btm

        tvBalance.text = "Balance: $1000" //cambiar con una cuenta demo

        btnAgregarIngreso.setOnClickListener {
            val intent = Intent(this, AgregarIngresoActivity::class.java)
            startActivity(intent)
        }

        btnAgregarGasto.setOnClickListener {
            val intent = Intent(this, AgregarGastoActivity::class.java)
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
