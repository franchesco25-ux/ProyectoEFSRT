package com.example.ingresosgastosapp.Data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface GastosDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addGasto(gastos: Gastos)

    @Update
    suspend fun updateGasto(gastos: Gastos)

    @Delete
    suspend fun deleteGasto(gastos: Gastos)

    @Query("DELETE FROM gastos")
    suspend fun deleteAllGastos()

    @Query("SELECT * FROM gastos ORDER BY id ASC")
    fun readAllData(): LiveData<List<Gastos>>
}