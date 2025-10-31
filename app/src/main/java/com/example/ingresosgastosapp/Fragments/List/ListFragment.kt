package com.example.ingresosgastosapp.Fragments.List

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ingresosgastosapp.Data.IngresosViewModel
import com.example.ingresosgastosapp.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ListFragment : Fragment() {

    private lateinit var mIngresosViewModel: IngresosViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_list, container, false)

        val verView = view.findViewById<FloatingActionButton>(R.id.floatingActionButton)

        //RecyclerView
        val adapter = ListAdapter()

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerview)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        //UserViewModel
        mIngresosViewModel = ViewModelProvider(this).get(IngresosViewModel::class.java)
        mIngresosViewModel.readAllData.observe(viewLifecycleOwner, Observer{ user ->
            adapter.setData(user)
        })


        verView.setOnClickListener {
            findNavController().navigate(R.id.action_listFragment_to_addFragment)
        }

        return verView
    }
}