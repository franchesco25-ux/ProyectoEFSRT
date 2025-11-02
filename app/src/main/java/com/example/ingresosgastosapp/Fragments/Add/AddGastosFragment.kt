package com.example.ingresosgastosapp.Fragments.Add

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.ingresosgastosapp.Data.BalanceViewModel
import com.example.ingresosgastosapp.Data.Gastos
import com.example.ingresosgastosapp.Data.GastosViewModel
import com.example.ingresosgastosapp.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime

// Archivo: addGastos.kt (Para Gastos)
class addGastos : Fragment() {

    private lateinit var mGastosViewModel: GastosViewModel
    private val balanceViewModel: BalanceViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Asegúrate de usar el layout correcto para gastos
        val view = inflater.inflate(R.layout.fragment_add_gastos, container, false)

        // Inicialización de ViewModel (Asegúrate de tener un GastosViewModel)
        mGastosViewModel = ViewModelProvider(this).get(GastosViewModel::class.java)

        // 1. Identificación de Vistas
        val descripcionEt = view.findViewById<EditText>(R.id.addDescripcionGasto_et)
        val montoEt = view.findViewById<EditText>(R.id.addMontoGasto_et)
        val categoriaEt = view.findViewById<EditText>(R.id.addCategoriaGasto_et)
        val button = view.findViewById<Button>(R.id.addGasto_btn)

        button.setOnClickListener {
            val descripcion = descripcionEt.text.toString()
            val montoStr = montoEt.text.toString()
            val categoria = categoriaEt.text.toString()

            if (!TextUtils.isEmpty(descripcion) && !TextUtils.isEmpty(montoStr) && !TextUtils.isEmpty(categoria)) {

                // Conversión segura del monto
                val monto = montoStr.toDoubleOrNull()

                if (monto != null && monto > 0) {
                    val fecha = LocalDateTime.now().toString()
                    val gasto = Gastos(0, descripcion, monto, categoria, fecha)

                    viewLifecycleOwner.lifecycleScope.launch {

                        // 2. OBTENER el balance actual forzando la lectura de la DB
                        val currentBalance = balanceViewModel.getCurrentBalance()

                        // 3. Calcular el nuevo balance: RESTA
                        val newBalance = currentBalance - monto

                        // 4. Validación de saldo
                        if (newBalance >= 0) {
                            // Guardar el gasto
                            mGastosViewModel.addGasto(gasto)

                            // Actualizar el balance
                            balanceViewModel.updateBalance(newBalance)

                            withContext(Dispatchers.Main) {
                                Toast.makeText(requireContext(), "Gasto agregado correctamente", Toast.LENGTH_SHORT).show()
                                // Asegúrate de que esta acción de navegación sea correcta
                                findNavController().navigate(R.id.action_addFragment_to_listFragment)
                            }
                        } else {
                            withContext(Dispatchers.Main) {
                                // Muestra el saldo actual para dar contexto
                                Toast.makeText(requireContext(), "No tienes suficiente balance (Actual: $currentBalance)", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } else {
                    Toast.makeText(requireContext(), "El monto debe ser un número positivo", Toast.LENGTH_SHORT).show()
                }

            } else {
                Toast.makeText(requireContext(), "Complete todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }
}