package com.example.ingresosgastosapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.drawerlayout.widget.DrawerLayout
import com.example.ingresosgastosapp.Data.BalanceViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

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
        
        // Nuevos IDs del diseño de Stitch
        val btnAgregar = findViewById<View>(R.id.btnMainAgregar)
        val btnEnviar = findViewById<View>(R.id.btnMainEnviar)
        val fabAdd = findViewById<FloatingActionButton>(R.id.fab_add)

        // 2. Configuración de saludo
        val nombre = intent.getStringExtra("NOMBRE_USUARIO") ?: "Alex"
        tvSaludo.text = "Hola, $nombre"

        // 3. Configuración del Balance Real (Observando el ViewModel)
        balanceViewModel.balance.observe(this) { balanceEntity ->
            val totalReal = balanceEntity?.total ?: 0.0
            tvBalance.text = "$ %.2f".format(totalReal)

            if (totalReal < 0) {
                tvBalance.setTextColor(android.graphics.Color.parseColor("#EF4444")) // Rojo Stitch
            } else {
                tvBalance.setTextColor(android.graphics.Color.WHITE)
            }
        }

        // 4. Configuración de botones de acceso rápido (Dashboard)
        btnAgregar.setOnClickListener {
            startActivity(Intent(this, PruebaActivity::class.java))
        }

        btnEnviar.setOnClickListener {
            abrirHistorialFiltrado("Gastos")
        }

        fabAdd.setOnClickListener {
            startActivity(Intent(this, PruebaActivity::class.java))
        }

        // 5. Listener de la barra de navegación inferior
        bottomNav.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.nav_inicio -> true
                R.id.nav_presupuesto -> {
                    startActivity(Intent(this, ResumenPresupuestoActivity::class.java))
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
        navDrawer.findViewById<View>(R.id.drawer_item_inicio).setOnClickListener {
            drawerLayout.closeDrawer(navDrawer)
        }

        navDrawer.findViewById<View>(R.id.drawer_item_ingreso).setOnClickListener {
            drawerLayout.closeDrawer(navDrawer)
            startActivity(Intent(this, PruebaActivity::class.java))
        }

        navDrawer.findViewById<View>(R.id.drawer_item_gasto).setOnClickListener {
            drawerLayout.closeDrawer(navDrawer)
            val intent = Intent(this, GastosActivity::class.java)
            startActivity(intent)
        }

        navDrawer.findViewById<View>(R.id.drawer_item_historial).setOnClickListener {
            drawerLayout.closeDrawer(navDrawer)
            startActivity(Intent(this, HistorialGastosActivity::class.java))
        }

        navDrawer.findViewById<View>(R.id.drawer_item_lista_ingreso).setOnClickListener {
            abrirHistorialFiltrado("Ingresos")
        }

        navDrawer.findViewById<View>(R.id.drawer_item_lista_gasto).setOnClickListener {
            abrirHistorialFiltrado("Gastos")
        }

        navDrawer.findViewById<View>(R.id.drawer_item_reporte).setOnClickListener {
            drawerLayout.closeDrawer(navDrawer)
            exportarReporteExcel()
        }
    }

    private fun abrirHistorialFiltrado(tipo: String) {
        drawerLayout.closeDrawer(navDrawer)
        val intent = Intent(this, HistorialGastosActivity::class.java)
        intent.putExtra("FILTRO_TIPO", tipo)
        startActivity(intent)
    }

    private fun exportarReporteExcel() {
        android.widget.Toast.makeText(this, "Preparando reporte Excel...", android.widget.Toast.LENGTH_SHORT).show()
    }
}
