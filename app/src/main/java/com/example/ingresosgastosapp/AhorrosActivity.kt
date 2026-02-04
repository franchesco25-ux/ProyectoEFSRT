package com.example.ingresosgastosapp

import android.content.Intent
import android.os.Bundle
import com.example.ingresosgastosapp.BaseActivity
import androidx.appcompat.widget.Toolbar

class AhorrosActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ahorros)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener { finish() }

        findViewById<android.view.View>(R.id.btn_ver_detalle).setOnClickListener {
            android.widget.Toast.makeText(this, "Detalle de meta próximamente", android.widget.Toast.LENGTH_SHORT).show()
        }
    }
}
