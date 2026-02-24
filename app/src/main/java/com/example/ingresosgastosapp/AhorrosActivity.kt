package com.example.ingresosgastosapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ingresosgastosapp.Adapter.MetaAhorroAdapter
import com.example.ingresosgastosapp.DataBase.AppDatabase
import java.text.NumberFormat
import java.util.Locale

class AhorrosActivity : BaseActivity() {

    private lateinit var adapter: MetaAhorroAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ahorros)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener {
            startActivity(Intent(this, MainMenuActivity::class.java))
            finish()
        }

        val btnCrearMeta = findViewById<TextView>(R.id.btn_crear_meta)
        val btnCrearMetaEmpty = findViewById<TextView>(R.id.btn_crear_meta_empty)
        val rvMetas = findViewById<RecyclerView>(R.id.rv_metas)
        val emptyState = findViewById<View>(R.id.empty_state)
        val tvTotalAhorrado = findViewById<TextView>(R.id.tv_total_ahorrado)
        val tvMetasCount = findViewById<TextView>(R.id.tv_metas_count)

        // Bottom Nav
        setupCustomBottomNav("ahorros")

        // Adapter
        adapter = MetaAhorroAdapter { meta ->
            val intent = Intent(this, DetalleMetaActivity::class.java)
            intent.putExtra("META_ID", meta.id)
            startActivity(intent)
        }
        rvMetas.layoutManager = LinearLayoutManager(this)
        rvMetas.adapter = adapter
        rvMetas.isNestedScrollingEnabled = false

        // DB
        val db = AppDatabase.getDatabase(this)
        val dao = db.metaAhorroDao()

        // Observe
        dao.getAllMetas().observe(this) { metas ->
            adapter.submitList(metas)

            // Update summary
            val nf = NumberFormat.getNumberInstance(Locale.US).apply {
                minimumFractionDigits = 2
                maximumFractionDigits = 2
            }
            val total = metas.sumOf { it.montoActual }
            tvTotalAhorrado.text = "S/.${nf.format(total)}"

            val activas = metas.count { !it.completada }
            tvMetasCount.text = "$activas meta${if (activas != 1) "s" else ""} activa${if (activas != 1) "s" else ""}"

            // Empty state
            emptyState.visibility = if (metas.isEmpty()) View.VISIBLE else View.GONE
            rvMetas.visibility = if (metas.isEmpty()) View.GONE else View.VISIBLE
        }

        // Crear Meta
        val crearMeta = View.OnClickListener {
            val intent = Intent(this, EditarMetaActivity::class.java)
            intent.putExtra("MODE", "CREATE")
            startActivity(intent)
        }
        btnCrearMeta.setOnClickListener(crearMeta)
        btnCrearMetaEmpty.setOnClickListener(crearMeta)
    }

    override fun onResume() {
        super.onResume()
        // RecyclerView auto-updates via LiveData, no manual refresh needed
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        val intent = Intent(this, MainMenuActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        startActivity(intent)
        finish()
    }

}

