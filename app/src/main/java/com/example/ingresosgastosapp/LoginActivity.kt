package com.example.ingresosgastosapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ScrollView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.ingresosgastosapp.DataBase.AppDatabase
import kotlinx.coroutines.launch

class LoginActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Scroll al campo enfocado cuando el teclado aparece
        val contentFrame = findViewById<android.view.ViewGroup>(android.R.id.content)
        val scrollView = contentFrame.getChildAt(0) as? ScrollView
        fun scrollToFocused(view: android.view.View) {
            scrollView?.postDelayed({
                scrollView.smoothScrollTo(0, view.bottom + 200)
            }, 300)
        }

        val btnIngresar = findViewById<Button>(R.id.btnIngresar)
        val btnCrearCuenta = findViewById<Button>(R.id.btnCrearCuenta)
        val edtCorreo = findViewById<EditText>(R.id.edtCorreo)
        val edtClave = findViewById<EditText>(R.id.edtClave)

        // TextInputLayout handles password toggle animation automatically

        // Scroll al campo enfocado cuando el teclado aparece
        edtCorreo.setOnFocusChangeListener { v, hasFocus -> if (hasFocus) scrollToFocused(v) }
        edtClave.setOnFocusChangeListener { v, hasFocus -> if (hasFocus) scrollToFocused(v) }

        val db = AppDatabase.getDatabase(this)

        btnIngresar.setOnClickListener {
            val correo = edtCorreo.text.toString().trim()
            val clave = edtClave.text.toString().trim()

            if (correo.isNotEmpty() && clave.isNotEmpty()) {
                lifecycleScope.launch {
                    val usuario = db.userDao().getUser(correo, clave)
                    if (usuario != null) {
                        // Guardar en SharedPreferences
                        val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
                        with(prefs.edit()) {
                            putString("NOMBRE_USUARIO", usuario.nombre)
                            putString("EMAIL_USUARIO", usuario.email)
                            putString("MONEDA_PRINCIPAL", usuario.moneda)
                            putString("PAIS_USUARIO", usuario.pais)
                            putString("LAST_LOGGED_IN_EMAIL", usuario.email)
                            apply()
                        }

                        Toast.makeText(this@LoginActivity, "Bienvenido ${usuario.nombre}", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@LoginActivity, MainMenuActivity::class.java)
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

        // BIOMETRIC LOGIC
        val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val touchIdEnabled = prefs.getBoolean("TOUCH_ID_ENABLED", false)
        val lastEmail = prefs.getString("LAST_LOGGED_IN_EMAIL", null)

        if (touchIdEnabled && lastEmail != null) {
            showBiometricPrompt(lastEmail, db)
        }
    }

    private fun showBiometricPrompt(email: String, db: AppDatabase) {
        val executor = ContextCompat.getMainExecutor(this)
        val biometricPrompt = BiometricPrompt(this, executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    Toast.makeText(this@LoginActivity, "Error de Touch ID: $errString", Toast.LENGTH_SHORT).show()
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    loginWithBiometrics(email, db)
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    Toast.makeText(this@LoginActivity, "Huella no reconocida", Toast.LENGTH_SHORT).show()
                }
            })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Iniciar sesión")
            .setSubtitle("Usa tu huella para acceder a tu cuenta")
            .setNegativeButtonText("Usar Contraseña")
            .build()

        biometricPrompt.authenticate(promptInfo)
    }

    private fun loginWithBiometrics(email: String, db: AppDatabase) {
        lifecycleScope.launch {
            val usuario = db.userDao().getUserByEmail(email)
            if (usuario != null) {
                // Guardar/Actualizar en SharedPreferences
                val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
                with(prefs.edit()) {
                    putString("NOMBRE_USUARIO", usuario.nombre)
                    putString("EMAIL_USUARIO", usuario.email)
                    putString("MONEDA_PRINCIPAL", usuario.moneda)
                    putString("PAIS_USUARIO", usuario.pais)
                    apply()
                }
                Toast.makeText(this@LoginActivity, "Bienvenido ${usuario.nombre}", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@LoginActivity, MainMenuActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this@LoginActivity, "Usuario no encontrado", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
