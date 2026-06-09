# -*- coding: utf-8 -*-
# Авто-сканер «запахов»: ищет мусор в текстовых полях упражнений сида,
# который парсер мог протащить (остатки разметки, инструкции, заметки).
# Поля 'explanation' и теорию НЕ трогаем — там ✗/→/** легитимны.
# Запуск: py test/smell.py [seed_name]   (по умолчанию — basics)
import json, re, io, os, sys

SEED = os.path.join(os.path.dirname(__file__), '..', 'seed')
_name = sys.argv[1] if len(sys.argv) > 1 else 'basics'
d = json.load(open(os.path.normpath(os.path.join(SEED, _name + '.json')), encoding='utf-8'))
SUS = [
    (re.compile(r'[✗❌]'), 'маркер ✗/❌'),
    (re.compile(r'→'), 'стрелка →'),
    (re.compile(r'\*'), 'звёздочка * (markdown-утечка)'),
    (re.compile(r'(?:^|\s)[a-dA-Dа-гА-Г]\)\s'), 'буквенная опция a)/b)'),
    (re.compile(r'\[\?{2,}|\?{3,}|\[___\]'), 'пропуск [???]/[___]'),
    (re.compile(r'\b(Подсказка|Правильн\w*\s+предложени|Дано)\b'), 'инструкция в данных'),
    (re.compile(r'/[А-Яа-яёЁ]+\s+[А-Яа-яёЁ]'), 'встроенная заметка (слэш + проза)'),
    (re.compile(r'\((это верный ответ|лишнее|пусто)[^)]*\)'), 'служебная пометка в тексте'),
    (re.compile(r'^\s|\s$'), 'лидирующий/хвостовой пробел'),
]
# поля-инструкции пропускаем — там →, /, (лишнее) и т.п. легитимны
SKIP_KEYS = {'explanation', 'userInstruction', 'title', 'taskDescription', 'groupDescription'}

hits = []

def scan(val, path):
    if isinstance(val, dict):
        for k, v in val.items():
            if k in SKIP_KEYS:
                continue
            scan(v, f'{path}.{k}')
    elif isinstance(val, list):
        for i, v in enumerate(val):
            scan(v, f'{path}[{i}]')
    elif isinstance(val, str):
        # → легитимна как подсказка в предложении TextInput ("one knife → two ___")
        skip_arrow = path.endswith('sentence') and 'text_input_exercises' in path
        for rx, name in SUS:
            if name == 'стрелка →' and skip_arrow:
                continue
            if rx.search(val):
                hits.append(f'[{name}] {path} = {val!r}')

# сканируем только упражнения и AI-клиент (не теорию)
for key in d:
    if key.endswith('_exercises') or key == 'ai_exercises':
        scan(d[key], key)

io.open('_smell.txt', 'w', encoding='utf-8').write(
    f'ВСЕГО ЗАПАХОВ: {len(hits)}\n\n' + '\n'.join(hits) if hits else 'ЧИСТО — 0 запахов')
print('запахов:', len(hits))