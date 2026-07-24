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
    (re.compile(r'(?:^|\s)[a-dA-Dа-гА-Г]\)\s'), 'буквенная опция a)/b)'),
    (re.compile(r'\[\?{2,}|\?{3,}|\[___\]'), 'пропуск [???]/[___]'),
    (re.compile(r'\b(Подсказка|Правильн\w*\s+предложени|Дано)\b'), 'инструкция в данных'),
    (re.compile(r'/[А-Яа-яёЁ]+\s+[А-Яа-яёЁ]'), 'встроенная заметка (слэш + проза)'),
    (re.compile(r'\((это верный ответ|лишнее|пусто)[^)]*\)'), 'служебная пометка в тексте'),
    (re.compile(r'^\s|\s$'), 'лидирующий/хвостовой пробел'),
]
# поля-инструкции пропускаем — там →, /, (лишнее) и т.п. легитимны
SKIP_KEYS = {'explanation', 'userInstruction', 'title', 'taskDescription', 'groupDescription', 'wordBank'}

# Парное **…** в текстах упражнений — ЛЕГИТИМНОЕ выделение жирным (канон: guide §8,
# exercise_templates «Общие правила»; UI рендерит Bold). Запах — только НЕПАРНЫЕ звёздочки,
# оставшиеся после вычитания парных: опечатки вида "I **love this place."
BOLD = re.compile(r'\*\*[^*\n]+?\*\*')

# --- Транскрипция: только двойные скобки [[wɜːk]] (канон guide §8) -------------------------------
# Одинарные [...] заняты пропусками ([___]) и слотами ([предмет]) — по содержимому транскрипцию от
# них не отличить, поэтому маркер ставит автор, а скрипт стережёт обе стороны правила.
IPA_MARKS = 'ˈˌːəɪʊæʌɜɑɒɔθðʃʒŋɡʤʧɹɐɛʔ'
IPA_ALLOWED = set('abcdefghijklmnopqrstuvwxyz' + IPA_MARKS + ' .-') | {'г'}
DOUBLE_BRACKET = re.compile(r'\[\[([^\[\]]*)\]\]')
SINGLE_BRACKET = re.compile(r'(?<!\[)\[([^\[\]]+)\](?!\])')
# Поле-транскрипция карточки слова — структурное, маркер там не нужен (и был бы мусором в БД).
PHONETIC_SKIP_KEYS = {'transcription'}

def scan_phonetics(val, path, key):
    """Две стороны канона: мусор внутри [[…]] и забытый маркер у «голой» […]."""
    if key in PHONETIC_SKIP_KEYS:
        return
    for body in DOUBLE_BRACKET.findall(val):
        if not body or not set(body) <= IPA_ALLOWED:
            hits.append(f'[в [[…]] не транскрипция] {path} = {body!r}')
    for body in SINGLE_BRACKET.findall(val):
        looks_phonetic = any(ch in IPA_MARKS for ch in body) or (len(body) == 1 and body.islower())
        if looks_phonetic:
            hits.append(f'[транскрипция без двойных скобок] {path} = {body!r}')

hits = []

def scan(val, path, key=''):
    if isinstance(val, dict):
        for k, v in val.items():
            if k in SKIP_KEYS:
                continue
            scan(v, f'{path}.{k}', k)
    elif isinstance(val, list):
        for i, v in enumerate(val):
            scan(v, f'{path}[{i}]', key)
    elif isinstance(val, str):
        # → легитимна как подсказка в предложении TextInput ("one knife → two ___")
        skip_arrow = path.endswith('sentence') and 'text_input_exercises' in path
        for rx, name in SUS:
            if name == 'стрелка →' and skip_arrow:
                continue
            if rx.search(val):
                hits.append(f'[{name}] {path} = {val!r}')
        if '*' in BOLD.sub('', val):
            hits.append(f'[непарная звёздочка * (markdown-утечка)] {path} = {val!r}')

def walk_phonetics(val, path, key=''):
    """Транскрипцию проверяем по ВСЕМУ сиду: правило одно для теории, примеров и упражнений."""
    if isinstance(val, dict):
        for k, v in val.items():
            walk_phonetics(v, f'{path}.{k}', k)
    elif isinstance(val, list):
        for i, v in enumerate(val):
            walk_phonetics(v, f'{path}[{i}]', key)
    elif isinstance(val, str):
        scan_phonetics(val, path, key)

# сканируем только упражнения и AI-клиент (не теорию)
for key in d:
    if key.endswith('_exercises') or key == 'ai_exercises':
        scan(d[key], key)

walk_phonetics(d, 'seed')

io.open('_smell.txt', 'w', encoding='utf-8').write(
    f'ВСЕГО ЗАПАХОВ: {len(hits)}\n\n' + '\n'.join(hits) if hits else 'ЧИСТО — 0 запахов')
print('запахов:', len(hits))