package com.shubhanshi.smartexpense.ui.transaction

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.shubhanshi.smartexpense.data.local.AppDatabase
import com.shubhanshi.smartexpense.domain.repository.TransactionRepository
import com.shubhanshi.smartexpense.domain.repository.TransactionRepositoryImpl

class TransactionViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionViewModel::class.java)) {
            val db = AppDatabase.getInstance(context)
            val dao = db.transactionDao()
            val repo: TransactionRepository = TransactionRepositoryImpl(dao)
            @Suppress("UNCHECKED_CAST")
            return TransactionViewModel(repo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
    }
}