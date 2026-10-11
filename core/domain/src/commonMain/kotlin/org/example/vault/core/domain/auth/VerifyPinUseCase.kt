package org.example.vault.core.domain.auth

/** Проверка PIN при входе. Заведомо некорректный PIN даже не доходит до репозитория. */
class VerifyPinUseCase(
    private val repository: AuthRepository,
) {
    suspend operator fun invoke(pin: String): Boolean =
        PinPolicy.isValidFormat(pin) && repository.verifyPin(pin)
}
