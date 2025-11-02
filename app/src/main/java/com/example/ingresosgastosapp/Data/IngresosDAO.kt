package com.example.ingresosgastosapp.Data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update


@Dao
interface IngresosDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addIngreso(ingreso: Ingresos)

    @Update
    suspend fun updateIngreso(ingreso: Ingresos)

    @Delete
    suspend fun deleteIngreso(ingreso: Ingresos)

    @Query("DELETE FROM ingresos")
    suspend fun deleteAllIngresos()

    @Query("SELECT * FROM ingresos ORDER BY id ASC")
    fun readAllData(): LiveData<List<Ingresos>>

    @Query("SELECT * FROM ingresos WHERE id = :id LIMIT 1")
    suspend fun getIngresoById(id: Int): Ingresos?
}