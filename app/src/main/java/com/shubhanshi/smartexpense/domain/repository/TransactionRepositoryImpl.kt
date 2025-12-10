package com.shubhanshi.smartexpense.domain.repository

import com.shubhanshi.smartexpense.data.local.TransactionDao
import com.shubhanshi.smartexpense.data.toDomain
import com.shubhanshi.smartexpense.data.toEntity
import com.shubhanshi.smartexpense.domain.model.Transaction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TransactionRepositoryImpl(
    private val dao: TransactionDao
) : TransactionRepository{
    override fun getTransactionForDate(dateEpochDate: Long): Flow<List<Transaction>> {
        return dao.getTransactionsForDate(dateEpochDate).map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun addTransaction(transaction: Transaction) {
        dao.insertTransaction(transaction.toEntity())
    }

    override suspend fun deleteTransaction(id:Long) {
        dao.deleteTransaction(id)
    }

    override suspend fun clearForDate(dateEpochDate: Long) {
       dao.clearForDate(dateEpochDate)
    }
}