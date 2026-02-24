package com.example.ingresosgastosapp

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ingresosgastosapp.Data.BalanceViewModel
import com.example.ingresosgastosapp.Data.Gastos
import com.example.ingresosgastosapp.Data.GastosViewModel
import com.example.ingresosgastosapp.Data.MetaAhorro
import com.example.ingresosgastosapp.DataBase.AppDatabase
import com.example.ingresosgastosapp.Data.MetaAhorroDAO
import com.example.ingresosgastosapp.CurrencyTextWatcher
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class EditarMetaActivity : BaseActivity() {

    private lateinit var dao: MetaAhorroDAO
    private val balanceViewModel: BalanceViewModel by viewModels()
    private val gastosViewModel: GastosViewModel by viewModels()

    private lateinit var etNombre: EditText
    private lateinit var etMontoObjetivo: EditText
    private lateinit var etMontoActual: EditText
    private lateinit var etFechaLimite: EditText
    private lateinit var spinnerFrecuencia: Spinner
    private lateinit var btnSave: MaterialButton
    private lateinit var btnDelete: View
    private lateinit var btnAgregarDinero: MaterialButton
    private lateinit var tvToolbarTitle: TextView
    private lateinit var avatarView: ShapeableImageView

    private var metaId: Int = -1
    private var isCreateMode = false
    private var metaActual: MetaAhorro? = null
    private var selectedColor: String = "#0DF259"
    private var selectedIcon: String = "ic_goal"

    private lateinit var colorViews: List<View>
    private val colorValues = listOf("#0DF259", "#3B82F6", "#A855F7", "#F97316", "#E11D48")

    private val frecuenciaOptions = arrayOf("No avisar", "Diario", "Semanal", "Quincenal", "Mensual")
    private val frecuenciaValues = arrayOf("none", "diario", "semanal", "quincenal", "mensual")

    // 25 clean, circular icons
    private val iconList = listOf(
        "ic_goal", "ic_meta_casa", "ic_meta_auto", "ic_meta_avion", "ic_meta_viaje",
        "ic_meta_laptop", "ic_meta_telefono", "ic_meta_libro", "ic_meta_comida",
        "ic_meta_cafe", "ic_meta_estrella", "ic_meta_corazon", "ic_meta_regalo",
        "ic_meta_musica", "ic_meta_billetera", "ic_meta_tarjeta", "ic_meta_reloj",
        "ic_meta_maleta", "ic_meta_cohete", "ic_meta_montana", "ic_meta_graduacion",
        "ic_meta_hospital", "ic_meta_herramientas", "ic_meta_dinero", "ic_meta_compras"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_meta)

        dao = AppDatabase.getDatabase(this).metaAhorroDao()

        // Initialize views
        etNombre = findViewById(R.id.et_nombre_meta)
        etMontoObjetivo = findViewById(R.id.et_monto_objetivo)
        etMontoObjetivo.addTextChangedListener(CurrencyTextWatcher(etMontoObjetivo))
        etMontoActual = findViewById(R.id.et_monto_actual)
        etMontoActual.addTextChangedListener(CurrencyTextWatcher(etMontoActual))
        etFechaLimite = findViewById(R.id.et_fecha_limite)
        spinnerFrecuencia = findViewById(R.id.spinner_frecuencia)
        btnSave = findViewById(R.id.btn_save_meta_changes)
        btnDelete = findViewById(R.id.btn_delete_meta)
        btnAgregarDinero = findViewById(R.id.btn_agregar_dinero)
        tvToolbarTitle = findViewById(R.id.tv_toolbar_title)
        avatarView = findViewById(R.id.icon_meta_avatar)

        val btnClose = findViewById<ImageButton>(R.id.btn_close_edit_meta)
        btnClose.setOnClickListener { finish() }

        // Icon picker
        val btnChangeIcon = findViewById<ImageView>(R.id.btn_change_icon)
        btnChangeIcon.setOnClickListener { showIconPicker() }

        // Color selectors
        colorViews = listOf(
            findViewById(R.id.color_green),
            findViewById(R.id.color_blue),
            findViewById(R.id.color_purple),
            findViewById(R.id.color_orange),
            findViewById(R.id.color_rose)
        )
        setupColorSelection()

        // Date picker
        etFechaLimite.setOnClickListener { showDatePicker() }

        // Notification frequency spinner
        setupFrequencySpinner()

        // Determine mode
        val mode = intent.getStringExtra("MODE")
        metaId = intent.getIntExtra("META_ID", -1)
        isCreateMode = mode == "CREATE" || metaId == -1

        if (isCreateMode) {
            tvToolbarTitle.text = "Nueva Meta"
            btnSave.text = "Crear Meta"
            btnDelete.visibility = View.GONE
            btnAgregarDinero.visibility = View.GONE
        } else {
            tvToolbarTitle.text = "Editar Meta"
            btnSave.text = "Guardar Cambios"
            btnDelete.visibility = View.VISIBLE
            btnAgregarDinero.visibility = View.VISIBLE
            cargarDatosMeta()
        }

        // Save button
        btnSave.setOnClickListener { guardarMeta() }

        // Delete button
        btnDelete.setOnClickListener { confirmarEliminar() }

        // Add money button
        btnAgregarDinero.setOnClickListener { mostrarDialogoAgregarDinero() }
    }

    private fun setupFrequencySpinner() {
        val adapter = ArrayAdapter(this, R.layout.spinner_item_premium, android.R.id.text1, frecuenciaOptions)
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item_premium)
        spinnerFrecuencia.adapter = adapter
    }

    private fun setupColorSelection() {
        updateColorSelection()
        colorViews.forEachIndexed { index, view ->
            view.setOnClickListener {
                selectedColor = colorValues[index]
                updateColorSelection()
                updateAvatarColor()
            }
        }
    }

    private fun updateColorSelection() {
        colorViews.forEachIndexed { index, view ->
            if (colorValues[index] == selectedColor) {
                view.scaleX = 1.3f
                view.scaleY = 1.3f
                view.elevation = 8f
            } else {
                view.scaleX = 1.0f
                view.scaleY = 1.0f
                view.elevation = 0f
            }
        }
    }

    private fun updateAvatarColor() {
        try {
            val color = Color.parseColor(selectedColor)
            avatarView.backgroundTintList = ColorStateList.valueOf(Color.argb(26, Color.red(color), Color.green(color), Color.blue(color)))
            avatarView.imageTintList = ColorStateList.valueOf(color)
            avatarView.strokeColor = ColorStateList.valueOf(color)
        } catch (_: Exception) { }
    }

    private fun updateAvatarIcon() {
        try {
            val resId = resources.getIdentifier(selectedIcon, "drawable", packageName)
            if (resId != 0) {
                avatarView.setImageResource(resId)
            }
        } catch (_: Exception) { }
    }

    private fun showIconPicker() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_icon_picker, null)
        val rvIcons = dialogView.findViewById<RecyclerView>(R.id.rv_icons)
        rvIcons.layoutManager = GridLayoutManager(this, 5)

        val dialog = AlertDialog.Builder(this, R.style.AlertDialogTheme)
            .setView(dialogView)
            .create()

        rvIcons.adapter = IconPickerAdapter(iconList, selectedIcon, selectedColor) { iconName ->
            selectedIcon = iconName
            updateAvatarIcon()
            updateAvatarColor()
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val currentText = etFechaLimite.text.toString()
        if (currentText.isNotEmpty()) {
            try {
                val parts = currentText.split("/")
                if (parts.size == 3) {
                    calendar.set(Calendar.DAY_OF_MONTH, parts[0].toInt())
                    calendar.set(Calendar.MONTH, parts[1].toInt() - 1)
                    calendar.set(Calendar.YEAR, parts[2].toInt())
                }
            } catch (_: Exception) { }
        }

        val datePicker = DatePickerDialog(
            this,
            R.style.DatePickerTheme,
            { _, year, month, dayOfMonth ->
                val fechaFormateada = "%02d/%02d/%04d".format(dayOfMonth, month + 1, year)
                etFechaLimite.setText(fechaFormateada)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.datePicker.minDate = System.currentTimeMillis() - 1000
        datePicker.show()
    }

    private fun cargarDatosMeta() {
        lifecycleScope.launch {
            val meta = withContext(Dispatchers.IO) {
                dao.getMetaById(metaId)
            }

            if (meta != null) {
                metaActual = meta
                etNombre.setText(meta.nombre)
                etMontoObjetivo.setText("%,.2f".format(meta.montoObjetivo))
                etMontoActual.setText("%,.2f".format(meta.montoActual))
                if (meta.fechaLimite.isNotEmpty()) {
                    etFechaLimite.setText(meta.fechaLimite)
                }
                selectedColor = meta.color
                selectedIcon = meta.icono
                updateColorSelection()
                updateAvatarColor()
                updateAvatarIcon()

                // Set frequency spinner
                val freqIndex = frecuenciaValues.indexOf(meta.frecuenciaNotificacion)
                if (freqIndex >= 0) {
                    spinnerFrecuencia.setSelection(freqIndex)
                }
            } else {
                Toast.makeText(this@EditarMetaActivity, "No se pudo cargar la meta", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun mostrarDialogoAgregarDinero() {
        val input = EditText(this)
        input.inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
        input.hint = "Monto a agregar"
        input.setTextColor(Color.WHITE)
        input.setHintTextColor(Color.parseColor("#66FFFFFF"))
        input.setPadding(48, 32, 48, 32)

        AlertDialog.Builder(this, R.style.AlertDialogTheme)
            .setTitle("Agregar Dinero")
            .setMessage("Este monto se registrará como un gasto y se descontará de tu balance.\n¿Cuánto deseas agregar a esta meta?")
            .setView(input)
            .setPositiveButton("Agregar") { _, _ ->
                val montoAgregar = input.text.toString().replace(",", "").toDoubleOrNull()
                if (montoAgregar != null && montoAgregar > 0) {
                    lifecycleScope.launch {
                        val currentBalance = balanceViewModel.getCurrentBalance()
                        if (currentBalance >= montoAgregar) {
                            // 1. Deduct from balance
                            balanceViewModel.updateBalance(currentBalance - montoAgregar)

                            // 2. Create a Gasto record with category = meta name
                            val nombreMeta = etNombre.text.toString().trim().ifEmpty { "Meta de Ahorro" }
                            val fechaHoy = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                            val gasto = Gastos(
                                descripcion = "Ahorro: $nombreMeta",
                                monto = montoAgregar,
                                categoria = nombreMeta,
                                fecha = fechaHoy
                            )
                            gastosViewModel.addGasto(gasto)

                            // 3. Add to current goal amount
                            val montoActualVal = etMontoActual.text.toString().replace(",", "").toDoubleOrNull() ?: 0.0
                            val nuevoMonto = montoActualVal + montoAgregar
                            etMontoActual.setText("%,.2f".format(nuevoMonto))

                            Toast.makeText(this@EditarMetaActivity, "S/. %,.2f agregados a la meta".format(montoAgregar), Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this@EditarMetaActivity, "Saldo insuficiente. Tu balance es S/. %,.2f".format(currentBalance), Toast.LENGTH_LONG).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Ingrese un monto válido", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun guardarMeta() {
        val nombre = etNombre.text.toString().trim()
        val montoObjetivoStr = etMontoObjetivo.text.toString().replace(",", "").trim()
        val montoActualStr = etMontoActual.text.toString().replace(",", "").trim()
        val fechaLimite = etFechaLimite.text.toString().trim()
        val frecuenciaSel = frecuenciaValues[spinnerFrecuencia.selectedItemPosition]

        if (nombre.isEmpty()) {
            etNombre.error = "Ingrese un nombre"
            etNombre.requestFocus()
            return
        }

        if (montoObjetivoStr.isEmpty()) {
            etMontoObjetivo.error = "Ingrese un monto objetivo"
            etMontoObjetivo.requestFocus()
            return
        }

        val montoObjetivo = montoObjetivoStr.toDoubleOrNull()
        if (montoObjetivo == null || montoObjetivo <= 0) {
            etMontoObjetivo.error = "Ingrese un monto válido"
            etMontoObjetivo.requestFocus()
            return
        }

        val montoActual = if (montoActualStr.isNotEmpty()) {
            montoActualStr.toDoubleOrNull() ?: 0.0
        } else {
            0.0
        }

        val completada = montoActual >= montoObjetivo

        lifecycleScope.launch(Dispatchers.IO) {
            if (isCreateMode) {
                val nuevaMeta = MetaAhorro(
                    nombre = nombre,
                    montoObjetivo = montoObjetivo,
                    montoActual = montoActual,
                    fechaLimite = fechaLimite,
                    color = selectedColor,
                    icono = selectedIcon,
                    frecuenciaNotificacion = frecuenciaSel,
                    completada = completada
                )
                dao.insertMeta(nuevaMeta)
            } else {
                val metaActualizada = MetaAhorro(
                    id = metaId,
                    nombre = nombre,
                    montoObjetivo = montoObjetivo,
                    montoActual = montoActual,
                    fechaLimite = fechaLimite,
                    color = selectedColor,
                    icono = selectedIcon,
                    frecuenciaNotificacion = frecuenciaSel,
                    completada = completada
                )
                dao.updateMeta(metaActualizada)
            }

            withContext(Dispatchers.Main) {
                val msg = if (isCreateMode) "Meta creada correctamente" else "Meta actualizada correctamente"
                Toast.makeText(this@EditarMetaActivity, msg, Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun confirmarEliminar() {
        AlertDialog.Builder(this, R.style.AlertDialogTheme)
            .setTitle("Eliminar Meta")
            .setMessage("¿Estás seguro de que deseas eliminar esta meta de ahorro?")
            .setPositiveButton("Eliminar") { _, _ ->
                lifecycleScope.launch(Dispatchers.IO) {
                    dao.deleteMetaById(metaId)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@EditarMetaActivity, "Meta eliminada", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    // --- Inner Adapter for Icon Picker ---
    inner class IconPickerAdapter(
        private val icons: List<String>,
        private val currentIcon: String,
        private val accentColor: String,
        private val onIconSelected: (String) -> Unit
    ) : RecyclerView.Adapter<IconPickerAdapter.ViewHolder>() {

        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val container: FrameLayout = view.findViewById(R.id.icon_container)
            val icon: ImageView = view.findViewById(R.id.iv_icon)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_icon_picker, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val iconName = icons[position]
            val resId = resources.getIdentifier(iconName, "drawable", packageName)
            if (resId != 0) {
                holder.icon.setImageResource(resId)
            }

            try {
                val color = Color.parseColor(accentColor)
                if (iconName == currentIcon) {
                    holder.container.backgroundTintList = ColorStateList.valueOf(Color.argb(40, Color.red(color), Color.green(color), Color.blue(color)))
                    holder.icon.imageTintList = ColorStateList.valueOf(color)
                } else {
                    holder.container.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#1AFFFFFF"))
                    holder.icon.imageTintList = ColorStateList.valueOf(Color.parseColor("#88FFFFFF"))
                }
            } catch (_: Exception) {
                holder.icon.imageTintList = ColorStateList.valueOf(Color.WHITE)
            }

            holder.itemView.setOnClickListener {
                onIconSelected(iconName)
            }
        }

        override fun getItemCount() = icons.size
    }
}

