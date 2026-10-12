package org.example.vault.feature.auth

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import org.example.vault.core.domain.auth.IsPinSetUseCase
import org.example.vault.core.domain.auth.PinPolicy
import org.example.vault.core.domain.auth.SetupPinResult
import org.example.vault.core.domain.auth.SetupPinUseCase
import org.example.vault.core.domain.auth.VerifyPinUseCase
import org.example.vault.core.ui.mvi.BaseViewModel

class AuthViewModel(
    private val isPinSet: IsPinSetUseCase,
    private val setupPin: SetupPinUseCase,
    private val verifyPin: VerifyPinUseCase,
) : BaseViewModel<AuthState, AuthAction, AuthEvent>(AuthState()) {

    // Первый введённый PIN держим здесь, а не в State: для отрисовки он не нужен
    private var firstPin: String = ""

    init {
        // При запуске выясняем режим: PIN уже есть -> вход, иначе -> создание
        viewModelScope.launch {
            val step = if (isPinSet()) PinStep.Unlock else PinStep.Create
            updateState { copy(isLoading = false, step = step) }
        }
    }

    override fun onAction(action: AuthAction) {
        when (action) {
            is AuthAction.DigitClicked -> onDigitClicked(action.digit)
            AuthAction.DeleteClicked -> updateState {
                copy(enteredDigits = enteredDigits.dropLast(1), error = null)
            }
        }
    }

    private fun onDigitClicked(digit: Int) {
        val digits = state.value.enteredDigits + digit
        updateState { copy(enteredDigits = digits, error = null) }

        if (digits.length == PinPolicy.PIN_LENGTH) {
            viewModelScope.launch { onPinCompleted(digits) }
        }
    }

    // ТВОЯ ЧАСТЬ: что происходит, когда введены все цифры (см. критерии ниже)
    private suspend fun onPinCompleted(pin: String) {
        when (state.value.step) {
            PinStep.Create -> onCreateCompleted(pin)
            PinStep.Confirm -> onConfirmCompleted(pin)
            PinStep.Unlock -> onUnlockCompleted(pin)
        }
    }

    private fun onCreateCompleted(pin: String) {
        firstPin = pin
        updateState { copy(step = PinStep.Confirm, enteredDigits = "") }
    }

    private suspend fun onConfirmCompleted(pin: String) {
        val result = setupPin(firstPin, pin)
        // Первый PIN больше не нужен при любом исходе — не держим его в памяти
        firstPin = ""

        when (result) {
            SetupPinResult.Success -> sendEvent(AuthEvent.NavigateToVault)
            // InvalidFormat через UI недостижим, но молча глотать его нельзя
            SetupPinResult.PinsDoNotMatch,
            SetupPinResult.InvalidFormat -> updateState {
                copy(step = PinStep.Create, enteredDigits = "", error = PinError.PinsDoNotMatch)
            }
        }
    }

    private suspend fun onUnlockCompleted(pin: String) {
        if (verifyPin(pin)) {
            sendEvent(AuthEvent.NavigateToVault)
        } else {
            updateState { copy(enteredDigits = "", error = PinError.WrongPin) }
        }
    }
}
