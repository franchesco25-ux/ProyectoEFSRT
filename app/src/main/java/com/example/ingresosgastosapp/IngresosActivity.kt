package com.example.ingresosgastosapp

import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment

class IngresosActivity : BaseActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ingresos)

        findViewById<android.view.View>(R.id.btn_close).setOnClickListener { finish() }

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment) as NavHostFragment

        val navController = navHostFragment.navController

    }


    override fun onSupportNavigateUp(): Boolean{

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment) as NavHostFragment
        val navController = navHostFragment.navController

        return navController.navigateUp()||super.onSupportNavigateUp()
    }
}