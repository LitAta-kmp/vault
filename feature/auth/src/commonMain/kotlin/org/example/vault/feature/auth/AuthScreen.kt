package org.example.vault.feature.auth

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.example.vault.core.domain.auth.PinPolicy
import org.example.vault.feature.auth.components.PinDots
import org.example.vault.feature.auth.components.PinKeypad

/** Stateless-экран: только рисует State и сообщает о действиях пользователя. */
@Composable
internal fun AuthScreen(
    state: AuthState,
    onAction: (AuthAction) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (state.isLoading) {
            CircularProgressIndicator()
        } else {
            AuthContent(state = state, onAction = onAction)
        }
    }
}

@Composable
private fun AuthContent(
    state: AuthState,
    onAction: (AuthAction) -> Unit,
) {
    Column(
        modifier = Modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = state.step.title(), style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))
        PinDots(
            filledCount = state.enteredDigits.length,
            totalCount = PinPolicy.PIN_LENGTH,
        )
        // Место под ошибку резервируем всегда, чтобы клавиатура не прыгала при её появлении
        Box(modifier = Modifier.height(48.dp), contentAlignment = Alignment.Center) {
            state.error?.let { error ->
                Text(text = error.message(), color = MaterialTheme.colorScheme.error)
            }
        }
        PinKeypad(
            onDigitClick = { digit -> onAction(AuthAction.DigitClicked(digit)) },
            onDeleteClick = { onAction(AuthAction.DeleteClicked) },
        )
    }
}

private fun PinStep.title(): String = when (this) {
    PinStep.Create -> "Create a PIN"
    PinStep.Confirm -> "Confirm your PIN"
    PinStep.Unlock -> "Enter your PIN"
}

private fun PinError.message(): String = when (this) {
    PinError.PinsDoNotMatch -> "PINs do not match. Try again."
    PinError.WrongPin -> "Wrong PIN. Try again."
}

// Preview нужны, чтобы видеть экран в Android Studio без запуска на эмуляторе
@Preview
@Composable
internal fun AuthScreenCreatePreview() {
    MaterialTheme {
        AuthScreen(
            state = AuthState(isLoading = false, step = PinStep.Create, enteredDigits = "123"),
            onAction = {},
        )
    }
}

@Preview
@Composable
internal fun AuthScreenErrorPreview() {
    MaterialTheme {
        AuthScreen(
            state = AuthState(isLoading = false, step = PinStep.Unlock, error = PinError.WrongPin),
            onAction = {},
        )
    }
}
