package org.example.vault.core.data.vault

import org.example.vault.core.data.db.EntryEntity
import org.example.vault.core.domain.vault.VaultEntry

// Маппер: строка таблицы -> domain-модель. Единственная граница между моделью хранения и domain
internal fun EntryEntity.toDomain(): VaultEntry = VaultEntry(
    id = id,
    title = title,
    username = username,
    password = password,
    notes = notes,
    createdAt = createdAt,
    updatedAt = updatedAt,
)
