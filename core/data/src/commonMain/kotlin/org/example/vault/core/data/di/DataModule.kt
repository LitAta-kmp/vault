package org.example.vault.core.data.di

import org.example.vault.core.data.auth.InMemoryAuthRepository
import org.example.vault.core.domain.auth.AuthRepository
import org.koin.dsl.module

val dataModule = module {
    // single: один экземпляр на всё приложение, иначе сохранённый PIN потерялся бы между экранами
    single<AuthRepository> { InMemoryAuthRepository() }
}
