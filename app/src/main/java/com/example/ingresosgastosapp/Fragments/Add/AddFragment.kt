package com.example.ingresosgastosapp.Fragments.Add

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
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
import com.example.ingresosgastosapp.Data.Ingresos
import com.example.ingresosgastosapp.Data.IngresosViewModel
import com.example.ingresosgastosapp.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import kotlin.jvm.java

// Archivo: AddFragment.kt (Para Ingresos)
class AddFragment : Fragment() {

    private lateinit var mIngresosViewModel: IngresosViewModel
    private val balanceViewModel: BalanceViewModel by activityViewModels()

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add, container, false)
        mIngresosViewModel = ViewModelProvider(this).get(IngresosViewModel::class.java)

        val descripcionEt = view.findViewById<EditText>(R.id.addDescripcion_et)
        val montoEt = view.findViewById<EditText>(R.id.addMonto_et)
        val categoriaEt = view.findViewById<EditText>(R.id.addCategoria_et)
        val button = view.findViewById<View>(R.id.add_btn)

        button.setOnClickListener {
            val descripcion = descripcionEt.text.toString().trim()
            val montoStr = montoEt.text.toString().replace("S/", "").replace("$", "").replace(",", "").trim()
            val categoria = categoriaEt.text.toString().trim()

            if (!TextUtils.isEmpty(descripcion) && !TextUtils.isEmpty(montoStr) && !TextUtils.isEmpty(categoria)) {

                val monto = montoStr.toDoubleOrNull()

                if (monto != null && monto > 0) {
                    val fecha = LocalDateTime.now().toString()
                    val ingreso = Ingresos(0, descripcion, monto, categoria, fecha)

                    viewLifecycleOwner.lifecycleScope.launch {

                        val currentBalance = balanceViewModel.getCurrentBalance()

                        val newBalance = currentBalance + monto

                        mIngresosViewModel.addIngresos(ingreso)
                        balanceViewModel.updateBalance(newBalance)

                        withContext(Dispatchers.Main) {
                            Toast.makeText(requireContext(), "Ingreso agregado correctamente", Toast.LENGTH_SHORT).show()
                            findNavController().navigate(R.id.action_addFragment_to_listFragment)
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