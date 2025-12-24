package com.shubhanshi.smartexpense.ui.transaction

import com.shubhanshi.smartexpense.domain.model.Transaction
import java.time.LocalDate

/**
 * Immutable state for the screen.
 * UI will just read this.
 */

data class TransactionUiState(
    val selectedDate: LocalDate = LocalDate.now(),
    val transactionsForDay: List<Transaction> = emptyList(),
    val monthIncome: Double = 0.0,
    val monthExpense: Double = 0.0,
    val monthNet: Double = 0.0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
