package org.example.vault.feature.vault

import app.cash.turbine.test
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.example.vault.core.domain.vault.DeleteEntryUseCase
import org.example.vault.core.domain.vault.ObserveEntriesUseCase
import org.example.vault.core.domain.vault.VaultEntry
import org.example.vault.core.domain.vault.VaultRepository

@OptIn(ExperimentalCoroutinesApi::class)
class VaultViewModelTest {

    private val repository = FakeVaultRepository()

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(source: VaultRepository = repository) = VaultViewModel(
        observeEntries = ObserveEntriesUseCase(source),
        deleteEntry = DeleteEntryUseCase(source),
    )

    private fun entry(id: String, title: String = "Gmail") = VaultEntry(
        id = id,
        title = title,
        username = "me",
        password = "secret",
        notes = "note",
        createdAt = 1L,
        updatedAt = 1L,
    )

    @Test
    fun empty_vault_stops_loading_and_shows_empty_list() {
        val viewModel = createViewModel()

        assertEquals(false, viewModel.state.value.isLoading)
        assertTrue(viewModel.state.value.entries.isEmpty())
    }

    @Test
    fun entries_are_shown_as_items_without_secrets() = runTest {
        repository.saveEntry(entry("1", "Gmail"))

        val viewModel = createViewModel()

        assertEquals(listOf(EntryItem("1", "Gmail", "me")), viewModel.state.value.entries)
    }

    @Test
    fun list_updates_when_repository_changes() = runTest {
        val viewModel = createViewModel()

        repository.saveEntry(entry("1"))
        repository.saveEntry(entry("2"))

        assertEquals(2, viewModel.state.value.entries.size)
    }

    @Test
    fun locked_vault_navigates_to_auth() = runTest {
        // Событие уходит в init, до подписки теста: Channel его сохраняет
        val viewModel = createViewModel(source = LockedVaultRepository())

        viewModel.events.test {
            assertEquals(VaultEvent.NavigateToAuth, awaitItem())
        }
    }

    @Test
    fun add_click_opens_editor_without_id() = runTest {
        val viewModel = createViewModel()

        viewModel.events.test {
            viewModel.onAction(VaultAction.AddClicked)
            assertEquals(VaultEvent.NavigateToEditor(entryId = null), awaitItem())
        }
    }

    @Test
    fun entry_click_opens_editor_with_id() = runTest {
        val viewModel = createViewModel()

        viewModel.events.test {
            viewModel.onAction(VaultAction.EntryClicked("42"))
            assertEquals(VaultEvent.NavigateToEditor(entryId = "42"), awaitItem())
        }
    }

    @Test
    fun delete_click_asks_for_confirmation_and_keeps_entry() = runTest {
        repository.saveEntry(entry("1"))
        val viewModel = createViewModel()

        viewModel.onAction(VaultAction.DeleteClicked("1"))

        assertEquals("1", viewModel.state.value.entryPendingDelete?.id)
        assertEquals(1, repository.saved.size)
    }

    @Test
    fun confirmed_delete_removes_entry_and_closes_dialog() = runTest {
        repository.saveEntry(entry("1"))
        val viewModel = createViewModel()
        viewModel.onAction(VaultAction.DeleteClicked("1"))

        viewModel.onAction(VaultAction.DeleteConfirmed)

        assertTrue(repository.saved.isEmpty())
        assertNull(viewModel.state.value.entryPendingDelete)
    }

    @Test
    fun dismissed_delete_keeps_entry_and_closes_dialog() = runTest {
        repository.saveEntry(entry("1"))
        val viewModel = createViewModel()
        viewModel.onAction(VaultAction.DeleteClicked("1"))

        viewModel.onAction(VaultAction.DeleteDismissed)

        assertEquals(1, repository.saved.size)
        assertNull(viewModel.state.value.entryPendingDelete)
    }

    @Test
    fun confirmed_delete_without_pending_entry_does_nothing() = runTest {
        repository.saveEntry(entry("1"))
        val viewModel = createViewModel()

        viewModel.onAction(VaultAction.DeleteConfirmed)

        assertEquals(1, repository.saved.size)
    }
}
