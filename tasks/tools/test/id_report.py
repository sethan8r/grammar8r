# -*- coding: utf-8 -*-
# Генерирует канон по ID из всех сидов: next-free по типам + диапазоны по темам + коллизии.
# Выход: tasks/phases/phase1/theory/_id-registry.md (РУКАМИ НЕ ПРАВИТЬ — пересобирается отсюда).
# Запуск из tools/:  py test/id_report.py
import json, os, io, glob
from collections import defaultdict

HERE = os.path.dirname(__file__)
SEED = os.path.join(HERE, '..', 'seed')
REGISTRY = os.path.join(HERE, '..', '..', 'phases', 'phase1', 'theory', '_id-registry.md')

EX = {
    'table_fill_exercises': 'TableFill', 'true_false_exercises': 'TrueFalse',
    'word_arrangement_exercises': 'WordArrangement', 'text_input_exercises': 'TextInput',
    'dialog_restore_exercises': 'DialogRestore', 'matching_exercises': 'Matching',
    'error_correction_exercises': 'ErrorCorrection', 'construction_meaning_exercises': 'ConstructionMeaning',
    'transformation_exercises': 'Transformation', 'categorization_exercises': 'Categorization',
    'find_the_odd_exercises': 'FindTheOdd',
}

files = sorted(f for f in glob.glob(os.path.join(SEED, '*.json')) if not f.endswith('_prompts.json'))
tracks = defaultdict(list)                          # track -> [(theme, id)]
per_theme = defaultdict(lambda: defaultdict(list))  # theme -> track -> [ids]
themes = []

for f in files:
    theme = os.path.splitext(os.path.basename(f))[0]
    themes.append(theme)
    d = json.load(open(f, encoding='utf-8'))

    def add(track, ids):
        for i in ids:
            tracks[track].append((theme, i))
            per_theme[theme][track].append(i)

    add('Topic', [t['id'] for t in d.get('grammar_topics', [])])
    add('Microtopic', [m['id'] for m in d.get('grammar_microtopics', [])])
    add('Card', [c['id'] for c in d.get('grammar_cards', [])])
    add('course_word', [w['id'] for w in d.get('course_words', [])])
    for key, name in EX.items():
        add(name, [e['id'] for e in d.get(key, [])])
    mc = defaultdict(list)
    for e in d.get('multiple_choice_exercises', []):
        mc[e['choiceType']].append(e['id'])
    for ct, ids in mc.items():
        add('MultipleChoice·' + ct, ids)
    add('AiExercise', [a['id'] for a in d.get('ai_exercises', [])])   # строковые id

# коллизии: один id встречается в треке более одного раза (между темами или внутри)
collisions = []
for track, entries in sorted(tracks.items()):
    seen = defaultdict(list)
    for theme, i in entries:
        seen[i].append(theme)
    for i, ths in sorted(seen.items(), key=lambda kv: str(kv[0])):
        if len(ths) > 1:
            collisions.append(f'{track}: id={i!r} встречается {len(ths)}× (темы: {ths})')

# пропуски: дыры в нумерации (информационно — на дубли не влияет, но видно)
gaps = []
for track, entries in sorted(tracks.items()):
    nums = sorted(i for _, i in entries if isinstance(i, int))
    if not nums:
        continue
    missing = sorted(set(range(min(nums), max(nums) + 1)) - set(nums))
    if missing:
        shown = missing if len(missing) <= 15 else missing[:15] + ['…']
        gaps.append(f'{track}: пропущены {shown} (диапазон {min(nums)}–{max(nums)})')

# next free (только числовые треки)
nextfree = {}
for track, entries in tracks.items():
    nums = [i for _, i in entries if isinstance(i, int)]
    if nums:
        nextfree[track] = max(nums) + 1

out = io.open(REGISTRY, 'w', encoding='utf-8')
out.write('# ID Registry — единый канон распределения ID\n\n')
out.write('> ⚙️ ГЕНЕРИРУЕТСЯ `tools/test/id_report.py` из сидов. **Руками не править.**\n')
out.write('> Перед новой темой — смотри «Next free». После написания — перегенерируй и проверь коллизии.\n\n')
out.write('## Next free (отсюда продолжает новая тема)\n\n| Трек | Next free |\n|------|-----------|\n')
for t in sorted(nextfree):
    out.write(f'| {t} | {nextfree[t]} |\n')
out.write('\n## Диапазоны по темам\n')
for theme in themes:
    out.write(f'\n### {theme}\n')
    for track in sorted(per_theme[theme]):
        ids = [i for i in per_theme[theme][track] if isinstance(i, int)]
        if ids:
            out.write(f'- {track}: {min(ids)}–{max(ids)} ({len(ids)})\n')
out.write('\n## Коллизии ID (КРИТИЧНО — должно быть пусто)\n\n')
out.write(('\n'.join('- ⚠️ ' + c for c in collisions) + '\n') if collisions else 'нет — все ID уникальны ✓\n')
out.write('\n## Пропуски в нумерации (информационно — на дубли не влияют)\n\n')
out.write(('\n'.join('- ' + g for g in gaps) + '\n') if gaps else 'нет — нумерация сплошная\n')
out.close()

print('themes:', len(themes), '| collisions:', len(collisions), '| gaps (инфо):', len(gaps))
for c in collisions:
    print('  ! КОЛЛИЗИЯ:', c)
for g in gaps:
    print('  · пропуск:', g)