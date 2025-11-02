package com.example.ingresosgastosapp.Data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.ingresosgastosapp.DataBase.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class IngresosViewModel(application: Application) : AndroidViewModel(application) {

    val readAllData: LiveData<List<Ingresos>>
    private val repository: IngresosRepository
    private val balanceRepository: BalanceRepository

    init {
        val database = AppDatabase.getDatabase(application)
        val ingresosDAO = database.ingresosDao()
        val balanceDAO = database.balanceDao()

        repository = IngresosRepository(ingresosDAO)
        balanceRepository = BalanceRepository(balanceDAO)
        readAllData = repository.readAllData
    }

    fun addIngresos(ingreso: Ingresos) = viewModelScope.launch(Dispatchers.IO) {
        repository.addIngresos(ingreso)
    }

    fun updateIngresos(ingreso: Ingresos) = viewModelScope.launch(Dispatchers.IO) {
        repository.updateIngresos(ingreso)
    }

    fun updateIngresosWithBalance(oldIngreso: Ingresos, newIngreso: Ingresos) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateIngresos(newIngreso)

            val currentBalance = balanceRepository.getCurrentTotal() ?: 0.0
            val balanceAdjustment = newIngreso.monto - oldIngreso.monto
            val newBalance = currentBalance + balanceAdjustment

            balanceRepository.updateBalance(newBalance)
        }

    fun deleteIngresosWithBalance(ingreso: Ingresos) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteIngresos(ingreso)

        val currentBalance = balanceRepository.getCurrentTotal() ?: 0.0
        val newBalance = currentBalance - ingreso.monto

        balanceRepository.updateBalance(newBalance)
    }

    fun deleteIngresos(ingreso: Ingresos) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteIngresos(ingreso)
    }

    fun deleteAll() = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteAll()
    }

    suspend fun getIngresoById(id: Int): Ingresos? {
        return withContext(Dispatchers.IO) {
            repository.getIngresoById(id)
        }
    }
}