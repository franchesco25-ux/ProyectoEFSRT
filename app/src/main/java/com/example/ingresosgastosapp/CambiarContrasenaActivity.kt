package com.example.ingresosgastosapp

import android.content.Context
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.ingresosgastosapp.DataBase.AppDatabase
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.launch

class CambiarContrasenaActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cambiar_contrasena)

        val btnClose = findViewById<ImageView>(R.id.btn_close_pass)
        val btnUpdate = findViewById<MaterialButton>(R.id.btn_update_pass)
        val etNew = findViewById<EditText>(R.id.et_new_password)
        val etConfirm = findViewById<EditText>(R.id.et_confirm_password)

        btnClose.setOnClickListener { finish() }

        btnUpdate.setOnClickListener {
            val pass1 = etNew.text.toString().trim()
            val pass2 = etConfirm.text.toString().trim()

            if (pass1.isEmpty() || pass2.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (pass1 != pass2) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (pass1.length < 6) {
                Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val prefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            val emailUsuario = prefs.getString("EMAIL_USUARIO", null)

            if (emailUsuario != null) {
                lifecycleScope.launch {
                    val db = AppDatabase.getDatabase(this@CambiarContrasenaActivity)
                    db.userDao().updatePassword(emailUsuario, pass1)
                    Toast.makeText(this@CambiarContrasenaActivity, "Contraseña actualizada con éxito", Toast.LENGTH_SHORT).show()
                    finish()
                }
            } else {
                Toast.makeText(this, "Sesión no válida o expirada", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
