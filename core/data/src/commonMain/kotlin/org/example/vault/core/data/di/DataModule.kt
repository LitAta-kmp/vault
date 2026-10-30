package org.example.vault.core.data.di

import org.example.vault.core.data.auth.EncryptedAuthRepository
import org.example.vault.core.data.common.RandomIdGenerator
import org.example.vault.core.data.common.SystemTimeProvider
import org.example.vault.core.data.db.VaultSession
import org.example.vault.core.data.vault.SqlDelightVaultRepository
import org.example.vault.core.domain.auth.AuthRepository
import org.example.vault.core.domain.common.IdGenerator
import org.example.vault.core.domain.common.TimeProvider
import org.example.vault.core.domain.vault.VaultRepository
import org.koin.dsl.module

val dataModule = module {
    includes(platformDataModule())

    // single: одна сессия на всё приложение, иначе открытая база «потеряется» между экранами
    single { VaultSession(get()) }
    single<AuthRepository> { EncryptedAuthRepository(get()) }
    single<VaultRepository> { SqlDelightVaultRepository(get<VaultSession>()) }

    single<IdGenerator> { RandomIdGenerator() }
    single<TimeProvider> { SystemTimeProvider() }
}
