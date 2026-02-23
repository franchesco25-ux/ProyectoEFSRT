package com.example.ingresosgastosapp

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ingresosgastosapp.Adapter.HistorialAdapter
import com.example.ingresosgastosapp.Data.IngresosViewModel
import com.example.ingresosgastosapp.Data.TipoTransaccion
import com.example.ingresosgastosapp.Data.TransaccionItem
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HistorialActivity : BaseActivity() {

    private lateinit var rvHistorial: RecyclerView
    private lateinit var tvTotalMes: TextView
    private lateinit var bottomNav: BottomNavigationView
    
    private val ingresosViewModel: IngresosViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historial)

        // 1. Inicialización de Vistas
        tvTotalMes = findViewById(R.id.tvTotalMes)
        rvHistorial = findViewById(R.id.rvIngresosHistorial) // Corregido el ID aquí
        bottomNav = findViewById(R.id.bottom_navigation_historial)
        
        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        val fabAdd = findViewById<FloatingActionButton>(R.id.fab_add_historial)

        // 2. Configuración del Adapter
        val adapter = HistorialAdapter(
            onEditClick = { /* Lógica de edición */ },
            onDeleteClick = { /* Lógica de eliminación */ }
        )
        
        rvHistorial.layoutManager = LinearLayoutManager(this)
        rvHistorial.adapter = adapter

        // 3. Observar datos reales de Ingresos
        ingresosViewModel.readAllData.observe(this) { listaIngresos ->
            val transacciones = listaIngresos.map { 
                TransaccionItem(
                    it.id, 
                    it.descripcion, 
                    it.monto, 
                    it.categoria, 
                    it.fecha, 
                    TipoTransaccion.INGRESO
                ) 
            }
            adapter.setData(transacciones)
            
            // Calcular Total Real del Mes (Suma de ingresos)
            val total = listaIngresos.sumOf { it.monto }
            val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
            val monedaPref = prefs.getString("MONEDA_PRINCIPAL", "USD ($)")
            val currencySymbol = if (monedaPref != null && monedaPref.contains("(")) monedaPref.substringAfter("(").replace(")", "") else "$"
            
            tvTotalMes.text = "+$currencySymbol %.2f".format(total)
        }

        // 4. Configuración de Clics
        btnBack.setOnClickListener { finish() }
        
        fabAdd.setOnClickListener {
            // Aquí puedes abrir la pantalla para agregar ingreso
        }

        // 5. Configuración de Bottom Navigation
        bottomNav.selectedItemId = R.id.nav_ahorros
        bottomNav.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.nav_inicio -> {
                    startActivity(Intent(this, MainMenuActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_perfil -> {
                    startActivity(Intent(this, PerfilActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }
}
