package com.example.ingresosgastosapp.Fragments.List

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ingresosgastosapp.Adapter.ListAdapter
import com.example.ingresosgastosapp.Data.IngresosViewModel
import com.example.ingresosgastosapp.EditarIngresoActivity
import com.example.ingresosgastosapp.MainActivity
import com.example.ingresosgastosapp.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ListFragment : Fragment() {

    private lateinit var mIngresosViewModel: IngresosViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_list, container, false)

        val verView = view.findViewById<FloatingActionButton>(R.id.floatingActionButton)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerview)
        val btnVolver = view.findViewById<Button>(R.id.btnVolverMain)

        mIngresosViewModel = ViewModelProvider(this)[IngresosViewModel::class.java]

        val adapter = ListAdapter(
            onEditClick = { ingreso ->
                val intent = Intent(requireContext(), EditarIngresoActivity::class.java)
                intent.putExtra("INGRESO_ID", ingreso.id)
                startActivity(intent)
            },
            onDeleteClick = { ingreso ->
                AlertDialog.Builder(requireContext())
                    .setTitle("Eliminar Ingreso")
                    .setMessage("¿Estás seguro de eliminar '${ingreso.descripcion}'? Esto también afectará tu balance.")
                    .setPositiveButton("Eliminar") { _, _ ->
                        mIngresosViewModel.deleteIngresosWithBalance(ingreso)
                        Toast.makeText(requireContext(), "Ingreso eliminado", Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }
        )

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        mIngresosViewModel.readAllData.observe(viewLifecycleOwner) { ingresos ->
            adapter.setData(ingresos)
        }

        verView.setOnClickListener {
            findNavController().navigate(R.id.action_listFragment_to_addFragment)
        }

        btnVolver.setOnClickListener {
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        return view
    }

}