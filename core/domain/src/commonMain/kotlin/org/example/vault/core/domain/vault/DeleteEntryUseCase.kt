package org.example.vault.core.domain.vault

/** Удаление записи. Удалить несуществующую запись не ошибка: результат тот же, записи нет. */
class DeleteEntryUseCase(
    private val repository: VaultRepository,
) {
    suspend operator fun invoke(id: String) = repository.deleteEntry(id)
}
