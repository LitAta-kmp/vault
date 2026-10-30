package org.example.vault.core.data.common

import kotlin.random.Random
import kotlin.time.Clock
import org.example.vault.core.domain.common.IdGenerator
import org.example.vault.core.domain.common.TimeProvider

private const val HEX_RADIX = 16

// Два случайных 64-битных числа в hex: для уникальности записей достаточно.
// Позже можно заменить на UUID, когда kotlin.uuid станет полностью стабильным
internal class RandomIdGenerator : IdGenerator {
    override fun newId(): String =
        "${Random.nextLong().toULong().toString(HEX_RADIX)}-${Random.nextLong().toULong().toString(HEX_RADIX)}"
}

internal class SystemTimeProvider : TimeProvider {
    override fun nowMillis(): Long = Clock.System.now().toEpochMilliseconds()
}
