# Старт следующей сессии — промт для Claude (после очистки контекста)

> Скормить в начало новой сессии: «прочитай `tasks/foundation/_next_session_prompt.md` и
> действуй по нему». Цель — поднять весь контекст без переразбора с нуля.
> Файл живой: в конце шага обновить «что сделано / что дальше» (или заменить на промт следующего шага).
> **Сейчас на очереди — Шаг F3: группа «ввод / сборка».** ⭐
> Перед F3 — убедиться, что сборка F1+F2 зелёная и 4 типа F2 проходятся (см. §0.1).

---

## 0. Первым делом прочитай (именно в этом порядке)

1. `CLAUDE.md` (корень) — правила: **код только по явной команде «пиши»**, общение по-русски на «Вы».
   Критично: **архитектура** (тонкий VM, логика в repo/usecase, общая механика — делегат/зонтик, не
   копипаста); `TranslatableText`/`MarkdownText` для контента; **только векторные иконки**;
   жесты/анимации — официальный API (для F4 это `AnchoredDraggable` и т.п.).
2. `tasks/foundation/execution_roadmap.md` — карта A→F, статусы. **F1 ✅, F2 ✅**, блок F3 — список типов.
   Ключевое: «14 типов сводятся к ~8–9 UI-механикам делегатом/зонтиком, не 14 копипастами».
3. `tasks/foundation/decision_log.md` — разделы «Шаг F1» и «Шаг F2»: движок, делегат `AnswerDelegate`,
   зонтик `Exercise.SingleSelect` (7 типов выбора варианта на одном рендерере), `ProgressRepository`
   (единая запись, write-once анти-чит), `AnswerNormalizer`, `ExerciseExplanation`. **F3 — 🟡, каждое
   неочевидное решение писать сюда.**
4. `tasks/phases/phase1/exercise_templates.md` — форматы типов + DB-схемы + «❌ Антипаттерны».
5. `tasks/db_schema.md` — таблицы упражнений, `CardExerciseIndex`, `UserCardProgress`, `UserExerciseResult`.

Память (MEMORY.md) подтянется сама.

## 0.1 ⏳ Перед F3 — проверить, что F1+F2 работают

Сборку/прогон делает пользователь сам. В начале сессии убедись:
- Сборка `:grammar-app:assembleDebug` зелёная. **user.db в F2 НЕ менялась** — сносить данные не нужно.
- На `basics.json` проходятся 4 типа F2: ERROR_CORRECTION, CONSTRUCTION_MEANING, DIALOG_RESTORE, FIND_THE_ODD
  (выбор варианта, 2 попытки, reveal с объяснением, прогресс/зелёный ID/сводка). Плашка `Unsupported`
  для них больше НЕ появляется.
- Если что-то не так — чинить ДО F3.

## 1. Что это за проект (коротко)

Grammar8r — Android (Compose, Room, Hilt), продакшен-уровень. Контент data-driven: теория/упражнения в
`content.db` (read-only, из assets), прогресс — в `user.db`. Идёт **Фаза 1 / фундамент**. Реальный AI,
Words8r, перевод по тапу, монетизация — заглушки. Корневой пакет `dev.sethan8r.grammar.app`.

## 2. Дисциплина работы (важно)

- **Никакого кода без явной команды «пиши»/«давай».** Сначала план/описание в чате → ждать.
- Архитектурные развилки обсуждать с мнением/рекомендацией, **НЕ давать multiple-choice**.
- Диагностические read-only скрипты — кратко описать и сразу запускать.
- **Только векторные иконки**; жесты/анимации — официальный Jetpack/Material3 API.
- Сборку гонит пользователь сам (не запускать gradle assemble автоматически).
- В конце шага: roadmap → этот файл → decision_log (🟡) → notes_for_fable → «контекст можно чистить».

## 3. Что готово к F3 — движок F1+F2 (на нём сидят все типы)

- **Сессия:** `ui/screens/exercise/ExerciseSessionScreen` + `ExerciseSessionViewModel` (тонкий) +
  `AnswerDelegate` (фазы ANSWERING/WRONG_FIRST/CORRECT/REVEALED, 2 попытки). Роут `ExerciseSessionRoute`.
- **Ответ:** `ExerciseAnswer` (sealed) = `SingleChoice` | `TextAnswers(List<String>)`. **F3 добавит сюда
  новый вариант для WORD_ARRANGEMENT** (упорядоченная сборка из слов — это НЕ SingleChoice и не TextAnswers).
- **Проверка:** `ExerciseEvaluator` (ветка по типу/зонтику) + `AnswerNormalizer` (сокращения don't=do not).
- **Рендереры:** `ui/components/exercise/` — `SingleSelectExerciseView` (+`SingleSelectHeader`, 7 типов
  выбора), `TextInputExerciseView`, `ExerciseFrame` (тряска/пульс/вибро), `ExerciseDivider`,
  `ExerciseExplanation` (общий reveal-блок), `UnsupportedExerciseView` (плашка). **F3 добавляет новые
  рендереры сюда** + заводит в `when(exercise)` сессии + в `ExerciseRepositoryImpl` (маппинг типа).
- **Домен:** `Exercise` (sealed) в `domain/model/exercise/`. `ExerciseContentMapper` (JSON→domain),
  DAO-геттеры `getWordArrangement`/`getTransformation`/`getTableFill` уже есть.
- **Прогресс:** `ProgressRepository.completeCard(...)` / `recordExerciseResult(...)` — единая запись.
- **Хелперы:** `AnswerPhase.isEditable` (правило «можно отвечать»), `parseOptions` в маппере,
  `ui/components/exercise/BlankBar` (полоска-пропуск фикс. длины вместо `____` — переиспользовать
  для слотов WORD_ARRANGEMENT / ячеек TABLE_FILL, не писать заново).

## 4. Что делать ДАЛЬШЕ — Шаг F3 (группа «ввод / сборка») 🟡 ⭐

Сначала **описать план в чате и дождаться «пиши»**. Типы:
- **TABLE_FILL** — таблица `rows[{hint, answer}]`, вписать ответ в каждую строку. Ввод текстовый — близок к
  TextInput; ответ можно представить `ExerciseAnswer.TextAnswers` (по строке на ячейку), проверка через
  `AnswerNormalizer`. Рендер — таблица с инлайн-полями.
- **TRANSFORMATION** — `taskDescription` + ровно 3 пары `items[{original, transformed}]`. Пользователь
  вписывает трансформированное предложение для каждого из 3 — снова текстовый ввод (`TextAnswers` ×3),
  проверка `AnswerNormalizer`. Отличие от TABLE_FILL — подача (original → поле ввода).
- **WORD_ARRANGEMENT** — `situationRu` + `correctSentence` + `words[]` + `distractors[]`. Сборка предложения
  из чипов (тап по слову → добавляется в ответ; повторный тап убирает). **Нужен НОВЫЙ `ExerciseAnswer`**
  (упорядоченный список выбранных токенов) + новая ветка evaluator (сравнение собранного с `correctSentence`
  через `AnswerNormalizer`) + новый рендерер с чипами. Жесты/перетаскивание не нужны — выбор тапом
  достаточно (drag — только если решишь, но официальным API). Чипы — контентные EN → `TranslatableText`/`MarkdownText`
  (попап перевода по long-press пока заглушка, но текст идёт через обёртку).
- Где механика совпадает (TABLE_FILL/TRANSFORMATION ↔ существующий TextAnswers) — переиспользовать, не плодить
  (Правило №0). Маппинг типов — в `ExerciseRepositoryImpl`, доменные варианты — в `Exercise`.
- Проверить на реальном `basics.json` (там есть WORD_ARRANGEMENT, TRANSFORMATION, TABLE_FILL — посчитать
  скриптом по seed перед стартом).
- **DoD F3:** эти типы проходятся в сессии вживую; плашка `Unsupported` для них больше не появляется.
- **decision_log** (каждое 🟡) + **notes_for_fable**. В конце — roadmap (F3 ✅), этот файл под **F4**,
  «контекст можно чистить».

## 5. Полезные команды
- Сборка (пользователь): `Set-Location E:\AndroidProjects\Grammar8r; .\gradlew.bat :grammar-app:assembleDebug --console=plain`.
- content.db в debug пересобирается сам; user.db при смене схемы — снести данные приложения (в F3 схема
  user.db, скорее всего, не меняется — проверь).

## 6. Маршрут дальше (детали — `execution_roadmap.md`)
F1 ✅ → F2 ✅ → **F3** (ввод/сборка: TABLE_FILL/TRANSFORMATION/WORD_ARRANGEMENT) →
**F4** (интерактивные: MATCHING/TRUE_FALSE/CATEGORIZATION — жесты официальным API `AnchoredDraggable` и т.п.).
**DoD после F4:** все 14 типов работают на реальном сиде; формат карточек-заданий обкатан.