package com.example.ingresosgastosapp

import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.firebase.auth.FirebaseAuth
import java.io.File

class PerfilActivity : BaseActivity() {

    private lateinit var tvNombreUsuario: TextView
    private lateinit var tvEmailUsuario: TextView
    private lateinit var btnLogout: View
    private lateinit var imgPerfil: ShapeableImageView

    // Launcher para seleccionar imagen de la galería
    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedUri ->
            // Copiar la imagen al almacenamiento interno para persistirla
            val file = File(filesDir, "profile_photo.jpg")
            try {
                contentResolver.openInputStream(selectedUri)?.use { input ->
                    file.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                loadProfilePhoto(file.absolutePath)
                // Guardar la ruta en SharedPreferences
                getSharedPreferences("user_prefs", MODE_PRIVATE)
                    .edit()
                    .putString("PROFILE_PHOTO_PATH", file.absolutePath)
                    .apply()
                Toast.makeText(this, "Foto de perfil actualizada", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this, "Error al guardar la foto", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener {
            startActivity(Intent(this, MainMenuActivity::class.java))
            finish()
        }

        // 1. Inicializar vistas principales
        tvNombreUsuario = findViewById(R.id.tvNombreUsuario)
        tvEmailUsuario = findViewById(R.id.tvEmailUsuario)
        btnLogout = findViewById(R.id.btnLogout)
        imgPerfil = findViewById(R.id.imgPerfil)

        // Botón para cambiar foto de perfil
        val btnCambiarFoto = findViewById<ImageButton>(R.id.btnCambiarFoto)
        btnCambiarFoto.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }

        // Cargar foto de perfil guardada
        val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val photoPath = prefs.getString("PROFILE_PHOTO_PATH", null)
        if (photoPath != null) {
            loadProfilePhoto(photoPath)
        }

        // Bottom Nav personalizado
        setupCustomBottomNav("ajustes")

        // 5. Cargar datos del usuario
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

        
        btnLogout.setOnClickListener {
            val p = getSharedPreferences("user_prefs", MODE_PRIVATE)
            val touchIdEnabled = p.getBoolean("TOUCH_ID_ENABLED", false)
            val lastEmail = p.getString("LAST_LOGGED_IN_EMAIL", null)
            val lang = p.getString("IDIOMA_APP", "Español")
            val savedPhotoPath = p.getString("PROFILE_PHOTO_PATH", null)

            p.edit().clear()
                .putBoolean("TOUCH_ID_ENABLED", touchIdEnabled)
                .putString("LAST_LOGGED_IN_EMAIL", lastEmail)
                .putString("IDIOMA_APP", lang)
                .putString("PROFILE_PHOTO_PATH", savedPhotoPath)
                .apply()
                
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

    }

    private fun loadProfilePhoto(path: String) {
        val file = File(path)
        if (file.exists()) {
            val bitmap = BitmapFactory.decodeFile(path)
            if (bitmap != null) {
                imgPerfil.setImageBitmap(bitmap)
                imgPerfil.imageTintList = null
                imgPerfil.setPadding(0, 0, 0, 0)
                imgPerfil.contentPadding = 0
            }
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