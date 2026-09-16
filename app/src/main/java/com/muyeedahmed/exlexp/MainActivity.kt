package com.muyeedahmed.exlexp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.muyeedahmed.exlexp.ui.navigation.AppNavHost
import com.muyeedahmed.exlexp.ui.theme.ExlExpTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ExlExpTheme {
                AppNavHost()
            }
        }
    }
}
