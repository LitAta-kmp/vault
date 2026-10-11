package org.example.vault.core.ui.mvi

import app.cash.turbine.test
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain

// Минимальный экран-счётчик, чтобы проверить сам BaseViewModel
private data class CounterState(val count: Int = 0) : UiState

private sealed interface CounterAction : UiAction {
    data object Increment : CounterAction
}

private sealed interface CounterEvent : UiEvent {
    data class CountChanged(val value: Int) : CounterEvent
}

private class CounterViewModel :
    BaseViewModel<CounterState, CounterAction, CounterEvent>(CounterState()) {

    override fun onAction(action: CounterAction) {
        when (action) {
            CounterAction.Increment -> {
                updateState { copy(count = count + 1) }
                sendEvent(CounterEvent.CountChanged(state.value.count))
            }
        }
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class BaseViewModelTest {

    // viewModelScope работает на Dispatchers.Main, которого в JVM-тестах нет — подменяем
    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun onAction_updates_state() {
        val viewModel = CounterViewModel()

        viewModel.onAction(CounterAction.Increment)
        viewModel.onAction(CounterAction.Increment)

        assertEquals(2, viewModel.state.value.count)
    }

    @Test
    fun sendEvent_delivers_event_to_collector() = runTest {
        val viewModel = CounterViewModel()

        viewModel.events.test {
            viewModel.onAction(CounterAction.Increment)
            assertEquals(CounterEvent.CountChanged(1), awaitItem())
        }
    }
}
