# Фаза 4 — Сервер (Ktor)

> Полный план серверной части. Общий контекст — в [grammar8r_plan.md](../../grammar8r_plan.md) и [subscription.md](../../subscription.md).

---

## Стек и хостинг

- **Язык/фреймворк:** Kotlin + Ktor
- **БД:** PostgreSQL (или SQLite для старта)
- **Деплой:** Docker (~50–80 MB образ)
- **Хостинг:** Hetzner (Финляндия / Германия) ~€3.5/мес
- **Почему зарубежный:** OpenAI блокирует российские IP — сервер должен быть за пределами РФ
- **YooKassa:** принимает вебхуки на любой URL, зарубежный сервер не проблема

---

## Эндпоинты

### Авторизация

```
POST /auth/register
  Body: { email, password }
  → { uid, accessToken, refreshToken }
  Логика: проверить email уникальность, bcrypt пароль, создать uid, выдать оба токена

POST /auth/login
  Body: { email, password }
  → { uid, accessToken, refreshToken }
  Логика: найти по email, проверить bcrypt, выдать оба токена

POST /auth/yandex
  Body: { yandexToken }
  → { uid, accessToken, refreshToken }
  Логика: верифицировать токен у Яндекса, получить yandex_uid,
          найти или создать пользователя, выдать оба токена

POST /auth/refresh
  Body: { refreshToken }
  → { accessToken, refreshToken }
  Логика: проверить refreshToken в БД → если валиден:
          1. Прочитать актуальный tier из таблицы subscriptions
          2. Выдать новый accessToken (с актуальным tier внутри)
          3. Выдать новый refreshToken (ротация — старый инвалидируется)
          Если невалиден → 401
```

**Access token** — живёт **1 час**. Содержит: `{ uid, tier, exp }`. Сервер верифицирует подпись без запроса к БД — tier читается прямо из токена.

**Refresh token** — живёт **30 дней**. Хранится в таблице `refresh_tokens` в БД. Используется только для получения нового access token. При каждом /auth/refresh — ротация: старый удаляется, выдаётся новый.

**Итог:** tier актуален с погрешностью до 1 часа (время жизни access token). Это приемлемо — при покупке подписки клиент сразу вызывает /auth/refresh чтобы получить токен с новым tier.

### Подписка

```
GET /subscription
  Headers: Authorization: Bearer <jwt>
  → {
      tier: "free" | "tier1" | "tier2" | "tester",
      expires: "2026-05-01" | null,
      aiRequestsToday: 2,
      aiDailyLimit: 3,
      microtopicsToday: 1,
      microtopicsDailyLimit: 5 | null,   ← null = безлимит
      streakCurrent: 14,
      streakBonusRequests: 1
    }
  Лимиты берутся из таблицы subscription_limits по tier пользователя (кэш TTL ~5 мин).

GET /tier-limits
  Публичный, без авторизации.
  → {
      free:  { aiPerDay: 3,  microtopicsPerDay: 3,  wordsInPrompt: 50  },
      tier1: { aiPerDay: 30, microtopicsPerDay: null, wordsInPrompt: 100 },
      tier2: { aiPerDay: 50, microtopicsPerDay: null, wordsInPrompt: 150 }
    }
  Значения берутся из subscription_limits (кэш TTL ~5 мин). Тир tester не включается.
  Используется на маркетинговом экране подписки — показывает актуальные лимиты без авторизации.

```

### Оплата и промокоды

```
POST /promo/validate
  Headers: Authorization: Bearer <jwt>
  Body: { code: "ABCD-1234" }
  → { valid: true, discountPercent: 25, finalPrice: { tier1: 75, tier2: 134 } }
  | { valid: false, reason: "not_found" | "expired" | "used_up" }
  Логика: найти промокод → проверить expires_at и used_count < max_uses → вернуть скидку.
  Промокод не списывается здесь — только при успешной оплате через вебхук.

POST /payment/create
  Headers: Authorization: Bearer <jwt>
  Body: { tier: "tier1" | "tier2", promoCode?: "ABCD-1234" }
  → { paymentUrl: "https://yookassa.ru/..." }
  Логика:
    1. Если promoCode передан — проверить валидность (те же проверки что в /promo/validate)
    2. Рассчитать итоговую цену со скидкой (если есть промокод)
    3. Создать платёж в YooKassa через их API: сумма, описание, metadata: { uid, tier, promoCode? }
    4. Вернуть клиенту URL страницы оплаты — клиент открывает WebView/браузер
  ⚠️ Цену устанавливает только сервер — клиент не может передать сумму.

POST /webhook/yookassa
  ← Вебхук от YooKassa об успешной оплате
  Логика: верифицировать подпись YooKassa → из metadata достать uid, tier, promoCode?
          → обновить tier и expires в subscriptions
          → если promoCode есть → инкрементировать promo_codes.used_count
          → клиент узнаёт о смене тира при следующем /auth/refresh
```

### AI-прокси

```
POST /ai/exercise/generate
  Headers: Authorization: Bearer <jwt>
         | X-Device-Id: <deviceId>   ← если не залогинен, tier = Free принудительно
  Body: { exerciseId, words[], cardTheory? }
  ← exerciseId = AiExercise.id — сервер находит промт в ai_exercise_prompts
  ← words[] — клиент собирает сам по WordSource из wordsSource упражнения
  ← cardTheory — theorySummary карточки, подставляется в {{theorySummary}}
  → { taskText }              ← сгенерированное задание, клиент показывает пользователю
  | { error: "limit_exceeded" | "ai_error" | "prompt_not_found" }
  ⚠️ Лимит daily_ai_requests списывается здесь — на генерации, не на проверке.

POST /ai/exercise/evaluate
  Headers: Authorization: Bearer <jwt> | X-Device-Id: <deviceId>
  Body: { exerciseId, taskText, userAnswer }
  ← taskText — то что вернул /generate, клиент хранил в ViewModel
  ← userAnswer — ответ пользователя
  → { score, correctedAnswer, feedback }
  | { error: "ai_error" }
  ⚠️ Лимит НЕ списывается. Стрик обновляется здесь (пользователь реально выполнил задание).

POST /ai/clarification
  Headers: Authorization: Bearer <jwt> | X-Device-Id: <deviceId>
  Body: { cardId, cardTheory, userQuestion }
  → { answer, aiRequestsToday, aiDailyLimit }
  | { error: "limit_exceeded" | "ai_error" }
```

### AI-диалог (режим практики «Диалог»)

> Контекст и UX — `practice_plan.md` → «Режим: Диалог». Здесь — серверная механика хранения сессии и потока обмена репликами.
>
> **Главный принцип:** в отличие от остальных AI-режимов (одношаговые: запрос → ответ), диалог — многошаговый и стейтфул. История переписки и срез слов пользователя хранятся **на сервере** в одной сессии, фиксируются один раз при старте — клиент при каждом обмене шлёт только новую реплику + `sessionId`, не пересылая ни историю, ни слова заново.

```
POST /ai/dialog/start
  Headers: Authorization: Bearer <jwt> | X-Device-Id: <deviceId>
  Body: { difficulty: "simple" | "medium" | "hard", words[] }
  ← words[] — срез слов пользователя, клиент собирает один раз по алгоритму выборки
  → { sessionId, reply, translation, expiresAt }
  | { error: "limit_exceeded" | "ai_error" }
  Логика:
    1. Сформировать system_prompt: фиксирует уровень сложности + срез слов (на весь диалог, неизменно)
    2. Создать запись в ai_dialog_sessions (history = пустой массив, turn_count = 0, expires_at = now + 1 час)
    3. Запросить у LLM открывающую реплику (первый вопрос соответствующего уровня)
    4. Записать реплику в history, вернуть клиенту { sessionId, reply, translation, expiresAt }
  ⚠️ Лимит daily_ai_requests списывается здесь — это первый AI-запрос сессии (генерация открывающей реплики)

POST /ai/dialog/message
  Headers: Authorization: Bearer <jwt> | X-Device-Id: <deviceId>
  Body: { sessionId, userMessage }
  → { feedback: { hasErrors, errors[] }, reply, translation, turnCount }
  | { isFinal: true, summary }                         ← если turnCount достиг 5 — диалог завершён
  | { error: "session_not_found" | "session_expired" | "limit_exceeded" | "ai_error" }
  Логика:
    1. Загрузить сессию по sessionId; если expires_at < now → удалить, вернуть session_expired
    2. Собрать полный промт = system_prompt (из сессии, без изменений) + history + userMessage
    3. Один запрос к LLM — модель одновременно оценивает грамматику userMessage
       (errors[] с original/correction/explanation) И формирует следующую реплику с учётом
       уровня сложности и контекста беседы — единый JSON-ответ { feedback, reply, translation }
    4. Дописать обмен (userMessage + ответ ИИ) в history, turn_count += 1
    5. Если turn_count == 5 → запросить у LLM финальную сводку по всему диалогу,
       удалить сессию, вернуть { isFinal: true, summary }
    6. Иначе сохранить сессию, вернуть { feedback, reply, translation, turnCount }
  ⚠️ Лимит списывается на каждый вызов — один LLM-запрос даёт сразу фидбек ПРЕДЫДУЩЕЙ реплики
     и саму следующую реплику, поэтому 1 обмен = 1 единица лимита, не 2

POST /ai/dialog/end
  Headers: Authorization: Bearer <jwt> | X-Device-Id: <deviceId>
  Body: { sessionId }
  → { ok: true }
  Логика: немедленное удаление сессии по кнопке [Завершить диалог] на экране клиента —
          без сводки, невзирая на expires_at. Лимит не списывается и не возвращается.
```

**Формат ответа LLM на каждый обмен** (то, что сервер просит у модели и парсит из её ответа):
```json
{
  "feedback": {
    "hasErrors": true,
    "errors": [
      { "original": "I goes there", "correction": "I go there", "explanation": "I — не 3-е лицо, окончание -s не нужно" }
    ]
  },
  "reply": "That's interesting! How often do you go there?",
  "translation": "Интересно! Как часто ты туда ходишь?"
}
```
Если ошибок не было — `hasErrors: false`, `errors: []`. `translation` — перевод реплики ИИ на русский, чтобы вопрос/реакция ИИ не были барьером для понимания.

**Очистка просроченных сессий:** фоновая задача удаляет записи `ai_dialog_sessions` где `expires_at < now` — раз в несколько минут (например, каждые 5). Это покрывает случай «бросил диалог на середине» — сессия исчезает сама, без сводки.

### Лимиты (для микротем — трекинг на сервере)

```
POST /limits/microtopic
  Headers: Authorization: Bearer <jwt> | X-Device-Id: <deviceId>
  Body: { microtopicId }
  → { allowed: true, microtopicsToday, microtopicsDailyLimit }
  | { allowed: false, resetAt: "2026-04-24T00:00:00+03:00" }
  Логика: проверить лимит микротем, инкрементировать если разрешено
```

### Стрик — активность

```
POST /streak/activity
  Headers: Authorization: Bearer <jwt>
  Body: {
    source: "grammar8r" | "words8r",
    activityType: "ai_exercise" | "repetition" | "wordly" | "maze" | "new_words"
  }
  → {
      streakIncremented: bool,   ← true = стрик вырос сегодня, false = уже засчитано ранее
      currentStreak: Int,
      daysToNextMilestone: Int,  ← сколько дней до следующего milestone (0 если только что достигнут)
      nextMilestone: Int,        ← номер следующего milestone (7/20/30/60/100)
      nextMilestoneBonus: String ← "+2 запроса/день" — для отображения в баннере
    }

  Логика:
    1. Проверить last_activity_msk == today_msk? → streakIncremented: false, вернуть текущие данные
    2. Иначе → применить streak_logic (продолжить/сбросить/заморозка)
    3. Вернуть актуальные данные стрика

  Вызывается:
    - Grammar8r: параллельно с POST /ai/exercise (не ждём ответа перед показом фидбека AI)
    - Words8r: после завершения сессии/игры
```

**Важно:** шаг 9 в потоке `POST /ai/exercise` ("обновить стрик") теперь вынесен в отдельную функцию `streak_logic()`, которая используется и здесь, и там. Не дублировать логику.

### Синхронизация прогресса

```
POST /progress/sync
  Headers: Authorization: Bearer <jwt>
  Body: { events: [{ type: "card_completed" | "exercise_done", entityId, score?, timestamp }] }
  → { ok: true }
  Логика: сохранить события прогресса, офлайн-буфер на клиенте улетает пачкой при появлении сети

GET /progress
  Headers: Authorization: Bearer <jwt>
  → { completedCards: [id...], dailyStats: {...}, streakData: {...} }
  Логика: вернуть весь прогресс пользователя — используется при переустановке/смене телефона
```

### Голоса озвучки (TTS-модели)

Список голосов и превью MP3 захардкожены в APK — сервер их не отдаёт.
Сервер только раздаёт файлы моделей когда пользователь выбирает платный голос.

```
GET /voices/{id}/model.onnx
  Headers: Authorization: Bearer <jwt>
  Логика: проверить что tier пользователя = tier1 или tier2. Если free → 403.
  → бинарный файл модели (.onnx)

GET /voices/{id}/tokens.txt
  Headers: Authorization: Bearer <jwt>
  Логика: та же проверка подписки
  → текстовый файл токенов
```

Бесплатный голос (`hfc_male`) бандлен в APK — для него запросы на сервер не нужны.
Файлы моделей хранятся на сервере в статике или объектном хранилище.
Клиент скачивает выбранный голос в `filesDir/voices/<id>/` и при следующем запуске грузит оттуда.

### Объявления (баннеры и push)

```
GET /announcements
  Headers: Authorization: Bearer <jwt> | X-Device-Id: <deviceId>
  Query: app=grammar8r | words8r
  → { announcements: [{ id, message, type: "modal" | "push", createdAt, expiresAt }] }
  Логика: вернуть только активные (now < expires_at). Приложение вызывает при каждом старте.
  Клиент хранит локально id показанных modal-объявлений — не показывать повторно.
```

### Веб-панель администратора

Простой веб-интерфейс для управления проектом. Доступен только тебе (пароль из env).

На том же Hetzner-сервере также хостится **мини-сайт**: лендинг с двумя приложениями, ссылки на RuStore, описание, политика конфиденциальности, пользовательское соглашение, условия подписки. Статические страницы, никаких дополнительных расходов.

> ⚠️ **Напомнить при реализации подписок:** попросить скинуть примеры файлов политики конфиденциальности и пользовательского соглашения для адаптации под проект.

**Раздел: Объявления**
- Создать объявление: тип (modal-баннер / push), текст, целевое приложение (Grammar8r / Words8r / оба)
- При создании modal: если есть активное (не истёкшее) → **предупреждение**: "Уже есть активное объявление, истекает через X ч Y мин. Всё равно создать?"
  - "Выйти" → отмена; "Всё равно создать" → оба активны, показываются по очереди (от старого к новому)
- Объявления перестают возвращаться через **24 часа** после создания (автоматически по `expires_at`)

**Раздел: Пользователи**

Список всех зарегистрированных пользователей. Колонки: UUID, email, есть ли подписка (тир + до когда), флаг `multi_device` ("Множество устройств" ⚠️), статус бана. Сортировка по флагу `multi_device`.

Карточка пользователя (по клику на UUID):
- Email, UUID (копируется), дата регистрации
- Подписка: тир, дата покупки, дата окончания
- Список устройств: device_id + first_seen + last_seen
- Флаг `multi_device` (выставляется автоматически если > 3 устройств)

**Действия в карточке пользователя:**
1. **Выдать подписку** → попап: выбор тира (Tier 1 / Tier 2) + количество часов → применяется сразу
2. **Suspend (мягкий бан)** → подписка аннулируется, пользователь видит Free-лимиты, контент доступен → опционально: поле для персонального сообщения пользователю (modal-баннер при следующем открытии приложения с текстом причины + "Если не согласен — напиши апелляцию на [email]")
3. **Ban (жёсткий бан)** → аккаунт заблокирован, при входе сообщение об ошибке → опционально: то же персональное сообщение с объяснением и адресом для апелляции
4. **Снять бан** → восстановить доступ
5. **Отправить персональное уведомление** → выбор типа (push / modal-баннер) + текст → только этому пользователю

Для suspend/ban указывается срок в часах (0 = бессрочно).

**Раздел: Промокоды**

Форма создания промокода:
- Скидка в % (1–99)
- Дата окончания (или бессрочно)
- Максимальное число применений
- Чекбокс **"Сгенерировать автоматически"** (по умолчанию включён):
  - Включён → поле кода неактивно, сервер генерирует `XXXX-NNNN` при сохранении
  - Выключен → поле кода становится активным, вводится вручную (минимум 5 символов, только латиница и цифры)

После создания — экран-подтверждение со всеми деталями:
> Промокод создан: **ABCD-1234**
> Скидка: 25%
> Действует до: 31 декабря 2026
> Максимум применений: 50
> Цена со скидкой: Tier 1 → 75₽ / Tier 2 → 134₽

Список промокодов — три фильтра: Активные / Неактивные / Все. Таблица:

| Код | Скидка | Истекает | Максимум | Использовано | Статус |
|-----|--------|----------|----------|--------------|--------|

Статус: `Активен` / `Истёк` (expires_at прошло) / `Исчерпан` (used_count == max_uses) / `Отключён` (is_active = false).
Действие: кнопка "Отключить" / "Включить" — меняет is_active без удаления.

Поток на клиенте: пользователь вводит промокод на экране подписки → `POST /promo/validate` → сервер возвращает скидку и итоговую цену → клиент отображает "Скидка 25%: было 100₽, стало 75₽" → при оплате клиент передаёт code в `POST /payment/create` → сервер инкрементирует `used_count` при получении вебхука об успешной оплате.

**Расчёт итоговой цены со скидкой:**
```
finalPrice = max(1, floor(basePrice * (1 - discountPercent / 100)))
```
Цена всегда целое число рублей, минимум 1₽. Копеек нет.
Пример: базовая 100₽, скидка 25% → floor(100 * 0.75) = 75₽. Скидка 99% → max(1, floor(100 * 0.01)) = 1₽.

**Раздел: Статистика**
- Активные пользователи за день / неделю / месяц
- AI-запросы в день (суммарно)
- Выручка (из YooKassa webhook)

**Раздел: Эндпоинты (только для панели, защищены admin-паролем)**

```
GET  /admin/users                    → список пользователей с флагами
GET  /admin/users/{uid}              → карточка пользователя
POST /admin/users/{uid}/subscription → { tier, hours } → выдать подписку
POST /admin/users/{uid}/suspend      → { hours, message? } → мягкий бан
POST /admin/users/{uid}/ban          → { hours, message? } → жёсткий бан
POST /admin/users/{uid}/unban        → снять бан
POST /admin/users/{uid}/notify       → { type: "push"|"modal", message } → персональное уведомление (без TTL, хранится в БД)
GET  /admin/users/{uid}/notifications → история персональных уведомлений пользователя (текст, дата, shown_at)
POST /admin/announcements            → { type, message, targetApp } → создать общее объявление (TTL 24ч)
GET  /admin/stats                    → агрегированная статистика
GET  /admin/promo                    → список всех промокодов (фильтр: active | inactive | all)
POST /admin/promo                    → { discountPercent, expiresAt?, maxUses } → создать промокод, вернуть сгенерированный code
PATCH /admin/promo/{code}/toggle     → включить / отключить промокод (is_active)

# Клиентский эндпоинт (вызывается приложением после показа modal)
POST /announcements/{id}/seen        → записать shown_at = now для этого объявления
```

---

## Хранилище промтов (server-side)

Промты AI-упражнений не хранятся в клиенте — только на сервере. Ключ = `AiExercise.id` клиента.

> ⚠️ **Базовый системный промт** — перед `system_prompt` каждого упражнения сервер
> автоматически подставляет базовый промт. Он составной: собирается из блоков по двум
> условиям — prefix `basics_` у `exercise_id` и наличие `words[]` в запросе.
> Полное описание блоков и правила сборки — [phase3/ai_base_prompt.md](../phase3/ai_base_prompt.md).
>
> Итоговый system prompt для OpenAI = `базовый промт` + `\n\n` + `system_prompt из таблицы`.

### Таблица `ai_exercise_prompts`

```sql
CREATE TABLE ai_exercise_prompts (
    exercise_id       VARCHAR(100) PRIMARY KEY,
    -- Связующий ключ = AiExercise.id клиента ("present_simple_card2_ex1").
    -- По нему сервер находит всё остальное при запросе POST /ai/exercise.

    system_prompt     TEXT        NOT NULL,
    -- Системный промт: правила для AI — как себя вести, как оценивать,
    -- в каком JSON-формате отвечать. У каждого упражнения свой.
    -- Пример: "Ты преподаватель английского. Оценивай строго.
    --          Отвечай в JSON: {score, correctedAnswer, feedback}"

    user_prompt       TEXT        NOT NULL,
    -- Шаблон задания. Сервер подставляет плейсхолдеры перед отправкой в OpenAI:
    --   {{theorySummary}} ← cardTheory из тела запроса (прислало приложение)
    --   {{words}}         ← слова пользователя из тела запроса (клиент собирает сам по WordSource)
    -- Приложение не собирает промт — только присылает сырые данные.
    -- Пример: "Правило: {{theorySummary}}. Слова: {{words}}.
    --          Дай русское предложение для перевода в Present Simple."

    ai_config_profile VARCHAR(50) NOT NULL
    -- Профиль настроек AI: имя из AiConfigProfile enum ("EXERCISE_LIGHT", "EXERCISE_HEAVY_300" и т.д.).
    -- Сервер смотрит на это поле и применяет нужные maxTokens и temperature из конфига профилей.
    -- Клиент это поле не шлёт — сервер сам знает по exercise_id.
);
```

Обновление промта — один `UPDATE` в БД, без релиза приложения.

---

## Поток AI-запроса

### POST /ai/exercise/generate

```
1. Парсинг авторизации:
   - Есть accessToken → верифицировать подпись (без DB) → читаем uid и tier прямо из токена
   - Нет accessToken → X-Device-Id → uid = deviceId, tier = "free" принудительно

1.5. Проверка бана (единственный запрос к БД на этом шаге):
   - users[uid].ban_status == "ban" → вернуть 403 { error: "account_banned" }
   - users[uid].ban_status == "suspend" → tier = "free" принудительно (продолжаем)
   - Обновить user_devices: сохранить device_id + uid + last_seen
   - Если уникальных device_id для uid > 3 → users[uid].multi_device = true

2. Если tier = "admin" → пропустить шаги 3–4 полностью, перейти к шагу 5.

3. Определить лимит по тиру (из кэша subscription_limits, TTL ~5 мин):
   - free: ai_per_day из subscription_limits
   - tier1/tier2/tester: ai_per_day + streak_bonus_requests

4. Проверить + инкрементировать лимит (АТОМАРНО, один SQL):
   - daily_ai_requests[uid, date_msk]
   - Если count >= limit → вернуть 429 { error: "limit_exceeded" }
   - Иначе → count++

5. Найти промт в ai_exercise_prompts по exerciseId:
   - Не найден → { error: "prompt_not_found" }
   - Подставить {{words}} из тела запроса и {{theorySummary}} из cardTheory

6. Сформировать запрос к OpenAI:
   - System: system_prompt из таблицы
   - User: user_prompt с подставленными плейсхолдерами
   - temperature и max_tokens берутся из ai_config_profile записи в таблице
   - response_format: plain text (задание для пользователя)

7. POST → OpenAI API (ключ только на сервере, никогда в APK)

8. Валидация ответа:
   - Непустая строка ≤ 2000 символов
   - Если невалидно → { error: "ai_error" }

9. Вернуть клиенту:
   { taskText, aiRequestsToday, aiDailyLimit }
```

### POST /ai/exercise/evaluate

```
1. Парсинг авторизации + проверка бана (те же шаги 1–1.5 что выше)

2. Санитизация входа:
   - userAnswer: обрезать до 500 символов, убрать управляющие символы
   - taskText: обрезать до 2000 символов, убрать управляющие символы

3. Найти промт в ai_exercise_prompts по exerciseId:
   - Не найден → { error: "prompt_not_found" }

4. Сформировать оценочный запрос к OpenAI:
   - System: системный промт оценки (из ai_exercise_prompts или общий оценочный)
   - User:
       ЗАДАНИЕ: { taskText }
       ОТВЕТ ПОЛЬЗОВАТЕЛЯ: { userAnswer }
   - Метки ЗАДАНИЕ/ОТВЕТ ПОЛЬЗОВАТЕЛЯ защищают от prompt injection
   - response_format: Structured Outputs strict: true
     { score: Int 0–100, correctedAnswer: String, feedback: String }

5. POST → OpenAI API

6. Валидация ответа:
   - score: Int 0–100
   - correctedAnswer: непустая строка ≤ 500 символов
   - feedback: непустая строка ≤ 500 символов
   - Если невалидно → { error: "ai_error" }

7. Обновить стрик — вызвать streak_logic(uid):
   - Та же функция что в POST /streak/activity
   - Результат НЕ включается в ответ — клиент получает его отдельно через параллельный POST /streak/activity

8. Вернуть клиенту:
   { score, correctedAnswer, feedback }
```

---

## Защита от манипуляций

| Угроза | Защита |
|--------|--------|
| API-ключ OpenAI в APK | Ключ только на сервере, запросы через прокси |
| Превышение лимита | Атомарный инкремент в БД, проверка на сервере |
| Спуф deviceId | 3 запроса/день — низкая ценность, не критично |
| Prompt injection в userAnswer | Метка USER_ANSWER: + max 500 символов + system prompt |
| Кривой ответ от AI | Валидация на сервере, клиент не видит сырой ответ |
| score > 100 | Валидация: 0 ≤ score ≤ 100, иначе ai_error |

---

## Схема БД

### users
| Поле | Тип | Описание |
|------|-----|----------|
| uid | UUID PK | Уникальный ID пользователя |
| email | String? | Null если вход через Яндекс |
| password_hash | String? | bcrypt-хэш, null если вход через Яндекс. В открытом виде никогда не хранится. |
| yandex_uid | String? | Null если собственная регистрация |
| created_at | Timestamp | |
| ban_status | Enum | none / suspend / ban |
| ban_until | Timestamp? | Null = бессрочно. Для suspend/ban с ограничением по времени. |
| multi_device | Boolean | true если у аккаунта зафиксировано > 3 уникальных устройств. Выставляется автоматически. |

### user_devices
| Поле | Тип | Описание |
|------|-----|----------|
| uid | UUID FK | |
| device_id | String | ID устройства (X-Device-Id) |
| first_seen | Timestamp | Когда устройство впервые связалось с аккаунтом |
| last_seen | Timestamp | Последний запрос с этого устройства |
Логика: при каждом запросе с JWT — сохраняем device_id + uid. Если уникальных device_id > 3 → `users.multi_device = true`.

### subscriptions
| Поле | Тип | Описание |
|------|-----|----------|
| uid | UUID PK FK | |
| tier | Enum | free / tier1 / tier2 / admin / tester |
| expires | Timestamp? | Null = бессрочно (ручная выдача через панель) |
| purchased_at | Timestamp? | Когда куплена (null для ручной выдачи) |
| updated_at | Timestamp | |

### subscription_limits
Лимиты тиров. Читаются сервером с кэшем TTL ~5 минут — изменения вступают в силу без рестарта.

| Поле | Тип | Описание |
|------|-----|----------|
| tier | VARCHAR(20) PK | free / tier1 / tier2 / tester |
| ai_per_day | Int | AI-запросов в день. |
| microtopics_per_day | Int? | Микротем в день. Null = безлимит. |
| updated_at | Timestamp | Когда последний раз менялось. |

Начальные значения:

| tier | ai_per_day | microtopics_per_day |
|------|-----------|---------------------|
| free | 3 | 3 |
| tier1 | 30 | null |
| tier2 | 50 | null |
| tester | 30 | null |

### promo_codes
| Поле | Тип | Описание |
|------|-----|----------|
| code | VARCHAR(20) PK | Сам промокод (например SUMMER25). Генерируется случайно: 4 буквы + дефис + 4 цифры. |
| discount_percent | Int | Размер скидки в %, 1–99. |
| expires_at | Timestamp? | Null = бессрочно. |
| max_uses | Int | Максимальное количество применений. |
| used_count | Int | Сколько раз уже применён (инкрементируется при успешной оплате через вебхук YooKassa). |
| created_at | Timestamp | |
| is_active | Boolean | Ручное отключение из панели без удаления. |

**Логика генерации кода:** сервер генерирует `XXXX-NNNN` (4 случайных заглавных буквы + дефис + 4 случайных цифры), проверяет уникальность в БД, при коллизии генерирует заново.

### refresh_tokens
| Поле | Тип | Описание |
|------|-----|----------|
| token | String PK | Сам refresh token (случайный UUID или подписанная строка) |
| uid | UUID FK | Какому пользователю принадлежит |
| expires_at | Timestamp | Когда истекает (30 дней с момента выдачи) |
| created_at | Timestamp | |
Логика: при /auth/refresh — старый токен удаляется, выдаётся новый (ротация). Истёкшие токены можно чистить по крону.

### daily_ai_requests
| Поле | Тип | Описание |
|------|-----|----------|
| uid | UUID | |
| date_msk | Date | Дата по МСК (UTC+3) |
| count | Int | Количество запросов за день |
| PRIMARY KEY | (uid, date_msk) | |

### ai_dialog_sessions

> Хранилище сессий режима практики «Диалог» (см. `practice_plan.md` → «Режим: Диалог» и раздел «AI-диалог» выше — там же поток обмена и формат JSON).

| Поле | Тип | Описание |
|------|-----|----------|
| session_id | UUID PK | |
| uid | UUID FK | |
| difficulty | Enum | simple / medium / hard |
| system_prompt | TEXT | Системный промт, сформирован и зафиксирован при старте: уровень сложности + срез слов пользователя. Не пересчитывается и не запрашивается у клиента повторно ни на одном следующем обмене — экономит токены и трафик |
| history | TEXT (JSON) | Массив реплик `[{ role: "user"\|"assistant", content, feedback? }, ...]`, накапливается с каждым обменом |
| turn_count | Int | Счётчик завершённых обменов, максимум 5 — на 5-м диалог завершается сводкой |
| created_at | Timestamp | |
| expires_at | Timestamp | `created_at + 1 час`. По истечении сессия удаляется фоновой задачей без сводки — пользователь её бросил |

Логика очистки: фоновая задача удаляет записи где `expires_at < now`, периодичность — раз в несколько минут.
Удаление также происходит мгновенно по `POST /ai/dialog/end` (кнопка [Завершить диалог]) и по достижении `turn_count == 5` (после отдачи финальной сводки).

### daily_microtopics
| Поле | Тип | Описание |
|------|-----|----------|
| uid | UUID | |
| date_msk | Date | |
| count | Int | Пройдено микротем за день |
| PRIMARY KEY | (uid, date_msk) | |

### streaks
| Поле | Тип | Описание |
|------|-----|----------|
| uid | UUID PK FK | |
| current_streak | Int | Текущий стрик (дней подряд) |
| last_activity_msk | Date | Последний день с засчитанным действием (из любого приложения) |
| last_activity_source | Enum | grammar8r / words8r — какое приложение первым засчитало сегодня |
| bonus_requests | Int | Текущие бонусные запросы/день (только Grammar8r AI-лимиты) |
| freezes_used | Int | Заморозок использовано в текущем месяце |
| freeze_month | String | "2026-04" — для сброса заморозок |

### jwt_secrets
Секрет для подписи access token хранится в env переменной (`JWT_SECRET`), не в БД.
Refresh token — случайный UUID, подпись не нужна, валидность проверяется по наличию в таблице `refresh_tokens`.

### fcm_tokens
| Поле | Тип | Описание |
|------|-----|----------|
| uid | UUID FK | |
| token | String | FCM registration token устройства |
| app | Enum | grammar8r / words8r |
| updated_at | Timestamp | Обновляется при каждом запуске приложения |

### announcements
| Поле | Тип | Описание |
|------|-----|----------|
| id | UUID PK | |
| message | String | Текст объявления |
| type | Enum | modal / push |
| target_app | Enum | grammar8r / words8r / all |
| uid | UUID? FK | Null = общее (всем). Заполнено = персональное (только этому пользователю). |
| is_personal | Boolean | true = персональное. Дублирует uid != null для удобства запросов. |
| created_at | Timestamp | |
| expires_at | Timestamp? | Для общих: created_at + 24 часа. Для персональных: **null** (без срока, хранится вечно). |
| shown_at | Timestamp? | Когда пользователь увидел (приложение сообщает). Null = ещё не показано. |

**Логика на клиенте при открытии приложения:**
```
GET /announcements → список активных
1. Сначала показать персональные (is_personal = true, shown_at = null) — по одному, от старого к новому
2. После всех персональных — показать общие (is_personal = false, в рамках TTL 24ч)
После показа каждого modal → POST /announcements/{id}/seen → сервер записывает shown_at = now
```

**Персональные modal-уведомления хранятся в БД навсегда** — доступны в истории профиля пользователя в панели.

### user_progress
| Поле | Тип | Описание |
|------|-----|----------|
| uid | UUID FK | |
| event_type | Enum | card_completed / exercise_done |
| entity_id | Int | id карточки или упражнения |
| score | Int? | Результат AI-упражнения (0–100), null для хардкодных |
| occurred_at | Timestamp | Время события (клиентское, MSK) |

---

## Стрик — серверная логика

Общий стрик для Grammar8r и Words8r. Засчитывается первое квалифицирующее действие за день из любого приложения.

**Квалифицирующие действия:**
- Grammar8r: любой AI-запрос (упражнение, уточнение)
- Words8r: завершена сессия повторения / игра Wordly / игра Maze / выучено 5 новых слов

```python
# streak_logic(uid) — вызывается из /streak/activity И из /ai/exercise
def streak_logic(uid) -> StreakResult:
    today = current_date_msk()
    streak = db.get_streak(uid)
    tier = db.get_tier(uid)

    # Уже засчитано сегодня (из любого приложения)
    if streak.last_activity_msk == today:
        return StreakResult(
            streakIncremented=False,
            currentStreak=streak.current_streak,
            daysToNextMilestone=days_to_next(streak.current_streak),
            nextMilestone=next_milestone(streak.current_streak),
            nextMilestoneBonus=bonus_label(tier, next_milestone(streak.current_streak))
        )

    milestone_reached = None

    if streak.last_activity_msk == today - 1:
        streak.current_streak += 1          # продолжаем серию
    elif streak.last_activity_msk == today - 2 and freeze_available(uid):
        streak.freezes_used += 1            # заморозка — стрик сохраняется
    else:
        streak.current_streak = 1           # сброс
        streak.bonus_requests = 0

    streak.last_activity_msk = today

    # Проверить milestone
    milestone_reached = check_milestone(uid, streak.current_streak, tier)

    db.save_streak(uid, streak)

    return StreakResult(
        streakIncremented=True,
        currentStreak=streak.current_streak,
        daysToNextMilestone=days_to_next(streak.current_streak),
        nextMilestone=next_milestone(streak.current_streak),
        nextMilestoneBonus=bonus_label(tier, next_milestone(streak.current_streak)),
        milestoneReached=milestone_reached  # None если не достигнут
    )


def days_to_next(current_streak) -> Int:
    for m in [7, 20, 30, 60, 100]:
        if current_streak < m:
            return m - current_streak
    return 0  # все milestones пройдены

def next_milestone(current_streak) -> Int:
    for m in [7, 20, 30, 60, 100]:
        if current_streak < m:
            return m
    return 100  # уже за 100
```

**Атомарность:** `streak_logic` выполняется в одной транзакции БД — нет race condition если Grammar8r и Words8r шлют запросы одновременно.

### Milestone-бонусы (серверная таблица)

```
MILESTONES = [7, 20, 30, 60, 100]

BONUS_TABLE = {
    "free":  { 7: 1, 20: 1, 30: 1, 60: 1, 100: 1 },   # всегда +1, max +5 → итого 8/день
    "tier1": { 7: 2, 20: 4, 30: 7, 60: 10, 100: 12 },  # max +35 → итого 65/день
    "tier2": { 7: 3, 20: 7, 30: 11, 60: 15, 100: 18 }, # max +54 → итого 104/день
}

def check_milestone(uid, streak, tier):
    for milestone in MILESTONES:
        if streak == milestone:
            bonus = BONUS_TABLE[tier][milestone]
            streak.bonus_requests += bonus
            # Для Free: bonus_requests не может превышать 5
            if tier == "free":
                streak.bonus_requests = min(streak.bonus_requests, 5)
```

---

## Сброс лимитов

- AI-запросы и микротемы: сбрасываются через `date_msk` — новая дата = новые строки
- Заморозки: `freeze_month` != текущий месяц → `freezes_used = 0`, обновить `freeze_month`
- Бонусные запросы: сгорают при сбросе стрика (streak.bonus_requests = 0)

---

## Конфигурация (env переменные)

```
OPENAI_API_KEY=sk-...
JWT_SECRET=...
DATABASE_URL=postgresql://...
YOOKASSA_SECRET_KEY=...
YOOKASSA_SHOP_ID=...
YANDEX_CLIENT_ID=...       # для верификации Яндекс токенов
FIREBASE_SERVER_KEY=...    # FCM для отправки push-уведомлений
ADMIN_PASSWORD=...         # пароль веб-панели администратора
```

---

## Порядок реализации

- [ ] **S1** Ktor проект, Docker, деплой на Hetzner
- [ ] **S2** PostgreSQL подключение, схема БД (миграции через Flyway или Exposed)
- [ ] **S3** POST /auth/register + /auth/login (email+пароль, bcrypt, accessToken + refreshToken)
- [ ] **S3a** POST /auth/refresh (ротация refresh token, актуальный tier из БД в новый accessToken)
- [ ] **S4** POST /auth/yandex (верификация Яндекс-токена, те же два токена на выходе)
- [ ] **S5** GET /subscription — возвращает тир, лимиты, стрик
- [ ] **S6** POST /ai/exercise — полный поток: лимит → OpenAI → валидация → стрик
- [ ] **S7** POST /ai/clarification — уточнение по карточке
- [ ] **S8** POST /limits/microtopic — проверка и трекинг микротем
- [ ] **S9** POST /webhook/yookassa — обработка оплаты
- [ ] **S10** Консольная команда ручной выдачи подписки (без изменений кода)
- [ ] **S11** Интеграция YooKassa SDK в приложении
- [ ] **S12** Таблица `user_devices`: трекинг device_id → uid, автовыставление флага `multi_device` при > 3 устройствах
- [ ] **S13** Поля `ban_status` / `ban_until` в таблице users: suspend/ban логика в потоке каждого запроса
- [ ] **S14** FCM интеграция: регистрация токенов (`POST /fcm/register`), отправка push из панели (всем / одному)
- [ ] **S14b** POST /streak/activity — общий стрик для Grammar8r и Words8r: принимает источник + тип действия, возвращает данные для баннера. Атомарная транзакция.
- [ ] **S15** POST /progress/sync + GET /progress — синк прогресса по событию
- [ ] **S16** GET /announcements — активные объявления с фильтром по TTL 24ч
- [ ] **S17** Веб-панель: список пользователей с флагами, карточка пользователя, выдача подписки (тир + часы), suspend/ban/unban с персональным сообщением, персональные уведомления, создание объявлений, статистика
- [ ] **S18** Добавить FCM в оба приложения (Grammar8r + Words8r): регистрация токена при старте, обработка входящих push
- [ ] **S19** Мини-сайт на том же сервере: лендинг + ссылки на RuStore + страницы с правовыми документами (политика конфиденциальности, пользовательское соглашение, условия подписки)
- [ ] **S20** Таблица `subscription_limits`: начальные значения для free/tier1/tier2/tester, серверный кэш с TTL ~5 мин, использование в `/ai/exercise` и `/tier-limits`
- [ ] **S21** `GET /tier-limits` — публичный эндпоинт, читает из кэша subscription_limits
- [ ] **S22** Промокоды: таблица `promo_codes`, `POST /promo/validate`, вебхук YooKassa инкрементирует used_count, панель: создание / список / toggle