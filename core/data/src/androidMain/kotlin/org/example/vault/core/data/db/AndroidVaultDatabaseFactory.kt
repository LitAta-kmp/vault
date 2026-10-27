package org.example.vault.core.data.db

import android.content.Context
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import net.zetetic.database.sqlcipher.SQLiteNotADatabaseException
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory

internal class AndroidVaultDatabaseFactory(
    private val context: Context,
) : VaultDatabaseFactory {

    init {
        // Нативную библиотеку SQLCipher загружаем вручную, до первого обращения к базе
        System.loadLibrary("sqlcipher")
    }

    override fun databaseExists(): Boolean = context.getDatabasePath(DB_NAME).exists()

    override fun open(passphrase: String): OpenVault {
        val driver = AndroidSqliteDriver(
            schema = VaultDatabase.Schema,
            context = context,
            name = DB_NAME,
            // Массив байт создаём заново при каждом открытии: библиотека затирает его после использования
            factory = SupportOpenHelperFactory(passphrase.toByteArray()),
        )
        val database = VaultDatabase(driver)
        try {
            // Неверный ключ обнаруживается только при первом обращении: проверяем его сразу
            database.entryQueries.countEntries().executeAsOne()
        } catch (e: SQLiteNotADatabaseException) {
            driver.close()
            throw WrongPassphraseException(e)
        }
        return OpenVault(database, driver)
    }

    private companion object {
        const val DB_NAME = "vault.db"
    }
}
