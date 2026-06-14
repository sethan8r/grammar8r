package dev.sethan8r.grammar.app.data.local.converter

import androidx.room.TypeConverter
import dev.sethan8r.grammar.app.domain.model.exercise.AiExerciseWordsSource
import dev.sethan8r.grammar.app.domain.model.exercise.ChoiceType
import dev.sethan8r.grammar.app.domain.model.exercise.HardcodedExerciseType
import dev.sethan8r.grammar.app.domain.model.exercise.WordTable
import dev.sethan8r.grammar.shared.AiExerciseInputMode

/**
 * Room TypeConverters — конвертация enum'ов доменного слоя ↔ String на границе data-слоя
 * (ровно один раз, без stringly-typed `when` в логике). Значение в БД — `enum.name`.
 *
 * JSON-поля (theory, examples, options, items, …) сюда НЕ попадают: в Entity они хранятся как
 * сырой `String` (колонка TEXT), типизированный разбор делает domain-маппер при чтении.
 * Так kotlinx.serialization не растекается в data-слой, а колонка остаётся TEXT (identity hash
 * content.db от этого не зависит).
 *
 * Один класс регистрируется на обе БД; неиспользуемые конкретной БД конвертеры безвредны.
 */
class Converters {

    @TypeConverter
    fun hardcodedExerciseTypeToString(value: HardcodedExerciseType): String = value.name

    @TypeConverter
    fun stringToHardcodedExerciseType(value: String): HardcodedExerciseType =
        HardcodedExerciseType.valueOf(value)

    @TypeConverter
    fun choiceTypeToString(value: ChoiceType): String = value.name

    @TypeConverter
    fun stringToChoiceType(value: String): ChoiceType = ChoiceType.valueOf(value)

    @TypeConverter
    fun aiInputModeToString(value: AiExerciseInputMode): String = value.name

    @TypeConverter
    fun stringToAiInputMode(value: String): AiExerciseInputMode = AiExerciseInputMode.valueOf(value)

    @TypeConverter
    fun aiWordsSourceToString(value: AiExerciseWordsSource): String = value.name

    @TypeConverter
    fun stringToAiWordsSource(value: String): AiExerciseWordsSource =
        AiExerciseWordsSource.valueOf(value)

    @TypeConverter
    fun wordTableToString(value: WordTable): String = value.name

    @TypeConverter
    fun stringToWordTable(value: String): WordTable = WordTable.valueOf(value)
}
