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
import com.example.ingresosgastosapp.Adapter.ListGastosAdapter
import com.example.ingresosgastosapp.Data.GastosViewModel
import com.example.ingresosgastosapp.EditarGastoActivity
import com.example.ingresosgastosapp.MainActivity
import com.example.ingresosgastosapp.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ListGastosFragment : Fragment() {

    private lateinit var mGastosViewModel: GastosViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_list_gastos, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerviewGastos)
        val btnVolver = view.findViewById<Button>(R.id.btnVolverMainGastos)
        val fab = view.findViewById<FloatingActionButton>(R.id.floatingActionButtonGasto)

        mGastosViewModel = ViewModelProvider(this)[GastosViewModel::class.java]

        val adapter = ListGastosAdapter(
            onEditClick = { gasto ->
                val intent = Intent(requireContext(), EditarGastoActivity::class.java)
                intent.putExtra("GASTO_ID", gasto.id)
                startActivity(intent)
            },
            onDeleteClick = { gasto ->
                AlertDialog.Builder(requireContext())
                    .setTitle("Eliminar Gasto")
                    .setMessage("¿Estás seguro de eliminar '${gasto.descripcion}'? Esto también afectará tu balance.")
                    .setPositiveButton("Eliminar") { _, _ ->
                        mGastosViewModel.deleteGastoWithBalance(gasto)
                        Toast.makeText(requireContext(), "Gasto eliminado", Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("Cancelar", null)
                    .show()
            }
        )

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        mGastosViewModel.readAllData.observe(viewLifecycleOwner) { gastos ->
            adapter.setData(gastos)
        }

        fab.setOnClickListener {
            findNavController().navigate(R.id.action_listGastosFragment_to_addGastosFragment)
        }

        btnVolver.setOnClickListener {
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

        return view
    }
}