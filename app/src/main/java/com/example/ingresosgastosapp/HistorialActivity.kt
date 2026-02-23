package com.example.ingresosgastosapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HistorialActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historial)

        val btnVolver = findViewById<ImageButton>(R.id.btnVolverHistoriales)
        val cardGastos = findViewById<LinearLayout>(R.id.cardHistorialGastos)
        val cardIngresos = findViewById<LinearLayout>(R.id.cardHistorialIngresos)
        val cardMetas = findViewById<LinearLayout>(R.id.cardHistorialMetas)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val fabAdd = findViewById<FloatingActionButton>(R.id.fab_add)

        btnVolver.setOnClickListener {
            finish()
        }

        // Navigate passing FILTRO_TIPO to HistorialGastosActivity
        cardGastos.setOnClickListener {
            val intent = Intent(this, HistorialGastosActivity::class.java)
            intent.putExtra("FILTRO_TIPO", "Gastos")
            startActivity(intent)
        }

        cardIngresos.setOnClickListener {
            val intent = Intent(this, HistorialGastosActivity::class.java)
            intent.putExtra("FILTRO_TIPO", "Ingresos")
            startActivity(intent)
        }

        // Navigate to Ahorros for Metas
        cardMetas.setOnClickListener {
            val intent = Intent(this, AhorrosActivity::class.java)
            startActivity(intent)
        }

        // FAB Action
        fabAdd.setOnClickListener {
            val bottomSheet = QuickActionsBottomSheet()
            bottomSheet.show(supportFragmentManager, "QuickActionsBottomSheet")
        }

        // Para este activity desenmarcamos la navegación porque Historial no está predeterminado en el BottomNav
        val navMenu = bottomNav.menu
        navMenu.setGroupCheckable(0, false, true)

        bottomNav.setOnItemSelectedListener { item ->
            navMenu.setGroupCheckable(0, true, true)
            when(item.itemId) {
                R.id.nav_inicio -> {
                    startActivity(Intent(this, MainMenuActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                R.id.nav_presupuesto -> {
                    startActivity(Intent(this, ResumenPresupuestoActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                R.id.nav_ahorros -> {
                    startActivity(Intent(this, AhorrosActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                R.id.nav_perfil -> {
                    val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
                    val nombreUsuario = prefs.getString("NOMBRE_USUARIO", "Usuario") ?: "Usuario"
                    val emailUsuario = prefs.getString("EMAIL_USUARIO", "") ?: ""
                    
                    val intent = Intent(this, PerfilActivity::class.java)
                    intent.putExtra("NOMBRE_USUARIO", nombreUsuario)
                    intent.putExtra("EMAIL_USUARIO", emailUsuario)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                else -> false
            }
        }
    }
}
