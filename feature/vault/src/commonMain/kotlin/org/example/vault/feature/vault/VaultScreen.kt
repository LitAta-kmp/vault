package org.example.vault.feature.vault

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/** Stateless-экран списка: рисует State и сообщает о действиях пользователя. */
@Composable
internal fun VaultScreen(
    state: VaultState,
    onAction: (VaultAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(onClick = { onAction(VaultAction.AddClicked) }) {
                Text(text = "+", style = MaterialTheme.typography.headlineSmall)
            }
        },
    ) { padding ->
        // Три состояния списка выводим из State: загрузка, пусто, есть данные
        when {
            state.isLoading -> CenteredBox(padding) { CircularProgressIndicator() }
            state.entries.isEmpty() -> CenteredBox(padding) { Text("No entries yet. Tap + to add one.") }
            else -> EntryList(entries = state.entries, onAction = onAction, padding = padding)
        }
    }

    state.entryPendingDelete?.let { item ->
        DeleteConfirmationDialog(item = item, onAction = onAction)
    }
}

@Composable
private fun CenteredBox(
    padding: PaddingValues,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize().padding(padding),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}

@Composable
private fun EntryList(
    entries: List<EntryItem>,
    onAction: (VaultAction) -> Unit,
    padding: PaddingValues,
) {
    LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)) {
        // key = id: Compose отличает записи друг от друга при добавлении и удалении
        items(items = entries, key = { it.id }) { item ->
            ListItem(
                headlineContent = { Text(item.title) },
                supportingContent = if (item.username.isBlank()) null else { { Text(item.username) } },
                trailingContent = {
                    TextButton(onClick = { onAction(VaultAction.DeleteClicked(item.id)) }) {
                        Text("Delete")
                    }
                },
                modifier = Modifier.clickable { onAction(VaultAction.EntryClicked(item.id)) },
            )
        }
    }
}

@Composable
private fun DeleteConfirmationDialog(
    item: EntryItem,
    onAction: (VaultAction) -> Unit,
) {
    AlertDialog(
        onDismissRequest = { onAction(VaultAction.DeleteDismissed) },
        title = { Text("Delete entry?") },
        text = { Text("\"${item.title}\" will be permanently deleted.") },
        confirmButton = {
            TextButton(onClick = { onAction(VaultAction.DeleteConfirmed) }) { Text("Delete") }
        },
        dismissButton = {
            TextButton(onClick = { onAction(VaultAction.DeleteDismissed) }) { Text("Cancel") }
        },
    )
}

@Preview
@Composable
internal fun VaultScreenListPreview() {
    MaterialTheme {
        VaultScreen(
            state = VaultState(
                isLoading = false,
                entries = listOf(
                    EntryItem("1", "Gmail", "me@example.com"),
                    EntryItem("2", "GitHub", "litata"),
                ),
            ),
            onAction = {},
        )
    }
}

@Preview
@Composable
internal fun VaultScreenEmptyPreview() {
    MaterialTheme {
        VaultScreen(state = VaultState(isLoading = false), onAction = {})
    }
}
