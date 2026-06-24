#!/usr/bin/env python3
"""Repair small utility classes with CFR local-slot collisions."""

from __future__ import annotations

import sys
from pathlib import Path


REPAIRS = {
    "FileUtil.java": (
        """    public static boolean exists(String object) {
        return ((File)(object = new File((String)object))).exists();
    }
""",
        """    public static boolean exists(String path) {
        return new File(path).exists();
    }
""",
    ),
    "TimestampedPrintStream.java": (
        """    @Override
    public final void println(String object) {
        object = "[" + this.dateFormat.format(new Date()) + "]: " + (String)object;
        super.println((String)object);
        String string = object;
        object = this;
        try {
            if (((TimestampedPrintStream)object).logWriter == null) {
                return;
            }
            ((TimestampedPrintStream)object).logWriter.write(string);
            ((TimestampedPrintStream)object).logWriter.newLine();
            ((TimestampedPrintStream)object).logWriter.flush();
            return;
        }
        catch (IOException iOException) {
            object = iOException;
            iOException.printStackTrace();
            return;
        }
    }
""",
        """    @Override
    public final void println(String string) {
        string = "[" + this.dateFormat.format(new Date()) + "]: " + string;
        super.println(string);
        try {
            if (this.logWriter == null) {
                return;
            }
            this.logWriter.write(string);
            this.logWriter.newLine();
            this.logWriter.flush();
            return;
        }
        catch (IOException iOException) {
            iOException.printStackTrace();
            return;
        }
    }
""",
    ),
    "Converter.java": (
        """    public static void main(String[] object) {
        try {
            ServerSettings.mysqlPlayerSaveEnabled = true;
            object = new File("./data/characters/");
            DatabaseService.setInstance(new DatabaseService(8, ServerSettings.mysqlDriverClass, ServerSettings.mysqlJdbcUrl, ServerSettings.mysqlUsername, ServerSettings.mysqlPassword));
            System.out.println("Preparing to convert " + ((File)object).listFiles().length + " character files...");
            Object object2 = Arrays.asList(((File)object).listFiles());
            object2 = object2.iterator();
            while (true) {
                if (!readyForNextFile) {
                    continue;
                }
                if (object2.hasNext()) {
                    Object object3 = (File)object2.next();
                    readyForNextFile = false;
                    object3 = ((File)object3).getName().substring(0, ((File)object3).getName().indexOf(46)).toLowerCase();
                    Player player = new Player(null);
                    player.setUsername((String)object3);
                    ConverterUidLookupQuery converterUidLookupQuery = new ConverterUidLookupQuery("SELECT uid FROM `prs06_users` WHERE username = ?", (String)object3);
                    DatabaseService.getInstance().submit(converterUidLookupQuery, new ConverterUidLookupCallback((File)object, player, (String)object3));
                    continue;
                }
                break;
            }
        }
        catch (Exception exception) {
            object = exception;
            exception.printStackTrace();
        }
    }
""",
        """    public static void main(String[] stringArray) {
        try {
            ServerSettings.mysqlPlayerSaveEnabled = true;
            File characterDirectory = new File("./data/characters/");
            DatabaseService.setInstance(new DatabaseService(8, ServerSettings.mysqlDriverClass, ServerSettings.mysqlJdbcUrl, ServerSettings.mysqlUsername, ServerSettings.mysqlPassword));
            File[] characterFiles = characterDirectory.listFiles();
            System.out.println("Preparing to convert " + characterFiles.length + " character files...");
            for (File characterFile : Arrays.asList(characterFiles)) {
                while (!readyForNextFile) {
                }
                readyForNextFile = false;
                String username = characterFile.getName().substring(0, characterFile.getName().indexOf(46)).toLowerCase();
                Player player = new Player(null);
                player.setUsername(username);
                ConverterUidLookupQuery converterUidLookupQuery = new ConverterUidLookupQuery("SELECT uid FROM `prs06_users` WHERE username = ?", username);
                DatabaseService.getInstance().submit(converterUidLookupQuery, new ConverterUidLookupCallback(characterDirectory, player, username));
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }
""",
    ),
}


def replace_exact(text: str, old: str, new: str, path: Path) -> tuple[str, int]:
    count = text.count(old)
    if count == 0:
        preview = old.strip().splitlines()[0][:120] if old.strip() else "<empty>"
        raise SystemExit(f"{path}: expected snippet not found: {preview!r}")
    return text.replace(old, new), count


def main() -> int:
    if len(sys.argv) != 2:
        print("usage: repair_util_locals.py <source_root>", file=sys.stderr)
        return 2
    source_root = Path(sys.argv[1]).resolve()
    util_root = source_root / "com" / "rs2" / "util"
    repairs = 0
    for filename, (old, new) in REPAIRS.items():
        path = util_root / filename
        text = path.read_text(encoding="utf-8")
        text, count = replace_exact(text, old, new, path)
        path.write_text(text, encoding="utf-8")
        repairs += count
    comparator_path = util_root / "WeightedChanceEntryThresholdComparator.java"
    text = comparator_path.read_text(encoding="utf-8")
    if "int compare(Object object, Object object2)" not in text:
        insert = """
    public final int compare(Object object, Object object2) {
        WeightedChanceEntry entry = (WeightedChanceEntry)object;
        WeightedChanceEntry entry2 = (WeightedChanceEntry)object2;
        return Integer.compare(entry2.requiredLevel, entry.requiredLevel);
    }
"""
        marker = "\n}\n"
        index = text.rfind(marker)
        if index == -1:
            raise SystemExit(f"{comparator_path}: class closing brace not found")
        text = text[:index] + insert + text[index:]
        comparator_path.write_text(text, encoding="utf-8")
        repairs += 1
    print(f"repaired utility locals: {repairs}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
