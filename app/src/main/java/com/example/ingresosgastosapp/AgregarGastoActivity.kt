package com.example.ingresosgastosapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.ingresosgastosapp.Data.Gasto
import com.example.ingresosgastosapp.DataBase.GastosDatabaseHelper
import com.example.ingresosgastosapp.databinding.ActivityAgregarGastoBinding

class AgregarGastoActivity : AppCompatActivity() {

    /*  Variables
    private lateinit var etMonto: EditText
    private lateinit var etDescripcion: EditText
    private lateinit var spinnerCategoria: Spinner
    private lateinit var btnGuardar: Button
    */

    /*
        etMonto = findViewById(R.id.etMontoGasto)
        etDescripcion = findViewById(R.id.etDescripcionGasto)
        spinnerCategoria = findViewById(R.id.spinnerCategoriaGasto)
        btnGuardar = findViewById(R.id.btnGuardarGasto)
        */

    private lateinit var binding : ActivityAgregarGastoBinding
    private lateinit var db : GastosDatabaseHelper

    private lateinit var categorias: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAgregarGastoBinding.inflate(layoutInflater)
        setContentView(binding.root)


        db = GastosDatabaseHelper(this)

        // "Categoría" como primera opción
        categorias = arrayOf(
            "Categoría",
            "Alimentos",
            "Transporte",
            "Vivienda",
            "Entretenimiento",
            "Salud",
            "Educación",
            "Otros"
        )

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            categorias
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCategoriaGasto.adapter = adapter

        binding.spinnerCategoriaGasto.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val selectedItem = parent?.getItemAtPosition(position).toString()
                Toast.makeText(this@AgregarGastoActivity, "Elemento seleccionado $selectedItem", Toast.LENGTH_SHORT).show()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }


        binding.btnGuardarGasto.setOnClickListener {

            //declaracion de variables
            val monto = binding.etMontoGasto.text.toString().toInt()
            val descripcion = binding.etDescripcionGasto.text.toString()
            val categoria = binding.spinnerCategoriaGasto.selectedItem.toString()

            //inicializacion de clases
            val gasto = Gasto(0, monto, descripcion, categoria)

            //insercion de la databaseHelper
            db.insertGastos(gasto)

            binding.etMontoGasto.text.clear()
            binding.etDescripcionGasto.text.clear()
            binding.spinnerCategoriaGasto.setSelection(0)
            finish()
            Toast.makeText(this, "Gasto agregado", Toast.LENGTH_SHORT).show()

            var intent = Intent(this, HistorialGastosActivity::class.java)
            startActivity(intent)
        }


    }
}
