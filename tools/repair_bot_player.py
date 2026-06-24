#!/usr/bin/env python3
"""Repair BotPlayer calls that still reference pre-remap Player members."""

from __future__ import annotations

import sys
from pathlib import Path


REPLACEMENTS = [
    ("        this.az = n;\n", "        this.botMode = n;\n"),
    (
        "        this.d(TextUtil.capitalizeFirst((String)object));\n",
        "        this.setUsername(TextUtil.capitalizeFirst((String)object));\n",
    ),
    (
        "        this.b(TextUtil.encodeNameHash(this.di().toLowerCase()));\n",
        "        this.setNameHash(TextUtil.encodeNameHash(this.getUsername().toLowerCase()));\n",
    ),
    ("        this.e(string);\n", "        this.setPassword(string);\n"),
    ("        this.f(string);\n", "        this.setSubmittedPassword(string);\n"),
    (
        "        this.co = this.ei = System.currentTimeMillis();\n",
        "        this.co = System.currentTimeMillis();\n"
        "        this.sessionStartMillis = this.co;\n",
    ),
    (
        "        this.a(PlayerConnectionState.LOGIN_QUEUED);\n",
        "        this.setConnectionState(PlayerConnectionState.LOGIN_QUEUED);\n",
    ),
    (
        "                this.a(\"sbot\", null, false);\n",
        "                this.executeCheatCommand(\"sbot\", null, false);\n",
    ),
    ("            object = exception;\n", ""),
    (
        "        this.a(new Position(ServerSettings.respawnX, ServerSettings.respawnY, ServerSettings.respawnPlane));\n",
        "        this.moveTo(new Position(ServerSettings.respawnX, ServerSettings.respawnY, ServerSettings.respawnPlane));\n",
    ),
    (
        "        for (String string : removedBotNames) {\n",
        "        for (Object removedBotNameObject : removedBotNames) {\n"
        "            String string = (String)removedBotNameObject;\n",
    ),
    (
        "        object = ((File)object).listFiles();\n"
        "        File[] fileArray = object;\n"
        "        int n = ((File[])object).length;\n",
        "        File[] fileArray = ((File)object).listFiles();\n"
        "        if (fileArray == null) {\n"
        "            return;\n"
        "        }\n"
        "        int n = fileArray.length;\n",
    ),
]


def replace_exact(text: str, old: str, new: str, path: Path) -> tuple[str, int]:
    count = text.count(old)
    if count == 0:
        preview = old.strip().splitlines()[0][:120] if old.strip() else "<empty>"
        raise SystemExit(f"{path}: expected snippet not found: {preview!r}")
    return text.replace(old, new), count


def repair_file(path: Path) -> int:
    text = path.read_text(encoding="utf-8")
    repairs = 0
    for old, new in REPLACEMENTS:
        text, count = replace_exact(text, old, new, path)
        repairs += count
    path.write_text(text, encoding="utf-8")
    return repairs


def main() -> int:
    if len(sys.argv) != 2:
        print("usage: repair_bot_player.py <source_root>", file=sys.stderr)
        return 2
    source_root = Path(sys.argv[1]).resolve()
    repairs = repair_file(source_root / "com" / "rs2" / "bot" / "BotPlayer.java")
    print(f"repaired BotPlayer remap references: {repairs}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
