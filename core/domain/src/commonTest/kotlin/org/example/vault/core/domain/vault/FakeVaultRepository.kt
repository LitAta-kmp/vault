package org.example.vault.core.domain.vault

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

/** Тестовый репозиторий в памяти. Работает как настоящий: после изменений поток обновляется. */
class FakeVaultRepository : VaultRepository {

    private val entries = MutableStateFlow<List<VaultEntry>>(emptyList())

    /** Текущее содержимое для проверок в тестах. */
    val saved: List<VaultEntry> get() = entries.value

    override fun observeEntries(): Flow<List<VaultEntry>> = entries

    override suspend fun getEntry(id: String): VaultEntry? = entries.value.firstOrNull { it.id == id }

    override suspend fun saveEntry(entry: VaultEntry) {
        entries.update { list -> list.filterNot { it.id == entry.id } + entry }
    }

    override suspend fun deleteEntry(id: String) {
        entries.update { list -> list.filterNot { it.id == id } }
    }
}
