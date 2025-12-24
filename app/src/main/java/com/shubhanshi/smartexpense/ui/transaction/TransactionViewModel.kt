package com.shubhanshi.smartexpense.ui.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shubhanshi.smartexpense.domain.model.Transaction
import com.shubhanshi.smartexpense.domain.model.TransactionType
import com.shubhanshi.smartexpense.domain.repository.TransactionRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

class TransactionViewModel(
    private val repository: TransactionRepository
) : ViewModel() {
    // Which calendar day is selected in the UI
    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate

    private val _selectedMonth = MutableStateFlow(YearMonth.from(LocalDate.now()))
    val selectedMonth: StateFlow<YearMonth> = _selectedMonth.asStateFlow()

    private val _currentMonth = MutableStateFlow(YearMonth.now())
    val currentMonth: StateFlow<YearMonth> = _currentMonth

    // Whole UI state
    private val _uiState = MutableStateFlow(
        TransactionUiState(
            _selectedDate.value,
            isLoading = true
        )
    )
    val uiState: StateFlow<TransactionUiState> = _uiState

    init {
        transactionForSelectedDate()
    }

    fun transactionForSelectedDate() {
        viewModelScope.launch {
            selectedDate.collectLatest { date ->
                repository.getTransactionForDate(date.toEpochDay())
                    .collect { transactions ->
                        _uiState.update { s ->
                            s.copy(transactionsForDay = transactions, isLoading = false)
                        }
                    }
            }


        }
    }

    private fun transactionForCurrentMonth() {
        viewModelScope.launch {
            selectedMonth.collectLatest { date ->
                val startDate = date.atDay(1).toEpochDay()
                val endDate = date.atEndOfMonth().toEpochDay()
                repository.getTransactionsForMonth(startDate, endDate)
                    .collectLatest { txs ->
                        val income = txs
                            .filter { it.type == TransactionType.INCOME }
                            .sumOf { it.amount }
                        val expense = txs
                            .filter { it.type == TransactionType.EXPENSE }
                            .sumOf { it.amount }

                        _uiState.update { s ->
                            s.copy(
                                monthIncome = income,
                                monthExpense = expense,
                                monthNet = income - expense
                            )
                        }
                    }
            }
        }
    }

    fun onDateSelected(date: LocalDate) {
        _selectedDate.value = date
    }

    fun onMonthChanged(newMonth: YearMonth) {
        _selectedMonth.value = newMonth
        // optionally change selectedDate to first day of new month:
        _selectedDate.value = newMonth.atDay(1)
    }

    fun onMonthSelected(newDate: LocalDate) {
        _selectedDate.value = newDate
    }

    fun nextMonth() {
        _selectedDate.value = _selectedDate.value
            .plusMonths(1)
    }

    fun previousMonth() {
        _selectedDate.value = _selectedDate.value
            .minusMonths(1)
    }

    fun addTransaction(
        title: String,
        amount: Double,
        type: TransactionType
    ) {

        if (amount <= 0.0) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Enter valid amount"
            )
            return

        }
        viewModelScope.launch {
            val dateEpochDay = _selectedDate.value.toEpochDay()
            repository.addTransaction(
                Transaction(
                    title = title,
                    amount = amount,
                    dateEpochDay = dateEpochDay,
                    type = type
                )
            )
        }

    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun clearTransaction(id: Long) {
        viewModelScope.launch {
            repository.deleteTransaction(id)
        }
    }

    fun clearForSelectedDate() {
        viewModelScope.launch {
            repository.clearForDate(_selectedDate.value.toEpochDay())
        }
    }

    // --- Optional: helps test DB in Database Inspector ----
    @Suppress("unused")
    private fun insertDummyForTesting() {
        viewModelScope.launch {
            repository.addTransaction(
                Transaction(
                    title = "Dummy test",
                    amount = 123.0,
                    dateEpochDay = LocalDate.now().toEpochDay(),
                    type = TransactionType.EXPENSE
                )
            )
        }
    }

}