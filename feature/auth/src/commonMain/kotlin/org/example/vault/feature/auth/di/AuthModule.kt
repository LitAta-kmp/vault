package org.example.vault.feature.auth.di

import org.example.vault.core.domain.auth.IsPinSetUseCase
import org.example.vault.core.domain.auth.SetupPinUseCase
import org.example.vault.core.domain.auth.VerifyPinUseCase
import org.example.vault.feature.auth.AuthViewModel
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val authModule = module {
    // factory: use case'ы без состояния, новый экземпляр каждый раз дёшев и безопасен
    factoryOf(::IsPinSetUseCase)
    factoryOf(::SetupPinUseCase)
    factoryOf(::VerifyPinUseCase)

    // viewModel: время жизни привязано к экрану. Зависимости Koin подставит сам по типам конструктора
    viewModelOf(::AuthViewModel)
}
