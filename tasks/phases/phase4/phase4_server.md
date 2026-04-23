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

---

## Поток AI-запроса (POST /ai/exercise)

```
1. Парсинг авторизации:
   - Есть JWT → верифицировать подпись (без DB) → uid из токена
   - Нет JWT → X-Device-Id → uid = deviceId, tier = Free принудительно

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

9. Обновить стрик:
   - streak[uid].last_activity_date == today_msk? → ничего
   - Иначе → стрик +1, проверить milestone, начислить bonus_requests

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
| password_hash | String? | bcrypt, null если вход через Яндекс |
| yandex_uid | String? | Null если собственная регистрация |
| created_at | Timestamp | |

### subscriptions
| Поле | Тип | Описание |
|------|-----|----------|
| uid | UUID PK FK | |
| tier | Enum | free / tier1 / tier2 |
| expires | Timestamp? | Null = бессрочно (ручная выдача) |
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
| last_activity_msk | Date | Последний день с AI-заданием |
| bonus_requests | Int | Текущие бонусные запросы/день |
| freezes_used | Int | Заморозок использовано в текущем месяце |
| freeze_month | String | "2026-04" — для сброса заморозок |

### jwt_secrets
Один секрет для подписи JWT, хранится в env переменной, не в БД.

---

## Стрик — серверная логика

```
При каждом AI-запросе (шаг 9 выше):

today = current_date_msk()

if streak.last_activity_msk == today:
    return  # уже засчитан сегодня

if streak.last_activity_msk == today - 1 day:
    streak.current_streak += 1  # продолжаем серию
elif streak.last_activity_msk == today - 2 days AND freeze_available:
    # заморозка — стрик сохраняется, заморозка тратится
    streak.freezes_used += 1
else:
    streak.current_streak = 1  # сброс
    streak.bonus_requests = 0

streak.last_activity_msk = today

# Проверить milestone и начислить бонус
check_milestone(uid, streak.current_streak, tier)
```

### Milestone-бонусы (серверная таблица)

```
MILESTONES = [7, 20, 30, 60, 100]

BONUS_TABLE = {
    "free":  { 7: 1, 20: 1, 30: 1, 60: 1, 100: 1 },  # всегда +1, max +5
    "tier1": { 7: 3, 20: 5, 30: 7, 60: 10, 100: 15 },
    "tier2": { 7: 5, 20: 8, 30: 12, 60: 15, 100: 20 },
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