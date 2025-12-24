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

    @Query(
        """
    SELECT * FROM transactions
    WHERE dateEpochDay BETWEEN :startEpochDay AND :endEpochDay
    ORDER BY dateEpochDay DESC, id DESC
    """
    )
    fun getTransactionsForMonth(
        startEpochDay: Long,
        endEpochDay: Long
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


    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteTransaction(
        id: Long
    )

    // Clear a specific day
    @Query("DELETE from transactions WHERE dateEpochDay =:dateEpochDay" )
    suspend fun clearForDate(
        dateEpochDay: Long
    )
}