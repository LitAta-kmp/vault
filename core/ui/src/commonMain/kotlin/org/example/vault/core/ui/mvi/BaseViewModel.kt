package org.example.vault.core.ui.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Базовый класс для всех экранов: единый поток данных
 * UiAction -> ViewModel -> UiState (+ UiEvent).
 */
abstract class BaseViewModel<S : UiState, A : UiAction, E : UiEvent>(
    initialState: S,
) : ViewModel() {

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<S> = _state.asStateFlow()

    // Channel вместо SharedFlow: событие, отправленное в момент, когда UI не подписан
    // (например, во время поворота экрана), не потеряется, а дождётся подписчика
    private val _events = Channel<E>(Channel.BUFFERED)
    val events: Flow<E> = _events.receiveAsFlow()

    /** Единственная точка входа для UI. */
    abstract fun onAction(action: A)

    /** Меняет состояние через copy: updateState { copy(isLoading = true) }. */
    protected fun updateState(reducer: S.() -> S) {
        _state.update { it.reducer() }
    }

    protected fun sendEvent(event: E) {
        viewModelScope.launch { _events.send(event) }
    }
}
