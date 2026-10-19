package org.example.vault.core.domain.vault

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

class UpdateEntryUseCaseTest {

    private val repository = FakeVaultRepository()
    private val timeProvider = FakeTimeProvider(now = 1_000L)
    private val add = AddEntryUseCase(repository, FakeIdGenerator(), timeProvider)
    private val update = UpdateEntryUseCase(repository, timeProvider)

    @Test
    fun update_changes_fields_keeps_created_at_and_bumps_updated_at() = runTest {
        add(EntryDraft(title = "Gmail", password = "old"))
        timeProvider.now = 2_000L

        val result = update("id-1", EntryDraft(title = "Gmail work", password = "new"))

        assertEquals(UpdateEntryResult.Success, result)
        val entry = repository.saved.single()
        assertEquals("Gmail work", entry.title)
        assertEquals("new", entry.password)
        assertEquals(1_000L, entry.createdAt)
        assertEquals(2_000L, entry.updatedAt)
    }

    @Test
    fun update_of_missing_entry_returns_not_found() = runTest {
        val result = update("missing", EntryDraft(title = "Gmail"))

        assertEquals(UpdateEntryResult.NotFound, result)
        assertTrue(repository.saved.isEmpty())
    }

    @Test
    fun update_with_blank_title_is_rejected_and_entry_is_unchanged() = runTest {
        add(EntryDraft(title = "Gmail"))

        val result = update("id-1", EntryDraft(title = " "))

        assertEquals(UpdateEntryResult.TitleIsBlank, result)
        assertEquals("Gmail", repository.saved.single().title)
    }
}
