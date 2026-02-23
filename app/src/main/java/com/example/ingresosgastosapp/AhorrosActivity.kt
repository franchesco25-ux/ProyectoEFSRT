package com.example.ingresosgastosapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class AhorrosActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ahorros)

        // 1. Inicializar Vistas
        val btnCrearMeta = findViewById<TextView>(R.id.btn_crear_meta)
        val imgProfile = findViewById<View>(R.id.img_profile_ahorros)
        
        // Tarjetas de metas
        val cardEmergencia = findViewById<View>(R.id.card_meta_emergencia)
        val cardViaje = findViewById<View>(R.id.card_meta_viaje)

        // Bottom Navigation (IDs estandarizados del include)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val fabAdd = findViewById<FloatingActionButton>(R.id.fab_add)

        // 2. Configurar Clics de Redirección
        imgProfile?.setOnClickListener { abrirPerfil() }

        val abrirEdicion = View.OnClickListener {
            startActivity(Intent(this, EditarMetaActivity::class.java))
        }

        cardEmergencia?.setOnClickListener(abrirEdicion)
        cardViaje?.setOnClickListener(abrirEdicion)

        btnCrearMeta.setOnClickListener {
            Toast.makeText(this, "Función para crear meta próximamente", Toast.LENGTH_SHORT).show()
        }

        // Bottom Navigation Logic
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
                R.id.nav_perfil -> {
                    abrirPerfil()
                    finish()
                    true
                }
                else -> false
            }
        }

        fabAdd.setOnClickListener {
            val bottomSheet = QuickActionsBottomSheet()
            bottomSheet.show(supportFragmentManager, "QuickActionsBottomSheet")
        }
    }

    private fun abrirPerfil() {
        val intentProfile = Intent(this, PerfilActivity::class.java)
        intentProfile.putExtra("NOMBRE_USUARIO", intent.getStringExtra("NOMBRE_USUARIO"))
        intentProfile.putExtra("EMAIL_USUARIO", intent.getStringExtra("EMAIL_USUARIO"))
        startActivity(intentProfile)
    }
}