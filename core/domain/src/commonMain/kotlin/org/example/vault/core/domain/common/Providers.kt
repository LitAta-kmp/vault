package org.example.vault.core.domain.common

/** Источник идентификаторов. Реализация живёт в data, в тестах подставляется предсказуемая. */
fun interface IdGenerator {
    fun newId(): String
}

/** Источник текущего времени (миллисекунды с 1970 года). Подменяется в тестах. */
fun interface TimeProvider {
    fun nowMillis(): Long
}
