package com.example.ingresosgastosapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast

class AhorrosActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ahorros)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener {
            startActivity(Intent(this, MainMenuActivity::class.java))
            finish()
        }

        // 1. Inicializar Vistas
        val btnCrearMeta = findViewById<TextView>(R.id.btn_crear_meta)
        
        // Tarjetas de metas
        val cardEmergencia = findViewById<View>(R.id.card_meta_emergencia)
        val cardViaje = findViewById<View>(R.id.card_meta_viaje)

        // Bottom Nav personalizado
        setupCustomBottomNav("ahorros")


        val abrirEdicion = View.OnClickListener {
            startActivity(Intent(this, EditarMetaActivity::class.java))
        }

        cardEmergencia?.setOnClickListener(abrirEdicion)
        cardViaje?.setOnClickListener(abrirEdicion)

        btnCrearMeta.setOnClickListener {
            Toast.makeText(this, "Función para crear meta próximamente", Toast.LENGTH_SHORT).show()
        }
    }

    private fun abrirPerfil() {
        val intentProfile = Intent(this, PerfilActivity::class.java)
        intentProfile.putExtra("NOMBRE_USUARIO", intent.getStringExtra("NOMBRE_USUARIO"))
        intentProfile.putExtra("EMAIL_USUARIO", intent.getStringExtra("EMAIL_USUARIO"))
        startActivity(intentProfile)
    }
}