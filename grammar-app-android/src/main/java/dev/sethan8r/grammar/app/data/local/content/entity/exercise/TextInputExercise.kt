package dev.sethan8r.grammar.app.data.local.content.entity.exercise

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * TEXT_INPUT — вписать ответ вручную (case-insensitive), 3–5 пунктов в блоке.
 * `items` — JSON-`String` (sentence, contextRu, answer, alternatives), разбор в domain.
 *
 * Опциональный режим «банк слов» (обе колонки NULL у обычных заданий — вид не меняется):
 * `taskDescription` — строка-задание в шапке (напр. «Впиши глагол в форме прошедшего времени»);
 * `wordBank` — JSON-массив русских слов-глоссов, показывается пулом статичных чипов над пропусками
 * (вместо пер-пунктовых `contextRu`). Задаётся строкой `Банк: …` в MD — см. exercise_templates.md.
 * Схема — exercise_templates.md → `TextInputExercise`.
 */
@Entity(tableName = "text_input_exercises")
data class TextInputExercise(
    @PrimaryKey val id: Int,
    val items: String,
    val explanation: String,
    val taskDescription: String? = null,
    val wordBank: String? = null
)