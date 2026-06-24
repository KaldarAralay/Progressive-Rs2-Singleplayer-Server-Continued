#!/usr/bin/env python3
"""Repair small one-file local-slot collisions left after CFR."""

from __future__ import annotations

import sys
from pathlib import Path


REPAIRS = {
    "com/rs2/ConfigFile.java": [
        ("            serializable.delete();\n", "            ((File)serializable).delete();\n"),
        (
            "    private static void writeConfigLine(BufferedWriter bufferedWriter, String string) {\n",
            "    private static void writeConfigLine(BufferedWriter bufferedWriter, String string) throws IOException {\n",
        ),
        (
            "                    } else if (((String)object).equals(\"CACHE_VERSION\")) {\n"
            "                        ServerSettings.cacheVersion = Integer.parseInt(stringArray[0]);\n"
            "                        if (ServerSettings.cacheVersion <= 245) {\n",
            "                    } else if (((String)object).equals(\"CACHE_VERSION\")) {\n"
            "                        ServerSettings.cacheVersion = Integer.parseInt(stringArray[0]);\n"
            "                        ServerSettings.clientBuild = ServerSettings.cacheVersion;\n"
            "                        if (ServerSettings.cacheVersion <= 245) {\n",
        ),
    ],
    "com/rs2/ConnectionThrottle.java": [
        (
            "        Integer n = connectionCountsByHost.putIfAbsent(string, 1);\n",
            "        Integer n = (Integer)connectionCountsByHost.putIfAbsent(string, 1);\n",
        ),
    ],
    "com/rs2/MinuteMaintenanceTickTask.java": [
        ("                Player player = object;\n", "                Player player = (Player)object;\n"),
    ],
    "com/rs2/util/ProfilerRegistry.java": [
        (
            "        for (Map.Entry entry : timers.entrySet()) {\n"
            "            ((ProfilerTimer)entry.getValue()).reset();\n"
            "        }\n",
            "        for (Object entryObject : timers.entrySet()) {\n"
            "            Map.Entry entry = (Map.Entry)entryObject;\n"
            "            ((ProfilerTimer)entry.getValue()).reset();\n"
            "        }\n",
        ),
    ],
    "com/rs2/util/CountingDataOutputStream.java": [
        ("import java.io.FilterOutputStream;\n", "import java.io.FilterOutputStream;\nimport java.io.IOException;\n"),
        ("    public final void flush() {\n", "    public final void flush() throws IOException {\n"),
        ("    public final void write(byte[] byArray, int n, int n2) {\n", "    public final void write(byte[] byArray, int n, int n2) throws IOException {\n"),
        ("    public final void write(int n) {\n", "    public final void write(int n) throws IOException {\n"),
        ("    public final void writeBoolean(boolean bl) {\n", "    public final void writeBoolean(boolean bl) throws IOException {\n"),
        ("    public final void writeUnsignedByte(int n) {\n", "    public final void writeUnsignedByte(int n) throws IOException {\n"),
        ("    public final void writeByte(int n) {\n", "    public final void writeByte(int n) throws IOException {\n"),
        ("    public final void writeBytes(String string) {\n", "    public final void writeBytes(String string) throws IOException {\n"),
        ("    public final void writeChar(int n) {\n", "    public final void writeChar(int n) throws IOException {\n"),
        ("    public final void writeChars(String string) {\n", "    public final void writeChars(String string) throws IOException {\n"),
        ("    public final void writeDouble(double d) {\n", "    public final void writeDouble(double d) throws IOException {\n"),
        ("    public final void writeFloat(float f) {\n", "    public final void writeFloat(float f) throws IOException {\n"),
        ("    public final void writeInt(int n) {\n", "    public final void writeInt(int n) throws IOException {\n"),
        ("    public final void writeLong(long l) {\n", "    public final void writeLong(long l) throws IOException {\n"),
        ("    public final void writeShort(int n) {\n", "    public final void writeShort(int n) throws IOException {\n"),
        ("    public final void writeUTF(String string) {\n", "    public final void writeUTF(String string) throws IOException {\n"),
    ],
    "com/rs2/util/TimestampedPrintStream.java": [
        (
            "    public TimestampedPrintStream(OutputStream outputStream, String string) {\n",
            "    public TimestampedPrintStream(OutputStream outputStream, String string) throws IOException {\n",
        ),
    ],
    "com/rs2/util/db/DatabaseQuery.java": [
        (
            "    public abstract ResultSet executeStatement(PreparedStatement var1);\n",
            "    public abstract ResultSet executeStatement(PreparedStatement var1) throws SQLException;\n",
        ),
        ("                    object.close();\n", "                    ((ResultSet)object).close();\n"),
    ],
    "com/rs2/util/ConverterUidLookupQuery.java": [
        (
            "    public final ResultSet executeStatement(PreparedStatement preparedStatement) {\n",
            "    public final ResultSet executeStatement(PreparedStatement preparedStatement) throws java.sql.SQLException {\n",
        ),
    ],
    "com/rs2/util/db/DatabaseService.java": [
        (
            "    public DatabaseService(int n, String string, String string2, String string3, String string4) {\n",
            "    public DatabaseService(int n, String string, String string2, String string3, String string4) throws ClassNotFoundException {\n",
        ),
        (
            "    protected final Connection openConnection() {\n",
            "    protected final Connection openConnection() throws java.sql.SQLException {\n",
        ),
    ],
    "com/rs2/util/db/DatabaseCallback.java": [
        (
            "    public void onResult(ResultSet var1);\n",
            "    public void onResult(ResultSet var1) throws java.sql.SQLException;\n",
        ),
    ],
    "com/rs2/util/PlayerLoginLoadCallback.java": [
        (
            "    private boolean processUidLookupResult(ResultSet object) {\n",
            "    private boolean processUidLookupResult(ResultSet object) throws java.sql.SQLException {\n",
        ),
    ],
    "com/rs2/util/db/player/PlayerProfileLoadCallback.java": [
        ("            object = PlayerLoadQueryFactory.getPlayer(this.factory);\n", ""),
    ],
    "com/rs2/model/gameplay/fightcave/FightCaveWaveSpawner.java": [
        (
            "        Collections.sort(object2, new FightCaveNpcLevelComparator());\n",
            "        Collections.sort((ArrayList)object2, new FightCaveNpcLevelComparator());\n",
        ),
        ("            npc.a(position);\n", "            npc.moveTo(position);\n"),
    ],
    "com/rs2/net/packet/PacketDispatcher.java": [
        ("            object = exception;\n", ""),
        (
            "                    LoginProtocol.processLoginBuffer(player, player.getInboundBuffer());\n"
            "                    break;\n",
            "                    if (LoginProtocol.processLoginBuffer(player, player.getInboundBuffer())) {\n"
            "                        return;\n"
            "                    }\n"
            "                    break;\n",
        ),
    ],
    "com/rs2/net/DedicatedReactor.java": [
        (
            "            DedicatedReactor dedicatedReactor = instance;\n"
            "            // MONITORENTER : dedicatedReactor\n"
            "            // MONITOREXIT : dedicatedReactor\n"
            "            try {\n",
            "            DedicatedReactor dedicatedReactor = instance;\n"
            "            synchronized (dedicatedReactor) {\n"
            "            }\n"
            "            try {\n",
        ),
    ],
    "com/rs2/model/skill/GatheringToolDefinition.java": [
        (
            "        Object object = this;\n"
            "        object = new ItemStack(object.brokenToolItemId, 1);\n"
            "        return ((ItemStack)object).getDefinition().getValue();\n",
            "        ItemStack itemStack = new ItemStack(this.brokenToolItemId, 1);\n"
            "        return itemStack.getDefinition().getValue();\n",
        ),
    ],
    "com/rs2/model/skill/SkillManager.java": [
        ("        if (d >= d4 && d5 >= d3) {\n", "        if (d5 >= d4 && d5 >= d3) {\n"),
    ],
    "com/rs2/model/combat/requirement/InventoryItemRequirement.java": [
        (
            "        for (ItemStack itemStack : this.requiredItems) {\n",
            "        for (Object itemObject : this.requiredItems) {\n"
            "            ItemStack itemStack = (ItemStack)itemObject;\n",
        ),
    ],
    "com/rs2/model/skill/prayer/BoneBuryingHandler.java": [
        ("            this.player.getSkillManager().addExperience(5, boneDefinition.experience);\n", "            this.player.getSkillManager().addExperience(5, boneDefinition2.experience);\n"),
    ],
    "com/rs2/model/npc/combat/NpcCombatDefinition.java": [
        ("                if (d <= 0.0) {\n", "                if (d2 <= 0.0) {\n"),
    ],
    "com/rs2/model/skill/crafting/DramenStaffCarvingTask.java": [
        (
            "        object = this.player;\n"
            "        ((Player)object).packetSender",
            "        this.player.packetSender",
        ),
    ],
    "com/rs2/model/skill/crafting/GlassblowingTask.java": [
        (
            "        object = this.player;\n"
            "        ((Player)object).packetSender",
            "        this.player.packetSender",
        ),
    ],
    "com/rs2/model/skill/crafting/PotteryOvenTask.java": [
        (
            "        object = this.player;\n"
            "        ((Player)object).packetSender",
            "        this.player.packetSender",
        ),
    ],
    "com/rs2/model/skill/crafting/PotteryWheelTask.java": [
        (
            "        object = this.player;\n"
            "        ((Player)object).packetSender",
            "        this.player.packetSender",
        ),
    ],
    "com/rs2/model/skill/crafting/SpinningTask.java": [
        (
            "        object = this.player;\n"
            "        ((Player)object).packetSender",
            "        this.player.packetSender",
        ),
    ],
    "com/rs2/model/skill/crafting/WeavingTask.java": [
        (
            "        object = this.player;\n"
            "        ((Player)object).packetSender",
            "        this.player.packetSender",
        ),
    ],
    "com/rs2/bot/BotTradeAdvertManager.java": [
        ("        player.botPublicChatEffect = (int)object[n2];\n", "        player.botPublicChatEffect = ((int[])object)[n2];\n"),
        ("        if (d < 1.0) {\n", "        if (d2 < 1.0) {\n"),
        (
            "            int n;\n"
            "            BotCombatLoadoutManager.prepareRangedLoadout(player2);\n"
            "            int n2 = player2.getSkillManager().getCurrentLevels()[4] >= 40 ? 1731 : (n = GameUtil.randomInt(3) == 0 ? 1478 : 1729);\n",
            "            int n;\n"
            "            BotCombatLoadoutManager.prepareRangedLoadout(player2);\n"
            "            n = player2.getSkillManager().getCurrentLevels()[4] >= 40 ? 1731 : (GameUtil.randomInt(3) == 0 ? 1478 : 1729);\n",
        ),
    ],
    "com/rs2/bot/tasks/FaladorYewWoodcuttingBotTask.java": [
        ("        object = this;\n", ""),
    ],
    "com/rs2/bot/tasks/VarrockPalaceYewWoodcuttingBotTask.java": [
        ("        object = this;\n", ""),
    ],
    "com/rs2/bot/tasks/DraynorYewWoodcuttingBotTask.java": [
        ("        object = this;\n", ""),
    ],
    "com/rs2/bot/DropPartyLeaderTickTask.java": [
        (
            "                for (Object object22 : DropPartyBotManager.dropPartyParticipants) {\n"
            "                    if (object22 == this.leader) continue;\n"
            "                    ((Player)object22).botTaskState = \"do task\";\n"
            "                    EntityTargetMovement.clearMovementTarget((Entity)object22);\n"
            "                    ((Entity)object22).getMovementQueue().clear();\n"
            "                    object = this.leader.currentBotTask.getRandomTaskAreaPosition();\n"
            "                    PathFinder.getInstance();\n"
            "                    PathFinder.findPath((Player)object22, ((Position)object).getX(), ((Position)object).getY(), true, 0, 0);\n"
            "                }\n",
            "                for (Object participantObject : DropPartyBotManager.dropPartyParticipants) {\n"
            "                    if (participantObject == this.leader) continue;\n"
            "                    ((Player)participantObject).botTaskState = \"do task\";\n"
            "                    EntityTargetMovement.clearMovementTarget((Entity)participantObject);\n"
            "                    ((Entity)participantObject).getMovementQueue().clear();\n"
            "                    object = this.leader.currentBotTask.getRandomTaskAreaPosition();\n"
            "                    PathFinder.getInstance();\n"
            "                    PathFinder.findPath((Player)participantObject, ((Position)object).getX(), ((Position)object).getY(), true, 0, 0);\n"
            "                }\n",
        ),
    ],
    "com/rs2/model/MovementStep.java": [
        ("        object = this;\n", ""),
    ],
    "com/rs2/model/objects/ObjectDefinition.java": [
        (
            "        Object object = new int[]{2440, 2441, 2442, 2443, 2637, 9563, 9565, 14462, 14464, 14465, 14466, 14467, 14468, 14470, 14502, 11754, 3007, 980, 997, 4262, 14437, 14438, 4437, 4439, 3487, 3457};\n"
            "        int[] nArray = object;\n",
            "        int[] nArray = new int[]{2440, 2441, 2442, 2443, 2637, 9563, 9565, 14462, 14464, 14465, 14466, 14467, 14468, 14470, 14502, 11754, 3007, 980, 997, 4262, 14437, 14438, 4437, 4439, 3487, 3457};\n",
        ),
        (
            "            object = this.name.toLowerCase();\n"
            "            Object object2 = new String[]{\"fungus\", \"mushroom\", \"sarcophagus\", \"counter\", \"plant\", \"altar\", \"pew\", \"log\", \"stump\", \"stool\", \"sign\", \"cart\", \"chest\", \"rock\", \"bush\", \"hedge\", \"chair\", \"table\", \"crate\", \"barrel\", \"box\", \"skeleton\", \"corpse\", \"vent\", \"stone\", \"rockslide\"};\n"
            "            String[] stringArray = object2;\n",
            "            String objectName = this.name.toLowerCase();\n"
            "            String[] stringArray = new String[]{\"fungus\", \"mushroom\", \"sarcophagus\", \"counter\", \"plant\", \"altar\", \"pew\", \"log\", \"stump\", \"stool\", \"sign\", \"cart\", \"chest\", \"rock\", \"bush\", \"hedge\", \"chair\", \"table\", \"crate\", \"barrel\", \"box\", \"skeleton\", \"corpse\", \"vent\", \"stone\", \"rockslide\"};\n",
        ),
        (
            "                object2 = stringArray[n3];\n"
            "                if (((String)object).contains((CharSequence)object2)) {\n",
            "                String ignoredNameFragment = stringArray[n3];\n"
            "                if (objectName.contains(ignoredNameFragment)) {\n",
        ),
    ],
    "com/rs2/model/skill/cooking/CookingManager.java": [
        (
            "                this.player.interfaceAction = object2;\n",
            "                this.player.interfaceAction = (String)object2;\n",
        ),
    ],
    "com/rs2/model/objects/functions/PickableObjectHandler.java": [
        (
            "            object = nArray[n5];\n"
            "            if (object[0] == n) {\n"
            "                n4 = object[1];\n",
            "            int[] pickableObject = nArray[n5];\n"
            "            if (pickableObject[0] == n) {\n"
            "                n4 = pickableObject[1];\n",
        ),
    ],
    "com/rs2/model/objects/functions/DoorHandler.java": [
        (
            "        DoorHandler doorHandler2;\n"
            "        for (DoorHandler doorHandler2 : doorStates) {\n"
            "            if (doorHandler2.currentX != n2 || doorHandler2.currentY != n3 || doorHandler2.plane != n4) continue;\n"
            "            return doorHandler2;\n"
            "        }\n"
            "        doorHandler2 = new DoorHandler(n, n2, n3, n4);\n",
            "        for (Object doorStateObject : doorStates) {\n"
            "            DoorHandler doorHandler2 = (DoorHandler)doorStateObject;\n"
            "            if (doorHandler2.currentX != n2 || doorHandler2.currentY != n3 || doorHandler2.plane != n4) continue;\n"
            "            return doorHandler2;\n"
            "        }\n"
            "        DoorHandler doorHandler2 = new DoorHandler(n, n2, n3, n4);\n",
        ),
        (
            "        if (!((String)object2).contains(\"fence\") && !((String)object).contains(\"gate\") && !((String)object).contains(\"door\") || ((String)object).contains(\"trapdoor\") || ((String)object).contains(\"tree\")) {\n",
            "        if (!((String)object2).contains(\"fence\") && !((String)object2).contains(\"gate\") && !((String)object2).contains(\"door\") || ((String)object2).contains(\"trapdoor\") || ((String)object2).contains(\"tree\")) {\n",
        ),
    ],
    "com/rs2/model/objects/functions/DoubleDoorHandler.java": [
        (
            "        Object object2;\n"
            "        for (Object object2 : doorStates) {\n"
            "            if (((DoubleDoorHandler)object2).currentX != n || ((DoubleDoorHandler)object2).currentY != n2 || ((DoubleDoorHandler)object2).plane != n3) continue;\n"
            "            return object2;\n"
            "        }\n"
            "        object2 = WorldObjectLookup.findObjectByNameAt(\"door\", n, n2, n3);\n",
            "        Object object2;\n"
            "        for (Object doorStateObject : doorStates) {\n"
            "            DoubleDoorHandler doubleDoorHandler = (DoubleDoorHandler)doorStateObject;\n"
            "            if (doubleDoorHandler.currentX != n || doubleDoorHandler.currentY != n2 || doubleDoorHandler.plane != n3) continue;\n"
            "            return doubleDoorHandler;\n"
            "        }\n"
            "        object2 = WorldObjectLookup.findObjectByNameAt(\"door\", n, n2, n3);\n",
        ),
    ],
    "com/rs2/model/player/CharacterFileRecord.java": [
        (
            "            object = this.inventoryItems;\n"
            "            n = this.inventoryItems.length;\n"
            "            int n3 = 0;\n"
            "            while (n3 < n) {\n"
            "                ItemStack itemStack = object[n3];\n",
            "            ItemStack[] inventoryItems = this.inventoryItems;\n"
            "            n = this.inventoryItems.length;\n"
            "            int n3 = 0;\n"
            "            while (n3 < n) {\n"
            "                ItemStack itemStack = inventoryItems[n3];\n",
        ),
        (
            "            object = this.equipmentItems;\n"
            "            n = this.equipmentItems.length;\n"
            "            n3 = 0;\n"
            "            while (n3 < n) {\n"
            "                Object object3 = object[n3];\n",
            "            ItemStack[] equipmentItems = this.equipmentItems;\n"
            "            n = this.equipmentItems.length;\n"
            "            n3 = 0;\n"
            "            while (n3 < n) {\n"
            "                ItemStack object3 = equipmentItems[n3];\n",
        ),
    ],
    "com/rs2/model/interaction/SearchCrateTask.java": [
        (
            "            Object object = new ItemStack[]{new ItemStack(995, 10), new ItemStack(686), new ItemStack(687), new ItemStack(689), new ItemStack(690), new ItemStack(697), new ItemStack(1059), new ItemStack(1061)};\n"
            "            object = object[GameUtil.randomExclusive(8)];\n",
            "            ItemStack[] crateRewards = new ItemStack[]{new ItemStack(995, 10), new ItemStack(686), new ItemStack(687), new ItemStack(689), new ItemStack(690), new ItemStack(697), new ItemStack(1059), new ItemStack(1061)};\n"
            "            ItemStack object = crateRewards[GameUtil.randomExclusive(8)];\n",
        ),
    ],
    "com/rs2/bot/combat/BotCombatEscapeLogoutTask.java": [
        (
            "                Object var2_1 = null;\n"
            "                Player player = this.player;\n"
            "                this.player.currentBotTask = var2_1;\n"
            "                var2_1 = null;\n"
            "                player = this.player;\n"
            "                this.player.deferredBotTask = var2_1;\n",
            "                this.player.currentBotTask = null;\n"
            "                this.player.deferredBotTask = null;\n",
        ),
    ],
    "com/rs2/model/gameplay/partyroom/PartyRoomBalloonSpawnTask.java": [
        (
            "        for (Position position : this.balloonPositions) {\n",
            "        for (Object balloonPositionObject : this.balloonPositions) {\n"
            "            Position position = (Position)balloonPositionObject;\n",
        ),
    ],
    "com/rs2/model/quest/event/FrozenBotReloginTask.java": [
        (
            "        for (String string : this.a) {\n",
            "        for (Object reloginNameObject : this.a) {\n"
            "            String string = (String)reloginNameObject;\n",
        ),
    ],
    "com/rs2/model/skill/fishing/FishingHandler.java": [
        (
            "            n2 = object.getSourceNpcIds()[0];\n",
            "            n2 = ((FishingWhirlpool)object).getSourceNpcIds()[0];\n",
        ),
    ],
    "com/rs2/model/skill/farming/CompostBinManager.java": [
        (
            "            object = CompostBin.forPosition(object);\n",
            "            object = CompostBin.forPosition((Position)object);\n",
        ),
    ],
    "com/rs2/model/skill/farming/AllotmentPatch.java": [
        (
            "                    if (!FarmingPatchUtils.containsPosition(allotmentPatch2.bounds[2], allotmentPatch3.bounds[3], position)) break block4;\n",
            "                    if (!FarmingPatchUtils.containsPosition(allotmentPatch.bounds[2], allotmentPatch3.bounds[3], position)) break block4;\n",
        ),
    ],
    "com/rs2/model/skill/mining/MiningManager.java": [
        (
            "        Object object = new int[]{10587, 10585, 10586, 14832, 14833, 14834, 10944, 10945, 9723, 9724, 9725, 11555, 11552, 11553, 11554, 11557, 11556, 450, 451, 452};\n"
            "        int[] nArray = object;\n",
            "        int[] nArray = new int[]{10587, 10585, 10586, 14832, 14833, 14834, 10944, 10945, 9723, 9724, 9725, 11555, 11552, 11553, 11554, 11557, 11556, 450, 451, 452};\n",
        ),
        (
            "        MineableRockDefinition mineableRockDefinition = MineableRockDefinition.forObjectId(n);\n"
            "        object = (Object)mineableRockDefinition;\n"
            "        if (mineableRockDefinition == null) {\n"
            "            return false;\n"
            "        }\n"
            "        if (this.player.getQuestState(0) != 1) {\n"
            "            this.player.getDialogueManager().showTutorialInstructionOverlay(\"Please wait.\", \"\", \"Your character is now attempting to prospect the rock. This\", \"should only take a few seconds.\", \"\", true);\n"
            "        }\n"
            "        Player player = this.player;\n"
            "        player.packetSender.sendGameMessage(\"You examine the rock for ores...\");\n"
            "        this.player.setActionLocked(true);\n"
            "        int n4 = MiningManager.rollMinedItemId(n, MineableRockDefinition.getOreItemId((MineableRockDefinition)((Object)object)));\n"
            "        ItemService.getInstance();\n"
            "        object = ItemService.getItemName(n4).toLowerCase().replaceAll(\"ore\", \"\").trim();\n"
            "        CycleEventHandler.getInstance().schedule(this.player, new ProspectingTask(this, n, (String)object), 5);\n",
            "        MineableRockDefinition mineableRockDefinition = MineableRockDefinition.forObjectId(n);\n"
            "        if (mineableRockDefinition == null) {\n"
            "            return false;\n"
            "        }\n"
            "        if (this.player.getQuestState(0) != 1) {\n"
            "            this.player.getDialogueManager().showTutorialInstructionOverlay(\"Please wait.\", \"\", \"Your character is now attempting to prospect the rock. This\", \"should only take a few seconds.\", \"\", true);\n"
            "        }\n"
            "        Player player = this.player;\n"
            "        player.packetSender.sendGameMessage(\"You examine the rock for ores...\");\n"
            "        this.player.setActionLocked(true);\n"
            "        int n4 = MiningManager.rollMinedItemId(n, MineableRockDefinition.getOreItemId(mineableRockDefinition));\n"
            "        ItemService.getInstance();\n"
            "        String oreName = ItemService.getItemName(n4).toLowerCase().replaceAll(\"ore\", \"\").trim();\n"
            "        CycleEventHandler.getInstance().schedule(this.player, new ProspectingTask(this, n, oreName), 5);\n",
        ),
    ],
    "com/rs2/model/c/ProjectileDefinition.java": [
        (
            "        if (entity.getTransformTicksRemaining() > 0) {\n",
            "        if (((Npc)entity).getTransformTicksRemaining() > 0) {\n",
        ),
    ],
    "com/rs2/model/npc/NpcDefinition.java": [
        (
            "                Object object3 = object;\n"
            "                World.getNpcDefinitions()[((NpcDefinition)object3).id] = object;\n",
            "                NpcDefinition npcDefinition2 = (NpcDefinition)object;\n"
            "                World.getNpcDefinitions()[npcDefinition2.id] = npcDefinition2;\n",
        ),
        (
            "        catch (IOException iOException) {\n"
            "            object = iOException;\n"
            "            iOException.printStackTrace();\n"
            "        }\n",
            "        catch (Exception exception) {\n"
            "            object = exception;\n"
            "            exception.printStackTrace();\n"
            "        }\n",
        ),
    ],
    "com/rs2/model/skill/crafting/CraftingHandler.java": [
        (
            "        for (Map.Entry entry : PotteryRecipe.definitionsByButtonId.entrySet()) {\n"
            "            PotteryRecipe object3 = (PotteryRecipe)((Object)entry.getValue());\n",
            "        for (Object entryObject : PotteryRecipe.definitionsByButtonId.entrySet()) {\n"
            "            Map.Entry entry = (Map.Entry)entryObject;\n"
            "            PotteryRecipe object3 = (PotteryRecipe)((Object)entry.getValue());\n",
        ),
    ],
    "com/rs2/model/skill/crafting/JewelleryCraftingHandler.java": [
        (
            "        if ((n == 1592 ? 0 : (n == 1597 ? 1 : (n2 = n == 1595 ? 2 : -1))) < 0) {\n"
            "            return;\n"
            "        }\n",
            "        n2 = n == 1592 ? 0 : (n == 1597 ? 1 : (n == 1595 ? 2 : -1));\n"
            "        if (n2 < 0) {\n"
            "            return;\n"
            "        }\n",
        ),
    ],
    "com/rs2/model/gameplay/fightcave/FightCaveController.java": [
        (
            "        for (Object object2 : this.player.getFightCaveNpcs()) {\n"
            "            ((Npc)object2).setActive(false);\n"
            "            World.unregisterNpc((Npc)object2);\n"
            "        }\n",
            "        for (Object fightCaveNpcObject : this.player.getFightCaveNpcs()) {\n"
            "            Npc npc = (Npc)fightCaveNpcObject;\n"
            "            npc.setActive(false);\n"
            "            World.unregisterNpc(npc);\n"
            "        }\n",
        ),
    ],
    "com/rs2/model/gameplay/SwampGasExplosionTask.java": [
        (
            "            ItemStack itemStack = object;\n",
            "            ItemStack itemStack = (ItemStack)object;\n",
        ),
    ],
    "com/rs2/model/skill/cooking/CookingTask.java": [
        (
            "        if (object.interfaceAction.equals(\"cookFire\") && !SkillActionHelper.isObjectPresent(this.player.getCookingObjectId(), this.player.getCookingManager().firePosition.getX(), this.player.getCookingManager().firePosition.getY(), this.player.getCookingManager().firePosition.getPlane())) {\n",
            "        if (((Player)object).interfaceAction.equals(\"cookFire\") && !SkillActionHelper.isObjectPresent(this.player.getCookingObjectId(), this.player.getCookingManager().firePosition.getX(), this.player.getCookingManager().firePosition.getY(), this.player.getCookingManager().firePosition.getPlane())) {\n",
        ),
    ],
    "com/rs2/model/skill/magic/NecromancyReanimateTask.java": [
        (
            "        GameplayHelper.spawnOwnedNpcAdjacentToPlayer(MagicSpellAction.getPlayer(this.spellAction), entity, true, false);\n",
            "        GameplayHelper.spawnOwnedNpcAdjacentToPlayer(MagicSpellAction.getPlayer(this.spellAction), (Npc)entity, true, false);\n",
        ),
    ],
    "com/rs2/model/skill/runecrafting/MysteriousRuinsTeleportTask.java": [
        (
            "        Object object = this.player;\n"
            "        object.packetSender.sendGameMessage(\"You feel a powerful force take hold of you...\");\n"
            "        object = this.altarDefinition;\n",
            "        this.player.packetSender.sendGameMessage(\"You feel a powerful force take hold of you...\");\n"
            "        Object object = this.altarDefinition;\n",
        ),
    ],
    "com/rs2/model/EntityTargetMovement.java": [
        (
            "        if (this.entity.isNpc()) {\n"
            "            Entity entity2 = (Npc)this.entity;\n"
            "            if (this.entity.isOverlapping(entity)) {\n"
            "                this.moveAwayFromOverlap();\n"
            "                return;\n"
            "            }\n"
            "            if (EntityTargetMovement.canReachTarget(this.entity, entity)) {\n"
            "                return;\n"
            "            }\n"
            "            Npc npc = entity2;\n"
            "            entity2 = entity;\n"
            "            entity = npc;\n",
            "        if (this.entity.isNpc()) {\n"
            "            Npc npc = (Npc)this.entity;\n"
            "            if (this.entity.isOverlapping(entity)) {\n"
            "                this.moveAwayFromOverlap();\n"
            "                return;\n"
            "            }\n"
            "            if (EntityTargetMovement.canReachTarget(this.entity, entity)) {\n"
            "                return;\n"
            "            }\n"
            "            Entity entity2 = entity;\n"
            "            entity = npc;\n",
        ),
    ],
    "com/rs2/model/combat/ProjectileTiming.java": [
        (
            "        int n = 40;\n"
            "        ProjectileTiming projectileTiming = b.copy();\n"
            "        b.copy().startDelay = n;\n"
            "        v0.speed = n = 2;\n"
            "        c = projectileTiming;\n",
            "        ProjectileTiming projectileTiming = b.copy();\n"
            "        projectileTiming.startDelay = 40;\n"
            "        projectileTiming.speed = 2;\n"
            "        c = projectileTiming;\n",
        ),
    ],
    "com/rs2/model/combat/special/DarkBowSpecialAttack.java": [
        ("            double d2;\n", ""),
        ("            if (d2 >= 48.0) {\n", "            if (d >= 48.0) {\n"),
    ],
    "com/rs2/model/combat/special/MagicShortbowSpecialAttack.java": [
        (
            "import com.rs2.model.c.ProjectileDefinition;\n",
            "import com.rs2.model.c.ProjectileDefinition;\n"
            "import com.rs2.model.combat.AmmunitionProfile;\n",
        ),
        (
            "        Object object2 = this.sourceWeaponProfile.getAmmunitionProfile();\n",
            "        AmmunitionProfile ammunitionProfile = this.sourceWeaponProfile.getAmmunitionProfile();\n",
        ),
        (
            "        if (object == null || object2 == null) {\n",
            "        if (object == null || ammunitionProfile == null) {\n",
        ),
        (
            "        object = this.player.isPlayer() ? this.player.getEquipmentManager().getContainer().getItemAt(object2.getEquipmentSlot()) : null;\n",
            "        object = this.player.isPlayer() ? this.player.getEquipmentManager().getContainer().getItemAt(ammunitionProfile.getEquipmentSlot()) : null;\n",
        ),
        (
            "        this.setRequirements(new CombatRequirement[]{new MagicShortbowArrowRequirement(this, object2.getEquipmentSlot(), ((ItemStack)object).getId(), 2, true)});\n",
            "        this.setRequirements(new CombatRequirement[]{new MagicShortbowArrowRequirement(this, ammunitionProfile.getEquipmentSlot(), ((ItemStack)object).getId(), 2, true)});\n",
        ),
        (
            "        object = object2.getProjectileTiming();\n",
            "        object = ammunitionProfile.getProjectileTiming();\n",
        ),
        (
            "        object = ammunitionProfile.getProjectileTiming();\n"
            "        object2 = new ProjectileDefinition(249, ((ProjectileTiming)object).copy().setStartDelay(20).setSpeed(3));\n"
            "        object = new ProjectileDefinition(249, ((ProjectileTiming)object).copy().setStartDelay(50).setSpeed(2));\n"
            "        this.setHitDefinitions(new HitDefinition[]{new HitDefinition(this.getAttackStyle(), HitType.NORMAL, d).enableRandomDamage().setAccuracyMultiplier(1.1).setProjectile((ProjectileDefinition)object2).setDelay(1).enableAccuracyCheck(true), new HitDefinition(this.getAttackStyle(), HitType.NORMAL, d).enableRandomDamage().setAccuracyMultiplier(1.1).setProjectile((ProjectileDefinition)object).setDelay(0).enableAccuracyCheck(true)});\n",
            "        ProjectileTiming projectileTiming = ammunitionProfile.getProjectileTiming();\n"
            "        ProjectileDefinition firstProjectile = new ProjectileDefinition(249, projectileTiming.copy().setStartDelay(20).setSpeed(3));\n"
            "        ProjectileDefinition secondProjectile = new ProjectileDefinition(249, projectileTiming.copy().setStartDelay(50).setSpeed(2));\n"
            "        this.setHitDefinitions(new HitDefinition[]{new HitDefinition(this.getAttackStyle(), HitType.NORMAL, d).enableRandomDamage().setAccuracyMultiplier(1.1).setProjectile(firstProjectile).setDelay(1).enableAccuracyCheck(true), new HitDefinition(this.getAttackStyle(), HitType.NORMAL, d).enableRandomDamage().setAccuracyMultiplier(1.1).setProjectile(secondProjectile).setDelay(0).enableAccuracyCheck(true)});\n",
        ),
    ],
    "com/rs2/model/gameplay/duel/NoMeleeDuelRule.java": [
        (
            "            if (!object.isEnabledFor(player2)) {\n",
            "            if (!((DuelRule)object).isEnabledFor(player2)) {\n",
        ),
    ],
    "com/rs2/model/gameplay/duel/NoRangedDuelRule.java": [
        (
            "            if (!object.isEnabledFor(player2)) {\n",
            "            if (!((DuelRule)object).isEnabledFor(player2)) {\n",
        ),
    ],
    "com/rs2/model/gameplay/duel/DuelSession.java": [
        (
            "        ItemStack[] itemStackArray = player2.getDuelSession();\n"
            "        itemStackArray = DuelSession.toItemArray(itemStackArray.stakedItems);\n",
            "        ItemStack[] itemStackArray = DuelSession.toItemArray(player2.getDuelSession().stakedItems);\n",
        ),
    ],
    "com/rs2/model/gameplay/duel/DuelInterfaceManager.java": [
        (
            "            this.player.interfaceAction = object;\n",
            "            this.player.interfaceAction = (String)object;\n",
        ),
        (
            "        for (ItemStack itemStack : this.player.getDuelSession().getEquipmentToRemove()) {\n"
            "            if (itemStack.getId() <= 0) continue;\n"
            "            ++n;\n"
            "        }\n",
            "        for (Object itemObject : this.player.getDuelSession().getEquipmentToRemove()) {\n"
            "            ItemStack itemStack = (ItemStack)itemObject;\n"
            "            if (itemStack.getId() <= 0) continue;\n"
            "            ++n;\n"
            "        }\n",
        ),
    ],
    "com/rs2/model/skill/thieving/StallThievingHandler.java": [
        (
            "                object = stallDefinitionArray[n6];\n"
            "                int[] nArray = object.getObjectIds();\n",
            "                stallDefinition = stallDefinitionArray[n6];\n"
            "                int[] nArray = stallDefinition.getObjectIds();\n",
        ),
        (
            "                        stallDefinition2 = object;\n",
            "                        stallDefinition2 = stallDefinition;\n",
        ),
    ],
    "com/rs2/model/skill/magic/Spellbook.java": [
        (
            "public final class Spellbook\n"
            "extends Enum {\n",
            "public final class Spellbook {\n",
        ),
        (
            "    private Map spellByButtonId;\n"
            "    private Map autocastSpellByButtonId;\n"
            "    private static final /* synthetic */ Spellbook[] VALUES;\n",
            "    private Map spellByButtonId;\n"
            "    private Map autocastSpellByButtonId;\n"
            "    private final String name;\n"
            "    private final int ordinal;\n"
            "    private static final /* synthetic */ Spellbook[] VALUES;\n",
        ),
        (
            "    /*\n"
            "     * WARNING - void declaration\n"
            "     */\n"
            "    private Spellbook(String string, int n, Map map, Map map2) {\n"
            "        super(string, n);\n"
            "        this.spellByButtonId = map;\n",
            "    private Spellbook(String string, int n, Map map, Map map2) {\n"
            "        this.name = string;\n"
            "        this.ordinal = n;\n"
            "        this.spellByButtonId = map;\n",
        ),
        (
            "    public final Map getSpellByButtonId() {\n"
            "        return this.spellByButtonId;\n"
            "    }\n"
            "\n"
            "    public static Spellbook[] values() {\n",
            "    public final Map getSpellByButtonId() {\n"
            "        return this.spellByButtonId;\n"
            "    }\n"
            "\n"
            "    public final String name() {\n"
            "        return this.name;\n"
            "    }\n"
            "\n"
            "    public final int ordinal() {\n"
            "        return this.ordinal;\n"
            "    }\n"
            "\n"
            "    public final String toString() {\n"
            "        return this.name;\n"
            "    }\n"
            "\n"
            "    public static Spellbook[] values() {\n",
        ),
        (
            "    public static Spellbook valueOf(String string) {\n"
            "        return Enum.valueOf(Spellbook.class, string);\n"
            "    }\n",
            "    public static Spellbook valueOf(String string) {\n"
            "        if (string == null) {\n"
            "            throw new NullPointerException(\"Name is null\");\n"
            "        }\n"
            "        Spellbook[] spellbookArray = Spellbook.values();\n"
            "        int n = spellbookArray.length;\n"
            "        int n2 = 0;\n"
            "        while (n2 < n) {\n"
            "            Spellbook spellbook = spellbookArray[n2];\n"
            "            if (spellbook.name().equals(string)) {\n"
            "                return spellbook;\n"
            "            }\n"
            "            ++n2;\n"
            "        }\n"
            "        throw new IllegalArgumentException(\"No enum constant com.rs2.model.skill.magic.Spellbook.\" + string);\n"
            "    }\n",
        ),
    ],
    "com/rs2/model/skill/woodcutting/WoodcuttingTask.java": [
        (
            "            object = new Position(this.x, this.y, this.player.getPosition().getPlane());\n"
            "            Object object2 = this.treeDefinition;\n"
            "            if (object2.getEntNpcIds() == null) {\n"
            "                bl = false;\n"
            "            } else {\n"
            "                boolean bl2;\n"
            "                int[] nArray = object2.getEntNpcIds();\n"
            "                int n2 = nArray.length;\n"
            "                int n3 = 0;\n"
            "                while (n3 < n2) {\n"
            "                    int n4 = nArray[n3];\n"
            "                    object2 = Npc.findByDefinitionIdAtPosition(n4, (Position)object);\n"
            "                    if (object2 != null) {\n"
            "                        bl = true;\n"
            "                        break block39;\n"
            "                    }\n"
            "                    ++n3;\n"
            "                }\n"
            "                bl = bl2 = false;\n"
            "            }\n",
            "            object = new Position(this.x, this.y, this.player.getPosition().getPlane());\n"
            "            TreeDefinition treeDefinition = this.treeDefinition;\n"
            "            if (treeDefinition.getEntNpcIds() == null) {\n"
            "                bl = false;\n"
            "            } else {\n"
            "                int[] nArray = treeDefinition.getEntNpcIds();\n"
            "                int n2 = nArray.length;\n"
            "                int n3 = 0;\n"
            "                while (n3 < n2) {\n"
            "                    int n4 = nArray[n3];\n"
            "                    Npc entNpc = Npc.findByDefinitionIdAtPosition(n4, (Position)object);\n"
            "                    if (entNpc != null) {\n"
            "                        bl = true;\n"
            "                        break block39;\n"
            "                    }\n"
            "                    ++n3;\n"
            "                }\n"
            "                bl = false;\n"
            "            }\n",
        ),
    ],
    "com/rs2/model/combat/WeaponProfile.java": [
        (
            "public final class WeaponProfile\n"
            "extends Enum {\n",
            "public final class WeaponProfile {\n",
        ),
        (
            "    private int[] movementAnimations;\n"
            "    private int blockAnimationId;\n"
            "    private int attackDelay;\n"
            "    private static final /* synthetic */ WeaponProfile[] Z;\n",
            "    private int[] movementAnimations;\n"
            "    private int blockAnimationId;\n"
            "    private int attackDelay;\n"
            "    private final String name;\n"
            "    private final int ordinal;\n"
            "    private static final /* synthetic */ WeaponProfile[] Z;\n",
        ),
        (
            "    /*\n"
            "     * WARNING - Possible parameter corruption\n"
            "     * WARNING - void declaration\n"
            "     */\n"
            "    private WeaponProfile(String string, int n, WeaponInterfaceDefinition weaponInterfaceDefinition, AmmunitionProfile ammunitionProfile, int n2, int[] nArray, int[] nArray2, int n3) {\n"
            "        super(string, n);\n"
            "        this.interfaceDefinition = weaponInterfaceDefinition;\n",
            "    private WeaponProfile(String string, int n, WeaponInterfaceDefinition weaponInterfaceDefinition, AmmunitionProfile ammunitionProfile, int n2, int[] nArray, int[] nArray2, int n3) {\n"
            "        this.name = string;\n"
            "        this.ordinal = n;\n"
            "        this.interfaceDefinition = weaponInterfaceDefinition;\n",
        ),
        (
            "    public final WeaponInterfaceDefinition getInterfaceDefinition() {\n",
            "    public final String name() {\n"
            "        return this.name;\n"
            "    }\n"
            "\n"
            "    public final int ordinal() {\n"
            "        return this.ordinal;\n"
            "    }\n"
            "\n"
            "    public final String toString() {\n"
            "        return this.name;\n"
            "    }\n"
            "\n"
            "    public final WeaponInterfaceDefinition getInterfaceDefinition() {\n",
        ),
        (
            "    public static WeaponProfile valueOf(String string) {\n"
            "        return Enum.valueOf(WeaponProfile.class, string);\n"
            "    }\n",
            "    public static WeaponProfile valueOf(String string) {\n"
            "        if (string == null) {\n"
            "            throw new NullPointerException(\"Name is null\");\n"
            "        }\n"
            "        WeaponProfile[] weaponProfileArray = WeaponProfile.values();\n"
            "        int n = weaponProfileArray.length;\n"
            "        int n2 = 0;\n"
            "        while (n2 < n) {\n"
            "            WeaponProfile weaponProfile = weaponProfileArray[n2];\n"
            "            if (weaponProfile.name().equals(string)) {\n"
            "                return weaponProfile;\n"
            "            }\n"
            "            ++n2;\n"
            "        }\n"
            "        throw new IllegalArgumentException(\"No enum constant com.rs2.model.combat.WeaponProfile.\" + string);\n"
            "    }\n",
        ),
    ],
    "com/rs2/model/combat/WeaponInterfaceDefinition.java": [
        (
            "        this(string, n, n2, n3, nArray, -1, -1, -1, attackStyleDefinitionArray);\n",
            "        this(n2, n3, nArray, -1, -1, -1, attackStyleDefinitionArray);\n",
        ),
    ],
    "com/rs2/model/c/aM.java": [
        (
            "public abstract class aM\n"
            "extends Enum {\n",
            "public abstract class aM {\n",
        ),
        (
            "    private static /* enum */ aM a = new aN();\n"
            "    private static final /* synthetic */ aM[] b;\n",
            "    private static /* enum */ aM a = new aN();\n"
            "    private final String name;\n"
            "    private final int ordinal;\n"
            "    private static final /* synthetic */ aM[] b;\n",
        ),
        (
            "    private aM(String string, int n) {\n"
            "        super(string, n);\n"
            "    }\n",
            "    protected aM(String string, int n) {\n"
            "        this.name = string;\n"
            "        this.ordinal = n;\n"
            "    }\n",
        ),
        (
            "    public static aM[] values() {\n",
            "    public final String name() {\n"
            "        return this.name;\n"
            "    }\n"
            "\n"
            "    public final int ordinal() {\n"
            "        return this.ordinal;\n"
            "    }\n"
            "\n"
            "    public String toString() {\n"
            "        return this.name;\n"
            "    }\n"
            "\n"
            "    public static aM[] values() {\n",
        ),
        (
            "    public static aM valueOf(String string) {\n"
            "        return Enum.valueOf(aM.class, string);\n"
            "    }\n",
            "    public static aM valueOf(String string) {\n"
            "        if (string == null) {\n"
            "            throw new NullPointerException(\"Name is null\");\n"
            "        }\n"
            "        aM[] aMArray = aM.values();\n"
            "        int n = aMArray.length;\n"
            "        int n2 = 0;\n"
            "        while (n2 < n) {\n"
            "            aM aM2 = aMArray[n2];\n"
            "            if (aM2.name().equals(string)) {\n"
            "                return aM2;\n"
            "            }\n"
            "            ++n2;\n"
            "        }\n"
            "        throw new IllegalArgumentException(\"No enum constant com.rs2.model.c.aM.\" + string);\n"
            "    }\n",
        ),
    ],
    "com/rs2/bot/route/BotWorldRouteChoice.java": [
        (
            "    public static boolean showCrypticDigClue(Player stringArray, int n) {\n",
            "    public static boolean showCrypticDigClue(Player player, int n) {\n",
        ),
        (
            "        String[] stringArray2 = stringArray;\n"
            "        stringArray.packetSender.showInterface(6965);\n",
            "        String[] stringArray2;\n"
            "        player.packetSender.showInterface(6965);\n",
        ),
        (
            "            stringArray2 = stringArray;\n"
            "            PacketSender packetSender = stringArray.packetSender;\n",
            "            PacketSender packetSender = player.packetSender;\n",
        ),
    ],
    "com/rs2/cache/CacheFile.java": [
        (
            "    public static boolean showSearchClue(Player stringArray, int n) {\n",
            "    public static boolean showSearchClue(Player player, int n) {\n",
        ),
        (
            "        String[] stringArray2 = stringArray;\n"
            "        stringArray.packetSender.showInterface(6965);\n",
            "        String[] stringArray2;\n"
            "        player.packetSender.showInterface(6965);\n",
        ),
        (
            "            stringArray2 = stringArray;\n"
            "            PacketSender packetSender = stringArray.packetSender;\n",
            "            PacketSender packetSender = player.packetSender;\n",
        ),
    ],
    "com/rs2/model/player/PetManager.java": [
        (
            "        this.activePetNpc.a(new Position(this.owner.getPosition().getX() - 1, this.owner.getPosition().getY(), this.owner.getPosition().getPlane()));\n",
            "        this.activePetNpc.moveTo(new Position(this.owner.getPosition().getX() - 1, this.owner.getPosition().getY(), this.owner.getPosition().getPlane()));\n",
        ),
        (
            "        this.activePetNpc.a(new Position(this.owner.getPosition().getX(), this.owner.getPosition().getY() - 1, this.owner.getPosition().getPlane()));\n",
            "        this.activePetNpc.moveTo(new Position(this.owner.getPosition().getX(), this.owner.getPosition().getY() - 1, this.owner.getPosition().getPlane()));\n",
        ),
    ],
    "com/rs2/model/combat/CombatAction.java": [
        (
            "    public static boolean handlePickpocketAttempt(Player player, Npc npc) {\n"
            "        Object object;\n"
            "        int n;\n"
            "        int n2;\n"
            "        Object object2;\n"
            "        String string;\n"
            "        String string2;\n"
            "        block9: {\n"
            "            if (player == null || player.isStunned() || !player.getSkillManager().tryStartActionDelay(2200)) {\n"
            "                return true;\n"
            "            }\n"
            "            string2 = npc.getDefinition().getName().toLowerCase();\n"
            "            string = string2.toLowerCase();\n"
            "            object2 = PickpocketDefinition.values();\n"
            "            n2 = ((PickpocketDefinition[])object2).length;\n"
            "            n = 0;\n"
            "            while (n < n2) {\n"
            "                PickpocketDefinition pickpocketDefinition = object2[n];\n"
            "                String[] stringArray = pickpocketDefinition.getNpcNames();\n"
            "                int n3 = stringArray.length;\n"
            "                int n4 = 0;\n"
            "                while (n4 < n3) {\n"
            "                    String string3 = stringArray[n4];\n"
            "                    if (string.equalsIgnoreCase(string3)) {\n"
            "                        object = pickpocketDefinition;\n"
            "                        break block9;\n"
            "                    }\n"
            "                    ++n4;\n"
            "                }\n"
            "                ++n;\n"
            "            }\n"
            "            object = string = null;\n"
            "        }\n"
            "        if (object == null) {\n"
            "            return false;\n"
            "        }\n"
            "        if (!ServerSettings.thievingEnabled) {\n"
            "            object2 = player;\n"
            "            object2.packetSender.sendGameMessage(\"This skill is currently disabled.\");\n"
            "            return true;\n"
            "        }\n"
            "        if (!player.isMember()) {\n"
            "            player.packetSender.sendGameMessage(\"You need a members account to access members content.\");\n"
            "            return true;\n"
            "        }\n"
            "        if (ServerSettings.freeToPlayWorld) {\n"
            "            player.packetSender.sendGameMessage(\"You need to be in members world to access members content.\");\n"
            "            return true;\n"
            "        }\n"
            "        if (!SkillActionHelper.checkSkillRequirement(player, 17, ((PickpocketDefinition)((Object)string)).getRequiredLevel(), \"pickpocket this npc\")) {\n"
            "            return true;\n"
            "        }\n"
            "        int bl = ((PickpocketDefinition)((Object)string)).getSuccessChanceLow();\n"
            "        n = ((PickpocketDefinition)((Object)string)).getSuccessChanceHigh();\n"
            "        boolean bl2 = GameUtil.rollLevelScaledChance(bl, n, player.getSkillManager().getCurrentLevels()[17]);\n"
            "        ItemStack itemStack = ((PickpocketDefinition)((Object)string)).getRareRewards() != null && GameUtil.randomInclusive(30) == 0 ? ((PickpocketDefinition)((Object)string)).getRareRewards()[GameUtil.randomExclusive(((PickpocketDefinition)((Object)string)).getRareRewards().length)] : ((PickpocketDefinition)((Object)string)).getCommonRewards()[GameUtil.randomExclusive(((PickpocketDefinition)((Object)string)).getCommonRewards().length)];\n"
            "        itemStack = new ItemStack(itemStack.getId(), itemStack.getAmount());\n"
            "        n2 = GameUtil.randomBetweenInclusive(((PickpocketDefinition)((Object)string)).getMinDamage(), ((PickpocketDefinition)((Object)string)).getMaxDamage());\n"
            "        player.setActionLocked(true);\n"
            "        player.getUpdateState().setAnimation(881);\n"
            "        object2 = player;\n"
            "        object2.packetSender.sendGameMessage(\"You attempt to pick the \" + string2 + \"'s pocket.\");\n"
            "        CycleEventHandler.getInstance().schedule(player, new PickpocketTask(bl2, player, npc, itemStack, (PickpocketDefinition)((Object)string), string2, n2), 2);\n"
            "        return true;\n"
            "    }\n",
            "    public static boolean handlePickpocketAttempt(Player player, Npc npc) {\n"
            "        if (player == null || player.isStunned() || !player.getSkillManager().tryStartActionDelay(2200)) {\n"
            "            return true;\n"
            "        }\n"
            "        String npcName = npc.getDefinition().getName().toLowerCase();\n"
            "        PickpocketDefinition definition = null;\n"
            "        PickpocketDefinition[] definitions = PickpocketDefinition.class.getEnumConstants();\n"
            "        if (definitions != null) {\n"
            "            for (PickpocketDefinition candidate : definitions) {\n"
            "                for (String name : candidate.getNpcNames()) {\n"
            "                    if (npcName.equalsIgnoreCase(name)) {\n"
            "                        definition = candidate;\n"
            "                        break;\n"
            "                    }\n"
            "                }\n"
            "                if (definition != null) {\n"
            "                    break;\n"
            "                }\n"
            "            }\n"
            "        }\n"
            "        if (definition == null) {\n"
            "            return false;\n"
            "        }\n"
            "        if (!ServerSettings.thievingEnabled) {\n"
            "            player.packetSender.sendGameMessage(\"This skill is currently disabled.\");\n"
            "            return true;\n"
            "        }\n"
            "        if (!player.isMember()) {\n"
            "            player.packetSender.sendGameMessage(\"You need a members account to access members content.\");\n"
            "            return true;\n"
            "        }\n"
            "        if (ServerSettings.freeToPlayWorld) {\n"
            "            player.packetSender.sendGameMessage(\"You need to be in members world to access members content.\");\n"
            "            return true;\n"
            "        }\n"
            "        if (!SkillActionHelper.checkSkillRequirement(player, 17, definition.getRequiredLevel(), \"pickpocket this npc\")) {\n"
            "            return true;\n"
            "        }\n"
            "        int lowChance = definition.getSuccessChanceLow();\n"
            "        int highChance = definition.getSuccessChanceHigh();\n"
            "        boolean successful = GameUtil.rollLevelScaledChance(lowChance, highChance, player.getSkillManager().getCurrentLevels()[17]);\n"
            "        ItemStack reward = definition.getRareRewards() != null && GameUtil.randomInclusive(30) == 0 ? definition.getRareRewards()[GameUtil.randomExclusive(definition.getRareRewards().length)] : definition.getCommonRewards()[GameUtil.randomExclusive(definition.getCommonRewards().length)];\n"
            "        reward = new ItemStack(reward.getId(), reward.getAmount());\n"
            "        int damage = GameUtil.randomBetweenInclusive(definition.getMinDamage(), definition.getMaxDamage());\n"
            "        player.setActionLocked(true);\n"
            "        player.getUpdateState().setAnimation(881);\n"
            "        player.packetSender.sendGameMessage(\"You attempt to pick the \" + npcName + \"'s pocket.\");\n"
            "        CycleEventHandler.getInstance().schedule(player, new PickpocketTask(successful, player, npc, reward, definition, npcName, damage), 2);\n"
            "        return true;\n"
            "    }\n",
        ),
        (
            "        if (this.target != null && this.attacker != null) {\n"
            "            n = this.attacker.isPlayer() ? ((Player)this.attacker).getQuestManager().getQuestDamageOverride(this.attacker, this.target) : ((Player)this.target).getQuestManager().getQuestDamageOverride(this.attacker, this.target);\n"
            "            if (this.attacker.isInMageArena() && this.hitDefinition.getAttackStyle().getCombatType() != CombatType.MAGIC) {\n"
            "                n = 0;\n"
            "            }\n"
            "            if (n != -1) {\n"
            "                this.damage = n;\n"
            "            }\n"
            "        }\n",
            "        if (this.target != null && this.attacker != null) {\n"
            "            int questDamageOverride = this.attacker.isPlayer() ? ((Player)this.attacker).getQuestManager().getQuestDamageOverride(this.attacker, this.target) : ((Player)this.target).getQuestManager().getQuestDamageOverride(this.attacker, this.target);\n"
            "            if (this.attacker.isInMageArena() && this.hitDefinition.getAttackStyle().getCombatType() != CombatType.MAGIC) {\n"
            "                questDamageOverride = 0;\n"
            "            }\n"
            "            if (questDamageOverride != -1) {\n"
            "                this.damage = questDamageOverride;\n"
            "            }\n"
            "        }\n",
        ),
        (
            "        if (this.hitDefinition.getSpell() != null && (this.hitDefinition.getSpell() == SpellDefinition.FLAMES_OF_ZAMORAK || this.hitDefinition.getSpell() == SpellDefinition.SARADOMIN_STRIKE || this.hitDefinition.getSpell() == SpellDefinition.CLAWS_OF_GUTHIX)) {\n"
            "            n = 0;\n"
            "            if (this.hitDefinition.getSpell() == SpellDefinition.FLAMES_OF_ZAMORAK) {\n"
            "                int n7 = n = this.hitSuccessful ? 290 : 293;\n"
            "            }\n"
            "            if (this.hitDefinition.getSpell() == SpellDefinition.SARADOMIN_STRIKE) {\n"
            "                int n8 = n = this.hitSuccessful ? 297 : 299;\n"
            "            }\n"
            "            if (this.hitDefinition.getSpell() == SpellDefinition.CLAWS_OF_GUTHIX) {\n"
            "                int n9 = n = this.hitSuccessful ? 291 : 296;\n"
            "            }\n",
            "        if (this.hitDefinition.getSpell() != null && (this.hitDefinition.getSpell() == SpellDefinition.FLAMES_OF_ZAMORAK || this.hitDefinition.getSpell() == SpellDefinition.SARADOMIN_STRIKE || this.hitDefinition.getSpell() == SpellDefinition.CLAWS_OF_GUTHIX)) {\n"
            "            int godSpellSoundId = 0;\n"
            "            if (this.hitDefinition.getSpell() == SpellDefinition.FLAMES_OF_ZAMORAK) {\n"
            "                godSpellSoundId = this.hitSuccessful ? 290 : 293;\n"
            "            }\n"
            "            if (this.hitDefinition.getSpell() == SpellDefinition.SARADOMIN_STRIKE) {\n"
            "                godSpellSoundId = this.hitSuccessful ? 297 : 299;\n"
            "            }\n"
            "            if (this.hitDefinition.getSpell() == SpellDefinition.CLAWS_OF_GUTHIX) {\n"
            "                godSpellSoundId = this.hitSuccessful ? 291 : 296;\n"
            "            }\n",
        ),
        (
            "                ((Player)entity2).packetSender.sendSoundEffect(n, 1, 0);\n",
            "                ((Player)entity2).packetSender.sendSoundEffect(godSpellSoundId, 1, 0);\n",
        ),
        (
            "                ((Player)entity).packetSender.sendSoundEffect(n, 1, 0);\n",
            "                ((Player)entity).packetSender.sendSoundEffect(godSpellSoundId, 1, 0);\n",
        ),
        (
            "                for (Object object32 : this.hitDefinition.getEffects()) {\n"
            "                    if (!this.target.canApplyCombatEffect((CombatEffect)object32)) continue;\n"
            "                    ((CombatEffect)object32).apply(this);\n"
            "                }\n",
            "                for (Object effectObject : this.hitDefinition.getEffects()) {\n"
            "                    if (!this.target.canApplyCombatEffect((CombatEffect)effectObject)) continue;\n"
            "                    ((CombatEffect)effectObject).apply(this);\n"
            "                }\n",
        ),
        (
            "                for (Object object32 : this.hitDefinition.getEffects()) {\n"
            "                    if (object32 == null) continue;\n"
            "                    ((CombatEffect)object32).afterApply(this);\n"
            "                }\n",
            "                for (Object effectObject : this.hitDefinition.getEffects()) {\n"
            "                    if (effectObject == null) continue;\n"
            "                    ((CombatEffect)effectObject).afterApply(this);\n"
            "                }\n",
        ),
        (
            "                    for (CombatEffect combatEffect : this.hitDefinition.getEffects()) {\n"
            "                        if (!this.target.canApplyCombatEffect(combatEffect)) continue;\n"
            "                        combatEffect.apply(this);\n"
            "                    }\n",
            "                    for (Object effectObject : this.hitDefinition.getEffects()) {\n"
            "                        CombatEffect combatEffect = (CombatEffect)effectObject;\n"
            "                        if (!this.target.canApplyCombatEffect(combatEffect)) continue;\n"
            "                        combatEffect.apply(this);\n"
            "                    }\n",
        ),
        (
            "            for (CombatEffect combatEffect : this.hitDefinition.getEffects()) {\n"
            "                if (combatEffect == null || !this.target.canApplyCombatEffect(combatEffect)) continue;\n"
            "                combatEffect.apply(this);\n"
            "            }\n",
            "            for (Object effectObject : this.hitDefinition.getEffects()) {\n"
            "                CombatEffect combatEffect = (CombatEffect)effectObject;\n"
            "                if (combatEffect == null || !this.target.canApplyCombatEffect(combatEffect)) continue;\n"
            "                combatEffect.apply(this);\n"
            "            }\n",
        ),
    ],
    "com/rs2/model/combat/hit/HitDefinition.java": [
        (
            "                HitDefinition hitDefinition3 = this;\n"
            "                hitDefinition2 = hitDefinition3;\n"
            "                hitDefinition2 = this;\n"
            "                hitDefinition.addEffects(hitDefinition3.effects.toArray(new CombatEffect[hitDefinition2.effects.size()]));\n",
            "                CombatEffect[] effects = (CombatEffect[])this.effects.toArray(new CombatEffect[this.effects.size()]);\n"
            "                hitDefinition.addEffects(effects);\n",
        ),
        (
            "        if (object != null) {\n"
            "            CombatEffect[] combatEffectArray = object;\n"
            "            int n = ((CombatEffect[])object).length;\n"
            "            int n2 = 0;\n"
            "            while (n2 < n) {\n"
            "                object = combatEffectArray[n2];\n"
            "                if (object != null) {\n"
            "                    this.effects.add(object);\n"
            "                }\n"
            "                ++n2;\n"
            "            }\n"
            "        }\n",
            "        if (object != null) {\n"
            "            CombatEffect[] combatEffectArray = object;\n"
            "            int n = object.length;\n"
            "            int n2 = 0;\n"
            "            while (n2 < n) {\n"
            "                CombatEffect combatEffect = combatEffectArray[n2];\n"
            "                if (combatEffect != null) {\n"
            "                    this.effects.add(combatEffect);\n"
            "                }\n"
            "                ++n2;\n"
            "            }\n"
            "        }\n",
        ),
        (
            "            int n2 = GameUtil.getDistance(position, (Position)object);\n"
            "            object = this.projectile.getTiming();\n"
            "            double d = (double)(((ProjectileTiming)object).getStartDelay() + ((ProjectileTiming)object).getSpeed()) + (double)n2 * 5.0;\n",
            "            int n2 = GameUtil.getDistance(position, object);\n"
            "            ProjectileTiming timing = this.projectile.getTiming();\n"
            "            double d = (double)(timing.getStartDelay() + timing.getSpeed()) + (double)n2 * 5.0;\n",
        ),
    ],
    "com/rs2/model/item/action/BirdNestSearchHandler.java": [
        (
            "        int n3 = n2 <= 60 ? nArray3[GameUtil.randomInclusive(nArray3.length - 1)] : (n2 <= 80 ? object[GameUtil.randomInclusive(((int[])object).length - 1)] : (n2 <= 95 ? nArray2[GameUtil.randomInclusive(nArray2.length - 1)] : nArray[GameUtil.randomInclusive(0)]));\n",
            "        int n3 = n2 <= 60 ? nArray3[GameUtil.randomInclusive(nArray3.length - 1)] : (n2 <= 80 ? ((int[])object)[GameUtil.randomInclusive(((int[])object).length - 1)] : (n2 <= 95 ? nArray2[GameUtil.randomInclusive(nArray2.length - 1)] : nArray[GameUtil.randomInclusive(0)]));\n",
        ),
    ],
    "com/rs2/model/item/ItemContainerTab.java": [
        ("        this(0);\n", "        this((byte)0);\n"),
    ],
    "com/rs2/model/item/DegradableEquipmentHandler.java": [
        ("                ItemStack itemStack = object;\n", "                ItemStack itemStack = (ItemStack)object;\n"),
    ],
    "com/rs2/model/skill/magic/MagicSpellAction.java": [
        ("    final boolean tryStartCast() {\n", "    public final boolean tryStartCast() {\n"),
        (
            "                new WoodcuttingHandler(this.player.getPosition(), this.player.getSize(), position, 0, projectile).a();\n",
            "                new WoodcuttingHandler(this.player.getPosition(), this.player.getSize(), position, 0, projectile).sendProjectileToNearbyPlayers();\n",
        ),
    ],
}


def replace_exact(text: str, old: str, new: str, path: Path) -> tuple[str, int]:
    count = text.count(old)
    if count == 0:
        preview = old.strip().splitlines()[0][:120] if old.strip() else "<empty>"
        raise SystemExit(f"{path}: expected snippet not found: {preview!r}")
    return text.replace(old, new), count


def repair_database_query_signatures(source_root: Path) -> int:
    old = "    public final ResultSet executeStatement(PreparedStatement preparedStatement) {\n"
    new = "    public final ResultSet executeStatement(PreparedStatement preparedStatement) throws java.sql.SQLException {\n"
    repairs = 0
    for path in source_root.rglob("*.java"):
        text = path.read_text(encoding="utf-8")
        count = text.count(old)
        if count:
            path.write_text(text.replace(old, new), encoding="utf-8")
            repairs += count
    return repairs


def repair_database_callback_signatures(source_root: Path) -> int:
    replacements = {
        "    public final void onResult(ResultSet resultSet) {\n": "    public final void onResult(ResultSet resultSet) throws java.sql.SQLException {\n",
        "    public void onResult(ResultSet resultSet) {\n": "    public void onResult(ResultSet resultSet) throws java.sql.SQLException {\n",
        "    public final void onResult(ResultSet object) {\n": "    public final void onResult(ResultSet object) throws java.sql.SQLException {\n",
    }
    repairs = 0
    for path in source_root.rglob("*.java"):
        text = path.read_text(encoding="utf-8")
        new_text = text
        for old, new in replacements.items():
            count = new_text.count(old)
            if count:
                new_text = new_text.replace(old, new)
                repairs += count
        if new_text != text:
            path.write_text(new_text, encoding="utf-8")
    return repairs


def repair_grand_exchange_comparator(path: Path) -> int:
    text = path.read_text(encoding="utf-8")
    if "int compare(Object object, Object object2)" in text:
        return 0
    insert = """
    public final int compare(Object object, Object object2) {
        GrandExchangePriceSample sample = (GrandExchangePriceSample)object;
        GrandExchangePriceSample sample2 = (GrandExchangePriceSample)object2;
        return Long.compare(sample2.timestampMillis, sample.timestampMillis);
    }
"""
    marker = "\n}\n"
    index = text.rfind(marker)
    if index == -1:
        raise SystemExit(f"{path}: class closing brace not found")
    path.write_text(text[:index] + insert + text[index:], encoding="utf-8")
    return 1


def repair_bot_combat_escape_handler(path: Path) -> int:
    text = path.read_text(encoding="utf-8")
    start_marker = (
        "    /*\n"
        "     * Enabled aggressive block sorting\n"
        "     */\n"
        "    static void processBotCombatEscape(Player arrayList) {\n"
    )
    end_marker = "\n\n    private static void walkFallbackEscapePath(Player player) {\n"
    start = text.find(start_marker)
    if start == -1:
        if "static void processBotCombatEscape(Player player)" in text:
            return 0
        raise SystemExit(f"{path}: processBotCombatEscape start marker not found")
    end = text.find(end_marker, start)
    if end == -1:
        raise SystemExit(f"{path}: processBotCombatEscape end marker not found")
    replacement = """    static void processBotCombatEscape(Player player) {
        if (player.botFoodItemId != -1) {
            FoodDefinition food = FoodDefinition.forItemId(player.botFoodItemId);
            if (food != null) {
                int healAmount = food.getHealAmount();
                int healedLevel = player.getSkillManager().getCurrentLevels()[3] + healAmount;
                player.getSkillManager();
                if (healedLevel <= SkillManager.getLevelForExperience(player.getSkillManager().getExperience()[3]) && !player.botFoodDepleted) {
                    if (player.botEatDelayTicks < 3) {
                        ++player.botEatDelayTicks;
                    } else {
                        BotCombatHelper.eatBotFood(player);
                        player.botEatDelayTicks = 0;
                    }
                }
            }
        }
        if (player.getPoisonDamage() > 0.0) {
            BotCombatHelper.drinkAntipoisonPotion(player);
        }
        if (!player.getRecentCombatTimer().hasElapsed()) {
            if (player.getSkillManager().getCurrentLevels()[5] > 1 && !player.getActivePrayers()[8]) {
                player.getSkillManager();
                if (SkillManager.getLevelForExperience(player.getSkillManager().getExperience()[5]) >= 25) {
                    player.getPrayerManager().togglePrayer(8);
                }
            }
            if (BotCombatHelper.hasPrayerLevelForCombatStyle(player, player.botOpponentCombatStyle)) {
                BotCombatHelper.toggleProtectionPrayerForOpponentStyle(player);
            }
        } else {
            player.getPrayerManager().deactivateAll();
        }
        if (player.getPosition().getY() < 3760 && player.botCombatState != null && player.botCombatState.equals("tele") && player.getEquipmentManager().getItemIdAtSlot(2) == 1712 && !player.isTeleblocked()) {
            BotCombatHelper.operateGloryTeleport(player);
        }
        if (player.getPosition().getY() < 3680 && player.botCombatState != null && player.botCombatState.equals("tele") && !player.isTeleblocked()) {
            if (player.getSpellbook() == Spellbook.MODERN) {
                MagicSpellAction.castSelfSpell(player, SpellDefinition.VARROCK_TELEPORT);
            } else if (player.getSpellbook() == Spellbook.ANCIENT) {
                MagicSpellAction.castSelfSpell(player, SpellDefinition.PADDEWWA_TELEPORT);
            }
        }
        if (BotCombatHelper.isPlayerInAnyArea(player, chaosTempleEscapeAreas) || player.botEscapeRouteName.equals("chaos temple")) {
            if (!player.botEscapeRouteName.equals("chaos temple")) {
                player.botEscapeRouteName = "chaos temple";
                player.botPathWaypointIndex = 0;
            }
            BotCombatHelper.advanceBotEscapeWaypoints(player, chaosTempleEscapeWaypoints);
            return;
        }
        if (BotCombatHelper.isPlayerInAnyArea(player, runeRockEscapeAreas) || player.botEscapeRouteName.equals("rune rocks")) {
            if (!player.botEscapeRouteName.equals("rune rocks")) {
                player.botEscapeRouteName = "rune rocks";
                player.botPathWaypointIndex = 0;
            }
            BotCombatHelper.advanceBotEscapeWaypoints(player, runeRockEscapeWaypoints);
            return;
        }
        if (BotCombatHelper.isPlayerInAnyArea(player, p2pGateSouthEscapeAreas) || player.botEscapeRouteName.equals("escape south of p2p gates")) {
            if (!player.botEscapeRouteName.equals("escape south of p2p gates")) {
                player.botEscapeRouteName = "escape south of p2p gates";
                player.botPathWaypointIndex = 0;
            }
            BotCombatHelper.advanceBotEscapeWaypoints(player, p2pGateSouthEscapeWaypoints);
            return;
        }
        if (BotCombatHelper.isPlayerInAnyArea(player, p2pGateSouthwestEscapeAreas) || player.botEscapeRouteName.equals("escape south west of p2p gates")) {
            if (!player.botEscapeRouteName.equals("escape south west of p2p gates")) {
                player.botEscapeRouteName = "escape south west of p2p gates";
                player.botPathWaypointIndex = 0;
            }
            BotCombatHelper.advanceBotEscapeWaypoints(player, p2pGateSouthwestEscapeWaypoints);
            return;
        }
        if (BotCombatHelper.isPlayerInAnyArea(player, ruinsNorthEscapeAreas) || player.botEscapeRouteName.equals("escape north of ruins")) {
            if (!player.botEscapeRouteName.equals("escape north of ruins")) {
                player.botEscapeRouteName = "escape north of ruins";
                player.botPathWaypointIndex = 0;
            }
            BotCombatHelper.advanceBotEscapeWaypoints(player, ruinsNorthEscapeWaypoints);
            return;
        }
        if (player.getPosition().getY() < 3904 && !player.botEscapeRouteName.equals("escape p2p gates")) {
            if (player.botEscapeRouteName.equals("")) {
                BotCombatEscapeHandler.walkFallbackEscapePath(player);
                player.botPathWaypointIndex = -1;
            }
            return;
        }
        if (!player.botEscapeRouteName.equals("escape p2p gates")) {
            player.botEscapeRouteName = "escape p2p gates";
            player.botPathWaypointIndex = 0;
        }
        Position gatePosition = new Position(3224, 3904);
        if (GameUtil.isWithinDistance(player.getPosition(), gatePosition, 1)) {
            ArrayList<Integer> objectTargets = new ArrayList<Integer>();
            objectTargets.add(1597);
            objectTargets.add(1596);
            player.interactWithBotObjectTargets(objectTargets);
            return;
        }
        BotCombatHelper.walkBotTowardPosition(player, gatePosition);
    }"""
    path.write_text(text[:start] + replacement + text[end:], encoding="utf-8")
    return 1


def repair_karamja_volcano_route_locals(source_root: Path) -> int:
    repairs = 0
    for relative_path in (
        "com/rs2/bot/tasks/KaramjaVolcanoNorthLesserDemonCombatBotTask.java",
        "com/rs2/bot/tasks/KaramjaVolcanoSouthLesserDemonCombatBotTask.java",
    ):
        path = source_root / relative_path
        text = path.read_text(encoding="utf-8")
        replacements = [
            ("    public final void advanceTaskRouteSegment(Player player, boolean n) {\n", "    public final void advanceTaskRouteSegment(Player player, boolean continuing) {\n"),
            ("player.botTaskState.equals(\"walk to task\") && n != 0", "player.botTaskState.equals(\"walk to task\") && continuing"),
            ("player.botTaskState.equals(\"walk to bank\") && n != 0", "player.botTaskState.equals(\"walk to bank\") && continuing"),
            ("if (n == 0)", "if (!continuing)"),
            ("            n = GameUtil.getRegionId(player.getPosition().getX(), player.getPosition().getY());\n", "            int regionId = GameUtil.getRegionId(player.getPosition().getX(), player.getPosition().getY());\n"),
            ("&& n == 12082", "&& regionId == 12082"),
            ("&& n == 11313", "&& regionId == 11313"),
            ("&& n == 11825", "&& regionId == 11825"),
            ("&& n == 11413", "&& regionId == 11413"),
        ]
        changed = 0
        for old, new in replacements:
            count = text.count(old)
            if count == 0:
                raise SystemExit(f"{path}: expected route-local snippet not found: {old!r}")
            text = text.replace(old, new)
            changed += count
        path.write_text(text, encoding="utf-8")
        repairs += changed
    return repairs


def repair_world_and_gameplay_helper(source_root: Path) -> int:
    repairs = 0

    gameplay_path = source_root / "com/rs2/model/GameplayHelper.java"
    text = gameplay_path.read_text(encoding="utf-8")
    text, count = replace_exact(
        text,
        "        player.ad();\n",
        "        player.clearCombatEffectTasks();\n",
        gameplay_path,
    )
    repairs += count
    gameplay_path.write_text(text, encoding="utf-8")

    world_path = source_root / "com/rs2/model/World.java"
    text = world_path.read_text(encoding="utf-8")
    replacements = [
        (
            "    public static void logoutBotAndScheduleRelogin(Player object) {\n"
            "        if (!((Player)object).isBot) {\n"
            "            return;\n"
            "        }\n"
            "        String string = ((Player)object).getUsername();\n"
            "        Player player = object;\n"
            "        player.packetSender.sendLogout();\n"
            "        ((Player)object).disconnect();\n"
            "        object = new BotReloginTask(30, string);\n"
            "        taskScheduler.schedule((TickTask)object);\n"
            "    }\n",
            "    public static void logoutBotAndScheduleRelogin(Player player) {\n"
            "        if (!player.isBot) {\n"
            "            return;\n"
            "        }\n"
            "        String username = player.getUsername();\n"
            "        player.packetSender.sendLogout();\n"
            "        player.disconnect();\n"
            "        TickTask reloginTask = new BotReloginTask(30, username);\n"
            "        taskScheduler.schedule(reloginTask);\n"
            "    }\n",
        ),
        (
            "        Object object4 = new LinkedList();\n"
            "        object4.addAll(taskScheduler.getTasks());\n"
            "        object4 = object4.iterator();\n"
            "        taskScheduler.getTasks().clear();\n"
            "        profilerTimer.start();\n"
            "        while (object4.hasNext()) {\n"
            "            object3 = (TickTask)object4.next();\n",
            "        LinkedList taskSnapshot = new LinkedList();\n"
            "        taskSnapshot.addAll(taskScheduler.getTasks());\n"
            "        java.util.Iterator taskIterator = taskSnapshot.iterator();\n"
            "        taskScheduler.getTasks().clear();\n"
            "        profilerTimer.start();\n"
            "        while (taskIterator.hasNext()) {\n"
            "            object3 = (TickTask)taskIterator.next();\n",
        ),
        (
            "    public static synchronized void unregisterPlayer(Player object) {\n"
            "        try {\n"
            "            CharacterFileManager.savePlayer((Player)object);\n"
            "            ((Player)object).setRegistered(false);\n"
            "            ((Player)object).setConnectionState(PlayerConnectionState.DISCONNECTED);\n"
            "            if (((Entity)object).getIndex() == -1) {\n"
            "                return;\n"
            "            }\n"
            "            object.ad();\n"
            "            World.players[((Entity)object).getIndex()] = null;\n"
            "            ((Entity)object).setIndex(-1);\n"
            "            return;\n"
            "        }\n"
            "        catch (Exception exception) {\n"
            "            object = exception;\n"
            "            exception.printStackTrace();\n"
            "            return;\n"
            "        }\n"
            "    }\n",
            "    public static synchronized void unregisterPlayer(Player player) {\n"
            "        try {\n"
            "            CharacterFileManager.savePlayer(player);\n"
            "            player.setRegistered(false);\n"
            "            player.setConnectionState(PlayerConnectionState.DISCONNECTED);\n"
            "            if (player.getIndex() == -1) {\n"
            "                return;\n"
            "            }\n"
            "            player.clearCombatEffectTasks();\n"
            "            World.players[player.getIndex()] = null;\n"
            "            player.setIndex(-1);\n"
            "            return;\n"
            "        }\n"
            "        catch (Exception exception) {\n"
            "            exception.printStackTrace();\n"
            "            return;\n"
            "        }\n"
            "    }\n",
        ),
        (
            "    public static synchronized void unregisterNpc(Npc npc) {\n"
            "        if (npc.getIndex() == -1) {\n"
            "            return;\n"
            "        }\n"
            "        npc.ad();\n"
            "        World.npcs[npc.getIndex()] = null;\n"
            "        npc.setIndex(-1);\n"
            "    }\n",
            "    public static synchronized void unregisterNpc(Npc npc) {\n"
            "        if (npc.getIndex() == -1) {\n"
            "            return;\n"
            "        }\n"
            "        npc.clearCombatEffectTasks();\n"
            "        World.npcs[npc.getIndex()] = null;\n"
            "        npc.setIndex(-1);\n"
            "    }\n",
        ),
        (
            "    public static Player findPlayerByUsername(String object) {\n"
            "        long l = TextUtil.encodeNameHash((String)object);\n"
            "        Player[] playerArray = players;\n"
            "        int n = players.length;\n"
            "        int n2 = 0;\n"
            "        while (n2 < n) {\n"
            "            object = playerArray[n2];\n"
            "            if (object != null && ((Player)object).getNameHash() == l) {\n"
            "                return object;\n"
            "            }\n"
            "            ++n2;\n"
            "        }\n"
            "        return null;\n"
            "    }\n",
            "    public static Player findPlayerByUsername(String username) {\n"
            "        long nameHash = TextUtil.encodeNameHash(username);\n"
            "        Player[] playerArray = players;\n"
            "        int length = players.length;\n"
            "        int index = 0;\n"
            "        while (index < length) {\n"
            "            Player player = playerArray[index];\n"
            "            if (player != null && player.getNameHash() == nameHash) {\n"
            "                return player;\n"
            "            }\n"
            "            ++index;\n"
            "        }\n"
            "        return null;\n"
            "    }\n",
        ),
    ]
    for old, new in replacements:
        text, count = replace_exact(text, old, new, world_path)
        repairs += count
    world_path.write_text(text, encoding="utf-8")
    return repairs


def repair_bot_task_definition_locals(source_root: Path) -> int:
    path = source_root / "com/rs2/bot/BotTaskDefinition.java"
    text = path.read_text(encoding="utf-8")
    replacements = [
        ("    static ArrayList tradeAdvertTaskPool = new ArrayList();\n", "    public static ArrayList tradeAdvertTaskPool = new ArrayList();\n"),
        ("    static ArrayList dropPartyTaskPool = new ArrayList();\n", "    public static ArrayList dropPartyTaskPool = new ArrayList();\n"),
        ("    Position startPosition;\n", "    public Position startPosition;\n"),
        ("    BotRoute taskRoute;\n", "    public BotRoute taskRoute;\n"),
        ("    int selectionWeight;\n", "    public int selectionWeight;\n"),
        ("    ArrayList assignedBotPlayers = new ArrayList();\n", "    public ArrayList assignedBotPlayers = new ArrayList();\n"),
        ("    static int dropPartyBotJoinIndex;\n", "    public static int dropPartyBotJoinIndex;\n"),
        (
            "    public static ArrayList getLootSellShopTasks() {\n"
            "        if (lootSellShopTasks.size() == 0) {\n"
            "            for (BotTaskDefinition botTaskDefinition : shopTasks) {\n"
            "                int n = botTaskDefinition.getShopId();\n"
            "                ShopDefinition shopDefinition = (ShopDefinition)ShopManager.getShopDefinitions().get(n);\n"
            "                if (!shopDefinition.isGeneralStore()) continue;\n"
            "                lootSellShopTasks.add(botTaskDefinition);\n"
            "            }\n"
            "        }\n"
            "        return lootSellShopTasks;\n"
            "    }\n",
            "    public static ArrayList getLootSellShopTasks() {\n"
            "        if (lootSellShopTasks.size() == 0) {\n"
            "            for (Object taskObject : shopTasks) {\n"
            "                BotTaskDefinition botTaskDefinition = (BotTaskDefinition)taskObject;\n"
            "                int n = botTaskDefinition.getShopId();\n"
            "                ShopDefinition shopDefinition = (ShopDefinition)ShopManager.getShopDefinitions().get(n);\n"
            "                if (!shopDefinition.isGeneralStore()) continue;\n"
            "                lootSellShopTasks.add(botTaskDefinition);\n"
            "            }\n"
            "        }\n"
            "        return lootSellShopTasks;\n"
            "    }\n",
        ),
        (
            "    public static void initializeTradeAdvertTaskPool() {\n"
            "        BotTradeAdvertManager.initializeTradeAdvertOfferPools();\n"
            "        Object object2 = tradeAdvertTaskDefinitions;\n"
            "        int n = tradeAdvertTaskDefinitions.length;\n"
            "        int n2 = 0;\n"
            "        while (n2 < n) {\n"
            "            BotTaskDefinition botTaskDefinition = object2[n2];\n"
            "            if (!ServerSettings.freeToPlayWorld || !botTaskDefinition.membersOnly) {\n"
            "                tradeAdvertTaskPool.add(botTaskDefinition);\n"
            "                totalTradeAdvertTaskWeight += botTaskDefinition.selectionWeight;\n"
            "            }\n"
            "            ++n2;\n"
            "        }\n"
            "        int n3 = ServerSettings.tradeBotCount;\n"
            "        if (n3 < 0) {\n"
            "            n3 = 0;\n"
            "        } else if (n3 > 100) {\n"
            "            n3 = 100;\n"
            "        }\n"
            "        if (n3 == 100) {\n"
            "            for (Object object2 : tradeAdvertTaskPool) {\n"
            "                ++object2.selectionWeight;\n"
            "            }\n"
            "        }\n"
            "    }\n",
            "    public static void initializeTradeAdvertTaskPool() {\n"
            "        BotTradeAdvertManager.initializeTradeAdvertOfferPools();\n"
            "        BotTaskDefinition[] taskDefinitions = tradeAdvertTaskDefinitions;\n"
            "        int n = tradeAdvertTaskDefinitions.length;\n"
            "        int n2 = 0;\n"
            "        while (n2 < n) {\n"
            "            BotTaskDefinition botTaskDefinition = taskDefinitions[n2];\n"
            "            if (!ServerSettings.freeToPlayWorld || !botTaskDefinition.membersOnly) {\n"
            "                tradeAdvertTaskPool.add(botTaskDefinition);\n"
            "                totalTradeAdvertTaskWeight += botTaskDefinition.selectionWeight;\n"
            "            }\n"
            "            ++n2;\n"
            "        }\n"
            "        int n3 = ServerSettings.tradeBotCount;\n"
            "        if (n3 < 0) {\n"
            "            n3 = 0;\n"
            "        } else if (n3 > 100) {\n"
            "            n3 = 100;\n"
            "        }\n"
            "        if (n3 == 100) {\n"
            "            for (Object taskObject : tradeAdvertTaskPool) {\n"
            "                ++((BotTaskDefinition)taskObject).selectionWeight;\n"
            "            }\n"
            "        }\n"
            "    }\n",
        ),
        (
            "    public static void initializeProgressiveTaskPool() {\n"
            "        Object object2 = progressiveTaskDefinitions;\n"
            "        int n = progressiveTaskDefinitions.length;\n"
            "        int n2 = 0;\n"
            "        while (n2 < n) {\n"
            "            BotTaskDefinition botTaskDefinition = object2[n2];\n"
            "            if (!(ServerSettings.freeToPlayWorld && botTaskDefinition.membersOnly || botTaskDefinition.minimumServerRevision != -1 && ServerSettings.cacheVersion < botTaskDefinition.minimumServerRevision)) {\n"
            "                progressiveTaskPool.add(botTaskDefinition);\n"
            "                totalProgressiveTaskWeight += botTaskDefinition.selectionWeight;\n"
            "            }\n"
            "            ++n2;\n"
            "        }\n"
            "        int n3 = ServerSettings.skillingBotCount;\n"
            "        if (n3 < 0) {\n"
            "            n3 = 0;\n"
            "        } else if (n3 > 100) {\n"
            "            n3 = 100;\n"
            "        }\n"
            "        if (n3 == 100) {\n"
            "            for (Object object2 : progressiveTaskPool) {\n"
            "                ++object2.selectionWeight;\n"
            "            }\n"
            "        }\n"
            "    }\n",
            "    public static void initializeProgressiveTaskPool() {\n"
            "        BotTaskDefinition[] taskDefinitions = progressiveTaskDefinitions;\n"
            "        int n = progressiveTaskDefinitions.length;\n"
            "        int n2 = 0;\n"
            "        while (n2 < n) {\n"
            "            BotTaskDefinition botTaskDefinition = taskDefinitions[n2];\n"
            "            if (!(ServerSettings.freeToPlayWorld && botTaskDefinition.membersOnly || botTaskDefinition.minimumServerRevision != -1 && ServerSettings.cacheVersion < botTaskDefinition.minimumServerRevision)) {\n"
            "                progressiveTaskPool.add(botTaskDefinition);\n"
            "                totalProgressiveTaskWeight += botTaskDefinition.selectionWeight;\n"
            "            }\n"
            "            ++n2;\n"
            "        }\n"
            "        int n3 = ServerSettings.skillingBotCount;\n"
            "        if (n3 < 0) {\n"
            "            n3 = 0;\n"
            "        } else if (n3 > 100) {\n"
            "            n3 = 100;\n"
            "        }\n"
            "        if (n3 == 100) {\n"
            "            for (Object taskObject : progressiveTaskPool) {\n"
            "                ++((BotTaskDefinition)taskObject).selectionWeight;\n"
            "            }\n"
            "        }\n"
            "    }\n",
        ),
        (
            "    public ArrayList getRequiredItems(Player object) {\n"
            "        object = new ArrayList();\n"
            "        return object;\n"
            "    }\n",
            "    public ArrayList getRequiredItems(Player player) {\n"
            "        return new ArrayList();\n"
            "    }\n",
        ),
        (
            "    public final ArrayList getMissingRequiredItems(Player player) {\n"
            "        ArrayList<Object> arrayList = new ArrayList<Object>();\n"
            "        Object object = this.getRequiredItems(player);\n"
            "        player.botTaskRequiredItems = new ItemStack[((ArrayList)object).size()];\n"
            "        int n = 0;\n"
            "        Iterator iterator = ((ArrayList)object).iterator();\n"
            "        while (iterator.hasNext()) {\n"
            "            player.botTaskRequiredItems[n] = object = (ItemStack)iterator.next();\n"
            "            if (!player.ownsItemAmount(((ItemStack)object).getId(), ((ItemStack)object).getAmount())) {\n"
            "                arrayList.add(object);\n"
            "            }\n"
            "            ++n;\n"
            "        }\n"
            "        return arrayList;\n"
            "    }\n",
            "    public final ArrayList getMissingRequiredItems(Player player) {\n"
            "        ArrayList<Object> arrayList = new ArrayList<Object>();\n"
            "        ArrayList requiredItems = this.getRequiredItems(player);\n"
            "        player.botTaskRequiredItems = new ItemStack[requiredItems.size()];\n"
            "        int n = 0;\n"
            "        Iterator iterator = requiredItems.iterator();\n"
            "        while (iterator.hasNext()) {\n"
            "            ItemStack itemStack = (ItemStack)iterator.next();\n"
            "            player.botTaskRequiredItems[n] = itemStack;\n"
            "            if (!player.ownsItemAmount(itemStack.getId(), itemStack.getAmount())) {\n"
            "                arrayList.add(itemStack);\n"
            "            }\n"
            "            ++n;\n"
            "        }\n"
            "        return arrayList;\n"
            "    }\n",
        ),
    ]
    repairs = 0
    for old, new in replacements:
        text, count = replace_exact(text, old, new, path)
        repairs += count
    path.write_text(text, encoding="utf-8")
    return repairs


def repair_gameplay_helper_bot_task_locals(source_root: Path) -> int:
    path = source_root / "com/rs2/model/GameplayHelper.java"
    text = path.read_text(encoding="utf-8")
    old = (
        "    public static void startNextBotTask(Player player) {\n"
        "        Object object;\n"
        "        Object object2;\n"
        "        Object object3;\n"
        "        block5: {\n"
        "            Object object4;\n"
        "            object3 = player;\n"
        "            double d = 0.0;\n"
        "            object2 = ((Player)object3).currentBotTask;\n"
        "            object = new ArrayList<BotTaskDefinition>();\n"
        "            for (BotTaskDefinition botTaskDefinition : ((Player)object3).botMode == 2 ? BotTaskDefinition.tradeAdvertTaskPool : BotTaskDefinition.progressiveTaskPool) {\n"
        "                if (object2 != null && botTaskDefinition == object2) continue;\n"
        "                ((ArrayList)object).add(botTaskDefinition);\n"
        "                d += (double)botTaskDefinition.selectionWeight;\n"
        "            }\n"
        "            double d2 = Math.random() * d;\n"
        "            double d3 = 0.0;\n"
        "            Iterator iterator = ((ArrayList)object).iterator();\n"
        "            while (iterator.hasNext()) {\n"
        "                double d4;\n"
        "                object3 = (BotTaskDefinition)iterator.next();\n"
        "                d3 += (double)((BotTaskDefinition)object3).selectionWeight;\n"
        "                if (!(d4 >= d2)) continue;\n"
        "                object4 = object3;\n"
        "                break block5;\n"
        "            }\n"
        "            object4 = object3 = ((ArrayList)object).size() > 0 ? (BotTaskDefinition)((ArrayList)object).get(0) : object2;\n"
        "        }\n"
        "        if (player.currentBotTask != null) {\n"
        "            player.currentBotTask.assignedBotPlayers.remove(player);\n"
        "        }\n"
        "        player.botTaskReturnToBankRequested = false;\n"
        "        player.botEnabled = true;\n"
        "        ((BotTaskDefinition)object3).assignedBotPlayers.add(player);\n"
        "        object = object3;\n"
        "        object2 = player;\n"
        "        player.currentBotTask = object;\n"
        "        if (!BotTaskDefinition.shopTasks.contains(object3)) {\n"
        "            player.botTaskRequiredItems = null;\n"
        "        }\n"
        "        ((BotTaskDefinition)object3).startTask(player);\n"
        "        player.botTaskStartTimeMillis = System.currentTimeMillis();\n"
        "        int n = player.botMode == 2 ? 16 : 106;\n"
        "        player.botTaskDurationMinutes = n = 15 + GameUtil.randomInt(n);\n"
        "        if (player.currentBotTask.usesEscapeMonitor) {\n"
        "            player.currentBotTask.startEscapeMonitor(player);\n"
        "        }\n"
        "    }\n"
    )
    new = (
        "    public static void startNextBotTask(Player player) {\n"
        "        BotTaskDefinition currentTask = player.currentBotTask;\n"
        "        double totalWeight = 0.0;\n"
        "        ArrayList<BotTaskDefinition> availableTasks = new ArrayList<BotTaskDefinition>();\n"
        "        ArrayList taskPool = player.botMode == 2 ? BotTaskDefinition.tradeAdvertTaskPool : BotTaskDefinition.progressiveTaskPool;\n"
        "        Iterator taskIterator = taskPool.iterator();\n"
        "        while (taskIterator.hasNext()) {\n"
        "            BotTaskDefinition botTaskDefinition = (BotTaskDefinition)taskIterator.next();\n"
        "            if (currentTask != null && botTaskDefinition == currentTask) continue;\n"
        "            availableTasks.add(botTaskDefinition);\n"
        "            totalWeight += (double)botTaskDefinition.selectionWeight;\n"
        "        }\n"
        "        double roll = Math.random() * totalWeight;\n"
        "        double cumulativeWeight = 0.0;\n"
        "        BotTaskDefinition selectedTask = null;\n"
        "        Iterator availableIterator = availableTasks.iterator();\n"
        "        while (availableIterator.hasNext()) {\n"
        "            BotTaskDefinition botTaskDefinition = (BotTaskDefinition)availableIterator.next();\n"
        "            cumulativeWeight += (double)botTaskDefinition.selectionWeight;\n"
        "            if (cumulativeWeight < roll) continue;\n"
        "            selectedTask = botTaskDefinition;\n"
        "            break;\n"
        "        }\n"
        "        if (selectedTask == null) {\n"
        "            selectedTask = availableTasks.size() > 0 ? (BotTaskDefinition)availableTasks.get(0) : currentTask;\n"
        "        }\n"
        "        if (player.currentBotTask != null) {\n"
        "            player.currentBotTask.assignedBotPlayers.remove(player);\n"
        "        }\n"
        "        player.botTaskReturnToBankRequested = false;\n"
        "        player.botEnabled = true;\n"
        "        selectedTask.assignedBotPlayers.add(player);\n"
        "        player.currentBotTask = selectedTask;\n"
        "        if (!BotTaskDefinition.shopTasks.contains(selectedTask)) {\n"
        "            player.botTaskRequiredItems = null;\n"
        "        }\n"
        "        selectedTask.startTask(player);\n"
        "        player.botTaskStartTimeMillis = System.currentTimeMillis();\n"
        "        int durationRange = player.botMode == 2 ? 16 : 106;\n"
        "        player.botTaskDurationMinutes = 15 + GameUtil.randomInt(durationRange);\n"
        "        if (player.currentBotTask.usesEscapeMonitor) {\n"
        "            player.currentBotTask.startEscapeMonitor(player);\n"
        "        }\n"
        "    }\n"
    )
    text, count = replace_exact(text, old, new, path)
    path.write_text(text, encoding="utf-8")
    return count


def repair_player_update_task_locals(source_root: Path) -> int:
    path = source_root / "com/rs2/model/player/PlayerUpdateTask.java"
    text = path.read_text(encoding="utf-8")
    replacements = [
        (
            "        PacketWriter packetWriter3 = packetWriter;\n"
            "        Object object2 = player;\n"
            "        int n4 = ((Entity)object2).getUpdateState().isUpdateRequired();\n"
            "        if (((Player)object2).isTeleporting()) {\n"
            "            int n5;\n"
            "            packetWriter3.writeBoolean(true);\n"
            "            ((Entity)object2).getPosition();\n"
            "            n = Position.updateLocalX((Player)object2);\n"
            "            ((Entity)object2).getPosition();\n"
            "            int n6 = n5 = Position.updateLocalY((Player)object2);\n"
            "            n5 = n4;\n"
            "            boolean bl = ((Player)object2).isTeleportPlacementUpdateRequired();\n"
            "            int n7 = ((Entity)object2).getPosition().getPlane();\n"
            "            int n8 = n6;\n"
            "            int n9 = n;\n"
            "            object = packetWriter3;\n"
            "            ((PacketWriter)object).writeBits(2, 3);\n"
            "            ((PacketWriter)object).writeBits(2, n7);\n"
            "            ((PacketWriter)object).writeBoolean(bl);\n"
            "            ((PacketWriter)object).writeBoolean(n5 != 0);\n"
            "            ((PacketWriter)object).writeBits(7, n8);\n"
            "            ((PacketWriter)object).writeBits(7, n9);\n"
            "        } else {\n"
            "            n = ((Entity)object2).getWalkDirection();\n"
            "            int n10 = ((Entity)object2).getRunDirection();\n"
            "            if (n != -1) {\n"
            "                packetWriter3.writeBoolean(true);\n"
            "                if (n10 != -1) {\n"
            "                    PlayerUpdateTask.a(packetWriter3, n, n10, n4 != 0);\n"
            "                } else {\n"
            "                    PlayerUpdateTask.a(packetWriter3, n, n4 != 0);\n"
            "                }\n"
            "            } else if (n4 != 0) {\n"
            "                packetWriter3.writeBoolean(true);\n"
            "                PacketWriter packetWriter4 = packetWriter3;\n"
            "                packetWriter4.writeBits(2, 0);\n"
            "            } else {\n"
            "                packetWriter3.writeBoolean(false);\n"
            "            }\n"
            "        }\n",
            "        PacketWriter packetWriter3 = packetWriter;\n"
            "        Object object2 = player;\n"
            "        int n4 = 0;\n"
            "        boolean updateRequired = ((Entity)object2).getUpdateState().isUpdateRequired();\n"
            "        if (((Player)object2).isTeleporting()) {\n"
            "            packetWriter3.writeBoolean(true);\n"
            "            ((Entity)object2).getPosition();\n"
            "            n = Position.updateLocalX((Player)object2);\n"
            "            ((Entity)object2).getPosition();\n"
            "            int localY = Position.updateLocalY((Player)object2);\n"
            "            boolean teleportPlacementUpdateRequired = ((Player)object2).isTeleportPlacementUpdateRequired();\n"
            "            int plane = ((Entity)object2).getPosition().getPlane();\n"
            "            PacketWriter movementWriter = packetWriter3;\n"
            "            movementWriter.writeBits(2, 3);\n"
            "            movementWriter.writeBits(2, plane);\n"
            "            movementWriter.writeBoolean(teleportPlacementUpdateRequired);\n"
            "            movementWriter.writeBoolean(updateRequired);\n"
            "            movementWriter.writeBits(7, localY);\n"
            "            movementWriter.writeBits(7, n);\n"
            "        } else {\n"
            "            n = ((Entity)object2).getWalkDirection();\n"
            "            int n10 = ((Entity)object2).getRunDirection();\n"
            "            if (n != -1) {\n"
            "                packetWriter3.writeBoolean(true);\n"
            "                if (n10 != -1) {\n"
            "                    PlayerUpdateTask.a(packetWriter3, n, n10, updateRequired);\n"
            "                } else {\n"
            "                    PlayerUpdateTask.a(packetWriter3, n, updateRequired);\n"
            "                }\n"
            "            } else if (updateRequired) {\n"
            "                packetWriter3.writeBoolean(true);\n"
            "                PacketWriter packetWriter4 = packetWriter3;\n"
            "                packetWriter4.writeBits(2, 0);\n"
            "            } else {\n"
            "                packetWriter3.writeBoolean(false);\n"
            "            }\n"
            "        }\n",
        ),
        (
            "                        Object object5 = packetWriter;\n"
            "                        object2 = player3;\n"
            "                        n4 = ((Entity)object2).getUpdateState().isUpdateRequired();\n"
            "                        int n11 = ((Entity)object2).getWalkDirection();\n"
            "                        int n12 = ((Entity)object2).getRunDirection();\n"
            "                        if (n11 != -1) {\n"
            "                            ((PacketWriter)object5).writeBoolean(true);\n"
            "                            if (n12 != -1) {\n"
            "                                PlayerUpdateTask.a((PacketWriter)object5, n11, n12, n4 != 0);\n"
            "                            } else {\n"
            "                                PlayerUpdateTask.a((PacketWriter)object5, n11, n4 != 0);\n"
            "                            }\n"
            "                        } else if (n4 != 0) {\n"
            "                            ((PacketWriter)object5).writeBoolean(true);\n"
            "                            PacketWriter packetWriter5 = object5;\n"
            "                            packetWriter5.writeBits(2, 0);\n"
            "                        } else {\n"
            "                            ((PacketWriter)object5).writeBoolean(false);\n"
            "                        }\n",
            "                        Object object5 = packetWriter;\n"
            "                        object2 = player3;\n"
            "                        boolean localUpdateRequired = ((Entity)object2).getUpdateState().isUpdateRequired();\n"
            "                        int n11 = ((Entity)object2).getWalkDirection();\n"
            "                        int n12 = ((Entity)object2).getRunDirection();\n"
            "                        if (n11 != -1) {\n"
            "                            ((PacketWriter)object5).writeBoolean(true);\n"
            "                            if (n12 != -1) {\n"
            "                                PlayerUpdateTask.a((PacketWriter)object5, n11, n12, localUpdateRequired);\n"
            "                            } else {\n"
            "                                PlayerUpdateTask.a((PacketWriter)object5, n11, localUpdateRequired);\n"
            "                            }\n"
            "                        } else if (localUpdateRequired) {\n"
            "                            ((PacketWriter)object5).writeBoolean(true);\n"
            "                            PacketWriter packetWriter5 = (PacketWriter)object5;\n"
            "                            packetWriter5.writeBits(2, 0);\n"
            "                        } else {\n"
            "                            ((PacketWriter)object5).writeBoolean(false);\n"
            "                        }\n",
        ),
        (
            "        if (player.getUpdateState().isAppearanceUpdateRequired() && !bl2) {\n"
            "            object = packetWriter;\n"
            "            Player player2 = player;\n"
            "            ((PacketWriter)object).writeShort(((player2.getPublicChatColor() & 0xFF) << 8) + (player2.getPublicChatEffects() & 0xFF), ByteOrder.LITTLE);\n"
            "            ((PacketWriter)object).writeByte(player2.getPlayerRights());\n"
            "            ((PacketWriter)object).writeByte(player2.getPublicChatPayload().length, ByteTransform.NEGATE);\n"
            "            byte[] byArray = player2.getPublicChatPayload();\n"
            "            Object object2 = object;\n"
            "            int n3 = byArray.length - 1;\n"
            "            while (n3 >= 0) {\n"
            "                ((PacketWriter)object2).writeByte(byArray[n3]);\n"
            "                --n3;\n"
            "            }\n"
            "        }\n",
            "        if (player.getUpdateState().isAppearanceUpdateRequired() && !bl2) {\n"
            "            PacketWriter chatWriter = packetWriter;\n"
            "            Player player2 = player;\n"
            "            chatWriter.writeShort(((player2.getPublicChatColor() & 0xFF) << 8) + (player2.getPublicChatEffects() & 0xFF), ByteOrder.LITTLE);\n"
            "            chatWriter.writeByte(player2.getPlayerRights());\n"
            "            chatWriter.writeByte(player2.getPublicChatPayload().length, ByteTransform.NEGATE);\n"
            "            byte[] byArray = player2.getPublicChatPayload();\n"
            "            int n3 = byArray.length - 1;\n"
            "            while (n3 >= 0) {\n"
            "                chatWriter.writeByte(byArray[n3]);\n"
            "                --n3;\n"
            "            }\n"
            "        }\n",
        ),
        ("        if (player.isAppearanceUpdateRequired() || bl) {\n            object = packetWriter;\n", "        if (player.isAppearanceUpdateRequired() || bl) {\n            PacketWriter appearanceWriter = packetWriter;\n"),
        ("            ((PacketWriter)object).writeByte(packetWriter2.getBuffer().position(), ByteTransform.NEGATE);\n", "            appearanceWriter.writeByte(packetWriter2.getBuffer().position(), ByteTransform.NEGATE);\n"),
        ("            ((PacketWriter)object).writeBuffer(packetWriter2.getBuffer());\n", "            appearanceWriter.writeBuffer(packetWriter2.getBuffer());\n"),
    ]
    repairs = 0
    for old, new in replacements:
        text, count = replace_exact(text, old, new, path)
        repairs += count
    path.write_text(text, encoding="utf-8")
    return repairs


def repair_grand_exchange_manager_locals(source_root: Path) -> int:
    path = source_root / "com/rs2/model/player/GrandExchangeManager.java"
    text = path.read_text(encoding="utf-8")
    replacements = [
        (
            "        ItemStack itemStack;\n"
            "        ItemStack[] itemStackArray;\n"
            "        String string;\n",
            "        ItemStack secondaryCollectItem;\n"
            "        ItemStack primaryCollectItem;\n"
            "        String string;\n",
        ),
        (
            "        if (player.grandExchangeSellOfferFlags[player.selectedGrandExchangeSlot]) {\n"
            "            itemStackArray = new ItemStack(995, player.grandExchangePrimaryCollectAmounts[player.selectedGrandExchangeSlot]);\n"
            "            itemStack = new ItemStack(player.grandExchangeItemIds[player.selectedGrandExchangeSlot], player.grandExchangeSecondaryCollectAmounts[player.selectedGrandExchangeSlot]);\n"
            "        } else {\n"
            "            itemStackArray = new ItemStack(player.grandExchangeItemIds[player.selectedGrandExchangeSlot], player.grandExchangePrimaryCollectAmounts[player.selectedGrandExchangeSlot]);\n"
            "            itemStack = new ItemStack(995, player.grandExchangeSecondaryCollectAmounts[player.selectedGrandExchangeSlot]);\n"
            "        }\n"
            "        if (itemStackArray.getAmount() <= 0) {\n"
            "            itemStackArray = null;\n"
            "        }\n"
            "        if (itemStack.getAmount() <= 0) {\n"
            "            itemStack = null;\n"
            "        }\n"
            "        itemStackArray = new ItemStack[]{itemStackArray, itemStack};\n"
            "        player2 = player;\n"
            "        player2.packetSender.sendItemContainer(19006, itemStackArray);\n",
            "        if (player.grandExchangeSellOfferFlags[player.selectedGrandExchangeSlot]) {\n"
            "            primaryCollectItem = new ItemStack(995, player.grandExchangePrimaryCollectAmounts[player.selectedGrandExchangeSlot]);\n"
            "            secondaryCollectItem = new ItemStack(player.grandExchangeItemIds[player.selectedGrandExchangeSlot], player.grandExchangeSecondaryCollectAmounts[player.selectedGrandExchangeSlot]);\n"
            "        } else {\n"
            "            primaryCollectItem = new ItemStack(player.grandExchangeItemIds[player.selectedGrandExchangeSlot], player.grandExchangePrimaryCollectAmounts[player.selectedGrandExchangeSlot]);\n"
            "            secondaryCollectItem = new ItemStack(995, player.grandExchangeSecondaryCollectAmounts[player.selectedGrandExchangeSlot]);\n"
            "        }\n"
            "        if (primaryCollectItem.getAmount() <= 0) {\n"
            "            primaryCollectItem = null;\n"
            "        }\n"
            "        if (secondaryCollectItem.getAmount() <= 0) {\n"
            "            secondaryCollectItem = null;\n"
            "        }\n"
            "        ItemStack[] collectItems = new ItemStack[]{primaryCollectItem, secondaryCollectItem};\n"
            "        player2 = player;\n"
            "        player2.packetSender.sendItemContainer(19006, collectItems);\n",
        ),
        (
            "                ItemStack[] itemStackArray = new ItemStack[]{object2, object};\n",
            "                ItemStack[] itemStackArray = new ItemStack[]{(ItemStack)object2, (ItemStack)object};\n",
        ),
        (
            "                player.grandExchangeSellOfferFlags[player.selectedGrandExchangeSlot] = n3;\n",
            "                player.grandExchangeSellOfferFlags[player.selectedGrandExchangeSlot] = n3 != 0;\n",
        ),
        (
            "        int n2 = itemDefinition.isNote();\n"
            "        n2 = n2 != 0 ? itemDefinition.getUnnotedId() : itemDefinition.getId();\n",
            "        int n2 = itemDefinition.isNote() ? itemDefinition.getUnnotedId() : itemDefinition.getId();\n",
        ),
    ]
    repairs = 0
    for old, new in replacements:
        text, count = replace_exact(text, old, new, path)
        repairs += count
    path.write_text(text, encoding="utf-8")
    return repairs


def repair_telekinetic_grab_spell_action_locals(source_root: Path) -> int:
    path = source_root / "com/rs2/model/skill/magic/TelekineticGrabSpellAction.java"
    text = path.read_text(encoding="utf-8")
    replacements = [
        (
            "    public final void applyImpact(HitDefinition object) {\n",
            "    public final void applyImpact(HitDefinition hitDefinition) {\n",
        ),
        (
            "                    ((HitDefinition)object).setGraphic(null);\n",
            "                    hitDefinition.setGraphic(null);\n",
        ),
        (
            "                    object = this.caster;\n"
            "                    ((Player)object).packetSender.sendGameMessage(\"That item does not seem to exist anymore.\");\n"
            "                    object = this.caster;\n"
            "                    ((Player)object).packetSender.sendGroundItemRemove(this.groundItem);\n",
            "                    this.caster.packetSender.sendGameMessage(\"That item does not seem to exist anymore.\");\n"
            "                    this.caster.packetSender.sendGroundItemRemove(this.groundItem);\n",
        ),
        (
            "                object = this.caster.getTelekineticTheatreController().getPlayerMazeSide(this.caster.getTelekineticTheatreController().mazeIndex);\n",
            "                String mazeSide = this.caster.getTelekineticTheatreController().getPlayerMazeSide(this.caster.getTelekineticTheatreController().mazeIndex);\n",
        ),
        ("                if (object == \"right\") {\n", "                if (mazeSide == \"right\") {\n"),
        ("                } else if (object == \"left\") {\n", "                } else if (mazeSide == \"left\") {\n"),
        ("                } else if (object == \"bottom\") {\n", "                } else if (mazeSide == \"bottom\") {\n"),
        ("                } else if (object == \"upper\") {\n", "                } else if (mazeSide == \"upper\") {\n"),
    ]
    repairs = 0
    for old, new in replacements:
        text, count = replace_exact(text, old, new, path)
        repairs += count
    path.write_text(text, encoding="utf-8")
    return repairs


def repair_login_protocol_locals(source_root: Path) -> int:
    path = source_root / "com/rs2/net/LoginProtocol.java"
    text = path.read_text(encoding="utf-8")
    replacements = [
        (
            "    public static void processLoginBuffer(Player player, ByteBuffer object) {\n",
            "    public static boolean processLoginBuffer(Player player, ByteBuffer object) {\n",
        ),
        (
            "                    ((ByteBuffer)object).compact();\n"
            "                    return;\n",
            "                    ((ByteBuffer)object).compact();\n"
            "                    return true;\n",
        ),
        (
            "                int n = ((ByteBuffer)object).get();\n"
            "                if (n != 16 && n != 18) {\n",
            "                int loginPayloadStart = ((Buffer)object).position();\n"
            "                int n = ((ByteBuffer)object).get();\n"
            "                if (n != 16 && n != 18) {\n",
        ),
        (
            "                if (((Buffer)object).remaining() < n) {\n"
            "                    ((ByteBuffer)object).compact();\n"
            "                    return true;\n"
            "                }\n",
            "                if (((Buffer)object).remaining() < n) {\n"
            "                    ((Buffer)object).position(loginPayloadStart);\n"
            "                    ((ByteBuffer)object).compact();\n"
            "                    return true;\n"
            "                }\n",
        ),
        (
            "                int n2 = n - 40;\n",
            "                int n2 = n - 44;\n",
        ),
        (
            "                    if (n3 != --n2) {\n"
            "                        System.err.println(\"Encrypted packet size zero or negative : \" + n2);\n"
            "                        player.disconnect();\n"
            "                        return false;\n"
            "                    }\n"
            "                    byte[] byArray = new byte[n2];\n",
            "                    if (n3 <= 0 || n3 > ((Buffer)object).remaining()) {\n"
            "                        System.err.println(\"Encrypted packet size zero or negative : \" + n3);\n"
            "                        player.disconnect();\n"
            "                        return false;\n"
            "                    }\n"
            "                    byte[] byArray = new byte[n3];\n",
        ),
        (
            "                object = PacketBuffer.allocateWriter(17);\n"
            "                ((PacketWriter)object).writeLong(0L);\n"
            "                ((PacketWriter)object).writeByte(0);\n"
            "                ((PacketWriter)object).writeLong(new SecureRandom().nextLong());\n"
            "                player.writePacketBuffer(((PacketWriter)object).getBuffer());\n",
            "                PacketWriter handshakeResponse = PacketBuffer.allocateWriter(17);\n"
            "                handshakeResponse.writeLong(0L);\n"
            "                handshakeResponse.writeByte(0);\n"
            "                handshakeResponse.writeLong(new SecureRandom().nextLong());\n"
            "                player.writePacketBuffer(handshakeResponse.getBuffer());\n",
        ),
        (
            "                    object = new int[]{(int)(l >> 32), (int)l, (int)(l2 >> 32), (int)l2};\n"
            "                    player.setInboundCipher(new IsaacCipher((int[])object));\n"
            "                    int n5 = 0;\n"
            "                    while (n5 < 4) {\n"
            "                        Object object2 = object;\n"
            "                        int n6 = n5++;\n"
            "                        object2[n6] = object2[n6] + 50;\n"
            "                    }\n"
            "                    player.setOutboundCipher(new IsaacCipher((int[])object));\n"
            "                    byteBuffer.getInt();\n"
            "                    String string = TextUtil.readLine(byteBuffer).trim();\n"
            "                    object = TextUtil.readLine(byteBuffer).trim();\n"
            "                    player.setSubmittedPassword((String)object);\n",
            "                    int[] isaacSeed = new int[]{(int)(l >> 32), (int)l, (int)(l2 >> 32), (int)l2};\n"
            "                    player.setInboundCipher(new IsaacCipher(isaacSeed));\n"
            "                    int n5 = 0;\n"
            "                    while (n5 < 4) {\n"
            "                        int n6 = n5++;\n"
            "                        isaacSeed[n6] = isaacSeed[n6] + 50;\n"
            "                    }\n"
            "                    player.setOutboundCipher(new IsaacCipher(isaacSeed));\n"
            "                    byteBuffer.getInt();\n"
            "                    String string = TextUtil.readLine(byteBuffer).trim();\n"
            "                    String string2 = TextUtil.readLine(byteBuffer).trim();\n"
            "                    player.setSubmittedPassword(string2);\n",
        ),
        (
            "                    player.getSocketChannel().register(Server.getInstance().getSelector(), 1, player);\n"
            "                    return;\n",
            "                    try {\n"
            "                        player.getSocketChannel().register(Server.getInstance().getSelector(), 1, player);\n"
            "                    }\n"
            "                    catch (java.nio.channels.ClosedChannelException exception) {\n"
            "                        player.disconnect();\n"
            "                    }\n"
            "                    return;\n",
        ),
    ]
    repairs = 0
    for old, new in replacements:
        text, count = replace_exact(text, old, new, path)
        repairs += count
    false_return_count = 0
    for return_line in (
        "                return;\n",
        "                    return;\n",
        "                        return;\n",
    ):
        count = text.count(return_line)
        text = text.replace(return_line, return_line.replace("return;", "return false;"))
        false_return_count += count
    if false_return_count == 0:
        raise RuntimeError(f"{path}: expected login terminal returns not found")
    repairs += false_return_count
    text, count = replace_exact(
        text,
        "            }\n"
        "        }\n"
        "    }\n"
        "}\n",
        "            }\n"
        "        }\n"
        "        return false;\n"
        "    }\n"
        "}\n",
        path,
    )
    repairs += count
    path.write_text(text, encoding="utf-8")
    return repairs


def repair_plugin_manager_locals(source_root: Path) -> int:
    path = source_root / "com/rs2/util/plugin/PluginManager.java"
    text = path.read_text(encoding="utf-8")

    load_start = "    public static void loadPlugins() {\n"
    load_end = (
        "\n    /*\n"
        "     * WARNING - Removed try catching itself - possible behaviour change.\n"
        "     */\n"
        "    public static void attachPlayerPlugins(Player player) {\n"
    )
    load_method = """    public static void loadPlugins() {
        try {
            String packageName = "com.rs2.util.plugin.impl";
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            assert (classLoader != null);
            String packagePath = packageName.replace('.', '/');
            java.util.Enumeration resources = classLoader.getResources(packagePath);
            ArrayList pluginDirectories = new ArrayList();
            while (resources.hasMoreElements()) {
                URL url = (URL)resources.nextElement();
                pluginDirectories.add(new File(url.getFile()));
            }
            ArrayList pluginClassList = new ArrayList();
            Iterator directoryIterator = pluginDirectories.iterator();
            while (directoryIterator.hasNext()) {
                File pluginDirectory = (File)directoryIterator.next();
                pluginClassList.addAll(PluginManager.findPluginClasses(pluginDirectory, packageName));
            }
            Class[] pluginClasses = (Class[])pluginClassList.toArray(new Class[pluginClassList.size()]);
            int n = pluginClasses.length;
            int n2 = 0;
            while (n2 < n) {
                Class pluginClass = pluginClasses[n2];
                Plugin plugin = (Plugin)pluginClass.newInstance();
                if (plugin.getPluginType() == PluginType.GLOBAL) {
                    GlobalPlugin globalPlugin = (GlobalPlugin)plugin;
                    List list = playerPluginClasses;
                    synchronized (list) {
                        System.out.println("Loaded global plugin: " + globalPlugin.getName() + " v" + globalPlugin.getVersion() + " by " + globalPlugin.getAuthor());
                        globalPlugins.add(globalPlugin);
                    }
                }
                if (plugin.getPluginType() == PluginType.PLAYER_LOCAL) {
                    List list = playerPluginClasses;
                    synchronized (list) {
                        System.out.println("Loaded local plugin: " + pluginClass.getSimpleName());
                        playerPluginClasses.add(pluginClass);
                    }
                }
                ++n2;
            }
            return;
        }
        catch (InstantiationException instantiationException) {
            InstantiationException instantiationException2 = instantiationException;
            instantiationException.printStackTrace();
            return;
        }
        catch (IllegalAccessException illegalAccessException) {
            IllegalAccessException illegalAccessException2 = illegalAccessException;
            illegalAccessException.printStackTrace();
            return;
        }
        catch (ClassNotFoundException classNotFoundException) {
            ClassNotFoundException classNotFoundException2 = classNotFoundException;
            classNotFoundException.printStackTrace();
            return;
        }
        catch (IOException iOException) {
            IOException iOException2 = iOException;
            iOException.printStackTrace();
            return;
        }
    }
"""
    try:
        start = text.index(load_start)
        end = text.index(load_end, start)
    except ValueError as exc:
        raise SystemExit(f"{path}: expected loadPlugins region not found") from exc
    text = text[:start] + load_method + text[end:]

    find_start = "    private static List findPluginClasses(File object, String string) {\n"
    find_end = "\n}\n"
    find_method = """    private static List findPluginClasses(File file, String string) throws ClassNotFoundException {
        ArrayList arrayList = new ArrayList();
        if (!file.exists()) {
            return arrayList;
        }
        File[] fileArray = file.listFiles();
        if (fileArray == null) {
            return arrayList;
        }
        int n = fileArray.length;
        int n2 = 0;
        while (n2 < n) {
            File child = fileArray[n2];
            if (child.isDirectory()) {
                assert (!child.getName().contains("."));
                arrayList.addAll(PluginManager.findPluginClasses(child, String.valueOf(string) + "." + child.getName()));
            } else if (child.getName().endsWith(".class")) {
                arrayList.add(Class.forName(String.valueOf(string) + '.' + child.getName().substring(0, child.getName().length() - 6)));
            }
            ++n2;
        }
        return arrayList;
    }
"""
    try:
        start = text.index(find_start)
        end = text.index(find_end, start)
    except ValueError as exc:
        raise SystemExit(f"{path}: expected findPluginClasses region not found") from exc
    text = text[:start] + find_method + text[end:]
    path.write_text(text, encoding="utf-8")
    return 2


def repair_item_combination_handler_locals(source_root: Path) -> int:
    path = source_root / "com/rs2/model/skill/ItemCombinationHandler.java"
    text = path.read_text(encoding="utf-8")

    handle_start = "    public final boolean handleItemCombination(ItemStack object, ItemStack object2) {\n"
    handle_end = "\n    public static boolean isGatheringToolItemId(int n) {\n"
    handle_method = """    public final boolean handleItemCombination(ItemStack firstItem, ItemStack secondItem) {
        ItemCombinationRecipe recipe = ItemCombinationRecipe.forItemIds(firstItem.getId(), secondItem.getId());
        if (recipe == null) {
            return false;
        }
        int[] skillRequirement = ItemCombinationRecipe.getSkillRequirement(recipe);
        if (skillRequirement != null && this.player.getSkillManager().getCurrentLevels()[skillRequirement[0]] < skillRequirement[1]) {
            this.player.packetSender.sendGameMessage("Your " + SkillManager.SKILL_NAMES[skillRequirement[0]].toLowerCase() + " level is not high enough to do this.");
            return true;
        }
        ItemStack[] requiredItems = ItemCombinationRecipe.getRequiredItems(recipe);
        if (requiredItems != null) {
            int n = 0;
            while (n < requiredItems.length) {
                ItemStack itemStack = requiredItems[n];
                if (this.player.getInventoryManager().getItemAmount(itemStack.getId()) < itemStack.getAmount()) {
                    return true;
                }
                ++n;
            }
            n = 0;
            while (n < requiredItems.length) {
                this.player.getInventoryManager().removeItem(requiredItems[n]);
                ++n;
            }
        }
        if (ItemCombinationRecipe.getMessage(recipe) != null) {
            this.player.packetSender.sendGameMessage(ItemCombinationRecipe.getMessage(recipe));
        }
        ItemStack[] productItems = ItemCombinationRecipe.getProductItems(recipe);
        if (productItems != null) {
            if (productItems.length == 2 && requiredItems != null && requiredItems.length == 2) {
                this.player.getInventoryManager().addItem(productItems[0]);
                this.player.getInventoryManager().addItem(productItems[1]);
            } else {
                int n = 0;
                while (n < productItems.length) {
                    this.player.getInventoryManager().addItem(productItems[n]);
                    ++n;
                }
                if (this.player.getActiveCaveLightLevel() > 0 && this.caveLightShortcutArea.containsExclusive(this.player.getPosition())) {
                    this.player.packetSender.sendGameMessage("The light lets you see further into the room.");
                    this.player.moveTo(new Position(this.player.getPosition().getX(), this.player.getPosition().getY() + 23, 0));
                }
            }
        }
        if (ItemCombinationRecipe.getAnimationId(recipe) > 0) {
            this.player.getUpdateState().setAnimation(ItemCombinationRecipe.getAnimationId(recipe));
        }
        if (skillRequirement != null) {
            this.player.getSkillManager().addExperience(skillRequirement[0], ItemCombinationRecipe.getExperience(recipe));
        }
        return true;
    }
"""
    try:
        start = text.index(handle_start)
        end = text.index(handle_end, start)
    except ValueError as exc:
        raise SystemExit(f"{path}: expected handleItemCombination region not found") from exc
    text = text[:start] + handle_method + text[end:]

    fallback_start = "    public static GatheringToolDefinition getOwnedOrFallbackGatheringTool(Player object, int n) {\n"
    fallback_end = "\n    public static GatheringToolDefinition forBrokenToolItemId(int n) {\n"
    fallback_method = """    public static GatheringToolDefinition getOwnedOrFallbackGatheringTool(Player player, int n) {
        GatheringToolDefinition ownedTool = ItemCombinationHandler.findOwnedGatheringTool(player, n);
        if (ownedTool != null) {
            return ownedTool;
        }
        ArrayList tools = ItemCombinationHandler.getGatheringToolsForSkill(n);
        return (GatheringToolDefinition)tools.get(0);
    }
"""
    try:
        start = text.index(fallback_start)
        end = text.index(fallback_end, start)
    except ValueError as exc:
        raise SystemExit(f"{path}: expected getOwnedOrFallbackGatheringTool region not found") from exc
    text = text[:start] + fallback_method + text[end:]

    path.write_text(text, encoding="utf-8")
    return 2


def repair_grand_exchange_offer_locals(source_root: Path) -> int:
    path = source_root / "com/rs2/model/grandexchange/GrandExchangeOffer.java"
    text = path.read_text(encoding="utf-8")
    replacements = [
        ("        if (d < 1.0) {\n", "        if (d2 < 1.0) {\n"),
        (
            "        for (GrandExchangeOffer grandExchangeOffer22 : pendingBuyOfferRemovals) {\n"
            "            buyOffers.remove(grandExchangeOffer22);\n"
            "        }\n"
            "        for (GrandExchangeOffer grandExchangeOffer22 : pendingSellOfferRemovals) {\n"
            "            sellOffers.remove(grandExchangeOffer22);\n"
            "        }\n",
            "        for (Object pendingBuyOfferObject : pendingBuyOfferRemovals) {\n"
            "            GrandExchangeOffer pendingBuyOffer = (GrandExchangeOffer)pendingBuyOfferObject;\n"
            "            buyOffers.remove(pendingBuyOffer);\n"
            "        }\n"
            "        for (Object pendingSellOfferObject : pendingSellOfferRemovals) {\n"
            "            GrandExchangeOffer pendingSellOffer = (GrandExchangeOffer)pendingSellOfferObject;\n"
            "            sellOffers.remove(pendingSellOffer);\n"
            "        }\n",
        ),
        (
            "        for (GrandExchangeOffer grandExchangeOffer9 : pendingBuyOfferRemovals) {\n"
            "            buyOffers.remove(grandExchangeOffer9);\n"
            "            if (!serverOffers.contains(grandExchangeOffer9)) continue;\n"
            "            serverOffers.remove(grandExchangeOffer9);\n"
            "            ++n;\n"
            "        }\n"
            "        for (GrandExchangeOffer grandExchangeOffer10 : pendingSellOfferRemovals) {\n"
            "            sellOffers.remove(grandExchangeOffer10);\n"
            "            if (!serverOffers.contains(grandExchangeOffer10)) continue;\n"
            "            serverOffers.remove(grandExchangeOffer10);\n"
            "            ++n;\n"
            "        }\n",
            "        for (Object pendingBuyOfferObject : pendingBuyOfferRemovals) {\n"
            "            GrandExchangeOffer pendingBuyOffer = (GrandExchangeOffer)pendingBuyOfferObject;\n"
            "            buyOffers.remove(pendingBuyOffer);\n"
            "            if (!serverOffers.contains(pendingBuyOffer)) continue;\n"
            "            serverOffers.remove(pendingBuyOffer);\n"
            "            ++n;\n"
            "        }\n"
            "        for (Object pendingSellOfferObject : pendingSellOfferRemovals) {\n"
            "            GrandExchangeOffer pendingSellOffer = (GrandExchangeOffer)pendingSellOfferObject;\n"
            "            sellOffers.remove(pendingSellOffer);\n"
            "            if (!serverOffers.contains(pendingSellOffer)) continue;\n"
            "            serverOffers.remove(pendingSellOffer);\n"
            "            ++n;\n"
            "        }\n",
        ),
        (
            "            GrandExchangeOffer.settleRecordVsRecordMatch(object4, grandExchangeOffer2, object3, grandExchangeOffer);\n",
            "            GrandExchangeOffer.settleRecordVsRecordMatch((CharacterFileRecord)object4, grandExchangeOffer2, (CharacterFileRecord)object3, grandExchangeOffer);\n",
        ),
        (
            "            GrandExchangeOffer.settlePlayerVsPlayerMatch(object2, grandExchangeOffer2, (Player)object, grandExchangeOffer);\n",
            "            GrandExchangeOffer.settlePlayerVsPlayerMatch((Player)object2, grandExchangeOffer2, (Player)object, grandExchangeOffer);\n",
        ),
        (
            "            GrandExchangeOffer.settleRecordVsPlayerMatch(object4, grandExchangeOffer2, (Player)object, grandExchangeOffer);\n",
            "            GrandExchangeOffer.settleRecordVsPlayerMatch((CharacterFileRecord)object4, grandExchangeOffer2, (Player)object, grandExchangeOffer);\n",
        ),
        (
            "            GrandExchangeOffer.settlePlayerVsRecordMatch(object2, grandExchangeOffer2, object3, grandExchangeOffer);\n",
            "            GrandExchangeOffer.settlePlayerVsRecordMatch((Player)object2, grandExchangeOffer2, (CharacterFileRecord)object3, grandExchangeOffer);\n",
        ),
    ]
    repairs = 0
    for old, new in replacements:
        text, count = replace_exact(text, old, new, path)
        repairs += count
    path.write_text(text, encoding="utf-8")
    return repairs


def repair_game_util_locals(source_root: Path) -> int:
    path = source_root / "com/rs2/util/GameUtil.java"
    text = path.read_text(encoding="utf-8")

    roll_level_start = "    public static int rollLevelScaledChanceIndex(int[] nArray, int[] nArray2, int[] nArray3, int n) {\n"
    roll_level_end = "\n    public static boolean rollLevelScaledChance(int n, int n2, int n3) {\n"
    roll_level_method = """    public static int rollLevelScaledChanceIndex(int[] nArray, int[] nArray2, int[] nArray3, int n) {
        ArrayList entries = new ArrayList();
        ArrayList sortedEntries = new ArrayList();
        int n2 = 0;
        while (n2 < nArray.length) {
            WeightedChanceEntry entry = new WeightedChanceEntry(nArray[n2], nArray2[n2], nArray3[n2]);
            sortedEntries.add(entry);
            entries.add(entry);
            ++n2;
        }
        Collections.sort(sortedEntries, new WeightedChanceEntryThresholdComparator());
        double[] sortedProbabilities = GameUtil.calculateLevelScaledProbabilities(sortedEntries, n);
        double[] probabilities = new double[sortedProbabilities.length];
        int n3 = 0;
        while (n3 < sortedProbabilities.length) {
            probabilities[entries.indexOf(sortedEntries.get(n3))] = sortedProbabilities[n3];
            ++n3;
        }
        n3 = GameUtil.rollProbabilityIndex(probabilities);
        return n3;
    }
"""
    try:
        start = text.index(roll_level_start)
        end = text.index(roll_level_end, start)
    except ValueError as exc:
        raise SystemExit(f"{path}: expected rollLevelScaledChanceIndex region not found") from exc
    text = text[:start] + roll_level_method + text[end:]

    probabilities_start = "    private static double[] calculateLevelScaledProbabilities(ArrayList arrayList, int n) {\n"
    probabilities_end = "\n    public static int rollProbabilityIndex(double[] dArray) {\n"
    probabilities_method = """    private static double[] calculateLevelScaledProbabilities(ArrayList arrayList, int n) {
        double[] probabilities = new double[arrayList.size()];
        int n2 = 0;
        while (n2 < arrayList.size()) {
            WeightedChanceEntry entry = (WeightedChanceEntry)arrayList.get(n2);
            if (n < entry.requiredLevel) {
                probabilities[n2] = 0.0;
            } else {
                double probability = 1.0;
                int n3 = 0;
                while (n3 < arrayList.size()) {
                    WeightedChanceEntry weightedChanceEntry = (WeightedChanceEntry)arrayList.get(n3);
                    if (n3 == n2) {
                        probability *= GameUtil.calculateLevelScaledChance(weightedChanceEntry.lowChance, weightedChanceEntry.highChance, n);
                        probabilities[n2] = probability;
                        break;
                    }
                    if (n >= weightedChanceEntry.requiredLevel) {
                        probability *= 1.0 - GameUtil.calculateLevelScaledChance(weightedChanceEntry.lowChance, weightedChanceEntry.highChance, n);
                    }
                    ++n3;
                }
                if (n3 >= arrayList.size()) {
                    throw new IllegalStateException("Index out of bounds");
                }
            }
            ++n2;
        }
        return probabilities;
    }
"""
    try:
        start = text.index(probabilities_start)
        end = text.index(probabilities_end, start)
    except ValueError as exc:
        raise SystemExit(f"{path}: expected calculateLevelScaledProbabilities region not found") from exc
    text = text[:start] + probabilities_method + text[end:]

    replacements = [
        ("            if (d4 >= d2) {\n", "            if (d3 >= d2) {\n"),
        ("            if (d8 >= d6) {\n", "            if (d7 >= d6) {\n"),
        (
            "        for (ItemStack itemStack2 : Server.trackedRareItems) {\n"
            "            if (itemStack.getId() == itemStack2.getId()) {\n"
            "                int n2 = itemStack2.getAmount() + itemStack.getAmount();\n"
            "                itemStack.setAmount(n2);\n"
            "                Server.trackedRareItems.set(n, itemStack);\n"
            "                return;\n"
            "            }\n"
            "            ++n;\n"
            "        }\n",
            "        for (Object trackedRareItemObject : Server.trackedRareItems) {\n"
            "            ItemStack itemStack2 = (ItemStack)trackedRareItemObject;\n"
            "            if (itemStack.getId() == itemStack2.getId()) {\n"
            "                int n2 = itemStack2.getAmount() + itemStack.getAmount();\n"
            "                itemStack.setAmount(n2);\n"
            "                Server.trackedRareItems.set(n, itemStack);\n"
            "                return;\n"
            "            }\n"
            "            ++n;\n"
            "        }\n",
        ),
        (
            "    private static boolean hasClearPath(int n, int n2, int n3, int n4, int n5, boolean n6) {\n"
            "        if (n6) {\n",
            "    private static boolean hasClearPath(int n, int n2, int n3, int n4, int n5, boolean bl) {\n"
            "        if (bl) {\n",
        ),
        (
            "        int n7 = n;\n"
            "        n = 1;\n"
            "        n = 1;\n"
            "        n = n7;\n"
            "        n = n3 - n;\n"
            "        n2 = n4 - n2;\n"
            "        n6 = Math.max(Math.abs(n), Math.abs(n2));\n"
            "        int n8 = 0;\n"
            "        while (n8 < n6) {\n",
            "        int n7 = n;\n"
            "        n = n3 - n7;\n"
            "        n2 = n4 - n2;\n"
            "        int n6 = Math.max(Math.abs(n), Math.abs(n2));\n"
            "        int n8 = 0;\n"
            "        while (n8 < n6) {\n",
        ),
    ]
    repairs = 2
    for old, new in replacements:
        text, count = replace_exact(text, old, new, path)
        repairs += count
    path.write_text(text, encoding="utf-8")
    return repairs


def repair_music_manager_locals(source_root: Path) -> int:
    path = source_root / "com/rs2/model/music/MusicManager.java"
    text = path.read_text(encoding="utf-8")
    start_marker = "    public final void updateForPlayerPosition(Player player) {\n"
    end_marker = "\n    public static void unlockTrack(Player player, int n) {\n"
    method = """    public final void updateForPlayerPosition(Player player) {
        int playerX = player.getPosition().getX();
        int playerY = player.getPosition().getY();
        int[] areaIds = new int[5];
        int areaIdCount = 0;
        int areaId = 0;
        while (areaId < MusicAreaDefinition.areaCount) {
            boolean inArea = false;
            MusicAreaDefinition musicAreaDefinition = MusicAreaDefinition.forAreaId(areaId);
            if (musicAreaDefinition.getRegionCount() == 0) {
                inArea = musicAreaDefinition.getAreaBounds().contains(new Position(playerX, playerY, 0));
            } else {
                int regionId = GameUtil.getRegionId(playerX, playerY);
                int[] regionIds = musicAreaDefinition.getRegionIds();
                int n = 0;
                while (n < musicAreaDefinition.getRegionCount()) {
                    if (regionId == regionIds[n]) {
                        inArea = true;
                        break;
                    }
                    ++n;
                }
            }
            if (inArea) {
                areaIds[areaIdCount] = areaId;
                ++areaIdCount;
            }
            ++areaId;
        }
        int bestPriority = 0;
        int bestAreaId = -1;
        int n = 0;
        while (n < areaIdCount) {
            MusicAreaDefinition musicAreaDefinition = MusicAreaDefinition.forAreaId(areaIds[n]);
            if (n == 0) {
                bestPriority = musicAreaDefinition.getPriority();
                bestAreaId = musicAreaDefinition.getAreaId();
            } else if (bestPriority < musicAreaDefinition.getPriority()) {
                bestPriority = musicAreaDefinition.getPriority();
                bestAreaId = musicAreaDefinition.getAreaId();
            }
            ++n;
        }
        this.currentAreaId = bestAreaId;
        int trackId;
        if (this.currentAreaId != -1) {
            MusicAreaDefinition musicAreaDefinition = MusicAreaDefinition.forAreaId(this.currentAreaId);
            int areaTrackId = musicAreaDefinition.getTrackId();
            if (player.eo) {
                this.currentTrackId = areaTrackId;
            }
            MusicTrackDefinition musicTrackDefinition = MusicTrackDefinition.forTrackId(areaTrackId);
            int unlockConfigId = musicTrackDefinition.getUnlockConfigId();
            int unlockBitMask = musicTrackDefinition.getUnlockBitMask();
            if (unlockConfigId != -1 && (player.ep[unlockConfigId] & unlockBitMask) == 0) {
                player.eo = true;
                player.ep[unlockConfigId] = player.ep[unlockConfigId] + unlockBitMask;
                player.packetSender.sendConfig(unlockConfigId, player.ep[unlockConfigId]);
                player.packetSender.sendGameMessage("@red@You have unlocked a new music track: " + musicTrackDefinition.getName());
                this.currentTrackId = areaTrackId;
            }
            trackId = this.currentTrackId;
        } else {
            trackId = -1;
        }
        if (trackId != -1) {
            if (trackId == player.cg) {
                return;
            }
            player.cg = trackId;
            MusicTrackDefinition musicTrackDefinition = MusicTrackDefinition.forTrackId(trackId);
            if (musicTrackDefinition.getButtonId() != -1 || buttonlessTrackIds.contains(trackId)) {
                player.packetSender.sendMusicTrack(musicTrackDefinition);
            }
        }
    }
"""
    try:
        start = text.index(start_marker)
        end = text.index(end_marker, start)
    except ValueError as exc:
        raise SystemExit(f"{path}: expected updateForPlayerPosition region not found") from exc
    text = text[:start] + method + text[end:]
    path.write_text(text, encoding="utf-8")
    return 1


def repair_cache_store_locals(source_root: Path) -> int:
    path = source_root / "com/rs2/cache/CacheStore.java"
    text = path.read_text(encoding="utf-8")
    replacements = {
        "    private CacheStore(File file) {\n": "    private CacheStore(File file) throws CacheStoreException {\n",
        "    public final CacheFile readFile(int n, int n2) {\n": "    public final CacheFile readFile(int n, int n2) throws IOException {\n",
        "    public final void close() {\n": "    public final void close() throws IOException {\n",
    }
    for old, new in replacements.items():
        if old in text:
            text = text.replace(old, new, 1)
    start_marker = "    private void verifyLauncherJarIntegrity() {\n"
    end_marker = "\n}\n"
    method = """    private void verifyLauncherJarIntegrity() {
        char[] chars = new char[]{'.', '/', 'd', 'a', 't', 'S', 'e', 'r', 'v', 'j'};
        int[] indices = new int[15];
        indices[1] = 1;
        indices[2] = 2;
        indices[3] = 3;
        indices[4] = 4;
        indices[5] = 3;
        indices[6] = 1;
        indices[7] = 2;
        indices[8] = 3;
        indices[9] = 4;
        indices[10] = 3;
        indices[12] = 2;
        indices[13] = 3;
        indices[14] = 4;
        try {
            String string = "";
            int n = 0;
            while (n < 15) {
                string = String.valueOf(string) + chars[indices[n]];
                ++n;
            }
            byte[] metadataBytes = FileUtil.readBytes(string, false);
            if (metadataBytes == null) {
                cacheVerificationFailed = true;
                return;
            }
            ByteArrayReader byteArrayReader = new ByteArrayReader(metadataBytes);
            byteArrayReader.readUnsignedByte();
            int expectedCrc = byteArrayReader.readInt();
            int md5Length = byteArrayReader.readUnsignedByte();
            byte[] expectedMd5Bytes = new byte[md5Length];
            n = 0;
            while (n < md5Length) {
                expectedMd5Bytes[n] = (byte)byteArrayReader.readUnsignedByte();
                ++n;
            }
            int sha1Length = byteArrayReader.readUnsignedByte();
            byte[] expectedSha1Bytes = new byte[sha1Length];
            int n2 = 0;
            while (n2 < sha1Length) {
                expectedSha1Bytes[n2] = (byte)byteArrayReader.readUnsignedByte();
                ++n2;
            }
            byte[] launcherBytes = FileUtil.readBytes(ServerSettings.launcherJarPath, false);
            if (launcherBytes == null) {
                cacheVerificationFailed = true;
                return;
            }
            CRC32 crc32 = new CRC32();
            crc32.reset();
            crc32.update(launcherBytes);
            int actualCrc = (int)crc32.getValue();
            String actualMd5 = new BigInteger(1, MessageDigest.getInstance("MD5").digest(launcherBytes)).toString(16);
            String expectedMd5 = new BigInteger(1, expectedMd5Bytes).toString(16);
            String actualSha1 = new BigInteger(1, MessageDigest.getInstance("SHA-1").digest(launcherBytes)).toString(16);
            String expectedSha1 = new BigInteger(1, expectedSha1Bytes).toString(16);
            if (expectedCrc != actualCrc || !expectedMd5.equals(actualMd5) || !expectedSha1.equals(actualSha1)) {
                cacheVerificationFailed = true;
            }
            ServerSettings.cacheVerificationShutdownPending = false;
            return;
        }
        catch (Exception exception) {
            cacheVerificationFailed = true;
            return;
        }
    }
"""
    try:
        start = text.index(start_marker)
        end = text.index(end_marker, start)
    except ValueError as exc:
        raise SystemExit(f"{path}: expected verifyLauncherJarIntegrity region not found") from exc
    text = text[:start] + method + text[end:]
    path.write_text(text, encoding="utf-8")
    return 1


def repair_cache_definition_index_signature(source_root: Path) -> int:
    path = source_root / "com/rs2/cache/CacheDefinitionIndex.java"
    text = path.read_text(encoding="utf-8")
    repairs = 0
    if "import java.io.IOException;\n" not in text:
        text = text.replace("import com.rs2.net.packet.PacketSender;\n", "import com.rs2.net.packet.PacketSender;\nimport java.io.IOException;\n", 1)
        repairs += 1
    old = "    public CacheDefinitionIndex(CacheStore cacheStore) {\n"
    new = "    public CacheDefinitionIndex(CacheStore cacheStore) throws IOException {\n"
    if old in text:
        text = text.replace(old, new, 1)
        repairs += 1
    path.write_text(text, encoding="utf-8")
    return repairs


def repair_shop_manager_locals(source_root: Path) -> int:
    path = source_root / "com/rs2/model/shop/ShopManager.java"
    text = path.read_text(encoding="utf-8")
    refresh_start = "    public static void refreshShopForPlayers(int n) {\n"
    refresh_end = "\n    public static void openShop(Player player, int n) {\n"
    refresh_method = """    public static void refreshShopForPlayers(int n) {
        ShopDefinition shopDefinition = (ShopDefinition)shopDefinitions.get(n);
        Player[] playerArray = World.getPlayers();
        int n2 = playerArray.length;
        int n3 = 0;
        while (n3 < n2) {
            Player player = playerArray[n3];
            if (player != null && player.getCurrentShopId() == n) {
                ItemStack[] itemStackArray = shopDefinition.getStock().getRawItems();
                player.packetSender.sendItemContainer(3900, itemStackArray);
            }
            ++n3;
        }
    }
"""
    try:
        start = text.index(refresh_start)
        end = text.index(refresh_end, start)
    except ValueError as exc:
        raise SystemExit(f"{path}: expected refreshShopForPlayers region not found") from exc
    text = text[:start] + refresh_method + text[end:]

    open_start = "    public static void openShop(Player player, int n) {\n"
    open_end = "\n    private static boolean isSkillcapeBundleItem(int n) {\n"
    open_method = """    public static void openShop(Player player, int n) {
        Player player2;
        if (n >= shopDefinitions.toArray().length) {
            return;
        }
        ShopDefinition shopDefinition = (ShopDefinition)shopDefinitions.get(n);
        if (shopDefinition.isMembersOnly()) {
            if (player.isMember()) {
                if (ServerSettings.freeToPlayWorld) {
                    player.packetSender.sendGameMessage("You need to be in members world to access members content.");
                    return;
                }
            } else {
                player.packetSender.sendGameMessage("You need a members account to access members content.");
                return;
            }
        }
        String string = shopDefinition.getName();
        if (shopDefinition.getCurrency() == ShopCurrency.DONATOR_POINTS) {
            string = "<img=2>" + string + "<img=2>";
            player2 = player;
            player2.packetSender.sendGameMessage("You have " + player.getDonatorPoints() + " Donator points.");
        }
        ItemStack[] itemStackArray = shopDefinition.getStock().getRawItems();
        player.getInventoryManager().sendToInterface(3823);
        player2 = player;
        player2.packetSender.sendItemContainer(3900, itemStackArray);
        player2 = player;
        player2.packetSender.sendInterfaceText(string, 3901);
        player2 = player;
        player2.packetSender.showInterfaceWithInventory(3824, 3822);
        player.setCurrentShopId(n);
        player.getAttributes().put("isShopping", Boolean.TRUE);
    }
"""
    try:
        start = text.index(open_start)
        end = text.index(open_end, start)
    except ValueError as exc:
        raise SystemExit(f"{path}: expected openShop region not found") from exc
    text = text[:start] + open_method + text[end:]

    replacements = [
        ("            n7 = shopDefinition.getCurrencyItemId();\n", "            n6 = shopDefinition.getCurrencyItemId();\n"),
        ("                    n7 = ((Player)object).getDonatorPoints();\n", "                    n6 = ((Player)object).getDonatorPoints();\n"),
        ("                    n7 = n6 = -1;\n", "                    n6 = -1;\n"),
    ]
    repairs = 2
    for old, new in replacements:
        text, count = replace_exact(text, old, new, path)
        repairs += count
    path.write_text(text, encoding="utf-8")
    return repairs


def repair_collision_map_area_locals(source_root: Path) -> int:
    repairs = 0
    for class_name in ("ProjectileCollisionMap", "WalkingCollisionMap"):
        path = source_root / f"com/rs2/util/path/{class_name}.java"
        text = path.read_text(encoding="utf-8")
        old = (
            f"    private static void removeAreaCollision(int n, int n2, int n3, int n4, int n5, boolean n6) {{\n"
            "        int n7 = 256;\n"
            "        if (n6 != 0) {\n"
            "            n7 = 131328;\n"
            "        }\n"
            "        n6 = n;\n"
            "        while (n6 < n + n4) {\n"
            "            int n8 = n2;\n"
            "            while (n8 < n2 + n5) {\n"
            f"                {class_name}.clearTileFlag(n6, n8, n3, n7);\n"
            "                ++n8;\n"
            "            }\n"
            "            ++n6;\n"
            "        }\n"
            "    }\n"
        )
        new = (
            f"    private static void removeAreaCollision(int n, int n2, int n3, int n4, int n5, boolean bl) {{\n"
            "        int n7 = 256;\n"
            "        if (bl) {\n"
            "            n7 = 131328;\n"
            "        }\n"
            "        int n6 = n;\n"
            "        while (n6 < n + n4) {\n"
            "            int n8 = n2;\n"
            "            while (n8 < n2 + n5) {\n"
            f"                {class_name}.clearTileFlag(n6, n8, n3, n7);\n"
            "                ++n8;\n"
            "            }\n"
            "            ++n6;\n"
            "        }\n"
            "    }\n"
        )
        text, count = replace_exact(text, old, new, path)
        repairs += count
        count = text.count("                int n13 = 256;\n")
        if count:
            text = text.replace("                int n13 = 256;\n", "                n9 = 256;\n")
            repairs += count
        path.write_text(text, encoding="utf-8")
    return repairs


def repair_small_recipe_constructors(source_root: Path) -> int:
    repairs = 0

    path = source_root / "com/rs2/model/skill/fletching/logs/AcheyLogFletchingRecipe.java"
    text = path.read_text(encoding="utf-8")
    old = (
        "    private AcheyLogFletchingRecipe(int n3, int n4, int n5, double d) {\n"
        "        this.buttonId = n3;\n"
        "        this.logItemId = 2862;\n"
        "        this.productItemId = 4825;\n"
        "        this.menuQuantity = (int)d;\n"
        "        this.requiredLevel = 30;\n"
        "        this.experience = 45.0;\n"
        "    }\n"
    )
    new = (
        "    private AcheyLogFletchingRecipe(int buttonId, int logItemId, int productItemId, int menuQuantity, int requiredLevel, double experience) {\n"
        "        this.buttonId = buttonId;\n"
        "        this.logItemId = logItemId;\n"
        "        this.productItemId = productItemId;\n"
        "        this.menuQuantity = menuQuantity;\n"
        "        this.requiredLevel = requiredLevel;\n"
        "        this.experience = experience;\n"
        "    }\n"
    )
    text, count = replace_exact(text, old, new, path)
    repairs += count
    path.write_text(text, encoding="utf-8")

    path = source_root / "com/rs2/model/skill/crafting/DramenStaffRecipe.java"
    text = path.read_text(encoding="utf-8")
    old = (
        "    private DramenStaffRecipe(int n3, int n4, int n5, int n6, double d) {\n"
        "        this.buttonId = n3;\n"
        "        this.ingredientItemId = 771;\n"
        "        this.productItemId = 772;\n"
        "        this.quantity = n6;\n"
        "        this.ingredientAmount = 1;\n"
        "        this.requiredLevel = 31;\n"
        "        this.experience = 0.0;\n"
        "    }\n"
    )
    new = (
        "    private DramenStaffRecipe(int buttonId, int ingredientItemId, int productItemId, int quantity, int ingredientAmount, int requiredLevel, double experience) {\n"
        "        this.buttonId = buttonId;\n"
        "        this.ingredientItemId = ingredientItemId;\n"
        "        this.productItemId = productItemId;\n"
        "        this.quantity = quantity;\n"
        "        this.ingredientAmount = ingredientAmount;\n"
        "        this.requiredLevel = requiredLevel;\n"
        "        this.experience = experience;\n"
        "    }\n"
    )
    text, count = replace_exact(text, old, new, path)
    repairs += count
    path.write_text(text, encoding="utf-8")

    return repairs


def repair_silver_crafting_task_locals(source_root: Path) -> int:
    path = source_root / "com/rs2/model/skill/crafting/SilverCraftingTask.java"
    text = path.read_text(encoding="utf-8")
    old = (
        "        ((CycleEventContainer)object).setTickDelay(3);\n"
        "        this.player.getUpdateState().setAnimation(899);\n"
        "        object = this.player;\n"
        "        ((Player)object).packetSender.sendSoundEffect(469, 1, 0);\n"
        "        object = this.player;\n"
        "        ((Player)object).packetSender.sendGameMessage(\"You make the silver bar into \" + GameplayHelper.getIndefiniteArticle(new ItemStack(this.recipe.getProductItemId()).getDefinition().getName().toLowerCase()) + \" \" + new ItemStack(this.recipe.getProductItemId()).getDefinition().getName().toLowerCase() + \".\");\n"
    )
    new = (
        "        ((CycleEventContainer)object).setTickDelay(3);\n"
        "        this.player.getUpdateState().setAnimation(899);\n"
        "        this.player.packetSender.sendSoundEffect(469, 1, 0);\n"
        "        this.player.packetSender.sendGameMessage(\"You make the silver bar into \" + GameplayHelper.getIndefiniteArticle(new ItemStack(this.recipe.getProductItemId()).getDefinition().getName().toLowerCase()) + \" \" + new ItemStack(this.recipe.getProductItemId()).getDefinition().getName().toLowerCase() + \".\");\n"
    )
    text, count = replace_exact(text, old, new, path)
    path.write_text(text, encoding="utf-8")
    return count


def repair_social_packet_handler_locals(source_root: Path) -> int:
    path = source_root / "com/rs2/net/packet/handler/SocialPacketHandler.java"
    text = path.read_text(encoding="utf-8")
    start_marker = "    public final void handle(Player player, IncomingPacket object) {\n"
    end_marker = "\n}\n"
    method = """    public final void handle(Player player, IncomingPacket packet) {
        switch (packet.getOpcode()) {
            case 188: {
                long l = packet.getReader().readLong();
                player.getSocialManager().addFriend(l);
                return;
            }
            case 215: {
                long l = packet.getReader().readLong();
                player.getSocialManager().removeFromList(player.getFriendsList(), l);
                return;
            }
            case 133: {
                long l = packet.getReader().readLong();
                player.getSocialManager().addIgnore(l);
                return;
            }
            case 74: {
                long l = packet.getReader().readLong();
                player.getSocialManager().removeFromList(player.getIgnoreList(), l);
                return;
            }
            case 126: {
                long l = packet.getReader().readLong();
                int n = packet.getLength() - 8;
                if (n < 0) {
                    return;
                }
                byte[] messageBytes = packet.getReader().readBytes(n);
                if (player.isMuted()) {
                    player.packetSender.sendGameMessage("You are muted and cannot talk. Mute expires in: " + (GameplayHelper.getHoursBetween(System.currentTimeMillis(), player.getMuteExpires()) + 1) + " hours.");
                    return;
                }
                player.getSocialManager().sendPrivateMessage(player, l, messageBytes, n);
            }
        }
    }
"""
    try:
        start = text.index(start_marker)
        end = text.index(end_marker, start)
    except ValueError as exc:
        raise SystemExit(f"{path}: expected handle region not found") from exc
    text = text[:start] + method + text[end:]
    path.write_text(text, encoding="utf-8")
    return 1


def repair_grand_exchange_price_sample_locals(source_root: Path) -> int:
    path = source_root / "com/rs2/model/grandexchange/GrandExchangePriceSample.java"
    text = path.read_text(encoding="utf-8")
    old = (
        "    GrandExchangePriceSample(GrandExchangeOffer object) {\n"
        "        this.timestampMillis = System.currentTimeMillis();\n"
        "        GrandExchangeOffer grandExchangeOffer = object;\n"
        "        this.itemId = grandExchangeOffer.itemId;\n"
        "        grandExchangeOffer = object;\n"
        "        this.quantity = grandExchangeOffer.quantity;\n"
        "        grandExchangeOffer = object;\n"
        "        this.unitPrice = grandExchangeOffer.unitPrice;\n"
        "        object = ItemDefinition.forId(this.itemId);\n"
        "        if (!sampledItemIds.contains(this.itemId)) {\n"
        "            sampledItemIds.add(this.itemId);\n"
        "        }\n"
        "        ((ItemDefinition)object).grandExchangePriceSamples.add(this);\n"
        "        allSamples.add(this);\n"
        "        try {\n"
        "            GrandExchangePriceSample.savePriceSamples();\n"
        "            return;\n"
        "        }\n"
        "        catch (IOException iOException) {\n"
        "            object = iOException;\n"
        "            iOException.printStackTrace();\n"
        "            return;\n"
        "        }\n"
        "    }\n"
    )
    new = (
        "    GrandExchangePriceSample(GrandExchangeOffer grandExchangeOffer) {\n"
        "        this.timestampMillis = System.currentTimeMillis();\n"
        "        this.itemId = grandExchangeOffer.itemId;\n"
        "        this.quantity = grandExchangeOffer.quantity;\n"
        "        this.unitPrice = grandExchangeOffer.unitPrice;\n"
        "        ItemDefinition itemDefinition = ItemDefinition.forId(this.itemId);\n"
        "        if (!sampledItemIds.contains(this.itemId)) {\n"
        "            sampledItemIds.add(this.itemId);\n"
        "        }\n"
        "        itemDefinition.grandExchangePriceSamples.add(this);\n"
        "        allSamples.add(this);\n"
        "        try {\n"
        "            GrandExchangePriceSample.savePriceSamples();\n"
        "            return;\n"
        "        }\n"
        "        catch (IOException iOException) {\n"
        "            iOException.printStackTrace();\n"
        "            return;\n"
        "        }\n"
        "    }\n"
    )
    text, count = replace_exact(text, old, new, path)
    repairs = count
    replacements = [
        (
            "    private static void savePriceSamples() {\n",
            "    private static void savePriceSamples() throws IOException {\n",
        ),
        (
            "            for (GrandExchangePriceSample grandExchangePriceSample : ((ItemDefinition)object).grandExchangePriceSamples) {\n"
            "                if (n7 >= n2) {\n"
            "                    arrayList.add(grandExchangePriceSample);\n"
            "                    continue;\n"
            "                }\n"
            "                GrandExchangePriceSample grandExchangePriceSample3 = grandExchangePriceSample;\n"
            "                n7 += grandExchangePriceSample3.quantity;\n"
            "            }\n",
            "            for (Object grandExchangePriceSampleObject : ((ItemDefinition)object).grandExchangePriceSamples) {\n"
            "                GrandExchangePriceSample grandExchangePriceSample = (GrandExchangePriceSample)grandExchangePriceSampleObject;\n"
            "                if (n7 >= n2) {\n"
            "                    arrayList.add(grandExchangePriceSample);\n"
            "                    continue;\n"
            "                }\n"
            "                GrandExchangePriceSample grandExchangePriceSample3 = grandExchangePriceSample;\n"
            "                n7 += grandExchangePriceSample3.quantity;\n"
            "            }\n",
        ),
    ]
    for old, new in replacements:
        text, count = replace_exact(text, old, new, path)
        repairs += count
    path.write_text(text, encoding="utf-8")
    return repairs


def repair_rune_thrownaxe_special_attack_locals(source_root: Path) -> int:
    path = source_root / "com/rs2/model/combat/special/RuneThrownaxeSpecialAttack.java"
    text = path.read_text(encoding="utf-8")
    start_marker = "    public final boolean prepareSpecialAttack() {\n"
    end_marker = "\n}\n"
    method = """    public final boolean prepareSpecialAttack() {
        if (!super.prepareSpecialAttack()) {
            return false;
        }
        ArrayList chainedTargets = new ArrayList();
        if (this.player.isInMultiCombatArea() && this.primaryTarget.isInMultiCombatArea()) {
            Player[] players = World.getPlayers();
            int n = 0;
            while (n < players.length) {
                Player nearbyPlayer = players[n];
                if (nearbyPlayer != null && nearbyPlayer != this.getAttacker() && nearbyPlayer != this.getTarget() && nearbyPlayer.isInMultiCombatArea() && GameUtil.isWithinDistance(this.getTarget().getPosition(), nearbyPlayer.getPosition(), 3)) {
                    chainedTargets.add(nearbyPlayer);
                }
                ++n;
            }
            Npc[] npcs = World.getNpcs();
            n = 0;
            while (n < npcs.length) {
                Npc nearbyNpc = npcs[n];
                if (nearbyNpc != null && nearbyNpc != this.getTarget() && nearbyNpc.isInMultiCombatArea() && GameUtil.isWithinDistance(this.getTarget().getPosition(), nearbyNpc.getPosition(), 3)) {
                    chainedTargets.add(nearbyNpc);
                }
                ++n;
            }
            Collections.sort(chainedTargets, new RuneThrownaxeTargetDistanceComparator(this));
        }
        ArrayList validTargets = new ArrayList();
        if (chainedTargets.size() > 5) {
            int n = 0;
            while (n < chainedTargets.size()) {
                Entity entity = (Entity)chainedTargets.get(n);
                AttackValidationResult attackValidationResult = CombatCycleEvent.validateAttack(this.player, entity);
                boolean doorSupportNpc = false;
                if (entity.isNpc()) {
                    Npc npc = (Npc)entity;
                    doorSupportNpc = npc.isDoorSupportNpc();
                }
                if ((attackValidationResult == AttackValidationResult.VALID || doorSupportNpc) && validTargets.size() < 5) {
                    validTargets.add(entity);
                    if (validTargets.size() == 5) break;
                }
                ++n;
            }
        } else {
            validTargets = chainedTargets;
        }
        double d = this.calculateMaxHit();
        ProjectileTiming projectileTiming = this.sourceWeaponProfile.getAmmunitionProfile().getProjectileTiming();
        ProjectileDefinition projectileDefinition = new ProjectileDefinition(258, projectileTiming.copy());
        this.setHitDefinitions(new HitDefinition[]{new HitDefinition(this.getAttackStyle(), HitType.NORMAL, d).enableRandomDamage().setProjectile(projectileDefinition).setAccuracyMultiplier(1.0).enableAccuracyCheck(true).setChainedTargets(validTargets).setChainedSource(this.player)});
        return true;
    }
"""
    try:
        start = text.index(start_marker)
        end = text.index(end_marker, start)
    except ValueError as exc:
        raise SystemExit(f"{path}: expected prepareSpecialAttack region not found") from exc
    text = text[:start] + method + text[end:]
    path.write_text(text, encoding="utf-8")
    return 1


def repair_more_small_cluster_locals(source_root: Path) -> int:
    repairs = 0

    path = source_root / "com/rs2/model/skill/crafting/armor/HardLeatherRecipe.java"
    text = path.read_text(encoding="utf-8")
    old = (
        "    private HardLeatherRecipe(int n3, int n4, int n5, int n6, double d) {\n"
        "        this.buttonId = n3;\n"
        "        this.materialItemId = 1743;\n"
        "        this.materialAmount = 1;\n"
        "        this.productItemId = 1131;\n"
        "        this.quantity = (int)d;\n"
        "        this.requiredLevel = 28;\n"
        "        this.experience = 35.0;\n"
        "    }\n"
    )
    new = (
        "    private HardLeatherRecipe(int buttonId, int materialItemId, int materialAmount, int productItemId, int quantity, int requiredLevel, double experience) {\n"
        "        this.buttonId = buttonId;\n"
        "        this.materialItemId = materialItemId;\n"
        "        this.materialAmount = materialAmount;\n"
        "        this.productItemId = productItemId;\n"
        "        this.quantity = quantity;\n"
        "        this.requiredLevel = requiredLevel;\n"
        "        this.experience = experience;\n"
        "    }\n"
    )
    text, count = replace_exact(text, old, new, path)
    repairs += count
    path.write_text(text, encoding="utf-8")

    path = source_root / "com/rs2/model/EntityReference.java"
    text = path.read_text(encoding="utf-8")
    start_marker = "    public final Entity resolve() {\n"
    end_marker = "\n    public boolean equals(Object object) {\n"
    method = """    public final Entity resolve() {
        if (this.cachedEntity == null || this.cachedEntity.getIndex() == -1) {
            this.cachedEntity = null;
            Entity[] entities = this.playerReference ? World.getPlayers() : World.getNpcs();
            int n = entities.length;
            int n2 = 0;
            while (n2 < n) {
                Entity entity = entities[n2];
                if (entity != null) {
                    Player player;
                    if (entity.isPlayer() && this.playerReference && (player = (Player)entity).getUsername().equals(this.playerUsername)) {
                        return entity;
                    }
                    if (!this.playerReference && entity.getReferenceId() == this.referenceId) {
                        this.cachedEntity = entity;
                        break;
                    }
                }
                ++n2;
            }
        }
        return this.cachedEntity;
    }
"""
    try:
        start = text.index(start_marker)
        end = text.index(end_marker, start)
    except ValueError as exc:
        raise SystemExit(f"{path}: expected resolve region not found") from exc
    text = text[:start] + method + text[end:]
    replacements = [
        ("                this.cachedEntity = object;\n", "                this.cachedEntity = (Entity)object;\n"),
    ]
    repairs += 1
    for old, new in replacements:
        text, count = replace_exact(text, old, new, path)
        repairs += count
    path.write_text(text, encoding="utf-8")

    path = source_root / "com/rs2/model/ground/GroundItem.java"
    text = path.read_text(encoding="utf-8")
    old = (
        "        position = position.copy();\n"
        "        object = this;\n"
        "        this.position = position;\n"
    )
    new = (
        "        position = position.copy();\n"
        "        this.position = position;\n"
    )
    text, count = replace_exact(text, old, new, path)
    repairs += count
    path.write_text(text, encoding="utf-8")

    path = source_root / "com/rs2/bot/BotRoute.java"
    text = path.read_text(encoding="utf-8")
    old = (
        "    public BotRoute reversed() {\n"
        "        Position[] positionArray = new ArrayList(Arrays.asList(this.waypoints));\n"
        "        Collections.reverse(positionArray);\n"
        "        positionArray = positionArray.toArray(new Position[positionArray.size()]);\n"
        "        return new BotRoute(positionArray);\n"
        "    }\n"
    )
    new = (
        "    public BotRoute reversed() {\n"
        "        ArrayList positionList = new ArrayList(Arrays.asList(this.waypoints));\n"
        "        Collections.reverse(positionList);\n"
        "        Position[] positionArray = (Position[])positionList.toArray(new Position[positionList.size()]);\n"
        "        return new BotRoute(positionArray);\n"
        "    }\n"
    )
    text, count = replace_exact(text, old, new, path)
    repairs += count
    path.write_text(text, encoding="utf-8")

    path = source_root / "com/rs2/model/item/consumable/FoodHandler.java"
    text = path.read_text(encoding="utf-8")
    old = (
        "                ItemStack itemStack = new ItemStack(n4);\n"
        "                Object object = itemStack.getDefinition();\n"
        "                if ((object = object.getName()).contains(\"(\")) {\n"
        "                    object = object.split(\"\\\\(\");\n"
        "                    object = object[0];\n"
        "                }\n"
        "                PotionDefinition.setName(potionDefinition, (String)object);\n"
    )
    new = (
        "                ItemStack itemStack = new ItemStack(n4);\n"
        "                String potionName = itemStack.getDefinition().getName();\n"
        "                if (potionName.contains(\"(\")) {\n"
        "                    String[] potionNameParts = potionName.split(\"\\\\(\");\n"
        "                    potionName = potionNameParts[0];\n"
        "                }\n"
        "                PotionDefinition.setName(potionDefinition, potionName);\n"
    )
    text, count = replace_exact(text, old, new, path)
    repairs += count
    path.write_text(text, encoding="utf-8")

    path = source_root / "com/rs2/model/skill/farming/CompostBinEmptyTask.java"
    text = path.read_text(encoding="utf-8")
    old = (
        "        if (this.manager.states[this.binIndex] < 16) {\n"
        "            int n2 = this.binIndex;\n"
        "            object = this.manager;\n"
        "            ((CompostBinManager)object).states[n2] = 0;\n"
        "            ((CompostBinManager)object).itemIds[n2] = 0;\n"
        "            ((CompostBinManager)object).lastUpdateTicks[n2] = 0L;\n"
        "        }\n"
    )
    new = (
        "        if (this.manager.states[this.binIndex] < 16) {\n"
        "            int n2 = this.binIndex;\n"
        "            this.manager.states[n2] = 0;\n"
        "            this.manager.itemIds[n2] = 0;\n"
        "            this.manager.lastUpdateTicks[n2] = 0L;\n"
        "        }\n"
    )
    text, count = replace_exact(text, old, new, path)
    repairs += count
    path.write_text(text, encoding="utf-8")

    return repairs


def repair_next_small_cluster_locals(source_root: Path) -> int:
    repairs = 0

    path = source_root / "com/rs2/HiscoresDatabase.java"
    text = path.read_text(encoding="utf-8")
    start_marker = "    private static ResultSet executeSql(String object) {\n"
    end_marker = "\n    public static void disconnect() {\n"
    method = """    private static ResultSet executeSql(String sql) {
        try {
            if (sql.toLowerCase().startsWith("select")) {
                return statement.executeQuery(sql);
            }
            statement.executeUpdate(sql);
            return null;
        }
        catch (Exception exception) {
            exception.printStackTrace();
            HiscoresDatabase.disconnect();
            return null;
        }
    }
"""
    try:
        start = text.index(start_marker)
        end = text.index(end_marker, start)
    except ValueError as exc:
        raise SystemExit(f"{path}: expected executeSql region not found") from exc
    text = text[:start] + method + text[end:]
    text, count = replace_exact(text, "            object = exception;\n", "", path)
    repairs += 1 + count
    path.write_text(text, encoding="utf-8")

    path = source_root / "com/rs2/model/item/ItemDefinition.java"
    text = path.read_text(encoding="utf-8")
    text, count = replace_exact(
        text,
        "                ItemDefinition.definitionsById[n3] = object;\n",
        "                ItemDefinition.definitionsById[n3] = (ItemDefinition)object;\n",
        path,
    )
    repairs += count
    text, count = replace_exact(
        text,
        "        catch (IOException iOException) {\n"
        "            IOException iOException2 = iOException;\n"
        "            iOException.printStackTrace();\n"
        "        }\n",
        "        catch (Exception exception) {\n"
        "            exception.printStackTrace();\n"
        "        }\n",
        path,
    )
    repairs += count
    start_marker = "    public static int findIdByName(String object) {\n"
    end_marker = "\n}\n"
    method = """    public static int findIdByName(String name) {
        String normalizedName = name.toLowerCase();
        ItemDefinition[] itemDefinitionArray = definitionsById;
        int n = definitionsById.length;
        int n2 = 0;
        while (n2 < n) {
            ItemDefinition itemDefinition = itemDefinitionArray[n2];
            if (itemDefinition != null && itemDefinition.getName() != null && itemDefinition.getName().toLowerCase().equalsIgnoreCase(normalizedName)) {
                return itemDefinition.id;
            }
            ++n2;
        }
        return 0;
    }
"""
    try:
        start = text.index(start_marker)
        end = text.index(end_marker, start)
    except ValueError as exc:
        raise SystemExit(f"{path}: expected findIdByName region not found") from exc
    text = text[:start] + method + text[end:]
    repairs += 1
    path.write_text(text, encoding="utf-8")

    path = source_root / "com/rs2/model/MovementQueue.java"
    text = path.read_text(encoding="utf-8")
    old = (
        "                            if (this.stepHistory.size() >= 25) {\n"
        "                                object2 = this.stepHistory.iterator();\n"
        "                                while (object2.hasNext() && this.stepHistory.size() >= 10) {\n"
        "                                    object2.next();\n"
        "                                    object2.remove();\n"
        "                                }\n"
        "                            }\n"
    )
    new = (
        "                            if (this.stepHistory.size() >= 25) {\n"
        "                                java.util.Iterator stepIterator = this.stepHistory.iterator();\n"
        "                                while (stepIterator.hasNext() && this.stepHistory.size() >= 10) {\n"
        "                                    stepIterator.next();\n"
        "                                    stepIterator.remove();\n"
        "                                }\n"
        "                            }\n"
    )
    text, count = replace_exact(text, old, new, path)
    repairs += count
    old = (
        "        while (n5 < 14) {\n"
        "            object = nArray[n5];\n"
        "            if (this.entity.getPosition().getX() + n == object[0] && this.entity.getPosition().getY() + n2 == object[1]) {\n"
        "                bl = true;\n"
        "                break;\n"
        "            }\n"
        "            ++n5;\n"
        "        }\n"
    )
    new = (
        "        while (n5 < 14) {\n"
        "            int[] bypassTile = nArray[n5];\n"
        "            if (this.entity.getPosition().getX() + n == bypassTile[0] && this.entity.getPosition().getY() + n2 == bypassTile[1]) {\n"
        "                bl = true;\n"
        "                break;\n"
        "            }\n"
        "            ++n5;\n"
        "        }\n"
    )
    text, count = replace_exact(text, old, new, path)
    repairs += count
    path.write_text(text, encoding="utf-8")

    path = source_root / "com/rs2/model/skill/woodcutting/TreeDefinition.java"
    text = path.read_text(encoding="utf-8")
    start_marker = "    public static int[] collectObjectIds(TreeDefinition[] object) {\n"
    end_marker = "\n    public static TreeDefinition forEntNpcId(int n) {\n"
    method = """    public static int[] collectObjectIds(TreeDefinition[] treeDefinitions) {
        ArrayList<Integer> arrayList = new ArrayList<Integer>();
        for (TreeDefinition treeDefinition : treeDefinitions) {
            int[] nArray = treeDefinition.objectIds;
            int n = nArray.length;
            int n2 = 0;
            while (n2 < n) {
                int n3 = nArray[n2];
                arrayList.add(n3);
                ++n2;
            }
        }
        int[] objectIds = new int[arrayList.size()];
        int n = 0;
        while (n < objectIds.length) {
            objectIds[n] = arrayList.get(n);
            ++n;
        }
        return objectIds;
    }
"""
    try:
        start = text.index(start_marker)
        end = text.index(end_marker, start)
    except ValueError as exc:
        raise SystemExit(f"{path}: expected collectObjectIds region not found") from exc
    text = text[:start] + method + text[end:]
    repairs += 1
    path.write_text(text, encoding="utf-8")

    path = source_root / "com/rs2/model/skill/mining/MineableRockDefinition.java"
    text = path.read_text(encoding="utf-8")
    start_marker = "    public static int[] collectObjectIds(MineableRockDefinition[] object) {\n"
    end_marker = "\n    static /* synthetic */ int getOreItemId(MineableRockDefinition mineableRockDefinition) {\n"
    method = """    public static int[] collectObjectIds(MineableRockDefinition[] rockDefinitions) {
        ArrayList<Integer> arrayList = new ArrayList<Integer>();
        for (MineableRockDefinition rockDefinition : rockDefinitions) {
            int[] nArray = rockDefinition.objectIds;
            int n = nArray.length;
            int n2 = 0;
            while (n2 < n) {
                int n3 = nArray[n2];
                arrayList.add(n3);
                ++n2;
            }
        }
        int[] objectIds = new int[arrayList.size()];
        int n = 0;
        while (n < objectIds.length) {
            objectIds[n] = arrayList.get(n);
            ++n;
        }
        return objectIds;
    }
"""
    try:
        start = text.index(start_marker)
        end = text.index(end_marker, start)
    except ValueError as exc:
        raise SystemExit(f"{path}: expected collectObjectIds region not found") from exc
    text = text[:start] + method + text[end:]
    repairs += 1
    path.write_text(text, encoding="utf-8")

    path = source_root / "com/rs2/model/skill/fishing/FishingSpotDefinition.java"
    text = path.read_text(encoding="utf-8")
    old = (
        "    private FishingSpotDefinition(int[] nArray, int n2, int n3, int n4, int[] nArray2, int[] nArray3, double[] dArray, int[] nArray4, int[] nArray5) {\n"
        "        this.spotNpcIds = nArray;\n"
        "        this.toolItem = new ItemStack(n2);\n"
        "        this.baitItem = n3 == -1 ? null : new ItemStack(n3);\n"
        "        this.animationId = n4;\n"
        "        this.requiredLevels = nArray2;\n"
        "        itemStackArray = new ItemStack[nArray3.length];\n"
        "        n = 0;\n"
        "        while (n < nArray3.length) {\n"
        "            itemStackArray[n] = new ItemStack(nArray3[n]);\n"
        "            ++n;\n"
        "        }\n"
        "        this.catchItems = itemStackArray;\n"
        "        this.experienceRewards = dArray;\n"
        "        this.chanceLowValues = nArray4;\n"
        "        this.chanceHighValues = nArray5;\n"
        "    }\n"
    )
    new = (
        "    private FishingSpotDefinition(int[] nArray, int n2, int n3, int n4, int[] nArray2, int[] nArray3, double[] dArray, int[] nArray4, int[] nArray5) {\n"
        "        this.spotNpcIds = nArray;\n"
        "        this.toolItem = new ItemStack(n2);\n"
        "        this.baitItem = n3 == -1 ? null : new ItemStack(n3);\n"
        "        this.animationId = n4;\n"
        "        this.requiredLevels = nArray2;\n"
        "        ItemStack[] itemStackArray = new ItemStack[nArray3.length];\n"
        "        int n = 0;\n"
        "        while (n < nArray3.length) {\n"
        "            itemStackArray[n] = new ItemStack(nArray3[n]);\n"
        "            ++n;\n"
        "        }\n"
        "        this.catchItems = itemStackArray;\n"
        "        this.experienceRewards = dArray;\n"
        "        this.chanceLowValues = nArray4;\n"
        "        this.chanceHighValues = nArray5;\n"
        "    }\n"
    )
    text, count = replace_exact(text, old, new, path)
    repairs += count
    path.write_text(text, encoding="utf-8")

    path = source_root / "com/rs2/model/npc/NpcDefinition.java"
    text = path.read_text(encoding="utf-8")
    replacements = [
        (
            "    static /* synthetic */ int getAttackBonusTypeId(NpcDefinition npcDefinition) {\n",
            "    public static /* synthetic */ int getAttackBonusTypeId(NpcDefinition npcDefinition) {\n",
        ),
        (
            "    static /* synthetic */ int getDefaultMaxHit(NpcDefinition npcDefinition) {\n",
            "    public static /* synthetic */ int getDefaultMaxHit(NpcDefinition npcDefinition) {\n",
        ),
        (
            "    static /* synthetic */ int getDefaultAttackDelay(NpcDefinition npcDefinition) {\n",
            "    public static /* synthetic */ int getDefaultAttackDelay(NpcDefinition npcDefinition) {\n",
        ),
        (
            "    static /* synthetic */ int getDefaultAttackAnimationId(NpcDefinition npcDefinition) {\n",
            "    public static /* synthetic */ int getDefaultAttackAnimationId(NpcDefinition npcDefinition) {\n",
        ),
    ]
    for old, new in replacements:
        text, count = replace_exact(text, old, new, path)
        repairs += count
    path.write_text(text, encoding="utf-8")

    return repairs


def repair_additional_small_cluster_locals(source_root: Path) -> int:
    repairs = 0

    path = source_root / "com/rs2/model/player/PlayerGroup.java"
    text = path.read_text(encoding="utf-8")
    old = (
        "            object = this.members.iterator();\n"
        "            while (object.hasNext()) {\n"
        "                Player player3 = (Player)object.next();\n"
        "                if (this.deferredRemovedMembers.contains(player3)) {\n"
        "                    object.remove();\n"
        "                    continue;\n"
        "                }\n"
        "                player = player3;\n"
        "                player.packetSender.sendGameMessage(String.valueOf(this.leader.getUsername()) + \" is the new group leader.\");\n"
        "            }\n"
    )
    new = (
        "            Iterator memberIterator = this.members.iterator();\n"
        "            while (memberIterator.hasNext()) {\n"
        "                Player player3 = (Player)memberIterator.next();\n"
        "                if (this.deferredRemovedMembers.contains(player3)) {\n"
        "                    memberIterator.remove();\n"
        "                    continue;\n"
        "                }\n"
        "                player = player3;\n"
        "                player.packetSender.sendGameMessage(String.valueOf(this.leader.getUsername()) + \" is the new group leader.\");\n"
        "            }\n"
    )
    text, count = replace_exact(text, old, new, path)
    repairs += count
    old = (
        "            for (Player player2 : arrayList) {\n"
        "                object = arrayList3.iterator();\n"
        "                while (object.hasNext()) {\n"
        "                    Player player3 = (Player)object.next();\n"
        "                    object2 = player2;\n"
        "                    ((Player)object2).packetSender.sendGameMessage(String.valueOf(player3.getUsername()) + \" was too far away to roll for the loot.\");\n"
        "                }\n"
        "            }\n"
    )
    new = (
        "            for (Player player2 : arrayList) {\n"
        "                Iterator distantIterator = arrayList3.iterator();\n"
        "                while (distantIterator.hasNext()) {\n"
        "                    Player player3 = (Player)distantIterator.next();\n"
        "                    object2 = player2;\n"
        "                    ((Player)object2).packetSender.sendGameMessage(String.valueOf(player3.getUsername()) + \" was too far away to roll for the loot.\");\n"
        "                }\n"
        "            }\n"
    )
    text, count = replace_exact(text, old, new, path)
    repairs += count
    path.write_text(text, encoding="utf-8")

    path = source_root / "com/rs2/model/gameplay/partyroom/PartyRoomManager.java"
    text = path.read_text(encoding="utf-8")
    start_marker = "    public static boolean startBalloonBonanza(Player object) {\n"
    end_marker = "\n    public static boolean startNightlyDance(Player player) {\n"
    method = """    public static boolean startBalloonBonanza(Player player) {
        ArrayList<ItemStack> arrayList;
        int n;
        if (PartyRoomManager.hasActiveDropParty()) {
            return false;
        }
        if (player.getInventoryManager().containsItemAmount(995, 1000)) {
            player.getInventoryManager().removeItem(new ItemStack(995, 1000));
            balloonDropPending = true;
            balloonRewards.clear();
            ArrayList positions = new ArrayList();
            int n2 = 2730;
            while (n2 < 2745) {
                n = 3462;
                while (n < 3476) {
                    if (n != 3468 || n2 < 2735 || n2 > 2740) {
                        positions.add(new Position(n2, n));
                    }
                    ++n;
                }
                ++n2;
            }
            Collections.shuffle(positions);
            arrayList = new ArrayList<ItemStack>();
            n = 0;
            while (n < partyChestContainer.g()) {
                ItemStack itemStack;
                if (partyChestContainer.getItemAt(n) != null && (itemStack = partyChestContainer.getItemAt(n)) != null) {
                    arrayList.add(itemStack);
                }
                ++n;
            }
            Collections.shuffle(arrayList);
            n = 20;
            if (partyChestValue >= 50000 && partyChestValue < 150000) {
                n = 100;
            } else if (partyChestValue >= 150000 && partyChestValue < 1000000) {
                n = 500;
            } else if (partyChestValue >= 1000000) {
                n = 1000;
            }
            balloonDropTask = new PartyRoomBalloonSpawnTask(n, positions, arrayList);
            World.getTaskScheduler().schedule(balloonDropTask);
            return true;
        }
        return false;
    }
"""
    try:
        start = text.index(start_marker)
        end = text.index(end_marker, start)
    except ValueError as exc:
        raise SystemExit(f"{path}: expected startBalloonBonanza region not found") from exc
    text = text[:start] + method + text[end:]
    repairs += 1
    old = (
        "                object2 = partyRoomBalloonReward;\n"
        "                object = new GroundItem(new ItemStack(n8, n7), (Entity)object, ((PartyRoomBalloonReward)object2).balloonPosition);\n"
        "                GroundItemManager.getInstance().spawn((GroundItem)object);\n"
    )
    new = (
        "                object2 = partyRoomBalloonReward;\n"
        "                GroundItem groundItem = new GroundItem(new ItemStack(n8, n7), (Entity)object, ((PartyRoomBalloonReward)object2).balloonPosition);\n"
        "                GroundItemManager.getInstance().spawn(groundItem);\n"
    )
    text, count = replace_exact(text, old, new, path)
    repairs += count
    path.write_text(text, encoding="utf-8")

    path = source_root / "com/rs2/model/objects/WorldObjectLookup.java"
    text = path.read_text(encoding="utf-8")
    start_marker = "    public static LoadedWorldObject findObjectAt(int n, int n2, int n3) {\n"
    end_marker = "\n    public static LoadedWorldObject findObjectByIdAt(int n, int n2, int n3, int n4) {\n"
    method = """    public static LoadedWorldObject findObjectAt(int n, int n2, int n3) {
        Position position = new Position(n, n2, n3);
        World.getInstance().getObjectRegionIndex();
        AgilityObstacleHandler bucket = WorldObjectRegionIndex.getOrCreateRegionBucket(position);
        for (Object loadedObject : bucket.getLoadedObjects()) {
            LoadedWorldObject loadedWorldObject = (LoadedWorldObject)loadedObject;
            if (!loadedWorldObject.getPosition().equals(position)) continue;
            ObjectDefinition objectDefinition = ObjectDefinition.forId(loadedWorldObject.getWorldObject().getObjectId());
            if (loadedWorldObject.getType() == 0 && !objectDefinition.interactive || loadedWorldObject.getType() == 3 && !objectDefinition.interactive || loadedWorldObject.getType() == 4 && !objectDefinition.interactive || loadedWorldObject.getType() == 22 && !objectDefinition.interactive) continue;
            return loadedWorldObject;
        }
        return null;
    }
"""
    try:
        start = text.index(start_marker)
        end = text.index(end_marker, start)
    except ValueError as exc:
        raise SystemExit(f"{path}: expected findObjectAt region not found") from exc
    text = text[:start] + method + text[end:]
    repairs += 1
    start_marker = "    public static LoadedWorldObject findObjectByIdAt(int n, int n2, int n3, int n4) {\n"
    end_marker = "\n    public static LoadedWorldObject findObjectByNameAt(String string, int n, int n2, int n3) {\n"
    method = """    public static LoadedWorldObject findObjectByIdAt(int n, int n2, int n3, int n4) {
        Position position = new Position(n2, n3, n4);
        World.getInstance().getObjectRegionIndex();
        AgilityObstacleHandler bucket = WorldObjectRegionIndex.getOrCreateRegionBucket(position);
        for (Object loadedObject : bucket.getLoadedObjects()) {
            LoadedWorldObject loadedWorldObject = (LoadedWorldObject)loadedObject;
            if (loadedWorldObject.getWorldObject().getObjectId() != n || !loadedWorldObject.getPosition().equals(position)) continue;
            return loadedWorldObject;
        }
        return null;
    }
"""
    try:
        start = text.index(start_marker)
        end = text.index(end_marker, start)
    except ValueError as exc:
        raise SystemExit(f"{path}: expected findObjectByIdAt region not found") from exc
    text = text[:start] + method + text[end:]
    repairs += 1
    start_marker = "    public static LoadedWorldObject findObjectByNameAt(String string, int n, int n2, int n3) {\n"
    end_marker = "\n}\n"
    method = """    public static LoadedWorldObject findObjectByNameAt(String string, int n, int n2, int n3) {
        Position position = new Position(n, n2, n3);
        World.getInstance().getObjectRegionIndex();
        AgilityObstacleHandler bucket = WorldObjectRegionIndex.getOrCreateRegionBucket(position);
        for (Object loadedObject : bucket.getLoadedObjects()) {
            LoadedWorldObject loadedWorldObject = (LoadedWorldObject)loadedObject;
            String objectName;
            if (ObjectDefinition.forId(loadedWorldObject.getWorldObject().getObjectId()) != null) {
                ObjectDefinition objectDefinition = ObjectDefinition.forId(loadedWorldObject.getWorldObject().getObjectId());
                objectName = objectDefinition.name.toLowerCase();
            } else {
                objectName = "";
            }
            if (!objectName.contains(string.toLowerCase()) || !loadedWorldObject.getPosition().equals(position)) continue;
            return loadedWorldObject;
        }
        return null;
    }
"""
    try:
        start = text.index(start_marker)
        end = text.index(end_marker, start)
    except ValueError as exc:
        raise SystemExit(f"{path}: expected findObjectByNameAt region not found") from exc
    text = text[:start] + method + text[end:]
    repairs += 1
    path.write_text(text, encoding="utf-8")

    path = source_root / "com/rs2/model/skill/smithing/SmeltingHandler.java"
    text = path.read_text(encoding="utf-8")
    start_marker = "    public static boolean handleSmeltingButton(Player player, int n, int n2) {\n"
    end_marker = "\n    /*\n     * Enabled aggressive block sorting\n     */\n    private static boolean startSmeltingTask(Player player, int n) {\n"
    method = """    public static boolean handleSmeltingButton(Player player, int n, int n2) {
        if (player.interfaceAction != "smelt") {
            return false;
        }
        int[][] nArray = SMELTING_BUTTONS;
        int n3 = 0;
        while (n3 < 32) {
            int[] buttonDefinition = nArray[n3];
            if (n == buttonDefinition[0]) {
                player.setSelectedSmithingBarItemId(buttonDefinition[1]);
                if (buttonDefinition[2] == 28 && n2 <= 0) {
                    player.packetSender.sendEnterInputPrompt(n);
                    return true;
                }
                SmeltingHandler.startSmeltingTask(player, n2 <= 0 ? buttonDefinition[2] : n2);
                return true;
            }
            ++n3;
        }
        return false;
    }
"""
    try:
        start = text.index(start_marker)
        end = text.index(end_marker, start)
    except ValueError as exc:
        raise SystemExit(f"{path}: expected handleSmeltingButton region not found") from exc
    text = text[:start] + method + text[end:]
    repairs += 1
    old = (
        "                boolean bl = false;\n"
        "                int n8 = 0;\n"
        "                if (SMELTING_DEFINITIONS[n3].length > 2) {\n"
        "                    bl = SMELTING_DEFINITIONS[n3][2][0];\n"
        "                    n8 = SMELTING_DEFINITIONS[n3][2][1];\n"
        "                }\n"
        "                if (!SkillActionHelper.checkSkillRequirement(player, 13, n4, \"smelt this bar\")) {\n"
        "                    if (player.botEnabled) {\n"
        "                        player.currentBotTask.startWalkToBank(player);\n"
        "                    }\n"
        "                    return true;\n"
        "                }\n"
        "                n3 = player.nextActionSequence();\n"
        "                ItemStack itemStack = new ItemStack(n6, n7);\n"
        "                ItemStack itemStack2 = new ItemStack(bl ? 1 : 0, n8);\n"
        "                ItemStack itemStack3 = new ItemStack(n2);\n"
        "                boolean bl2 = bl = bl > false;\n"
    )
    new = (
        "                int secondaryOreId = 0;\n"
        "                int secondaryOreAmount = 0;\n"
        "                if (SMELTING_DEFINITIONS[n3].length > 2) {\n"
        "                    secondaryOreId = SMELTING_DEFINITIONS[n3][2][0];\n"
        "                    secondaryOreAmount = SMELTING_DEFINITIONS[n3][2][1];\n"
        "                }\n"
        "                if (!SkillActionHelper.checkSkillRequirement(player, 13, n4, \"smelt this bar\")) {\n"
        "                    if (player.botEnabled) {\n"
        "                        player.currentBotTask.startWalkToBank(player);\n"
        "                    }\n"
        "                    return true;\n"
        "                }\n"
        "                n3 = player.nextActionSequence();\n"
        "                ItemStack itemStack = new ItemStack(n6, n7);\n"
        "                ItemStack itemStack2 = new ItemStack(secondaryOreId, secondaryOreAmount);\n"
        "                ItemStack itemStack3 = new ItemStack(n2);\n"
        "                boolean bl = secondaryOreId > 0;\n"
    )
    text, count = replace_exact(text, old, new, path)
    repairs += count
    path.write_text(text, encoding="utf-8")

    return repairs


def repair_bot_task_route_and_required_item_locals(source_root: Path) -> int:
    def replace_method_region(text: str, start_marker: str, replacement_func, path: Path) -> tuple[str, int]:
        try:
            start = text.index(start_marker)
            brace = text.index("{", start)
        except ValueError as exc:
            raise SystemExit(f"{path}: expected method region not found: {start_marker!r}") from exc
        depth = 0
        end = None
        for index in range(brace, len(text)):
            char = text[index]
            if char == "{":
                depth += 1
            elif char == "}":
                depth -= 1
                if depth == 0:
                    end = index + 1
                    break
        if end is None:
            raise SystemExit(f"{path}: could not locate method end for {start_marker!r}")
        method = text[start:end]
        new_method = replacement_func(method)
        if new_method == method:
            raise SystemExit(f"{path}: method transform made no changes for {start_marker!r}")
        return text[:start] + new_method + text[end:], 1

    repairs = 0

    for relative_path in (
        "com/rs2/bot/tasks/AlKharidFlyFishingBotTask.java",
        "com/rs2/bot/tasks/BarbarianVillageFlyFishingBotTask.java",
        "com/rs2/bot/tasks/DraynorNetFishingBotTask.java",
        "com/rs2/bot/tasks/DraynorSheepShearingBotTask.java",
        "com/rs2/bot/tasks/FaladorWineOfZamorakTelegrabBotTask.java",
        "com/rs2/bot/tasks/LeatherCraftingBotTask.java",
        "com/rs2/bot/tasks/VarrockSteelDaggerSmithingBotTask.java",
        "com/rs2/bot/tasks/WizardsTowerLesserDemonMagicBotTask.java",
    ):
        path = source_root / relative_path
        text = path.read_text(encoding="utf-8")

        def transform_required_items(method: str) -> str:
            method = method.replace("    public final ArrayList getRequiredItems(Player object) {\n", "    public final ArrayList getRequiredItems(Player player) {\n")
            method = method.replace("        object = new ArrayList<ItemStack>();\n", "        ArrayList<ItemStack> requiredItems = new ArrayList<ItemStack>();\n")
            method = method.replace("        ((ArrayList)object).add(", "        requiredItems.add(")
            method = method.replace("        return object;\n", "        return requiredItems;\n")
            return method

        text, count = replace_method_region(
            text,
            "    public final ArrayList getRequiredItems(Player object) {\n",
            transform_required_items,
            path,
        )
        repairs += count
        path.write_text(text, encoding="utf-8")

    for relative_path in (
        "com/rs2/bot/tasks/DwarvenMineBotTask.java",
        "com/rs2/bot/tasks/DwarvenMineDwarfCombatBotTask.java",
        "com/rs2/bot/tasks/EdgevilleDungeonBrassKeyBotTask.java",
        "com/rs2/bot/tasks/EdgevilleDungeonHillGiantCombatBotTask.java",
        "com/rs2/bot/tasks/MindRuneRunecraftingBotTask.java",
        "com/rs2/bot/tasks/MiningGuildMineBotTask.java",
        "com/rs2/bot/tasks/VarrockRuneEssenceMiningBotTask.java",
    ):
        path = source_root / relative_path
        text = path.read_text(encoding="utf-8")

        def transform_route_method(method: str) -> str:
            method = method.replace("    public final void advanceTaskRouteSegment(Player player, boolean n) {\n", "    public final void advanceTaskRouteSegment(Player player, boolean continuing) {\n")
            method = method.replace('player.botTaskState.equals("walk to task") && n != 0', 'player.botTaskState.equals("walk to task") && continuing')
            method = method.replace('player.botTaskState.equals("walk to bank") && n != 0', 'player.botTaskState.equals("walk to bank") && continuing')
            method = method.replace("if (n == 0)", "if (!continuing)")
            method = method.replace(
                "            n = GameUtil.getRegionId(player.getPosition().getX(), player.getPosition().getY());\n",
                "            int regionId = GameUtil.getRegionId(player.getPosition().getX(), player.getPosition().getY());\n",
            )
            method = method.replace("&& n ==", "&& regionId ==")
            method = method.replace("&& n !=", "&& regionId !=")
            return method

        text, count = replace_method_region(
            text,
            "    public final void advanceTaskRouteSegment(Player player, boolean n) {\n",
            transform_route_method,
            path,
        )
        repairs += count
        path.write_text(text, encoding="utf-8")

    return repairs


def repair_misc_contained_type_flow_locals(source_root: Path) -> int:
    repairs = 0

    path = source_root / "com/rs2/model/task/CycleEventHandler.java"
    text = path.read_text(encoding="utf-8")
    old = (
        "    public final CycleEventContainer schedule(Entity entity, CycleEvent object, int n) {\n"
        "        object = new CycleEventContainer(entity, (CycleEvent)object, n);\n"
        "        if (entity.getIndex() == -1) {\n"
        "            return object;\n"
        "        }\n"
        "        this.pendingEvents.add(object);\n"
        "        return object;\n"
        "    }\n"
    )
    new = (
        "    public final CycleEventContainer schedule(Entity entity, CycleEvent cycleEvent, int n) {\n"
        "        CycleEventContainer cycleEventContainer = new CycleEventContainer(entity, cycleEvent, n);\n"
        "        if (entity.getIndex() == -1) {\n"
        "            return cycleEventContainer;\n"
        "        }\n"
        "        this.pendingEvents.add(cycleEventContainer);\n"
        "        return cycleEventContainer;\n"
        "    }\n"
    )
    text, count = replace_exact(text, old, new, path)
    repairs += count
    path.write_text(text, encoding="utf-8")

    path = source_root / "com/rs2/model/gameplay/duel/DuelController.java"
    text = path.read_text(encoding="utf-8")
    old = (
        "    public final void resetDuel(boolean n) {\n"
        "        if (n != 0) {\n"
        "            n = 0;\n"
        "            while (n < this.player.getDuelSession().getStakedItems().size()) {\n"
        "                this.player.getInventoryManager().addItem((ItemStack)this.player.getDuelSession().getStakedItems().get(n));\n"
        "                ++n;\n"
        "            }\n"
        "        }\n"
    )
    new = (
        "    public final void resetDuel(boolean returnStakedItems) {\n"
        "        if (returnStakedItems) {\n"
        "            int n = 0;\n"
        "            while (n < this.player.getDuelSession().getStakedItems().size()) {\n"
        "                this.player.getInventoryManager().addItem((ItemStack)this.player.getDuelSession().getStakedItems().get(n));\n"
        "                ++n;\n"
        "            }\n"
        "        }\n"
    )
    text, count = replace_exact(text, old, new, path)
    repairs += count
    path.write_text(text, encoding="utf-8")

    path = source_root / "com/rs2/LanDiscoveryListener.java"
    text = path.read_text(encoding="utf-8")
    start_marker = "    public final void run() {\n"
    end_marker = "\n}\n"
    method = """    public final void run() {
        try {
            while (LanDiscoveryService.running) {
                LanDiscoveryService.getSocket().receive(this.packet);
                String request = new String(this.packet.getData()).trim();
                if (!request.equals(LanDiscoveryService.requestMessage)) continue;
                byte[] responseBytes = LanDiscoveryService.responseMessage.getBytes();
                DatagramPacket responsePacket = new DatagramPacket(responseBytes, responseBytes.length, this.packet.getAddress(), this.packet.getPort());
                LanDiscoveryService.getSocket().send(responsePacket);
                byte[] reservedBytes = LanDiscoveryService.d.getBytes();
                byte[] maxBytes = LanDiscoveryService.c.getBytes();
                String hostName = InetAddress.getLocalHost().getHostName();
                if (reservedBytes.length > maxBytes.length) {
                    hostName = "invalid";
                }
                byte[] hostBytes = hostName.getBytes();
                DatagramPacket hostPacket = new DatagramPacket(hostBytes, hostBytes.length, this.packet.getAddress(), this.packet.getPort());
                LanDiscoveryService.getSocket().send(hostPacket);
            }
            return;
        }
        catch (Exception exception) {
            exception.printStackTrace();
            return;
        }
    }
"""
    try:
        start = text.index(start_marker)
        end = text.index(end_marker, start)
    except ValueError as exc:
        raise SystemExit(f"{path}: expected run region not found") from exc
    text = text[:start] + method + text[end:]
    repairs += 1
    path.write_text(text, encoding="utf-8")

    path = source_root / "com/rs2/LanDiscoveryService.java"
    text = path.read_text(encoding="utf-8")
    old = """    static /* synthetic */ DatagramSocket getSocket() {
        if (socket == null) {
            socket = new DatagramSocket(8002, InetAddress.getByName("0.0.0.0"));
            socket.setBroadcast(true);
        }
        return socket;
    }
"""
    new = """    static /* synthetic */ DatagramSocket getSocket() {
        if (socket == null) {
            try {
                socket = new DatagramSocket(8002, InetAddress.getByName("0.0.0.0"));
                socket.setBroadcast(true);
            }
            catch (Exception exception) {
                exception.printStackTrace();
                throw new IllegalStateException(exception);
            }
        }
        return socket;
    }
"""
    text, count = replace_exact(text, old, new, path)
    repairs += count
    path.write_text(text, encoding="utf-8")

    path = source_root / "com/rs2/model/combat/requirement/EquipmentItemRequirement.java"
    text = path.read_text(encoding="utf-8")
    old = (
        "    final boolean isSatisfiedBy(Entity object) {\n"
        "        if (!((Entity)object).isPlayer()) {\n"
        "            return true;\n"
        "        }\n"
        "        object = (Player)object;\n"
        "        if ((object = ((Player)object).getEquipmentManager().getContainer().getItems()[this.equipmentSlot]) == null) {\n"
        "            return false;\n"
        "        }\n"
        "        return ((ItemStack)object).getId() == this.itemId && ((ItemStack)object).getAmount() >= this.amount;\n"
        "    }\n"
    )
    new = (
        "    final boolean isSatisfiedBy(Entity entity) {\n"
        "        if (!entity.isPlayer()) {\n"
        "            return true;\n"
        "        }\n"
        "        Player player = (Player)entity;\n"
        "        ItemStack itemStack = player.getEquipmentManager().getContainer().getItems()[this.equipmentSlot];\n"
        "        if (itemStack == null) {\n"
        "            return false;\n"
        "        }\n"
        "        return itemStack.getId() == this.itemId && itemStack.getAmount() >= this.amount;\n"
        "    }\n"
    )
    text, count = replace_exact(text, old, new, path)
    repairs += count
    path.write_text(text, encoding="utf-8")

    path = source_root / "com/rs2/model/item/action/BarrowsRepairHandler.java"
    text = path.read_text(encoding="utf-8")
    old = (
        "    public static boolean repairItem(Player object, ItemStack itemStack) {\n"
        "        int n = BarrowsRepairHandler.calculateRepairCost(itemStack);\n"
        "        if (!((Player)object).getInventoryManager().containsItemStack(new ItemStack(995, n)) && n > 0) {\n"
        "            ((Player)object).packetSender.sendGameMessage(\"You don't have enough coins to fix that.\");\n"
        "            return false;\n"
        "        }\n"
        "        ((Player)object).getInventoryManager().removeItem(new ItemStack(995, n));\n"
        "        ((Player)object).getInventoryManager().removeItemFromSlot(itemStack, ((Player)object).getSelectedItemSlot());\n"
        "        InventoryManager inventoryManager = ((Player)object).getInventoryManager();\n"
        "        object = itemStack;\n"
        "        object = BarrowsRepairHandler.forItem((ItemStack)object);\n"
        "        inventoryManager.addItem(new ItemStack(object == null ? -1 : ((BarrowsRepairHandler)object).repairedItemId, 1));\n"
        "        return true;\n"
        "    }\n"
    )
    new = (
        "    public static boolean repairItem(Player player, ItemStack itemStack) {\n"
        "        int n = BarrowsRepairHandler.calculateRepairCost(itemStack);\n"
        "        if (!player.getInventoryManager().containsItemStack(new ItemStack(995, n)) && n > 0) {\n"
        "            player.packetSender.sendGameMessage(\"You don't have enough coins to fix that.\");\n"
        "            return false;\n"
        "        }\n"
        "        player.getInventoryManager().removeItem(new ItemStack(995, n));\n"
        "        player.getInventoryManager().removeItemFromSlot(itemStack, player.getSelectedItemSlot());\n"
        "        InventoryManager inventoryManager = player.getInventoryManager();\n"
        "        BarrowsRepairHandler repairDefinition = BarrowsRepairHandler.forItem(itemStack);\n"
        "        inventoryManager.addItem(new ItemStack(repairDefinition == null ? -1 : repairDefinition.repairedItemId, 1));\n"
        "        return true;\n"
        "    }\n"
    )
    text, count = replace_exact(text, old, new, path)
    repairs += count
    path.write_text(text, encoding="utf-8")

    path = source_root / "com/rs2/model/skill/fishing/FishingSpotManager.java"
    text = path.read_text(encoding="utf-8")
    text, count = replace_exact(text, "                npc.a(spotPositions[n][n2]);\n", "                npc.moveTo(spotPositions[n][n2]);\n", path)
    repairs += count
    old = (
        "    static boolean isSpotAtPosition(Position object, FishingSpotDefinition fishingSpotDefinition) {\n"
        "        if ((object = (Npc)activeSpotsByPosition.get(object)) != null) {\n"
        "            int[] nArray = fishingSpotDefinition.getSpotNpcIds();\n"
        "            int n = nArray.length;\n"
        "            int n2 = 0;\n"
        "            while (n2 < n) {\n"
        "                int n3 = nArray[n2];\n"
        "                if (((Npc)object).getDefinition().getId() == n3) {\n"
        "                    return true;\n"
        "                }\n"
        "                ++n2;\n"
        "            }\n"
        "        }\n"
        "        return false;\n"
        "    }\n"
    )
    new = (
        "    static boolean isSpotAtPosition(Position position, FishingSpotDefinition fishingSpotDefinition) {\n"
        "        Npc npc = (Npc)activeSpotsByPosition.get(position);\n"
        "        if (npc != null) {\n"
        "            int[] nArray = fishingSpotDefinition.getSpotNpcIds();\n"
        "            int n = nArray.length;\n"
        "            int n2 = 0;\n"
        "            while (n2 < n) {\n"
        "                int n3 = nArray[n2];\n"
        "                if (npc.getDefinition().getId() == n3) {\n"
        "                    return true;\n"
        "                }\n"
        "                ++n2;\n"
        "            }\n"
        "        }\n"
        "        return false;\n"
        "    }\n"
    )
    text, count = replace_exact(text, old, new, path)
    repairs += count
    path.write_text(text, encoding="utf-8")

    path = source_root / "com/rs2/model/skill/smithing/SmithingHandler.java"
    text = path.read_text(encoding="utf-8")
    start_marker = "    public static void startSmithingTask(Player player, int n, int n2) {\n"
    end_marker = "\n    public SmithingHandler(CombatAttack combatAttack, CombatAttackState combatAttackState) {\n"
    method = """    public static void startSmithingTask(Player player, int n, int n2) {
        SmithingBarDefinition smithingBarDefinition = player.getSelectedSmithingBarDefinition();
        if (smithingBarDefinition == null) {
            return;
        }
        int productItemId = n;
        SmithableItemDefinition smithableItemDefinition = null;
        SmithableItemDefinition[] smithableItems = smithingBarDefinition.getSmithableItems();
        int count = smithableItems.length;
        int index = 0;
        while (index < count) {
            SmithableItemDefinition candidate = smithableItems[index];
            if (candidate != null && candidate.getProductItemId() == productItemId) {
                smithableItemDefinition = candidate;
                break;
            }
            ++index;
        }
        if (smithableItemDefinition != null) {
            if (!ItemDefinition.isDefined(smithableItemDefinition.getProductItemId())) {
                return;
            }
            ItemStack itemStack = new ItemStack(n);
            if (itemStack.getDefinition().isMembersOnly()) {
                if (!player.isMember()) {
                    player.packetSender.sendGameMessage("You need a members account to access members content.");
                    return;
                }
                if (ServerSettings.freeToPlayWorld) {
                    player.packetSender.sendGameMessage("You need to be in members world to access members content.");
                    return;
                }
            }
            String string = itemStack.getDefinition().getName().toLowerCase();
            if (!SkillActionHelper.checkSkillRequirement(player, 13, smithableItemDefinition.getRequiredLevel(), "smith " + string)) {
                return;
            }
            if (!player.getInventoryManager().getContainer().containsItem(2347)) {
                player.packetSender.sendGameMessage("You need a hammer to smith on an anvil.");
                if (player.botEnabled) {
                    player.currentBotTask.startWalkToBank(player);
                }
                return;
            }
            if (player.getQuestState(0) != 1 && itemStack.getId() != 1205) {
                player.packetSender.sendGameMessage("You can only smith daggers here.");
                return;
            }
            ItemStack itemStack2 = new ItemStack(player.getSelectedSmithingBarItemId(), smithableItemDefinition.getBarCount());
            if (!player.getInventoryManager().containsItemStack(itemStack2)) {
                player.packetSender.sendGameMessage("You need at least " + smithableItemDefinition.getBarCount() + " bars to make " + string + ".");
                if (player.botEnabled) {
                    player.currentBotTask.startWalkToBank(player);
                }
                return;
            }
            player.packetSender.closeInterfaces();
            int n6 = player.nextActionSequence();
            player.packetSender.sendSoundEffect(468, 1, 0);
            player.getUpdateState().setAnimation(898);
            player.setActiveCycleEvent(new SmithingTask(n2, player, n6, itemStack2, n, smithableItemDefinition, smithingBarDefinition, itemStack));
            CycleEventHandler.getInstance().schedule(player, player.getActiveCycleEvent(), 5);
        }
    }
"""
    try:
        start = text.index(start_marker)
        end = text.index(end_marker, start)
    except ValueError as exc:
        raise SystemExit(f"{path}: expected startSmithingTask region not found") from exc
    text = text[:start] + method + text[end:]
    repairs += 1
    path.write_text(text, encoding="utf-8")

    return repairs


def repair_more_misc_small_clusters(source_root: Path) -> int:
    repairs = 0

    path = source_root / "com/rs2/model/quest/QuestHook.java"
    text = path.read_text(encoding="utf-8")
    old = (
        "    public static int calculateProjectileTravelTicks(Position position, Position object) {\n"
        "        int n = GameUtil.getDistance(position, (Position)object);\n"
        "        object = ProjectileTiming.d;\n"
        "        double d = (double)(((ProjectileTiming)object).getStartDelay() + ((ProjectileTiming)object).getSpeed()) + (double)n * 5.0;\n"
        "        d = Math.ceil(d * 12.0 / 600.0);\n"
        "        if (n > 1) {\n"
        "            d += 1.0;\n"
        "        }\n"
        "        n = 0 + (int)d;\n"
        "        return n;\n"
        "    }\n"
    )
    new = (
        "    public static int calculateProjectileTravelTicks(Position position, Position targetPosition) {\n"
        "        int n = GameUtil.getDistance(position, targetPosition);\n"
        "        ProjectileTiming projectileTiming = ProjectileTiming.d;\n"
        "        double d = (double)(projectileTiming.getStartDelay() + projectileTiming.getSpeed()) + (double)n * 5.0;\n"
        "        d = Math.ceil(d * 12.0 / 600.0);\n"
        "        if (n > 1) {\n"
        "            d += 1.0;\n"
        "        }\n"
        "        n = 0 + (int)d;\n"
        "        return n;\n"
        "    }\n"
    )
    text, count = replace_exact(text, old, new, path)
    repairs += count
    path.write_text(text, encoding="utf-8")

    path = source_root / "com/rs2/model/travel/HajedyCartRoute.java"
    text = path.read_text(encoding="utf-8")
    old = (
        "    private HajedyCartRoute(int n2) {\n"
        "        this.destination = (Position)n2;\n"
        "        this.fareCoins = 0;\n"
        "    }\n"
    )
    new = (
        "    private HajedyCartRoute(Position destination, int fareCoins, int unused) {\n"
        "        this.destination = destination;\n"
        "        this.fareCoins = fareCoins;\n"
        "    }\n"
    )
    text, count = replace_exact(text, old, new, path)
    repairs += count
    path.write_text(text, encoding="utf-8")

    path = source_root / "com/rs2/model/randomevent/sandwichlady/SandwichLadyManager.java"
    text = path.read_text(encoding="utf-8")
    start_marker = "    public final boolean handleButtonClick(int n) {\n"
    end_marker = "\n    static /* synthetic */ Player getPlayer(SandwichLadyManager sandwichLadyManager) {\n"
    method = """    public final boolean handleButtonClick(int n) {
        Player player = this.player;
        if (player.H == null) {
            return false;
        }
        this.player.getSandwichLadyManager();
        player = this.player;
        SandwichLadyRewardSet rewardSet = SandwichLadyManager.getRewardSetForNpcId(player.H.getNpcId());
        if (rewardSet == null) {
            return false;
        }
        if (rewardSet.getButtonIds().contains(n)) {
            if (n == (Integer)rewardSet.getButtonIds().get(this.player.getSandwichLadyManager().selectedOfferIndex)) {
                ++this.player.getSandwichLadyManager().correctSelectionCount;
            } else {
                rewardSet.punishWrongChoice(this.player);
            }
            if (this.player.getSandwichLadyManager().correctSelectionCount == 1) {
                player = this.player;
                Npc npc = player.H;
                SandwichLadyManager sandwichLadyManager = this.player.getSandwichLadyManager();
                SandwichLadyRewardSet sandwichLadyRewardSet = SandwichLadyManager.getRewardSetForNpcId(npc.getNpcId());
                GameUtil.randomInclusive(6);
                player = sandwichLadyManager.player;
                player.packetSender.closeInterfaces();
                Player cfr_ignored_0 = sandwichLadyManager.player;
                npc.getUpdateState().setForcedTextAndMarkUpdated(sandwichLadyRewardSet.getMessages()[0]);
                npc.getUpdateState().setAnimation(863);
                sandwichLadyManager.player.getSandwichLadyManager();
                sandwichLadyManager.player.getInventoryManager().addItem(sandwichLadyRewardSet.getRewards()[sandwichLadyManager.selectedOfferIndex]);
                CycleEventHandler.getInstance().schedule(sandwichLadyManager.player, new SandwichLadyCleanupEvent(sandwichLadyManager, npc), 5);
            }
        }
        return true;
    }
"""
    try:
        start = text.index(start_marker)
        end = text.index(end_marker, start)
    except ValueError as exc:
        raise SystemExit(f"{path}: expected handleButtonClick region not found") from exc
    text = text[:start] + method + text[end:]
    repairs += 1
    path.write_text(text, encoding="utf-8")

    path = source_root / "com/rs2/model/randomevent/RandomEventManager.java"
    text = path.read_text(encoding="utf-8")
    start_marker = "    public static void spawnRandomEventNpc(Player player, RandomEventNpcDefinition stringArray) {\n"
    end_marker = "\n    public static boolean handleLampButtonClick(Player player, int n) {\n"
    method = """    public static void spawnRandomEventNpc(Player player, RandomEventNpcDefinition randomEventNpcDefinition) {
        if (player.H != null) {
            return;
        }
        Npc npc = new Npc(randomEventNpcDefinition.npcId);
        player.setActiveRandomEventNpc(npc);
        GameplayHelper.spawnOwnedNpcAdjacentToPlayer(player, npc, false, false);
        player.packetSender.sendStillGraphic(86, npc.getPosition(), 0x640000);
        player.packetSender.sendSoundEffect(300, 1, 0);
        int ignorePenaltyNpcId = randomEventNpcDefinition.ignorePenaltyNpcId;
        String[] reminderLines = randomEventNpcDefinition.reminderLines;
        CycleEventHandler.getInstance().schedule(npc, new RandomEventNpcReminderEvent(player, npc, reminderLines, ignorePenaltyNpcId), 1);
    }
"""
    try:
        start = text.index(start_marker)
        end = text.index(end_marker, start)
    except ValueError as exc:
        raise SystemExit(f"{path}: expected spawnRandomEventNpc region not found") from exc
    text = text[:start] + method + text[end:]
    repairs += 1
    path.write_text(text, encoding="utf-8")

    path = source_root / "com/rs2/model/skill/mining/MiningTask.java"
    text = path.read_text(encoding="utf-8")
    old = (
        "        ObjectManager.getInstance();\n"
        "        String[] stringArray = ObjectManager.findDynamicObjectAt(this.x, this.y, MiningManager.getPlayer(this.manager).getPosition().getPlane());\n"
        "        if (stringArray != null && stringArray.getWorldObject().getObjectId() != this.rockObjectId) {\n"
        "            if (MiningManager.getPlayer(this.manager).getQuestState(0) != 1) {\n"
        "                MiningManager.getPlayer(this.manager).getDialogueManager().showOneLineStatement(\"There is no more ore in this rock.\");\n"
        "                MiningManager.getPlayer(this.manager).setInteractionTargetId(0);\n"
        "            }\n"
        "            stringArray = MiningManager.getPlayer(this.manager);\n"
        "            stringArray.packetSender.sendGameMessage(\"There is no more ore in this rock.\");\n"
        "            stringArray = MiningManager.getPlayer(this.manager);\n"
        "            stringArray.packetSender.sendSoundEffect(429, 1, 0);\n"
    )
    new = (
        "        ObjectManager.getInstance();\n"
        "        DynamicObject dynamicObject2 = ObjectManager.findDynamicObjectAt(this.x, this.y, MiningManager.getPlayer(this.manager).getPosition().getPlane());\n"
        "        if (dynamicObject2 != null && dynamicObject2.getWorldObject().getObjectId() != this.rockObjectId) {\n"
        "            if (MiningManager.getPlayer(this.manager).getQuestState(0) != 1) {\n"
        "                MiningManager.getPlayer(this.manager).getDialogueManager().showOneLineStatement(\"There is no more ore in this rock.\");\n"
        "                MiningManager.getPlayer(this.manager).setInteractionTargetId(0);\n"
        "            }\n"
        "            Player player2 = MiningManager.getPlayer(this.manager);\n"
        "            player2.packetSender.sendGameMessage(\"There is no more ore in this rock.\");\n"
        "            player2 = MiningManager.getPlayer(this.manager);\n"
        "            player2.packetSender.sendSoundEffect(429, 1, 0);\n"
    )
    text, count = replace_exact(text, old, new, path)
    repairs += count
    text, count = replace_exact(
        text,
        "            stringArray = new String[]{\"1/157\", \"1/344\", \"1/688\", \"1/1376\", \"1/5504\"};\n",
        "            String[] stringArray = new String[]{\"1/157\", \"1/344\", \"1/688\", \"1/1376\", \"1/5504\"};\n",
        path,
    )
    repairs += count
    path.write_text(text, encoding="utf-8")

    return repairs


def repair_special_attack_definition_locals(source_root: Path) -> int:
    repairs = 0
    special_dir = source_root / "com/rs2/model/combat/special"
    argument_overrides = {
        "DarkBowSpecialDefinition": "this, player, entity, weaponProfile, weaponProfile, player",
        "DragonHalberdSpecialDefinition": "this, player, entity, weaponProfile, entity",
        "MagicLongbowSpecialDefinition": "this, player, entity, weaponProfile, weaponProfile, player",
        "MagicShortbowSpecialDefinition": "this, player, entity, weaponProfile, weaponProfile, player",
        "RuneThrownaxeSpecialDefinition": "this, player, entity, weaponProfile, player, entity, weaponProfile",
        "SeercullSpecialDefinition": "this, player, entity, weaponProfile, weaponProfile, player",
    }
    null_attack_definitions = {"DragonBattleaxeSpecialDefinition", "ExcaliburSpecialDefinition"}

    def replace_optional(text: str, old: str, new: str) -> tuple[str, int]:
        count = text.count(old)
        if count == 0:
            return text, 0
        return text.replace(old, new), count

    for path in special_dir.glob("*SpecialDefinition.java"):
        class_name = path.stem
        if class_name == "SpecialAttackDefinition":
            continue
        text = path.read_text(encoding="utf-8")
        old_constructor = (
            f"    public {class_name}(int n2, String ... stringArray) {{\n"
            "    }\n"
        )
        new_constructor = (
            f"    public {class_name}(int n2, String ... stringArray) {{\n"
            "        super(n2, stringArray);\n"
            "    }\n"
        )
        text, count = replace_optional(text, old_constructor, new_constructor)
        repairs += count

        if class_name not in null_attack_definitions:
            attack_class = class_name.replace("SpecialDefinition", "SpecialAttack")
            arguments = argument_overrides.get(class_name, "this, player, entity, weaponProfile")
            text, count = replace_optional(
                text,
                "    public final WeaponCombatAttack createAttack(Player object, Entity entity, WeaponProfile weaponProfile) {\n",
                "    public final WeaponCombatAttack createAttack(Player player, Entity entity, WeaponProfile weaponProfile) {\n",
            )
            repairs += count
            old_body = (
                f"        object = new {attack_class}(this, (Player)object, entity, weaponProfile);\n"
                "        return object;\n"
            )
            if class_name in argument_overrides:
                if class_name in {"DarkBowSpecialDefinition", "MagicLongbowSpecialDefinition", "MagicShortbowSpecialDefinition", "SeercullSpecialDefinition"}:
                    old_body = (
                        f"        object = new {attack_class}(this, (Player)object, entity, weaponProfile, weaponProfile, (Player)object);\n"
                        "        return object;\n"
                    )
                elif class_name == "DragonHalberdSpecialDefinition":
                    old_body = (
                        f"        object = new {attack_class}(this, (Player)object, entity, weaponProfile, entity);\n"
                        "        return object;\n"
                    )
                elif class_name == "RuneThrownaxeSpecialDefinition":
                    old_body = (
                        f"        object = new {attack_class}(this, (Player)object, entity, weaponProfile, (Player)object, entity, weaponProfile);\n"
                        "        return object;\n"
                    )
            new_body = f"        return new {attack_class}({arguments});\n"
            text, count = replace_optional(text, old_body, new_body)
            repairs += count

        path.write_text(text, encoding="utf-8")

    return repairs


def repair_am_subclass_constructor(source_root: Path) -> int:
    path = source_root / "com/rs2/model/c/aN.java"
    text = path.read_text(encoding="utf-8")
    if "    public aN() {\n" in text:
        return 0
    for marker in ("public final class aN\n", "final class aN\n"):
        start = text.find(marker)
        if start != -1:
            break
    else:
        raise SystemExit(f"{path}: expected aN class header not found")
    replacement = """public final class aN
extends aM {
    public aN() {
        super("a", 0, (byte)0);
    }
}
"""
    text = text[:start] + replacement
    path.write_text(text, encoding="utf-8")
    return 1


def repair_runecrafting_handler_locals(source_root: Path) -> int:
    path = source_root / "com/rs2/model/skill/runecrafting/RunecraftingHandler.java"
    text = path.read_text(encoding="utf-8")
    repairs = 0

    def replace_method(start_marker: str, end_marker: str, method: str) -> None:
        nonlocal text, repairs
        try:
            start = text.index(start_marker)
            end = text.index(end_marker, start)
        except ValueError as exc:
            raise SystemExit(f"{path}: expected runecrafting method region not found") from exc
        text = text[:start] + method + text[end:]
        repairs += 1

    replace_method(
        "    public static void craftRunesAtAltar(Player player, RuneDefinition object) {\n",
        "\n    public static void recordScryingOrbTeleport(Player player) {\n",
        """    public static void craftRunesAtAltar(Player player, RuneDefinition runeDefinition) {
        if (!ServerSettings.runecraftingEnabled) {
            player.packetSender.sendGameMessage("This skill is currently disabled.");
            return;
        }
        if (player.getQuestState(14) != 1) {
            QuestDefinition questDefinition = QuestDefinition.forId(14);
            String string = questDefinition.getName();
            player.packetSender.sendGameMessage("You need to complete " + string + " to do this.");
            return;
        }
        if (!SkillActionHelper.checkSkillRequirement(player, 20, runeDefinition.getRequiredLevel(), "craft this rune")) {
            return;
        }
        int n = player.getInventoryManager().getItemAmount(1436);
        int n2 = player.getInventoryManager().getItemAmount(PURE_ESSENCE_ITEM_ID);
        double d = runeDefinition.getMultipleRunesLevelInterval() < 0 ? 0 : player.getSkillManager().getBaseLevel(20) / runeDefinition.getMultipleRunesLevelInterval();
        int n3 = (int)Math.floor(d) + 1;
        if (runeDefinition.getRuneItemId() >= 560) {
            if (n2 <= 0) {
                if (PURE_ESSENCE_ITEM_ID != 1436) {
                    player.packetSender.sendGameMessage("You need pure essence to make these kinds of runes.");
                } else {
                    player.packetSender.sendGameMessage("You need rune essence to make runes.");
                }
                if (player.botEnabled) {
                    player.currentBotTask.startWalkToBank(player);
                }
                return;
            }
            player.getInventoryManager().removeItem(new ItemStack(PURE_ESSENCE_ITEM_ID, n2));
            player.getInventoryManager().addItem(new ItemStack(runeDefinition.getRuneItemId(), n2 * n3));
            player.getSkillManager().addExperience(20, runeDefinition.getExperience() * (double)n2);
        } else {
            if (n2 <= 0 && n <= 0) {
                if (PURE_ESSENCE_ITEM_ID != 1436) {
                    player.packetSender.sendGameMessage("You need rune or pure essence to make these kinds of runes.");
                } else {
                    player.packetSender.sendGameMessage("You need rune essence to make runes.");
                }
                if (player.botEnabled) {
                    player.currentBotTask.startWalkToBank(player);
                }
                return;
            }
            int n4 = 0;
            while (n4 < 28) {
                player.getInventoryManager().removeItem(new ItemStack(1436, n));
                if (PURE_ESSENCE_ITEM_ID != 1436) {
                    player.getInventoryManager().removeItem(new ItemStack(PURE_ESSENCE_ITEM_ID, n2));
                }
                ++n4;
            }
            player.getInventoryManager().addItem(new ItemStack(runeDefinition.getRuneItemId(), n * n3));
            player.getSkillManager().addExperience(20, runeDefinition.getExperience() * (double)n);
            if (PURE_ESSENCE_ITEM_ID != 1436) {
                player.getInventoryManager().addItem(new ItemStack(runeDefinition.getRuneItemId(), n2 * n3));
                player.getSkillManager().addExperience(20, runeDefinition.getExperience() * (double)n2);
            }
            if (player.botEnabled) {
                player.currentBotTask.startWalkToBank(player);
            }
        }
        player.packetSender.sendSoundEffect(481, 1, 0);
        player.getUpdateState().setAnimation(791);
        player.getUpdateState().setGraphicHeight100(186);
    }
""",
    )
    replace_method(
        "    public static void startAbyssMageTeleport(Player player, Npc object) {\n",
        "\n    public static boolean locateTalismanDirection(Player player, int n) {\n",
        """    public static void startAbyssMageTeleport(Player player, Npc npc) {
        if (player.getQuestState(14) != 1) {
            QuestDefinition questDefinition = QuestDefinition.forId(14);
            String string = questDefinition.getName();
            player.packetSender.sendGameMessage("You need to complete " + string + " to do this.");
            return;
        }
        player.setAbyssMageNpcId(npc.getNpcId());
        npc.startAbyssMageTeleport(player, 2911, 4832, 0, "Senventior disthine molenko!");
    }
""",
    )
    replace_method(
        "    public static boolean locateTalismanDirection(Player player, int n) {\n",
        "\n}\n",
        """    public static boolean locateTalismanDirection(Player player, int n) {
        RunecraftingAltarDefinition altarDefinition = null;
        for (RunecraftingAltarDefinition candidate : RunecraftingAltarDefinition.values()) {
            if (n == candidate.talismanItemId) {
                altarDefinition = candidate;
                break;
            }
        }
        if (altarDefinition == null) {
            return false;
        }
        int n2 = altarDefinition.ruinsPosition.getY();
        int n4 = altarDefinition.ruinsPosition.getX();
        String string = "";
        String string2 = "";
        if (player.getPosition().getX() >= n4) {
            string = "west";
        }
        if (player.getPosition().getY() > n2) {
            string2 = "South";
        }
        if (player.getPosition().getX() < n4) {
            string = "east";
        }
        if (player.getPosition().getY() <= n2) {
            string2 = "North";
        }
        player.packetSender.sendGameMessage("You feel a slight pull towards " + string2 + "-" + string + "...");
        return true;
    }
""",
    )
    path.write_text(text, encoding="utf-8")
    return repairs


def repair_runecrafting_object_handler_locals(source_root: Path) -> int:
    path = source_root / "com/rs2/model/skill/runecrafting/RunecraftingObjectHandler.java"
    text = path.read_text(encoding="utf-8")
    repairs = 0

    def replace_method(start_marker: str, end_marker: str, method: str) -> None:
        nonlocal text, repairs
        try:
            start = text.index(start_marker)
            end = text.index(end_marker, start)
        except ValueError as exc:
            raise SystemExit(f"{path}: expected runecrafting object method region not found") from exc
        text = text[:start] + method + text[end:]
        repairs += 1

    replace_method(
        "    public static boolean handleRuinsOrPortalObject(Player player, int n) {\n",
        "\n    public static boolean handleTalismanOnMysteriousRuins(Player player, int n, int n2) {\n",
        """    public static boolean handleRuinsOrPortalObject(Player player, int n) {
        RunecraftingAltarDefinition ruinsDefinition = null;
        for (RunecraftingAltarDefinition candidate : RunecraftingAltarDefinition.values()) {
            if (n == candidate.ruinsObjectId) {
                ruinsDefinition = candidate;
                break;
            }
        }
        RunecraftingAltarDefinition portalDefinition = null;
        for (RunecraftingAltarDefinition candidate : RunecraftingAltarDefinition.values()) {
            if (n == candidate.portalObjectId) {
                portalDefinition = candidate;
                break;
            }
        }
        if (ruinsDefinition == null && portalDefinition == null) {
            return false;
        }
        if (!ServerSettings.runecraftingEnabled) {
            player.packetSender.sendGameMessage("This skill is currently disabled.");
            return true;
        }
        if (ruinsDefinition != null) {
            if (ruinsDefinition.membersOnly && !player.isMember()) {
                player.packetSender.sendGameMessage("You need a members account to access members content.");
                return true;
            }
            if (ruinsDefinition.membersOnly && ServerSettings.freeToPlayWorld) {
                player.packetSender.sendGameMessage("You need to be in members world to access members content.");
                return true;
            }
        }
        if (player.getQuestState(14) != 1) {
            QuestDefinition questDefinition = QuestDefinition.forId(14);
            String string = questDefinition.getName();
            player.packetSender.sendGameMessage("You need to complete " + string + " to do this.");
            return true;
        }
        if (ruinsDefinition != null) {
            player.moveTo(ruinsDefinition.altarPosition);
            player.packetSender.sendGameMessage("You feel a powerful force take hold of you...");
            return true;
        }
        if (portalDefinition != null) {
            player.moveTo(portalDefinition.ruinsPosition);
            player.packetSender.sendGameMessage("You step through the portal...");
            return true;
        }
        return false;
    }
""",
    )
    replace_method(
        "    public static boolean handleTalismanOnMysteriousRuins(Player player, int n, int n2) {\n",
        "\n    public RunecraftingObjectHandler(Player player) {\n",
        """    public static boolean handleTalismanOnMysteriousRuins(Player player, int n, int n2) {
        RunecraftingAltarDefinition altarDefinition = null;
        for (RunecraftingAltarDefinition candidate : RunecraftingAltarDefinition.values()) {
            if (n == candidate.talismanItemId && n2 == candidate.ruinsObjectId) {
                altarDefinition = candidate;
                break;
            }
        }
        if (altarDefinition == null) {
            return false;
        }
        if (!ServerSettings.runecraftingEnabled) {
            player.packetSender.sendGameMessage("This skill is currently disabled.");
            return true;
        }
        if (altarDefinition.membersOnly && !player.isMember()) {
            player.packetSender.sendGameMessage("You need a members account to access members content.");
            return true;
        }
        if (altarDefinition.membersOnly && ServerSettings.freeToPlayWorld) {
            player.packetSender.sendGameMessage("You need to be in members world to access members content.");
            return true;
        }
        if (player.getQuestState(14) != 1) {
            QuestDefinition questDefinition = QuestDefinition.forId(14);
            String string = questDefinition.getName();
            player.packetSender.sendGameMessage("You need to complete " + string + " to do this.");
            return true;
        }
        PacketSender packetSender = player.packetSender;
        StringBuilder stringBuilder = new StringBuilder("You hold the ");
        ItemService.getInstance();
        packetSender.sendGameMessage(stringBuilder.append(ItemService.getItemName(altarDefinition.talismanItemId)).append(" towards the mysterious ruins.").toString());
        player.getUpdateState().setAnimation(827);
        player.setActionLocked(true);
        CycleEventHandler.getInstance().schedule(player, new MysteriousRuinsTeleportTask(player, altarDefinition), 2);
        return true;
    }
""",
    )
    path.write_text(text, encoding="utf-8")
    return repairs


def repair_farming_patch_manager_locals(source_root: Path) -> int:
    replacements_by_file = {
        "com/rs2/model/skill/farming/AllotmentPatchManager.java": [
            ("object.getGrowthCycleTicks()", "((AllotmentCropDefinition)object).getGrowthCycleTicks()"),
            ("object.getGrowthStageCount()", "((AllotmentCropDefinition)object).getGrowthStageCount()"),
            ("object.getIndex()", "((AllotmentPatch)object).getIndex()"),
            ("object.getBaseContainerItemId()", "((CropStorageDefinition)object).getBaseContainerItemId()"),
            ("CropStorageDefinition.isSack(object)", "CropStorageDefinition.isSack((CropStorageDefinition)object)"),
            ("cropStorageDefinition2 = object;", "cropStorageDefinition2 = (CropStorageDefinition)object;"),
            ("cropStorageDefinition.getProduceItemId()", "cropStorageDefinition2.getProduceItemId()"),
            ("cropStorageDefinition.getBaseContainerItemId()", "cropStorageDefinition2.getBaseContainerItemId()"),
            ("cropStorageDefinition.isSack()", "cropStorageDefinition2.isSack()"),
        ],
        "com/rs2/model/skill/farming/FlowerPatchManager.java": [
            ("object.getGrowthCycleTicks()", "((FlowerDefinition)object).getGrowthCycleTicks()"),
            ("object.getGrowthStageCount()", "((FlowerDefinition)object).getGrowthStageCount()"),
            ("object.getIndex()", "((FlowerPatch)object).getIndex()"),
        ],
        "com/rs2/model/skill/farming/HopsPatchManager.java": [
            ("object.getGrowthCycleTicks()", "((HopsDefinition)object).getGrowthCycleTicks()"),
            ("object.getGrowthStageCount()", "((HopsDefinition)object).getGrowthStageCount()"),
            ("object.getIndex()", "((HopsPatch)object).getIndex()"),
        ],
        "com/rs2/model/skill/farming/TreePatchManager.java": [
            ("object.getGrowthCycleTicks()", "((FarmedTreeDefinition)object).getGrowthCycleTicks()"),
            ("object.getIndex()", "((TreePatch)object).getIndex()"),
        ],
        "com/rs2/model/skill/farming/FruitTreePatchManager.java": [
            ("object.getGrowthCycleTicks()", "((FruitTreeDefinition)object).getGrowthCycleTicks()"),
            ("object.getIndex()", "((FruitTreePatch)object).getIndex()"),
        ],
        "com/rs2/model/skill/farming/SpecialTreePatchManager.java": [
            ("object.getGrowthCycleTicks()", "((SpecialTreeDefinition)object).getGrowthCycleTicks()"),
            ("object.getIndex()", "((SpecialTreePatch)object).getIndex()"),
        ],
        "com/rs2/model/skill/farming/SpecialCropPatchManager.java": [
            ("object.getGrowthCycleTicks()", "((SpecialCropDefinition)object).getGrowthCycleTicks()"),
            ("object.getIndex()", "((SpecialCropPatch)object).getIndex()"),
        ],
    }
    repairs = 0
    for relative_path, replacements in replacements_by_file.items():
        path = source_root / relative_path
        text = path.read_text(encoding="utf-8")
        for old, new in replacements:
            count = text.count(old)
            if count:
                text = text.replace(old, new)
                repairs += count
        path.write_text(text, encoding="utf-8")
    return repairs


def repair_cutscene_and_abyss_locals(source_root: Path) -> int:
    repairs = 0

    path = source_root / "com/rs2/model/cutscene/Cutscene.java"
    text = path.read_text(encoding="utf-8")

    def replace_cutscene_method(start_marker: str, end_marker: str, method: str) -> None:
        nonlocal text, repairs
        try:
            start = text.index(start_marker)
            end = text.index(end_marker, start)
        except ValueError as exc:
            raise SystemExit(f"{path}: expected cutscene method region not found") from exc
        text = text[:start] + method + text[end:]
        repairs += 1

    replace_cutscene_method(
        "    public Cutscene(Player player, ArrayList object) {\n",
        "\n    public final void addStep(CutsceneStep cutsceneStep) {\n",
        """    public Cutscene(Player player, ArrayList object) {
        this.player = player;
        if (object != null) {
            for (Object npcObject : object) {
                this.npcs.add((Npc)npcObject);
            }
        }
        CutsceneStep sceneSetupStep = new CutsceneSceneSetupStep(this, this, 4, player);
        CutsceneStep dialogueStartStep = new CutsceneDialogueStartStep(this, this, 1, player);
        this.addStep(sceneSetupStep);
        this.addStep(dialogueStartStep);
        this.addCustomSteps();
    }
""",
    )
    replace_cutscene_method(
        "    public final void startCutscene() {\n",
        "\n    private int getMovementLockDurationMillis() {\n",
        """    public final void startCutscene() {
        this.player.cutsceneActive = true;
        PacketSender packetSender = this.player.packetSender;
        packetSender.sendMinimapState(2);
        packetSender.showInterface(8677);
        int[] sidebarInterfaces = new int[]{QuestConstants.COMBAT_TAB_INTERFACE[0], QuestConstants.STATS_TAB_INTERFACE[0], QuestConstants.QUEST_TAB_INTERFACE[0], QuestConstants.INVENTORY_TAB_INTERFACE[0], QuestConstants.EQUIPMENT_TAB_INTERFACE[0], QuestConstants.PRAYER_TAB_INTERFACE[0], QuestConstants.MAGIC_TAB_INTERFACE[0], QuestConstants.OPTIONS_TAB_INTERFACE[0], QuestConstants.EMOTES_TAB_INTERFACE[0]};
        int n = 0;
        while (n < 9) {
            packetSender.setSidebarInterface(sidebarInterfaces[n], -1);
            ++n;
        }
        this.player.getMovementLockTimer().setDelayTicks(this.getMovementLockDurationMillis());
        if (this.npcs != null) {
            for (Object npcObject : this.npcs) {
                ((Npc)npcObject).getMovementLockTimer().setDelayTicks(this.getMovementLockDurationMillis());
            }
        }
        int n2 = 0;
        for (Object stepObject : this.steps) {
            CutsceneStep cutsceneStep = (CutsceneStep)stepObject;
            n2 += cutsceneStep.getDelayTicks();
            World.getTaskScheduler().schedule(new CutsceneStepTask(this, n2, cutsceneStep));
        }
        World.getTaskScheduler().schedule(new CutsceneEndTask(this, this.getTotalDelayTicks()));
    }
""",
    )
    text = text.replace(
        "        for (CutsceneStep cutsceneStep : this.steps) {\n",
        "        for (Object stepObject : this.steps) {\n"
        "            CutsceneStep cutsceneStep = (CutsceneStep)stepObject;\n",
    )
    repairs += 2
    path.write_text(text, encoding="utf-8")

    path = source_root / "com/rs2/model/gameplay/abyss/AbyssManager.java"
    text = path.read_text(encoding="utf-8")

    def replace_abyss_method(start_marker: str, end_marker: str, method: str) -> None:
        nonlocal text, repairs
        try:
            start = text.index(start_marker)
            end = text.index(end_marker, start)
        except ValueError as exc:
            raise SystemExit(f"{path}: expected abyss method region not found") from exc
        text = text[:start] + method + text[end:]
        repairs += 1

    replace_abyss_method(
        "    public static void rollAbyssPouchDrop(Player object, Npc npc) {\n",
        "\n    public static boolean handleAbyssObjectAction(Player player, int n, int n2, int n3) {\n",
        """    public static void rollAbyssPouchDrop(Player player, Npc npc) {
        int missingPouchItemId = AbyssManager.getMissingPouchItemId(player);
        if (missingPouchItemId == -1) {
            return;
        }
        if ((npc.getDefinition().getName().equalsIgnoreCase("leech") || npc.getDefinition().getName().equalsIgnoreCase("guardian") || npc.getDefinition().getName().equalsIgnoreCase("walker")) && GameUtil.randomInclusive(100) == 5) {
            GroundItem groundItem = new GroundItem(new ItemStack(missingPouchItemId), player, npc.getPosition());
            GroundItemManager.getInstance().spawn(groundItem);
        }
    }
""",
    )
    replace_abyss_method(
        "    public static void sendObstacleSuccessMessage(Player player, String object) {\n",
        "\n    private static void playObstacleAttemptAnimation(Player player, String object) {\n",
        """    public static void sendObstacleSuccessMessage(Player player, String object) {
        if (object == "chopTendrils") {
            player.packetSender.sendGameMessage("...and manage to chop down the tendrils.");
            return;
        }
        if (object == "mineRock") {
            player.packetSender.sendGameMessage("...and manage to break through the rock.");
            return;
        }
        if (object == "crossGap") {
            player.packetSender.sendGameMessage("...and you manage to crawl through.");
            player.getUpdateState().setAnimation(1332);
            return;
        }
        if (object == "blockade") {
            player.packetSender.sendGameMessage("...and manage to burn it down and get past.");
            return;
        }
        if (object == "distractEye") {
            player.packetSender.sendGameMessage("...and sneak past while they're not looking.");
        }
    }
""",
    )
    replace_abyss_method(
        "    private static void playObstacleAttemptAnimation(Player player, String object) {\n",
        "\n    private static void attemptMineRockObstacle(Player player, int n, int n2, int n3, int n4) {\n",
        """    private static void playObstacleAttemptAnimation(Player player, String object) {
        if (object == "mineRock") {
            player.getUpdateState().setAnimation(ItemCombinationHandler.findUsableGatheringTool(player, 14).getGatherAnimationId());
            return;
        }
        if (object == "chopTendrils") {
            player.getUpdateState().setAnimation(ItemCombinationHandler.findUsableGatheringTool(player, 8).getGatherAnimationId());
            return;
        }
        if (object == "blockade") {
            player.getUpdateState().setAnimation(733);
            return;
        }
        if (object == "distractEye") {
            int[] animations = new int[]{855, 856, 857, 858, 859, 860, 861, 862, 863, 864, 865, 866, 2113, 2109, 2111, 2106, 2107, 2108, 1368, 2105, 2110, 2112, 2127, 2128, 1131, 1130, 1129, 1128, 1745, 3544, 3543, 2836};
            int n = new Random().nextInt(32);
            player.getUpdateState().setAnimation(animations[n]);
            return;
        }
        if (object == "crossGap") {
            player.getUpdateState().setAnimation(1331);
        }
    }
""",
    )
    path.write_text(text, encoding="utf-8")

    return repairs


def repair_item_container_raw_collection_locals(source_root: Path) -> int:
    path = source_root / "com/rs2/model/item/ItemContainer.java"
    text = path.read_text(encoding="utf-8")
    repairs = 0

    replacements = [
        (
            "        Object object = (ItemContainerTab)this.tabs.get(n);\n"
            "        object = ((ItemContainerTab)object).items.iterator();\n"
            "        while (object.hasNext()) {\n"
            "            ItemStack itemStack = (ItemStack)object.next();\n"
            "            if (itemStack == null) {\n"
            "                object.remove();\n"
            "                continue;\n"
            "            }\n"
            "            if (itemStack.getId() != -1) continue;\n"
            "            object.remove();\n"
            "        }\n",
            "        ItemContainerTab itemContainerTab = (ItemContainerTab)this.tabs.get(n);\n"
            "        Iterator iterator = itemContainerTab.items.iterator();\n"
            "        while (iterator.hasNext()) {\n"
            "            ItemStack itemStack = (ItemStack)iterator.next();\n"
            "            if (itemStack == null) {\n"
            "                iterator.remove();\n"
            "                continue;\n"
            "            }\n"
            "            if (itemStack.getId() != -1) continue;\n"
            "            iterator.remove();\n"
            "        }\n",
        ),
        (
            "                for (Object object3 : ((ItemContainerTab)object2).items) {\n"
            "                    if (object3 == null || ((ItemStack)object3).getId() == -1) continue;\n",
            "                for (Object itemObject : ((ItemContainerTab)object2).items) {\n"
            "                    if (itemObject == null || ((ItemStack)itemObject).getId() == -1) continue;\n",
        ),
        (
            "        return arrayList.toArray(new ItemStack[0]);\n",
            "        return (ItemStack[])arrayList.toArray(new ItemStack[0]);\n",
        ),
        (
            "            for (ItemContainerTab itemContainerTab : this.tabs) {\n",
            "            for (Object itemContainerTabObject : this.tabs) {\n"
            "                ItemContainerTab itemContainerTab = (ItemContainerTab)itemContainerTabObject;\n",
        ),
        (
            "                for (ItemStack itemStack : itemContainerTab.items) {\n",
            "                for (Object itemStackObject : itemContainerTab.items) {\n"
            "                    ItemStack itemStack = (ItemStack)itemStackObject;\n",
        ),
        (
            "    private void notifySlotsUpdated(int[] object) {\n"
            "        object = this.updateListeners.iterator();\n"
            "        while (object.hasNext()) {\n"
            "            object.next();\n"
            "        }\n"
            "    }\n",
            "    private void notifySlotsUpdated(int[] object) {\n"
            "        Iterator iterator = this.updateListeners.iterator();\n"
            "        while (iterator.hasNext()) {\n"
            "            iterator.next();\n"
            "        }\n"
            "    }\n",
        ),
    ]
    for old, new in replacements:
        count = text.count(old)
        if count:
            text = text.replace(old, new)
            repairs += count
    path.write_text(text, encoding="utf-8")
    return repairs


def repair_object_manager_raw_collection_locals(source_root: Path) -> int:
    path = source_root / "com/rs2/model/objects/ObjectManager.java"
    text = path.read_text(encoding="utf-8")
    repairs = 0

    replacements = [
        (
            "        for (DynamicObject dynamicObject : activeDynamicObjects) {\n",
            "        for (Object dynamicObjectObject : activeDynamicObjects) {\n"
            "            DynamicObject dynamicObject = (DynamicObject)dynamicObjectObject;\n",
        ),
        (
            "        for (DynamicObject dynamicObject : pendingRemovalObjects) {\n",
            "        for (Object dynamicObjectObject : pendingRemovalObjects) {\n"
            "            DynamicObject dynamicObject = (DynamicObject)dynamicObjectObject;\n",
        ),
        (
            "        for (DynamicObject dynamicObject : player.visibleDynamicObjects) {\n",
            "        for (Object visibleDynamicObjectObject : player.visibleDynamicObjects) {\n"
            "            DynamicObject dynamicObject = (DynamicObject)visibleDynamicObjectObject;\n",
        ),
        (
            "            for (DynamicObject dynamicObject2 : activeDynamicObjects) {\n",
            "            for (Object dynamicObject2Object : activeDynamicObjects) {\n"
            "                DynamicObject dynamicObject2 = (DynamicObject)dynamicObject2Object;\n",
        ),
        (
            "        for (DynamicObject dynamicObject : player.pendingDynamicObjectRemovals) {\n",
            "        for (Object pendingDynamicObjectObject : player.pendingDynamicObjectRemovals) {\n"
            "            DynamicObject dynamicObject = (DynamicObject)pendingDynamicObjectObject;\n",
        ),
        (
            "            for (DynamicObject dynamicObject2 : player.visibleDynamicObjects) {\n",
            "            for (Object dynamicObject2Object : player.visibleDynamicObjects) {\n"
            "                DynamicObject dynamicObject2 = (DynamicObject)dynamicObject2Object;\n",
        ),
    ]
    for old, new in replacements:
        count = text.count(old)
        if count:
            text = text.replace(old, new)
            repairs += count

    start_marker = "    public final void addDynamicObject(DynamicObject object, boolean n) {\n"
    end_marker = "\n    public static void prepareObjectInteractionMovement(Player player, int n, int n2, int n3) {\n"
    method = """    public final void addDynamicObject(DynamicObject dynamicObject, boolean updateCollision) {
        if (ObjectManager.findDynamicObjectAt(dynamicObject.getWorldObject().getPosition().getX(), dynamicObject.getWorldObject().getPosition().getY(), dynamicObject.getWorldObject().getPosition().getPlane()) == null) {
            activeDynamicObjects.add(dynamicObject);
            if (updateCollision) {
                if (dynamicObject.getWorldObject().getObjectId() > 0 && dynamicObject.getWorldObject().getObjectId() != ServerSettings.placeholderObjectId && !FiremakingHandler.isFireObjectId(dynamicObject.getWorldObject().getObjectId()) && !MithrilSeedFlowerHandler.isMithrilSeedFlowerObjectId(dynamicObject.getWorldObject().getObjectId())) {
                    ObjectManager.restoreObjectCollision(dynamicObject.restoreObjectId, dynamicObject.getWorldObject().getPosition().getX(), dynamicObject.getWorldObject().getPosition().getY(), dynamicObject.getWorldObject().getPosition().getPlane(), dynamicObject.getWorldObject().getOrientation(), dynamicObject.getWorldObject().getType());
                } else {
                    ObjectManager.removeObjectCollision(dynamicObject.restoreObjectId, dynamicObject.getWorldObject().getPosition().getX(), dynamicObject.getWorldObject().getPosition().getY(), dynamicObject.getWorldObject().getPosition().getPlane(), dynamicObject.getWorldObject().getOrientation(), dynamicObject.getWorldObject().getType());
                }
            }
            Player[] playerArray = World.getPlayers();
            int n3 = playerArray.length;
            int n2 = 0;
            while (n2 < n3) {
                Player player = playerArray[n2];
                if (player != null && player.getPosition().getPlane() == dynamicObject.getWorldObject().getPosition().getPlane() && GameUtil.isWithinDistance(dynamicObject.getWorldObject().getPosition().getX(), dynamicObject.getWorldObject().getPosition().getY(), player.getPosition().getX(), player.getPosition().getY(), 60)) {
                    player.packetSender.sendObjectCreate(dynamicObject.getWorldObject().getObjectId(), dynamicObject.getWorldObject().getPosition().getX(), dynamicObject.getWorldObject().getPosition().getY(), dynamicObject.getWorldObject().getPosition().getPlane(), dynamicObject.getWorldObject().getOrientation(), dynamicObject.getWorldObject().getType());
                    player.visibleDynamicObjects.add(dynamicObject);
                }
                ++n2;
            }
        }
    }
"""
    try:
        start = text.index(start_marker)
        end = text.index(end_marker, start)
    except ValueError as exc:
        raise SystemExit(f"{path}: expected addDynamicObject region not found") from exc
    text = text[:start] + method + text[end:]
    repairs += 1

    path.write_text(text, encoding="utf-8")
    return repairs


def repair_direct_path_strategy_locals(source_root: Path) -> int:
    path = source_root / "com/rs2/model/path/DirectPathStrategy.java"
    text = path.read_text(encoding="utf-8")
    start_marker = "    public final PathResult buildPath(Entity object, Position position, boolean bl) {\n"
    end_marker = "\n}\n"
    method = """    public final PathResult buildPath(Entity entity, Position position, boolean bl) {
        PathResult pathResult = new PathResult();
        Position currentPosition = entity.getPosition().copy();
        Position previousPosition = currentPosition.copy();
        int n = position.getX() - currentPosition.getX();
        int n2 = position.getY() - currentPosition.getY();
        int n3 = 0;
        int n4 = 0;
        int n5 = 0;
        int n6 = 0;
        if (n < 0) {
            n3 = -1;
        } else if (n > 0) {
            n3 = 1;
        }
        if (n2 < 0) {
            n4 = -1;
        } else if (n2 > 0) {
            n4 = 1;
        }
        if (n < 0) {
            n5 = -1;
        } else if (n > 0) {
            n5 = 1;
        }
        int n7 = Math.abs(n);
        int n8 = Math.abs(n2);
        if (n7 <= n8) {
            n7 = Math.abs(n2);
            n8 = Math.abs(n);
            if (n2 < 0) {
                n6 = -1;
            } else if (n2 > 0) {
                n6 = 1;
            }
            n5 = 0;
        }
        n2 = n7 >> 1;
        n = 0;
        while (n <= n7) {
            n2 += n8;
            if (!previousPosition.equals(currentPosition) && bl && !GameUtil.hasClearPath(currentPosition, previousPosition, true)) {
                return pathResult;
            }
            previousPosition = currentPosition.copy();
            pathResult.getSteps().add(new PathStep(currentPosition.getX(), currentPosition.getY()));
            if (n2 >= n7) {
                n2 -= n7;
                currentPosition.setX(currentPosition.getX() + n3);
                currentPosition.setY(currentPosition.getY() + n4);
            } else {
                currentPosition.setX(currentPosition.getX() + n5);
                currentPosition.setY(currentPosition.getY() + n6);
            }
            ++n;
        }
        return pathResult;
    }
"""
    try:
        start = text.index(start_marker)
        end = text.index(end_marker, start)
    except ValueError as exc:
        raise SystemExit(f"{path}: expected buildPath region not found") from exc
    text = text[:start] + method + text[end:]
    path.write_text(text, encoding="utf-8")
    return 1


def repair_path_finder_find_path_locals(source_root: Path) -> int:
    path = source_root / "com/rs2/util/path/PathFinder.java"
    text = path.read_text(encoding="utf-8")
    start_marker = "    public static boolean findPath(Player player, int n, int n2, boolean n3, int n4, int n5) {\n"
    end_marker = "\n}\n"
    try:
        start = text.index(start_marker)
        end = text.index(end_marker, start)
    except ValueError as exc:
        raise SystemExit(f"{path}: expected findPath region not found") from exc
    method = text[start:end]
    method = method.replace(
        "    public static boolean findPath(Player player, int n, int n2, boolean n3, int n4, int n5) {\n",
        "    public static boolean findPath(Player player, int n, int n2, boolean bl, int n4, int n5) {\n",
    )
    method = method.replace(
        "        if (n == player.getPosition().getLocalX() && n2 == player.getPosition().getLocalY() && n3 == 0) {\n",
        "        if (n == player.getPosition().getLocalX() && n2 == player.getPosition().getLocalY() && !bl) {\n",
    )
    method = method.replace(
        "            if (n3 != 0) {\n",
        "            if (bl) {\n",
    )
    method = method.replace(
        "                n3 = n - 10;\n"
        "                while (n3 <= n + 10) {\n"
        "                    n12 = n2 - 10;\n"
        "                    while (n12 <= n2 + 10) {\n"
        "                        if (n3 >= 0 && n12 >= 0 && n3 < 104 && n12 < 104 && nArray2[n3][n12] < 100) {\n"
        "                            n11 = 0;\n"
        "                            if (n3 < n) {\n"
        "                                n11 = n - n3;\n"
        "                            } else if (n3 > n + n4 - 1) {\n"
        "                                n11 = n3 - (n + n4 - 1);\n"
        "                            }\n"
        "                            n6 = 0;\n"
        "                            if (n12 < n2) {\n"
        "                                n6 = n2 - n12;\n"
        "                            } else if (n12 > n2 + n5 - 1) {\n"
        "                                n6 = n12 - (n2 + n5 - 1);\n"
        "                            }\n"
        "                            n11 = n11 * n11 + n6 * n6;\n"
        "                            if (n11 < n8 || n11 == n8 && nArray2[n3][n12] < n7) {\n"
        "                                n8 = n11;\n"
        "                                n7 = nArray2[n3][n12];\n"
        "                                n10 = n3;\n"
        "                                n9 = n12;\n"
        "                            }\n"
        "                        }\n"
        "                        ++n12;\n"
        "                    }\n"
        "                    ++n3;\n"
        "                }\n",
        "                int n13 = n - 10;\n"
        "                while (n13 <= n + 10) {\n"
        "                    n12 = n2 - 10;\n"
        "                    while (n12 <= n2 + 10) {\n"
        "                        if (n13 >= 0 && n12 >= 0 && n13 < 104 && n12 < 104 && nArray2[n13][n12] < 100) {\n"
        "                            n11 = 0;\n"
        "                            if (n13 < n) {\n"
        "                                n11 = n - n13;\n"
        "                            } else if (n13 > n + n4 - 1) {\n"
        "                                n11 = n13 - (n + n4 - 1);\n"
        "                            }\n"
        "                            n6 = 0;\n"
        "                            if (n12 < n2) {\n"
        "                                n6 = n2 - n12;\n"
        "                            } else if (n12 > n2 + n5 - 1) {\n"
        "                                n6 = n12 - (n2 + n5 - 1);\n"
        "                            }\n"
        "                            n11 = n11 * n11 + n6 * n6;\n"
        "                            if (n11 < n8 || n11 == n8 && nArray2[n13][n12] < n7) {\n"
        "                                n8 = n11;\n"
        "                                n7 = nArray2[n13][n12];\n"
        "                                n10 = n13;\n"
        "                                n9 = n12;\n"
        "                            }\n"
        "                        }\n"
        "                        ++n12;\n"
        "                    }\n"
        "                    ++n13;\n"
        "                }\n",
    )
    method = method.replace(
        "        n3 = (player.getPosition().getRegionY() << 3) + (Integer)linkedList2.get(n11);\n"
        "        player.getMovementQueue().addStep(new Position(n6, n3));\n",
        "        int n13 = (player.getPosition().getRegionY() << 3) + (Integer)linkedList2.get(n11);\n"
        "        player.getMovementQueue().addStep(new Position(n6, n13));\n",
    )
    method = method.replace(
        "            n3 = (player.getPosition().getRegionY() << 3) + (Integer)linkedList2.get(n11);\n"
        "            player.getMovementQueue().addStep(new Position(n6, n3));\n",
        "            n13 = (player.getPosition().getRegionY() << 3) + (Integer)linkedList2.get(n11);\n"
        "            player.getMovementQueue().addStep(new Position(n6, n13));\n",
    )
    text = text[:start] + method + text[end:]
    path.write_text(text, encoding="utf-8")
    return 1


def repair_spell_definition_constructor_delegates(source_root: Path) -> int:
    path = source_root / "com/rs2/model/skill/magic/SpellDefinition.java"
    text = path.read_text(encoding="utf-8")
    count = text.count("        this(string, n, ")
    if count:
        text = text.replace("        this(string, n, ", "        this(")
        path.write_text(text, encoding="utf-8")
    return count


def repair_ammunition_definition_locals(source_root: Path) -> int:
    path = source_root / "com/rs2/model/combat/AmmunitionDefinition.java"
    text = path.read_text(encoding="utf-8")
    repairs = 0

    def replace_method(start_marker: str, end_marker: str, method: str) -> None:
        nonlocal text, repairs
        try:
            start = text.index(start_marker)
            end = text.index(end_marker, start)
        except ValueError as exc:
            raise SystemExit(f"{path}: expected ammunition method region not found") from exc
        text = text[:start] + method + text[end:]
        repairs += 1

    replace_method(
        "    public static boolean isCompatible(Player object, ItemStack itemStack, ItemStack itemStack2) {\n",
        "\n    public static AmmunitionDefinition findEquippedAmmunition(Player player, WeaponProfile object, boolean bl) {\n",
        """    public static boolean isCompatible(Player player, ItemStack itemStack, ItemStack itemStack2) {
        WeaponProfile weaponProfile = WeaponProfile.forItem(itemStack);
        AmmunitionProfile ammunitionProfile = weaponProfile.getAmmunitionProfile();
        if (ammunitionProfile == null) {
            return false;
        }
        if (itemStack2 == null) {
            return false;
        }
        if (itemStack == null) {
            return false;
        }
        int n = ammunitionProfile.getEquipmentSlot();
        String string = ItemDefinition.forId(itemStack2.getId()).getName().toLowerCase().replaceAll(" ", "_").replaceAll("\\\\(", "").replaceAll("\\\\)", "");
        AmmunitionDefinition[] ammunitionDefinitionArray = ammunitionProfile.getAllowedAmmunition();
        int n2 = ammunitionDefinitionArray.length;
        int n3 = 0;
        while (n3 < n2) {
            AmmunitionDefinition ammunitionDefinition = ammunitionDefinitionArray[n3];
            boolean bl = false;
            if (weaponProfile == WeaponProfile.METAL_CROSSBOW) {
                bl = true;
            }
            if (string.contains(ammunitionDefinition.name().toLowerCase()) && !bl || bl && string.startsWith(ammunitionDefinition.name().toLowerCase())) {
                if (n != 3) {
                    int n4;
                    if (string.contains("ogre")) {
                        String weaponProfileName = player.getWeaponProfile().name().toLowerCase();
                        n4 = weaponProfileName.contains("ogre") ? 0 : 1;
                    } else {
                        n4 = new ItemStack(itemStack.getId()).getDefinition().getRequiredLevel(4);
                        int n5 = new ItemStack(itemStack2.getId()).getDefinition().getRequiredLevel(4);
                        n4 = n5 > n4 ? 1 : 0;
                    }
                    if (n4 != 0) {
                        return false;
                    }
                }
                return true;
            }
            ++n3;
        }
        return false;
    }
""",
    )
    replace_method(
        "    public static AmmunitionDefinition findEquippedAmmunition(Player player, WeaponProfile object, boolean bl) {\n",
        "\n}\n",
        """    public static AmmunitionDefinition findEquippedAmmunition(Player player, WeaponProfile weaponProfile, boolean bl) {
        AmmunitionProfile ammunitionProfile = weaponProfile.getAmmunitionProfile();
        if (ammunitionProfile == null) {
            player.packetSender.sendGameMessage("That weapon is not configured properly, please report to staff!");
            return null;
        }
        int n = ammunitionProfile.getEquipmentSlot();
        ItemStack itemStack = player.getEquipmentManager().getContainer().getItemAt(n);
        int n2 = 0;
        if (itemStack != null) {
            n2 = itemStack.getAmount();
        }
        if (itemStack == null || weaponProfile == WeaponProfile.DARK_BOW && n2 < 2) {
            if (weaponProfile == WeaponProfile.DARK_BOW && n2 < 2) {
                player.packetSender.sendGameMessage("You don't have enough ammo left.");
            } else {
                player.packetSender.sendGameMessage("You have no ammo left!");
            }
            if (player.botEnabled) {
                if (player.isInWilderness()) {
                    BotCombatEscapeHandler.tryStartBotCombatEscape(player);
                } else if (player.currentBotTask != null) {
                    player.botCombatState = "escape";
                }
            }
            return null;
        }
        String string = ItemDefinition.forId(itemStack.getId()).getName().toLowerCase().replaceAll(" ", "_").replaceAll("\\\\(", "").replaceAll("\\\\)", "");
        AmmunitionDefinition[] ammunitionDefinitionArray = ammunitionProfile.getAllowedAmmunition();
        int n3 = ammunitionDefinitionArray.length;
        int n4 = 0;
        while (n4 < n3) {
            AmmunitionDefinition ammunitionDefinition = ammunitionDefinitionArray[n4];
            boolean bl2 = false;
            if (weaponProfile == WeaponProfile.METAL_CROSSBOW) {
                bl2 = true;
            }
            if (string.contains(ammunitionDefinition.name().toLowerCase()) && !bl2 || bl2 && string.startsWith(ammunitionDefinition.name().toLowerCase())) {
                if (n != 3) {
                    int n5;
                    ItemStack weaponItemStack = player.getEquipmentManager().getContainer().getItemAt(3);
                    if (weaponItemStack == null) {
                        return null;
                    }
                    if (string.contains("ogre")) {
                        String weaponProfileName = player.getWeaponProfile().name().toLowerCase();
                        n5 = weaponProfileName.contains("ogre") ? 0 : 1;
                    } else {
                        n5 = new ItemStack(weaponItemStack.getId()).getDefinition().getRequiredLevel(4);
                        n = new ItemStack(itemStack.getId()).getDefinition().getRequiredLevel(4);
                        n5 = n > n5 ? 1 : 0;
                    }
                    if (n5 != 0) {
                        Player player2 = player;
                        player2.packetSender.sendGameMessage("You cannot use that ammo with that weapon!");
                        return null;
                    }
                }
                return ammunitionDefinition;
            }
            ++n4;
        }
        player.packetSender.sendGameMessage("You can not use that kind of ammo!");
        return null;
    }
""",
    )
    path.write_text(text, encoding="utf-8")
    return repairs


def repair_herblore_handler_locals(source_root: Path) -> int:
    path = source_root / "com/rs2/model/skill/herblore/HerbloreHandler.java"
    text = path.read_text(encoding="utf-8")
    repairs = 0

    def replace_method(start_marker: str, end_marker: str, method: str) -> None:
        nonlocal text, repairs
        try:
            start = text.index(start_marker)
            end = text.index(end_marker, start)
        except ValueError as exc:
            raise SystemExit(f"{path}: expected herblore method region not found") from exc
        text = text[:start] + method + text[end:]
        repairs += 1

    replace_method(
        "    public static boolean handlePotionMaking(Player player, ItemStack object, ItemStack itemStack, int n, int n2) {\n",
        "\n    public static boolean emptyContainer(Player player, ItemStack itemStack, int n) {\n",
        """    public static boolean handlePotionMaking(Player player, ItemStack itemStack, ItemStack itemStack2, int n, int n2) {
        n = itemStack.getId();
        int n3 = itemStack2.getId();
        double[][] dArray = POTION_RECIPES;
        int n4 = 0;
        while (n4 < 27) {
            double[] recipe = dArray[n4];
            int n5 = (int)recipe[0];
            double ingredientId = recipe[1];
            double unfinishedPotionId = recipe[2];
            double finishedPotionId = recipe[3];
            double experience = recipe[4];
            double requiredLevel = recipe[5];
            int n6 = n5 == 2398 || ingredientId == 6018.0 || n5 == 6016 || ingredientId == 6049.0 ? 5935 : 227;
            if (n == n5 && n3 == n6 || n == n6 && n3 == n5) {
                if ((double)player.getSkillManager().getCurrentLevels()[15] < requiredLevel) {
                    player.getDialogueManager().showOneLineStatement("You need a Herblore level of " + (int)requiredLevel + " in order to make this potion.");
                    return true;
                }
                int vialItemId = n6;
                ItemStack itemStack3 = new ItemStack(n5, 1);
                ItemStack itemStack4 = new ItemStack(vialItemId, 1);
                if (!ServerSettings.herbloreEnabled) {
                    Player player2 = player;
                    player2.packetSender.sendGameMessage("This skill is currently disabled.");
                } else if (player.getQuestState(29) != 1) {
                    QuestDefinition questDefinition = QuestDefinition.forId(29);
                    String string = questDefinition.getName();
                    Player player3 = player;
                    player3.packetSender.sendGameMessage("You need to complete " + string + " to do this.");
                } else {
                    player.getUpdateState().setAnimation(363);
                    Player player4 = player;
                    player4.packetSender.sendGameMessage("You put the " + itemStack3.getDefinition().getName().replace(" herb", "").toLowerCase() + " into the " + itemStack4.getDefinition().getName() + ".");
                    int n7 = player.nextActionSequence();
                    CycleEventHandler.getInstance().schedule(player, new UnfinishedPotionTask(player, n7, itemStack3, vialItemId, unfinishedPotionId), 1);
                }
                return true;
            }
            if ((double)n == unfinishedPotionId && (double)n3 == ingredientId || (double)n == ingredientId && (double)n3 == unfinishedPotionId) {
                if ((double)player.getSkillManager().getCurrentLevels()[15] < requiredLevel) {
                    player.getDialogueManager().showOneLineStatement("You need a Herblore level of " + (int)requiredLevel + " in order to make this potion.");
                    return true;
                }
                ItemStack ingredientStack = new ItemStack((int)ingredientId, 1);
                if (!ServerSettings.herbloreEnabled) {
                    Player player5 = player;
                    player5.packetSender.sendGameMessage("This skill is currently disabled.");
                } else if (player.getQuestState(29) != 1) {
                    QuestDefinition questDefinition = QuestDefinition.forId(29);
                    String string = questDefinition.getName();
                    Player player6 = player;
                    player6.packetSender.sendGameMessage("You need to complete " + string + " to do this.");
                } else {
                    player.getUpdateState().setAnimation(363);
                    Player player7 = player;
                    player7.packetSender.sendGameMessage("You mix the " + ingredientStack.getDefinition().getName().toLowerCase() + " into your potion");
                    int n8 = player.nextActionSequence();
                    CycleEventHandler.getInstance().schedule(player, new FinishedPotionTask(player, n8, ingredientStack, unfinishedPotionId, finishedPotionId, experience), 1);
                }
                return true;
            }
            ++n4;
        }
        return false;
    }
""",
    )
    replace_method(
        "    private static int getEmptyContainerItemId(ItemStack object) {\n",
        "\n}\n",
        """    private static int getEmptyContainerItemId(ItemStack itemStack) {
        String string = itemStack.getDefinition().getDescription().toLowerCase();
        String string2 = itemStack.getDefinition().getName().toLowerCase();
        if (string.contains("potion") || string.contains("vial") || string.contains("dose")) {
            return 229;
        }
        if (string.contains("bucket") || string.contains("compost")) {
            return 1925;
        }
        if (string.contains("bowl") || string.contains("curry")) {
            return 1923;
        }
        if (string2.contains("jug") || string.contains("jug")) {
            return 1935;
        }
        if (string2.contains("flour")) {
            return 1931;
        }
        if (string.contains("cup")) {
            return 1980;
        }
        return -1;
    }
""",
    )
    text, count = replace_exact(
        text,
        "                    catch (Exception exception) {}\n",
        "                    catch (Exception exception) {\n"
        "                        return false;\n"
        "                    }\n",
        path,
    )
    repairs += count
    path.write_text(text, encoding="utf-8")
    return repairs


def repair_slayer_manager_locals(source_root: Path) -> int:
    path = source_root / "com/rs2/model/skill/slayer/SlayerManager.java"
    text = path.read_text(encoding="utf-8")
    repairs = 0

    def replace_method(start_marker: str, end_marker: str, method: str) -> None:
        nonlocal text, repairs
        try:
            start = text.index(start_marker)
            end = text.index(end_marker, start)
        except ValueError as exc:
            raise SystemExit(f"{path}: expected slayer method region not found") from exc
        text = text[:start] + method + text[end:]
        repairs += 1

    replace_method(
        "    public final void assignTaskFromMaster(int n) {\n",
        "\n    private static SlayerAssignmentDefinition chooseWeightedAssignment",
        """    public final void assignTaskFromMaster(int n) {
        SlayerMasterDefinition slayerMasterDefinition = SlayerMasterDefinition.forNpcId(n);
        if (slayerMasterDefinition == null) {
            return;
        }
        if (!ServerSettings.slayerEnabled) {
            Player player = this.player;
            player.packetSender.sendGameMessage("This skill is currently disabled.");
            player = this.player;
            player.packetSender.closeInterfaces();
            return;
        }
        if (this.player.getCombatLevel() < slayerMasterDefinition.getRequiredCombatLevel()) {
            this.player.getDialogueManager().showNpcOneLineDialogue("You need a combat level of " + slayerMasterDefinition.getRequiredCombatLevel() + " to recieve a task from me.", 588);
            return;
        }
        if (slayerMasterDefinition.getNpcId() == 1599 && this.player.getSkillManager().getCurrentLevels()[18] < 50) {
            this.player.getDialogueManager().showNpcOneLineDialogue("You need a slayer level of 50 to recieve a task from me.", 588);
            return;
        }
        SlayerAssignmentDefinition slayerAssignmentDefinition = SlayerManager.chooseWeightedAssignment(slayerMasterDefinition.getAssignments(), this.player);
        if (slayerAssignmentDefinition == null) {
            Player player = this.player;
            player.packetSender.sendGameMessage("No tasks available currently.");
            player = this.player;
            player.packetSender.closeInterfaces();
            return;
        }
        this.slayerMasterId = n;
        this.slayerTaskName = slayerAssignmentDefinition.taskName;
        this.taskAmount = GameUtil.randomBetweenInclusive(slayerAssignmentDefinition.minAmount, slayerAssignmentDefinition.maxAmount);
        this.player.getDialogueManager().showNpcOneLineDialogue("Your new task is to kill " + this.taskAmount + " " + this.slayerTaskName + "s.", 588);
    }
""",
    )
    replace_method(
        "    private static SlayerAssignmentDefinition chooseWeightedAssignment(SlayerAssignmentDefinition[] slayerAssignmentDefinitionArray, Player object) {\n",
        "\n    public final void completeTask() {\n",
        """    private static SlayerAssignmentDefinition chooseWeightedAssignment(SlayerAssignmentDefinition[] slayerAssignmentDefinitionArray, Player player) {
        ArrayList<SlayerAssignmentDefinition> arrayList = new ArrayList<SlayerAssignmentDefinition>();
        SlayerAssignmentDefinition[] slayerAssignmentDefinitionArray2 = slayerAssignmentDefinitionArray;
        int n = slayerAssignmentDefinitionArray.length;
        int n2 = 0;
        while (n2 < n) {
            SlayerAssignmentDefinition slayerAssignmentDefinition = slayerAssignmentDefinitionArray2[n2];
            if (slayerAssignmentDefinition.isAvailableFor(player)) {
                arrayList.add(slayerAssignmentDefinition);
            }
            ++n2;
        }
        if (arrayList.isEmpty()) {
            return null;
        }
        double d = 0.0;
        for (SlayerAssignmentDefinition slayerAssignmentDefinition : arrayList) {
            d += (double)slayerAssignmentDefinition.weight;
        }
        double[] dArray = new double[arrayList.size()];
        int n3 = 0;
        while (n3 < arrayList.size()) {
            SlayerAssignmentDefinition slayerAssignmentDefinition = (SlayerAssignmentDefinition)arrayList.get(n3);
            double d2 = slayerAssignmentDefinition.weight;
            dArray[n3] = d2 / d;
            ++n3;
        }
        n3 = GameUtil.rollProbabilityIndex(dArray);
        return (SlayerAssignmentDefinition)arrayList.get(n3);
    }
""",
    )
    replace_method(
        "    public final boolean canAttackSlayerMonster(Npc object) {\n",
        "\n    public final boolean requiresFinishingItem",
        """    public final boolean canAttackSlayerMonster(Npc npc) {
        SlayerMonsterDefinition slayerMonsterDefinition = SlayerMonsterDefinition.forName(npc.getDefinition().getName().toLowerCase());
        if (slayerMonsterDefinition == null) {
            return true;
        }
        if (this.player.getSkillManager().getCurrentLevels()[18] < slayerMonsterDefinition.getRequiredSlayerLevel()) {
            Player player = this.player;
            player.packetSender.sendGameMessage("You need a slayer level of " + slayerMonsterDefinition.getRequiredSlayerLevel() + " to attack this monster.");
            return false;
        }
        if (slayerMonsterDefinition.getMonsterName().equalsIgnoreCase("kurask") || slayerMonsterDefinition.getMonsterName().equalsIgnoreCase("turoth")) {
            if (this.player.getWeaponProfile() != null && (this.player.getWeaponProfile().getInterfaceDefinition() != null && this.player.getWeaponProfile().getInterfaceDefinition() == WeaponInterfaceDefinition.LONG_BOW || this.player.getWeaponProfile().getInterfaceDefinition() == WeaponInterfaceDefinition.BOW) && this.player.getEquipmentManager().getContainer().getItemAt(13) != null && this.player.getEquipmentManager().getContainer().getItemAt(13).getId() == 4160) {
                return true;
            }
            if (this.player.getQueuedCombatSpell() == SpellDefinition.MAGIC_DART || this.player.getAutocastSpell() == SpellDefinition.MAGIC_DART) {
                return true;
            }
        }
        if (slayerMonsterDefinition.getRequiredItemIds() == null) {
            return true;
        }
        if (slayerMonsterDefinition.getRequirementMode().equals("use") || slayerMonsterDefinition.getRequirementMode().equals("none")) {
            return true;
        }
        ItemStack[] itemStackArray = this.player.getEquipmentManager().getContainer().getRawItems();
        int n = itemStackArray.length;
        int n2 = 0;
        while (n2 < n) {
            ItemStack itemStack = itemStackArray[n2];
            if (itemStack != null) {
                int[] nArray = slayerMonsterDefinition.getRequiredItemIds();
                int n3 = nArray.length;
                int n4 = 0;
                while (n4 < n3) {
                    int n5 = nArray[n4];
                    if (itemStack.getId() == n5) {
                        return true;
                    }
                    ++n4;
                }
            }
            ++n2;
        }
        Player player = this.player;
        player.packetSender.sendGameMessage("You don't have the required protection against this kind of monster!");
        return false;
    }
""",
    )
    replace_method(
        "    public final boolean requiresFinishingItem(Npc entity, boolean bl) {\n",
        "\n    public final boolean handleZygomiteSpawn",
        """    public final boolean requiresFinishingItem(Npc entity, boolean bl) {
        SlayerMonsterDefinition slayerMonsterDefinition = SlayerMonsterDefinition.forName(entity.getDefinition().getName().toLowerCase());
        if (slayerMonsterDefinition == null) {
            return false;
        }
        if (slayerMonsterDefinition.getRequirementMode().equals("use") && entity.getCurrentHitpoints() > 1) {
            return true;
        }
        int n = entity.getMaxHitpoints() / 10;
        if (slayerMonsterDefinition.getRequirementMode().equals("use") && entity.getCurrentHitpoints() <= n) {
            this.player.packetSender.sendGameMessage("The " + GameUtil.capitalizeLowercaseFirst(slayerMonsterDefinition.getMonsterName()) + " is on its last legs! Finish it quickly!");
            return true;
        }
        return false;
    }
""",
    )
    replace_method(
        "    public final void useFinishingItemOnMonster(Npc npc, int n) {\n",
        "\n    public final boolean combineFungicideSpray",
        """    public final void useFinishingItemOnMonster(Npc npc, int n) {
        SlayerMonsterDefinition slayerMonsterDefinition = SlayerMonsterDefinition.forName(npc.getDefinition().getName().toLowerCase());
        if (slayerMonsterDefinition == null) {
            return;
        }
        int n2 = npc.getMaxHitpoints() / 10;
        if (!slayerMonsterDefinition.getRequirementMode().equals("use") || npc.getCurrentHitpoints() > n2) {
            return;
        }
        int[] nArray = slayerMonsterDefinition.getRequiredItemIds();
        if (nArray == null) {
            return;
        }
        n2 = nArray.length;
        int n3 = 0;
        while (n3 < n2) {
            int requiredItemId = nArray[n3];
            if (requiredItemId == n) {
                this.player.getUpdateState().setAnimation(832);
                npc.setDead(true);
                CombatManager.handleDeath(npc);
                if (n != 4162) {
                    this.player.getInventoryManager().removeItem(new ItemStack(n));
                    this.player.getInventoryManager().addItem(new ItemStack(n != 7432 ? (n < 7421 || n > 7430 ? -1 : n + 1) : 7421));
                }
                return;
            }
            ++n3;
        }
    }
""",
    )
    path.write_text(text, encoding="utf-8")
    return repairs


def repair_treasure_trail_manager_locals(source_root: Path) -> int:
    path = source_root / "com/rs2/model/clue/TreasureTrailManager.java"
    text = path.read_text(encoding="utf-8")
    repairs = 0

    old = "                        DialogueManager.setTreasureTrailDialogueStep(player, 10009, 1);\n                        player.getDialogueManager().showNpcOneLineDialogue"
    new = "                        player.getDialogueManager().setDialogueId(10009);\n                        player.getDialogueManager().setNextDialogueStep(1);\n                        player.getDialogueManager().showNpcOneLineDialogue"
    text, count = replace_exact(text, old, new, path)
    repairs += count

    def replace_method(start_marker: str, end_marker: str, method: str) -> None:
        nonlocal text, repairs
        try:
            start = text.index(start_marker)
            end = text.index(end_marker, start)
        except ValueError as exc:
            raise SystemExit(f"{path}: expected treasure trail method region not found") from exc
        text = text[:start] + method + text[end:]
        repairs += 1

    replace_method(
        "    public static void completeTreasureTrail(Player player, int n) {\n",
        "\n    private static ItemStack[] rollRewardItems",
        """    public static void completeTreasureTrail(Player player, int n) {
        ++player.treasureTrailStepCount;
        int n2 = 1;
        if (player.clueRequiredItems != null) {
            if (!player.getInventoryManager().containsItemStack(player.clueRequiredItems[0])) {
                n2 = 0;
            }
            if (n2 != 0) {
                n2 = 0;
                while (n2 < player.clueRequiredItems.length) {
                    player.getInventoryManager().removeItem(player.clueRequiredItems[n2]);
                    ++n2;
                }
                player.clueRequiredItems = null;
            } else {
                return;
            }
        }
        ItemStack[] itemStackArray = null;
        switch (n) {
            case 1: {
                itemStackArray = TreasureTrailManager.rollRewardItems(player, 1);
                ++player.easyCluesCompleted;
                break;
            }
            case 2: {
                itemStackArray = TreasureTrailManager.rollRewardItems(player, 2);
                ++player.mediumCluesCompleted;
                break;
            }
            case 3: {
                itemStackArray = TreasureTrailManager.rollRewardItems(player, 3);
                ++player.hardCluesCompleted;
            }
        }
        int[] rewardItemIds = new int[itemStackArray.length];
        int[] nArray = new int[itemStackArray.length];
        int n3 = 0;
        int n4 = 0;
        while (n4 < itemStackArray.length) {
            rewardItemIds[n4] = itemStackArray[n4].getId();
            nArray[n4] = itemStackArray[n4].getAmount();
            player.getInventoryManager().addOrDropItem(new ItemStack(rewardItemIds[n4], nArray[n4]));
            int n5 = GrandExchangeManager.getGuidePrice(rewardItemIds[n4]);
            n3 += n5 * nArray[n4];
            ++n4;
        }
        Player player2 = player;
        player2.packetSender.sendGameMessage("Well done, you've completed the Treasure Trail! (" + player.treasureTrailStepCount + " " + (player.treasureTrailStepCount < 2 ? "step" : "steps") + ")");
        Player player3 = player;
        player3.packetSender.sendGameMessage("Your treasure is worth around " + GameUtil.formatNumber(n3) + " coins!");
        player.treasureTrailStepCount = 0;
        player.sliderPuzzlePieces = new ItemStack[25];
        Player player4 = player;
        player4.packetSender.sendItemContainer(6963, itemStackArray);
        Player player5 = player;
        player5.packetSender.showInterface(6960);
        Player player6 = player;
        player6.packetSender.sendMusicJingle(237, 256);
    }
""",
    )
    replace_method(
        "    private static ItemStack[] rollRewardItems(Player itemStackArray, int n) {\n",
        "\n    public static int randomClueItemForLevel",
        """    private static ItemStack[] rollRewardItems(Player player, int n) {
        int n2;
        ArrayList<ItemStack> arrayList = new ArrayList<ItemStack>();
        int n3 = 0;
        if (n == 1) {
            n3 = 2;
        } else if (n == 2) {
            n3 = 3;
        } else if (n == 3) {
            n3 = 4;
        }
        n3 += GameUtil.randomInt(3);
        int n4 = 0;
        while (n4 < n3) {
            n2 = n;
            ItemStack[] itemStackArray3 = null;
            if (n2 == 1) {
                itemStackArray3 = NpcDropManager.rollDrops((Entity)player, 6420, false);
            } else if (n2 == 2) {
                itemStackArray3 = NpcDropManager.rollDrops((Entity)player, 6421, false);
            } else if (n2 == 3) {
                itemStackArray3 = NpcDropManager.rollDrops((Entity)player, 6422, false);
            }
            if (itemStackArray3 != null) {
                n2 = 0;
                while (n2 < itemStackArray3.length) {
                    arrayList.add(itemStackArray3[n2]);
                    ++n2;
                }
            }
            ++n4;
        }
        HashMap<Integer, Integer> hashMap = new HashMap<Integer, Integer>();
        int n5 = 0;
        while (n5 < arrayList.size()) {
            n2 = ((ItemStack)arrayList.get(n5)).getId();
            int amount = ((ItemStack)arrayList.get(n5)).getAmount();
            if (!hashMap.containsKey(n2)) {
                hashMap.put(n2, amount);
            } else {
                int n6 = (Integer)hashMap.get(n2);
                hashMap.put(n2, n6 + amount);
            }
            ++n5;
        }
        ArrayList<ItemStack> arrayList2 = new ArrayList<ItemStack>();
        for (Map.Entry<Integer, Integer> entry : hashMap.entrySet()) {
            int n7 = (Integer)entry.getKey();
            int n8 = (Integer)entry.getValue();
            arrayList2.add(new ItemStack(n7, n8));
        }
        ArrayList<ItemStack> arrayList3 = new ArrayList<ItemStack>();
        int n9 = 0;
        while (n9 < arrayList2.size()) {
            int n10 = ((ItemStack)arrayList2.get(n9)).getId();
            int n11 = ((ItemStack)arrayList2.get(n9)).getAmount();
            ItemDefinition itemDefinition = ((ItemStack)arrayList2.get(n9)).getDefinition();
            if (itemDefinition.isStackable() || n11 == 1) {
                arrayList3.add((ItemStack)arrayList2.get(n9));
            } else {
                int n12 = 0;
                while (n12 < n11) {
                    arrayList3.add(new ItemStack(n10, 1));
                    ++n12;
                }
            }
            ++n9;
        }
        ItemStack[] itemStackArray = new ItemStack[arrayList3.size()];
        n9 = 0;
        while (n9 < arrayList3.size()) {
            itemStackArray[n9] = (ItemStack)arrayList3.get(n9);
            ++n9;
        }
        return itemStackArray;
    }
""",
    )
    replace_method(
        "    public static int randomClueItemForLevel(int n) {\n",
        "\n    public static boolean handleRewardContainerItem",
        """    public static int randomClueItemForLevel(int n) {
        ArrayList<Integer> candidateItems = new ArrayList<Integer>();
        ArrayList<Integer> arrayList = new ArrayList<Integer>();
        switch (n) {
            case 1: {
                candidateItems.add(CacheArchiveEntry.randomMapClueItemForLevel(1));
                candidateItems.add(CacheFile.randomSearchClueItemForLevel(1));
                candidateItems.add(CacheDefinitionIndex.randomNpcClueItemForLevel(1));
                candidateItems.add(CacheArchiveEntry.randomMapClueItemForLevel(1));
                candidateItems.add(CacheFile.randomSearchClueItemForLevel(1));
                candidateItems.add(CacheDefinitionIndex.randomNpcClueItemForLevel(1));
                candidateItems.add(BotWorldRouteChoice.randomCrypticDigClueItemForLevel(1));
                break;
            }
            case 2: {
                candidateItems.add(GameplayHelper.randomAnagramClueItemForLevel(2));
                candidateItems.add(CacheArchiveEntry.randomMapClueItemForLevel(2));
                candidateItems.add(CacheFile.randomSearchClueItemForLevel(2));
                candidateItems.add(CacheDefinitionIndex.randomNpcClueItemForLevel(2));
                candidateItems.add(CoordinateClueHandler.randomClueItemForLevel(2));
                break;
            }
            case 3: {
                candidateItems.add(GameplayHelper.randomAnagramClueItemForLevel(3));
                candidateItems.add(CacheArchiveEntry.randomMapClueItemForLevel(3));
                candidateItems.add(CacheFile.randomSearchClueItemForLevel(3));
                candidateItems.add(CacheDefinitionIndex.randomNpcClueItemForLevel(3));
                candidateItems.add(BotWorldRouteChoice.randomCrypticDigClueItemForLevel(3));
                candidateItems.add(CoordinateClueHandler.randomClueItemForLevel(3));
                break;
            }
            default: {
                return -1;
            }
        }
        for (Integer clueItemId : candidateItems) {
            n = clueItemId;
            if (!ItemDefinition.isDefined(n)) continue;
            arrayList.add(n);
        }
        if (arrayList.isEmpty()) {
            return -1;
        }
        return (Integer)arrayList.get(GameUtil.randomInclusive(arrayList.size() - 1));
    }
""",
    )
    replace_method(
        "    public static void recordClueWizardKill(Player player, Npc npc) {\n",
        "\n}\n",
        """    public static void recordClueWizardKill(Player player, Npc npc) {
        if (npc.getNpcId() == 1007 || npc.getNpcId() == 1264) {
            player.killedClueAttacker = true;
        }
    }
""",
    )
    path.write_text(text, encoding="utf-8")
    return repairs


def repair_mid_cluster_locals(source_root: Path) -> int:
    repairs = 0

    path = source_root / "com/rs2/model/skill/magic/TeleportManager.java"
    text = path.read_text(encoding="utf-8")
    start_marker = "    public final boolean castSpellbookTeleport(Position object) {\n"
    end_marker = "\n    public final boolean castItemTeleport"
    try:
        start = text.index(start_marker)
        end = text.index(end_marker, start)
    except ValueError as exc:
        raise SystemExit(f"{path}: expected teleport method region not found") from exc
    text = text[:start] + """    public final boolean castSpellbookTeleport(Position position) {
        if (this.player.getEnchantmentChamberController().isInsideChamber() || this.player.getAlchemistPlaygroundController().isInsidePlayground() || this.player.getCreatureGraveyardController().isInsideGraveyard() || this.player.getTelekineticTheatreController().isInsideTheatre()) {
            Player player = this.player;
            player.packetSender.sendGameMessage("You can't teleport out of here.");
            return false;
        }
        if (this.player.isInWilderness() && this.player.getWildernessLevel() > 20) {
            Player player = this.player;
            player.packetSender.sendGameMessage("You can't teleport above level 20 in the wilderness.");
            return false;
        }
        if (this.player.isTeleblocked()) {
            Player player = this.player;
            player.packetSender.sendGameMessage("A magical force prevents you from teleporting.");
            return false;
        }
        if (this.player.isInTeleportRestrictedArea()) {
            Player player = this.player;
            player.packetSender.sendGameMessage("You can't teleport from here.");
            return false;
        }
        this.startMagicTeleportTask(position.getX(), position.getY(), position.getPlane(), this.player.getSpellbook() == Spellbook.ANCIENT);
        return true;
    }
""" + text[end:]
    path.write_text(text, encoding="utf-8")
    repairs += 1

    path = source_root / "com/rs2/model/quest/QuestManager.java"
    text = path.read_text(encoding="utf-8")
    start_marker = "    public final boolean handleButtonClick(int n) {\n"
    end_marker = "\n}\n"
    try:
        start = text.index(start_marker)
        end = text.index(end_marker, start)
    except ValueError as exc:
        raise SystemExit(f"{path}: expected quest manager method region not found") from exc
    text = text[:start] + """    public final boolean handleButtonClick(int n) {
        boolean bl;
        block10: {
            int n2;
            int n3;
            if (ServerSettings.cacheVersion > 245) {
                n3 = 1;
                while (n3 < QuestDefinition.questCount) {
                    QuestDefinition questDefinition = QuestDefinition.forId(n3);
                    String string = questDefinition.getName();
                    n2 = questDefinition.getJournalButtonId();
                    if (n == n2) {
                        QuestManager questManager = this;
                        Player player = questManager.player;
                        player.packetSender.sendInterfaceText("", 8145);
                        n2 = 8147;
                        while (n2 <= 8195) {
                            player = questManager.player;
                            player.packetSender.sendInterfaceText("", n2);
                            ++n2;
                        }
                        n2 = 12174;
                        while (n2 <= 12223) {
                            player = questManager.player;
                            player.packetSender.sendInterfaceText("", n2);
                            ++n2;
                        }
                        player = this.player;
                        player.packetSender.sendInterfaceText(string, 8144);
                        this.player.getQuestPoints();
                        n2 = n3;
                        questManager = this;
                        QuestScript questScript = QuestDefinition.getQuestScript(n2);
                        String[] stringArray = questScript.buildQuestJournal(questManager.player, questManager.player.getQuestState(n2));
                        player = questManager.player;
                        player.packetSender.sendInterfaceText(stringArray[0], 8145);
                        int n4 = 1;
                        while (n4 < stringArray.length) {
                            player = questManager.player;
                            player.packetSender.sendInterfaceText(stringArray[n4], n4 + 8146);
                            ++n4;
                        }
                        player = this.player;
                        player.packetSender.showInterface(8134);
                        return true;
                    }
                    ++n3;
                }
            }
            n2 = n;
            QuestManager questManager = this;
            n3 = 1;
            while (n3 < QuestDefinition.questCount) {
                QuestScript questScript = QuestDefinition.getQuestScript(n3);
                if (questScript.getQuestId() != -1 && QuestManager.isQuestJournalButtonAvailable(questScript.getQuestId())) {
                    if (!QuestDefinition.forId(questScript.getQuestId()).isMembersOnly() || questManager.player.isMember()) {
                        if (!(QuestDefinition.forId(questScript.getQuestId()).isMembersOnly() && ServerSettings.freeToPlayWorld || !questScript.handleButtonClick(questManager.player, n2, questManager.player.getQuestState(n3)))) {
                            bl = true;
                            break block10;
                        }
                    }
                }
                ++n3;
            }
            bl = false;
        }
        return bl;
    }
""" + text[end:]
    path.write_text(text, encoding="utf-8")
    repairs += 1

    path = source_root / "com/rs2/model/clue/PuzzleBoxHandler.java"
    text = path.read_text(encoding="utf-8")

    def replace_puzzle_method(start_marker: str, end_marker: str, method: str) -> None:
        nonlocal text, repairs
        try:
            start = text.index(start_marker)
            end = text.index(end_marker, start)
        except ValueError as exc:
            raise SystemExit(f"{path}: expected puzzle method region not found") from exc
        text = text[:start] + method + text[end:]
        repairs += 1

    replace_puzzle_method(
        "    private static void showCluePuzzleInterface(Player itemStackArray) {\n",
        "\n    private static void showQuestPuzzleInterface",
        """    private static void showCluePuzzleInterface(Player player) {
        player.packetSender.showInterface(6976);
        player.packetSender.sendItemContainer(6980, player.sliderPuzzlePieces);
        PacketSender packetSender = player.packetSender;
        ItemStack[] itemStackArray = new ItemStack[25];
        int n = 0;
        while (n < 25) {
            itemStackArray[n] = new ItemStack(PuzzleBoxHandler.getPiecesForPuzzleType(activePuzzleType)[n]);
            ++n;
        }
        packetSender.sendItemContainer(6985, itemStackArray);
        if (!PuzzleBoxHandler.isCluePuzzleSolved(player)) {
            player.en = false;
        }
    }
""",
    )
    replace_puzzle_method(
        "    private static void swapBlankWithPosition(Player player, Position object, boolean bl) {\n",
        "\n    private static void scramblePuzzle",
        """    private static void swapBlankWithPosition(Player player, Position position, boolean bl) {
        int n = 0;
        int n2 = 0;
        int n3 = 0;
        while (n3 < player.sliderPuzzlePieces.length) {
            if (player.sliderPuzzlePieces[n3].getId() == -1) {
                n = n3;
            }
            if (PuzzleBoxHandler.getPiecePosition(player, player.sliderPuzzlePieces[n3].getId()).equals(position)) {
                n2 = n3;
            }
            ++n3;
        }
        ItemStack itemStack = player.sliderPuzzlePieces[n];
        player.sliderPuzzlePieces[n] = player.sliderPuzzlePieces[n2];
        player.sliderPuzzlePieces[n2] = itemStack;
        if (bl) {
            if (player.getOpenInterfaceId() == 6976) {
                PuzzleBoxHandler.showCluePuzzleInterface(player);
                return;
            }
            if (player.getOpenInterfaceId() == 11126) {
                PuzzleBoxHandler.showQuestPuzzleInterface(player);
            }
        }
    }
""",
    )
    path.write_text(text, encoding="utf-8")
    return repairs


def repair_quest_cluster_locals(source_root: Path) -> int:
    repairs = 0

    for path in (source_root / "com/rs2/model/quest/impl").glob("*.java"):
        text = path.read_text(encoding="utf-8")
        count = text.count("this.d(")
        if count == 0:
            continue
        text = text.replace("this.d(", "this.startQuest(")
        path.write_text(text, encoding="utf-8")
        repairs += count

    path = source_root / "com/rs2/model/quest/impl/MonkeyMadnessQuest.java"
    text = path.read_text(encoding="utf-8")
    text, count = replace_exact(text, "(Player)stringArray", "player", path)
    repairs += count
    count = text.count("                        this.d(player);\n")
    if count:
        text = text.replace("                        this.d(player);\n", "                        this.startQuest(player);\n")
        repairs += count
    text, count = replace_exact(
        text,
        "                    for (Npc npc2 : player.temporaryCutsceneNpcs) {\n                        if (npc2 == null) continue;\n                        CombatManager.startCombat(npc2, npc);\n                    }\n",
        "                    for (Object temporaryNpc : player.temporaryCutsceneNpcs) {\n                        Npc npc2 = (Npc)temporaryNpc;\n                        if (npc2 == null) continue;\n                        CombatManager.startCombat(npc2, npc);\n                    }\n",
        path,
    )
    repairs += count
    path.write_text(text, encoding="utf-8")

    path = source_root / "com/rs2/model/quest/impl/FremennikTrialsQuest.java"
    text = path.read_text(encoding="utf-8")
    text, count = replace_exact(
        text,
        """            Object object = new ArrayList(this.doorRiddleAnswers);
            Collections.shuffle(object, new Random(player.bK));
            Object object2 = (String)object.get(0);
            object = String.valueOf(this.doorRiddleLetters[player.fremennikDoorRiddleFirstLetterIndex]) + this.doorRiddleLetters[player.fremennikDoorRiddleSecondLetterIndex] + this.doorRiddleLetters[player.fremennikDoorRiddleThirdLetterIndex] + this.doorRiddleLetters[player.fremennikDoorRiddleFourthLetterIndex];
            if (((String)object2).equals(object)) {
                object2 = player;
                ((Player)object2).packetSender.closeInterfaces();
                object2 = player;
                ((Player)object2).packetSender.sendGameMessage("You have solved the riddle!");
                if ((player.questProgressFlags[this.getQuestId()] & GameUtil.bitFlag(21)) == 0 && n2 >= 2) {
                    int n7 = this.getQuestId();
                    player.questProgressFlags[n7] = player.questProgressFlags[n7] + GameUtil.bitFlag(21);
                }
            } else {
                object2 = player;
                ((Player)object2).packetSender.sendGameMessage("Your answer is not correct!");
            }
""",
        """            ArrayList object = new ArrayList(this.doorRiddleAnswers);
            Collections.shuffle(object, new Random(player.bK));
            String object2 = (String)object.get(0);
            String string = String.valueOf(this.doorRiddleLetters[player.fremennikDoorRiddleFirstLetterIndex]) + this.doorRiddleLetters[player.fremennikDoorRiddleSecondLetterIndex] + this.doorRiddleLetters[player.fremennikDoorRiddleThirdLetterIndex] + this.doorRiddleLetters[player.fremennikDoorRiddleFourthLetterIndex];
            if (object2.equals(string)) {
                Player player6 = player;
                player6.packetSender.closeInterfaces();
                player6 = player;
                player6.packetSender.sendGameMessage("You have solved the riddle!");
                if ((player.questProgressFlags[this.getQuestId()] & GameUtil.bitFlag(21)) == 0 && n2 >= 2) {
                    int n7 = this.getQuestId();
                    player.questProgressFlags[n7] = player.questProgressFlags[n7] + GameUtil.bitFlag(21);
                }
            } else {
                Player player7 = player;
                player7.packetSender.sendGameMessage("Your answer is not correct!");
            }
""",
        path,
    )
    repairs += count
    count = text.count("                        this.d((Player)object);\n")
    if count:
        text = text.replace("                        this.d((Player)object);\n", "                        this.startQuest((Player)object);\n")
        repairs += count
    text, count = replace_exact(
        text,
        """                    Object object2 = new ArrayList(((Player)object).getGender() == 0 ? this.maleFremennikNamePool : this.femaleFremennikNamePool);
                    Collections.shuffle(object2, new Random(((Player)object).bK));
                    object2 = (String)object2.get(0);
                    ((Player)object).getDialogueManager().showNpcFourLineDialogue("From this day onward, you are outerlander no more!", "In honour of your acceptance into the Fremennik You", "gain a new name to be known as; You will now be", "called " + (String)object2, 591);
""",
        """                    ArrayList object2 = new ArrayList(((Player)object).getGender() == 0 ? this.maleFremennikNamePool : this.femaleFremennikNamePool);
                    Collections.shuffle(object2, new Random(((Player)object).bK));
                    String fremennikName = (String)object2.get(0);
                    ((Player)object).getDialogueManager().showNpcFourLineDialogue("From this day onward, you are outerlander no more!", "In honour of your acceptance into the Fremennik You", "gain a new name to be known as; You will now be", "called " + fremennikName, 591);
""",
        path,
    )
    repairs += count

    start_marker = "    public final boolean handleItemOnItem(Player object, int n, int n2, int n3) {\n"
    end_marker = "\n    @Override\n    public final boolean handleItemOnNpc"
    try:
        start = text.index(start_marker)
        end = text.index(end_marker, start)
    except ValueError as exc:
        raise SystemExit(f"{path}: expected Fremennik handleItemOnItem region not found") from exc
    text = text[:start] + """    public final boolean handleItemOnItem(Player player, int n, int n2, int n3) {
        if (n3 < 2) {
            return false;
        }
        if (n == 3735 && n2 == 3737 || n2 == 3735 && n == 3737) {
            player.packetSender.sendGameMessage("You screw the lid on tightly.");
            player.getInventoryManager().replaceItem(new ItemStack(3735, 1), new ItemStack(3740, 1));
            player.getInventoryManager().removeItem(new ItemStack(3737, 1));
            return true;
        }
        if (n == 3744 && n2 == 3746 || n2 == 3744 && n == 3746) {
            player.packetSender.sendGameMessage("You coat the wooden coin with the sticky red goop.");
            player.getInventoryManager().replaceItem(new ItemStack(3744, 1), new ItemStack(3743, 1));
            player.getInventoryManager().removeItem(new ItemStack(3746, 1));
            if ((player.questProgressFlags[this.getQuestId()] & GameUtil.bitFlag(22)) == 0) {
                int n4 = this.getQuestId();
                player.questProgressFlags[n4] = player.questProgressFlags[n4] + GameUtil.bitFlag(22);
            }
            return true;
        }
        if (n == 3729 && n2 == 3727 || n2 == 3729 && n == 3727) {
            player.packetSender.sendGameMessage("You empty the jug into the bucket.");
            player.getInventoryManager().replaceItem(new ItemStack(3729, 1), new ItemStack(3732, 1));
            player.getInventoryManager().replaceItem(new ItemStack(3727, 1), new ItemStack(3724, 1));
            return true;
        }
        if (n == 3729 && n2 == 3724 || n2 == 3729 && n == 3724) {
            player.packetSender.sendGameMessage("You fill the bucket to the brim.");
            player.getInventoryManager().replaceItem(new ItemStack(3729, 1), new ItemStack(3731, 1));
            player.getInventoryManager().replaceItem(new ItemStack(3724, 1), new ItemStack(3722, 1));
            return true;
        }
        if (n == 3731 && n2 == 3727 || n2 == 3731 && n == 3727) {
            player.packetSender.sendGameMessage("You empty the jug into the bucket.");
            player.getInventoryManager().replaceItem(new ItemStack(3731, 1), new ItemStack(3732, 1));
            player.getInventoryManager().replaceItem(new ItemStack(3727, 1), new ItemStack(3726, 1));
            return true;
        }
        if (n == 3729 && n2 == 3726 || n2 == 3729 && n == 3726) {
            player.packetSender.sendGameMessage("You empty the jug into the bucket.");
            player.getInventoryManager().replaceItem(new ItemStack(3729, 1), new ItemStack(3732, 1));
            player.getInventoryManager().replaceItem(new ItemStack(3726, 1), new ItemStack(3723, 1));
            return true;
        }
        if (n == 590 && n2 == 3713 || n2 == 590 && n == 3713) {
            player.packetSender.sendGameMessage("You light the string of the strange object. It starts to hiss slightly.");
            player.getInventoryManager().replaceItem(new ItemStack(3713, 1), new ItemStack(3714, 1));
            return true;
        }
        if (n == 946 && n2 == 3692 || n2 == 946 && n == 3692) {
            player.packetSender.sendGameMessage("You craft an unstrung lyre out of the branch.");
            player.getUpdateState().setAnimation(1248);
            player.getInventoryManager().replaceItem(new ItemStack(3692, 1), new ItemStack(3688, 1));
            return true;
        }
        if (n == 3801 && n2 == 3712 || n2 == 3801 && n == 3712) {
            player.packetSender.sendGameMessage("You empty the keg and refill it with low alcohol beer.");
            player.getInventoryManager().removeItem(new ItemStack(3801, 1));
            player.getInventoryManager().removeItem(new ItemStack(3712, 1));
            player.getInventoryManager().addOrDropItem(new ItemStack(3711, 1));
            if (this.longhallDistractionArea.contains(player.getPosition())) {
                Npc[] npcArray = Npc.findActiveInArea(this.longhallDistractionArea);
                n2 = npcArray.length;
                int n5 = 0;
                while (n5 < n2) {
                    Npc npc = npcArray[n5];
                    npc.getUpdateState().setForcedText("What was THAT?");
                    ++n5;
                }
            }
            return true;
        }
        if (n == 3694 && n2 == 3688 || n2 == 3694 && n == 3688) {
            player.packetSender.sendGameMessage("You attach the golden strings to the lyre.");
            player.getInventoryManager().removeItem(new ItemStack(3688, 1));
            player.getInventoryManager().removeItem(new ItemStack(3694, 1));
            player.getInventoryManager().addOrDropItem(new ItemStack(3689, 1));
            return true;
        }
        return false;
    }
""" + text[end:]
    repairs += 1
    path.write_text(text, encoding="utf-8")

    path = source_root / "com/rs2/model/quest/impl/TutorialQuest.java"
    text = path.read_text(encoding="utf-8")
    for old, new in (
        ("            Object object2 = itemStackArray;\n", "            Player player2 = itemStackArray;\n"),
        ("            object2.getEquipmentManager().getContainer().clear();\n", "            player2.getEquipmentManager().getContainer().clear();\n"),
        ("            object2.getBankContainer().clear();\n", "            player2.getBankContainer().clear();\n"),
        ("            object2.getInventoryManager().refresh();\n", "            player2.getInventoryManager().refresh();\n"),
        ("            object2.getEquipmentManager().refresh();\n", "            player2.getEquipmentManager().refresh();\n"),
        ("            object2.setAppearanceUpdateRequired(true);\n", "            player2.setAppearanceUpdateRequired(true);\n"),
        ("            object2.getBankContainer().addToTab(new ItemStack(995, 25), 0);\n", "            player2.getBankContainer().addToTab(new ItemStack(995, 25), 0);\n"),
        ("                object2.getInventoryManager().addItem((ItemStack)object);\n", "                player2.getInventoryManager().addItem((ItemStack)object);\n"),
    ):
        text, count = replace_exact(text, old, new, path)
        repairs += count
    text, count = replace_exact(
        text,
        """                object2 = new ItemStack(7956, 1);
                itemStackArray.getInventoryManager().addItem((ItemStack)object2);
                GameUtil.addTrackedRareItemAmount((ItemStack)object2);
""",
        """                ItemStack itemStack = new ItemStack(7956, 1);
                itemStackArray.getInventoryManager().addItem(itemStack);
                GameUtil.addTrackedRareItemAmount(itemStack);
""",
        path,
    )
    repairs += count
    path.write_text(text, encoding="utf-8")

    path = source_root / "com/rs2/model/quest/impl/GrandTreeQuest.java"
    text = path.read_text(encoding="utf-8")
    text, count = replace_exact(
        text,
        """            Object object = new ArrayList(this.daconiaRootSearchPositions);
            Collections.shuffle(object, new Random(player.bK));
            if (n2 == ((Position)object.get(0)).getX() && n3 == ((Position)object.get(0)).getY() && !player.ownsItem(793)) {
                player.getDialogueManager().showItemMessage("You've found a Daconia rock!", new ItemStack(793, 1));
                player.getInventoryManager().addOrDropItem(new ItemStack(793, 1));
            } else {
                object = player;
                ((Player)object).packetSender.sendGameMessage("You search the root but don't find anything.");
            }
""",
        """            ArrayList object = new ArrayList(this.daconiaRootSearchPositions);
            Collections.shuffle(object, new Random(player.bK));
            Position position = (Position)object.get(0);
            if (n2 == position.getX() && n3 == position.getY() && !player.ownsItem(793)) {
                player.getDialogueManager().showItemMessage("You've found a Daconia rock!", new ItemStack(793, 1));
                player.getInventoryManager().addOrDropItem(new ItemStack(793, 1));
            } else {
                Player player4 = player;
                player4.packetSender.sendGameMessage("You search the root but don't find anything.");
            }
""",
        path,
    )
    repairs += count
    count = text.count("while (n2 < player.length)")
    if count:
        text = text.replace("while (n2 < player.length)", "while (n2 < stringArray.length)")
        repairs += count
    path.write_text(text, encoding="utf-8")

    path = source_root / "com/rs2/model/quest/impl/HeroesQuest.java"
    text = path.read_text(encoding="utf-8")
    text, count = replace_exact(text, "(Player)stringArray", "player", path)
    repairs += count
    text, count = replace_exact(
        text,
        """    public final boolean handleNpcKill(Player object, int n, int n2) {
        if (n == 792) {
            object = Npc.findByDefinitionId(792);
            object = new GroundItem(new ItemStack(1588, 1), ((Entity)object).getPosition(), false, true);
            GroundItemManager.getInstance().spawn((GroundItem)object);
            return true;
        }
        return false;
    }
""",
        """    public final boolean handleNpcKill(Player player, int n, int n2) {
        if (n == 792) {
            Npc npc = Npc.findByDefinitionId(792);
            GroundItem groundItem = new GroundItem(new ItemStack(1588, 1), npc.getPosition(), false, true);
            GroundItemManager.getInstance().spawn(groundItem);
            return true;
        }
        return false;
    }
""",
        path,
    )
    repairs += count
    path.write_text(text, encoding="utf-8")

    for relative_path, item_id, npc_id in (
        ("com/rs2/model/quest/impl/FamilyCrestQuest.java", 781, 667),
        ("com/rs2/model/quest/impl/WitchsPotionQuest.java", 300, 47),
        ("com/rs2/model/quest/impl/PriestInPerilQuest.java", 2944, 1046),
    ):
        path = source_root / relative_path
        text = path.read_text(encoding="utf-8")
        text, count = replace_exact(
            text,
            f"""            object = new GroundItem(new ItemStack({item_id}, 1), (Entity)object, position);
            GroundItemManager.getInstance().spawn((GroundItem)object);
""",
            f"""            GroundItem groundItem = new GroundItem(new ItemStack({item_id}, 1), (Entity)object, position);
            GroundItemManager.getInstance().spawn(groundItem);
""",
            path,
        )
        repairs += count
        path.write_text(text, encoding="utf-8")

    path = source_root / "com/rs2/model/quest/impl/DragonSlayerQuest.java"
    text = path.read_text(encoding="utf-8")
    text, count = replace_exact(
        text,
        """                object = new GroundItem(new ItemStack(1536, 1), (Entity)object, entity.getPosition());
                GroundItemManager.getInstance().spawn((GroundItem)object);
""",
        """                GroundItem groundItem = new GroundItem(new ItemStack(1536, 1), (Entity)object, entity.getPosition());
                GroundItemManager.getInstance().spawn(groundItem);
""",
        path,
    )
    repairs += count
    path.write_text(text, encoding="utf-8")

    path = source_root / "com/rs2/model/quest/impl/HolyGrailQuest.java"
    text = path.read_text(encoding="utf-8")
    text, count = replace_exact(
        text,
        """                object = new GroundItem(new ItemStack(16, 1), (Entity)object, new Position(3107, 3359, 2));
                GroundItemManager.getInstance().spawn((GroundItem)object);
                if (n4 < 8) {
                    GroundItemManager.getInstance().spawn((GroundItem)object);
                }
""",
        """                GroundItem groundItem = new GroundItem(new ItemStack(16, 1), (Entity)object, new Position(3107, 3359, 2));
                GroundItemManager.getInstance().spawn(groundItem);
                if (n4 < 8) {
                    GroundItemManager.getInstance().spawn(groundItem);
                }
""",
        path,
    )
    repairs += count
    path.write_text(text, encoding="utf-8")

    path = source_root / "com/rs2/model/quest/impl/LostCityQuest.java"
    text = path.read_text(encoding="utf-8")
    text, count = replace_exact(
        text,
        """                entity = Npc.findByDefinitionId(654);
                ((Npc)entity).setActive(false);
                World.unregisterNpc((Npc)entity);
""",
        """                Npc npc = Npc.findByDefinitionId(654);
                npc.setActive(false);
                World.unregisterNpc(npc);
""",
        path,
    )
    repairs += count
    path.write_text(text, encoding="utf-8")

    path = source_root / "com/rs2/model/quest/impl/MerlinsCrystalQuest.java"
    text = path.read_text(encoding="utf-8")
    text, count = replace_exact(
        text,
        """                new DynamicObject(ServerSettings.placeholderObjectId, 2767, 3493, player.getPosition().getPlane(), 0, 10, n2, 100);
                Entity entity = new Npc(249);
                GameplayHelper.replaceOwnedRoamingNpcAtPosition(player, entity, 2767, 3493, 2, -1, false, false);
                DialogueManager.continueDialogue(player, 249, 1, 0);
                entity = player;
                ((Player)entity).packetSender.sendGameMessage("You attempt to smash the crystal...");
                entity = player;
                ((Player)entity).packetSender.sendGameMessage("... and it shatters under the force of Excalibur!");
""",
        """                new DynamicObject(ServerSettings.placeholderObjectId, 2767, 3493, player.getPosition().getPlane(), 0, 10, n2, 100);
                Npc npc = new Npc(249);
                GameplayHelper.replaceOwnedRoamingNpcAtPosition(player, npc, 2767, 3493, 2, -1, false, false);
                DialogueManager.continueDialogue(player, 249, 1, 0);
                player.packetSender.sendGameMessage("You attempt to smash the crystal...");
                player.packetSender.sendGameMessage("... and it shatters under the force of Excalibur!");
""",
        path,
    )
    repairs += count
    path.write_text(text, encoding="utf-8")

    path = source_root / "com/rs2/model/quest/impl/RestlessGhostQuest.java"
    text = path.read_text(encoding="utf-8")
    if "import com.rs2.util.GameUtil;\n" not in text:
        text = text.replace("import com.rs2.model.task.TickTask;\n", "import com.rs2.model.task.TickTask;\nimport com.rs2.util.GameUtil;\n")
        repairs += 1
    text, count = replace_exact(
        text,
        """            Object object2 = object;
            ((Player)object2).packetSender.sendGameMessage("You put the skull in the coffin.");
            ObjectManager.getInstance().removeDynamicObjectAt(3249, 3192, 0, 0);
            ObjectManager.getInstance().addDynamicObject(new DynamicObject(2145, 3249, 3192, 0, 0, 10, 2145, 999999999), true);
            object2 = new ArrayList<Npc>();
            ((ArrayList)object2).add(Npc.findByDefinitionId(457));
            object = new RestlessGhostCutscene((Player)object, (ArrayList)object2);
            ((Cutscene)object).startCutscene();
""",
        """            Player player = (Player)object;
            player.packetSender.sendGameMessage("You put the skull in the coffin.");
            ObjectManager.getInstance().removeDynamicObjectAt(3249, 3192, 0, 0);
            ObjectManager.getInstance().addDynamicObject(new DynamicObject(2145, 3249, 3192, 0, 0, 10, 2145, 999999999), true);
            ArrayList<Npc> object2 = new ArrayList<Npc>();
            object2.add(Npc.findByDefinitionId(457));
            Cutscene cutscene = new RestlessGhostCutscene(player, object2);
            cutscene.startCutscene();
""",
        path,
    )
    repairs += count
    text, count = replace_exact(text, "                    n4 = RestlessGhostQuest.a((Position)position2, (Position)position);\n", "                    n4 = GameUtil.getDistance(position2, position);\n", path)
    repairs += count
    path.write_text(text, encoding="utf-8")

    path = source_root / "com/rs2/model/quest/impl/WitchsHouseQuest.java"
    text = path.read_text(encoding="utf-8")
    text, count = replace_exact(
        text,
        """            if (player.getInventoryManager().containsItem(2411)) {
                Entity entity = player;
                entity.packetSender.openSingleDoor(n2, 2934, 3463, 0);
                entity = player;
                entity.packetSender.queueRelativeMovementStep(player.getPosition().getX() < 2934 ? 1 : -1, 0, true);
                if (n3 == 3) {
                    entity = new Npc(897);
                    GameplayHelper.spawnOwnedNpcAtPosition(player, new Position(2935, 3462, 0), (Npc)entity, false, false);
                }
""",
        """            if (player.getInventoryManager().containsItem(2411)) {
                Player player2 = player;
                player2.packetSender.openSingleDoor(n2, 2934, 3463, 0);
                player2 = player;
                player2.packetSender.queueRelativeMovementStep(player.getPosition().getX() < 2934 ? 1 : -1, 0, true);
                if (n3 == 3) {
                    Npc npc = new Npc(897);
                    GameplayHelper.spawnOwnedNpcAtPosition(player, new Position(2935, 3462, 0), npc, false, false);
                }
""",
        path,
    )
    repairs += count
    text, count = replace_exact(
        text,
        """        if (n2 == 2410 && n == 901) {
            Entity entity = player;
            if (entity.H.getNpcId() == 901) {
                player.getDialogueManager().showFourLineStatement("You attach the magnet to the mouse's harness. The mouse finishes", "the cheese and runs back into its hole. You hear some odd noises", "from inside the walls. There is a strange whirring noise from above", "the door frame.");
                entity = player;
                entity = entity.H;
                GameplayHelper.unregisterTemporaryNpc((Npc)entity);
                player.pendingGameMode = 2410;
                player.getInventoryManager().removeItem(new ItemStack(2410, 1));
                player.getDialogueManager().finishDialogue();
                return true;
            }
        }
""",
        """        if (n2 == 2410 && n == 901) {
            if (player.H.getNpcId() == 901) {
                player.getDialogueManager().showFourLineStatement("You attach the magnet to the mouse's harness. The mouse finishes", "the cheese and runs back into its hole. You hear some odd noises", "from inside the walls. There is a strange whirring noise from above", "the door frame.");
                Npc npc = player.H;
                GameplayHelper.unregisterTemporaryNpc(npc);
                player.pendingGameMode = 2410;
                player.getInventoryManager().removeItem(new ItemStack(2410, 1));
                player.getDialogueManager().finishDialogue();
                return true;
            }
        }
""",
        path,
    )
    repairs += count
    path.write_text(text, encoding="utf-8")

    path = source_root / "com/rs2/model/quest/impl/ErnestTheChickenQuest.java"
    text = path.read_text(encoding="utf-8")
    text, count = replace_exact(
        text,
        """                    object = Npc.findByDefinitionId(286);
                    ((Entity)object).getMovementQueue().addStep(new Position(((Entity)object).getPosition().getX() < 3108 ? 3107 : 3111, 3367, 2));
                    Object object2 = ((Entity)object).getPosition();
                    object = new Position(((Entity)object).getPosition().getX() < 3108 ? 3107 : 3111, 3367, 2);
                    Position position = object2;
                    object2 = object;
                    object = position;
                    int n5 = GameUtil.getDistance(position, (Position)object2);
""",
        """                    Npc npc = Npc.findByDefinitionId(286);
                    npc.getMovementQueue().addStep(new Position(npc.getPosition().getX() < 3108 ? 3107 : 3111, 3367, 2));
                    Position position = npc.getPosition();
                    Position position2 = new Position(npc.getPosition().getX() < 3108 ? 3107 : 3111, 3367, 2);
                    int n5 = GameUtil.getDistance(position, position2);
""",
        path,
    )
    repairs += count
    path.write_text(text, encoding="utf-8")

    path = source_root / "com/rs2/model/quest/impl/PouletmorphMachineTransformTask.java"
    text = path.read_text(encoding="utf-8")
    if "import com.rs2.util.GameUtil;\n" not in text:
        text = text.replace("import com.rs2.model.task.TickTask;\n", "import com.rs2.model.task.TickTask;\nimport com.rs2.util.GameUtil;\n")
        repairs += 1
    text, count = replace_exact(text, "        int n2 = ErnestTheChickenQuest.a((Position)position2, (Position)position);\n", "        int n2 = GameUtil.getDistance(position2, position);\n", path)
    repairs += count
    path.write_text(text, encoding="utf-8")
    return repairs


def repair_object_interaction_packet_handler_locals(source_root: Path) -> int:
    path = source_root / "com/rs2/net/packet/handler/ObjectInteractionPacketHandler.java"
    text = path.read_text(encoding="utf-8")
    start_marker = "    public final void handle(Player player, IncomingPacket object) {\n"
    end_marker = "\n}\n"
    try:
        start = text.index(start_marker)
        end = text.index(end_marker, start)
    except ValueError as exc:
        raise SystemExit(f"{path}: expected object interaction handler region not found") from exc
    text = text[:start] + """    public final void handle(Player player, IncomingPacket incomingPacket) {
        if (player.isActionLocked()) {
            return;
        }
        Player player2 = player;
        player2.packetSender.closeInterfaces();
        player.resetInteractionState();
        switch (incomingPacket.getOpcode()) {
            case 192: {
                player.setSelectedItemInterfaceId(incomingPacket.getReader().readSignedShort());
                player.setInteractionTargetId(incomingPacket.getReader().readSignedShort(true, ByteOrder.LITTLE));
                player.setInteractionTargetY(incomingPacket.getReader().readShort(true, ByteTransform.ADD, ByteOrder.LITTLE));
                player.setSelectedItemSlot(incomingPacket.getReader().readSignedShort(ByteOrder.LITTLE));
                player.setInteractionTargetX(incomingPacket.getReader().readShort(true, ByteTransform.ADD, ByteOrder.LITTLE));
                player.setInteractionTargetPlane(player.getPosition().getPlane());
                player.setSelectedItemId(incomingPacket.getReader().readSignedShort());
                if (player.getSelectedItemSlot() <= 28) {
                    ItemStack itemStack = player.getInventoryManager().getContainer().getItemAt(player.getSelectedItemSlot());
                    if (itemStack == null || itemStack.getId() != player.getSelectedItemId()) break;
                    InterfaceDefinition interfaceDefinition = InterfaceDefinition.forId(player.getSelectedItemInterfaceId());
                    if (player.isInterfaceOpen(interfaceDefinition)) {
                        if (player.getPlayerRights() > 1 && ServerSettings.debugModeEnabled) {
                            System.out.println("item: " + player.getSelectedItemId() + " object: " + player.getInteractionTargetId());
                        }
                        EntityTargetMovement.clearMovementTarget(player);
                        InteractionDispatcher.setCurrentInteractionType(InteractionType.ITEM_ON_OBJECT);
                        InteractionDispatcher.dispatchCurrentInteraction(player);
                    }
                }
                return;
            }
            case 132: {
                player.setInteractionTargetX(incomingPacket.getReader().readShort(true, ByteTransform.ADD, ByteOrder.LITTLE));
                player.setInteractionTargetId(incomingPacket.getReader().readSignedShort());
                player.setInteractionTargetY(incomingPacket.getReader().readSignedShort(ByteTransform.ADD));
                player.setInteractionTargetPlane(player.getPosition().getPlane());
                if (player.getPlayerRights() > 1 && ServerSettings.debugModeEnabled) {
                    System.out.println("first click id = " + player.getInteractionTargetId() + " x = " + player.getInteractionTargetX() + " y = " + player.getInteractionTargetY() + " type " + SkillActionHelper.getObjectType(player.getInteractionTargetId(), player.getInteractionTargetX(), player.getInteractionTargetY(), player.getPosition().getPlane()));
                }
                EntityTargetMovement.clearMovementTarget(player);
                InteractionDispatcher.setCurrentInteractionType(InteractionType.FIRST_OBJECT);
                InteractionDispatcher.dispatchCurrentInteraction(player);
                return;
            }
            case 252: {
                player.setInteractionTargetId(incomingPacket.getReader().readSignedShort(ByteTransform.ADD, ByteOrder.LITTLE));
                player.setInteractionTargetY(incomingPacket.getReader().readSignedShort(true, ByteOrder.LITTLE));
                player.setInteractionTargetX(incomingPacket.getReader().readSignedShort(ByteTransform.ADD));
                player.setInteractionTargetPlane(player.getPosition().getPlane());
                if (player.getPlayerRights() > 1 && ServerSettings.debugModeEnabled) {
                    System.out.println("second click id = " + player.getInteractionTargetId() + " x = " + player.getInteractionTargetX() + " y = " + player.getInteractionTargetY());
                }
                EntityTargetMovement.clearMovementTarget(player);
                ObjectManager.prepareObjectInteractionMovement(player, player.getInteractionTargetId(), player.getInteractionTargetX(), player.getInteractionTargetY());
                InteractionDispatcher.setCurrentInteractionType(InteractionType.SECOND_OBJECT);
                InteractionDispatcher.dispatchCurrentInteraction(player);
                return;
            }
            case 70: {
                player.setInteractionTargetX(incomingPacket.getReader().readSignedShort(true, ByteOrder.LITTLE));
                player.setInteractionTargetY(incomingPacket.getReader().readSignedShort());
                player.setInteractionTargetId(incomingPacket.getReader().readSignedShort(ByteTransform.ADD, ByteOrder.LITTLE));
                player.setInteractionTargetPlane(player.getPosition().getPlane());
                if (player.getPlayerRights() > 1 && ServerSettings.debugModeEnabled) {
                    System.out.println("third click id = " + player.getInteractionTargetId() + " x = " + player.getInteractionTargetX() + " y = " + player.getInteractionTargetY());
                }
                EntityTargetMovement.clearMovementTarget(player);
                ObjectManager.prepareObjectInteractionMovement(player, player.getInteractionTargetId(), player.getInteractionTargetX(), player.getInteractionTargetY());
                InteractionDispatcher.setCurrentInteractionType(InteractionType.THIRD_OBJECT);
                InteractionDispatcher.dispatchCurrentInteraction(player);
                return;
            }
            case 234: {
                player.setInteractionTargetX(incomingPacket.getReader().readSignedShort(ByteTransform.ADD, ByteOrder.LITTLE));
                player.setInteractionTargetId(incomingPacket.getReader().readSignedShort(ByteTransform.ADD));
                player.setInteractionTargetY(incomingPacket.getReader().readSignedShort(ByteTransform.ADD, ByteOrder.LITTLE));
                player.setInteractionTargetPlane(player.getPosition().getPlane());
                if (player.getPlayerRights() > 1 && ServerSettings.debugModeEnabled) {
                    System.out.println("fourth click id = " + player.getInteractionTargetId() + " x = " + player.getInteractionTargetX() + " y = " + player.getInteractionTargetY());
                }
                EntityTargetMovement.clearMovementTarget(player);
                ObjectManager.prepareObjectInteractionMovement(player, player.getInteractionTargetId(), player.getInteractionTargetX(), player.getInteractionTargetY());
                InteractionDispatcher.setCurrentInteractionType(InteractionType.FOURTH_OBJECT);
                InteractionDispatcher.dispatchCurrentInteraction(player);
                return;
            }
            case 35: {
                PacketReader packetReader = incomingPacket.getReader();
                int n = packetReader.readSignedShort(ByteOrder.LITTLE);
                int n2 = packetReader.readSignedShort(ByteTransform.ADD, ByteOrder.BIG);
                int n3 = packetReader.readSignedShort(ByteTransform.ADD, ByteOrder.BIG);
                int n4 = packetReader.readSignedShort(ByteOrder.LITTLE);
                player.setInteractionTargetX(n);
                player.setInteractionTargetId(n4);
                player.setInteractionTargetY(n3);
                player.setInteractionTargetPlane(player.getPosition().getPlane());
                player.setInteractionSpellButtonId(n2);
                if (!SkillActionHelper.isObjectPresent(n4, n, n3, player.getPosition().getPlane())) break;
                EntityTargetMovement.clearMovementTarget(player);
                ObjectManager.prepareObjectInteractionMovement(player, player.getInteractionTargetId(), player.getInteractionTargetX(), player.getInteractionTargetY());
                InteractionDispatcher.setCurrentInteractionType(InteractionType.SPELL_ON_OBJECT);
                InteractionDispatcher.dispatchCurrentInteraction(player);
            }
        }
    }
""" + text[end:]
    path.write_text(text, encoding="utf-8")
    return 1


def repair_small_bot_cluster_locals(source_root: Path) -> int:
    repairs = 0

    path = source_root / "com/rs2/bot/ClanWarsBotManager.java"
    text = path.read_text(encoding="utf-8")
    for old, new in (
        ("    public static ArrayList clanWarsTeamOneBots = new ArrayList();\n", "    public static ArrayList<Player> clanWarsTeamOneBots = new ArrayList<Player>();\n"),
        ("    public static ArrayList clanWarsTeamTwoBots = new ArrayList();\n", "    public static ArrayList<Player> clanWarsTeamTwoBots = new ArrayList<Player>();\n"),
        ("        Object object = clanWarsTeamOneBots.contains(player2) ? clanWarsTeamSpawnAreas[0] : clanWarsTeamSpawnAreas[1];\n", "        RectangularArea object = clanWarsTeamOneBots.contains(player2) ? clanWarsTeamSpawnAreas[0] : clanWarsTeamSpawnAreas[1];\n"),
    ):
        text, count = replace_exact(text, old, new, path)
        repairs += count
    path.write_text(text, encoding="utf-8")

    path = source_root / "com/rs2/bot/DropPartyBotManager.java"
    text = path.read_text(encoding="utf-8")

    def replace_method(start_marker: str, end_marker: str, method: str) -> None:
        nonlocal text, repairs
        try:
            start = text.index(start_marker)
            end = text.index(end_marker, start)
        except ValueError as exc:
            raise SystemExit(f"{path}: expected drop party method region not found") from exc
        text = text[:start] + method + text[end:]
        repairs += 1

    replace_method(
        "    public static void startDropPartyTick(Player object) {\n",
        "\n    public static void prepareDropPartyCombatLoadout",
        """    public static void startDropPartyTick(Player player) {
        player.botPublicChatMessage = "Follow for Drop party!";
        player.botPublicChatColor = GameUtil.randomInt(12);
        int[] nArray = new int[3];
        nArray[1] = 1;
        nArray[2] = 3;
        int[] object2 = nArray;
        int n = GameUtil.randomInt(3);
        player.botPublicChatEffect = object2[n];
        if (player.dropPartyLeader) {
            World.getTaskScheduler().schedule(new DropPartyLeaderTickTask(3, player));
            return;
        }
        Player player2 = (Player)dropPartyParticipants.get(0);
        World.getTaskScheduler().schedule(new DropPartyFollowerTickTask(3, player, player2));
    }
""",
    )
    replace_method(
        "    public static void prepareDropPartyInventory(Player player) {\n",
        "\n}\n",
        """    public static void prepareDropPartyInventory(Player player) {
        int n;
        player.getInventoryManager().getContainer().clear();
        player.getEquipmentManager().getContainer().clear();
        if (player.dropPartyLeader) {
            int n2;
            n = 0;
            ArrayList<GameplayHelper> arrayList = new ArrayList<GameplayHelper>();
            for (Object object32 : dropPartyRewardPool) {
                arrayList.add((GameplayHelper)object32);
            }
            ArrayList<GameplayHelper> object32 = new ArrayList<GameplayHelper>();
            for (Object object22 : valuableDropPartyRewardPool) {
                object32.add((GameplayHelper)object22);
            }
            ArrayList<GameplayHelper> object22 = new ArrayList<GameplayHelper>();
            ArrayList<GameplayHelper> object4 = arrayList;
            int n3 = 0;
            while (n3 < leaderDropItemCount) {
                int n4;
                if (n3 == leaderDropItemCount - 1 && n < valuableDropMinValue) {
                    object4 = object32;
                }
                int n5 = GameUtil.randomInt(object4.size());
                GameplayHelper object = (GameplayHelper)object4.get(n5);
                object22.add(object);
                arrayList.remove(object);
                if (object32.contains(object)) {
                    object32.remove(object);
                }
                if ((n4 = GrandExchangeManager.getGuidePrice(object.getTradeAdvertItemId())) > n) {
                    n = n4;
                    player.botAdvertItemId = object.getTradeAdvertItemId();
                }
                ++n3;
            }
            n = n2 = n;
            n2 = baseDropPartySize;
            if (n >= 10000 && n < 250000) {
                n2 = 6;
            }
            if (n >= 25000 && n < 100000) {
                n2 = 8;
            }
            if (n >= 100000 && n < 200000) {
                n2 = 10;
            }
            if (n >= 200000 && n < 400000) {
                n2 = 12;
            }
            if (n >= 400000 && n < 600000) {
                n2 = 14;
            }
            if (n >= 600000 && n < 800000) {
                n2 = 16;
            }
            if (n >= 800000 && n < 1000000) {
                n2 = 18;
            }
            if (n >= 1000000) {
                n2 = 20;
            }
            if (n2 > ServerSettings.otherBotCount) {
                n2 = ServerSettings.otherBotCount;
            }
            targetDropPartySize = n2;
            Collections.shuffle(object22);
            Iterator<GameplayHelper> iterator = object22.iterator();
            while (iterator.hasNext()) {
                GameplayHelper gameplayHelper = iterator.next();
                player.getInventoryManager().addItem(new ItemStack(gameplayHelper.getTradeAdvertItemId(), 1));
            }
            ItemDefinition itemDefinition = ItemDefinition.forId(player.botAdvertItemId);
            player.botPublicChatMessage = "Follow for Drop party! Best drop: " + itemDefinition.getDisplayName();
        }
        Player player2 = player;
        if (player2.botCombatStyle == 0) {
            BotCombatLoadoutManager.prepareMeleeLoadout(player2);
            BotCombatLoadoutManager.equipGlovesAndBoots(player2);
        } else if (player2.botCombatStyle == 2) {
            BotCombatLoadoutManager.prepareMagicLoadout(player2);
        } else if (player2.botCombatStyle == 1) {
            BotCombatLoadoutManager.prepareRangedLoadout(player2);
            int n6 = player2.getSkillManager().getCurrentLevels()[4] >= 40 ? 1731 : (GameUtil.randomInt(3) == 0 ? 1478 : 1729);
            if (!BotCombatHelper.isFreeToPlayWorld() && player2.getCombatLevel() >= 60 && GameUtil.randomInt(2) == 0) {
                n6 = 1712;
            }
            player2.getEquipmentManager().getContainer().setItem(2, new ItemStack(n6));
        }
        BotCombatLoadoutManager.equipRandomCape(player2);
        player.getInventoryManager().refresh();
        player.getEquipmentManager().refresh();
    }
""",
    )
    path.write_text(text, encoding="utf-8")

    path = source_root / "com/rs2/bot/route/BotWorldRouteWalker.java"
    text = path.read_text(encoding="utf-8")

    def replace_route_method(start_marker: str, end_marker: str, method: str) -> None:
        nonlocal text, repairs
        try:
            start = text.index(start_marker)
            end = text.index(end_marker, start)
        except ValueError as exc:
            raise SystemExit(f"{path}: expected route walker method region not found") from exc
        text = text[:start] + method + text[end:]
        repairs += 1

    replace_route_method(
        "    private static void findConnectingWorldRoute(Player player, ArrayList object) {\n",
        "\n    public static boolean findWorldRoute",
        """    private static void findConnectingWorldRoute(Player player, ArrayList object) {
        BotTaskDefinition botTaskDefinition = player.currentBotTask;
        Position targetPosition = botTaskDefinition.getStartPosition();
        BotWorldRouteChoice bestStartingChoice = null;
        BotWorldRouteChoice bestConnectingChoice = null;
        double bestScore = 0.0;
        int currentDistance = GameUtil.getDistance(player.getPosition(), targetPosition);
        Iterator iterator = object.iterator();
        while (iterator.hasNext()) {
            BotWorldRouteChoice currentChoice = (BotWorldRouteChoice)iterator.next();
            ArrayList<BotWorldRouteChoice> candidateChoices = new ArrayList<BotWorldRouteChoice>();
            ArrayList<BotWorldRouteChoice> improvingChoices = new ArrayList<BotWorldRouteChoice>();
            Position routeEndPosition = currentChoice.reversed ? currentChoice.route.getStartPosition() : currentChoice.route.getEndPosition();
            BotWorldRoute[] botWorldRouteArray = BotWorldRoute.values();
            int n2 = botWorldRouteArray.length;
            int n3 = 0;
            while (n3 < n2) {
                BotWorldRoute route = botWorldRouteArray[n3];
                if (route != currentChoice.route) {
                    int startDistance = GameUtil.getDistance(route.getStartPosition(), routeEndPosition);
                    int endDistance = GameUtil.getDistance(route.getEndPosition(), routeEndPosition);
                    if (startDistance < endDistance) {
                        if (startDistance <= routeDistanceThreshold) {
                            candidateChoices.add(new BotWorldRouteChoice(route, false));
                        }
                    } else if (endDistance <= routeDistanceThreshold) {
                        candidateChoices.add(new BotWorldRouteChoice(route, true));
                    }
                }
                ++n3;
            }
            if (candidateChoices.size() > 1) {
                int currentRouteIndex = -1;
                n3 = 0;
                for (BotWorldRouteChoice botWorldRouteChoice : candidateChoices) {
                    if (player.currentWorldRouteChoice != null && player.currentWorldRouteChoice.route == botWorldRouteChoice.route) {
                        currentRouteIndex = n3;
                    }
                    ++n3;
                }
                if (currentRouteIndex != -1) {
                    candidateChoices.remove(currentRouteIndex);
                }
            }
            if (candidateChoices.size() == 1) {
                BotWorldRouteChoice candidateChoice = (BotWorldRouteChoice)candidateChoices.get(0);
                Position candidateEndPosition = candidateChoice.reversed ? candidateChoice.route.getStartPosition() : candidateChoice.route.getEndPosition();
                if (GameUtil.getDistance(candidateEndPosition, targetPosition) < currentDistance) {
                    improvingChoices.add(candidateChoice);
                }
            } else {
                for (BotWorldRouteChoice botWorldRouteChoice : candidateChoices) {
                    if (player.currentWorldRouteChoice != null && player.currentWorldRouteChoice.route == botWorldRouteChoice.route) continue;
                    Position candidateEndPosition = botWorldRouteChoice.reversed ? botWorldRouteChoice.route.getStartPosition() : botWorldRouteChoice.route.getEndPosition();
                    int candidateDistance = GameUtil.getDistance(candidateEndPosition, targetPosition);
                    if (candidateDistance >= currentDistance) continue;
                    improvingChoices.add(botWorldRouteChoice);
                }
            }
            for (BotWorldRouteChoice botWorldRouteChoice : improvingChoices) {
                Position candidateEndPosition = botWorldRouteChoice.reversed ? botWorldRouteChoice.route.getStartPosition() : botWorldRouteChoice.route.getEndPosition();
                int candidateDistance = GameUtil.getDistance(candidateEndPosition, targetPosition);
                double improvement = currentDistance - candidateDistance;
                double currentRouteDistance = currentChoice.route.getDistance();
                double candidateRouteDistance = botWorldRouteChoice.route.getDistance();
                double score = improvement / (candidateRouteDistance + currentRouteDistance);
                if (score < bestScore) continue;
                bestScore = score;
                bestStartingChoice = currentChoice;
                bestConnectingChoice = botWorldRouteChoice;
            }
        }
        if (bestStartingChoice != null && bestConnectingChoice != null) {
            if (!player.getMovementQueue().isRunning() && player.getRunEnergyPercent() >= 80) {
                player.getMovementQueue().setRunning(true);
            }
            BotWorldRouteWalker.startWorldRoute(player, bestStartingChoice);
            return;
        }
        System.out.println("Error! Could not find suitable overworld walk path for player: " + player.getUsername() + " at: " + player.getPosition() + " " + player.currentBotTask);
    }
""",
    )
    replace_route_method(
        "    public static boolean findWorldRoute(Player player) {\n",
        "\n    public static int getRouteIndex",
        """    public static boolean findWorldRoute(Player player) {
        if (player.currentBotTask == null) {
            return true;
        }
        BotTaskDefinition botTaskDefinition = player.currentBotTask;
        Position targetPosition = botTaskDefinition.getStartPosition();
        int currentDistance = GameUtil.getDistance(player.getPosition(), targetPosition);
        if (currentDistance <= routeDistanceThreshold) {
            player.currentWorldRouteChoice = null;
            GameplayHelper.startBotTaskRoute(player);
            return true;
        }
        ArrayList<BotWorldRouteChoice> nearbyChoices = new ArrayList<BotWorldRouteChoice>();
        BotWorldRouteChoice bestChoice = null;
        BotWorldRoute[] botWorldRouteArray = BotWorldRoute.values();
        int n3 = botWorldRouteArray.length;
        int n4 = 0;
        while (n4 < n3) {
            BotWorldRoute botWorldRoute = botWorldRouteArray[n4];
            int startDistance = GameUtil.getDistance(botWorldRoute.getStartPosition(), player.getPosition());
            int endDistance = GameUtil.getDistance(botWorldRoute.getEndPosition(), player.getPosition());
            if (startDistance < endDistance) {
                if (startDistance <= routeDistanceThreshold) {
                    nearbyChoices.add(new BotWorldRouteChoice(botWorldRoute, false));
                }
            } else if (endDistance <= routeDistanceThreshold) {
                nearbyChoices.add(new BotWorldRouteChoice(botWorldRoute, true));
            }
            ++n4;
        }
        ArrayList<BotWorldRouteChoice> improvingChoices = new ArrayList<BotWorldRouteChoice>();
        n4 = 0;
        if (nearbyChoices.size() > 1) {
            n3 = -1;
            int n6 = 0;
            for (BotWorldRouteChoice botWorldRouteChoice : nearbyChoices) {
                if (player.currentWorldRouteChoice != null && player.currentWorldRouteChoice.route == botWorldRouteChoice.route) {
                    n3 = n6;
                }
                ++n6;
            }
            if (n3 != -1) {
                nearbyChoices.remove(n3);
            }
        }
        if (nearbyChoices.size() == 1) {
            bestChoice = (BotWorldRouteChoice)nearbyChoices.get(0);
            Position position = bestChoice.reversed ? bestChoice.route.getStartPosition() : bestChoice.route.getEndPosition();
            n4 = GameUtil.getDistance(position, targetPosition);
        } else {
            for (BotWorldRouteChoice botWorldRouteChoice : nearbyChoices) {
                if (player.currentWorldRouteChoice != null && player.currentWorldRouteChoice.route == botWorldRouteChoice.route) continue;
                Position position = botWorldRouteChoice.reversed ? botWorldRouteChoice.route.getStartPosition() : botWorldRouteChoice.route.getEndPosition();
                int candidateDistance = GameUtil.getDistance(position, targetPosition);
                if (candidateDistance >= currentDistance) continue;
                improvingChoices.add(botWorldRouteChoice);
            }
            double bestScore = 0.0;
            Iterator<BotWorldRouteChoice> iterator = improvingChoices.iterator();
            while (iterator.hasNext()) {
                BotWorldRouteChoice candidateChoice = iterator.next();
                Position position = candidateChoice.reversed ? candidateChoice.route.getStartPosition() : candidateChoice.route.getEndPosition();
                int candidateDistance = GameUtil.getDistance(position, targetPosition);
                double improvement = currentDistance - candidateDistance;
                double routeDistance = candidateChoice.route.getDistance();
                double score = improvement / routeDistance;
                if (score < bestScore) continue;
                bestScore = score;
                bestChoice = candidateChoice;
                n4 = candidateDistance;
            }
        }
        if (bestChoice != null) {
            if (n4 <= routeDistanceThreshold) {
                player.botTaskState = "world walk finish";
                player.getMovementQueue().setRunning(false);
            }
            if (!player.getMovementQueue().isRunning() && player.getRunEnergyPercent() >= 80) {
                player.getMovementQueue().setRunning(true);
            }
            BotWorldRouteWalker.startWorldRoute(player, bestChoice);
        } else if (nearbyChoices.size() > 0) {
            BotWorldRouteWalker.findConnectingWorldRoute(player, nearbyChoices);
        } else {
            System.out.println("Error! Could not find suitable overworld walk path for player: " + player.getUsername() + " at: " + player.getPosition() + " " + player.currentBotTask);
            return false;
        }
        return true;
    }
""",
    )
    replace_route_method(
        "    public static void continueWorldRoute(Player player, BotWorldRouteChoice object) {\n",
        "\n    private static void startWorldRoute",
        """    public static void continueWorldRoute(Player player, BotWorldRouteChoice object) {
        BotWorldRouteChoice botWorldRouteChoice = object;
        BotWorldRoute botWorldRoute = botWorldRouteChoice.route;
        player.currentWorldRouteChoice = object;
        if (botWorldRoute.getRoute() != null) {
            botWorldRouteChoice = object;
            player.currentBotRoute = botWorldRouteChoice.reversed ? botWorldRoute.getRoute().reversed() : botWorldRoute.getRoute();
        } else {
            botWorldRouteChoice = object;
            BotRoute botRoute = player.currentBotRoute = botWorldRouteChoice.reversed ? botWorldRoute.getSegments()[player.botPathSegmentIndex].reversed() : botWorldRoute.getSegments()[player.botPathSegmentIndex];
        }
        if (botWorldRoute.getRouteNpcId() != -1) {
            player.botTargetNpcId = botWorldRoute.getRouteNpcId();
        }
        Position position = player.currentBotRoute.waypoints[player.botPathWaypointIndex];
        int n = GameUtil.getDistance(player.getPosition(), position);
        if (n > routeDistanceThreshold) {
            System.out.println("Detected possibly frozen bot: " + player.getUsername() + " at: " + player.getPosition() + ", trying to apply fix.");
            BotWorldRouteWalker.findWorldRoute(player);
            return;
        }
        player.continueBotRoute();
    }
""",
    )
    path.write_text(text, encoding="utf-8")

    path = source_root / "com/rs2/bot/combat/BotPvpTargetSearchTickTask.java"
    text = path.read_text(encoding="utf-8")
    for old, new in (
        (
            """                        object = new String[]{"sure", "okay", "ok", "k"};
                        ((Player)object2).queuePublicChatMessage(object[GameUtil.randomInt(4)]);
""",
            """                        String[] responses = new String[]{"sure", "okay", "ok", "k"};
                        ((Player)object2).queuePublicChatMessage(responses[GameUtil.randomInt(4)]);
""",
        ),
        (
            """            ArrayList<Object> arrayList = new ArrayList<Object>();
            Object object3 = World.players;
            n2 = World.players.length;
""",
            """            ArrayList<Player> arrayList = new ArrayList<Player>();
            Player[] playerArray = World.players;
            n2 = World.players.length;
""",
        ),
        ("                            object = object3[var3_12];\n", "                            object = playerArray[var3_12];\n"),
        ("                            arrayList.add(object);\n", "                            arrayList.add((Player)object);\n"),
        (
            """                object3 = player;
                if (player.currentGroup != null) {
                    object3 = player.currentGroup.leader;
                }
                if (!object3.isBot && BotCombatHelper.canTeamWithBotPvpPlayer(this.bot, (Player)object3, true) && !this.bot.botPvpRejectedTeamTargets.contains(object3)) {
                    this.bot.getMovementQueue().setRunning(true);
                    this.bot.getUpdateState().setFaceEntity(player.getEncodedIndex());
                    this.bot.setAttackRange(1);
                    this.bot.setMovementTarget(player);
                    this.bot.botPvpPendingTeamTarget = object3;
                    if (GameUtil.getDistance(this.bot.getPosition(), player.getPosition()) > 5) continue;
                    this.bot.queuePublicChatMessage("team?");
                    object3.botPvpTeamRequesters.add(this.bot);
                    this.bot.botPvpTeamInviteTicks = 1;
                    continue;
                }
""",
            """                Player teamTarget = player;
                if (player.currentGroup != null) {
                    teamTarget = player.currentGroup.leader;
                }
                if (!teamTarget.isBot && BotCombatHelper.canTeamWithBotPvpPlayer(this.bot, teamTarget, true) && !this.bot.botPvpRejectedTeamTargets.contains(teamTarget)) {
                    this.bot.getMovementQueue().setRunning(true);
                    this.bot.getUpdateState().setFaceEntity(player.getEncodedIndex());
                    this.bot.setAttackRange(1);
                    this.bot.setMovementTarget(player);
                    this.bot.botPvpPendingTeamTarget = teamTarget;
                    if (GameUtil.getDistance(this.bot.getPosition(), player.getPosition()) > 5) continue;
                    this.bot.queuePublicChatMessage("team?");
                    teamTarget.botPvpTeamRequesters.add(this.bot);
                    this.bot.botPvpTeamInviteTicks = 1;
                    continue;
                }
""",
        ),
    ):
        text, count = replace_exact(text, old, new, path)
        repairs += count
    path.write_text(text, encoding="utf-8")

    return repairs


def repair_ground_item_manager_locals(source_root: Path) -> int:
    path = source_root / "com/rs2/model/ground/GroundItemManager.java"
    text = path.read_text(encoding="utf-8")
    repairs = 0

    text, count = replace_exact(
        text,
        "    private LinkedList groundItems = new LinkedList();\n",
        "    private LinkedList<GroundItem> groundItems = new LinkedList<GroundItem>();\n",
        path,
    )
    repairs += count

    def replace_region(start_marker: str, end_marker: str, replacement: str) -> None:
        nonlocal text, repairs
        try:
            start = text.index(start_marker)
            end = text.index(end_marker, start)
        except ValueError as exc:
            raise SystemExit(f"{path}: expected method region not found: {start_marker!r}") from exc
        text = text[:start] + replacement + text[end:]
        repairs += 1

    replace_region(
        "    private static void mergeStackedItems(GroundItem groundItem, GroundItem groundItem2, Player[] object) {\n",
        "\n    private void removeForPlayers",
        """    private static void mergeStackedItems(GroundItem groundItem, GroundItem groundItem2, Player[] players) {
        LinkedList<Player> affectedPlayers = new LinkedList<Player>();
        if (groundItem == null || groundItem2 == null) {
            return;
        }
        if (players != null) {
            for (Player player : players) {
                if (player == null || !groundItem2.isVisibleTo(player)) {
                    continue;
                }
                if (player.getVisibleGroundItems().contains(groundItem2)) {
                    player.packetSender.sendGroundItemRemove(groundItem2);
                    player.getVisibleGroundItems().remove(groundItem2);
                }
                if (player.getVisibleGroundItems().contains(groundItem)) {
                    player.packetSender.sendGroundItemRemove(groundItem);
                    player.getVisibleGroundItems().remove(groundItem);
                }
                affectedPlayers.add(player);
            }
        }
        groundItem2.getItem().setAmount(groundItem2.getItem().getAmount() + groundItem.getItem().getAmount());
        groundItem2.getTimer().reset();
        for (Player player : affectedPlayers) {
            if (player == null || !groundItem.isVisibleTo(player)) {
                continue;
            }
            player.packetSender.sendGroundItemCreate(groundItem2);
            player.getVisibleGroundItems().add(groundItem2);
        }
    }
""",
    )

    replace_region(
        "    private void removeForPlayers(GroundItem groundItem, Player[] object) {\n",
        "\n    private static void showToPlayers",
        """    private void removeForPlayers(GroundItem groundItem, Player[] players) {
        if (groundItem == null) {
            return;
        }
        if (DropPartyBotManager.pendingDropPartyGroundItems.contains(groundItem)) {
            DropPartyBotManager.pendingDropPartyGroundItems.remove(groundItem);
        }
        for (Player player : players) {
            if (player == null || !groundItem.isVisibleTo(player)) {
                continue;
            }
            player.getVisibleGroundItems().remove(groundItem);
            player.packetSender.sendGroundItemRemove(groundItem);
        }
        this.groundItems.remove(groundItem);
    }
""",
    )

    replace_region(
        "    private static void showToPlayers(GroundItem groundItem, Player[] object) {\n",
        "\n    public final boolean removeForPickup",
        """    private static void showToPlayers(GroundItem groundItem, Player[] players) {
        if (groundItem == null) {
            return;
        }
        for (Player player : players) {
            if (player == null || !groundItem.isVisibleTo(player)) {
                continue;
            }
            player.getVisibleGroundItems().add(groundItem);
            player.packetSender.sendGroundItemCreate(groundItem);
        }
    }
""",
    )

    replace_region(
        "    public static boolean isVisible(Player object2, GroundItem groundItem) {\n",
        "\n    public static GroundItem findVisibleItem",
        """    public static boolean isVisible(Player player, GroundItem groundItem) {
        if (player == null || groundItem == null) {
            return false;
        }
        for (Object visibleObject : player.getVisibleGroundItems()) {
            if (!visibleObject.equals(groundItem)) {
                continue;
            }
            return true;
        }
        return false;
    }
""",
    )

    replace_region(
        "    public static GroundItem findVisibleItem(Player object2, int n, Position position) {\n",
        "\n    public static GroundItem findVisibleItemAt",
        """    public static GroundItem findVisibleItem(Player player, int n, Position position) {
        if (player == null) {
            return null;
        }
        for (Object visibleObject : player.getVisibleGroundItems()) {
            GroundItem visibleItem = (GroundItem)visibleObject;
            if (visibleItem.getItem().getId() != n || !visibleItem.getPosition().equals(position)) {
                continue;
            }
            return visibleItem;
        }
        return null;
    }
""",
    )

    replace_region(
        "    public static GroundItem findVisibleItemAt(Player object2, Position position) {\n",
        "\n    public static void clearVisibleItems",
        """    public static GroundItem findVisibleItemAt(Player player, Position position) {
        if (player == null) {
            return null;
        }
        for (Object visibleObject : player.getVisibleGroundItems()) {
            GroundItem visibleItem = (GroundItem)visibleObject;
            if (!visibleItem.getPosition().equals(position)) {
                continue;
            }
            return visibleItem;
        }
        return null;
    }
""",
    )

    replace_region(
        "    public static void clearVisibleItems(Player player) {\n",
        "\n    public final void refreshForPlayer",
        """    public static void clearVisibleItems(Player player) {
        if (player == null) {
            return;
        }
        for (Object groundItemObject : player.getVisibleGroundItems()) {
            GroundItem groundItem = (GroundItem)groundItemObject;
            player.packetSender.sendGroundItemRemove(groundItem);
        }
        player.getVisibleGroundItems().clear();
    }
""",
    )

    path.write_text(text, encoding="utf-8")
    return repairs


def repair_npc_locals(source_root: Path) -> int:
    path = source_root / "com/rs2/model/npc/Npc.java"
    text = path.read_text(encoding="utf-8")
    repairs = 0

    replacements = [
        (
            """        Entity entity = player;
        entity.packetSender.closeInterfaces();
        entity = this;
        int n4 = ((Npc)entity).definition.getId();
""",
            """        player.packetSender.closeInterfaces();
        int n4 = this.definition.getId();
""",
        ),
        (
            """        Entity entity = player;
        entity.packetSender.closeInterfaces();
        this.getUpdateState().setAnimation(402);
        player.getUpdateState().setAnimation(2304);
        entity = player;
        entity.packetSender.showInterface(8677);
        entity = this;
""",
            """        player.packetSender.closeInterfaces();
        this.getUpdateState().setAnimation(402);
        player.getUpdateState().setAnimation(2304);
        player.packetSender.showInterface(8677);
""",
        ),
        (
            "        CycleEventHandler.getInstance().schedule(player, new NpcRelocationEvent(this, player, n3, n4, n5, bl, (Npc)entity), 4);\n",
            "        CycleEventHandler.getInstance().schedule(player, new NpcRelocationEvent(this, player, n3, n4, n5, bl, this), 4);\n",
        ),
    ]
    for old, new in replacements:
        text, count = replace_exact(text, old, new, path)
        repairs += count

    def replace_region(start_marker: str, end_marker: str, replacement: str) -> None:
        nonlocal text, repairs
        try:
            start = text.index(start_marker)
            end = text.index(end_marker, start)
        except ValueError as exc:
            raise SystemExit(f"{path}: expected method region not found: {start_marker!r}") from exc
        text = text[:start] + replacement + text[end:]
        repairs += 1

    replace_region(
        "    public final void queueScriptedPath(Position[] object) {\n",
        "\n    public final void queueStageAdvancePath",
        """    public final void queueScriptedPath(Position[] positions) {
        this.setScriptedMovementEnabled(true);
        this.getMovementQueue().clear();
        for (Position position : positions) {
            this.getMovementQueue().addStep(position);
        }
        this.scriptedPathTargetX = ((MovementStep)this.getMovementQueue().getSteps().getLast()).getX();
        this.scriptedPathTargetY = ((MovementStep)this.getMovementQueue().getSteps().getLast()).getY();
        this.getMovementQueue().removeFirstStep();
    }
""",
    )

    replace_region(
        "    public final boolean isProtectedFrom(CombatType object) {\n",
        "\n    @Override\n    public final void moveTo",
        """    public final boolean isProtectedFrom(CombatType combatType) {
        if (combatType == CombatType.MELEE) {
            return this.definition.isProtectedFromMelee();
        }
        if (combatType == CombatType.RANGED) {
            return this.definition.isProtectedFromRanged();
        }
        if (combatType == CombatType.MAGIC) {
            return this.definition.isProtectedFromMagic();
        }
        return false;
    }
""",
    )

    replace_region(
        "    public final void moveTo(Position position) {\n",
        "\n    @Override\n    public final void setCurrentHitpoints",
        """    public final void moveTo(Position position) {
        this.active = false;
        this.b = true;
        this.setPosition(position);
        this.getMovementQueue().clear();
        this.active = true;
    }
""",
    )

    replace_region(
        "    public static boolean isUndead(Entity object) {\n",
        "\n}\n",
        """    public static boolean isUndead(Entity entity) {
        if (entity.isPlayer()) {
            return false;
        }
        String npcName = ((Npc)entity).definition.getName().toLowerCase();
        return npcName.contains("spectre") || npcName.contains("banshee") || npcName.contains("shade") || npcName.contains("zombie") || npcName.contains("skeleton") || npcName.contains("ghost") || npcName.contains("crawling hand") || npcName.contains("skeletal hand") || npcName.contains("zombie hand") || npcName.contains("zogre") || npcName.contains("skorge") || npcName.contains("ankous");
    }
""",
    )

    path.write_text(text, encoding="utf-8")
    return repairs


def repair_entity_locals(source_root: Path) -> int:
    path = source_root / "com/rs2/model/Entity.java"
    text = path.read_text(encoding="utf-8")
    repairs = 0

    replacements = [
        ("    private List combatEffectTasks;\n", "    private List<CombatEffectTask> combatEffectTasks;\n"),
        ("    private Queue damageContributions;\n", "    private Queue<DamageContribution> damageContributions;\n"),
        ("        this.combatEffectTasks = new LinkedList();\n", "        this.combatEffectTasks = new LinkedList<CombatEffectTask>();\n"),
        (
            "        this.damageContributions = new PriorityQueue(1, new DamageContributionComparator(this));\n",
            "        this.damageContributions = new PriorityQueue<DamageContribution>(1, new DamageContributionComparator(this));\n",
        ),
        ("        LinkedList linkedList = new LinkedList();\n", "        LinkedList<CombatEffectTask> linkedList = new LinkedList<CombatEffectTask>();\n"),
        (
            "            Entity entity;\n"
            "            Entity entity2 = this;\n"
            "            entity2 = this;\n"
            "            if (Q.contains(entity.position.getX(), entity2.position.getY())) {\n",
            "            Entity entity2 = this;\n"
            "            if (Q.contains(this.position.getX(), entity2.position.getY())) {\n",
        ),
    ]
    for old, new in replacements:
        text, count = replace_exact(text, old, new, path)
        repairs += count

    def replace_region(start_marker: str, end_marker: str, replacement: str) -> None:
        nonlocal text, repairs
        try:
            start = text.index(start_marker)
            end = text.index(end_marker, start)
        except ValueError as exc:
            raise SystemExit(f"{path}: expected method region not found: {start_marker!r}") from exc
        text = text[:start] + replacement + text[end:]
        repairs += 1

    replace_region(
        "    public final boolean isMoving() {\n",
        "\n    public final boolean isRunningMovement",
        """    public final boolean isMoving() {
        Queue steps = this.movementQueue.getSteps();
        return !steps.isEmpty() && ((MovementStep)steps.peek()).getDirection() != -1;
    }
""",
    )

    replace_region(
        "    public final void setInteractionTarget(Entity entity) {\n",
        "\n    public final Entity getInteractionTarget",
        """    public final void setInteractionTarget(Entity entity) {
        if (this.isNpc() && entity != null && !((Npc)this).isFaceEntityUpdateDisabled()) {
            this.updateState.setFaceEntityId(entity.encodedIndex);
        }
        this.interactionTarget = entity;
    }
""",
    )

    replace_region(
        "    public final Entity getTopDamageContributor() {\n",
        "\n    public final ArrayList getDamageContributorList",
        """    public final Entity getTopDamageContributor() {
        DamageContribution damageContribution = (DamageContribution)this.damageContributions.peek();
        if (damageContribution != null) {
            Entity contributor = damageContribution.resolve();
            if (this.damageContributions.size() > 1 && contributor != null && contributor.isPlayer()) {
                Player player = (Player)contributor;
                if (player.gameMode != 0) {
                    player.packetSender.sendGameMessage("You are not playing on normal gamemode and cannot receive the loot.");
                    return null;
                }
            }
            return contributor;
        }
        return null;
    }
""",
    )

    replace_region(
        "    public final ArrayList getDamageContributorList() {\n",
        "\n    public final CombatTargetDelayTimer getRecentCombatTimer",
        """    public final ArrayList getDamageContributorList() {
        ArrayList<Entity> arrayList = new ArrayList<Entity>();
        for (DamageContribution damageContribution : this.damageContributions) {
            Entity contributor = damageContribution.resolve();
            if (contributor == null) {
                continue;
            }
            arrayList.add(contributor);
        }
        return arrayList;
    }
""",
    )

    replace_region(
        "    public final DamageContribution getDamageContribution(Entity entity) {\n",
        "\n    public final int collectCombatAttackOptions",
        """    public final DamageContribution getDamageContribution(Entity entity) {
        for (DamageContribution damageContribution : this.damageContributions) {
            if (damageContribution.resolve() != entity) {
                continue;
            }
            return damageContribution;
        }
        return null;
    }
""",
    )

    replace_region(
        "    public final int collectCombatAttackOptions(List list, Entity entity, int n) {\n",
        "\n    public final void setActiveCycleEvent",
        """    public final int collectCombatAttackOptions(List list, Entity entity, int n) {
        CombatAttack[] combatAttackArray = this.combatAttackProvider.createAttacks(this, entity);
        int usableAttackCount = combatAttackArray.length;
        int kbdSpecialIndex = -1;
        for (int attackIndex = 0; attackIndex < combatAttackArray.length; ++attackIndex) {
            CombatAttack combatAttack = combatAttackArray[attackIndex];
            combatAttack.prepare();
            BaseCombatAttack baseCombatAttack = (BaseCombatAttack)combatAttack;
            if (baseCombatAttack == null || baseCombatAttack.getHitDefinitions() == null) {
                continue;
            }
            HitDefinition[] hitDefinitions = baseCombatAttack.getHitDefinitions();
            for (HitDefinition hitDefinition : hitDefinitions) {
                if (hitDefinition != null && hitDefinition.getAttackStyle().getXpMode() == AttackXpMode.KBD_SPECIAL) {
                    kbdSpecialIndex = attackIndex;
                }
            }
        }
        if (kbdSpecialIndex != -1) {
            CombatAttack[] kbdSpecialAttacks = new CombatAttack[]{
                BaseCombatAttack.createProjectileAttackWithEffect(this, entity, CombatType.MAGIC, AttackXpMode.KBD_SPECIAL, 50, 4, 81, new GraphicEffect(-1, 0), new GraphicEffect(-1, 0), 394, ProjectileTiming.a, new PoisonEffect(8.0)),
                BaseCombatAttack.createProjectileAttackWithEffect(this, entity, CombatType.MAGIC, AttackXpMode.KBD_SPECIAL, 50, 4, 81, new GraphicEffect(-1, 0), new GraphicEffect(-1, 0), 395, ProjectileTiming.a, new MovementLockEffect(10)),
                BaseCombatAttack.createProjectileAttackWithEffect(this, entity, CombatType.MAGIC, AttackXpMode.KBD_SPECIAL, 50, 4, 81, new GraphicEffect(-1, 0), new GraphicEffect(-1, 0), 396, ProjectileTiming.a, new StatDrainEffect(-1, 2))
            };
            CombatAttack selectedAttack = kbdSpecialAttacks[GameUtil.randomInt(3)];
            selectedAttack.prepare();
            combatAttackArray[kbdSpecialIndex] = selectedAttack;
        }
        for (CombatAttack combatAttack : combatAttackArray) {
            CombatAttackState combatAttackState = combatAttack.getState();
            if (this.isNpc() && entity.isPlayer()) {
                Npc npc = (Npc)this;
                Player player = (Player)entity;
                if (npc.getNpcId() == 1264) {
                    if (player.getActivePrayers()[14]) {
                        if (combatAttack.getCombatType() == CombatType.MELEE) {
                            combatAttackState = CombatAttackState.a;
                        }
                    } else if (player.getActivePrayers()[12] && combatAttack.getCombatType() != CombatType.MELEE) {
                        combatAttackState = CombatAttackState.a;
                    }
                }
            }
            if (combatAttackState == CombatAttackState.a) {
                if (this.isPlayer()) {
                    Player player = (Player)this;
                    if (player.botEnabled && (combatAttack.getCombatType() == CombatType.MAGIC || combatAttack.getCombatType() == CombatType.RANGED)) {
                        if (player.isInWilderness()) {
                            BotCombatEscapeHandler.tryStartBotCombatEscape(player);
                        } else if (player.currentBotTask != null) {
                            player.botCombatState = "escape";
                        }
                    }
                }
                --usableAttackCount;
                if (this.isPlayer() && ((Player)this).isSpecialAttackEnabled()) {
                    ((Player)this).setSpecialAttackEnabled(false);
                    ((Player)this).refreshSpecialAttackWidgets();
                }
                continue;
            }
            int attackRange = combatAttack.getAttackRange();
            if (this.isMoving() && !this.isRunningMovement() && entity.isMoving() && !entity.isRunningMovement()) {
                ++attackRange;
            } else if (this.isMoving() && this.isRunningMovement() && entity.isMoving() && entity.isRunningMovement()) {
                attackRange += 2;
            }
            if (!EntityTargetMovement.canReachTarget(this, entity, attackRange)) {
                combatAttackState = CombatAttackState.b;
            }
            list.add(new SmithingHandler(combatAttack, combatAttackState));
        }
        return usableAttackCount;
    }
""",
    )

    replace_region(
        "    public final boolean canApplyCombatEffect(CombatEffect combatEffect) {\n",
        "\n    public final void clearCombatEffectTasks()",
        """    public final boolean canApplyCombatEffect(CombatEffect combatEffect) {
        for (CombatEffectTask combatEffectTask : this.combatEffectTasks) {
            if (!combatEffectTask.getEffect().equals(combatEffect)) {
                continue;
            }
            return false;
        }
        return combatEffect.canApplyTo(this);
    }
""",
    )

    replace_region(
        "    public final void queuePathTo(Position object, boolean bl) {\n",
        "\n    public final EntityTargetMovement getTargetMovement",
        """    public final void queuePathTo(Position position, boolean bl) {
        PathResult pathResult = new DirectPathStrategy().buildPath(this, position, bl);
        this.movementQueue.clear();
        while (!pathResult.getSteps().isEmpty()) {
            PathStep pathStep = (PathStep)pathResult.getSteps().poll();
            this.movementQueue.addStep(new Position(pathStep.getX(), pathStep.getY(), this.position.getPlane()));
        }
        this.movementQueue.removeFirstStep();
    }
""",
    )

    replace_region(
        "    public final boolean isWithinReach(Entity object, int n) {\n",
        "\n    public final boolean isOverlapping",
        """    public final boolean isWithinReach(Entity entity, int n) {
        Rectangle sourceArea = new Rectangle(this.position.getX() - n, this.position.getY() - n, 2 * n + this.size, 2 * n + this.size);
        Rectangle targetArea = new Rectangle(entity.position.getX(), entity.position.getY(), entity.size, entity.size);
        return sourceArea.intersects(targetArea);
    }
""",
    )

    replace_region(
        "    public final boolean isOverlapping(Entity object) {\n",
        "\n    public final void beginInterruptibleAction",
        """    public final boolean isOverlapping(Entity entity) {
        Rectangle sourceArea = new Rectangle(this.position.getX(), this.position.getY(), this.size, this.size);
        Rectangle targetArea = new Rectangle(entity.position.getX(), entity.position.getY(), entity.size, entity.size);
        return sourceArea.intersects(targetArea);
    }
""",
    )

    path.write_text(text, encoding="utf-8")
    return repairs


def repair_skill_guide_manager_locals(source_root: Path) -> int:
    path = source_root / "com/rs2/model/skill/guide/SkillGuideManager.java"
    text = path.read_text(encoding="utf-8")
    repairs = 0

    def replace_region(start_marker: str, end_marker: str, replacement: str) -> None:
        nonlocal text, repairs
        try:
            start = text.index(start_marker)
            end = text.index(end_marker, start)
        except ValueError as exc:
            raise SystemExit(f"{path}: expected method region not found: {start_marker!r}") from exc
        text = text[:start] + replacement + text[end:]
        repairs += 1

    replace_region(
        "    private void sendGuideFrame(String string, String string2, String stringArray, String object, String string3, String object2, String string4, String string5, String string6, String string7, String string8, String string9, String string10, String string11, String string12) {\n",
        "\n    private void sendEntryRow",
        """    private void sendGuideFrame(String title, String subtitle, String category0, String category1, String category2, String category3, String category4, String category5, String category6, String category7, String category8, String category9, String category10, String category11, String category12) {
        int[] hiddenComponentIds = new int[]{8844, 8813, 8813, 8825, 8828, 8838, 8841, 8850, 8860, 8863, 15294, 15304, 15307};
        int[] textComponentIds = new int[]{8846, 8823, 8824, 8827, 8837, 8840, 8843, 8859, 8862, 8865, 15303, 15306, 15309};
        String[] categories = new String[]{category0, category1, category2, category3, category4, category5, category6, category7, category8, category9, category10, category11, category12};
        this.player.packetSender.sendInterfaceScrollPosition(8717, 0);
        this.player.packetSender.setInterfaceHiddenFlag(categories[1] == "" ? 1 : 0, 8800);
        this.player.packetSender.setInterfaceHiddenFlag(categories[1] == "" ? 1 : 0, hiddenComponentIds[0]);
        this.player.packetSender.setInterfaceHiddenFlag(categories[1] == "" ? 1 : 0, hiddenComponentIds[1]);
        if (categories[2] == "") {
            categories[2] = "Milestones";
        }
        int categoryIndex = 3;
        while (categoryIndex < 13) {
            this.player.packetSender.setInterfaceHiddenFlag(categories[categoryIndex] == "" ? 1 : 0, hiddenComponentIds[categoryIndex]);
            ++categoryIndex;
        }
        this.player.packetSender.sendInterfaceText(title, 8716);
        this.player.packetSender.sendInterfaceText(subtitle, 8849);
        categoryIndex = 0;
        while (categoryIndex < 13) {
            this.player.packetSender.sendInterfaceText(categories[categoryIndex], textComponentIds[categoryIndex]);
            ++categoryIndex;
        }
        this.player.resetInteractionState();
        this.player.packetSender.showInterface(8714);
    }
""",
    )

    replace_region(
        "    private void sendItemContainer(int[] object) {\n",
        "\n    private void showSkillGuide",
        """    private void sendItemContainer(int[] itemIds) {
        ItemStack[] itemStackArray = new ItemStack[itemIds.length];
        int n = 0;
        while (n < itemIds.length) {
            itemStackArray[n] = new ItemStack(itemIds[n]);
            ++n;
        }
        this.player.packetSender.sendItemContainer(8847, itemStackArray);
    }
""",
    )

    replace_region(
        "    private void showSkillGuide(String string, ArrayList arrayList, int n) {\n",
        "\n    public final void showAttackGuide",
        """    private void showSkillGuide(String title, ArrayList arrayList, int n) {
        String subtitle = "";
        if (n >= arrayList.size()) {
            this.clearEntryRows();
            this.sendItemContainer(this.displayItemIds);
        } else {
            SkillGuideCategory category = (SkillGuideCategory)arrayList.get(n);
            subtitle = category.name;
            if (category.skipItemDefinitionLookup) {
                subtitle = String.valueOf(subtitle) + " - Members";
            }
            this.clearEntryRows();
            int entryIndex = 0;
            while (entryIndex < category.entries.size()) {
                SkillGuideEntry skillGuideEntry = (SkillGuideEntry)category.entries.get(entryIndex);
                String levelText = category.getLevelText();
                if (levelText.equals("-1") && (levelText = skillGuideEntry.getLevelText()).equals("-1")) {
                    levelText = "";
                }
                int itemId = skillGuideEntry.itemId;
                String displayLabelPrefix = "";
                if (itemId >= 0 && !category.skipItemDefinitionLookup && ItemDefinition.isDefined(itemId)) {
                    ItemStack itemStack = new ItemStack(itemId);
                    if (itemStack.getDefinition().isMembersOnly()) {
                        displayLabelPrefix = "Members: ";
                    }
                }
                String displayLabel = String.valueOf(displayLabelPrefix) + skillGuideEntry.getDisplayLabel();
                if (category.prefixEntriesWithName && !skillGuideEntry.suppressCategoryPrefix) {
                    displayLabel = String.valueOf(category.name) + " " + skillGuideEntry.getDisplayLabel();
                }
                this.sendEntryRow(levelText, displayLabel, skillGuideEntry.itemId, entryIndex);
                ++entryIndex;
            }
        }
        String[] categoryNames = new String[13];
        int categoryIndex = 0;
        while (categoryIndex < 13) {
            if (categoryIndex < arrayList.size()) {
                SkillGuideCategory category = (SkillGuideCategory)arrayList.get(categoryIndex);
                categoryNames[categoryIndex] = category.name;
            } else {
                categoryNames[categoryIndex] = "";
            }
            ++categoryIndex;
        }
        this.sendGuideFrame(title, subtitle, categoryNames[0], categoryNames[1], categoryNames[2], categoryNames[3], categoryNames[4], categoryNames[5], categoryNames[6], categoryNames[7], categoryNames[8], categoryNames[9], categoryNames[10], categoryNames[11], categoryNames[12]);
    }
""",
    )

    text, count = replace_exact(
        text,
        "        skillGuideCategory7 = object;\n",
        "        skillGuideCategory7 = (SkillGuideCategory)object;\n",
        path,
    )
    repairs += count

    path.write_text(text, encoding="utf-8")
    return repairs


def repair_potion_handler_locals(source_root: Path) -> int:
    path = source_root / "com/rs2/model/item/consumable/PotionHandler.java"
    text = path.read_text(encoding="utf-8")
    repairs = 0
    replacements = [
        (
            "            Object object = definitions[this.selectedDefinitionIndex].getSkillIds();\n",
            "            int[] skillIds = definitions[this.selectedDefinitionIndex].getSkillIds();\n",
        ),
        ("            while (n3 < ((int[])object).length) {\n", "            while (n3 < skillIds.length) {\n"),
        ("                    n7 = object[n3];\n", "                    n7 = skillIds[n3];\n"),
        ("            object = this;\n", ""),
    ]
    for old, new in replacements:
        text, count = replace_exact(text, old, new, path)
        repairs += count
    count = text.count("object.player")
    if count == 0:
        raise SystemExit(f"{path}: expected object.player references not found")
    text = text.replace("object.player", "this.player")
    repairs += count
    path.write_text(text, encoding="utf-8")
    return repairs


def repair_item_action_packet_handler_locals(source_root: Path) -> int:
    path = source_root / "com/rs2/net/packet/handler/ItemActionPacketHandler.java"
    text = path.read_text(encoding="utf-8")
    repairs = 0

    def replace_region(start_marker: str, end_marker: str, replacement: str) -> None:
        nonlocal text, repairs
        try:
            start = text.index(start_marker)
            end = text.index(end_marker, start)
        except ValueError as exc:
            raise SystemExit(f"{path}: expected method region not found: {start_marker!r}") from exc
        text = text[:start] + replacement + text[end:]
        repairs += 1

    replace_region(
        "    private static void handleDropItem(Player player, IncomingPacket incomingPacket) {\n",
        "\n    private static void handleInterfaceItemAmountFive",
        """    private static void handleDropItem(Player player, IncomingPacket incomingPacket) {
        int itemId = incomingPacket.getReader().readSignedShort(ByteTransform.ADD);
        incomingPacket.getReader().readSignedShort();
        int slot = incomingPacket.getReader().readSignedShort(ByteTransform.ADD);
        if (slot < 0 || slot > player.getInventoryManager().getContainer().k() - 1) {
            return;
        }
        player.setSelectedItemSlot(slot);
        ItemStack itemStack = player.getInventoryManager().getContainer().getItemAt(player.getSelectedItemSlot());
        if (PuzzleBoxHandler.movePuzzlePiece(player, itemId)) {
            return;
        }
        if (itemStack == null || itemStack.getId() != itemId || !itemStack.isValid()) {
            return;
        }
        if (itemStack.getDefinition().isStackable()) {
            itemStack.setAmount(player.getInventoryManager().getContainer().getItemAmount(itemStack.getId()));
        } else {
            itemStack.setAmount(1);
        }
        if (!player.getInventoryManager().getContainer().containsItem(itemStack.getId())) {
            return;
        }
        if (player.getQuestManager().handleDropItem(itemStack.getId())) {
            return;
        }
        int[][] petItemNpcPairs = PetManager.petItemNpcPairs;
        int pairIndex = 0;
        while (pairIndex < 6) {
            int[] petPair = petItemNpcPairs[pairIndex];
            if (itemStack.getDefinition().getId() == petPair[0]) {
                player.getPetManager().summonPetFromItem(petPair[0], petPair[1]);
                return;
            }
            ++pairIndex;
        }
        BarrowsRepairHandler barrowsRepairHandler = BarrowsRepairHandler.forItem(itemStack);
        if (itemStack.getDefinition().hasDestroyOption() || barrowsRepairHandler != null && itemStack.getDefinition().isUntradeable()) {
            String destroyMessage = "Dropping this item will make you lose it forever.";
            if (barrowsRepairHandler != null) {
                destroyMessage = "Dropping this item will make it degrade to 0.";
            }
            String[][] destroyDialogRows = new String[][]{{"Are you sure you want to drop this item?", "14174"}, {"Yes.", "14175"}, {"No.", "14176"}, {"", "14177"}, {destroyMessage, "14182"}, {"", "14183"}, {itemStack.getDefinition().getName(), "14184"}};
            player.packetSender.sendInterfaceSlotItem(itemStack, 0, 14171, 1);
            int rowIndex = 0;
            while (rowIndex < 7) {
                String[] row = destroyDialogRows[rowIndex];
                player.packetSender.sendInterfaceText(row[0], Integer.parseInt(row[1]));
                ++rowIndex;
            }
            player.setPendingDestroyItem(itemStack);
            player.packetSender.showChatboxInterface(14170);
            return;
        }
        if (player.getInventoryManager().getContainer().containsItem(itemStack.getId())) {
            player.packetSender.sendSoundEffect(376, 1, 0);
            if (!ServerSettings.adminInteractionsAllowed && player.getPlayerRights() >= 2) {
                player.packetSender.sendGameMessage("Your item disappears because you're an administrator.");
            } else if (itemStack.getId() == 11283) {
                GroundItemManager.getInstance().spawn(new GroundItem(new ItemStack(11284, itemStack.getAmount()), player));
            } else {
                GroundItemManager.getInstance().spawn(new GroundItem(new ItemStack(itemStack.getId(), itemStack.getAmount(), itemStack.getMetadata()), player));
            }
            if (!player.getInventoryManager().removeItemFromSlot(itemStack, player.getSelectedItemSlot())) {
                player.getInventoryManager().removeItem(itemStack);
            }
        }
        player.getEquipmentManager().refreshCarriedValue();
    }
""",
    )

    replace_region(
        "    private static void handleInterfaceItemAmountFive(Player player, IncomingPacket object) {\n",
        "\n    private static void handleInterfaceItemAmountTenOrOperate",
        """    private static void handleInterfaceItemAmountFive(Player player, IncomingPacket incomingPacket) {
        int interfaceId = incomingPacket.getReader().readSignedShort(ByteTransform.ADD, ByteOrder.LITTLE);
        int itemId = incomingPacket.getReader().readShort(true, ByteTransform.ADD, ByteOrder.LITTLE);
        player.setSelectedItemSlot(incomingPacket.getReader().readSignedShort(true, ByteOrder.LITTLE));
        InterfaceDefinition interfaceDefinition = InterfaceDefinition.forId(interfaceId);
        if (!player.isInterfaceOpen(interfaceDefinition)) {
            return;
        }
        if (interfaceId == 5064 || interfaceId == 7423) {
            BankManager.depositInventoryItem(player, player.getSelectedItemSlot(), itemId, 5);
        } else if (interfaceId == 2006) {
            PartyRoomManager.stageInventoryItemForChest(player, player.getSelectedItemSlot(), itemId, 5);
        } else if (interfaceId == 2274) {
            PartyRoomManager.withdrawStagedChestItem(player, player.getSelectedItemSlot(), itemId, 5);
        } else if (interfaceId == 5382 || interfaceId == 19532 || interfaceId == 19533 || interfaceId == 19534 || interfaceId == 19535 || interfaceId == 19536 || interfaceId == 19537 || interfaceId == 19538 || interfaceId == 19539 || interfaceId == 19540) {
            BankManager.withdrawItemFromTab(player, player.getSelectedItemSlot(), itemId, 5, interfaceId);
        } else if (interfaceId == 15948) {
            MageTrainingArenaRewardShop.buyReward(player, player.getSelectedItemSlot());
        } else if (interfaceId == 3900) {
            ShopManager.buyItem(player, player.getSelectedItemSlot(), itemId, 1);
        } else if (interfaceId == 3823) {
            ShopManager.sellItem(player, player.getSelectedItemSlot(), itemId, 1);
        } else if (interfaceId == 3322) {
            if (player.interfaceAction == "duel") {
                player.getDuelSession().addStakeItem(new ItemStack(itemId, 5), player.getSelectedItemSlot());
            } else {
                GameplayHelper.addTradeOfferItem(player, player.getSelectedItemSlot(), itemId, 5);
            }
        } else if (interfaceId == 3415) {
            GameplayHelper.removeTradeOfferItem(player, player.getSelectedItemSlot(), itemId, 5);
        } else if (interfaceId == 15682 || interfaceId == 15683) {
            player.getFarmingToolStore().withdrawItem(itemId, 5);
        } else if (interfaceId == 15594 || interfaceId == 15595) {
            player.getFarmingToolStore().depositItem(itemId, 5);
        } else if (interfaceId == 1119 || interfaceId == 1120 || interfaceId == 1121 || interfaceId == 1122 || interfaceId == 1123) {
            SmithingHandler.startSmithingTask(player, itemId, 5);
        } else if (interfaceId == 6669) {
            player.getDuelSession().removeStakeItem(new ItemStack(itemId, 5));
        }
        switch (interfaceId) {
            case 4233: {
                JewelleryCraftingHandler.startJewelleryCraftingTask(player, JewelleryCraftingData.getMaterialItemIds()[player.getSelectedItemSlot()], 5, 0);
                return;
            }
            case 4239: {
                JewelleryCraftingHandler.startJewelleryCraftingTask(player, JewelleryCraftingData.getMaterialItemIds()[player.getSelectedItemSlot()], 5, 1);
                return;
            }
            case 4245: {
                JewelleryCraftingHandler.startJewelleryCraftingTask(player, JewelleryCraftingData.getMaterialItemIds()[player.getSelectedItemSlot()], 5, 2);
            }
        }
    }
""",
    )

    for old, new in (
        (
            """                        object2 = "operate";
                        object = player2;
                        player2.interfaceAction = object2;
""",
            """                        player2.interfaceAction = "operate";
""",
        ),
    ):
        text, count = replace_exact(text, old, new, path)
        repairs += count

    replace_region(
        "    private static void handleInventoryItemThirdOption(Player player, IncomingPacket object) {\n",
        "\n    private static void handleEquipItem",
        """    private static void handleInventoryItemThirdOption(Player player, IncomingPacket incomingPacket) {
        int interfaceId = incomingPacket.getReader().readSignedShort(ByteTransform.ADD, ByteOrder.LITTLE);
        player.setSelectedItemSlot(incomingPacket.getReader().readSignedShort(ByteOrder.LITTLE));
        player.setSelectedItemId(incomingPacket.getReader().readSignedShort(true, ByteTransform.ADD));
        InterfaceDefinition interfaceDefinition = InterfaceDefinition.forId(interfaceId);
        if (!player.isInterfaceOpen(interfaceDefinition)) {
            return;
        }
        ItemStack itemStack = player.getInventoryManager().getContainer().getItemAt(player.getSelectedItemSlot());
        if (itemStack == null || itemStack.getId() != player.getSelectedItemId()) {
            return;
        }
        if (itemStack.getDefinition().isMembersOnly() && !player.isMember()) {
            player.packetSender.sendGameMessage("You need a members account to access members content.");
            return;
        }
        if (itemStack.getDefinition().isMembersOnly() && ServerSettings.freeToPlayWorld) {
            player.packetSender.sendGameMessage("You need to be in members world to access members content.");
            return;
        }
        if (HerbloreHandler.emptyContainer(player, new ItemStack(player.getSelectedItemId()), player.getSelectedItemSlot())) {
            return;
        }
        if (RunecraftingHandler.locateTalismanDirection(player, player.getSelectedItemId())) {
            return;
        }
        switch (itemStack.getId()) {
            case 11283: {
                if (player.getInventoryManager().removeItemFromSlot(itemStack, player.getSelectedItemSlot())) {
                    player.getInventoryManager().setItemInSlot(new ItemStack(11284), player.getSelectedItemSlot());
                }
                player.getUpdateState().setAnimation(6700);
                player.getUpdateState().setGraphic(1168, 10);
                player.packetSender.sendGameMessage("You release the charges.");
                return;
            }
            case 4079: {
                player.getUpdateState().setAnimation(1460, 0);
                return;
            }
            case 2552:
            case 2554:
            case 2556:
            case 2558:
            case 2560:
            case 2562:
            case 2564:
            case 2566: {
                player.interfaceAction = "rub";
                DialogueManager.startDialogue(player, 10004);
                return;
            }
            case 1706:
            case 1708:
            case 1710:
            case 1712: {
                player.interfaceAction = "rub";
                DialogueManager.startDialogue(player, 10003);
                return;
            }
            case 3853:
            case 3855:
            case 3857:
            case 3859:
            case 3861:
            case 3863:
            case 3865:
            case 3867: {
                player.interfaceAction = "rub";
                DialogueManager.startDialogue(player, 10002);
            }
        }
    }
""",
    )

    replace_region(
        "    private static void handleEquipItem(Player player, IncomingPacket object) {\n",
        "\n    private static void handleMagicOnItem",
        """    private static void handleEquipItem(Player player, IncomingPacket incomingPacket) {
        boolean handledPouch;
        int itemId = incomingPacket.getReader().readSignedShort();
        player.setSelectedItemSlot(incomingPacket.getReader().readSignedShort(ByteTransform.ADD));
        player.setSelectedItemInterfaceId(incomingPacket.getReader().readSignedShort(ByteTransform.ADD));
        InterfaceDefinition interfaceDefinition = InterfaceDefinition.forId(player.getSelectedItemInterfaceId());
        if (!player.isInterfaceOpen(interfaceDefinition)) {
            return;
        }
        ItemStack itemStack = player.getInventoryManager().getContainer().getItemAt(player.getSelectedItemSlot());
        if (itemStack == null || itemStack.getId() != itemId || !itemStack.isValid()) {
            return;
        }
        EssencePouchDefinition essencePouchDefinition = EssencePouchDefinition.forItemOrIndex(itemId);
        if (essencePouchDefinition == null) {
            handledPouch = false;
        } else if (itemId != essencePouchDefinition.getItemId() && itemId != essencePouchDefinition.getDegradedItemId()) {
            handledPouch = false;
        } else {
            if (!ServerSettings.runecraftingEnabled) {
                player.packetSender.sendGameMessage("This skill is currently disabled.");
            } else if (player.getQuestState(14) != 1) {
                QuestDefinition questDefinition = QuestDefinition.forId(14);
                player.packetSender.sendGameMessage("You need to complete " + questDefinition.getName() + " to do this.");
            } else if (player.getEssencePouchAmount(essencePouchDefinition.getPouchIndex()) > 0) {
                if (player.getInventoryManager().getContainer().getFreeSlots() >= player.getEssencePouchAmount(essencePouchDefinition.getPouchIndex())) {
                    player.getInventoryManager().addItem(new ItemStack(7936, player.getEssencePouchAmount(essencePouchDefinition.getPouchIndex())));
                    player.setEssencePouchAmount(essencePouchDefinition.getPouchIndex(), 0);
                } else {
                    player.packetSender.sendGameMessage("Not enough space in your inventory.");
                }
            } else {
                PacketSender packetSender = player.packetSender;
                StringBuilder stringBuilder = new StringBuilder("Your ");
                ItemService.getInstance();
                packetSender.sendGameMessage(stringBuilder.append(ItemService.getItemName(itemId)).append(" is empty.").toString());
            }
            handledPouch = true;
        }
        if (handledPouch) {
            return;
        }
        if (AllotmentPatchManager.emptyCropStorageContainer(player, itemId)) {
            return;
        }
        if (itemStack.getDefinition().getEquipmentSlot() == -1 && GameplayHelper.extinguishCaveLightSource(player, itemId, true)) {
            return;
        }
        switch (itemStack.getId()) {
            case 4079: {
                player.getUpdateState().setAnimation(1458, 0);
                return;
            }
            case 4035: {
                if (player.getQuestState(62) != 20) {
                    player.packetSender.sendGameMessage("You have already defeated the demon.");
                    return;
                }
                if (player.isInWilderness() || player.isInTenthSquadSigilInstance()) {
                    player.packetSender.sendGameMessage("You can't use this item here.");
                    return;
                }
                DialogueManager.startDialogue(player, 9998);
                return;
            }
        }
        if (new ItemStack(itemId).getDefinition().getEquipmentSlot() == -1) {
            return;
        }
        if (player.getDuelSession().getOpponent() != null && !player.isInDuelArena()) {
            player.getDuelController().resetDuel(true);
            return;
        }
        player.getEquipmentManager().equipFromInventorySlot(player.getSelectedItemSlot());
    }
""",
    )

    replace_region(
        "    private static void handleMagicOnItem(Player player, IncomingPacket object) {\n",
        "\n    private static void handleMagicOnGroundItem",
        """    private static void handleMagicOnItem(Player player, IncomingPacket incomingPacket) {
        PacketReader packetReader = incomingPacket.getReader();
        player.setSelectedItemSlot(packetReader.readSignedShort());
        int itemId = packetReader.readSignedShort(ByteTransform.ADD);
        player.setSelectedItemInterfaceId(packetReader.readSignedShort());
        int spellButtonId = packetReader.readSignedShort(ByteTransform.ADD);
        SpellDefinition spellDefinition = (SpellDefinition)((Object)player.getSpellbook().getSpellByButtonId().get(spellButtonId));
        ItemStack itemStack = player.getInventoryManager().getContainer().getItemAt(player.getSelectedItemSlot());
        player.temporaryActionValue = itemId;
        if (itemStack == null || itemStack.getId() != itemId || !itemStack.isValid()) {
            return;
        }
        if (spellDefinition != null) {
            MagicSpellAction.castItemSpell(player, spellDefinition, itemId, player.getSelectedItemSlot());
            return;
        }
        if (player.getPlayerRights() > 1 && ServerSettings.debugModeEnabled) {
            System.out.println("Slot: " + player.getSelectedItemSlot() + " Item id: " + itemId + " Interface ID: " + player.getSelectedItemInterfaceId() + " magic id: " + spellButtonId);
        }
    }
""",
    )

    replace_region(
        "    private static void handleMagicOnGroundItem(Player player, IncomingPacket object) {\n",
        "\n}\n",
        """    private static void handleMagicOnGroundItem(Player player, IncomingPacket incomingPacket) {
        PacketReader packetReader = incomingPacket.getReader();
        int y = packetReader.readSignedShort(ByteOrder.LITTLE);
        int itemId = packetReader.readSignedShort();
        int x = packetReader.readSignedShort(ByteOrder.LITTLE);
        int spellButtonId = packetReader.readSignedShort(ByteTransform.ADD);
        SpellDefinition spellDefinition = (SpellDefinition)((Object)player.getSpellbook().getSpellByButtonId().get(spellButtonId));
        if (player.getQuestManager().handleGroundItemInteraction(itemId)) {
            return;
        }
        if (spellDefinition != null) {
            MagicSpellAction.scheduleTelekineticGrab(player, spellDefinition, itemId, new Position(x, y, player.getPosition().getPlane()));
            return;
        }
        if (player.getPlayerRights() > 1 && ServerSettings.debugModeEnabled) {
            System.out.println("Magic ID: " + spellButtonId + " Item ID: " + itemId + " X: " + x + " Y: " + y);
        }
    }
""",
    )

    path.write_text(text, encoding="utf-8")
    return repairs


def repair_combat_cycle_event_locals(source_root: Path) -> int:
    path = source_root / "com/rs2/model/combat/CombatCycleEvent.java"
    text = path.read_text(encoding="utf-8")
    repairs = 0
    try:
        start = text.index("    public final void execute(CycleEventContainer object) {\n")
        end = text.index("\n    @Override\n    public final void onStop()", start)
    except ValueError as exc:
        raise SystemExit(f"{path}: expected execute method not found") from exc
    execute_method = """    public final void execute(CycleEventContainer container) {
        try {
            if (!this.attacker.isCurrentActionSequence(this.actionSequence) || this.attacker.getCombatTarget() == null) {
                if (this.attacker.isPlayer()) {
                    Player player = (Player)this.attacker;
                    if (player.getUsername().toLowerCase().equals("mod test")) {
                        System.out.println("check combat cycle3 " + this.attacker.getCombatTarget() + " " + this.target.isDead());
                    }
                }
                container.stop();
                return;
            }
            AttackValidationResult attackValidationResult = CombatCycleEvent.validateAttack(this.attacker, this.target);
            if (attackValidationResult != AttackValidationResult.VALID) {
                container.stop();
                CombatManager.stopCombat(this.attacker);
                if (this.attacker.isPlayer()) {
                    Player player = (Player)this.attacker;
                    if (player.currentBotTask != null) {
                        player.interactWithBotNpcTargets(player.botInteractionTargetIds);
                    }
                    switch (attackValidationResult) {
                        case INVALID_TARGET_LOCATION: {
                            player.packetSender.sendGameMessage("You can't attack that npc from here.");
                            return;
                        }
                        case WILDERNESS_LEVEL_MISMATCH: {
                            player.packetSender.sendGameMessage("Your level difference is too great!");
                            player.packetSender.sendGameMessage("You need to move deeper into the Wilderness.");
                            CombatManager.stopCombat(player);
                            return;
                        }
                        case ALREADY_IN_COMBAT: {
                            player.packetSender.sendGameMessage("You are already under attack!");
                            return;
                        }
                        case NOT_DUEL_OPPONENT: {
                            player.packetSender.sendGameMessage("That player is not your opponent!");
                            return;
                        }
                        case TARGET_NOT_IN_WILDERNESS: {
                            player.packetSender.sendGameMessage("That player is not in the wilderness!");
                        }
                    }
                }
                return;
            }
            attackValidationResult = CombatCycleEvent.validateAttack(this.target, this.attacker);
            if (attackValidationResult != AttackValidationResult.VALID) {
                container.stop();
                CombatManager.stopCombat(this.attacker);
                if (this.attacker.isPlayer()) {
                    Player player = (Player)this.attacker;
                    if (player.currentBotTask != null) {
                        player.interactWithBotNpcTargets(player.botInteractionTargetIds);
                    }
                    switch (attackValidationResult) {
                        case WILDERNESS_LEVEL_MISMATCH: {
                            player.packetSender.sendGameMessage("Your level difference is too great!");
                            player.packetSender.sendGameMessage("You need to move deeper into the Wilderness.");
                            CombatManager.stopCombat(player);
                            return;
                        }
                        case ALREADY_IN_COMBAT: {
                            player.packetSender.sendGameMessage("That " + (this.target.isPlayer() ? "player" : "monster") + " is already in combat!");
                            return;
                        }
                        case NOT_DUEL_OPPONENT: {
                            player.packetSender.sendGameMessage("That player is not your opponent!");
                        }
                    }
                }
                return;
            }
            if (this.attacker.isPlayer() && this.target.isPlayer()) {
                Player player = (Player)this.attacker;
                Player targetPlayer = (Player)this.target;
                if (player.botEnabled || targetPlayer.botEnabled) {
                    if (player.botCombatState != null) {
                        return;
                    }
                    BotPvpCombatHandler.startBotPvpCombatTicks(player, targetPlayer);
                }
            }
            if (this.attacker.isPlayer() && this.target.isNpc()) {
                Player player = (Player)this.attacker;
                Npc targetNpc = (Npc)this.target;
                if (player.botEnabled) {
                    if (player.currentBotTask == null) {
                        return;
                    }
                    if (!player.botTaskState.equals("do task")) {
                        return;
                    }
                    player.currentBotTask.startNpcCombatTick(player, targetNpc);
                }
            }
            int distance = GameUtil.getDistance(this.attacker.getPosition(), this.target.getPosition());
            List<SmithingHandler> attackOptions = new LinkedList<SmithingHandler>();
            int usableAttackCount = this.attacker.collectCombatAttackOptions(attackOptions, this.target, distance);
            CombatAttack movementAttack = null;
            boolean attackDelayElapsed = this.attacker.getAttackDelayTimer().hasElapsed();
            if (attackDelayElapsed) {
                Collections.shuffle(attackOptions);
            }
            Iterator<SmithingHandler> iterator = attackOptions.iterator();
            while (iterator.hasNext()) {
                SmithingHandler attackOption = iterator.next();
                CombatAttackState combatAttackState = attackOption.getState();
                if (this.attacker.isStunned()) {
                    combatAttackState = CombatAttackState.b;
                }
                this.attacker.getUpdateState().setFaceEntity(this.target.getEncodedIndex());
                if (combatAttackState == CombatAttackState.b) {
                    movementAttack = attackOption.getAttack();
                    continue;
                }
                if (combatAttackState != CombatAttackState.c) {
                    continue;
                }
                if (!this.attacker.isPlayer()) {
                    this.attacker.setAttackRange(attackOption.getAttack().getAttackRange());
                }
                if (attackDelayElapsed) {
                    int delayTicks = attackOption.getAttack().execute(container);
                    if (delayTicks == -1) {
                        CombatManager.stopCombat(this.attacker);
                        if (this.attacker.isPlayer()) {
                            Player player = (Player)this.attacker;
                            if (player.getUsername().toLowerCase().equals("mod test")) {
                                System.out.println("check combat cycle2");
                            }
                        }
                        container.stop();
                        return;
                    }
                    this.target.getRecentCombatTimer().setTargetDelay(this.attacker, 17);
                    this.target.getSingleCombatTimer().setTargetDelay(this.attacker, 8);
                    this.attacker.setAttackDelayTicks(delayTicks);
                }
                return;
            }
            if (movementAttack == null) {
                if (usableAttackCount <= attackOptions.size()) {
                    CombatManager.stopCombat(this.attacker);
                    if (this.attacker.isPlayer()) {
                        Player player = (Player)this.attacker;
                        if (player.getUsername().toLowerCase().equals("mod test")) {
                            System.out.println("check combat cycle1");
                        }
                    }
                    container.stop();
                }
                this.attacker.getMovementQueue().clear();
                return;
            }
            if (!this.attacker.isPlayer()) {
                this.attacker.setAttackRange(movementAttack.getAttackRange());
            }
            return;
        }
        catch (Exception exception) {
            exception.printStackTrace();
            return;
        }
    }
"""
    text = text[:start] + execute_method + text[end:]
    repairs += 1
    path.write_text(text, encoding="utf-8")
    return repairs


def repair_control_panel_locals(source_root: Path) -> int:
    path = source_root / "com/rs2/launcher/ControlPanel.java"
    text = path.read_text(encoding="utf-8")
    repairs = 0
    for old, new in [
        ("        object2.setFont(", "        ((Component)object2).setFont("),
        ("        object2.setBounds(", "        ((Component)object2).setBounds("),
        ("        object2.setForeground(", "        ((Component)object2).setForeground("),
        ("        object2.setBackground(", "        ((Component)object2).setBackground("),
        ("        object2.setEnabled(", "        ((Component)object2).setEnabled("),
        (
            "        object2.addActionListener((ActionListener)object);\n",
            "        ((JCheckBox)object2).addActionListener((ActionListener)object);\n",
        ),
        (
            "        object2.add(noLoginRestrictionButton);\n",
            "        ((ButtonGroup)object2).add(noLoginRestrictionButton);\n",
        ),
        (
            "        object2.add(p2pLoginRestrictionButton);\n",
            "        ((ButtonGroup)object2).add(p2pLoginRestrictionButton);\n",
        ),
        (
            "        object2.add(modLoginRestrictionButton);\n",
            "        ((ButtonGroup)object2).add(modLoginRestrictionButton);\n",
        ),
        (
            "        object2.add(adminLoginRestrictionButton);\n",
            "        ((ButtonGroup)object2).add(adminLoginRestrictionButton);\n",
        ),
        (
            "                ServerSettings.serverName = object2;\n",
            "                ServerSettings.serverName = (String)object2;\n",
        ),
        (
            "                object2 = startPositionField.getText().split(\",\");\n"
            "                ServerSettings.startX = Integer.parseInt(object2[0]);\n"
            "                ServerSettings.startY = Integer.parseInt(object2[1]);\n"
            "                ServerSettings.startPlane = Integer.parseInt(object2[2]);\n",
            "                String[] startPositionParts = startPositionField.getText().split(\",\");\n"
            "                ServerSettings.startX = Integer.parseInt(startPositionParts[0]);\n"
            "                ServerSettings.startY = Integer.parseInt(startPositionParts[1]);\n"
            "                ServerSettings.startPlane = Integer.parseInt(startPositionParts[2]);\n",
        ),
        (
            "                object2 = respawnPositionField.getText().split(\",\");\n"
            "                ServerSettings.respawnX = Integer.parseInt(object2[0]);\n"
            "                ServerSettings.respawnY = Integer.parseInt(object2[1]);\n"
            "                ServerSettings.respawnPlane = Integer.parseInt(object2[2]);\n",
            "                String[] respawnPositionParts = respawnPositionField.getText().split(\",\");\n"
            "                ServerSettings.respawnX = Integer.parseInt(respawnPositionParts[0]);\n"
            "                ServerSettings.respawnY = Integer.parseInt(respawnPositionParts[1]);\n"
            "                ServerSettings.respawnPlane = Integer.parseInt(respawnPositionParts[2]);\n",
        ),
        (
            "                    ServerSettings.mysqlDriverClass = object2;\n",
            "                    ServerSettings.mysqlDriverClass = (String)object2;\n",
        ),
        (
            "                    ServerSettings.mysqlJdbcUrl = object2;\n",
            "                    ServerSettings.mysqlJdbcUrl = (String)object2;\n",
        ),
        (
            "                    ServerSettings.mysqlUsername = object2;\n",
            "                    ServerSettings.mysqlUsername = (String)object2;\n",
        ),
        (
            "                    ServerSettings.mysqlPassword = object2;\n",
            "                    ServerSettings.mysqlPassword = (String)object2;\n",
        ),
        (
            "                    ServerSettings.rsaModulusString = object2;\n",
            "                    ServerSettings.rsaModulusString = (String)object2;\n",
        ),
        (
            "                    ServerSettings.rsaPrivateExponentString = object2;\n",
            "                    ServerSettings.rsaPrivateExponentString = (String)object2;\n",
        ),
        (
            "        catch (IOException iOException) {\n",
            "        catch (Exception iOException) {\n",
        ),
    ]:
        count = text.count(old)
        text = text.replace(old, new)
        repairs += count

    old_main = """    public static void main(String[] object) {
        String string;
        block3: {
            object = new ControlPanel();
            ((Window)object).setVisible(true);
            String string2 = object.getClass().getName().replace('.', '/');
            object = object.getClass().getResource("/" + string2 + ".class").toString();
            if (((String)object).startsWith("jar:")) {
                object = ((String)object).split("/");
                String[] stringArray = object;
                int n = ((String[])object).length;
                int n2 = 0;
                while (n2 < n) {
                    object = stringArray[n2];
                    if (((String)object).contains("!")) {
                        string = ((String)object).substring(0, ((String)object).length() - 1);
                        break block3;
                    }
                    ++n2;
                }
            }
            string = null;
        }
        object = string;
        ServerSettings.launcherJarPath = string;
    }
"""
    new_main = """    public static void main(String[] args) {
        ControlPanel controlPanel = new ControlPanel();
        controlPanel.setVisible(true);
        String classResourceName = controlPanel.getClass().getName().replace('.', '/');
        String classResourcePath = controlPanel.getClass().getResource("/" + classResourceName + ".class").toString();
        String launcherJarPath = null;
        if (classResourcePath.startsWith("jar:")) {
            String[] pathParts = classResourcePath.split("/");
            int pathPartCount = pathParts.length;
            int n = 0;
            while (n < pathPartCount) {
                String pathPart = pathParts[n];
                if (pathPart.contains("!")) {
                    launcherJarPath = pathPart.substring(0, pathPart.length() - 1);
                    break;
                }
                ++n;
            }
        }
        ServerSettings.launcherJarPath = launcherJarPath;
    }
"""
    text, count = replace_exact(text, old_main, new_main, path)
    repairs += count

    old_action_performed = """    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        Object object;
        if (actionEvent.getActionCommand() == "Start Server") {
            object = new String[]{""};
            Server.main(object);
            ControlPanel.refreshStatusDisplay();
        }
        if (actionEvent.getActionCommand() == "Shutdown Server") {
            if (World.getPlayerCount() > 0 && World.getNonBotPlayerCount() == 0) {
                World.logoutBotsAndScheduleShutdown(true);
            } else {
                Server.scheduleShutdown(false);
            }
            ControlPanel.refreshStatusDisplay();
        }
        if (actionEvent.getActionCommand() == "Send") {
            object = serverMessageField.getText();
            Server.broadcastServerMessage((String)object);
        }
        if (actionEvent.getActionCommand() == "Map") {
            this.worldMapFrame.setVisible(true);
        }
        if (actionEvent.getActionCommand() == "Show names") {
            ControlPanel.worldMapPanel.showPlayerNames = this.showPlayerNamesCheckbox.isSelected();
            worldMapPanel.repaint();
        }
    }
"""
    new_action_performed = """    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getActionCommand() == "Start Server") {
            Server.main(new String[]{""});
            ControlPanel.refreshStatusDisplay();
        }
        if (actionEvent.getActionCommand() == "Shutdown Server") {
            if (World.getPlayerCount() > 0 && World.getNonBotPlayerCount() == 0) {
                World.logoutBotsAndScheduleShutdown(true);
            } else {
                Server.scheduleShutdown(false);
            }
            ControlPanel.refreshStatusDisplay();
        }
        if (actionEvent.getActionCommand() == "Send") {
            String serverMessage = serverMessageField.getText();
            Server.broadcastServerMessage(serverMessage);
        }
        if (actionEvent.getActionCommand() == "Map") {
            this.worldMapFrame.setVisible(true);
        }
        if (actionEvent.getActionCommand() == "Show names") {
            ControlPanel.worldMapPanel.showPlayerNames = this.showPlayerNamesCheckbox.isSelected();
            worldMapPanel.repaint();
        }
    }
"""
    text, count = replace_exact(text, old_action_performed, new_action_performed, path)
    repairs += count

    text, count = replace_exact(
        text,
        "    public static void refreshStatusDisplay() {\n",
        "    public static void refreshStatusDisplay() {\n"
        "        if (startServerButton == null || restartServerButton == null || shutdownServerButton == null || sendServerMessageButton == null || serverStatusLabel == null || usersOnlineLabel == null || serverNameStatusLabel == null || runtimeLabel == null) {\n"
        "            return;\n"
        "        }\n",
        path,
    )
    repairs += count

    path.write_text(text, encoding="utf-8")
    return repairs


def repair_bot_combat_helper_locals(source_root: Path) -> int:
    path = source_root / "com/rs2/bot/combat/BotCombatHelper.java"
    text = path.read_text(encoding="utf-8")
    repairs = 0

    def replace_section(text: str, start_marker: str, end_marker: str, replacement: str) -> tuple[str, int]:
        try:
            start = text.index(start_marker)
            end = text.index(end_marker, start)
        except ValueError as exc:
            raise SystemExit(f"{path}: expected section not found: {start_marker.strip()[:80]!r}") from exc
        return text[:start] + replacement + text[end:], 1

    text, count = replace_section(
        text,
        "    public static boolean isPlayerInAnyArea(Player player, RectangularArea[] object) {\n",
        "\n    public static void advanceBotEscapeWaypoints",
        """    public static boolean isPlayerInAnyArea(Player player, RectangularArea[] areas) {
        int n = areas.length;
        int n2 = 0;
        while (n2 < n) {
            RectangularArea area = areas[n2];
            if (area.contains(player.getPosition())) {
                return true;
            }
            ++n2;
        }
        return false;
    }

    private static boolean isPositionInAnyArea(Position position, RectangularArea[] areas) {
        int n = areas.length;
        int n2 = 0;
        while (n2 < n) {
            RectangularArea area = areas[n2];
            if (area.contains(position)) {
                return true;
            }
            ++n2;
        }
        return false;
    }
""",
    )
    repairs += count

    for old, new in [
        (
            "            for (ItemStack itemStack : player.botLootSellItems) {\n",
            "            for (Object itemStackObject : player.botLootSellItems) {\n"
            "                ItemStack itemStack = (ItemStack)itemStackObject;\n",
        ),
        (
            "        for (GroundItem groundItem : player.botLootGroundItems) {\n"
            "            int n;\n",
            "        for (Object groundItemObject : player.botLootGroundItems) {\n"
            "            GroundItem groundItem = (GroundItem)groundItemObject;\n"
            "            int n;\n",
        ),
    ]:
        text, count = replace_exact(text, old, new, path)
        repairs += count

    text, count = replace_section(
        text,
        "    public static boolean hasRunesForSpell(Player player, SpellDefinition object) {\n",
        "\n    public static boolean eatBotFood",
        """    public static boolean hasRunesForSpell(Player player, SpellDefinition spellDefinition) {
        ItemStack[] itemStackArray = spellDefinition.getRuneCosts();
        int n = itemStackArray.length;
        int n2 = 0;
        while (n2 < n) {
            ItemStack runeCost = itemStackArray[n2];
            if (!(runeCost.getId() == 556 && player.getEquipmentManager().getItemIdAtSlot(3) == 1381 || runeCost.getId() == 555 && player.getEquipmentManager().getItemIdAtSlot(3) == 1383 || runeCost.getId() == 557 && player.getEquipmentManager().getItemIdAtSlot(3) == 1385 || runeCost.getId() == 554 && player.getEquipmentManager().getItemIdAtSlot(3) == 1387 || player.getInventoryManager().containsItemAmount(runeCost.getId(), runeCost.getAmount()))) {
                return false;
            }
            ++n2;
        }
        return true;
    }
""",
    )
    repairs += count

    text, count = replace_section(
        text,
        "    static boolean isTargetLootWorthRisk(Player player, Player player2) {\n",
        "\n    public static int selectBotLoadoutItemId",
        """    static boolean isTargetLootWorthRisk(Player player, Player player2) {
        if (player.skulled) {
            return true;
        }
        if (player.currentGroup != null && player2.currentGroup == null && player.isInMultiCombatArea() && player2.isInMultiCombatArea()) {
            return true;
        }
        double riskRatio = player2.getCombatLevel() > player.getCombatLevel() + 5 ? 0.3 : 0.2;
        int targetLootValue = 0;
        for (Object itemStackObject : player2.getUnprotectedItems(player2.getEquipmentManager().getContainer().getItems())) {
            ItemStack itemStack = (ItemStack)itemStackObject;
            if (itemStack == null) continue;
            int guidePrice = GrandExchangeManager.getGuidePrice(itemStack.getId());
            targetLootValue += guidePrice * itemStack.getAmount();
        }
        int playerRiskValue = 0;
        ItemStack[] itemStackArray = player.getInventoryManager().getContainer().getItems();
        int n = itemStackArray.length;
        int n2 = 0;
        while (n2 < n) {
            ItemStack itemStack = itemStackArray[n2];
            if (itemStack != null) {
                int guidePrice = GrandExchangeManager.getGuidePrice(itemStack.getId());
                playerRiskValue += guidePrice * itemStack.getAmount();
            }
            ++n2;
        }
        itemStackArray = player.getEquipmentManager().getContainer().getItems();
        n = itemStackArray.length;
        n2 = 0;
        while (n2 < n) {
            ItemStack itemStack = itemStackArray[n2];
            if (itemStack != null) {
                int guidePrice = GrandExchangeManager.getGuidePrice(itemStack.getId());
                playerRiskValue += guidePrice * itemStack.getAmount();
            }
            ++n2;
        }
        return !((double)targetLootValue < (double)playerRiskValue * riskRatio);
    }
""",
    )
    repairs += count

    text, count = replace_section(
        text,
        "    public static int selectBotLoadoutItemId(Player player, int[] object, int[] nArray, boolean bl) {\n",
        "\n    static int selectBestBotLoadoutItemId",
        """    public static int selectBotLoadoutItemId(Player player, int[] freeItemIds, int[] memberItemIds, boolean randomize) {
        ArrayList<Integer> itemIds = new ArrayList<Integer>();
        if (freeItemIds != null) {
            int n = freeItemIds.length;
            int n2 = 0;
            while (n2 < n) {
                itemIds.add(freeItemIds[n2]);
                ++n2;
            }
        }
        if (!BotCombatHelper.isFreeToPlayWorld() && memberItemIds != null) {
            int n = memberItemIds.length;
            int n2 = 0;
            while (n2 < n) {
                itemIds.add(memberItemIds[n2]);
                ++n2;
            }
        }
        if (!randomize) {
            int selectedIndex = 0;
            int n = 0;
            while (n < itemIds.size()) {
                int itemId = (Integer)itemIds.get(n);
                if (ItemDefinition.isDefined(itemId)) {
                    if (!player.getEquipmentManager().canEquipItem(itemId)) {
                        selectedIndex = n - 1;
                        break;
                    }
                    selectedIndex = n;
                }
                ++n;
            }
            if (selectedIndex < 0 || itemIds.size() == 0) {
                return -1;
            }
            return (Integer)itemIds.get(selectedIndex);
        }
        ArrayList<Integer> equippableItemIds = new ArrayList<Integer>();
        Iterator iterator = itemIds.iterator();
        while (iterator.hasNext()) {
            int itemId = (Integer)iterator.next();
            if (!ItemDefinition.isDefined(itemId) || !player.getEquipmentManager().canEquipItem(itemId)) continue;
            equippableItemIds.add(itemId);
        }
        if (equippableItemIds.size() == 0) {
            return -1;
        }
        return (Integer)equippableItemIds.get(GameUtil.randomInt(equippableItemIds.size()));
    }

    public static int[] filterEquippableMemberLoadoutItems(Player player, int[] freeItemIds, int[] memberItemIds) {
        ArrayList<Integer> candidateItemIds = new ArrayList<Integer>();
        if (!BotCombatHelper.isFreeToPlayWorld() && memberItemIds != null) {
            int n = memberItemIds.length;
            int n2 = 0;
            while (n2 < n) {
                candidateItemIds.add(memberItemIds[n2]);
                ++n2;
            }
        }
        ArrayList<Integer> equippableItemIds = new ArrayList<Integer>();
        Iterator iterator = candidateItemIds.iterator();
        while (iterator.hasNext()) {
            int itemId = (Integer)iterator.next();
            if (!ItemDefinition.isDefined(itemId) || !player.getEquipmentManager().canEquipItem(itemId)) continue;
            equippableItemIds.add(itemId);
        }
        int[] itemIds = new int[equippableItemIds.size()];
        int n = 0;
        while (n < equippableItemIds.size()) {
            itemIds[n] = (Integer)equippableItemIds.get(n);
            ++n;
        }
        return itemIds;
    }
""",
    )
    repairs += count

    text, count = replace_section(
        text,
        "    public static void grantBotSpellRunes(Player player, SpellDefinition object, int n) {\n",
        "\n    public static void walkBotTowardPosition",
        """    public static void grantBotSpellRunes(Player player, SpellDefinition spellDefinition, int n) {
        if (player.botMode == 4 || BotPlayer.defaultProgressiveBotNames.contains(player.getUsername().toLowerCase())) {
            String string = "CRITICAL BUG, REPORT! BotUtil " + player.getUsername() + " " + player.botMode + " " + player.currentBotTaskIndex + " " + player.currentBotTaskTypeId + " " + player.currentBotTask;
            System.out.println(string);
            GameplayHelper.appendLogLine(string, "errors");
            return;
        }
        ArrayList<ItemStack> grantedRuneCosts = new ArrayList<ItemStack>();
        ItemStack[] itemStackArray = spellDefinition.getRuneCosts();
        int n2 = itemStackArray.length;
        int n3 = 0;
        while (n3 < n2) {
            ItemStack runeCost = itemStackArray[n3];
            if (!(runeCost.getId() == 556 && player.getEquipmentManager().getItemIdAtSlot(3) == 1381 || runeCost.getId() == 555 && player.getEquipmentManager().getItemIdAtSlot(3) == 1383 || runeCost.getId() == 557 && player.getEquipmentManager().getItemIdAtSlot(3) == 1385 || runeCost.getId() == 554 && player.getEquipmentManager().getItemIdAtSlot(3) == 1387)) {
                ItemStack grantedRuneCost = new ItemStack(runeCost.getId(), runeCost.getAmount() * n);
                grantedRuneCosts.add(grantedRuneCost);
                player.getInventoryManager().addItem(grantedRuneCost);
            }
            ++n3;
        }
        if (player.currentBotTask != null) {
            ItemStack[] requiredItems;
            if (player.botTaskRequiredItems != null) {
                requiredItems = new ItemStack[player.botTaskRequiredItems.length + grantedRuneCosts.size()];
                n3 = 0;
                while (n3 < player.botTaskRequiredItems.length) {
                    requiredItems[n3] = player.botTaskRequiredItems[n3];
                    ++n3;
                }
                n3 = 0;
                while (n3 < grantedRuneCosts.size()) {
                    ItemStack grantedRuneCost = (ItemStack)grantedRuneCosts.get(n3);
                    requiredItems[player.botTaskRequiredItems.length + n3] = grantedRuneCost;
                    player.getBankContainer().addToTab(new ItemStack(grantedRuneCost.getId(), grantedRuneCost.getAmount() * 10), 0);
                    ++n3;
                }
            } else {
                requiredItems = new ItemStack[grantedRuneCosts.size()];
                n3 = 0;
                while (n3 < grantedRuneCosts.size()) {
                    ItemStack grantedRuneCost = (ItemStack)grantedRuneCosts.get(n3);
                    requiredItems[n3] = grantedRuneCost;
                    player.getBankContainer().addToTab(new ItemStack(grantedRuneCost.getId(), grantedRuneCost.getAmount() * 10), 0);
                    ++n3;
                }
            }
            player.botTaskRequiredItems = requiredItems;
        }
    }
""",
    )
    repairs += count

    path.write_text(text, encoding="utf-8")
    return repairs


def repair_combat_manager_locals(source_root: Path) -> int:
    path = source_root / "com/rs2/model/combat/CombatManager.java"
    text = path.read_text(encoding="utf-8")
    repairs = 0

    def replace_section(text: str, start_marker: str, end_marker: str, replacement: str) -> tuple[str, int]:
        try:
            start = text.index(start_marker)
            end = text.index(end_marker, start)
        except ValueError as exc:
            raise SystemExit(f"{path}: expected section not found: {start_marker.strip()[:80]!r}") from exc
        return text[:start] + replacement + text[end:], 1

    text, count = replace_section(
        text,
        "    public static void startCombat(Entity entity, Entity entity2) {\n",
        "\n    private static boolean b(Entity entity, Entity entity2) {\n",
        """    public static void startCombat(Entity entity, Entity entity2) {
        if (entity2.isNpc() && !((Npc)entity2).isActive() || entity2.isDead()) {
            if (entity.isPlayer()) {
                Player player = (Player)entity;
                if (player.currentBotTask != null) {
                    player.interactWithBotNpcTargets(player.botInteractionTargetIds);
                }
            }
            return;
        }
        if (entity2.getMaxHitpoints() <= 0 || entity2.isNpc() && (((Npc)entity2).getNpcId() == 411 || RandomEventManager.isRandomEventCombatExcludedNpcId(((Npc)entity2).getNpcId()))) {
            if (entity.isPlayer()) {
                Player player = (Player)entity;
                player.packetSender.sendGameMessage("You cannot attack this npc.");
            }
            CombatManager.stopCombat(entity);
            return;
        }
        if (entity2.isPlayer() && entity.isNpc()) {
            Npc npc = (Npc)entity;
            Player player = (Player)entity2;
            if (npc.getNpcId() == 1456 && player.ai) {
                CombatManager.stopCombat(entity);
                return;
            }
        }
        if (entity2.isPlayer() && entity.isNpc() && ((Npc)entity).getNpcId() == 745) {
            CombatManager.stopCombat(entity);
            return;
        }
        if (entity.isPlayer() && entity2.isNpc() && !((Player)entity).getQuestManager().canAttackNpc(((Npc)entity2).getNpcId())) {
            Player player = (Player)entity;
            player.packetSender.sendGameMessage("You cannot attack this npc.");
            CombatManager.stopCombat(entity);
            return;
        }
        if (entity.isPlayer() && entity.isInDuelArena() && !((Player)entity).getDuelSession().isActiveDuelStarted()) {
            CombatManager.stopCombat(entity);
            return;
        }
        List attackOptions = new LinkedList();
        int n = GameUtil.getDistance(entity.getPosition(), entity2.getPosition());
        entity.collectCombatAttackOptions(attackOptions, entity2, n);
        SmithingHandler selectedAttackOption = null;
        Iterator iterator = attackOptions.iterator();
        while (iterator.hasNext()) {
            SmithingHandler attackOption = (SmithingHandler)iterator.next();
            if (attackOption.getState() == CombatAttackState.a || selectedAttackOption != null && attackOption.getAttack().getAttackRange() <= selectedAttackOption.getAttack().getAttackRange()) continue;
            selectedAttackOption = attackOption;
        }
        if (entity.isPlayer() && entity2.isNpc() && selectedAttackOption != null) {
            Npc targetNpc = (Npc)entity2;
            Player attackingPlayer = (Player)entity;
            if (selectedAttackOption.getAttack().getCombatType() == CombatType.MELEE && GodWarsDungeonManager.armadylNpcIds.contains(targetNpc.getNpcId())) {
                attackingPlayer.packetSender.sendGameMessage("The Aviansie is flying too high for you to attack using melee.");
                return;
            }
        }
        int n2 = selectedAttackOption == null ? 1 : selectedAttackOption.getAttack().getAttackRange();
        entity.setAttackRange(n2);
        if (entity.isPlayer() && ((Player)entity).getUsername().toLowerCase().equals("mod test")) {
            System.out.println("check attack");
        }
        Entity target = entity2;
        Entity attacker = entity;
        boolean shouldStartCombatCycle = true;
        if (attacker.isPlayer()) {
            Player player = (Player)attacker;
            if (player.botCombatEscapeActive || player.currentBotTask != null && !player.botTaskState.equals("do task")) {
                shouldStartCombatCycle = false;
            }
        }
        if (shouldStartCombatCycle) {
            CombatCycleEvent combatCycleEvent = new CombatCycleEvent(attacker, target);
            attacker.setCombatTarget(target);
            attacker.getUpdateState().setFaceEntity(target.getEncodedIndex());
            attacker.getUpdateState().setFacePosition(target.getPosition());
            attacker.setMovementTarget(target);
            attacker.setActiveCycleEvent(combatCycleEvent);
            if (attacker.isPlayer() && ((Player)attacker).getUsername().toLowerCase().equals("mod test")) {
                System.out.println("check start combat");
            }
            if (attacker.isNpc()) {
                Npc attackerNpc = (Npc)attacker;
                if (attackerNpc.getTransformTicksRemaining() <= 0 && attackerNpc.getCombatTransformNpcId() > 0) {
                    attackerNpc.transformToNpcId(attackerNpc.getCombatTransformNpcId(), 999999);
                }
            }
            if (attacker.isPlayer()) {
                Player player = (Player)attacker;
                if (player.npcTransformationId == 2626 || player.npcTransformationId >= 3689 && player.npcTransformationId <= 3694) {
                    player.npcTransformationId = -1;
                    player.packetSender.refreshSidebarInterfaces();
                    player.setAppearanceUpdateRequired(true);
                }
            }
            CycleEventHandler.getInstance().schedule(attacker, attacker.getActiveCycleEvent(), 1);
        }
        if (entity.isPlayer() && ((Player)entity).getQuestState(0) == 47) {
            ((Player)entity).getDialogueManager().showTutorialInstructionOverlay("Sit back and watch.", "While you are fighting you will see a bar over your head. The", "bar shows how much health you have left. Your opponent will", "have one too. You will continue to attack the rat until it's dead", "or you do something else.", true);
        }
    }
""",
    )
    repairs += count

    text, count = replace_section(
        text,
        "    @Override\n    public final void execute() {\n",
        "\n    public static void handleDeath(Entity entity) {\n",
        """    @Override
    public final void execute() {
        try {
            List pendingActionsSnapshot = new LinkedList();
            pendingActionsSnapshot.addAll(this.pendingActions);
            this.pendingActions.clear();
            Iterator iterator = pendingActionsSnapshot.iterator();
            while (iterator.hasNext()) {
                CombatAction combatAction = (CombatAction)iterator.next();
                combatAction.tickDelay();
                if (combatAction.isReady()) {
                    if (!combatAction.getTarget().isDead()) {
                        combatAction.applyHit();
                    }
                    if (combatAction.getTarget().getCurrentHitpoints() > 0 || combatAction.getTarget().isDead()) continue;
                    if (combatAction.getAttacker() != null && combatAction.getTarget() != null && (combatAction.getAttacker().isPlayer() && combatAction.getTarget().isNpc() || combatAction.getAttacker().isNpc() && combatAction.getTarget().isPlayer())) {
                        boolean handledQuestDeath = combatAction.getAttacker().isPlayer() ? ((Player)combatAction.getAttacker()).getQuestManager().handleCombatDeath(combatAction.getAttacker(), combatAction.getTarget()) : ((Player)combatAction.getTarget()).getQuestManager().handleCombatDeath(combatAction.getAttacker(), combatAction.getTarget());
                        if (!handledQuestDeath) {
                            handledQuestDeath = CombatManager.b(combatAction.getAttacker(), combatAction.getTarget());
                        }
                        if (handledQuestDeath) {
                            return;
                        }
                    }
                    if (combatAction.getAttacker() != null && combatAction.getAttacker().isNpc()) {
                        Npc attackerNpc = (Npc)combatAction.getAttacker();
                        if (attackerNpc.a != null && attackerNpc.a.getQuestManager().handleCombatDeath(combatAction.getAttacker(), combatAction.getTarget())) {
                            return;
                        }
                    }
                    if (combatAction.getTarget() != null && combatAction.getTarget().isNpc()) {
                        Npc targetNpc = (Npc)combatAction.getTarget();
                        if (targetNpc.a != null && targetNpc.a.getQuestManager().handleCombatDeath(combatAction.getAttacker(), combatAction.getTarget())) {
                            return;
                        }
                    }
                    if (combatAction.getTarget() != null && combatAction.getTarget().isPlayer()) {
                        Player player = (Player)combatAction.getTarget();
                        if (player.godModeEnabled) {
                            player.setCurrentHitpoints(player.getMaxHitpoints());
                            return;
                        }
                    }
                    combatAction.getTarget().setDead(true);
                    CombatManager.handleDeath(combatAction.getTarget());
                    if (!combatAction.getTarget().isPlayer() || !combatAction.getTarget().isInDuelArena()) continue;
                    ((Player)combatAction.getTarget()).getDuelSession().getOpponent().getAttributes().put("canTakeDamage", false);
                    return;
                }
                if (combatAction.getTarget().isDead()) continue;
                this.pendingActions.add(combatAction);
            }
            return;
        }
        catch (Exception exception) {
            exception.printStackTrace();
            return;
        }
    }
""",
    )
    repairs += count

    text, count = replace_section(
        text,
        "    public static void handleDeath(Entity entity) {\n",
        "\n    public static void finishDeath(Entity entity, Entity killer, boolean dropItems) {\n",
        """    public static void handleDeath(Entity entity) {
        int n;
        Entity entity2 = entity.getTopDamageContributor();
        ArrayList damageContributorList = entity.getDamageContributorList();
        if (damageContributorList.size() > 0) {
            Iterator iterator = damageContributorList.iterator();
            while (iterator.hasNext()) {
                Entity contributor = (Entity)iterator.next();
                if (entity2 == contributor || contributor.getCombatTarget() != entity || !contributor.isPlayer()) continue;
                Player player = (Player)contributor;
                if (!player.botEnabled || player.currentBotTask == null) continue;
                player.interactWithBotNpcTargets(player.botInteractionTargetIds);
            }
        }
        if (entity != null && entity2 != null && entity.isNpc() && entity2.isPlayer()) {
            Npc deadNpc = (Npc)entity;
            Player[] playerArray = World.getPlayers();
            int n2 = playerArray.length;
            int n3 = 0;
            while (n3 < n2) {
                Player player = playerArray[n3];
                if (player != null) {
                    if (player.bP == deadNpc.getIndex() && deadNpc.getOwnerPlayer() == null) {
                        player.packetSender.sendEntityHintIcon(1, Npc.findByDefinitionId(deadNpc.getNpcId()).getIndex());
                    }
                    if (player.bP == deadNpc.getIndex() && deadNpc.getOwnerPlayer() == player) {
                        player.packetSender.sendEntityHintIcon(1, -1);
                    }
                }
                ++n3;
            }
        }
        entity.nextActionSequence();
        entity.setDeathPosition(entity.getPosition().copy());
        if (entity2 != null && entity2.isPlayer()) {
            Player killerPlayer = (Player)entity2;
            killerPlayer.getSingleCombatTimer().setDelayTicks(0);
            killerPlayer.getSingleCombatTimer().reset();
            if (killerPlayer.getQuestState(0) == 47 || killerPlayer.getQuestState(0) == 49) {
                killerPlayer.ea();
            }
        }
        if (entity.isPlayer()) {
            Player deadPlayer = (Player)entity;
            deadPlayer.setActionLocked(true);
            if (deadPlayer.botEnabled && deadPlayer.currentBotTask == null && !deadPlayer.clanWarsBot) {
                String[] deathMessages = new String[]{"gf", "fuck", "shit", "damn"};
                deadPlayer.queuePublicChatMessage(deathMessages[GameUtil.randomInt(4)]);
            }
        }
        if (entity2 != null && entity2.isPlayer()) {
            Player killerPlayer = (Player)entity2;
            if (killerPlayer.botEnabled) {
                if (killerPlayer.getCombatTarget() == entity) {
                    killerPlayer.botCombatState = "wait for loot";
                }
                if (entity.isPlayer() && killerPlayer.currentGroup == null) {
                    if (killerPlayer.clanWarsBot) {
                        if (killerPlayer.clanWarsTeamId == 1) {
                            killerPlayer.queuePublicChatMessage("#" + ClanWarsBotManager.clanWarsTeamOneTag);
                        } else {
                            killerPlayer.queuePublicChatMessage("#" + ClanWarsBotManager.clanWarsTeamTwoTag);
                        }
                    } else {
                        killerPlayer.queuePublicChatMessage("gf");
                    }
                }
            }
        }
        if ((n = entity.getDeathAnimationId()) != -1) {
            World.getTaskScheduler().schedule(new DeathAnimationTask(2, n, entity, entity2));
        }
        World.getTaskScheduler().schedule(new DeathCleanupTask(entity.getDeathDelayTicks(), entity, entity2));
        entity.nextActionSequence();
        entity.clearCombatEffectTasks();
        entity.getRecentCombatTimer().setDelayTicks(entity.getDeathDelayTicks() + 2);
        entity.getRecentCombatTimer().reset();
        entity.getSingleCombatTimer().setDelayTicks(entity.getDeathDelayTicks() + 2);
        entity.getSingleCombatTimer().reset();
        if (entity2 != null && entity.isPlayer() && ((Player)entity).getActivePrayers()[15]) {
            PrayerManager.triggerRetribution(entity, entity2);
        }
    }
""",
    )
    repairs += count

    text, count = replace_section(
        text,
        "    public static double calculateWeaponMaxHit(Player player, WeaponCombatAttack object) {\n",
        "\n    public static double calculateMeleeMaxHit(Player player, WeaponCombatAttack object) {\n",
        """    public static double calculateWeaponMaxHit(Player player, WeaponCombatAttack weaponCombatAttack) {
        AttackStyleDefinition attackStyleDefinition = weaponCombatAttack.getAttackStyle();
        if (attackStyleDefinition.getCombatType() == CombatType.MELEE) {
            return CombatManager.calculateMeleeMaxHit(player, weaponCombatAttack);
        }
        if (attackStyleDefinition.getCombatType() == CombatType.RANGED && weaponCombatAttack.getAmmunition() != null) {
            if (ServerSettings.modernCombatSystemEnabled) {
                double rangedLevel = player.getSkillManager().getCurrentLevels()[4];
                int effectiveRangedLevel = (int)Math.floor(rangedLevel * CombatManager.getPrayerMultiplier(player, 4));
                if (attackStyleDefinition.getXpMode() == AttackXpMode.RANGED_ACCURATE) {
                    effectiveRangedLevel += 3;
                }
                effectiveRangedLevel += 8;
                if (player.hasFullVoidRangedSet()) {
                    effectiveRangedLevel = (int)((double)effectiveRangedLevel * 1.1);
                }
                effectiveRangedLevel = (int)Math.floor(effectiveRangedLevel);
                double rangedStrengthBonus = player.getCombatBonus(12);
                return (int)Math.floor(0.5 + (double)effectiveRangedLevel * (rangedStrengthBonus + 64.0) / 640.0);
            }
            int rangedLevel = player.getSkillManager().getCurrentLevels()[4];
            double styleBonus = 0.0;
            if (attackStyleDefinition.getXpMode() == AttackXpMode.RANGED_ACCURATE) {
                styleBonus = 3.0;
            } else if (attackStyleDefinition.getXpMode() == AttackXpMode.LONGRANGE) {
                styleBonus = 1.0;
            }
            rangedLevel = (int)((double)rangedLevel + styleBonus);
            double rangedStrength = weaponCombatAttack.getAmmunition().getRangedStrength();
            double maxHit = ((double)rangedLevel + rangedStrength / 8.0 + (double)rangedLevel * rangedStrength * Math.pow(64.0, -1.0) + 14.0) / 10.0;
            return (int)Math.floor(maxHit);
        }
        return 0.0;
    }
""",
    )
    repairs += count

    text, count = replace_section(
        text,
        "    public static double calculateMeleeMaxHit(Player player, WeaponCombatAttack object) {\n",
        "\n    public static double calculateHitChance(double d, double d2) {\n",
        """    public static double calculateMeleeMaxHit(Player player, WeaponCombatAttack weaponCombatAttack) {
        if (ServerSettings.modernCombatSystemEnabled) {
            return CombatManager.calculateMeleeMaxHit((Entity)player, weaponCombatAttack);
        }
        double strengthLevel = player.getSkillManager().getCurrentLevels()[2];
        if (player.getActivePrayers()[1]) {
            strengthLevel *= 1.05;
        } else if (player.getActivePrayers()[4]) {
            strengthLevel *= 1.1;
        } else if (player.getActivePrayers()[10]) {
            strengthLevel *= 1.15;
        }
        AttackStyleDefinition attackStyleDefinition = weaponCombatAttack.getAttackStyle();
        int styleBonus = 0;
        if (attackStyleDefinition.getXpMode() == AttackXpMode.AGGRESSIVE) {
            styleBonus = 3;
        } else if (attackStyleDefinition.getXpMode() == AttackXpMode.CONTROLLED) {
            styleBonus = 1;
        }
        int effectiveStrengthLevel = (int)(strengthLevel + (double)styleBonus);
        double maxHitRoll = 5 + (effectiveStrengthLevel + 8) * (player.getCombatBonus(10) + 64) / 64;
        int maxHit = (int)Math.floor(maxHitRoll);
        return (int)Math.floor(maxHit / 10);
    }
""",
    )
    repairs += count

    text, count = replace_section(
        text,
        "    public static double calculateHitChance(double d, double d2) {\n",
        "\n    public static boolean rollAccuracy(double d) {\n",
        """    public static double calculateHitChance(double d, double d2) {
        if (ServerSettings.modernCombatSystemEnabled) {
            return d > d2 ? 1.0 - (d2 + 2.0) / (2.0 * (d + 1.0)) : d / (2.0 * (d2 + 1.0));
        }
        double d3 = Math.floor(d);
        double d4 = Math.floor(d2);
        double d5 = d3 < d4 ? (d3 - 1.0) / (d4 * 2.0) : 1.0 - (d4 + 1.0) / (d3 * 2.0);
        d5 = d5 > 0.9999 ? 0.9999 : (d5 < 1.0E-4 ? 1.0E-4 : d5);
        return d5;
    }
""",
    )
    repairs += count

    text, count = replace_section(
        text,
        "    public static double calculateDefenceRoll(Entity entity, HitDefinition object) {\n",
        "\n    public static double calculateAttackRoll(Entity entity, HitDefinition object) {\n",
        """    public static double calculateDefenceRoll(Entity entity, HitDefinition hitDefinition) {
        AttackStyleDefinition attackStyleDefinition = hitDefinition.getAttackStyle();
        if (ServerSettings.modernCombatSystemEnabled) {
            double defenceRoll = 0.0;
            if (attackStyleDefinition.getCombatType() == CombatType.MELEE) {
                int effectiveDefenceLevel;
                if (entity.isPlayer()) {
                    Player player = (Player)entity;
                    effectiveDefenceLevel = (int)Math.floor((double)player.getSkillManager().getCurrentLevels()[1] * CombatManager.getPrayerMultiplier(player, 1));
                    AttackStyleDefinition playerAttackStyle = player.getWeaponProfile().getInterfaceDefinition().getAttackStyles()[player.getFightMode()];
                    if (playerAttackStyle.getXpMode() == AttackXpMode.DEFENSIVE) {
                        effectiveDefenceLevel += 3;
                    } else if (playerAttackStyle.getXpMode() == AttackXpMode.CONTROLLED) {
                        ++effectiveDefenceLevel;
                    }
                    effectiveDefenceLevel += 8;
                    effectiveDefenceLevel = (int)Math.floor(effectiveDefenceLevel);
                    int defenceBonus = player.getCombatBonus(attackStyleDefinition.getDefenseBonusIndex());
                    defenceRoll = (int)Math.floor((double)effectiveDefenceLevel * (double)(defenceBonus + 64));
                } else {
                    Npc npc = (Npc)entity;
                    effectiveDefenceLevel = npc.getCurrentDefenceLevel();
                    int defenceBonus = npc.getDefenceBonus(attackStyleDefinition.getDefenseBonusIndex());
                    defenceRoll = (effectiveDefenceLevel + 9) * (defenceBonus + 64);
                }
            } else if (attackStyleDefinition.getCombatType() == CombatType.RANGED) {
                int defenceLevel;
                int defenceBonus;
                if (entity.isPlayer()) {
                    Player player = (Player)entity;
                    defenceLevel = player.getSkillManager().getCurrentLevels()[1];
                    defenceBonus = player.getCombatBonus(9);
                } else {
                    Npc npc = (Npc)entity;
                    defenceLevel = npc.getCurrentDefenceLevel();
                    defenceBonus = npc.getDefenceBonus(attackStyleDefinition.getDefenseBonusIndex());
                }
                defenceRoll = (defenceLevel + 9) * (defenceBonus + 64);
            } else if (attackStyleDefinition.getCombatType() == CombatType.MAGIC) {
                int magicLevel;
                int defenceBonus;
                if (entity.isPlayer()) {
                    Player player = (Player)entity;
                    magicLevel = player.getSkillManager().getCurrentLevels()[6];
                    defenceBonus = player.getCombatBonus(8);
                } else {
                    Npc npc = (Npc)entity;
                    magicLevel = npc.getCurrentMagicLevel();
                    defenceBonus = npc.getDefenceBonus(attackStyleDefinition.getDefenseBonusIndex());
                }
                defenceRoll = (magicLevel + 9) * (defenceBonus + 64);
            }
            return (int)defenceRoll;
        }
        double defenceBonus = entity.getCombatBonus(attackStyleDefinition.getAttackBonusType().getIndex() + AttackBonusType.values().length);
        if (attackStyleDefinition.getXpMode() == AttackXpMode.SARADOMIN_SWORD) {
            defenceBonus = entity.getCombatBonus(AttackBonusType.MAGIC.getIndex() + AttackBonusType.values().length);
        }
        double defenceLevel = entity.getDefenceLevelFor(attackStyleDefinition.getCombatType());
        if (defenceBonus < 0.0) {
            defenceLevel /= 2.0;
            defenceBonus *= 3.0;
        }
        if (attackStyleDefinition.getCombatType() == CombatType.MELEE && entity.isPlayer()) {
            Player player = (Player)entity;
            if (player.getActivePrayers()[0]) {
                defenceLevel *= 1.05;
            } else if (player.getActivePrayers()[3]) {
                defenceLevel *= 1.1;
            } else if (player.getActivePrayers()[9]) {
                defenceLevel *= 1.15;
            }
        }
        double defenceRoll = Math.floor(defenceLevel + defenceBonus) + 8.0;
        if (entity.isPlayer()) {
            Player player = (Player)entity;
            if (attackStyleDefinition.getCombatType() == CombatType.MAGIC) {
                int magicLevel = player.getSkillManager().getCurrentLevels()[6];
                defenceRoll = (int)(Math.floor((double)magicLevel * 0.7) + Math.floor(defenceRoll * 0.3));
            }
        }
        if (hitDefinition.getSpecialEffectId() == 11) {
            defenceRoll *= 0.75;
        }
        if (entity.isNpc()) {
            if (attackStyleDefinition.getCombatType() == CombatType.MAGIC) {
                defenceRoll *= 0.7;
            }
            if (attackStyleDefinition.getCombatType() == CombatType.MELEE) {
                defenceRoll *= 0.6;
            }
        }
        return defenceRoll;
    }
""",
    )
    repairs += count

    text, count = replace_section(
        text,
        "    public static double calculateAttackRoll(Entity entity, HitDefinition object) {\n",
        "\n    public static void stopCombat(Entity entity) {\n",
        """    public static double calculateAttackRoll(Entity entity, HitDefinition hitDefinition) {
        Player player;
        AttackStyleDefinition attackStyleDefinition = hitDefinition.getAttackStyle();
        double accuracyMultiplier = hitDefinition.getAccuracyMultiplier();
        if (ServerSettings.modernCombatSystemEnabled) {
            double attackRoll = 0.0;
            if (attackStyleDefinition.getCombatType() == CombatType.MELEE) {
                int effectiveAttackLevel;
                if (entity.isPlayer()) {
                    Player attackingPlayer = (Player)entity;
                    effectiveAttackLevel = (int)Math.floor((double)attackingPlayer.getSkillManager().getCurrentLevels()[0] * CombatManager.getPrayerMultiplier(attackingPlayer, 0));
                    if (attackStyleDefinition.getXpMode() == AttackXpMode.MELEE_ACCURATE) {
                        effectiveAttackLevel += 3;
                    } else if (attackStyleDefinition.getXpMode() == AttackXpMode.CONTROLLED) {
                        ++effectiveAttackLevel;
                    }
                    effectiveAttackLevel += 8;
                    if (attackingPlayer.hasFullVoidMeleeSet()) {
                        effectiveAttackLevel = (int)((double)effectiveAttackLevel * 1.1);
                    }
                    effectiveAttackLevel = (int)Math.floor(effectiveAttackLevel);
                    int attackBonus = attackingPlayer.getCombatBonus(attackStyleDefinition.getAttackBonusIndex());
                    attackRoll = (int)Math.floor((double)effectiveAttackLevel * (double)(attackBonus + 64));
                } else {
                    Npc npc = (Npc)entity;
                    effectiveAttackLevel = (int)npc.getCurrentAttackLevel();
                    effectiveAttackLevel += 8;
                    effectiveAttackLevel = (int)Math.floor(effectiveAttackLevel);
                    int attackBonus = npc.getMeleeAttackBonus();
                    attackRoll = (int)Math.floor((double)effectiveAttackLevel * (double)(attackBonus + 64));
                }
            } else if (attackStyleDefinition.getCombatType() == CombatType.RANGED) {
                int effectiveRangedLevel;
                if (entity.isPlayer()) {
                    Player attackingPlayer = (Player)entity;
                    effectiveRangedLevel = (int)Math.floor((double)attackingPlayer.getSkillManager().getCurrentLevels()[4] * CombatManager.getPrayerMultiplier(attackingPlayer, 4));
                    if (attackStyleDefinition.getXpMode() == AttackXpMode.RANGED_ACCURATE) {
                        effectiveRangedLevel += 3;
                    }
                    effectiveRangedLevel += 8;
                    if (attackingPlayer.hasFullVoidRangedSet()) {
                        effectiveRangedLevel = (int)((double)effectiveRangedLevel * 1.1);
                    }
                    effectiveRangedLevel = (int)Math.floor(effectiveRangedLevel);
                    int attackBonus = attackingPlayer.getCombatBonus(4);
                    attackRoll = (int)Math.floor((double)effectiveRangedLevel * (double)(attackBonus + 64));
                } else {
                    Npc npc = (Npc)entity;
                    effectiveRangedLevel = (int)npc.getCurrentRangedLevel();
                    effectiveRangedLevel += 8;
                    effectiveRangedLevel = (int)Math.floor(effectiveRangedLevel);
                    int attackBonus = npc.getRangedAttackBonus();
                    attackRoll = (int)Math.floor((double)effectiveRangedLevel * (double)(attackBonus + 64));
                }
            } else if (attackStyleDefinition.getCombatType() == CombatType.MAGIC) {
                int effectiveMagicLevel;
                int attackBonus;
                if (entity.isPlayer()) {
                    Player attackingPlayer = (Player)entity;
                    effectiveMagicLevel = (int)Math.floor((double)attackingPlayer.getSkillManager().getCurrentLevels()[6] * CombatManager.getPrayerMultiplier(attackingPlayer, 6));
                    if (attackingPlayer.hasFullVoidMagicSet()) {
                        effectiveMagicLevel = (int)((double)effectiveMagicLevel * 1.45);
                    }
                    effectiveMagicLevel += 9;
                    effectiveMagicLevel = (int)Math.floor(effectiveMagicLevel);
                    attackBonus = attackingPlayer.getCombatBonus(3);
                } else {
                    Npc npc = (Npc)entity;
                    effectiveMagicLevel = (int)npc.getCurrentMagicLevel();
                    effectiveMagicLevel += 9;
                    effectiveMagicLevel = (int)Math.floor(effectiveMagicLevel);
                    attackBonus = npc.getMagicAttackBonus();
                }
                attackRoll = (int)Math.floor((double)effectiveMagicLevel * (double)(attackBonus + 64));
            }
            return (int)(attackRoll * accuracyMultiplier);
        }
        double attackBonus = entity.getCombatBonus(attackStyleDefinition.getAttackBonusType().getIndex());
        double attackLevel = entity.getAttackLevelFor(attackStyleDefinition.getCombatType());
        if (attackBonus < 0.0) {
            attackLevel /= 2.0;
            attackBonus *= 3.0;
        }
        if (attackStyleDefinition.getCombatType() == CombatType.MELEE && entity.isPlayer()) {
            player = (Player)entity;
            if (player.getActivePrayers()[2]) {
                attackLevel *= 1.05;
            } else if (player.getActivePrayers()[5]) {
                attackLevel *= 1.1;
            } else if (player.getActivePrayers()[11]) {
                attackLevel *= 1.15;
            }
            if (player.hasFullVoidMeleeSet()) {
                attackLevel *= 1.1;
            }
        }
        if (attackStyleDefinition.getCombatType() == CombatType.RANGED && entity.isPlayer() && (player = (Player)entity).hasFullVoidRangedSet()) {
            attackLevel *= 1.1;
        }
        if (attackStyleDefinition.getCombatType() == CombatType.MAGIC && entity.isPlayer() && (player = (Player)entity).hasFullVoidMagicSet()) {
            attackLevel *= 1.45;
        }
        double attackRoll = Math.floor(attackLevel + attackBonus) + 8.0;
        return (int)(attackRoll * accuracyMultiplier);
    }
""",
    )
    repairs += count

    path.write_text(text, encoding="utf-8")
    return repairs


def repair_gameplay_helper_remaining_locals(source_root: Path) -> int:
    path = source_root / "com/rs2/model/GameplayHelper.java"
    text = path.read_text(encoding="utf-8")
    repairs = 0

    def replace_section(text: str, start_marker: str, end_marker: str, replacement: str) -> tuple[str, int]:
        try:
            start = text.index(start_marker)
            end = text.index(end_marker, start)
        except ValueError as exc:
            raise SystemExit(f"{path}: expected section not found: {start_marker.strip()[:80]!r}") from exc
        return text[:start] + replacement + text[end:], 1

    replacements = [
        ("                            player.deferredBotTask = object;\n", "                            player.deferredBotTask = (BotTaskDefinition)object;\n"),
        ("                    player.deferredBotTask = object;\n", "                    player.deferredBotTask = (BotTaskDefinition)object;\n"),
        ("                    player.currentBotTask = var4_10;\n", "                    player.currentBotTask = null;\n"),
        ("                    player.currentBotTask = var4_11;\n", "                    player.currentBotTask = null;\n"),
        ("                    player.currentBotTask = var4_12;\n", "                    player.currentBotTask = null;\n"),
        ("                    player.currentBotTask = var4_13;\n", "                    player.currentBotTask = null;\n"),
        ("                    player.currentBotTask = var4_14;\n", "                    player.currentBotTask = null;\n"),
        ("                player.currentBotTask = var4_15;\n", "                player.currentBotTask = null;\n"),
        ("        BotTaskDefinition botTaskDefinition = object;\n", "        BotTaskDefinition botTaskDefinition = (BotTaskDefinition)object;\n"),
        (
            "        Player player = object;\n"
            "        object = string;\n"
            "        object2 = player;\n"
            "        player.interfaceAction = object;\n",
            "        Player player = object;\n"
            "        player.interfaceAction = string;\n",
        ),
        ("        Object object = new String[]{\"a\", \"e\", \"i\", \"o\", \"u\", \"y\"};\n        String[] stringArray = object;\n", "        String[] stringArray = new String[]{\"a\", \"e\", \"i\", \"o\", \"u\", \"y\"};\n"),
        (
            "            object = stringArray[n];\n"
            "            if (string.toLowerCase().startsWith((String)object)) {\n",
            "            String vowel = stringArray[n];\n"
            "            if (string.toLowerCase().startsWith(vowel)) {\n",
        ),
        ("                    String string = object;\n", "                    String string = (String)object;\n"),
        ("        player.interfaceAction = object;\n", "        player.interfaceAction = (String)object;\n"),
        ("                player4.packetSender.sendInterfaceText(stringArray[n2 / 2], (int)object[n2]);\n", "                player4.packetSender.sendInterfaceText(stringArray[n2 / 2], ((int[])object)[n2]);\n"),
        ("                    player5.packetSender.sendInterfaceText(stringArray2[n2 / 2], (int)object[n2]);\n", "                    player5.packetSender.sendInterfaceText(stringArray2[n2 / 2], ((int[])object)[n2]);\n"),
        ("        object = \"\";\n        player = player2;\n", "        player = player2;\n"),
        ("        player2.interfaceAction = object;\n", "        player2.interfaceAction = \"\";\n"),
        ("        int[][] nArrayArray = object;\n", "        int[][] nArrayArray = (int[][])object;\n"),
        ("                player.packetSender.sendConfig(491, (int)object[1]);\n", "                player.packetSender.sendConfig(491, nArray[1]);\n"),
        ("                DialogueManager.setTreasureTrailDialogueStep(player, 10009, 3);\n", "                DialogueManager.a(player, 10009, 3);\n"),
        ("                DialogueManager.setTreasureTrailDialogueStep(player, 10009, 2);\n", "                DialogueManager.a(player, 10009, 2);\n"),
        ("            DialogueManager.setTreasureTrailDialogueStep(player, 10009, 2);\n", "            DialogueManager.a(player, 10009, 2);\n"),
        (
            "        catch (IOException exception) {\n"
            "            exception.printStackTrace();\n"
            "        }\n",
            "        catch (Exception exception) {\n"
            "            exception.printStackTrace();\n"
            "        }\n",
        ),
        (
            "                    player2 = object;\n"
            "                    object = CoordinateClueHandler.formatPositionAsCoordinate(player2.getPosition().getX(), player2.getPosition().getY())[0];\n"
            "                    String string = CoordinateClueHandler.formatPositionAsCoordinate(player2.getPosition().getX(), player2.getPosition().getY())[1];\n"
            "                    player2.getDialogueManager().showTwoLineStatement((String)object, string);\n"
            "                    player = player2;\n"
            "                    player.packetSender.sendGameMessage(\"the sextant displays:\");\n"
            "                    player = player2;\n"
            "                    player.packetSender.sendGameMessage((String)object);\n"
            "                    player = player2;\n"
            "                    player.packetSender.sendGameMessage(string);\n",
            "                    player2 = object;\n"
            "                    String[] coordinateLines = CoordinateClueHandler.formatPositionAsCoordinate(player2.getPosition().getX(), player2.getPosition().getY());\n"
            "                    String coordinateLine = coordinateLines[0];\n"
            "                    String string = coordinateLines[1];\n"
            "                    player2.getDialogueManager().showTwoLineStatement(coordinateLine, string);\n"
            "                    player = player2;\n"
            "                    player.packetSender.sendGameMessage(\"the sextant displays:\");\n"
            "                    player = player2;\n"
            "                    player.packetSender.sendGameMessage(coordinateLine);\n"
            "                    player = player2;\n"
            "                    player.packetSender.sendGameMessage(string);\n",
        ),
        ("        npc.a(", "        npc.moveTo("),
        (
            "        Npc npc2 = npc;\n"
            "        object = player;\n"
            "        player.H = npc2;\n",
            "        player.H = npc;\n",
        ),
        (
            "            Object var2_1 = null;\n"
            "            Player player = npc.getOwnerPlayer();\n"
            "            npc.getOwnerPlayer().H = var2_1;\n",
            "            npc.getOwnerPlayer().H = null;\n",
        ),
    ]
    for old, new in replacements:
        text, count = replace_exact(text, old, new, path)
        repairs += count

    text, count = replace_section(
        text,
        "    public static boolean handleTiaraCrafting(Player player, int n, int n2) {\n",
        "\n    public static void refreshRunecraftingTiaraConfig(Player player, int n) {\n",
        """    public static boolean handleTiaraCrafting(Player player, int n, int n2) {
        RunecraftingAltarDefinition altarDefinition = null;
        for (RunecraftingAltarDefinition candidate : RunecraftingAltarDefinition.values()) {
            if (n == candidate.getTalismanItemId() && n2 == candidate.getAltarObjectId()) {
                altarDefinition = candidate;
                break;
            }
        }
        if (altarDefinition == null) {
            return false;
        }
        if (!ServerSettings.runecraftingEnabled) {
            player.packetSender.sendGameMessage("This skill is currently disabled.");
            return true;
        }
        if (player.getQuestState(14) != 1) {
            QuestDefinition questDefinition = QuestDefinition.forId(14);
            String string = questDefinition.getName();
            player.packetSender.sendGameMessage("You need to complete " + string + " to do this.");
            return true;
        }
        if (player.getInventoryManager().containsItem(5525)) {
            player.getInventoryManager().removeItem(new ItemStack(5525, 1));
            player.getInventoryManager().removeItem(new ItemStack(altarDefinition.getTalismanItemId(), 1));
            player.getInventoryManager().addItem(new ItemStack(altarDefinition.getTiaraItemId(), 1));
            player.getSkillManager().addExperience(20, altarDefinition.getTiaraExperience());
            player.packetSender.sendGameMessage("You bind the power of the talisman into the tiara.");
        }
        return true;
    }
""",
    )
    repairs += count

    text, count = replace_section(
        text,
        "    public static byte[] inflateGzipCacheFile(CacheFile object) {\n",
        "\n    public static int getNpcShopId(int n) {\n",
        """    public static byte[] inflateGzipCacheFile(CacheFile cacheFile) {
        byte[] compressedBytes = new byte[cacheFile.getBuffer().remaining()];
        cacheFile.getBuffer().get(compressedBytes);
        try {
            byte[] inflatedBuffer = new byte[999999];
            int length = 0;
            GZIPInputStream gzipInputStream = new GZIPInputStream(new ByteArrayInputStream(compressedBytes));
            while (true) {
                if (length == 999999) {
                    System.out.println("Error inflating data.\\nGZIP buffer overflow.");
                    break;
                }
                int readCount = gzipInputStream.read(inflatedBuffer, length, 999999 - length);
                if (readCount == -1) break;
                length += readCount;
            }
            byte[] inflatedBytes = new byte[length];
            System.arraycopy(inflatedBuffer, 0, inflatedBytes, 0, length);
            if (inflatedBytes.length < 10) {
                return null;
            }
            return inflatedBytes;
        }
        catch (IOException exception) {
            exception.printStackTrace();
            return null;
        }
    }
""",
    )
    repairs += count

    text, count = replace_section(
        text,
        "    public static void acceptTradeSecondScreen(Player player) {\n",
        "\n    private static void refreshTradeConfirmationSummary(Player player) {\n",
        """    public static void acceptTradeSecondScreen(Player player) {
        if (!player.getTradeState().equals((Object)TradeState.CONFIRM_SCREEN)) {
            return;
        }
        Player player2 = (Player)player.getTradePartner();
        player.setTradeState(TradeState.ACCEPTED);
        if (!player2.getTradeState().equals((Object)TradeState.ACCEPTED)) {
            Player player3 = player;
            player3.packetSender.sendInterfaceText("Waiting for other player...", 3535);
            player3 = player2;
            player3.packetSender.sendInterfaceText("Other player accepted.", 3535);
            return;
        }
        ItemStack[] playerTradeItems = new ItemStack[28];
        int n = 0;
        while (n < 28) {
            ItemStack itemStack = player.getTradeOfferContainer().getItemAt(n);
            if (itemStack != null) {
                playerTradeItems[n] = itemStack;
                player.getTradeOfferContainer().remove(itemStack);
                player2.getInventoryManager().addItem(itemStack);
            }
            ++n;
        }
        ItemStack[] partnerTradeItems = new ItemStack[28];
        n = 0;
        while (n < 28) {
            ItemStack itemStack = player2.getTradeOfferContainer().getItemAt(n);
            if (itemStack != null) {
                partnerTradeItems[n] = itemStack;
                player2.getTradeOfferContainer().remove(itemStack);
                player.getInventoryManager().addItem(itemStack);
            }
            ++n;
        }
        player.setTradeState(TradeState.NONE);
        player2.setTradeState(TradeState.NONE);
        player.packetSender.sendGameMessage("You accept the trade.");
        player2.packetSender.sendGameMessage("You accept the trade.");
        player.packetSender.closeInterfaces();
        player2.packetSender.closeInterfaces();
        player.setTradePartner(null);
        player2.setTradePartner(null);
        player2.pendingTradeTarget = null;
        player.pendingTradeTarget = null;
        CharacterFileManager.savePlayer(player);
        CharacterFileManager.savePlayer(player2);
        boolean bothPlayersAreBots = false;
        if (player.botEnabled && player2.botEnabled) {
            bothPlayersAreBots = true;
        }
        if (player.tradeAdvertMode != -1) {
            BotTaskDefinition cfr_ignored_0 = player.currentBotTask;
            BotTaskDefinition.completeTradeAdvertOffer(player, bothPlayersAreBots);
        }
        if (player2.tradeAdvertMode != -1) {
            BotTaskDefinition cfr_ignored_1 = player2.currentBotTask;
            BotTaskDefinition.completeTradeAdvertOffer(player2, bothPlayersAreBots);
        }
    }
""",
    )
    repairs += count

    text, count = replace_section(
        text,
        "    public static boolean castSelectedItemTeleport(Player player, Position position) {\n",
        "\n    public static int getNextDegradedJewelryItemId(int n) {\n",
        """    public static boolean castSelectedItemTeleport(Player player, Position position) {
        Player player2 = player;
        player2.packetSender.closeInterfaces();
        player2 = player;
        if (player2.interfaceAction.equals("operate") ? player.getEquipmentManager().getItemIdAtSlot(player.getSelectedItemSlot()) != player.getSelectedItemId() : !player.getInventoryManager().containsItem(player.getSelectedItemId())) {
            return false;
        }
        boolean teleportStarted = player.getTeleportManager().castItemTeleport(position);
        if (player.botEnabled && !teleportStarted && player.botCombatState.startsWith("escape")) {
            player.botCombatState = "tele";
            BotCombatEscapeHandler.startBotCombatWalkingEscape(player);
        } else if (player.botEnabled && !teleportStarted && player.botCombatState.equals("tele")) {
            player.botCombatState = "run";
        }
        if (!teleportStarted) {
            return false;
        }
        player2 = player;
        if (player.interfaceAction.equals("operate")) {
            int nextItemId;
            if (player.getEquipmentManager().removeItem(new ItemStack(player.getSelectedItemId())) && (nextItemId = GameplayHelper.getNextDegradedJewelryItemId(player.getSelectedItemId())) > 0) {
                player.getEquipmentManager().setSlotItem(nextItemId, player.getSelectedItemSlot());
            }
        } else {
            int nextItemId;
            if (player.getInventoryManager().removeItem(new ItemStack(player.getSelectedItemId())) && (nextItemId = GameplayHelper.getNextDegradedJewelryItemId(player.getSelectedItemId())) > 0) {
                player.getInventoryManager().addItem(new ItemStack(nextItemId));
            }
        }
        return true;
    }
""",
    )
    repairs += count

    text, count = replace_section(
        text,
        "    public static void appendLogLine(String string, String object) {\n",
        "\n    public static int getDaysBetweenMidnights(long l, long l2) {\n",
        """    public static void appendLogLine(String string, String logName) {
        String path = "./data/logs/" + logName + ".txt";
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(path, true));
            try {
                writer.write(string);
                writer.newLine();
            }
            finally {
                writer.close();
            }
            return;
        }
        catch (IOException exception) {
            exception.printStackTrace();
            return;
        }
    }
""",
    )
    repairs += count

    text, count = replace_section(
        text,
        "    public static String formatDateDayMonthYear(long l) {\n",
        "\n    public static String formatDurationHoursMinutes(long l) {\n",
        """    public static String formatDateDayMonthYear(long l) {
        DateTime dateTime = new DateTime(l);
        return String.valueOf(dateTime.getDayOfMonth()) + "-" + dateTime.getMonthOfYear() + "-" + dateTime.getYear();
    }
""",
    )
    repairs += count

    path.write_text(text, encoding="utf-8")
    return repairs


def repair_player_remaining_locals(source_root: Path) -> int:
    path = source_root / "com/rs2/model/player/Player.java"
    text = path.read_text(encoding="utf-8")
    repairs = 0

    def replace_section(text: str, start_marker: str, end_marker: str, replacement: str) -> tuple[str, int]:
        try:
            start = text.index(start_marker)
            end = text.index(end_marker, start)
        except ValueError as exc:
            raise SystemExit(f"{path}: expected Player section not found: {start_marker!r}") from exc
        return text[:start] + replacement + text[end:], 1

    replacements = [
        (
            "    public final void clearTemporaryCutsceneNpcs() {\n"
            "        if (this.temporaryCutsceneNpcs.size() > 0) {\n"
            "            for (Npc npc : this.temporaryCutsceneNpcs) {\n"
            "                if (npc == null) continue;\n"
            "                GameplayHelper.unregisterTemporaryNpc(npc);\n"
            "            }\n"
            "        }\n"
            "        this.temporaryCutsceneNpcs = new ArrayList();\n"
            "    }\n"
            "\n"
            "    public final Npc findTemporaryCutsceneNpc(int n) {\n"
            "        if (this.temporaryCutsceneNpcs.size() > 0) {\n"
            "            for (Npc npc : this.temporaryCutsceneNpcs) {\n"
            "                if (npc == null || npc.getNpcId() != n) continue;\n"
            "                return npc;\n"
            "            }\n"
            "        }\n"
            "        return null;\n"
            "    }\n",
            "    public final void clearTemporaryCutsceneNpcs() {\n"
            "        if (this.temporaryCutsceneNpcs.size() > 0) {\n"
            "            for (Object npcObject : this.temporaryCutsceneNpcs) {\n"
            "                Npc npc = (Npc)npcObject;\n"
            "                if (npc == null) continue;\n"
            "                GameplayHelper.unregisterTemporaryNpc(npc);\n"
            "            }\n"
            "        }\n"
            "        this.temporaryCutsceneNpcs = new ArrayList();\n"
            "    }\n"
            "\n"
            "    public final Npc findTemporaryCutsceneNpc(int n) {\n"
            "        if (this.temporaryCutsceneNpcs.size() > 0) {\n"
            "            for (Object npcObject : this.temporaryCutsceneNpcs) {\n"
            "                Npc npc = (Npc)npcObject;\n"
            "                if (npc == null || npc.getNpcId() != n) continue;\n"
            "                return npc;\n"
            "            }\n"
            "        }\n"
            "        return null;\n"
            "    }\n",
        ),
        (
            "    public final void d(int n, int n2) {\n"
            "        ItemStack[] itemStackArray;\n"
            "        Object object = new ItemStack(n, n2);\n"
            "        ItemStack itemStack = object;\n"
            "        object = this;\n"
            "        if (((Player)object).botTaskRequiredItems != null) {\n"
            "            itemStackArray = new ItemStack[((Player)object).botTaskRequiredItems.length + 1];\n"
            "            int n3 = 0;\n"
            "            while (n3 < ((Player)object).botTaskRequiredItems.length) {\n"
            "                itemStackArray[n3] = ((Player)object).botTaskRequiredItems[n3];\n"
            "                ++n3;\n"
            "            }\n"
            "            itemStackArray[((Player)object).botTaskRequiredItems.length] = itemStack;\n"
            "        } else {\n"
            "            ItemStack[] itemStackArray2 = new ItemStack[1];\n"
            "            itemStackArray = itemStackArray2;\n"
            "            itemStackArray2[0] = itemStack;\n"
            "        }\n"
            "        ((Player)object).botTaskRequiredItems = itemStackArray;\n"
            "    }\n",
            "    public final void d(int n, int n2) {\n"
            "        ItemStack itemStack = new ItemStack(n, n2);\n"
            "        ItemStack[] itemStackArray;\n"
            "        if (this.botTaskRequiredItems != null) {\n"
            "            itemStackArray = new ItemStack[this.botTaskRequiredItems.length + 1];\n"
            "            int n3 = 0;\n"
            "            while (n3 < this.botTaskRequiredItems.length) {\n"
            "                itemStackArray[n3] = this.botTaskRequiredItems[n3];\n"
            "                ++n3;\n"
            "            }\n"
            "            itemStackArray[this.botTaskRequiredItems.length] = itemStack;\n"
            "        } else {\n"
            "            itemStackArray = new ItemStack[1];\n"
            "            itemStackArray[0] = itemStack;\n"
            "        }\n"
            "        this.botTaskRequiredItems = itemStackArray;\n"
            "    }\n",
        ),
        ("            this.currentBotTask = object;\n", "            this.currentBotTask = (BotTaskDefinition)object;\n"),
        (
            "            BotWorldRouteWalker.continueWorldRoute(this, object2);\n",
            "            BotWorldRouteWalker.continueWorldRoute(this, (BotWorldRouteChoice)object2);\n",
        ),
        ("        this.a(new Position(ServerSettings.startX, ServerSettings.startY, ServerSettings.startPlane));\n", "        this.setPosition(new Position(ServerSettings.startX, ServerSettings.startY, ServerSettings.startPlane));\n"),
        (
            "        object = this;\n"
            "        ((Entity)object).getAttributes().put(\"smithing\", Boolean.FALSE);\n"
            "        ((Entity)object).getAttributes().put(\"smelting\", Boolean.FALSE);\n"
            "        ((Entity)object).getAttributes().put(\"isBanking\", Boolean.FALSE);\n"
            "        ((Entity)object).getAttributes().put(\"isShopping\", Boolean.FALSE);\n"
            "        ((Entity)object).getAttributes().put(\"canPickup\", Boolean.FALSE);\n"
            "        ((Entity)object).getAttributes().put(\"canTakeDamage\", Boolean.TRUE);\n"
            "        this.resetAppearance();\n"
            "        object = this;\n"
            "        ((Player)object).appearanceColors[0] = 7;\n"
            "        object = this;\n"
            "        ((Player)object).appearanceColors[1] = 0;\n"
            "        object = this;\n"
            "        ((Player)object).appearanceColors[2] = 9;\n"
            "        object = this;\n"
            "        ((Player)object).appearanceColors[3] = 5;\n"
            "        object = this;\n"
            "        ((Player)object).appearanceColors[4] = 0;\n",
            "        this.getAttributes().put(\"smithing\", Boolean.FALSE);\n"
            "        this.getAttributes().put(\"smelting\", Boolean.FALSE);\n"
            "        this.getAttributes().put(\"isBanking\", Boolean.FALSE);\n"
            "        this.getAttributes().put(\"isShopping\", Boolean.FALSE);\n"
            "        this.getAttributes().put(\"canPickup\", Boolean.FALSE);\n"
            "        this.getAttributes().put(\"canTakeDamage\", Boolean.TRUE);\n"
            "        this.resetAppearance();\n"
            "        this.appearanceColors[0] = 7;\n"
            "        this.appearanceColors[1] = 0;\n"
            "        this.appearanceColors[2] = 9;\n"
            "        this.appearanceColors[3] = 5;\n"
            "        this.appearanceColors[4] = 0;\n",
        ),
        ("                this.connectionState = object;\n", "                this.connectionState = (PlayerConnectionState)object;\n"),
        (
            "                object = new ItemStack(n, n3);\n"
            "                this.inventoryManager.addItem((ItemStack)object);\n",
            "                ItemStack itemStack = new ItemStack(n, n3);\n"
            "                this.inventoryManager.addItem(itemStack);\n",
        ),
        (
            "        Object object;\n"
            "        Object object2;\n"
            "        block42: {\n",
            "        Object object;\n"
            "        Object object2;\n"
            "        Position var8_30 = null;\n"
            "        block42: {\n",
        ),
        ("                        object3 = object4[npc];\n", "                        object3 = ((Npc[])object4)[npc];\n"),
        (
            "                    for (Npc position : arrayList2) {\n"
            "                        int n14 = GameUtil.getDistance(position2, position.getPosition());\n",
            "                    for (Object positionObject : arrayList2) {\n"
            "                        Npc position = (Npc)positionObject;\n"
            "                        int n14 = GameUtil.getDistance(position2, position.getPosition());\n",
        ),
        ("                    Position var8_30 = null;\n", ""),
        (
            "                    }\n"
            "                    Position var8_30 = null;\n"
            "                    if (((ArrayList)object3).size() <= 1) break block41;\n",
            "                    }\n"
            "                    var8_30 = null;\n"
            "                    if (((ArrayList)object3).size() <= 1) break block41;\n",
        ),
        ("                    Collections.shuffle(object3);\n", "                    Collections.shuffle((ArrayList)object3);\n"),
        ("                    while (object4.hasNext()) {\n", "                    while (((Iterator)object4).hasNext()) {\n"),
        ("                        object = (Npc)object4.next();\n", "                        object = (Npc)((Iterator)object4).next();\n"),
        (
            "            arrayList = null;\n"
            "            player = this;\n"
            "            this.currentBotTask = arrayList;\n"
            "            arrayList = null;\n"
            "            player = this;\n"
            "            this.deferredBotTask = arrayList;\n",
            "            this.currentBotTask = null;\n"
            "            this.deferredBotTask = null;\n",
        ),
        (
            "            for (WorldObject worldObject7 : arrayList2) {\n"
            "                ObjectDefinition objectDefinition = ObjectDefinition.forId(worldObject7.getObjectId());\n"
            "                n3 = GameUtil.getDistance(this.getPosition(), worldObject7.getPosition());\n"
            "                object = this.a(worldObject7, objectDefinition);\n"
            "                if (object == null && n3 > 2) continue;\n"
            "                worldObject2 = worldObject7;\n",
            "            for (WorldObject nearbyObject : arrayList2) {\n"
            "                ObjectDefinition objectDefinition = ObjectDefinition.forId(nearbyObject.getObjectId());\n"
            "                n3 = GameUtil.getDistance(this.getPosition(), nearbyObject.getPosition());\n"
            "                object = this.a(nearbyObject, objectDefinition);\n"
            "                if (object == null && n3 > 2) continue;\n"
            "                worldObject2 = nearbyObject;\n",
        ),
        (
            "        if (this.barrowsChestOpened && !this.isInBarrows()) {\n"
            "            object = this;\n"
            "            player = object;\n"
            "            ((Player)object).packetSender.resetCamera();\n"
            "            ((Player)object).barrowsChestOpened = false;\n"
            "            BarrowsManager.resetBarrowsState(this);\n"
            "        }\n",
            "        if (this.barrowsChestOpened && !this.isInBarrows()) {\n"
            "            player = this;\n"
            "            player.packetSender.resetCamera();\n"
            "            this.barrowsChestOpened = false;\n"
            "            BarrowsManager.resetBarrowsState(this);\n"
            "        }\n",
        ),
        (
            "        boolean bl = true;\n"
            "        object = this;\n"
            "        this.teleportPlacementUpdateRequired = bl;\n"
            "        bl = true;\n"
            "        object = this;\n"
            "        this.teleporting = bl;\n"
            "        object = this;\n"
            "        ((Player)object).packetSender.sendPlayerIndex();\n",
            "        this.teleportPlacementUpdateRequired = true;\n"
            "        this.teleporting = true;\n"
            "        this.packetSender.sendPlayerIndex();\n",
        ),
        (
            "        Player player4 = this;\n"
            "        Object object2 = player4.inventoryManager;\n"
            "        ((InventoryManager)object2).refresh();\n"
            "        object2 = player4.equipmentManager;\n"
            "        ((EquipmentManager)object2).refresh();\n"
            "        player4.bankPinManager.processPendingPinChanges();\n"
            "        object2 = player4;\n"
            "        player = object2;\n"
            "        World.scheduleTickTask(new PostLoginSyncTask((Player)object2, 3, player));\n",
            "        Player player4 = this;\n"
            "        player4.inventoryManager.refresh();\n"
            "        player4.equipmentManager.refresh();\n"
            "        player4.bankPinManager.processPendingPinChanges();\n"
            "        player = player4;\n"
            "        World.scheduleTickTask(new PostLoginSyncTask(player4, 3, player));\n",
        ),
        (
            "            object2 = new PoisonEffect(this.getPoisonDamage(), false);\n"
            "            object2.a((CombatAction)object3);\n",
            "            PoisonEffect poisonEffect = new PoisonEffect(this.getPoisonDamage(), false);\n"
            "            poisonEffect.apply((CombatAction)object3);\n",
        ),
        ("        this.password = object2;\n", "        this.password = (String)object2;\n"),
        (
            "                    Object object4;\n"
            "                    object = object2;\n"
            "                    object = object2;\n"
            "                    object = this;\n"
            "                    if (((Player)object4).hostAddress.equals(((Player)object).hostAddress)) {\n",
            "                    object = this;\n"
            "                    if (((Player)object2).hostAddress.equals(((Player)object).hostAddress)) {\n",
        ),
        ("            object2 = TextUtil.capitalizeFirst(object2.name().toLowerCase().replaceAll(\"_\", \" \"));\n", "            object2 = TextUtil.capitalizeFirst(((SpellDefinition)object2).name().toLowerCase().replaceAll(\"_\", \" \"));\n"),
        ("        while (n < ((Object)object).length) {\n", "        while (n < ((int[])object).length) {\n"),
        ("                return object;\n", "                return (ItemStack)object;\n"),
    ]

    for old, new in replacements:
        count = text.count(old)
        text = text.replace(old, new)
        repairs += count

    lines = text.splitlines(keepends=True)
    seen_var8_decl = False
    for index, line in enumerate(lines):
        if line.strip() == "Position var8_30 = null;":
            if seen_var8_decl:
                lines[index] = line.replace("Position var8_30", "var8_30")
                repairs += 1
            else:
                seen_var8_decl = True
    text = "".join(lines)

    text, count = replace_section(
        text,
        "    public final void queuePublicChatMessage(String object, int n, int n2) {\n",
        "\n    public final void queuePublicChatMessage(String string) {\n",
        """    public final void queuePublicChatMessage(String message, int n, int n2) {
        if (message == null) {
            return;
        }
        if (message.equals("")) {
            return;
        }
        byte[] byArray = new byte[100];
        int n3 = ChatTextCodec.encode(message, byArray);
        byte[] byArray2 = new byte[n3];
        ChatTextCodec.encode(message, byArray2);
        this.publicChatEffects = n2;
        this.publicChatColor = n;
        this.publicChatPayload = byArray2;
        this.ay = true;
        this.flagAppearanceUpdate(true);
        this.getUpdateState().setUpdateRequired(true);
    }
""",
    )
    repairs += count

    text, count = replace_section(
        text,
        "    public final void showHiscoreInterface(int n) {\n",
        "\n    public final void completeQuestJournal() {\n",
        """    public final void showHiscoreInterface(int n) {
        int n2 = 0;
        while (n2 < 16) {
            this.packetSender.sendInterfaceText("", 18819 + 4 * n2);
            this.packetSender.sendInterfaceText("", 18820 + 4 * n2);
            this.packetSender.sendInterfaceText("", 18821 + 4 * n2);
            this.packetSender.sendInterfaceText("", 18822 + 4 * n2);
            ++n2;
        }
        ArrayList<CharacterFileRecord> arrayList = new ArrayList<CharacterFileRecord>();
        for (Object recordObject : CharacterFileManager.liveHiscoreRecords) {
            CharacterFileRecord characterFileRecord = (CharacterFileRecord)recordObject;
            if (characterFileRecord.playerRights >= 2 || characterFileRecord.gameMode != n) continue;
            arrayList.add(characterFileRecord);
        }
        if (n == 3) {
            for (Object recordObject : CharacterFileManager.deadHardcoreIronmanRecords) {
                arrayList.add((CharacterFileRecord)recordObject);
            }
        }
        int n3 = this.et;
        int n4 = this.es;
        String string = n3 != 22 ? String.valueOf(n3 < 21 ? SkillManager.SKILL_NAMES[n3] : "Overall") + " Hiscores" : "Wealth Hiscores";
        String string2 = "";
        if (n == 1) {
            string2 = "<img=3>";
        } else if (n == 2) {
            string2 = "<img=4>";
        }
        if (n == 3) {
            string2 = String.valueOf(string2) + "<img=5>";
        }
        this.packetSender.sendInterfaceText(String.valueOf(string2) + string + string2, 18814);
        Collections.sort(arrayList, new HiscoreEntryComparator(this, n3));
        int n5 = n4 << 4;
        while (n5 < arrayList.size()) {
            CharacterFileRecord characterFileRecord = (CharacterFileRecord)arrayList.get(n5);
            int n6 = n5 - (n4 << 4);
            if (n5 == ((n4 + 1) << 4)) break;
            if (!(n3 == 22 ? characterFileRecord.getStoredItemValue() < 100000 : n3 < 21 && CharacterFileRecord.getLevelForExperience(characterFileRecord.getSkillExperience(n3)) < 30)) {
                String string3 = "@bla@";
                if (characterFileRecord.username.equals(this.username)) {
                    string3 = "@whi@";
                }
                boolean bl = characterFileRecord.memberFlag;
                int n7 = characterFileRecord.playerRights;
                String string4 = "";
                if (n7 == 1) {
                    string4 = String.valueOf(string4) + "<img=0>";
                }
                if (n7 == 2) {
                    string4 = String.valueOf(string4) + "<img=1>";
                }
                if (bl && n7 < 2) {
                    string4 = String.valueOf(string4) + "<img=2>";
                }
                if (n == 3 && characterFileRecord.gameMode != 3) {
                    string4 = "<img=6>" + string4;
                }
                this.packetSender.sendInterfaceText(String.valueOf(string3) + (n5 + 1), 18819 + 4 * n6);
                this.packetSender.sendInterfaceText(String.valueOf(string3) + string4 + characterFileRecord.username, 18820 + 4 * n6);
                String string5 = "";
                String string6 = "";
                if (n3 < 21) {
                    string5 = GameUtil.formatNumber((long)CharacterFileRecord.getLevelForExperience(characterFileRecord.getSkillExperience(n3)));
                    string6 = GameUtil.formatNumber(characterFileRecord.getSkillExperience(n3));
                }
                if (n3 == 21) {
                    string5 = GameUtil.formatNumber((long)characterFileRecord.getTotalLevel());
                    string6 = GameUtil.formatNumber(characterFileRecord.getSkillExperience(n3));
                }
                if (n3 == 22) {
                    string5 = GameUtil.formatCompactAmountHighThreshold(characterFileRecord.getStoredItemValue());
                }
                this.packetSender.sendInterfaceText(String.valueOf(string3) + string5, 18821 + 4 * n6);
                this.packetSender.sendInterfaceText(String.valueOf(string3) + string6, 18822 + 4 * n6);
            }
            ++n5;
        }
        this.packetSender.showInterface(18788);
    }
""",
    )
    repairs += count

    text, count = replace_section(
        text,
        "    public final void addPvpCombatReference(Player object, int n) {\n",
        "\n    private void setSkulled(boolean bl) {\n",
        """    public final void addPvpCombatReference(Player player, int n) {
        PvpCombatReference pvpCombatReference = new PvpCombatReference((Entity)player, n);
        this.pvpCombatReferences.add(pvpCombatReference);
        this.setSkulled(true);
    }

    public final void recordPvpAttack(Player player) {
        if (!player.isPlayer()) {
            return;
        }
        if (this.isInDuelArena()) {
            return;
        }
        for (Object referenceObject : player.pvpCombatReferences) {
            if (((EntityReference)referenceObject).resolve() != this) continue;
            return;
        }
        for (Object referenceObject : this.pvpCombatReferences) {
            if (((EntityReference)referenceObject).resolve() != player) continue;
            ((PvpCombatReference)referenceObject).resetTimer();
            return;
        }
        this.addPvpCombatReference(player, 2000);
    }
""",
    )
    repairs += count

    text, count = replace_section(
        text,
        "    public final ArrayList getUnprotectedItems(ItemStack[] object) {\n",
        "\n    public final void ea() {\n",
        """    public final ArrayList getUnprotectedItems(ItemStack[] items) {
        ArrayList<ItemStack> arrayList = new ArrayList<ItemStack>();
        PriorityQueue<ItemStack> priorityQueue = new PriorityQueue<ItemStack>(1, new ProtectedItemValueComparator(this));
        int n = 0;
        while (n < items.length) {
            ItemStack itemStack = items[n];
            if (!(itemStack == null || itemStack.getDefinition().isUntradeable() && itemStack.getDefinition().getValue() == 1)) {
                ItemStack protectedItem = new ItemStack(itemStack.getId());
                priorityQueue.add(protectedItem);
                arrayList.add(protectedItem);
            }
            ++n;
        }
        int n2 = this.skulled ? 0 : 3;
        if (this.activePrayers[8]) {
            ++n2;
        }
        if (this.gameMode == 2) {
            n2 = 0;
        }
        ArrayList<ItemStack> protectedItems = new ArrayList<ItemStack>();
        while (protectedItems.size() < n2 && priorityQueue.size() > 0) {
            ItemStack itemStack = (ItemStack)priorityQueue.poll();
            protectedItems.add(itemStack);
            arrayList.remove(itemStack);
        }
        return arrayList;
    }
""",
    )
    repairs += count

    text, count = replace_section(
        text,
        "    @Override\n    public final void dropDeathItems(Entity entity) {\n",
        "\n    public final SpellDefinition getQueuedCombatSpell() {\n",
        """    @Override
    public final void dropDeathItems(Entity entity) {
        if (this.playerRights >= 2 || this.isInDuelArena() || this.creatureGraveyardController.isInsideGraveyard() || this.isInFightCave()) {
            return;
        }
        if (entity == null || !(entity instanceof Player)) {
            entity = this;
        }
        ItemStack[] itemStackArray = new ItemStack[this.equipmentManager.getContainer().g() + this.inventoryManager.getContainer().g()];
        System.arraycopy(this.equipmentManager.getContainer().getItems(), 0, itemStackArray, 0, this.equipmentManager.getContainer().getItems().length);
        System.arraycopy(this.inventoryManager.getContainer().getItems(), 0, itemStackArray, this.equipmentManager.getContainer().getItems().length, this.inventoryManager.getContainer().getItems().length);
        PriorityQueue<ItemStack> priorityQueue = new PriorityQueue<ItemStack>(1, new DeathItemValueComparator(this));
        int n = 0;
        while (n < itemStackArray.length) {
            ItemStack itemStack = itemStackArray[n];
            if (!(itemStack == null || itemStack.getDefinition().isUntradeable() && itemStack.getDefinition().getValue() == 1)) {
                priorityQueue.add(new ItemStack(itemStack.getId(), itemStack.getAmount(), itemStack.getMetadata()));
            }
            ++n;
        }
        ArrayList<ItemStack> protectedItems = new ArrayList<ItemStack>();
        int n2 = this.skulled ? 0 : 3;
        if (this.activePrayers[8]) {
            ++n2;
        }
        if (this.gameMode == 2) {
            n2 = 0;
        }
        n = 0;
        while (n < n2 && priorityQueue.size() > 0) {
            ItemStack itemStack = (ItemStack)priorityQueue.poll();
            int n3 = n2 - n;
            if (itemStack.getAmount() < n3) {
                n3 = itemStack.getAmount();
            }
            n += n3;
            itemStack.setAmount(n3);
            protectedItems.add(itemStack);
        }
        ArrayList<ItemStack> droppedItems = new ArrayList<ItemStack>(Arrays.asList(itemStackArray));
        block0: for (ItemStack protectedItem : protectedItems) {
            if (protectedItem == null) continue;
            Iterator iterator = droppedItems.iterator();
            while (iterator.hasNext()) {
                ItemStack itemStack = (ItemStack)iterator.next();
                if (itemStack == null || itemStack.getId() != protectedItem.getId()) continue;
                itemStack.setAmount(itemStack.getAmount() - protectedItem.getAmount());
                if (itemStack.getAmount() > 0) continue block0;
                iterator.remove();
                continue block0;
            }
        }
        this.equipmentManager.getContainer().clear();
        this.inventoryManager.getContainer().clear();
        Entity lootOwner = entity;
        if (entity.isPlayer() && entity != this) {
            Player killer = (Player)entity;
            if (killer.gameMode != 0) {
                killer.packetSender.sendGameMessage("You are not playing on normal gamemode and cannot receive the loot.");
                lootOwner = this;
            }
        }
        for (ItemStack itemStack : protectedItems) {
            this.inventoryManager.addItem(itemStack);
        }
        for (ItemStack itemStack : droppedItems) {
            if (itemStack == null) continue;
            if (itemStack.getDefinition().getName().toLowerCase().contains("clue scroll")) {
                this.treasureTrailStepCount = 0;
            }
            BarrowsRepairHandler barrowsRepairHandler = BarrowsRepairHandler.forItem(itemStack);
            if (itemStack.getDefinition().isUntradeable() && barrowsRepairHandler == null) continue;
            ItemStack groundStack = new ItemStack(itemStack.getId(), itemStack.getAmount());
            if (barrowsRepairHandler != null) {
                groundStack = new ItemStack(barrowsRepairHandler.getFullyDegradedItemId(), 1);
            }
            GroundItem groundItem = new GroundItem(groundStack, (Entity)this, lootOwner, this.getDeathPosition());
            GroundItemManager.getInstance().spawn(groundItem);
            if (!entity.isPlayer() || entity == this) continue;
            Player killer = (Player)entity;
            if (!killer.botEnabled) continue;
            killer.botLootGroundItems.add(groundItem);
        }
        if (entity.isPlayer() && entity != this) {
            Player killer = (Player)entity;
            if (killer.botEnabled) {
                if (killer.botLootGroundItems.size() > 0) {
                    killer.botCombatState = "loot items";
                    BotCombatHelper.processBotLootQueue(killer);
                } else {
                    killer.botCombatState = null;
                }
            }
        }
        boolean bl = false;
        if (this.gameMode == 3) {
            this.gameMode = 1;
            this.packetSender.sendAccountStatus();
            bl = true;
            this.packetSender.sendGameMessage("You have fallen as a Hardcore Iron Man, your Hardcore status has been revoked.");
        }
        CharacterFileManager.savePlayer((Player)this);
        if (bl) {
            CharacterFileManager.archiveDeadHardcoreIronman((Player)this);
        }
        if (entity != null && this.getDeathPosition() != null) {
            GroundItem groundItem = new GroundItem(new ItemStack(526, 1), (Entity)this, entity, this.getDeathPosition());
            GroundItemManager.getInstance().spawn(groundItem);
        }
        this.equipmentManager.refresh();
        this.inventoryManager.refresh();
    }
""",
    )
    repairs += count

    path.write_text(text, encoding="utf-8")
    return repairs


def main() -> int:
    if len(sys.argv) != 2:
        print("usage: repair_small_local_collisions.py <source_root>", file=sys.stderr)
        return 2
    source_root = Path(sys.argv[1]).resolve()
    repairs = 0
    for relative_path, replacements in REPAIRS.items():
        path = source_root / relative_path
        text = path.read_text(encoding="utf-8")
        for old, new in replacements:
            text, count = replace_exact(text, old, new, path)
            repairs += count
        path.write_text(text, encoding="utf-8")
    repairs += repair_database_query_signatures(source_root)
    repairs += repair_database_callback_signatures(source_root)
    repairs += repair_am_subclass_constructor(source_root)
    repairs += repair_runecrafting_handler_locals(source_root)
    repairs += repair_runecrafting_object_handler_locals(source_root)
    repairs += repair_farming_patch_manager_locals(source_root)
    repairs += repair_cutscene_and_abyss_locals(source_root)
    repairs += repair_item_container_raw_collection_locals(source_root)
    repairs += repair_object_manager_raw_collection_locals(source_root)
    repairs += repair_direct_path_strategy_locals(source_root)
    repairs += repair_path_finder_find_path_locals(source_root)
    repairs += repair_spell_definition_constructor_delegates(source_root)
    repairs += repair_ammunition_definition_locals(source_root)
    repairs += repair_herblore_handler_locals(source_root)
    repairs += repair_slayer_manager_locals(source_root)
    repairs += repair_treasure_trail_manager_locals(source_root)
    repairs += repair_mid_cluster_locals(source_root)
    repairs += repair_quest_cluster_locals(source_root)
    repairs += repair_object_interaction_packet_handler_locals(source_root)
    repairs += repair_small_bot_cluster_locals(source_root)
    repairs += repair_ground_item_manager_locals(source_root)
    repairs += repair_npc_locals(source_root)
    repairs += repair_entity_locals(source_root)
    repairs += repair_skill_guide_manager_locals(source_root)
    repairs += repair_potion_handler_locals(source_root)
    repairs += repair_item_action_packet_handler_locals(source_root)
    repairs += repair_combat_cycle_event_locals(source_root)
    repairs += repair_control_panel_locals(source_root)
    repairs += repair_bot_combat_helper_locals(source_root)
    repairs += repair_combat_manager_locals(source_root)
    repairs += repair_gameplay_helper_remaining_locals(source_root)
    repairs += repair_player_remaining_locals(source_root)
    repairs += repair_grand_exchange_comparator(
        source_root / "com/rs2/model/grandexchange/GrandExchangePriceSampleTimestampComparator.java"
    )
    repairs += repair_bot_combat_escape_handler(
        source_root / "com/rs2/bot/combat/BotCombatEscapeHandler.java"
    )
    repairs += repair_karamja_volcano_route_locals(source_root)
    repairs += repair_world_and_gameplay_helper(source_root)
    repairs += repair_bot_task_definition_locals(source_root)
    repairs += repair_gameplay_helper_bot_task_locals(source_root)
    repairs += repair_player_update_task_locals(source_root)
    repairs += repair_grand_exchange_manager_locals(source_root)
    repairs += repair_telekinetic_grab_spell_action_locals(source_root)
    repairs += repair_login_protocol_locals(source_root)
    repairs += repair_plugin_manager_locals(source_root)
    repairs += repair_item_combination_handler_locals(source_root)
    repairs += repair_grand_exchange_offer_locals(source_root)
    repairs += repair_game_util_locals(source_root)
    repairs += repair_music_manager_locals(source_root)
    repairs += repair_cache_store_locals(source_root)
    repairs += repair_cache_definition_index_signature(source_root)
    repairs += repair_shop_manager_locals(source_root)
    repairs += repair_collision_map_area_locals(source_root)
    repairs += repair_small_recipe_constructors(source_root)
    repairs += repair_silver_crafting_task_locals(source_root)
    repairs += repair_social_packet_handler_locals(source_root)
    repairs += repair_grand_exchange_price_sample_locals(source_root)
    repairs += repair_rune_thrownaxe_special_attack_locals(source_root)
    repairs += repair_more_small_cluster_locals(source_root)
    repairs += repair_next_small_cluster_locals(source_root)
    repairs += repair_additional_small_cluster_locals(source_root)
    repairs += repair_bot_task_route_and_required_item_locals(source_root)
    repairs += repair_misc_contained_type_flow_locals(source_root)
    repairs += repair_more_misc_small_clusters(source_root)
    repairs += repair_special_attack_definition_locals(source_root)
    print(f"repaired small local collisions: {repairs}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
