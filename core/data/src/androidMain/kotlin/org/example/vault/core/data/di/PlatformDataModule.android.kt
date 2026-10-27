package org.example.vault.core.data.di

import org.example.vault.core.data.db.AndroidVaultDatabaseFactory
import org.example.vault.core.data.db.VaultDatabaseFactory
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

internal actual fun platformDataModule(): Module = module {
    single<VaultDatabaseFactory> { AndroidVaultDatabaseFactory(androidContext()) }
}
