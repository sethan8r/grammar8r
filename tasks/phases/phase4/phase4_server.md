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
  → { uid, jwt }
  Логика: проверить email уникальность, bcrypt пароль, создать uid, выдать JWT

POST /auth/login
  Body: { email, password }
  → { uid, jwt }
  Логика: найти по email, проверить bcrypt, выдать JWT

POST /auth/yandex
  Body: { yandexToken }
  → { uid, jwt }
  Логика: верифицировать токен у Яндекса, получить yandex_uid,
          найти или создать пользователя, выдать JWT
```

### Подписка

```
GET /subscription?uid=X
  Headers: Authorization: Bearer <jwt>
  → {
      tier: "free" | "tier1" | "tier2",
      expires: "2026-05-01" | null,
      aiRequestsToday: 2,
      aiDailyLimit: 3,
      microtopicsToday: 1,
      microtopicsDailyLimit: 5 | null,   ← null = безлимит
      streakCurrent: 14,
      streakBonusRequests: 1
    }

POST /webhook/yookassa
  ← Вебхук от YooKassa об успешной оплате
  Логика: верифицировать подпись YooKassa, обновить tier и expires в БД
```

### AI-прокси

```
POST /ai/exercise
  Headers: Authorization: Bearer <jwt>
         | X-Device-Id: <deviceId>   ← если не залогинен, tier = Free принудительно
  Body: { cardId, userAnswer, exerciseType, cardTheory? }
  → { score, correctedAnswer, feedback, aiRequestsToday, aiDailyLimit }
  | { error: "limit_exceeded" | "ai_error" }

POST /ai/clarification
  Headers: Authorization: Bearer <jwt> | X-Device-Id: <deviceId>
  Body: { cardId, cardTheory, userQuestion }
  → { answer, aiRequestsToday, aiDailyLimit }
  | { error: "limit_exceeded" | "ai_error" }
```

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

# Клиентский эндпоинт (вызывается приложением после показа modal)
POST /announcements/{id}/seen        → записать shown_at = now для этого объявления
```

---

## Поток AI-запроса (POST /ai/exercise)

```
1. Парсинг авторизации:
   - Есть JWT → верифицировать подпись (без DB) → uid из токена
   - Нет JWT → X-Device-Id → uid = deviceId, tier = Free принудительно

1.5. Проверка бана:
   - users[uid].ban_status == "ban" → вернуть 403 { error: "account_banned" }
   - users[uid].ban_status == "suspend" → tier = Free принудительно (продолжаем)
   - Обновить user_devices: сохранить device_id + uid + last_seen
   - Если уникальных device_id для uid > 3 → users[uid].multi_device = true

2. Получить подписку из БД:
   - subscriptions[uid] → { tier, expires }
   - Если expires < now → tier = Free

3. Определить лимит по тиру:
   - Free: 3/день
   - Tier1: 30/день + streak_bonus_requests
   - Tier2: 50/день + streak_bonus_requests

4. Проверить + инкрементировать лимит (АТОМАРНО, один SQL):
   - daily_ai_requests[uid, date_msk]
   - Если count >= limit → вернуть 429 { error: "limit_exceeded" }
   - Иначе → count++

5. Санитизация входа:
   - userAnswer: обрезать до 500 символов, убрать управляющие символы

6. Сформировать промт:
   - System: инструкции + теория карточки (для clarification)
   - User: USER_ANSWER: { userAnswer }
   - response_format: Structured Outputs strict: true
   - temperature: 0 (для упражнений) / 0.3 (для уточнений)
   - max_tokens: 200 (упражнение) / 150 (уточнение)

7. POST → OpenAI API (ключ только на сервере, никогда в APK)

8. Валидация ответа:
   - score: Int 0–100
   - correctedAnswer: непустая строка, ≤ 500 символов
   - feedback: непустая строка, ≤ 500 символов
   - Если невалидно → { error: "ai_error" }, не отдавать сырой ответ клиенту

9. Обновить стрик — вызвать streak_logic(uid):
   - Та же функция что используется в POST /streak/activity
   - Результат стрика в ответ /ai/exercise НЕ включается — клиент получает его отдельно через параллельный POST /streak/activity

10. Вернуть клиенту:
    { score, correctedAnswer, feedback, aiRequestsToday, aiDailyLimit }
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
| tier | Enum | free / tier1 / tier2 |
| expires | Timestamp? | Null = бессрочно (ручная выдача через панель) |
| purchased_at | Timestamp? | Когда куплена (null для ручной выдачи) |
| updated_at | Timestamp | |

### daily_ai_requests
| Поле | Тип | Описание |
|------|-----|----------|
| uid | UUID | |
| date_msk | Date | Дата по МСК (UTC+3) |
| count | Int | Количество запросов за день |
| PRIMARY KEY | (uid, date_msk) | |

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
Один секрет для подписи JWT, хранится в env переменной, не в БД.

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
- [ ] **S3** POST /auth/register + /auth/login (email+пароль, bcrypt, JWT)
- [ ] **S4** POST /auth/yandex (верификация Яндекс-токена)
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