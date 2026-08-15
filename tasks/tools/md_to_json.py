#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
md_to_json.py — парсер теории Grammar8r: MD (строгий формат) -> JSON-сиды.

Выходы (на тему):
  seed/<тема>.json          — контент в content.db (теория+упражнения+AI-клиент+слова+категории)
  seed/<тема>_prompts.json  — серверный (промты + AI Config Profile), в APK НЕ попадает

Формат MD — tasks/theory_content_guide.md, раздел 8. Схема — tasks/db_schema.md.
Запуск:  py md_to_json.py <path-to-theory.md> [--microtopic N] [--out DIR]

ВНИМАНИЕ: build-инструмент, живёт в репо (не удалять). См. CLAUDE.md → «Конвейер контента».
"""
import re
import os
import sys
import json
import argparse

# ---------- утилиты ----------

def strip_md(text):
    """Убрать инлайн-маркеры ** и * для полей-значений (опции, ответы)."""
    return text.replace('**', '').replace('*', '').strip()

def variant_for(label):
    """Вариант плашки по ключевому слову ярлыка. None — ярлык НЕ плашка
    (строка целиком в болде станет heading, болд+проза → paragraph)."""
    low = label.lower()
    if 'ловушк' in low:
        return 'trap'
    if any(k in low for k in ('не путай', 'важно', 'осторожно')):
        return 'warning'
    if any(k in low for k in ('кстати', 'запомни', 'секрет', 'совет', 'лайфхак')):
        return 'tip'
    if 'формул' in low:
        return 'formula'
    if any(k in low for k in ('примечани', 'заметк')):
        return 'note'
    return None

def cap_first(text):
    """Поднять в заглавную первую БУКВУ (рус/лат), пропустив ведущие markdown-маркеры
    (`*`, `_`), пробелы, пунктуацию и символы (`≠`, `→` и т.п.). Остальной текст не трогаем.
    Идемпотентно: уже заглавная остаётся заглавной.

    Транскрипцию `[[…]]` не трогаем совсем: символ IPA — это конкретный звук, `æ` и `Æ` разные вещи."""
    for i, ch in enumerate(text):
        if ch.isalpha():
            head = text[:i]
            if head.rfind('[[') > head.rfind(']]'):
                return text  # буква стоит внутри транскрипции — регистр менять нельзя
            return text[:i] + ch.upper() + text[i + 1:]
    return text

def capitalize_body(blocks):
    """Капитализировать первую букву в теле плашки: абзацы, мини-заголовки и КАЖДЫЙ пункт
    списка. Ячейки таблиц и вложенные плашки не трогаем (структурные данные). Мутирует и
    возвращает тот же список (удобно оборачивать прямо в месте сборки callout)."""
    for b in blocks:
        t = b.get('type')
        if t in ('paragraph', 'heading'):
            b['text'] = cap_first(b['text'])
        elif t == 'list':
            b['items'] = [cap_first(it) for it in b['items']]
    return blocks

def is_table_line(s):
    return s.lstrip().startswith('|')

def is_separator_row(s):
    return bool(re.fullmatch(r'\|[\s:|-]+\|', s.strip()))

def split_row(s):
    cells = s.strip().strip('|').split('|')
    return [c.strip() for c in cells]

# ---------- сбор таблиц/списков ----------

def collect_table(lines, j):
    rows = []
    while j < len(lines) and is_table_line(lines[j]):
        if not is_separator_row(lines[j]):
            rows.append(split_row(lines[j]))
        j += 1
    header = rows[0] if rows else []
    body = rows[1:] if len(rows) > 1 else []
    return header, body, j

def collect_list(lines, j):
    items = []
    ordered = bool(re.match(r'^\s*\d+\.\s+', lines[j]))
    while j < len(lines) and re.match(r'^\s*([-*]|\d+\.)\s+', lines[j]):
        item = re.sub(r'^\s*([-*]|\d+\.)\s+', '', lines[j]).rstrip()
        items.append(item)
        j += 1
    return {'type': 'list', 'ordered': ordered, 'items': items}, j

# ---------- блоки теории ----------

def parse_theory(body):
    blocks = []
    j = 0
    while j < len(body):
        raw = body[j]
        s = raw.strip()
        if not s:
            j += 1
            continue
        if is_table_line(s):
            header, rows, j = collect_table(body, j)
            blocks.append({'type': 'table', 'header': header, 'rows': rows})
            continue
        if re.match(r'^\s*([-*]|\d+\.)\s+', raw):
            lst, j = collect_list(body, j)
            blocks.append(lst)
            continue
        # строка начинается с жирного фрагмента: heading / callout / обычный параграф.
        # Покрывает все формы плашек (автор оформляет ловушки по-разному):
        #   **Ловушка:** тело                          (ярлык в болде, тело после)
        #   **Ловушка 1: what vs. who.**               (вся строка в болде)
        #   **Ловушка 1: his и her.** тело             (ярлык+мини-заголовок в болде, тело после)
        bm = re.match(r'^\*\*(.+?)\*\*\s*(.*)$', s)
        if bm:
            bold_inner = bm.group(1).strip()
            after = bm.group(2).strip()
            lm = re.match(r'^([^:]+):\s*(.*)$', bold_inner)
            if lm and variant_for(lm.group(1)) is not None:
                label = cap_first(lm.group(1).strip())
                title = lm.group(2).strip()          # мини-заголовок (был внутри болда)
                inline_parts = []
                if title:
                    inline_parts.append(f'**{title}**')  # сохраняем жирный мини-заголовок в теле
                if after:
                    inline_parts.append(after)
                j += 1
                if inline_parts:
                    # тело прямо в строке -> абзац. Плюс: если СРАЗУ под ярлыком (без пустой
                    # строки) идёт список или таблица — это продолжение тела плашки, впитываем
                    # её в тот же callout (иначе список «вываливается» из фрейма). Обычный абзац
                    # после пустой строки сюда не попадает — он остаётся отдельным блоком.
                    body_blocks = [{'type': 'paragraph', 'text': ' '.join(inline_parts).strip()}]
                    if j < len(body) and body[j].strip() and (
                        is_table_line(body[j]) or re.match(r'^\s*([-*]|\d+\.)\s+', body[j])
                    ):
                        more_blocks, j = absorb_block(body, j)
                        body_blocks.extend(more_blocks)
                else:
                    # ярлык на отдельной строке (**Ловушки:** / **Формула:**) — впитываем следующий
                    # блок (список/таблицу/абзац, в т.ч. жирный) и парсим его как тело плашки.
                    body_blocks, j = absorb_block(body, j)
                blocks.append({'type': 'callout', 'variant': variant_for(label),
                               'label': label, 'blocks': capitalize_body(body_blocks)})
                continue
            if not after:
                # вся строка в болде, не плашка → подзаголовок секции
                blocks.append({'type': 'heading', 'text': bold_inner})
                j += 1
                continue
            # жирный фрагмент в начале + проза дальше → обычный абзац (жирный остаётся инлайном)
            blocks.append({'type': 'paragraph', 'text': s})
            j += 1
            continue
        # callout без болда: короткий ярлык-ключевое-слово + ":" (автор забыл **).
        # Ярлык ≤ 3 слов и относится к плашке (Кстати/Ловушка/Важно/...), иначе это проза с двоеточием.
        pm = re.match(r'^([^:*]{1,40}?):\s+(.+)$', s)
        if pm and variant_for(pm.group(1)) is not None and len(pm.group(1).split()) <= 3:
            label = cap_first(pm.group(1).strip())
            blocks.append({'type': 'callout', 'variant': variant_for(label), 'label': label,
                           'blocks': capitalize_body(
                               [{'type': 'paragraph', 'text': pm.group(2).strip()}])})
            j += 1
            continue
        blocks.append({'type': 'paragraph', 'text': s})
        j += 1
    return blocks


def absorb_block(body, j):
    """Собрать ОДИН следующий блок (после ярлыка плашки на отдельной строке) как тело callout:
    пропустить ведущие пустые строки, забрать смежные непустые строки до пустой/разделителя,
    распарсить их как блоки. Возвращает (список блоков, новый индекс j)."""
    while j < len(body) and not body[j].strip():
        j += 1
    raw = []
    while j < len(body) and body[j].strip():
        if re.fullmatch(r'(-{3,}|\*{3,}|_{3,})', body[j].strip()):
            break
        raw.append(body[j])
        j += 1
    return (parse_theory(raw) if raw else []), j

# ---------- examples / clarification ----------

def parse_examples(body):
    out = []
    for j, line in enumerate(body):
        if not is_table_line(line) or is_separator_row(line):
            continue
        cells = split_row(line)
        if len(cells) >= 3 and cells[0].strip('#').strip().isdigit():
            out.append({'ru': cells[1], 'en': cells[2]})
    return out

def parse_clarification(body):
    return [re.sub(r'^\s*[-*]\s+', '', l).strip()
            for l in body if re.match(r'^\s*[-*]\s+', l)]

# ---------- упражнения ----------

def parse_explanation(body):
    for l in body:
        s = l.strip()
        m = re.match(r'^\*Explanation[^:]*:\*\s*(.+)$', s)
        if m:
            return m.group(1).strip().rstrip('*').strip()
    return ''

def parse_options(body):
    opts = []
    for l in body:
        s = l.strip()
        if s.startswith('- '):
            text = s[2:].strip()
            correct = '✓' in text
            text = strip_md(text.replace('✓', '').replace('✗', '').replace('❌', ''))
            opts.append({'text': text, 'isCorrect': correct})
    return opts

CHOICE_INSTR = (
    'Выбери правильный вариант:', 'Выбери правильный перевод:',
    'Переведи предложение на русский:', 'Переведи на русский:',
    'Выбери английский перевод:', 'В предложении есть ошибка. Выбери правильный вариант:',
)

def clean_prompt(body):
    """prompt для MultipleChoice/ErrorCorrection/ConstructionMeaning:
    снять инструкцию-префикс, italic-обёртку, кавычки; вынести русский контекст в contextRu.
    Контекст берётся из строки `RU: …` (приоритет — единый маркер «русская строка», как в
    WordArrangement) либо, если её нет, из хвостовых скобок `(...)` (легаси-форма)."""
    ru_parts, en_parts = [], []
    for line in parse_prompt_lines(body):
        m = re.match(r'^RU:\s*(.+)$', line)
        if m:
            ru_parts.append(m.group(1).strip())
        else:
            en_parts.append(line)
    text = ' '.join(en_parts).strip()
    for p in CHOICE_INSTR:
        if text.startswith(p):
            text = text[len(p):].strip()
    ctx = ' '.join(ru_parts).strip()
    if not ctx:
        m = re.search(r'\*?\(([^()]+)\)\*?\s*$', text)
        if m:
            ctx, text = m.group(1).strip(), text[:m.start()].strip()
    text = text.strip().strip('*').strip().strip('"').strip()
    return text, ctx

INSTR_LINES = (
    'Переведи предложение на русский:', 'Переведи на русский:',
    'Выбери английский перевод:', 'В предложении есть ошибка. Выбери правильный вариант:',
    'Выбери правильный перевод:',
)

def parse_prompt_lines(body):
    out = []
    for l in body:
        s = l.strip()
        if not s or s.startswith('- ') or s.startswith('*Explanation') or s.startswith('**Ex'):
            continue
        if re.fullmatch(r'-{3,}', s):       # `---` в MD — лишь визуальный разделитель автора;
            continue                        # каждое упражнение = свой экран, в данных не нужен
        if s in INSTR_LINES:
            continue
        out.append(s)
    return out

def ex_multiple_choice(body, type_id, subtype):
    prompt, ctx = clean_prompt(body)
    return {'id': type_id, 'choiceType': subtype, 'prompt': prompt,
            'contextRu': ctx, 'options': parse_options(body),
            'explanation': parse_explanation(body)}

def ex_true_false(body, type_id):
    statements = []
    for l in body:
        if not is_table_line(l) or is_separator_row(l):
            continue
        c = split_row(l)
        # Два формата: 4 кол. `# | EN | RU | Верно?` (до Present Simple) и 3 кол. `# | EN | Верно?`
        # (с Present Simple RU убран, guide §4). Флаг — всегда последняя колонка.
        if len(c) >= 3 and c[0].strip('#').strip().isdigit():
            ru = c[2] if len(c) >= 4 else ''
            statements.append({'en': c[1], 'ru': ru, 'isTrue': '✓' in c[-1]})
    return {'id': type_id, 'statements': statements, 'explanation': parse_explanation(body)}

def ex_table_fill(body, type_id):
    task = next((l.strip()[len('Задание:'):].strip()
                 for l in body if l.strip().startswith('Задание:')), '')
    header, rows = first_table(body)   # первая строка таблицы = заголовок, пропускается
    out = [{'hint': r[0], 'answer': r[1]} for r in rows if len(r) >= 2]
    return {'id': type_id, 'taskDescription': task, 'rows': out,
            'explanation': parse_explanation(body)}

def ex_word_arrangement(body, type_id):
    situation = ''
    correct = ''
    words, distractors = [], []
    for l in body:
        s = l.strip()
        if s.startswith('RU:') or s.startswith('Ситуация:'):
            situation = s
        elif s.startswith('Правильное предложение:'):
            correct = strip_md(s.split(':', 1)[1])
        elif is_table_line(l) and not is_separator_row(l):
            c = split_row(l)
            if len(c) >= 2 and c[0] not in ('Слово',):
                flag = c[-1]
                if '✗' in flag or 'дистрактор' in flag.lower():
                    distractors.append(c[0])
                else:
                    words.append(c[0])
    return {'id': type_id, 'situationRu': situation, 'correctSentence': correct,
            'words': words, 'distractors': distractors,
            'explanation': parse_explanation(body)}

def first_table(body):
    j = 0
    while j < len(body) and not is_table_line(body[j]):
        j += 1
    if j >= len(body):
        return [], []
    header, rows, _ = collect_table(body, j)
    return header, rows

def _ti_split_ctx(txt):
    """Вынести контекст из хвостовых (...) и убрать кавычки у предложения."""
    txt = txt.strip()
    ctx = ''
    cm = re.search(r'\(([^()]+)\)\s*$', txt)
    if cm:
        ctx, txt = cm.group(1).strip(), txt[:cm.start()].strip()
    return txt.strip().strip('"').strip(), ctx

def ex_text_input(body, type_id):
    # items: таблица разрешает {sentence, contextRu, answer, alternatives}.
    # Опц. «банк слов»: строка `Банк: w1, w2, …` -> wordBank (пул статичных чипов),
    # а первая не-пунктовая строка -> taskDescription (шапка-задание). Обе NULL у обычных
    # заданий -> вид не меняется. 'Подсказка:' в схеме НЕТ -> выкидываем. Старый MD не переделываем.
    items, cur, task, bank = [], None, None, None
    for l in body:
        s = l.strip()
        if not s or s.startswith('*Explanation') or s.startswith('Подсказка:'):
            continue
        # строка банка слов:  Банк: жил, любил, получала
        mb = re.match(r'^Банк\s*:\s*(.+)$', s)
        if mb:
            bank = [strip_md(w.strip()) for w in mb.group(1).split(',') if w.strip()]
            continue
        # italic-only строка контекста:  *(нужен ли предлог?)*
        ic = re.fullmatch(r'\*\((.+)\)\*', s)
        if ic and cur is not None and not cur['answer']:
            cur['contextRu'] = ic.group(1).strip()
            continue
        # строка ответа:  Ответ: / Правильный ответ:
        ans = re.match(r'^(?:Правильный ответ|Ответ)\s*:\s*(.+)$', s)
        if ans and cur is not None:
            val = ans.group(1).strip()
            if re.search(r'пуст', val):            # "*(пусто — предлог не нужен)*"
                cur['answer'] = ''
            else:
                parts = [strip_md(a) for a in val.split('/')]
                cur['answer'], cur['alternatives'] = parts[0], parts[1:]
            continue
        # формат-стрелка в одну строку:  prompt → **answer**
        am = re.match(r'^(?:\d+\.\s*)?(.+?)\s*→\s*\*\*(.+?)\*\*\s*$', s)
        if am:
            if cur:
                items.append(cur)
            sent, ctx = _ti_split_ctx(am.group(1))
            items.append({'sentence': sent, 'contextRu': ctx,
                          'answer': strip_md(am.group(2)), 'alternatives': []})
            cur = None
            continue
        # строка-предложение (есть ___, нумерация или кавычки)
        if '___' in s or re.match(r'^\d+\.', s) or s.startswith('"'):
            if cur:
                items.append(cur)
            sent, ctx = _ti_split_ctx(re.sub(r'^\d+\.\s*', '', s))
            cur = {'sentence': sent, 'contextRu': ctx, 'answer': '', 'alternatives': []}
            continue
        # первая не-пунктовая строка ДО пунктов = задание-шапка (напр. «Впиши глагол…»)
        if cur is None and not items and task is None:
            task = strip_md(s)
    if cur:
        items.append(cur)
    out = {'id': type_id, 'items': items, 'explanation': parse_explanation(body)}
    if task:
        out['taskDescription'] = task
    if bank:
        out['wordBank'] = bank
    return out

def ex_dialog_restore(body, type_id):
    dlg = []
    for l in body:
        m = re.match(r'^([AB]):\s*(.+)$', l.strip())
        if m:
            txt = m.group(2).strip()
            blank = (txt.strip('_') == '')
            dlg.append({'speaker': m.group(1),
                        'text': None if blank else strip_md(txt.strip('"'))})
    return {'id': type_id, 'lines': dlg, 'options': parse_options(body),
            'explanation': parse_explanation(body)}

def ex_matching(body, type_id):
    task = next((l.strip()[len('Задание:'):].strip() for l in body
                 if l.strip().startswith('Задание:')), '')
    _, rows = first_table(body)
    pairs = [{'left': r[0], 'right': r[1]} for r in rows if len(r) >= 2]
    return {'id': type_id, 'taskDescription': task, 'pairs': pairs,
            'explanation': parse_explanation(body)}

def ex_categorization(body, type_id):
    task = next((l.strip()[len('Задание:'):].strip() for l in body
                 if l.strip().startswith('Задание:')), '')
    header, rows = first_table(body)
    cats = [{'title': h, 'items': []} for h in header]
    # Один элемент = одна ячейка (канон «в столбик»): запятая внутри фразы элемент не режет.
    for r in rows:
        for idx, cell in enumerate(r):
            if idx < len(cats) and cell and cell != '—':
                cats[idx]['items'].append(cell.strip())
    return {'id': type_id, 'taskDescription': task, 'categories': cats,
            'explanation': parse_explanation(body)}

def ex_error_correction(body, type_id):
    prompt, _ = clean_prompt(body)
    return {'id': type_id, 'wrongSentence': prompt, 'options': parse_options(body),
            'explanation': parse_explanation(body)}

def ex_construction_meaning(body, type_id):
    prompt, _ = clean_prompt(body)
    return {'id': type_id, 'construction': prompt, 'options': parse_options(body),
            'explanation': parse_explanation(body)}

def ex_transformation(body, type_id):
    task = next((l.strip()[len('Задание:'):].strip() for l in body
                 if l.strip().startswith('Задание:')), '')
    items = []
    for l in body:
        m = re.match(r'^\d+\.\s+"?(.+?)"?\s*→\s*\*\*"?(.+?)"?\*\*\s*$', l.strip())
        if m:
            items.append({'original': m.group(1).strip().strip('"'),
                          'transformed': strip_md(m.group(2)).strip('"')})
    return {'id': type_id, 'taskDescription': task, 'items': items,
            'explanation': parse_explanation(body)}

def ex_find_the_odd(body, type_id):
    group = ' '.join(parse_prompt_lines(body)).strip().rstrip(':').strip()
    items = []
    for l in body:
        s = l.strip()
        if s.startswith('- '):
            t = s[2:].strip()
            odd = ('✓' in t) or ('лишн' in t.lower())
            t = re.sub(r'\(лишнее\)', '', t).replace('✓', '')
            items.append({'text': strip_md(t).strip(), 'isOdd': odd})
    return {'id': type_id, 'groupDescription': group, 'items': items,
            'explanation': parse_explanation(body)}

CHOICE_TYPES = {'CHOICE', 'FORWARD_CHOICE', 'REVERSE_CHOICE'}

# 12 таблиц упражнений (MultipleChoice — одна таблица на 3 подтипа, обрабатывается отдельно).
# базовый тип -> (ключ JSON, парсер, значение enum HardcodedExerciseType)
EX_HANDLERS = {
    'TableFill': ('table_fill_exercises', ex_table_fill, 'TABLE_FILL'),
    'TrueFalse': ('true_false_exercises', ex_true_false, 'TRUE_FALSE'),
    'WordArrangement': ('word_arrangement_exercises', ex_word_arrangement, 'WORD_ARRANGEMENT'),
    'TextInput': ('text_input_exercises', ex_text_input, 'TEXT_INPUT'),
    'DialogRestore': ('dialog_restore_exercises', ex_dialog_restore, 'DIALOG_RESTORE'),
    'Matching': ('matching_exercises', ex_matching, 'MATCHING'),
    'ErrorCorrection': ('error_correction_exercises', ex_error_correction, 'ERROR_CORRECTION'),
    'ConstructionMeaning': ('construction_meaning_exercises', ex_construction_meaning, 'CONSTRUCTION_MEANING'),
    'Transformation': ('transformation_exercises', ex_transformation, 'TRANSFORMATION'),
    'Categorization': ('categorization_exercises', ex_categorization, 'CATEGORIZATION'),
    'FindTheOdd': ('find_the_odd_exercises', ex_find_the_odd, 'FIND_THE_ODD'),
}

# ---------- AI exercise ----------

EXAMPLE_LINE = re.compile(r"^Пример вывода AI:\s*'(.+)'\s*$")

# Дописывается к Prompt Template вместе с примером — явно говорит AI, что это ОБРАЗЕЦ
# структуры/формата, а не готовый текст для копирования. Так пример одновременно:
# 1) помогает AI с форматом вывода (особенно для FILL_BLANKS), 2) виден человеку при
# ревью прямо в MD (раздел 5 → «Критичные правила»), 3) не приводит к дословным повторам.
EXAMPLE_FRAME = (
    "\n\nПример формата вывода (это ОБРАЗЕЦ СТРУКТУРЫ — не повторяй его дословно, "
    "придумай свою ситуацию со своими словами и числами, сохранив только формат): '{example}'"
)

# Канон пропусков — db_schema.md → AiExerciseInputMode.FILL_BLANKS: сервер парсит именно [___].
FILL_BLANKS_NOTE = (
    "\n\nЕсли в задании нужны пропуски — оформляй их СТРОГО как [___] (с квадратными скобками). "
    "Это формат, который понимает наш парсер ответов, и именно он считается приоритетным."
)


def parse_ai(body, card_id):
    f = {}
    example = None
    for l in body:
        l = l.strip()
        m = re.match(r'^\*\*([^*]+):\*\*\s*(.+)$', l)
        if m:
            f[m.group(1).strip()] = m.group(2).strip().strip('"')
            continue
        em = EXAMPLE_LINE.match(l)
        if em:
            example = em.group(1).strip()
    client = {
        'id': f.get('ID', ''),
        'cardId': card_id,
        'title': f.get('Title', ''),
        'userInstruction': f.get('User Instruction', ''),
        'inputMode': f.get('Input Mode', ''),
        'wordsSource': f.get('Words Source', ''),
    }
    prompt = f.get('Prompt Template', '')
    if example:
        prompt = prompt.rstrip() + EXAMPLE_FRAME.format(example=example)
    if client['inputMode'] == 'FILL_BLANKS':
        prompt = prompt.rstrip() + FILL_BLANKS_NOTE
    server = {
        'id': f.get('ID', ''),
        'promptTemplate': prompt,
        'aiConfigProfile': f.get('AI Config Profile', ''),
    }
    return client, server, (example is not None)

# ---------- words8r sync ----------

def parse_words(body, microtopic_id, category_id, source, word_id_counter):
    out = []
    for l in body:
        if not is_table_line(l) or is_separator_row(l):
            continue
        c = split_row(l)
        if len(c) >= 3 and c[0] not in ('Слово', 'V1'):
            word_id_counter[0] += 1
            out.append({
                'id': word_id_counter[0],
                'word': c[0],
                'translation': c[1],
                'transcription': c[2] if c[2] else None,
                'microtopicId': microtopic_id,
                'categoryId': category_id,
            })
    return out

# ---------- основной разбор ----------

HDR_META = re.compile(r'\*\*ID:\*\*\s*([\w]+)\s*\|\s*\*\*Order:\*\*\s*(\d+)')
EX_HDR = re.compile(r'^\*\*Ex\s+\d+\s*·\s*(.+?)\*\*\s*\*\(ID:\s*(\d+)\)\*')
WORDS_HDR = re.compile(r'^###\s+Words8r Sync\s*·\s*(.+?)(?:\s*\[category:\s*(\w+)\])?\s*$')
MT_CAT = re.compile(r'\*\*Категория слов:\*\*\s*(\w+)')
WORD_START_HDR = re.compile(r'\*\*Слова курса\s*—\s*стартовый ID:\*\*\s*(\d+)')
TOPIC_CATEGORY = re.compile(r'\*\*Раздел:\*\*\s*(\d+)\s*·\s*(.+?)\s*·\s*order=(\d+)\s*$')
TOPIC_CATEGORY_DESC = re.compile(r'\*\*Раздел\s*·\s*Описание:\*\*\s*(.+)$')
TAGS_HDR = re.compile(r'\*\*Теги:\*\*\s*(.+)$')
# Тег — живая фраза, какой её напишет пользователь («как сказать что чего то не было»),
# поэтому потолок мягкий: он ловит тег, выродившийся в предложение, а не нормальный вопрос.
MAX_TAG_WORDS = 6


def parse_tags(head, title, level, warns):
    """Строка `**Теги:**` из шапки темы/микротемы -> searchKeywords (через запятую, строчными).

    Строки нет или она пустая -> None (темы без тегов собираются как раньше). Нарушения канона
    дают предупреждения, а не ошибку: 85 микротем ещё без тегов, ретрофит идёт постепенно.
    """
    raw = next((m.group(1) for h in head for m in [TAGS_HDR.search(h)] if m), None)
    if not raw:
        return None
    title_words = {w for w in re.findall(r'\w+', title.lower()) if len(w) > 2}
    tags, seen = [], set()
    for tag in (t.strip().lower() for t in raw.split(',')):
        if not tag:
            continue
        if tag in seen:
            warns.append(f'ТЕГИ {level}: дубль «{tag}»')
            continue
        seen.add(tag)
        words = re.findall(r'\w+', tag)
        if len(words) > MAX_TAG_WORDS:
            warns.append(f'ТЕГИ {level}: длиннее {MAX_TAG_WORDS} слов — «{tag}»')
        if words and set(words) <= title_words:
            warns.append(f'ТЕГИ {level}: «{tag}» целиком повторяет заголовок — толку в поиске нет')
        tags.append(tag)
    return ', '.join(tags) if tags else None


def collect_section(lines, i):
    """Собрать тело секции до следующего заголовка #/##/### /####/**Ex/---разделителя верхнего уровня."""
    body = []
    while i < len(lines):
        l = lines[i]
        if re.match(r'^#{1,4}\s', l) or l.startswith('### Words8r Sync') or EX_HDR.match(l):
            break
        body.append(l)
        i += 1
    return body, i


def parse_file(path, only_mt=None, word_start=1):
    with open(path, encoding='utf-8') as fh:
        lines = fh.read().splitlines()

    content = {
        'grammar_topics': [], 'grammar_topic_categories': [],
        'grammar_microtopics': [], 'grammar_cards': [],
        'card_exercise_index': [], 'ai_exercises': [],
        'course_word_groups': [], 'course_categories': [], 'course_words': [],
        'table_fill_exercises': [], 'true_false_exercises': [],
        'word_arrangement_exercises': [], 'multiple_choice_exercises': [],
        'text_input_exercises': [], 'dialog_restore_exercises': [],
        'matching_exercises': [], 'error_correction_exercises': [],
        'construction_meaning_exercises': [], 'transformation_exercises': [],
        'categorization_exercises': [], 'find_the_odd_exercises': [],
    }
    server = {'ai_exercise_prompts': []}
    warnings = []
    word_counter = [word_start - 1]

    topic_id = None
    group = None
    default_cat = None
    cur_mt = None
    cur_mt_cat = None
    cur_card = None

    i = 0
    n = len(lines)
    while i < n:
        line = lines[i]

        # --- навигационные разделители "# БЛОК N · ..." — пропускаем ---
        if line.startswith('# БЛОК'):
            i += 1
            continue

        # --- шапка темы (только первый "# " заголовок) ---
        if re.match(r'^#\s+(?!#)', line) and topic_id is None:
            title = line[1:].strip()
            head, i = collect_section(lines, i + 1)
            meta = HDR_META.search('\n'.join(head))
            topic_id = int(meta.group(1)) if meta else None
            order = int(meta.group(2)) if meta else 1
            is_pre = 'isPretopic:** true' in '\n'.join(head)
            desc = ''
            topic_category = None
            category_id = None
            category_desc = ''
            for h in head:
                if h.startswith('**Описание:**'):
                    desc = h.split('**', 4)[-1].strip()
                if h.startswith('**Группа слов:**'):
                    gp = h.split('**', 2)[-1].split('·')
                    group = {'id': gp[0].replace(':', '').strip(), 'nameRus': gp[1].strip(), 'order': 1}
                if h.startswith('**Категория слов:**') and '·' in h and 'source=' in h:
                    parts = h.split('**', 2)[-1].split('·')
                    default_cat = {
                        'id': parts[0].replace(':', '').strip(),
                        'nameRus': parts[1].strip(),
                        'groupId': group['id'] if group else '',
                        'order': 1,
                        'source': parts[2].replace('source=', '').strip(),
                    }
                tc = TOPIC_CATEGORY.search(h)
                if tc:
                    category_id = int(tc.group(1))
                    topic_category = {
                        'id': category_id, 'title': tc.group(2).strip(),
                        'description': '', 'order': int(tc.group(3)),
                    }
                tcd = TOPIC_CATEGORY_DESC.search(h)
                if tcd:
                    category_desc = tcd.group(1).strip()
                ws = WORD_START_HDR.search(h)
                if ws:
                    word_counter[0] = int(ws.group(1)) - 1
            if topic_category:
                topic_category['description'] = category_desc
            topic_title = title.split('·')[-1].strip() if '·' in title else title
            content['grammar_topics'].append({
                'id': topic_id, 'title': topic_title,
                'order': order, 'isPretopic': is_pre, 'description': desc,
                'categoryId': category_id,
                'searchKeywords': parse_tags(head, topic_title, f'тема {topic_id}', warnings),
            })
            if group:
                content['course_word_groups'].append(group)
            if default_cat:
                content['course_categories'].append(default_cat)
            if topic_category:
                content['grammar_topic_categories'].append(topic_category)
            continue

        # --- микротема ---
        if line.startswith('## Microtopic'):
            mt_title = line.split('—', 1)[-1].strip()
            head, i = collect_section(lines, i + 1)
            meta = HDR_META.search('\n'.join(head))
            mt_id = int(meta.group(1)) if meta else None
            mt_order = int(meta.group(2)) if meta else 1
            catm = MT_CAT.search('\n'.join(head))
            cur_mt_cat = catm.group(1) if catm else (default_cat['id'] if default_cat else None)
            cur_mt = mt_id
            if only_mt is None or mt_id == only_mt:
                # Полное двойное название "EN · RU" (UI рисует его как две строки). Нормализуем
                # пробелы вокруг разделителя к ровно " · ".
                full_title = re.sub(r'\s*·\s*', ' · ', mt_title).strip()
                content['grammar_microtopics'].append({
                    'id': mt_id, 'topicId': topic_id,
                    'title': full_title,
                    'order': mt_order,
                    'searchKeywords': parse_tags(head, full_title, f'микротема {mt_id}', warnings),
                })
            continue

        # --- Words8r Sync ---
        wm = WORDS_HDR.match(line)
        if wm:
            cat = wm.group(2) or cur_mt_cat
            body, i = collect_section(lines, i + 1)
            if only_mt is None or cur_mt == only_mt:
                src = default_cat['source'] if default_cat else 'course_words'
                content['course_words'].extend(
                    parse_words(body, cur_mt, cat, src, word_counter))
            continue

        # --- карточка ---
        if line.startswith('### Card'):
            cm = re.match(r'^### Card\s+\d+\s*·\s*(.+)$', line)
            card_title = cm.group(1).strip() if cm else ''
            head, i = collect_section(lines, i + 1)
            meta = HDR_META.search('\n'.join(head))
            card_id = int(meta.group(1)) if meta else None
            card_order = int(meta.group(2)) if meta else 1
            cur_card = {'id': card_id, 'microtopicId': cur_mt, 'title': card_title,
                        'order': card_order, 'theory': [], 'theorySummary': '',
                        'examples': [], 'clarificationOptions': []}
            active = (only_mt is None or cur_mt == only_mt)
            order_in_card = [0]
            # парсим под-секции карточки
            while i < n and not re.match(r'^#{1,3}\s', lines[i]) and not lines[i].startswith('### Card') and not lines[i].startswith('## Microtopic'):
                sub = lines[i]
                if sub.startswith('#### Theory'):
                    body, i = collect_section(lines, i + 1)
                    cur_card['theory'] = parse_theory(body)
                elif sub.startswith('#### Summary'):
                    body, i = collect_section(lines, i + 1)
                    cur_card['theorySummary'] = ' '.join(b.strip() for b in body if b.strip())
                elif sub.startswith('#### Examples'):
                    body, i = collect_section(lines, i + 1)
                    cur_card['examples'] = parse_examples(body)
                elif sub.startswith('#### Exercises'):
                    i += 1  # сами упражнения — отдельными **Ex заголовками ниже
                elif sub.startswith('#### Clarification Options'):
                    body, i = collect_section(lines, i + 1)
                    cur_card['clarificationOptions'] = parse_clarification(body)
                elif sub.startswith('#### AI Exercise'):
                    body, i = collect_section(lines, i + 1)
                    client, srv, has_example = parse_ai(body, card_id)
                    if not has_example:
                        warnings.append(
                            f"AI Exercise {client['id'] or '?'} (card {card_id}): "
                            f"нет строки 'Пример вывода AI' — нельзя оценить задание при ревью")
                    if active:
                        content['ai_exercises'].append(client)
                        server['ai_exercise_prompts'].append(srv)
                elif EX_HDR.match(sub):
                    m = EX_HDR.match(sub)
                    type_str, type_id = m.group(1).strip(), int(m.group(2))
                    body, i = collect_section(lines, i + 1)
                    parts = [p.strip() for p in type_str.split('·')]
                    base = parts[0]
                    subtype = parts[1] if len(parts) > 1 else None
                    if active:
                        # MultipleChoice пишут двумя способами: "MultipleChoice · X" или просто "X"
                        if base == 'MultipleChoice' or base in CHOICE_TYPES:
                            st = (subtype if base == 'MultipleChoice' else base) or 'CHOICE'
                            content['multiple_choice_exercises'].append(
                                ex_multiple_choice(body, type_id, st))
                            # enum HardcodedExerciseType: CHOICE→MULTIPLE_CHOICE,
                            # FORWARD_CHOICE/REVERSE_CHOICE — отдельные значения (иначе id неоднозначен)
                            key = 'MULTIPLE_CHOICE' if st == 'CHOICE' else st
                        elif base in EX_HANDLERS:
                            jkey, handler, enumkey = EX_HANDLERS[base]
                            content[jkey].append(handler(body, type_id))
                            key = enumkey
                        else:
                            warnings.append(f'НЕ РЕАЛИЗОВАН тип упражнения: {base} (card {card_id})')
                            key = None
                        if key:
                            content['card_exercise_index'].append({
                                'cardId': card_id, 'exerciseType': key,
                                'exerciseId': type_id, 'orderInCard': order_in_card[0]})
                            order_in_card[0] += 1
                else:
                    i += 1
            if active and card_id is not None:
                content['grammar_cards'].append(cur_card)
            continue

        i += 1

    return content, server, warnings


def validate(content):
    """Санити-проверки упражнений против ограничений типов. Находит сломанный контент."""
    idx = {(x['exerciseType'], x['exerciseId']): x['cardId'] for x in content['card_exercise_index']}

    def card_of(enum, eid):
        return idx.get((enum, eid), '?')

    def n_correct(opts):
        return sum(1 for o in opts if o.get('isCorrect'))

    issues = []

    def chk(cond, enum, eid, msg):
        if not cond:
            issues.append(f"[card {card_of(enum, eid)}] {enum} id={eid}: {msg}")

    for e in content['find_the_odd_exercises']:
        chk(len(e['items']) == 4, 'FIND_THE_ODD', e['id'], f"{len(e['items'])} элементов (нужно 4)")
        odd = sum(1 for i in e['items'] if i['isOdd'])
        chk(odd == 1, 'FIND_THE_ODD', e['id'], f"{odd} лишних (нужно ровно 1)")
    for e in content['true_false_exercises']:
        n = len(e['statements']); t = sum(1 for s in e['statements'] if s['isTrue'])
        chk(n == 5, 'TRUE_FALSE', e['id'], f"{n} утверждений (нужно 5)")
        # для НОВЫХ делаем мин 2/2, но легаси с другим балансом ОК — флагуем только если все одинаковые
        chk(t >= 1 and (n - t) >= 1, 'TRUE_FALSE', e['id'], "все утверждения одинаковы (нужны и верные, и неверные)")
    for e in content['transformation_exercises']:
        chk(len(e['items']) == 3, 'TRANSFORMATION', e['id'], f"{len(e['items'])} пунктов (нужно 3)")
        chk(all(i['original'] and i['transformed'] for i in e['items']), 'TRANSFORMATION', e['id'], "пустой original/transformed")
    for e in content['matching_exercises']:
        n = len(e['pairs'])
        chk(4 <= n <= 6, 'MATCHING', e['id'], f"{n} пар (нужно 4-6)")
        chk(all(p['left'] and p['right'] for p in e['pairs']), 'MATCHING', e['id'], "пустая часть пары")
    for e in content['multiple_choice_exercises']:
        enum = 'MULTIPLE_CHOICE' if e['choiceType'] == 'CHOICE' else e['choiceType']
        chk(n_correct(e['options']) == 1, enum, e['id'], f"{n_correct(e['options'])} правильных (нужно 1)")
        chk(len(e['options']) >= 2, enum, e['id'], f"{len(e['options'])} опций")
        chk(bool(e['prompt']), enum, e['id'], "пустой prompt")
    for e in content['error_correction_exercises']:
        chk(n_correct(e['options']) >= 1, 'ERROR_CORRECTION', e['id'], "0 правильных опций")
        chk(bool(e['wrongSentence']), 'ERROR_CORRECTION', e['id'], "пустое предложение")
    for e in content['construction_meaning_exercises']:
        chk(len(e['options']) == 4, 'CONSTRUCTION_MEANING', e['id'], f"{len(e['options'])} опций (нужно 4)")
        chk(n_correct(e['options']) == 1, 'CONSTRUCTION_MEANING', e['id'], f"{n_correct(e['options'])} правильных (нужно 1)")
    for e in content['dialog_restore_exercises']:
        # пропуск = целиком пустая реплика (text=null) ИЛИ '___' внутри строки
        blanks = sum(1 for l in e['lines'] if l['text'] is None or (l['text'] and '___' in l['text']))
        chk(blanks == 1, 'DIALOG_RESTORE', e['id'], f"{blanks} пропусков (нужно 1)")
        chk(n_correct(e['options']) == 1, 'DIALOG_RESTORE', e['id'], f"{n_correct(e['options'])} правильных (нужно 1)")
    for e in content['categorization_exercises']:
        chk(2 <= len(e['categories']) <= 4, 'CATEGORIZATION', e['id'], f"{len(e['categories'])} категорий (2-4)")
        chk(all(c['items'] for c in e['categories']), 'CATEGORIZATION', e['id'], "пустая категория")
    for e in content['text_input_exercises']:
        chk(len(e['items']) >= 1, 'TEXT_INPUT', e['id'], "0 пунктов")
        chk(all(it['sentence'] for it in e['items']), 'TEXT_INPUT', e['id'], "пункт без предложения")
        if e.get('wordBank'):
            chk(bool(e.get('taskDescription')), 'TEXT_INPUT', e['id'], "есть Банк, но нет строки-задания (шапки)")
            chk(len(e['wordBank']) >= 1, 'TEXT_INPUT', e['id'], "пустой Банк")
            chk(len(e['wordBank']) <= 3, 'TEXT_INPUT', e['id'],
                f"в Банке {len(e['wordBank'])} слов — держим ≤3 (капсула компактная, раскрытие не двигает layout)")
            chk(not any(it['contextRu'] for it in e['items']), 'TEXT_INPUT', e['id'],
                "у банк-задания не должно быть пер-пунктовых глоссов (contextRu) — слова только в Банке")
    for e in content['word_arrangement_exercises']:
        chk(bool(e['correctSentence']), 'WORD_ARRANGEMENT', e['id'], "нет correctSentence")
        chk(bool(e['situationRu']), 'WORD_ARRANGEMENT', e['id'], "нет задания (строка RU:/Ситуация:)")
        chk(len(e['words']) >= 2, 'WORD_ARRANGEMENT', e['id'], f"{len(e['words'])} слов")
        chk(len(e['distractors']) >= 1, 'WORD_ARRANGEMENT', e['id'], "нет дистракторов")
    for e in content['table_fill_exercises']:
        chk(len(e['rows']) >= 1, 'TABLE_FILL', e['id'], "0 строк")
        chk(all(r['hint'] and r['answer'] for r in e['rows']), 'TABLE_FILL', e['id'], "пустой hint/answer")
    for c in content['grammar_cards']:
        chk(len(c['theory']) > 0, 'CARD', c['id'], "пустая теория")
        chk(bool(c['theorySummary']), 'CARD', c['id'], "пустой theorySummary")
    return issues


def package_subdir(md_path):
    """Если MD лежит в подпапке-пакете theory/<package>/file.md (см. theory_content_guide.md →
    «Структура файлов — пакет на раздел», напр. theory/02-language-structure/...) — вернуть
    '<package>', чтобы повторить ту же структуру в seed/. Так темы и их сиды видно рядом —
    легче ориентироваться, когда тем много. Файлы прямо в theory/ (как 01-basics.md) — без подпапки."""
    parts = os.path.normpath(os.path.abspath(md_path)).split(os.sep)
    try:
        idx = len(parts) - 1 - parts[::-1].index('theory')
    except ValueError:
        return None
    sub = parts[idx + 1:-1]   # всё между 'theory' и именем файла
    return os.path.join(*sub) if sub else None


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument('md')
    ap.add_argument('--microtopic', type=int, default=None)
    ap.add_argument('--out', default=os.path.join(os.path.dirname(__file__), 'seed'))
    ap.add_argument('--name', default=None)
    args = ap.parse_args()

    # Стартовый course_word.id берётся из шапки темы (**Слова курса — стартовый ID:**) —
    # см. theory_content_guide.md → раздел 2. word_start=1 — запасной вариант для файлов
    # без Words8r Sync вообще (поле в шапке тогда не нужно и не парсится).
    content, server, warnings = parse_file(args.md, args.microtopic, word_start=1)

    # пакеты в seed/ повторяют структуру пакетов в theory/ (theory/02-lang.../X.md -> seed/02-lang.../X.json)
    out_dir = args.out
    sub = package_subdir(args.md)
    if sub:
        out_dir = os.path.join(out_dir, sub)
    os.makedirs(out_dir, exist_ok=True)

    # имя сида = имя файла без номер-префикса (01-basics.md -> basics)
    name = args.name or re.sub(r'^\d+[-_]', '', os.path.splitext(os.path.basename(args.md))[0])
    if args.microtopic:
        name += f'_mt{args.microtopic}'

    cpath = os.path.join(out_dir, f'{name}.json')
    ppath = os.path.join(out_dir, f'{name}_prompts.json')
    with open(cpath, 'w', encoding='utf-8') as f:
        json.dump(content, f, ensure_ascii=False, indent=2)
    with open(ppath, 'w', encoding='utf-8') as f:
        json.dump(server, f, ensure_ascii=False, indent=2)

    print(f'OK  content -> {cpath}')
    print(f'OK  server  -> {ppath}')
    for k, v in content.items():
        if v:
            print(f'  {k}: {len(v)}')
    # Покрытие тегами поиска — чтобы check.py показал его в сводке (теги невидимы в UI,
    # без этой строки забытая шапка обнаружится только на живых запросах).
    mts = content['grammar_microtopics']
    tagged_mt = sum(1 for mt in mts if mt.get('searchKeywords'))
    topic_tagged = any(t.get('searchKeywords') for t in content['grammar_topics'])
    print(f'TAGS: topic={"yes" if topic_tagged else "NO"} microtopics={tagged_mt}/{len(mts)}')
    if warnings:
        print('WARNINGS:')
        for w in warnings:
            print('  !', w)
    issues = validate(content)
    if issues:
        print(f'VALIDATION: {len(issues)} проблем')
        for it in issues:
            print('  x', it)
    else:
        print('VALIDATION: OK (0 проблем)')


if __name__ == '__main__':
    main()