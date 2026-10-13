package org.example.vault.di

import org.example.vault.core.data.di.dataModule
import org.example.vault.feature.auth.di.authModule

/** Единый список Koin-модулей приложения. Сюда добавляем модуль каждой новой фичи. */
val appModules = listOf(
    dataModule,
    authModule,
)
