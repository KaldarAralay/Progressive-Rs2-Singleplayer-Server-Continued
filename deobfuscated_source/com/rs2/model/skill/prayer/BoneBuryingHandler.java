/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.prayer;

import com.rs2.ServerSettings;
import com.rs2.model.GameplayHelper;
import com.rs2.model.item.ItemService;
import com.rs2.model.item.ItemStack;
import com.rs2.model.player.Player;
import com.rs2.model.randomevent.SkillRandomEventNpc;
import com.rs2.model.skill.SkillActionHelper;
import com.rs2.model.skill.prayer.BoneDefinition;
import com.rs2.net.packet.PacketSender;
import com.rs2.util.GameUtil;

public final class BoneBuryingHandler {
    private Player player;

    public BoneBuryingHandler(Player player) {
        this.player = player;
    }

    public final boolean handleBuryBone(int n, int n2) {
        BoneDefinition boneDefinition;
        BoneDefinition boneDefinition2;
        block8: {
            int n3 = n;
            BoneDefinition[] boneDefinitionArray = BoneDefinition.values();
            int n4 = boneDefinitionArray.length;
            int n5 = 0;
            while (n5 < n4) {
                BoneDefinition boneDefinition3;
                BoneDefinition boneDefinition4 = boneDefinition3 = boneDefinitionArray[n5];
                int[] nArray = boneDefinition3.itemIds;
                int n6 = boneDefinition3.itemIds.length;
                int n7 = 0;
                while (n7 < n6) {
                    int n8 = nArray[n7];
                    if (n8 == n3) {
                        boneDefinition2 = boneDefinition3;
                        break block8;
                    }
                    ++n7;
                }
                ++n5;
            }
            boneDefinition2 = boneDefinition = null;
        }
        if (boneDefinition2 == null) {
            return false;
        }
        if (!ServerSettings.prayerEnabled) {
            Player player = this.player;
            player.packetSender.sendGameMessage("This skill is currently disabled.");
            return true;
        }
        if (!this.player.getSkillManager().tryStartActionDelay(800)) {
            return true;
        }
        if (this.player.getInventoryManager().removeItemFromSlot(new ItemStack(n), n2)) {
            this.player.nextActionSequence();
            this.player.getSkillManager().addExperience(5, boneDefinition2.experience);
            this.player.getUpdateState().setAnimation(827);
            Player player = this.player;
            player.packetSender.sendSoundEffect(380, 1, 0);
            player = this.player;
            PacketSender packetSender = player.packetSender;
            StringBuilder stringBuilder = new StringBuilder("You bury the ");
            ItemService.getInstance();
            packetSender.sendGameMessage(stringBuilder.append(ItemService.getItemName(n).toLowerCase()).append(".").toString());
            if (SkillActionHelper.shouldTriggerRandomEvent(this.player) && !this.player.botEnabled && !this.player.isInTutorialIsland()) {
                GameplayHelper.spawnSkillRandomEventNpc(this.player, GameUtil.randomInclusive(3) == 0 ? SkillRandomEventNpc.b : SkillRandomEventNpc.a);
            }
        }
        return true;
    }
}

