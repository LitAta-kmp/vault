package org.example.vault.core.domain.auth

/** Контракт хранения PIN. Реализация (SQLCipher и т.д.) живёт в core:data. */
interface AuthRepository {
    suspend fun isPinSet(): Boolean
    suspend fun savePin(pin: String)
    suspend fun verifyPin(pin: String): Boolean
}
