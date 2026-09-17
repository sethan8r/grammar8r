package dev.sethan8r.grammar.app.data.local.content.entity.exercise

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import dev.sethan8r.grammar.core.model.exercise.HardcodedExerciseType

/**
 * Индекс упражнений карточки: какие упражнения и в каком порядке показывать.
 * `exerciseType` (через TypeConverter) говорит, в какой таблице искать упражнение по `exerciseId`.
 * Источник правды по схеме — db_schema.md → `CardExerciseIndex`.
 */
@Entity(
    tableName = "card_exercise_index",
    indices = [Index("cardId")]
)
data class CardExerciseIndex(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val cardId: Int,
    val exerciseType: HardcodedExerciseType,
    val exerciseId: Int,
    val orderInCard: Int
)