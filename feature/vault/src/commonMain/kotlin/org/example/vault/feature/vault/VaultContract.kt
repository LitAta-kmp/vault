package org.example.vault.feature.vault

import org.example.vault.core.ui.mvi.UiAction
import org.example.vault.core.ui.mvi.UiEvent
import org.example.vault.core.ui.mvi.UiState

/** Строка списка. Пароля и заметок здесь нет специально: списку они не нужны. */
data class EntryItem(
    val id: String,
    val title: String,
    val username: String,
)

data class VaultState(
    val isLoading: Boolean = true,
    val entries: List<EntryItem> = emptyList(),
    // Запись, удаление которой ждёт подтверждения (для диалога)
    val entryPendingDelete: EntryItem? = null,
) : UiState

sealed interface VaultAction : UiAction {
    data object AddClicked : VaultAction
    data class EntryClicked(val id: String) : VaultAction
    data class DeleteClicked(val id: String) : VaultAction
    data object DeleteConfirmed : VaultAction
    data object DeleteDismissed : VaultAction
}

sealed interface VaultEvent : UiEvent {
    /** [entryId] равен null, когда создаётся новая запись. */
    data class NavigateToEditor(val entryId: String?) : VaultEvent
    data object NavigateToAuth : VaultEvent
}
