# Фаза 2 — Слова из Words8r

> Всё о получении и использовании слов из Words8r в Grammar8r.
> Отправка слов обратно в Words8r — в [Фазе 5](../development_plan.md#фаза-5).
> Общий контекст — в [grammar8r_plan.md → Синхронизация](../../grammar8r_plan.md) и [words8r_plan.md](../../words8r_plan.md).

---

## Зачем

Grammar8r использует слова пользователя из Words8r в двух местах:
1. **AI-упражнения** — генерирует предложения из слов, которые пользователь уже учит
2. **Перевод по тапу** — кэш переводов слов (LingvoLive / Yandex Dictionary)

---

## Часть 1 — ContentProvider (Words8r → Grammar8r)

### Что нужно сделать в Words8r

Добавить `WordsProvider : ContentProvider`:
- Зарегистрировать в `AndroidManifest.xml` с `android:exported="true"`
- Читает из существующей БД Words8r — не ломает архитектуру
- Возвращает колонки: `word`, `translation`, `transcription`, `level`, `qRep`

URI:
```
content://dev.sethan8r.sethanwords.provider/words           ← все слова
content://dev.sethan8r.sethanwords.provider/words?level=A1  ← фильтр по уровню
content://dev.sethan8r.sethanwords.provider/irregular_verbs ← неправильные глаголы (V1/V2/V3)
```

Подробно про реализацию ContentProvider в Words8r — [words8r_plan.md → W1](../../words8r_plan.md).

### qRep — индикатор изученности

`qRep` — сколько раз слово было повторено в Words8r:
- `qRep = 0` — слово добавлено, но ещё не изучалось → **не использовать в AI**
- `qRep 1–8` — слово новое, недавно начали учить
- `qRep 9+` — слово хорошо изучено, много повторений

### Неправильные глаголы

ContentProvider отдаёт отдельным запросом с тремя формами: `{ v1, v2, v3, translation, qRep }`.
Grammar8r использует все три формы при составлении AI-промта: `go / went / gone`.

---

## Часть 2 — KnownWord в Grammar8r Room DB

После получения от ContentProvider — сохраняем локально в Grammar8r (кэш словаря).

### Сущность KnownWord

| Поле | Тип | Описание |
|------|-----|----------|
| word | String PK | Слово |
| translation | String | Перевод |
| transcription | String? | Транскрипция |
| level | String? | A1/A2/B1/... |
| qRep | Int | Количество повторений из Words8r |
| syncedAt | Long | Unix timestamp последней синхронизации |

### Сущность KnownIrregularVerb

| Поле | Тип | Описание |
|------|-----|----------|
| v1 | String PK | Базовая форма |
| v2 | String | Past Simple |
| v3 | String | Past Participle |
| translation | String | |
| qRep | Int | |
| syncedAt | Long | |

---

## Часть 3 — Синхронизация

### Когда синхронизировать

- **Автоматически** при открытии приложения (если прошло > N часов с последней синхронизации)
- **Вручную** по кнопке в MenuScreen

### Логика синхронизации

```
1. Запрос к ContentProvider Words8r
2. Words8r установлен? → получить список слов
   Words8r не установлен → показать подсказку "Установи Words8r"
3. Обновить KnownWord и KnownIrregularVerb в Room
   (upsert — обновить qRep если слово уже есть)
4. Записать syncedAt = now()
```

### Минимум слов для AI-практики

Если `KnownWord` содержит менее 50 слов с `qRep > 0`:
- AI-практика недоступна
- Показать: "Добавь больше слов в Words8r для умной практики"

---

## Часть 4 — Словарная база для Words8r sync (после микротем)

После завершения каждой микротемы — предложение добавить изученные слова в Words8r.

Хранится в assets JSON (не в Room). Формат:
```json
{ "word": "on", "translations": ["на", "над"], "transcription": "[ɒn]" }
```

Таблица слов по микротемам (что предлагаем добавить):

| Микротема | Слова |
|-----------|-------|
| Предлоги места | in, on, at, near, under, between, above, behind, in front of, beside, by |
| Предлоги времени | in, on, at, before, after, during, since, for, until |
| Предлоги направления | to, from, into, out of, through, past, along, across |
| Предлог of | of |
| Личные местоимения | I, you, he, she, it, we, they |
| Притяжательные местоимения | my, your, his, her, its, our, their |
| Указательные местоимения | this, that, these, those |
| Вопросительные слова | what, where, when, who, why, how, which, whose |
| Глагол to be | am, is, are, was, were |
| There is / There are | there |
| Глагол have/has | have, has |

> Артикли `a`, `an`, `the` — не добавляются (нет перевода, только теория).

**UI после микротемы:**
BottomSheet: "Хочешь добавить [название группы слов] в Words8r?"
→ Список слов с переводами + галочки
→ Пользователь выбирает нужные
→ Выбранные слова отправляются в Words8r (категория "Grammar Basics")
→ Реальная отправка — Фаза 5, сейчас заглушка

---

## Часть 5 — Перевод слов по тапу (DictionaryCache)

Тап на любое английское слово в теории или упражнениях → попап с переводом.

### Провайдеры

**Primary: LingvoLive (ABBYY)**
- `GET /api/v1/Minicard?text={word}&srcLang=1033&dstLang=1049`
- Бесплатно: 50 000 символов/день
- Документация: https://developers.lingvolive.com/ru-ru/Help

**Fallback: Yandex Dictionary**
- При ошибке / таймауте / исчерпании лимита LingvoLive
- Бесплатно: 1 000 запросов/день
- Документация: https://yandex.ru/dev/dictionary

**Архитектура:** `DictionaryRepository` интерфейс — смена провайдера без изменения UI.

### Кэш (Room: DictionaryCache)

| Поле | Тип | Описание |
|------|-----|----------|
| word | String PK | Слово в нижнем регистре |
| provider | String | "lingvolive" / "yandex" |
| transcription | String? | |
| translations | String | JSON-массив переводов |
| responseJson | String | Сырой ответ (для повторного использования при добавлении) |
| fetchedAt | Long | Unix timestamp |

### Поток

```
Тап на слово
    ↓
DictionaryCache (Room) → есть? → показать мгновенно
    ↓ нет
LingvoLive Minicard API
    ↓ ошибка
Yandex Dictionary (fallback)
    ↓ тоже ошибка
"Перевод недоступен"
```

### UI попапа (речевой пузырь)

```
┌──────────────────────┐
│  cat  [kæt]          │
│  • кошка, кот        │
│  • (разг.) чувак     │
│  [+ В мои слова]     │
└──────────────────────┘
```

При нажатии "В мои слова":
- Показать все варианты из кэша с галочками
- Пользователь выбирает нужные переводы
- Слово + выбранные переводы + транскрипция → отправить в Words8r (Фаза 5)

---

## Порядок реализации

**В Words8r (задачи W1):**
- [ ] **W1a** `WordsProvider : ContentProvider` — чтение слов с qRep
- [ ] **W1b** URI для неправильных глаголов с V1/V2/V3

**В Grammar8r:**
- [ ] **2.1** Room: `KnownWord` + `KnownIrregularVerb` entity, DAO, миграция
- [ ] **2.2** `WordsRepository` — чтение из ContentProvider + upsert в Room
- [ ] **2.3** Автосинхронизация при старте приложения (если прошло > 6 часов)
- [ ] **2.4** Кнопка ручной синхронизации в MenuScreen + индикатор "последний раз синхронизировано"
- [ ] **2.5** `DictionaryRepository` интерфейс + LingvoLive реализация + Yandex fallback
- [ ] **2.6** `DictionaryCache` Room entity + DAO
- [ ] **2.7** UI попапа (речевой пузырь) при тапе на слово
- [ ] **2.8** Words8r sync BottomSheet после микротемы (заглушка отправки — реальная в Фазе 5)
- [ ] **2.9** MicrotopicVocabulary assets JSON (слова по микротемам для sync)
- [ ] **2.10** Проверка минимума слов перед стартом AI-практики (50 слов с qRep > 0)