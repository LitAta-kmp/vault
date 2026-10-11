package org.example.vault.core.domain.auth

/** Тестовая реализация: хранит PIN в переменной, без базы. */
class FakeAuthRepository : AuthRepository {

    var savedPin: String? = null
        private set

    override suspend fun isPinSet(): Boolean = savedPin != null

    override suspend fun savePin(pin: String) {
        savedPin = pin
    }

    override suspend fun verifyPin(pin: String): Boolean = savedPin == pin
}
