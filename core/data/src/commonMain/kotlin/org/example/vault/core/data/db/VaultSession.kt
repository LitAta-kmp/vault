package org.example.vault.core.data.db

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.example.vault.core.domain.vault.VaultLockedException

/**
 * Сессия хранилища: пока PIN не введён, базы нет (хранилище заперто).
 * Открытие базы медленное (key derivation), поэтому всё выполняется на IO под замком.
 */
internal class VaultSession(
    private val factory: VaultDatabaseFactory,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) {
    private val mutex = Mutex()
    private var openVault: OpenVault? = null

    /** Открытая база. Бросает [VaultLockedException], если хранилище заперто. */
    val database: VaultDatabase
        get() = openVault?.database ?: throw VaultLockedException()

    fun vaultExists(): Boolean = factory.databaseExists()

    /** Первый запуск: создаёт зашифрованную базу с этим PIN и сразу остаётся открытой. */
    suspend fun create(pin: String) = withContext(ioDispatcher) {
        mutex.withLock {
            check(!factory.databaseExists()) { "Vault already exists" }
            openVault = factory.open(pin)
        }
    }

    /** Открывает существующую базу. Неверный PIN: возвращает false, хранилище остаётся запертым. */
    suspend fun unlock(pin: String): Boolean = withContext(ioDispatcher) {
        mutex.withLock {
            closeCurrent()
            try {
                openVault = factory.open(pin)
                true
            } catch (ignored: WrongPassphraseException) {
                false
            }
        }
    }

    suspend fun lock() = withContext(ioDispatcher) {
        mutex.withLock { closeCurrent() }
    }

    private fun closeCurrent() {
        openVault?.driver?.close()
        openVault = null
    }
}
