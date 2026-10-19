package org.example.vault.core.domain.vault

import kotlinx.coroutines.flow.Flow

/** Поток списка записей для экрана хранилища. */
class ObserveEntriesUseCase(
    private val repository: VaultRepository,
) {
    operator fun invoke(): Flow<List<VaultEntry>> = repository.observeEntries()
}
