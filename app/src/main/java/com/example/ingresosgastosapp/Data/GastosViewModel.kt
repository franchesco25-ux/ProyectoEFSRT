package com.example.ingresosgastosapp.Data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.ingresosgastosapp.DataBase.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GastosViewModel(application: Application) : AndroidViewModel(application) {

    val readAllData: LiveData<List<Gastos>>
    private val repository: GastosRepository
    private val balanceRepository: BalanceRepository

    init {
        val database = AppDatabase.getDatabase(application)
        val gastosDAO = database.gastosDao()
        val balanceDAO = database.balanceDao()

        repository = GastosRepository(gastosDAO)
        balanceRepository = BalanceRepository(balanceDAO)
        readAllData = repository.readAllData
    }

    fun addGasto(gasto: Gastos) = viewModelScope.launch(Dispatchers.IO) {
        repository.addGasto(gasto)
    }

    fun updateGasto(gasto: Gastos) = viewModelScope.launch(Dispatchers.IO) {
        repository.updateGasto(gasto)
    }

    fun updateGastoWithBalance(oldGasto: Gastos, newGasto: Gastos) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateGasto(newGasto)

            val currentBalance = balanceRepository.getCurrentTotal() ?: 0.0
            val balanceAdjustment = oldGasto.monto - newGasto.monto
            val newBalance = currentBalance + balanceAdjustment

            balanceRepository.updateBalance(newBalance)
        }

    fun deleteGastoWithBalance(gasto: Gastos) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteGasto(gasto)

        val currentBalance = balanceRepository.getCurrentTotal() ?: 0.0
        val newBalance = currentBalance + gasto.monto

        balanceRepository.updateBalance(newBalance)
    }

    fun deleteGasto(gasto: Gastos) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteGasto(gasto)
    }

    fun deleteAll() = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteAll()
    }

    suspend fun getGastoById(id: Int): Gastos? {
        return withContext(Dispatchers.IO) {
            repository.getGastoById(id)
        }
    }
}