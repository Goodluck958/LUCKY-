package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.ui.OmniApp
import com.example.ui.OmniViewModel
import com.example.ui.theme.ObsidianBg
import com.example.ui.theme.OmniTheme

class MainActivity : ComponentActivity() {
    private val viewModel: OmniViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            OmniTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = ObsidianBg
                ) {
                    OmniApp(viewModel = viewModel)
                }
            }
        }
    }
}

