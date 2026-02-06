package com.example.ingresosgastosapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.ingresosgastosapp.Data.User
import com.example.ingresosgastosapp.DataBase.AppDatabase
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val btnRegistrar = findViewById<Button>(R.id.btnRegistrar)
        val imgAtras = findViewById<ImageView>(R.id.imgAtras)
        val txtLogin = findViewById<TextView>(R.id.txtLogin)
        
        val edtNombre = findViewById<EditText>(R.id.edtNombre)
        val edtCorreoReg = findViewById<EditText>(R.id.edtCorreoReg)
        val edtClaveReg = findViewById<EditText>(R.id.edtClaveReg)

        val db = AppDatabase.getDatabase(this)

        btnRegistrar.setOnClickListener {
             val nombre = edtNombre.text.toString().trim()
             val correo = edtCorreoReg.text.toString().trim()
             val clave = edtClaveReg.text.toString().trim()

             if (nombre.isNotEmpty() && correo.isNotEmpty() && clave.isNotEmpty()) {
                 lifecycleScope.launch {
                     val usuarioExistente = db.userDao().getUserByEmail(correo)
                     if (usuarioExistente != null) {
                         Toast.makeText(this@RegisterActivity, "El correo ya está registrado", Toast.LENGTH_SHORT).show()
                     } else {
                         val nuevoUsuario = User(nombre = nombre, email = correo, password = clave)
                         db.userDao().insertUser(nuevoUsuario)
                         Toast.makeText(this@RegisterActivity, "Cuenta creada exitosamente!", Toast.LENGTH_SHORT).show()
                         
                         val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                         intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK 
                         startActivity(intent)
                     }
                 }
             } else {
                 Toast.makeText(this, "Por favor completa todos los campos", Toast.LENGTH_SHORT).show()
             }
        }

        imgAtras.setOnClickListener {
            finish()
        }
        
        txtLogin.setOnClickListener {
            finish()
        }
    }
}
