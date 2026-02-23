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

        val bottomNav = findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(R.id.bottom_navigation)
        val fabAdd = findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fab_add)

        bottomNav.selectedItemId = R.id.nav_presupuesto
        bottomNav.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.nav_inicio -> {
                    startActivity(android.content.Intent(this, MainMenuActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                R.id.nav_presupuesto -> true
                R.id.nav_ahorros -> {
                    startActivity(android.content.Intent(this, AhorrosActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true
                }
                R.id.nav_perfil -> {
                    val intent = android.content.Intent(this, PerfilActivity::class.java)
                    intent.putExtra("NOMBRE_USUARIO", getNombreUsuario())
                    intent.putExtra("EMAIL_USUARIO", getEmailUsuario())
                    startActivity(intent)
                    overridePendingTransition(0, 0)
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

        findViewById<android.view.View>(R.id.btn_revisar_planes).setOnClickListener {
            android.widget.Toast.makeText(this, "Revisar planes próximamente", android.widget.Toast.LENGTH_SHORT).show()
        }
    }

    private fun getNombreUsuario(): String = intent.getStringExtra("NOMBRE_USUARIO") ?: "Usuario"
    private fun getEmailUsuario(): String = intent.getStringExtra("EMAIL_USUARIO") ?: ""
}