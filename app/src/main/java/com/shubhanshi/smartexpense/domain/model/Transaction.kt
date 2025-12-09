package com.shubhanshi.smartexpense.domain.model

data class Transaction(
    val id : Long = 0,
    val title: String,
    val amount: Double,
    val dateEpochDay: Long,
    val type: TransactionType
)