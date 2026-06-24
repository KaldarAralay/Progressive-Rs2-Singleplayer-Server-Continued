/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.thieving;

import com.rs2.model.combat.hit.HitType;
import com.rs2.model.item.ItemDefinition;
import com.rs2.model.item.ItemStack;
import com.rs2.model.npc.Npc;
import com.rs2.model.player.InventoryManager;
import com.rs2.model.player.Player;
import com.rs2.model.skill.thieving.PickpocketDefinition;
import com.rs2.model.task.CycleEvent;
import com.rs2.model.task.CycleEventContainer;
import com.rs2.util.GameUtil;

public final class PickpocketTask
extends CycleEvent {
    private final /* synthetic */ boolean success;
    private final /* synthetic */ Player player;
    private final /* synthetic */ Npc npc;
    private final /* synthetic */ ItemStack reward;
    private final /* synthetic */ PickpocketDefinition definition;
    private final /* synthetic */ String npcName;
    private final /* synthetic */ int damage;

    public PickpocketTask(boolean bl, Player player, Npc npc, ItemStack itemStack, PickpocketDefinition pickpocketDefinition, String string, int n) {
        this.success = bl;
        this.player = player;
        this.npc = npc;
        this.reward = itemStack;
        this.definition = pickpocketDefinition;
        this.npcName = string;
        this.damage = n;
    }

    @Override
    public final void execute(CycleEventContainer cycleEventContainer) {
        if (this.success) {
            int n;
            Player player = this.player;
            player.packetSender.sendGameMessage("You manage to pick the " + this.npc.getDefinition().getName().toLowerCase() + "'s pocket.");
            player = this.player;
            player.packetSender.sendGameMessage("You steal some " + ItemDefinition.forId(this.reward.getId()).getName().toLowerCase() + ".");
            player = this.player;
            player.packetSender.sendSoundEffect(358, 1, 0);
            InventoryManager inventoryManager = this.player.getInventoryManager();
            int n2 = this.reward.getId();
            int n3 = this.reward.getAmount();
            int n4 = this.definition.getRequiredLevel();
            player = this.player;
            if (GameUtil.randomInclusive(25) == 0 && player.getSkillManager().getCurrentLevels()[17] > n4 + 30 && player.getSkillManager().getCurrentLevels()[16] > n4 + 20) {
                player.packetSender.sendGameMessage("You recieve a quadruple loot!");
                n = 4;
            } else if (GameUtil.randomInclusive(20) == 0 && player.getSkillManager().getCurrentLevels()[17] > n4 + 20 && player.getSkillManager().getCurrentLevels()[16] > n4 + 10) {
                player.packetSender.sendGameMessage("You recieve a triple loot!");
                n = 3;
            } else if (GameUtil.randomInclusive(15) == 0 && player.getSkillManager().getCurrentLevels()[17] > n4 + 10 && player.getSkillManager().getCurrentLevels()[16] > n4) {
                player.packetSender.sendGameMessage("You recieve a double loot!");
                n = 2;
            } else {
                n = 1;
            }
            inventoryManager.addOrDropItem(new ItemStack(n2, n3 * n));
            this.player.getSkillManager().addExperience(17, this.definition.getExperience());
        } else {
            this.npc.getUpdateState().setForcedTextAndMarkUpdated("What do you think you're doing?");
            this.npc.getUpdateState().setAnimation(401);
            Player player = this.player;
            player.packetSender.sendSoundEffect(458, 1, 0);
            this.npc.setInteractionTarget(this.player);
            this.player.getUpdateState().setAnimation(this.player.getBlockAnimationId());
            player = this.player;
            player.packetSender.sendGameMessage("You fail to pick the " + this.npcName + "'s pocket.");
            this.player.getUpdateState().setGraphic(254, 0x640000);
            this.player.applyDirectHit(this.damage <= 0 ? 1 : this.damage, HitType.NORMAL);
            this.player.getStunTimer().setDelayTicks(this.definition.getStunTicks());
            this.player.getStunTimer().reset();
        }
        cycleEventContainer.stop();
    }

    @Override
    public final void onStop() {
        this.player.setActionLocked(false);
    }
}

