package com.shubhanshi.smartexpense.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.shubhanshi.smartexpense.domain.model.Transaction
import com.shubhanshi.smartexpense.domain.model.TransactionType
import com.shubhanshi.smartexpense.ui.components.MonthPickerDialog
import com.shubhanshi.smartexpense.ui.transaction.AddTransactionBottomSheet
import com.shubhanshi.smartexpense.ui.transaction.TransactionUiState
import com.shubhanshi.smartexpense.ui.transaction.TransactionViewModel
import com.shubhanshi.smartexpense.ui.transaction.TransactionViewModelFactory
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarTransactionScreen() {
    val context = LocalContext.current
    val viewModel: TransactionViewModel = viewModel(factory = TransactionViewModelFactory(context))
    val uiState by viewModel.uiState.collectAsState()
    var title by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    val month by viewModel.selectedMonth.collectAsState()
    var selectedType by remember { mutableStateOf(TransactionType.EXPENSE) }
    var showMonthPicker by remember { mutableStateOf(false) }
    var showTransactionSheet by remember { mutableStateOf(false)}
    val selectedMonth by viewModel.selectedMonth.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val transactionsByDate = uiState.transactionsByDate
    val transactionsForSelectedDate = uiState.transactionsByDate[selectedDate].orEmpty()

    Scaffold(
        topBar = { TopAppBar(title = {
            Text("Smart Expense",
                fontWeight = FontWeight.Bold)}) },
    ) { padding ->
        Column(modifier = Modifier
            .padding(padding)
            .navigationBarsPadding()
            .padding(20.dp)
            .fillMaxSize()) {
            MonthHeader(
                yearMonth = selectedMonth,
                onPrevMonth = {
                    viewModel.onMonthSelected(selectedDate.minusMonths(1))
                },
                onNextMonth = {
                    viewModel.onMonthSelected(selectedDate.plusMonths(1))
                },
                onHeaderClick = { showMonthPicker = true },
            )

            if (showMonthPicker) {
                MonthPickerDialog(
                    selectedDate = selectedDate,
                    onMonthSelected = { newDate ->
                        viewModel.onMonthSelected(newDate)
                        showMonthPicker = false
                    },
                    onDismiss = { showMonthPicker = false }
                )
            }

            Spacer(Modifier.height(8.dp))

            SelectSummary()

            Spacer(Modifier.height(10.dp))
            MonthlyTransactionSummary(uiState)

            Spacer(modifier = Modifier.height(16.dp))

            DaysOfWeek()
            Spacer(modifier = Modifier.height(8.dp))

            CalendarGrid(
                modifier = Modifier.weight(1f),
                yearMonth = month,
                transactionsByDate = transactionsByDate,
                onDateClick = { clickedDate ->
                    viewModel.onDateSelected(clickedDate)
                    showTransactionSheet = true
                }
            )

            if (showTransactionSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showTransactionSheet = false }
                ) {
                    AddTransactionBottomSheet(
                        selectedDate = selectedDate,
                        transactions = uiState.transactionsByDate[selectedDate].orEmpty(),
                        onAddTransaction = { title, amount, type ->
                            viewModel.addTransaction(title, amount, type)
                        },
                        onDeleteTransaction = { transaction ->
                            viewModel.deleteTransaction(transaction)
                        },
                        onDismiss = { showTransactionSheet = false }
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

        }
    }
}

@Composable
fun MonthHeader(
    yearMonth: YearMonth,
    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onHeaderClick: () -> Unit,
) {
    val formatter = DateTimeFormatter.ofPattern("MMMM yyyy")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        IconButton(onClick = onPrevMonth) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Previous Month")
        }

        Text(
            text = yearMonth.format(formatter),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable { onHeaderClick() }
        )

        IconButton(onClick = onNextMonth) {
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Next Month")
        }

    }

}

@Composable
fun CalendarGrid(
    yearMonth: YearMonth,
    transactionsByDate: Map<LocalDate, List<Transaction>>,
    onDateClick: (LocalDate) -> Unit,
    modifier: Modifier
) {
    val firstDay = yearMonth.atDay(1)
    val startOffset = firstDay.dayOfWeek.value - 1
    val daysInMonth = yearMonth.lengthOfMonth()

    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        modifier = Modifier.fillMaxWidth()
    ) {

        items(startOffset) {
            Spacer(modifier = Modifier.height(80.dp))
        }

        items(daysInMonth) { index ->
            val day = index + 1
            val date = yearMonth.atDay(day)

            DayCell(
                date = date,
                transactions = transactionsByDate[date].orEmpty(),
                onClick = { onDateClick(date) }
            )
        }
    }
}

@Composable
fun DayCell(
    date: LocalDate,
    transactions: List<Transaction>,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxWidth()
            .heightIn(min = 80.dp)
            .clickable { onClick() }
    ) {
        Text(
            text = date.dayOfMonth.toString(),
            fontWeight = FontWeight.Bold
        )

        transactions.forEach {
            Text(
                text = "₹${it.amount}",
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
            )
        }

        if (transactions.size > 4) {
            Text(
                text = "+${transactions.size - 4} more",
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}



@Composable
fun MonthlyTransactionSummary(uiState: TransactionUiState) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.Start) {
            Text(text = "Income", style = MaterialTheme.typography.bodySmall)
            Text(text = "₹${uiState.monthIncome}",
                 style = MaterialTheme.typography.titleMedium,
                 color = Color.Blue)
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Exp.", style = MaterialTheme.typography.bodySmall)
            Text(text = "₹${uiState.monthExpense}",
                 style = MaterialTheme.typography.titleMedium,
                 color = Color.Red)
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(text = "Total", style = MaterialTheme.typography.bodySmall)
            Text(
                text = "₹${uiState.monthNet}",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}

@Composable
fun SelectSummary() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Calendar",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .clickable{}
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Summary",
                 style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .clickable{})
        }
    }
}

@Composable
fun DaysOfWeek() {
    val days = listOf("Mon", "Tue", "Wed", "Thur", "Fri", "Sat", "Sun")

    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 4.dp)){
        days.forEach { day->
            Text(
                text = day,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
        }

    }
}


