# Grammar8r — Правила разработки

## Рабочий процесс

**Код без явной команды не писать.**

Порядок работы:
1. Пользователь даёт ТЗ или задачу.
2. Claude изучает существующий код, задаёт уточняющие вопросы, описывает план в чате.
3. Пользователь уточняет, корректирует, даёт добро.
4. Только после явной команды ("пиши", "давай", "начинай") — пишется код.

То же самое для правок: сначала описать что и как изменится, потом ждать подтверждения.

### Работа с планами

Планы связаны между собой — изменение в одном часто требует правок в других.

При любом обновлении планов проверять:
- `tasks/grammar8r_plan.md` — общий план, затрагивает всё
- `tasks/subscription.md` — лимиты, тиры, стрик
- `tasks/features.md` — функциональность приложения
- `tasks/phases/development_plan.md` — фазы разработки
- `tasks/phases/phase1/` — теория, упражнения, контент
- `tasks/phases/phase3/` — AI-упражнения, практика
- `tasks/phases/phase4/phase4_server.md` — сервер
- `tasks/words8r_plan.md` — интеграция с Words8r

Если что-то изменилось в одном файле и это касается другого — обновить оба. Из планов ничего не удалять без явной команды — лучше добавить пометку "устарело" или обновить на месте.

Могут появится и другие ланы, смотри в tasks и если надо, то обновялй claude.md

---

## Тема и визуальный стиль

**Цвета, скругления, отступы — 1 в 1 как в Words8r.**

Эталон только для визуала: `E:\AndroidProjects\SethanWords\app\src\main\java\dev\sethan8r\sethanwords\ui\theme\`

Words8r как референс — **только тема**. Архитектура и структура кода там не образцовая, не копировать. Делать надо по заветам прода и бест практис, как бы этот код выглядил нга проде по структуре 


### Цветовая палитра

Файл: `grammar-app/ui/theme/Color.kt`

```kotlin
val Background     = Color(0xFF121212)  // фон приложения
val CardBackground = Color(0xFF1E1E1E)  // фон карточек и поверхностей
val TextPrimary    = Color(0xFFFFFFFF)  // основной текст
val TextSecondary  = Color(0xFFB0B0B0)  // второстепенный текст, подписи
val Accent         = Color(0xFFFF9B27)  // акцент: кнопки, прогресс, активные элементы
val Inactive       = Color(0xFF404040)  // неактивные/задизейбленные элементы
val CorrectGreen   = Color(0xFF4CAF50)  // правильный ответ, позитивный фидбек
```

### Material 3 цветовая схема

```kotlin
private val DarkColorScheme = darkColorScheme(
    primary          = Accent,
    onPrimary        = Background,
    secondary        = TextSecondary,
    background       = Background,
    onBackground     = TextPrimary,
    surface          = CardBackground,
    onSurface        = TextPrimary,
    surfaceVariant   = CardBackground,
    onSurfaceVariant = TextSecondary,
)
```

Только тёмная тема. Никакого светлого варианта.

### Скругления

| Значение | Где использовать |
|----------|-----------------|
| `16.dp`  | Основное: карточки, диалоги, большинство поверхностей |
| `12.dp`  | Кнопки, средние элементы |
| `24.dp`  | Большие контейнеры, расширенные поверхности |

### Отступы (8-point grid)

Используй только значения из сетки: `4.dp`, `8.dp`, `12.dp`, `16.dp`, `24.dp`, `32.dp`, `48.dp`, `56.dp`

Стандартные паттерны:
- Экран от краёв: `padding(horizontal = 16.dp)`
- Карточка внутри: `padding(16.dp)`
- Между элементами списка: `12–16.dp`
- Высота кнопки: `48–56.dp`

### Типографика

Шрифт: системный (FontFamily.Default). Размеры:

| Размер | Назначение |
|--------|-----------|
| `13.sp` | Подписи, мелкий текст |
| `14.sp` | Обычный UI текст |
| `16.sp` | Body text |
| `18.sp` | Подзаголовки |
| `22.sp` | Заголовки секций |
| `28.sp`+ | Крупные заголовки |

Начертание: `FontWeight.Bold` — основное, `FontWeight.Medium` — вторичное.

---

## Архитектура и структура кода

### Принципы

- **Каждый класс — одна ответственность.** ViewModel не знает про Room напрямую, Repository не знает про UI.
- **Никакой смешанной логики.** Бизнес-логика в UseCase/Repository, UI-логика в ViewModel, отображение в Composable.
- **Никакого дублирования.** Если что-то используется дважды — вынеси в общий компонент или утилиту.
- **Структура сразу как на проде.** Не "потом порефакторим" — сразу правильно.

### Слои (Android-модуль)

```
data/
  local/       ← Room DB, DAO, Entity
  remote/      ← API-клиент, DTO
  repository/  ← реализация репозиториев

domain/
  model/       ← доменные модели
  repository/  ← интерфейсы репозиториев
  usecase/     ← бизнес-логика

ui/
  theme/       ← Color.kt, Theme.kt, Type.kt
  components/  ← переиспользуемые Composable
  screens/
    <feature>/
      <Feature>Screen.kt
      <Feature>ViewModel.kt
```

### Правила

- Composable получает только UI-state и коллбеки, никаких ViewModel внутри composable напрямую (только на уровне экрана).
- ViewModel не импортирует ничего из `data/` напрямую — только через domain-интерфейсы.
- Зависимости через DI (Hilt).
- Модели не текут между слоями: Entity → доменная модель → UI-state — отдельные классы.

---

## Навигация и кнопка «Назад»

### Правило: никаких BackHandler в экранах

`BackHandler` — костыль, который нужен только когда подэкраны рендерятся через boolean-флаги внутри одного Composable (антипаттерн из Words8r). В Grammar8r так не делаем.

**Правильный подход:**

| Контент | Паттерн | Back работает |
|---------|---------|---------------|
| Полноэкранный подэкран (добавить слово, настройки) | Отдельный `composable` route в NavController | Автоматически через back stack |
| Подтверждение, информация, ввод | `AlertDialog` / `BottomSheet` | Автоматически — закрывает диалог |

Никаких `var showX by remember { mutableStateOf(false) }` + `if (showX) { FullScreenContent(); return }`.

### System navigation bar (insets)

`enableEdgeToEdge()` включён в Activity. Insets обрабатываются один раз на уровне `MainActivity`:

- Нижний nav bar: внешний `Box` с `.navigationBarsPadding()` + `NavigationBar(windowInsets = WindowInsets(0))`  
- Scaffold прокидывает `innerPadding` в `NavHost` через `.padding(innerPadding)`

**Следствие:** экраны (MenuScreen, TheoryScreen и т.д.) не занимаются insets самостоятельно. Никаких `navigationBarsPadding()` или `statusBarsPadding()` внутри экранов — Scaffold уже обо всём позаботился. Работает и с gesture navigation, и с 3-кнопочной.