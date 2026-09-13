package com.example.okdriverpanicbutton

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.example.okdriverpanicbutton.data.HistoryRepository
import com.example.okdriverpanicbutton.navigation.AppNavGraph
import com.example.okdriverpanicbutton.ui.theme.OkDriverPanicButtonTheme
import com.example.okdriverpanicbutton.viewmodel.PanicViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val historyRepository = HistoryRepository(this)
        val contactRepository = com.example.okdriverpanicbutton.data.ContactRepository(this)
        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(PanicViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return PanicViewModel(historyRepository, contactRepository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
        val panicViewModel = ViewModelProvider(this, factory)[PanicViewModel::class.java]
        setContent {
            OkDriverPanicButtonTheme {
                val navController = rememberNavController()
                AppNavGraph(
                    navController = navController,
                    panicViewModel = panicViewModel
                )
            }
        }
    }
}