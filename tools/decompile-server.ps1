$ErrorActionPreference = 'Stop'

$root = Resolve-Path (Join-Path $PSScriptRoot '..')
$cfr = Join-Path $root 'tools\cfr.jar'

if (-not (Test-Path $cfr)) {
    throw "Missing tools\cfr.jar. Download CFR before running this script."
}

$classpath = @(
    'Server_lib\commons-compress-1.0.jar',
    'Server_lib\javac++.jar',
    'Server_lib\joda-time-2.3.jar',
    'Server_lib\mysql-connector-j-8.0.32.jar'
) -join ';'

Push-Location $root
try {
    java -jar $cfr .\Server.jar `
        --outputdir decompiled_readable `
        --extraclasspath $classpath `
        --renameillegalidents true `
        --silent true
}
finally {
    Pop-Location
}
