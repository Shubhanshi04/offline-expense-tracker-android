package com.shubhanshi.smartexpense.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.shubhanshi.smartexpense.domain.model.TransactionType

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val amount: Double,
    // Same value for same calendar date
    val dateEpochDay: Long,

    val type: TransactionType
)