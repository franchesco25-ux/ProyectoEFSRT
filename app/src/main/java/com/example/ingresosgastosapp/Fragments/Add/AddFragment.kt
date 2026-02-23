package com.example.ingresosgastosapp.Fragments.Add

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
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
import com.example.ingresosgastosapp.Data.BalanceViewModel
import com.example.ingresosgastosapp.Data.Ingresos
import com.example.ingresosgastosapp.Data.IngresosViewModel
import com.example.ingresosgastosapp.HistorialActivity
import com.example.ingresosgastosapp.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

// Archivo: AddFragment.kt (Para Ingresos)
class AddFragment : Fragment() {

    private lateinit var mIngresosViewModel: IngresosViewModel
    private val balanceViewModel: BalanceViewModel by activityViewModels()

    private var selectedDate: LocalDate = LocalDate.now()
    private lateinit var tvFechaHeader: TextView
    private lateinit var calendarGrid: GridLayout

    // Lista de categorías de ingresos
    private val categorias = mutableListOf(
        "Salario", "Freelance", "Venta", "Inversión",
        "Regalo", "Reembolso", "Otros"
    )
    private lateinit var categoriaAdapter: ArrayAdapter<String>

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_ingreso, container, false)
        mIngresosViewModel = ViewModelProvider(this).get(IngresosViewModel::class.java)

        val descripcionEt = view.findViewById<EditText>(R.id.addDescripcion_et)
        val montoEt = view.findViewById<EditText>(R.id.addMonto_et)
        val categoriaEt = view.findViewById<AutoCompleteTextView>(R.id.addCategoria_et)
        val btnAddCategoria = view.findViewById<ImageView>(R.id.btnAddCategoriaIngreso)
        val button = view.findViewById<View>(R.id.add_btn)

        // Configurar calendario
        tvFechaHeader = view.findViewById(R.id.tvFechaIngreso)
        calendarGrid = view.findViewById(R.id.calendar_grid_ingreso)
        val btnPrev = view.findViewById<ImageView>(R.id.btn_prev_month_ingreso)
        val btnNext = view.findViewById<ImageView>(R.id.btn_next_month_ingreso)

        // Configurar combo de categorías
        setupCategoriaCombo(categoriaEt, btnAddCategoria)

        updateCalendar()

        btnPrev.setOnClickListener {
            selectedDate = selectedDate.minusMonths(1)
            updateCalendar()
        }

        btnNext.setOnClickListener {
            selectedDate = selectedDate.plusMonths(1)
            updateCalendar()
        }

        // Botón "Ver lista de ingresos" (opcional, puede no existir en nav)
        view.findViewById<View?>(R.id.btnVerListaIngresos)?.setOnClickListener {
            val intent = Intent(requireContext(), HistorialActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }

        button.setOnClickListener {
            val descripcion = descripcionEt.text.toString().trim()
            val montoStr = montoEt.text.toString().replace("S/", "").replace("$", "").replace(",", "").trim()
            val categoria = categoriaEt.text.toString().trim()

            if (!TextUtils.isEmpty(montoStr) && !TextUtils.isEmpty(categoria)) {

                val monto = montoStr.toDoubleOrNull()

                if (monto != null && monto > 0) {
                    val fecha = selectedDate.atTime(LocalTime.now()).toString()
                    val ingreso = Ingresos(0, descripcion.ifBlank { categoria }, monto, categoria, fecha)

                    viewLifecycleOwner.lifecycleScope.launch {

                        val currentBalance = balanceViewModel.getCurrentBalance()
                        val newBalance = currentBalance + monto

                        mIngresosViewModel.addIngresos(ingreso)
                        balanceViewModel.updateBalance(newBalance)

                        withContext(Dispatchers.Main) {
                            Toast.makeText(requireContext(), "Ingreso agregado correctamente", Toast.LENGTH_SHORT).show()

                            val intent = Intent(requireContext(), HistorialActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                            requireActivity().finish()
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

    private fun setupCategoriaCombo(categoriaEt: AutoCompleteTextView, btnAddCategoria: ImageView) {
        categoriaAdapter = ArrayAdapter(
            requireContext(),
            R.layout.item_dropdown_dark,
            categorias
        )
        categoriaEt.setAdapter(categoriaAdapter)

        // Al hacer click, mostrar todas las opciones
        categoriaEt.setOnClickListener {
            categoriaEt.showDropDown()
        }

        // Botón para agregar nueva categoría
        btnAddCategoria.setOnClickListener {
            mostrarDialogoNuevaCategoria(categoriaEt)
        }
    }

    private fun mostrarDialogoNuevaCategoria(categoriaEt: AutoCompleteTextView) {
        val input = EditText(requireContext()).apply {
            hint = "Nombre de la categoría"
            setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            setHintTextColor(ContextCompat.getColor(requireContext(), R.color.stitch_text_muted))
            setBackgroundResource(R.drawable.bg_stitch_input)
            setPadding(40, 30, 40, 30)
        }

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Nueva Categoría")
            .setMessage("Ingresa el nombre de la nueva categoría:")
            .setView(input)
            .setPositiveButton("Agregar") { _, _ ->
                val nuevaCategoria = input.text.toString().trim()
                if (nuevaCategoria.isNotEmpty()) {
                    if (!categorias.contains(nuevaCategoria)) {
                        categorias.add(categorias.size, nuevaCategoria)
                        categoriaAdapter.notifyDataSetChanged()
                        categoriaEt.setText(nuevaCategoria, false)
                        Toast.makeText(requireContext(), "Categoría '$nuevaCategoria' agregada", Toast.LENGTH_SHORT).show()
                    } else {
                        categoriaEt.setText(nuevaCategoria, false)
                        Toast.makeText(requireContext(), "La categoría ya existe", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Cancelar", null)
            .create()

        dialog.show()
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

        fun getDayLayoutParams(): GridLayout.LayoutParams {
            val params = GridLayout.LayoutParams(
                GridLayout.spec(GridLayout.UNDEFINED, 1f),
                GridLayout.spec(GridLayout.UNDEFINED, 1f)
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
