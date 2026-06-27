$ErrorActionPreference = "Stop"

$root = Resolve-Path (Join-Path $PSScriptRoot "..")

function Read-Source($relativePath) {
    Get-Content -Raw -Path (Join-Path $root $relativePath)
}

function Assert-Contains($name, $text, $pattern) {
    if ($text -notmatch [regex]::Escape($pattern)) {
        throw "Missing invariant: $name ($pattern)"
    }
}

function Assert-Regex($name, $text, $pattern) {
    if ($text -notmatch $pattern) {
        throw "Missing invariant: $name ($pattern)"
    }
}

$characterFiles = Read-Source "deobfuscated_source/com/rs2/util/CharacterFileManager.java"
$planner = Read-Source "deobfuscated_source/com/rs2/bot/BotTaskPlanner.java"
$gameplay = Read-Source "deobfuscated_source/com/rs2/model/GameplayHelper.java"
$player = Read-Source "deobfuscated_source/com/rs2/model/player/Player.java"

Assert-Contains "truncated character files fail validation" $characterFiles "throw iOException;"
Assert-Contains "validation logs concise invalid file message" $characterFiles "Invalid character file: "
Assert-Regex "validateCharacterFile no longer prints stack traces" $characterFiles "private static boolean validateCharacterFile[\s\S]*Invalid character file:[\s\S]*return false;"
Assert-Contains "invalid bot files are archived and reset" $characterFiles "resetInvalidBotCharacterFile(file, player)"
Assert-Contains "bot reset queues fresh login" $characterFiles "Server.getInstance().queueLogin(player)"
Assert-Contains "initial bot task selection handles null" $planner "No initial progressive bot task available for: "
Assert-Contains "task-goal setup handles null current task" $planner "if (botTaskDefinition == null)"
Assert-Contains "next bot task selection handles null" $gameplay "No bot task available for: "
Assert-Contains "player has missing-task recovery helper" $player "recoverMissingCurrentBotTask"
Assert-Contains "stall recovery handles missing current task" $player 'this.recoverMissingCurrentBotTask("stall recovery")'
Assert-Contains "resume dispatch handles missing current task" $player 'this.recoverMissingCurrentBotTask("resume " + this.botTaskState)'
Assert-Contains "task interaction handles missing current task" $player 'this.recoverMissingCurrentBotTask("start task interaction")'
Assert-Contains "route completion handles missing current task" $player 'this.recoverMissingCurrentBotTask("route completion " + this.botTaskState)'

[pscustomobject]@{
    Probe = "bot-login-recovery"
    Status = "passed"
    Checked = 13
}
