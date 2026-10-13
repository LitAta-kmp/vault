package org.example.vault.feature.auth.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

private const val KEYPAD_COLUMNS = 3
private const val LAST_DIGIT = 9
private const val KEY_SIZE_DP = 72
private const val KEY_SPACING_DP = 12

// Цифры 1..9 делим на строки по три: [1,2,3], [4,5,6], [7,8,9]
private val digitRows = (1..LAST_DIGIT).chunked(KEYPAD_COLUMNS)

/** Цифровая клавиатура: 1-9, затем пустая клетка, 0 и удаление. */
@Composable
internal fun PinKeypad(
    onDigitClick: (Int) -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(KEY_SPACING_DP.dp),
    ) {
        digitRows.forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(KEY_SPACING_DP.dp)) {
                row.forEach { digit ->
                    KeypadButton(label = digit.toString(), onClick = { onDigitClick(digit) })
                }
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(KEY_SPACING_DP.dp)) {
            // Пустая клетка слева от нуля, чтобы 0 оказался по центру
            Spacer(modifier = Modifier.size(KEY_SIZE_DP.dp))
            KeypadButton(label = "0", onClick = { onDigitClick(0) })
            KeypadButton(label = "Del", onClick = onDeleteClick)
        }
    }
}

@Composable
private fun KeypadButton(
    label: String,
    onClick: () -> Unit,
) {
    FilledTonalButton(
        onClick = onClick,
        modifier = Modifier.size(KEY_SIZE_DP.dp),
        shape = CircleShape,
        contentPadding = PaddingValues(0.dp),
    ) {
        Text(text = label, style = MaterialTheme.typography.titleLarge)
    }
}
