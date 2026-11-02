package org.example.vault.feature.vault

import org.example.vault.core.domain.vault.VaultEntry

// Маппер domain -> UI-модель: здесь секреты отсекаются
internal fun VaultEntry.toItem(): EntryItem = EntryItem(
    id = id,
    title = title,
    username = username,
)
