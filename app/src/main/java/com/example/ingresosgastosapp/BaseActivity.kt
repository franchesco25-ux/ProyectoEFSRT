package com.example.ingresosgastosapp

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

/**
 * Actividad base: Configuración pura de pantalla completa para el diseño Stitch.
 */
abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Activamos pantalla completa (Edge-to-Edge)
        enableEdgeToEdge()

        // 2. Aplicamos ajuste inteligente: 
        // Solo arriba (status bar) para no tapar la hora/batería.
        // Abajo dejamos 0 para que nuestro menú flote perfectamente.
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                0 // Sin espacio extra abajo para evitar el "doble menú"
            )
            insets
        }
    }
}
