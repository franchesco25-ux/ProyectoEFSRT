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
    private var nombreUsuario: String = "Usuario"
    private var emailUsuario: String = ""

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
        val imgProfile = findViewById<View>(R.id.imgProfile)
        
        // Botones y FAB
        val btnAgregar = findViewById<View>(R.id.btnMainAgregar)
        val btnEnviar = findViewById<View>(R.id.btnMainEnviar)
        val fabAdd = findViewById<FloatingActionButton>(R.id.fab_add)
        
        // Botones de acceso rápido
        val btnMetas = findViewById<View>(R.id.btn_quick_metas)
        val btnPresupuesto = findViewById<View>(R.id.btn_quick_presupuesto)
        val btnAnalisis = findViewById<View>(R.id.btn_quick_analisis)
        val btnMas = findViewById<View>(R.id.btn_quick_mas)

        // 2. Configuración de datos del usuario (Desde LoginActivity)
        nombreUsuario = intent.getStringExtra("NOMBRE_USUARIO") ?: "Usuario"
        emailUsuario = intent.getStringExtra("EMAIL_USUARIO") ?: ""
        tvSaludo.text = "Hola, $nombreUsuario"

        // 3. Configuración del Balance Real
        balanceViewModel.balance.observe(this) { balanceEntity ->
            val totalReal = balanceEntity?.total ?: 0.0
            tvBalance.text = "$ %.2f".format(totalReal)
            if (totalReal < 0) {
                tvBalance.setTextColor(android.graphics.Color.parseColor("#EF4444"))
            } else {
                tvBalance.setTextColor(android.graphics.Color.WHITE)
            }
        }

        // 4. Configuración de clics
        imgProfile.setOnClickListener { abrirPerfil() }
        btnAgregar.setOnClickListener { startActivity(Intent(this, PruebaActivity::class.java)) }
        btnEnviar.setOnClickListener { abrirHistorialFiltrado("Gastos") }
        fabAdd.setOnClickListener { startActivity(Intent(this, GastosActivity::class.java)) }
        btnMas.setOnClickListener { drawerLayout.openDrawer(navDrawer) }

        // REDIRECCIONES DE ACCESO RÁPIDO
        btnMetas.setOnClickListener { startActivity(Intent(this, AhorrosActivity::class.java)) }
        btnPresupuesto.setOnClickListener { startActivity(Intent(this, ResumenPresupuestoActivity::class.java)) }
        btnAnalisis.setOnClickListener { startActivity(Intent(this, HistorialGastosActivity::class.java)) }

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
                R.id.nav_perfil -> {
                    abrirPerfil()
                    true
                }
                else -> false
            }
        }

        setupDrawerItems()
    }

    private fun abrirPerfil() {
        val intent = Intent(this, PerfilActivity::class.java)
        intent.putExtra("NOMBRE_USUARIO", nombreUsuario)
        intent.putExtra("EMAIL_USUARIO", emailUsuario)
        startActivity(intent)
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
            startActivity(Intent(this, GastosActivity::class.java))
        }
        navDrawer.findViewById<View>(R.id.drawer_item_historial).setOnClickListener {
            drawerLayout.closeDrawer(navDrawer)
            startActivity(Intent(this, HistorialGastosActivity::class.java))
        }
        navDrawer.findViewById<View>(R.id.drawer_item_lista_ingreso).setOnClickListener { abrirHistorialFiltrado("Ingresos") }
        navDrawer.findViewById<View>(R.id.drawer_item_lista_gasto).setOnClickListener { abrirHistorialFiltrado("Gastos") }
        navDrawer.findViewById<View>(R.id.drawer_item_reporte).setOnClickListener {
            drawerLayout.closeDrawer(navDrawer)
            android.widget.Toast.makeText(this, "Preparando reporte Excel...", android.widget.Toast.LENGTH_SHORT).show()
        }
    }

    private fun abrirHistorialFiltrado(tipo: String) {
        drawerLayout.closeDrawer(navDrawer)
        val intent = Intent(this, HistorialGastosActivity::class.java)
        intent.putExtra("FILTRO_TIPO", tipo)
        startActivity(intent)
    }
}