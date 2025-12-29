package com.shubhanshi.smartexpense.ui.transaction

import com.shubhanshi.smartexpense.domain.model.Transaction
import java.time.LocalDate
import java.time.YearMonth

/**
 * Immutable state for the screen.
 * UI will just read this.
 */

data class TransactionUiState(
    val selectedMonth: YearMonth = YearMonth.now(),
    val transactionsByDate: Map<LocalDate, List<Transaction>> = emptyMap(),
    val monthIncome: Double = 0.0,
    val monthExpense: Double = 0.0,
    val monthNet: Double = 0.0
)
