package dev.sethan8r.grammar.app.data.local.content

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dev.sethan8r.grammar.app.data.local.content.dao.CourseWordDao
import dev.sethan8r.grammar.app.data.local.content.dao.ExerciseDao
import dev.sethan8r.grammar.app.data.local.content.dao.TheoryDao
import dev.sethan8r.grammar.app.data.local.content.entity.exercise.AiExercise
import dev.sethan8r.grammar.app.data.local.content.entity.exercise.CardExerciseIndex
import dev.sethan8r.grammar.app.data.local.content.entity.exercise.CategorizationExercise
import dev.sethan8r.grammar.app.data.local.content.entity.exercise.ConstructionMeaningExercise
import dev.sethan8r.grammar.app.data.local.content.entity.word.CourseCategory
import dev.sethan8r.grammar.app.data.local.content.entity.word.CourseWord
import dev.sethan8r.grammar.app.data.local.content.entity.word.CourseWordGroup
import dev.sethan8r.grammar.app.data.local.content.entity.exercise.DialogRestoreExercise
import dev.sethan8r.grammar.app.data.local.content.entity.exercise.ErrorCorrectionExercise
import dev.sethan8r.grammar.app.data.local.content.entity.exercise.FindTheOddExercise
import dev.sethan8r.grammar.app.data.local.content.entity.theory.GrammarCard
import dev.sethan8r.grammar.app.data.local.content.entity.theory.GrammarMicrotopic
import dev.sethan8r.grammar.app.data.local.content.entity.theory.GrammarTopic
import dev.sethan8r.grammar.app.data.local.content.entity.theory.GrammarTopicCategory
import dev.sethan8r.grammar.app.data.local.content.entity.word.IrregularVerb
import dev.sethan8r.grammar.app.data.local.content.entity.exercise.MatchingExercise
import dev.sethan8r.grammar.app.data.local.content.entity.exercise.MultipleChoiceExercise
import dev.sethan8r.grammar.app.data.local.content.entity.exercise.TableFillExercise
import dev.sethan8r.grammar.app.data.local.content.entity.exercise.TextInputExercise
import dev.sethan8r.grammar.app.data.local.content.entity.exercise.TransformationExercise
import dev.sethan8r.grammar.app.data.local.content.entity.exercise.TrueFalseExercise
import dev.sethan8r.grammar.app.data.local.content.entity.exercise.WordArrangementExercise
import dev.sethan8r.grammar.app.data.local.converter.Converters

/**
 * content.db — read-only БД контента, поставляется готовой в assets и открывается через
 * `createFromAsset`. ЗАМЕНЯЕТСЯ ЦЕЛИКОМ при обновлении приложения: версия БД = версия контента,
 * на релизе с новым контентом — bump версии + destructive fallback ТОЛЬКО для этой БД (легально:
 * пользовательских данных тут нет). Прогресс живёт отдельно в user.db; JOIN/FK между БД нет.
 *
 * Источник правды по схеме и архитектуре двух БД — db_schema.md.
 */
@Database(
    entities = [
        // Теория
        GrammarTopicCategory::class,
        GrammarTopic::class,
        GrammarMicrotopic::class,
        GrammarCard::class,
        // Индекс + AI
        CardExerciseIndex::class,
        AiExercise::class,
        // 12 таблиц хардкодных упражнений
        WordArrangementExercise::class,
        MultipleChoiceExercise::class,
        TextInputExercise::class,
        TrueFalseExercise::class,
        ErrorCorrectionExercise::class,
        TransformationExercise::class,
        CategorizationExercise::class,
        TableFillExercise::class,
        MatchingExercise::class,
        FindTheOddExercise::class,
        ConstructionMeaningExercise::class,
        DialogRestoreExercise::class,
        // Слова курса
        CourseWordGroup::class,
        CourseCategory::class,
        CourseWord::class,
        IrregularVerb::class
    ],
    // TODO(dev-versioning): во время обкатки контента КАЖДОЕ изменение схемы content.db (новая
    //   колонка/таблица) = +1 к version. Ручных Migration НЕ пишем — БД read-only и пересобирается
    //   из сидов, а destructive fallback (DatabaseModule) при смене версии просто перекопирует
    //   свежий ассет (пользовательских данных тут нет). ПЕРЕД РЕЛИЗОМ: схлопнуть все dev-бампы
    //   обратно в version = 1 и удалить лишние schemas/ContentDatabase/*.json — выпускаемся с чистой v1.
    // v2: TextInputExercise + taskDescription/wordBank («банк слов»).
    version = 2,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class ContentDatabase : RoomDatabase() {
    abstract fun theoryDao(): TheoryDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun courseWordDao(): CourseWordDao
}
