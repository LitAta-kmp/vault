package org.example.vault.feature.auth

import org.example.vault.core.ui.mvi.UiAction
import org.example.vault.core.ui.mvi.UiEvent
import org.example.vault.core.ui.mvi.UiState

/** Какой сейчас шаг у экрана PIN. */
enum class PinStep { Create, Confirm, Unlock }

/** Ошибки, которые экран показывает пользователю. */
enum class PinError { PinsDoNotMatch, WrongPin }

data class AuthState(
    // Пока идёт проверка «задан ли PIN», экран показывает загрузку
    val isLoading: Boolean = true,
    val step: PinStep = PinStep.Create,
    // Введённые цифры храним строкой; по длине экран рисует заполненные точки
    val enteredDigits: String = "",
    val error: PinError? = null,
) : UiState

sealed interface AuthAction : UiAction {
    data class DigitClicked(val digit: Int) : AuthAction
    data object DeleteClicked : AuthAction
}

sealed interface AuthEvent : UiEvent {
    data object NavigateToVault : AuthEvent
}
