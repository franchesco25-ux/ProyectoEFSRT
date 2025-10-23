package com.example.ingresosgastosapp.DataBase

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.ingresosgastosapp.Data.Gasto

class GastosDatabaseHelper(context: Context):
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object{
        private const val DATABASE_NAME = "gastos.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME="gastos"
        private const val COLUMN_ID = "id"
        private const val COLUMN_CATEGORIA = "categoria"
        private const val COLUMN_MONTO = "monto"
        private const val COLUMN_DESCRIPCION = "descripcion"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createTableQuery = "CREATE TABLE $TABLE_NAME($COLUMN_ID INTEGER PRIMARY KEY, " +
                "$COLUMN_MONTO INTEGER, " +
                "$COLUMN_DESCRIPCION TEXT," +
                "$COLUMN_CATEGORIA TEXT)"
        //metodo que ejecuta la statement
        db?.execSQL(createTableQuery)
    }

    override fun onUpgrade(
        db: SQLiteDatabase?,
        oldVersion: Int,
        newVersion: Int
    ) {
        //elimina la duplicidad si llegase a existir mas de una tabla con el mismo nombre
        val dropTableQuery = "DROP TABLE IF EXISTS $TABLE_NAME"
        db?.execSQL(dropTableQuery)
        onCreate(db)
    }

    fun insertGastos(gasto: Gasto){
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_MONTO, gasto.monto)
            put(COLUMN_DESCRIPCION, gasto.descripcion)
            put(COLUMN_CATEGORIA, gasto.categoria)
        }
        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    fun allgetGastos(): List<Gasto>{

        //Una lista mutable se refiere que puede ser modificada o es flexible
        val gastosList = mutableListOf<Gasto>()
        val db = readableDatabase
        val query = "SELECT * FROM $TABLE_NAME"

        //Un cursor es usado para iterar a traves de filas de la tabla
        val cursor = db.rawQuery(query, null)

        while (cursor.moveToNext()){
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
            val monto = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MONTO))
            val categoria = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CATEGORIA))
            val descripcion = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPCION))

            val gasto = Gasto(id, monto, descripcion, categoria)

            gastosList.add(gasto)
        }

        cursor.close()
        db.close()
        return gastosList
    }
}