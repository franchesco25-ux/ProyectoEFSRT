package com.example.ingresosgastosapp

import android.os.Bundle
import com.google.android.material.button.MaterialButton

class TerminosPrivacidadActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terminos)

        val btnAccept = findViewById<MaterialButton>(R.id.btn_accept_terms)
        btnAccept.setOnClickListener {
            // "al momento de aceptar te redirija a la misma vista del perfil"
            finish()
        }
    }
}

