package com.example.ingresosgastosapp.Data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.ingresosgastosapp.DataBase.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class BalanceViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: BalanceRepository

    val balance: LiveData<com.example.ingresosgastosapp.Data.Balance?>

    init {
        val balanceDao = AppDatabase.getDatabase(application).balanceDao()
        repository = BalanceRepository(balanceDao)
        balance = repository.balance

        // Crear balance inicial si no existe
        viewModelScope.launch(Dispatchers.IO) {
            repository.setInitialBalanceIfNeeded()
        }
    }

    fun updateBalance(newTotal: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateBalance(newTotal)
        }
    }
    suspend fun getCurrentBalance(): Double {
        return withContext(Dispatchers.IO) {
            repository.getCurrentTotal() ?: 0.0
        }
    }
}