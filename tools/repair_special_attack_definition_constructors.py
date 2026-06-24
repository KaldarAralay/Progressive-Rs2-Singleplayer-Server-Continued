#!/usr/bin/env python3
"""Restore missing super calls in special-attack definition subclasses."""

from __future__ import annotations

import sys
from pathlib import Path


def main() -> int:
    if len(sys.argv) != 2:
        print("usage: repair_special_attack_definition_constructors.py <source_root>", file=sys.stderr)
        return 2

    source_root = Path(sys.argv[1]).resolve()
    special_dir = source_root / "com/rs2/model/combat/special"
    repairs = 0
    for path in sorted(special_dir.glob("*SpecialDefinition.java")):
        class_name = path.stem
        if class_name == "SpecialAttackDefinition":
            continue
        text = path.read_text(encoding="utf-8")
        old = (
            f"    public {class_name}(int n2, String ... stringArray) {{\n"
            "    }\n"
        )
        new = (
            f"    public {class_name}(int n2, String ... stringArray) {{\n"
            "        super(n2, stringArray);\n"
            "    }\n"
        )
        count = text.count(old)
        if count == 0:
            continue
        path.write_text(text.replace(old, new), encoding="utf-8")
        repairs += count

    print(f"repaired special attack definition constructors: {repairs}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
