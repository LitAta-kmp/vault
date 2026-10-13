package org.example.vault.feature.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

/**
 * Единственная публичная точка входа фичи.
 * Связывает ViewModel с экраном, сама ничего не рисует и про навигацию ничего не знает.
 */
@Composable
fun AuthRoute(
    onNavigateToVault: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    // Колбэк может смениться, пока LaunchedEffect живёт: всегда вызываем актуальный
    val currentOnNavigateToVault by rememberUpdatedState(onNavigateToVault)

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                AuthEvent.NavigateToVault -> currentOnNavigateToVault()
            }
        }
    }

    AuthScreen(
        state = state,
        onAction = viewModel::onAction,
        modifier = modifier,
    )
}
