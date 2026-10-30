package org.example.vault.core.data.vault

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.example.vault.core.data.db.DatabaseSource
import org.example.vault.core.domain.vault.VaultEntry
import org.example.vault.core.domain.vault.VaultRepository

internal class SqlDelightVaultRepository(
    private val source: DatabaseSource,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : VaultRepository {

    override fun observeEntries(): Flow<List<VaultEntry>> = flow {
        // К базе обращаемся внутри flow: если хранилище заперто, ошибка придёт при подписке
        emitAll(
            source.database.entryQueries.selectAll()
                .asFlow()
                .mapToList(ioDispatcher)
                .map { rows -> rows.map { it.toDomain() } },
        )
    }

    override suspend fun getEntry(id: String): VaultEntry? = withContext(ioDispatcher) {
        source.database.entryQueries.selectById(id).executeAsOneOrNull()?.toDomain()
    }

    override suspend fun saveEntry(entry: VaultEntry) {
        withContext(ioDispatcher) {
            source.database.entryQueries.insertOrReplace(
                id = entry.id,
                title = entry.title,
                username = entry.username,
                password = entry.password,
                notes = entry.notes,
                createdAt = entry.createdAt,
                updatedAt = entry.updatedAt,
            )
        }
    }

    override suspend fun deleteEntry(id: String) {
        withContext(ioDispatcher) {
            source.database.entryQueries.deleteById(id)
        }
    }
}
