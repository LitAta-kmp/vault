package org.example.vault.core.domain.auth

/** Правила, каким должен быть PIN. Единое место: UI и use case'ы берут правила отсюда. */
object PinPolicy {
    const val PIN_LENGTH = 6

    fun isValidFormat(pin: String): Boolean =
        pin.length == PIN_LENGTH && pin.all { it.isDigit() }
}
