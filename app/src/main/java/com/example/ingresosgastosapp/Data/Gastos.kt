package com.example.ingresosgastosapp.Data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gastos")
data class Gastos(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "descripcion")
    val descripcion: String,
    @ColumnInfo(name = "monto")
    val monto: Double,
    @ColumnInfo(name = "categoria")
    val categoria: String,
    @ColumnInfo(name = "fecha")
    val fecha: String
)