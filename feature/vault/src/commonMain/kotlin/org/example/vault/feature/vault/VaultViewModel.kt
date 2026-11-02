package org.example.vault.feature.vault

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import org.example.vault.core.domain.vault.DeleteEntryUseCase
import org.example.vault.core.domain.vault.ObserveEntriesUseCase
import org.example.vault.core.domain.vault.VaultLockedException
import org.example.vault.core.ui.mvi.BaseViewModel

class VaultViewModel(
    private val observeEntries: ObserveEntriesUseCase,
    private val deleteEntry: DeleteEntryUseCase,
) : BaseViewModel<VaultState, VaultAction, VaultEvent>(VaultState()) {

    init {
        startObserving()
    }

    override fun onAction(action: VaultAction) {
        when (action) {
            VaultAction.AddClicked -> sendEvent(VaultEvent.NavigateToEditor(entryId = null))
            is VaultAction.EntryClicked -> sendEvent(VaultEvent.NavigateToEditor(action.id))
            is VaultAction.DeleteClicked -> onDeleteClicked(action.id)
            VaultAction.DeleteDismissed -> updateState { copy(entryPendingDelete = null) }
            VaultAction.DeleteConfirmed -> onDeleteConfirmed()
        }
    }

    // Удаление не выполняем сразу: сначала просим подтверждение
    private fun onDeleteClicked(id: String) {
        val item = state.value.entries.firstOrNull { it.id == id } ?: return
        updateState { copy(entryPendingDelete = item) }
    }

    private fun startObserving() {
        viewModelScope.launch {
            observeEntries()
                .catch { cause ->
                    // Ловим только «хранилище заперто», остальные ошибки пробрасываем дальше
                    if (cause !is VaultLockedException) throw cause
                    sendEvent(VaultEvent.NavigateToAuth)
                }
                .collect { entries ->
                    updateState {
                        copy(isLoading = false, entries = entries.map { it.toItem() })
                    }
                }
        }
    }

    private fun onDeleteConfirmed() {
        val item = state.value.entryPendingDelete ?: return

        // Диалог закрываем сразу, не дожидаясь базы: быстрый повторный клик не запустит удаление дважды
        updateState { copy(entryPendingDelete = null) }
        viewModelScope.launch { deleteEntry(item.id) }
    }
}
