$ErrorActionPreference = "Stop"

$root = Resolve-Path (Join-Path $PSScriptRoot "..")
$classesDir = Join-Path $root "qa-output\full-compile"
$manifestDir = Join-Path $root "qa-output\jar"
$manifestFile = Join-Path $manifestDir "MANIFEST.MF"
$jarFile = Join-Path $root "Server-deob-compiled.jar"

if (-not (Test-Path $classesDir)) {
    throw "Compiled classes not found: $classesDir"
}

New-Item -ItemType Directory -Force -Path $manifestDir | Out-Null
@"
Manifest-Version: 1.0
Class-Path: . Server_lib/commons-compress-1.0.jar Server_lib/javac++.jar Server_lib/joda-time-2.3.jar Server_lib/mysql-connector-j-8.0.32.jar
Main-Class: com.rs2.launcher.ControlPanel

"@ | Set-Content -Encoding ASCII -NoNewline -Path $manifestFile

if (Test-Path $jarFile) {
    Remove-Item -LiteralPath $jarFile -Force
}

& jar cfm $jarFile $manifestFile -C $classesDir .
if ($LASTEXITCODE -ne 0) {
    throw "jar failed with exit code $LASTEXITCODE"
}

Get-Item $jarFile | Select-Object FullName, Length, LastWriteTime
