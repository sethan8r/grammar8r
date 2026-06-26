# -*- coding: utf-8 -*-
# Глубокий чек сида: дубли опций, enum'ы, ширина таблиц, пустые поля, порядок, explanation.
# Запуск: py test/deepcheck.py [seed_name]   (по умолчанию — basics)
import json, io, os, sys
from collections import defaultdict

SEED = os.path.join(os.path.dirname(__file__), '..', 'seed')
_name = sys.argv[1] if len(sys.argv) > 1 else 'basics'
d = json.load(open(os.path.normpath(os.path.join(SEED, _name + '.json')), encoding='utf-8'))
prompts = json.load(open(os.path.normpath(os.path.join(SEED, _name + '_prompts.json')), encoding='utf-8'))['ai_exercise_prompts']

idx = {(x['exerciseType'], x['exerciseId']): x['cardId'] for x in d['card_exercise_index']}
issues = []

def card_of(enum, eid):
    return idx.get((enum, eid), '?')

def chk(cond, enum, eid, msg):
    if not cond:
        issues.append(f"[card {card_of(enum, eid)}] {enum} id={eid}: {msg}")

def dups(opts):
    t = [o['text'] for o in opts]
    return len(t) != len(set(t))

for e in d['multiple_choice_exercises']:
    enum = 'MULTIPLE_CHOICE' if e['choiceType'] == 'CHOICE' else e['choiceType']
    chk(not dups(e['options']), enum, e['id'], "дублирующиеся опции")
    chk(2 <= len(e['options']) <= 4, enum, e['id'], f"{len(e['options'])} опций")
    chk(all(o['text'] for o in e['options']), enum, e['id'], "пустая опция")
for e in d['error_correction_exercises']:
    chk(not dups(e['options']), 'ERROR_CORRECTION', e['id'], "дублирующиеся опции")
    chk(all(o['text'] for o in e['options']), 'ERROR_CORRECTION', e['id'], "пустая опция")
for e in d['construction_meaning_exercises']:
    chk(not dups(e['options']), 'CONSTRUCTION_MEANING', e['id'], "дублирующиеся опции")
for e in d['dialog_restore_exercises']:
    chk(not dups(e['options']), 'DIALOG_RESTORE', e['id'], "дублирующиеся опции")
    chk(all(o['text'] for o in e['options']), 'DIALOG_RESTORE', e['id'], "пустая опция")
for e in d['true_false_exercises']:
    # ru может быть пустым: с Present Simple колонка RU убрана (canon, guide §4). en обязателен.
    chk(all(s['en'] for s in e['statements']), 'TRUE_FALSE', e['id'], "пустой en")
for e in d['matching_exercises']:
    chk(all(p['left'] != p['right'] for p in e['pairs']), 'MATCHING', e['id'], "left == right")
for e in d['word_arrangement_exercises']:
    chk(all(w for w in e['words']), 'WORD_ARRANGEMENT', e['id'], "пустое слово")
    chk(all(dd for dd in e['distractors']), 'WORD_ARRANGEMENT', e['id'], "пустой дистрактор")
for e in d['categorization_exercises']:
    allitems = [it for c in e['categories'] for it in c['items']]
    chk(len(allitems) == len(set(allitems)), 'CATEGORIZATION', e['id'], "дублирующиеся элементы между колонками")

for a in d['ai_exercises']:
    chk(a['inputMode'] in ('FREE_WRITE', 'FILL_BLANKS'), 'AI', a['id'], f"inputMode={a['inputMode']!r}")
    chk(a['wordsSource'] in ('NONE', 'GENERAL', 'INFORMAL_ENGLISH', 'VERB_FORMS'), 'AI', a['id'], f"wordsSource={a['wordsSource']!r}")
    chk(bool(a['title']) and bool(a['userInstruction']), 'AI', a['id'], "пустой title/userInstruction")
pid = {p['id']: p for p in prompts}
for a in d['ai_exercises']:
    p = pid.get(a['id'])
    chk(p is not None, 'AI', a['id'], "нет промта на сервере")
    if p:
        chk(bool(p.get('promptTemplate')), 'AI', a['id'], "пустой promptTemplate")
        chk(bool(p.get('aiConfigProfile')), 'AI', a['id'], "пустой aiConfigProfile")

for c in d['grammar_cards']:
    for b in c['theory']:
        if b['type'] == 'table':
            chk(all(len(r) == len(b['header']) for r in b['rows']), 'CARD', c['id'],
                f"таблица ширина != заголовка ({len(b['header'])})")
    chk(all(ex['ru'] and ex['en'] for ex in c['examples']), 'CARD', c['id'], "пример без ru/en")
    chk(len(c['clarificationOptions']) >= 1, 'CARD', c['id'], "нет clarificationOptions")
for w in d['course_words']:
    chk(bool(w['word']) and bool(w['translation']), 'WORD', w['id'], f"пустое слово/перевод {w['word']!r}")

byc = defaultdict(list)
for x in d['card_exercise_index']:
    byc[x['cardId']].append(x['orderInCard'])
for cid, orders in byc.items():
    chk(sorted(orders) == list(range(len(orders))), 'CARD', cid, f"orderInCard не 0..n: {sorted(orders)}")

# explanation обязателен у каждого упражнения
KEY2ENUM = {
    'error_correction_exercises': 'ERROR_CORRECTION', 'true_false_exercises': 'TRUE_FALSE',
    'word_arrangement_exercises': 'WORD_ARRANGEMENT', 'table_fill_exercises': 'TABLE_FILL',
    'matching_exercises': 'MATCHING', 'transformation_exercises': 'TRANSFORMATION',
    'categorization_exercises': 'CATEGORIZATION', 'find_the_odd_exercises': 'FIND_THE_ODD',
    'construction_meaning_exercises': 'CONSTRUCTION_MEANING', 'dialog_restore_exercises': 'DIALOG_RESTORE',
    'text_input_exercises': 'TEXT_INPUT',
}
for e in d['multiple_choice_exercises']:
    enum = 'MULTIPLE_CHOICE' if e['choiceType'] == 'CHOICE' else e['choiceType']
    chk(bool(e.get('explanation', '').strip()), enum, e['id'], "пустой explanation")
for k, enum in KEY2ENUM.items():
    for e in d[k]:
        chk(bool(e.get('explanation', '').strip()), enum, e['id'], "пустой explanation")

io.open('_deep.txt', 'w', encoding='utf-8').write(
    f'ВСЕГО: {len(issues)}\n\n' + '\n'.join(issues) if issues else 'ЧИСТО — 0')
print('deep issues:', len(issues))