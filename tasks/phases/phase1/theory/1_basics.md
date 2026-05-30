# 1 Basics · Основы

**ID:** 1 | **Order:** 1 | **isPretopic:** true  
**Описание:** Личные местоимения, глагол to be, артикли, предлоги — фундамент перед временами

> **Категория слов:** все слова предтемы "Основы" при открытии попадают в категорию **"Grammar Basics"** (раздел "Слова курса" → "Основы").


## ✅ Эталонные таблицы для каждого типа упражнений

Шаблоны всех 14 типов + AI Exercise + DB-схемы: [`tasks/phases/phase1/exercise_templates.md`](../../exercise_templates.md)

> ⚠️ **КРИТИЧНО при написании упражнений:**
> - Каждый тип имеет чёткое количество вопросов на один `Ex N` — смотри таблицу в начале exercise_templates.md
> - **1 вопрос = 1 упражнение** для: MultipleChoice, FORWARD_CHOICE, REVERSE_CHOICE, ErrorCorrection, ConstructionMeaning, DialogRestore, FindTheOdd
> - **Слипание нескольких вопросов в один Ex N — ошибка**, требующая разбивки и пересчёта всех ID
> - Формат каждого упражнения — жёстко по шаблону из exercise_templates.md, без отступлений


## ✅ Перенумерация упражнений по типам — выполнено

Каждый тип упражнения имеет свою нумерацию ID от 1. Глобальный `Ex N` в заголовках сохранён для навигации по файлу — только `(ID: N)` отражает реальный DB-ID внутри типа.
---

> **Clarification Options — как работает:** Пользователь нажимает готовую фразу, она уходит в AI, AI генерирует объяснение в реальном времени. В файле — только тексты вопросов, не ответы.
>
> **WordArrangement — формат:** Карточки со словами показываются в ПЕРЕМЕШАННОМ порядке. Дистракторы — лишние слова, которые не входят в правильное предложение. Пользователь сам решает, какие слова использовать. Зажать карточку = узнать перевод слова.
>
> **Tap-to-translate в AI-упражнениях:** Нажатие на любое слово в тексте от AI показывает его перевод. Глобальная функция для всех AI-упражнений в приложении. ⚠️ *Добавить в grammar8r_plan.md / features.md*
>
> **TrueFalse — стандарт:** всегда 5 предложений. Пользователь отмечает каждое верно/неверно. Схема и план обновлены.
>
> **Tap-to-translate:** нажатие на слово в AI-тексте уже описано в grammar8r_plan.md (раздел "Где работает" → AI-упражнения).
>
> ⚠️ **Card 19 добавлена вне плановой последовательности:** притяжательный 's (Lena's cat, Igor's phone) вставлена как третья карточка Microtopic 7 после Cards 13–14. ID карточки = 19 (продолжение сквозной нумерации), ID следующего блока начинаются после неё. grammar8r_plan.md обновлён.

---

# БЛОК 1 · Микротемы 1–3

---

## Microtopic 1 — Personal Pronouns · Личные местоимения

**ID:** 1 | **Order:** 1

---

### Card 1 · I, you, he, she, it, we, they

**ID:** 1 | **Order:** 1

#### Theory

Местоимение — слово-замена. Вместо того чтобы каждый раз повторять имя, мы заменяем его коротким словом.

"Максим работает в офисе. Максим устал. Максим поехал домой." — три раза "Максим" звучит странно. Поэтому: "Максим работает в офисе. **Он** устал. **Он** поехал домой." — "он" и есть личное местоимение.

В английском семь личных местоимений:

| Местоимение | Перевод | Когда использовать |
|---|---|---|
| **I** | я | всегда с заглавной буквы — даже в середине предложения |
| **you** | ты / вы | одинаково для одного человека и для группы |
| **he** | он | мужчина или мальчик |
| **she** | она | женщина или девочка |
| **it** | оно | предмет, животное (пол неважен), явление природы |
| **we** | мы | я + кто-то ещё |
| **they** | они | несколько человек или предметов |

**Ловушка 1:** По-русски "Вы" — это вежливое обращение к одному человеку. В английском такого нет. "You" — просто ты или вы, без особой вежливости.

**Ловушка 2:** "it" для животных. Если пол неизвестен или неважен — it. Если знаешь пол — можно he или she: "This is my dog Max. He loves to run."

#### Summary *(кнопка «?» в упражнениях)*

I/you/he/she/it/we/they — слова-замены для имён и предметов. I — всегда с заглавной. you — и ты, и вы (вежливого "Вы" в английском нет). it — для предметов и животных неизвестного пола.

#### Examples

| # | RU | EN |
|---|----|----|
| 1 | Я еду на работу. | I am going to work. |
| 2 | Она готовит ужин. | She is cooking dinner. |
| 3 | Он смотрит футбол. | He is watching football. |
| 4 | Мы живём в Санкт-Петербурге. | We live in Saint Petersburg. |
| 5 | Они учатся в университете. | They study at university. |
| 6 | Мой телефон разрядился. Он не включается. | My phone died. It won't turn on. |

---

#### Exercises

**Ex 1 · TableFill** *(ID: 1)*

Задание: вспомни и запиши английское местоимение для каждого перевода

| Подсказка (RU) | Ответ |
|----------------|-------|
| я | I |
| ты / вы | you |
| он | he |
| она | she |
| оно / предмет | it |
| мы | we |
| они | they |

*Explanation (при ошибке):* Семь местоимений — основа любого предложения. I — я (всегда заглавная). You — ты/вы. He — мужчина. She — женщина. It — предмет или животное. We — мы. They — они.

---

**Ex 2 · MultipleChoice · REVERSE_CHOICE** *(ID: 1)*

Переведи предложение на русский:  
"She works at a hospital."

- Он работает в больнице.
- **Она работает в больнице.** ✓
- Они работают в больнице.

*Explanation (при ошибке):* She = она. Works = работает. At a hospital = в больнице.

---

**Ex 3 · MultipleChoice · FORWARD_CHOICE** *(ID: 1)*

Выбери английский перевод:  
"Мой брат — программист."

- She is a programmer.
- **He is a programmer.** ✓
- It is a programmer.

*Explanation (при ошибке):* Брат — мужчина, поэтому he (он). She — только для женщин, it — только для предметов.

---

**Ex 4 · TrueFalse** *(ID: 1)*

Задание: отметь верные и неверные утверждения

| # | EN | RU | Верно? |
|---|----|----|--------|
| 1 | In English, 'you' is only used for one person. | В английском 'you' — только для одного человека. | ✗ |
| 2 | The pronoun 'I' is always written with a capital letter. | Местоимение 'I' всегда пишется с заглавной. | ✓ |
| 3 | When talking about an animal of unknown gender, we use 'it'. | О животном неизвестного пола говорим 'it'. | ✓ |
| 4 | In English there is a polite form of 'you', like in Russian. | В английском есть вежливая форма 'you', как в русском. | ✗ |
| 5 | 'They' refers to more than one person or thing. | 'They' обозначает несколько людей или предметов. | ✓ |

*Explanation (при ошибке):* "You" используется и для одного (ты), и для нескольких (вы). Вежливого "Вы" в английском нет. "It" — для предметов и животных неизвестного пола. "I" — всегда заглавная.

---

**Ex 5 · MultipleChoice · FORWARD_CHOICE** *(ID: 2)*

Выбери английский перевод:  
"Моя машина сломалась."

- He broke down.
- She broke down.
- **It broke down.** ✓

*Explanation (при ошибке):* Машина — предмет. Для предметов в английском — it. He и she — только для людей (и животных с известным полом).

---

**Ex 6 · WordArrangement** *(ID: 1)*

RU: Они живут в Москве.

Правильное предложение: **They live in Moscow.**

Слова (включая лишние):

| Слово | Перевод | Лишнее? |
|-------|---------|---------|
| They | Они | — |
| live | живут | — |
| in | в | — |
| Moscow | Москве | — |
| I | Я | ✗ дистрактор |
| She | Она | ✗ дистрактор |
| He | Он | ✗ дистрактор |

*Explanation (при ошибке):* They = они. Live = живут. In = в (предлог для городов). Moscow = Москва. I/She/He — лишние, речь идёт о группе (они).

---

#### AI Exercise

**ID:** basics_card1_ex1
**Title:** "Вставь местоимение"
**Input Mode:** FILL_BLANKS
**Words Source:** NONE
**AI Config Profile:** EXERCISE_LIGHT
**User Instruction:** "AI опишет человека или ситуацию по-русски с пропуском. Вставь нужное местоимение: I / you / he / she / it / we / they."
**Prompt Template:** "Дай описание человека или предмета по-русски с пропуском [___] на личное местоимение (например: 'Твоя подруга Лена учится на врача. [___] очень умная.'). Слова-подсказки не нужны — контекст ясен. Проверь выбор и объясни ошибку: почему именно he/she/it/they. Пример вывода AI: 'Твой друг Игорь работает в офисе. [___] очень занятой.'"

#### Clarification Options

- Почему I всегда с заглавной буквы
- Когда животное — it, а когда he или she
- Разница между you (ты) и you (вы) — почему одно слово

---

### Words8r Sync · Личные местоимения

После завершения микротемы предлагаем добавить в Words8r (категория **"Grammar Basics"**):

| Слово | Переводы | Транскрипция |
|-------|----------|-------------|
| I | я | [aɪ] |
| you | ты, вы | [juː] |
| he | он | [hiː] |
| she | она | [ʃiː] |
| it | оно, это (для предметов и животных) | [ɪt] |
| we | мы | [wiː] |
| they | они | [ðeɪ] |

---
---

## Microtopic 2 — Verb "to be" · Глагол to be

**ID:** 2 | **Order:** 2

---

### Card 2 · am / is / are — утверждение

**ID:** 2 | **Order:** 1

#### Theory

"To be" — самый главный глагол английского языка. Переводится как "быть, являться, находиться". Но вот в чём штука: по-русски мы его почти не произносим вслух.

По-русски: "Я студент." — никакого "быть" нет.  
По-английски: "I **am** a student." — без "am" предложение не существует.

Этот глагол выполняет в английском три роли:

1. **Кто ты есть:** "She is a nurse." — Она медсестра.
2. **Какой ты:** "I am tired." — Я устал.
3. **Где ты находишься:** "They are in the garden." — Они в саду.

В русском все три варианта работают без глагола — тире или интонацией: "Она медсестра. Я устал. Они в саду." В английском без to be предложение обрывается на полуслове.

Запомни три формы:

| Кто | Форма | Краткая | Пример |
|-----|-------|---------|--------|
| I | **am** | I'm | I am hungry. |
| you | **are** | you're | You are right. |
| he | **is** | he's | He is at work. |
| she | **is** | she's | She is a doctor. |
| it | **is** | it's | It is cold today. |
| we | **are** | we're | We are friends. |
| they | **are** | they're | They are at home. |

Краткие формы (I'm, you're, he's...) — разговорные. В обычной речи всегда используем их. Полные формы — для официального текста или для акцента: "I **am** serious!" (чтобы подчеркнуть).

**Не путай:** **it's** = it is. А **its** (без апострофа) — притяжательное: "The cat licked its paw." Апостроф — это пропущенная буква "i". Запомни: если можно сказать "it is" — пишем "it's". Если нельзя — пишем "its".

Кстати: у to be есть прошедшие формы — was и were. Они понадобятся позже, когда будем изучать прошедшие времена. Пока работаем с am/is/are.

#### Summary *(кнопка «?» в упражнениях)*

am — только с I. is — с he/she/it. are — с you/we/they. В разговоре сокращай: I'm, he's, they're. Без to be предложение на английском не работает — даже если в русском он не звучит.

#### Examples

| # | RU | EN |
|---|----|----|
| 7 | Я студент. | I am a student. / I'm a student. |
| 8 | Ты прав. | You are right. / You're right. |
| 9 | Он на работе. | He is at work. / He's at work. |
| 10 | Она врач. | She is a doctor. / She's a doctor. |
| 11 | Сегодня холодно. | It is cold today. / It's cold today. |
| 12 | Мы друзья. | We are friends. / We're friends. |
| 13 | Они дома. | They are at home. / They're at home. |

---

#### Exercises

**Ex 7 · TableFill** *(ID: 2)*

Задание: вставь нужную форму to be

| Подлежащее | Ответ |
|------------|-------|
| I ___ | am |
| you ___ | are |
| he ___ | is |
| she ___ | is |
| it ___ | is |
| we ___ | are |
| they ___ | are |

*Explanation (при ошибке):* am — только с I. is — с he/she/it (единственное число, 3-е лицо). are — с you/we/they. Три формы: am, is, are.

---

**Ex 8 · TextInput** *(ID: 1)*

"She ___ tired after work."  
Правильный ответ: **is**

*Explanation (при ошибке):* She — 3-е лицо единственного числа (как he и it). Для he/she/it используется is.

---

**Ex 9 · MultipleChoice · REVERSE_CHOICE** *(ID: 2)*

Переведи на русский:  
"They're at school."

- Он в школе.
- Мы в школе.
- **Они в школе.** ✓

*Explanation (при ошибке):* They're = They are = они. At school = в школе.

---

**Ex 10 · TextInput** *(ID: 2)*

"We ___ ready."  
Правильный ответ: **are**

*Explanation (при ошибке):* We — это мы, несколько человек. Для you/we/they используется are.

---

**Ex 11 · TrueFalse** *(ID: 2)*

Задание: отметь верные и неверные предложения

| # | EN | RU | Верно? |
|---|----|----|--------|
| 1 | He are a good cook. | Он хороший повар. | ✗ |
| 2 | They are at home. | Они дома. | ✓ |
| 3 | I is hungry. | Я голоден. | ✗ |
| 4 | She is a doctor. | Она врач. | ✓ |
| 5 | We is ready. | Мы готовы. | ✗ |

*Explanation (при ошибке):* am — только с I. is — с he/she/it. are — с you/we/they. He are → He is. I is → I am. We is → We are.

---

**Ex 12 · WordArrangement** *(ID: 2)*

RU: Она — моя сестра.

Правильное предложение: **She is my sister.**

Слова (включая лишние):

| Слово | Перевод | Лишнее? |
|-------|---------|---------|
| She | Она | — |
| is | есть (является) | — |
| my | моя | — |
| sister | сестра | — |
| He | Он | ✗ дистрактор |
| am | есть (только для I) | ✗ дистрактор |
| are | есть (мн.ч.) | ✗ дистрактор |

*Explanation (при ошибке):* She = она → форма to be = is. My = моя. Sister = сестра. He — не подходит (мужской род), am — только с I, are — для you/we/they.

---

**Ex 13 · MultipleChoice · CHOICE** *(ID: 1)*

"___ very happy today."

- **I am** ✓
- I is
- I are

*Explanation (при ошибке):* С местоимением I используется только am — это уникальная форма. "I is" и "I are" — не существуют в английском.

---

#### AI Exercise

**ID:** basics_card2_ex1
**Title:** "Переведи предложение с to be"
**Input Mode:** FREE_WRITE
**Words Source:** NONE
**AI Config Profile:** EXERCISE_LIGHT
**User Instruction:** "AI даст русское предложение и слова-подсказки (существительные и прилагательные). Составь полный перевод на английский с правильной формой am / is / are."
**Prompt Template:** "Дай русское предложение с глаголом to be (например: 'Я — учитель.' / 'Она устала.' / 'Мы дома.'). Предоставь слова-подсказки для лексики (teacher, tired, at home) — но НЕ давай местоимения и НЕ давай форму to be. Пользователь пишет полный перевод. Проверь форму am/is/are и объясни ошибку."

#### Clarification Options

- Зачем нужен to be, если в русском его нет
- Разница: I'm vs I am — когда полная форма
- Почему it's и its — разные слова

---

### Card 3 · Отрицание: am not / isn't / aren't

**ID:** 3 | **Order:** 2

#### Theory

Чтобы сказать "не является, не находится" — добавляем **not** после формы to be:

| Полная форма | Краткая | Перевод |
|---|---|---|
| I am not | I'm not | я не... |
| you are not | you aren't | ты/вы не... |
| he is not | he isn't | он не... |
| she is not | she isn't | она не... |
| it is not | it isn't | оно не... |
| we are not | we aren't | мы не... |
| they are not | they aren't | они не... |

**Важно:** форма для I — только **I'm not**. Слова "I amn't" не существует в английском — никогда не использовалось и не используется. Для всех остальных: **isn't** (is not) и **aren't** (are not).

В разговорной речи почти всегда используем краткие формы: "He isn't ready." / "We aren't home."

#### Summary *(кнопка «?» в упражнениях)*

not ставится сразу после am/is/are. Краткие формы: I'm not (не "I amn't"!), isn't, aren't. В разговоре — всегда краткие.

#### Examples

| # | RU | EN |
|---|----|----|
| 14 | Я не дома. | I am not at home. / I'm not at home. |
| 15 | Он не врач. | He is not a doctor. / He isn't a doctor. |
| 16 | Мы не готовы. | We are not ready. / We aren't ready. |
| 17 | Она не устала. | She is not tired. / She isn't tired. |
| 18 | Они не в офисе. | They are not in the office. / They aren't in the office. |

---

#### Exercises

**Ex 14 · TextInput** *(ID: 3)*

"He ___ at work today." *(сделай отрицание)*  
Правильные ответы: **is not / isn't**

*Explanation (при ошибке):* Отрицание с he: is + not = is not = isn't.

---

**Ex 15 · MultipleChoice · CHOICE** *(ID: 2)*

"I ___ ready yet."

- **am not** ✓
- isn't
- aren't

*Explanation (при ошибке):* С I — только "am not". "I amn't" — такой формы не существует. "Isn't" и "aren't" — с другими местоимениями.

---

**Ex 16 · TrueFalse** *(ID: 3)*

Задание: отметь верные и неверные предложения

| # | EN | RU | Верно? |
|---|----|----|--------|
| 1 | I amn't tired. | Я не устал. | ✗ |
| 2 | She isn't at work today. | Её нет на работе сегодня. | ✓ |
| 3 | They isn't ready. | Они не готовы. | ✗ |
| 4 | He isn't a teacher. | Он не учитель. | ✓ |
| 5 | We am not hungry. | Мы не голодны. | ✗ |

*Explanation (при ошибке):* "Amn't" — не существует: I am not / I'm not. They → aren't (не isn't). We → aren't (не am not).

---

**Ex 17 · WordArrangement** *(ID: 3)*

RU: Её нет дома.

Правильное предложение: **She isn't at home.**

Слова (включая лишние):

| Слово | Перевод | Лишнее? |
|-------|---------|---------|
| She | Она | — |
| isn't | не (is not) | — |
| at | предлог места | — |
| home | дом | — |
| He | Он | ✗ дистрактор |
| I | Я | ✗ дистрактор |
| am not | не (только для I) | ✗ дистрактор |
| aren't | не (мн.ч.) | ✗ дистрактор |

*Explanation (при ошибке):* She isn't = she is not = она не. At home = дома. He/I — неверные местоимения. Am not — только с I. Aren't — с you/we/they.

---

**Ex 18 · MultipleChoice · FORWARD_CHOICE** *(ID: 3)*

"Мы не студенты."

- We isn't students.
- **We aren't students.** ✓
- We am not students.

*Explanation (при ошибке):* We → are → отрицание: are not / aren't. "Isn't" — только с he/she/it. "Am not" — только с I.

---

**Ex 19 · DialogRestore** *(ID: 1)*

A: "Is your brother a doctor?"  
B: ___

- **No, he isn't. He's a teacher.** ✓
- No, he aren't. He's a teacher.
- No, he am not. He's a teacher.

*Explanation (при ошибке):* He → is → отрицание: he isn't (= he is not). "He aren't" и "he am not" — неверные формы для he.

---

#### AI Exercise

**ID:** basics_card3_ex1
**Title:** "Сделай отрицание с to be"
**Input Mode:** FREE_WRITE
**Words Source:** NONE
**AI Config Profile:** EXERCISE_LIGHT
**User Instruction:** "AI даст утвердительное предложение с to be. Перепиши его в отрицательной форме."
**Prompt Template:** "Дай одно утвердительное предложение с to be (например: 'She is at home.'). Пользователь пишет отрицательную форму. Проверь правильность am not / isn't / aren't и объясни ошибку. Поддерживай сокращения (isn't / aren't) и полные формы (is not / are not)."

#### Clarification Options

- Почему нельзя сказать "I amn't"
- Разница: isn't и aren't — когда какое
- Когда использовать полную форму "is not", а не краткую "isn't"

---

### Card 4 · Вопрос: Am I? / Is he? / Are they?

**ID:** 4 | **Order:** 3

#### Theory

В английском вопрос с to be строится очень просто: **глагол встаёт перед подлежащим**.

Утверждение: He **is** at home. → Вопрос: **Is** he at home?  
Утверждение: They **are** ready. → Вопрос: **Are** they ready?  
Утверждение: I **am** late. → Вопрос: **Am** I late?

Формула: **Am/Is/Are + подлежащее + остальное + ?**

**Краткие ответы:**

- "Is she tired?" → "Yes, she **is**." / "No, she **isn't**."  
  *(Она устала? — Да. / Нет.)*
- "Are they home?" → "Yes, they **are**." / "No, they **aren't**."  
  *(Они дома? — Да. / Нет.)*
- "Am I right?" → "Yes, you **are**." / "No, you **aren't**."  
  *(Я прав? — Да. / Нет.)*

**Важно:** в кратком положительном ответе сокращать нельзя. "Yes, she **is**." — правильно. "Yes, she's." — так не говорят. Сокращение делает ответ незавершённым.

#### Summary *(кнопка «?» в упражнениях)*

Вопрос: am/is/are ставится перед подлежащим. Am I? Is he/she/it? Are you/we/they? Краткий ответ: Yes, she is. / No, she isn't. В положительном кратком ответе сокращение не используется.

#### Examples

| # | RU | EN |
|---|----|----|
| 19 | Он дома? | Is he at home? |
| 20 | Ты готов? | Are you ready? |
| 21 | Они студенты? | Are they students? |
| 22 | Я опоздал? | Am I late? |
| 23 | Да, она устала. | Yes, she is. |
| 24 | Нет, он не дома. | No, he isn't. |

---

#### Exercises

**Ex 20 · MultipleChoice · FORWARD_CHOICE** *(ID: 4)*

"Ты голоден?"

- You are hungry?
- **Are you hungry?** ✓
- Is you hungry?

*Explanation (при ошибке):* В вопросе to be встаёт перед подлежащим. You + are → вопрос: Are you...? "Is you" — ошибка: is используется только с he/she/it.

---

**Ex 21 · TextInput** *(ID: 4)*

"___ she at school?"  
Правильный ответ: **Is**

*Explanation (при ошибке):* She — 3-е лицо единственного числа. Вопрос: Is she...?

---

**Ex 22 · TrueFalse** *(ID: 4)* ⚠️ *Новый тип упражнения — несколько предложений*

Задание: отметь все ВЕРНЫЕ предложения (их может быть несколько)

| # | EN | RU | Верно? |
|---|----|----|--------|
| 1 | Yes, they are. | Да, они. | ✓ |
| 2 | Yes, they're. | Да, они. | ✗ |
| 3 | Yes, she is. | Да, она. | ✓ |
| 4 | Yes, she's. | Да, она. | ✗ |
| 5 | Yes, I am. | Да, я. | ✓ |

*Explanation (при ошибке):* В кратком положительном ответе нельзя сокращать. Yes, they **are**. / Yes, she **is**. / Yes, I **am**. — правильно. "They're" и "she's" как ответ — незавершённо: так не говорят.

---

**Ex 23 · WordArrangement** *(ID: 4)*

RU: Ты — учитель?

Правильное предложение: **Are you a teacher?**

Слова (включая лишние; "a teacher" — одна карточка, артикль разберём позже):

| Слово | Перевод | Лишнее? |
|-------|---------|---------|
| Are | вопрос (являешься ли) | — |
| you | ты | — |
| a teacher | учитель | — |
| I | Я | ✗ дистрактор |
| She | Она | ✗ дистрактор |
| is | есть (для he/she/it) | ✗ дистрактор |
| am | есть (для I) | ✗ дистрактор |

*Explanation (при ошибке):* В вопросе are/is/am стоит первым. Are you = вопрос для "ты/вы". A teacher — одна карточка (артикль изучим позже). Is и am — неверные формы для you.

---

**Ex 24 · MultipleChoice · CHOICE** *(ID: 3)*

"___ I in the right place?"

- **Am** ✓
- Is
- Are

*Explanation (при ошибке):* Вопрос с I — только Am. Am I? — единственная правильная форма для первого лица.

---

**Ex 25 · DialogRestore** *(ID: 2)*

A: "___ your parents at home?"  
B: "Yes, they are. They're having dinner."

Варианты для реплики A:
- **Are** ✓
- Is
- Am

*Explanation (при ошибке):* Parents = родители = they. С they → вопрос: Are.

---

#### AI Exercise

**ID:** basics_card4_ex1
**Title:** "Составь вопрос с to be"
**Input Mode:** FREE_WRITE
**Words Source:** NONE
**AI Config Profile:** EXERCISE_LIGHT
**User Instruction:** "AI даст утвердительное предложение с to be. Переделай его в вопрос и дай краткий ответ Yes или No (на твой выбор)."
**Prompt Template:** "Дай одно утвердительное предложение с to be (например: 'She is tired.'). Пользователь пишет вопрос и краткий Yes/No ответ. Проверь порядок слов в вопросе (Is she tired?) и правильность краткого ответа (Yes, she is. / No, she isn't.). Объясни ошибку если есть."

#### Clarification Options

- Почему в "Yes, she is" нельзя сказать "Yes, she's"
- Как строить вопрос с I — Am I?
- Разница между Is he? и Are they?

---

### Words8r Sync · Глагол to be

После завершения микротемы предлагаем добавить в Words8r (категория **"Grammar Basics"**):

| Слово | Переводы | Транскрипция |
|-------|----------|-------------|
| to be | быть, являться, находиться | [tuː biː] |
| am | есть, являюсь (форма to be для I) | [æm] |
| is | есть, является (форма to be для he/she/it) | [ɪz] |
| are | есть, являетесь (форма to be для you/we/they) | [ɑː] |

---
---

## Microtopic 3 — There is / There are

**ID:** 3 | **Order:** 3

---

### Card 5 · There is / There are — утверждение

**ID:** 5 | **Order:** 1

#### Theory

"There is" и "There are" — конструкция, которой в русском нет как отдельного явления. Мы говорим "На столе стоит кружка" или "В парке есть лавочки". В английском — другой порядок: сначала объявляем существование, потом уточняем место.

"**There is** a cup on the table." — Есть кружка на столе.

Представь, что ты заходишь в незнакомую квартиру и описываешь что в ней есть. Каждое утверждение о наличии чего-то начинается с "there is/are":
- "There is a bed in the bedroom." — В спальне есть кровать.
- "There are two windows in the kitchen." — На кухне два окна.
- "There is a coffee machine on the counter." — На стойке стоит кофемашина.

Это невероятно частая конструкция в английской речи — потому что нужна всякий раз, когда говоришь о том, что что-то **есть** или **находится** в каком-то месте.

Правило выбора:
- **There is** — если дальше **один** предмет (или неисчисляемое вещество): "There is a cat." / "There is water in the glass."
- **There are** — если дальше **несколько** предметов: "There are three cats." / "There are books on the shelf."

Краткая форма: **There's** = there is. Очень часто в разговоре. "There're" (there are) технически существует, но в устной речи почти не используют — говорят полностью "there are".

**Важно:** "there" в этой конструкции — **НЕ "там"** в значении места. "There is a problem." — не "там есть проблема", а просто "есть проблема". Не путай с "over there" (вон там).

#### Summary *(кнопка «?» в упражнениях)*

There is — для одного предмета или неисчисляемого. There are — для нескольких. "There" здесь — не "там", а конструкция "есть/находится". There's = there is.

#### Examples

| # | RU | EN |
|---|----|----|
| 25 | В холодильнике есть молоко. | There is milk in the fridge. |
| 26 | На улице есть кафе. | There is a café on the street. |
| 27 | В парке три скамейки. | There are three benches in the park. |
| 28 | В классе 20 студентов. | There are 20 students in the class. |
| 29 | Есть одна проблема. | There is a problem. |
| 30 | В меню много вариантов. | There are many options on the menu. |

---

#### Exercises

**Ex 26 · MultipleChoice · CHOICE** *(ID: 4)*

"___ a hospital near here."

- **There is** ✓
- There are
- Is there

*Explanation (при ошибке):* "A hospital" — один предмет (a = один). Один предмет → There is.

---

**Ex 27 · MultipleChoice · REVERSE_CHOICE** *(ID: 3)*

Переведи на русский:  
"There's a new café on my street."

- **На моей улице есть новое кафе.** ✓
- Там есть новое кафе.
- Это моя улица.

*Explanation (при ошибке):* There's = There is = "есть, находится". Это не "там" — это конструкция наличия. On my street = на моей улице.

---

**Ex 28 · MultipleChoice · CHOICE** *(ID: 5)*

RU: В комнате пять человек.  
"___ five people in the room."

- There is
- **There are** ✓
- There were

*Explanation (при ошибке):* Five people — несколько (множественное число). Несколько → There are.

---

**Ex 29 · TrueFalse** *(ID: 5)*

Задание: отметь верные и неверные предложения

| # | EN | RU | Верно? |
|---|----|----|--------|
| 1 | There are a dog in the garden. | В саду есть собака. | ✗ |
| 2 | There are two books on the table. | На столе две книги. | ✓ |
| 3 | There is three chairs in the kitchen. | На кухне три стула. | ✗ |
| 4 | There is some water in the bottle. | В бутылке есть вода. | ✓ |
| 5 | There are a problem. | Есть проблема. | ✗ |

*Explanation (при ошибке):* A dog, a problem — одно → There is. Three chairs, two books — несколько → There are. Some water — неисчисляемое → There is.

---

**Ex 30 · WordArrangement** *(ID: 5)*

RU: На диване два кота.

Правильное предложение: **There are two cats on the sofa.**

Слова (включая лишние; "the sofa" — одна карточка, артикль разберём позже; зажать карточку = узнать перевод):

| Слово | Перевод | Лишнее? |
|-------|---------|---------|
| There | конструкция «есть» | — |
| are | есть (мн.ч.) | — |
| two | два | — |
| cats | кота | — |
| on | на | — |
| the sofa | диване | — |
| is | есть (ед.ч.) | ✗ дистрактор |
| There's | есть (сокр.) | ✗ дистрактор |
| a cat | кот (ед.ч.) | ✗ дистрактор |

*Explanation (при ошибке):* There are = есть (несколько). Two cats = два кота. On the sofa = на диване. "is" и "There's" — для единственного числа. "A cat" — один кот, нам нужно "two cats".

---

#### AI Exercise

**ID:** basics_card5_ex1
**Title:** "There is или There are?"
**Input Mode:** FILL_BLANKS
**Words Source:** NONE
**AI Config Profile:** EXERCISE_LIGHT
**User Instruction:** "AI даст русское предложение и его частичный английский перевод с пропуском. Вставь There is или There are."
**Prompt Template:** "Дай русское предложение с конструкцией there is/are и частичный английский перевод с пропуском '___' (например: RU: 'В комнате стоит диван.' → EN: '___ a sofa in the room.'). Пользователь вписывает 'There is' или 'There are'. Проверь и объясни ошибку — обрати внимание на число существительного после."  
Затем кнопка "Ещё задание".

#### Clarification Options

- Почему "there" — не "там"
- There is или There are с неисчисляемыми (water, milk, bread)
- Можно ли сократить "there are" → "there're"

---

### Card 6 · There isn't / There aren't / Is there? / Are there?

**ID:** 6 | **Order:** 2

#### Theory

Отрицание и вопрос строятся по той же логике, что и в to be.

**Отрицание:**
- **There isn't** (= there is not) — нет одного предмета
- **There aren't** (= there are not) — нет нескольких

"There isn't any milk." — Молока нет.  
"There aren't any seats." — Мест нет.

**Про слово "any":**

В обычных предложениях используем "a" (один предмет) или "some" (несколько): "There is a book." / "There are some books." В отрицаниях и вопросах вместо них ставится **any** — оно значит "хоть один, хоть сколько-нибудь":

- "There isn't **any** milk." — Молока нет совсем, ни капли.
- "Is there **any** milk?" — Есть хоть немного молока?

Проще говоря: в вопросах — "есть хоть что-нибудь?", в отрицаниях — "нет совсем ничего". Пока просто замечай "any" в примерах, и оно само отложится.

**Вопрос:**
- **Is there** + существительное + ? — есть ли (один)?
- **Are there** + существительное + ? — есть ли (несколько)?

"Is there a supermarket near here?" — Здесь есть супермаркет?  
"Are there any free tables?" — Есть свободные столики?

**Краткие ответы:**
- "Is there a lift?" → "Yes, there is." / "No, there isn't."
- "Are there any seats?" → "Yes, there are." / "No, there aren't."

#### Summary *(кнопка «?» в упражнениях)*

Отрицание: there isn't (один), there aren't (несколько). Вопрос: Is there? (один), Are there? (несколько). any заменяет "a/some" в отрицаниях и вопросах — значит "хоть один/совсем нет". Краткий ответ: Yes, there is. / No, there aren't.

#### Examples

| # | RU | EN |
|---|----|----|
| 31 | Молока нет. | There isn't any milk. |
| 32 | В парке нет скамеек. | There aren't any benches in the park. |
| 33 | Здесь есть банк? | Is there a bank near here? |
| 34 | В меню есть вегетарианские блюда? | Are there any vegetarian dishes on the menu? |
| 35 | Да, есть. | Yes, there is. |
| 36 | Нет, мест нет. | No, there aren't any seats. |

---

#### Exercises

**Ex 31 · MultipleChoice · CHOICE** *(ID: 6)*

RU: В холодильнике нет яиц.  
"___ any eggs in the fridge."

- There isn't
- **There aren't** ✓
- There not are

*Explanation (при ошибке):* "Eggs" — множественное число. Отрицание для нескольких: There aren't.

---

**Ex 32 · MultipleChoice · FORWARD_CHOICE** *(ID: 5)*

"Здесь нет парковки."

- **There isn't a parking lot here.** ✓
- There aren't a parking lot here.
- Is there a parking lot here?

*Explanation (при ошибке):* "Парковка" — один объект (a parking lot). Отрицание для одного: There isn't.

---

**Ex 33 · TextInput** *(ID: 5)*

RU: В классе есть студенты?  
"___ there any students in the classroom?"  
Правильный ответ: **Are**  
Подсказка: Is / Are

*Explanation (при ошибке):* "Students" — множественное число. Вопрос для нескольких: Are there...?

---

**Ex 34 · MultipleChoice · FORWARD_CHOICE** *(ID: 6)*

"Здесь есть банкомат?"

- There is an ATM here.
- **Is there an ATM here?** ✓
- Are there an ATM here?

*Explanation (при ошибке):* Вопрос об одном предмете: Is there...? "ATM" — один → Is. "Are there" — для нескольких.

---

**Ex 35 · DialogRestore** *(ID: 3)*

RU: A: "Извините, здесь рядом есть аптека?" / B: "Да, есть. Она на следующей улице."

A: "Excuse me, is there a pharmacy near here?"  
B: ___

- **Yes, there is. It's on the next street.** ✓
- Yes, there are. It's on the next street.
- Yes, is there. It's on the next street.

*Explanation (при ошибке):* Краткий ответ на "Is there...?" — "Yes, there is." Сокращать нельзя. "Yes, there are" — ошибка: вопрос был про один предмет (a pharmacy).

---

**Ex 36 · TrueFalse** *(ID: 6)*

Задание: отметь верные и неверные предложения

| # | EN | RU | Верно? |
|---|----|----|--------|
| 1 | Is there many people at the concert? | На концерте много людей? | ✗ |
| 2 | Are there any seats left? | Есть свободные места? | ✓ |
| 3 | There aren't a parking lot here. | Здесь нет парковки. | ✗ |
| 4 | Is there a supermarket near here? | Здесь есть супермаркет? | ✓ |
| 5 | There isn't any milk in the fridge. | В холодильнике нет молока. | ✓ |

*Explanation (при ошибке):* Many people — множественное → Are there. A parking lot — одно → There isn't. Вопрос об одном: Is there? Вопрос о нескольких: Are there?

---

#### AI Exercise

**ID:** basics_card6_ex1
**Title:** "Отрицание и вопрос: There is/are"
**Input Mode:** FREE_WRITE
**Words Source:** NONE
**AI Config Profile:** EXERCISE_LIGHT
**User Instruction:** "AI даст утвердительное предложение с There is/are. Напиши его отрицание и вопрос из него."
**Prompt Template:** "Дай русское предложение и его английский утвердительный перевод с there is/are (например: 'There are free tables in the café.'). Пользователь пишет (1) отрицание и (2) вопрос. Проверь обе формы: There isn't/aren't и Is/Are there...? Объясни ошибки."  
Отрицание: переставить/добавить not. Вопрос: поменять порядок слов.  
AI проверяет обе формы и объясняет ошибки.

#### Clarification Options

- Когда "any" появляется в there isn't/aren't — и почему не "a/some"
- Разница: Is there? vs Are there?
- Краткий ответ "Yes, there is" — почему нельзя "Yes, there's"

---

### Words8r Sync · There is / There are

После завершения микротемы предлагаем добавить в Words8r (категория **"Grammar Basics"**):

| Слово | Переводы | Транскрипция |
|-------|----------|-------------|
| there | там; (there is/are: есть, находится) | [ðer] |

---
---
#######################################################################################################################

---

# БЛОК 2 · Микротемы 4–6

---

## Microtopic 4 — Have / Has · Глагол have / has

**ID:** 4 | **Order:** 4

---

### Card 7 · have / has — утверждение

**ID:** 7 | **Order:** 1

#### Theory

"Have" и "has" — это глагол "иметь, обладать". Главное, что нужно понять: в английском без него нельзя сказать "у меня есть".

По-русски: "У меня есть кошка" — подлежащее (я) прячется внутри "у меня". В английском всё переворачивается: "I **have** a cat." — дословно "Я имею кошку". *Субъект всегда на первом месте, глагол идёт следом.*

**Have не только для вещей.** Глагол have — один из самых многозначных в английском. Вот все его роли:

- **Владение вещами:** "I have a car." — *У меня есть машина.* "She has a laptop." — *У неё есть ноутбук.*
- **Отношения:** "He has a brother." — *У него есть брат.* "We have a new neighbour." — *У нас новый сосед.*
- **Внешность и части тела:** "She has long hair." — *У неё длинные волосы.* "He has blue eyes." — *У него голубые глаза.*
- **Физические состояния:** "I have a cold." — *У меня простуда.* "She has a headache." — *У неё болит голова.*
- **Еда и напитки:** "Let's have lunch." — *Давай пообедаем.* "Do you want to have a coffee?" — *Хочешь выпить кофе?*
- **Опыт:** "Have a look." — *Взгляни.* "Have fun!" — *Получи удовольствие!*

Пока запомни главное: для владения, отношений и состояний — have/has. Остальные значения встретятся в примерах и отложатся сами.

**Одна важная ловушка:** have в значении "владения" **не используется в форме Continuous**. Нельзя сказать "I am having a car" — только "I have a car". Про Continuous мы ещё не дошли, но запомни уже сейчас: "у меня есть" — это всегда have, без am/is/are.

Правило выбора формы:
- **have** — с I, you, we, they
- **has** — с he, she, it (3-е лицо единственного числа — та же логика, что у to be)

| Кто | Форма | Пример |
|-----|-------|--------|
| I | **have** | I have a car. |
| you | **have** | You have a good job. |
| he | **has** | He has a brother. |
| she | **has** | She has long hair. |
| it | **has** | It has four wheels. |
| we | **have** | We have a meeting today. |
| they | **have** | They have two cats. |

Краткой формы у have/has в значении "владения" нет — только полная. "I've" существует, но это часть других конструкций (Present Perfect) — не простого "у меня есть".

#### Summary *(кнопка «?» в упражнениях)*

have — с I/you/we/they. has — с he/she/it. По-русски: "У меня есть" = "I have". Краткой формы для "владения" нет.

#### Examples

| # | RU | EN |
|---|----|----|
| 37 | У меня есть собака. | I have a dog. |
| 38 | У неё есть сестра. | She has a sister. |
| 39 | У него есть машина. | He has a car. |
| 40 | У нас есть свободное время. | We have free time. |
| 41 | У них есть большой дом. | They have a big house. |
| 42 | У кошки острые когти. | The cat has sharp claws. |

---

#### Exercises

**Ex 37 · TableFill** *(ID: 3)*

Задание: вставь нужную форму have или has

| Подлежащее | Ответ |
|------------|-------|
| I ___ | have |
| you ___ | have |
| he ___ | has |
| she ___ | has |
| it ___ | has |
| we ___ | have |
| they ___ | have |

*Explanation (при ошибке):* have — с I/you/we/they. has — с he/she/it. Та же логика что у to be: единственное число третьего лица получает особую форму.

---

**Ex 38 · MultipleChoice · CHOICE** *(ID: 7)*

"My sister ___ a new phone."

- have
- **has** ✓
- is have

*Explanation (при ошибке):* My sister = she = 3-е лицо ед.ч. → has. "have" — для I/you/we/they. "is have" — такой формы не существует.

---

**Ex 39 · MultipleChoice · FORWARD_CHOICE** *(ID: 7)*

"У нас есть план."

- We has a plan.
- **We have a plan.** ✓
- We are have a plan.

*Explanation (при ошибке):* We → have. "We has" — ошибка: has только с he/she/it. "are have" — не существует.

---

**Ex 40 · TextInput** *(ID: 6)*

"He ___ two brothers."  
Правильный ответ: **has**

*Explanation (при ошибке):* He — 3-е лицо единственного числа → has.

---

**Ex 41 · TrueFalse** *(ID: 7)*

Задание: отметь верные и неверные предложения

| # | EN | RU | Верно? |
|---|----|----|--------|
| 1 | She have a bicycle. | У неё есть велосипед. | ✗ |
| 2 | They have a small apartment. | У них маленькая квартира. | ✓ |
| 3 | He have a meeting at 5. | У него встреча в 5. | ✗ |
| 4 | I have a headache. | У меня болит голова. | ✓ |
| 5 | My dog has three toys. | У моей собаки три игрушки. | ✓ |

*Explanation (при ошибке):* She/he → has (не have). They/I → have (не has). My dog = it = 3-е лицо → has.

---

**Ex 42 · WordArrangement** *(ID: 6)*

RU: У него есть синяя машина.

Правильное предложение: **He has a blue car.**

Слова (включая лишние):

| Слово | Перевод | Лишнее? |
|-------|---------|---------|
| He | Он | — |
| has | имеет / у него есть | — |
| a | артикль | — |
| blue | синяя | — |
| car | машина | — |
| have | имеет (для I/you/we/they) | ✗ дистрактор |
| She | Она | ✗ дистрактор |

*Explanation (при ошибке):* He → has (не have). A blue car = синяя машина. She/have — неверные формы для he.

---

#### AI Exercise

**ID:** basics_card7_ex1
**Title:** "Составь предложение с have/has"
**Input Mode:** FREE_WRITE
**Words Source:** NONE
**AI Config Profile:** EXERCISE_LIGHT
**User Instruction:** "AI даст местоимение и существительное. Составь из них предложение с правильной формой have или has."
**Prompt Template:** "Дай набор: местоимение + существительное (например: 'they / two cats'). Пользователь составляет предложение 'They have two cats.' Проверь форму have/has и объясни ошибку — особо отметь ловушку he/she/it → has."

#### Clarification Options

- Почему has, а не have — для he/she/it
- Разница "I have" и "у меня есть" по-русски
- Можно ли сказать "I've a cat"

---

### Card 8 · Отрицание: don't have / doesn't have

**ID:** 8 | **Order:** 2

#### Theory

Чтобы сказать "у меня нет" — используем **don't have** или **doesn't have**.

**Почему появляется do/does?**

В английском "have" — обычный глагол. У обычных глаголов есть правило: чтобы сделать отрицание, нужен вспомогательный глагол **do** или **does**. Само "have" прямо отрицать нельзя.

Сравни с to be: to be — особенный глагол, он отрицается напрямую: "She **is** at home" → "She **isn't** at home". Но have — обычный, поэтому нужен помощник:

- "She **has** a car." — *У неё есть машина.*
- "She **doesn't have** a car." — *У неё нет машины.*

Схема: **do/does + not + have** = don't have / doesn't have.

| Полная форма | Краткая | Перевод |
|---|---|---|
| I do not have | I don't have | у меня нет |
| you do not have | you don't have | у тебя нет |
| he does not have | he doesn't have | у него нет |
| she does not have | she doesn't have | у неё нет |
| it does not have | it doesn't have | у него/неё нет |
| we do not have | we don't have | у нас нет |
| they do not have | they don't have | у них нет |

**Главная ловушка:** когда добавляем **doesn't** (для he/she/it), сам глагол have НЕ получает -s. Отрицание уже несёт форму для 3-го лица: "She doesn't **have**" — не "she doesn't has". Ошибка "doesn't has" — очень распространённая.

**Почему так:** "doesn't" уже "отвечает" за 3-е лицо (он/она/оно). Если добавить ещё "-s" к have, получится двойное обозначение одного и того же — в английском такого не бывает. Запомни: doesn't = does + not, и does уже несёт всю нагрузку.

В разговоре всегда используй краткие формы: "I don't have", "she doesn't have". Полные (do not have, does not have) — для официального текста или для особого акцента: "I do **not** have time for this!" — *У меня реально нет на это времени!*

#### Summary *(кнопка «?» в упражнениях)*

don't have — с I/you/we/they. doesn't have — с he/she/it. После doesn't — всегда "have", не "has".

#### Examples

| # | RU | EN |
|---|----|----|
| 43 | У меня нет машины. | I don't have a car. |
| 44 | У неё нет братьев. | She doesn't have any brothers. |
| 45 | У него нет времени. | He doesn't have time. |
| 46 | У них нет домашних животных. | They don't have any pets. |
| 47 | У нас нет денег на поездку. | We don't have enough money for the trip. |

---

#### Exercises

**Ex 43 · MultipleChoice · CHOICE** *(ID: 8)*

"She ___ a car."

- don't have
- **doesn't have** ✓
- doesn't has

*Explanation (при ошибке):* She — 3-е лицо ед.ч. → doesn't have. "don't have" — для I/you/we/they. "doesn't has" — ошибка: после doesn't глагол have остаётся в базовой форме.

---

**Ex 44 · TextInput** *(ID: 7)*

"We ___ enough money for the trip."  
Правильный ответ: **don't have**  
Подсказка: don't have / doesn't have

*Explanation (при ошибке):* We → don't have.

---

**Ex 45 · MultipleChoice · FORWARD_CHOICE** *(ID: 8)*

"У него нет сестёр."

- He don't have sisters.
- **He doesn't have any sisters.** ✓
- He doesn't has sisters.

*Explanation (при ошибке):* He → doesn't have. "don't have" — только с I/you/we/they. "doesn't has" — ошибка: после doesn't используется базовая форма "have".

---

**Ex 46 · TrueFalse** *(ID: 8)*

Задание: отметь верные и неверные предложения

| # | EN | RU | Верно? |
|---|----|----|--------|
| 1 | She doesn't has a dog. | У неё нет собаки. | ✗ |
| 2 | They don't have a garden. | У них нет сада. | ✓ |
| 3 | He don't have a job. | У него нет работы. | ✗ |
| 4 | I don't have time today. | У меня нет времени сегодня. | ✓ |
| 5 | We doesn't have a car. | У нас нет машины. | ✗ |

*Explanation (при ошибке):* she/he/it → doesn't have (не "doesn't has", не "don't have"). I/you/we/they → don't have (не "doesn't have").

---

**Ex 47 · WordArrangement** *(ID: 7)*

RU: У неё нет домашних животных.

Правильное предложение: **She doesn't have any pets.**

Слова (включая лишние):

| Слово | Перевод | Лишнее? |
|-------|---------|---------|
| She | Она | — |
| doesn't | не (для he/she/it) | — |
| have | иметь | — |
| any | никаких | — |
| pets | домашних животных | — |
| don't | не (для I/you/we/they) | ✗ дистрактор |
| has | имеет (для he/she/it) | ✗ дистрактор |

*Explanation (при ошибке):* She → doesn't have (не "don't have", не "doesn't has"). Any pets = домашних животных. "Don't" — для I/you/we/they. "Has" — в отрицании с doesn't использовать нельзя.

---

**Ex 48 · MultipleChoice · REVERSE_CHOICE** *(ID: 4)*

Переведи на русский:  
"My brother doesn't have a bike."

- **У моего брата нет велосипеда.** ✓
- Мой брат не едет на велосипеде.
- Мой брат не любит велосипеды.

*Explanation (при ошибке):* doesn't have = нет (у него). My brother = у моего брата. A bike = велосипед.

---

#### AI Exercise

**ID:** basics_card8_ex1
**Title:** "Сделай отрицание с have/has"
**Input Mode:** FREE_WRITE
**Words Source:** NONE
**AI Config Profile:** EXERCISE_LIGHT
**User Instruction:** "AI даст утвердительное предложение с have/has. Перепиши его в отрицательной форме."
**Prompt Template:** "Дай утвердительное предложение с have/has (например: 'He has a new laptop.'). Пользователь пишет отрицательную форму. Проверь don't have / doesn't have и объясни ошибку — особо отметь что форма глагола возвращается к V1: 'doesn't have', не 'doesn't has'."

#### Clarification Options

- Почему "doesn't has" — ошибка
- Разница don't have и doesn't have
- Почему в отрицании do/does, а не просто not

---

### Card 9 · Вопрос: Do you have? / Does she have?

**ID:** 9 | **Order:** 3

#### Theory

Вопрос с have/has строится с помощью вспомогательного глагола **do** или **does**, который встаёт перед подлежащим.

- **do** — с I, you, we, they
- **does** — с he, she, it

Формула: **Do/Does + подлежащее + have + остальное + ?**

| Местоимение | Вопрос | Краткий ответ |
|---|---|---|
| I/you/we/they | Do you have...? | Yes, I do. / No, I don't. |
| he/she/it | Does she have...? | Yes, she does. / No, she doesn't. |

**Краткие ответы — как они устроены:**

"Yes, I do." — слово "do" здесь заменяет весь оборот "do have". Это удобно: не нужно повторять весь глагол.

- "Do you have a pen?" → "Yes, I do." *(= Yes, I have a pen)*
- "Does she have a job?" → "No, she doesn't." *(= No, she doesn't have a job)*
- "Do they have tickets?" → "Yes, they do." *(= Yes, they have tickets)*

**Развёрнутый ответ** тоже возможен — если хочешь добавить подробности:
- "Do you have a brother?" → "Yes, I do. His name is Alex." — *Да. Его зовут Алекс.*
- "Does he have a car?" → "No, he doesn't. He takes the bus." — *Нет. Он ездит на автобусе.*

**Сокращать do/does в кратком положительном ответе нельзя:** "Yes, I do." — правильно. Сокращение внутри предложения работает: "I don't have", "She doesn't have" — но не в конце краткого ответа.

**Акцентное "do":** иногда do используется для подчёркивания:
- "A: You don't have a car." — *У тебя нет машины.*
- "B: I **do** have a car!" — *У меня ЕСТЬ машина!* (акцент)

Здесь "do" звучит с ударением и означает "всё-таки есть, несмотря на то что ты говоришь".

И снова: после does в вопросе — "have", не "has". "Does she have?" — правильно. "Does she has?" — ошибка.

#### Summary *(кнопка «?» в упражнениях)*

Вопрос: Do/Does + подлежащее + have? Does — для he/she/it. После does — have (не has). Краткий ответ: Yes, I do. / No, she doesn't.

#### Examples

| # | RU | EN |
|---|----|----|
| 48 | У тебя есть брат? | Do you have a brother? |
| 49 | У неё есть машина? | Does she have a car? |
| 50 | У них есть дети? | Do they have children? |
| 51 | Да, есть. | Yes, I do. / Yes, she does. |
| 52 | Нет. | No, I don't. / No, she doesn't. |
| 53 | У твоего друга есть работа? | Does your friend have a job? |

---

#### Exercises

**Ex 49 · MultipleChoice · CHOICE** *(ID: 9)*

"___ she have a driving licence?"

- Do
- **Does** ✓
- Is

*Explanation (при ошибке):* She — 3-е лицо ед.ч. → Does. "Do" — для I/you/we/they. "Is" — это вопрос с to be, не с have.

---

**Ex 50 · TextInput** *(ID: 8)*

"___ your parents have a garden?"  
Правильный ответ: **Do**  
Подсказка: Do / Does

*Explanation (при ошибке):* Your parents = they → Do.

---

**Ex 51 · MultipleChoice · FORWARD_CHOICE** *(ID: 9)*

"У него есть кредитная карта?"

- Have he a credit card?
- Do he have a credit card?
- **Does he have a credit card?** ✓

*Explanation (при ошибке):* He — 3-е лицо → Does. "Have he" — неверный порядок слов, так не говорят. "Do he" — ошибка: с he только Does.

---

**Ex 52 · MultipleChoice · REVERSE_CHOICE** *(ID: 5)*

Переведи на русский:  
"Do they have a washing machine?"

- **У них есть стиральная машина?** ✓
- Они стирают?
- Они хотят стиральную машину?

*Explanation (при ошибке):* Do they have = у них есть? A washing machine = стиральная машина.

---

**Ex 53 · TrueFalse** *(ID: 9)*

Задание: отметь верные и неверные предложения

| # | EN | RU | Верно? |
|---|----|----|--------|
| 1 | Does he have a cat? | У него есть кошка? | ✓ |
| 2 | Do she have a bike? | У неё есть велосипед? | ✗ |
| 3 | Does they have time? | У них есть время? | ✗ |
| 4 | Do we have enough food? | У нас достаточно еды? | ✓ |
| 5 | Does she has a job? | У неё есть работа? | ✗ |

*Explanation (при ошибке):* she/he/it → Does. we/they/you → Do. После does → have (не has).

---

**Ex 54 · DialogRestore** *(ID: 4)*

A: "___ you have a moment?"  
B: "Yes, I do. What's up?"

Варианты для реплики A:
- **Do** ✓
- Does
- Have

*Explanation (при ошибке):* You → Do. "Does" — для he/she/it. "Have you" — устаревший формальный стиль, в обычной речи не используется.

---

#### AI Exercise

**ID:** basics_card9_ex1
**Title:** "Составь вопрос: Do/Does have?"
**Input Mode:** FREE_WRITE
**Words Source:** NONE
**AI Config Profile:** EXERCISE_LIGHT
**User Instruction:** "AI даст русский вопрос о владении и слова-подсказки. Составь вопрос на английском с правильным Do или Does."
**Prompt Template:** "Дай русский вопрос о владении (например: 'У твоей подруги есть машина?') и слова-подсказки для лексики (girlfriend, car, job, brother и т.п.). Пользователь составляет вопрос с Do/Does...have. Проверь форму вспомогательного глагола Do/Does и объясни ошибку."

#### Clarification Options

- Разница Do have и Does have
- Почему "Does she has?" — ошибка
- Краткий ответ "Yes, I do" — что за do в конце

---

### Words8r Sync · Глагол have / has

После завершения микротемы предлагаем добавить в Words8r (категория **"Grammar Basics"**):

| Слово | Переводы | Транскрипция |
|-------|----------|-------------|
| to have | иметь, обладать | [tuː hæv] |
| have | имею/имеешь/имеем/имеют (форма для I/you/we/they) | [hæv] |
| has | имеет (форма для he/she/it) | [hæz] |

---
---

## Microtopic 5 — Articles a / an · Артикли a / an

**ID:** 5 | **Order:** 5

---

### Card 10 · Что такое артикль. Когда использовать a / an

**ID:** 10 | **Order:** 1

#### Theory

В английском перед большинством существительных стоит маленькое слово — **артикль**. В русском его нет совсем, поэтому поначалу кажется, что это лишний элемент. Но без артикля предложение звучит странно или меняет смысл.

Всего артиклей три: **a**, **an** и **the**. Сначала разберём первые два — неопределённый артикль.

В английском артикль — это такое маленькое слово перед существительным, которое указывает: мы говорим о конкретном предмете или о каком-то неопределённом? В русском ничего подобного нет — мы угадываем смысл из контекста или интонации. В английском — специальное слово.

**A / An** — неопределённый артикль. Слово "a" исторически произошло от числительного "one" (один). Это и есть его суть: **один какой-то, неважно какой конкретно**.

**Три случая, когда нужен a/an:**

**1. Говоришь о чём-то впервые, и слушатель ещё не знает о чём речь:**
"I saw **a** dog on the street." — *Я видел (какую-то) собаку на улице.* Не конкретную, просто некую собаку.

**2. Называешь профессию, роль или национальный класс (но не национальность):**
- "She is **a** doctor." — *Она врач.* Обязательно с артиклем — нельзя "She is doctor".
- "He is **a** teacher." — *Он учитель.*
- "I want to be **a** pilot." — *Я хочу стать пилотом.*

**Осторожно:** с национальностями артикль не нужен. "She is Russian." — *Она русская.* Не "She is a Russian" (это звучит иначе — "одна русская"). Но "She is **a** Russian teacher." — *Она русская учительница* (teacher = профессия → артикль нужен).

**3. Говоришь "один из" — один экземпляр чего-то:**
- "Can I have **a** coffee?" — *Мне один кофе.* (один стакан/чашку кофе)
- "Give me **a** pen." — *Дай мне ручку.* (любую одну ручку)
- "There is **a** problem." — *Есть проблема.* (одна проблема, неизвестная заранее)

**A vs An — правило:**
- **a** — перед словами, начинающимися с согласного звука: **a** cat, **a** dog, **a** book.
- **an** — перед словами, начинающимися с гласного звука: **an** apple, **an** orange, **an** idea.

**Исчисляемые и неисчисляемые существительные:**

Некоторые вещи "считаются штуками" — одна кошка, две кошки, три кошки. Это исчисляемые. A/an — только с ними.

Другие вещи не считаются отдельными штуками — вода, молоко, воздух, музыка, информация. Нельзя сказать "одна вода" или "две воды" в смысле отдельных единиц. Это неисчисляемые. Перед ними a/an не ставится совсем:
- "Can I have **a** water?" — ошибка. → "Can I have some water?" — *Можно мне воды?*
- "a milk" — ошибка. → "some milk" / "a glass of milk" — *стакан молока*
- "an information" — ошибка. → "some information" / "a piece of information"

**Множественное число:** если слов несколько — артикль a/an исчезает. Появляется или ничего, или "some":
- "a cat" → "cats" или "some cats" — *кошки / какие-то кошки*
- "a book" → "books" или "some books" — *книги / несколько книг*

#### Summary *(кнопка «?» в упражнениях)*

a/an = "один какой-то". a — перед согласным звуком, an — перед гласным. Только с исчисляемыми существительными: "a cat", но не "a water".

#### Examples

| # | RU | EN |
|---|----|----|
| 54 | Я хочу яблоко. | I want an apple. |
| 55 | Она врач. | She is a doctor. |
| 56 | На улице есть кафе. | There is a café on the street. |
| 57 | Это хорошая идея. | That's a good idea. |
| 58 | У него есть собака. | He has a dog. |
| 59 | Дай мне апельсин. | Give me an orange. |

---

#### Exercises

**Ex 55 · MultipleChoice · CHOICE** *(ID: 10)*

"She is ___ engineer."

- a
- **an** ✓
- the

*Explanation (при ошибке):* engineer начинается с гласного звука "э" → an. "a" — перед согласным. "the" — определённый артикль, здесь не нужен.

---

**Ex 56 · MultipleChoice · CHOICE** *(ID: 11)*

"I have ___ idea."

- a
- **an** ✓
- —

*Explanation (при ошибке):* idea начинается с гласного звука "ай" → an.

---

**Ex 57 · MultipleChoice · FORWARD_CHOICE** *(ID: 10)*

"Я вижу (какую-то) собаку."

- I see dog.
- I see the dog.
- **I see a dog.** ✓

*Explanation (при ошибке):* Собака упоминается впервые, неконкретная → a dog. Без артикля ("see dog") — ошибка. "The" — только если это конкретная собака, которую оба уже знают.

---

**Ex 58 · TextInput** *(ID: 9)*

"He is ___ artist."  
Правильный ответ: **an**  
Подсказка: a / an

*Explanation (при ошибке):* artist начинается с гласного звука "а" → an.

---

**Ex 59 · TrueFalse** *(ID: 10)*

Задание: отметь верные и неверные предложения

| # | EN | RU | Верно? |
|---|----|----|--------|
| 1 | She has a orange. | У неё есть апельсин. | ✗ |
| 2 | He is an engineer. | Он инженер. | ✓ |
| 3 | I want a apple. | Я хочу яблоко. | ✗ |
| 4 | There is a cat on the sofa. | На диване есть кошка. | ✓ |
| 5 | She is an nurse. | Она медсестра. | ✗ |

*Explanation (при ошибке):* orange, apple — начинаются с гласного звука → an. nurse — начинается с согласного "н" → a nurse. engineer — гласный звук "э" → an engineer.

---

**Ex 60 · WordArrangement** *(ID: 8)*

RU: У него есть новый телефон.

Правильное предложение: **He has a new phone.**

Слова (включая лишние):

| Слово | Перевод | Лишнее? |
|-------|---------|---------|
| He | Он | — |
| has | у него есть | — |
| a | артикль | — |
| new | новый | — |
| phone | телефон | — |
| an | артикль (перед гласным) | ✗ дистрактор |
| the | определённый артикль | ✗ дистрактор |

*Explanation (при ошибке):* phone начинается с согласного звука "ф" → a phone. "an" — для гласных звуков. "the" — определённый артикль (здесь не нужен).

---

#### AI Exercise

**ID:** basics_card10_ex1
**Title:** "a, an или прочерк?"
**Input Mode:** FILL_BLANKS
**Words Source:** NONE
**AI Config Profile:** EXERCISE_LIGHT
**User Instruction:** "AI даст русское слово и его английский перевод. Выбери правильный артикль: a, an или — (без артикля)."
**Prompt Template:** "Дай 2–3 пары: русское слово + английское существительное (включая несчётные: water, milk, music). Пользователь выбирает a / an / —. Проверь выбор. Особое внимание на несчётные → прочерк. Объясни ошибку с указанием почему именно этот вариант."
#### Clarification Options

- Когда артикль вообще не нужен
- Почему нельзя сказать "a water"
- Разница: a dog и the dog

---

### Card 11 · a vs an — ловушки: звук важнее буквы

**ID:** 11 | **Order:** 2

#### Theory

Правило "an перед гласным звуком" работает безотказно — но есть слова, где буква и звук расходятся. Это главный источник ошибок даже у людей с хорошим уровнем.

**Вспомни суть:** артикль a/an стоит перед первым словом, которое ты произносишь вслух. Твой рот открывается для гласного? Нужен an. Начинаешь с согласного? Нужен a. Всё просто — пока не встречаешь слова-ловушки.

**Слова-ловушки группа 1 — буква гласная, звук согласный:**

Слова на U и E, которые произносятся через "ю" или "й":

| Слово | Звучание | Артикль | Пример |
|---|---|---|---|
| university | "юнивёрсити" | **a** | a university |
| union | "юниэн" | **a** | a union |
| European | "юрэпиэн" | **a** | a European country |
| useful | "юсфул" | **a** | a useful tool |
| unit | "юнит" | **a** | a unit |
| unique | "юник" | **a** | a unique opportunity |

*a unique opportunity — уникальная возможность*
*a European city — европейский город*

**Слова-ловушки группа 2 — буква H не произносится:**

| Слово | Звучание | Артикль | Пример |
|---|---|---|---|
| hour | "аур" | **an** | an hour |
| honest | "онист" | **an** | an honest answer |
| heir | "эр" | **an** | an heir |
| honour | "онэ" | **an** | an honour |

*an hour — час (H молчит, слышится "аур")*
*an honest person — честный человек*

**Осторожно:** не все слова на H молчат! "Hotel", "history", "happy" — H произносится: **a** hotel, **a** history lesson, **a** happy day.

**Артикль с прилагательными:**

Если перед существительным стоит прилагательное — артикль согласуется со звуком прилагательного, не существительного:

- "**a** big apple" — big начинается с "б" → a *(хотя apple начинается с гласного)*
- "**an** old apple" — old начинается с "о" → an
- "**an** interesting book" — interesting начинается с "и" → an *(хотя book согласный)*
- "**a** useful idea" — useful начинается с "ю" → a *(хотя idea гласный)*

**Числа:** артикль согласуется с тем, как число звучит при прочтении:
- "**an** 8-hour day" — eight начинается с "э" → an
- "**a** 5-minute break" — five начинается с "ф" → a

**Слова-ловушки группа 3 — аббревиатуры:**

Буква по алфавиту может начинаться с гласного звука:
- "**an** MBA degree" — M звучит как "эм" → an
- "**an** MP" — M звучит как "эм" → an *(MP = Member of Parliament)*
- "**a** PhD" — P звучит как "пи" → a

**Слова-ловушки:**

| Слово | Артикль | Почему |
|---|---|---|
| university | **a** university | звучит как "ю..." — согласный |
| union | **a** union | звучит как "ю..." — согласный |
| European | **a** European | звучит как "юро..." — согласный |
| useful | **a** useful tool | звучит как "юс..." — согласный |
| hour | **an** hour | h не произносится, слышится "аур" — гласный |
| honest | **an** honest | h не произносится, слышится "они..." — гласный |
| heir | **an** heir | h не произносится, слышится "эр" — гласный |

**Правило с прилагательными:** артикль согласуется со звуком первого слова, которое слышишь. Если перед существительным стоит прилагательное — артикль зависит от звука этого прилагательного.

- "**a** big apple" — big начинается с "б" → a
- "**an** old apple" — old начинается с "о" → an
- "**an** interesting book" — interesting начинается с "и" → an
- "**a** useful tool" — useful начинается с "ю" → a

#### Summary *(кнопка «?» в упражнениях)*

an — перед гласным звуком, не буквой. university, union, European → a (звук "ю"). hour, honest → an (h молчит). С прилагательным: артикль согласуется со звуком прилагательного.

#### Examples

| # | RU | EN |
|---|----|----|
| 60 | Это хороший университет. | It's a good university. |
| 61 | У меня честный ответ. | I have an honest answer. |
| 62 | Там большое яблоко. | There is a big apple there. |
| 63 | Это интересная книга. | It's an interesting book. |
| 64 | Подожди час. | Wait for an hour. |
| 65 | Это полезный инструмент. | It's a useful tool. |

---

#### Exercises

**Ex 61 · MultipleChoice · CHOICE** *(ID: 12)*

"She studies at ___ university."

- an
- **a** ✓
- the

*Explanation (при ошибке):* university звучит как "юнивёрсити" — первый звук "ю" (согласный) → a university. Буква U не означает гласный звук здесь.

---

**Ex 62 · MultipleChoice · CHOICE** *(ID: 13)*

"I waited for ___ hour."

- a
- **an** ✓
- —

*Explanation (при ошибке):* hour — h не произносится, первый звук "а" (гласный) → an hour.

---

**Ex 63 · MultipleChoice · FORWARD_CHOICE** *(ID: 11)*

"Это интересный фильм."

- It's a interesting film.
- **It's an interesting film.** ✓
- It's the interesting film.

*Explanation (при ошибке):* interesting начинается с гласного звука "и" → an. Артикль согласуется с первым словом (an interesting), не с "film".

---

**Ex 64 · MultipleChoice · CHOICE** *(ID: 14)*

"He is ___ honest person."

- a
- **an** ✓
- the

*Explanation (при ошибке):* honest — h не произносится, первый звук "о" (гласный) → an honest.

---

**Ex 65 · TrueFalse** *(ID: 11)*

Задание: отметь верные и неверные предложения

| # | EN | RU | Верно? |
|---|----|----|--------|
| 1 | She has an useful idea. | У неё есть полезная идея. | ✗ |
| 2 | It's a European country. | Это европейская страна. | ✓ |
| 3 | He is an honest man. | Он честный человек. | ✓ |
| 4 | I need an university degree. | Мне нужна университетская степень. | ✗ |
| 5 | We waited for an hour. | Мы ждали час. | ✓ |

*Explanation (при ошибке):* useful звучит как "юсфул" → a useful. university звучит как "юни..." → a university. European звучит как "юро..." → a European. honest — h молчит → an honest. hour — h молчит → an hour.

---

**Ex 66 · WordArrangement** *(ID: 9)*

RU: Это европейский город.

Правильное предложение: **It's a European city.**

Слова (включая лишние):

| Слово | Перевод | Лишнее? |
|-------|---------|---------|
| It's | Это | — |
| a | артикль | — |
| European | европейский | — |
| city | город | — |
| an | артикль (перед гласным звуком) | ✗ дистрактор |
| the | определённый артикль | ✗ дистрактор |

*Explanation (при ошибке):* European звучит как "юро..." — первый звук "ю" (согласный) → a. "an" — перед настоящим гласным звуком.

---

#### AI Exercise

**ID:** basics_card11_ex1
**Title:** "a или an перед прилагательным?"
**Input Mode:** FILL_BLANKS
**Words Source:** NONE
**AI Config Profile:** EXERCISE_LIGHT
**User Instruction:** "AI даст предложение с пропуском перед прилагательным или существительным. Вставь a или an."
**Prompt Template:** "Дай предложение с пропуском перед прилагательным или существительным-ловушкой (например: '___ old man', '___ European university', '___ honest person'). Пользователь вписывает a/an. Проверь и объясни: правило зависит от ЗВУКА, а не буквы — an old [æn], a university [ə] несмотря на гласную."

#### Clarification Options

- Почему "a European" если начинается на E
- Почему "an hour" если начинается на H
- Как определить — гласный звук или согласный

---

### Words8r Sync · Артикли a / an

Артикли не добавляются в Words8r — у них нет перевода, только теория. Само понятие "a/an" закрепляется через упражнения.

---
---

## Microtopic 6 — Article the · Артикль the

**ID:** 6 | **Order:** 6

---

### Card 12 · Когда использовать the

**ID:** 12 | **Order:** 1

#### Theory

**The** — **определённый артикль**. Если "a/an" значит "один какой-то, неважно какой", то "the" значит "вот этот конкретный, тот самый". И ты, и собеседник знаете, о каком именно предмете речь.

**Когда использовать the:**

**1. Предмет уже упоминался — теперь он конкретный:**  
"I saw **a** dog on the street." — *Я увидел собаку на улице.* ← первый раз: a dog (какая-то собака)  
"**The** dog was huge." — *Собака была огромной.* ← второй раз: the dog (та самая, уже известная)

Логика: первый раз — "a", потому что собака ещё незнакома. Второй раз — "the", потому что мы уже знаем о какой собаке речь.

**2. Когда из контекста ясно о каком предмете речь:**  
"Close **the** window, please." — *Закрой окно, пожалуйста.* ← в комнате одно окно, ясно какое  
"Can you pass **the** salt?" — *Передай соль.* ← на столе соль стоит, понятно о какой  
"I went to **the** doctor." — *Я пошёл к врачу.* ← к своему врачу, не к случайному

**3. Единственный в своём роде:**  
"**The** sun is bright today." — Солнце одно.  
"**The** moon is full." — Луна одна.  
"**The** Eiffel Tower is in Paris." — Эйфелева башня одна.

**4. Реки, океаны, моря, горные цепи:**  
"**The** Amazon", "**the** Nile", "**the** Pacific Ocean", "**the** Alps" — всегда с the.

**5. Страны с "the" в официальном названии:**  
"**The** UK", "**the** USA", "**the** Netherlands" — с the. Обычные страны — без: "Russia", "France", "Japan".

**Чего нет в русском:** в русском "тот самый" можно выразить интонацией или указательным словом "эта/та". В английском для этого есть the — и предложение без него прозвучит как будто это впервые упоминается или ничего конкретного.

#### Summary *(кнопка «?» в упражнениях)*

the = "тот самый, конкретный". Используй: когда уже упомянуто, когда ясно из контекста, когда единственный в своём роде. Реки, океаны, моря — всегда the.

#### Examples

| # | RU | EN |
|---|----|----|
| 66 | Закрой окно, пожалуйста. | Close the window, please. |
| 67 | Я видел кошку. Кошка была чёрной. | I saw a cat. The cat was black. |
| 68 | Сегодня солнце яркое. | The sun is bright today. |
| 69 | Передай соль. | Can you pass the salt? |
| 70 | Амазонка — самая длинная река. | The Amazon is the longest river. |
| 71 | Она живёт в Великобритании. | She lives in the UK. |

---

#### Exercises

**Ex 67 · MultipleChoice · CHOICE** *(ID: 15)*

RU: Я купил книгу вчера. ___ книга очень интересная.  
"I bought a book yesterday. ___ book is really interesting."

- A
- An
- **The** ✓

*Explanation (при ошибке):* Первый раз — a book (какая-то книга). Второй раз та же книга, уже известная → The book.

---

**Ex 68 · MultipleChoice · CHOICE** *(ID: 16)*

RU: Солнце встаёт на востоке.  
"___ sun rises in the east."

- A
- An
- **The** ✓

*Explanation (при ошибке):* Солнце единственное в своём роде → the. "A sun" звучит как будто солнц несколько.

---

**Ex 69 · MultipleChoice · FORWARD_CHOICE** *(ID: 12)*

"Передай, пожалуйста, соль."

- Pass a salt, please.
- **Pass the salt, please.** ✓
- Pass salt, please.

*Explanation (при ошибке):* Соль конкретная — та, что на столе. Из контекста ясно о какой → the. "A salt" — ошибка: соль неисчисляемая, a/an с ней нельзя. "Pass salt" — звучит незавершённо.

---

**Ex 70 · TrueFalse** *(ID: 12)*

Задание: отметь верные и неверные предложения

| # | EN | RU | Верно? |
|---|----|----|--------|
| 1 | I saw a dog. The dog was small. | Я видел собаку. Собака была маленькой. | ✓ |
| 2 | The moon is shining. | Луна светит. | ✓ |
| 3 | Can you close a window? | Можешь закрыть окно? | ✗ |
| 4 | She swims in a Pacific Ocean. | Она плавает в Тихом океане. | ✗ |
| 5 | He works at the bank. | Он работает в банке. | ✓ |

*Explanation (при ошибке):* "a window" — ясно о конкретном окне → the window. Океан — один, по правилу → the Pacific Ocean. Первое упоминание: a dog → второе: the dog. Луна единственная → the moon.

---

**Ex 71 · MultipleChoice · REVERSE_CHOICE** *(ID: 6)*

"She lives near the Nile."

- Она живёт возле какой-то реки.
- **Она живёт возле Нила.** ✓
- Она любит реки.

*Explanation (при ошибке):* the Nile = Нил (конкретная река, единственная). Реки и водоёмы всегда с the.

---

**Ex 72 · WordArrangement** *(ID: 10)*

RU: Закрой, пожалуйста, дверь.

Правильное предложение: **Close the door, please.**

Слова (включая лишние):

| Слово | Перевод | Лишнее? |
|-------|---------|---------|
| Close | Закрой | — |
| the | определённый артикль | — |
| door | дверь | — |
| please | пожалуйста | — |
| a | неопределённый артикль | ✗ дистрактор |
| an | артикль (перед гласным) | ✗ дистрактор |
| Open | Открой | ✗ дистрактор |

*Explanation (при ошибке):* "The door" — конкретная дверь в этой комнате, ясно о какой → the. "a door" / "an door" — неверно: говорим о конкретной двери.

---

#### AI Exercise

**ID:** basics_card12_ex1
**Title:** "Вставь все артикли"
**Input Mode:** FILL_BLANKS
**Words Source:** NONE
**AI Config Profile:** EXERCISE_LIGHT
**User Instruction:** "AI даст предложение с 2–3 пропусками на месте артиклей и его русский перевод. Вставь a / an / the / — в каждый пропуск."
**Prompt Template:** "Сгенерируй предложение с 2–3 пропусками на разные типы артиклей (a, an, the, —) и его русский перевод. Пропуски обозначены '___'. Пользователь заполняет все пропуски и отправляет целиком. Проверь каждый артикль отдельно и объясни каждую ошибку."

Пример задания:
> ___ sun rises in ___ east. *(Солнце встаёт на востоке.)*
> Ответ: `the` / `the`

#### Clarification Options

- Разница: a book vs the book
- Почему "the sun", а не "a sun"
- Когда при первом упоминании "a" меняется на "the"

---

### Words8r Sync · Артикль the

Артикли не добавляются в Words8r — у них нет перевода, только теория.

---
---

# БЛОК 3 · Микротемы 7–9

---

## Microtopic 7 — Possessive Pronouns · Притяжательные местоимения

**ID:** 7 | **Order:** 7

---

### Card 13 · my, your, his, her, its, our, their

**ID:** 13 | **Order:** 1

#### Theory

Когда хочешь сказать "моя сумка", "его машина", "их дом" — в английском для этого есть специальные слова. Они называются притяжательными местоимениями и стоят **перед существительным**, показывая кому оно принадлежит.

Принцип простой: смотришь на "владельца" → берёшь его личное местоимение → заменяешь на притяжательное:

| Кто владеет | Личное | Притяжательное | Пример |
|-------------|--------|----------------|--------|
| я | I | **my** | my bag — моя сумка |
| ты / вы | you | **your** | your car — твоя машина |
| он | he | **his** | his phone — его телефон |
| она | she | **her** | her coat — её пальто |
| оно / предмет | it | **its** | its name — его название |
| мы | we | **our** | our house — наш дом |
| они | they | **their** | their dog — их собака |

Притяжательное всегда стоит **перед** существительным. Нельзя сказать "bag my" — только "my bag".

**Ловушка 1: his и her.** В русском "его" и "её" — противоположности, и в английском тоже: `his` = мужской, `her` = женский. Если перепутать — предложение меняет смысл.

"Иван купил машину. **His** car is red." (Его машина красная.)

"Анна купила машину. **Her** car is blue." (Её машина синяя.)

Если сказать "Anna's car is **his**" — это будет значить, что машина Анны принадлежит какому-то мужчине. Совсем другой смысл.

**Ловушка 2: its vs. it's.** Это разные слова, хотя звучат одинаково!
- `its` (без апострофа) — притяжательное: "The cat licks **its** paws." (Кот лижет свои лапы.)
- `it's` (с апострофом) = it is: "**It's** cold today." (Сегодня холодно.)

Простая проверка: попробуй заменить на "it is". Звучит нормально? Тогда `it's`. Нет? Тогда `its`.

**Ловушка 3: their, there, they're.** Три слова — одно произношение [ðer], три разных значения:
- `their` — притяжательное: "**their** house" (их дом)
- `there` — место: "over **there**" (вон там)
- `they're` — they are: "**They're** at home." (Они дома.)

Пока просто запомни: если говоришь о владении — пишем `their`.

#### Summary *(кнопка «?» в упражнениях)*

my/your/his/her/its/our/their — стоят перед существительным, показывают кому оно принадлежит. his = мужской, her = женский. its (без апострофа) = принадлежность предмета; it's = it is.

#### Examples

| # | RU | EN |
|---|----|----|
| 72 | Это моя сумка. | This is my bag. |
| 73 | Где твой телефон? | Where is your phone? |
| 74 | Его машина стоит у дома. | His car is near the house. |
| 75 | Её сестра живёт в Москве. | Her sister lives in Moscow. |
| 76 | Наш учитель очень строгий. | Our teacher is very strict. |
| 77 | Их дети учатся в школе. | Their children are at school. |
| 78 | Кот ест свою еду. | The cat is eating its food. |

---

#### Exercises

**Ex 73 · TableFill** *(ID: 4)*

Задание: вспомни и запиши притяжательное местоимение для каждого личного

| Личное местоимение | Притяжательное |
|--------------------|----------------|
| I | my |
| you | your |
| he | his |
| she | her |
| it | its |
| we | our |
| they | their |

*Explanation (при ошибке):* I→my, you→your, he→his, she→her, it→its, we→our, they→their. Притяжательное показывает кому принадлежит предмет.

---

**Ex 74 · MultipleChoice · CHOICE** *(ID: 17)*

"Егор забыл ___ телефон дома."

- **his** ✓
- her
- their

*Explanation (при ошибке):* Егор — мужчина, значит he → his. "her" — для женщин (she). "their" — для нескольких владельцев (they).

---

**Ex 75 · Matching** *(ID: 1)*

Задание: соедини владельца с правильным притяжательным в предложении

| Ситуация            | Фраза |
|---------------------|-------|
| Анна и её книга     | her book |
| Кирилл и его машина | his car |
| Я и мой дом         | my house |
| Мы и наша собака    | our dog |

*Explanation (при ошибке):* Анна — she → her. Кирилл — he → his. Я — I → my. Мы — we → our.

---

**Ex 76 · TrueFalse** *(ID: 13)*

| # | EN | RU | Верно? |
|---|----|----|--------|
| 1 | Katya lost her keys. | Катя потеряла свои ключи. | ✓ |
| 2 | Roma likes his job. | Роме нравится его работа. | ✓ |
| 3 | Ivan has a car. Her car is new. | У Ивана есть машина. Машина новая. | ✗ |
| 4 | We love our city. | Мы любим наш город. | ✓ |
| 5 | The dog is eating it's food. | Собака ест свою еду. | ✗ |

*Explanation (при ошибке):* Предл. 3: Ivan — мужчина → his, не her. "Ivan has a car. His car is new." Предл. 5: притяжательное пишется без апострофа — `its`. `it's` = it is, это другое слово.

---

**Ex 77 · WordArrangement** *(ID: 11)*

RU: Их кошка спит на диване.

Правильное предложение: **Their cat is sleeping on the sofa.**

| Слово | Перевод | Лишнее? |
|-------|---------|---------|
| Their | Их | — |
| cat | кошка | — |
| is | (глагол to be) | — |
| sleeping | спит (сейчас) | — |
| on | на | — |
| the | определённый артикль | — |
| sofa | диван | — |
| His | Его (муж.) | ✗ дистрактор |
| Her | Её (жен.) | ✗ дистрактор |

*Explanation (при ошибке):* "Their" — для нескольких владельцев (они). His/Her — для одного конкретного человека.

---

#### AI Exercise

**ID:** basics_card13_ex1
**Title:** "Вставь притяжательное местоимение"
**Input Mode:** FILL_BLANKS
**Words Source:** NONE
**AI Config Profile:** EXERCISE_LIGHT
**User Instruction:** "AI даст предложения с 2–3 пропусками и русский перевод. Вставь нужные my / your / his / her / its / our / their."
**Prompt Template:** "Дай предложения с 2–3 пропусками на притяжательные местоимения и русский перевод (например: '___ brother is a doctor. ___ name is Alex.' → 'Её брат — врач. Его зовут Алекс.'). Пользователь заполняет пропуски. Проверь выбор и объясни ошибку."

Пользователь вписывает `Her` / `His` в поля. Заполненное предложение отправляется AI — AI проверяет и объясняет ошибку если есть.

#### Clarification Options

- Разница между his и her
- Почему its без апострофа
- Как не путать their / there / they're

---

### Card 14 · mine, yours, his, hers, ours, theirs

**ID:** 14 | **Order:** 2

#### Theory

Представь: вы с другом стоите у двух одинаковых сумок и спорите, чья какая. Ты говоришь: "Это моя сумка" — и берёшь её. Потом тебя перебивают вопросом, и ты просто говоришь: "Моя!" — уже без слова "сумка", потому что оба понимают о чём речь.

Вот здесь и нужна **самостоятельная форма** — притяжательное местоимение без существительного после него.

| Зависимая (перед существительным) | Самостоятельная (без существительного) |
|-----------------------------------|----------------------------------------|
| my bag — моя сумка | That bag is **mine** — Та сумка моя |
| your coat — твоё пальто | That coat is **yours** — То пальто твоё |
| his phone — его телефон | That phone is **his** — не меняется! |
| her key — её ключ | That key is **hers** — Тот ключ её |
| our car — наша машина | That car is **ours** — Та машина наша |
| their house — их дом | That house is **theirs** — Тот дом их |

**Главное правило:** после глагола `to be` — всегда самостоятельная форма.

❌ "This bag is my." — так нельзя
✓ "This bag is mine." — правильно

❌ "Is this jacket your?" — так нельзя
✓ "Is this jacket yours?" — правильно

**Важно: `his` не меняется.** В зависимой форме "his car", и в самостоятельной "That car is his." Не добавляй -s!

**Самый частый контекст:** вопрос "Чьё это?" — "Whose is this?"

"Whose pen is this?" — "It's **mine**." / "It's **hers**." / "It's not **mine**."
(Чья это ручка? — Моя. / Её. / Не моя.)

#### Summary *(кнопка «?» в упражнениях)*

mine/yours/his/hers/ours/theirs — форма без существительного. Используй после глагола to be: "This is mine." his не меняется в обеих формах. Чьё это? → "Whose is this?"

#### Examples

| # | RU | EN |
|---|----|----|
| 79 | Эта машина моя. | This car is mine. |
| 80 | Чья это куртка? Твоя? | Whose jacket is this? Is it yours? |
| 81 | Этот телефон его, не её. | This phone is his, not hers. |
| 82 | Эта идея наша. | This idea is ours. |
| 83 | Тот дом их. | That house is theirs. |

---

#### Exercises

**Ex 78 · MultipleChoice · CHOICE** *(ID: 18)*

"Whose bag is this?" — "It's ___."

- my
- **mine** ✓
- me

*Explanation (при ошибке):* После "It's" нет существительного → нужна самостоятельная форма "mine". "my bag" — с существительным. "me" — это личное местоимение объектного падежа, здесь не подходит.

---

**Ex 79 · ErrorCorrection** *(ID: 1)*

В предложении есть ошибка. Выбери правильный вариант:

"Don't take that pen — it's my."

- Don't take that pen — it's me.
- **Don't take that pen — it's mine.** ✓
- Don't take that pen — it's my pen.

*Explanation (при ошибке):* После "it's" (= it is) существительного нет → самостоятельная форма "mine". "my" всегда стоит перед существительным: "my pen". Без существительного — только "mine".

---

**Ex 80 · ConstructionMeaning** *(ID: 1)*

Выбери правильный перевод:

"That laptop is hers, not yours."

- **Тот ноутбук её, а не твой.** ✓
- Тот ноутбук ваш, а не её.
- Это её ноутбук, а не твой ноутбук.
- Тот ноутбук его, а не твой.

*Explanation (при ошибке):* `hers` = её (женщина-владелец, без существительного), `yours` = твой/ваш (без существительного). "That laptop is hers" = ноутбук принадлежит ей. Не путай `hers` (её) с `his` (его).

---

**Ex 81 · WordArrangement** *(ID: 12)*

RU: Чья это сумка? Моя.

Правильные предложения: **Whose bag is this? It's mine.** /я думаю, что в таких заданиях не обязательно давать перевод this It's моё / моя / мой (самостоят.) моя (зависимая форма), ибо мы прошли эти слова в прошлой теме, да и можно же просто нажать на слово и задержать палец и тогда откроется перевод, ибо у нас есть аткая система. Примени эту инфу ко всем заданиям подобного рода, что были до и будут после, мол не надо давать сразу перевод того, что прошел пользователь в предыдущих темах/

| Слово | Перевод | Лишнее? |
|-------|---------|---------|
| Whose | Чей / Чья / Чьё | — |
| bag | сумка | — |
| is | (глагол to be) | — |
| this | это / этот / эта | — |
| It's | Это (= It is) | — |
| mine | моё / моя / мой (самостоят.) | — |
| my | моя (зависимая форма) | ✗ дистрактор |
| That's | То (= That is) | ✗ дистрактор |

*Explanation (при ошибке):* "It's mine" — без существительного → самостоятельная форма. "It's my" — неверно, "my" требует существительного после себя: "my bag".

---

#### AI Exercise

**ID:** basics_card14_ex1
**Title:** "Мой или mine? Самостоятельные формы"
**Input Mode:** FILL_BLANKS
**Words Source:** NONE
**AI Config Profile:** EXERCISE_LIGHT
**User Instruction:** "AI даст предложение с пропуском и русский перевод. Вставь правильную самостоятельную форму: mine / yours / his / hers / ours / theirs."
**Prompt Template:** "Дай предложение с пропуском на самостоятельное притяжательное местоимение и русский перевод (например: 'That jacket is ___, not ___.' → 'Та куртка её, а не моя.'). Пользователь вписывает mine/yours/his/hers/ours/theirs. Проверь и объясни ошибку — напомни что эти формы стоят БЕЗ существительного после."

Пользователь вписывает `hers` / `mine`. Заполненное предложение отправляется AI — AI проверяет форму и объясняет ошибку если есть.

#### Clarification Options

- Когда mine, а когда my
- Почему his не меняется
- Как спросить "чьё это?"

---

### Card 19 · Притяжательный 's — Lena's cat, Igor's phone

**ID:** 19 | **Order:** 3

#### Theory

Ты уже знаешь как показать владение через местоимение: "my cat", "her bag", "his car". Но что если владелец — конкретный человек по имени или существительное? Для этого в английском есть специальный знак — апостроф с буквой s: **'s**

Формула: **[владелец] + 's + [то чем владеет]**

- Lena**'s** dog → собака Лены
- my brother**'s** car → машина моего брата
- the teacher**'s** book → книга учителя
- the dog**'s** tail → хвост собаки

**Порядок слов — противоположный русскому.** Это главная ловушка.

По-русски: "собака **Лены**" — сначала предмет, потом владелец.
По-английски: "**Lena's** dog" — сначала владелец, потом предмет.

Желание написать "dog of Lena" понятно — это калька с русского. Но правильно только "Lena's dog".

**Когда 's, а когда of?**

Два способа выразить принадлежность, и каждый на своём месте:

| Ситуация | Способ | Пример |
|----------|--------|--------|
| Владелец — человек или животное | **'s** | the doctor's office, the cat's paws |
| Владелец — предмет или место | **of** | the name of the city, the end of the film |

Грубое правило: живой владелец → 's. Неодушевлённый → of. В разговорной речи 's используют шире — "the city's name" тоже правильно, но "the name of the city" звучит нейтральнее.

**Имена на -s — James's или James'?**

Если имя заканчивается на -s (Denis, James, Boris), оба варианта правильные:
- Denis**'s** car ИЛИ Denis**'** car

В повседневной речи чаще пишут Denis's.

**Ловушка: 's не всегда означает владение.**

В английском апостроф + s используется ещё и как сокращение:
- "Katya**'s** tired." = "Katya **is** tired." — Катя устала. (сокращение is)
- "The cat**'s** eaten." = "The cat **has** eaten." — Кот поел. (сокращение has)
- "Katya**'s** cat is big." — кошка Кати. (притяжательный 's)

Как отличить: после притяжательного 's идёт **существительное** ("Katya's **cat**"). После сокращения is/has идёт **прилагательное или глагол** ("Katya's **tired**").

#### Summary *(кнопка «?» в упражнениях)*

owner + 's + thing: "Lena's dog" = собака Лены. Порядок слов обратный русскому — сначала владелец. Для людей/животных → 's, для предметов/мест → of (the name of the city). 's = is/has когда после него прилагательное или глагол.

#### Examples

| # | RU | EN |
|---|----|----|
| 109 | Это кошка Кирилла. | This is Kirill's cat. |
| 110 | Машина моего брата синяя. | My brother's car is blue. |
| 111 | Книга учителя на столе. | The teacher's book is on the table. |
| 112 | Как называется этот город? | What is the name of this city? |
| 113 | Хвост собаки очень длинный. | The dog's tail is very long. |
| 114 | Это офис Дениса. | This is Denis's office. |

---

#### Exercises

**Ex 102 · Transformation** *(ID: 1)*

Задание: перепиши с использованием 's вместо of

1. "the phone of Igor" → **"Igor's phone"**
2. "the car of my sister" → **"my sister's car"**
3. "the room of the doctor" → **"the doctor's room"**

*Explanation (при ошибке):* Владелец — человек → используем 's. Порядок: сначала владелец, потом предмет. "Igor's phone" — не "phone of Igor".

---

**Ex 103 · MultipleChoice · CHOICE** *(ID: 19)*

"What is ___ this film?" (Как называется этот фильм?)

- film's name of
- **the name of** ✓
- the name's

*Explanation (при ошибке):* "Фильм" — неодушевлённый предмет → используем of: "the name of this film". 's — для людей и животных. "the name's" — грамматически невозможно в этом контексте.

---

**Ex 104 · ErrorCorrection** *(ID: 2)*

В предложении есть ошибка. Выбери правильный вариант:

"This is cat Sonya's."

- cat Sonya's ✗
- **Sonya's cat** ✓
- cat's Sonya

*Explanation (при ошибке):* Владелец ('s) всегда стоит ПЕРЕД тем, чем владеет. "Sonya's cat" — правильный порядок. "cat Sonya's" — калька с русского "кошка Сони", в английском так нельзя.

---

**Ex 105 · TrueFalse** *(ID: 14)*

| # | EN | RU | Верно? |
|---|----|----|--------|
| 1 | Artyom's bag is on the chair. | Сумка Артёма на стуле. | ✓ |
| 2 | The end of the film was great. | Конец фильма был отличным. | ✓ |
| 3 | The bag of Artyom is blue. | Сумка Артёма синяя. | ✗ |
| 4 | This is Roma's. | Это вещь Ромы. | ✓ |
| 5 | Denis' car | Машина Дениса. | ✓ |

*Explanation (при ошибке):* Предл. 3: для людей используем 's, не of → "Artyom's bag is blue". Предл. 5: для имён на -s допустимы оба написания — "Denis' car" и "Denis's car".

---

**Ex 106 · WordArrangement** *(ID: 13)*

Ситуация: ты увидел красивую машину и хочешь сказать, что это машина твоего друга Глеба.

Правильное предложение: **This is Gleb's car.**

| Слово | Перевод | Лишнее? |
|-------|---------|---------|
| This | — | — |
| is | — | — |
| Gleb's | — | — |
| car | машина | — |
| of Gleb | — | ✗ дистрактор |
| car's Gleb | — | ✗ дистрактор |

*Explanation (при ошибке):* Владелец — человек → 's. Порядок: Gleb's car (сначала владелец). "of Gleb" — используется для предметов, не для людей. "car's Gleb" — такого в английском нет.

---

#### AI Exercise

**ID:** basics_card19_ex1
**Title:** "Притяжательный 's: чьё это?"
**Input Mode:** FILL_BLANKS
**Words Source:** NONE
**AI Config Profile:** EXERCISE_LIGHT
**User Instruction:** "AI даст предложения с пропусками и переводы. Заполни пропуски, используя конструкцию с притяжательным 's."
**Prompt Template:** "Дай 2–3 предложения с пропусками на конструкцию притяжательного 's и русские переводы с указанием владельца (например: '___ dog is very big.' → 'Собака Саши очень большая.'). Пользователь составляет конструкцию name's. Проверь апостроф и место 's. Объясни ошибку."
> "I like ___ new jacket." *(Мне нравится новая куртка Кати.)*

Пользователь вписывает `Sasha's` / `the name of` / `Katya's`. Заполненные предложения отправляются AI — AI проверяет правильность 's или of и объясняет выбор.

#### Clarification Options

- Когда 's, а когда of
- Почему "Lena's dog", а не "dog of Lena"
- Как отличить притяжательный 's от сокращения 's (is/has)

---

### Words8r Sync · Притяжательные местоимения

| Слово | Переводы | Транскрипция |
|-------|----------|-------------|
| my | мой, моя, моё, мои | [maɪ] |
| your | твой, ваш, твоя, ваша | [jɔːr] |
| his | его | [hɪz] |
| her | её | [hɜːr] |
| its | его, её (для предметов/животных) | [ɪts] |
| our | наш, наша, наше, наши | [aʊər] |
| their | их | [ðer] |
| mine | мой, моя (без сущ.) | [maɪn] |
| yours | твой, ваш (без сущ.) | [jɔːrz] |
| hers | её (без сущ.) | [hɜːrz] |
| ours | наш, наша (без сущ.) | [aʊərz] |
| theirs | их (без сущ.) | [ðerz] |

---

### Card 20 · Объектные местоимения: me, him, her, us, them

**ID:** 20 | **Order:** 4

#### Theory

Ты уже знаешь две формы местоимений. Субъектную — когда местоимение **делает** действие: "**He** works here." И притяжательную — когда местоимение **владеет**: "This is **his** phone."

Но есть третья форма — **объектная**. Она нужна когда местоимение **получает** действие: кто-то что-то делает с ним, для него, о нём.

"I love **him**." — я люблю его. He — субъект (делает), **him** — объект (получает).

Вот все три формы рядом:

| Субъект (кто делает) | Объект (на кого направлено) | Притяжательное (чьё) |
|----------------------|-----------------------------|----------------------|
| **I** | **me** | my / mine |
| **you** | **you** | your / yours |
| **he** | **him** | his / his |
| **she** | **her** | her / hers |
| **it** | **it** | its |
| **we** | **us** | our / ours |
| **they** | **them** | their / theirs |

Обрати внимание: **you** и **it** не меняются вообще. **his** — одинаков для притяжательного и объектного. **her** — одинакова для объектного и притяжательного прилагательного (но не для "hers").

**Когда использовать объектную форму?**

Два случая — после **глагола** и после **предлога**.

После глагола (действие направлено на кого-то):
- I see **him** every day. — Я вижу его каждый день.
- She called **me** this morning. — Она позвонила мне утром.
- We invited **them** to the party. — Мы пригласили их на вечеринку.

После предлога (for, with, to, about, near, from...):
- This gift is for **her**. — Этот подарок для неё.
- Come with **us**! — Иди с нами!
- He's talking about **me**. — Он говорит обо мне.
- She lives near **them**. — Она живёт рядом с ними.

**Главная ловушка — "between she and I"**

В русском языке мы говорим "между мной и ей" — и это нормально, потому что русские падежи сами подсказывают форму. В английском ухо часто "слышит" что после "and" должен быть субъект — и люди пишут "between she and I". Это ошибка.

Предлог требует объектную форму всегда, даже если стоит союз "and":
- "between **her** and **me**" ✓ — не "between she and I" ✗
- "for **him** and **her**" ✓ — не "for he and she" ✗
- "with **us** and **them**" ✓

**his и her — двойники**

Эти два местоимения работают в двух ролях и различить их можно только по контексту:

| Форма | Роль | Пример |
|-------|------|--------|
| **his** | притяжательное прилагательное | **His** phone is new. (Его телефон новый.) |
| **his** | притяжательное местоимение | This phone is **his**. (Этот телефон — его.) |
| **her** | объектное местоимение | I see **her**. (Я вижу её.) |
| **her** | притяжательное прилагательное | **Her** bag is red. (Её сумка красная.) |

Если после **his/her** стоит существительное — это притяжательное. Если нет — либо объектное (her), либо притяжательное местоимение (his/her).

#### Summary *(кнопка «?» в упражнениях)*

Три формы: субъект (he/she/we) → объект (him/her/us) → притяжательное (his/her/our). Объектную форму используем после глагола ("I see him") и после предлога ("for her", "with us"). Ловушка: "between her and me" — не "between she and I". his и her работают в двух ролях — определяй по контексту.

#### Examples

| # | RU | EN |
|---|----|----|
| 160 | Я вижу его каждый день. | I see him every day. |
| 161 | Она позвонила мне утром. | She called me this morning. |
| 162 | Этот подарок для неё. | This gift is for her. |
| 163 | Иди с нами! | Come with us! |
| 164 | Мы пригласили их на вечеринку. | We invited them to the party. |
| 165 | Он говорит обо мне, а не о тебе. | He's talking about me, not about you. |
| 166 | Это между ним и мной. | This is between him and me. |

---

#### Exercises

**Ex 146 · MultipleChoice · CHOICE** *(ID: 20)*

"I really like ___. She's very funny." (Она мне очень нравится. Она очень смешная.)

- **her** ✓
- she
- hers

*Explanation (при ошибке):* После глагола "like" нужна объектная форма. "she" — субъектная форма, она используется когда она сама делает действие. "her" — объектная, используем когда действие направлено на неё.

---

**Ex 147 · MultipleChoice · CHOICE** *(ID: 21)*

"This present is from ___ and ___." (Этот подарок от неё и него.)

- **her** and **him** ✓
- she and he
- hers and his

*Explanation (при ошибке):* После предлога "from" нужна объектная форма — him и her. "she" и "he" — субъектные формы, они идут только перед глаголом. "hers" и "his" — притяжательные местоимения (стоят без существительного после), а не объектные.

---

**Ex 148 · ErrorCorrection** *(ID: 3)*

В предложении есть ошибка. Выбери правильный вариант:

"Can you help I?"

- Can you help I?  ✗
- **Can you help me?** ✓
- Can you help my?

*Explanation (при ошибке):* После глагола "help" нужна объектная форма — "me", не "I". "I" — это субъект (тот кто делает: "I help"). "my" — притяжательное прилагательное (my bag, my phone). Правильно: "Can you help me?" — Ты можешь мне помочь?

---

**Ex 149 · ErrorCorrection** *(ID: 4)*

В предложении есть ошибка. Выбери правильный вариант:

"This is a secret between she and I."

- This is a secret between she and I. ✗
- This is a secret between her and me. ✓
- This is a secret between hers and mine.

*Explanation (при ошибке):* После предлога "between" нужна объектная форма — "her" и "me". Предлог требует объектную форму всегда, даже если рядом стоит "and". "she" и "I" — субъектные формы, только перед глаголом. "hers" и "mine" — притяжательные местоимения, не объектные.

---

**Ex 150 · TextInput** *(ID: 10)*

Вставь правильную объектную форму местоимения:

1. "I love ___." (Я люблю её.) → **her**
2. "She sees ___ every morning." (Она видит его каждое утро.) → **him**
3. "Can they hear ___?" (Они могут нас слышать?) → **us**
4. "This letter is for ___." (Это письмо для них.) → **them**

*Explanation (при ошибке):* После глагола и после предлога — всегда объектная форма: me / you / him / her / it / us / them.

---

**Ex 151 · TrueFalse** *(ID: 15)*

| # | EN | RU | Верно? |
|---|----|----|--------|
| 1 | She called him yesterday. | Она позвонила ему вчера. | ✓ |
| 2 | I see she every day. | Я вижу её каждый день. | ✗ |
| 3 | This bag is not hers. | Эта сумка не её. | ✓ |
| 4 | Between you and I, this is wrong. | Между тобой и мной, это неправильно. | ✗ |
| 5 | Come with us to the café. | Пойдём с нами в кафе. | ✓ |

*Explanation (при ошибке):* Предл. 2: после глагола "see" нужна объектная форма — "her", не "she". Предл. 4: после предлога "between" — объектная форма "me", не "I". Запомни: предлог всегда требует объектную форму, даже если рядом "and".

---

**Ex 152 · WordArrangement** *(ID: 14)*

Ситуация: ты хочешь сказать, что этот подарок — для неё и для него.

Правильное предложение: **This present is for her and him.**

| Слово | Перевод | Лишнее? |
|-------|---------|---------|
| This present | этот подарок | — |
| is | — | — |
| for | для | — |
| her | её | — |
| and | и | — |
| him | него | — |
| she | она | ✗ дистрактор |
| he | он | ✗ дистрактор |

*Explanation (при ошибке):* После предлога "for" — объектная форма: "her" и "him". "she" и "he" — субъектные формы, ставятся только перед глаголом.

---

#### AI Exercise

**ID:** basics_card20_ex1
**Title:** "Субъект, объект или притяжательное?"
**Input Mode:** FILL_BLANKS
**Words Source:** NONE
**AI Config Profile:** EXERCISE_LIGHT
**User Instruction:** "AI даст предложения с пропусками. Выбери правильную форму местоимения — субъектную, объектную или притяжательную."
**Prompt Template:** "Дай 3 английских предложения с пропусками на выбор формы личного местоимения (субъект/объект/притяжательное). Отмечай пропуск как [___], рядом в скобках — русский перевод нужного слова. Пример вывода: '1. I saw ___ (его) at the café yesterday. 2. ___ (Он) called me this morning. 3. This is ___ (его) book.' Проверь каждый ответ. При ошибке объясни какая форма нужна и почему."

#### Clarification Options

- Чем отличается him от his
- Почему нельзя сказать "I love she"
- Когда her — это объектное, а когда — притяжательное
- Почему "between her and me", а не "between she and I"

---
---

## Microtopic 8 — Demonstrative Pronouns · Указательные местоимения

**ID:** 8 | **Order:** 8

---

### Card 15 · this / that · Этот / Тот

**ID:** 15 | **Order:** 1

#### Theory

В русском языке слово "это" используется для всего подряд — и для близкого, и для далёкого. В английском так не работает. Здесь всегда важно **расстояние**!

**this** — ЭТОТ / ЭТА / ЭТО → предмет **рядом**, близко. Можно протянуть руку и потрогать.
**that** — ТОТ / ТА / ТО → предмет **далеко**. Нужно показывать жестом "вон там".

Оба слова — для **одного предмета**.

Классический пример: ты в кофейне.
- Показываешь на свой стакан передо мной: "**This** coffee is cold." (Вот этот кофе — холодный.)
- Показываешь на чужой стакан в дальнем конце зала: "**That** coffee looks delicious." (Тот кофе выглядит вкусно.)

**Формула для представления:** `This is...` и `That is...` используются когда нужно что-то или кого-то назвать/представить.

"**This is** my friend Dima." (Познакомься, это мой друг Дима.) — Дима стоит рядом.
"**That is** the Eiffel Tower." (Вон — Эйфелева башня.) — показываешь вдаль.
"**What is that?**" (Что это там такое?) — о чём-то непонятном вдали.

**Ловушка: "это" и "вот это".** В русском оба случая — "это". В английском ты обязан выбрать: "this" (рядом) или "that" (далеко). Сказать "this" о далёком предмете — носители поймут, но звучит странно, как будто предмет вдруг прыгнул к тебе.

**Ещё одна ловушка:** слово `that` ещё используется как союз ("Я знаю, что он устал" → "I know **that** he is tired"). Это другое "that" — не указательное. Здесь мы изучаем только указательное.

#### Summary *(кнопка «?» в упражнениях)*

this = этот/эта/это (рядом), that = тот/та/то (далеко). Оба — один предмет. "This is..." — представить близкое. "That is..." — указать на далёкое или спросить: "What is that?"

#### Examples

| # | RU | EN |
|---|----|----|
| 84 | Это мой телефон. (держу в руке) | This is my phone. |
| 85 | Что это там? | What is that? |
| 86 | Эта куртка слишком маленькая. (примеряю) | This jacket is too small. |
| 87 | Тот ресторан очень дорогой. (показываю вдаль) | That restaurant is very expensive. |
| 88 | Это моя мама. (она стоит рядом) | This is my mum. |
| 89 | Кто это там? | Who is that? |

---

#### Exercises

**Ex 82 · Categorization** *(ID: 1)*

Задание: перетащи каждую ситуацию в нужную колонку

| this (рядом — можно потрогать) | that (далеко — нужно показывать) |
|-------------------------------|----------------------------------|
| Книга у тебя в руках | Здание на другом конце улицы |
| Телефон на твоём столе | Человек в конце коридора |
| Кофе который ты пьёшь | Гора вдали за окном |
| Куртка на твоём стуле | Магазин через дорогу |

*Explanation (при ошибке):* this — предмет близко, можно протянуть руку. that — предмет далеко, нужно показывать.

---

**Ex 83 · MultipleChoice · CHOICE** *(ID: 22)*

"___ is my sister." (Сестра стоит рядом с тобой, представляешь её)

- **This** ✓
- That
- These

*Explanation (при ошибке):* Сестра рядом → this. "That" — человек или предмет далеко. "These" — несколько людей/предметов (мн. число), здесь один человек.

---

**Ex 84 · MultipleChoice · FORWARD_CHOICE** *(ID: 13)*

Смотришь в окно и говоришь про незнакомое высотное здание вдалеке: "Вон то здание — новое."

- This building is new.
- **That building is new.** ✓
- These buildings are new.

*Explanation (при ошибке):* Здание вдали → that. "This" — только для того что рядом. "These" — множественное число, а здание одно.

---

**Ex 85 · TrueFalse** *(ID: 16)*

| # | Ситуация | EN | Верно? |
|---|----------|----|--------|
| 1 | Держишь щенка на руках. | This is my dog. | ✓ |
| 2 | Твой кофе стоит перед тобой. | That is my coffee. | ✗ |
| 3 | Видишь что-то непонятное вдали. | What is that? | ✓ |
| 4 | Смотришь на Кремль в 500 метрах. | This is the Kremlin. | ✗ |
| 5 | Знакомишь маму — она рядом. | This is my mum. | ✓ |

*Explanation (при ошибке):* Предл. 2: кофе рядом → this, не that. Предл. 4: Кремль далеко → that, не this.

---

**Ex 86 · WordArrangement** *(ID: 15)*

Ситуация: видишь в конце коридора незнакомца и хочешь спросить друга — кто это.

Правильное предложение: **Who is that?**

| Слово | Перевод | Лишнее? |
|-------|---------|---------|
| Who | Кто | — |
| is | — | — |
| that | — | — |
| this | — | ✗ дистрактор |
| What | — | ✗ дистрактор |

*Explanation (при ошибке):* Незнакомец вдалеке → "that". "Who" — спрашиваем о человеке (а не "What"). "Who is that?" = "Кто это там?"

---

#### AI Exercise

**ID:** basics_card15_ex1
**Title:** "This или that? Близко или далеко?"
**Input Mode:** FILL_BLANKS
**Words Source:** NONE
**AI Config Profile:** EXERCISE_LIGHT
**User Instruction:** "AI опишет ситуацию и даст предложения с пропусками. Вставь this или that — в зависимости от расстояния."
**Prompt Template:** "Дай ситуацию-контекст с указанием расстояния и 2–3 предложения с пропусками на this/that + переводы (например: '___ is my coffee.' → 'Вот этот кофе — мой. Стоит передо мной.'). Пользователь вставляет this/that. Проверь и объясни ошибку — акцент на то что this = близко, that = далеко."
> "Who is ___?" *(Кто это там?)*

Пользователь вписывает `This` / `That` / `that`. Заполненные предложения отправляются AI — AI проверяет каждое и объясняет почему this или that.

#### Clarification Options

- Когда this, а когда that
- Почему нельзя всегда говорить "this"
- Что значит "This is..." в начале предложения

---

### Card 16 · these / those · Множественное число

**ID:** 16 | **Order:** 2

#### Theory

Ты уже знаешь: `this` = рядом (один), `that` = далеко (один). Теперь добавим **число**!

Когда предметов **несколько** — слова меняются:

| | Один предмет | Несколько предметов |
|---|---|---|
| **Рядом** | this — этот/эта/это | **these** — эти |
| **Далеко** | that — тот/та/то | **those** — те |

Запомнить просто: `this` и `these` оба начинаются с "th" и содержат "is"/"ese". `that` → `those` (похожи по звуку).

**Важно: когда предметов несколько, глагол to be меняется!**

- "**This is** my key." → "**These are** my keys." (Это мой ключ. → Это мои ключи.)
- "**That is** your bag." → "**Those are** your bags." (Та сумка твоя. → Те сумки твои.)

С `these` и `those` — всегда `are`, не `is`.

❌ "These **is** my books." — грубая ошибка, которую делают почти все
✓ "These **are** my books." — правильно

**В живой речи** `these` и `those` часто используют без существительного, если предмет понятен из контекста:

Продавец показывает на витрину рядом: "**These** are on sale." (Вот эти — на распродаже.)
Ты видишь что-то непонятное: "What are **those**?" (Что это там такое?)
В магазине, выбирая: "I'll take **these**." (Я возьму вот эти.)

#### Summary *(кнопка «?» в упражнениях)*

these = эти (рядом, несколько), those = те (далеко, несколько). После these/those — всегда are. this is → these are, that is → those are.

#### Examples

| # | RU | EN |
|---|----|----|
| 90 | Эти ключи мои. | These keys are mine. |
| 91 | Те дома очень старые. | Those houses are very old. |
| 92 | Эти дети в моём классе. | These children are in my class. |
| 93 | Что это за животные вон там? | What are those animals? |
| 94 | Эти книги не мои. | These books are not mine. |
| 95 | Те люди — туристы. | Those people are tourists. |

---

#### Exercises

**Ex 87 · Transformation** *(ID: 2)*

Задание: сделай предложение во множественном числе

1. "This is my book." → **"These are my books."**
2. "That is her key." → **"Those are her keys."**
3. "This is a good idea." → **"These are good ideas."**

*Explanation (при ошибке):* this → these, that → those. Глагол меняется: is → are. Существительное во множественном числе получает окончание +s.

---

**Ex 88 · MultipleChoice · CHOICE** *(ID: 23)*

"___ are my glasses." (Очки лежат прямо передо мной на столе)

- This
- That
- **These** ✓
- Those

*Explanation (при ошибке):* Очки — несколько предметов → множественное число. Лежат рядом → these. "Those" — для далёких предметов. "This/That" — единственное число.

---

**Ex 89 · ErrorCorrection** *(ID: 5)*

В предложении есть ошибка. Выбери правильный вариант:

"These is my parents."

- These is my parents. ✗
- **These are my parents.** ✓
- This are my parents. ✗

*Explanation (при ошибке):* Родители — двое, значит множественное число → these. После these — всегда are, не is. "These is" — одна из самых частых ошибок у начинающих.

---

**Ex 90 · WordArrangement** *(ID: 16)*

Ситуация: ты с другом в незнакомом городе, вдалеке видите красивые здания.

Правильное предложение: **Those buildings are very beautiful.**

| Слово | Перевод | Лишнее? |
|-------|---------|---------|
| Those | — | — |
| buildings | здания | — |
| are | — | — |
| very | очень | — |
| beautiful | красивые | — |
| These | — | ✗ дистрактор |
| is | — | ✗ дистрактор |

*Explanation (при ошибке):* Вдали → those (не these). Несколько зданий → are (не is).

---

#### AI Exercise

**ID:** basics_card16_ex1
**Title:** "These или those? Во множественном числе"
**Input Mode:** FILL_BLANKS
**Words Source:** NONE
**AI Config Profile:** EXERCISE_LIGHT
**User Instruction:** "AI даст предложения с пропусками и описание ситуации. Вставь these или those."
**Prompt Template:** "Дай 2–3 предложения с пропусками на these/those + переводы с указанием расстояния (например: '___ keys are mine.' → 'Вот эти ключи — мои. Лежат рядом.'). Пользователь вставляет these/those. Проверь и объясни ошибку — these = близко (мн.ч.), those = далеко (мн.ч.)."
> "What are ___?" *(Что это там такое?)*

Пользователь вписывает `These` / `Those` / `those`. Заполненные предложения отправляются AI — AI проверяет каждое и объясняет ошибку если есть.

#### Clarification Options

- Почему these are, а не these is
- Разница: these vs. those
- Как запомнить все четыре: this / that / these / those

---

### Words8r Sync · Указательные местоимения

| Слово | Переводы | Транскрипция |
|-------|----------|-------------|
| this | это, этот, эта (рядом) | [ðɪs] |
| that | то, тот, та (далеко) | [ðæt] |
| these | эти (рядом, мн. ч.) | [ðiːz] |
| those | те (далеко, мн. ч.) | [ðoʊz] |

---
---

## Microtopic 9 — Question Words · Вопросительные слова

**ID:** 9 | **Order:** 9

---

### Card 17 · What, Where, When, Who, Why, How

**ID:** 17 | **Order:** 1

#### Theory

Вопросительное слово стоит в самом начале вопроса и сразу говорит **о чём именно** ты спрашиваешь — о предмете, месте, времени, человеке, причине или способе.

| Слово | Перевод | Спрашивает о | Пример |
|-------|---------|--------------|--------|
| **what** | что / какой | предмете, факте, содержании | What is this? — Что это? |
| **where** | где / куда | месте | Where is my phone? — Где мой телефон? |
| **when** | когда | времени | When is the meeting? — Когда встреча? |
| **who** | кто | человеке | Who is she? — Кто она? |
| **why** | почему | причине | Why are you sad? — Почему ты грустишь? |
| **how** | как | способе, состоянии | How are you? — Как ты? |

**Порядок слов:** вопросительное слово → глагол to be → подлежащее (всё остальное).

"**Where** is my bag?" — Где моя сумка?
"**Who** is that man?" — Кто этот мужчина?
"**When** is your birthday?" — Когда твой день рождения?

Обрати внимание: по-русски мы говорим "**Как** тебя зовут?", а по-английски буквально спрашивают "**Что** твоё имя?" — "**What** is your name?" Дословный перевод ("How is your name?") — неправильно. Это один из случаев, когда русский и английский работают по-разному.

**Ловушка 1: what vs. who.**
`what` — о предмете, факте, явлении: "What is your job?" (Какая у тебя работа?)
`who` — о человеке: "Who is your boss?" (Кто твой начальник?)

Мысленная проверка: как будет звучать ответ? Если ответ — человек ("Мой начальник — Иван") → `who`. Если ответ — вещь или факт ("Я менеджер") → `what`.

**Ловушка 2: where vs. when.**
Очень похожие слова — легко перепутать при быстром чтении.
`where` — место: "**Where** is the station?" (Где вокзал?)
`when` — время: "**When** is the train?" (Когда поезд?)

Подсказка: `where` содержит слово `here` (здесь) — оба о месте.

**Ловушка 3: how vs. what** в вопросах о состоянии.
"How are you?" — Как ты? (о состоянии/самочувствии)
"What are you?" — Кто ты по профессии? (буквально: "Чем ты являешься?")
Разные вопросы — разные ответы.

#### Summary *(кнопка «?» в упражнениях)*

What — что/какой, where — где/куда, when — когда, who — кто, why — почему, how — как. Порядок: вопросительное слово + to be + остальное. What — о вещах и фактах, who — только о людях.

#### Examples

| # | RU | EN |
|---|----|----|
| 96 | Что это такое? | What is this? |
| 97 | Где находится вокзал? | Where is the station? |
| 98 | Когда твой день рождения? | When is your birthday? |
| 99 | Кто твой любимый актёр? | Who is your favourite actor? |
| 100 | Почему ты такой грустный? | Why are you so sad? |
| 101 | Как дела? | How are you? |
| 102 | Какая у тебя работа? | What is your job? |

---

#### Exercises

**Ex 91 · Matching** *(ID: 2)*

Задание: соедини тип вопроса с нужным вопросительным словом

| Спрашиваю о... | Слово |
|----------------|-------|
| человеке | who |
| месте | where |
| времени | when |
| причине | why |
| предмете / факте | what |
| способе / состоянии | how |

*Explanation (при ошибке):* who = кто (о людях), where = где (о месте), when = когда (о времени), why = почему (о причине), what = что/какой (о предметах и фактах), how = как (о способе/состоянии).

---

**Ex 92 · MultipleChoice · CHOICE** *(ID: 24)*

"___ is your sister?" (Хочу узнать, где находится сестра)

- What
- Who
- **Where** ✓
- When

*Explanation (при ошибке):* Спрашиваем о месте → where. "Who" — о человеке (но мы уже знаем что это сестра, нас интересует её местонахождение). "What" — о предмете/факте. "When" — о времени.

---

**Ex 93 · MultipleChoice · FORWARD_CHOICE** *(ID: 14)*

"Почему ты опоздал?"

- What are you late?
- Where are you late?
- **Why are you late?** ✓
- Who are you late?

*Explanation (при ошибке):* Причина опоздания → why (почему). "What" — о предмете/факте. "Where" — о месте. "Who" — о человеке.

---

**Ex 94 · FindTheOdd** *(ID: 1)*

Три слова связаны с местом. Найди лишнее — слово о времени, а не о месте:

- where
- here
- there
- **when** ✓

*Explanation (при ошибке):* where (где?), here (здесь), there (там) — все связаны с местом. "when" — это вопрос о времени. Лишнее — when.

---

**Ex 95 · TrueFalse** *(ID: 17)*

| # | EN | RU | Верно? |
|---|----|----|--------|
| 1 | Who is that woman? | Кто та женщина? | ✓ |
| 2 | Where is your birthday? | Когда твой день рождения? | ✗ |
| 3 | What is your name? | Как тебя зовут? | ✓ |
| 4 | Why are you happy? | Почему ты такой счастливый? | ✓ |
| 5 | Who is the capital of Russia? | Какая столица России? | ✗ |

*Explanation (при ошибке):* Предл. 2: день рождения — время, не место → when, не where. Предл. 5: столица — предмет, не человек → what, не who.

---

**Ex 96 · WordArrangement** *(ID: 17)*

Ситуация: ты не можешь найти ключи. Что ты спросишь вслух?

Правильное предложение: **Where are my keys?**

| Слово | Перевод | Лишнее? |
|-------|---------|---------|
| Where | — | — |
| are | — | — |
| my | мои | — |
| keys | ключи | — |
| When | — | ✗ дистрактор |
| is | — | ✗ дистрактор |
| What | — | ✗ дистрактор |

*Explanation (при ошибке):* Место → where (не when, не what). Ключей несколько → are (не is).

---

#### AI Exercise

**ID:** basics_card17_ex1
**Title:** "Вставь вопросительное слово"
**Input Mode:** FILL_BLANKS
**Words Source:** NONE
**AI Config Profile:** EXERCISE_LIGHT
**User Instruction:** "AI даст вопросы с пропусками и переводы. Вставь нужное вопросительное слово: what / where / when / who / why / how."
**Prompt Template:** "Дай 2–3 вопроса с пропусками на вопросительные слова (what/where/when/who/why/how) + переводы (например: '___ is your favourite season?' → 'Какое твоё любимое время года?'). Пользователь вставляет слово. Проверь и объясни ошибку — укажи на разницу в значении перепутанных слов."
> "___ are you so tired?" *(Почему ты так устал?)*

Пользователь вписывает `What` / `Where` / `Why`. Заполненные предложения отправляются AI — AI проверяет каждое и объясняет ошибку если есть.

#### Clarification Options

- Разница между what и who
- Почему "What is your name?" а не "How is your name?"
- Как не путать where и when

---

### Card 18 · How + прилагательное

**ID:** 18 | **Order:** 2

#### Theory

Слово `how` в одиночку значит "как". Но стоит добавить к нему прилагательное — получается совершенно новый вопрос со своим конкретным смыслом.

| Конструкция | Перевод | Пример |
|-------------|---------|--------|
| **how old** | сколько лет | How old are you? — Сколько тебе лет? |
| **how many** | сколько (исчисляемые) | How many cats do you have? — Сколько у тебя кошек? |
| **how much** | сколько (несчётные) / сколько стоит | How much is it? — Сколько стоит? |
| **how long** | как долго / какой длины | How long is the film? — Сколько идёт фильм? |
| **how far** | как далеко | How far is the station? — Далеко ли вокзал? |
| **how often** | как часто | How often do you exercise? — Как часто ты занимаешься? |

**Главная ловушка: how many vs. how much.**

Это одна из самых частых ошибок у тех, кто только начинает учить английский.

`how many` — для того что **можно посчитать** по штукам: cats (кошки), books (книги), people (люди), days (дни). Можно сказать "один кот, два кота, три кота".

`how much` — для того что **нельзя посчитать** по штукам: water (вода), money (деньги), time (время), milk (молоко). Нельзя сказать "одна вода, две воды".

Простая проверка в голове: можно сказать "один, два, три ___"?
- ✓ Один кот, два кота — **how many cats?**
- ✗ Одна вода, две воды... нет — **how much water?**

**"How much is it?"** — обязательная фраза для любого магазина. Дословно: "Сколько это стоит?" Выучи как одно целое — пригодится сразу. Ответ: "It's 500 roubles." (Это 500 рублей.)

**"How old"** — единственный способ спросить возраст. Буквально: "Насколько старый?" По-английски это абсолютно вежливо — не грубость, не неловкость. "How old is she?" — нормальный вопрос.

**"How long"** работает и про время ("How long is the meeting?" — Сколько длится встреча?), и про размер ("How long is this rope?" — Какой длины эта верёвка?). Контекст подскажет.

#### Summary *(кнопка «?» в упражнениях)*

how old — возраст, how many — счётные предметы, how much — несчётные + цена ("How much is it?"), how long — длительность/длина, how far — расстояние, how often — частота.

#### Examples

| # | RU | EN |
|---|----|----|
| 103 | Сколько тебе лет? | How old are you? |
| 104 | Сколько у тебя братьев? | How many brothers do you have? |
| 105 | Сколько стоит этот телефон? | How much is this phone? |
| 106 | Как долго идёт фильм? | How long is the film? |
| 107 | Как далеко отсюда до центра? | How far is it to the city centre? |
| 108 | Как часто ты занимаешься спортом? | How often do you do sport? |

---

#### Exercises

**Ex 97 · FindTheOdd** *(ID: 2)*

Три конструкции ожидают в ответ число или сумму. Найди ту, что ожидает другой тип ответа:

- how old → ответ: 25 years
- how many → ответ: 3 cats
- how much → ответ: 500 roubles
- **how often** ✓ → ответ: every day / twice a week (не число, а наречие частоты)

*Explanation (при ошибке):* how old, how many, how much — все ожидают числовой ответ. how often ожидает наречие частоты: "every day", "sometimes", "twice a week". Это другой тип ответа.

---

**Ex 98 · MultipleChoice · CHOICE** *(ID: 25)*

"___ brothers do you have?"

- how much
- **how many** ✓
- how old
- how long

*Explanation (при ошибке):* Братья — конкретные люди, их можно посчитать (один брат, два брата) → how many. "how much" — для несчётного: воды, времени, денег. "how old" — о возрасте. "how long" — о длительности или длине.

---

**Ex 99 · MultipleChoice · REVERSE_CHOICE** *(ID: 7)*

"How much is it?"

- Как долго это длится?
- **Сколько это стоит?** ✓
- Сколько их штук?
- Как далеко это?

*Explanation (при ошибке):* "How much is it?" — классическая фраза в магазине: "Сколько стоит?" it = цена/стоимость товара. "How long" — о времени или длине. "How many" — о количестве штук. "How far" — о расстоянии.

---

**Ex 100 · TextInput** *(ID: 11)*

Вставь нужную конструкцию:

1. "___ is the film?" (Спрашиваю, как долго идёт фильм)
   Ответ: **How long**

2. "___ are you?" (Спрашиваю о возрасте)
   Ответ: **How old**

*Explanation (при ошибке):* How long — длительность (фильма, поездки, урока). How old — возраст человека. Нельзя поменять местами: "How old is the film?" — странный вопрос о возрасте фильма, не о его продолжительности.

---

**Ex 101 · WordArrangement** *(ID: 18)*

Ситуация: ты в магазине, хочешь узнать цену куртки у продавца.

Правильное предложение: **How much is this jacket?**

| Слово | Перевод | Лишнее? |
|-------|---------|---------|
| How much | — | — |
| is | — | — |
| this | — | — |
| jacket | куртка | — |
| How many | — | ✗ дистрактор |
| How long | — | ✗ дистрактор |

*Explanation (при ошибке):* Цена → how much (не how many — оно для счётных предметов: "How many jackets?"). Конструкция: "How much is [предмет]?"

---

#### AI Exercise

**ID:** basics_card18_ex1
**Title:** "How much, how many или how long?"
**Input Mode:** FILL_BLANKS
**Words Source:** NONE
**AI Config Profile:** EXERCISE_LIGHT
**User Instruction:** "AI даст вопросы с пропусками и переводы. Вставь нужное выражение: how much / how many / how long / how far / how old."
**Prompt Template:** "Дай 2–3 вопроса с пропусками на how much/how many/how long/how far/how old + переводы (например: '___ people are in your family?' → 'Сколько человек в твоей семье?'). Пользователь вставляет выражение. Проверь и объясни ошибку — акцент на how much (несчётные) vs how many (счётные)."
> "___ is the flight?" *(Как долго летит самолёт?)*

Пользователь вписывает `How many` / `How much` / `How long`/ `how far` / `how often`. Заполненные предложения отправляются AI — AI проверяет каждое и объясняет ошибку если есть.

#### Clarification Options

- How many или how much — как выбрать?
- Почему "How much is it?", а не "How many is it?"
- Разница: how long (время) vs. how far (расстояние)

---

### Words8r Sync · Вопросительные слова

| Слово | Переводы | Транскрипция |
|-------|----------|-------------|
| what | что, какой, какая, какое | [wɒt] |
| where | где, куда | [wer] |
| when | когда | [wen] |
| who | кто | [huː] |
| why | почему | [waɪ] |
| how | как | [haʊ] |
| how old | сколько лет | — |
| how many | сколько (счётные) | — |
| how much | сколько (несчётные), сколько стоит | — |
| how long | как долго, какой длины | — |
| how far | как далеко | — |
| how often | как часто | — |

---
---

# БЛОК 4 · Микротемы 10–12

---

## Microtopic 10 — Предлоги места · Basic Place Prepositions

**ID:** 10 | **Order:** 10

---

### Card 27 · in / on / at — главная тройка

**ID:** 27 | **Order:** 1

#### Theory

Три предлога — `in`, `on`, `at` — встречаются в английском чаще любых других. Все три переводятся на русский как "в", "на" или "у", но выбирать их нужно по разной логике.

**`in` — внутри**

`in` говорит о том, что что-то находится *внутри* чего-то: внутри комнаты, внутри коробки, внутри города, внутри страны. Представь трёхмерное пространство, в котором ты оказался.

- "The keys are **in** the bag." — *Ключи в сумке.*
- "She lives **in** Moscow." — *Она живёт в Москве.*
- "There's milk **in** the fridge." — *В холодильнике есть молоко.*

**`on` — на поверхности**

`on` говорит о том, что что-то лежит, стоит или висит *на поверхности*. Книга на столе, картина на стене, кошка на диване.

- "The phone is **on** the table." — *Телефон на столе.*
- "There is a map **on** the wall." — *На стене висит карта.*
- "The cat is sleeping **on** the sofa." — *Кошка спит на диване.*

**`at` — в точке присутствия**

`at` — самый хитрый из трёх. Он говорит не о физическом пространстве, а о *присутствии в месте по функции*. Важно не то, что ты буквально внутри или снаружи, а то, что ты *там находишься* по какой-то роли.

- "She is **at** work." — *Она на работе.* (Не внутри здания, а "в статусе на работе".)
- "He is **at** school." — *Он в школе.* (Учится — это его место сейчас.)
- "We are **at** the airport." — *Мы в аэропорту.* (Ждём рейс — это наша точка.)
- "I'll meet you **at** the café." — *Встретимся в кафе.* (Место встречи — конкретная точка.)

**Главная ловушка: `at school` vs `in school`**

- "He is **at** school." — он *учится*, это его место прямо сейчас.
- "He is **in** the school building." — он физически внутри здания (например, ищет директора).

`at` — функциональное присутствие. `in` — буквально внутри замкнутого пространства.

**Транспорт — особое правило:**

- **in**: закрытое личное пространство → "in a car", "in a taxi"
- **on**: общественный или открытый транспорт → "on a bus", "on a train", "on a plane", "on a bike"

Логика: в машине ты *внутри*. В автобусе — ты *едешь на* маршруте, ты *на* нём.

#### Summary *(кнопка «?» в упражнениях)*

in = внутри замкнутого пространства (комната, сумка, город). on = на поверхности (стол, стена, пол). at = присутствие в точке/месте назначения (at work, at school, at the airport). Транспорт: in a car / on a bus.

#### Examples

| # | RU | EN |
|---|----|----|
| 115 | Книга лежит на полке. | The book is on the shelf. |
| 116 | Артём живёт в Казани. | Artyom lives in Kazan. |
| 117 | Соня на работе. | Sonya is at work. |
| 118 | Телефон в кармане. | The phone is in the pocket. |
| 119 | Мы в аэропорту. | We are at the airport. |
| 120 | Кошка сидит на подоконнике. | The cat is sitting on the windowsill. |
| 121 | Дима едет на автобусе. | Dima is on the bus. |
| 122 | Они в театре. | They are at the theatre. |

---

#### Exercises

**Ex 107 · Categorization** *(ID: 2)*

Задание: распредели слова и фразы по колонкам in / on / at

| in | on | at |
|----|----|----|
| the box | the table | work |
| Moscow | the wall | school |
| the fridge | the floor | the station |
| the bag | the shelf | the airport |

*Explanation (при ошибке):* in = внутри замкнутого пространства. on = на поверхности. at = место назначения/присутствия. Города и страны — всегда in. Работа, школа, станция — at.

---

**Ex 108 · MultipleChoice · CHOICE** *(ID: 26)*

"She is ___ the kitchen."

- **in** ✓
- on
- at

*Explanation (при ошибке):* Кухня — закрытое пространство, она внутри него → in. "On the kitchen" — такой конструкции нет. "At the kitchen" — нестандартно; говорят "at the kitchen table" (за кухонным столом), но не "at the kitchen" о самом помещении.

---

**Ex 109 · MultipleChoice · CHOICE** *(ID: 27)*

"The keys are ___ the table."

- in
- **on** ✓
- at

*Explanation (при ошибке):* Ключи лежат на поверхности стола → on. "In the table" — значило бы "внутри стола" (в ящике). "At the table" — значит "за столом" (сидеть, есть).

---

**Ex 110 · TrueFalse** *(ID: 18)*

| # | EN | RU | Верно? |
|---|----|----|--------|
| 1 | She is in work. | Она на работе. | ✗ |
| 2 | The cat is on the sofa. | Кошка на диване. | ✓ |
| 3 | He is on the school. | Он в школе. | ✗ |
| 4 | I'm on the bus. | Я в автобусе. | ✓ |
| 5 | We are in the car. | Мы в машине. | ✓ |

*Explanation (при ошибке):* Работа → at work (не in). Школа → at school (не on). Автобус → on a bus (общественный транспорт). Машина → in a car (закрытое личное пространство).

---

**Ex 111 · ErrorCorrection** *(ID: 6)*

"My sister is in school right now." *(Выбери правильный вариант)*

- My sister is on school right now.
- My sister is to school right now.
- **My sister is at school right now.** ✓

*Explanation (при ошибке):* "At school" = она там учится, это её место сейчас. "In school" означало бы "физически внутри здания" — акцент на пространстве, а не на факте учёбы. "On school" и "to school" — не существуют в этом контексте.

---

**Ex 112 · MultipleChoice · CHOICE** *(ID: 28)*

"The passport ___ ___ the drawer."

- is at
- is on
- **is in** ✓
- are in

*Explanation (при ошибке):* "The passport" — единственное число, 3-е лицо → to be = is (не are). Ящик стола — закрытое пространство → in. "Is on" — значило бы паспорт лежит сверху на ящике. "Is at" — у ящика как у точки, нестандартно.

---

**Ex 113 · WordArrangement** *(ID: 19)*

Ситуация: звонишь другу и спрашиваешь, где он сейчас работает.

Правильное предложение: **Are you at the office?**

| Слово | Перевод | Лишнее? |
|-------|---------|---------|
| Are | — | — |
| you | ты | — |
| at | предлог (точка присутствия) | — |
| the office | офис | — |
| in | предлог (внутри) | ✗ дистрактор |
| on | предлог (поверхность) | ✗ дистрактор |

*Explanation (при ошибке):* Офис как место работы, точка функционального присутствия → at the office. "In the office" тоже встречается и означает "внутри помещения", но "at the office" — стандартный ответ на вопрос "ты на работе?". "On the office" — не существует.

---

#### AI Exercise

**ID:** basics_card27_ex1
**Title:** "Вставь in, on или at"
**Input Mode:** FILL_BLANKS
**Words Source:** NONE
**AI Config Profile:** EXERCISE_LIGHT
**User Instruction:** "AI даст 3 предложения с пропусками. Вставь нужный предлог места: in / on / at."
**Prompt Template:** "Дай 3 разных предложения с пропуском на предлог места (in/on/at), каждое в разной ситуации: одно про нахождение внутри помещения, одно про поверхность, одно про место присутствия (at work, at school и т.п.). Дай RU перевод для контекста. Пользователь вписывает предлоги. Проверь каждый ответ и объясни ошибку — акцент на разницу at (функциональное место) vs in (внутри пространства).

Пример вывода AI: '1. My phone is ___ the table. (Мой телефон ___ столе.) 2. She is ___ work right now. (Она сейчас ___ работе.) 3. The milk is ___ the fridge. (Молоко ___ холодильнике.)'"

#### Clarification Options

- Разница at school и in school — почему разные предлоги?
- Почему on a bus, но in a car?
- Как запомнить когда at, а когда in?

---

### Card 21 · near / under / between — и другие предлоги положения

**ID:** 21 | **Order:** 2

#### Theory

После главной тройки — ещё несколько предлогов, которые описывают положение точнее. Давай разберём каждый с конкретной ситуацией — иначе они все сольются в одно.

---

**`under` — под**

Самый простой. Что-то находится ниже другого объекта, под ним.

- "The cat is **under** the bed." — *Кот под кроватью.* (Klassika — кошки обожают прятаться под кроватью.)
- "The documents are **under** the laptop." — *Документы под ноутбуком.*

Не путай с "below" — below используется для абстрактных вещей (below average — ниже среднего), а under — для физического положения предметов.

---

**`near` vs `next to` — оба "рядом", но разная дистанция**

Это самая частая путаница в этой группе.

**near** — где-то поблизости, в общем районе. Необязательно вплотную:
- "There is a pharmacy **near** the metro." — *Рядом с метро есть аптека.* (Может в трёх минутах ходьбы — это "near".)
- "He lives **near** the centre." — *Он живёт рядом с центром.* (Общий район, не конкретный адрес.)

**next to** — прямо бок о бок, вплотную, касаются или почти касаются:
- "Sit **next to** me." — *Сядь рядом со мной.* (Буквально на соседнее место, между нами нет зазора.)
- "The bank is **next to** the café." — *Банк прямо рядом с кафе.* (Они стоят вплотную, стена к стене.)

Простая проверка: если между двумя объектами можно поставить ещё один — это near. Если нельзя — next to.

---

**`between` — между (ровно двумя объектами)**

"The café is **between** the bank and the bookshop." — кафе стоит между банком и книжным: слева банк, справа книжный.

Важно: **between** — только когда объектов ровно два. Если их три и больше — "among":

- "She was sitting **between** her mum and her dad." ✓ (двое — мама и папа)
- "He was standing **among** the crowd." ✓ (много людей вокруг)
- "She was sitting **between** her three colleagues." ✗ — три человека → нужно among

---

**`in front of` — перед / впереди**

Что-то стоит спереди от другого объекта, лицом к нему:
- "The car is **in front of** the house." — *Машина стоит перед домом.*
- "She stood **in front of** the mirror." — *Она стояла перед зеркалом.*

---

**`behind` — за / позади**

Противоположность in front of. Что-то скрыто сзади, позади:
- "My backpack is **behind** the sofa." — *Мой рюкзак за диваном.*
- "The keys fell **behind** the radiator." — *Ключи упали за радиатор.*

---

**`above` — над (без контакта)**

Над чем-то, но не касаясь:
- "The lamp hangs **above** the table." — *Лампа висит над столом.* (Не лежит на нём, а висит выше.)
- "The plane flew **above** the clouds." — *Самолёт летел выше облаков.*

Не путай с `on`: книга лежит **on** the table (касается), лампа висит **above** the table (не касается). Контакт или нет — вот в чём разница.

#### Summary *(кнопка «?» в упражнениях)*

under = под (физически ниже). near = рядом (в общем районе). next to = вплотную, бок о бок. between = между ровно двумя. in front of = перед. behind = за, позади. above = над без касания (в отличие от on — с контактом).

#### Examples

| # | RU | EN |
|---|----|----|
| 123 | Кот под кроватью. | The cat is under the bed. |
| 124 | Магазин рядом с домом. | The shop is near the house. |
| 125 | Кресло стоит между окном и дверью. | The armchair is between the window and the door. |
| 126 | Катя сидит рядом со мной. | Katya is sitting next to me. |
| 127 | Машина стоит перед домом. | The car is in front of the house. |
| 128 | Мой рюкзак за диваном. | My backpack is behind the sofa. |

---

#### Exercises

**Ex 114 · Matching** *(ID: 3)*

Соедини RU ↔ EN:

| RU | EN |
|----|-----|
| под столом | under the table |
| рядом с дверью | next to the door |
| перед домом | in front of the house |
| между стульями | between the chairs |

*Explanation (при ошибке):* under = под. next to = рядом с (вплотную). in front of = перед. between = между.

---

**Ex 115 · MultipleChoice · CHOICE** *(ID: 29)*

"The café is ___ the bank and the bookshop."

- near
- next to
- **between** ✓
- behind

*Explanation (при ошибке):* Кафе стоит между двумя объектами — банком и книжным. "Between" — ровно для двух объектов по обе стороны. "Near" — поблизости без уточнения с какой стороны. "Next to" — рядом с одним объектом. "Behind" — позади, не между.

---

**Ex 116 · MultipleChoice · FORWARD_CHOICE** *(ID: 15)*

"Глеб спрятался за диваном."

- Gleb is under the sofa.
- Gleb is near the sofa.
- **Gleb is behind the sofa.** ✓

*Explanation (при ошибке):* "За" в значении "позади" → behind. "Under" — это под диваном (буквально под ним). "Near" — просто рядом, без указания стороны.

---

**Ex 117 · TrueFalse** *(ID: 19)*

| # | EN | RU | Верно? |
|---|----|----|--------|
| 1 | The lamp is above the table. | Лампа над столом. | ✓ |
| 2 | The dog is between three trees. | Собака между тремя деревьями. | ✗ |
| 3 | She sat next to her friend. | Она сидела рядом со своей подругой. | ✓ |
| 4 | The shoes are under the bed. | Ботинки под кроватью. | ✓ |
| 5 | The lamp is on the ceiling. | Лампа на потолке. | ✗ |

*Explanation (при ошибке):* Предл. 2: "between" — только для двух объектов. Три дерева → "among the trees". Предл. 5: лампа висит над потолком без контакта → above the ceiling; "on" — для касания поверхности.

---

**Ex 118 · WordArrangement** *(ID: 20)*

Ситуация: ищешь пульт от телевизора и описываешь где он лежит.

Правильное предложение: **The remote is under the sofa.**

| Слово | Перевод | Лишнее? |
|-------|---------|---------|
| The remote | пульт | — |
| is | находится | — |
| under | под | — |
| the sofa | диван | — |
| behind | за (позади) | ✗ дистрактор |
| between | между | ✗ дистрактор |
| near | рядом | ✗ дистрактор |

*Explanation (при ошибке):* "Under" = под — ровно то, что нужно. "Behind" = за, позади. "Between" = между. "Near" = рядом. Пульт лежит под диваном, снизу.

---

**Ex 119 · ErrorCorrection** *(ID: 7)*

"Igor is sitting between his three colleagues."  
*(Выбери правильный вариант)*

- Igor is sitting near his three colleagues.
- Igor is sitting next to his three colleagues.
- **Igor is sitting among his three colleagues.** ✓

*Explanation (при ошибке):* "Between" — строго для двух объектов. Трое → "among". "Near" — просто поблизости, без уточнения что он в окружении. "Next to" — рядом с одним человеком.

---

#### AI Exercise

**ID:** basics_card21_ex1
**Title:** "Опиши где что находится"
**Input Mode:** FREE_WRITE
**Words Source:** NONE
**AI Config Profile:** EXERCISE_LIGHT
**User Instruction:** "AI опишет три предмета в комнате по-русски. Составь предложения на английском с правильными предлогами: near / under / between / next to / in front of / behind."
**Prompt Template:** "Опиши расположение 3 предметов в комнате по-русски (например: 'Кресло стоит между столом и окном. Книга лежит под подушкой. Цветок стоит рядом с дверью.'). Дай слова-подсказки (armchair, window, door, flower, pillow и т.п.). Пользователь составляет 3 предложения по-английски. Проверь предлоги и объясни ошибку — особо акцентируй between (только два объекта) vs near (общая близость) vs next to (вплотную).

Пример вывода AI: 'Расположи предметы: Кресло стоит между диваном и окном. Книга лежит под подушкой. Лампа стоит рядом с дверью. Слова-подсказки: armchair — кресло, sofa — диван, window — окно, book — книга, pillow — подушка, lamp — лампа, door — дверь.'"

#### Clarification Options

- Разница near и next to — оба "рядом", но в чём разница?
- Почему between — только для двух объектов?
- Разница above (над) и on (на)?

---

### Card 26 · nearby / by / beside — ещё три "рядом"

**ID:** 26 | **Order:** 3

#### Theory

В Card 21 мы разобрали `near` и `next to`. Но в английском есть ещё три слова с похожим смыслом — и каждое работает по-своему.

---

**`nearby` — поблизости (наречие и прилагательное)**

`nearby` — это НЕ предлог. Это наречие или прилагательное. Ключевое отличие от `near`: после `nearby` **не ставят существительное**.

- **near** (предлог) → всегда + существительное: "The shop is **near** the station."
- **nearby** (наречие) → без существительного: "There is a shop **nearby**."
- **nearby** (прилагательное) → перед существительным: "We found a **nearby** café."

Ловушка: "There is a shop near." — ошибка. Если нет существительного после — используй `nearby`, не `near`.

Простой тест: если после слова нет существительного — это `nearby`. Если есть — `near`.

---

**`by` в значении "рядом с"**

`by` — один из самых многозначных предлогов английского. Одно из его значений: **рядом с, у, около** — то же что `next to`, но чуть поэтичнее и встречается в устойчивых выражениях.

- "Come and sit **by** me." — *Иди сядь рядом со мной.*
- "The house **by** the river." — *Дом у реки.*
- "She was standing **by** the window." — *Она стояла у окна.*
- "A café **by** the sea." — *Кафе у моря.*

`by` в этом значении очень распространён — слышишь его постоянно. Просто знай: "by + место" часто значит "рядом с этим местом, у него".

Не путай с другими значениями `by`: "written by Pushkin" (кем сделано) или "by car" (на чём едешь) — это уже другие роли предлога.

---

**`beside` — рядом с, бок о бок**

`beside` — это почти синоним `next to`. Точно такое же расположение вплотную, но звучит чуть более формально и часто встречается в письменной речи.

- "She sat **beside** him." = "She sat **next to** him." — *Она сидела рядом с ним.*
- "Leave your bag **beside** the door." — *Оставь сумку рядом с дверью.*
- "He was standing **beside** his car." — *Он стоял у своей машины.*

В разговорной речи `next to` встречается чаще. `beside` — немного книжнее, но полностью нормально.

**Не путай `beside` и `besides`:** это разные слова!
- "**beside** the door" = рядом с дверью (место)
- "**besides** that" = кроме того, помимо этого (связующее слово)

---

**Сводная таблица — все "рядом" в одном месте:**

| Слово | Тип | Использование | Пример |
|-------|-----|---------------|--------|
| **near** | предлог | + существительное | near the shop |
| **nearby** | наречие/прил. | без существительного после | a nearby café / the café is nearby |
| **next to** | предлог | вплотную + существительное | next to the door |
| **beside** | предлог | вплотную + существительное (формальнее) | beside him |
| **by** | предлог | рядом с / у + существительное | by the window |

#### Summary *(кнопка «?» в упражнениях)*

nearby = наречие/прилагательное (без существительного после или перед существительным). by = рядом с (у реки, у окна). beside = рядом с = next to, но чуть формальнее. Не путай beside (место) и besides (кроме того).

#### Examples

| # | RU | EN |
|---|----|----|
| 154 | Есть ли здесь поблизости аптека? | Is there a pharmacy nearby? |
| 155 | Они нашли ближайшее кафе. | They found a nearby café. |
| 156 | Иди сядь рядом со мной. | Come and sit by me. |
| 157 | Домик стоит у озера. | The cottage is by the lake. |
| 158 | Она сидела рядом с сестрой. | She was sitting beside her sister. |
| 159 | Оставь зонт рядом с дверью. | Leave the umbrella beside the door. |

---

#### Exercises

**Ex 141 · MultipleChoice · CHOICE** *(ID: 30)*

"Is there a supermarket ___?" *(Есть ли здесь поблизости супермаркет?)*

- near
- **nearby** ✓
- beside

*Explanation (при ошибке):* После вопроса нет существительного — значит нужно наречие "nearby". "Near" — предлог, после него обязательно существительное: "near the station". "Beside" — рядом с (конкретным объектом), требует существительного.

---

**Ex 142 · MultipleChoice · CHOICE** *(ID: 31)*

"She was sitting ___ the window, watching the rain."

- nearby
- **by** ✓
- between

*Explanation (при ошибке):* "By the window" — классическое выражение: у окна, рядом с окном. "Nearby" — без существительного после. "Between" — между двумя объектами, здесь только одно.

---

**Ex 143 · ErrorCorrection** *(ID: 8)*

"There isn't a café near." *(Выбери правильный вариант)*

- There isn't a café next.
- There isn't a café between.
- **There isn't a café nearby.** ✓

*Explanation (при ошибке):* "Near" без существительного после — ошибка. Если нет существительного, нужно наречие "nearby". "There isn't a café nearby" = поблизости нет кафе.

---

**Ex 144 · TrueFalse** *(ID: 20)*

| # | EN | RU | Верно? |
|---|----|----|--------|
| 1 | The school is near our house. | Школа рядом с нашим домом. | ✓ |
| 2 | There is a park near. | Поблизости есть парк. | ✗ |
| 3 | He sat beside his friend. | Он сел рядом с другом. | ✓ |
| 4 | Besides the sofa means next to the sofa. | Besides the sofa — рядом с диваном. | ✗ |
| 5 | The café by the river is my favourite. | Кафе у реки — моё любимое. | ✓ |

*Explanation (при ошибке):* Предл. 2: "near" без существительного — ошибка → "nearby". Предл. 4: "besides" ≠ "beside". "Besides" = кроме того, помимо. "Beside the sofa" = рядом с диваном — но это "beside", без 's'.

---

**Ex 145 · WordArrangement** *(ID: 21)*

Ситуация: описываешь, где стоит твой велосипед.

Правильное предложение: **My bike is by the entrance.**

| Слово | Перевод | Лишнее? |
|-------|---------|---------|
| My bike | мой велосипед | — |
| is | стоит | — |
| by | у (рядом с) | — |
| the entrance | вход | — |
| near | рядом с (предлог) | ✗ дистрактор |
| nearby | поблизости | ✗ дистрактор |

*Explanation (при ошибке):* "By the entrance" — у входа, рядом с ним. "Near the entrance" тоже возможно и означает "поблизости от входа". "Nearby" — без существительного после, но тут после идёт "the entrance" → не подходит.

---

#### AI Exercise

**ID:** basics_card26_ex1
**Title:** "near, nearby или by?"
**Input Mode:** FILL_BLANKS
**Words Source:** NONE
**AI Config Profile:** EXERCISE_LIGHT
**User Instruction:** "AI даст предложения с пропусками. Вставь near / nearby / by / beside."
**Prompt Template:** "Дай 3 английских предложения с пропусками на near/nearby/by/beside. Включи один случай без существительного после (→ nearby) и один с 'by' в значении 'у'. Дай RU перевод рядом. Пользователь вписывает слово. Проверь и объясни ошибку.

Пример вывода AI: '1. Is there a hotel ___ ? (Есть ли гостиница поблизости?) 2. She was sitting ___ the fireplace. (Она сидела у камина.) 3. There is a small shop ___ the school. (Рядом со школой есть небольшой магазин.)'"

#### Clarification Options

- Разница near и nearby — почему нельзя сказать "a shop near"?
- by и beside — в чём между ними разница?
- Не путай beside и besides — что значит besides?

---

### Words8r Sync · Предлоги места

| Слово | Переводы | Транскрипция |
|-------|----------|-------------|
| in | в, внутри | [ɪn] |
| on | на (поверхности) | [ɒn] |
| at | в, на (место присутствия) | [æt] |
| near | рядом, вблизи (предлог) | [nɪr] |
| nearby | рядом, поблизости (наречие/прил.) | [ˈnɪəbaɪ] |
| under | под | [ˈʌndər] |
| between | между | [bɪˈtwiːn] |
| next to | рядом с (вплотную) | [nekst tuː] |
| beside | рядом с, бок о бок | [bɪˈsaɪd] |
| by | у, рядом с (у окна, у реки) | [baɪ] |
| in front of | перед | [ɪn frʌnt əv] |
| behind | за, позади | [bɪˈhaɪnd] |
| above | над | [əˈbʌv] |

---
---

## Microtopic 11 — Предлоги времени · Time Prepositions

**ID:** 11 | **Order:** 11

---

### Card 22 · at / on / in — для времени

**ID:** 22 | **Order:** 1

#### Theory

Те же три предлога — `at`, `on`, `in` — но теперь для времени. Правила совсем другие, чем у предлогов места. Придётся запомнить заново.

**Маленький секрет:** логика всё равно есть. Представь, что время "приближается" к точке: большие периоды требуют `in`, конкретные дни — `on`, а точные моменты — `at`.

---

**`at` — точный момент времени**

Самое конкретное: точное время на часах и особые моменты суток.

- "The meeting is **at** 3 o'clock." — *Встреча в 3 часа.*
- "We arrive **at** noon." — *Мы приедем в полдень.*
- "She woke up **at** midnight." — *Она проснулась в полночь.*

Особый случай — **at night** (ночью). Кажется странным, что не "in night" — ведь утро и вечер с `in`. Но ночь воспринимается как особый точечный момент темноты, не как период с границами. Запомни как исключение: **at night**.

---

**`on` — конкретный день или дата**

Всё, что можно найти в календаре: дни недели, конкретные даты, праздники.

- "I don't work **on** Mondays." — *Я не работаю по понедельникам.*
- "Her birthday is **on** 5th March." — *Её день рождения 5 марта.*
- "The concert is **on** Saturday evening." — *Концерт в субботу вечером.*

---

**`in` — период времени**

Самое широкое: месяцы, сезоны, годы, части суток (кроме ночи).

- "I was born **in** 1998." — *Я родился в 1998 году.*
- "It's hot **in** summer." — *Летом жарко.*
- "I start work **in** the morning." — *Я начинаю работу утром.*
- "She graduated **in** May." — *Она окончила учёбу в мае.*

Части суток: **in the morning, in the afternoon, in the evening** — всегда с `in`. Единственное исключение: **at night**.

---

**Сводная таблица:**

| Предлог | Для чего | Примеры |
|---------|----------|---------|
| **at** | точное время, ночь | at 7 pm, at noon, at midnight, at night |
| **on** | дни, даты, праздники | on Monday, on 3rd July, on Christmas Day |
| **in** | месяцы, сезоны, годы, утро/вечер | in March, in winter, in 2020, in the morning |

**Главная ловушка: at night vs in the evening/morning**

По-русски всё звучит одинаково: "утром, вечером, ночью". В английском:
- "I read **in** the morning." ✓
- "We met **in** the evening." ✓
- "She cried **at** night." ✓ — исключение, просто запомни.

#### Summary *(кнопка «?» в упражнениях)*

at = точное время на часах и ночь. on = конкретный день недели или дата. in = период: месяц, год, сезон, утро/вечер. Исключение: at night (не "in night").

#### Examples

| # | RU | EN |
|---|----|----|
| 129 | Самолёт вылетает в 14:00. | The plane leaves at 2 pm. |
| 130 | Лена родилась в июне. | Lena was born in June. |
| 131 | Встреча в среду. | The meeting is on Wednesday. |
| 132 | По утрам я пью кофе. | I drink coffee in the morning. |
| 133 | Её день рождения 12 апреля. | Her birthday is on 12th April. |
| 134 | Ночью было тихо. | It was quiet at night. |
| 135 | В 2022 году я переехал. | I moved in 2022. |
| 136 | Магазин закрыт по воскресеньям. | The shop is closed on Sundays. |

---

#### Exercises

**Ex 120 · Categorization** *(ID: 3)*

Задание: распредели в колонки at / on / in

| at | on | in |
|----|----|----|
| midnight | Friday | summer |
| 9 o'clock | 15th August | the morning |
| night | New Year's Day | 2019 |
| noon | Saturday | December |

*Explanation (при ошибке):* at = точное время и night. on = дни недели и даты. in = месяцы, годы, сезоны, части суток (утро/вечер).

---

**Ex 121 · MultipleChoice · CHOICE** *(ID: 32)*

"I usually wake up ___ 7 in the morning."

- on
- in
- **at** ✓

*Explanation (при ошибке):* 7 — точное время на часах → at. "On 7" — звучало бы как дата (7-е число). "In 7" — такой конструкции нет для времени суток.

---

**Ex 122 · TrueFalse** *(ID: 21)*

| # | EN | RU | Верно? |
|---|----|----|--------|
| 1 | She was born on July. | Она родилась в июле. | ✗ |
| 2 | The party is on Friday evening. | Вечеринка в пятницу вечером. | ✓ |
| 3 | I study English in the morning. | Я учу английский по утрам. | ✓ |
| 4 | He arrived at Monday. | Он приехал в понедельник. | ✗ |
| 5 | It's cold at night in winter. | Ночью зимой холодно. | ✓ |

*Explanation (при ошибке):* Июль — месяц → in July. Понедельник — день недели → on Monday. Ночь — исключение → at night. Утро — часть суток → in the morning.

---

**Ex 123 · TextInput** *(ID: 12)*

Вставь предлог:

1. "The film starts ___ 8 o'clock."  
   Ответ: **at**

2. "I always rest ___ the afternoon."  
   Ответ: **in**

*Explanation (при ошибке):* 8 o'clock — точное время → at. The afternoon — часть суток (не ночь) → in.

---

**Ex 124 · MultipleChoice · FORWARD_CHOICE** *(ID: 16)*

"Я не работаю по субботам."

- I don't work in Saturday.
- I don't work at Saturday.
- **I don't work on Saturdays.** ✓

*Explanation (при ошибке):* Суббота — день недели → on. "In Saturday" — ошибка: in для месяцев, лет, сезонов. "At Saturday" — ошибка: at для точного времени.

---

**Ex 125 · WordArrangement** *(ID: 22)*

Ситуация: говоришь другу когда начинается тренировка.

Правильное предложение: **The training starts at six in the evening.**

| Слово | Перевод | Лишнее? |
|-------|---------|---------|
| The training | тренировка | — |
| starts | начинается | — |
| at | предлог (точное время) | — |
| six | шесть | — |
| in | предлог (часть суток) | — |
| the evening | вечер | — |
| on | предлог (дни/даты) | ✗ дистрактор |

*Explanation (при ошибке):* "At six" — точное время на часах. "In the evening" — часть суток. "On" — для дней недели и конкретных дат, здесь не нужен.

---

#### AI Exercise

**ID:** basics_card22_ex1
**Title:** "Вставь at, on или in — время"
**Input Mode:** FILL_BLANKS
**Words Source:** NONE
**AI Config Profile:** EXERCISE_LIGHT
**User Instruction:** "AI даст 3 предложения с пропусками. Вставь нужный предлог времени: at / on / in."
**Prompt Template:** "Дай 3 предложения с пропусками на предлоги времени (at/on/in), каждое про разный тип: точное время на часах, день недели, и месяц или часть суток. Добавь переводы для контекста. Пользователь вписывает предлоги. Проверь и объясни ошибку — особо объясни 'at night' как исключение и разницу on (дни) vs in (месяцы, части суток).

Пример вывода AI: '1. The alarm rings ___ 7 in the morning. (Будильник звонит ___ 7 утра.) 2. I don't work ___ Sundays. (Я не работаю ___ воскресеньям.) 3. She was born ___ March. (Она родилась ___ марте.)'"

#### Clarification Options

- Почему at night, а не in night?
- В чём разница on Monday и in the morning?
- Как запомнить разницу at/on/in для времени?

---

### Card 23 · Когда предлог не нужен совсем

**ID:** 23 | **Order:** 2

#### Theory

Есть слова, которые сами по себе уже несут значение времени — и перед ними предлог не нужен. Добавить предлог — значит сделать ошибку.

**Без предлога всегда:**

| Слово | Перевод | Правильно | Ошибка |
|-------|---------|-----------|--------|
| yesterday | вчера | I saw him **yesterday**. | ~~at yesterday~~ |
| today | сегодня | She's busy **today**. | ~~on today~~ |
| tomorrow | завтра | Call me **tomorrow**. | ~~on tomorrow~~ |
| last week/year | на прошлой неделе/в прошлом году | **Last week** was great. | ~~in last week~~ |
| next week/year | на следующей неделе/в следующем году | I'll see you **next week**. | ~~on next week~~ |
| this morning/evening | этим утром/вечером | I called you **this morning**. | ~~in this morning~~ |
| every day/week | каждый день/неделю | He jogs **every day**. | ~~at every day~~ |

**Почему русские делают эту ошибку:**

В русском мы говорим "вчера", "сегодня" — без предлога. В английском — то же самое. Ошибка появляется потому, что только что выучили правила `at`, `on`, `in` — и начинают применять везде. Эти слова предлога не требуют — они сами наречия времени.

**Логика:** если к слову уже добавлено уточнение `this`, `last`, `next`, `every` — предлог лишний. Уточнение само по себе указывает на время.

**Бонус: два устойчивых сочетания с разными предлогами**

- "**on time**" — вовремя, точно по расписанию: "The train arrived **on time**." *(Поезд прибыл по расписанию.)*
- "**in time**" — успел, не опоздал: "We got there **in time** for the film." *(Мы успели к фильму.)*

Эти два сочетания просто запоминаем как целые фразы.

#### Summary *(кнопка «?» в упражнениях)*

yesterday / today / tomorrow / last... / next... / this... / every... — без предлога. Добавлять at/on/in к ним — ошибка. Фразы-исключения: "on time" (вовремя по расписанию) и "in time" (успел).

#### Examples

| # | RU | EN |
|---|----|----|
| 137 | Вчера была хорошая погода. | The weather was nice yesterday. |
| 138 | Она позвонит завтра. | She will call tomorrow. |
| 139 | На следующей неделе праздники. | Next week there are holidays. |
| 140 | Каждый день он ходит в спортзал. | He goes to the gym every day. |
| 141 | Этим вечером мы идём в ресторан. | This evening we are going to a restaurant. |

---

#### Exercises

**Ex 126 · FindTheOdd** *(ID: 3)*

Три из четырёх слов требуют предлог at/on/in. Найди то, что предлога НЕ требует:

- Monday → on Monday ✓
- July → in July ✓
- **yesterday** → просто yesterday, предлог не нужен ✓ (это верный ответ)
- 3 o'clock → at 3 o'clock ✓

*Explanation (при ошибке):* Monday, July, 3 o'clock — конкретные временные ориентиры, которые требуют at/on/in. "Yesterday" — наречие времени, само по себе указывает на время. Добавлять к нему предлог — ошибка: "at yesterday", "on yesterday" не существуют.

---

**Ex 127 · ErrorCorrection** *(ID: 9)*

"I'll meet you on tomorrow at the café."  
*(Выбери правильный вариант)*

- I'll meet you at tomorrow in the café.
- I'll meet you in tomorrow at the café.
- **I'll meet you tomorrow at the café.** ✓

*Explanation (при ошибке):* "Tomorrow" не требует предлога — это наречие времени. "On tomorrow", "at tomorrow", "in tomorrow" — все ошибки. "At the café" — правильно: at для места.

---

**Ex 128 · TrueFalse** *(ID: 22)*

| # | EN | RU | Верно? |
|---|----|----|--------|
| 1 | She called me in yesterday. | Она позвонила мне вчера. | ✗ |
| 2 | We have a meeting next week. | У нас встреча на следующей неделе. | ✓ |
| 3 | He goes jogging every day. | Он бегает каждый день. | ✓ |
| 4 | I saw Oleg on last Friday. | Я видел Олега в прошлую пятницу. | ✗ |
| 5 | This morning I forgot my keys. | Сегодня утром я забыл ключи. | ✓ |

*Explanation (при ошибке):* Предл. 1: "yesterday" — наречие, без предлога. Предл. 4: "last Friday" — без предлога. "Last" уже уточняет, какая пятница; "on" здесь лишний.

---

**Ex 129 · MultipleChoice · CHOICE** *(ID: 33)*

Выбери правильный вариант:

"The train arrived ___ time." *(Поезд прибыл точно по расписанию.)*

- in
- **on** ✓
- at

*Explanation (при ошибке):* "On time" = вовремя, точно по расписанию. "In time" = успел, не опоздал — немного другой смысл. Здесь речь о расписании → "on time".

---

#### AI Exercise

**ID:** basics_card23_ex1
**Title:** "Нужен ли предлог?"
**Input Mode:** FILL_BLANKS
**Words Source:** NONE
**AI Config Profile:** EXERCISE_LIGHT
**User Instruction:** "AI даст 3 предложения с пропуском перед словом времени. Реши: вставь at/on/in или оставь пустым (предлог не нужен)."
**Prompt Template:** "Дай 3 предложения с пропуском перед словом времени. Два из них требуют предлог (at/on/in), одно — не требует (yesterday/tomorrow/last.../next.../every...). Дай переводы для контекста. Пользователь решает что вставить. Проверь: если вставлен лишний предлог к наречию — объясни почему наречие его не требует. Если пропущен нужный — объясни правило.

Пример вывода AI: '1. I called him ___ yesterday. (Я позвонил ему ___ вчера.) 2. The concert is ___ Friday. (Концерт ___ пятницу.) 3. She goes to the gym ___ the morning. (Она ходит в спортзал ___ утром.)'"

#### Clarification Options

- Почему к "tomorrow" нельзя добавить "on"?
- Разница "on time" и "in time"
- Что общего у yesterday/today/tomorrow — почему без предлога?

---

### Words8r Sync · Предлоги времени

*(at, on, in добавлены в предыдущей микротеме — дублировать не нужно)*

| Слово | Переводы | Транскрипция |
|-------|----------|-------------|
| yesterday | вчера | [ˈjestərdeɪ] |
| today | сегодня | [təˈdeɪ] |
| tomorrow | завтра | [təˈmɒrəʊ] |
| last week | на прошлой неделе | [lɑːst wiːk] |
| next week | на следующей неделе | [nekst wiːk] |
| every day | каждый день | [ˈevri deɪ] |
| on time | вовремя (по расписанию) | [ɒn taɪm] |
| in time | успеть (не опоздать) | [ɪn taɪm] |

---
---

## Microtopic 12 — Предлоги направления · Direction Prepositions

**ID:** 12 | **Order:** 12

---

### Card 24 · to / from — движение к и от

**ID:** 24 | **Order:** 1

#### Theory

Раньше мы учили предлоги для мест — *где* что-то находится. Теперь — для движения: *куда* и *откуда*.

**`to` — движение в направлении цели**

`to` показывает, куда ты едешь, идёшь, направляешься. Важно: именно *движение к конкретной цели*, а не просто нахождение там.

- "I'm going **to** work." — *Я иду на работу.* (Движение, ещё не там.)
- "She flew **to** Paris." — *Она улетела в Париж.*
- "He walked **to** the shop." — *Он пошёл в магазин.*
- "Send this **to** me." — *Отправь это мне.* (Направление адресату.)

**Исключение — домой:** "I'm going **home**." — никогда не "to home". Слово `home` уже само несёт идею направления. Так же: "I'm going **there**" (не "to there").

**`from` — источник, начальная точка**

`from` показывает откуда: откуда приехал, откуда родом, откуда звонит.

- "She is **from** Italy." — *Она из Италии.*
- "I got a message **from** Natasha." — *Я получил сообщение от Наташи.*
- "The bus leaves **from** platform 3." — *Автобус отправляется с 3-й платформы.*
- "He works **from** home." — *Он работает из дома (удалённо).*

**`to` и `from` часто идут в паре:**

"I travel **from** Moscow **to** London." — *Я еду из Москвы в Лондон.* Из А → в Б.

**Ловушка — глагол "arrive":**

В русском: "приехать *в* место". В английском arrive работает иначе:
- "arrive **in** Moscow" — прибываем в большой город или страну (in, не to)
- "arrive **at** the station" — прибываем в конкретную точку (at the station, at the airport)

"**Arrive to**" — ошибка! Глагол "arrive" уже содержит идею прибытия, добавлять `to` не нужно.

#### Summary *(кнопка «?» в упражнениях)*

to = движение к цели (go to work, fly to Paris). from = источник/начало (from Italy, from my friend). "Go home" — без to. "Arrive in London / at the station" — не "arrive to".

#### Examples

| # | RU | EN |
|---|----|----|
| 142 | Я иду в библиотеку. | I'm going to the library. |
| 143 | Она из Новосибирска. | She is from Novosibirsk. |
| 144 | Поезд едет из Москвы в Санкт-Петербург. | The train goes from Moscow to Saint Petersburg. |
| 145 | Я получил письмо от мамы. | I got a letter from my mum. |
| 146 | Он идёт домой. | He is going home. |
| 147 | Самолёт прилетел в Берлин. | The plane arrived in Berlin. |

---

#### Exercises

**Ex 130 · MultipleChoice · CHOICE** *(ID: 34)*

"She is going ___ the gym."

- from
- **to** ✓
- at

*Explanation (при ошибке):* Она идёт в направлении цели — спортзала. Движение к цели → to. "From" — откуда, не куда. "At" — место присутствия, не движение.

---

**Ex 131 · MultipleChoice · FORWARD_CHOICE** *(ID: 17)*

"Роман из Екатеринбурга."

- Roman is to Yekaterinburg.
- **Roman is from Yekaterinburg.** ✓
- Roman is at Yekaterinburg.

*Explanation (при ошибке):* "Из Екатеринбурга" — откуда родом → from. "To" — направление движения, не происхождение. "At" — нахождение в точке.

---

**Ex 132 · TextInput** *(ID: 13)*

"I'm going ___ home now."  
*(Нужен ли здесь предлог?)*

Правильный ответ: *(пусто — предлог не нужен)*  
Подсказка: to / (без предлога)

*Explanation (при ошибке):* "Home" не требует предлога "to". "I'm going home" — правильно. "Going to home" — ошибка: home само несёт значение направления.

---

**Ex 133 · TrueFalse** *(ID: 23)*

| # | EN | RU | Верно? |
|---|----|----|--------|
| 1 | She arrived to the airport. | Она прибыла в аэропорт. | ✗ |
| 2 | He is from Japan. | Он из Японии. | ✓ |
| 3 | We're going to home. | Мы идём домой. | ✗ |
| 4 | The letter is from Katya. | Письмо от Кати. | ✓ |
| 5 | I go to school every morning. | Я хожу в школу каждое утро. | ✓ |

*Explanation (при ошибке):* Предл. 1: "arrive to" — ошибка. Правильно: arrive **in** (город) / arrive **at** (конкретная точка). Предл. 3: "going to home" — ошибка. Правильно: просто "going home".

---

**Ex 134 · WordArrangement** *(ID: 23)*

Ситуация: описываешь маршрут поезда.

Правильное предложение: **The train goes from Kazan to Moscow.**

| Слово | Перевод | Лишнее? |
|-------|---------|---------|
| The train | поезд | — |
| goes | едет | — |
| from | из | — |
| Kazan | Казань | — |
| to | в (направление) | — |
| Moscow | Москва | — |
| at | предлог места | ✗ дистрактор |
| in | предлог внутри | ✗ дистрактор |

*Explanation (при ошибке):* from [начало] + to [цель] — из Казани в Москву. "At" и "in" — для мест нахождения, не для описания маршрута.

---

#### AI Exercise

**ID:** basics_card24_ex1
**Title:** "to или from — куда и откуда"
**Input Mode:** FILL_BLANKS
**Words Source:** NONE
**AI Config Profile:** EXERCISE_LIGHT
**User Instruction:** "AI даст английские предложения с пропусками. Вставь to / from / или оставь пустым (если предлог не нужен)."
**Prompt Template:** "Дай 2–3 английских предложения с пропусками на to/from. Одно из пропусков должно быть пустым — например 'She is going ___ home' (предлог не нужен). Дай RU перевод рядом. Пользователь вставляет to / from / или ничего. Проверь и объясни ошибку — особо укажи на 'going home' без to и 'arrive in/at' без to.

Пример вывода AI: '1. Masha is flying ___ Moscow ___ London next week. (Маша летит ___ Москвы ___ Лондон на следующей неделе.) 2. I got a message ___ Oleg — he is going ___ home now. (Я получил сообщение ___ Олега — он сейчас идёт домой.)'"

#### Clarification Options

- Почему "going home", а не "going to home"?
- Почему "arrive in London", а не "arrive to London"?
- Разница: "to school" (иду) vs "at school" (нахожусь)?

---

### Card 25 · into / out of — и кое-что об upon

**ID:** 25 | **Order:** 2

#### Theory

Предыдущие предлоги (`to`, `from`) показывают направление к цели или источнику. Теперь — более конкретные предлоги, которые подчёркивают *пересечение границы* пространства.

---

**`into` — движение внутрь**

`into` = `in` (внутри) + движение. Ты не просто оказываешься внутри — ты *входишь*, *пересекаешь границу*.

- "She walked **into** the room." — *Она вошла в комнату.* (Движение через дверь.)
- "He jumped **into** the pool." — *Он прыгнул в бассейн.*
- "Pour the milk **into** the glass." — *Налей молоко в стакан.*

**`in` vs `into` — ключевая разница:**

- "She is **in** the room." — *Она в комнате.* (Нахождение, движения нет.)
- "She walked **into** the room." — *Она вошла в комнату.* (Движение, пересечение порога.)

Простой тест: есть глагол движения (walked, jumped, ran, poured) → скорее всего `into`. Просто нахождение → `in`.

---

**`out of` — движение наружу**

`out of` — противоположность `into`. Движение изнутри наружу, выход.

- "She walked **out of** the office." — *Она вышла из офиса.*
- "Take the phone **out of** your pocket." — *Достань телефон из кармана.*
- "He got **out of** the car." — *Он вышел из машины.*

**`out` vs `out of`:**

- "Go **out**!" — *Выйди!* (Просто наружу, без уточнения откуда именно.)
- "She walked **out of** the building." — *Она вышла из здания.* (Из конкретного замкнутого пространства.)

`out of` всегда идёт с существительным после него. `out` — без существительного.

---

**Резюме пары:**

| Движение внутрь | Движение наружу |
|-----------------|-----------------|
| **into** (She walked into the café.) | **out of** (She walked out of the café.) |

---

**⭐ Небольшое отступление: upon — и при чём тут "жили-были"**

Ты уже знаешь предлог `on`. У него есть старший брат — `upon`. Значение то же самое, но звучит гораздо торжественнее. В разговорной речи его не используют — он живёт в книгах, официальных документах и устойчивых выражениях.

**Самое знаменитое:** **"Once upon a time..."** — *Жили-были...* Дословно: "однажды, в некое время". Именно с этой фразы начинается большинство английских сказок — и так было несколько столетий подряд. Если слышишь "once upon a time" — ты в сказке.

Ещё места, где встретишь `upon`:
- "**Upon** arrival, please show your passport." — *По прибытии предъявите паспорт.* (официальный язык)
- "**Upon** reflection, I changed my mind." — *Поразмыслив, я передумал.* (книжный стиль)
- "**Upon** hearing the news, she burst into tears." — *Услышав новость, она расплакалась.* (литература)

`upon` — пассивное знание. Не нужно его использовать в речи, но нужно понимать, когда видишь. Видишь `upon` — знай: это торжественное `on`, скорее всего официальный или литературный текст.

#### Summary *(кнопка «?» в упражнениях)*

into = движение внутрь (She walked into the room). out of = движение наружу (She walked out of the room). in vs into: нахождение vs движение. upon = торжественное on в книгах и формулах; "once upon a time" = жили-были.

#### Examples

| # | RU | EN |
|---|----|----|
| 148 | Она вошла в кафе. | She walked into the café. |
| 149 | Он достал ключи из кармана. | He took the keys out of his pocket. |
| 150 | Кот прыгнул в коробку. | The cat jumped into the box. |
| 151 | Вика вышла из здания. | Vika walked out of the building. |
| 152 | Жили-были... | Once upon a time... |
| 153 | По прибытии заполните форму. | Upon arrival, please fill in the form. |

---

#### Exercises

**Ex 135 · MultipleChoice · CHOICE** *(ID: 35)*

"He ran ___ the building when he heard the alarm."

- in
- **into** ✓
- at

*Explanation (при ошибке):* Он *побежал внутрь* здания — движение через границу → into. "In" — нахождение, не движение. "In the building" = он уже там. "Into the building" = он туда входит. "At" — место присутствия, не пересечение границы.

---

**Ex 136 · MultipleChoice · CHOICE** *(ID: 36)*

"She took her phone ___ her bag."

- to
- into
- **out of** ✓

*Explanation (при ошибке):* Телефон был внутри сумки, она его достала — движение изнутри наружу → out of. "Into" — движение внутрь (противоположное). "To" — направление к цели, не выход из пространства.

---

**Ex 137 · ErrorCorrection** *(ID: 10)*

"He got out the car and walked in the office."  
*(Выбери правильный вариант)*

- **He got out of the car and walked into the office.** ✓
- He got of the car and walked to the office.
- He got out the car and walked at the office.

*Explanation (при ошибке):* "Got out of the car" — вышел из машины (out of + существительное, не просто out). "Walked into the office" — вошёл в офис (движение внутрь → into, не просто in).

---

**Ex 138 · TrueFalse** *(ID: 24)*

| # | EN | RU | Верно? |
|---|----|----|--------|
| 1 | She walked in the room. | Она вошла в комнату. | ✗ |
| 2 | He jumped into the lake. | Он прыгнул в озеро. | ✓ |
| 3 | Take the card out of your wallet. | Достань карту из кошелька. | ✓ |
| 4 | The cat ran out the house. | Кот выбежал из дома. | ✗ |
| 5 | Once upon a time, there was a dragon. | Жил-был дракон. | ✓ |

*Explanation (при ошибке):* Предл. 1: вошла в комнату — движение внутрь → "walked **into** the room". "Walked in the room" = ходила по комнате (нахождение + хождение внутри, не вход). Предл. 4: выбежал из — "ran **out of** the house" (out of + существительное).

---

**Ex 139 · Matching** *(ID: 4)*

Соедини ситуацию с правильным предлогом:

| Ситуация | Предлог |
|---------|---------|
| Наливаю кофе в чашку | into |
| Достаю деньги из кошелька | out of |
| Он вышел из лифта | out of |
| Она прыгнула в бассейн | into |

*Explanation (при ошибке):* into = движение внутрь (наливаю, прыгаю). out of = движение наружу из замкнутого пространства (достаю, вышел).

---

**Ex 140 · WordArrangement** *(ID: 24)*

Ситуация: котёнок выпрыгнул из коробки.

Правильное предложение: **The kitten jumped out of the box.**

| Слово | Перевод | Лишнее? |
|-------|---------|---------|
| The kitten | котёнок | — |
| jumped | прыгнул | — |
| out of | из (наружу) | — |
| the box | коробка | — |
| into | в (внутрь) | ✗ дистрактор |
| from | от (источник) | ✗ дистрактор |
| out | наружу | ✗ дистрактор |

*Explanation (при ошибке):* "Out of" = движение изнутри наружу из замкнутого пространства. "Into" — противоположное движение (внутрь). "From" — источник без акцента на пересечение пространства. Просто "out" — без существительного после него.

---

#### AI Exercise

**ID:** basics_card25_ex1
**Title:** "into или out of — вход и выход"
**Input Mode:** FILL_BLANKS
**Words Source:** NONE
**AI Config Profile:** EXERCISE_LIGHT
**User Instruction:** "AI даст английские предложения с пропусками. Вставь into или out of."
**Prompt Template:** "Дай 2–3 английских предложения с пропусками на into/out of, описывающих движение персонажей (входят/выходят). Дай RU перевод рядом. Пользователь вставляет предлоги. Проверь и объясни ошибку — акцент на разницу 'walked in' (ходила по помещению) vs 'walked into' (вошла) и 'out' (просто наружу) vs 'out of' (из конкретного пространства).

Пример вывода AI: '1. She walked ___ the café and sat down. (Она вошла ___ кафе и села.) 2. He took his keys ___ his pocket. (Он достал ключи ___ кармана.) 3. The dog ran ___ the house when it heard the thunder. (Собака вбежала ___ дом, когда услышала гром.)'"

#### Clarification Options

- Разница walked in и walked into — почему важен предлог?
- Почему "out of the car", а не просто "out the car"?
- "Once upon a time" — что это значит буквально?

---

### Words8r Sync · Предлоги направления

| Слово | Переводы | Транскрипция |
|-------|----------|-------------|
| to | в, к (направление движения) | [tuː] |
| from | из, от (источник, происхождение) | [frɒm] |
| into | в (движение внутрь, через границу) | [ˈɪntuː] |
| out of | из, наружу из (движение наружу) | [aʊt əv] |

---

### Card 28 · over, across, along, past, through — движение через, вдоль и мимо

**ID:** 28 | **Order:** 3

#### Theory

Пять предлогов движения — все переводятся как "через", "по" или "мимо", но каждый рисует свой маршрут. Разобрать их раз и запомнить навсегда.

---

**`over` — поверх препятствия**

`over` = маршрут идёт поверх чего-то: вверх, через верхнюю точку, вниз. Само слово не говорит что именно делаешь — это говорит глагол.

- "He climbed **over** the wall." — *Он перелез через стену.* (глагол climb = лезть, over = маршрут поверху)
- "She jumped **over** the puddle." — *Она перепрыгнула через лужу.*
- "They walked **over** a small hill." — *Они перешли через холм.* (вверх и вниз с другой стороны)
- "We drove **over** the bridge." — *Мы переехали по мосту.*

**`over` vs `above` — движение против статики:**

| | Пример | Смысл |
|---|---|---|
| `above` | The lamp is **above** the table. | Статика — лампа просто висит выше стола, никуда не движется |
| `over` | She jumped **over** the fence. | Движение — перепрыгнула через забор поверху |

`above` = просто выше. `over` = движение поверх.

---

**`across` — по поверхности с одного края на другой**

`across` = пересечь что-то вширину, двигаясь по поверхности. Ты не лезешь поверх и не идёшь насквозь — ты пересекаешь горизонтально.

- "She swam **across** the river." — *Она переплыла реку.* (с берега на берег по поверхности воды)
- "Walk **across** the road carefully." — *Переходи дорогу осторожно.*
- "He ran **across** the field." — *Он пробежал через поле.*
- "They sailed **across** the ocean." — *Они переплыли океан.*

**`across` vs `over` — главная путаница:**

Оба могут переводиться "через мост" — но смысл разный:

| | Пример | Акцент |
|---|---|---|
| `over` | walk **over** the bridge | мост как препятствие, маршрут идёт поверху |
| `across` | walk **across** the bridge | мост как поверхность, идёшь с одного конца на другой |

На практике для моста оба варианта звучат нормально. Но для реки:
- "swim **across** the river" ✓ — плыть по поверхности с берега на берег
- "swim **over** the river" ✗ — нельзя плыть поверх реки

Ключ: `over` требует чтобы маршрут шёл выше препятствия. `across` — горизонтальное пересечение по поверхности.

---

**`along` — вдоль, параллельно**

`along` — полная противоположность `across`. Вместо того чтобы пересекать — движешься параллельно чему-то.

- "We walked **along** the river." — *Мы шли вдоль реки.* (не через реку, а рядом с ней)
- "She drove **along** the coast." — *Она ехала вдоль побережья.*
- "Just go **along** this street and you'll see it." — *Иди по этой улице и увидишь.*
- "He jogged **along** the path." — *Он бежал трусцой по дорожке.*

**`along` vs `across` на примере реки:**

| | Пример | Куда движешься |
|---|---|---|
| `across` | swim **across** the river | поперёк реки — с берега на берег |
| `along` | walk **along** the river | вдоль берега — параллельно реке |

---

**`past` — движение мимо, не останавливаясь**

`past` = проходишь рядом с чем-то и продолжаешь дальше. Не заходишь внутрь, не пересекаешь — просто мимо.

- "Walk **past** the post office and turn left." — *Пройди мимо почты и поверни налево.*
- "She drove **past** my house." — *Она проехала мимо моего дома.*
- "He walked **past** me without saying hello." — *Он прошёл мимо не поздоровавшись.*

---

**`through` — насквозь через внутреннее пространство**

`through` = ты внутри чего-то пока движешься. Лес, туннель, толпа, город — ты проходишь через внутренность.

- "We drove **through** the tunnel." — *Мы проехали через туннель.*
- "She walked **through** the forest." — *Она шла через лес.*
- "He pushed **through** the crowd." — *Он протиснулся сквозь толпу.*
- "Light comes **through** the window." — *Свет проходит сквозь окно.*

---

**Все пять рядом — итоговая таблица:**

| Предлог | Образ движения | Пример |
|---------|---------------|--------|
| `over` | поверх препятствия — вверх и вниз | climb over a wall |
| `across` | по поверхности — с одного края на другой | swim across the river |
| `along` | вдоль — параллельно чему-то | walk along the river |
| `past` | мимо — не заходя внутрь | walk past the shop |
| `through` | насквозь — через внутреннее пространство | drive through the tunnel |

**Тест-картинка: как пересечь реку?**
- `over the river` — перепрыгнуть / перелететь поверху (мост как препятствие)
- `across the river` — переплыть / переехать с берега на берег (по поверхности)
- `along the river` — идти вдоль берега (параллельно реке, не пересекать)
- `through the river` — идти вброд насквозь (через воду)

#### Summary *(кнопка «?» в упражнениях)*

over = поверх препятствия (climb over a wall). across = по поверхности с одного края на другой (swim across the river). along = вдоль, параллельно (walk along the river). past = мимо не заходя (walk past the shop). through = насквозь через внутренность (drive through the tunnel). over ≠ above: above — статика, over — движение. across ≠ along: across — поперёк, along — вдоль.

#### Examples

| # | RU | EN |
|---|----|----|
| 167 | Они перешли через мост. | They walked over the bridge. |
| 168 | Он перелез через забор. | He climbed over the fence. |
| 169 | Она переплыла реку. | She swam across the river. |
| 170 | Осторожно переходи дорогу. | Walk across the road carefully. |
| 171 | Мы шли вдоль побережья. | We walked along the coast. |
| 172 | Просто иди по этой улице. | Just go along this street. |
| 173 | Пройди мимо банка и поверни направо. | Walk past the bank and turn right. |
| 174 | Она проехала мимо моего дома. | She drove past my house. |
| 175 | Мы ехали через туннель. | We drove through the tunnel. |
| 176 | Он протиснулся сквозь толпу. | He pushed through the crowd. |

---

#### Exercises

**Ex 141 · MultipleChoice · FORWARD_CHOICE** *(ID: 18)*

"Ей пришлось перелезть через забор."

- She had to climb **over** the fence. ✓
- She had to climb **across** the fence.
- She had to climb **through** the fence.

*Explanation: over = поверх препятствия (перелезть, прыгнуть). across = по поверхности горизонтально. through = насквозь (не то для забора).*

---

**Ex 142 · MultipleChoice · FORWARD_CHOICE** *(ID: 19)*

"Они переплыли реку с одного берега на другой."

- They swam **across** the river. ✓
- They swam **over** the river.
- They swam **along** the river.

*Explanation: across = по поверхности с берега на берег. over = поверху (нельзя плыть над рекой). along = вдоль берега, а не через реку.*

---

**Ex 143 · MultipleChoice · FORWARD_CHOICE** *(ID: 20)*

"После работы Дима шёл вдоль реки."

- Dima walked **along** the river after work. ✓
- Dima walked **across** the river after work.
- Dima walked **through** the river after work.

*Explanation: along = вдоль, параллельно реке. across = пересечь реку (с берега на берег). through = вброд насквозь.*

---

**Ex 144 · MultipleChoice · FORWARD_CHOICE** *(ID: 21)*

"Просто пройди мимо кофейни — нам туда не нужно."

- Just walk **past** the café. ✓
- Just walk **along** the café.
- Just walk **through** the café.

*Explanation: past = пройти мимо не заходя. along = вдоль (не то значение). through = насквозь внутри (войти и выйти).*

---

**Ex 145 · MultipleChoice · FORWARD_CHOICE** *(ID: 22)*

"Мы проехали через весь город."

- We drove **through** the city. ✓
- We drove **across** the city.
- We drove **along** the city.

*Explanation: through = насквозь через внутренность (город, лес, туннель). across тоже возможно, но through точнее когда находишься внутри городского пространства.*

---

**Ex 146 · MultipleChoice · FORWARD_CHOICE** *(ID: 23)*

"Она ехала вдоль побережья."

- She drove **along** the coast. ✓
- She drove **across** the coast.
- She drove **past** the coast.

*Explanation: along = вдоль, параллельно побережью. across = поперёк (пересечь). past = проехать мимо одной точки, не вдоль всего побережья.*

---

**Ex 147 · MultipleChoice · FORWARD_CHOICE** *(ID: 24)*

"Свет проходил сквозь окно."

- Light came **through** the window. ✓
- Light came **across** the window.
- Light came **over** the window.

*Explanation: through = насквозь (свет, воздух, звук проходят через материал). across = по поверхности. over = поверху.*

---

**Ex 148 · WordArrangement** *(ID: 25)*

Ситуация: Рома и Лена шли домой вдоль реки через парк.

Правильное предложение: **Roma and Lena walked along the river through the park.**

| Слово | Перевод | Лишнее? |
|-------|---------|---------|
| Roma and Lena | Рома и Лена | — |
| walked | шли | — |
| along | вдоль | — |
| the river | река | — |
| through | через (насквозь) | — |
| the park | парк | — |
| across | поперёк | ✗ дистрактор |
| past | мимо | ✗ дистрактор |

*Explanation (при ошибке):* along the river = вдоль реки (параллельно, не пересекая). through the park = насквозь через парк. across = поперёк (пересечь реку), не то. past = мимо одной точки, не вдоль всего маршрута.

---

#### AI Exercise

**ID:** basics_card28_ex1
**Title:** "over, across, along, past или through?"
**Input Mode:** FILL_BLANKS
**Words Source:** NONE
**AI Config Profile:** EXERCISE_LIGHT
**User Instruction:** "AI даст предложения с пропусками. Вставь нужный предлог движения: over / across / along / past / through."
**Prompt Template:** "Дай 4 английских предложения с пропусками на предлоги движения (over/across/along/past/through). Используй 4 разных предлога. Дай RU перевод рядом. Проверь и объясни ошибку с акцентом на разницу: over = поверх препятствия, across = по поверхности поперёк, along = вдоль параллельно, past = мимо не заходя, through = насквозь.

Пример вывода AI: '1. She climbed ___ the wall to get into the garden. (Она перелезла ___ стену, чтобы попасть в сад.) 2. We drove ___ a long tunnel before reaching the city. (Мы ехали ___ длинный туннель, прежде чем добраться до города.) 3. He jogged ___ the beach every morning. (Он бегал ___ пляжу каждое утро.) 4. They walked ___ the bakery without stopping. (Они прошли ___ булочной не останавливаясь.)'"

#### Clarification Options

- Почему "swim across the river", а не "swim over the river"?
- Разница along и across на примере реки?
- Можно ли сказать и "over the bridge" и "across the bridge"?

---

### Words8r Sync · Предлоги движения: через, вдоль, мимо

| Слово | Переводы | Транскрипция |
|-------|----------|-------------|
| over | через (поверх), поверх препятствия | [ˈoʊvər] |
| across | через (поперёк), на другую сторону | [əˈkrɒs] |
| along | вдоль, по | [əˈlɒŋ] |
| past | мимо | [pæst] |
| through | через (насквозь), сквозь | [θruː] |

---
---

# БЛОК 5 · Микротемы 13–15

---

## Microtopic 13 — Plural of Nouns · Множественное число

**ID:** 13 | **Order:** 13

---

### Card 29 · Правила образования множественного числа

**ID:** 29 | **Order:** 1

#### Theory

В русском слова меняются постоянно: стол — стола — столу — столом. Английский куда скромнее — большинство существительных просто добавляют **-s** в конце. Но в деталях есть несколько нюансов.

---

**Правило 1: Большинство существительных → +s**

Просто добавить -s. Это работает для большинства слов.

- cat → **cats** *(кошки)*
- phone → **phones** *(телефоны)*
- table → **tables** *(столы)*
- book → **books** *(книги)*

---

**Правило 2: Слова на -s, -sh, -ch, -x, -o → +es**

Добавляем -es, потому что иначе окончание трудно произнести.

- bus → **buses** *(автобусы)*
- dish → **dishes** *(тарелки)*
- watch → **watches** *(часы)*
- box → **boxes** *(коробки)*
- tomato → **tomatoes** *(помидоры)*

**Но!** Слова-заимствования на -o чаще просто +s:
photo → **photos**, piano → **pianos**, video → **videos**

---

**Правило 3: Слова на -y после согласной → -ies**

- city → **cities** *(города)*
- party → **parties** *(вечеринки)*
- country → **countries** *(страны)*

**Но!** Если перед -y стоит **гласная** — просто +s:
- day → **days** *(дни)*
- key → **keys** *(ключи)*
- toy → **toys** *(игрушки)*

---

**Правило 4: Слова на -f / -fe → -ves**

- leaf → **leaves** *(листья)*
- knife → **knives** *(ножи)*
- wife → **wives** *(жёны)*
- life → **lives** *(жизни)*

**Исключения** (просто +s): roof → **roofs**, chief → **chiefs**, belief → **beliefs**

---

**Сводная таблица:**

| Правило | Пример | Мн. число |
|---------|--------|-----------|
| +s (стандарт) | cat, book | cats, books |
| +es (-s/-sh/-ch/-x/-o) | bus, watch, box | buses, watches, boxes |
| -y → -ies (согл. + y) | city, party | cities, parties |
| -y → +s (гласн. + y) | day, key | days, keys |
| -f/-fe → -ves | leaf, knife | leaves, knives |

#### Summary *(кнопка «?» в упражнениях)*

Большинство слов → +s. После -s/-sh/-ch/-x/-o → +es. -y после согласной → -ies (city→cities), после гласной просто +s (day→days). -f/-fe → -ves (leaf→leaves), кроме roof/chief/belief.

#### Examples

| # | RU | EN |
|---|----|----|
| 177 | В парке много деревьев. | There are many trees in the park. |
| 178 | Вера купила три коробки яблок. | Vera bought three boxes of apples. |
| 179 | Дима потерял свои ключи. | Dima lost his keys. |
| 180 | В этом городе много старых церквей. | There are many old churches in this city. |
| 181 | Осенью листья желтеют. | In autumn the leaves turn yellow. |
| 182 | У Коли два ножа на кухне. | Kolya has two knives in the kitchen. |

---

#### Exercises

**Ex 162 · TableFill** *(ID: 5)*

Задание: запиши правильную форму множественного числа

| Единственное | Правило | Множественное |
|---|---|---|
| bus | -es | ___ |
| city | -y → -ies | ___ |
| leaf | -f → -ves | ___ |
| photo | +s | ___ |
| watch | -es | ___ |
| key | +s | ___ |

*Answers:* buses / cities / leaves / photos / watches / keys

*Explanation:* bus → buses (-s+es), city → cities (согл.+y → ies), leaf → leaves (-f → ves), photo → photos (заимствование, +s), watch → watches (-ch+es), key → keys (гласн.+y, +s).

---

**Ex 163 · MultipleChoice · CHOICE** *(ID: 37)*

"There are three ___ on the shelf." *(на полке три коробки)*

- boxs
- **boxes** ✓
- boxies

*Explanation (при ошибке):* box оканчивается на -x → +es = boxes.

---

**Ex 164 · MultipleChoice · CHOICE** *(ID: 38)*

"She has two ___ in her garden." *(у неё в саду два...)*

- babys
- babyes
- **babies** ✓

*Explanation (при ошибке):* baby оканчивается на согласную + -y → y→i + es = babies.

---

**Ex 165 · MultipleChoice · CHOICE** *(ID: 39)*

"The ___ are playing in the park." *(... играют в парке)*

- childs
- **children** ✓
- childrens

*Explanation (при ошибке):* child → children — исключение, правило +s не работает. Подробнее — следующая карточка.

---

**Ex 166 · TextInput** *(ID: 14)*

Напиши правильную форму множественного числа:

1. one knife → two ___ *(ножа)* → **knives**
2. one party → three ___ *(вечеринки)* → **parties**
3. one roof → five ___ *(крыш)* → **roofs**
4. one dish → four ___ *(тарелки)* → **dishes**
5. one day → seven ___ *(дней)* → **days**

*Explanation (при ошибке):* knife → knives (-fe → ves). party → parties (согл.+y → ies). roof → roofs (исключение из правила -f). dish → dishes (-sh + es). day → days (гласн.+y → просто +s).

---

**Ex 167 · MultipleChoice · FORWARD_CHOICE** *(ID: 25)*

"Коля купил два билета."

- Kolya bought two ticket.
- **Kolya bought two tickets.** ✓
- Kolya bought two ticketes.

*Explanation (при ошибке):* ticket → tickets, стандартное +s.

---

**Ex 168 · MultipleChoice · FORWARD_CHOICE** *(ID: 26)*

"На столе три ножа."

- There are three knifes on the table.
- **There are three knives on the table.** ✓
- There are three knife on the table.

*Explanation (при ошибке):* knife → knives (-fe → ves).

---

**Ex 169 · MultipleChoice · FORWARD_CHOICE** *(ID: 27)*

"В этом районе много заводов."

- There are many factorys in this area.
- **There are many factories in this area.** ✓
- There are many factoris in this area.

*Explanation (при ошибке):* factory (согл.+y) → factories.

---

#### AI Exercise

**ID:** basics_card29_ex1
**Title:** "Правильно ли мн.ч.?"
**Input Mode:** FREE_WRITE
**Words Source:** NONE
**AI Config Profile:** EXERCISE_LIGHT
**User Instruction:** "AI даст 5 слов в единственном числе. Напиши каждое во множественном числе. Если ошибёшься — AI объяснит правило."
**Prompt Template:** "Дай пользователю 5 английских существительных в единственном числе — по одному на строке с русским переводом. Выбери слова разных типов: 1) стандартное +s, 2) слово на -ch/-sh/-x/-o (+es), 3) слово на -y после согласной (-ies), 4) слово на -f/-fe (-ves), 5) слово-заимствование на -o (+s). Пользователь пишет форму мн.ч. для каждого. Проверь каждый ответ. При ошибке объясни правило кратко по-русски.

Пример вывода AI: '1. box (коробка) → ? 2. party (вечеринка) → ? 3. knife (нож) → ? 4. photo (фото) → ? 5. city (город) → ?'"

#### Clarification Options

- Почему photo → photos, а tomato → tomatoes?
- Как понять что перед -y стоит согласная, а не гласная?
- Есть ли правило для слов на -o или это нужно просто помнить?

---

### Card 30 · Неправильные формы и особые случаи

**ID:** 30 | **Order:** 2

#### Theory

Правила из предыдущей карточки покрывают 95% слов. Остальные 5% — исключения которые нужно просто запомнить. Их немного, но они очень частые.

---

**Исключения — уникальные формы множественного числа**

| Ед. число | Мн. число | Перевод |
|-----------|-----------|---------|
| man | **men** | мужчина → мужчины |
| woman | **women** | женщина → женщины |
| child | **children** | ребёнок → дети |
| tooth | **teeth** | зуб → зубы |
| foot | **feet** | ступня → ступни |
| goose | **geese** | гусь → гуси |
| mouse | **mice** | мышь → мыши |
| person | **people** | человек → люди |

**Главные ловушки:**
"two mans" ✗ → "two **men**" ✓
"three childs" ✗ → "three **children**" ✓
"several persons" — формально правильно, но звучит юридически; в разговоре всегда "people"

---

**Неизменяемые — одинаково в единственном и множественном числе**

- one fish → three **fish** *(три рыбы)*
- one sheep → many **sheep** *(много овец)*
- one deer → five **deer** *(пять оленей)*
- one series → two **series** *(две серии)*

"I caught three **fish**." ✓ — три рыбы, не "three fishes".
*(fishes используют только когда говорят о разных **видах** рыб: "The ocean has many fishes" = много видов)*

---

**Только во множественном числе**

Некоторые предметы существуют только во мн.ч. Для одного предмета — "a pair of":

| Слово | Перевод | Как сказать "одни" |
|-------|---------|-------------------|
| scissors | ножницы | a pair of scissors |
| trousers | брюки | a pair of trousers |
| jeans | джинсы | a pair of jeans |
| glasses | очки | a pair of glasses |
| headphones | наушники | a pair of headphones |

"Where are my **glasses**?" ✓ — даже когда речь об одной паре, глагол всегда во мн.ч.

#### Summary *(кнопка «?» в упражнениях)*

Неправильные: man→men, woman→women, child→children, tooth→teeth, foot→feet, mouse→mice, person→people. Неизменяемые: fish, sheep, deer — форма одна. Только мн.ч.: scissors, jeans, glasses — для одного предмета говорят "a pair of".

#### Examples

| # | RU | EN |
|---|----|----|
| 183 | Дети бегали по всему двору. | The children were running all around the yard. |
| 184 | У Ромы болят зубы. | Roma's teeth hurt. |
| 185 | В поле паслось стадо овец. | A flock of sheep was grazing in the field. |
| 186 | Игорь не может найти свои очки. | Igor can't find his glasses. |
| 187 | В Москве живут миллионы людей. | Millions of people live in Moscow. |

---

#### Exercises

**Ex 170 · Matching** *(ID: 5)*

Задание: соедини каждое слово с правильной формой множественного числа.

| Ед. число | Мн. число |
|-----------|-----------|
| child | men |
| man | mice |
| mouse | geese |
| goose | feet |
| foot | children |

*Explanation:* child→children, man→men, mouse→mice, goose→geese, foot→feet — все исключения, не подчиняются стандартным правилам.

---

**Ex 171 · Categorization** *(ID: 4)*

Задание: перетащи слова в нужную колонку по типу мн.ч.

**Слова:** fish, book, sheep, city, glasses, knife, jeans, dog, series, piano

| Стандартное мн.ч. (+s/+es/-ies/-ves) | Неизменяемое (ед.=мн.) | Только мн.ч. |
|---------------------------------------|------------------------|--------------|
| books, cities, knives, dogs, pianos | fish, sheep, series | glasses, jeans |

*Explanation:* piano → pianos (заимствование, +s). series не изменяется. glasses и jeans не имеют формы ед.ч.

---

**Ex 172 · FindTheOdd** *(ID: 4)*

Три слова — правильные формы мн.ч., одно — с ошибкой:

- cats
- buses
- **leafs** ✓
- parties

*Explanation (при ошибке):* leafs — ошибка: leaf → leaves (-f → -ves). Остальные правильны: cats (+s), buses (-s+es), parties (согл.+y → ies).

---

**Ex 173 · FindTheOdd** *(ID: 5)*

Три слова — правильные формы мн.ч., одно — с ошибкой:

- photos
- pianos
- **tomatos** ✓
- videos

*Explanation (при ошибке):* tomatos — ошибка: tomato → tomatoes (-o+es, не заимствование). Остальные — слова-заимствования на -o, берут просто +s: photos, pianos, videos.

---

**Ex 174 · ErrorCorrection** *(ID: 11)*

В предложении есть ошибка. Выбери правильный вариант:

"There are five childs in the park."

- There are five childs in the park.
- **There are five children in the park.** ✓
- There are five childrens in the park.

*Explanation (при ошибке):* child → children — неправильное мн.ч., правило +s не работает. Никакого -s или -ren добавить нельзя, форма уникальная.

---

**Ex 175 · ErrorCorrection** *(ID: 12)*

В предложении есть ошибка. Выбери правильный вариант:

"I need a new scissor for the project."

- I need a new scissor for the project.
- **I need a new pair of scissors for the project.** ✓
- I need new scissors for the project.

*Explanation (при ошибке):* scissors существует только во мн.ч. — "a scissor" невозможно. Для одного предмета — "a pair of scissors".

---

#### AI Exercise

**ID:** basics_card30_ex1
**Title:** "Ед. число или мн.?"
**Input Mode:** FREE_WRITE
**Words Source:** NONE
**AI Config Profile:** EXERCISE_LIGHT
**User Instruction:** "AI покажет предложения с существительными в скобках. Напиши правильную форму множественного числа для каждого."
**Prompt Template:** "Дай 4 коротких английских предложения с пропуском. В каждом — существительное в скобках в форме ед.ч., пользователь должен написать его мн.ч. Включи: 1) исключение (man/child/tooth/foot/mouse), 2) неизменяемое (fish/sheep/deer), 3) только-мн.ч. слово — попроси написать фразу 'a pair of ___', 4) стандартное слово. Рядом с каждым — русский перевод контекста. Проверь, при ошибке объясни по-русски.

Пример вывода AI: '1. I saw three (woman) at the café. (Я видел трёх женщин.) 2. We caught five (fish) yesterday. (Вчера мы поймали пять рыб.) 3. Как сказать «одна пара джинс»? 4. She found two (key) on the floor. (Она нашла два ключа.)'"

#### Clarification Options

- Почему scissors/jeans/glasses всегда во мн.ч.?
- Когда можно сказать "fishes" вместо "fish"?
- Почему person → people, а не persons?

---

### Words8r Sync · Множественное число: особые случаи

| Слово | Переводы | Транскрипция |
|-------|----------|-------------|
| man / men | мужчина / мужчины | [mæn] / [men] |
| woman / women | женщина / женщины | [ˈwʊmən] / [ˈwɪmɪn] |
| child / children | ребёнок / дети | [tʃaɪld] / [ˈtʃɪldrən] |
| tooth / teeth | зуб / зубы | [tuːθ] / [tiːθ] |
| foot / feet | ступня / ступни | [fʊt] / [fiːt] |
| mouse / mice | мышь / мыши | [maʊs] / [maɪs] |
| scissors | ножницы | [ˈsɪzəz] |
| jeans | джинсы | [dʒiːnz] |
| glasses | очки | [ˈɡlɑːsɪz] |

---
---

## Microtopic 14 — Numbers · Числа

**ID:** 14 | **Order:** 14

---

### Card 31 · Cardinal Numbers · Кардинальные числа

**ID:** 31 | **Order:** 1

#### Theory

Кардинальные числа — это обычные числа которыми мы считаем: один, два, три... В английском их нужно знать, потому что они используются буквально везде.

---

**1–12: уникальные слова, нужно запомнить**

| 1 | 2 | 3 | 4 | 5 | 6 |
|---|---|---|---|---|---|
| one | two | three | four | five | six |

| 7 | 8 | 9 | 10 | 11 | 12 |
|---|---|---|---|---|---|
| seven | eight | nine | ten | eleven | twelve |

---

**13–19: корень числа + -teen**

thirteen (13), fourteen (14), **fifteen** (15), sixteen (16), seventeen (17), **eighteen** (18), **nineteen** (19)

Ловушка: fifteen (не fiveteen), eighteen (не eightyteen), nineteen (не ninteen).

---

**20–90: десятки**

twenty (20), thirty (30), **forty** (40), fifty (50), sixty (60), seventy (70), eighty (80), ninety (90)

Ловушка: **forty** — не "fourty". Одна из самых частых орфографических ошибок в английском.

---

**21–99: десятки + единицы через дефис**

twenty-**one** (21), thirty-**five** (35), forty-**eight** (48), ninety-**nine** (99)

Дефис обязателен: "twenty one" без дефиса — ошибка в письме.

---

**Сотни, тысячи, миллионы**

- 100 = a hundred / one hundred
- 1,000 = a thousand / one thousand
- 1,000,000 = a million
- 1,000,000,000 = a billion

**Важно:** hundred / thousand / million — без -s когда идут с числом!
"two **hundred**" ✓ — не "two hundreds" ✗
"five **thousand**" ✓ — не "five thousands" ✗

Но: "**hundreds** of people" (сотни людей, без точного числа) — тогда -s нормально.

---

**Большие числа**

1,234 = "one thousand two hundred and thirty-four" *(British: "and" перед последними двумя разрядами)*
2,500 = "two thousand five hundred"
15,000 = "fifteen thousand"
250,000 = "two hundred and fifty thousand"

#### Summary *(кнопка «?» в упражнениях)*

1–12: уникальные. 13–19: корень + -teen (fifteen, eighteen — особое написание). 20–90: twenty/thirty/forty (не fourty!). 21–99: дефис (twenty-one). hundred/thousand/million без -s после числа.

#### Examples

| # | RU | EN |
|---|----|----|
| 188 | Ей сорок два года. | She is forty-two years old. |
| 189 | В городе живут три миллиона человек. | Three million people live in the city. |
| 190 | У Кати пятнадцать книг на полке. | Katya has fifteen books on the shelf. |
| 191 | Билет стоит девятнадцать фунтов. | The ticket costs nineteen pounds. |
| 192 | В нашей группе двадцать один студент. | There are twenty-one students in our group. |
| 193 | Он выиграл сто тысяч рублей. | He won a hundred thousand roubles. |
| 194 | На стадионе было восемьдесят тысяч зрителей. | There were eighty thousand spectators at the stadium. |

---

#### Exercises

**Ex 176 · MultipleChoice · FORWARD_CHOICE** *(ID: 28)*

"У Миши сорок пять книг."

- Misha has fourty-five books.
- **Misha has forty-five books.** ✓
- Misha has forty five books.

*Explanation (при ошибке):* forty — не fourty (частая ошибка!). Дефис обязателен между десятком и единицей: forty-five.

---

**Ex 177 · MultipleChoice · FORWARD_CHOICE** *(ID: 29)*

"В зале было двести человек."

- There were two hundreds people in the hall.
- **There were two hundred people in the hall.** ✓
- There were two-hundred people in the hall.

*Explanation (при ошибке):* После числа hundred без -s. Дефис между числом и hundred не нужен.

---

**Ex 178 · MultipleChoice · FORWARD_CHOICE** *(ID: 30)*

"Поезд прибывает через пятнадцать минут."

- The train arrives in fiveteen minutes.
- **The train arrives in fifteen minutes.** ✓
- The train arrives in fifthteen minutes.

*Explanation (при ошибке):* fifteen — особое написание, не fiveteen и не fifthteen.

---

**Ex 179 · TextInput** *(ID: 15)*

Напиши число словами:

1. 13 → **thirteen**
2. 40 → **forty**
3. 17 → **seventeen**
4. 56 → **fifty-six**
5. 100 → **a hundred** / **one hundred**
6. 1,000 → **a thousand** / **one thousand**

*Explanation (при ошибке):* 40 = forty (не fourty). 56 = fifty-six (дефис). 13 = thirteen (не thirtheen).

---

**Ex 180 · MultipleChoice · REVERSE_CHOICE** *(ID: 8)*

Переведи на русский:
"forty-eight"

- 84
- **48** ✓
- 14

*Explanation (при ошибке):* forty = 40, eight = 8. forty-eight = 48.

---

**Ex 181 · MultipleChoice · REVERSE_CHOICE** *(ID: 9)*

Переведи на русский:
"fifteen hundred"

- **1,500** ✓
- 150
- 15,000

*Explanation (при ошибке):* "fifteen hundred" = 15 × 100 = 1,500. Разговорный способ назвать числа от 1,100 до 1,900 — два блока по сотне.

---

**Ex 182 · MultipleChoice · REVERSE_CHOICE** *(ID: 10)*

Переведи на русский:
"two million three hundred thousand"

- 2,030,000
- **2,300,000** ✓
- 23,000,000

*Explanation (при ошибке):* two million = 2,000,000. three hundred thousand = 300,000. Итого: 2,300,000.

---

**Ex 183 · TrueFalse** *(ID: 25)*

| # | EN | RU | Верно? |
|---|----|----|--------|
| 1 | "Forty" is the correct spelling, not "fourty". | "Forty" — правильное написание, не "fourty". | ✓ |
| 2 | After a number, "hundred" doesn't get -s. | После числа к "hundred" не добавляют -s. | ✓ |
| 3 | The number 21 is written "twenty one" without a hyphen. | Число 21 пишется "twenty one" без дефиса. | ✗ |
| 4 | "Fifteen" comes from "five" with the suffix -teen. | "Fifteen" образовано от "five" + суффикс -teen. | ✓ |
| 5 | "Hundreds of people" with -s is correct English. | "Hundreds of people" с -s — правильный английский. | ✓ |

*Explanation (при ошибке):* Предл. 3 — ошибка: дефис обязателен: twenty-one. Предл. 1 — forty без u, это частая ошибка. Предл. 5 — без конкретного числа перед hundred/thousand/million -s допустимо: "hundreds of people".

---

#### AI Exercise

**ID:** basics_card31_ex1
**Title:** "Числа в контексте"
**Input Mode:** FREE_WRITE
**Words Source:** NONE
**AI Config Profile:** EXERCISE_LIGHT
**User Instruction:** "AI даст 4 задания: запиши число словами по-английски. Числа будут в контексте предложений."
**Prompt Template:** "Дай 4 коротких предложения по-русски. В каждом — число цифрами. Пользователь должен написать число словами по-английски и вписать его в предложение уже по-английски. Включи: двузначное число с дефисом, число с -teen, слово forty (частая ошибка), большое число 100–1000. Проверь ответы. При ошибке с forty — особо подчеркни что там нет 'u'.

Пример вывода AI: '1. У неё 18 кошек. → She has ___ cats. 2. В классе 40 парт. → There are ___ desks. 3. Ему 27 лет. → He is ___ years old. 4. В библиотеке 500 книг. → There are ___ books.'"

#### Clarification Options

- Почему forty, а не fourty?
- Когда можно сказать "fifteen hundred" вместо "one thousand five hundred"?
- Как читать числа типа 1,234,567?

---

### Card 32 · Ordinal Numbers · Порядковые числа

**ID:** 32 | **Order:** 2

#### Theory

Порядковые числа отвечают на вопрос "который по счёту?": первый, второй, третий... Они нужны для этажей, дат, мест в соревновании, веков.

---

**Первые три — исключения, запомнить:**

1st = **first**, 2nd = **second**, 3rd = **third**

---

**Остальные: кардинальное число + -th**

4th = fourth, 5th = **fifth**\*, 6th = sixth, 7th = seventh, 8th = **eighth**\*, 9th = **ninth**\*, 10th = tenth

\* Особое написание: **fifth** (не fiveth), **eighth** (не eighthth), **ninth** (не nineth). Одна буква меняется или убирается.

11th = eleventh, 12th = **twelfth**\*, 13th = thirteenth...
\* twelfth — запомнить написание (twelve → twelfth, v исчезает)

---

**Десятки: -y → -ieth**

20th = twen**tieth**, 30th = thir**tieth**, 40th = for**tieth**, 50th = fif**tieth**

---

**Составные порядковые: дефис, последнее слово в порядковой форме**

21st = twenty-**first**, 22nd = twenty-**second**, 23rd = twenty-**third**, 24th = twenty-**fourth**, 35th = thirty-**fifth**

---

**Суффиксы в записи цифрами:**

Суффикс берётся от самого слова:
- first → **1st**, second → **2nd**, third → **3rd**, fourth → **4th**
- Правило для составных: смотрим на последнее слово: 21st (first), 22nd (second), 33rd (third), 44th (fourth)

---

**Где используются:**

- **Этажи:** the 3rd floor *(третий этаж)*, the 1st floor *(первый этаж)*
- **Даты:** on the 5th of May, on June 21st
- **Места:** she finished 2nd *(она финишировала второй)*
- **Века:** the 21st century *(двадцать первый век)*

#### Summary *(кнопка «?» в упражнениях)*

1st/2nd/3rd — исключения. Остальные: +th (fifth/eighth/ninth — особое написание). -y → -ieth (twentieth). Суффикс в записи: -st/-nd/-rd/-th берётся от последнего слова. Используются для этажей, дат, мест, веков.

#### Examples

| # | RU | EN |
|---|----|----|
| 195 | Офис находится на пятом этаже. | The office is on the fifth floor. |
| 196 | Её день рождения — двадцать первого июня. | Her birthday is on the twenty-first of June. |
| 197 | Тёма пришёл к финишу вторым. | Tyoma finished second. |
| 198 | Мы живём в двадцать первом веке. | We live in the twenty-first century. |
| 199 | Это мой второй визит в Лондон. | This is my second visit to London. |

---

#### Exercises

**Ex 184 · TableFill** *(ID: 6)*

Задание: запиши порядковое числительное для каждого кардинального

| Кардинальное | Порядковое |
|---|---|
| one | ___ |
| two | ___ |
| three | ___ |
| five | ___ |
| eight | ___ |
| twelve | ___ |
| twenty | ___ |
| twenty-three | ___ |

*Answers:* first / second / third / fifth / eighth / twelfth / twentieth / twenty-third

---

**Ex 185 · MultipleChoice · CHOICE** *(ID: 40)*

"She lives on the ___ floor." *(на третьем этаже)*

- three
- **third** ✓
- thirteenth

*Explanation (при ошибке):* этажи — порядковые числительные: the third floor. three — кардинальное, не подходит.

---

**Ex 186 · MultipleChoice · CHOICE** *(ID: 41)*

"It's his ___ birthday today." *(сегодня ему исполняется тридцать лет)*

- thirty
- thirtyth
- **thirtieth** ✓

*Explanation (при ошибке):* thirty → thirtieth (-y → -ieth). thirtyth — несуществующая форма.

---

**Ex 187 · MultipleChoice · CHOICE** *(ID: 42)*

"She finished ___ in the race." *(она финишировала второй)*

- **second** ✓
- two
- secondly

*Explanation (при ошибке):* места в соревновании — порядковые числительные. secondly — наречие, не число.

---

**Ex 188 · MultipleChoice · FORWARD_CHOICE** *(ID: 31)*

"Его кабинет на двенадцатом этаже."

- His office is on the twelve floor.
- His office is on the twelvth floor.
- **His office is on the twelfth floor.** ✓

*Explanation (при ошибке):* twelve → twelfth — особое написание: v исчезает, добавляется -fth.

---

**Ex 189 · MultipleChoice · FORWARD_CHOICE** *(ID: 32)*

"Это был её двадцать первый день рождения."

- It was her twenty-oneth birthday.
- **It was her twenty-first birthday.** ✓
- It was her twentyfirst birthday.

*Explanation (при ошибке):* twenty-first — дефис обязателен. Суффикс -st берётся от "first", не от "one".

---

**Ex 190 · WordArrangement** *(ID: 26)*

Ситуация: Саша занял третье место на соревновании.

Правильное предложение: **Sasha finished in third place.**

| Слово | Перевод | Лишнее? |
|-------|---------|---------|
| Sasha | Саша | — |
| finished | закончил | — |
| in | — | — |
| third | третьем | — |
| place | месте | — |
| three | три | ✗ дистрактор |
| the | — | ✗ дистрактор |

*Explanation (при ошибке):* места в соревновании — "finished in third place" или просто "finished third". Артикль здесь не нужен. three — кардинальное, здесь нужно порядковое third.

---

#### AI Exercise

**ID:** basics_card32_ex1
**Title:** "Порядковые в контексте"
**Input Mode:** FILL_BLANKS
**Words Source:** NONE
**AI Config Profile:** EXERCISE_LIGHT
**User Instruction:** "AI даст предложения с пропусками. Вставь правильное порядковое числительное."
**Prompt Template:** "Дай 4 английских предложения с пропусками [___]. В каждом пропуске — порядковое числительное (напиши его словом или цифрой+суффикс). Дай русский перевод рядом. Включи: исключение (1st/2nd/3rd), пятое/восьмое (особое написание), двадцатое или двадцать первое, двенадцатое. Проверь ответы. При ошибке подчеркни правило написания.

Пример вывода AI: '1. She won [___] prize. (Она выиграла первый приз.) 2. Turn left on the [___] street. (Повернй налево на пятой улице.) 3. His [___] birthday is in March. (Его двенадцатый день рождения в марте.) 4. We celebrate our [___] anniversary this year. (В этом году мы отмечаем наш двадцатый юбилей.)'"

#### Clarification Options

- Почему fifth, а не fiveth?
- Как правильно писать даты по-английски?
- Чем отличается "the first floor" в UK и в US?

---

### Card 33 · Как читать годы

**ID:** 33 | **Order:** 3

#### Theory

Годы читаются по особым правилам. Это одна из частых ловушек для русскоязычных — потому что в русском мы просто говорим "тысяча девятьсот девяносто пять", а в английском другая логика.

---

**До 2000 года: делим на два блока по две цифры**

Разбиваем год пополам и читаем каждую половину как обычное число:

- **1985** → 19 + 85 → "**nineteen eighty-five**"
- **1812** → 18 + 12 → "**eighteen twelve**"
- **1776** → 17 + 76 → "**seventeen seventy-six**"
- **1900** → 19 + 00 → "**nineteen hundred**" *(особый случай: ровный год)*
- **1904** → 19 + 04 → "**nineteen oh four**" *(ноль = "oh" в разговоре)*

---

**2000 год: особый случай**

- **2000** = "**two thousand**"

---

**2001–2009: два варианта**

- **2005** = "**two thousand and five**" *(брит.)* / "**two thousand five**" *(амер.)*
- **2001** = "**two thousand and one**" / "**two thousand one**"

---

**2010 и далее: снова два варианта, оба правильны**

- **2010** = "**twenty ten**" ✓ или "**two thousand and ten**" ✓
- **2024** = "**twenty twenty-four**" ✓ или "**two thousand and twenty-four**" ✓

В разговоре чаще говорят "twenty twenty-four" — короче и привычнее.

---

**Шпаргалка:**

| Год | Читается |
|-----|---------|
| 1984 | nineteen eighty-four |
| 1900 | nineteen hundred |
| 1907 | nineteen oh seven |
| 2000 | two thousand |
| 2003 | two thousand and three |
| 2024 | twenty twenty-four |

#### Summary *(кнопка «?» в упражнениях)*

До 2000: делим 19|84 = "nineteen eighty-four". 2000 = "two thousand". 2001–2009: "two thousand and five". 2010+: "twenty ten" или "two thousand and ten" — оба верны.

#### Examples

| # | RU | EN |
|---|----|----|
| 200 | Игорь родился в 1991 году. | Igor was born in nineteen ninety-one. |
| 201 | Компания основана в 2003 году. | The company was founded in two thousand and three. |
| 202 | Сейчас 2024 год. | It's twenty twenty-four. |
| 203 | Первая мировая война началась в 1914 году. | The First World War began in nineteen fourteen. |
| 204 | Олимпиада прошла в 2000 году в Сиднее. | The Olympics took place in two thousand in Sydney. |

---

#### Exercises

**Ex 191 · TextInput** *(ID: 16)*

Напиши год словами по-английски:

1. 1999 → **nineteen ninety-nine**
2. 2000 → **two thousand**
3. 1812 → **eighteen twelve**
4. 2024 → **twenty twenty-four** / two thousand and twenty-four
5. 1905 → **nineteen oh five**

*Explanation (при ошибке):* 1999: nineteen + ninety-nine (две половины). 1905: ноль читается "oh". 2024: допустимы оба варианта.

---

**Ex 192 · MultipleChoice · REVERSE_CHOICE** *(ID: 11)*

Переведи на русский:
"nineteen eighty-four"

- 1948
- **1984** ✓
- 1894

*Explanation (при ошибке):* nineteen = 19, eighty-four = 84. Два блока: 19|84 = 1984.

---

**Ex 193 · MultipleChoice · REVERSE_CHOICE** *(ID: 12)*

Переведи на русский:
"twenty oh eight"

- 2080
- 2800
- **2008** ✓

*Explanation (при ошибке):* "oh" = ноль. twenty oh eight = 20|08 = 2008.

---

**Ex 194 · MultipleChoice · REVERSE_CHOICE** *(ID: 13)*

Переведи на русский:
"two thousand and twelve"

- **2012** ✓
- 2120
- 2102

*Explanation (при ошибке):* two thousand = 2000, and twelve = 12. Итого: 2012.

---

**Ex 195 · ErrorCorrection** *(ID: 13)*

В предложении есть ошибка. Выбери правильный вариант:

"She was born in one thousand nine hundred ninety-five."

- She was born in one thousand nine hundred ninety-five.
- **She was born in nineteen ninety-five.** ✓
- She was born in nineteen hundred ninety-five.

*Explanation (при ошибке):* Годы до 2000 читаются как два блока: 19|95 = "nineteen ninety-five". Формат "one thousand nine hundred..." грамматически не ошибка, но так не говорят.

---

**Ex 196 · ErrorCorrection** *(ID: 14)*

В предложении есть ошибка. Выбери правильный вариант:

"The game came out in twenty hundred and three."

- The game came out in twenty hundred and three.
- **The game came out in two thousand and three.** ✓
- The game came out in twenty and three.

*Explanation (при ошибке):* 2003 = "two thousand and three". "Twenty hundred" — несуществующий формат для годов после 2000.

---

#### AI Exercise

**ID:** basics_card33_ex1
**Title:** "Как читается этот год?"
**Input Mode:** FREE_WRITE
**Words Source:** NONE
**AI Config Profile:** EXERCISE_LIGHT
**User Instruction:** "AI даст 4 года цифрами. Напиши как они читаются по-английски словами."
**Prompt Template:** "Дай пользователю 4 года цифрами с небольшим контекстом по-русски. Включи: год из 1900-х (стандартный), год с нулём в середине (1907 / 1904), год 2000-х до 2010, год 2010+. Пользователь пишет как читается каждый год по-английски. Проверь. При ошибке объясни правило с примером.

Пример вывода AI: '1. Чемпионат мира: 1966. 2. Катастрофа произошла в: 1906. 3. Фильм вышел в: 2007. 4. Сейчас идёт: 2024.'"

#### Clarification Options

- Как читать 2000-е годы (2001, 2005)?
- Почему нельзя сказать "one thousand nine hundred eighty"?
- Как читать годы типа 1900 или 1800?

---

### Card 34 · one more · Ещё один

**ID:** 34 | **Order:** 4

#### Theory

**one more** + существительное = ещё один / ещё одна / ещё раз

Это короткая конструкция, которую носители используют постоянно — и которую легко пропустить мимо.

---

**Примеры:**

- "**One more** time!" — Ещё раз!
- "Can I have **one more** coffee?" — Можно мне ещё один кофе?
- "Give me **one more** chance." — Дай мне ещё один шанс.
- "Just **one more** question." — Только ещё один вопрос.
- "**One more** piece, please." — Ещё один кусочек, пожалуйста.

---

**one more vs another**

Оба переводятся как "ещё один", но акцент разный:

- **one more** — следующий из той же серии. Как будто ты продолжаешь счёт. "One more minute" = ещё одна минута из тех же минут.
- **another** — просто ещё один, другой. "Another chance" = другой шанс (не обязательно из той же серии).

В разговоре их часто используют взаимозаменяемо — разница скорее в оттенке, чем в правиле.

#### Summary *(кнопка «?» в упражнениях)*

one more + noun = ещё один. "One more time" — ещё раз. Разница с another: one more = следующий из тех же, another = просто ещё один. В разговоре часто взаимозаменяемы.

#### Examples

| # | RU | EN |
|---|----|----|
| 205 | Ещё один вопрос — и мы закончим. | One more question and we're done. |
| 206 | Дай мне ещё одну минуту. | Give me one more minute. |
| 207 | Можно мне ещё один кусок торта? | Can I have one more piece of cake? |

---

#### Exercises

**Ex 197 · ConstructionMeaning** *(ID: 2)*

Выбери правильный перевод:

"One more try."

- **Ещё одна попытка.** ✓
- Первая попытка.
- Последняя попытка.
- Одна попытка.

*Explanation (при ошибке):* one more = ещё один/одна (следующий из той же серии). "One more try" = ещё одна попытка.

---

**Ex 198 · ConstructionMeaning** *(ID: 3)*

Выбери правильный перевод:

"Just one more minute."

- Только одна минута.
- Ещё целая минута.
- **Всего ещё одна минута.** ✓
- Последняя минута.

*Explanation (при ошибке):* just смягчает — "буквально ещё одна минута". one more = ещё одна из той же серии, а не просто "одна".

---

**Ex 199 · MultipleChoice · FORWARD_CHOICE** *(ID: 33)*

"Оля попросила официанта ещё один кофе."

- Olya asked the waiter for one more coffees.
- **Olya asked the waiter for one more coffee.** ✓
- Olya asked the waiter for one another coffee.

*Explanation (при ошибке):* one more + noun (ед.ч.). После "one more" существительное не меняется: one more coffee, не coffees. "One another" — несуществующая конструкция.

---

**Ex 200 · MultipleChoice · FORWARD_CHOICE** *(ID: 34)*

"Повтори ещё раз, пожалуйста!"

- Repeat one time more, please!
- **Repeat one more time, please!** ✓
- Repeat one more again, please!

*Explanation (при ошибке):* "one more time" — устойчивая фраза. Порядок: one more + noun. "One more again" — дублирование (again уже значит "снова"). "One time more" — неправильный порядок слов.

---

#### Clarification Options

- В чём реальная разница между one more и another?
- Можно ли сказать "one more of" + существительное?
- Как сказать "ещё немного"?

---

### Words8r Sync · Числа

| Слово | Переводы | Транскрипция |
|-------|----------|-------------|
| eleven | одиннадцать | [ɪˈlevən] |
| twelve | двенадцать | [twelv] |
| fifteen | пятнадцать | [ˌfɪfˈtiːn] |
| eighteen | восемнадцать | [ˌeɪˈtiːn] |
| forty | сорок | [ˈfɔːti] |
| fifty | пятьдесят | [ˈfɪfti] |
| eighty | восемьдесят | [ˈeɪti] |
| hundred | сто | [ˈhʌndrəd] |
| thousand | тысяча | [ˈθaʊzənd] |
| million | миллион | [ˈmɪljən] |
| first | первый | [fɜːst] |
| second | второй | [ˈsekənd] |
| third | третий | [θɜːd] |
| fifth | пятый | [fɪfθ] |
| eighth | восьмой | [eɪtθ] |
| twelfth | двенадцатый | [twelfθ] |
| twentieth | двадцатый | [ˈtwentiəθ] |

---
---

## Microtopic 15 — The Calendar · Календарь

**ID:** 15 | **Order:** 15

---

### Card 35 · Заглавные буквы: дни, месяцы, сезоны

**ID:** 35 | **Order:** 1

#### Theory

В русском языке дни недели и месяцы пишутся со строчной буквы: понедельник, январь, осень.

В английском — другие правила. Важно понять не просто "так надо", а почему.

---

**Дни недели — ЗАГЛАВНАЯ буква**

Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday

---

**Месяцы — ЗАГЛАВНАЯ буква**

January, February, March, April, May, June, July, August, September, October, November, December

---

**Сезоны — строчная буква** ⚠️

spring, summer, autumn, winter

Это исключение которое путает всех русских.

---

**Почему такая разница?**

В английском есть понятие "proper noun" — собственное имя. Это уникальные имена конкретных вещей: имена людей (Anna, Igor), названия городов (London, Moscow), названия стран (Russia, France).

Дни недели и месяцы считаются собственными именами — у каждого есть своё уникальное "имя": Monday — это конкретный день, как имя человека. January — конкретный месяц с именем.

Сезоны — нет. Spring — это описательное слово, оно описывает тип времени года, а не называет конкретную уникальную вещь. Поэтому строчная.

Аналогия: мы пишем "река" (common noun — строчная) но "река Нева" или просто "Нева" (proper noun — заглавная). Так же: "season" строчная, но "Monday" — это как имя, заглавная.

---

**Ловушка:** в названиях событий сезон может стать собственным именем:
"The Summer Olympics", "The Spring Festival" — здесь Summer и Spring как часть официального названия → заглавная. Это исключение из исключения.

---

**Сокращения тоже с заглавной:**

Mon, Tue, Wed, Thu, Fri, Sat, Sun
Jan, Feb, Mar, Apr, May, Jun, Jul, Aug, Sep, Oct, Nov, Dec

#### Summary *(кнопка «?» в упражнениях)*

Дни недели и месяцы — заглавная (Monday, January): они собственные имена. Сезоны — строчная (spring, winter): описательные слова. Сокращения тоже с заглавной: Mon, Jan.

#### Examples

| # | RU | EN |
|---|----|----|
| 208 | Встреча в понедельник. | The meeting is on **M**onday. |
| 209 | Я родилась в январе. | I was born in **J**anuary. |
| 210 | Летом жарко. | It's hot in **s**ummer. |
| 211 | Весной цветут деревья. | Trees blossom in **s**pring. |

---

#### Clarification Options

- Почему сезоны пишутся со строчной, а дни и месяцы — с заглавной?
- Всегда ли Summer/Winter пишутся со строчной?
- Правило заглавной распространяется на сокращения (Mon, Jan)?

---

### Card 36 · Days of the Week · Дни недели

**ID:** 36 | **Order:** 2

#### Theory

**Monday** (пн) · **Tuesday** (вт) · **Wednesday** (ср) · **Thursday** (чт) · **Friday** (пт) · **Saturday** (сб) · **Sunday** (вс)

---

**Помогалки для запоминания:**

- **Monday** — Moon day *(день Луны)*
- **Saturday** — Saturn's day *(день Сатурна)*
- **Wednesday** — произносится "**WENZday**" [ˈwenzdeɪ], буква d в середине не читается! Wed-nes-day → [ˈwenzdeɪ]. Это одна из самых частых ошибок произношения.

---

**Структура недели**

- **Weekdays** — рабочие дни: Monday to Friday
- **Weekend** — выходные: Saturday + Sunday

В США неделя в большинстве календарей начинается с **Sunday**. В UK и большинстве стран — с **Monday**.

---

**Предлоги с днями недели**

- **on** + день: "I have a meeting **on** Monday." *(в понедельник)*
- **on** + день + **s** = каждый такой день: "I don't work **on** Mondays." *(по понедельникам)*
- **at the weekend** (брит.) / **on the weekend** (амер.)

**Ловушка:** "in Monday", "in Friday" — ошибка. Только **on**!

По-русски мы говорим "в понедельник" — но это не "in". В английском день = конкретная дата на календаре → **on**.

---

**Полезные фразы:**

- "What day is it today?" — Какой сегодня день?
- "It's Monday." — Сегодня понедельник.
- "See you on Friday!" — Увидимся в пятницу!
- "I'll do it first thing Monday morning." — Сделаю первым делом в понедельник утром.

#### Summary *(кнопка «?» в упражнениях)*

Monday–Sunday, все с заглавной. Wednesday произносится [ˈwenzdeɪ], d не читается. on + день (on Monday). on + день + s = каждую неделю (on Mondays). at the weekend (брит.) / on the weekend (амер.).

#### Examples

| # | RU | EN |
|---|----|----|
| 212 | В пятницу у Лены важное собеседование. | Lena has an important interview on Friday. |
| 213 | По средам я хожу в спортзал. | I go to the gym on Wednesdays. |
| 214 | В эти выходные мы едем на дачу. | We're going to the dacha this weekend. |
| 215 | Что ты делал в воскресенье? | What did you do on Sunday? |
| 216 | В будни он встаёт в семь утра. | On weekdays he gets up at seven. |
| 217 | Магазин закрыт по субботам. | The shop is closed on Saturdays. |

---

#### Exercises

**Ex 201 · Matching** *(ID: 6)*

Задание: соедини каждый день с русским переводом.

| EN | RU |
|----|----|
| Monday | суббота |
| Wednesday | пятница |
| Friday | среда |
| Saturday | понедельник |
| Sunday | воскресенье |

*Answers:* Monday→понедельник, Wednesday→среда, Friday→пятница, Saturday→суббота, Sunday→воскресенье

---

**Ex 202 · MultipleChoice · FORWARD_CHOICE** *(ID: 35)*

"Встреча состоится в четверг."

- The meeting will take place in Thursday.
- **The meeting will take place on Thursday.** ✓
- The meeting will take place at Thursday.

*Explanation (при ошибке):* день недели → предлог on. По-русски "в четверг" — но в английском это on, не in.

---

**Ex 203 · MultipleChoice · FORWARD_CHOICE** *(ID: 36)*

"По понедельникам я работаю из дома."

- **I work from home on Mondays.** ✓
- I work from home in Mondays.
- I work from home at Monday.

*Explanation (при ошибке):* "каждый понедельник" = on Mondays (с -s). Предлог on. in и at с днями недели — ошибка.

---

**Ex 204 · MultipleChoice · FORWARD_CHOICE** *(ID: 37)*

"Что ты делаешь в эти выходные?"

- **What are you doing at the weekend?** ✓
- What are you doing in the weekend?
- What are you doing on a weekend?

*Explanation (при ошибке):* "at the weekend" — устойчивая британская фраза. "on the weekend" тоже корректно в американском. "in the weekend" — ошибка.

---

**Ex 205 · TextInput** *(ID: 17)*

Вставь правильный предлог (on / at):

1. "I'll see you ___ Friday." → **on**
2. "She goes to the market ___ Sundays." → **on**
3. "What do you usually do ___ the weekend?" → **at**
4. "The game is ___ Saturday evening." → **on**
5. "He never works ___ weekends." → **on**

*Explanation (при ошибке):* Дни недели и даты — всегда on. "At the weekend" — устойчивое выражение с at (брит.).

---

**Ex 206 · Transformation** *(ID: 3)*

Задание: сделай отрицание (Present Simple)

1. "She works on Saturdays." → **"She doesn't work on Saturdays."**
2. "They have classes on Wednesdays." → **"They don't have classes on Wednesdays."**
3. "He visits his parents on Sundays." → **"He doesn't visit his parents on Sundays."**

*Explanation (при ошибке):* Present Simple отрицание = don't / doesn't + V1 (инфинитив без to). She/he/it → doesn't. I/you/we/they → don't. Глагол возвращается в базовую форму: works → doesn't work.

---

**Ex 207 · FindTheOdd** *(ID: 6)*

Три слова написаны правильно, одно — с ошибкой (строчная вместо заглавной):

- Monday
- **tuesday** ✓
- Wednesday
- Thursday

*Explanation (при ошибке):* Все дни недели пишутся с заглавной буквы. tuesday → Tuesday.

---

**Ex 208 · FindTheOdd** *(ID: 7)*

Три слова написаны правильно, одно — с опечаткой:

- **Wensday** ✓
- Tuesday
- Friday
- Sunday

*Explanation (при ошибке):* Wednesday — не Wensday. Буква d в середине не читается, но пишется: Wed-nes-day.

---

#### AI Exercise

**ID:** basics_card36_ex1
**Title:** "Дни недели в контексте"
**Input Mode:** FILL_BLANKS
**Words Source:** NONE
**AI Config Profile:** EXERCISE_LIGHT
**User Instruction:** "AI даст предложения с пропусками. Вставь правильный предлог (on/at) или день недели."
**Prompt Template:** "Дай 4 английских предложения с пропусками [___]. Чередуй задания: вставить предлог перед днём, вставить сам день по контексту, вставить 'at the weekend' vs 'on weekdays'. Дай русский перевод рядом. Проверь ответы. При ошибке объясни кратко по-русски.

Пример вывода AI: '1. I have a dentist appointment [___] Tuesday. (У меня приём у зубного во вторник.) 2. She visits her parents every [___]. (Она навещает родителей каждое воскресенье.) 3. Do you usually work [___] the weekend? (Ты обычно работаешь в выходные?) 4. The gym is closed [___] Sundays. (Спортзал закрыт по воскресеньям.)'"

#### Clarification Options

- Почему "on Monday" а не "in Monday"?
- Как правильно: "at the weekend" или "on the weekend"?
- Как сказать "каждый вторник" по-английски?

---

### Card 37 · Months · Месяцы

**ID:** 37 | **Order:** 3

#### Theory

**January** (янв) · **February** (фев) · **March** (мар) · **April** (апр) · **May** (май) · **June** (июн)
**July** (июл) · **August** (авг) · **September** (сен) · **October** (окт) · **November** (ноя) · **December** (дек)

---

**Предлог с месяцами — всегда in**

- "in **January**" — в январе
- "in **July**" — в июле
- "in **December**" — в декабре

**Ловушка:** "on January" без числа — ошибка. Месяц как период → **in**.

С числом:
- "**on** the 5th of January" — 5 января *(брит. порядок: число + of + месяц)*
- "**on** January 5th" — 5 января *(амер. порядок: месяц + число)*

Оба варианта правильны. Запятая после года в американском стиле: "January 5th, 2024".

---

**Произношение-ловушки:**

- **February** [ˈfebrʊəri] — первое "r" часто проглатывается в разговоре, звучит как "Febyuary". Это нормально — так говорят многие носители.
- **August** [ˈɔːɡəst] — ударение на первый слог: **AU**-gust, не au-**GUST**.
- **Wednesday** — уже знаешь *(из карточки про дни)*

---

**Сокращения:**

Jan · Feb · Mar · Apr · May · Jun · Jul · Aug · Sep/Sept · Oct · Nov · Dec

---

**В каком месяце какой сезон** *(северное полушарие)*:

| Сезон | Месяцы |
|-------|--------|
| Spring (весна) | March, April, May |
| Summer (лето) | June, July, August |
| Autumn/Fall (осень) | September, October, November |
| Winter (зима) | December, January, February |

#### Summary *(кнопка «?» в упражнениях)*

January–December, все с заглавной. Предлог: in + месяц (in March). С числом: on the 5th of March или on March 5th. February произносится часто как "Febyuary". August — ударение на первый слог.

#### Examples

| # | RU | EN |
|---|----|----|
| 218 | Её день рождения в мае. | Her birthday is in May. |
| 219 | Экзамены начинаются в июне. | Exams start in June. |
| 220 | Концерт прошёл третьего октября. | The concert was on the 3rd of October. |
| 221 | Саша приедет в декабре. | Sasha is coming in December. |
| 222 | В феврале мало дней. | February has few days. |
| 223 | Они поженились в августе 2019 года. | They got married in August 2019. |

---

#### Exercises

**Ex 209 · Categorization** *(ID: 5)*

Распредели месяцы по временам года:

**Месяцы:** January, March, June, September, December, April, July, October, February, May, August, November

| Spring | Summer | Autumn | Winter |
|---|---|---|---|
| March, April, May | June, July, August | September, October, November | December, January, February |

---

**Ex 210 · TextInput** *(ID: 18)*

Вставь правильный месяц:

1. Первый месяц года → **January**
2. Самый короткий месяц → **February**
3. Последний месяц года → **December**
4. Месяц после July → **August**
5. Месяц перед June → **May**

---

**Ex 211 · MultipleChoice · CHOICE** *(ID: 43)*

"I was born ___ October." *(в октябре)*

- at
- on
- **in** ✓

*Explanation (при ошибке):* месяц как период времени → in. "In October" = в октябре.

---

**Ex 212 · MultipleChoice · CHOICE** *(ID: 44)*

"The festival is ___ the 3rd of September." *(третьего сентября)*

- in
- **on** ✓
- at

*Explanation (при ошибке):* конкретная дата → on. "On the 3rd of September". Когда есть число — только on.

---

**Ex 213 · MultipleChoice · CHOICE** *(ID: 45)*

"We're going on holiday ___ August." *(в августе)*

- **in** ✓
- on
- at

*Explanation (при ошибке):* месяц без конкретного числа → in. "In August".

---

**Ex 214 · WordArrangement** *(ID: 27)*

Ситуация: День рождения Иры — третье апреля.

Правильное предложение: **Ira's birthday is on the 3rd of April.**

| Слово | Перевод | Лишнее? |
|-------|---------|---------|
| Ira's birthday | день рождения Иры | — |
| is | — | — |
| on | — | — |
| the | — | — |
| 3rd | третьего | — |
| of April | апреля | — |
| in | — | ✗ дистрактор |
| at | — | ✗ дистрактор |

*Explanation (при ошибке):* конкретная дата → on. "On the 3rd of April" — стандартная британская форма записи даты.

---

#### AI Exercise

**ID:** basics_card37_ex1
**Title:** "Месяцы и предлоги"
**Input Mode:** FILL_BLANKS
**Words Source:** NONE
**AI Config Profile:** EXERCISE_LIGHT
**User Instruction:** "AI даст предложения с пропусками. Вставь правильный предлог или месяц."
**Prompt Template:** "Дай 4 английских предложения с пропусками [___]. Включи: 1) пропуск на предлог перед месяцем (in/on), 2) пропуск на название месяца по описанию или порядку, 3) пропуск на предлог перед датой с числом, 4) пропуск на название месяца по сезону. Дай русский перевод рядом. Проверь. При ошибке объясни.

Пример вывода AI: '1. School starts [___] September. (Школа начинается в сентябре.) 2. The third month of the year is [___]. (Третий месяц года — ...) 3. Her wedding was [___] the 14th of June. (Её свадьба была четырнадцатого июня.) 4. Which month is the last month of summer? [___] (Какой месяц — последний летний?)'"

#### Clarification Options

- Почему "in January" но "on the 5th of January"?
- Как правильно писать дату по-английски: британский и американский варианты?
- Почему February часто произносят без первого r?

---

### Card 38 · Seasons · Времена года

**ID:** 38 | **Order:** 4

#### Theory

**spring** (весна) · **summer** (лето) · **autumn** / **fall** (осень) · **winter** (зима)

Все четыре — **строчными буквами** (помни из Card 35).

---

**Autumn или Fall?**

Оба слова означают осень — это просто разные диалекты:
- **autumn** — британский английский
- **fall** — американский английский

Оба абсолютно правильны. Если смотришь американское кино или сериалы — услышишь fall. Британские книги и BBC — autumn.

---

**Предлог с временами года — всегда in**

- "in **spring**" — весной
- "in **summer**" — летом
- "in **autumn**" / "in **fall**" — осенью
- "in **winter**" — зимой

**Ловушка:** "on summer", "on spring" — ошибка.
**Ловушка 2:** "in Summer" с заглавной — ошибка (если это не название события).

В американском английском часто добавляют артикль: "in **the** spring", "in **the** fall" — тоже правильно.

---

**Полезные фразы:**

- "What's your favourite season?" — Какое у тебя любимое время года?
- "I love summer because..." — Я люблю лето, потому что...
- "It gets cold in autumn." — Осенью становится холодно.
- "Spring is in the air." — Весной пахнет. *(идиома: "весна в воздухе")*

---

**Сезоны и месяцы — напоминание:**

| Сезон | Месяцы |
|-------|--------|
| spring | March · April · May |
| summer | June · July · August |
| autumn / fall | September · October · November |
| winter | December · January · February |

#### Summary *(кнопка «?» в упражнениях)*

spring / summer / autumn / fall / winter — строчными. Autumn (брит.) = fall (амер.). Предлог: in + сезон (in spring). В американском: in the spring тоже нормально.

#### Examples

| # | RU | EN |
|---|----|----|
| 224 | Я обожаю осень за её цвета. | I love autumn for its colours. |
| 225 | Летом мы ездим на море. | In summer we go to the seaside. |
| 226 | Зимой в Москве очень холодно. | It's very cold in Moscow in winter. |
| 227 | Весной день становится длиннее. | The days get longer in spring. |
| 228 | Какое у тебя любимое время года? | What's your favourite season? |

---

#### Exercises

**Ex 215 · ErrorCorrection** *(ID: 15)*

В предложении есть ошибка. Выбери правильный вариант:

"I love Summer — it's my favourite season."

- I love Summer — it's my favourite season.
- **I love summer — it's my favourite season.** ✓
- I love a summer — it's my favourite season.

*Explanation (при ошибке):* Сезоны пишутся со строчной буквы: summer, not Summer (если это не название мероприятия типа "The Summer Olympics").

---

**Ex 216 · ErrorCorrection** *(ID: 16)*

В предложении есть ошибка. Выбери правильный вариант:

"We always go skiing on winter."

- We always go skiing on winter.
- **We always go skiing in winter.** ✓
- We always go skiing at winter.

*Explanation (при ошибке):* Времена года → предлог in. "In winter" — единственный правильный вариант.

---

**Ex 217 · ErrorCorrection** *(ID: 17)*

В предложении есть ошибка. Выбери правильный вариант:

"At autumn the leaves turn red."

- At autumn the leaves turn red.
- On autumn the leaves turn red.
- **In autumn the leaves turn red.** ✓

*Explanation (при ошибке):* in autumn — единственный правильный предлог для сезонов. at и on с сезонами не используются.

---

**Ex 218 · MultipleChoice · FORWARD_CHOICE** *(ID: 38)*

"Весной часто идут дожди."

- It often rains on spring.
- **It often rains in spring.** ✓
- It often rains at spring.

*Explanation (при ошибке):* сезон → предлог in.

---

**Ex 219 · MultipleChoice · FORWARD_CHOICE** *(ID: 39)*

"Осенью листья падают с деревьев."

- In Autumn the leaves fall from the trees.
- **In autumn the leaves fall from the trees.** ✓
- On autumn the leaves fall from the trees.

*Explanation (при ошибке):* autumn — строчная (не Autumn, кроме названий событий). Предлог in.

---

**Ex 220 · MultipleChoice · FORWARD_CHOICE** *(ID: 40)*

"Летом солнце садится поздно."

- **In summer the sun sets late.** ✓
- In Summer the sun sets late.
- On summer the sun sets late.

*Explanation (при ошибке):* summer — строчная. Предлог in.

---

**Ex 221 · TrueFalse** *(ID: 26)*

| # | Утверждение | Верно? |
|---|---|---|
| 1 | Все четыре сезона пишутся со строчной буквы. | ✓ |
| 2 | Autumn — американский вариант, fall — британский. | ✗ |
| 3 | Предлог с временами года — всегда "in". | ✓ |
| 4 | "In the summer" — ошибка, артикль не нужен. | ✗ |
| 5 | Spring начинается в марте (северное полушарие). | ✓ |

*Explanation (при ошибке):* 2) наоборот: autumn = брит., fall = амер. 4) "in the summer" — нейтральный американский вариант, ошибкой не является.

---

**Ex 222 · DialogRestore** *(ID: 5)*

Диалог с пропуском — выбери правильную реплику:

> — What time of year do you prefer?
> — ___
> — Really? I prefer winter. I love snow.

Варианты:
- a) "I like in summer. It's warm." ✗
- b) "I prefer summer. It's warm and sunny." ✓
- c) "I like the Summer better." ✗

*Explanation:* b — правильно: prefer + существительное (prefer summer), строчная буква. a — ошибка: "like in summer" — лишний "in". c — ошибка: заглавная Summer.

---

#### AI Exercise

**ID:** basics_card38_ex1
**Title:** "Любимый сезон"
**Input Mode:** FREE_WRITE
**Words Source:** NONE
**AI Config Profile:** EXERCISE_LIGHT
**User Instruction:** "AI задаст вопросы о сезонах. Отвечай по-английски. AI проверит правильность предлогов и заглавных букв."
**Prompt Template:** "Задай пользователю 3 вопроса о временах года по-английски (с русским переводом). Вопросы должны требовать ответа с предлогом in + сезон, названиями месяцев и правильным регистром букв. После ответа проверь: 1) правильный предлог (in spring, не on spring), 2) строчная буква у сезонов, 3) осмысленность ответа. При ошибке исправь и объясни по-русски.

Пример вывода AI: '1. What is your favourite season and why? (Какое твоё любимое время года и почему?) 2. What do you usually do in winter? (Что ты обычно делаешь зимой?) 3. What happens to nature in spring? (Что происходит с природой весной?)'"

#### Clarification Options

- Почему предлог "in" а не "on" перед сезоном?
- В каких странах осень называют "fall"?
- Можно ли сказать "in the winter" с артиклем?

---

### Words8r Sync · Календарь

| Слово | Переводы | Транскрипция |
|-------|----------|-------------|
| Monday | понедельник | [ˈmʌndeɪ] |
| Tuesday | вторник | [ˈtjuːzdeɪ] |
| Wednesday | среда | [ˈwenzdeɪ] |
| Thursday | четверг | [ˈθɜːzdeɪ] |
| Friday | пятница | [ˈfraɪdeɪ] |
| Saturday | суббота | [ˈsætədeɪ] |
| Sunday | воскресенье | [ˈsʌndeɪ] |
| January | январь | [ˈdʒænjʊəri] |
| February | февраль | [ˈfebrʊəri] |
| March | март | [mɑːtʃ] |
| April | апрель | [ˈeɪprəl] |
| June | июнь | [dʒuːn] |
| July | июль | [dʒʊˈlaɪ] |
| August | август | [ˈɔːɡəst] |
| September | сентябрь | [sepˈtembə] |
| October | октябрь | [ɒkˈtoʊbə] |
| November | ноябрь | [noʊˈvembə] |
| December | декабрь | [dɪˈsembə] |
| spring | весна | [sprɪŋ] |
| summer | лето | [ˈsʌmə] |
| autumn | осень | [ˈɔːtəm] |
| fall | осень (амер.) | [fɔːl] |
| winter | зима | [ˈwɪntə] |
| weekend | выходные | [ˌwiːkˈend] |
| weekday | будний день | [ˈwiːkdeɪ] |

---
---

## Счётчики IDs для продолжения

> Следующий блок начинает IDs с этих значений:

| Тип | Последний использованный ID | Следующий блок начинает с |
|-----|-----------------------------|--------------------------|
| Microtopic | 15 | 16 |
| Card | 38 | 39 |
| Example | 228 | 229 |
| Exercise | 222 | 223 |

---



**Таблица счётчиков** (актуально после микротем 13–15):

| Тип | Последний ID | Следующий |
|-----|-------------|-----------|
| WordArrangement | 27 | 28 |
| MultipleChoice | 45 | 46 |
| TrueFalse | 26 | 27 |
| ErrorCorrection | 17 | 18 |
| TextInput | 18 | 19 |
| Matching | 6 | 7 |
| Transformation | 3 | 4 |
| Categorization | 5 | 6 |
| TableFill | 6 | 7 |
| FindTheOdd | 7 | 8 |
| FORWARD_CHOICE | 40 | 41 |
| ReverseChoice | 13 | 14 |
| ConstructionMeaning | 3 | 4 |
| DialogRestore | 5 | 6 |
| AiExercise | 36 штук в файле | ID строковый: basics_card{N}_ex1 |
| Microtopic | 15 | 16 |
| Card | 38 | 39 |
| Example | 228 | 229 |
