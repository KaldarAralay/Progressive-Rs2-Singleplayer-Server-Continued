/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.thieving;

import com.rs2.ServerSettings;
import com.rs2.model.Entity;
import com.rs2.model.World;
import com.rs2.model.combat.CombatManager;
import com.rs2.model.item.ItemStack;
import com.rs2.model.npc.Npc;
import com.rs2.model.objects.ObjectManager;
import com.rs2.model.player.Player;
import com.rs2.model.skill.SkillActionHelper;
import com.rs2.model.skill.thieving.StallDefinition;
import com.rs2.model.skill.thieving.StallThievingTask;
import com.rs2.model.task.CycleEventHandler;
import com.rs2.util.GameUtil;
import java.util.Random;

public final class StallThievingHandler {
    private static final Random random = new Random();

    public static int getEmptyStallObjectId(int n) {
        if (n >= 4874 && n <= 4878) {
            return 4276;
        }
        if (n == 6163) {
            return 6573;
        }
        return 634;
    }

    public static boolean handleStallThieving(Player player, int n, int n2, int n3) {
        StallDefinition stallDefinition;
        StallDefinition stallDefinition2;
        Object object;
        int n4;
        block13: {
            int n5 = n;
            StallDefinition[] stallDefinitionArray = StallDefinition.values();
            n4 = stallDefinitionArray.length;
            int n6 = 0;
            while (n6 < n4) {
                stallDefinition = stallDefinitionArray[n6];
                int[] nArray = stallDefinition.getObjectIds();
                int n7 = nArray.length;
                int n8 = 0;
                while (n8 < n7) {
                    int n9 = nArray[n8];
                    if (n5 == n9) {
                        stallDefinition2 = stallDefinition;
                        break block13;
                    }
                    ++n8;
                }
                ++n6;
            }
            stallDefinition2 = stallDefinition = null;
        }
        if (stallDefinition2 == null) {
            return false;
        }
        if (!ServerSettings.thievingEnabled) {
            Player player2 = player;
            player2.packetSender.sendGameMessage("This skill is currently disabled.");
            return true;
        }
        if (!player.isMember()) {
            player.packetSender.sendGameMessage("You need a members account to access members content.");
            return true;
        }
        if (ServerSettings.freeToPlayWorld) {
            player.packetSender.sendGameMessage("You need to be in members world to access members content.");
            return true;
        }
        ObjectManager.getInstance();
        if (ObjectManager.findDynamicObjectAt(n2, n3, player.getPosition().getPlane()) != null) {
            Player player3 = player;
            player3.packetSender.sendGameMessage("Too late, the items are gone.");
            return true;
        }
        if (!SkillActionHelper.checkSkillRequirement(player, 17, stallDefinition.getRequiredLevel(), "steal from this stall")) {
            return true;
        }
        object = stallDefinition.getRewards()[random.nextInt(stallDefinition.getRewards().length)];
        if (!player.getInventoryManager().canAddItem((ItemStack)object)) {
            return true;
        }
        Entity entity = player;
        ((Player)entity).packetSender.sendGameMessage("You attempt to steal from the stall..");
        Npc[] npcArray = World.getNpcs();
        int n10 = npcArray.length;
        n4 = 0;
        while (n4 < n10) {
            entity = npcArray[n4];
            if (entity != null && !entity.isDead() && ((Npc)entity).getMaxHitpoints() > 0 && !entity.hasCombatTarget() && GameUtil.isWithinDistance(entity.getPosition().getX(), entity.getPosition().getY(), player.getPosition().getX(), player.getPosition().getY(), 4)) {
                entity.getUpdateState().setForcedTextAndMarkUpdated("Hey! Get away from there!");
                if (((Npc)entity).getDefinition().isAttackable()) {
                    CombatManager.startCombat(entity, player);
                }
                return true;
            }
            ++n4;
        }
        int n11 = player.nextActionSequence();
        player.setActionLocked(false);
        player.setHideHeldItemsInAppearance(true);
        player.getUpdateState().setAnimation(832);
        player.setActiveCycleEvent(new StallThievingTask(player, n11, n, n2, n3, (ItemStack)object, stallDefinition));
        CycleEventHandler.getInstance().schedule(player, player.getActiveCycleEvent(), 2);
        return true;
    }
}

