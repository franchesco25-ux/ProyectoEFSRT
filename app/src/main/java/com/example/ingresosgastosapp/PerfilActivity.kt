package com.example.ingresosgastosapp

import android.content.Intent
import android.os.Bundle
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import android.widget.TextView

class PerfilActivity : BaseActivity() {

    private lateinit var tvNombreUsuario: TextView
    private lateinit var tvEmailUsuario: TextView
    private lateinit var toolbar: MaterialToolbar
    private lateinit var btnLogout: MaterialButton
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var fabAdd: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        // 1. Inicializar vistas
        tvNombreUsuario = findViewById(R.id.tvNombreUsuario)
        tvEmailUsuario = findViewById(R.id.tvEmailUsuario)
        toolbar = findViewById(R.id.toolbarPerfil)
        btnLogout = findViewById(R.id.btnLogout)
        bottomNav = findViewById(R.id.bottom_navigation_perfil)
        fabAdd = findViewById(R.id.fab_add_perfil)

        // 2. Configurar el botón de retroceso de la Toolbar
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // 3. Mostrar Datos del Usuario
        val nombreExtra = intent.getStringExtra("NOMBRE_USUARIO") ?: "Usuario"
        val emailExtra = intent.getStringExtra("EMAIL_USUARIO") ?: ""
        tvNombreUsuario.text = nombreExtra
        tvEmailUsuario.text = emailExtra

        // 4. Configurar Barra de Navegación Inferior (Mismo comportamiento que MainMenu)
        bottomNav.selectedItemId = R.id.nav_perfil // Marcar "Perfil" como seleccionado
        
        bottomNav.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.nav_inicio -> {
                    finish() // Regresa al MainMenu que ya está en el stack
                    true
                }
                R.id.nav_presupuesto -> {
                    startActivity(Intent(this, ResumenPresupuestoActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_ahorros -> {
                    startActivity(Intent(this, AhorrosActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_perfil -> true
                else -> false
            }
        }

        // 5. Configurar FAB central (Agregar Gasto)
        fabAdd.setOnClickListener {
            startActivity(Intent(this, GastosActivity::class.java))
        }

        // 6. Configurar Cerrar Sesión
        btnLogout.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }
}