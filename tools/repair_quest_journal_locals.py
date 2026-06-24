#!/usr/bin/env python3
"""Repair CFR Player/String[] slot reuse in quest journal builders."""

from __future__ import annotations

import re
import sys
from pathlib import Path


SIGNATURE = "String[] buildQuestJournal(Player stringArray, int n)"


def repair_file(path: Path) -> bool:
    text = path.read_text(encoding="utf-8")
    if SIGNATURE not in text:
        return False

    text = text.replace(SIGNATURE, "String[] buildQuestJournal(Player player, int n)")
    text = text.replace("stringArray.", "player.")
    text = text.replace("(String.valueOf(stringArray", "(String.valueOf(player")
    text = re.sub(r"while \((n\d+) < player\.length\)", r"while (\1 < stringArray.length)", text)

    assignment_return = re.compile(
        r"(?ms)^(?P<indent>\s*)stringArray = (?P<array>new String\[\]\{.*?\});\n(?P=indent)return stringArray;",
        re.MULTILINE,
    )

    def replace_assignment(match: re.Match[str]) -> str:
        return f"{match.group('indent')}return {match.group('array')};"

    text = assignment_return.sub(replace_assignment, text)
    path.write_text(text, encoding="utf-8")
    return True


def main() -> int:
    if len(sys.argv) != 2:
        print("usage: repair_quest_journal_locals.py <source_root>", file=sys.stderr)
        return 2

    root = Path(sys.argv[1]).resolve()
    if not root.exists():
        raise SystemExit(f"missing source root: {root}")

    repaired = 0
    for path in sorted(root.rglob("*.java")):
        if repair_file(path):
            repaired += 1

    print(f"repaired quest journal locals: {repaired}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
