package org.example.vault.core.domain.vault

/** Запись в хранилище: логин, пароль и заметки к одному сервису. */
data class VaultEntry(
    val id: String,
    val title: String,
    val username: String,
    val password: String,
    val notes: String,
    val createdAt: Long,
    val updatedAt: Long,
) {
    // Автоматический toString напечатал бы пароль в лог: показываем только безопасное
    override fun toString(): String = "VaultEntry(id=$id, title=$title)"
}
