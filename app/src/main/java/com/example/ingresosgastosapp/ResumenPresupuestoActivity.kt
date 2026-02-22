package com.example.ingresosgastosapp

import android.os.Bundle
import com.example.ingresosgastosapp.BaseActivity
import androidx.appcompat.widget.Toolbar
// import com.google.android.material.bottomnavigation.BottomNavigationView
// import com.google.android.material.floatingactionbutton.FloatingActionButton

class ResumenPresupuestoActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resumen_presupuesto)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        /* 
        // Lógica comentada temporalmente para evitar errores de compilación
        // ya que la barra está comentada en el XML
        
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation_presupuesto)
        val fabAdd = findViewById<FloatingActionButton>(R.id.fab_add_presupuesto)

        bottomNav.selectedItemId = R.id.nav_presupuesto
        bottomNav.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.nav_inicio -> {
                    finish()
                    true
                }
                R.id.nav_presupuesto -> true
                R.id.nav_ahorros -> {
                    startActivity(android.content.Intent(this, AhorrosActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_perfil -> {
                    val intent = android.content.Intent(this, PerfilActivity::class.java)
                    intent.putExtra("NOMBRE_USUARIO", getNombreUsuario())
                    intent.putExtra("EMAIL_USUARIO", getEmailUsuario())
                    startActivity(intent)
                    finish()
                    true
                }
                else -> false
            }
        }

        fabAdd.setOnClickListener {
            startActivity(android.content.Intent(this, GastosActivity::class.java))
        }
        */

        findViewById<android.view.View>(R.id.btn_revisar_planes).setOnClickListener {
            android.widget.Toast.makeText(this, "Revisar planes próximamente", android.widget.Toast.LENGTH_SHORT).show()
        }
    }

    private fun getNombreUsuario(): String = intent.getStringExtra("NOMBRE_USUARIO") ?: "Usuario"
    private fun getEmailUsuario(): String = intent.getStringExtra("EMAIL_USUARIO") ?: ""
}