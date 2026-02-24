package com.example.ingresosgastosapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ScrollView
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

        // Add padding to the scroll view when the keyboard is open so the user can scroll to the bottom
        val contentFrame = findViewById<android.view.ViewGroup>(android.R.id.content)
        androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(contentFrame) { view, insets ->
            val imeHeight = insets.getInsets(androidx.core.view.WindowInsetsCompat.Type.ime()).bottom
            val sysBarHeight = insets.getInsets(androidx.core.view.WindowInsetsCompat.Type.systemBars()).bottom
            view.setPadding(0, 0, 0, Math.max(imeHeight, sysBarHeight))
            insets
        }

        val btnRegistrar = findViewById<Button>(R.id.btnRegistrar)
        val imgAtras = findViewById<ImageButton>(R.id.imgAtras)
        val txtLogin = findViewById<TextView>(R.id.txtLogin)
        
        val edtNombre = findViewById<EditText>(R.id.edtNombre)
        val edtCorreoReg = findViewById<EditText>(R.id.edtCorreoReg)
        val edtPais = findViewById<EditText>(R.id.edtPais)
        val edtClaveReg = findViewById<EditText>(R.id.edtClaveReg)


        val db = AppDatabase.getDatabase(this)

        // TextInputLayout handles password toggle animation automatically

        btnRegistrar.setOnClickListener {
             val nombre = edtNombre.text.toString().trim()
             val pais = edtPais.text.toString().trim()
             val correo = edtCorreoReg.text.toString().trim()
             val clave = edtClaveReg.text.toString().trim()

             if (nombre.isNotEmpty() && pais.isNotEmpty() && correo.isNotEmpty() && clave.isNotEmpty()) {
                 lifecycleScope.launch {
                     val usuarioExistente = db.userDao().getUserByEmail(correo)
                     if (usuarioExistente != null) {
                         Toast.makeText(this@RegisterActivity, "El correo ya está registrado", Toast.LENGTH_SHORT).show()
                     } else {
                         val moneda = when (pais.lowercase()) {
                             "peru", "perú" -> "PEN (S/)"
                             "mexico", "méxico" -> "MXN ($)"
                             "colombia" -> "COP ($)"
                             "chile" -> "CLP ($)"
                             "argentina" -> "ARS ($)"
                             "españa", "espana" -> "EUR (€)"
                             "estados unidos", "usa", "eeuu", "us" -> "USD ($)"
                             else -> "USD ($)"
                         }
                         val nuevoUsuario = User(nombre = nombre, email = correo, password = clave, pais = pais, moneda = moneda)
                         db.userDao().insertUser(nuevoUsuario)

                         // Resetear datos financieros para el nuevo usuario
                         db.ingresosDao().deleteAllIngresos()
                         db.gastosDao().deleteAllGastos()
                         db.balanceDao().insertBalance(com.example.ingresosgastosapp.Data.Balance(id = 1, total = 0.0))

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
