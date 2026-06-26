# Бриф: контент теории для Grammar8r

## ⚠️ ID для AI упражнений — обязательно

Каждое AI упражнение **должно иметь уникальный ID**. Это ключ промта на сервере — без него упражнение не работает.

**Формат:** `<topic>_card<N>_ex1`

| Топик | Префикс | Пример |
|-------|---------|--------|
| Предтема "Основы" | `basics_` | `basics_card1_ex1` |
| Present Simple | `present_simple_` | `present_simple_card3_ex1` |
| Past Simple | `past_simple_` | `past_simple_card2_ex1` |
| (и т.д. по теме) | | |

**Правила:**
- `<N>` — ID карточки из файла теории
- `_ex<K>` — номер AI внутри карточки. На карточке может быть **1, 2 или 3** AI (`_ex1`, `_ex2`,
  `_ex3`) или **ни одного** (вводные/справочные карточки — без AI). Обновлено 25.06.2026.
- ID глобально уникален — никаких совпадений между темами
- При написании новой карточки — сразу писать ID, не откладывать

**В файле теории формат:**
```
#### AI Exercise

**ID:** basics_card1_ex1
**Title:** "Вставь местоимение"
...
```

---

## ⚠️ Words8r Sync — перед сливом ВСЕГДА проверять дубли

**Никогда не сливать слово, которое уже сливалось раньше в курсе.** Перед добавлением любого слова
в `### Words8r Sync` — проверить, не встречалось ли оно (**слово + перевод**) в ЛЮБОЙ предыдущей
микротеме любой темы. Дубль = два независимых прогресса на одно слово в Словаре = путаница у
пользователя. Особенно касается базовых глаголов (`to want`, `to need`, `to like`, `to go`,
`to eat`…) — многие уже слиты в ранних темах.

Канон правила и критерий дубля (омонимы с разным переводом — НЕ дубль) — `theory_content_guide.md` §6.
Автопроверка по всему курсу — `check.py` → строка `WORD DUPLICATES`.

---

## Главная задача

> Качество теории и упражнений — это причина, по которой пользователи платят подписку. Не дизайн, не фичи. Именно контент.

Каждая карточка должна объяснять правило так, чтобы человек, который видит его впервые, сказал: "О, вот оно что" — а не "что-то я запутался". Теория разжёвана до состояния, где пользователю остаётся только проглотить. Никакой воды, но и никакой спешки — каждый нюанс объяснён, каждая ловушка предупреждена заранее.

**Карточка считается качественной если:**
- Её поймёт человек без знания английского
- Примеры из реальной жизни (еда, работа, семья, путешествия) — не "The cat sat on the mat"
- Упражнения проверяют именно это правило, а не что-то соседнее
- При неправильном ответе объяснение конкретное: не "неправильно", а "для he/she/it глагол получает -s: goes, works, likes"
- `aiExercise` описывает упражнение, которое невозможно заменить хардкодом — то, что требует живого понимания контекста. Имеет три поля: `title` (название для поиска и дерева Практики), `userInstruction` (что делать — показывается пользователю), `promptTemplate` (инструкции для AI — не показывается пользователю)

---

## Контекст

Grammar8r — Android-приложение для изучения английской грамматики.
Теория разбита на **темы** (например, "Present Simple"), каждая тема — на **микротемы**, каждая микротема — на **карточки** (одна карточка = одно правило/нюанс).

Весь контент загружается в базу данных из assets JSON.

---

## Стиль изложения

- Объясняй как умный друг который сидит рядом — не как учебник
- Никакой академической терминологии без объяснения что это вообще значит
- Каждое правило сразу подкреплено примером прямо в тексте, не в конце
- Короткие абзацы. Если абзац длиннее 4 строк — разбить
- Если правило имеет исключения — сказать об этом сразу, не прятать
- Если можно перепутать с другим правилом — предупредить: "Не путай с..."

### ⚠️ ОБЯЗАТЕЛЬНО: разница в стиле между похожими словами и конструкциями

Когда тема содержит несколько слов или конструкций с похожим смыслом (например, also / too / as well, say / tell / speak, although / but / however) — **обязательно объяснять разницу в регистре и стиле**. Это одна из самых ценных вещей которые можно объяснить — пользователь часто знает что слова похожи, но не понимает когда что уместно.

Формат: сравнительная таблица сразу после объяснения каждого слова:

| Слово/конструкция | Позиция | Стиль | Пример |
|-------------------|---------|-------|--------|
| also | перед глаголом | нейтральный | "I also like tea." |
| too | в конец | разговорный | "I like tea, too." |
| as well | в конец | чуть формальнее | "She speaks French as well." |

Колонки таблицы — по смыслу: не всегда нужны все четыре. Может быть "Когда использовать", "Формальность", "Частота" — смотри по контексту темы.

**Типичные ситуации где это нужно обязательно:**
- Синонимы с разным регистром (also / too / as well, moreover / also / besides)
- Способы выразить одну и ту же грамматическую идею (can / be able to, must / have to)
- Конструкции которые переводятся одинаково но употребляются по-разному (say / tell / speak / talk)

---

## Структура вывода

Выводи контент в формате JSON. Один файл = весь контент.

```json
{
  "topics": [
    {
      "id": 1,
      "title": "Основы",
      "description": "Артикли, предлоги, местоимения — фундамент перед временами",
      "order": 1,
      "isPretopic": true,
      "microtopics": [...]
    },
    {
      "id": 2,
      "title": "Формы глаголов (V1/V2/V3)",
      "description": "Что такое три формы глагола, правильные и неправильные глаголы",
      "order": 2,
      "isPretopic": false,
      "microtopics": [...]
    },
    {
      "id": 3,
      "title": "Present Simple",
      "description": "Привычки, факты, расписания — действия которые повторяются",
      "order": 3,
      "isPretopic": false,
      "microtopics": [...]
    }
  ],
  "pretopicVocabulary": [...]
}
```

### Структура микротемы → карточки

Каждая микротема состоит из **карточек**. Каждая карточка = одно маленькое правило или нюанс. Пользователь листает карточки вверх/вниз.

```json
{
  "id": 1,
  "topicId": 1,
  "title": "Артикли",
  "order": 1,
  "cards": [
    {
      "id": 1,
      "microtopicId": 1,
      "title": "Что такое артикль",
      "theory": "Текст теории — дружелюбный, простой, 1–3 абзаца, одно правило...",
      "theorySummary": "Краткая суть в 2–3 предложениях — показывается по кнопке '?'.",
      "order": 1,
      "examples": [
        { "id": 1, "russian": "Это кошка.", "english": "This is a cat." },
        { "id": 2, "russian": "Он ест яблоко.", "english": "He is eating an apple." }
      ],
      "wordArrangementExercises": [
        {
          "id": 1,
          "correctSentence": "She has a dog.",
          "words": [
            { "word": "She", "translation": "Она" },
            { "word": "has", "translation": "имеет / у неё есть" },
            { "word": "a", "translation": "артикль (один/какой-то)" },
            { "word": "dog", "translation": "собака" }
          ]
        }
      ],
      "multipleChoiceExercises": [
        {
          "id": 1,
          "questionText": "Give me ___ water.",
          "options": ["a", "an", "the", "—"],
          "correctOption": "the",
          "explanation": "Вода конкретная — та, о которой мы говорим. Нужен определённый артикль the.",
          "exerciseType": "CHOICE"
        },
        {
          "id": 2,
          "questionText": "Миша стоит у доски.",
          "options": ["Misha is in the board.", "Misha is on the board.", "Misha is at the board."],
          "correctOption": "Misha is at the board.",
          "explanation": "at — используется для мест/точек присутствия: at the board, at school, at work.",
          "exerciseType": "FORWARD_CHOICE"
        },
        {
          "id": 3,
          "questionText": "He is at school.",
          "options": ["Он физически внутри здания школы.", "Он учится в школе (это его место).", "Он около школы."],
          "correctOption": "Он учится в школе (это его место).",
          "explanation": "at school — значит человек там учится, это его место. in school — физически внутри здания.",
          "exerciseType": "REVERSE_CHOICE"
        }
      ],
      "textInputExercises": [
        {
          "id": 1,
          "questionText": "She ___ happy.",
          "correctAnswers": ["is"],
          "explanation": "She — единственное число, третье лицо. Глагол to be = is.",
          "hint": "am / is / are"
        }
      ],
      "matchingExercises": [
        {
          "id": 1,
          "pairs": [
            { "left": "Кот на столе.", "right": "The cat is on the table." },
            { "left": "Кот под стулом.", "right": "The cat is under the chair." },
            { "left": "Кот у окна.", "right": "The cat is near the window." },
            { "left": "Кот в коробке.", "right": "The cat is in the box." }
          ]
        }
      ],
      "trueFalseExercises": [
        {
          "id": 1,
          "sentence": "She are tired.",
          "isCorrect": false,
          "explanation": "She — единственное число, нужно is, а не are. Верно: She is tired."
        }
      ],
      "aiExercise": {
        "id": "articles_card1_ex1",
        "title": "Вставь артикль",
        "userInstruction": "AI даст тебе предложение с пропуском. Вставь нужный артикль: a, an, the или —.",
        "inputMode": "FILL_BLANKS",
        "wordsSource": "NONE"
      },
      "clarificationOptions": [
        "Разница между a и an",
        "Когда артикль не нужен совсем",
        "Почему the, а не a?"
      ]
    }
  ]
}
```

**Требования к каждой карточке:**
- `theory` — 1–3 абзаца, **одно правило**, без скролла. Дружелюбно, с примерами прямо в тексте. Никакой сухой терминологии без объяснения.
- `theorySummary` — 2–3 предложения, суть правила. Подсказка по кнопке "?".
- `examples` — минимум 3 пары RU → EN, разнообразные жизненные ситуации.
- Упражнения на карточку — **минимум 3**, разных типов. Не повторять один тип подряд.
- `aiExercise` — **0, 1, 2 или 3 на карточку** (обновлено 25.06.2026): где правило того стоит —
  несколько, у вводных/справочных карточек — ни одного. Каждое — отдельный объект с полями:
  - `id` — уникальный строковый ключ упражнения. Формат: `"<microtopic>_card<N>_ex<K>"` (K — номер AI внутри карточки). Пример: `"articles_card1_ex1"`, `"present_simple_card3_ex2"`. **Этот же id — ключ промта на сервере.** Должен быть уникальным глобально.
  - `title` — короткое название для дерева Практики и поиска
  - `userInstruction` — что делать, показывается пользователю перед заданием
  - `inputMode` — `"FREE_WRITE"` (свободный ввод) или `"FILL_BLANKS"` (пропуски в тексте). Подробнее: `db_schema.md → AiExerciseInputMode`
  - `wordsSource` — `"NONE"` / `"GENERAL"` / `"INFORMAL_ENGLISH"` / `"VERB_FORMS"`. Подробнее: `db_schema.md → AiExerciseWordsSource`

**Требования к упражнениям предтемы (пользователь почти не знает английский):**
- Больше `REVERSE_CHOICE` и `FORWARD_CHOICE` — пользователь ориентируется на русский контекст.
- `TextInput` — только для простых форм (is/are/am, предлоги из 2–3 букв).
- `Matching` — хорошо для предлогов, подходит любому уровню.
- Предложения в упражнениях — **очень простые**, знакомые слова (кот, дом, школа, мама, Миша).

---

## Что нужно сгенерировать

### 1. Предтема: "Основы" (isPretopic: true)

Порядок выстроен по принципу "нужно знать раньше, чтобы понять следующее":
личные местоимения → to be → артикли → притяжательные/указательные → вопросы → предлоги → мн.число → числа → дни → месяцы → время → счётные/несчётные → императив → порядок слов SVO.

Микротемы:
1. **Личные местоимения** — I, you, he, she, it, we, they + когда какое использовать. Фундамент: без них нельзя составить ни одного примера дальше.
2. **Глагол to be** — I am / You are / He is / She is / We are / They are + отрицание и вопрос
3. **There is / There are** — "есть, существует, находится" + отрицание и вопрос. Строится на to be.
4. **Глагол have / has** — владение и принадлежность: I have a cat / She has a dog + отрицание и вопрос
5. **Артикли: a и an** — когда использовать неопределённый артикль. Теперь есть контекст: "I have a cat."
6. **Артикль: the** — когда использовать определённый артикль
7. **Без артикля** — когда артикль не нужен совсем. Завершает блок артиклей.
8. **Притяжательные местоимения** — my, your, his, her, its, our, their. После артиклей понятна разница: "a cat" vs "my cat".
9. **Указательные местоимения** — this / that / these / those: это/то/эти/те + разница ед./мн. и близко/далеко
10. **Вопросительные слова** — what, where, when, who, why, how + короткие примеры вопросов. Теперь можно строить вопросы с to be: "Where is my cat?"
11. **Базовые предлоги места** — in, on, at, near, under, between
12. **Базовые предлоги времени** — in, on, at (для времени)
13. **Базовые предлоги направления** — to, from, into, out of
14. **Предлог of** — генитивная конструкция: a cup of tea, the name of the city, a piece of music. Завершает блок предлогов.
15. **Множественное число** — правило +s/+es, правила написания + топ неправильных форм (man→men, child→children, tooth→teeth и др.)
16. **Числа** — паттерн: 1–12 уникальные, 13–19 = +teen, 20–90 = +ty, дальше комбинации (twenty-one и т.д.), 100/1000. Порядковые: first/second/third, далее +th. Идут после мн.числа: "I have 2 cats."
17. **Дни недели** — Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday.
    Главное правило: в английском дни недели **всегда пишутся с заглавной буквы** — в отличие от русского.
    Дополнительно: weekday (будний), weekend (выходной), разговорные сокращения (Mon, Tue, Wed...).
    Слова синкаются в Words8r (категория "Grammar Basics") после микротемы.
18. **Месяцы и сезоны** — January–December, spring/summer/autumn/winter. Правило заглавной буквы (как у дней). Предлоги: *in January, in spring, in 2024*. Как читать годы: 1995 = "nineteen ninety-five", 2024 = "two thousand and twenty-four". Слова синкаются в Words8r (категория "Grammar Basics").
19. **Время на часах** — "What time is it?" / "It's...". Паттерны: 3:00 = three o'clock, 3:15 = a quarter past three, 3:30 = half past three, 3:45 = a quarter to four. 12:00 = noon / midnight. Предлоги: *at three o'clock, at half past five*. AM / PM. Связь с числами.
20. **Счётные и несчётные существительные (введение)** — базовая концепция: одни существительные можно считать (*a cat / two cats*), другие — нельзя (*water, advice, information, furniture, news, luggage, money*). Ловушка для русских: в русском "информация/мебель/совет" считаются нормально, в английском — нет. "I need an advice" — ошибка, правильно "I need advice". Артикль a/an — только со счётными. Связь с артиклями (уже пройдено) и квантификаторами much/many (впереди).
21. **Императив** — как давать команды, инструкции, просьбы. Образование: просто V1 без подлежащего: "Open the door!", "Listen!" Отрицание: Don't + V1: "Don't run!" Смягчение: "Please open..." / "Open..., please." Let's для приглашения: "Let's go!" / "Let's not argue." Встречается везде — инструкции, рецепты, знаки, просьбы.
22. **Порядок слов: SVO (базовый)** — в русском порядок свободный ("Кошку я вижу" = "Я вижу кошку"). В английском жёсткий: Подлежащее → Сказуемое → Дополнение. "I see the cat" — нельзя переставить. Это фундамент всего что дальше. Дополнения к порядку слов (наречия, вопросы, отрицания) появятся в следующих темах — все они навигируются из дерева тем.
    ⚠️ Связанные микротемы: "Порядок слов: наречия частотности" (в Present Simple), "Порядок слов: вопросы" (в каждом времени).

### 2. Present Simple

Первая карточка — подготовительная ("Basic Verbs"):
- Список 15-20 базовых глаголов V1 (go, work, eat, drink, live, play, read, write, speak, know, want, need, like, have, make, come, see, give, take, think)
- Объяснение почему важно выучить их в Words8r: именно эти глаголы будут использоваться во всех упражнениях темы
- Кнопка → слить в Words8r (категория "Basic Verbs")
- Кнопка → повысить приоритет этих слов в Words8r

Остальные микротемы:
1. **Образование: утверждение** — I/you/we/they + глагол, he/she/it + глагол+s
2. **Образование: отрицание** — don't / doesn't
3. **Образование: вопрос** — Do / Does
4. **Употребление** — привычки, факты, расписания
5. **Маркеры времени** — always, usually, often, sometimes, never, every day/week
6. **Порядок слов: наречия частотности** — где стоит always/usually/often/never в предложении. Правило: *перед основным глаголом* ("She **always** drinks coffee"), но *после to be* ("He **is** always late"). Ловушка для русских: хочется поставить в конец — "She drinks coffee always" — это ошибка. Связана с базовой карточкой SVO из предтемы.

Все примеры и упражнения строятся только на глаголах из "Basic Verbs".

### 3. Present Continuous

Микротемы:
1. **Что такое Present Continuous** — общая идея, отличие от Present Simple
2. **Образование** — am/is/are + глагол-ing
3. **Употребление** — что происходит прямо сейчас, временные ситуации
4. **Маркеры времени** — now, right now, at the moment, today, this week
5. **Глаголы, которые не используются в Continuous** — know, like, love, hate, want, need

### 4. Глаголы: V1, V2, V3  ← идёт после Present Continuous, перед инфинитивом

Микротемы:
1. **Что такое три формы глагола** — зачем они нужны, где используются
2. **Правильные глаголы** — правило +ed, правила написания (stop→stopped, study→studied)
3. **Неправильные глаголы** — уникальные формы, топ-20 самых частых с примерами
4. **Как запомнить** — связь с Words8r, советы по запоминанию

После последней карточки темы:
- Слить ~200 неправильных глаголов в Words8r (категория "Verb Forms") со всеми тремя формами (V1/V2/V3)
- Кнопка → перейти в режим изучения форм глаголов в Words8r

### 4.5. Инфинитив: с to и без to  ← после "Глаголы V1/V2/V3", перед наречиями

Микротемы:
1. **Что такое инфинитив** — базовая форма глагола, to + V1
2. **Глаголы которые требуют to** — want to, need to, like to, decide to, try to, plan to, agree to, refuse to, forget to, learn to...
3. **Глаголы после которых to не нужен** — модальные (can, must, should, will, would, may, might) + let, make, help (в некоторых конструкциях)
4. **Частые ошибки русскоговорящих** — "I want go" (нет), "I can to go" (нет). Объяснить почему интуитивно хочется добавить/убрать to там где не надо.

### 4.7. Наречия (Adverbs)  ← после инфинитива, перед Past Simple

Вводная карточка: что такое наречие — слово которое уточняет глагол, прилагательное или другое наречие. Отвечает на вопросы "как?", "когда?", "где?", "насколько?".

Микротемы:
1. **Образование наречий** — правило: прилагательное + -ly (quick → quickly, careful → carefully). Исключения: good → well, fast → fast, hard → hard, late → late/lately. Слова на -ly которые НЕ наречия: friendly, lovely, lively.
2. **Наречия образа действия (Manner)** — как что-то делается: slowly, quietly, carefully, badly, well. Позиция: после глагола / после дополнения. "She sings **beautifully**." / "He drives the car **carefully**." Ловушка: "She sings beautiful" (ошибка — нужно наречие).
3. **Степени сравнения наречий** — short → shorter/fastest, carefully → more carefully / most carefully. Исключения: well → better → best, badly → worse → worst, far → further → furthest.
4. **Наречия степени (Degree)** — very, quite, rather, fairly, extremely, incredibly + прилагательное/наречие. Разница: *very cold / quite cold / rather cold / fairly cold* — нюансы интенсивности. too = слишком, enough = достаточно (связано с конструкциями too/enough впереди).
5. **Наречия места и времени** — here/there, everywhere/nowhere, now/then/soon/already/yet/still. Позиция в предложении. "**Still** waiting", "not **yet**", "**already** done".

### 5. Past Simple

Вводная карточка: список 10-20 глаголов из "Verb Forms" нужных для темы (went, saw, came, took, gave, got, made, said, knew, thought и др.)
→ Кнопка "Повысить приоритет в Words8r" + рекомендация выучить перед началом

Микротемы:
1. **Что такое Past Simple** — общая идея
2. **Образование: правильные глаголы** — глагол + ed
3. **Образование: неправильные глаголы** — объяснение + топ-20 самых частых
4. **Образование: отрицание и вопрос** — didn't, Did...?
5. **Употребление** — завершённые действия в прошлом
6. **Маркеры времени** — yesterday, last week/year, ago, in 2020

### 6. Past Continuous

Вводная карточка: глаголы из "Verb Forms" нужные для темы → кнопка приоритета

Микротемы:
1. **Что такое Past Continuous** — общая идея
2. **Образование** — was/were + глагол-ing
3. **Употребление** — действие в процессе в прошлом, фон для другого действия
4. **Past Simple vs Past Continuous** — разница на примерах

### 7. Present Perfect

Вводная карточка: глаголы из "Verb Forms" нужные для темы (been, seen, done, gone, had, made и др.) → кнопка приоритета

Микротемы:
1. **Что такое Present Perfect** — общая идея, почему это сложно для русских
2. **Образование** — have/has + причастие прошедшего времени (Past Participle)
3. **Правильные и неправильные глаголы в PP**
4. **Употребление** — опыт, результат, недавнее прошлое
5. **Маркеры времени** — ever, never, already, yet, just, recently

### 8. Present Perfect Continuous

Вводная карточка: глаголы из "Verb Forms" нужные для темы → кнопка приоритета

Микротемы:
1. **Что такое Present Perfect Continuous**
2. **Образование** — have/has been + глагол-ing
3. **Употребление** — длительное действие от прошлого до настоящего
4. **Present Perfect vs Present Perfect Continuous** — разница

### 9. Past Perfect

Вводная карточка: глаголы из "Verb Forms" нужные для темы → кнопка приоритета

Микротемы:
1. **Что такое Past Perfect**
2. **Образование** — had + Past Participle
3. **Употребление** — действие до другого действия в прошлом
4. **Маркеры времени** — before, after, already, when, by the time

### 9.5. Past Perfect Continuous

Вводная карточка: глаголы из "Verb Forms" нужные для темы → кнопка приоритета

Микротемы:
1. **Что такое Past Perfect Continuous**
2. **Образование** — had been + глагол-ing
3. **Употребление** — длительное действие которое происходило вплоть до какого-то момента в прошлом ("She had been waiting for two hours when he finally arrived.")
4. **Past Perfect vs Past Perfect Continuous** — разница: результат vs процесс ("He had read the book" vs "He had been reading")
5. **Маркеры времени** — for, since, before, when, by the time

### 10. Future Simple (will)

Вводная карточка: глаголы из "Verb Forms" нужные для темы → кнопка приоритета

Микротемы:
1. **Что такое Future Simple**
2. **Образование** — will + глагол
3. **Употребление** — спонтанные решения, предсказания, обещания
4. **Отрицание и вопрос** — won't, Will...?
5. **Маркеры времени** — tomorrow, next week, soon, in the future

### 11. Future с "going to"

Вводная карточка: глаголы из "Verb Forms" нужные для темы → кнопка приоритета

Микротемы:
1. **Going to vs Will** — когда какое использовать
2. **Образование** — am/is/are going to + глагол
3. **Употребление** — планы, намерения, очевидные предсказания

### 12. Future Continuous

Вводная карточка: глаголы из "Verb Forms" нужные для темы → кнопка приоритета

Микротемы:
1. **Что такое Future Continuous**
2. **Образование** — will be + глагол-ing
3. **Употребление** — действие в процессе в будущий момент

### 13. Future Perfect

Вводная карточка: глаголы из "Verb Forms" нужные для темы → кнопка приоритета

Микротемы:
1. **Что такое Future Perfect**
2. **Образование** — will have + Past Participle
3. **Употребление** — действие завершится до определённого момента в будущем
4. **Маркеры времени** — by tomorrow, by next year, by the time

### 14. Модальные глаголы

Вводная карточка: обзор всех модальных глаголов — зачем они, чем отличаются от обычных (нет -s в 3-м лице, нет to после них).

Микротемы:
1. **Что такое модальный глагол** — не спрягается, всегда стоит перед V1 без to. Список: can, could, may, might, must, should, ought to, will, would, shall
2. **can / could** — способность ("I can swim") и возможность/вежливая просьба ("Could you help me?"). could — прошедшее can или смягчённая просьба
3. **may / might** — разрешение ("May I leave?") и вероятность ("It might rain"). might — меньше уверенности чем may
4. **must / have to** — обязанность: must — внутренняя (сам считаю нужным), have to — внешняя (правила, обстоятельства). Отрицания разные: mustn't (нельзя!) vs don't have to (не обязан)
5. **should / ought to** — совет, рекомендация: "You should see a doctor." ought to = should, но чуть формальнее
6. **will / would** — будущее и вежливые просьбы. would — мягче и вежливее: "Would you like some tea?"
7. **Модальные + have + V3** — рассуждение о прошлом: "He must have forgotten." / "She could have called." / "You should have warned me."
8. **Частые ловушки** — "I must to go" (нет), "I can to swim" (нет). Почему to не нужен. Разница must/have to/should для русскоговорящих которые всё это переводят как "надо"

### 15. Passive Voice (Страдательный залог)

Микротемы:
1. **Что такое Passive** — деятель неважен, неизвестен или очевиден. "The letter was sent." vs "Someone sent the letter."
2. **Образование** — to be (в нужном времени) + V3. Таблица форм: is done / was done / will be done / has been done / had been done
3. **Passive в Present и Past Simple** — наиболее частые формы. Много примеров из жизни
4. **Passive в Future и Perfect-временах** — will be done, has been done, had been done
5. **by + деятель** — когда и зачем: "The Mona Lisa was painted by Leonardo da Vinci." Когда by опускается
6. **Get-passive** — разговорный вариант: "He got fired." / "She got promoted." Отличие от be-passive
7. **Частые ловушки** — "Меня зовут Иван" → "I am called Ivan" (Passive!). Конструкции где русский не чувствует Passive

### 16. Conditionals (Условные предложения)

Вводная карточка: все четыре типа в одной таблице — краткий обзор перед детальным изучением.

Микротемы:
1. **Zero Conditional** — факты и законы природы: "If you heat ice, it melts." Оба глагола в Present Simple
2. **First Conditional** — реальное условие в будущем: "If it rains, I'll stay home." If + Present Simple → will + V1
3. **Second Conditional** — нереальное/маловероятное: "If I were rich, I would travel." If + Past Simple → would + V1. Почему "were" а не "was" (даже для I)
4. **Third Conditional** — нереальное прошлое: "If I had studied, I would have passed." If + Past Perfect → would have + V3
5. **Mixed Conditional** — смешанный: прошлое условие → настоящий результат. "If I had slept more, I wouldn't be tired now."
6. **Unless / as long as / provided that** — альтернативы слову if. unless = if not, as long as = при условии что
7. **Частые ловушки** — "If I will go" (ошибка), "If I would" (ошибка). Почему will не идёт после if в придаточном

### 17. Relative Clauses (Относительные придаточные)

Микротемы:
1. **Что такое Relative Clause** — уточнение к существительному. "The man who called you is my boss." who = какой именно человек
2. **who / that для людей, which / that для предметов** — "The book which/that I read" / "The person who/that called"
3. **where / when / whose** — место, время, чьё: "The city where I was born" / "The day when we met" / "The girl whose phone rang"
4. **Defining vs Non-defining (с запятыми и без)** — без запятых: уточняет о каком предмете речь. С запятыми: просто добавляет информацию. "My sister who lives in London" (у меня несколько сестёр) vs "My sister, who lives in London," (одна сестра)
5. **Пропуск who/which/that** — когда можно опустить: "The book (that) I read was great." Только когда местоимение — объект, не подлежащее

### 18. Reported Speech (Косвенная речь)

Микротемы:
1. **Что такое Reported Speech** — пересказываем что кто-то сказал. "I'm tired" → He said he was tired. Не цитата, а пересказ
2. **Сдвиг времён назад** — Present → Past, Past Simple → Past Perfect, will → would. Таблица сдвигов
3. **Сдвиг местоимений и обстоятельств** — I → he/she, we → they, now → then, here → there, today → that day, tomorrow → the next day
4. **say vs tell vs ask** — say (не называет кому), tell (обязательно кому), ask (вопрос/просьба). "He said that...", "He told me that...", "She asked if..."
5. **Reported Questions** — порядок слов как в утверждении, без вспомогательного do/does: "Where do you live?" → He asked where I lived.
6. **Reported Commands** — told/asked + to + V1: "Wait here!" → She told me to wait there.
7. **Частые ловушки** — "He said me" (ошибка: нужно told me или said to me). Когда сдвиг времён не нужен (вечная истина)

### 18.2. Make vs Do

Микротемы:
1. **Принцип разграничения** — make = создать/произвести результат, do = выполнить процесс/деятельность. Нет строгого правила — надо учить устойчивые сочетания.
2. **Make: устойчивые сочетания** — make a decision, make a mistake, make an effort, make a plan, make a call, make money, make a noise, make a difference, make friends, make progress, make sense.
3. **Do: устойчивые сочетания** — do homework, do the dishes, do the laundry, do sport/exercise, do business, do damage, do someone a favour, do well/badly, do nothing.
4. **Ловушки и частые ошибки** — "make homework" (ошибка), "do a mistake" (ошибка). Упражнения на разграничение.

### 18.3. So/Such и Too/Enough

Микротемы:
1. **so + прилагательное/наречие** vs **such + (a/an) + существительное** — одно значение, разная конструкция. "The film was **so** good." / "It was **such** a good film." Ловушка: "It was so a good film" (ошибка).
2. **so/such...that** — результат: "It was **so** cold **that** I couldn't go out." / "It was **such** a long trip **that** we all fell asleep."
3. **too + прилагательное/наречие** — слишком (негативно, проблема): "It's **too** cold to swim." / "He speaks **too** fast to understand." Конструкция: too + adj/adv + to + V1.
4. **прилагательное/наречие + enough** — достаточно (позитивно): "She's old **enough** to drive." / "Is it warm **enough**?" Порядок важен: enough идёт ПОСЛЕ прилагательного/наречия. Конструкция: adj/adv + enough + to + V1.

### 18.4. Глагол get — часть 1: базовые значения

Микротемы:
1. **get = получать / доставать** — get a letter, get a gift, get tickets, get information. Самое базовое значение.
2. **get = становиться (+ прилагательное)** — get tired, get cold, get angry, get married, get better, get worse, get lost, get ready. Процесс изменения состояния. Ловушка: в русском используем "стать/становиться", в английском часто get.
3. **get = добираться / приходить** — get home, get to work, get to the airport, get here/there. Как спросить: "How do you get to work?"
4. **get + существительное/объект** — get a job, get a taxi, get dressed, get up. Устойчивые сочетания.

### 18.5. Linking Words (Связующие слова)

Микротемы:
1. **Причина: because / since / as / due to / because of** — "I stayed home **because** it was raining." Разница структур: because + clause, because of + noun ("**because of** the rain").
2. **Результат: so / therefore / as a result / consequently** — "It was raining, **so** I stayed home." therefore/as a result — более формально, в начале предложения.
3. **Противопоставление: but / however / although / even though / whereas / while** — but (простое), however (формальное, с запятой), although/even though + clause, whereas/while = тогда как.
4. **Despite / In spite of** — противопоставление с существительным или герундием. "**Despite** being tired, she finished." / "**In spite of** the rain, we went out." Разница с although: despite/in spite of + noun/-ing, although + clause.
5. **Добавление: and / also / too / as well / moreover / furthermore / in addition** — разный регистр и позиция. also/too/as well — разговорные, moreover/furthermore — формальные.
6. **Цель: in order to / so that** — "She woke up early **in order to** catch the train." (= to, но формальнее). "She woke up early **so that** she could catch the train." (так чтобы — + clause с could/would).

### 19. Разговорный английский

Микротемы:
1. **[глагол] + to → стяжения** — gonna, wanna, hafta, gotta, oughta, tryna
2. **[modal] + have → стяжения** — coulda, woulda, shoulda, musta, mighta
3. **Слияния и клиппинги** — dunno, lemme, gimme, kinda, sorta, ya
4. **Вопросительные редукции** — whatcha, whaddya, howya, whataya
5. **Современный слэнг — происхождение и значение**
   Упор на этимологию — так запоминается лучше и интереснее.
   Примеры: shawty (shorty → малышка), finesse (от фр. finesse — тонкость → двигаться плавно/без усилий),
   slay (убивать → быть огонь/выглядеть потрясающе), lit (зажжённый → зажигательный/крутой),
   vibe (вибрация → атмосфера/ощущение), flex (сгибать мышцы → хвастаться), cap/no cap (колпак → ложь/без лжи).
   Слова синкаются в Words8r (категория "Informal English") после микротемы.
6. **Разговорные отрицательные стяжения**
   ain't = am not / isn't / aren't — крайне распространено в музыке, сериалах, разговорной речи.
   Неформальное, иногда считается грубоватым: в официальных текстах и эссе — ошибка.
   Примеры: "I ain't going." (I'm not going.) / "She ain't here." (She isn't here.)
   won't = will not — нейтральное стяжение, нормально в любом контексте, в отличие от ain't.
   Полный справочник стяжений: can't, couldn't, shouldn't, wouldn't, don't, doesn't, didn't,
   isn't, aren't, wasn't, weren't, haven't, hasn't, hadn't.
   Ловушка: пользователь слышит ain't постоянно → решает использовать сам → ошибка в формальном контексте.
   Упражнения: разворачивать стяжения (won't → will not) и наоборот; найти ain't в тексте песни и объяснить смысл.

### 20. Дополнительные конструкции

Микротемы:
1. **Pronoun one / ones** — замена существительного чтобы не повторять его дважды.
   "The pink one was faster." = "The pink [car] was faster." В русском так не говорят — ловушка.
2. **Have got** — разговорный синоним have: "I've got a problem" = "I have a problem"
3. **Used to** — действие которое было в прошлом но больше не происходит: "I used to play football."
4. **Got used to / Be used to** — привык к чему-то (не путать с used to!):
   "I got used to waking up early." / "I'm used to the noise."
5. **Be about to** — вот-вот произойдёт: "I'm about to leave."
6. **Предпочтения: would rather / prefer / rather than / had better**
   - `would rather` — хочу скорее это, чем то: "I'd rather stay home than go out." После rather → V1 без to
   - `prefer + V-ing / to + V` — общее устойчивое предпочтение: "I prefer cooking to eating out."
   - `rather than` — вместо, а не: "Call me rather than texting." Противопоставление двух действий
   - `had better` — совет с оттенком предупреждения: "You'd better hurry or we'll be late." Не о прошлом — несмотря на had!
   Частая ловушка: "I'd rather to go" (нет), "I prefer cook" (нет) — правила разные для каждого
7. **Герундий после глагола** — одни глаголы требуют -ing, другие to + V1, третьи принимают оба варианта с разным смыслом.
   enjoy/finish/avoid/mind/consider + V-ing: "I enjoy swimming."
   want/need/decide/hope/plan/afford + to: "I want to go."
   remember/forget/try/stop — оба варианта, смысл разный: "I stopped smoking" (бросил) vs "I stopped to smoke" (остановился чтобы покурить)
8. **Сравнение прилагательных** — comparative и superlative.
   Короткие (1–2 слога): big → bigger → the biggest; правило удвоения согласной.
   Длинные (3+ слога): beautiful → more beautiful → the most beautiful.
   Исключения: good → better → the best, bad → worse → the worst, far → further → the furthest.
   Частые ловушки: "more bigger" (ошибка), "the most best" (ошибка)
9. **Квантификаторы** — сколько: much/many, a lot of, few/little, some/any.
   much + несчётное ("much water"), many + счётное ("many cars").
   few/little (мало, с оттенком "почти нет") vs a few/a little (немного, достаточно).
   some (утверждения, предложения), any (вопросы, отрицания): "Do you have any questions?"
10. **Возвратные местоимения** — myself/yourself/himself/herself/itself/ourselves/yourselves/themselves.
    Когда подлежащее и объект — одно лицо: "She hurt herself." / "I did it myself." (сам, без помощи).
    by myself/yourself... = в одиночку, без помощи: "He lives by himself."
11. **Question Tags** — вопросительные хвостики. "You're coming, aren't you?" / "It's cold, isn't it?" / "He didn't call, did he?"
    Правило: утверждение → отрицательный хвостик, отрицание → утверждающий. Вспомогательный глагол из основного предложения.
    Частые ловушки: "I am right, aren't I?" (не "amn't I"). "Let's go, shall we?"
12. **So / Neither / Either в ответах** — выражаем согласие кратко.
    "I like coffee." — "So do I." (я тоже) / "Neither do I." (я тоже нет).
    So + вспомогательный глагол + подлежащее. Neither/Nor — то же для отрицательных.
13. **upon** — формальный и книжный вариант предлога on. Не для повседневной речи — встречается в текстах, официальных фразах и устойчивых выражениях.
    Основные случаи: "upon arrival" (по прибытии), "once upon a time" (жили-были), "upon reflection" (поразмыслив), "upon hearing the news" (услышав новость). Смысл тот же что у on — но звучит торжественнее.
    Показываем пассивное узнавание: пользователь должен понять when he sees it, а не активно использовать.
14. **Прилагательные на -ing и -ed** — частая ошибка: boring vs bored, interesting vs interested, exciting vs excited, frightening vs frightened, confusing vs confused, surprising vs surprised, exhausting vs exhausted.
    Правило: *-ing* описывает то, что вызывает чувство ("The film is **boring**" — фильм скучный). *-ed* описывает того, кто испытывает чувство ("I am **bored**" — мне скучно). "I am boring" = я сам скучный человек — совсем другой смысл. Ловушка для русских: в русском одно слово "скучный" — в английском два разных.
15. **Глагол have — нестандартные употребления** — помимо "иметь" have означает действие или опыт. have breakfast/lunch/dinner, have a drink, have a rest, have a shower, have a look, have a good time, have a dream, have a party, have a conversation. Это не владение — нельзя заменить have got: "I've got breakfast" — ошибка.
16. **Предлоги после глаголов** — глагол + предлог: устойчивое сочетание которое надо учить вместе с глаголом. Логически не угадывается.
    *in*: interested in, believe in; *of*: think of, dream of, consist of; *on*: depend on, rely on, insist on; *for*: wait for, look for, pay for, ask for; *to*: listen to, belong to, talk to; *at*: look at, laugh at; *about*: think about, worry about, know about; *with*: agree with, deal with.
17. **Предлоги после прилагательных** — тоже устойчивые сочетания:
    *of*: afraid of, proud of, tired of, aware of, capable of; *in*: interested in, involved in; *at*: good at, bad at, amazed at; *with*: pleased with, satisfied with, bored with, angry with; *about*: worried about, excited about, upset about; *for*: responsible for, famous for, ready for, grateful for, sorry for.
18. **Глагол get — часть 2: продвинутые значения** ← продолжение раздела 18.4 (базовые значения)
    *get + V3 (causative)*: организовать чтобы кто-то сделал / сдать в работу: "I **got** my hair **cut**." / "She **got** her car **repaired**." Разница с make: "I made him do it" (заставил) vs "I got him to do it" (убедил).
    *get + объект + to + V1*: убедить: "She got me to help her."
    *get = понять* (разговорное): "Do you get it?" / "I don't get the joke."
    *get = раздражать* (разговорное): "It really gets me when people are late."
    Слова из get-сочетаний синкаются в Words8r (категория "Useful Verbs").

> *(Пометка: тема «Дополнительные конструкции» может расти. Кандидаты на добавление: wish / if only, эмфатические конструкции It's...that, восклицания What!/How!)*

### 21. Устойчивые выражения и идиомы

Микротемы:
1. **Фразовые глаголы** — глагол + предлог = новый смысл.
   give up (сдаться), make up (придумать / мириться), look for (искать),
   come across (наткнуться), run out of (закончиться), put off (откладывать),
   turn down (отказать), bring up (воспитать / поднять тему), carry on (продолжать),
   step out (выйти на время / отойти ненадолго)
2. **Идиомы** — выражения непереводимые дословно.
   it's raining cats and dogs, break the ice, hit the nail on the head,
   bite the bullet, cost an arm and a leg, once in a blue moon,
   spill the beans, under the weather, beat around the bush,
   break a leg (ловушка: дословно звучит как угроза "сломай ногу", а на деле — пожелание удачи
   перед выступлением, театральное суеверие; русский аналог — "ни пуха ни пера")
3. **Устойчивые конструкции** — фиксированные фразы с внутренней логикой.
   by the way, one way or another, as long as, no matter what,
   on the other hand, at the end of the day, to be honest, in other words,
   sooner or later, for the time being

Для каждой микротемы: теория (как образована, почему именно так), примеры в живом контексте,
упражнения на распознавание и применение. Слова синкаются в Words8r (категория "Fixed Expressions").

### 22. Фразы из жизни

Отдельная большая тема — ситуативные выражения разговорного английского. Не грамматика, не слэнг — а конкретные фразы которые носители говорят в конкретных жизненных ситуациях. После каждой микротемы → слова открываются (isView = true, категория **"Everyday English"**). Микротемы сгруппированы по коммуникативным ситуациям.

#### Микротема 1: Команды и пространство

Фразы-команды для управления движением и положением людей. Встречаются у полицейского, на дороге, у врача, в играх.

- **pull over** — прижмись к обочине, остановись. Не "тормози" (это brake / slow down) — именно съехать на обочину
- **stay where you are** — не двигайся, оставайся на месте. Полицейский, врач после укола, игра в прятки
- **watch out / look out** — осторожно! Взаимозаменяемы. watch out — чуть чаще предупреждение заранее; look out — немедленная угроза
- **step aside** — отступи в сторону, уйди с дороги
- **hold on** — 1. погоди (подожди секунду), 2. держись (в машине, на аттракционе)
- **keep going** — продолжай, не останавливайся

AI-упражнения: диалоговые сцены → выбрать правильную команду по контексту, объяснить почему другие не подходят.

#### Микротема 2: Движение и следование

Нюанс: кто идёт первым и куда направлено внимание — меняет слово.

- **follow me** — следуй за мной (я впереди, ты сзади). Гид, инструктор, экскурсия
- **walk with me** — иди рядом (бок о бок). Разговор на ходу, начальник предлагает поговорить
- **come with me** — пойдём со мной (любое направление, акцент на совместности)
- **join me** — присоединись (за столом, в прогулке, в деле)
- **after you** — пожалуйста, проходите (пропускаем вперёд). Вежливость у двери, в лифте
- **lead the way** — веди, показывай дорогу (ты знаешь — ты впереди)

AI-упражнения: короткие диалоги → выбрать правильную фразу для ситуации.

#### Микротема 3: Вежливость и разрешение

Формальные и полуформальные фразы для управления разговором и действием. Разные регистры — важно понять где каждая уместна.

- **please proceed** — пожалуйста, продолжайте / приступайте. Формальный регистр: врач, экзаменатор, официальное собрание. В обычном разговоре — go ahead / carry on
- **go ahead** — давай, продолжай (нейтральное, самое частое в разговоре)
- **be my guest** — пожалуйста, конечно (разрешение). "Can I use your pen?" — "Be my guest."
- **if I may** — если позволите (оговорка перед тем как что-то сказать). "If I may, I'd like to add one point."
- **as you wish** — как вам угодно / как хочешь. Нейтрально-вежливое согласие, может звучать чуть холодно
- **I beg your pardon** — 1. Прошу прощения (формальное извинение), 2. Что? Повторите (не расслышал)

AI-упражнения: ситуация с указанием регистра → выбрать уместную фразу.

#### Микротема 4: Намерение — три регистра

Одна идея "я собираюсь что-то сделать" — три разных способа с разным оттенком и регистром.

- **I intend to** — я намерен (официально, серьёзно). Документы, переговоры, торжественные заявления. "I intend to challenge this decision."
- **I'm going to** — я собираюсь (нейтрально, план). Обычный разговор. "I'm going to call him later."
- **I'm about to** — я вот-вот (прямо сейчас начну). "I'm about to leave." — уже взял ключи
- **one of these days** — как-нибудь, в один прекрасный день (расплывчато, без плана). "One of these days I'll learn guitar." — обычно так и не делают

Ловушка: "I intended to go" ≠ "I intend to go" — разные времена, разные смыслы (намеревался но не пошёл vs намерен).

AI-упражнения: ситуация → выбрать правильный регистр намерения.

#### Микротема 5: Расплывчатость и мягкость (Vague Language)

Носители намеренно говорят неточно — это норма, а не ошибка. Русскоговорящие часто стремятся к точности там где английский специально размывает.

- **a word or two** — пару слов. "Can I have a word?" = можно поговорить? (one word — буквально одно слово, но значит "поговорить")
- **in a minute** — сейчас, сейчас (не ровно через 60 секунд — просто "скоро")
- **a couple of things** — пара вещей (не обязательно ровно две)
- **more or less** — более-менее, приблизительно
- **roughly** — примерно (о числах: "roughly 50 people")
- **at some point** — в какой-то момент (без указания когда)
- **Could I have a word?** — устойчивая формула просьбы о частном разговоре (с глазу на глаз)

AI-упражнения: в каком предложении носитель использовал бы расплывчатую формулировку, а в каком точную — и почему.

#### Микротема 6: Многозначные слова в контексте

Слова которые меняют значение в зависимости от ситуации. Нужно понять контекст, не искать "правильный" перевод.

**cheers** — пять значений:
1. За здоровье! (тост)
2. Спасибо (брит. неформально) — "Cheers for the help!"
3. Пока (брит. неформально) — "Cheers, see you!"
4. Ладно, принято (завершение разговора) — ответ на "Thank you"
5. Поднять настроение (глагол) — "That really cheered me up"
Подсказка: значения 2, 3, 4 — британские. В США это почти не используется.

**out there** — два значения:
1. Там снаружи (физически или метафорически) — "There are great opportunities out there."
2. Странный, нестандартный — "His ideas are a bit out there."

**right** — три значения + маркер:
1. Правильный, верный — "The right answer"
2. Направление — "Turn right"
3. Ладно, понятно (разговорный маркер) — "Right, let's start."
Отдельно: "Right?" в конце = question tag: "We're leaving at 8, right?"

**fair enough / fair point** — справедливо, согласен. Принимаешь аргумент или условие даже без энтузиазма.

AI-упражнения: диалоги → определить значение многозначного слова по контексту.

#### Микротема 7: Предлоги в жизненном контексте

Три способа сказать "через/посредством" — via, through, by — с разными оттенками и контекстами.

- **via** — посредством, через как канал или маршрут (формальный). "via email", "fly via Moscow", "heard via a friend". Не для физического прохождения сквозь что-то.
- **through** — через (физическое прохождение или процессный путь). "through the forest", "through the door", "got the job through a friend". Самый разговорный из трёх.
- **by** — с помощью, посредством (инструмент или агент). "by car", "by hand", "by accident", "by mistake". Не про маршрут — про способ.

Сравнение в одном контексте:
- "I contacted him **via** email." (электронная почта как формальный канал)
- "I contacted him **through** a colleague." (через коллегу, процессный путь)
- "I contacted him **by** phone." (телефон как инструмент)

AI-упражнения: выбрать via / through / by в предложении + объяснить логику выбора.

#### Микротема 8: Разговорные связки с so

**so they** = поэтому они — базовое: so как союз результата + подлежащее третьего лица. "They were tired, so they went to bed."

Расширенный паттерн **so + [итог/комментарий]** в живой речи:
- **so they say** = так говорят (неподтверждённая информация). "It's the best pizza in town, so they say."
- **so what?** = ну и что? Риторический вопрос — говорящему это неважно
- **so there** = вот так вот (завершение аргумента с небольшой дерзостью). "I did it my way, so there."
- **so be it** = пусть будет так (формальное смирение с ситуацией)
- **so far so good** = пока всё хорошо (устойчивое выражение, буквально "пока — хорошо")
- **and so on** = и так далее

Разница с so как наречием степени (so tired, so many) — здесь so как союз результата или дискурсный маркер.

AI-упражнения: диалоги → выбрать правильную so-конструкцию по смыслу.

### 23. Разное — полезные темы без строгого места

Последняя тема. Сюда попадает то, что важно знать, но не вписывается в основную последовательность и ни на что критично не влияет. Пользователь пройдёт основной курс и может изучить это по желанию — или вернуться сюда когда встретит непонятое.

Микротемы:
1. **Национальности и языки** — Italy → Italian, France → French, Russia → Russian, Germany → German, Japan → Japanese, Spain → Spanish, China → Chinese. Правило заглавной буквы всегда (как у дней/месяцев). *the + национальность во мн.ч.*: the French, the Russians, the British (= этот народ в целом). Язык vs национальность: "I speak **French**" / "She is **French**." Ловушка: в русском иногда пишем строчную — в английском никогда.
2. **Порядок прилагательных** — когда несколько прилагательных стоят подряд, порядок строгий: Мнение → Размер → Возраст → Форма → Цвет → Происхождение → Материал → Назначение + существительное. "A beautiful small old Italian leather bag" — не "an Italian old small beautiful leather bag." На практике: больше 2–3 прилагательных подряд бывает редко, но порядок нарушать нельзя.
3. **Составные существительные (Compound Nouns)** — два существительных подряд: первое работает как прилагательное. coffee cup (чашка для кофе), bus stop, football player, toothbrush, sunglasses, birthday cake, traffic jam, washing machine. Ударение обычно на первом слове. Слитно/раздельно/через дефис — нет правила, надо запоминать.
4. **The + прилагательное как существительное** — the + прилагательное = группа людей. "**The rich** get richer." / "**The poor** need help." / "**The elderly** deserve respect." / "**The homeless**." Без существительного, во множественном числе, с глаголом множественного числа.
5. **Восклицательные предложения** — What! и How!. "**What** a beautiful day!" / "**What** an amazing story!" (What + a/an + adj + noun). "**How** beautiful!" / "**How** quickly she runs!" (How + adj/adv). Ловушка для русских: хочется "How a beautiful day!" — ошибка, с How артикль не нужен.
6. **Отрицательные префиксы** — расширение словаря через приставки. un-: unhappy, unknown, unusual, uncomfortable, unclear. dis-: disagree, disappear, dishonest, disconnect. im-/in-/ir-/il-: impossible, incorrect, irregular, illogical. non-: non-stop, non-fiction. mis-: misunderstand, mistake, mislead. Правила выбора префикса частично угадываются по происхождению слова — латинские слова часто берут in-/im-/ir-/il-, германские — un-.
7. **Wish / If only** — желания и сожаления. "I **wish** I knew the answer." (хочу, но не знаю — нереальное настоящее, Past Simple в придаточном). "I **wish** I had studied harder." (сожаление о прошлом — Past Perfect). "I **wish** it would stop raining." (хочу чтобы что-то изменилось — would). "**If only** I were taller!" = усиленное wish. Связь с conditionals (Second/Third) — та же логика нереальности.
8. **Эмфатические конструкции** — выделение важного. "**It's** John **who** did it." (не кто-то, а именно Джон). "**What** I need **is** more time." (мне нужно именно это). "**The thing** I hate **is** waiting." Помогает расставить акцент там где в русском это делается интонацией.

### Словообразование: суффиксы — отдельный блок внутри темы «Разное»

Блок открывается **вводной карточкой** — она обязательна и идёт перед всеми микротемами.

**Вводная карточка: Классы слов и почему суффиксы не взаимозаменяемы**

Каждый суффикс «питается» от определённого класса слов — и превращает его в другой класс. Если класс не тот, суффикс не работает.

| Суффикс | Питается от | Делает | Пример |
|---------|-------------|--------|--------|
| -er / -est | прилагательного / наречия | степень сравнения | big → bigger |
| -ful / -less | существительного | прилагательное | care → careful |
| -ous / -ish | существительного | прилагательное | danger → dangerous |
| -able / -ible | глагола / существительного | прилагательное | read → readable |
| -ness | прилагательного | существительное | happy → happiness |
| -ment / -tion | глагола | существительное | enjoy → enjoyment |
| -er (агент) | глагола | существительное (кто делает) | teach → teacher |

Почему к `big` нельзя `-ful`? Потому что `big` — уже прилагательное, а `-ful` ест существительные. `bigful` — бессмыслица. Зато `-er/-est` работают: `bigger`, `biggest` — они едят прилагательные.

Эту логику нужно понять один раз — и все остальные карточки встанут на место. В начале каждой микротемы напоминание повторяется одной строкой.

---

**Микротема 1: -er / -est — суффиксы сравнения**

Питается от: прилагательных и наречий.
Делает: сравнительную (bigger) и превосходную (biggest) степень.

Правила:
- Короткие слова (1–2 слога): просто + -er / -est → sour→sourer, sweet→sweeter, cold→colder
- Удвоение согласной перед -er (если слово кончается на «согласная–гласная–согласная»): big→bigger, hot→hotter, thin→thinner
- Слова на -y → -ier / -iest: happy→happier, easy→easier, heavy→heavier
- Наречия: fast→faster, hard→harder (исключения без -ly)
- Длинные слова (3+ слога) → more / most, не -er: beautiful→more beautiful (не beautifuler!)
- Исключения: good→better→best, bad→worse→worst, far→further→furthest

Напоминание в начале карточки: «-er/-est едят прилагательные и наречия — превращают их в степень.»

AI-упражнение «угадай трансформацию»: ИИ смотрит Words8r пользователя, выбирает подходящее прилагательное (например, *cold*), задаёт вопрос по-русски **не называя базовое английское слово**:
> «Как одним словом сказать "более холодный"?» → пользователь пишет: `colder`

После ошибки: показывается базовое слово + правило удвоения / -y→-ier если применимо.

---

**Микротема 2: -ful и -less — противоположные пары**

Питается от: существительных.
Делает: прилагательные («наполненный чем-то» / «лишённый чего-то»).

Запоминать парой — это главный приём:
- care → careful / careless (осторожный / беззаботный)
- hope → hopeful / hopeless (полный надежды / безнадёжный)
- harm → harmful / harmless (вредный / безвредный)
- pain → painful / painless (болезненный / безболезненный)
- use → useful / useless (полезный / бесполезный)

Ловушка: `hopeless` ≠ «без надежды в смысле плохой». В разговорной речи: «He's hopeless at cooking» = он совершенно не умеет готовить.

Напоминание: «-ful/-less едят существительные — big не существительное, поэтому bigful — ошибка.»

AI-упражнение: «Как одним словом сказать "полный надежды"?» → `hopeful`

---

**Микротема 3: -ous и -ish**

Питается от: существительных.

**-ous** → прилагательное со значением «обладающий этим свойством»:
danger→dangerous, fame→famous, mystery→mysterious, nerve→nervous, glory→glorious

**-ish** → прилагательное со значением «немного такой / похожий на»:
child→childish, fool→foolish, self→selfish, green→greenish (немного зеленоватый)
Нюанс: `-ish` может добавляться и к прилагательным для смягчения: «It's coldish today» = немного холодновато.

Напоминание: оба суффикса едят существительные.

AI-упражнение: «Как одним словом сказать "полный опасности"?» → `dangerous`

---

**Микротема 4: -able / -ible**

Питается от: глаголов и существительных.
Делает: прилагательные со значением «можно сделать это» или «подходит для этого».

- read→readable (можно прочитать), wash→washable, break→breakable
- comfort→comfortable, fashion→fashionable, reason→reasonable

-ible чаще у слов латинского происхождения:
- access→accessible, flex→flexible, response→responsible, sense→sensible

Строгого правила -able vs -ible нет → запоминаем. Подсказка: если отрезать суффикс и осталось знакомое английское слово → скорее -able (read→readable). Если нет → скорее -ible (flex→flexible).

Напоминание: «-able/-ible едят глаголы или существительные. big — прилагательное, поэтому bигable — бессмыслица.»

AI-упражнение: «Как одним словом сказать "удобный" (про диван, одежду)?» → `comfortable`

---

**Микротема 5: -ness**

Питается от: прилагательных.
Делает: существительное — называет это качество как явление.

- happy→happiness, kind→kindness, sad→sadness, dark→darkness
- lonely→loneliness, good→goodness, mad→madness, weak→weakness

Ловушка: `busy→business` — это исторически совершенно другое слово, не применяй правило.
Ещё ловушка: `dark→darkness` пишется через -ness, но `large→largeness` звучит неестественно — лучше `size`. Язык не всегда логичен.

Напоминание: «-ness ест прилагательные → превращает их в существительные.»

AI-упражнение: «Как одним словом назвать качество быть добрым?» → `kindness`

---

**Микротема 6: -ment и -tion/-sion**

Питается от: глаголов.
Делает: существительное — называет действие или его результат.

**-ment:**
- enjoy→enjoyment, develop→development, achieve→achievement, move→movement, agree→agreement

**-tion/-sion:**
- decide→decision, act→action, create→creation, discuss→discussion, invite→invitation
- Написание меняется: decide → deci**sion** (не decidetion). Форма суффикса зависит от окончания глагола — нет строгого правила, запоминаем.

Ловушка: в русском «решение» и «решать» — родственные слова. В английском тоже: decide → decision. Но форма меняется сильнее чем в русском.

Напоминание: «-ment и -tion едят глаголы → называют само действие.»

AI-упражнение — **другой формат** (не "угадай трансформацию", а "назови явление"):
ИИ даёт базовый глагол по-русски:
> «Есть глагол "наслаждаться". Как одним словом назвать само это действие или его результат?» → `enjoyment`

---

**Микротема 7: -er как агент**

Питается от: глаголов.
Делает: существительное — «тот кто делает это действие».

- teach→teacher, write→writer, work→worker, drive→driver, sing→singer
- run→runner (удвоение: кончается на «согл–гласн–согл»), swim→swimmer
- manage→manager, dance→dancer, play→player, build→builder

Не путать с -er сравнения:
- `bigger` — это степень прилагательного big (больший)
- `driver` — это агент от глагола drive (водитель)
Класс питания разный → суффикс разный по смыслу.

Напоминание: «-er (агент) ест глаголы → называет того кто делает.»

AI-упражнение: «Кто водит машину — одним словом?» → `driver`
Если слова пользователя в Words8r содержат глаголы (teach, write и т.д.) — AI берёт их.

---

**⚠️ Место для расширения**

Следующие суффиксы — кандидаты на будущие микротемы (добавляем по мере написания контента):
- **-ity** (real→reality, active→activity, possible→possibility)
- **-ward/-wards** (forward, backward, toward, upward)
- **-ship** (friend→friendship, leader→leadership, partner→partnership)
- **-hood** (child→childhood, neighbour→neighbourhood, false→falsehood)
- **-ify** (simple→simplify, class→classify, pure→purify)

---

## Базовый словарь предтемы (pretopicVocabulary)

Это список слов и конструкций, который предлагается пользователю добавить в Words8r (категория "Grammar Basics") после прохождения предтемы.

**Что включаем:**
- Личные местоимения (standalone): `I`, `you`, `he`, `she`, `it`, `we`, `they`
- Притяжательные местоимения (standalone): `my`, `your`, `his`, `her`, `its`, `our`, `their`
- Предлоги (standalone): `in`, `on`, `at`, `to`, `from`, `near`, `under`, `between`, `into`, `out of`
- Конструкции с `to be`: `I am`, `You are`, `He is`, `She is`, `It is`, `We are`, `They are`
- Конструкции: `There is`, `There are`
- Базовые существительные, глаголы, прилагательные (~40–50 слов): самые частые и нужные для упражнений

**Что НЕ включаем:**
- Артикли `a`, `an`, `the` — у них нет перевода, только теория

Формат каждой записи:
```json
{
  "word": "in",
  "translation": "в, внутри",
  "transcription": "[ɪn]"
}
```

Для конструкций:
```json
{
  "word": "I am",
  "translation": "Я (есть), Я являюсь",
  "transcription": "[aɪ æm]"
}
```

```json
{
  "word": "There is",
  "translation": "Есть, Находится, Существует",
  "transcription": "[ðer ɪz]"
}
```

Итого ~80–100 записей.

---

## Формат разработки контента

Теория пишется в MD-файлах в `tasks/phases/phase1/theory/`.
После проверки и правок — конвертер делает JSON-сид → content.db. Пишем блоками по 3–4 микротемы.

### 📁 Конвенция организации theory-файлов (как раскладываем)

- **1 файл = 1 тема** (`GrammarTopic`). Внутри — её микротемы.
- **Имя файла = номер-префикс + название темы по дереву** `grammar8r_plan.md`:
  `01-basics.md`, `02-language-structure.md`, `03-present-simple.md`, `04-present-continuous.md`,
  `…`, `06-adverbs.md`, `07-past-simple.md` … — номер задаёт порядок в курсе.
- **Темы идут ЛИНЕЙНО** (как в плане): времена перемежаются с Наречиями и т.д. — **группировки «разделов»
  НЕТ**, ни в файлах, ни в БД (`GrammarTopic` плоский, порядок через `order`).
- **Вложенность (подпапка) — ТОЛЬКО там, где один узел плана расщепляется на несколько тем**
  (напр. «Дополнительные конструкции», «Устойчивые выражения»):
  `25-additional-constructions/emphasis.md`, `…/suffixes.md`. Если тема просто большая (много
  микротем) — это ОДИН файл, не папка.
- **Сиды** зеркалят имена: `seed/basics.json`, `seed/present-simple.json`. `json_to_db` склеивает все.

**Структура карточки в MD:**
- **Theory** — полный текст объяснения
- **Summary** — 2–3 предложения для кнопки «?» во время упражнений
- **Examples** — таблица RU → EN, IDs сквозные по всему файлу
- **Exercises** — все хардкодные упражнения; каждое содержит `Explanation (при ошибке)`
- **AI Exercise** — описание AI-упражнения, которое идёт после хардкодных
- **Clarification Options** — 2–3 варианта для кнопки «Не совсем понял»

В конце каждой **микротемы** (не карточки) — раздел **Words8r Sync** со списком слов для добавления в Words8r.
В приложении это BottomSheet после завершения микротемы: список слов с галочками (все выбраны), кнопки «Добавить» / «Отменить».

**Формат слов в Words8r Sync:** 

| Слово | Переводы | Транскрипция |
|-------|----------|-------------|
| you | ты, вы | [juː] |
| to be | быть, являться, находиться | [tuː biː] |

- Глаголы пишутся с `to`: `to be`, `to have`, `to watch`, `to go`
- Переводов может быть несколько через запятую
- Транскрипция в квадратных скобках, IPA

IDs (карточек, примеров, упражнений) продолжаются сквозно от блока к блоку. В конце каждого блока — одна таблица счётчиков с актуальными значениями. **При добавлении нового блока — старую таблицу счётчиков удалять.** В файле всегда должна быть только одна таблица счётчиков — самая последняя.

---

## Важные правила

1. **Не сокращай.** Каждая карточка — полноценное объяснение одного правила, не в одну строчку. Теория должна быть настолько понятной, что её поймёт человек без знания английского.

**⚠️ Выделение жирным в текстах упражнений: `**…**` (канон, июнь 2026).**
Нужно подсветить в условии задания слово (`определи часть речи слова **run**`), часть слова
или одну букву (`rec**o**rd` — ударная гласная, `walk**ed**` — окончание) — берём в `**…**`.
Приложение отрендерит содержимое маркеров жирным (UI-утилита `**` → AnnotatedString — общая
для всех типов упражнений; звёздочки текстом не показываются). `smell.py` парные `**…**`
не считает запахом, непарные — ловит. Не путать со служебным жирным-маркером ответа
(`Правильное предложение: **…**` в WordArrangement, `→ **"…"**` в Transformation) — там
жирный съедается конвертером. Полный канон: `theory_content_guide.md` §8 и
`exercise_templates.md` → «Общие правила».

**⚠️ ОБЯЗАТЕЛЬНО для всех карточек "Образование" (утверждение / отрицание / вопрос):**

Каждая такая карточка должна содержать явную **схему порядка слов** — не просто "добавь do/does" или "поставь -ed", а полную цепочку с позициями. Английский язык критически зависит от порядка слов, и пользователь должен видеть это наглядно каждый раз.

**Обязательный формат схемы в theory-тексте:**

```
✅ Subject + Verb(+s) + Object
   She      drinks      coffee.

✅ Subject + do/does + not + Verb + Object
   She      does  not   drink   coffee.

✅ Do/Does + Subject + Verb + Object + ?
   Does      she      drink  coffee?
```

Для вопросительных слов добавляем строку:
```
✅ Wh-word + Do/Does + Subject + Verb + ?
   What     does      she      drink?
```

**Правила оформления схемы:**
- Каждый элемент подписан (Subject, Verb, Object, Time, Place и т.д.)
- Под схемой — сразу живой пример с теми же слотами
- Если есть наречие (already, never, just) — показать его позицию отдельной строкой
- Для сложных времён (Perfect, Continuous) — показать auxiliary отдельно от main verb.

---

**🔴 ОБЯЗАТЕЛЬНЫЙ ФОРМАТ таблицы порядка слов — использовать ВЕЗДЕ где объясняется структура предложения**

Когда объясняем как строится утверждение, отрицание или вопрос — таблица всегда в этом формате: английские слова, английские грамматические термины, русские грамматические термины, и строка с дословным русским переводом В АНГЛИЙСКОМ ПОРЯДКЕ СЛОВ. Главный глагол переводить нормальным русским словом. Вспомогательный глагол — в квадратных скобках, чтобы было понятно что он служебный.

**Формат одной таблицы:**
```
She      │ has        │ seen       │ this film?
Subject  │ Auxiliary  │ Verb (V3)  │ Object
Подлеж.  │ Вспом. гл. │ Глагол     │ Дополнен.
─────────────────────────────────────────────
Она      │ [имеет]    │ смотрела   │ этот фильм?
```

По-английски звучит нормально.
По-русски буквально: **"Она [имеет] смотрела этот фильм"** — криво, но сразу видна логика: вспомогательный глагол стоит перед основным, порядок жёсткий.

**Таких примеров давать 2–3 штуки подряд — разные подлежащие, разные глаголы, разные дополнения.** Не один и тот же пример перефразированный.

**Пример блока из 3 таблиц для Present Perfect:**

```
She      │ has        │ seen       │ this film?
Subject  │ Auxiliary  │ Verb (V3)  │ Object
Подлеж.  │ Вспом. гл. │ Глагол     │ Дополнен.
─────────────────────────────────────────────
Она      │ [имеет]    │ смотрела   │ этот фильм?
```
**"Она [имеет] смотрела этот фильм"**

```
They     │ have       │ arrived    │ already
Subject  │ Auxiliary  │ Verb (V3)  │ Adverb
Подлеж.  │ Вспом. гл. │ Глагол     │ Наречие
─────────────────────────────────────────────
Они      │ [имеют]    │ приехали   │ уже
```
**"Они [имеют] приехали уже"**

```
Has      │ he         │ eaten      │ lunch      │ ?
Auxiliary│ Subject    │ Verb (V3)  │ Object     │
Вспом.гл.│ Подлеж.    │ Глагол     │ Дополнен.  │
─────────────────────────────────────────────────
[Имеет]  │ он         │ поел       │ обед       │ ?
```
**"[Имеет] он поел обед?"**

**Почему именно так:**
- Порядок слов в английском жёсткий, русские строят предложения "на слух" и ошибаются: "She drinks always coffee", "Does she drinks coffee?", "I have seen already the film."
- Дословный "сломанный" перевод в английском порядке — не путает, а наоборот показывает логику. Пользователь видит скелет и запоминает структуру, а не набор правил.
- Скобки вокруг вспомогательного глагола [имеет] / [делает] — сигнал: это служебное слово, не переводи буквально.
- Таблица = якорь. Пользователь может вернуться к ней из кнопки "?" во время упражнений.

**Это правило распространяется на ВСЕ темы:** каждое время, модальные глаголы, пассивный залог, условные предложения — везде где есть "Образование" или объяснение структуры предложения.
2. **Примеры разнообразные.** Разные жизненные ситуации: еда, работа, школа, путешествия, семья, хобби. Не повторяй одни и те же слова.
3. **Больше карточек, а не длиннее карточки.** Лучше разбить тему на 5 коротких карточек, чем делать одну длинную. Каждая карточка = одно правило или нюанс.
4. **Максимум типов упражнений.** На одной карточке стараться использовать 2–3 разных типа упражнений. Не давать подряд одинаковые типы.
5. **Упражнения демонстрируют именно это правило.** Предложения в упражнениях просты (5–8 слов), но точно попадают в тему карточки.
6. **Переводы слов в WordArrangement — только для незнакомой лексики.** Не переводить грамматические формы и конструкции, которые являются предметом проверки данной карточки — перевод таких слов превращает упражнение в механическое сопоставление, а не понимание правила. Переводить только лексику (существительные, глаголы, прилагательные), которую студент мог не знать. Пример: на карточке "how much vs how many" — `How much`, `How many`, `How long` идут **без перевода**; `jacket` — **с переводом** "куртка".
7. **explanation в упражнениях — обязателен и показывается при НЕПРАВИЛЬНОМ ответе.** Объясняет конкретно, почему правильный вариант именно такой: не "неправильно", а "She — 3-е лицо ед.ч., поэтому to be = is, а не are." Без воды.
8. **IDs — текущий формат (временно, будет изменён):**
   - Карточки, примеры — сквозная нумерация по всему файлу (уникальны глобально)
   - Упражнения — сейчас тоже сквозные, но это **временно**

   **⚠️ TODO: Перенумерация упражнений по типам.** Каждый тип упражнения хранится в своей таблице БД (`WordArrangementExercise`, `TrueFalseExercise` и т.д. — см. `db_schema.md`). Значит `id` должен быть уникален внутри типа, не глобально. `WordArrangement(id=1)` и `TrueFalse(id=1)` — разные упражнения, конфликта нет.
   
   После написания всего контента теории — пройтись по файлам и перенумеровать каждый тип отдельно от 1. Таблица счётчиков в конце каждого блока теории тоже будет обновлена — вместо одного `Exercise` счётчика будет счётчик на каждый тип (включая `AiExercise`). Подробнее: `01-basics.md` → раздел «⚠️ TODO: Перенумерация упражнений по типам».
9. **aiExercise** — обязательно для каждой карточки. Пять полей в assets JSON:
   - **`id`** — уникальный строковый ключ. Формат: `"<microtopic>_card<N>_ex1"`. Пример: `"present_simple_card2_ex1"`. **Глобально уникален — этот же id используется как ключ промта на сервере.**
   - **`title`** — короткое название по-русски, 2–4 слова. Показывается в дереве «Упражнения из теории» и в поиске. Примеры: "Вставь артикль", "Переведи предложение", "Составь вопрос в Past Simple"
   - **`userInstruction`** — что делать, показывается пользователю перед заданием. 1–2 предложения.
   - **`inputMode`** — `"FREE_WRITE"` (свободный ввод текста) или `"FILL_BLANKS"` (пропуски встроены в текст задания). Подробнее: `db_schema.md → AiExerciseInputMode`
   - **`wordsSource`** — `"NONE"` / `"GENERAL"` / `"INFORMAL_ENGLISH"` / `"VERB_FORMS"`. Подробнее: `db_schema.md → AiExerciseWordsSource`

   Промт хранится на сервере в `ai_exercise_prompts` под ключом `id`. При написании контента — промт фиксируется отдельно в серверном файле.
10. **clarificationOptions** — 2–3 готовых варианта уточняющего вопроса для кнопки "Не совсем понял". Это самые частые точки непонимания по этому правилу. Формат: короткая фраза, не вопрос целиком ("Разница между a и an", "Когда артикль не нужен совсем"). Пользователь тапает — вопрос уходит в AI.

11. **TrueFalse — формат таблицы и правила перевода.**
    - Стандартный формат: три колонки `EN | RU | Верно?` (✓ или ✗, без текста "верно/неверно")
    - Если для оценки предложения нужна ситуация (например, this vs that зависит от расстояния) — использовать три колонки `Ситуация | EN | Верно?`
    - **Правило перевода в RU-колонке:** перевод должен быть корректным по-русски и НЕ палить ошибку. Если английское предложение содержит неверное слово (например, неправильное вопросительное слово), RU должен быть правильным русским эквивалентом того что имелось в виду — не дословным переводом ошибки.
      - "Where is your birthday?" → RU: "Когда твой день рождения?" (правильный русский, не "Где твой день рождения?")
      - "Who is the capital of Russia?" → RU: "Какая столица России?" (правильный русский)
      - Пользователь видит нормальный русский → смотрит на английский → сам замечает несоответствие. Так тест проверяет знание, а не умение найти странный перевод.
    - Не добавлять в колонку "Предложение/EN" подсказки-пояснения вроде "— говорю о предмете рядом" — они раскрывают ответ до того как пользователь подумал.

12. **Разнообразие типов упражнений — обязательно для каждого блока микротем.** Нельзя давать одни и те же 3–4 типа подряд. На каждые 3 микротемы обязательно включить не менее 4–5 разных типов. Переизбыток (использовать умеренно, не в каждой карточке): `MultipleChoice CHOICE`, `TrueFalse`, `WordArrangement`. Типы которые нужно активно вводить:
    - **Matching** — пары RU↔EN, местоимение↔форма, слово↔категория
    - **Categorization** — перетаскивание в колонки (near/far, мой/твой, ед/мн)
    - **ErrorCorrection** — предложение с ошибкой → выбрать правильный вариант
    - **Transformation** — изменить форму: ед. → мн., утверждение → отрицание
    - **FindTheOdd** — найти лишнее слово в группе по правилу
    - **ConstructionMeaning** — дана конструкция → выбрать правильный RU смысл
    - **DialogRestore** — короткий диалог с пропуском → выбрать реплику
    - **TableFill** — таблица с пропусками (формы местоимений, глаголов)

12. **WordArrangement — 2 попытки.** Если студент собрал предложение неправильно — даём одну повторную попытку. Если и вторая неправильная — показываем правильный ответ с объяснением. Применяется ко всем WordArrangement упражнениям.

13. **WordArrangement — не давать прямой перевод задания.** Вместо "RU: Сколько стоит эта куртка?" — описание ситуации: "Ты в магазине, хочешь узнать цену у продавца." Прямой перевод позволяет механически сопоставить слова без понимания правила. Исключение: если задание тестирует порядок слов, а не выбор конструкции — прямой перевод допустим.

14. **Имена в примерах и упражнениях — разнообразные русские.** Не использовать одни и те же имена (Максим, Анна) во всём файле. Варианты мужских: Артём, Игорь, Дима, Кирилл, Саша, Денис, Роман, Глеб. Варианты женских: Лена, Катя, Маша, Соня, Вика, Оля, Ира, Юля, Наташа. Чередовать случайно, не по порядку.

15. **AI-упражнения — минимум 2–3 пропуска.** Если тема позволяет — дать одно предложение с 2–3 пропусками. Если связное предложение с таким числом пропусков не складывается — дать 2–3 отдельных коротких несвязанных предложения, каждое с одним пропуском. Одиночный пропуск в AI-упражнении — недостаточно.

16. **Теория — подробно и с разных сторон.** Пользователь должен понять правило без знания английского. Обязательно:
    - Объяснить ЗАЧЕМ это правило существует (не просто "так говорят")
    - Показать правило в живом контексте (ситуация из жизни, не "the cat sat on the mat")
    - Предупредить о ловушках — частых ошибках которые делают русскоговорящие
    - Если правило похоже на другое — сказать "не путай с..."
    Более подробная теория = больше понимания у пользователя = больше доверия = выше конверсия в подписку.

13. **AI-упражнения в предтеме — без свободного составления предложений.** Пользователь предтемы почти не знает английский. Допустимые форматы:
    - Предложение с пропусками `___` + русский перевод → пользователь вписывает нужную форму
    - Дать слова-подсказки (RU → EN) вместе с заданием → пользователь составляет из них
    Нельзя: "AI описывает ситуацию → пользователь сам придумывает предложение с нуля"