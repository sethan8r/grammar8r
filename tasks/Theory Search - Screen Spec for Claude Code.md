# Screen Spec: Теория → Поиск (результаты, стиль 5n-full)

Экран: вкладка **Теория** (`TheoryScreen.kt`) в режиме поиска. Это дизайн-референс (HTML-макет) —
recreate в существующем стеке приложения: Jetpack Compose, Kotlin, токены из `ui/theme/Color.kt` и
`ui/theme/Dimens.kt`, компонент `IdBadge` из `ui/components/progress/IdBadge.kt`. Ничего не копировать
из HTML впрямую — пересобрать композаблами по паттернам, уже принятым в `TheoryScreen.kt` /
`TopicScreen.kt` (см. `LazyColumn`, `AnimatedVisibility`, `HorizontalDivider`).

## 1. Общая структура экрана

`Box(fillMaxSize)` с тремя слоями:
1. `TheoryList` — `LazyColumn`, `contentPadding = PaddingValues(top = statusBarTopInset(), bottom = floatingBarBottomInset())`, `horizontal = Dimens.screenPadding` (16dp), `verticalArrangement = spacedBy(Dimens.spaceMedium)` — 12dp между группами списка.
2. Заголовок/поле поиска — `Box` фиксированной высоты 64dp поверх первого элемента списка (см. §3).
3. Плавающая капсула нижней навигации — уже существует, не трогать (см. `bottomBarFloatingMargin/BottomGap/ContainerRadius` в `Dimens.kt`).

Состояние экрана — enum, не роут:
```kotlin
sealed interface TheorySearchState {
    data object Closed : TheorySearchState
    data class Open(val query: String) : TheorySearchState
}
```
`query.isBlank()` → плейсхолдер «Введите запрос». `query` не blank и нет совпадений → «Ничего не найдено».
Иначе — список групп результатов. Логика поиска (нормализация, ранжирование) — отдельный класс в
`domain/usecase`, не во ViewModel (правило проекта, см. `CLAUDE.md`).

## 2. Заголовок «Теория» / поле поиска

Общий контейнер `Box(Modifier.height(64.dp))`, оба слоя занимают верхние 44–48dp этого контейнера
и анимированно кросс-фейдят/наезжают друг на друга.

**Заголовок (Closed):**
- `Text("Теория")`, `color = Accent`, `fontSize = 22.sp`, `fontWeight = Bold`.
- Справа — кнопка-лупа, `Box(36.dp)`, `RoundedCornerShape(Dimens.cornerButton)` (12dp) как hit-area, иконка `Icons.Filled.Search` 20dp, `tint = Accent`.
- При открытии поиска заголовок фейдит по opacity 1→0 за 150ms (не влияет на layout — его перекрывает поле).

**Поле поиска (Open)** — `Row` высотой 48dp, `background = Elevated (#2E3440)`, `RoundedCornerShape(Dimens.cornerButton)` (12dp), `padding(horizontal = 14.dp прим., trailing 8dp)`:
- Иконка-лупа 16dp, `tint = TextSecondary`, слева.
- `TextField`/`BasicTextField`, `weight(1f)`, `color = TextPrimary`, `fontSize = 16.sp`, `placeholder = "Введите запрос"` (`TextSecondary`), без внутренней рамки/подложки (transparent).
- Справа кнопка-крестик 28dp hit-area, иконка X 16dp `tint = Accent`.
- Появление: `translateY -8dp → 0dp` + `opacity 0→1` + `scale 0.97→1`, ~220ms ease (официальный API: `AnimatedVisibility` с `slideInVertically + fadeIn`, не ручной `Animatable`).
- Клавиатура открывается автоматически (`LocalFocusManager` / `FocusRequester.requestFocus()` в `LaunchedEffect` при входе в `Open`).

**Иконка лупа↔крестик:** не два отдельных элемента, а один `IconButton`, иконка меняется по `AnimatedContent` с `fadeIn/fadeOut + scale`, тот же `Accent` цвет в обоих состояниях (крестик того же цвета, что и лупа — акцент, не отдельный красный).

## 3. Пустое/безрезультатное состояния

Центрированы по всей доступной высоте списка (`Column(fillMaxSize, Arrangement.Center, Alignment.CenterHorizontally)`):
- **Пусто** (`query.isBlank()`): контурная иконка лупы 40dp `tint = Inactive` (`#404040`, `strokeWidth ~3dp`) + `Text("Введите запрос", TextSecondary, 16.sp, Medium)`, `gap = 12.dp`.
- **Ничего не найдено**: `Text("Ничего не найдено", TextPrimary, 16.sp, Bold)` + `Text("Попробуйте другой запрос", TextSecondary, 13.sp)`, `gap = 8.dp`.

## 4. Список результатов — структура группы

Группы отсортированы по релевантности (не по порядку курса). Каждая группа = один `Card`
(`background = CardBackground #1D2027`, `RoundedCornerShape(Dimens.cornerCard)` 16dp,
`padding(Dimens.cardPadding)` 16dp, `clickable` на весь верхний блок темы → навигация в тему).

### 4.1 Шапка группы (тема)
`Column(spacedBy(4.dp))`, кликабельна целиком:
- Если у темы есть раздел: `Text(section.title, TextSecondary, 13.sp)` над названием темы.
- `Text(topic.titleEn, TextPrimary, 16.sp, Bold)`.
- `Text(topic.titleRu, TextSecondary, 13.sp)` (обычная EN·RU двухстрочная пара, как везде в проекте).
- Прогресс: `LinearProgressIndicator` (`color = Accent`, `trackColor = Inactive`, `height = Dimens.progressBarHeight` 4dp, `clip(RoundedCornerShape(4.dp))`) + `Text("N из M", TextSecondary, 13.sp)` ниже. Прогресс в выдаче — **опционально по продукту, но в этом варианте показываем всегда**, включая группу-без-микротем.

### 4.2 Вложенная панель микротем («well»)
Если у группы есть совпавшие микротемы — сразу под шапкой темы, `margin-top = 4.dp`,
`Column(Modifier.background(Background #15171C, RoundedCornerShape(16.dp)).padding(horizontal = 14.dp, vertical = 4.dp))`
(вложенная панель того же цвета, что фон приложения — визуально «утоплена» под карточку темы). Если
у группы нет микротем (тема совпала целиком — см. §5), эта панель не рендерится вообще, шапка — сама
себе группа.

Внутри — строки микротем, `padding(vertical = 10.dp)` на строку, между строками —
`HorizontalDivider(color = Inactive, thickness = 1.dp)` **асимметричный**: `Modifier.padding(start = 14.dp)`
(как в `MenuGroup.kt` — divider инсетится слева под текст, но идёт до правого края без отступа).

Каждая строка микротемы:
```
Row(verticalAlignment = CenterVertically, spacedBy(8.dp)) {
    StatusDot(passed)              // 6dp кружок
    Column {
        Text(microtopic.titleEn, TextPrimary, 16.sp, Bold)
    }
}
Text(microtopic.titleRu, TextSecondary, 13.sp, Modifier.padding(start = 14.dp, top = 2.dp))
// если есть совпавшая карточка:
Row(Modifier.padding(start = 14.dp, top = 8.dp), spacedBy(8.dp), verticalAlignment = CenterVertically) {
    IdBadge(text = card.id.toString(), highlighted = card.completed)   // компонент из progress/IdBadge.kt as-is
    Text("«${card.title}»", TextSecondary, 12.sp)
}
```
- **StatusDot** — кружок 6dp, `background = CorrectGreen (#5BC98A)` если микротема пройдена целиком, иначе `Inactive (#404040)`. Показывает статус **микротемы**, не карточки.
- **IdBadge** — уже существующий компонент, использовать **без изменений**: `width = Dimens.idBadgeWidth` (52dp), `RoundedCornerShape(Dimens.cornerSmall)` (4dp), `background = if (highlighted) CorrectGreen else CardBackground`, текст = **просто номер карточки** («89», не «Card 89»), `color = if (highlighted) TextPrimary else TextSecondary`, `fontSize = 12.sp`. `highlighted` здесь = пройдена ли **конкретная карточка**, у которой совпало совпадение (не микротема) — это два разных сигнала пройденности на одной строке (точка = микротема целиком, бейдж = конкретная карточка-совпадение), так и оставить намеренно.
- Строка кликабельна целиком → переход в микротему (не в конкретную карточку).

## 5. Группа без микротем (тема совпала сама по себе)

Рендерится как обычная карточка темы (шапка §4.1), но **без** вложенной панели §4.2 — просто
`Card` с шапкой и прогрессом, ничего больше. Не добавлять пустую панель, псевдо-строки-заглушки
или decorations "чтобы не пустовало" — короткая карточка сама по себе валидна.

## 6. Пример данных экрана (как в макете)

```
Группа 1 — раздел «Времена», тема «Past Simple · Прошедшее простое», 4 из 9
  • Negatives · Отрицание: didn't                — микротема пройдена (зелёная точка)
  • used to · Раньше было, а теперь нет           — не пройдена (серая точка)
      Card 89, не пройдена → «I didn't went — двойного прошлого не бывает»
  • Time markers · Маркеры прошлого               — пройдена (зелёная точка)
      Card 92, пройдена → «Маркеры прошлого: словарь и слово ago»

Группа 2 — тема «Present Simple · Настоящее простое», 9 из 9 (без раздела)
  • Negatives · Отрицание: don't / doesn't        — пройдена (зелёная точка)

Группа 3 — раздел «Устройство языка», тема «Verb forms · Формы глагола», 2 из 6
  совпала сама тема, микротем под ней нет — карточка без вложенной панели
```

## 7. Дизайн-токены, использованные на экране

Все — уже существующие константы проекта, новых не вводить:

| Токен | Значение | Источник |
|---|---|---|
| Background | `#15171C` | `Color.kt` (панель-well использует именно фон приложения) |
| CardBackground | `#1D2027` | `Color.kt` — карточка группы |
| Elevated | `#2E3440` | `Color.kt` — поле поиска |
| TextPrimary / TextSecondary | `#FFFFFF` / `#B0B0B0` | `Color.kt` |
| Accent | `#7E9BFF` | `Color.kt` — заголовок, прогресс, иконки лупа/крестик |
| Inactive | `#404040` | `Color.kt` — трек прогресса, разделители, серая точка/бейдж |
| CorrectGreen | `#5BC98A` | `Color.kt` — зелёная точка и IdBadge пройденной карточки |
| cornerCard | 16dp | `Dimens.kt` — карточка группы и well-панель |
| cornerButton | 12dp | `Dimens.kt` — поле поиска, кнопки лупа/крестик |
| cornerSmall | 4dp | `Dimens.kt` — IdBadge |
| idBadgeWidth | 52dp | `Dimens.kt` |
| progressBarHeight | 4dp | `Dimens.kt` |
| cardPadding | 16dp | `Dimens.kt` |
| spaceMedium | 12dp | `Dimens.kt` — между группами списка |

## 8. Взаимодействие и анимация

1. Тап по лупе → `state = Open("")`, фокус в поле, клавиатура открывается, дерево тем (обычный список без поиска) скрывается через `AnimatedVisibility`.
2. Ввод символа → `query` обновляется, список результатов пересчитывается в реальном времени (`StateFlow` от usecase поиска, debounce не нужен — данные локальные).
3. Тап по крестику → `state = Closed`, поле уезжает вверх/фейдит, дерево тем возвращается, клавиатура закрывается.
4. Никакого `BackHandler` — состояние поиска не роут (правило проекта).
5. Тап по шапке темы → навигация в тему; тап по строке микротемы → навигация в микротему (не в конкретную карточку, даже если у строки показан `IdBadge` конкретной карточки).

## 9. Файлы дизайн-референса

`Theory Search — 6 Variants.dc.html` в этом проекте, секции `#5n` → `#5n-full` (итоговый вариант,
описанный здесь). Остальные секции файла (`5a`–`5r`, варианты 1–6) — отклонённые альтернативы,
не реализовывать.
