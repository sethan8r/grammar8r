package dev.sethan8r.grammar.app.data.mapper

import dev.sethan8r.grammar.app.data.local.content.entity.exercise.MultipleChoiceExercise
import dev.sethan8r.grammar.app.data.local.content.entity.exercise.TextInputExercise
import dev.sethan8r.grammar.app.domain.model.exercise.Exercise
import dev.sethan8r.grammar.app.domain.model.exercise.Option
import dev.sethan8r.grammar.app.domain.model.exercise.TextItem
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import javax.inject.Inject

/**
 * Разбор сырых JSON-полей упражнений (Entity content.db) в типизированные доменные [Exercise].
 * Граница data→domain: kotlinx.serialization не утекает в domain (как в [TheoryContentMapper], Шаг E).
 * [Json] инжектится из [dev.sethan8r.grammar.app.di.SerializationModule] (`ignoreUnknownKeys = true`).
 */
class ExerciseContentMapper @Inject constructor(private val json: Json) {

    fun toChoice(entity: MultipleChoiceExercise): Exercise.Choice = Exercise.Choice(
        id = entity.id,
        choiceType = entity.choiceType,
        prompt = entity.prompt,
        contextRu = entity.contextRu,
        options = json.decodeFromString<List<OptionJson>>(entity.options)
            .map { Option(text = it.text, isCorrect = it.isCorrect) },
        explanation = entity.explanation,
    )

    fun toTextInput(entity: TextInputExercise): Exercise.TextInput = Exercise.TextInput(
        id = entity.id,
        items = json.decodeFromString<List<TextItemJson>>(entity.items)
            .map { TextItem(it.sentence, it.contextRu, it.answer, it.alternatives) },
        explanation = entity.explanation,
    )

    @Serializable
    private data class OptionJson(val text: String = "", val isCorrect: Boolean = false)

    @Serializable
    private data class TextItemJson(
        val sentence: String = "",
        val contextRu: String = "",
        val answer: String = "",
        val alternatives: List<String> = emptyList(),
    )
}