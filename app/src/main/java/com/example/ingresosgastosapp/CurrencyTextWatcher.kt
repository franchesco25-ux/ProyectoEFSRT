package com.example.ingresosgastosapp

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import java.text.DecimalFormat

class CurrencyTextWatcher(private val editText: EditText) : TextWatcher {
    private val df = DecimalFormat("#,###.##")
    private var isUpdating = false

    init {
        df.isDecimalSeparatorAlwaysShown = false
        df.maximumFractionDigits = 2
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(s: Editable?) {
        if (isUpdating) return

        isUpdating = true

        val str = s.toString()
        val cleanString = str.replace(",", "")

        if (cleanString.isEmpty()) {
            isUpdating = false
            return
        }

        try {
            val hasDecimalPoint = cleanString.contains(".")
            
            // Allow typing ". " or ".0" at the end without truncating immediately
            if (hasDecimalPoint) {
                val parts = cleanString.split(".")
                val wholePart = if (parts[0].isEmpty()) "0" else parts[0]
                val formattedWhole = df.format(wholePart.toDouble())
                
                // Allow up to 2 decimal places manually
                var fractionalPart = if (parts.size > 1) parts[1] else ""
                if (fractionalPart.length > 2) fractionalPart = fractionalPart.substring(0, 2)
                
                val newString = "$formattedWhole.$fractionalPart"
                
                if (str != newString) {
                    editText.setText(newString)
                    editText.setSelection(newString.length)
                }
            } else {
                val parsed = cleanString.toDouble()
                val formatted = df.format(parsed)

                if (str != formatted) {
                    editText.setText(formatted)
                    editText.setSelection(formatted.length)
                }
            }

        } catch (e: Exception) {
            // Ignore format exception
        }

        isUpdating = false
    }
}
