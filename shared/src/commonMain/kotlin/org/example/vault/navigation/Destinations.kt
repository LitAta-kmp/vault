package org.example.vault.navigation

import kotlinx.serialization.Serializable

// Маршруты как типы: ошибиться в строке нельзя, компилятор проверит
@Serializable
data object AuthDestination

@Serializable
data object VaultDestination
