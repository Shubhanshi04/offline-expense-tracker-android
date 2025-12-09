package com.shubhanshi.smartexpense

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.shubhanshi.smartexpense.ui.theme.SmartExpenseTheme
import androidx.lifecycle.lifecycleScope
import com.shubhanshi.smartexpense.data.local.AppDatabase
import com.shubhanshi.smartexpense.data.local.TransactionEntity
import com.shubhanshi.smartexpense.domain.model.TransactionType
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            val db = AppDatabase.getInstance(applicationContext)

            db.transactionDao().insertTransaction(
                TransactionEntity(
                    title = "Test Expense",
                    amount = 100.0,
                    dateEpochDay = 1L,
                    type = TransactionType.EXPENSE
                )
            )
        }
    }

}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SmartExpenseTheme {
        Greeting("Android")
    }
}