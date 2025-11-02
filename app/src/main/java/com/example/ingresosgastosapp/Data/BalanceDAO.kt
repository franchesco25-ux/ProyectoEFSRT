package com.example.ingresosgastosapp.Data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface BalanceDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBalance(balance: com.example.ingresosgastosapp.Data.Balance)

    @Query("SELECT * FROM balance_table WHERE id = 1")
    fun getBalance(): LiveData<com.example.ingresosgastosapp.Data.Balance>

    @Query("SELECT * FROM balance_table WHERE id = 1")
    suspend fun getBalanceDirect(): com.example.ingresosgastosapp.Data.Balance?

    @Update(onConflict = OnConflictStrategy.REPLACE) // Usa @Update para actualizar la entidad existente
    suspend fun updateBalanceEntity(balance: Balance)
}