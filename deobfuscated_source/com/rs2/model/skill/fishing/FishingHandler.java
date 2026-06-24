/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.fishing;

import com.rs2.ServerSettings;
import com.rs2.model.Position;
import com.rs2.model.npc.Npc;
import com.rs2.model.player.Player;
import com.rs2.model.skill.fishing.FishingSpotDefinition;
import com.rs2.model.skill.fishing.FishingSpotManager;
import com.rs2.model.skill.fishing.FishingTask;
import com.rs2.model.skill.fishing.FishingWhirlpool;
import com.rs2.model.task.CycleEventHandler;
import com.rs2.util.GameUtil;
import java.util.Random;

public final class FishingHandler {
    private Player player;

    static {
        new Random();
    }

    public FishingHandler(Player player) {
        this.player = player;
    }

    public final boolean handleFishingSpot(Npc npc, int n) {
        boolean bl;
        Object object = FishingWhirlpool.forWhirlpoolNpcId(npc.getDefinition().getId());
        int n2 = npc.getNpcId();
        if (object != null) {
            n2 = ((FishingWhirlpool)object).getSourceNpcIds()[0];
        }
        FishingSpotDefinition fishingSpotDefinition = FishingSpotDefinition.forNpcIdAndOption(n2, n);
        Object object2 = npc.getPosition();
        if (!FishingSpotManager.isSpotAtPosition((Position)object2, fishingSpotDefinition) && object == null) {
            return false;
        }
        object2 = fishingSpotDefinition;
        object = this;
        if (!ServerSettings.fishingEnabled) {
            object = ((FishingHandler)object).player;
            ((Player)object).packetSender.sendGameMessage("This skill is currently disabled.");
            bl = false;
        } else if (((FishingHandler)object).player.getInventoryManager().getContainer().getFreeSlots() <= 0) {
            ((FishingHandler)object).player.getUpdateState().setAnimation(-1);
            ((FishingHandler)object).player.getDialogueManager().showOneLineStatement("Not enough space in your inventory.");
            ((FishingHandler)object).player.setInteractionTargetId(0);
            if (((FishingHandler)object).player.botEnabled) {
                ((FishingHandler)object).player.currentBotTask.startWalkToBank(((FishingHandler)object).player);
            }
            bl = false;
        } else {
            int n3 = ((FishingSpotDefinition)((Object)object2)).getRequiredLevels()[0];
            if (((FishingHandler)object).player.getSkillManager().getCurrentLevels()[10] < n3) {
                ((FishingHandler)object).player.getDialogueManager().showOneLineStatement("You need a fishing level of at least " + n3 + " in order to fish at this spot.");
                ((FishingHandler)object).player.getUpdateState().setAnimation(-1);
                ((FishingHandler)object).player.setInteractionTargetId(0);
                bl = false;
            } else if (!((FishingHandler)object).player.getInventoryManager().getContainer().containsItem(((FishingSpotDefinition)((Object)object2)).getToolItem().getId())) {
                ((FishingHandler)object).player.getDialogueManager().showOneLineStatement("You need a " + ((FishingSpotDefinition)((Object)object2)).getToolItem().getDefinition().getName().toLowerCase() + " in order to fish at this spot.");
                ((FishingHandler)object).player.getUpdateState().setAnimation(-1);
                ((FishingHandler)object).player.setInteractionTargetId(0);
                if (((FishingHandler)object).player.botEnabled) {
                    ((FishingHandler)object).player.currentBotTask.startWalkToBank(((FishingHandler)object).player);
                }
                bl = false;
            } else if (((FishingSpotDefinition)((Object)object2)).getBaitItem() != null && !((FishingHandler)object).player.getInventoryManager().getContainer().containsItem(((FishingSpotDefinition)((Object)object2)).getBaitItem().getId())) {
                ((FishingHandler)object).player.getDialogueManager().showOneLineStatement("you need more " + ((FishingSpotDefinition)((Object)object2)).getBaitItem().getDefinition().getName().toLowerCase().toLowerCase() + " in order to fish at this spot.");
                ((FishingHandler)object).player.getUpdateState().setAnimation(-1);
                ((FishingHandler)object).player.setInteractionTargetId(0);
                if (((FishingHandler)object).player.botEnabled) {
                    ((FishingHandler)object).player.currentBotTask.startWalkToBank(((FishingHandler)object).player);
                }
                bl = false;
            } else {
                bl = true;
            }
        }
        if (!bl) {
            return true;
        }
        if (this.player.getQuestState(0) != 1) {
            this.player.getDialogueManager().showTutorialInstructionOverlay("Please wait.", "This should only take a few seconds.", "As you gain Fishing experience you'll find that there are many", "types of fish and many ways to catch them.", "", true);
        } else {
            object = this.player;
            ((Player)object).packetSender.sendGameMessage("You attempt to catch a fish...");
        }
        this.player.getUpdateState().setAnimation(fishingSpotDefinition.getAnimationId());
        this.player.gatheringHazardCounter = 0;
        if (fishingSpotDefinition.getToolItem().getId() == 307 || fishingSpotDefinition.getToolItem().getId() == 309) {
            object = this.player;
            ((Player)object).packetSender.sendSoundEffect(377, 1, 0);
        } else {
            object = this.player;
            ((Player)object).packetSender.sendSoundEffect(289, 1, 0);
        }
        int n4 = this.player.nextActionSequence();
        if (this.player.getQuestState(0) == 1 && GameUtil.randomInt(800) == 0 && !this.player.botEnabled && ServerSettings.randomEventsMode == 0 && !this.player.isInTutorialIsland() && (object2 = FishingWhirlpool.forSourceNpcId(npc.getDefinition().getId())) != null) {
            npc.transformToNpcId(((FishingWhirlpool)((Object)object2)).getWhirlpoolNpcId(), 20);
        }
        this.player.setActiveCycleEvent(new FishingTask(this, n4, npc, fishingSpotDefinition));
        CycleEventHandler.getInstance().schedule(this.player, this.player.getActiveCycleEvent(), 5);
        return true;
    }

    static /* synthetic */ Player getPlayer(FishingHandler fishingHandler) {
        return fishingHandler.player;
    }
}

