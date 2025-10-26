package com.example.ingresosgastosapp.Data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface IngresosDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addIngreso(ingresos: Ingresos)

    @Query("SELECT * FROM ingresos ORDER BY id ASC")
    fun readAllData():LiveData<List<Ingresos>>
}