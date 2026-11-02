package org.example.vault.feature.vault.editor

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/** Заглушка: настоящий редактор записи появится в следующем PR. */
@Composable
fun EntryEditorRoute(
    entryId: String?,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(text = if (entryId == null) "New entry (editor coming soon)" else "Edit $entryId (editor coming soon)")
    }
}
