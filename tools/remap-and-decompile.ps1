$ErrorActionPreference = 'Stop'

$root = Resolve-Path (Join-Path $PSScriptRoot '..')
$inputJar = Join-Path $root 'Server.jar'
$outputJar = Join-Path $root 'Server-deob-pass1.jar'
$cfr = Join-Path $root 'tools\cfr.jar'
$outputSource = Join-Path $root 'deobfuscated_source'
$enumSignatureSource = Join-Path $root 'qa-output\cfr-unsugared-enums'
$python = @(
    "$env:LOCALAPPDATA\hermes\hermes-agent\venv\Scripts\python.exe",
    "$env:LOCALAPPDATA\Python\bin\python.exe"
    (Get-Command python -All -CommandType Application -ErrorAction SilentlyContinue | ForEach-Object { $_.Source })
) | Where-Object { $_ -and (Test-Path $_) } | Select-Object -First 1

if (-not (Test-Path $cfr)) {
    throw "Missing tools\cfr.jar. Download CFR before running this script."
}
if (-not $python) {
    throw "Could not find a usable python.exe for tools\remap_jar.py."
}

$classpath = @(
    'Server_lib\commons-compress-1.0.jar',
    'Server_lib\javac++.jar',
    'Server_lib\joda-time-2.3.jar',
    'Server_lib\mysql-connector-j-8.0.32.jar'
) -join ';'

Push-Location $root
try {
    if (Test-Path $outputSource) {
        $resolvedRoot = (Resolve-Path $root).Path
        $resolvedOutput = (Resolve-Path $outputSource).Path
        if (-not $resolvedOutput.StartsWith($resolvedRoot, [System.StringComparison]::OrdinalIgnoreCase)) {
            throw "Refusing to delete generated output outside workspace: $resolvedOutput"
        }
        Remove-Item -LiteralPath $resolvedOutput -Recurse -Force
    }
    if (Test-Path $enumSignatureSource) {
        $resolvedRoot = (Resolve-Path $root).Path
        $resolvedEnumOutput = (Resolve-Path $enumSignatureSource).Path
        if (-not $resolvedEnumOutput.StartsWith($resolvedRoot, [System.StringComparison]::OrdinalIgnoreCase)) {
            throw "Refusing to delete enum signature output outside workspace: $resolvedEnumOutput"
        }
        Remove-Item -LiteralPath $resolvedEnumOutput -Recurse -Force
    }
    & $python tools\remap_jar.py $inputJar $outputJar
    java -jar $cfr .\Server-deob-pass1.jar `
        --outputdir deobfuscated_source `
        --extraclasspath $classpath `
        --renameillegalidents true `
        --silent true
    java -jar $cfr .\Server-deob-pass1.jar `
        --outputdir $enumSignatureSource `
        --extraclasspath $classpath `
        --renameillegalidents true `
        --sugarenums false `
        --silent true
    & $python tools\repair_enum_constructors.py deobfuscated_source $enumSignatureSource
    & $python tools\apply_source_recoveries.py source_recovery deobfuscated_source
    & $python tools\repair_bot_player.py deobfuscated_source
    & $python tools\repair_packet_handler_locals.py deobfuscated_source
    & $python tools\repair_util_locals.py deobfuscated_source
    & $python tools\repair_small_local_collisions.py deobfuscated_source
    & $python tools\repair_known_cfr_void_locals.py deobfuscated_source
    & $python tools\repair_scheduler_task_locals.py deobfuscated_source
    & $python tools\repair_itemstack_array_locals.py deobfuscated_source
    & $python tools\repair_int_bridge_constructors.py deobfuscated_source
    & $python tools\repair_access_modifiers.py deobfuscated_source
    & $python tools\repair_constructor_super_order.py deobfuscated_source
    & $python tools\repair_quest_journal_locals.py deobfuscated_source
    & $python tools\repair_special_attack_definition_constructors.py deobfuscated_source
    & $python tools\repair_duel_rule_constructors.py deobfuscated_source
}
finally {
    Pop-Location
}
