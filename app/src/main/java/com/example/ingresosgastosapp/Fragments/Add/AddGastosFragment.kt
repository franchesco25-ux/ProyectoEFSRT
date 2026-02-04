package com.example.ingresosgastosapp.Fragments.Add

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
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
import java.time.format.DateTimeFormatter

class addGastos : Fragment() {

    private lateinit var mGastosViewModel: GastosViewModel
    private val balanceViewModel: BalanceViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_gastos, container, false)

        mGastosViewModel = ViewModelProvider(this).get(GastosViewModel::class.java)

        val descripcionEt = view.findViewById<EditText>(R.id.addDescripcionGasto_et)
        val montoEt = view.findViewById<EditText>(R.id.addMontoGasto_et)
        val categoriaEt = view.findViewById<EditText>(R.id.addCategoriaGasto_et)
        val button = view.findViewById<View>(R.id.addGasto_btn)
        val tvFecha = view.findViewById<TextView>(R.id.tvFechaGasto)
        val btnVerLista = view.findViewById<View>(R.id.btnVerListaGastos)

        tvFecha.text = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        btnVerLista.setOnClickListener {
            findNavController().navigate(R.id.action_addFragment_to_listFragment)
        }

        button.setOnClickListener {
            val descripcion = descripcionEt.text.toString().trim()
            val montoStr = montoEt.text.toString().replace("S/", "").replace("$", "").replace(",", "").trim()
            val categoria = categoriaEt.text.toString().trim()

            if (!TextUtils.isEmpty(montoStr) && !TextUtils.isEmpty(categoria)) {

                val monto = montoStr.toDoubleOrNull()

                if (monto != null && monto > 0) {
                    val fecha = LocalDateTime.now().toString()
                    val gasto = Gastos(0, descripcion.ifBlank { categoria }, monto, categoria, fecha)

                    viewLifecycleOwner.lifecycleScope.launch {

                        val currentBalance = balanceViewModel.getCurrentBalance()

                        val newBalance = currentBalance - monto

                        if (newBalance >= 0) {
                            mGastosViewModel.addGasto(gasto)

                            balanceViewModel.updateBalance(newBalance)

                            withContext(Dispatchers.Main) {
                                Toast.makeText(requireContext(), "Gasto agregado correctamente", Toast.LENGTH_SHORT).show()
                                findNavController().navigate(R.id.action_addFragment_to_listFragment)
                            }
                        } else {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(requireContext(), "No tienes suficiente balance (Actual: S/ %.2f)".format(currentBalance), Toast.LENGTH_SHORT).show()
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