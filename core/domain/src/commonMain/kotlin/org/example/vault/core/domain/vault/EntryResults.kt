package org.example.vault.core.domain.vault

/** Исходы добавления записи. */
sealed interface SaveEntryResult {
    data object Success : SaveEntryResult
    data object TitleIsBlank : SaveEntryResult
}

/** Исходы изменения записи. */
sealed interface UpdateEntryResult {
    data object Success : UpdateEntryResult
    data object TitleIsBlank : UpdateEntryResult
    data object NotFound : UpdateEntryResult
}
