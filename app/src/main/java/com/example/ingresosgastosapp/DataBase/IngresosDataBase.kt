package com.example.ingresosgastosapp.DataBase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.ingresosgastosapp.Data.Ingresos
import com.example.ingresosgastosapp.Data.IngresosDAO

@Database(entities = [Ingresos::class], version = 1, exportSchema = false)
abstract class IngresosDataBase: RoomDatabase() {

    abstract fun ingresosDAO(): IngresosDAO

    companion object{
        @Volatile
        private var INSTANCE: IngresosDataBase? = null

        fun getDataBase(context: Context): IngresosDataBase{
            val tempInstance = INSTANCE
            if(tempInstance != null ){
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    IngresosDataBase::class.java,
                    "ingresos_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}