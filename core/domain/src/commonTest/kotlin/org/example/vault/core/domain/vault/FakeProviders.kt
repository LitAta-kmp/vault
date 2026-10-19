package org.example.vault.core.domain.vault

import org.example.vault.core.domain.common.IdGenerator
import org.example.vault.core.domain.common.TimeProvider

/** Выдаёт предсказуемые id: id-1, id-2 и так далее. */
class FakeIdGenerator : IdGenerator {
    private var counter = 0

    override fun newId(): String {
        counter += 1
        return "id-$counter"
    }
}

/** Время, которым управляет тест: меняй [now] между шагами. */
class FakeTimeProvider(var now: Long = 1_000L) : TimeProvider {
    override fun nowMillis(): Long = now
}
