# План фундамента Grammar8r (Шаг 1)

> **Статус:** черновик на ревью. Создан Opus 13.06.2026 для старта 14.06.2026 утром.
> **Назначение:** детальный план первого слоя кода — то, что `kickoff_context.md` §6 шаг 1
> требует составить ДО написания кода и отдать на ревью.
> **Что НЕ здесь:** схемы таблиц (они в `db_schema.md` — источник правды, дублировать нельзя).
> Здесь — как это превратить в код: модули, пакеты, классы, границы, порядок.

---

## 0. Принцип разбиения на обратимое / необратимое

Два контура работы (из договорённости чата, см. `_self_prompt.md` §3):

- 🟢 **Обратимый каркас** — делается на полной скорости, цена ошибки низкая, до релиза
  пользователей нет. Сюда: структура модулей/пакетов, Hilt-проводка, type-safe навигация,
  `TranslatableText`-пустышка, `strings.xml`, заглушки репозиториев, DI-модули.
- 🟡 **Необратимое (`provisional`)** — пишется, но помечается изменяемым, ревьюится жёстче,
  каждое решение фиксируется в `decision_log.md`. Сюда: **финальная схема Room** (Entity +
  миграции + identity hash) и **DTO-контракт в `grammar-shared`**. Их Fable проверит одним
  проходом, когда вернётся.

Сигнал СТОП (по `kickoff_context.md` §6.1): если начинаю копировать соседнюю фичу вместо
выноса общей механики (нарушение правила №0 из `words8r_lessons.md`) — торможу и обсуждаю.

---

## 1. Gradle-модули

```
Grammar8r/
├── grammar-app/      ← Android-приложение (Compose, Room, Hilt)
├── grammar-server/   ← Spring Boot на Java (Фаза 4; решение 02.07.2026, было Ktor) — сейчас пустой шаблон, не трогаем
└── grammar-shared/   ← 🟡 контракт API клиент↔сервер (чистый Kotlin/JVM)
```

- `grammar-app` подключает `implementation(project(":grammar-shared"))`.
- `grammar-shared`: сейчас пустой `Models.kt`-плейсхолдер. Удалить его при первом наполнении
  DTO (см. §6). Внутри — только `@Serializable` DTO + enum-ы контракта. Никаких Room/UI/Android.
- `grammar-server` в этой сессии не трогаем (Фаза 4).

**Решение по версиям/плагинам:** Hilt, Room, KSP, kotlinx.serialization, Navigation Compose
2.8+. Версии — через version catalog (`libs.versions.toml`), не хардкодом в `build.gradle.kts`.

---

## 2. Структура пакетов `grammar-app`

По слоям из `CLAUDE.md` → «Слои (Android-модуль)». Предлагаемое дерево:

```
dev.sethan8r.grammar8r/
├── di/                        ← Hilt-модули (DatabaseModule, RepositoryModule, ...)
├── data/
│   ├── local/
│   │   ├── content/           ← ContentDatabase, Entity (read-only), DAO
│   │   │   ├── entity/        ← GrammarTopic, GrammarMicrotopic, GrammarCard, ...
│   │   │   └── dao/
│   │   ├── user/              ← UserDatabase, Entity (mutable), DAO
│   │   │   ├── entity/        ← UserCardProgress, UserWordProgress, DailyStats, ...
│   │   │   └── dao/
│   │   ├── converter/         ← Room TypeConverters (enum↔string на границе data)
│   │   └── seed/              ← интеграция json_to_db.py (Gradle task), assets
│   ├── remote/                ← API-клиент, маппинг DTO (shared) ↔ domain. Пока заглушки.
│   ├── preferences/           ← DataStore-обёртки (настройки, НЕ прогресс)
│   └── repository/            ← реализации domain-интерфейсов (combine content+user)
├── domain/
│   ├── model/                 ← доменные модели (Entity → model → ui-state)
│   ├── repository/            ← интерфейсы: AuthRepository, EntitlementsProvider,
│   │                            AiExerciseRepository, DictionaryRepository, ...
│   └── usecase/               ← бизнес-логика, тестируемая без Android
├── ui/
│   ├── theme/                 ← Color.kt, Theme.kt, Type.kt (уже частично есть)
│   ├── navigation/            ← @Serializable routes, NavHost, белый список навбара
│   ├── components/            ← TranslatableText, переиспользуемые Composable
│   └── screens/
│       ├── theory/
│       ├── practice/
│       ├── statistics/
│       └── menu/
└── MainActivity.kt            ← enableEdgeToEdge, Scaffold, insets (один раз)
```

> Точное имя корневого пакета (`dev.sethan8r.grammar8r`?) — сверить с тем, что уже заведено в
> `grammar-app`. Если отличается — берём существующее, дерево остаётся.

---

## 3. 🟡 Room — две БД (provisional)

Источник правды по схемам — `db_schema.md`. Здесь — решения по реализации.

### 3.1. Два отдельных `RoomDatabase`

- **`ContentDatabase`** (`content.db`) — read-only, `createFromAsset("content.db")`.
  Версия БД = версия контента. На релизе с новым контентом: bump версии +
  `fallbackToDestructiveMigration()` **только тут** (легально: данных пользователя нет).
  Entity: `GrammarTopicCategory`, `GrammarTopic`, `GrammarMicrotopic`, `GrammarCard`,
  все 14 таблиц упражнений + `CardExerciseIndex` + `AiExercise`, `course_word_groups`,
  `course_categories`, `course_words`, `irregular_verbs`, словарь Words8r.
- **`UserDatabase`** (`user.db`) — mutable, честные миграции с первого дня, `exportSchema = true`,
  **никакого destructive fallback**, автотесты миграций (`MigrationTestHelper`).
  Entity: `UserCardProgress`, `UserMicrotopicProgress`, `UserCardHardcodeStats`,
  `UserAiExerciseStats`, `FavoriteAiExercise`, `UserWordProgress`, `UserCategorySettings`,
  `DailyStats`, `AiRequestCounter`, `DictionaryCache`.

### 3.2. Три обязательных решения (db_schema.md §«Три обязательных решения»)

1. Замена `content.db` при апдейте — через bump версии + destructive fallback (только content).
2. **Никаких JOIN/FK между БД.** «Контент + прогресс» склеивается в репозитории:
   `combine(contentFlow, userFlow)`. ATTACH запрещён.
3. ID контента вечные; осиротевший прогресс в user.db игнорируется (не чистить, не крашиться).

### 3.3. exportSchema и сидинг

- `exportSchema = true`, схемы в `app/schemas/` под VCS.
- `json_to_db.py` генерирует `content.db` **из экспортированной Room-схемы** (identity hash
  обязан совпасть, иначе краш при `createFromAsset`). Gradle-таск вызывает `py json_to_db.py`,
  кладёт `content.db` в `assets/`, зависимость на `preBuild`/`mergeAssets`. `content.db`
  **не коммитим** (build-артефакт); коммитим MD + JSON-сиды.
- Делаем интеграцию сразу, как только есть Entity и экспортированная схема (db_schema.md §
  «ПОРЯДОК РАБОТ»). Ждать всю теорию не нужно.

### 3.4. TypeConverter

Все enum-ы (`HardcodedExerciseType`, `AiExerciseInputMode`, `WordTable`, ...) ↔ строка —
конвертация **один раз** на границе data-слоя через TypeConverter. Stringly-typed `when`
по доменным значениям запрещён (`words8r_lessons.md` §2).

---

## 4. DI (Hilt) — с первого коммита

- `@HiltAndroidApp` на Application, `@AndroidEntryPoint` на MainActivity.
- `DatabaseModule` (`@Module @InstallIn(SingletonComponent)`): `@Singleton` provide
  `ContentDatabase`, `UserDatabase`, и все DAO.
- `RepositoryModule`: `@Binds` интерфейс домена → реализация (включая `Fake*` для заглушек).
- ViewModel — `@HiltViewModel`, обычный `ViewModel` (НЕ `AndroidViewModel`). Context в
  ViewModel не попадает; ресурсы/prefs — через инжектируемую обёртку.
- Никаких `getInstance()` / `new` внутри ViewModel (`words8r_lessons.md` §1.1).

---

## 5. Навигация — type-safe (Navigation Compose 2.8+)

- Роуты — `@Serializable` data class/object в `ui/navigation`. Никакой конкатенации строк и
  ручного парсинга аргументов.
- Видимость нижней панели — **белый список** корневых вкладок (Theory/Practice/Statistics/
  Menu). Любой новый экран по умолчанию полноэкранный.
- Insets — один раз в `MainActivity` (`enableEdgeToEdge`, `navigationBarsPadding` на внешнем
  Box, `NavigationBar(windowInsets = WindowInsets(0))`, `innerPadding` в `NavHost`). Экраны
  insets не трогают. `BackHandler` запрещён (`CLAUDE.md` → «Навигация»).

---

## 6. 🟡 Серверные заглушки + DTO в grammar-shared (provisional)

Интерфейсы — в `domain/repository`, фейковые реализации — в `data/repository`, биндинг — Hilt.
DTO запросов/ответов — сразу в `grammar-shared` (`@Serializable`). Пустой `Models.kt` удалить.

| Интерфейс (domain) | Назначение | Fake-реализация (Фаза 1) |
|--------------------|-----------|--------------------------|
| `AuthRepository` | JWT/OAuth (Яндекс + email) | возвращает фиксированного фейк-юзера |
| `EntitlementsProvider` | тир, AI-запросов/день, микротем/день (Free), заморозки стрика | debug-конфиг, подменяемый |
| `AiExerciseRepository` | запрос AI-упражнения по `id` | заглушка/мок-ответ |
| `DictionaryRepository` | перевод по тапу (LingvoLive→Yandex→кэш) | пустышка (под `TranslatableText`) |
| синк прогресса | выгрузка/загрузка прогресса | no-op |

- Поведение dev/prod — через `BuildConfig`-поля, не комментарии (`words8r_lessons.md` §6.2).
- `EntitlementsProvider` — полный интерфейс сразу (kickoff §6.2 п.1). ⚠️ Лимита «слов в промте»
  НЕТ — выборка слов фиксирована для всех тиров.

---

## 7. Закладки на будущие фазы (kickoff §6.2) — заложить с первого дня

1. `TranslatableText` — тонкая обёртка-пустышка над `Text` в `ui/components/`. Весь контентный
   текст (из БД / от AI) рендерится через неё сразу. `DictionaryRepository` + `DictionaryCache`
   — заглушки. Сам попап/жест — позже (`CLAUDE.md` → «Перевод слов по долгому нажатию»).
2. Состояния микротемы в UI-модели — **enum**, не Boolean (заблокирована лимитом / доступна /
   пройдена / есть неоткрытые слова → Download).
3. «Микротема завершена» — единый use case / поток событий в domain. Подписчики добавляются
   по фазам. Логику завершения НЕ размазывать по ViewModel.
4. Единая точка записи прогресса/активности — один репозиторий/use case, через который идут
   ВСЕ записи. Разрозненные `dao.update()` из ViewModel запрещены.
5. Каркас экрана упражнения (общий scaffold 14 типов): ID упражнения мелким курсивом в углу,
   слот «объяснение при неверном ответе», прогресс «X из N», кнопка `?` (theorySummary).
6. Карточка теории — слоты под Фазу 3 (AI-блок «Умное задание», кнопка «Не совсем понял») —
   задизейбленные заглушки.

---

## 8. Все интерфейсные строки — `strings.xml` с первого экрана

Обращение на «Вы». Хардкод текста в Composable запрещён. Контент (теория/упражнения) — из БД,
через `TranslatableText`; интерфейс — из `strings.xml`, обычный `Text`.

---

## 9. Порядок выполнения (атомарными шагами, чтобы пользователь следил в IDE)

1. Version catalog + Gradle-плагины (Hilt/KSP/Room/serialization/Navigation). Скелет 3 модулей.
2. 🟢 Структура пакетов + `@HiltAndroidApp` + DI-скелет (пустые модули).
3. 🟡 Room: Entity обеих БД по `db_schema.md` → экспорт схем → ревью identity. **decision_log.**
4. `json_to_db.py` + Gradle-таск сидинга `content.db` из экспортированной схемы.
5. 🟡 `grammar-shared`: DTO заглушек. **decision_log.**
6. 🟢 Domain-интерфейсы + Fake-реализации + Hilt-биндинги.
7. 🟢 Type-safe навигация (роуты, белый список навбара) + insets в MainActivity.
8. 🟢 `TranslatableText`-пустышка, `strings.xml`, заглушки `DictionaryRepository`.
9. Читалка теории на готовых темах (Основы) → обкатка формата → правки `md_to_json.py`
   совместно с пользователем (это главный риск проекта, kickoff §6.1 п.4).
10. Каркас движка упражнений + первые типы — отдельной сессией после читалки.

> Шаги 9–10 (читалка + движок) — самые «суждательные», по kickoff это Fable-only. Если Fable
> не вернётся к этому моменту — делаем по той же схеме: план → жёсткое ревью → decision_log.

---

## 10. Чек-лист перед каждым модулем (`words8r_lessons.md` §8)

1. Зависимости через Hilt? 2. Доменные значения — типы, не строки, источник один?
3. Логика/UI уже существует — выносим, не копируем? 4. Есть официальный Jetpack/Material3 API?
5. Логика тестируема без Android, ViewModel тонкий? 6. Строки в `strings.xml`, на «Вы»?
7. Прогресс — в Room, настройки — в DataStore? 8. Файл < ~400 строк? 9. Нет «TODO
раскомментировать», dev/prod через BuildConfig? 10. Аналитика — через общий `Analytics`?
11. Цвета/отступы — через `Color.kt`/токены, английский текст — через `TranslatableText`?