package dev.sethan8r.grammar.app.data.local.user.entity

import androidx.room.Entity
import dev.sethan8r.grammar.core.model.exercise.HardcodedExerciseType

/**
 * Результат одного хардкод-упражнения карточки. Строка появляется в момент ПЕРВОГО ответа
 * (`INSERT OR IGNORE`) и живёт ровно один заход: пока идёт тот же заход, верный второй ответ может
 * поднять её с «неверно» на «верно». Перезаход в карточку результат уже не меняет — это анти-чит.
 * Источник правды для зелёного ID упражнения и для сводки «верно X из N».
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
    /**
     * true = решено верно в первом заходе (с первой или со второй попытки, не выходя из карточки).
     *
     * TODO(rename): имя поля осталось от старой логики «только первая попытка» и смыслу больше не
     *  соответствует — правильное имя `solvedOnFirstPass`. Переименование затрагивает колонку в
     *  user.db, поэтому его делаем не сейчас, а когда до релиза понадобится миграция схемы по другой
     *  причине — тогда оно пройдёт попутно, без отдельной миграции ради имени.
     */
    val correctFirstTry: Boolean,
)