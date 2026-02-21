package com.example.ingresosgastosapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class AhorrosActivity : BaseActivity() {
//hola
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ahorros)

        // 1. Inicializar Vistas
        val btnCrearMeta = findViewById<TextView>(R.id.btn_crear_meta)
        val fabAdd = findViewById<FloatingActionButton>(R.id.fab_add_ahorros)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation_ahorros)
        
        // Tarjetas de metas corregidas con los IDs del XML
        val cardEmergencia = findViewById<View>(R.id.card_meta_emergencia)
        val cardViaje = findViewById<View>(R.id.card_meta_viaje)

        // 2. Configurar Clics de Redirección
        val abrirEdicion = View.OnClickListener {
            val intent = Intent(this, EditarMetaActivity::class.java)
            startActivity(intent)
        }

        cardEmergencia?.setOnClickListener(abrirEdicion)
        cardViaje?.setOnClickListener(abrirEdicion)

        btnCrearMeta.setOnClickListener {
            Toast.makeText(this, "Función para crear meta próximamente", Toast.LENGTH_SHORT).show()
        }

        fabAdd.setOnClickListener {
            startActivity(Intent(this, PruebaActivity::class.java))
        }

        // 3. Configurar Navegación Inferior
        bottomNav.selectedItemId = R.id.nav_ahorros
        bottomNav.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.nav_inicio -> {
                    startActivity(Intent(this, MainMenuActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_presupuesto -> {
                    startActivity(Intent(this, ResumenPresupuestoActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_ahorros -> true
                R.id.nav_ajustes -> {
                    Toast.makeText(this, "Ajustes", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
    }
}
