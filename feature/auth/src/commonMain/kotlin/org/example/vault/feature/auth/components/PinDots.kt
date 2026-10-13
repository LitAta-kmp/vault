package org.example.vault.feature.auth.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

private val DOT_SIZE = 16.dp
private val DOT_SPACING = 12.dp

/**
 * Ряд из [totalCount] кружков. Первые [filledCount] закрашены, остальные пустые.
 * Показывает пользователю, сколько цифр PIN уже введено.
 */
@Composable
internal fun PinDots(
    filledCount: Int,
    totalCount: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        // Описание для скринридера (TalkBack): точки сами по себе ничего не говорят
        modifier = modifier.semantics {
            contentDescription = "$filledCount of $totalCount digits entered"
        },
        horizontalArrangement = Arrangement.spacedBy(DOT_SPACING),
    ) {
        repeat(totalCount) { index ->
            val isFilled = index < filledCount
            val backgroundColor = if (isFilled) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.outlineVariant
            }

            Box(
                modifier = Modifier
                    .size(DOT_SIZE)
                    // Сначала обрезаем по кругу, потом заливаем цветом: порядок важен
                    .clip(CircleShape)
                    .background(backgroundColor),
            )
        }
    }
}
