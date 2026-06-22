package dev.sethan8r.grammar.app.data.repository

import dev.sethan8r.grammar.app.data.local.content.dao.ExerciseDao
import dev.sethan8r.grammar.app.data.local.content.dao.TheoryDao
import dev.sethan8r.grammar.app.data.mapper.ExerciseContentMapper
import dev.sethan8r.grammar.app.domain.model.exercise.ChoiceType
import dev.sethan8r.grammar.app.domain.model.exercise.Exercise
import dev.sethan8r.grammar.app.domain.model.exercise.ExerciseSession
import dev.sethan8r.grammar.app.domain.model.exercise.HardcodedExerciseType
import dev.sethan8r.grammar.app.domain.repository.ExerciseRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/**
 * Сборка сессии упражнений карточки из content.db: индекс ([ExerciseDao.getExercisesForCard],
 * порядок по `orderInCard`) → упражнение из таблицы своего типа → доменная модель ([mapper]).
 * В конец добавляются сегменты умных заданий карточки (0, 1 или несколько — по числу `ai_exercises`).
 *
 * Три CHOICE-подтипа лежат в одной таблице с составным PK `(id, choiceType)` — нужный трек выбираем
 * по типу из индекса. Движок умеет все 14 хардкод-типов (F1–F4); пропавшие строки (осиротевший индекс)
 * отдаём как [Exercise.Unsupported]; умные задания — как [Exercise.AiPlaceholder] (Фаза 3).
 */
class ExerciseRepositoryImpl @Inject constructor(
    private val exerciseDao: ExerciseDao,
    private val theoryDao: TheoryDao,
    private val mapper: ExerciseContentMapper,
) : ExerciseRepository {

    override suspend fun getSession(cardId: Int): ExerciseSession {
        val card = theoryDao.getCard(cardId)
        val microtopicTitle = card?.microtopicId
            ?.let { theoryDao.observeMicrotopic(it).first()?.title }
            .orEmpty()

        val hardcoded = exerciseDao.getExercisesForCard(cardId).map { index ->
            val exercise: Exercise? = when (index.exerciseType) {
                HardcodedExerciseType.MULTIPLE_CHOICE ->
                    exerciseDao.getMultipleChoice(index.exerciseId, ChoiceType.CHOICE)?.let(mapper::toChoice)

                HardcodedExerciseType.FORWARD_CHOICE ->
                    exerciseDao.getMultipleChoice(index.exerciseId, ChoiceType.FORWARD_CHOICE)?.let(mapper::toChoice)

                HardcodedExerciseType.REVERSE_CHOICE ->
                    exerciseDao.getMultipleChoice(index.exerciseId, ChoiceType.REVERSE_CHOICE)?.let(mapper::toChoice)

                HardcodedExerciseType.TEXT_INPUT ->
                    exerciseDao.getTextInput(index.exerciseId)?.let(mapper::toTextInput)

                HardcodedExerciseType.ERROR_CORRECTION ->
                    exerciseDao.getErrorCorrection(index.exerciseId)?.let(mapper::toErrorCorrection)

                HardcodedExerciseType.CONSTRUCTION_MEANING ->
                    exerciseDao.getConstructionMeaning(index.exerciseId)?.let(mapper::toConstructionMeaning)

                HardcodedExerciseType.DIALOG_RESTORE ->
                    exerciseDao.getDialogRestore(index.exerciseId)?.let(mapper::toDialogRestore)

                HardcodedExerciseType.FIND_THE_ODD ->
                    exerciseDao.getFindTheOdd(index.exerciseId)?.let(mapper::toFindTheOdd)

                HardcodedExerciseType.TABLE_FILL ->
                    exerciseDao.getTableFill(index.exerciseId)?.let(mapper::toTableFill)

                HardcodedExerciseType.TRANSFORMATION ->
                    exerciseDao.getTransformation(index.exerciseId)?.let(mapper::toTransformation)

                HardcodedExerciseType.WORD_ARRANGEMENT ->
                    exerciseDao.getWordArrangement(index.exerciseId)?.let(mapper::toWordArrangement)

                HardcodedExerciseType.TRUE_FALSE ->
                    exerciseDao.getTrueFalse(index.exerciseId)?.let(mapper::toTrueFalse)

                HardcodedExerciseType.MATCHING ->
                    exerciseDao.getMatching(index.exerciseId)?.let(mapper::toMatching)

                HardcodedExerciseType.CATEGORIZATION ->
                    exerciseDao.getCategorization(index.exerciseId)?.let(mapper::toCategorization)
            }
            exercise ?: Exercise.Unsupported(index.exerciseId, index.exerciseType)
        }

        // Умные задания карточки — каждое отдельным сегментом-заглушкой (Фаза 3). Может быть 0, 1 или больше.
        val aiSegments = exerciseDao.getAiExercisesForCard(cardId)
            .map { Exercise.AiPlaceholder(exerciseId = it.id) }

        return ExerciseSession(
            cardTitle = card?.title.orEmpty(),
            microtopicTitle = microtopicTitle,
            theorySummary = card?.theorySummary.orEmpty(),
            exercises = hardcoded + aiSegments,
        )
    }
}