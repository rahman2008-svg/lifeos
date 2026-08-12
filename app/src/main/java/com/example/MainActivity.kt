package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.ui.LifeOSApp
import com.example.viewmodel.LifeOSViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val viewModelFactory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return LifeOSViewModel(application) as T
            }
        }

        val viewModel = ViewModelProvider(this, viewModelFactory)[LifeOSViewModel::class.java]

        setContent {
            LifeOSApp(viewModel = viewModel)
        }
    }
}
