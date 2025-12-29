package com.shubhanshi.smartexpense.ui.transaction

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shubhanshi.smartexpense.domain.model.Transaction
import com.shubhanshi.smartexpense.domain.model.TransactionType
import com.shubhanshi.smartexpense.ui.components.TransactionRow
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionBottomSheet(
    selectedDate: LocalDate,
    transactions: List<Transaction>,
    onAddTransaction: (String, Double, TransactionType) -> Unit,
    onDeleteTransaction: (Transaction) -> Unit,
    onEditTransaction: (Transaction) -> Unit,
    onDismiss: () -> Unit
) {

    var editingTransaction by remember { mutableStateOf<Transaction?>(null) }
    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(TransactionType.EXPENSE) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // 📅 Header
        Text(
            text = selectedDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy")),
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(Modifier.height(12.dp))

        // 🧾 Existing transactions
        if (transactions.isEmpty()) {
            Text(
                text = "No transactions yet",
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            LazyColumn {
                items(transactions) { transaction ->
                    TransactionRow(
                        transaction = transaction,
                        onDelete = { onDeleteTransaction(transaction) },
                        onEdit = {
                            editingTransaction = transaction
                            title = transaction.title
                            amount = transaction.amount.toString()
                            selectedType = transaction.type

                        }
                    )
                }
            }
        }

        Divider(Modifier.padding(vertical = 12.dp))

        // ➕ Add new transaction
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = amount,
            onValueChange = { amount = it },
            label = { Text("Amount") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = selectedType == TransactionType.EXPENSE,
                onClick = { selectedType = TransactionType.EXPENSE },
                label = { Text("Expense") }
            )
            FilterChip(
                selected = selectedType == TransactionType.INCOME,
                onClick = { selectedType = TransactionType.INCOME },
                label = { Text("Income") }
            )
        }

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = {
                val amt = amount.toDoubleOrNull() ?: return@Button

                if (editingTransaction == null) {
                    // ➕ ADD
                    onAddTransaction(title, amt, selectedType)
                } else {
                    // ✏️ EDIT
                    onEditTransaction(
                        editingTransaction!!.copy(
                            title = title,
                            amount = amt,
                            type = selectedType
                        )
                    )
                }

                // reset
                editingTransaction = null
                title = ""
                amount = ""
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                if (editingTransaction == null) "Add Transaction"
                else "Update Transaction"
            )
        }

        Spacer(Modifier.height(8.dp))

        TextButton(
            onClick = onDismiss,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Close")
        }
    }
}
