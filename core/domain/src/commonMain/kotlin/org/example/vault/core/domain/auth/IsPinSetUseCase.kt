package org.example.vault.core.domain.auth

/** Нужен, чтобы при запуске понять: показывать создание PIN или обычный вход. */
class IsPinSetUseCase(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke(): Boolean = repository.isPinSet()
}
