package org.example.vault.core.data.db

import app.cash.sqldelight.db.SqlDriver

/** Открытая зашифрованная база вместе с драйвером, которым её нужно закрыть при блокировке. */
internal class OpenVault(
    val database: VaultDatabase,
    val driver: SqlDriver,
)

/** Переданный PIN (парольная фраза) не подходит к существующей базе. */
internal class WrongPassphraseException(cause: Throwable? = null) :
    Exception("Wrong passphrase", cause)

/** Платформенная часть: как именно создаётся и открывается зашифрованная база. */
internal interface VaultDatabaseFactory {

    fun databaseExists(): Boolean

    /**
     * Открывает базу с ключом [passphrase] или создаёт новую, если файла нет.
     * @throws WrongPassphraseException если ключ не подходит.
     */
    fun open(passphrase: String): OpenVault
}
