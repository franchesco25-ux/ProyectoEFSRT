package com.example.ingresosgastosapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.drawerlayout.widget.DrawerLayout
import com.example.ingresosgastosapp.Data.BalanceViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainMenuActivity : BaseActivity() {

    private lateinit var tvBalance: TextView
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navDrawer: View

    private val balanceViewModel: BalanceViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)

        // 1. Inicialización de vistas
        drawerLayout = findViewById(R.id.drawer_layout)
        navDrawer = findViewById(R.id.nav_drawer)
        tvBalance = findViewById(R.id.tvMainBalance)
        bottomNav = findViewById(R.id.bottom_navigation)
        val tvSaludo = findViewById<TextView>(R.id.tvSaludoDashboard)
        val btnDashboardGastos = findViewById<android.widget.Button>(R.id.btnMainGastos)
        val btnDashboardIngresos = findViewById<android.widget.Button>(R.id.btnMainIngresos)

        // 2. Configuración de saludo
        val nombre = intent.getStringExtra("NOMBRE_USUARIO") ?: "Usuario"
        tvSaludo.text = "Hola, $nombre"

        // 3. Configuración del Balance Real (Observando el ViewModel)
        balanceViewModel.balance.observe(this) { balanceEntity ->
            // Si el balance es nulo, mostramos 0.00
            val totalReal = balanceEntity?.total ?: 0.0
            tvBalance.text = "S/ %.2f".format(totalReal)

            // Opcional: Cambiar color si es negativo (como en el historial)
            if (totalReal < 0) {
                tvBalance.setTextColor(android.graphics.Color.parseColor("#DC3545")) // Rojo
            } else {
                tvBalance.setTextColor(android.graphics.Color.WHITE)
            }
        }

        // 4. Configuración de botones de acceso rápido (Dashboard)
        btnDashboardGastos.setOnClickListener {
            val intent = Intent(this, GastosActivity::class.java)
            startActivity(intent)
        }

        btnDashboardIngresos.setOnClickListener {
            // Asumiendo que quieres abrir la misma lógica o una similar para ingresos
            abrirHistorialFiltrado("Ingresos")
        }

        // 5. Listener de la barra de navegación inferior
        bottomNav.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.nav_inicio -> true
                R.id.nav_presupuesto -> {
                    startActivity(Intent(this, ResumenPresupuestoActivity::class.java))
                    true
                }
                R.id.nav_agregar -> {
                    startActivity(Intent(this, PruebaActivity::class.java))
                    true
                }
                R.id.nav_ahorros -> {
                    startActivity(Intent(this, AhorrosActivity::class.java))
                    true
                }
                R.id.nav_ajustes -> {
                    drawerLayout.openDrawer(navDrawer)
                    true
                }
                else -> false
            }
        }

        setupDrawerItems()
    }

    private fun setupDrawerItems() {
        // 1. INICIO
        navDrawer.findViewById<View>(R.id.drawer_item_inicio).setOnClickListener {
            drawerLayout.closeDrawer(navDrawer)
        }

        // 2. AGREGAR INGRESOS (Mantiene tu lógica actual a PruebaActivity o la que decidas)
        navDrawer.findViewById<View>(R.id.drawer_item_ingreso).setOnClickListener {
            drawerLayout.closeDrawer(navDrawer)
            startActivity(Intent(this, PruebaActivity::class.java))
        }

        // 3. AGREGAR GASTOS (Esta es la conexión que pediste)
        navDrawer.findViewById<View>(R.id.drawer_item_gasto).setOnClickListener {
            drawerLayout.closeDrawer(navDrawer) // Cerramos el menú antes de saltar
            val intent = Intent(this, GastosActivity::class.java)
            startActivity(intent)
        }

        // 4. ANÁLISIS / HISTORIAL COMPLETO
        navDrawer.findViewById<View>(R.id.drawer_item_historial).setOnClickListener {
            drawerLayout.closeDrawer(navDrawer)
            startActivity(Intent(this, HistorialGastosActivity::class.java))
        }

        // 5. REGISTROS FILTRADOS (Reutilizando tu función abrirHistorialFiltrado)
        navDrawer.findViewById<View>(R.id.drawer_item_lista_ingreso).setOnClickListener {
            abrirHistorialFiltrado("Ingresos")
        }

        navDrawer.findViewById<View>(R.id.drawer_item_lista_gasto).setOnClickListener {
            abrirHistorialFiltrado("Gastos")
        }

        // 6. EXPORTAR REPORTE
        navDrawer.findViewById<View>(R.id.drawer_item_reporte).setOnClickListener {
            drawerLayout.closeDrawer(navDrawer)
            exportarReporteExcel()
        }
    }

    private fun abrirHistorialFiltrado(tipo: String) {
        drawerLayout.closeDrawer(navDrawer)
        val intent = Intent(this, HistorialGastosActivity::class.java)
        intent.putExtra("FILTRO_TIPO", tipo) // Pasamos "Ingresos" o "Gastos"
        startActivity(intent)
    }

    private fun exportarReporteExcel() {
        // Por ahora mostramos un aviso. Para implementar esto real,
        // necesitarías una librería como Apache POI.
        android.widget.Toast.makeText(this, "Preparando reporte Excel...", android.widget.Toast.LENGTH_SHORT).show()
    }
}