package com.example.ingresosgastosapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.ingresosgastosapp.DataBase.AppDatabase
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val btnIngresar = findViewById<Button>(R.id.btnIngresar)
        val btnCrearCuenta = findViewById<Button>(R.id.btnCrearCuenta)
        val edtCorreo = findViewById<EditText>(R.id.edtCorreo)
        val edtClave = findViewById<EditText>(R.id.edtClave)

        val db = AppDatabase.getDatabase(this)

        btnIngresar.setOnClickListener {
            val correo = edtCorreo.text.toString().trim()
            val clave = edtClave.text.toString().trim()

            if (correo.isNotEmpty() && clave.isNotEmpty()) {
                lifecycleScope.launch {
                    val usuario = db.userDao().getUser(correo, clave)
                    if (usuario != null) {
                        Toast.makeText(this@LoginActivity, "Bienvenido ${usuario.nombre}", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        intent.putExtra("NOMBRE_USUARIO", usuario.nombre)
                        startActivity(intent)
                        finish()
                    } else {
                        val usuarioExiste = db.userDao().getUserByEmail(correo)
                        if (usuarioExiste != null) {
                             Toast.makeText(this@LoginActivity, "Contraseña incorrecta", Toast.LENGTH_SHORT).show()
                        } else {
                             Toast.makeText(this@LoginActivity, "Usuario no encontrado", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
                Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        btnCrearCuenta.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}
