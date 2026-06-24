#!/usr/bin/env python3
"""Add int bridge constructors for CFR synthetic byte-marker calls."""

from __future__ import annotations

import sys
from pathlib import Path


def replace_exact(text: str, old: str, new: str, path: Path) -> tuple[str, int]:
    count = text.count(old)
    if count == 0:
        preview = old.strip().splitlines()[0][:120] if old.strip() else "<empty>"
        raise SystemExit(f"{path}: expected snippet not found: {preview!r}")
    return text.replace(old, new), count


def repair_rectangular_area(path: Path) -> int:
    text = path.read_text(encoding="utf-8")
    old = """    public RectangularArea(int n, int n2, int n3, int n4, byte by) {
        this.minX = n;
        this.minY = n2;
        this.maxX = n3;
        this.maxY = n4;
        this.plane = by;
    }
"""
    new = old + """
    public RectangularArea(int n, int n2, int n3, int n4, int plane) {
        this(n, n2, n3, n4, (byte)plane);
    }
"""
    text, count = replace_exact(text, old, new, path)
    path.write_text(text, encoding="utf-8")
    return count


def repair_known_bridge(path: Path, old: str, bridge: str) -> int:
    text = path.read_text(encoding="utf-8")
    text, count = replace_exact(text, old, old + "\n" + bridge, path)
    path.write_text(text, encoding="utf-8")
    return count


def repair_optional_bridge(path: Path, old: str, bridge: str) -> int:
    text = path.read_text(encoding="utf-8")
    count = text.count(old)
    if count == 0:
        return 0
    text = text.replace(old, old + "\n" + bridge)
    path.write_text(text, encoding="utf-8")
    return count


def main() -> int:
    if len(sys.argv) != 2:
        print("usage: repair_int_bridge_constructors.py <source_root>", file=sys.stderr)
        return 2
    root = Path(sys.argv[1]).resolve()
    repairs = 0
    repairs += repair_rectangular_area(root / "com/rs2/util/RectangularArea.java")
    repairs += repair_known_bridge(
        root / "com/rs2/net/packet/PacketReader.java",
        """    /* synthetic */ PacketReader(ByteBuffer byteBuffer, byte by) {
        this(byteBuffer);
    }
""",
        """    PacketReader(ByteBuffer byteBuffer, int n) {
        this(byteBuffer);
    }
""",
    )
    repairs += repair_known_bridge(
        root / "com/rs2/net/packet/PacketWriter.java",
        """    /* synthetic */ PacketWriter(int n, byte by) {
        this(n);
    }
""",
        """    PacketWriter(int n, int n2) {
        this(n);
    }
""",
    )
    repairs += repair_known_bridge(
        root / "com/rs2/model/player/CharacterFileBankTab.java",
        """    /* synthetic */ CharacterFileBankTab(byte by) {
        this();
    }
""",
        """    CharacterFileBankTab(int n) {
        this();
    }
""",
    )
    repairs += repair_known_bridge(
        root / "com/rs2/model/player/PlayerUpdateTask.java",
        """    /* synthetic */ PlayerUpdateTask(int n, int n2, int n3, byte by) {
        this(n, 19509, 19508);
    }
""",
        """    PlayerUpdateTask(int n, int n2, int n3, int n4) {
        this(n, 19509, 19508);
    }
""",
    )
    repairs += repair_known_bridge(
        root / "com/rs2/model/gameplay/partyroom/PartyRoomBalloonReward.java",
        """    /* synthetic */ PartyRoomBalloonReward(ItemStack itemStack, Position position, byte by) {
        this(itemStack, position);
    }
""",
        """    PartyRoomBalloonReward(ItemStack itemStack, Position position, int n) {
        this(itemStack, position);
    }
""",
    )
    repairs += repair_optional_bridge(
        root / "com/rs2/util/db/player/PlayerProfileLoadCallback.java",
        """    public /* synthetic */ PlayerProfileLoadCallback(PlayerLoadQueryFactory playerLoadQueryFactory, byte by) {
        this(playerLoadQueryFactory);
    }
""",
        """    public PlayerProfileLoadCallback(PlayerLoadQueryFactory playerLoadQueryFactory, int n) {
        this(playerLoadQueryFactory);
    }
""",
    )
    repairs += repair_optional_bridge(
        root / "com/rs2/util/db/player/PlayerSkillsLoadCallback.java",
        """    public /* synthetic */ PlayerSkillsLoadCallback(PlayerLoadQueryFactory playerLoadQueryFactory, byte by) {
        this(playerLoadQueryFactory);
    }
""",
        """    public PlayerSkillsLoadCallback(PlayerLoadQueryFactory playerLoadQueryFactory, int n) {
        this(playerLoadQueryFactory);
    }
""",
    )
    query_factory = root / "com/rs2/util/db/player/PlayerLoadQueryFactory.java"
    text = query_factory.read_text(encoding="utf-8")
    replacements = [
        ("new PlayerProfileLoadCallback(this, 0)", "new PlayerProfileLoadCallback(this, (byte)0)"),
        ("new PlayerSkillsLoadCallback(this, 0)", "new PlayerSkillsLoadCallback(this, (byte)0)"),
    ]
    query_count = 0
    for old, new in replacements:
        count = text.count(old)
        if count == 0:
            raise SystemExit(f"{query_factory}: expected snippet not found: {old!r}")
        text = text.replace(old, new)
        query_count += count
    query_factory.write_text(text, encoding="utf-8")
    repairs += query_count

    stream_path = root / "com/rs2/util/CountingDataOutputStream.java"
    text = stream_path.read_text(encoding="utf-8")
    old = "        this.scratch[0] = n >> 24;\n"
    new = "        this.scratch[0] = (byte)(n >> 24);\n"
    count = text.count(old)
    if count == 0:
        raise SystemExit(f"{stream_path}: expected snippet not found: {old.strip()!r}")
    stream_path.write_text(text.replace(old, new), encoding="utf-8")
    repairs += count
    print(f"added int bridge constructors: {repairs}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
