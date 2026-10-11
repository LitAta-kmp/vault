package org.example.vault.core.ui.mvi

// Маркерные интерфейсы: сами ничего не делают, но по типам сразу видно, кто что передаёт.

/** Полное состояние экрана. ViewModel хранит его и отдаёт UI. */
interface UiState

/** Действие пользователя: UI -> ViewModel (нажатие кнопки, ввод текста). */
interface UiAction

/** Одноразовое событие: ViewModel -> UI (навигация, snackbar). В состоянии не хранится. */
interface UiEvent
