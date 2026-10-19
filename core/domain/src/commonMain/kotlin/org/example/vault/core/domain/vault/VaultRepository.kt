package org.example.vault.core.domain.vault

import kotlinx.coroutines.flow.Flow

/** Контракт хранилища записей. Реализация (SQLDelight + SQLCipher) живёт в core:data. */
interface VaultRepository {

    /** Список обновляется при любом изменении. Если хранилище заперто, поток завершится [VaultLockedException]. */
    fun observeEntries(): Flow<List<VaultEntry>>

    suspend fun getEntry(id: String): VaultEntry?

    /** Добавляет запись или заменяет существующую с тем же id. */
    suspend fun saveEntry(entry: VaultEntry)

    suspend fun deleteEntry(id: String)
}

/** Хранилище недоступно, потому что не введён PIN. Это нарушенное предусловие, а не обычный исход. */
class VaultLockedException : IllegalStateException("Vault is locked")
