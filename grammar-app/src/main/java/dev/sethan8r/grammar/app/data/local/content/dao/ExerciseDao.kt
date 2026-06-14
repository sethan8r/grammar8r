package dev.sethan8r.grammar.app.data.local.content.dao

import androidx.room.Dao
import androidx.room.Query
import dev.sethan8r.grammar.app.data.local.content.entity.exercise.AiExercise
import dev.sethan8r.grammar.app.data.local.content.entity.exercise.CardExerciseIndex
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
import dev.sethan8r.grammar.app.domain.model.exercise.ChoiceType

/**
 * Чтение упражнений из content.db (read-only). По индексу карточки [getExercisesForCard] код
 * узнаёт тип и id, затем тянет конкретное упражнение из таблицы своего типа.
 * Базовый набор — расширяется в Шаге F (движок упражнений).
 */
@Dao
interface ExerciseDao {

    @Query("SELECT * FROM card_exercise_index WHERE cardId = :cardId ORDER BY orderInCard")
    suspend fun getExercisesForCard(cardId: Int): List<CardExerciseIndex>

    @Query("SELECT * FROM ai_exercises WHERE cardId = :cardId")
    suspend fun getAiExercisesForCard(cardId: Int): List<AiExercise>

    @Query("SELECT * FROM word_arrangement_exercises WHERE id = :id")
    suspend fun getWordArrangement(id: Int): WordArrangementExercise?

    @Query("SELECT * FROM multiple_choice_exercises WHERE id = :id AND choiceType = :choiceType")
    suspend fun getMultipleChoice(id: Int, choiceType: ChoiceType): MultipleChoiceExercise?

    @Query("SELECT * FROM text_input_exercises WHERE id = :id")
    suspend fun getTextInput(id: Int): TextInputExercise?

    @Query("SELECT * FROM true_false_exercises WHERE id = :id")
    suspend fun getTrueFalse(id: Int): TrueFalseExercise?

    @Query("SELECT * FROM error_correction_exercises WHERE id = :id")
    suspend fun getErrorCorrection(id: Int): ErrorCorrectionExercise?

    @Query("SELECT * FROM transformation_exercises WHERE id = :id")
    suspend fun getTransformation(id: Int): TransformationExercise?

    @Query("SELECT * FROM categorization_exercises WHERE id = :id")
    suspend fun getCategorization(id: Int): CategorizationExercise?

    @Query("SELECT * FROM table_fill_exercises WHERE id = :id")
    suspend fun getTableFill(id: Int): TableFillExercise?

    @Query("SELECT * FROM matching_exercises WHERE id = :id")
    suspend fun getMatching(id: Int): MatchingExercise?

    @Query("SELECT * FROM find_the_odd_exercises WHERE id = :id")
    suspend fun getFindTheOdd(id: Int): FindTheOddExercise?

    @Query("SELECT * FROM construction_meaning_exercises WHERE id = :id")
    suspend fun getConstructionMeaning(id: Int): ConstructionMeaningExercise?

    @Query("SELECT * FROM dialog_restore_exercises WHERE id = :id")
    suspend fun getDialogRestore(id: Int): DialogRestoreExercise?
}