package com.example.ingresosgastosapp.Fragments.Add

import android.graphics.Typeface
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
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
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

class addGastos : Fragment() {

    private lateinit var mGastosViewModel: GastosViewModel
    private val balanceViewModel: BalanceViewModel by activityViewModels()
    
    private var selectedDate: LocalDate = LocalDate.now()
    private lateinit var tvFechaHeader: TextView
    private lateinit var calendarGrid: GridLayout

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
        val btnVerLista = view.findViewById<View>(R.id.btnVerListaGastos)
        
        tvFechaHeader = view.findViewById(R.id.tvFechaGasto)
        calendarGrid = view.findViewById(R.id.calendar_grid)
        val btnPrev = view.findViewById<ImageView>(R.id.btn_prev_month)
        val btnNext = view.findViewById<ImageView>(R.id.btn_next_month)

        updateCalendar()

        btnPrev.setOnClickListener {
            selectedDate = selectedDate.minusMonths(1)
            updateCalendar()
        }

        btnNext.setOnClickListener {
            selectedDate = selectedDate.plusMonths(1)
            updateCalendar()
        }

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
                    val fechaGasto = selectedDate.atStartOfDay().toString()
                    val gasto = Gastos(0, descripcion.ifBlank { categoria }, monto, categoria, fechaGasto)

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
                                Toast.makeText(requireContext(), "No tienes suficiente balance", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Complete todos los campos", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }

    private fun updateCalendar() {
        val formatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale("es", "ES"))
        tvFechaHeader.text = selectedDate.format(formatter).replaceFirstChar { it.uppercase() }

        // Limpiar días anteriores pero manteniendo las cabeceras (D, L, M...)
        val childCount = calendarGrid.childCount
        if (childCount > 7) {
            calendarGrid.removeViews(7, childCount - 7)
        }

        val yearMonth = YearMonth.from(selectedDate)
        val firstDayOfMonth = yearMonth.atDay(1).dayOfWeek.value % 7
        val daysInMonth = yearMonth.lengthOfMonth()

        // Calcular altura de 48dp en pixeles
        val density = resources.displayMetrics.density
        val dayHeight = (48 * density).toInt()

        // Función para LayoutParams con peso para distribución uniforme
        fun getDayLayoutParams(): GridLayout.LayoutParams {
            val params = GridLayout.LayoutParams(
                GridLayout.spec(GridLayout.UNDEFINED, 1f), // rowWeight = 1
                GridLayout.spec(GridLayout.UNDEFINED, 1f)  // columnWeight = 1
            )
            params.width = 0
            params.height = dayHeight
            return params
        }

        // Celdas vacías al inicio del mes
        for (i in 0 until firstDayOfMonth) {
            val emptyView = TextView(requireContext())
            emptyView.layoutParams = getDayLayoutParams()
            calendarGrid.addView(emptyView)
        }

        // Celdas con los números de los días
        for (day in 1..daysInMonth) {
            val dayTextView = TextView(requireContext())
            dayTextView.text = day.toString()
            dayTextView.layoutParams = getDayLayoutParams()
            dayTextView.gravity = Gravity.CENTER
            dayTextView.textSize = 16f
            dayTextView.setTypeface(null, Typeface.BOLD)
            
            if (day == selectedDate.dayOfMonth) {
                dayTextView.setBackgroundResource(R.drawable.bg_stitch_fab)
                dayTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.stitch_background))
            } else {
                dayTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            }

            dayTextView.setOnClickListener {
                selectedDate = selectedDate.withDayOfMonth(day)
                updateCalendar()
            }

            calendarGrid.addView(dayTextView)
        }
    }
}
