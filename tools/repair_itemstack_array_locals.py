#!/usr/bin/env python3
"""Repair CFR `Object` locals that temporarily hold ItemStack arrays."""

from __future__ import annotations

import re
import sys
from pathlib import Path


REPLACEMENTS = [
    ("player.botTaskRequiredItems = object;", "player.botTaskRequiredItems = (ItemStack[])object;"),
    ("BankManager.canDepositItems(player, object)", "BankManager.canDepositItems(player, (ItemStack[])object)"),
    ("ItemStack[] itemStackArray2 = object;", "ItemStack[] itemStackArray2 = (ItemStack[])object;"),
    ("player.getInventoryManager().addItem(object[", "player.getInventoryManager().addItem(((ItemStack[])object)["),
]

ITEMSTACK_OBJECT_INDEX_RE = re.compile(r"(?m)^([ \t]*)object(\[[^\]]+\]) = \(ItemStack\)")


def repair_file(path: Path) -> int:
    text = path.read_text(encoding="utf-8")
    repairs = 0
    for old, new in REPLACEMENTS:
        count = text.count(old)
        if count:
            text = text.replace(old, new)
            repairs += count

    text, count = ITEMSTACK_OBJECT_INDEX_RE.subn(r"\1((ItemStack[])object)\2 = (ItemStack)", text)
    repairs += count

    if repairs:
        path.write_text(text, encoding="utf-8")
    return repairs


def main() -> int:
    if len(sys.argv) != 2:
        print("usage: repair_itemstack_array_locals.py <source_root>", file=sys.stderr)
        return 2
    root = Path(sys.argv[1]).resolve()
    repairs = 0
    for path in root.rglob("*.java"):
        repairs += repair_file(path)
    print(f"repaired ItemStack array locals: {repairs}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
