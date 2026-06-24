#!/usr/bin/env python3
"""Repair CFR enum constructor parameter corruption using unsugared output.

CFR sometimes keeps nice enum constant syntax while losing constructor
parameters, emitting invalid locals such as `void var3_2;`. A second CFR pass
with `--sugarenums false` exposes the real constructor descriptors and bodies.
This script copies only those constructor signatures/bodies back into the
normal enum source.
"""

from __future__ import annotations

import re
import sys
from dataclasses import dataclass
from pathlib import Path


@dataclass(frozen=True)
class Constructor:
    start: int
    end: int
    text: str


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


def split_params(params: str) -> list[str]:
    params = params.strip()
    if not params:
        return []
    return [part.strip() for part in params.split(",")]


def find_constructors(text: str, class_name: str, include_synthetic: bool = False) -> list[Constructor]:
    constructors: list[Constructor] = []
    prefix = r"(?:private |/\* synthetic \*/ )" if include_synthetic else r"private "
    pattern = re.compile(rf"(?m)^    {prefix}{re.escape(class_name)}\(([^)]*)\) \{{")
    for match in pattern.finditer(text):
        end = find_matching_brace(text, match.end() - 1) + 1
        constructors.append(Constructor(match.start(), end, text[match.start():end]))
    return constructors


def build_sugared_constructor(unsugared: Constructor, class_name: str) -> str:
    header_end = unsugared.text.index("{")
    params_match = re.search(rf"private {re.escape(class_name)}\(([^)]*)\)", unsugared.text[:header_end])
    if not params_match:
        raise ValueError(f"missing unsugared constructor params for {class_name}")

    params = split_params(params_match.group(1))
    if len(params) < 2:
        raise ValueError(f"unexpected unsugared enum constructor for {class_name}: {params_match.group(1)}")
    params = params[2:]

    body = unsugared.text[header_end + 1 : -1].splitlines()
    body = [line for line in body if not line.strip().startswith("super(")]
    while body and not body[0].strip():
        body.pop(0)
    while body and not body[-1].strip():
        body.pop()

    lines = [f"    private {class_name}({', '.join(params)}) {{"]
    lines.extend(body)
    lines.append("    }")
    return "\n".join(lines)


def repair_file(normal_file: Path, unsugared_file: Path) -> bool:
    text = normal_file.read_text(encoding="utf-8")
    if "void " not in text:
        return False

    raw_enum_mode = "extends Enum" in text
    class_match = re.search(r"(?m)^\s*(?:public\s+)?(?:abstract\s+|final\s+)?enum\s+([A-Za-z_$][A-Za-z0-9_$]*)\b", text)
    if class_match is None and raw_enum_mode:
        class_match = re.search(r"(?m)^\s*(?:public\s+)?(?:abstract\s+|final\s+)?class\s+([A-Za-z_$][A-Za-z0-9_$]*)\b", text)
    if not class_match:
        return False
    class_name = class_match.group(1)

    unsugared_text = unsugared_file.read_text(encoding="utf-8")
    normal_ctors = find_constructors(text, class_name, include_synthetic=raw_enum_mode)
    damaged_ctors = [ctor for ctor in normal_ctors if "void " in ctor.text]
    if not damaged_ctors:
        return False

    unsugared_ctors = find_constructors(unsugared_text, class_name, include_synthetic=raw_enum_mode)
    if len(unsugared_ctors) < len(damaged_ctors):
        raise SystemExit(
            f"{normal_file}: found {len(damaged_ctors)} damaged constructors but only "
            f"{len(unsugared_ctors)} unsugared constructors"
        )

    if raw_enum_mode:
        replacements = [ctor.text for ctor in unsugared_ctors[: len(damaged_ctors)]]
    else:
        replacements = [build_sugared_constructor(ctor, class_name) for ctor in unsugared_ctors[: len(damaged_ctors)]]
    pieces: list[str] = []
    cursor = 0
    for ctor, replacement in zip(damaged_ctors, replacements):
        pieces.append(text[cursor : ctor.start])
        pieces.append(replacement)
        cursor = ctor.end
    pieces.append(text[cursor:])
    new_text = "".join(pieces)
    if new_text != text:
        normal_file.write_text(new_text, encoding="utf-8")
        return True
    return False


def main() -> int:
    if len(sys.argv) != 3:
        print("usage: repair_enum_constructors.py <normal_source_root> <unsugared_source_root>", file=sys.stderr)
        return 2

    normal_root = Path(sys.argv[1]).resolve()
    unsugared_root = Path(sys.argv[2]).resolve()
    if not normal_root.exists():
        raise SystemExit(f"missing normal source root: {normal_root}")
    if not unsugared_root.exists():
        raise SystemExit(f"missing unsugared source root: {unsugared_root}")

    repaired = 0
    for normal_file in sorted(normal_root.rglob("*.java")):
        relative = normal_file.relative_to(normal_root)
        unsugared_file = unsugared_root / relative
        if unsugared_file.exists() and repair_file(normal_file, unsugared_file):
            repaired += 1

    print(f"repaired enum constructors: {repaired}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
