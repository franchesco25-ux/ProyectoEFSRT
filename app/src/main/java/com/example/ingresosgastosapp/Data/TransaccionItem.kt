package com.example.ingresosgastosapp.Data

data class TransaccionItem(
    val id: Int,
    val descripcion: String,
    val monto: Double,
    val categoria: String,
    val fecha: String,
    val tipo: TipoTransaccion
)

enum class TipoTransaccion {
    INGRESO,
    GASTO
}
