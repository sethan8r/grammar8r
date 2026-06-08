# -*- coding: utf-8 -*-
# Одна команда: пересобрать сид + прогнать все проверки темы и дать сводку.
# Запуск:  py check.py <path-to-theory.md>   (по умолчанию — 01-basics.md)
import subprocess, sys, os, re

try:
    sys.stdout.reconfigure(encoding='utf-8', errors='replace')   # чтобы не падать на юникоде в cp1251-консоли
except Exception:
    pass

HERE = os.path.dirname(os.path.abspath(__file__))
DEFAULT = os.path.join(HERE, '..', 'phases', 'phase1', 'theory', '01-basics.md')
md = sys.argv[1] if len(sys.argv) > 1 else DEFAULT
ENV = dict(os.environ, PYTHONIOENCODING='utf-8')


def run(*args):
    r = subprocess.run([sys.executable, *args], cwd=HERE, env=ENV,
                       capture_output=True, text=True, encoding='utf-8', errors='replace')
    return (r.stdout or '') + (r.stderr or '')


def num(text, pat, default=-1):
    m = re.search(pat, text)
    return int(m.group(1)) if m else default


o1 = run(os.path.join(HERE, 'md_to_json.py'), md)
o2 = run(os.path.join(HERE, 'test', 'deepcheck.py'))
o3 = run(os.path.join(HERE, 'test', 'smell.py'))
o4 = run(os.path.join(HERE, 'test', 'id_report.py'))
o5 = run(os.path.join(HERE, 'test', 'word_dup_report.py'))

val = 0 if 'VALIDATION: OK' in o1 else num(o1, r'VALIDATION:\s*(\d+)')
deep = num(o2, r'deep issues:\s*(\d+)')
smell = num(o3, r':\s*(\d+)')          # "запахов: N"
coll = num(o4, r'collisions:\s*(\d+)')
gaps = num(o4, r'gaps[^:]*:\s*(\d+)')
worddup = num(o5, r'word duplicates[^:]*:\s*(\d+)')


def mark(n):
    return 'PASS' if n == 0 else f'FAIL ({n})'


print('=' * 44)
print('CHECK:', os.path.basename(md))
print('=' * 44)
print(f'  VALIDATION (типы/ограничения) : {mark(val)}')
print(f'  DEEP (дубли/enum/explanation) : {mark(deep)}')
print(f'  ID collisions                 : {mark(coll)}')
print(f'  SMELL (мусор)                 : {smell}  (легит-стрелки/слэши допустимы — глянуть глазами)')
print(f'  GAPS (инфо, не баг)           : {gaps}')
print(f'  WORD DUPLICATES (по курсу)    : {mark(worddup)}')
print('-' * 44)
hard_ok = (val == 0 and deep == 0 and coll == 0 and worddup == 0)
print('  =>', 'ALL GREEN ✓' if hard_ok else 'НУЖНЫ ПРАВКИ ✗')
if not hard_ok:
    print('\n--- детали валидатора ---'); print(o1[-800:])
    print('--- детали deep ---'); print(o2)
    print('--- детали id ---'); print(o4)
    if worddup:
        print('--- детали дублей слов ---'); print(o5)