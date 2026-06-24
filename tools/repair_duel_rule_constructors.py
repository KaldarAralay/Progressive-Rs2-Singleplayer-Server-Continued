#!/usr/bin/env python3
"""Restore missing super calls in duel-rule subclasses."""

from __future__ import annotations

import sys
from pathlib import Path


def main() -> int:
    if len(sys.argv) != 2:
        print("usage: repair_duel_rule_constructors.py <source_root>", file=sys.stderr)
        return 2

    source_root = Path(sys.argv[1]).resolve()
    duel_dir = source_root / "com/rs2/model/gameplay/duel"
    repairs = 0
    for path in sorted(duel_dir.glob("*DuelRule.java")):
        class_name = path.stem
        if class_name == "DuelRule":
            continue
        text = path.read_text(encoding="utf-8")
        old = (
            f"    public {class_name}(int n2, int n3, int n4) {{\n"
            "    }\n"
        )
        new = (
            f"    public {class_name}(int n2, int n3, int n4) {{\n"
            "        super(n2, n3, n4);\n"
            "    }\n"
        )
        count = text.count(old)
        if count == 0:
            continue
        path.write_text(text.replace(old, new), encoding="utf-8")
        repairs += count

    print(f"repaired duel rule constructors: {repairs}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
