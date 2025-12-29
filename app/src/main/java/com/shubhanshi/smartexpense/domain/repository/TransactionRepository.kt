package com.shubhanshi.smartexpense.domain.repository

import com.shubhanshi.smartexpense.domain.model.Transaction
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate


/**
 * Abstraction over the data source.
 * ViewModel will depend on this, not directly on DAO.
 */

interface TransactionRepository {

    fun getTransactionForDate(dateEpochDate: Long): Flow<List<Transaction>>

    fun getAllTransactions(): Flow<List<Transaction>>

    fun getTransactionsForMonth(startEpochDay: LocalDate, endEpochDay: LocalDate): Flow<List<Transaction>>

    suspend fun addTransaction(transaction: Transaction)

    suspend fun deleteTransaction(id:Long)

    suspend fun updateTransaction(transaction: Transaction)

    suspend fun clearForDate(dateEpochDate: Long)
}