# -*- coding: utf-8 -*-
# Проверка дублей слов (word + translation) ГЛОБАЛЬНО по всему курсу — не только внутри категории.
# Критерий дубля = одинаковое написание И одинаковый перевод (см. theory_content_guide.md → раздел 6).
# Разный перевод одного и того же написания — НЕ дубль (омонимы: am-глагол / am=a.m. — оба остаются).
#
# Идея: пользователь идёт по курсу линейно и параллельно учит слова — встретить слово+перевод
# во второй раз (даже в другой категории/теме) означает дублирующийся прогресс на одно и то же слово.
#
# Порядок "сверху вниз" определяется по course_word.id — он назначается сквозным и стабильным
# по мере написания курса (см. _id-registry.md), поэтому отражает порядок прохождения надёжнее,
# чем имя файла сида.
#
# Запуск из tools/:  py test/word_dup_report.py
import json, os, glob
from collections import defaultdict

HERE = os.path.dirname(__file__)
SEED = os.path.join(HERE, '..', 'seed')

files = sorted(f for f in glob.glob(os.path.join(SEED, '**', '*.json'), recursive=True) if not f.endswith('_prompts.json'))

microtopic_title = {}   # microtopicId -> title
microtopic_topic = {}   # microtopicId -> topicId
topic_title = {}        # topicId -> title

entries = []  # [{id, word, translation, theme, microtopicId, categoryId}]

for f in files:
    theme = os.path.splitext(os.path.basename(f))[0]
    d = json.load(open(f, encoding='utf-8'))
    for t in d.get('grammar_topics', []):
        topic_title[t['id']] = t['title']
    for m in d.get('grammar_microtopics', []):
        microtopic_title[m['id']] = m['title']
        microtopic_topic[m['id']] = m['topicId']
    for w in d.get('course_words', []):
        entries.append({
            'id': w['id'],
            'word': w['word'],
            'translation': w['translation'],
            'theme': theme,
            'microtopicId': w.get('microtopicId'),
            'categoryId': w.get('categoryId'),
        })

# сортировка "сверху вниз по курсу" — по course_word.id (сквозной, стабильный, отражает порядок написания)
entries.sort(key=lambda e: e['id'])


def where(e):
    mid = e['microtopicId']
    mt = microtopic_title.get(mid, '?')
    tp = topic_title.get(microtopic_topic.get(mid), '?')
    return f"тема «{tp}» → микротема «{mt}» (microtopicId={mid}, файл сида: {e['theme']}, категория: {e['categoryId']})"


groups = defaultdict(list)
for e in entries:
    groups[(e['word'], e['translation'])].append(e)

dups = []
for (word, translation), group in groups.items():
    if len(group) > 1:
        first, *repeats = group   # первый по course_word.id = тот, где слово встретилось впервые
        for rep in repeats:
            dups.append((word, translation, first, rep))

dups.sort(key=lambda d: d[3]['id'])  # сортируем отчёт по id повторного вхождения — тоже "сверху вниз"

print(f'word duplicates (word+translation): {len(dups)}')
for word, translation, first, rep in dups:
    print(f"  ! '{word}' — «{translation}»")
    print(f"      впервые встретилось:  {where(first)}  [course_word id={first['id']}]")
    print(f"      ПОВТОР (править тут): {where(rep)}  [course_word id={rep['id']}]")