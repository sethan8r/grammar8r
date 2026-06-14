# -*- coding: utf-8 -*-
# Реконструирует выбранные карточки из сида для ручной сверки с MD.
# Запуск: py verify_dump.py <id карточек...>  → _verify.txt
import json, io, sys, os

d = json.load(open(os.path.join(os.path.dirname(__file__), '..', 'seed', 'basics.json'), encoding='utf-8'))
ENUM2KEY = {
    'TABLE_FILL': 'table_fill_exercises', 'TRUE_FALSE': 'true_false_exercises',
    'WORD_ARRANGEMENT': 'word_arrangement_exercises', 'TEXT_INPUT': 'text_input_exercises',
    'DIALOG_RESTORE': 'dialog_restore_exercises', 'MATCHING': 'matching_exercises',
    'ERROR_CORRECTION': 'error_correction_exercises', 'CONSTRUCTION_MEANING': 'construction_meaning_exercises',
    'TRANSFORMATION': 'transformation_exercises', 'CATEGORIZATION': 'categorization_exercises',
    'FIND_THE_ODD': 'find_the_odd_exercises',
}
CH = {'MULTIPLE_CHOICE': 'CHOICE', 'FORWARD_CHOICE': 'FORWARD_CHOICE', 'REVERSE_CHOICE': 'REVERSE_CHOICE'}
cards = {c['id']: c for c in d['grammar_cards']}
ai = {}
for a in d['ai_exercises']:
    ai.setdefault(a['cardId'], []).append(a)

def find_ex(et, eid):
    if et in CH:
        return next((e for e in d['multiple_choice_exercises'] if e['choiceType'] == CH[et] and e['id'] == eid), None)
    return next((e for e in d[ENUM2KEY[et]] if e['id'] == eid), None) if et in ENUM2KEY else None

out = io.open('_verify.txt', 'w', encoding='utf-8')
for cid in [int(x) for x in sys.argv[1:]]:
    c = cards.get(cid)
    if not c:
        out.write(f'### CARD {cid} НЕ НАЙДЕНА\n\n'); continue
    out.write('=' * 60 + f'\nCARD {cid} (mt {c["microtopicId"]}): {c["title"]}\n' + '=' * 60 + '\n')
    out.write('THEORY:\n')
    for b in c['theory']:
        if b['type'] == 'table':
            out.write(f'  [table] {b["header"]}\n')
            for r in b['rows']:
                out.write(f'      {r}\n')
        elif b['type'] == 'list':
            out.write(f'  [list ord={b["ordered"]}]\n')
            for it in b['items']:
                out.write(f'      - {it}\n')
        elif b['type'] == 'callout':
            out.write(f'  [callout {b["variant"]}|{b["label"]}] {b["text"]}\n')
        else:
            out.write(f'  [{b["type"]}] {b["text"]}\n')
    out.write(f'SUMMARY: {c["theorySummary"]}\n')
    out.write(f'EXAMPLES: {c["examples"]}\n')
    out.write(f'CLARIF: {c["clarificationOptions"]}\n')
    exs = sorted([x for x in d['card_exercise_index'] if x['cardId'] == cid], key=lambda x: x['orderInCard'])
    for x in exs:
        e = find_ex(x['exerciseType'], x['exerciseId'])
        out.write(f'--- #{x["orderInCard"]} {x["exerciseType"]} id={x["exerciseId"]} ---\n')
        out.write('  ' + json.dumps(e, ensure_ascii=False) + '\n')
    for a in ai.get(cid, []):
        out.write(f'AI: {json.dumps(a, ensure_ascii=False)}\n')
    out.write('\n')
out.close()
print('done')