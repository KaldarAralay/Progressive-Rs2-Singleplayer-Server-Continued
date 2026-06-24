# Easy RS2 Progressive Singleplayer Server Deob

## About

This is a deobfuscated source release of Mige5's Easy RS2 Progressive Singleplayer server.

The original project was abandoned by Mige5 years ago. The purpose of this deob is to make the server readable, buildable, and maintainable so people can continue development on their own forks.

Original Rune-Server release thread:
https://rune-server.org/threads/easyrs2-progressive-singleplayer.701962/

No ownership of the original project is claimed here. This release is intended as a preservation and development aid for people using the original server.

## Project Layout

```text
deobfuscated_source/     Editable Java server source.
Server_lib/              Required dependency jars.
tools/                   Build/deob helper scripts.
docs/                    Release and verification notes.
source_recovery/         Small reference set used during source repair.
data/                    Runtime server data, cache, definitions, and world files.
Config.cfg               Main server config.
Server-deob-compiled.jar Current rebuilt runnable server jar.
Server-deob-pass1.jar    Intermediate/reference jar used on the rebuild classpath.
```

The raw decompiler dumps, local QA output, and local character saves are intentionally not part of this GitHub layout.

## Java Requirements

Run the stock client with Java 8. The original client has a known mouse issue on newer Java runtimes where right-click can behave like left-click in two-button mouse mode. That is an old AWT client/runtime compatibility issue, not a server regression.

From the `Client` folder, use:

```powershell
& "<JAVA_8_HOME>\bin\java.exe" -jar "Client.jar"
```

The server can run on a newer JDK. Rebuilding requires a full JDK with `javac` and `jar`.

## Running The Server

From this server folder:

```bat
java -Xmx1024m -jar "Server-deob-compiled.jar"
```

`LAUNCH.bat` runs the same jar. After the control panel opens, press `Start Server`.

The working directory matters because the server loads `Config.cfg`, `data/`, and other files by relative path.

## Building From Source

For people who only want to rebuild the server jar, the required pieces are:

```text
deobfuscated_source/
Server_lib/
Server-deob-pass1.jar
tools/build-compiled-server-jar.ps1
```

For a runnable server after building, also keep:

```text
Config.cfg
data/
```

PowerShell rebuild command from the server folder:

```powershell
New-Item -ItemType Directory -Force .\qa-output\full-compile | Out-Null
Get-ChildItem .\deobfuscated_source -Recurse -Filter *.java |
    ForEach-Object { ".\" + (Resolve-Path $_.FullName -Relative) } |
    Set-Content -Encoding ASCII .\qa-output\all-sources.txt

javac -Xmaxerrs 5000 -classpath ".\Server-deob-pass1.jar;.\Server_lib\*" -d .\qa-output\full-compile "@.\qa-output\all-sources.txt"
powershell -ExecutionPolicy Bypass -File .\tools\build-compiled-server-jar.ps1
```

The build script packages `qa-output/full-compile` into `Server-deob-compiled.jar`.

## IDE Setup

Open this folder as a Java project.

Use `deobfuscated_source/` as the source root and add every jar in `Server_lib/` as project libraries.

Recommended run configuration:

```text
Main class: com.rs2.launcher.ControlPanel
Working directory: this server folder
VM options: -Xmx1024m
Classpath: compiled deobfuscated_source output plus Server_lib jars
```

## Development Notes

Treat `deobfuscated_source/` as the canonical editable source.

Keep behavior changes parity-focused. Do not change gameplay, packet behavior, balancing, drops, combat formulas, or cache/client assumptions unless you have confirmed a real original-vs-deob regression.

When something looks broken, compare against the original server first. If original and deob behave the same, document it as original behavior instead of patching it.
