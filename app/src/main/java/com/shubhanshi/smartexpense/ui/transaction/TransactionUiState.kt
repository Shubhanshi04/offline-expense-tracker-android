package com.shubhanshi.smartexpense.ui.transaction

import com.shubhanshi.smartexpense.domain.model.Transaction
import java.time.LocalDate

/**
 * Immutable state for the screen.
 * UI will just read this.
 */

data class TransactionUiState(
    val selectedDate: LocalDate = LocalDate.now(),
    val transactions: List<Transaction> = emptyList(),
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {
    val net: Double get() = totalIncome - totalExpense
}
