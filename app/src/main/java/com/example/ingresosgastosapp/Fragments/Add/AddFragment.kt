package com.example.ingresosgastosapp.Fragments.Add

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.ingresosgastosapp.Data.Ingresos
import com.example.ingresosgastosapp.Data.IngresosViewModel
import com.example.ingresosgastosapp.R
import kotlin.jvm.java

class AddFragment : Fragment() {

    private lateinit var mIngresosViewModel: IngresosViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_add, container, false)

        mIngresosViewModel = ViewModelProvider(this).get(IngresosViewModel::class.java)

        val viewR = view.findViewById<Button>(R.id.add_btn)

        viewR.setOnClickListener {
            insertDataToDatabase()
        }

        return viewR
    }

    private fun insertDataToDatabase() {
        val descripcion = view?.findViewById<EditText>(R.id.addLast_Name_et)!!.text.toString()
        val monto = view?.findViewById<EditText>(R.id.addLast_Name_et)!!.text

        if (inputCheck(monto, descripcion)){
            //Create User Object
            val ingreso = Ingresos(0, Integer.parseInt(monto.toString()), descripcion)

            //add Data to Database
            mIngresosViewModel.addIngresos(ingreso)
            Toast.makeText(requireContext(), "Successfully added!", Toast.LENGTH_SHORT).show()

            //Navigate Back
            findNavController().navigate(R.id.action_addFragment_to_listFragment)
        } else{
            Toast.makeText(requireContext(), "Please fill out all fields", Toast.LENGTH_SHORT).show()
        }
    }

    private fun inputCheck(monto: Editable, descripcion: String): Boolean{
        return !(TextUtils.isEmpty(descripcion) && monto.isEmpty())
    }
}