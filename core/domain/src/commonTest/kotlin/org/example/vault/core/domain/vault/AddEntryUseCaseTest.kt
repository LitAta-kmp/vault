package org.example.vault.core.domain.vault

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

class AddEntryUseCaseTest {

    private val repository = FakeVaultRepository()
    private val useCase = AddEntryUseCase(
        repository = repository,
        idGenerator = FakeIdGenerator(),
        timeProvider = FakeTimeProvider(now = 1_000L),
    )

    @Test
    fun valid_draft_is_saved_with_generated_id_and_timestamps() = runTest {
        val result = useCase(EntryDraft(title = "Gmail", username = "me", password = "secret"))

        assertEquals(SaveEntryResult.Success, result)
        val entry = repository.saved.single()
        assertEquals("id-1", entry.id)
        assertEquals(1_000L, entry.createdAt)
        assertEquals(1_000L, entry.updatedAt)
    }

    @Test
    fun title_is_trimmed_before_saving() = runTest {
        useCase(EntryDraft(title = "  Gmail  "))

        assertEquals("Gmail", repository.saved.single().title)
    }

    @Test
    fun blank_title_is_rejected_and_nothing_is_saved() = runTest {
        val result = useCase(EntryDraft(title = "   "))

        assertEquals(SaveEntryResult.TitleIsBlank, result)
        assertTrue(repository.saved.isEmpty())
    }
}
