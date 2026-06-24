#!/usr/bin/env python3
"""Move misplaced constructor super(...) calls to the first statement."""

from __future__ import annotations

import re
import sys
from pathlib import Path


CLASS_DECL = re.compile(
    r"(?m)^public\s+(?:(?:abstract|final|strictfp)\s+|/\* synthetic \*/\s+)*class\s+([A-Za-z_$][A-Za-z0-9_$]*)\b"
)


def find_matching_brace(text: str, open_index: int) -> int:
    depth = 0
    index = open_index
    while index < len(text):
        char = text[index]
        if char == "{":
            depth += 1
        elif char == "}":
            depth -= 1
            if depth == 0:
                return index
        index += 1
    raise ValueError("unmatched constructor brace")


def repair_file(path: Path) -> int:
    text = path.read_text(encoding="utf-8")
    class_match = CLASS_DECL.search(text)
    if class_match is None:
        return 0

    class_name = class_match.group(1)
    constructor_pattern = re.compile(
        rf"(?m)^    (?:public|private|protected)?\s*(?:/\* synthetic \*/\s+)?{re.escape(class_name)}\([^)]*\) \{{"
    )

    replacements: list[tuple[int, int, str]] = []
    repairs = 0
    for match in constructor_pattern.finditer(text):
        end = find_matching_brace(text, match.end() - 1) + 1
        constructor = text[match.start() : end]
        lines = constructor.splitlines(keepends=True)
        if len(lines) < 3:
            continue

        first_statement = None
        super_line = None
        for index in range(1, len(lines) - 1):
            stripped = lines[index].strip()
            if not stripped or stripped.startswith("//") or stripped.startswith("/*") or stripped.startswith("*"):
                continue
            if first_statement is None:
                first_statement = index
            if re.match(r"^super\([^;]*\);$", stripped):
                super_line = index
                break

        if first_statement is None or super_line is None or super_line == first_statement:
            continue

        moved = lines.pop(super_line)
        lines.insert(first_statement, moved)
        replacements.append((match.start(), end, "".join(lines)))
        repairs += 1

    if not replacements:
        return 0

    pieces: list[str] = []
    cursor = 0
    for start, end, replacement in replacements:
        pieces.append(text[cursor:start])
        pieces.append(replacement)
        cursor = end
    pieces.append(text[cursor:])
    path.write_text("".join(pieces), encoding="utf-8")
    return repairs


def main() -> int:
    if len(sys.argv) != 2:
        print("usage: repair_constructor_super_order.py <source_root>", file=sys.stderr)
        return 2

    root = Path(sys.argv[1]).resolve()
    if not root.exists():
        raise SystemExit(f"missing source root: {root}")

    repairs = 0
    for path in sorted(root.rglob("*.java")):
        repairs += repair_file(path)

    print(f"reordered constructor super calls: {repairs}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
