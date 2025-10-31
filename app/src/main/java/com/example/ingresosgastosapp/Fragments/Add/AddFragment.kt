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
import com.example.ingresosgastosapp.databinding.FragmentAddBinding
import kotlin.jvm.java

class AddFragment : Fragment() {

    private lateinit var mIngresosViewModel: IngresosViewModel

    private lateinit var binding: FragmentAddBinding

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
        val firstName = binding.addFirstNameEt.text.toString()
        val lastName = binding.addLastNameEt.text.toString()
        val age = binding.addAgeEt.text

        if (inputCheck(firstName, lastName, age)){

            //Create User Object
            val ingreso = Ingresos(0, firstName, lastName, Integer.parseInt(age.toString()))

            //add Data to Database
            mIngresosViewModel.addIngresos(ingreso)
            Toast.makeText(requireContext(), "Successfully added!", Toast.LENGTH_SHORT).show()

            //Navigate Back
            findNavController().navigate(R.id.action_addFragment_to_listFragment)
        } else{
            Toast.makeText(requireContext(), "Please fill out all fields", Toast.LENGTH_SHORT).show()
        }
    }

    private fun inputCheck(firstName: String, lastName: String, age:Editable): Boolean{
        return !(TextUtils.isEmpty(firstName) && TextUtils.isEmpty(lastName) && age.isEmpty())
    }
}