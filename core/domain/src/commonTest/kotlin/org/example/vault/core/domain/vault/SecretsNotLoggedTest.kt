package org.example.vault.core.domain.vault

import kotlin.test.Test
import kotlin.test.assertFalse

class SecretsNotLoggedTest {

    @Test
    fun entry_to_string_does_not_expose_secrets() {
        val entry = VaultEntry(
            id = "1",
            title = "Gmail",
            username = "me",
            password = "s3cret",
            notes = "private note",
            createdAt = 0L,
            updatedAt = 0L,
        )

        val text = entry.toString()

        assertFalse("s3cret" in text)
        assertFalse("private note" in text)
    }

    @Test
    fun draft_to_string_does_not_expose_secrets() {
        val draft = EntryDraft(title = "Gmail", password = "s3cret", notes = "private note")

        val text = draft.toString()

        assertFalse("s3cret" in text)
        assertFalse("private note" in text)
    }
}
