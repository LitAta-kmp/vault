package org.example.vault.feature.auth

import org.example.vault.core.domain.auth.AuthRepository

/** Тестовый репозиторий в памяти. Такой же есть в core:domain, но тесты других модулей его не видят. */
class FakeAuthRepository : AuthRepository {

    var savedPin: String? = null
        private set

    override suspend fun isPinSet(): Boolean = savedPin != null

    override suspend fun savePin(pin: String) {
        savedPin = pin
    }

    override suspend fun verifyPin(pin: String): Boolean = savedPin == pin
}
