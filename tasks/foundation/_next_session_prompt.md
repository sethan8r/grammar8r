# Старт следующей сессии — промт для Claude (после очистки контекста)

> Скормить в начало новой сессии: «прочитай `tasks/foundation/_next_session_prompt.md` и
> действуй по нему». Цель — поднять весь контекст без переразбора с нуля.
> Файл живой: в конце шага обновить «что сделано / что дальше» (или заменить на промт следующего шага).
> **Сейчас на очереди — Шаг F2: группа «выбор варианта» (остальные типы на готовом движке F1).** ⭐
> Перед F2 — убедиться, что сборка F1 зелёная и сквозной флоу работает (см. §0.1).

---

## 0. Первым делом прочитай (именно в этом порядке)

1. `CLAUDE.md` (корень) — правила: **код только по явной команде «пиши»**, общение по-русски на «Вы».
   Критично для движка: **«Навигация» → guard `PredictiveBackHandler`** (уже есть общий
   `ui/components/ExitConfirmationHandler`); **«Повторное прохождение» (АНТИ-ЧИТ)** — `isCompleted` не
   снимать, счёт `UserCardHardcodeStats` не перезаписывать; **архитектура** (тонкий VM, логика в
   repo/usecase, общая механика — делегат, не копипаста); `TranslatableText`/`MarkdownText` для контента;
   **только векторные иконки, никаких символов-текстом**.
2. `tasks/foundation/execution_roadmap.md` — карта A→F, статусы. **F1 ✅ (написан 16.06)**, блок F2 —
   список типов. Ключевое: «14 типов сводятся к ~8–9 UI-механикам делегатом, не 14 копипастами».
3. `tasks/foundation/decision_log.md` — раздел «Шаг F1» (7 решений: дубль `isCompleted` убран;
   `UserCardHardcodeStats`→`correctFirstTry/total` write-once; `ProgressRepository` — единая запись;
   `Unsupported`-плашка; делегат+`ChoiceExerciseView` на 3 трека; `AnswerNormalizer`; экран сводки).
   **F2 — 🟡, каждое неочевидное решение писать сюда.**
4. `tasks/phases/phase1/exercise_templates.md` — форматы типов + DB-схемы + «❌ Антипаттерны».
5. `tasks/db_schema.md` — таблицы упражнений, `CardExerciseIndex`, `UserCardProgress`,
   `UserCardHardcodeStats` (обновлены), ENUM `HardcodedExerciseType`/`ChoiceType`.

Память (MEMORY.md) подтянется сама.

## 0.1 ⏳ Перед F2 — проверить, что F1 действительно работает

F1 написан, но сборку/прогон делает пользователь сам. В начале сессии спроси/убедись:
- Сборка `:grammar-app:assembleDebug` зелёная. ⚠️ user.db переэкспортирован (поля
  `UserCardHardcodeStats` изменены) → на устройстве **снести данные приложения**, иначе Room упадёт
  (у user.db нет destructive fallback).
- Сквозной флоу на `basics.json`: теория → «Перейти к заданиям» → выбор варианта / ввод → 2 попытки
  (1-я ошибка «попробуйте ещё раз»; 2-я — правильный ответ БЕЗ удаления ответа юзера + explanation) →
  «Далее» → прогресс записан (бар/зелёный фрейм/галочка) → при полном завершении микротемы экран сводки
  «верно X из N» → «Далее» возвращает в список, наведённый на микротему.
- Если что-то не так — чинить ДО F2.

## 1. Что это за проект (коротко)

Grammar8r — Android (Compose, Room, Hilt), продакшен-уровень. Контент data-driven: теория/упражнения в
`content.db` (read-only, из assets), прогресс — в `user.db`. Идёт **Фаза 1 / фундамент**. Реальный AI,
Words8r, перевод по тапу, монетизация — заглушки. Корневой пакет `dev.sethan8r.grammar.app`.

## 2. Дисциплина работы (важно)

- **Никакого кода без явной команды «пиши»/«давай».** Сначала план/описание в чате → ждать.
- Архитектурные развилки обсуждать с мнением/рекомендацией, **НЕ давать multiple-choice**.
- Диагностические read-only скрипты — кратко описать и сразу запускать.
- **Только векторные иконки** (Material, `material-icons-extended` подключён) — никаких символов текстом.
- Сборку гонит пользователь сам (не запускать gradle assemble автоматически).
- В конце шага: roadmap → этот файл → decision_log (🟡) → «контекст можно чистить».

## 3. Что готово к F2 — движок F1 (на нём сидят все типы)

- **Сессия:** `ui/screens/exercise/ExerciseSessionScreen` + `ExerciseSessionViewModel` (тонкий) +
  `AnswerDelegate` (фазы ANSWERING/WRONG_FIRST/CORRECT/REVEALED, 2 попытки). Роут `ExerciseSessionRoute`.
- **Рендереры:** `ui/components/exercise/` — `ChoiceExerciseView` (3 трека выбора варианта),
  `TextInputExerciseView`, `UnsupportedExerciseView` (плашка). **F2 добавляет новые рендереры сюда** и
  заводит их в `when(exercise)` сессии + в `ExerciseRepositoryImpl` (маппинг типа).
- **Домен:** `Exercise` (sealed) в `domain/model/exercise/` — добавлять варианты тут. `ExerciseAnswer`
  (SingleChoice/TextAnswers — при новых механиках расширять). `ExerciseEvaluator` (проверка),
  `AnswerNormalizer` (сокращения).
- **Данные:** `ExerciseRepository`(+Impl), `ExerciseContentMapper` (JSON→domain).
- **Прогресс:** `ProgressRepository.completeCard(...)` — единая запись, не трогать в обход.
- **Сводка:** `MicrotopicSummaryScreen` (хардкод «X из N»; AI — Фаза 3).

## 4. Что делать ДАЛЬШЕ — Шаг F2 (группа «выбор варианта») 🟡 ⭐

Сначала **описать план в чате и дождаться «пиши»**. Суть: на готовом движке F1 добавить типы, которые
сводятся к выбору варианта / близким механикам, переиспользуя `ChoiceExerciseView` где можно:
**ERROR_CORRECTION** (сломанное EN + 3 варианта — как Choice), **CONSTRUCTION_MEANING** (конструкция +
4 RU-перевода — Choice), **DIALOG_RESTORE** (диалог + 3 реплики — Choice с особой подачей условия),
**FIND_THE_ODD** (4 элемента, выбрать лишний — single-select без «prompt с пропуском»).
- Где можно — тот же `ChoiceExerciseView`/single-select делегат; различие только в подаче условия
  (Правило №0, не плодить копии). Маппинг типов — в `ExerciseRepositoryImpl`, доменные варианты — в `Exercise`.
- Проверить на реальном `basics.json` (там есть ERROR_CORRECTION 37, FIND_THE_ODD 13, DIALOG_RESTORE 9,
  CONSTRUCTION_MEANING 5).
- **DoD F2:** эти типы проходятся в сессии вживую; плашка `Unsupported` для них больше не появляется.
- **decision_log** (каждое 🟡). В конце — обновить roadmap (F2 ✅), этот файл под **F3**, «контекст можно чистить».

## 5. Полезные команды
- Сборка (пользователь): `Set-Location E:\AndroidProjects\Grammar8r; .\gradlew.bat :grammar-app:assembleDebug --console=plain`.
- content.db в debug пересобирается сам; user.db при смене схемы — снести данные приложения.

## 6. Маршрут дальше (детали — `execution_roadmap.md`)
F1 ✅ → **F2** (выбор варианта: ERROR_CORRECTION/CONSTRUCTION_MEANING/DIALOG_RESTORE/FIND_THE_ODD) →
**F3** (ввод/сборка: WORD_ARRANGEMENT/TRANSFORMATION/TABLE_FILL) → **F4** (интерактивные:
MATCHING/TRUE_FALSE/CATEGORIZATION — жесты официальным API).