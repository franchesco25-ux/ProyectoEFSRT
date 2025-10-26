package com.example.ingresosgastosapp.Data

import androidx.lifecycle.LiveData

class IngresosRepository (private val ingresosDAO: IngresosDAO) {

    val readAllData: LiveData<List<Ingresos>> = ingresosDAO.readAllData()

    suspend fun addIngresos(ingresos: Ingresos){
        ingresosDAO.addIngreso(ingresos)
    }
}