package com.example.ingresosgastosapp.Data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "balance_table")
data class Balance(
    @PrimaryKey(autoGenerate = false)
    val id: Int = 1,
    var total: Double
)