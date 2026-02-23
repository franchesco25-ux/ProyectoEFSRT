package com.example.ingresosgastosapp

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton

class QuickActionsBottomSheet : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_quick_actions, container, false)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)?.setBackgroundColor(Color.TRANSPARENT)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnNuevoGasto = view.findViewById<View>(R.id.btnNuevoGasto)
        val btnNuevaMeta = view.findViewById<View>(R.id.btnNuevaMeta)
        val btnIngresoRapido = view.findViewById<View>(R.id.btnIngresoRapido)
        val btnClose = view.findViewById<FloatingActionButton>(R.id.btnCloseSheet)

        btnNuevoGasto.setOnClickListener {
            startActivity(Intent(requireContext(), GastosActivity::class.java))
            dismiss()
        }

        // CORRECCIÓN: Ahora redirige a AgregarMetaActivity
        btnNuevaMeta.setOnClickListener {
            startActivity(Intent(requireContext(), AgregarMetaActivity::class.java))
            dismiss()
        }

        btnIngresoRapido.setOnClickListener {
            startActivity(Intent(requireContext(), PruebaActivity::class.java))
            dismiss()
        }

        btnClose.setOnClickListener {
            dismiss()
        }
    }
}
