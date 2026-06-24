/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.mining;

import com.rs2.ServerSettings;
import com.rs2.model.GameplayHelper;
import com.rs2.model.Position;
import com.rs2.model.combat.hit.HitType;
import com.rs2.model.dialogue.DialogueManager;
import com.rs2.model.item.ItemService;
import com.rs2.model.item.ItemStack;
import com.rs2.model.objects.DynamicObject;
import com.rs2.model.objects.ObjectManager;
import com.rs2.model.player.Player;
import com.rs2.model.randomevent.SkillRandomEventNpc;
import com.rs2.model.skill.GatheringToolDefinition;
import com.rs2.model.skill.ItemCombinationHandler;
import com.rs2.model.skill.SkillActionHelper;
import com.rs2.model.skill.mining.MiningManager;
import com.rs2.model.task.CycleEvent;
import com.rs2.model.task.CycleEventContainer;
import com.rs2.net.packet.PacketSender;
import com.rs2.util.GameUtil;
import com.rs2.util.GameplayTrace;
import com.rs2.util.TextUtil;

public final class MiningTask
extends CycleEvent {
    private /* synthetic */ MiningManager manager;
    private final /* synthetic */ int actionSequence;
    private final /* synthetic */ int rockObjectId;
    private final /* synthetic */ int x;
    private final /* synthetic */ int y;
    private final /* synthetic */ GatheringToolDefinition gatheringTool;
    private final /* synthetic */ int mineChanceLow;
    private final /* synthetic */ int mineChanceHigh;
    private final /* synthetic */ int oreItemId;
    private final /* synthetic */ int baseExperience;
    private final /* synthetic */ double depletionChance;
    private final /* synthetic */ int respawnTicks;

    public MiningTask(MiningManager miningManager, int n, int n2, int n3, int n4, GatheringToolDefinition gatheringToolDefinition, int n5, int n6, int n7, int n8, double d, int n9) {
        this.manager = miningManager;
        this.actionSequence = n;
        this.rockObjectId = n2;
        this.x = n3;
        this.y = n4;
        this.gatheringTool = gatheringToolDefinition;
        this.mineChanceLow = n5;
        this.mineChanceHigh = n6;
        this.oreItemId = n7;
        this.baseExperience = n8;
        this.depletionChance = d;
        this.respawnTicks = n9;
    }

    @Override
    public final void execute(CycleEventContainer cycleEventContainer) {
        int n;
        boolean bl;
        block48: {
            block47: {
                if (!MiningManager.getPlayer(this.manager).isCurrentActionSequence(this.actionSequence)) {
                    if (GameplayTrace.enabled()) {
                        GameplayTrace.log("mining stop invalid-sequence player=" + GameplayTrace.describe(MiningManager.getPlayer(this.manager)) + " seq=" + this.actionSequence + " rockObjectId=" + this.rockObjectId + " x=" + this.x + " y=" + this.y);
                    }
                    if (MiningManager.getPlayer((MiningManager)this.manager).botEnabled) {
                        MiningManager.getPlayer((MiningManager)this.manager).currentBotTask.startWalkToBank(MiningManager.getPlayer(this.manager));
                    }
                    cycleEventContainer.stop();
                    return;
                }
                bl = false;
                n = 0;
                if (MiningManager.getRestoredRockObjectId(this.rockObjectId) != -1) break block47;
                int n2 = MiningManager.getRandomEventRockObjectId(this.rockObjectId, new Position(this.x, this.y, MiningManager.getPlayer(this.manager).getPosition().getPlane()));
                ObjectManager.getInstance();
                DynamicObject dynamicObject = ObjectManager.findDynamicObjectAt(this.x, this.y, MiningManager.getPlayer(this.manager).getPosition().getPlane());
                if (dynamicObject == null || n2 == -1 || dynamicObject.getWorldObject().getObjectId() != n2) break block48;
            }
            n = 1;
        }
        if (n != 0) {
            if (MiningManager.getPlayer((MiningManager)this.manager).gatheringHazardCounter >= 2) {
                ItemCombinationHandler.breakGatheringTool(MiningManager.getPlayer(this.manager), 14);
                Player player = MiningManager.getPlayer(this.manager);
                player.packetSender.sendStillGraphicToNearbyPlayers(157, this.x, this.y, 0, 1);
                player = MiningManager.getPlayer(this.manager);
                player.packetSender.sendGameMessage("Your pickaxe has been broken by the rock!");
                MiningManager.getPlayer(this.manager).applyDirectHit(GameUtil.randomInclusive(MiningManager.getPlayer(this.manager).getMaxHitpoints() / 20) + MiningManager.getPlayer(this.manager).getMaxHitpoints() / 20 + 1, HitType.NORMAL);
                MiningManager.getPlayer(this.manager).getUpdateState().setAnimation(-1);
                player = MiningManager.getPlayer(this.manager);
                player.packetSender.sendSoundEffect(42, 1, 0);
                if (MiningManager.getPlayer((MiningManager)this.manager).botEnabled) {
                    MiningManager.getPlayer((MiningManager)this.manager).currentBotTask.startWalkToBank(MiningManager.getPlayer(this.manager));
                }
                cycleEventContainer.stop();
                return;
            }
            MiningManager.getPlayer(this.manager).getUpdateState().setAnimation(this.gatheringTool.getGatherAnimationId());
            ++MiningManager.getPlayer((MiningManager)this.manager).gatheringHazardCounter;
            return;
        }
        ObjectManager.getInstance();
        DynamicObject dynamicObject2 = ObjectManager.findDynamicObjectAt(this.x, this.y, MiningManager.getPlayer(this.manager).getPosition().getPlane());
        if (dynamicObject2 != null && dynamicObject2.getWorldObject().getObjectId() != this.rockObjectId) {
            if (GameplayTrace.enabled()) {
                GameplayTrace.log("mining stop depleted-before-roll player=" + GameplayTrace.describe(MiningManager.getPlayer(this.manager)) + " seq=" + this.actionSequence + " rockObjectId=" + this.rockObjectId + " dynamicId=" + dynamicObject2.getWorldObject().getObjectId() + " x=" + this.x + " y=" + this.y);
            }
            if (MiningManager.getPlayer(this.manager).getQuestState(0) != 1) {
                MiningManager.getPlayer(this.manager).getDialogueManager().showOneLineStatement("There is no more ore in this rock.");
                MiningManager.getPlayer(this.manager).setInteractionTargetId(0);
            }
            Player player2 = MiningManager.getPlayer(this.manager);
            player2.packetSender.sendGameMessage("There is no more ore in this rock.");
            player2 = MiningManager.getPlayer(this.manager);
            player2.packetSender.sendSoundEffect(429, 1, 0);
            if (MiningManager.getPlayer((MiningManager)this.manager).botEnabled) {
                MiningManager.getPlayer(this.manager).interactWithBotObjectTargets(MiningManager.getPlayer((MiningManager)this.manager).botInteractionTargetIds);
            }
            cycleEventContainer.stop();
            return;
        }
        if (SkillActionHelper.shouldTriggerRandomEvent(MiningManager.getPlayer(this.manager)) && !MiningManager.getPlayer((MiningManager)this.manager).botEnabled && !MiningManager.getPlayer(this.manager).isInTutorialIsland()) {
            GameplayHelper.spawnSkillRandomEventNpc(MiningManager.getPlayer(this.manager), SkillRandomEventNpc.d);
        }
        MiningManager.getPlayer(this.manager).getUpdateState().setAnimation(this.gatheringTool.getGatherAnimationId());
        int n3 = 256;
        if (MiningManager.getPlayer(this.manager).hasChargedAmuletOfGloryEquipped()) {
            n3 = 86;
        }
        if (this.rockObjectId != 2111 && GameUtil.randomInt(n3) == 0 && MiningManager.getPlayer(this.manager).getQuestState(0) == 1) {
            String[] stringArray2 = new String[]{"10/4681", "1/1024", "1/2048", "1/4096", "1/16384"};
            String[] stringArray = new String[]{"1/157", "1/344", "1/688", "1/1376", "1/5504"};
            int[] nArray = new int[]{65000, 1623, 1621, 1619, 1617};
            int n4 = GameUtil.rollFractionWeightIndex(MiningManager.getPlayer(this.manager).hasChargedAmuletOfGloryEquipped() ? stringArray : stringArray2);
            int n5 = nArray[n4];
            if (n5 != 65000) {
                ItemStack itemStack = new ItemStack(n5, 1);
                MiningManager.getPlayer(this.manager).getInventoryManager().addItem(itemStack);
                String string = itemStack.getDefinition().getName().replace("Uncut ", "");
                Player player = MiningManager.getPlayer(this.manager);
                player.packetSender.sendGameMessage("You just found " + TextUtil.prependIndefiniteArticle(string) + "!");
                if (!MiningManager.getPlayer(this.manager).getInventoryManager().canAddItem(itemStack)) {
                    if (MiningManager.getPlayer((MiningManager)this.manager).botEnabled) {
                        MiningManager.getPlayer((MiningManager)this.manager).currentBotTask.startWalkToBank(MiningManager.getPlayer(this.manager));
                    }
                    cycleEventContainer.stop();
                    return;
                }
            }
            return;
        }
        n = this.mineChanceLow;
        int n6 = this.mineChanceHigh;
        if (this.rockObjectId == 2111 && MiningManager.getPlayer(this.manager).hasChargedAmuletOfGloryEquipped()) {
            n = 84;
            n6 = 210;
        }
        if (GameUtil.rollLevelScaledChance(n, n6, MiningManager.getPlayer(this.manager).getSkillManager().getCurrentLevels()[14])) {
            String string;
            if (this.rockObjectId == 2111) {
                String[] stringArray3 = new String[]{"1000/2133", "1000/4267", "1000/8533", "100/1422", "10/256", "10/256", "1/32"};
                int[] nArray = new int[]{1625, 1627, 1629, 1623, 1621, 1619, 1617};
                n = GameUtil.rollFractionWeightIndex(stringArray3);
                n3 = nArray[n];
            } else {
                n3 = MiningManager.rollMinedItemId(this.rockObjectId, this.oreItemId);
            }
            if (n3 == 444) {
                n = n3;
                if (this.manager.perfectGoldOreArea.containsExclusive(MiningManager.getPlayer(this.manager).getPosition())) {
                    n = 446;
                }
                MiningManager.getPlayer(this.manager).getInventoryManager().addItem(new ItemStack(n, 1));
            } else {
                MiningManager.getPlayer(this.manager).getInventoryManager().addItem(new ItemStack(n3, 1));
            }
            if (GameplayTrace.enabled()) {
                ItemService.getInstance();
                GameplayTrace.log("mining success player=" + GameplayTrace.describe(MiningManager.getPlayer(this.manager)) + " seq=" + this.actionSequence + " rockObjectId=" + this.rockObjectId + " minedItemId=" + n3 + " minedItem=" + ItemService.getItemName(n3) + " xp=" + MiningManager.getExperienceForMinedItem(n3, this.baseExperience) + " x=" + this.x + " y=" + this.y);
            }
            Object object = MiningManager.getPlayer(this.manager);
            PacketSender packetSender = ((Player)object).packetSender;
            StringBuilder stringBuilder = new StringBuilder("You manage to mine some ");
            if (this.rockObjectId == 2111) {
                string = "gem";
            } else if (this.rockObjectId == 10946) {
                string = "sandstone";
            } else if (this.rockObjectId == 10947) {
                string = "granite";
            } else {
                ItemService.getInstance();
                string = String.valueOf(ItemService.getItemName(this.oreItemId).toLowerCase()) + ".";
            }
            packetSender.sendGameMessage(stringBuilder.append(string).toString());
            if (MiningManager.getPlayer(this.manager).getQuestState(0) != 1) {
                String string2;
                DialogueManager dialogueManager = MiningManager.getPlayer(this.manager).getDialogueManager();
                StringBuilder stringBuilder2 = new StringBuilder("You manage to mine some ");
                if (this.rockObjectId == 2111) {
                    string2 = "gem";
                } else if (this.rockObjectId == 10946) {
                    string2 = "sandstone";
                } else if (this.rockObjectId == 10947) {
                    string2 = "granite";
                } else {
                    ItemService.getInstance();
                    string2 = String.valueOf(ItemService.getItemName(this.oreItemId).toLowerCase()) + ".";
                }
                dialogueManager.showOneLineStatement(stringBuilder2.append(string2).toString());
                MiningManager.getPlayer(this.manager).getDialogueManager().finishDialogue();
                MiningManager.getPlayer(this.manager).setInteractionTargetId(0);
                if (MiningManager.getPlayer(this.manager).getQuestState(0) == 33 && this.rockObjectId == 3043) {
                    MiningManager.getPlayer(this.manager).ea();
                } else if (MiningManager.getPlayer(this.manager).getQuestState(0) == 34 && this.rockObjectId == 3042) {
                    MiningManager.getPlayer(this.manager).ea();
                }
                MiningManager.getPlayer(this.manager).getQuestManager().refreshQuestJournal();
            }
            MiningManager.getPlayer(this.manager).getSkillManager().addExperience(14, MiningManager.getExperienceForMinedItem(n3, this.baseExperience));
            MiningManager.getPlayer(this.manager);
            Player.rollActionReward();
            if (!ServerSettings.wcStyleMiningEnabled || ServerSettings.wcStyleMiningEnabled && GameUtil.rollChance(this.depletionChance)) {
                try {
                    int n7 = SkillActionHelper.getObjectOrientation(this.rockObjectId, this.x, this.y, MiningManager.getPlayer(this.manager).getPosition().getPlane());
                    n = SkillActionHelper.getObjectType(this.rockObjectId, this.x, this.y, MiningManager.getPlayer(this.manager).getPosition().getPlane());
                    if (GameplayTrace.enabled()) {
                        GameplayTrace.log("mining depleted player=" + GameplayTrace.describe(MiningManager.getPlayer(this.manager)) + " seq=" + this.actionSequence + " rockObjectId=" + this.rockObjectId + " depletedObjectId=" + MiningManager.getDepletedRockObjectId(this.rockObjectId) + " x=" + this.x + " y=" + this.y);
                    }
                    new DynamicObject(MiningManager.getDepletedRockObjectId(this.rockObjectId), this.x, this.y, MiningManager.getPlayer(this.manager).getPosition().getPlane(), n == 22 ? MiningManager.rotateDepletedRockOrientation(n7) : n7, n == 11 ? 11 : 10, this.rockObjectId, this.respawnTicks);
                }
                catch (Exception exception) {
                    object = exception;
                    exception.printStackTrace();
                }
                if (MiningManager.getPlayer((MiningManager)this.manager).botEnabled) {
                    MiningManager.getPlayer(this.manager).interactWithBotObjectTargets(MiningManager.getPlayer((MiningManager)this.manager).botInteractionTargetIds);
                }
                cycleEventContainer.stop();
                bl = true;
            }
        }
        if (!bl && !MiningManager.getPlayer(this.manager).getInventoryManager().canAddItem(new ItemStack(MiningManager.rollMinedItemId(this.rockObjectId, this.oreItemId), 1))) {
            if (MiningManager.getPlayer((MiningManager)this.manager).botEnabled) {
                MiningManager.getPlayer((MiningManager)this.manager).currentBotTask.startWalkToBank(MiningManager.getPlayer(this.manager));
            }
            cycleEventContainer.stop();
            return;
        }
    }

    @Override
    public final void onStop() {
        MiningManager.getPlayer(this.manager).getUpdateState().setAnimation(-1);
    }
}

