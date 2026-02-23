package com.example.ingresosgastosapp

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * Actividad base: Configuración pura de pantalla completa para el diseño Stitch.
 */
abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Activamos pantalla completa (Edge-to-Edge)
        enableEdgeToEdge()

        // 2. Aplicamos ajuste inteligente: 
        // Solo arriba (status bar) para no tapar la hora/batería.
        // Abajo dejamos 0 para que nuestro menú flote perfectamente.
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                0 // Sin espacio extra abajo para evitar el "doble menú"
            )
            insets
        }
    }

    /**
     * Configura la barra de navegación inferior personalizada.
     * @param activeItem nombre del ítem activo: "inicio", "presupuesto", "ahorros", "ajustes"
     */
    protected fun setupCustomBottomNav(activeItem: String) {
        val greenColor = Color.parseColor("#0df259")
        val mutedColor = Color.parseColor("#4A5A5A")

        // Mapeo de ítems: id_container, id_icon, id_text
        data class NavItem(val containerId: Int, val iconId: Int, val textId: Int, val key: String)

        val items = listOf(
            NavItem(R.id.nav_item_inicio, R.id.nav_icon_inicio, R.id.nav_text_inicio, "inicio"),
            NavItem(R.id.nav_item_presupuesto, R.id.nav_icon_presupuesto, R.id.nav_text_presupuesto, "presupuesto"),
            NavItem(R.id.nav_item_ahorros, R.id.nav_icon_ahorros, R.id.nav_text_ahorros, "ahorros"),
            NavItem(R.id.nav_item_ajustes, R.id.nav_icon_ajustes, R.id.nav_text_ajustes, "ajustes")
        )

        // Aplicar colores
        for (item in items) {
            val icon = findViewById<ImageView>(item.iconId)
            val text = findViewById<TextView>(item.textId)
            if (item.key == activeItem) {
                icon.setColorFilter(greenColor)
                text.setTextColor(greenColor)
            } else {
                icon.setColorFilter(mutedColor)
                text.setTextColor(mutedColor)
            }
        }

        // Click listeners de navegación
        findViewById<android.view.View>(R.id.nav_item_inicio).setOnClickListener {
            if (activeItem != "inicio") {
                startActivity(Intent(this, MainMenuActivity::class.java))
                finish()
            }
        }
        findViewById<android.view.View>(R.id.nav_item_presupuesto).setOnClickListener {
            if (activeItem != "presupuesto") {
                startActivity(Intent(this, ResumenPresupuestoActivity::class.java))
                finish()
            }
        }
        findViewById<android.view.View>(R.id.nav_item_ahorros).setOnClickListener {
            if (activeItem != "ahorros") {
                startActivity(Intent(this, AhorrosActivity::class.java))
                finish()
            }
        }
        findViewById<android.view.View>(R.id.nav_item_ajustes).setOnClickListener {
            if (activeItem != "ajustes") {
                startActivity(Intent(this, PerfilActivity::class.java))
                finish()
            }
        }

        // FAB
        findViewById<FloatingActionButton>(R.id.fab_add).setOnClickListener {
            val bottomSheet = QuickActionsBottomSheet()
            bottomSheet.show(supportFragmentManager, "QuickActionsBottomSheet")
        }
    }
}
