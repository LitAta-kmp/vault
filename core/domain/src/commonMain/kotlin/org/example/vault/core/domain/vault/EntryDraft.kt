package org.example.vault.core.domain.vault

/** Данные, которые пользователь вводит в форме. Идентификатор и время появятся позже, в use case. */
data class EntryDraft(
    val title: String,
    val username: String = "",
    val password: String = "",
    val notes: String = "",
) {
    override fun toString(): String = "EntryDraft(title=$title)"
}

// Маппер: единственное место, где черновик превращается в запись (и где заголовок очищается от пробелов)
internal fun EntryDraft.toEntry(id: String, createdAt: Long, updatedAt: Long): VaultEntry =
    VaultEntry(
        id = id,
        title = title.trim(),
        username = username,
        password = password,
        notes = notes,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )
