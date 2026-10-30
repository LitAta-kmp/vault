package org.example.vault.core.data.vault

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import app.cash.turbine.test
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.example.vault.core.data.db.DatabaseSource
import org.example.vault.core.data.db.VaultDatabase
import org.example.vault.core.domain.vault.VaultEntry
import org.example.vault.core.domain.vault.VaultLockedException

private class OpenSource(override val database: VaultDatabase) : DatabaseSource

private object LockedSource : DatabaseSource {
    override val database: VaultDatabase get() = throw VaultLockedException()
}

@OptIn(ExperimentalCoroutinesApi::class)
class SqlDelightVaultRepositoryTest {

    // Обычный SQLite в памяти: без шифрования, но SQL и маппинг проверяются по-настоящему
    private fun TestScope.createRepository(): SqlDelightVaultRepository {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        VaultDatabase.Schema.create(driver)
        return SqlDelightVaultRepository(
            source = OpenSource(VaultDatabase(driver)),
            ioDispatcher = UnconfinedTestDispatcher(testScheduler),
        )
    }

    private fun entry(id: String, title: String = "Gmail", updatedAt: Long = 1L) = VaultEntry(
        id = id,
        title = title,
        username = "me",
        password = "secret",
        notes = "note",
        createdAt = 1L,
        updatedAt = updatedAt,
    )

    @Test
    fun saved_entry_can_be_read_back_with_all_fields() = runTest {
        val repository = createRepository()
        val original = entry(id = "1")

        repository.saveEntry(original)

        assertEquals(original, repository.getEntry("1"))
    }

    @Test
    fun missing_entry_is_returned_as_null() = runTest {
        val repository = createRepository()

        assertNull(repository.getEntry("missing"))
    }

    @Test
    fun saving_same_id_replaces_the_entry() = runTest {
        val repository = createRepository()
        repository.saveEntry(entry(id = "1", title = "Old"))

        repository.saveEntry(entry(id = "1", title = "New"))

        assertEquals("New", repository.getEntry("1")?.title)
    }

    @Test
    fun deleted_entry_is_gone() = runTest {
        val repository = createRepository()
        repository.saveEntry(entry(id = "1"))

        repository.deleteEntry("1")

        assertNull(repository.getEntry("1"))
    }

    @Test
    fun observed_list_updates_and_puts_recently_updated_first() = runTest {
        val repository = createRepository()

        repository.observeEntries().test {
            assertEquals(emptyList(), awaitItem())

            repository.saveEntry(entry(id = "1", updatedAt = 10L))
            assertEquals(listOf("1"), awaitItem().map { it.id })

            repository.saveEntry(entry(id = "2", updatedAt = 20L))
            assertEquals(listOf("2", "1"), awaitItem().map { it.id })

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun observing_a_locked_vault_fails_with_vault_locked_exception() = runTest {
        val repository = SqlDelightVaultRepository(
            source = LockedSource,
            ioDispatcher = UnconfinedTestDispatcher(testScheduler),
        )

        repository.observeEntries().test {
            assertIs<VaultLockedException>(awaitError())
        }
    }
}
