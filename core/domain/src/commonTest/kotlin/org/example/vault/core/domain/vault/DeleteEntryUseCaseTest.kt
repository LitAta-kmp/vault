package org.example.vault.core.domain.vault

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.test.runTest

class DeleteEntryUseCaseTest {

    private val repository = FakeVaultRepository()
    private val add = AddEntryUseCase(repository, FakeIdGenerator(), FakeTimeProvider())
    private val delete = DeleteEntryUseCase(repository)

    @Test
    fun delete_removes_only_the_given_entry() = runTest {
        add(EntryDraft(title = "A"))
        add(EntryDraft(title = "B"))

        delete("id-1")

        assertEquals(listOf("B"), repository.saved.map { it.title })
    }
}
