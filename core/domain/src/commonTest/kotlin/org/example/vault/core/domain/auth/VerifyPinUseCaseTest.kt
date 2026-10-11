package org.example.vault.core.domain.auth

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.test.runTest

class VerifyPinUseCaseTest {

    private val repository = FakeAuthRepository()
    private val useCase = VerifyPinUseCase(repository)

    @Test
    fun correct_pin_is_accepted() = runTest {
        repository.savePin("123456")

        assertTrue(useCase("123456"))
    }

    @Test
    fun wrong_pin_is_rejected() = runTest {
        repository.savePin("123456")

        assertFalse(useCase("654321"))
    }

    @Test
    fun malformed_pin_is_rejected() = runTest {
        repository.savePin("123456")

        assertFalse(useCase("12"))
    }
}
