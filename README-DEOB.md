# Easy RS2 Progressive Singleplayer Deob

## About

This is a deobfuscated source release of Mige5's Easy RS2 Progressive Singleplayer server.

The original project was abandoned years ago. The purpose of this deob is to make the server readable and maintainable so people can continue development on their own.

Original Rune-Server release thread:
https://rune-server.org/threads/easyrs2-progressive-singleplayer.701962/

No ownership of the original project is claimed here. This release is intended as a preservation and development aid for people using the original server.

## What To Upload

If you are publishing the deob source, the important source tree is:

```text
deobfuscated_source/com/
```

That `com` folder contains the Java packages for the rebuilt server.

For a usable project release, include more than just `com/`:

```text
Config.cfg
data/
deobfuscated_source/
Server_lib/
tools/
README-DEOB.md
docs/
Server-deob-compiled.jar
```

Optional/reference folders:

```text
source_recovery/
decompiled/
decompiled_fullcp/
decompiled_readable/
deobfuscation/
Server.jar
Server-deob-pass1.jar
```

Do not include `qa-output/` in a public release unless you intentionally want to share local verification logs, screenshots, probes, and temporary compile output.

Before publishing, remove private character saves or local test accounts from `data/characters/` if you do not want them included.

## Java Requirements

Run the stock client with Java 8.

The original client has a known mouse issue on newer Java runtimes: right-click can behave like left-click in two-button mouse mode. This is caused by old AWT mouse handling in the client, not by the deob server.

Example from the `Client` folder:

```powershell
& "<JAVA_8_HOME>\bin\java.exe" -jar "Client.jar"
```

If Java 8 is first on `PATH`, this also works:

```bat
java -jar "Client.jar"
```

The server can be run with a newer JDK. Rebuilding requires a full JDK with `javac` and `jar`.

## Running The Server

From the `Server` folder:

```bat
java -Xmx1024m -jar "Server-deob-compiled.jar"
```

`LAUNCH.bat` runs the same jar. After the control panel opens, press `Start Server`.

The server expects its working directory to be the `Server` folder because it loads `Config.cfg`, `data/`, and other files by relative path.

## IDE Setup

Open the `Server` folder as a Java project.

Use this source root:

```text
deobfuscated_source/
```

Add every jar in this folder as project libraries:

```text
Server_lib/
```

Recommended run configuration:

```text
Main class: com.rs2.launcher.ControlPanel
Working directory: Server
VM options: -Xmx1024m
Classpath: compiled deobfuscated_source output plus Server_lib jars
```

## Rebuilding

From PowerShell in the `Server` folder:

```powershell
New-Item -ItemType Directory -Force .\qa-output\full-compile | Out-Null
Get-ChildItem .\deobfuscated_source -Recurse -Filter *.java |
    ForEach-Object { ".\" + (Resolve-Path $_.FullName -Relative) } |
    Set-Content -Encoding ASCII .\qa-output\all-sources.txt

javac -Xmaxerrs 5000 -classpath ".\Server-deob-pass1.jar;.\Server_lib\*" -d .\qa-output\full-compile "@.\qa-output\all-sources.txt"
powershell -ExecutionPolicy Bypass -File .\tools\build-compiled-server-jar.ps1
```

The build script packages `qa-output/full-compile` into `Server-deob-compiled.jar`.

## Development Notes

Treat `deobfuscated_source/` as the canonical editable source.

Use recovered/decompiled folders only as references when checking original behavior or repairing decompiler output.

Keep behavior changes parity-focused. Do not change gameplay, packet behavior, balancing, drops, combat formulas, or cache/client assumptions unless you have confirmed a real original-vs-deob regression.

When something looks broken, compare against the original server first. If original and deob behave the same, document it as original behavior instead of patching it.
