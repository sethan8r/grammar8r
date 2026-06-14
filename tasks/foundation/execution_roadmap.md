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

## Маршрут: A → B → C → D1 → D2 → E → F1 → F2 → F3 → F4

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

### ⬜ Шаг C. Конвейер сидинга content.db  🟡
- `json_to_db.py`: `CREATE TABLE` генерируется ИЗ экспортированной Room-схемы (identity hash
  обязан совпасть). Gradle-таск: `py json_to_db.py` → `content.db` в `assets/` → зависимость на
  mergeAssets. content.db НЕ коммитим.
- `createFromAsset` в `DatabaseModule` + bump-версия (destructive fallback только для content.db).
- **DoD:** приложение открывает content.db без краха identity, тест-запрос читает темы/карточки.
- **Очистка контекста:** ✅ да. **decision_log.**
- **Self-prompt:** _<…>_

### ⬜ Шаг D1. Навигация type-safe + строки + TranslatableText  🟢
- Переписать строковую навигацию `MainActivity` на `@Serializable`-роуты; белый список навбара.
- `strings.xml` (весь интерфейс, на «Вы»). `TranslatableText`-пустышка в `ui/components/`.
- **DoD:** 4 таба работают на type-safe навигации; контентный текст готов идти через обёртку.
- **Очистка контекста:** ✅ да.
- **Self-prompt:** _<…>_

### ⬜ Шаг D2. Серверные заглушки + DTO  🟡 Fable-review
- domain-интерфейсы: `AuthRepository`, `EntitlementsProvider` (полный, без лимита «слов в
  промте»), `AiExerciseRepository`, `DictionaryRepository`, синк прогресса.
- `Fake*`-реализации + Hilt-биндинги; dev/prod через BuildConfig, не комментарии.
- DTO запросов/ответов в `grammar-shared` (`@Serializable`); удалить пустой `Models.kt`.
- **DoD:** компилируется, биндинги поднимаются Hilt'ом.
- **Очистка контекста:** ✅ да. **decision_log + notes_for_fable.**
- **Self-prompt:** _<…>_

### ⬜ Шаг E. Читалка теории  🟡 Fable-only (по provisional)  ⭐
- TheoryScreen (темы + прогресс) → TopicScreen (микротемы) → MicrotopicScreen (листание
  карточек). Рендер всех блоков: `paragraph / heading / list / table / callout` + инлайн
  `**жирный**`/`*курсив*` → `AnnotatedString`. Контентный текст — через `TranslatableText`.
- TheoryRepository (combine content+user), use cases, тонкие ViewModel.
- **ОБКАТКА реального `basics.json`** → правки `md_to_json.py` совместно с пользователем
  (главный риск проекта). По итогу — пометить в планах: формат обкатан / нужны правки.
- **DoD:** теория из реального сида рендерится корректно; пользователь посмотрел карточки.
- **Очистка контекста:** ✅ да. **decision_log.**
- **Self-prompt:** _<…>_

### ⬜ Шаг F1. Ядро движка упражнений  🟡 Fable-only (по provisional)  ⭐
- Экран упражнения: scaffold (X из N, кнопка `?` с theorySummary, ID упражнения в углу, слот
  «объяснение при ошибке»). Делегат-механика «ответ на карточку» (попытки, фидбек, «Далее»
  открывается после ответа). Логика прогресса/`isCompleted`. Переход «теория → упражнения».
- + 1–2 простых типа (выбор варианта + TextInput) для сквозного прогона на реальных данных.
- **DoD:** сквозной флоу упражнения работает на реальном `basics.json`.
- **Очистка контекста:** ✅ да. **decision_log.**
- **Self-prompt:** _<…>_

### ⬜ Шаг F2. Группа «выбор варианта»  🟡
MULTIPLE_CHOICE / FORWARD_CHOICE / REVERSE_CHOICE / ERROR_CORRECTION / CONSTRUCTION_MEANING /
DIALOG_RESTORE / FIND_THE_ODD — на одном базовом компоненте выбора, различие в подаче условия.
- **Очистка контекста:** ✅ да (нужны: готовый движок F1 + db_schema + exercise_templates).
- **Self-prompt:** _<…>_

### ⬜ Шаг F3. Группа «ввод / сборка»  🟡
WORD_ARRANGEMENT (чипы + попап перевода), TRANSFORMATION (двойной ввод: отрицание+вопрос),
TABLE_FILL (таблица с пропусками).
- **Очистка контекста:** ✅ да.
- **Self-prompt:** _<…>_

### ⬜ Шаг F4. Группа «интерактивные»  🟡
MATCHING (соединение пар), TRUE_FALSE (тоггл 5 предложений), CATEGORIZATION (перетаскивание в
колонки — жесты только официальными API: `AnchoredDraggable` и т.п.).
- **DoD после F4:** все 14 типов работают на реальном сиде; формат карточек-заданий обкатан.
- **Очистка контекста:** ✅ да.
- **Self-prompt:** _<…>_

---

## Сводка «где Fable обязательно смотрит потом»
B (схема Room), C (сидинг/identity), D2 (DTO-контракт), E (читалка/формат теории), F1–F4
(движок и общая механика упражнений). Всё это пишется как `provisional` с `decision_log.md`.