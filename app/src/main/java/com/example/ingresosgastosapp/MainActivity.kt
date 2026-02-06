package com.example.ingresosgastosapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import com.example.ingresosgastosapp.BaseActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ingresosgastosapp.Data.BalanceViewModel
import com.example.ingresosgastosapp.Data.GastosViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.lifecycle.ViewModelProvider

class MainActivity : BaseActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navDrawer: LinearLayout
    private lateinit var tvBalance: TextView
    private lateinit var tvBalanceVariacion: TextView
    private lateinit var btnAgregar: View
    private lateinit var btnEnviar: View
    private lateinit var recyclerGastosRecientes: androidx.recyclerview.widget.RecyclerView

    private lateinit var bottomNav: BottomNavigationView

    private val balanceViewModel: BalanceViewModel by viewModels()
    private lateinit var gastosViewModel: GastosViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gastosViewModel = ViewModelProvider(this)[GastosViewModel::class.java]

        drawerLayout = findViewById(R.id.drawer_layout)
        navDrawer = findViewById(R.id.nav_drawer)
        tvBalance = findViewById(R.id.tvBalance)
        tvBalanceVariacion = findViewById(R.id.tvBalanceVariacion)
        btnAgregar = findViewById(R.id.btnAgregar)
        btnEnviar = findViewById(R.id.btnEnviar)

        val nombreUsuario = intent.getStringExtra("NOMBRE_USUARIO")
        if (nombreUsuario != null) {
            findViewById<TextView>(R.id.tvSaludo).text = "Hola, $nombreUsuario"
        }

        findViewById<View>(R.id.btn_menu).setOnClickListener {
            drawerLayout.openDrawer(navDrawer)
        }

        findViewById<View>(R.id.btn_notifications).setOnClickListener {
            Toast.makeText(this, "Notificaciones próximamente", Toast.LENGTH_SHORT).show()
        }

        balanceViewModel.balance.observe(this, Observer { balance ->
            val total = balance?.total ?: 0.0
            tvBalance.text = "S/ %.2f".format(total)
            tvBalanceVariacion.text = "+0%"
        })

        btnAgregar.setOnClickListener {
            startActivity(Intent(this, PruebaActivity::class.java))
        }
        btnEnviar.setOnClickListener {
            startActivity(Intent(this, GastosActivity::class.java))
        }

        findViewById<View>(R.id.quick_metas).setOnClickListener {
            startActivity(Intent(this, AhorrosActivity::class.java))
        }
        findViewById<View>(R.id.quick_analisis).setOnClickListener {
            startActivity(Intent(this, HistorialGastosActivity::class.java))
        }
        findViewById<View>(R.id.quick_mas).setOnClickListener {
            drawerLayout.openDrawer(navDrawer)
        }

        setupDrawerItems()
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
        navDrawer.findViewById<View>(R.id.drawer_item_reporte).setOnClickListener {
            drawerLayout.closeDrawer(navDrawer)
            Toast.makeText(this, "Exportar Reportes próximamente", Toast.LENGTH_SHORT).show()
        }
    }
}
