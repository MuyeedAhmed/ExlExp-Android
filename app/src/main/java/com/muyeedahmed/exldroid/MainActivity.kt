package com.muyeedahmed.exldroid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.muyeedahmed.exldroid.ui.navigation.AppNavHost
import com.muyeedahmed.exldroid.ui.theme.ExlDroidTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ExlDroidTheme {
                AppNavHost()
            }
        }
    }
}
