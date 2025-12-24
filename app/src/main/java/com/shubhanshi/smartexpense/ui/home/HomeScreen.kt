package com.shubhanshi.smartexpense.ui.home

import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.shubhanshi.smartexpense.domain.model.TransactionType
import com.shubhanshi.smartexpense.ui.components.MonthPickerDialog
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
    val selectedDate by viewModel.selectedDate.collectAsState()
    var month by remember { mutableStateOf(selectedDate.month) }
    var title by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(TransactionType.EXPENSE) }
    var showMonthPicker by remember { mutableStateOf(false) }
    var currentMonth by remember {
        mutableStateOf(YearMonth.now())
    }

    Scaffold(
        topBar = { TopAppBar(title = {
            Text("SmartExpense",
                fontWeight = FontWeight.Bold)}) },
    ) { padding ->
        Column(modifier = Modifier
            .padding(padding)
            .navigationBarsPadding()
            .padding(20.dp)
            .fillMaxSize()) {
            MonthHeader(
                selectedDate = selectedDate,
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
                selectedDate = selectedDate,
                onDateSelected = { clickedDate ->
                    viewModel.onDateSelected(clickedDate)
                }
            )

            Spacer(Modifier.height(8.dp))

        }
    }
}

@Composable
fun MonthHeader(
    selectedDate: LocalDate,
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
            text = selectedDate.format(formatter),
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
    modifier: Modifier,
    selectedDate: LocalDate,
    onDateSelected: (LocalDate) -> Unit
) {
    val year = selectedDate.year
    val month = selectedDate.month

    val firstDayOfMonth = LocalDate.of(year, month, 1)

    // ISO: Monday = 1, Sunday = 7
    val startOffset = firstDayOfMonth.dayOfWeek.value - 1

    val daysInMonth = selectedDate.lengthOfMonth()

    LazyVerticalGrid(
        columns = GridCells.Fixed(7),
        modifier = Modifier.fillMaxSize()
    ) {

        // 1️⃣ Empty cells before day 1
        items(startOffset) {
            Box(modifier = Modifier.size(40.dp))
        }

        // 2️⃣ Actual days
        items(daysInMonth) { index ->
            val day = index + 1
            val date = LocalDate.of(year, month, day)
            val isSelected = date == selectedDate

            DayCell(
                day = day,
                isSelected = isSelected,
                onClick = { onDateSelected(date) }
            )
        }
    }
}


@Composable
fun DayCell(
    day: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(4.dp)
            .fillMaxSize()
            .clip(MaterialTheme.shapes.medium)
            .background(
                if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                else Color.Transparent
            )
            .clickable { onClick() }
            .padding(6.dp)
    ) {
        // Date number
        Text(
            text = day.toString(),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = if (isSelected)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(4.dp))

        // 🔽 Placeholder for transactions
        // Later this will be a LazyColumn
        Text(
            text = "",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray,
            maxLines = 2
        )
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


