package com.example.ingresosgastosapp

import android.os.Bundle
import android.widget.ImageView
import com.google.android.material.button.MaterialButton

class EditarMetaActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_meta)

        // Inicializar vistas
        val btnClose = findViewById<ImageView>(R.id.btn_close_edit_meta)
        val btnSave = findViewById<MaterialButton>(R.id.btn_save_meta_changes)

        // Configurar clics
        btnClose.setOnClickListener {
            finish() // Cerrar la actividad y volver atrás
        }

        btnSave.setOnClickListener {
            // Aquí irá la lógica para guardar en Room más adelante
            finish()
        }
    }
}
