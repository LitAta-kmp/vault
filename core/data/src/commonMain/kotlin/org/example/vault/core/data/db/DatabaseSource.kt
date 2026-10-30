package org.example.vault.core.data.db

import org.example.vault.core.domain.vault.VaultLockedException

/** Источник открытой базы. Репозиторию неважно, зашифрована она и кто её открыл. */
internal interface DatabaseSource {

    /** Бросает [VaultLockedException], если хранилище заперто. */
    val database: VaultDatabase
}
