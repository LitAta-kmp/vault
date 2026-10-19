package org.example.vault.core.domain.vault

/** Получение одной записи (например, для экрана редактирования). */
class GetEntryUseCase(
    private val repository: VaultRepository,
) {
    suspend operator fun invoke(id: String): VaultEntry? = repository.getEntry(id)
}
