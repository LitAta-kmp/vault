package org.example.vault.core.data.auth

import org.example.vault.core.domain.auth.AuthRepository

/**
 * ВРЕМЕННАЯ реализация: PIN хранится в памяти и пропадает при перезапуске.
 * Будет заменена реализацией на SQLCipher на шаге с хранилищем.
 */
internal class InMemoryAuthRepository : AuthRepository {

    private var pin: String? = null

    override suspend fun isPinSet(): Boolean = pin != null

    override suspend fun savePin(pin: String) {
        this.pin = pin
    }

    override suspend fun verifyPin(pin: String): Boolean = this.pin == pin
}
