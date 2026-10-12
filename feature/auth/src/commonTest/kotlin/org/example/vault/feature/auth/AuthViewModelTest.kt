package org.example.vault.feature.auth

import app.cash.turbine.test
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.example.vault.core.domain.auth.IsPinSetUseCase
import org.example.vault.core.domain.auth.SetupPinUseCase
import org.example.vault.core.domain.auth.VerifyPinUseCase

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    private val repository = FakeAuthRepository()

    // viewModelScope работает на Dispatchers.Main, которого в JVM-тестах нет — подменяем
    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() = AuthViewModel(
        isPinSet = IsPinSetUseCase(repository),
        setupPin = SetupPinUseCase(repository),
        verifyPin = VerifyPinUseCase(repository),
    )

    // Эмулирует нажатие на каждую цифру строки по очереди
    private fun AuthViewModel.enter(pin: String) {
        pin.forEach { onAction(AuthAction.DigitClicked(it.digitToInt())) }
    }

    @Test
    fun starts_in_create_step_when_pin_is_not_set() {
        val viewModel = createViewModel()

        assertEquals(PinStep.Create, viewModel.state.value.step)
        assertEquals(false, viewModel.state.value.isLoading)
    }

    @Test
    fun starts_in_unlock_step_when_pin_is_already_set() = runTest {
        repository.savePin("123456")

        val viewModel = createViewModel()

        assertEquals(PinStep.Unlock, viewModel.state.value.step)
    }

    @Test
    fun digit_click_appends_digit() {
        val viewModel = createViewModel()

        viewModel.enter("12")

        assertEquals("12", viewModel.state.value.enteredDigits)
    }

    @Test
    fun delete_click_removes_last_digit() {
        val viewModel = createViewModel()
        viewModel.enter("123")

        viewModel.onAction(AuthAction.DeleteClicked)

        assertEquals("12", viewModel.state.value.enteredDigits)
    }

    @Test
    fun completed_pin_in_create_step_moves_to_confirm_and_clears_digits() {
        val viewModel = createViewModel()

        viewModel.enter("123456")

        assertEquals(PinStep.Confirm, viewModel.state.value.step)
        assertEquals("", viewModel.state.value.enteredDigits)
    }

    @Test
    fun matching_confirmation_saves_pin_and_navigates_to_vault() = runTest {
        val viewModel = createViewModel()

        viewModel.events.test {
            viewModel.enter("123456")
            viewModel.enter("123456")

            assertEquals(AuthEvent.NavigateToVault, awaitItem())
        }
        assertEquals("123456", repository.savedPin)
    }

    @Test
    fun different_confirmation_shows_error_and_returns_to_create_step() {
        val viewModel = createViewModel()

        viewModel.enter("123456")
        viewModel.enter("654321")

        assertEquals(PinStep.Create, viewModel.state.value.step)
        assertEquals(PinError.PinsDoNotMatch, viewModel.state.value.error)
        assertEquals("", viewModel.state.value.enteredDigits)
        assertNull(repository.savedPin)
    }

    @Test
    fun correct_pin_in_unlock_step_navigates_to_vault() = runTest {
        repository.savePin("123456")
        val viewModel = createViewModel()

        viewModel.events.test {
            viewModel.enter("123456")

            assertEquals(AuthEvent.NavigateToVault, awaitItem())
        }
    }

    @Test
    fun wrong_pin_in_unlock_step_shows_error_and_clears_digits() = runTest {
        repository.savePin("123456")
        val viewModel = createViewModel()

        viewModel.enter("654321")

        assertEquals(PinStep.Unlock, viewModel.state.value.step)
        assertEquals(PinError.WrongPin, viewModel.state.value.error)
        assertEquals("", viewModel.state.value.enteredDigits)
    }

    @Test
    fun entering_a_digit_clears_previous_error() = runTest {
        repository.savePin("123456")
        val viewModel = createViewModel()
        viewModel.enter("654321")

        viewModel.enter("1")

        assertNull(viewModel.state.value.error)
    }
}
