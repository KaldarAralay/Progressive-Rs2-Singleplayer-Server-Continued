# Release Candidate Notes

This document tracks the current known-good deob release shape. It is intentionally operational: what to run, what to edit, what to ignore, and what must be proven before changing behavior.

## Current Baseline

- Server runtime jar: `Server-deob-compiled.jar`
- Original reference jar: `Server.jar`
- Intermediate/reference deob jar: `Server-deob-pass1.jar`
- Canonical editable source: `deobfuscated_source/`
- Local verification scratch space: `qa-output/`

`Server-deob-pass1.jar` is not the normal runtime jar. It is useful as a deob/reference artifact only.

## Java Requirements

### Client

Run the stock client with Java 8.

The original client uses old AWT mouse handling based on `MouseEvent.isMetaDown()`. On modern Java runtimes, normal right-click events may not set the same modifier state, causing two-button mouse mode to behave like left-click. This is an original-client runtime compatibility issue, not a deob server regression.

Java 8 client launch example:

```powershell
& "<JAVA_8_HOME>\bin\java.exe" -jar "Client.jar"
```

Run that command from the sibling `Client` folder.

Do not use the stock `Client\LAUNCH.bat` for verification unless the default `java` on `PATH` is Java 8. If `PATH` resolves `java` to a newer runtime, the known right-click issue can reproduce.

### Server

The server currently runs with the workspace default Java 21 JDK:

```powershell
java -Xmx1024m -jar "Server-deob-compiled.jar"
```

`LAUNCH.bat` runs the same jar and then pauses the terminal.

## Rebuild Procedure

From PowerShell in the `Server` folder:

```powershell
New-Item -ItemType Directory -Force .\qa-output\full-compile | Out-Null
Get-ChildItem .\deobfuscated_source -Recurse -Filter *.java |
    ForEach-Object { ".\" + (Resolve-Path $_.FullName -Relative) } |
    Set-Content -Encoding ASCII .\qa-output\all-sources.txt

javac -Xmaxerrs 5000 -classpath ".\Server-deob-pass1.jar;.\Server_lib\*" -d .\qa-output\full-compile "@.\qa-output\all-sources.txt"
powershell -ExecutionPolicy Bypass -File .\tools\build-compiled-server-jar.ps1
```

The build script writes `Server-deob-compiled.jar` using `qa-output/full-compile` and a manifest with `com.rs2.launcher.ControlPanel` as the main class.

## Canonical Folders

- Edit server source in `deobfuscated_source/`.
- Use `source_recovery/` to compare against recovered source when a class looks suspicious.
- Use `decompiled*` folders as raw evidence only.
- Keep new probes, temporary compile outputs, logs, screenshots, and protocol traces under `qa-output/`.
- Do not put release-critical source only in `qa-output/`.

## Parity Policy

Preserve current known-good behavior. Do not change gameplay, protocol, packet formats, balancing, drop tables, skilling rates, combat formulas, or cache/client assumptions as cleanup.

Before changing behavior, capture:

1. Exact repro on the deob server.
2. Result on the original `Server.jar`.
3. The expected parity behavior.
4. The smallest server-source change needed.
5. A rebuild and smoke test result.

If original and deob behave the same, document it as original behavior instead of patching it.

## Smoke Test Checklist

Use the stock client launched with Java 8.

- Server launcher opens and starts the server.
- New and existing accounts can log in.
- Logout/restart persistence works.
- Movement and pathing work.
- Right-click menus work in two-button mouse mode.
- Object interactions work, including trees, rocks, doors, ladders, and travel objects.
- NPC interactions work, including talk, attack, death, drops, and pickup.
- Basic combat produces nonzero hit flow when expected.
- Inventory actions work, including equip, unequip, eat, drop, bury, item-on-object, and item-on-item.
- Banking, shops, dialogue options, and travel/teleports work.
- Basic quest flow still behaves like the original.

Any failure should be compared against the original server before changing code.
