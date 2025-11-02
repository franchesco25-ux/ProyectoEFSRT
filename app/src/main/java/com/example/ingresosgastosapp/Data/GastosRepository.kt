package com.example.ingresosgastosapp.Data

import androidx.lifecycle.LiveData

class GastosRepository(private val gastosDAO: GastosDAO) {

    val readAllData: LiveData<List<Gastos>> = gastosDAO.readAllData()

    suspend fun addGasto(gasto: Gastos) {
        gastosDAO.addGasto(gasto)
    }

    suspend fun updateGasto(gasto: Gastos) {
        gastosDAO.updateGasto(gasto)
    }

    suspend fun deleteGasto(gasto: Gastos) {
        gastosDAO.deleteGasto(gasto)
    }

    suspend fun deleteAll() {
        gastosDAO.deleteAllGastos()
    }
}