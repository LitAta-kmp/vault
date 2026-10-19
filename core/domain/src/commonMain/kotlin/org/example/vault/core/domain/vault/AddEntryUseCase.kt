package org.example.vault.core.domain.vault

import org.example.vault.core.domain.common.IdGenerator
import org.example.vault.core.domain.common.TimeProvider

/** Добавление записи: проверяет заголовок, присваивает id и время, сохраняет. */
class AddEntryUseCase(
    private val repository: VaultRepository,
    private val idGenerator: IdGenerator,
    private val timeProvider: TimeProvider,
) {
    suspend operator fun invoke(draft: EntryDraft): SaveEntryResult {
        if (draft.title.isBlank()) return SaveEntryResult.TitleIsBlank

        val now = timeProvider.nowMillis()
        repository.saveEntry(draft.toEntry(id = idGenerator.newId(), createdAt = now, updatedAt = now))
        return SaveEntryResult.Success
    }
}
