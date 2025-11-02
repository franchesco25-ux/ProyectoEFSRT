package com.example.ingresosgastosapp.Data

import androidx.lifecycle.LiveData

class IngresosRepository (private val ingresosDAO: IngresosDAO) {

    val readAllData: LiveData<List<Ingresos>> = ingresosDAO.readAllData()

    suspend fun addIngresos(ingreso: Ingresos) {
        ingresosDAO.addIngreso(ingreso)
    }

    suspend fun updateIngresos(ingreso: Ingresos) {
        ingresosDAO.updateIngreso(ingreso)
    }

    suspend fun deleteIngresos(ingreso: Ingresos) {
        ingresosDAO.deleteIngreso(ingreso)
    }

    suspend fun deleteAll() {
        ingresosDAO.deleteAllIngresos()
    }

    suspend fun getIngresoById(id: Int): Ingresos? {
        return ingresosDAO.getIngresoById(id)
    }
}