# -*- coding: utf-8 -*-
# Временный: раскидывает RAW-MD каждого упражнения по файлам _dump/<Тип>.txt
# для визуального сравнения форматов внутри типа.
import re, os, io, sys

# путь к theory-MD: аргументом, иначе дефолт на 01-basics
SRC = sys.argv[1] if len(sys.argv) > 1 else os.path.join(
    os.path.dirname(__file__), '..', '..', 'phases', 'phase1', 'theory', '01-basics.md')
EX_HDR = re.compile(r'^\*\*Ex\s+(\d+)\s*·\s*(.+?)\*\*\s*\*\(ID:\s*(\d+)\)\*')
CARD = re.compile(r'^### Card\s+(\d+)\s*·\s*(.+)$')
STOP = re.compile(r'^(#{1,4}\s|\*\*Ex\s|### Card|## Microtopic|# БЛОК)')

lines = io.open(SRC, encoding='utf-8').read().splitlines()
buckets = {}
cur_card = '?'
i = 0
while i < len(lines):
    cm = CARD.match(lines[i])
    if cm:
        cur_card = f"Card {cm.group(1)} · {cm.group(2)}"
    m = EX_HDR.match(lines[i])
    if m:
        gnum, type_str, tid = m.group(1), m.group(2).strip(), m.group(3)
        base = type_str.split('·')[0].strip()
        if base in ('CHOICE', 'FORWARD_CHOICE', 'REVERSE_CHOICE'):
            base = 'MultipleChoice'
        block = [f'--- [{cur_card}]  Ex {gnum} · {type_str} (ID {tid}) ---']
        j = i + 1
        body = []
        while j < len(lines) and not STOP.match(lines[j]):
            body.append(lines[j])
            j += 1
        # обрезаем хвостовые пустые строки и одиночный ---
        while body and (not body[-1].strip() or body[-1].strip() == '---'):
            body.pop()
        block += body
        buckets.setdefault(base, []).append('\n'.join(block))
        i = j
        continue
    i += 1

outdir = os.path.join(os.path.dirname(__file__), '_dump')
os.makedirs(outdir, exist_ok=True)
for base, blocks in sorted(buckets.items()):
    p = os.path.join(outdir, f'{base}.txt')
    io.open(p, 'w', encoding='utf-8').write(f'=== {base}: {len(blocks)} упражнений ===\n\n' + '\n\n'.join(blocks) + '\n')
    print(f'{base}: {len(blocks)} -> {p}')