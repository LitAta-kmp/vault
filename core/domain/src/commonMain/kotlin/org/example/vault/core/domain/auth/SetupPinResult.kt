package org.example.vault.core.domain.auth

/** Все возможные исходы создания PIN. */
sealed interface SetupPinResult {
    data object Success : SetupPinResult
    data object InvalidFormat : SetupPinResult
    data object PinsDoNotMatch : SetupPinResult
}
