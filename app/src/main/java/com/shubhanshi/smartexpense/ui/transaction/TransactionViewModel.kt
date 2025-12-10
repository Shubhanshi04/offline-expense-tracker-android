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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import java.time.LocalDate

class TransactionViewModel(
    private val repository: TransactionRepository
): ViewModel() {
    // Which calendar day is selected in the UI
    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate : StateFlow<LocalDate> = _selectedDate

    // Whole UI state
    private val _uiState = MutableStateFlow(
        TransactionUiState(
            _selectedDate.value,
            isLoading = true
        )
    )
    val uiState : StateFlow<TransactionUiState> = _uiState

    init{
        transactionForSelectedDate()
    }

    private fun transactionForSelectedDate() {
        viewModelScope.launch {
            selectedDate.collectLatest { date ->
                repository.getTransactionForDate(date.toEpochDay())
                    .collect { transactions ->
                        val income = transactions
                            .filter { it.type == TransactionType.INCOME }
                            .sumOf { it.amount }

                        val expense = transactions
                            .filter { it.type == TransactionType.EXPENSE }
                            .sumOf { it.amount }

                        _uiState.value = _uiState.value.copy(
                            selectedDate = date,
                            transactions = transactions,
                            totalIncome = income,
                            totalExpense = expense,
                            isLoading = false
                        )
                    }
            }


        }
    }

    fun onDateSelected(date: LocalDate) {
        _uiState.value = _uiState.value.copy(
            selectedDate = date,
            isLoading = true
        )
        _selectedDate.value = date
    }

    fun addTransaction(
        title : String,
        amount : Double,
        type : TransactionType
    ) {

        if(amount <= 0.0) {
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

    fun clearTransaction(id:Long) {
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