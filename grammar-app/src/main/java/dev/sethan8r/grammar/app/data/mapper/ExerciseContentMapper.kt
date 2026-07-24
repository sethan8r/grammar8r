package dev.sethan8r.grammar.app.data.mapper

import dev.sethan8r.grammar.app.data.local.content.entity.exercise.CategorizationExercise
import dev.sethan8r.grammar.app.data.local.content.entity.exercise.ConstructionMeaningExercise
import dev.sethan8r.grammar.app.data.local.content.entity.exercise.DialogRestoreExercise
import dev.sethan8r.grammar.app.data.local.content.entity.exercise.ErrorCorrectionExercise
import dev.sethan8r.grammar.app.data.local.content.entity.exercise.FindTheOddExercise
import dev.sethan8r.grammar.app.data.local.content.entity.exercise.MatchingExercise
import dev.sethan8r.grammar.app.data.local.content.entity.exercise.MultipleChoiceExercise
import dev.sethan8r.grammar.app.data.local.content.entity.exercise.TableFillExercise
import dev.sethan8r.grammar.app.data.local.content.entity.exercise.TextInputExercise
import dev.sethan8r.grammar.app.data.local.content.entity.exercise.TransformationExercise
import dev.sethan8r.grammar.app.data.local.content.entity.exercise.TrueFalseExercise
import dev.sethan8r.grammar.app.data.local.content.entity.exercise.WordArrangementExercise
import dev.sethan8r.grammar.app.domain.model.exercise.Category
import dev.sethan8r.grammar.app.domain.model.exercise.DialogLine
import dev.sethan8r.grammar.app.domain.model.exercise.Exercise
import dev.sethan8r.grammar.app.domain.model.exercise.MatchPair
import dev.sethan8r.grammar.app.domain.model.exercise.Option
import dev.sethan8r.grammar.app.domain.model.exercise.Statement
import dev.sethan8r.grammar.app.domain.model.exercise.TableFillRow
import dev.sethan8r.grammar.app.domain.model.exercise.TextItem
import dev.sethan8r.grammar.app.domain.model.exercise.TransformItem
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import javax.inject.Inject

/**
 * Разбор сырых JSON-полей упражнений (Entity content.db) в типизированные доменные [Exercise].
 * Граница data→domain: kotlinx.serialization не утекает в domain (как в [TheoryContentMapper]).
 * [Json] инжектится из [dev.sethan8r.grammar.app.di.SerializationModule] (`ignoreUnknownKeys = true`).
 */
class ExerciseContentMapper @Inject constructor(private val json: Json) {

    fun toChoice(entity: MultipleChoiceExercise): Exercise.Choice = Exercise.Choice(
        id = entity.id,
        choiceType = entity.choiceType,
        prompt = entity.prompt,
        contextRu = entity.contextRu,
        options = parseOptions(entity.options),
        explanation = entity.explanation,
    )

    fun toTextInput(entity: TextInputExercise): Exercise.TextInput = Exercise.TextInput(
        id = entity.id,
        items = json.decodeFromString<List<TextItemJson>>(entity.items)
            .map { TextItem(it.sentence, it.contextRu, it.answer, it.alternatives) },
        explanation = entity.explanation,
        taskDescription = entity.taskDescription,
        wordBank = entity.wordBank?.let { json.decodeFromString<List<String>>(it) }.orEmpty(),
    )

    fun toErrorCorrection(entity: ErrorCorrectionExercise): Exercise.ErrorCorrection = Exercise.ErrorCorrection(
        id = entity.id,
        wrongSentence = entity.wrongSentence,
        options = parseOptions(entity.options),
        explanation = entity.explanation,
    )

    fun toConstructionMeaning(entity: ConstructionMeaningExercise): Exercise.ConstructionMeaning =
        Exercise.ConstructionMeaning(
            id = entity.id,
            construction = entity.construction,
            options = parseOptions(entity.options),
            explanation = entity.explanation,
        )

    fun toDialogRestore(entity: DialogRestoreExercise): Exercise.DialogRestore = Exercise.DialogRestore(
        id = entity.id,
        lines = json.decodeFromString<List<DialogLineJson>>(entity.lines)
            .map { DialogLine(speaker = it.speaker, text = it.text) },
        options = parseOptions(entity.options),
        explanation = entity.explanation,
    )

    /** FIND_THE_ODD: элементы ложатся в общие [Option] — лишний (`isOdd`) становится правильным выбором. */
    fun toFindTheOdd(entity: FindTheOddExercise): Exercise.FindTheOdd = Exercise.FindTheOdd(
        id = entity.id,
        groupDescription = entity.groupDescription,
        options = json.decodeFromString<List<OddItemJson>>(entity.items)
            .map { Option(text = it.text, isCorrect = it.isOdd) },
        explanation = entity.explanation,
    )

    fun toTableFill(entity: TableFillExercise): Exercise.TableFill = Exercise.TableFill(
        id = entity.id,
        taskDescription = entity.taskDescription,
        rows = json.decodeFromString<List<TableRowJson>>(entity.rows)
            .map { TableFillRow(hint = it.hint, answer = it.answer) },
        explanation = entity.explanation,
    )

    fun toTransformation(entity: TransformationExercise): Exercise.Transformation = Exercise.Transformation(
        id = entity.id,
        taskDescription = entity.taskDescription,
        items = json.decodeFromString<List<TransformItemJson>>(entity.items)
            .map { TransformItem(original = it.original, transformed = it.transformed) },
        explanation = entity.explanation,
    )

    fun toWordArrangement(entity: WordArrangementExercise): Exercise.WordArrangement = Exercise.WordArrangement(
        id = entity.id,
        situationRu = entity.situationRu,
        correctSentence = entity.correctSentence,
        words = parseWords(entity.words),
        distractors = parseWords(entity.distractors),
        explanation = entity.explanation,
    )

    fun toTrueFalse(entity: TrueFalseExercise): Exercise.TrueFalse = Exercise.TrueFalse(
        id = entity.id,
        statements = json.decodeFromString<List<StatementJson>>(entity.statements)
            .map { Statement(en = it.en, ru = it.ru, isTrue = it.isTrue) },
        explanation = entity.explanation,
    )

    fun toMatching(entity: MatchingExercise): Exercise.Matching = Exercise.Matching(
        id = entity.id,
        taskDescription = entity.taskDescription,
        pairs = json.decodeFromString<List<PairJson>>(entity.pairs)
            .map { MatchPair(left = it.left, right = it.right) },
        explanation = entity.explanation,
    )

    fun toCategorization(entity: CategorizationExercise): Exercise.Categorization = Exercise.Categorization(
        id = entity.id,
        taskDescription = entity.taskDescription,
        categories = json.decodeFromString<List<CategoryJson>>(entity.categories)
            .map { Category(title = it.title, items = it.items) },
        explanation = entity.explanation,
    )

    private fun parseWords(raw: String): List<String> = json.decodeFromString(raw)

    /** Общий разбор поля `options` (одинаков у всех типов с выбором варианта) — Правило №0. */
    private fun parseOptions(raw: String): List<Option> =
        json.decodeFromString<List<OptionJson>>(raw).map { Option(text = it.text, isCorrect = it.isCorrect) }

    @Serializable
    private data class OptionJson(val text: String = "", val isCorrect: Boolean = false)

    @Serializable
    private data class TextItemJson(
        val sentence: String = "",
        val contextRu: String = "",
        val answer: String = "",
        val alternatives: List<String> = emptyList(),
    )

    @Serializable
    private data class DialogLineJson(val speaker: String = "", val text: String? = null)

    @Serializable
    private data class OddItemJson(val text: String = "", val isOdd: Boolean = false)

    @Serializable
    private data class TableRowJson(val hint: String = "", val answer: String = "")

    @Serializable
    private data class TransformItemJson(val original: String = "", val transformed: String = "")

    @Serializable
    private data class StatementJson(val en: String = "", val ru: String = "", val isTrue: Boolean = false)

    @Serializable
    private data class PairJson(val left: String = "", val right: String = "")

    @Serializable
    private data class CategoryJson(val title: String = "", val items: List<String> = emptyList())
}