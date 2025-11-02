package com.example.ingresosgastosapp.Data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.ingresosgastosapp.DataBase.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GastosViewModel(application: Application) : AndroidViewModel(application) {

    val readAllData: LiveData<List<Gastos>>
    private val repository: GastosRepository

    init {
        val gastosDAO = AppDatabase.getDatabase(application).gastosDao()
        repository = GastosRepository(gastosDAO)
        readAllData = repository.readAllData
    }

    fun addGasto(gasto: Gastos) = viewModelScope.launch(Dispatchers.IO) {
        repository.addGasto(gasto)
    }

    fun updateGasto(gasto: Gastos) = viewModelScope.launch(Dispatchers.IO) {
        repository.updateGasto(gasto)
    }

    fun deleteGasto(gasto: Gastos) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteGasto(gasto)
    }

    fun deleteAll() = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteAll()
    }
}