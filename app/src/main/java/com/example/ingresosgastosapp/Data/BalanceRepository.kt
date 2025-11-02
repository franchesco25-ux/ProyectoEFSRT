package com.example.ingresosgastosapp.Data

import androidx.lifecycle.LiveData
import com.example.ingresosgastosapp.Data.BalanceDAO
import com.example.ingresosgastosapp.Data.Balance

class BalanceRepository(private val balanceDao: BalanceDAO) {

    val balance: LiveData<Balance?> = balanceDao.getBalance() as LiveData<Balance?>

    suspend fun setInitialBalanceIfNeeded() {
        val current = balanceDao.getBalanceDirect()
        if (current == null) {
            balanceDao.insertBalance(Balance(total = 1000.0))
        }
    }

    suspend fun getBalanceEntityDirect(): Balance? {
        return balanceDao.getBalanceDirect()
    }

    suspend fun updateBalance(newTotal: Double) {

        val existingBalance = getBalanceEntityDirect()


        val balanceToUpdate = existingBalance?.copy(total = newTotal)
            ?: Balance(id = 1, total = newTotal)


        balanceDao.updateBalanceEntity(balanceToUpdate)
    }
    suspend fun getCurrentTotal(): Double? {
        return balanceDao.getBalanceDirect()?.total
    }
}