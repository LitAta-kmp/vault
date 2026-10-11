package org.example.vault.core.domain.auth

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlinx.coroutines.test.runTest

class SetupPinUseCaseTest {

    private val repository = FakeAuthRepository()
    private val useCase = SetupPinUseCase(repository)

    @Test
    fun matching_valid_pins_are_saved() = runTest {
        val result = useCase("123456", "123456")

        assertEquals(SetupPinResult.Success, result)
        assertEquals("123456", repository.savedPin)
    }

    @Test
    fun different_pins_are_rejected_and_not_saved() = runTest {
        val result = useCase("123456", "654321")

        assertEquals(SetupPinResult.PinsDoNotMatch, result)
        assertNull(repository.savedPin)
    }

    @Test
    fun pin_with_wrong_length_is_rejected() = runTest {
        val result = useCase("123", "123")

        assertEquals(SetupPinResult.InvalidFormat, result)
        assertNull(repository.savedPin)
    }

    @Test
    fun pin_with_non_digits_is_rejected() = runTest {
        val result = useCase("12345a", "12345a")

        assertEquals(SetupPinResult.InvalidFormat, result)
        assertNull(repository.savedPin)
    }
}
