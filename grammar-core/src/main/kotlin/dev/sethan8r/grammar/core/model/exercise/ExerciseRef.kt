package dev.sethan8r.grammar.core.model.exercise

/**
 * Идентификатор хардкод-упражнения внутри карточки: `exerciseId` уникален лишь в пределах своего
 * [type], поэтому ссылка — пара. Используется для отметки «упражнение пройдено» (зелёный ID).
 */
data class ExerciseRef(val type: HardcodedExerciseType, val id: Int)