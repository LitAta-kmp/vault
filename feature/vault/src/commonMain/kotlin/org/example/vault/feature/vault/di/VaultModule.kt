package org.example.vault.feature.vault.di

import org.example.vault.core.domain.vault.DeleteEntryUseCase
import org.example.vault.core.domain.vault.ObserveEntriesUseCase
import org.example.vault.feature.vault.VaultViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val vaultModule = module {
    factoryOf(::ObserveEntriesUseCase)
    factoryOf(::DeleteEntryUseCase)
    viewModelOf(::VaultViewModel)
}
