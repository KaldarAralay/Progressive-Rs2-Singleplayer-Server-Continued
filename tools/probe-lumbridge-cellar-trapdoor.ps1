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

$handler = Read-Source "deobfuscated_source/com/rs2/model/gameplay/LumbridgeCastleCellarHandler.java"
$firstObject = Read-Source "deobfuscated_source/com/rs2/model/interaction/FirstObjectActionTask.java"
$secondObject = Read-Source "deobfuscated_source/com/rs2/model/interaction/SecondObjectActionTask.java"
$thirdObject = Read-Source "deobfuscated_source/com/rs2/model/interaction/ThirdObjectActionTask.java"
$fourthObject = Read-Source "deobfuscated_source/com/rs2/model/interaction/FourthObjectActionTask.java"

Assert-Contains "handler uses Lumbridge closed trapdoor id" $handler "KITCHEN_CLOSED_TRAPDOOR = 1568"
Assert-Contains "handler uses Lumbridge closed level variant" $handler "KITCHEN_CLOSED_TRAPDOOR_LEVEL_1 = 1569"
Assert-Contains "handler opens into Lumbridge open trapdoor id" $handler "KITCHEN_OPEN_TRAPDOOR = 1570"
Assert-Contains "handler uses Lumbridge open level variant" $handler "KITCHEN_OPEN_TRAPDOOR_LEVEL_1 = 1571"
Assert-Contains "handler uses cellar ladder id" $handler "CELLAR_LADDER = 1755"
Assert-Contains "handler preserves floor decoration type" $handler "KITCHEN_TRAPDOOR_TYPE = 22"
Assert-Contains "handler pins kitchen trapdoor coordinate" $handler "new Position(3209, 3216, 0)"
Assert-Contains "handler pins cellar ladder coordinate" $handler "new Position(3209, 9616, 0)"
Assert-Contains "handler matches Lumbridge kitchen area" $handler "isInLumbridgeKitchen(objectX, objectY, objectPlane)"
Assert-Contains "handler matches examine text" $handler "I wonder what's under it?"
Assert-Contains "handler matches open examine text" $handler "I wonder what's down there?"
Assert-Contains "handler falls back to player kitchen position" $handler "isPlayerInLumbridgeKitchen(player)"
Assert-Contains "handler reads object definitions" $handler "ObjectDefinition.forId(objectId)"
Assert-Contains "handler has trapdoor name fallback" $handler "isClosedTrapdoorDefinitionFallback(objectId)"
Assert-Contains "trapdoor opens before descent" $handler "openKitchenTrapdoor(player, objectId, objectX, objectY, objectPlane)"
Assert-Contains "trapdoor toggles open dynamically" $handler "openedObjectId"
Assert-Contains "open trapdoor descends to cellar" $handler "climbDownKitchenLadder(player)"
Assert-Contains "cellar ladder returns to kitchen" $handler "AttackStyleDefinition.startDelayedObjectMove(player, KITCHEN_EXIT_POSITION)"
Assert-Contains "first object path calls cellar handler" $firstObject "LumbridgeCastleCellarHandler.handleFirstObjectAction"
Assert-Contains "second object path calls cellar handler" $secondObject "LumbridgeCastleCellarHandler.handleSecondObjectAction"
Assert-Contains "third object path calls cellar handler" $thirdObject "LumbridgeCastleCellarHandler.handleThirdObjectAction"
Assert-Contains "fourth object path calls cellar handler" $fourthObject "LumbridgeCastleCellarHandler.handleFourthObjectAction"

[pscustomobject]@{
    Probe = "lumbridge-cellar-trapdoor"
    Status = "passed"
    Checked = 23
}
