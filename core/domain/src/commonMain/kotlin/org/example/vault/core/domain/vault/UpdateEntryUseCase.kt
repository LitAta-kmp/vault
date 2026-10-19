package org.example.vault.core.domain.vault

import org.example.vault.core.domain.common.TimeProvider

/** Изменение записи: время создания сохраняется, время изменения обновляется. */
class UpdateEntryUseCase(
    private val repository: VaultRepository,
    private val timeProvider: TimeProvider,
) {
    suspend operator fun invoke(id: String, draft: EntryDraft): UpdateEntryResult {
        val existing = repository.getEntry(id)

        return when {
            draft.title.isBlank() -> UpdateEntryResult.TitleIsBlank
            existing == null -> UpdateEntryResult.NotFound
            else -> {
                repository.saveEntry(
                    draft.toEntry(
                        id = existing.id,
                        createdAt = existing.createdAt,
                        updatedAt = timeProvider.nowMillis(),
                    ),
                )
                UpdateEntryResult.Success
            }
        }
    }
}
