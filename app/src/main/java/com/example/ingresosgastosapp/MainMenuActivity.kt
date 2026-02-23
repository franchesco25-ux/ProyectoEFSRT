package com.example.ingresosgastosapp

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
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
import com.example.ingresosgastosapp.Data.MetaAhorro
import com.example.ingresosgastosapp.DataBase.AppDatabase
import com.google.android.material.imageview.ShapeableImageView
import java.io.File
import java.text.NumberFormat
import java.util.Locale


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

    // Metas de ahorro
    private lateinit var llMetasProgress: LinearLayout
    private lateinit var tvSinMetasMain: TextView

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

        // 5. Metas de Ahorro - Top 3 Progress
        llMetasProgress = findViewById(R.id.ll_metas_progress)
        tvSinMetasMain = findViewById(R.id.tv_sin_metas_main)

        val tvVerTodoAhorro = findViewById<TextView>(R.id.tv_ver_todo_ahorro)
        tvVerTodoAhorro.setOnClickListener {
            startActivity(Intent(this, AhorrosActivity::class.java))
        }

        val metaDao = AppDatabase.getDatabase(this).metaAhorroDao()
        metaDao.getTopMetas(3).observe(this) { metas ->
            cargarMetasEnDashboard(metas)
        }

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

    private fun cargarMetasEnDashboard(metas: List<MetaAhorro>) {
        llMetasProgress.removeAllViews()

        if (metas.isEmpty()) {
            tvSinMetasMain.visibility = View.VISIBLE
            llMetasProgress.visibility = View.GONE
            return
        }

        tvSinMetasMain.visibility = View.GONE
        llMetasProgress.visibility = View.VISIBLE

        val nf = NumberFormat.getNumberInstance(Locale.US).apply {
            minimumFractionDigits = 2
            maximumFractionDigits = 2
        }

        for (meta in metas) {
            val cardView = LayoutInflater.from(this).inflate(R.layout.item_meta_progress_mini, llMetasProgress, false)

            val ivIcon = cardView.findViewById<ImageView>(R.id.iv_mini_icon)
            val iconBg = cardView.findViewById<View>(R.id.mini_icon_bg)
            val tvNombre = cardView.findViewById<TextView>(R.id.tv_mini_nombre)
            val tvMonto = cardView.findViewById<TextView>(R.id.tv_mini_monto)
            val tvPorcentaje = cardView.findViewById<TextView>(R.id.tv_mini_porcentaje)
            val progressBar = cardView.findViewById<ProgressBar>(R.id.mini_progress_bar)

            tvNombre.text = meta.nombre
            tvMonto.text = "S/.${nf.format(meta.montoActual)} / S/.${nf.format(meta.montoObjetivo)}"

            val porcentaje = if (meta.montoObjetivo > 0)
                ((meta.montoActual / meta.montoObjetivo) * 100).toInt().coerceAtMost(100) else 0
            tvPorcentaje.text = "${porcentaje}%"
            progressBar.progress = porcentaje

            // Apply meta color
            try {
                val metaColor = Color.parseColor(meta.color)
                iconBg.backgroundTintList = ColorStateList.valueOf(Color.argb(26, Color.red(metaColor), Color.green(metaColor), Color.blue(metaColor)))
                ivIcon.imageTintList = ColorStateList.valueOf(metaColor)
                tvPorcentaje.setTextColor(metaColor)
            } catch (_: Exception) { }

            // Load custom icon
            try {
                val resId = resources.getIdentifier(meta.icono, "drawable", packageName)
                if (resId != 0) {
                    ivIcon.setImageResource(resId)
                }
            } catch (_: Exception) { }

            // Click to open AhorrosActivity
            cardView.setOnClickListener {
                val intent = Intent(this, EditarMetaActivity::class.java)
                intent.putExtra("META_ID", meta.id)
                startActivity(intent)
            }

            llMetasProgress.addView(cardView)
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