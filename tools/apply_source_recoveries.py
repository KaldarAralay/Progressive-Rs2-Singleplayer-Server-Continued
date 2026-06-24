#!/usr/bin/env python3
"""Apply hand-recovered source files after CFR decompilation."""

from __future__ import annotations

import shutil
import sys
from pathlib import Path


def _resolve_inside(path: Path, root: Path) -> Path:
    resolved = path.resolve()
    root = root.resolve()
    try:
        resolved.relative_to(root)
    except ValueError as exc:
        raise SystemExit(f"Refusing to write outside output root: {resolved}") from exc
    return resolved


def main() -> int:
    if len(sys.argv) != 3:
        print("usage: apply_source_recoveries.py <recovery_root> <output_source_root>", file=sys.stderr)
        return 2

    recovery_root = Path(sys.argv[1]).resolve()
    output_root = Path(sys.argv[2]).resolve()

    if not recovery_root.exists():
        print("applied source recoveries: 0")
        return 0
    if not output_root.exists():
        raise SystemExit(f"Missing decompiled source output root: {output_root}")

    applied = 0
    for recovery_file in sorted(recovery_root.rglob("*.java")):
        relative_path = recovery_file.relative_to(recovery_root)
        output_file = _resolve_inside(output_root / relative_path, output_root)
        output_file.parent.mkdir(parents=True, exist_ok=True)
        shutil.copy2(recovery_file, output_file)
        applied += 1

    print(f"applied source recoveries: {applied}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
