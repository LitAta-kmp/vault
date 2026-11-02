package org.example.vault.feature.vault

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import org.example.vault.core.domain.vault.VaultEntry
import org.example.vault.core.domain.vault.VaultLockedException
import org.example.vault.core.domain.vault.VaultRepository

/** Тестовый репозиторий в памяти (копия из core:domain: тесты разных модулей друг друга не видят). */
class FakeVaultRepository : VaultRepository {

    private val entries = MutableStateFlow<List<VaultEntry>>(emptyList())

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

/** Репозиторий запертого хранилища: поток сразу падает, как в контракте. */
class LockedVaultRepository : VaultRepository {
    override fun observeEntries(): Flow<List<VaultEntry>> = flow { throw VaultLockedException() }
    override suspend fun getEntry(id: String): VaultEntry? = throw VaultLockedException()
    override suspend fun saveEntry(entry: VaultEntry) = throw VaultLockedException()
    override suspend fun deleteEntry(id: String) = throw VaultLockedException()
}
