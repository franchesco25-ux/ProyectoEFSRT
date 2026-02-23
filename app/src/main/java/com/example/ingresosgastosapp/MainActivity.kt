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


    private lateinit var tvBalance: TextView
    private lateinit var tvBalanceVariacion: TextView
    private lateinit var btnAgregar: View
    private lateinit var recyclerGastosRecientes: androidx.recyclerview.widget.RecyclerView

    private lateinit var bottomNav: BottomNavigationView

    private val balanceViewModel: BalanceViewModel by viewModels()
    private lateinit var gastosViewModel: GastosViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gastosViewModel = ViewModelProvider(this)[GastosViewModel::class.java]


        tvBalance = findViewById(R.id.tvBalance)
        tvBalanceVariacion = findViewById(R.id.tvBalanceVariacion)
        btnAgregar = findViewById(R.id.btnAgregar)

        val nombreUsuario = intent.getStringExtra("NOMBRE_USUARIO")
        if (nombreUsuario != null) {
            findViewById<TextView>(R.id.tvSaludo).text = "Hola, $nombreUsuario"
        }

        findViewById<View>(R.id.btn_menu).visibility = View.GONE

        findViewById<View>(R.id.btn_notifications).setOnClickListener {
            Toast.makeText(this, "Notificaciones próximamente", Toast.LENGTH_SHORT).show()
        }

        val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val monedaPref = prefs.getString("MONEDA_PRINCIPAL", "USD ($)")
        val currencySymbol = if (monedaPref != null && monedaPref.contains("(")) monedaPref.substringAfter("(").replace(")", "") else "$"

        balanceViewModel.balance.observe(this, Observer { balance ->
            val total = balance?.total ?: 0.0
            tvBalance.text = "$currencySymbol %.2f".format(total)
            tvBalanceVariacion.text = "+0%"
        })

        btnAgregar.setOnClickListener {
            val bottomSheet = QuickActionsBottomSheet()
            bottomSheet.show(supportFragmentManager, "QuickActionsBottomSheet")
        }

        findViewById<View>(R.id.quick_metas).setOnClickListener {
            startActivity(Intent(this, AhorrosActivity::class.java))
        }
        findViewById<View>(R.id.quick_analisis).setOnClickListener {
            startActivity(Intent(this, HistorialGastosActivity::class.java))
        }
        findViewById<View>(R.id.quick_historial).setOnClickListener {
            startActivity(Intent(this, HistorialGastosActivity::class.java))
        }
    }


}
