package com.example.ingresosgastosapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.firebase.auth.FirebaseAuth

class PerfilActivity : BaseActivity() {

    private lateinit var tvNombreUsuario: TextView
    private lateinit var tvEmailUsuario: TextView
    private lateinit var btnBack: ImageView
    private lateinit var btnLogout: View
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var fabAdd: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        // 1. Inicializar vistas principales
        tvNombreUsuario = findViewById(R.id.tvNombreUsuario)
        tvEmailUsuario = findViewById(R.id.tvEmailUsuario)
        btnBack = findViewById(R.id.btnBackPerfil)
        btnLogout = findViewById(R.id.btnLogout)
        bottomNav = findViewById(R.id.bottom_navigation)
        fabAdd = findViewById(R.id.fab_add)

        // 5. Cargar datos del usuario
        val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val monedaPref = prefs.getString("MONEDA_PRINCIPAL", "USD ($)")
        val touchIdPref = prefs.getBoolean("TOUCH_ID_ENABLED", false)
        val notifPref = prefs.getBoolean("NOTIFICACIONES_PUSH", true)
        val langPref = prefs.getString("IDIOMA_APP", "Español")

        // 2. Configurar Secciones (CUENTA Y SEGURIDAD)
        setupOption(R.id.rowChangePass, R.drawable.ic_settings, "Cambiar Contraseña") {
            startActivity(Intent(this, CambiarContrasenaActivity::class.java))
        }
        setupSwitch(R.id.rowFaceId, R.drawable.ic_person, "Touch ID", touchIdPref) { isChecked ->
            prefs.edit().putBoolean("TOUCH_ID_ENABLED", isChecked).apply()
            Toast.makeText(this, if (isChecked) "Touch ID Activado" else "Touch ID Desactivado", Toast.LENGTH_SHORT).show()
        }

        // 3. Configurar Secciones (PREFERENCIAS)
        setupValue(R.id.rowCurrency, R.drawable.ic_attach_money, "Moneda Principal", monedaPref!!)
        setupSwitch(R.id.rowNotifications, R.drawable.ic_notifications, "Notificaciones Push", notifPref) { isChecked ->
            prefs.edit().putBoolean("NOTIFICACIONES_PUSH", isChecked).apply()
            Toast.makeText(this, if (isChecked) "Notificaciones Activadas" else "Notificaciones Desactivadas", Toast.LENGTH_SHORT).show()
        }
        setupValue(R.id.rowLanguage, R.drawable.ic_history, "Idioma", langPref!!) {
            showLanguageDialog()
        }

        // 4. Configurar Secciones (AYUDA Y SOPORTE)
        setupOption(R.id.rowPrivacy, R.drawable.ic_history, "Términos y Privacidad") {
            startActivity(Intent(this, TerminosPrivacidadActivity::class.java))
        }

        tvNombreUsuario.text = prefs.getString("NOMBRE_USUARIO", "Usuario")
        tvEmailUsuario.text = prefs.getString("EMAIL_USUARIO", "usuario@correo.com")

        // 6. Configurar clics
        btnBack.setOnClickListener { finish() }
        
        btnLogout.setOnClickListener {
            val p = getSharedPreferences("user_prefs", MODE_PRIVATE)
            val touchIdEnabled = p.getBoolean("TOUCH_ID_ENABLED", false)
            val lastEmail = p.getString("LAST_LOGGED_IN_EMAIL", null)
            val lang = p.getString("IDIOMA_APP", "Español")

            p.edit().clear()
                .putBoolean("TOUCH_ID_ENABLED", touchIdEnabled)
                .putString("LAST_LOGGED_IN_EMAIL", lastEmail)
                .putString("IDIOMA_APP", lang)
                .apply()
                
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        // Navegación
        bottomNav.selectedItemId = R.id.nav_perfil
        bottomNav.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.nav_inicio -> {
                    startActivity(Intent(this, MainMenuActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true 
                }
                R.id.nav_presupuesto -> { 
                    startActivity(Intent(this, ResumenPresupuestoActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true 
                }
                R.id.nav_ahorros -> { 
                    startActivity(Intent(this, AhorrosActivity::class.java))
                    overridePendingTransition(0, 0)
                    finish()
                    true 
                }
                R.id.nav_perfil -> true
                else -> false
            }
        }

        fabAdd.setOnClickListener {
            val bottomSheet = QuickActionsBottomSheet()
            bottomSheet.show(supportFragmentManager, "QuickActionsBottomSheet")
        }
    }

    private fun setupOption(viewId: Int, iconId: Int, text: String, onClick: () -> Unit = {}) {
        val view = findViewById<View>(viewId) ?: return
        view.findViewById<ImageView>(R.id.itemIcon)?.setImageResource(iconId)
        view.findViewById<TextView>(R.id.itemText)?.text = text
        view.setOnClickListener { onClick() }
    }

    private fun setupSwitch(viewId: Int, iconId: Int, text: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit = {}) {
        val view = findViewById<View>(viewId) ?: return
        view.findViewById<ImageView>(R.id.itemIcon)?.setImageResource(iconId)
        view.findViewById<TextView>(R.id.itemText)?.text = text
        val sw = view.findViewById<SwitchMaterial>(R.id.itemSwitch)
        sw?.isChecked = checked
        sw?.setOnCheckedChangeListener { _, isChecked -> onCheckedChange(isChecked) }
    }

    private fun setupValue(viewId: Int, iconId: Int, text: String, value: String, onClick: () -> Unit = {}) {
        val view = findViewById<View>(viewId) ?: return
        view.findViewById<ImageView>(R.id.itemIcon)?.setImageResource(iconId)
        view.findViewById<TextView>(R.id.itemText)?.text = text
        view.findViewById<TextView>(R.id.itemValue)?.text = value
        view.setOnClickListener { 
            if (onClick != {}) {
                onClick()
            } else {
                Toast.makeText(this, "$text: $value", Toast.LENGTH_SHORT).show() 
            }
        }
    }

    private fun showLanguageDialog() {
        val languages = arrayOf("Español", "English", "Português", "Français")
        android.app.AlertDialog.Builder(this)
            .setTitle("Seleccionar Idioma")
            .setItems(languages) { _, which ->
                val selectedLang = languages[which]
                getSharedPreferences("user_prefs", MODE_PRIVATE).edit().putString("IDIOMA_APP", selectedLang).apply()
                setupValue(R.id.rowLanguage, R.drawable.ic_history, "Idioma", selectedLang) {}
                Toast.makeText(this, "Idioma cambiado a $selectedLang", Toast.LENGTH_SHORT).show()
                // In a real app we would recreate() the activity to change the locale context
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun setupStatus(viewId: Int, iconId: Int, text: String, status: String) {
        val view = findViewById<View>(viewId) ?: return
        view.findViewById<ImageView>(R.id.itemIcon)?.setImageResource(iconId)
        view.findViewById<TextView>(R.id.itemText)?.text = text
        view.findViewById<TextView>(R.id.itemStatus)?.text = status
    }
}