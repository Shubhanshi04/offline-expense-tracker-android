package com.shubhanshi.smartexpense.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shubhanshi.smartexpense.domain.model.Transaction
import com.shubhanshi.smartexpense.domain.model.TransactionType

@Composable
fun TransactionRow(transaction: Transaction, onDelete: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = transaction.title, style = MaterialTheme.typography.titleMedium)
            val label = if (transaction.type == TransactionType.EXPENSE) "Expense" else "Income"
            Text(
                text = "$label • ₹${transaction.amount}",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        // simple delete button; feel free to replace with swipe to delete
        TextButton(onClick = onDelete) {
            Text("Delete")
        }
    }
}