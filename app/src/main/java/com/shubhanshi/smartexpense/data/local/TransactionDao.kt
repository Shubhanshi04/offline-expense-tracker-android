package com.shubhanshi.smartexpense.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.shubhanshi.smartexpense.domain.model.TransactionType
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    // Get all transactions for a selected day
    @Query("SELECT * FROM transactions WHERE dateEpochDay = :dateEpochDay ORDER BY id DESC")
    fun getTransactionsForDate(
        dateEpochDay: Long
    ): Flow<List<TransactionEntity>>

    // Insert income / expense
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(
        transaction: TransactionEntity
    )

    // Clear income/expense
    @Query("DELETE from transactions Where id =:transactionId ")
    suspend fun clearATransaction(
        transactionId: Long
    )

    @Delete
    suspend fun deleteTransaction(
        transaction: TransactionEntity
    )

    // Clear a specific day
    @Query("DELETE from transactions WHERE dateEpochDay =:dateEpochDay" )
    suspend fun clearForDate(
        dateEpochDay: Long
    )
}