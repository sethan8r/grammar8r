package dev.sethan8r.grammar.app.data.local.user.entity

import androidx.room.Entity
import dev.sethan8r.grammar.app.domain.model.exercise.HardcodedExerciseType

/**
 * Результат одного хардкод-упражнения карточки. Пишется в момент ПЕРВОГО ответа (write-once,
 * `INSERT OR IGNORE`) и больше не меняется — это анти-чит: перезаход в карточку не сбрасывает
 * результат первой попытки. Источник правды для зелёного ID упражнения и для сводки «верно X из N».
 *
 * Ключ — `(cardId, exerciseType, exerciseId)`: `exerciseId` уникален лишь внутри своего типа.
 * Плашки (нереализованный тип / умное задание) сюда НЕ пишутся.
 */
@Entity(
    tableName = "user_exercise_results",
    primaryKeys = ["cardId", "exerciseType", "exerciseId"],
)
data class UserExerciseResult(
    val cardId: Int,
    val exerciseType: HardcodedExerciseType,
    val exerciseId: Int,
    /** true = решено верно С ПЕРВОЙ попытки. */
    val correctFirstTry: Boolean,
)