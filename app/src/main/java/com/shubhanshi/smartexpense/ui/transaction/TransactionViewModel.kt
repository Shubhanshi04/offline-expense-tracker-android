package com.shubhanshi.smartexpense.ui.transaction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shubhanshi.smartexpense.domain.model.Transaction
import com.shubhanshi.smartexpense.domain.model.TransactionType
import com.shubhanshi.smartexpense.domain.repository.TransactionRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

class TransactionViewModel(
    private val repository: TransactionRepository
) : ViewModel() {

    private val _selectedMonth = MutableStateFlow(YearMonth.now())
    val selectedMonth: StateFlow<YearMonth> = _selectedMonth

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate

    private val _uiState = MutableStateFlow(TransactionUiState())
    val uiState: StateFlow<TransactionUiState> = _uiState

    init {
        observeMonthTransactions()
    }

    private fun observeMonthTransactions() {
        viewModelScope.launch {
            _selectedMonth
                .flatMapLatest { month ->
                    repository.getTransactionsForMonth(
                        startEpochDay = month.atDay(1),
                        endEpochDay = month.atEndOfMonth()
                    )
                }
                .collect { transactions ->
                    val grouped = transactions.groupBy {
                        LocalDate.ofEpochDay(it.dateEpochDay)
                    }

                    val income = transactions
                        .filter { it.type == TransactionType.INCOME }
                        .sumOf { it.amount }

                    val expense = transactions
                        .filter { it.type == TransactionType.EXPENSE }
                        .sumOf { it.amount }

                    _uiState.value = TransactionUiState(
                        transactionsByDate = grouped,
                        monthIncome = income,
                        monthExpense = expense,
                        monthNet = income - expense
                    )
                }
        }
    }

    fun onMonthSelected(date: LocalDate) {
        _selectedMonth.value = YearMonth.from(date)
        _selectedDate.value = date.withDayOfMonth(1)
    }

    fun onDateSelected(date: LocalDate) {
        _selectedDate.value = date
    }

    fun deleteTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction.id)
        }
    }

    fun updateTransaction(transaction: Transaction) {
        viewModelScope.launch {
            repository.updateTransaction(transaction)
        }

    }

    fun addTransaction(
        title: String,
        amount: Double,
        type: TransactionType
    ) {
        viewModelScope.launch {
            repository.addTransaction(
                Transaction(
                    title = title,
                    amount = amount,
                    type = type,
                    dateEpochDay = _selectedDate.value.toEpochDay()
                )
            )
        }
    }
}

