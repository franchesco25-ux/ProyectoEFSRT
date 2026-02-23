package com.example.ingresosgastosapp

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ingresosgastosapp.Adapter.TransaccionReciente
import com.example.ingresosgastosapp.Adapter.TransaccionRecienteAdapter
import com.example.ingresosgastosapp.Data.BalanceViewModel
import com.example.ingresosgastosapp.Data.Gastos
import com.example.ingresosgastosapp.Data.GastosViewModel
import com.example.ingresosgastosapp.Data.Ingresos
import com.example.ingresosgastosapp.Data.IngresosViewModel
import com.google.android.material.imageview.ShapeableImageView
import java.io.File


class MainMenuActivity : BaseActivity() {

    private lateinit var tvBalance: TextView

    private var nombreUsuario: String = "Usuario"
    private var emailUsuario: String = ""

    private val balanceViewModel: BalanceViewModel by viewModels()
    private val gastosViewModel: GastosViewModel by viewModels()
    private val ingresosViewModel: IngresosViewModel by viewModels()

    // Transacciones recientes
    private lateinit var rvTransacciones: RecyclerView
    private lateinit var tvSinTransacciones: TextView
    private val transaccionesAdapter = TransaccionRecienteAdapter()
    private var listaGastos = listOf<Gastos>()
    private var listaIngresos = listOf<Ingresos>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)

        // 1. Inicialización de vistas
        tvBalance = findViewById(R.id.tvMainBalance)
        val tvSaludo = findViewById<TextView>(R.id.tvSaludoDashboard)
        val imgProfile = findViewById<ShapeableImageView>(R.id.imgProfile)
        val btnAgregar = findViewById<View>(R.id.btnMainAgregar)

        // Cargar foto de perfil guardada
        val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val photoPath = prefs.getString("PROFILE_PHOTO_PATH", null)
        if (photoPath != null) {
            val file = File(photoPath)
            if (file.exists()) {
                val bitmap = BitmapFactory.decodeFile(photoPath)
                if (bitmap != null) {
                    imgProfile.setImageBitmap(bitmap)
                    imgProfile.imageTintList = null
                    imgProfile.setPadding(0, 0, 0, 0)
                    imgProfile.setContentPadding(0, 0, 0, 0)
                }
            }
        }

        // Bottom Nav personalizado
        setupCustomBottomNav("inicio")

        // Botones de acceso rápido
        val btnMetas = findViewById<View>(R.id.btn_quick_metas)
        val btnPresupuesto = findViewById<View>(R.id.btn_quick_presupuesto)
        val btnAnalisis = findViewById<View>(R.id.btn_quick_analisis)
        val btnHistorial = findViewById<View>(R.id.btn_quick_historial)

        // 2. Configuración de datos del usuario (Desde SharedPreferences)
        nombreUsuario = prefs.getString("NOMBRE_USUARIO", "Usuario") ?: "Usuario"
        emailUsuario = prefs.getString("EMAIL_USUARIO", "") ?: ""
        tvSaludo.text = "Hola, $nombreUsuario"

        // 3. Configuración del Balance Real
        val monedaPref = prefs.getString("MONEDA_PRINCIPAL", "USD ($)")
        val currencySymbol = if (monedaPref != null && monedaPref.contains("(")) monedaPref.substringAfter("(").replace(")", "") else "$"

        balanceViewModel.balance.observe(this) { balanceEntity ->
            val totalReal = balanceEntity?.total ?: 0.0
            tvBalance.text = "$currencySymbol %.2f".format(totalReal)
            if (totalReal < 0) {
                tvBalance.setTextColor(android.graphics.Color.parseColor("#EF4444"))
            } else {
                tvBalance.setTextColor(android.graphics.Color.WHITE)
            }
        }

        // 4. Configuración de clics
        imgProfile.setOnClickListener { abrirPerfil() }
        btnAgregar.setOnClickListener {
            val bottomSheet = QuickActionsBottomSheet()
            bottomSheet.show(supportFragmentManager, "QuickActionsBottomSheet")
        }

        btnHistorial.setOnClickListener { startActivity(Intent(this, HistorialActivity::class.java)) }

        // REDIRECCIONES DE ACCESO RÁPIDO
        btnMetas.setOnClickListener { startActivity(Intent(this, AhorrosActivity::class.java)) }
        btnPresupuesto.setOnClickListener { startActivity(Intent(this, ResumenPresupuestoActivity::class.java)) }
        btnAnalisis.setOnClickListener { startActivity(Intent(this, AnalisisActivity::class.java)) }



        // 6. Últimas Transacciones
        rvTransacciones = findViewById(R.id.rvTransaccionesRecientes)
        tvSinTransacciones = findViewById(R.id.tvSinTransacciones)
        rvTransacciones.layoutManager = LinearLayoutManager(this)
        rvTransacciones.adapter = transaccionesAdapter

        val btnVerHistorial = findViewById<TextView>(R.id.btnVerHistorial)
        btnVerHistorial.setOnClickListener {
            startActivity(Intent(this, HistorialActivity::class.java))
        }

        gastosViewModel.readAllData.observe(this) { gastos ->
            listaGastos = gastos
            actualizarTransaccionesRecientes()
        }

        ingresosViewModel.readAllData.observe(this) { ingresos ->
            listaIngresos = ingresos
            actualizarTransaccionesRecientes()
        }
    }

    private fun actualizarTransaccionesRecientes() {
        val transacciones = mutableListOf<TransaccionReciente>()

        for (g in listaGastos) {
            transacciones.add(
                TransaccionReciente(
                    id = g.id,
                    descripcion = g.descripcion.ifBlank { g.categoria },
                    monto = g.monto,
                    categoria = g.categoria,
                    fecha = g.fecha,
                    esIngreso = false
                )
            )
        }

        for (i in listaIngresos) {
            transacciones.add(
                TransaccionReciente(
                    id = i.id,
                    descripcion = i.descripcion.ifBlank { i.categoria },
                    monto = i.monto,
                    categoria = i.categoria,
                    fecha = i.fecha,
                    esIngreso = true
                )
            )
        }

        // Orden por fecha descendente y tomar solo 5
        val ultimas5 = transacciones.sortedByDescending { it.fecha }.take(5)

        transaccionesAdapter.submitList(ultimas5)
        tvSinTransacciones.visibility = if (ultimas5.isEmpty()) View.VISIBLE else View.GONE
        rvTransacciones.visibility = if (ultimas5.isEmpty()) View.GONE else View.VISIBLE
    }

    private fun abrirPerfil() {
        val intent = Intent(this, PerfilActivity::class.java)
        intent.putExtra("NOMBRE_USUARIO", nombreUsuario)
        intent.putExtra("EMAIL_USUARIO", emailUsuario)
        startActivity(intent)
    }
}