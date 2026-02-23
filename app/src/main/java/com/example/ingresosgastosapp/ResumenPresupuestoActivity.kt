package com.example.ingresosgastosapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ResumenPresupuestoActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resumen_presupuesto)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        // Bottom Navigation (IDs estandarizados del include)
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val fabAdd = findViewById<FloatingActionButton>(R.id.fab_add)

        bottomNav.selectedItemId = R.id.nav_presupuesto
        bottomNav.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.nav_inicio -> {
                    startActivity(Intent(this, MainMenuActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_presupuesto -> true
                R.id.nav_ahorros -> {
                    startActivity(Intent(this, AhorrosActivity::class.java))
                    finish()
                    true
                }
                R.id.nav_perfil -> {
                    val intent = Intent(this, PerfilActivity::class.java)
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
            val bottomSheet = QuickActionsBottomSheet()
            bottomSheet.show(supportFragmentManager, "QuickActionsBottomSheet")
        }

        findViewById<android.view.View>(R.id.btn_revisar_planes).setOnClickListener {
            Toast.makeText(this, "Revisar planes próximamente", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getNombreUsuario(): String = intent.getStringExtra("NOMBRE_USUARIO") ?: "Usuario"
    private fun getEmailUsuario(): String = intent.getStringExtra("EMAIL_USUARIO") ?: ""
}