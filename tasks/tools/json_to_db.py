# -*- coding: utf-8 -*-
#
# Шаг C конвейера контента: JSON-сиды  ->  content.db (read-only БД в assets).
#
# Источник правды по СТРУКТУРЕ БД — экспортированная Room-схема
# (grammar-app/schemas/.../ContentDatabase/1.json). CREATE TABLE / индексы / identity hash
# берём ИЗ НЕЁ, руками SQL не пишем: при createFromAsset Room сверяет файл со своими Entity,
# и любое расхождение (тип/порядок колонок, NOT NULL, индекс, версия) = краш при открытии.
#
# Источник правды по ДАННЫМ — JSON-сиды (tasks/tools/seed/**/*.json), ключи верхнего уровня
# которых = имена таблиц. Файлы *_prompts.json НЕ входят (серверный секрет, не должен попасть в APK).
#
# Скрипт вызывается двумя способами:
#   - вручную:        py json_to_db.py
#   - из Gradle:      py json_to_db.py --schema <abs> --out <abs>   (таск generateContentDb)
#
# content.db — build-артефакт, в VCS НЕ коммитим (см. .gitignore). Схемы — коммитим.

import argparse
import glob
import json
import os
import sqlite3
import sys

try:
    sys.stdout.reconfigure(encoding="utf-8", errors="replace")  # не падать на юникоде в cp1251-консоли
except Exception:
    pass

HERE = os.path.dirname(os.path.abspath(__file__))
REPO = os.path.normpath(os.path.join(HERE, "..", ".."))

_CONTENT_SCHEMA_DIR = os.path.join(
    REPO, "grammar-app", "schemas",
    "dev.sethan8r.grammar.app.data.local.content.ContentDatabase",
)


def _latest_content_schema():
    """Последняя версия Room-схемы content.db (макс. N.json). При bump версии БД путь не правим —
    берём свежий N.json автоматически. Вызывается в рантайме (после ksp N.json уже на месте)."""
    versions = []
    for f in glob.glob(os.path.join(_CONTENT_SCHEMA_DIR, "*.json")):
        stem = os.path.splitext(os.path.basename(f))[0]
        if stem.isdigit():
            versions.append((int(stem), f))
    if not versions:
        return os.path.join(_CONTENT_SCHEMA_DIR, "1.json")  # запасной путь (пусть упадёт понятно ниже)
    return max(versions)[1]


DEFAULT_SCHEMA = _latest_content_schema()
DEFAULT_OUT = os.path.join(REPO, "grammar-app", "src", "main", "assets", "content.db")
SEED_DIR = os.path.join(HERE, "seed")

# room_master_table — служебная таблица Room. Если в ней лежит правильный identity_hash и у файла
# верный PRAGMA user_version, Room открывает БД вообще без валидации схемы и без миграции.
# Значения-константы из RoomMasterTable (id всегда 42).
ROOM_MASTER_ID = 42

# Категории/группы объявляются в КАЖДОЙ теме заново (раздел общий) — дедуп по id через OR IGNORE.
# Остальные таблицы: обычный INSERT, коллизия id между файлами = громкое падение (баг авторства).
DEDUP_TABLES = {
    "grammar_topic_categories",
    "course_word_groups",
    "course_categories",
}


def load_schema(schema_path):
    """Читает Room-схему -> (version, identity_hash, [таблицы по порядку])."""
    with open(schema_path, encoding="utf-8") as f:
        schema = json.load(f)
    db = schema["database"]
    tables = []
    for e in db["entities"]:
        tables.append({
            "name": e["tableName"],
            "create_sql": e["createSql"],
            "columns": [fld["columnName"] for fld in e["fields"]],
            "indices": [idx["createSql"] for idx in e.get("indices", [])],
        })
    return db["version"], db["identityHash"], tables


def collect_seed_files():
    """Все seed/**/*.json, КРОМЕ *_prompts.json. Сортируем — детерминированный порядок вставки."""
    files = glob.glob(os.path.join(SEED_DIR, "**", "*.json"), recursive=True)
    files = [f for f in files if not os.path.basename(f).endswith("_prompts.json")]
    return sorted(files)


def to_db_value(v):
    """JSON-значение -> значение для sqlite.
    bool -> 0/1; None -> NULL; list/dict -> сырая JSON-строка (колонка TEXT, типизированный разбор —
    в мапперах data/mapper приложения); остальное как есть (str/int/float)."""
    if isinstance(v, bool):
        return 1 if v else 0
    if isinstance(v, (list, dict)):
        return json.dumps(v, ensure_ascii=False, separators=(",", ":"))
    return v


def create_schema(conn, version, identity_hash, tables):
    for t in tables:
        name = t["name"]
        conn.execute(t["create_sql"].replace("${TABLE_NAME}", name))
        for idx_sql in t["indices"]:
            conn.execute(idx_sql.replace("${TABLE_NAME}", name))
    # user_version КРИТИЧЕН: при 0 Room решит, что нужна миграция 0->version, не найдёт её и по
    # destructive fallback СОТРЁТ только что засиданные таблицы. Ставим версию из схемы.
    conn.execute(f"PRAGMA user_version = {version}")
    conn.execute("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY, identity_hash TEXT)")
    conn.execute(
        "INSERT OR REPLACE INTO room_master_table (id, identity_hash) VALUES (?, ?)",
        (ROOM_MASTER_ID, identity_hash),
    )


def insert_rows(conn, table, rows):
    """Вставка строк. Колонки = пересечение ключей JSON с колонками таблицы:
    так card_exercise_index без `id` (autoincrement) и пропущенные nullable-поля обрабатываются сами."""
    cols = table["columns"]
    name = table["name"]
    verb = "INSERT OR IGNORE INTO" if name in DEDUP_TABLES else "INSERT INTO"
    inserted = 0
    for row in rows:
        present = [c for c in cols if c in row]
        col_list = ", ".join(f"`{c}`" for c in present)
        placeholders = ", ".join("?" for _ in present)
        values = [to_db_value(row[c]) for c in present]
        sql = f"{verb} `{name}` ({col_list}) VALUES ({placeholders})"
        cur = conn.execute(sql, values)
        inserted += cur.rowcount if cur.rowcount > 0 else 0
    return inserted


def build(schema_path, out_path):
    version, identity_hash, tables = load_schema(schema_path)
    table_by_name = {t["name"]: t for t in tables}

    seed_files = collect_seed_files()
    if not seed_files:
        print(f"FAIL  нет сидов в {SEED_DIR}")
        return 1

    # Сборка с нуля — детерминизм: старый файл удаляем.
    os.makedirs(os.path.dirname(out_path), exist_ok=True)
    if os.path.exists(out_path):
        os.remove(out_path)

    conn = sqlite3.connect(out_path)
    try:
        conn.execute("PRAGMA foreign_keys = OFF")  # FK в content.db нет; на всякий случай явно
        create_schema(conn, version, identity_hash, tables)

        warnings = []
        for path in seed_files:
            with open(path, encoding="utf-8") as f:
                data = json.load(f)
            rel = os.path.relpath(path, SEED_DIR)
            for key, rows in data.items():
                if key not in table_by_name:
                    warnings.append(f"  ! {rel}: неизвестный ключ '{key}' — пропущен (не таблица схемы)")
                    continue
                if not isinstance(rows, list):
                    warnings.append(f"  ! {rel}: ключ '{key}' не список — пропущен")
                    continue
                insert_rows(conn, table_by_name[key], rows)

        conn.commit()

        # Сводка: сколько строк реально легло в каждую непустую таблицу.
        print(f"OK  content.db -> {out_path}")
        print(f"    identity_hash = {identity_hash}, user_version = {version}, сидов = {len(seed_files)}")
        total = 0
        for t in tables:
            n = conn.execute(f"SELECT COUNT(*) FROM `{t['name']}`").fetchone()[0]
            total += n
            if n:
                print(f"    {t['name']}: {n}")
        print(f"    ИТОГО строк: {total}")
        for w in warnings:
            print(w)
    finally:
        conn.close()
    return 0


def main():
    ap = argparse.ArgumentParser(description="Сборка content.db из JSON-сидов по Room-схеме.")
    ap.add_argument("--schema", default=DEFAULT_SCHEMA, help="путь к ContentDatabase/1.json")
    ap.add_argument("--out", default=DEFAULT_OUT, help="путь к выходному content.db")
    args = ap.parse_args()

    if not os.path.exists(args.schema):
        print(f"FAIL  схема не найдена: {args.schema}")
        return 1
    return build(args.schema, args.out)


if __name__ == "__main__":
    sys.exit(main())