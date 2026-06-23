package dev.sethan8r.grammar.app.ui.screens.exercise

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Фаза ответа на текущее упражнение (UI-стейт сессии, в БД не пишется). */
enum class AnswerPhase {
    /** Ввод/выбор ответа, попыток ещё не было. */
    ANSWERING,

    /** Первая попытка неверна: «попробуйте ещё раз», ответ остаётся, объяснение НЕ показываем. */
    WRONG_FIRST,

    /** Ответ верный — открыта «Далее». */
    CORRECT,

    /** Обе попытки слиты: показываем правильный ответ рядом с ответом юзера + explanation, открыта «Далее». */
    REVEALED,
}

/**
 * Можно ли сейчас редактировать ответ (выбирать вариант / печатать). Единственное определение правила
 * (Правило №0) — используется и делегатом ([AnswerDelegate.State.isEditable]), и рендерерами заданий.
 */
val AnswerPhase.isEditable: Boolean
    get() = this == AnswerPhase.ANSWERING || this == AnswerPhase.WRONG_FIRST

/**
 * Делегат-механика «ответ на карточку-задание»: 2 попытки, фидбэк, «Далее» открывается только
 * после ответа. Свой [StateFlow] — включается КОМПОЗИЦИЕЙ во ViewModel сессии и переиспользуется
 * всеми типами упражнений (НЕ копипаста по типам, НЕ наследование).
 *
 * Логику попыток описывает grammar8r_plan.md → «Логика попыток (2 попытки)»; счётчик в БД не идёт.
 */
class AnswerDelegate {

    data class State(
        val phase: AnswerPhase = AnswerPhase.ANSWERING,
        val attemptsUsed: Int = 0,
    ) {
        /** Ответ можно редактировать (выбирать вариант / печатать). */
        val isEditable: Boolean get() = phase.isEditable
        /** Доступна кнопка «Далее» (ответ завершён — верно или показан правильный). */
        val canProceed: Boolean get() = phase == AnswerPhase.CORRECT || phase == AnswerPhase.REVEALED
    }

    private val _state = MutableStateFlow(State())
    val state: StateFlow<State> = _state.asStateFlow()

    /**
     * Готовит делегат к новому упражнению. [skipAnswering] = true для плашек-заглушек нереализованных
     * типов: отвечать нечего, сразу открыта «Далее».
     */
    fun start(skipAnswering: Boolean) {
        _state.value = State(phase = if (skipAnswering) AnswerPhase.REVEALED else AnswerPhase.ANSWERING)
    }

    /** Проверка ответа. 1-я ошибка → ещё попытка; 2-я ошибка → показ правильного; верно → готово. */
    fun submit(correct: Boolean) {
        _state.value = when {
            correct -> _state.value.copy(phase = AnswerPhase.CORRECT)
            _state.value.attemptsUsed == 0 -> State(phase = AnswerPhase.WRONG_FIRST, attemptsUsed = 1)
            else -> _state.value.copy(phase = AnswerPhase.REVEALED, attemptsUsed = 2)
        }
    }
}