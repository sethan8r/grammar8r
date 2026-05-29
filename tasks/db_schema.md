# Grammar8r — Схема базы данных

Единый справочник всех таблиц и ENUM-ов приложения.  
При любом изменении схемы — обновлять этот файл первым.

**Связанные планы:**
- [grammar8r_plan.md](grammar8r_plan.md) — общая архитектура, алгоритм выборки слов, AI-экран UX
- [practice_plan.md](practice_plan.md) — режимы Практики, где и как используются таблицы прогресса
- [subscription.md](subscription.md) — лимиты по тирам, связь с AI-запросами
- [tasks/phases/phase4/phase4_server.md](phases/phase4/phase4_server.md) — серверная сторона, AiConfigProfile на сервере

---

## ENUM-ы

---

### `AiExerciseInputMode`

> Используется в: `AiExercise.inputMode`  
> Влияет на: рендеринг зоны ввода на AI-экране  
> Подробнее: grammar8r_plan.md → «AI-упражнения — режимы ввода»

Определяет как пользователь вводит ответ на AI-упражнение. UI-компонент AI-экрана единый и переиспользуемый — этот флаг меняет только зону ввода внутри него.

```kotlin
// Расширяется при появлении новых типов ввода
enum class AiExerciseInputMode {

    FREE_WRITE,
    // Пользователь пишет свой ответ в свободное поле снизу.
    // Использовать для: перевод предложения, составить вопрос, исправить ошибку,
    // любое задание где нужен произвольный текст.

    FILL_BLANKS
    // AI присылает текст с пропусками, размеченными как [___].
    // Сервер парсит ответ AI и возвращает клиенту структуру сегментов:
    // [{ type: "text", content: "..." }, { type: "blank", index: 0 }, ...]
    // Клиент рендерит Text() и встроенные TextField() в одну строку (FlowRow).
    // Пользователь заполняет только пропуски — основной текст нередактируем.
    // Использовать для: вставь предлог, вставь форму глагола, заполни пропуск.
}
```

---

### `AiExerciseWordsSource`

> Используется в: `AiExercise.wordsSource`  
> Влияет на: какие слова тянуть для AI-промта перед стартом упражнения  
> Подробнее: grammar8r_plan.md → «Алгоритм выборки слов»

Определяет откуда брать слова для промта. Логика живёт в `WordsRepository`.

Слова берутся из двух БД:
- **Words8r DB** (`words` таблица) — личный словарь пользователя. Читаем, не трогаем.
- **Grammar8r DB** (`course_words`, `irregular_verbs`) — слова курса, разблокированные по ходу теории.

Фильтр для AI-промта:
- Words8r: `q_rep > 0`
- course_words / irregular_verbs: `is_unlocked = true AND (is_hidden = true OR q_rep > 0)`
  - `is_hidden = true` = пользователь отметил "уже знаю" → включаем даже при `q_rep = 0`
  - `q_rep > 0` = хотя бы раз повторял → включаем

Если слов меньше `minWords` → упражнение заблокировано:  
*«Нужно больше слов из категории «{displayName}», чтобы открылось это упражнение»*

В случайном режиме Практики упражнения с недостаточным количеством слов фильтруются ещё до попадания в стопку.

```kotlin
// Перечень таблиц-источников слов. Ровно 3 — фиксировано.
enum class WordTable {
    WORDS8R,          // Words8r DB, таблица `words` — личный словарь пользователя
    COURSE_WORDS,     // Grammar8r DB, таблица `course_words` — слова курса
    IRREGULAR_VERBS   // Grammar8r DB, таблица `irregular_verbs` — глаголы v1/v2/v3
}

// Вспомогательный класс — один источник слов: таблица + категория + количество
data class WordSource(
    val table: WordTable,
    // Из какой таблицы брать слова (см. WordTable выше).

    val categoryId: String?,
    // ID категории для фильтрации. null = вся таблица без фильтра по категории.
    // WORDS8R: ID из таблицы `categories` в Words8r DB (String PK, через word_category join).
    // COURSE_WORDS / IRREGULAR_VERBS: ID из `course_categories` в Grammar8r DB (String PK).
    // String а не Int — потому что оба источника используют текстовые стабильные ID
    // (например "basic_verbs", "informal_english"), заданные в assets JSON.

    val targetCount: Int
    // Сколько слов взять из этого источника (случайная выборка).
)

// Расширяется при появлении новых источников
enum class AiExerciseWordsSource(
    val sources: List<WordSource>,
    // Список источников. Клиент делает запрос к каждому и объединяет результат.

    val minWords: Int,
    // Минимальное суммарное количество подходящих слов по всем источникам.
    // Если меньше — упражнение недоступно.

    val displayName: String
    // Человекочитаемое название — показывается в подсказке если упражнение заблокировано.
) {
    NONE(
        sources = emptyList(),
        minWords = 0,
        displayName = ""
    ),
    // Упражнение не использует слова вообще.
    // Использовать для: AI-уточнение «Не совсем понял».

    GENERAL(
        sources = listOf(
            WordSource("words8r", null, 70),
            WordSource("course_words", null, 30)
        ),
        minWords = 10,
        displayName = "общий словарь"
    ),
    // Общий словарь: 70 слов из Words8r + 30 из course_words, без фильтра по категории.
    // Использовать для: большинство in-card упражнений и режимов Практики.

    INFORMAL_ENGLISH(
        sources = listOf(
            WordSource("words8r", "informal_english", 30)
            // "informal_english" — ID категории в Words8r DB
        ),
        minWords = 5,
        displayName = "Разговорный английский"
    ),
    // Только слова из категории «Informal English» в Words8r.
    // Разблокируется после прохождения темы «Разговорный английский» в теории.

    VERB_FORMS(
        sources = listOf(
            WordSource("irregular_verbs", null, 50)
        ),
        minWords = 10,
        displayName = "Формы глаголов"
    )
    // Неправильные глаголы (v1/v2/v3) из Grammar8r DB.
    // Разблокируются после темы «Глаголы V1/V2/V3» в теории.
    // Использовать для: режим «Заполни форму глагола» в Практике.
}
```

---

### `AiConfigProfile` — только серверная концепция

> ⚠️ Клиентского enum больше нет. Поле убрано из `AiExercise`.  
> Хранится в: `ai_exercise_prompts.ai_config_profile` (VARCHAR)  
> Подробнее: phase4_server.md → Хранилище промтов

Профиль хранится на сервере в таблице `ai_exercise_prompts` рядом с промтом. Клиент его не шлёт и не знает — сервер сам определяет по `exercise_id` какие `maxTokens` и `temperature` применить.

Число в названии HEAVY — ориентир на количество токенов выхода. Точные значения в серверном конфиге профилей.

Допустимые значения (строки в БД):
- `CLARIFICATION` — уточнение «Не совсем понял»: короткий выход, 2–4 предложения
- `EXERCISE_LIGHT` — большинство упражнений: короткая генерация + проверка ответа
- `EVALUATION_ONLY` — только проверка ответа, без генерации задания
- `EXERCISE_HEAVY_50` — короткая тяжёлая генерация
- `EXERCISE_HEAVY_100` — средняя генерация
- `EXERCISE_HEAVY_150`
- `EXERCISE_HEAVY_200`
- `EXERCISE_HEAVY_300` — длинные тексты: рассказы, абзацы
- `EXERCISE_HEAVY_500` — очень длинная генерация, запас на будущее

---

## Таблицы Room DB

---

### `AiExercise`

> Контент из assets JSON. Загружается один раз при первом запуске.  
> При обновлении приложения с новыми упражнениями — **INSERT OR IGNORE** (не REPLACE),  
> чтобы не затереть `qRep` в `UserAiExerciseStats`.  
> Связан с: `GrammarCard` (через `cardId`), `UserAiExerciseStats`, `FavoriteAiExercise`  
> Подробнее: grammar8r_plan.md → «AI-экран (умная практика)», «AI-упражнения — режимы ввода»

```kotlin
data class AiExercise(

    val id: String,
    // PK. Уникальный идентификатор упражнения. Совпадает с id в assets JSON.
    // Пример: "present_simple_card2_ex1"

    val cardId: String,
    // FK → GrammarCard.id. К какой карточке теории относится это упражнение.
    // Используется для:
    //   1. Кнопка «?» на AI-экране — JOIN с GrammarCard → показываем theorySummary.
    //   2. Случайный режим Практики — фильтрация по пройденным микротемам.
    //   3. Режим «Упражнения из теории» — группировка по темам и микротемам.

    val title: String,
    // Название упражнения. Показывается на карточке в случайном режиме и в дереве Практики.
    // Пример: «Составь предложение в Present Simple»

    val userInstruction: String,
    // Инструкция для пользователя. Показывается на AI-экране над размытой зоной
    // (до нажатия кнопки [Начать задание]).
    // Пример: «AI даст русское предложение — переведи его на английский»

    // promptTemplate убран — промты хранятся на сервере в таблице ai_exercise_prompts, ключ = id
    // aiConfigProfile убран — тоже на сервере в ai_exercise_prompts, клиент его не шлёт

    val inputMode: AiExerciseInputMode,
    // Как пользователь вводит ответ. Определяет что рендерится в зоне ввода AI-экрана.
    // FREE_WRITE → обычный TextField снизу.
    // FILL_BLANKS → сервер парсит [___] из ответа AI и возвращает segments[].

    val wordsSource: AiExerciseWordsSource
    // Откуда брать слова из Words8r для промта.
    // Проверяется ДО нажатия [Начать задание]: если minWords не выполнен → блокируем.
)
```

---

### `UserAiExerciseStats`

> Прогресс пользователя по AI-упражнениям.  
> Создаётся только при первом повторении (lazy). Если записи нет → qRep считается 0.  
> Связан с: `AiExercise` (через `exerciseId`)  
> Подробнее: practice_plan.md → «Случайная практика», «Упражнения из теории»

```kotlin
data class UserAiExerciseStats(

    val exerciseId: String,
    // PK и FK → AiExercise.id.
    // Один пользователь — одна запись на упражнение.

    val qRep: Int
    // Количество отправленных ответов. +1 при каждом нажатии [Отправить ответ].
    // Показывается на карточке упражнения если > 0: «Повторений: N».
    // Если записи нет — qRep = 0, ничего не показываем.
)
```

---

### `FavoriteAiExercise`

> Избранные AI-упражнения пользователя.  
> Доступ: иконка ❤️ в правом верхнем углу вкладки Практика.  
> Связан с: `AiExercise` (через `exerciseId`)  
> Подробнее: practice_plan.md → «Избранные упражнения»

```kotlin
data class FavoriteAiExercise(

    val id: Int = 0,
    // PK, autoGenerate. Используется для сортировки: ORDER BY id DESC
    // = последнее добавленное упражнение отображается первым в списке.

    val exerciseId: String
    // UNIQUE, FK → AiExercise.id.
    // UNIQUE-индекс гарантирует что одно упражнение нельзя добавить в избранное дважды.
)
```

---

## Хардкодные упражнения

> Контент из assets JSON. Загружается один раз при первом запуске (INSERT OR IGNORE).  
> Подробнее: grammar8r_plan.md → «Типы хардкодных упражнений», «Логика попыток»

---

### `HardcodedExerciseType` (enum)

Используется в `CardExerciseIndex.exerciseType` — определяет в какой таблице искать упражнение.

```kotlin
enum class HardcodedExerciseType {
    WORD_ARRANGEMENT,       // перемешанные слова → собрать предложение
    MULTIPLE_CHOICE,        // пропуск → выбрать из 2–4 вариантов
    FORWARD_CHOICE,         // RU предложение → выбрать правильный EN вариант
    REVERSE_CHOICE,         // EN предложение → выбрать правильный RU перевод
    TEXT_INPUT,             // пропуск → вписать ответ вручную
    MATCHING,               // 4 пары RU↔EN → соединить линиями
    TRUE_FALSE,             // 5 предложений → отметить верно/неверно
    ERROR_CORRECTION,       // сломанное предложение → выбрать правильный вариант
    TRANSFORMATION,         // утверждение → сделай отрицание и вопрос
    CATEGORIZATION,         // слова → перетащить в колонки-категории
    TABLE_FILL,             // таблица с пропусками → вписать или выбрать формы
    FIND_THE_ODD,           // 4 слова → найти лишнее по правилу
    CONSTRUCTION_MEANING,   // грамматическая конструкция → выбрать правильный RU смысл
    DIALOG_RESTORE          // диалог с пропуском → выбрать правильную реплику
}
```

---

### `CardExerciseIndex`

> Индексная таблица: связывает карточку с её упражнениями.  
> Позволяет коду знать какие упражнения и в каком порядке показывать для данной карточки.  
> Источник: grammar8r_plan.md → «Типы хардкодных упражнений»

```kotlin
data class CardExerciseIndex(

    val id: Int = 0,
    // PK, autoGenerate. Не несёт смыслового значения.

    val cardId: String,
    // FK → GrammarCard.id. К какой карточке относится упражнение.

    val exerciseType: HardcodedExerciseType,
    // Enum — в какой таблице искать упражнение.
    // Пример: WORD_ARRANGEMENT → ищем в таблице WordArrangementExercise.

    val exerciseId: Int,
    // ID упражнения в таблице конкретного типа.
    // Пример: exerciseType=WORD_ARRANGEMENT, exerciseId=140
    // → SELECT * FROM WordArrangementExercise WHERE id = 140

    val orderInCard: Int
    // Порядок упражнения внутри карточки (начиная с 0).
    // По этому полю сортируем при загрузке упражнений карточки.
)
```

---

### `UserCardHardcodeStats`

> Прогресс пользователя по хардкодным упражнениям карточки.  
> Логика попыток (2 попытки, подсветка ошибок) — UI-стейт в ViewModel, сюда не пишется.  
> Подробнее: grammar8r_plan.md → «Логика попыток в хардкодных упражнениях»

```kotlin
data class UserCardHardcodeStats(

    val cardId: String,
    // PK, FK → GrammarCard.id. Одна запись на карточку.

    val isCompleted: Boolean
    // true = все упражнения карточки пройдены (достигнуто N/N).
    // Пишется только при полном прохождении — не после каждого упражнения.
    // Сброс (кнопка "Повторить"): UPDATE SET isCompleted = false.
    // Сброс микротемы: DELETE FROM UserCardHardcodeStats WHERE cardId IN (карточки микротемы).
)
```

---

### Таблицы упражнений по типам

Каждый тип упражнения — отдельная таблица со своими специфичными полями.  
Общая таблица с null-полями не используется — у типов принципиально разная структура данных.

Ниже показаны два примера. Остальные 12 типов описываются по мере написания контента.

#### `WordArrangementExercise` (пример)

```kotlin
data class WordArrangementExercise(

    val id: Int,
    // PK. Совпадает с exerciseId в CardExerciseIndex.
    // ⚠️ ID уникален ВНУТРИ своего типа упражнений, не глобально.
    // WordArrangement(id=1) и TrueFalse(id=1) — разные упражнения, не конфликт.
    // Перенумерация выполнена — в 1_basics.md каждый тип нумеруется от 1 независимо.

    val situationRu: String,
    // Ситуация/контекст на русском. Показывается над заданием.
    // Пример: "Ситуация: котёнок выпрыгнул из коробки."

    val correctSentence: String,
    // Правильное предложение целиком. Используется для проверки и показа при второй ошибке.
    // Пример: "The kitten jumped out of the box."

    val words: String,
    // JSON: список слов которые нужно расставить. Каждое слово содержит текст и перевод.
    // Пример: [{"text":"The kitten","translation":"котёнок"}, {"text":"jumped","translation":"прыгнул"}]

    val distractors: String,
    // JSON: лишние слова-ловушки с переводами. Пользователь должен их не использовать.
    // Пример: [{"text":"into","translation":"в (внутрь)","isDistractor":true}]

    val explanation: String
    // Объяснение почему именно такой порядок/выбор. Показывается только при второй ошибке,
    // рядом с правильным ответом и подсвеченными расхождениями.
)
```

#### `TrueFalseExercise` (пример)

```kotlin
data class TrueFalseExercise(

    val id: Int,
    // PK. Совпадает с exerciseId в CardExerciseIndex.

    val statements: String,
    // JSON: ровно 5 утверждений. Каждое содержит текст и флаг isTrue.
    // Пример: [{"text":"He go to work every day.","isTrue":false}, ...]
    // Стандарт: всегда ровно 5 утверждений — не больше, не меньше.

    val explanation: String
    // Общее объяснение правила. Показывается при любой ошибке (после второй попытки).
    // Не объяснение каждого утверждения — одно общее для всего упражнения.
)
```

---

## Прочие таблицы

---

### `DailyStats`

> Статистика по дням. Хранится только локально — не синкается на сервер.  
> Используется исключительно для экрана Статистика (calendar-вид + дневные сводки).  
> Источник: grammar8r_plan.md → «Раздел Статистика»

```kotlin
data class DailyStats(

    val date: String,
    // PK. Дата в формате "2026-04-23" по московскому времени (UTC+3).
    // Всегда MSK — сброс в 00:00 MSK синхронно с сервером.

    val topicsVisited: String,
    // JSON: список ID карточек/тем открытых в этот день.
    // Используется для дневной сводки "какие темы открывал".

    val hardcodedCount: Int,
    // Количество завершённых хардкодных упражнений за день (засчитывается при N/N).

    val aiCount: Int,
    // Количество выполненных AI-упражнений за день (каждый [Отправить ответ] = +1).

    val avgScore: Float
    // Средний score AI-упражнений за день (0.0 если aiCount = 0).
    // Показывается как "Точность: X%" на экране статистики.
)
```

---

### `course_categories`

> Таблица категорий слов курса Grammar8r. Только для `course_words` и `irregular_verbs`.  
> Words8r `categories` — отдельная таблица в Words8r DB, сюда не дублируется.  
> Загружается из assets при первом запуске (INSERT OR IGNORE).

```kotlin
data class CourseCategory(

    val id: String,
    // PK. Уникальный идентификатор категории. Пример: "basic_verbs", "verb_forms"

    val nameRus: String,
    // Отображается пользователю в экране "Учить для Grammar8r".
    // Пример: "Базовые глаголы", "Формы глаголов"

    val source: String,
    // Из какой таблицы слова этой категории: "course_words" или "irregular_verbs".
    // Клиент использует это поле чтобы знать куда идти за словами.

    val isSelected: Boolean,
    // true = категория активна в сессии "Учить для Grammar8r".
    // Пользователь может включать/выключать галочкой.

    val isPriority: Boolean
    // ПРИОРИТЕТ УРОВНЯ КАТЕГОРИИ — вся категория идёт первой в сессии "Учить для Grammar8r".
    // Выставляется когда нужно срочно выучить целый раздел (например, "Базовые глаголы" перед Present Simple).
    // ⚠️ КРИТИЧНО: при смене — сначала UPDATE SET isPriority = false для ВСЕХ категорий,
    // затем isPriority = true только для нужной. Одновременно активна только одна.
    // Отличие от CourseWord.isPriority: здесь приоритет на уровне всей категории,
    // там — на уровне конкретных слов внутри категории.
)
```

---

### `course_words`

> Слова курса Grammar8r — одиночные слова с переводом.  
> Загружаются из assets при первом запуске (INSERT OR IGNORE).  
> Разблокируются по ходу прохождения теории (завершение микротемы → BottomSheet).

```kotlin
data class CourseWord(

    val id: Int = 0,
    // PK, autoGenerate.

    val word: String,
    // Английское слово. Пример: "go", "beautiful", "however"

    val translation: String,
    // Перевод. Один или несколько через запятую. Пример: "идти", "красивый, прекрасный"

    val microtopicId: String,
    // FK → GrammarMicrotopic.id. К какой микротеме относится слово.
    // Используется при завершении микротемы: SELECT * FROM course_words WHERE microtopicId = X
    // → показываем BottomSheet с чекбоксами для этих слов.

    val categoryId: String,
    // FK → course_categories.id. К какой категории относится слово.
    // Используется для фильтрации в AiExerciseWordsSource и сессии "Учить для Grammar8r".

    val isUnlocked: Boolean,
    // false = слово ещё не показывалось пользователю (микротема не пройдена).
    // true = пользователь прошёл BottomSheet этой микротемы.
    // Только unlocked слова участвуют в AI-промтах и сессиях повторения.

    val isHidden: Boolean,
    // true = пользователь снял галочку в BottomSheet ("уже знаю это слово").
    // Такие слова пропускаются в сессиях повторения, но включаются в AI-промт.

    val isPriority: Boolean,
    // ПРИОРИТЕТ УРОВНЯ СЛОВА — конкретное слово идёт первым внутри своей категории.
    // Выставляется Grammar8r автоматически при входе в тему (например, ключевые глаголы
    // перед конкретным временем). Пользователь может менять вручную в словаре.
    // Отличие от course_categories.isPriority: здесь приоритет на уровне отдельного слова,
    // там — на уровне всей категории.
    // ⚠️ При автовыставлении перед темой: сбрасывать предыдущие isPriority = false
    // для слов той же категории, затем ставить на нужные.

    val qRep: Int
    // Количество повторений в сессии "Учить для Grammar8r". +1 за каждую сессию.
    // Фильтр для AI-промта: isUnlocked = true AND (isHidden = true OR qRep > 0)
)
```

---

### `irregular_verbs`

> Неправильные глаголы курса Grammar8r — три формы + перевод.  
> Загружаются из assets при первом запуске (INSERT OR IGNORE).  
> Структура отличается от course_words — отдельная таблица для корректной механики повторений (проверка всех трёх форм).

```kotlin
data class IrregularVerb(

    val id: Int = 0,
    // PK, autoGenerate.

    val v1: String,
    // Базовая форма. Пример: "go"

    val v2: String,
    // Past Simple. Пример: "went"

    val v3: String,
    // Past Participle. Пример: "gone"

    val translation: String,
    // Перевод глагола. Пример: "идти, ходить"

    val microtopicId: String,
    // FK → GrammarMicrotopic.id. Для разблокировки через BottomSheet.

    val categoryId: String,
    // FK → course_categories.id (source = "irregular_verbs").

    val isUnlocked: Boolean,
    // Аналогично course_words.isUnlocked.

    val isHidden: Boolean,
    // Аналогично course_words.isHidden.

    val isPriority: Boolean,
    // Аналогично course_words.isPriority — приоритет уровня слова.

    val qRep: Int
    // Количество повторений. Фильтр для AI-промта: тот же что в course_words.
    // В AI-промт глагол идёт в формате "go / went / gone".
)
```

---

### `DictionaryCache`

> Кэш переводов от LingvoLive и Yandex Dictionary.  
> Повторный запрос того же слова — мгновенно из кэша, без обращения к API.  
> Источник: grammar8r_plan.md → «Работа со словами — перевод по тапу»

```kotlin
data class DictionaryCache(

    val word: String,
    // PK. Слово в нижнем регистре для case-insensitive поиска.

    val provider: String,
    // Какой API дал ответ: "lingvolive" или "yandex".
    // Приоритет: LingvoLive → если ошибка/таймаут → Yandex.

    val transcription: String?,
    // Транскрипция слова. Nullable — не всегда возвращается API.

    val translations: String,
    // JSON: упорядоченный список переводов. Самые ходовые первыми (как отдал API).
    // Используется для попапа перевода и экрана "В мои слова".

    val responseJson: String,
    // Сырой ответ API. Хранится для возможного повторного парсинга при изменении логики.

    val fetchedAt: Long
    // Unix timestamp получения ответа. Можно использовать для инвалидации устаревшего кэша.
)
```

---

## Таблицы теории

Контент загружается из assets JSON при первом запуске (INSERT OR IGNORE).  
Дерево навигации: **Тема → Микротема → Карточка**.  
Все ID — глобальные целые числа, сквозные по всем файлам контента.

---

### `GrammarTopic`

> Верхний уровень дерева. Пользователь видит список тем на вкладке Теория.  
> Загружается из assets. Связан с: `GrammarMicrotopic`

```kotlin
data class GrammarTopic(

    val id: Int,
    // PK. Глобально уникальный ID темы. Пример: 1 = "Основы", 2 = "Present Simple"

    val title: String,
    // Название темы. Показывается в списке тем.
    // Пример: "Основы", "Present Simple", "Past Simple"

    val order: Int,
    // Порядок в списке тем. Меньше = выше. "Основы" всегда первая (order = 1).

    val isPretopic: Boolean,
    // true только для предтемы "Основы".
    // Предтема открыта для всех тиров без лимита микротем в день.

    val description: String?
    // Краткое описание темы. Показывается под названием в списке. (пока-что сомнительно, что показывается под названием темы, там уже решим как лучше)
    // Пример: "Личные местоимения, глагол to be, артикли, предлоги — фундамент"
)
```

---

### `GrammarMicrotopic`

> Второй уровень дерева. Раскрывается внутри темы.  
> Загружается из assets. Связан с: `GrammarTopic`, `GrammarCard`, `UserMicrotopicProgress`

```kotlin
data class GrammarMicrotopic(

    val id: Int,
    // PK. Глобально уникальный ID микротемы. Сквозной по всем темам.

    val topicId: Int,
    // FK → GrammarTopic.id. К какой теме относится микротема.

    val title: String,
    // Название микротемы. Показывается внутри темы.
    // Пример: "Личные местоимения", "Глагол to be"

    val order: Int
    // Порядок внутри темы. Определяет последовательность прохождения.
)
```

---

### `GrammarCard`

> Третий уровень дерева — сама карточка с теорией и упражнениями.  
> Загружается из assets. Связан с: `GrammarMicrotopic`, `CardExerciseIndex`, `AiExercise`, `UserCardProgress`

```kotlin
data class GrammarCard(

    val id: Int,
    // PK. Глобально уникальный ID карточки. Сквозной по всем темам и микротемам.

    val microtopicId: Int,
    // FK → GrammarMicrotopic.id. К какой микротеме относится карточка.

    val title: String,
    // Название карточки. Показывается в заголовке при листании.
    // Пример: "I, you, he, she, it, we, they"

    val order: Int,
    // Порядок внутри микротемы. Определяет последовательность листания карточек.

    val theory: String,
    // Основной текст теории. Может содержать markdown-таблицы и форматирование.
    // Показывается пользователю на карточке.

    val theorySummary: String,
    // Краткое резюме правила — 2–3 предложения. Показывается по кнопке "?".
    // Также передаётся в AI-промт как cardTheory при генерации упражнений.

    val examples: String,
    // JSON: список пар RU → EN. Минимум 3 пары.
    // Пример: [{"ru":"Я еду на работу.","en":"I am going to work."}]

    val clarificationOptions: String
    // JSON: список готовых вопросов для кнопки "Не совсем понял". 2–3 варианта.
    // Пример: ["Разница между a и an","Когда артикль не нужен совсем"]
)
```

---

### `UserMicrotopicProgress`

> Прогресс пользователя по микротеме.  
> Создаётся при первом открытии карточки внутри микротемы (lazy).  
> Связан с: `GrammarMicrotopic`  
> Подробнее: CLAUDE.md → «Повторное прохождение в теории»

```kotlin
data class UserMicrotopicProgress(

    val microtopicId: Int,
    // PK, FK → GrammarMicrotopic.id.

    val isCompleted: Boolean
    // true = все карточки микротемы пройдены.
    // Зелёный сегмент прогресса в списке микротем. НЕ сбрасывается при повторном прохождении.
)
```

---

### `UserCardProgress`

> Прогресс пользователя по карточке.  
> Создаётся при первом открытии карточки (lazy).  
> Связан с: `GrammarCard`

```kotlin
data class UserCardProgress(

    val cardId: Int,
    // PK, FK → GrammarCard.id.

    val isCompleted: Boolean
    // true = пользователь долистал до конца карточки (прошёл все упражнения).
    // Используется для подсчёта прогресса внутри микротемы.
    // Сброс при повторном прохождении: UPDATE SET isCompleted = false.
)
```

---

## Таблицы с незафиксированной схемой

| Таблица | Назначение | Где описана |
|---------|-----------|-------------|
| `AiRequestCounter` | Счётчик AI-запросов для дневного лимита | subscription.md |