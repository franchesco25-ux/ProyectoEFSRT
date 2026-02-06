package com.example.ingresosgastosapp.DataBase

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.ingresosgastosapp.Data.Balance
import com.example.ingresosgastosapp.Data.BalanceDAO
import com.example.ingresosgastosapp.Data.Gastos
import com.example.ingresosgastosapp.Data.GastosDAO
import com.example.ingresosgastosapp.Data.Ingresos
import com.example.ingresosgastosapp.Data.IngresosDAO
import com.example.ingresosgastosapp.Data.User
import com.example.ingresosgastosapp.Data.UserDAO
import android.content.Context

@Database(
    entities = [Ingresos::class, Gastos::class, Balance::class, User::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun ingresosDao(): IngresosDAO
    abstract fun gastosDao(): GastosDAO
    abstract fun balanceDao(): BalanceDAO
    abstract fun userDao(): UserDAO

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}