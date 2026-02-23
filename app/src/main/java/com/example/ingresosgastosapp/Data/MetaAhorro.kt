package com.example.ingresosgastosapp.Data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "meta_ahorro")
data class MetaAhorro(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "nombre")
    val nombre: String,
    @ColumnInfo(name = "monto_objetivo")
    val montoObjetivo: Double,
    @ColumnInfo(name = "monto_actual")
    val montoActual: Double = 0.0,
    @ColumnInfo(name = "fecha_limite")
    val fechaLimite: String = "",
    @ColumnInfo(name = "color")
    val color: String = "#0DF259",
    @ColumnInfo(name = "icono")
    val icono: String = "ic_goal",
    @ColumnInfo(name = "frecuencia_notificacion")
    val frecuenciaNotificacion: String = "none",
    @ColumnInfo(name = "completada")
    val completada: Boolean = false
)
