package com.example.ingresosgastosapp

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.ingresosgastosapp.Data.Gasto

class HistorialActivity : AppCompatActivity() {

    /*
    * Reemplazo por ViewBinding
    * */
    private lateinit var spinnerFiltroCategoria: Spinner
    private lateinit var tvHistorial: TextView

    /*
    * Conectar a la base de datos para hacer el servicio total
    * */
    // Lista simulada de gastos (como si vinieran de la base de datos)
    /*
    private val listaGastos = listOf(
        Gasto(100.0, "Supermercado", "Alimentos"),
        Gasto(40.0, "Bus", "Transporte"),
        Gasto(500.0, "Alquiler", "Vivienda"),
        Gasto(20.0, "Netflix", "Entretenimiento"),
        Gasto(70.0, "Farmacia", "Salud"),
        Gasto(300.0, "Universidad", "Educación"),
        Gasto(50.0, "Cena", "Alimentos")
    )*/

    // Categorías disponibles para filtrar
    private val categorias = arrayOf(
        "Todos",
        "Alimentos",
        "Transporte",
        "Vivienda",
        "Entretenimiento",
        "Salud",
        "Educación",
        "Otros"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historial)

        spinnerFiltroCategoria = findViewById(R.id.spinnerFiltroCategoria)
        tvHistorial = findViewById(R.id.tvHistorial)

        // Configurar el Spinner
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categorias)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerFiltroCategoria.adapter = adapter
    }
}

        // Mostrar historial completo al inicio
        //mostrarHistorial("Todos")

        // Listener para filtrar al cambiar selección
        /*spinnerFiltroCategoria.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: android.view.View?,
                    position: Int,
                    id: Long
                ) {
                    val categoriaSeleccionada = categorias[position]
                    mostrarHistorial(categoriaSeleccionada)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // No hacer nada
                }
            }
    }   */

    /*
    private fun mostrarHistorial(categoria: String) {
        val historialFiltrado = if (categoria == "Todos") {
            listaGastos
        } else {
            listaGastos.filter { it.categoria == categoria }
        }

        if (historialFiltrado.isEmpty()) {
            tvHistorial.text = "No hay movimientos en esta categoría."
        } else {
            val texto = historialFiltrado.joinToString("\n") { gasto ->
                "- $${gasto.monto} | ${gasto.descripcion} | ${gasto.categoria}"
            }
            tvHistorial.text = texto
        }
    }*/

