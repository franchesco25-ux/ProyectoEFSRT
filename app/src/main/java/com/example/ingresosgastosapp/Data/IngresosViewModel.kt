package com.example.ingresosgastosapp.Data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.ingresosgastosapp.DataBase.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class IngresosViewModel(application: Application) : AndroidViewModel(application) {

    val readAllData: LiveData<List<Ingresos>>
    private val repository: IngresosRepository

    init {
        val ingresosDAO = AppDatabase.getDatabase(application).ingresosDao()
        repository = IngresosRepository(ingresosDAO)
        readAllData = repository.readAllData
    }

    fun addIngresos(ingreso: Ingresos) = viewModelScope.launch(Dispatchers.IO) {
        repository.addIngresos(ingreso)
    }

    fun updateIngresos(ingreso: Ingresos) = viewModelScope.launch(Dispatchers.IO) {
        repository.updateIngresos(ingreso)
    }

    fun deleteIngresos(ingreso: Ingresos) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteIngresos(ingreso)
    }

    fun deleteAll() = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteAll()
    }
}