package com.example.ingresosgastosapp.Data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.ingresosgastosapp.DataBase.IngresosDataBase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class IngresosViewModel(application: Application) : AndroidViewModel(application) {
    private val readAllData: LiveData<List<Ingresos>>
    private val repository: IngresosRepository

    init{
        val ingresosDAO = IngresosDataBase.getDataBase(application).ingresosDAO()
        repository = IngresosRepository(ingresosDAO)
        readAllData = repository.readAllData
    }

    fun addIngresos(ingresos: Ingresos){
        viewModelScope.launch(Dispatchers.IO) {
            repository.addIngresos(ingresos)
        }
    }
}