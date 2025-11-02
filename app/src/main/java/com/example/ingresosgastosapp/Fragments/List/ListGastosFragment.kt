package com.example.ingresosgastosapp.Fragments.List

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.ingresosgastosapp.R
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ingresosgastosapp.Adapter.ListGastosAdapter
import com.example.ingresosgastosapp.Data.GastosViewModel
import com.example.ingresosgastosapp.MainActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ListGastosFragment : Fragment() {

    private lateinit var mGastosViewModel: GastosViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_list_gastos, container, false)

        val adapter = ListGastosAdapter()
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerviewGastos)
        val btnVolver = view.findViewById<Button>(R.id.btnVolverMainGastos)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        mGastosViewModel = ViewModelProvider(this).get(GastosViewModel::class.java)
        mGastosViewModel.readAllData.observe(viewLifecycleOwner) { gastos ->
            adapter.setData(gastos)
        }

        val fab = view.findViewById<FloatingActionButton>(R.id.floatingActionButtonGasto)
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