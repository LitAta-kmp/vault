package org.example.vault.core.domain.auth

/** Создание PIN: проверяет формат, сравнивает с подтверждением и только потом сохраняет. */
class SetupPinUseCase(
    private val repository: AuthRepository,
) {
    // when вместо цепочки return: порядок проверок читается сверху вниз
    suspend operator fun invoke(pin: String, confirmation: String): SetupPinResult = when {
        !PinPolicy.isValidFormat(pin) -> SetupPinResult.InvalidFormat
        pin != confirmation -> SetupPinResult.PinsDoNotMatch
        else -> {
            repository.savePin(pin)
            SetupPinResult.Success
        }
    }
}
