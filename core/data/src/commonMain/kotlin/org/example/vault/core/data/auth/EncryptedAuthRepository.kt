package org.example.vault.core.data.auth

import org.example.vault.core.data.db.VaultSession
import org.example.vault.core.domain.auth.AuthRepository

/**
 * PIN нигде не хранится: он и есть ключ шифрования базы.
 * «Верный PIN» значит «база открылась».
 */
internal class EncryptedAuthRepository(
    private val session: VaultSession,
) : AuthRepository {

    override suspend fun isPinSet(): Boolean = session.vaultExists()

    override suspend fun savePin(pin: String) = session.create(pin)

    // Побочный эффект: при успехе хранилище остаётся открытым для дальнейшей работы
    override suspend fun verifyPin(pin: String): Boolean = session.unlock(pin)
}
