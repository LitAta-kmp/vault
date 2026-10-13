package org.example.vault

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import org.example.vault.navigation.AppNavHost

@Composable
fun App() {
    MaterialTheme {
        AppNavHost()
    }
}
