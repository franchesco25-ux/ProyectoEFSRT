package com.example.ingresosgastosapp.Data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ingresos")
data class Ingresos(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo(name = "monto")
    val monto:Int,
    @ColumnInfo(name = "descripcion")
    val descripcion: String
)