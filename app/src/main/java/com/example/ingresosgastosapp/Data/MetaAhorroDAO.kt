package com.example.ingresosgastosapp.Data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface MetaAhorroDAO {

    @Query("SELECT * FROM meta_ahorro ORDER BY completada ASC, id DESC")
    fun getAllMetas(): LiveData<List<MetaAhorro>>

    @Query("SELECT * FROM meta_ahorro ORDER BY completada ASC, id DESC LIMIT :limit")
    fun getTopMetas(limit: Int): LiveData<List<MetaAhorro>>

    @Query("SELECT * FROM meta_ahorro WHERE id = :id")
    suspend fun getMetaById(id: Int): MetaAhorro?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeta(meta: MetaAhorro)

    @Update
    suspend fun updateMeta(meta: MetaAhorro)

    @Delete
    suspend fun deleteMeta(meta: MetaAhorro)

    @Query("DELETE FROM meta_ahorro WHERE id = :id")
    suspend fun deleteMetaById(id: Int)
}

