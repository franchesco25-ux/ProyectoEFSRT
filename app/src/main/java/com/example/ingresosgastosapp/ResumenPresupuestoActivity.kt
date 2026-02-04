package com.example.ingresosgastosapp

import android.os.Bundle
import com.example.ingresosgastosapp.BaseActivity
import androidx.appcompat.widget.Toolbar

class ResumenPresupuestoActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_resumen_presupuesto)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener { finish() }

        findViewById<android.view.View>(R.id.btn_revisar_planes).setOnClickListener {
            android.widget.Toast.makeText(this, "Revisar planes próximamente", android.widget.Toast.LENGTH_SHORT).show()
        }
    }
}
