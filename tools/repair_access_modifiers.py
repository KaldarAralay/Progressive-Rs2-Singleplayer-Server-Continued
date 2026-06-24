#!/usr/bin/env python3
"""Promote generated top-level declarations needed for source recompilation."""

from __future__ import annotations

import re
import sys
from pathlib import Path


TOP_LEVEL_DECL = re.compile(
    r"(?m)^(?!public\b)((?:(?:abstract|final|strictfp)\s+|/\* synthetic \*/\s+)*)(class|interface|enum)\s+([A-Za-z_$][A-Za-z0-9_$]*)\b"
)


def promote_file(path: Path) -> tuple[bool, int]:
    text = path.read_text(encoding="utf-8")
    match = TOP_LEVEL_DECL.search(text)
    if match is None:
        return False, 0

    class_name = match.group(3)
    kind = match.group(2)
    text = text[: match.start()] + "public " + text[match.start() :]
    constructor_count = 0

    if kind == "class":
        constructor_pattern = re.compile(
            rf"(?m)^    (?!public\b|private\b|protected\b)((?:/\* synthetic \*/\s+)?{re.escape(class_name)}\()"
        )

        def constructor_replacement(ctor_match: re.Match[str]) -> str:
            nonlocal constructor_count
            constructor_count += 1
            return "    public " + ctor_match.group(1)

        text = constructor_pattern.sub(constructor_replacement, text)

    path.write_text(text, encoding="utf-8")
    return True, constructor_count


def main() -> int:
    if len(sys.argv) != 2:
        print("usage: repair_access_modifiers.py <source_root>", file=sys.stderr)
        return 2

    root = Path(sys.argv[1]).resolve()
    if not root.exists():
        raise SystemExit(f"missing source root: {root}")

    promoted = 0
    constructors = 0
    for path in sorted(root.rglob("*.java")):
        changed, constructor_count = promote_file(path)
        if changed:
            promoted += 1
            constructors += constructor_count

    print(f"promoted top-level declarations: {promoted}")
    print(f"promoted package-private constructors: {constructors}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
