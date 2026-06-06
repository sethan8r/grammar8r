# Проверки сида (tools/test)

**Основная команда — одна, из `tools/`:** `py check.py <тема.md>` — пересобирает сид + все проверки + сводка `ALL GREEN`.

Отдельные проверки (для точечной диагностики, из `tools/`):

```
py md_to_json.py <theory.md>     # сначала пересобрать сид в tools/seed/
py test/deepcheck.py             # дубли опций, enum'ы, ширина таблиц, пустые поля, порядок, explanation
py test/smell.py                 # мусор в текстовых полях (остатки ✗ → * a) [???] заметки)
py test/dump_by_type.py          # RAW-MD всех упражнений по типам → test/_dump/<Тип>.txt (сравнить форматы)
py test/verify_dump.py 5 8 13    # реконструкция указанных карточек из сида → test/_verify.txt (ручная сверка с MD)
py test/id_report.py             # канон ID: пересобирает theory/_id-registry.md + ищет коллизии (должно быть 0)
```

Валидатор по ограничениям типов встроен в `md_to_json.py` (печатает `VALIDATION:` при каждом прогоне).

Что считается «чисто»:
- VALIDATION: 0, deep: 0.
- smell: допустимы легит-стрелки `→` в подсказках TextInput и слэши в переводах.

Полный список правил оформления — `tasks/phases/phase1/exercise_templates.md` (эталоны + антипаттерны).