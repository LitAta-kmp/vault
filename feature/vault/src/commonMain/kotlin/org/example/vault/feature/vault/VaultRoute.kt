package org.example.vault.feature.vault

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

/** Точка входа фичи: связывает ViewModel с экраном. Куда переходить, решает вызывающий код. */
@Composable
fun VaultRoute(
    onNavigateToEditor: (entryId: String?) -> Unit,
    onNavigateToAuth: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: VaultViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val currentOnNavigateToEditor by rememberUpdatedState(onNavigateToEditor)
    val currentOnNavigateToAuth by rememberUpdatedState(onNavigateToAuth)

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is VaultEvent.NavigateToEditor -> currentOnNavigateToEditor(event.entryId)
                VaultEvent.NavigateToAuth -> currentOnNavigateToAuth()
            }
        }
    }

    VaultScreen(state = state, onAction = viewModel::onAction, modifier = modifier)
}
