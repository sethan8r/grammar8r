# Roadmap исполнения фундамента — пошагово с точками очистки контекста

> **Назначение:** операционный план «что делаем по шагам и где можно стирать контекст».
> Поверх `foundation_plan.md` (там — структура модулей/пакетов/схемы). Здесь — порядок
> исполнения, границы шагов и дисциплина токенов. Не дублирую источники — ссылаюсь.
> **Создан:** 14.06.2026, по итогам согласования в чате (Opus). Обновляется по ходу.

## Источники правды (не переписывать сюда)
- `foundation_plan.md` — структура модулей, пакеты, решения по Room/DI/навигации.
- `db_schema.md` — схемы всех таблиц и ENUM-ов (две БД: content.db / user.db).
- `tasks/phases/phase1/exercise_templates.md` — DB-схемы и форматы всех 14 типов упражнений.
- `tasks/tools/seed/*.json` — реальный контент (теория + упражнения), который рендерим.
- `kickoff_context.md` §6, §6.1, §6.2 — порядок шагов, Fable-only участки, закладки.
- `words8r_lessons.md` / `CLAUDE.md` — нормативы кода.

---

## Согласованные решения этого чата (14.06.2026)
1. **E и F (читалка теории, движок упражнений) делаем сейчас по provisional-схеме** —
   Opus пишет, ведёт `decision_log.md`, Fable делает ресёрч/аудит позже. Не ждём Fable
   (без них не проверить ни теорию, ни задания, цена ошибки сейчас низкая).
2. **Старт с шага A** (Gradle + Hilt + Room-проводка). Совпадает с `kickoff_context.md` §6
   шаг 1 «Фундамент».
3. **`composeBom` поднимаем** до свежего (под актуальные Material3/Compose API).
4. **Все 14 типов упражнений делаем за этот заход** (не 3–5). Причина: пользователь хочет
   сразу обкатать движок и ВСЕ форматы карточек-заданий в приложении, чтобы дальше смело
   писать теорию. Реализуется не 14 копипастами, а движком + делегат-механикой + ~8–9
   рендерерами (правило №0): 14 `HardcodedExerciseType` сводятся к 8–9 UI-механикам.

---

## Контуры работы
- 🟢 **Обратимый каркас** — полная скорость, цена ошибки низкая.
- 🟡 **Необратимое / provisional** — помечать изменяемым, жёсткое ревью, запись в
  `decision_log.md` + обновление `notes_for_fable.md`. Участки: схема Room, DTO-контракт,
  читалка/формат теории, движок упражнений.

## Дисциплина конца каждого шага (Definition of Done)
1. Проект собирается (сборка зелёная), приложение запускается.
2. Обновлены планы: что сделано / что дальше (в этом файле — чекбоксы ниже; в профильных
   планах — пометки «реализовано»).
3. Записан **self-prompt** для следующей сессии (в этом файле, в блоке шага) — чтобы после
   очистки контекста поднять контекст одной командой.
4. Для 🟡-шагов: запись в `decision_log.md` (каждое неочевидное решение) + обновлён
   `notes_for_fable.md` (что проверить).
5. Явно сказать пользователю: **«контекст можно чистить»**.

---

## Маршрут: A✅ → B✅ → C✅ → D1✅ → D2✅ → E✅ → F1✅ → F2✅ → F3✅ → F4✅  — ФУНДАМЕНТ ЗАВЕРШЁН

Легенда статуса: ⬜ не начато · 🟦 в работе · ✅ готово.

### ✅ Шаг A. Каркас сборки + DI  🟢 — ГОТОВО (14.06.2026)
- version catalog: добавлены Hilt, KSP, Room, kotlinx.serialization; `composeBom` поднят.
- `build.gradle.kts`: grammar-app (плагины ksp/hilt/serialization + зависимости + подключён
  `:grammar-shared`), grammar-shared (serialization-ready). grammar-server не трогали.
- `@HiltAndroidApp Grammar8rApp` (в манифесте `android:name=".Grammar8rApp"`),
  `@AndroidEntryPoint` MainActivity, пустые DI-модули `di/DatabaseModule` (object, под @Provides
  Room) и `di/RepositoryModule` (abstract, под @Binds).
- **DoD выполнен:** `:grammar-app:assembleDebug` зелёный, Hilt-граф генерируется, KSP работает.

**Self-prompt для следующей сессии (Шаг B):**
- Тулчейн проекта — июнь 2026, версии ушли далеко вперёд. **Зафиксированные рабочие версии**
  (в `gradle/libs.versions.toml`): AGP 9.1.1, Kotlin 2.2.10, Gradle 9.3.1, **Hilt 2.59.2**,
  **KSP 2.3.9** (KSP2, версионируется независимо от Kotlin — НЕ старый формат `2.2.10-x`),
  **Room 2.8.4**, composeBom 2026.05.01, kotlinx-serialization-json 1.11.0,
  hilt-navigation-compose 1.3.0, navigation-compose 2.8.0.
- ⚠️ **AGP 9 убрал старый `BaseExtension`** — Hilt < 2.59 падает с «Android BaseExtension not
  found». Нужен Hilt ≥ 2.59. Если версия библиотеки не резолвится — проверять реальные версии
  через maven-metadata.xml (Google Maven `dl.google.com/dl/android/maven2/...`, Maven Central
  `repo1.maven.org/maven2/...`), не гадать.
- AGP 9 имеет **встроенную поддержку Kotlin** — отдельный плагин `kotlin-android` не нужен и не
  подключён. Не добавлять.
- Пакеты создаём по мере появления файлов (git не хранит пустые папки). Сейчас есть только
  `di/`, `ui/` (theme, screens, components). Дерево-ориентир — `foundation_plan.md` §2.
- Дальше — Шаг B: Entity обеих БД по `db_schema.md` + DAO + TypeConverters + `exportSchema=true`
  (ksp arg `room.schemaLocation` → `$projectDir/schemas`). Полные схемы упражнений —
  `tasks/phases/phase1/exercise_templates.md`. Это 🟡 Fable-review: вести `decision_log.md` и
  `notes_for_fable.md`.
- **Сборку запускать так:** `.\gradlew.bat :grammar-app:assembleDebug --console=plain`
  (PowerShell, из корня; первый прогон ~3 мин).

### ✅ Шаг B. Схема Room — 2 БД  🟡 Fable-review — ГОТОВО (14.06.2026)
- content.db (22 Entity): теория (4), упражнения (12 типов + `CardExerciseIndex` + `AiExercise`),
  слова курса (`course_word_groups/categories/words`, `irregular_verbs`). Entity разнесены по
  подпакетам `entity/{theory,exercise,word}/`. Словарь Words8r отложен в Фазу 2 (по kickoff §6.2).
- user.db (10 Entity): весь прогресс/настройки/кэш. `AiRequestCounter` — провизорная `(date,count)`.
- DAO: content — `TheoryDao`/`ExerciseDao`/`CourseWordDao` (read-only); user —
  `ProgressDao`/`WordProgressDao`/`StatsDao`/`DictionaryCacheDao` (`@Upsert`).
- `Converters` (enum↔String) на обе БД; JSON-поля хранятся `String`, разбор — в маппере (Шаг E).
- `AiExerciseInputMode` → `grammar-shared`; клиентские enum-ы → `domain/model`.
- `exportSchema=true`, ksp arg `room.schemaLocation=$projectDir/schemas`; схемы в VCS
  (`grammar-app/schemas/…/1.json`). content.db — destructive fallback + `createFromAsset`
  (открытие ленивое, файл будет в C); user.db — без destructive fallback.
- **DoD выполнен:** `:grammar-app:assembleDebug` зелёный, схемы экспортированы (22+10 таблиц).
- **decision_log:** 7 записей от 2026-06-14. **notes_for_fable:** раздел «Шаг B».

**Self-prompt для следующей сессии (Шаг C — сидинг content.db):**
- Схема Room уже экспортирована в `grammar-app/schemas/`: `ContentDatabase/1.json` (22 таблицы),
  `UserDatabase/1.json` (10). Имена таблиц/колонок там — ИСТОЧНИК для генерации `CREATE TABLE`.
- Задача C: `json_to_db.py` собирает `seed/*.json` → `content.db`, **CREATE TABLE генерируется
  ИЗ экспортированной схемы** (не вручную) — identity hash обязан совпасть, иначе краш при
  `createFromAsset`. Сиды: `tasks/tools/seed/basics.json`, `transcription.json` (ключи верхнего
  уровня = имена таблиц: `grammar_topics`, `grammar_cards`, …, `course_words` и т.д.).
- Enum-поля в колонках — строкой `.name` (`WORD_ARRANGEMENT`, `FREE_WRITE`, `CHOICE`, `GENERAL`),
  иначе `Converters.valueOf` упадёт. Bool — 0/1, nullable — NULL.
- Gradle-таск зовёт `py json_to_db.py`, кладёт `content.db` в `grammar-app/src/main/assets/`,
  зависимость на `mergeDebugAssets`/`preBuild`. **content.db НЕ коммитим** (build-артефакт).
  В CI нужен setup-python.
- **DoD C:** приложение открывает content.db без краха identity; тест-запрос читает темы/карточки.
- Сборка: `Set-Location E:\AndroidProjects\Grammar8r; .\gradlew.bat :grammar-app:assembleDebug --console=plain`.

### ✅ Шаг C. Конвейер сидинга content.db  🟡 — ГОТОВО (14.06.2026)
- `tasks/tools/json_to_db.py`: DDL/индексы/identity из экспортированной схемы; данные из
  `seed/**/*.json` (кроме `*_prompts.json`); вставка по пересечению колонок; `INSERT OR IGNORE`
  для общих таблиц-разделов; list/dict→JSON-строка; в файл пишутся `room_master_table`+`user_version`.
- Gradle: `generateContentDb` (`Exec`, `mustRunAfter ksp*`, `merge*Assets dependsOn`) → content.db
  в `src/main/assets/`. content.db в `.gitignore` (build-артефакт), схемы — в VCS.
- ⚠️ **Пересмотр Шага B:** `MultipleChoiceExercise` PK → составной `(id, choiceType)`, схема
  ре-экспортирована (новый identityHash). Сид-ключ `summary→theorySummary` (правка md_to_json).
- **DoD выполнен:** `assembleDebug` зелёный, content.db (1.06 МБ, 1492 строки, 22 таблицы) в APK;
  offline-проверка sqlite (identity_hash + user_version + структура совпали со схемой); on-device
  подтверждено логом `Grammar8rDbCheck: тем=5, микротем(тема 1)=25` (Room открыл БД без краха).
  Временный зонд из MainActivity удалён.
- **decision_log:** 7 записей Шага C. **notes_for_fable:** раздел «Шаг C».

**Self-prompt для следующей сессии (Шаг D1 — навигация type-safe + строки + TranslatableText):**
- content.db собирается автоматически при сборке (Gradle `generateContentDb`). MainActivity — чистый
  (временный зонд БД уже удалён).
- D1: переписать строковую навигацию `MainActivity` (`Screen(route:String)` + `navigate(route)`)
  на type-safe `@Serializable`-роуты (Navigation Compose 2.8). Белый список корневых вкладок для
  видимости навбара. Все интерфейсные строки → `strings.xml` (на «Вы»). Создать
  `ui/components/TranslatableText.kt` — тонкую обёртку-пустышку над `Text` (тот же API).
- **DoD D1:** 4 таба на type-safe навигации; контентный текст готов идти через `TranslatableText`.
- Сборка: `Set-Location E:\AndroidProjects\Grammar8r; .\gradlew.bat :grammar-app:assembleDebug --console=plain`.

### ✅ Шаг D1. Навигация type-safe + строки + TranslatableText  🟢 — ГОТОВО (14.06.2026)
- `ui/navigation/TopLevelDestination.kt`: `sealed interface TopLevelRoute` + 4 `@Serializable data object`
  (`LearnRoute/PracticeRoute/StatisticsRoute/MenuRoute`) + `enum TopLevelDestination(route,labelRes,icon)`
  — единый источник правды (навбар и белый список итерируют `entries`).
- `MainActivity`: `NavHost(startDestination=LearnRoute)`, `composable<LearnRoute>{…}` и т.д.;
  навигация `navController.navigate(destination.route){popUpTo(startDest){saveState};launchSingleTop;restoreState}`;
  выбранность/видимость через `hasRoute(route::class)`. Навбар вынесен в `ui/components/Grammar8rBottomBar.kt`
  (insets там же, как было). `BackHandler` не вводился.
- Белый список: `bottomBar` рисуется только если текущий роут ∈ `TopLevelDestination.entries`.
- ⚠️ **Решение по структуре навбара (фидбек 14.06):** было 6 табов в `grammar8r_plan.md`, сведено к 4 —
  **Учить** (теория+слова) · **Практика** (AI) · **Статистика** · **Меню** (профиль+словарь). «Теория»→«Учить»
  (`LearnRoute`, рендерит пока `TheoryScreen`-заглушку). План обновлён, фидбек — в памяти `project_nav_tabs`.
- `strings.xml`: все интерфейсные строки (табы, заглушки, всё меню) на «Вы»; экраны на `stringResource`,
  github-URL — `private const` в `MenuScreen`.
- `ui/components/TranslatableText.kt`: пустышка-обёртка над `Text`, 2 перегрузки (`String` + `AnnotatedString`
  с `inlineContent` под читалку). ⚠️ Грабли: у `Text(AnnotatedString)` `onTextLayout` non-null → в той
  перегрузке параметр `(TextLayoutResult)->Unit = {}`, не nullable.
- Побочно: `.idea/deviceManager.xml` (локальный файл AS) убран из стейджа + добавлен в `.gitignore`.
- **DoD выполнен:** `:grammar-app:assembleDebug` зелёный; 4 таба на type-safe навигации; контентный текст
  готов идти через `TranslatableText`.
- **Очистка контекста:** ✅ да.

**Self-prompt для следующей сессии (Шаг D2 — серверные заглушки + DTO):**
- Навигация/строки/`TranslatableText` готовы. Корневой пакет `dev.sethan8r.grammar.app`. `grammar-shared`
  пока с пустым `Models.kt`-плейсхолдером (удалить при наполнении DTO).
- D2 (🟡 Fable-review): domain-интерфейсы заглушек сервера — `AuthRepository`, `EntitlementsProvider`
  (полный, без лимита «слов в промте»), `AiExerciseRepository`, `DictionaryRepository`, синк прогресса;
  `Fake*`-реализации + Hilt-биндинги (в `RepositoryModule`); dev/prod через `BuildConfig`, не комментарии.
  DTO запросов/ответов → `grammar-shared` (`@Serializable`), удалить `Models.kt`.
- Перед кодом — описать план в чате, дождаться «пиши». Вести `decision_log.md` + `notes_for_fable.md`.
- Сборка: `Set-Location E:\AndroidProjects\Grammar8r; .\gradlew.bat :grammar-app:assembleDebug --console=plain`.

### ✅ Шаг D2. Серверные заглушки + DTO  🟡 Fable-review — ГОТОВО (14.06.2026)
- domain-интерфейсы (`domain/repository/`): `AuthRepository`, `EntitlementsProvider` (полный, без лимита
  «слов в промте»), `AiExerciseRepository` (generate/evaluate/**clarify**), `DictionaryRepository`,
  `ProgressSyncRepository`.
- доменные модели (`domain/model/`): `Entitlements` (+`hasAiQuota`), `AuthSession`, `GeneratedExercise`/
  `ExerciseEvaluation`/`ClarificationAnswer`, `WordTranslation`, `ProgressEvent`/`ProgressSnapshot`,
  `ApiResult<T>`+`RequestError`. Даты — `java.time`.
- `Fake*` (`data/repository/fake/`, 5 шт) + `@Binds @Singleton` в `RepositoryModule`. dev/prod —
  `@DebugBuild Boolean` из `AppConfigModule` (BuildConfig.DEBUG в одном месте); `FakeEntitlementsProvider`
  debug→ADMIN, release→FREE. Включён `buildFeatures.buildConfig=true`.
- DTO в `grammar-shared` (`@Serializable`): `SubscriptionTier`(+`userVisible`), auth, `SubscriptionDto`,
  AI-упражнения (generate/evaluate/clarification), `ProgressEventType`/прогресс. `Models.kt` удалён.
- ⚠️ Контрактный enum кодов ошибок + маппер — отложены до remote-слоя (Фаза 4), чтобы не было мёртвого кода.
  `DictionaryRepository` — без DTO в shared (внешние словари, не наш контракт). Отложены (YAGNI):
  промо/оплата/AI-диалог/админ/голоса/FCM/announcements.
- **DoD выполнен:** `:grammar-app:assembleDebug` зелёный, Hilt-граф поднимается, `Models.kt` удалён, варнингов нет.
- **decision_log:** 5 записей D2. **notes_for_fable:** раздел «Шаг D2». **Очистка контекста:** ✅ да.

**Self-prompt для следующей сессии (Шаг E — читалка теории) ⭐ ГЛАВНЫЙ РИСК:**
- Заглушки сервера готовы (5 интерфейсов + Fake + DI). `EntitlementsProvider` в debug = топ-тир (всё открыто).
  Контентный текст уже идёт через `TranslatableText`-пустышку (D1). content.db собирается при сборке.
- E: `TheoryScreen` (темы+прогресс) → `TopicScreen` (микротемы) → `MicrotopicScreen` (листание карточек).
  Рендер блоков теории `paragraph/heading/list/table/callout` + инлайн `**жирное**`/`*курсив*` →
  `AnnotatedString`. Контентный текст — через `TranslatableText`. `TheoryRepository` (combine content+user),
  use cases, тонкие ViewModel (`@HiltViewModel`).
- ⚠️ ГЛАВНОЕ: **обкатать реальный `tasks/tools/seed/basics.json`** — как блоки теории парсятся из сырого
  JSON-`String` в `GrammarCard.theory` (разбор в domain-маппере, решение Шага B). Правки `md_to_json.py`/
  формата — совместно с пользователем. По итогу пометить в планах: формат обкатан / что поправили.
- Источник по формату блоков: `tasks/theory_content_guide.md` (§8 формат), `db_schema.md` (`GrammarCard`).
- Перед кодом — план в чате, дождаться «пиши». Вести `decision_log.md`.
- Сборка: `Set-Location E:\AndroidProjects\Grammar8r; .\gradlew.bat :grammar-app:assembleDebug --console=plain`.

### ✅ Шаг E. Читалка теории  🟡 Fable-only (по provisional)  ⭐ — ГОТОВО (обкатано 14.06.2026)
- ✅ Реализовано (сборка зелёная, без варнингов, 2026-06-14):
  - domain-модели: `TheoryBlock`(sealed)+`CalloutVariant`, `TheoryCard`/`Example`, `TheoryListItem`/
    `TopicSummary`/`MicrotopicSummary`+`MicrotopicState`(enum), `TheoryData`.
  - data: `TheoryJsonModels`(суррогаты)+`TheoryContentMapper`(разбор JSON), `SerializationModule`(Json).
  - `TheoryRepository`(+Impl, combine content+user) · `GetTheoryListUseCase`(чистая группировка).
  - DAO: `getAllMicrotopics`/`observeMicrotopic`. Биндинг в `RepositoryModule`.
  - UI: `TheoryScreen`/`TopicScreen`/`MicrotopicScreen` (+3 тонких VM), роуты `TopicRoute`/
    `MicrotopicRoute` в `MainActivity`. Рендер блоков — `ui/components/TheoryBlockView.kt`; инлайн
    `**…**`/`*…*` + вердикт ✓/✗ векторными иконками — `ui/components/InlineMarkdown.kt`. `BackTopBar`.
  - токены `ui/theme/Dimens.kt` + `IncorrectRed`; dep `lifecycle-runtime-compose`. Слоты Фазы 3 — задизейблены.
- ✅ ОБКАТАНО на устройстве (правки по фидбеку, всё в коде + планах):
  - **callout = вложенные блоки** (`Callout.blocks`, не строка) — в плашку лезут списки/таблицы; 3 формы
    записи + plain-text «Кстати:». **`divider`** из `paragraph "---"` (в маппере). Вердикт ✓/✗ и стрелки
    `→` — векторные иконки Material (`InlineMarkdown`/`MarkdownText`).
  - двойные имена микротем `EN · RU` (`DualTitle`; в шапке карточки — только EN); раздел = сворачиваемый
    фрейм + `InfoBubble` (попап-описание); `SegmentedProgressBar`; стрелка «Назад» белая; ID карточки в углу.
  - карточки НЕ свайпаются; низ карточки — система кнопок (Не совсем понял / Перейти к заданиям /
    Перейти к умному заданию); content.db debug-авто-рефреш в `DatabaseModule`.
  - `domain/model/` разнесён по подпакетам (theory/exercise/auth/subscription/progress/dictionary/common);
    правило «пакеты по смыслу» добавлено в CLAUDE.md.
  - конвейер `md_to_json.py` (3 формы плашек), `theory_content_guide.md` §8/§2, `db_schema.md`,
    `exercise_templates.md`, `verify_dump.py` — синхронизированы. `check.py` зелёный по всем 5 темам.
- **DoD выполнен:** теория из реального сида рендерится корректно; пользователь прошёлся по карточкам.
- **decision_log:** раздел «Шаг E» (включая callout→blocks, divider в маппере). **Очистка контекста:** ✅ да.

**Self-prompt для следующей сессии (Шаг F1):** развёрнут в `_next_session_prompt.md` (переписан под F1).

### ✅ Шаг F1. Ядро движка упражнений  🟡 Fable-only (по provisional)  ⭐ — НАПИСАНО (2026-06-16, сборку гонит пользователь)
- ✅ Реализовано:
  - **Движок/сессия:** `ExerciseSessionScreen` (+ тонкий VM) — scaffold (X из N, кнопка `?` с
    `theorySummary` векторной иконкой, ID упражнения в углу, слот `explanation`). Роут
    `ExerciseSessionRoute(cardId)` (полноэкранный). Делегат-механика `AnswerDelegate` (свой `StateFlow`,
    2 попытки, «Далее» после ответа) — включается композицией, переиспользуема всеми типами.
  - **Типы (2):** «выбор варианта» `ChoiceExerciseView` (покрывает MULTIPLE_CHOICE/FORWARD_CHOICE/
    REVERSE_CHOICE — одна таблица, один UI) + `TextInputExerciseView`. Остальные 11 → плашка
    `UnsupportedExerciseView` (F2–F4). Проверка — чистый `ExerciseEvaluator` + `AnswerNormalizer`
    (сокращения don't=do not и т.п.).
  - **Данные:** `ExerciseRepository`(+Impl) читает `CardExerciseIndex` (orderInCard) → упражнение по
    типу (CHOICE-трек по составному PK) → `ExerciseContentMapper` (JSON→domain). `Exercise` sealed.
  - **Прогресс — единая точка `ProgressRepository`(+Impl):** `completeCard()` пишет
    `UserCardProgress.isCompleted` + write-once счёт `UserCardHardcodeStats(correctFirstTry,total)`,
    отмечает микротему при N/N. Дубль `isCompleted` убран; `UserCardHardcodeStats` переформована.
  - **Guard выхода:** общий `ExitConfirmationHandler` (`PredictiveBackHandler` + диалог), 1 раз.
  - **Экран сводки микротемы** `MicrotopicSummaryScreen` (+VM, роут `MicrotopicSummaryRoute`):
    «верно X из N» по хардкоду (AI — Фаза 3, скрыта), «Далее» → список микротем, наведённый на
    пройденную (`focusMicrotopicId` через savedStateHandle, `TopicScreen` скроллит).
  - **Переход:** `MicrotopicScreen` «Перейти к заданиям» → сессия карточки; между карточками — полоса
    прогресса (frontier по `completedCardIds`).
  - Спавшие в Шаге E элементы (бар completion-based, зелёный фрейм, галочка микротемы) теперь оживают
    реальной записью `isCompleted`.
- **⏳ Осталось пользователю:** собрать (`assembleDebug`) — **снести данные приложения** (user.db
  переэкспортирован: удалён `isCompleted` из `UserCardHardcodeStats`, добавлены поля), пройти сквозной
  флоу на `basics.json` и подтвердить. На устройстве проверить: 2 попытки → показ правильного без
  удаления ответа; запись прогресса (бар/фрейм/галочка); экран сводки только при полном завершении
  микротемы + наведение списка.
- **decision_log:** раздел «Шаг F1» (7 записей). **CLAUDE/db_schema/grammar8r_plan** синхронизированы.
- **Self-prompt:** переписан в `_next_session_prompt.md` под **F2**.

### ✅ Шаг F2. Группа «выбор варианта»  🟡 — ГОТОВО (2026-06-19, сборку гонит пользователь)
MULTIPLE_CHOICE / FORWARD_CHOICE / REVERSE_CHOICE / ERROR_CORRECTION / CONSTRUCTION_MEANING /
DIALOG_RESTORE / FIND_THE_ODD — на одном базовом компоненте выбора, различие в подаче условия.
- ✅ Реализовано:
  - **Зонтик `Exercise.SingleSelect`** (sealed sub-interface, `options/explanation/type`): `Choice` его
    реализует, добавлены `ErrorCorrection`/`ConstructionMeaning`/`DialogRestore`/`FindTheOdd`. Движок
    (evaluator, VM `refOf`/`prepareForIndex`/`canCheck`, рендер-ветка) свёрнут на `is SingleSelect` —
    одна ветка на все 7 типов.
  - **Маппер** (+4 метода, общий `parseOptions`), **репозиторий** (+4 ветки; DAO-геттеры уже были).
    FIND_THE_ODD: `items[{text,isOdd}]` → `Option(text, isCorrect=isOdd)`.
  - **UI:** `SingleSelectExerciseView` (бывш. `ChoiceExerciseView`, обобщён слотом-шапкой) +
    `SingleSelectHeader` (5 шапок: Choice / Statement / Instruction / Dialog). reveal красит зелёным
    ВСЕ верные (multi-correct у ErrorCorrection). Строки `exercise_desc_*` (+4).
  - **Чистка дублей (Правило №0):** `AnswerPhase.isEditable` (1 источник, 3 копии убраны),
    `ExerciseExplanation` (общий reveal-блок, 2 копии убраны), `parseOptions`. `ChoiceExerciseView` удалён.
  - Плашка `Unsupported` для этих 4 типов больше не появляется (остаются TABLE_FILL/WORD_ARRANGEMENT/
    TRANSFORMATION/MATCHING/TRUE_FALSE/CATEGORIZATION → F3–F4).
- **⏳ Осталось пользователю:** собрать (`assembleDebug`), пройти на `basics.json` 4 новых типа
  (ERROR_CORRECTION 37, FIND_THE_ODD 13, DIALOG_RESTORE 9, CONSTRUCTION_MEANING 5). user.db НЕ менялась —
  сносить данные не нужно.
- **decision_log:** раздел «Шаг F2». **notes_for_fable:** раздел «3b Шаг F2».
- **Self-prompt:** переписан в `_next_session_prompt.md` под **F3**.
- **Очистка контекста:** ✅ да.

### 🟩 Шаг F3. Группа «ввод / сборка»  ✅ — ОБКАТАН И ОДОБРЕН пользователем (2026-06-22)
WORD_ARRANGEMENT (Duolingo-style drag-сборка с рефлоу чипов), TRANSFORMATION (3 поля ввода),
TABLE_FILL (плавающие поля как в TextInput).
- ✅ Реализовано: домен (`Exercise.TableFill/Transformation/WordArrangement` + модели,
  `ExerciseAnswer.WordOrder`), `ExerciseEvaluator` (вердикт all-or-nothing), `ExerciseContentMapper`
  (+3), `ExerciseRepositoryImpl` (+3 ветки), VM (prepareForIndex/refOf/canCheck/`onArrangementChanged`),
  `AnswerNormalizer` (игнор пунктуации + `matches()`), общий `ExerciseInputField` (Правило №0, TextInput
  переведён), рендереры `TableFillExerciseView`/`TransformationExerciseView`/`WordArrangementExerciseView`
  (drag+рефлоу через `ui/util/AnimatePlacement.kt`), `ExerciseSessionScreen` (ветки + `key(currentIndex)`),
  строки. Плашка `Unsupported` для этих 3 типов больше не появляется.
- ⏳ **Осталось пользователю:** собрать (`assembleDebug`, user.db НЕ менялась — сносить не нужно), пройти
  на сидах (WORD_ARRANGEMENT 50, TRANSFORMATION 14, TABLE_FILL 9), оценить drag-ощущения и вердикт.
- **decision_log:** раздел «Шаг F3». **Self-prompt:** `_next_session_prompt.md` (под F4 после обкатки).

### 🟩 Шаг F4. Группа «интерактивные»  ✅ — ОБКАТАН И ОДОБРЕН (2026-06-22)
TRUE_FALSE (multi-select «отметь верные», НЕ тоггл), MATCHING (две колонки, левая закреплена, правую
переставляют вертикальным drag-reorder — БЕЗ ниточек), CATEGORIZATION (drag из пула в колонки и обратно,
пресайз элементов под колонку).
- ✅ Реализовано: домен (`Exercise.TrueFalse/Matching/Categorization` + модели `Statement/MatchPair/Category`,
  `ExerciseAnswer.MultiChoice/Pairing/Buckets`), `ExerciseEvaluator` (+3 ветки, all-or-nothing),
  `ExerciseContentMapper` (+3 `toTrueFalse/toMatching/toCategorization` + DTO), `ExerciseRepositoryImpl`
  (+3 ветки → `when` стал ИСЧЕРПЫВАЮЩИМ, все 14 типов, `else` убран), VM
  (refOf/prepareForIndex/canCheck/`onStatementToggled`/`onPairingChanged`/`onCategorizationChanged`),
  `ExerciseSessionScreen` (ветки + лейблы), строки. **Анти-дубль:** вынесены общий `AnswerOptionSurface`
  (single+multi-select) и `ExerciseChip`+`hitTest` — `SingleSelectExerciseView` и
  `WordArrangementExerciseView` переведены на них.
- ✅ Рендереры: `TrueFalseExerciseView` (multi-select), `MatchingExerciseView` (вертикальный
  drag-reorder), `CategorizationExerciseView` (drag пул↔колонки, пресайз через `BoxWithConstraints`).
  Жесты — официальными кирпичами + ручной drag с рефлоу (`AnimatePlacement`) по протоколу костылей.
  Плашка `Unsupported` для этих 3 типов больше не появляется.
- ✅ **Обкатано на устройстве (2026-06-22):** все 3 типа проходятся на basics-сиде; по фидбэку
  допилены drag-со-скроллом (общий `detectChipDrag`, скролл длинных заданий) и плавный «переезд»
  чипа CATEGORIZATION (оверлей-анимация при сбросе). Заодно починен баг навигации (сводка показывалась
  после первой карточки на повторном проходе → теперь по последней карточке, `CardCompletion.isLastCard`).
- ✅ **DoD достигнут:** все 14 типов работают на реальном сиде; формат карточек-заданий обкатан.
- **decision_log:** раздел «Шаг F4».

> **🏁 ФУНДАМЕНТ A→F ЗАВЕРШЁН.** Движок упражнений (все 14 типов) + читалка теории + конвейер
> MD→JSON→content.db обкатаны. Дальше — НЕ шаги фундамента, а основная работа: писать теорию/упражнения
> (контент) на готовом движке. Точка входа — `_next_session_prompt.md` (раздел «Дальше»),
> `tasks/theory_content_guide.md`, `tasks/grammar8r_plan.md`. Отложенные фазы (реальный AI, Words8r-синк,
> перевод по тапу, подписка) — это Фазы 2–4, не «следующий шаг».

---

## Сводка «где Fable обязательно смотрит потом»
B (схема Room), C (сидинг/identity), D2 (DTO-контракт), E (читалка/формат теории), F1–F4
(движок и общая механика упражнений). Всё это пишется как `provisional` с `decision_log.md`.