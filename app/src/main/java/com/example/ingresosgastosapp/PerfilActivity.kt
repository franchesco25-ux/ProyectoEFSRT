package com.example.ingresosgastosapp

import android.os.Bundle
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth

class PerfilActivity : BaseActivity() {

    private lateinit var tvNombreUsuario: TextView
    private lateinit var tvEmailUsuario: TextView

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        tvNombreUsuario = findViewById(R.id.tvNombreUsuario)
        tvEmailUsuario = findViewById(R.id.tvEmailUsuario)

        auth = FirebaseAuth.getInstance()

        val user = auth.currentUser
        if (user != null) {
            tvNombreUsuario.text = user.displayName ?: "Usuario"
            tvEmailUsuario.text = user.email
        } else {
            // Manejar el caso en que el usuario no está logueado
            tvNombreUsuario.text = "Usuario no disponible"
            tvEmailUsuario.text = ""
        }
    }
}