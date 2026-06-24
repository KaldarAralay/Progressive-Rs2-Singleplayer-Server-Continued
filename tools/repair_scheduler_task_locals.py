#!/usr/bin/env python3
"""Inline CFR task locals that reuse typed parameters before scheduler calls."""

from __future__ import annotations

import re
import sys
from pathlib import Path


SCHEDULED_TASK_RE = re.compile(
    r"(?m)^([ \t]*)([A-Za-z_][A-Za-z0-9_]*) = new ([^;\n]+);\n"
    r"\1World\.getTaskScheduler\(\)\.schedule\(\(TickTask\)\2\);"
)


def repair_file(path: Path) -> int:
    text = path.read_text(encoding="utf-8")

    def replace(match: re.Match[str]) -> str:
        indent = match.group(1)
        constructor = match.group(3)
        return f"{indent}World.getTaskScheduler().schedule(new {constructor});"

    repaired, count = SCHEDULED_TASK_RE.subn(replace, text)
    if count:
        path.write_text(repaired, encoding="utf-8")
    return count


def main() -> int:
    if len(sys.argv) != 2:
        print("usage: repair_scheduler_task_locals.py <source_root>", file=sys.stderr)
        return 2
    root = Path(sys.argv[1]).resolve()
    repairs = 0
    for path in root.rglob("*.java"):
        repairs += repair_file(path)
    print(f"inlined scheduler task locals: {repairs}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
