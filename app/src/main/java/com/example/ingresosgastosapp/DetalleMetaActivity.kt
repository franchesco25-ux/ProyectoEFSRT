package com.example.ingresosgastosapp

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ingresosgastosapp.Adapter.TransaccionReciente
import com.example.ingresosgastosapp.Adapter.TransaccionRecienteAdapter
import com.example.ingresosgastosapp.Data.GastosViewModel
import com.example.ingresosgastosapp.Data.MetaAhorro
import com.example.ingresosgastosapp.DataBase.AppDatabase
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale

class DetalleMetaActivity : BaseActivity() {

    private var metaId: Int = -1
    private var metaActual: MetaAhorro? = null
    private val gastosViewModel: GastosViewModel by viewModels()
    private val transaccionesAdapter = TransaccionRecienteAdapter()

    private lateinit var tvMetaName: TextView
    private lateinit var tvMetaCurrentAmount: TextView
    private lateinit var tvMetaTargetAmount: TextView
    private lateinit var tvMetaPercentage: TextView
    private lateinit var pbMetaProgressBg: ProgressBar
    private lateinit var pbMetaProgress: ProgressBar
    private lateinit var ivMetaIcon: ImageView
    private lateinit var tvMetaRemaining: TextView
    private lateinit var tvMetaDeadline: TextView
    private lateinit var btnAddContribution: MaterialButton
    private lateinit var rvContributions: RecyclerView
    private lateinit var tvCountContributions: TextView
    private lateinit var tvNoContributions: TextView
    private lateinit var btnViewFullHistory: TextView

    private var currencySymbol = "$"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalle_meta)

        metaId = intent.getIntExtra("META_ID", -1)
        if (metaId == -1) {
            Toast.makeText(this, "Error: No se encontró la meta", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val monedaPref = prefs.getString("MONEDA_PRINCIPAL", "USD ($)")
        currencySymbol = if (monedaPref != null && monedaPref.contains("(")) {
            monedaPref.substringAfter("(").replace(")", "")
        } else "$"

        // Setup Toolbar
        findViewById<ImageButton>(R.id.btnBackDetalleMeta).setOnClickListener { finish() }
        findViewById<ImageButton>(R.id.btnEditMeta).setOnClickListener { 
            abrirEdicion() 
        }

        // Bind views
        tvMetaName = findViewById(R.id.tvMetaNameDetalle)
        tvMetaCurrentAmount = findViewById(R.id.tvMetaCurrentAmountDetalle)
        tvMetaTargetAmount = findViewById(R.id.tvMetaTargetAmountDetalle)
        tvMetaPercentage = findViewById(R.id.tvMetaPercentageDetalle)
        pbMetaProgressBg = findViewById(R.id.pbMetaProgressBg)
        pbMetaProgress = findViewById(R.id.pbMetaProgress)
        ivMetaIcon = findViewById(R.id.ivMetaIconDetalle)
        tvMetaRemaining = findViewById(R.id.tvMetaRemainingDetalle)
        tvMetaDeadline = findViewById(R.id.tvMetaDeadlineDetalle)
        btnAddContribution = findViewById(R.id.btnAddContributionDetalle)
        rvContributions = findViewById(R.id.rvContributionsDetalle)
        tvCountContributions = findViewById(R.id.tvCountContributionsDetalle)
        tvNoContributions = findViewById(R.id.tvNoContributionsDetalle)
        btnViewFullHistory = findViewById(R.id.btnViewFullHistoryDetalle)

        rvContributions.layoutManager = LinearLayoutManager(this)
        rvContributions.adapter = transaccionesAdapter

        btnAddContribution.setOnClickListener {
            abrirEdicion()
        }

        btnViewFullHistory.setOnClickListener {
            val intent = Intent(this, HistorialGastosActivity::class.java)
            startActivity(intent)
        }

        setupCustomBottomNav("ahorros")
    }

    override fun onResume() {
        super.onResume()
        cargarDatosMeta()
    }

    private fun abrirEdicion() {
        val intent = Intent(this, EditarMetaActivity::class.java)
        intent.putExtra("META_ID", metaId)
        intent.putExtra("MODE", "EDIT")
        startActivity(intent)
    }

    private fun cargarDatosMeta() {
        val dao = AppDatabase.getDatabase(this).metaAhorroDao()
        lifecycleScope.launch {
            val meta = withContext(Dispatchers.IO) {
                dao.getMetaById(metaId)
            }

            if (meta != null) {
                metaActual = meta
                actualizarUI(meta)
                cargarContribuciones(meta.nombre)
            } else {
                Toast.makeText(this@DetalleMetaActivity, "No se pudo cargar la meta", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun actualizarUI(meta: MetaAhorro) {
        val nf = NumberFormat.getNumberInstance(Locale.US).apply {
            minimumFractionDigits = 2
            maximumFractionDigits = 2
        }

        tvMetaName.text = meta.nombre
        tvMetaCurrentAmount.text = "$currencySymbol${nf.format(meta.montoActual)}"
        tvMetaTargetAmount.text = "de $currencySymbol${nf.format(meta.montoObjetivo)}"

        val porcentaje = if (meta.montoObjetivo > 0) {
            ((meta.montoActual / meta.montoObjetivo) * 100).toInt().coerceAtMost(100)
        } else 0

        tvMetaPercentage.text = "${porcentaje}%"
        pbMetaProgress.progress = porcentaje

        val restante = if (meta.montoObjetivo > meta.montoActual) meta.montoObjetivo - meta.montoActual else 0.0
        tvMetaRemaining.text = "$currencySymbol${nf.format(restante)}"

        if (meta.fechaLimite.isNotEmpty()) {
            tvMetaDeadline.text = formatearFechaCorta(meta.fechaLimite)
        } else {
            tvMetaDeadline.text = "Sin fecha"
        }

        // Aplicar color
        try {
            val color = Color.parseColor(meta.color)
            pbMetaProgress.progressTintList = ColorStateList.valueOf(color)
            tvMetaCurrentAmount.setTextColor(color)
            ivMetaIcon.imageTintList = ColorStateList.valueOf(color)
            btnAddContribution.backgroundTintList = ColorStateList.valueOf(color)
            
            // Fondo de la barra en opacidad del 20%
            pbMetaProgressBg.progressTintList = ColorStateList.valueOf(Color.argb(51, Color.red(color), Color.green(color), Color.blue(color)))
        } catch (_: Exception) {}

        // Aplicar Icono
        try {
            val resId = resources.getIdentifier(meta.icono, "drawable", packageName)
            if (resId != 0) {
                ivMetaIcon.setImageResource(resId)
            }
        } catch (_: Exception) {}
    }

    private fun cargarContribuciones(metaNombre: String) {
        gastosViewModel.readAllData.observe(this) { gastos ->
            val contribuciones = gastos.filter { it.categoria.equals(metaNombre, ignoreCase = true) }
            
            val transacciones = contribuciones.map { g ->
                TransaccionReciente(
                    id = g.id,
                    descripcion = g.descripcion,
                    monto = g.monto,
                    categoria = g.categoria,
                    fecha = g.fecha,
                    esIngreso = true // Mostrado como verde porque es dinero sumado a la meta
                )
            }.sortedByDescending { it.fecha }

            tvCountContributions.text = "${transacciones.size} depósitos"
            
            transaccionesAdapter.submitList(transacciones.take(5))
            
            if (transacciones.isEmpty()) {
                tvNoContributions.visibility = View.VISIBLE
                rvContributions.visibility = View.GONE
            } else {
                tvNoContributions.visibility = View.GONE
                rvContributions.visibility = View.VISIBLE
            }
        }
    }

    private fun formatearFechaCorta(fecha: String): String {
        return try {
            val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val outputFormat = SimpleDateFormat("MMM yyyy", Locale("es", "ES"))
            val date = inputFormat.parse(fecha)
            outputFormat.format(date!!).replaceFirstChar { it.uppercase() }
        } catch (e: Exception) {
            fecha
        }
    }
}
