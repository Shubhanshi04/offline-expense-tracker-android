package com.shubhanshi.smartexpense.domain.model

/**
 * Domain model used by ViewModel and UI.
 * Separate from Room's TransactionEntity.
 */

data class Transaction(
    val id : Long = 0,
    val title: String = "",
    val amount: Double,
    val dateEpochDay: Long,
    val type: TransactionType
)