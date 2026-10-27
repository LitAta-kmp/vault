package org.example.vault.core.data.di

import org.example.vault.core.data.auth.EncryptedAuthRepository
import org.example.vault.core.data.db.VaultSession
import org.example.vault.core.domain.auth.AuthRepository
import org.koin.dsl.module

val dataModule = module {
    includes(platformDataModule())

    // single: одна сессия на всё приложение, иначе открытая база «потеряется» между экранами
    single { VaultSession(get()) }
    single<AuthRepository> { EncryptedAuthRepository(get()) }
}
