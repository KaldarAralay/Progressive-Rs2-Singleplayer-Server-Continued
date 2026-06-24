/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.bot;

import com.rs2.bot.DropPartyBotManager;
import com.rs2.bot.combat.BotCombatHelper;
import com.rs2.model.Entity;
import com.rs2.model.EntityTargetMovement;
import com.rs2.model.Position;
import com.rs2.model.item.ItemStack;
import com.rs2.model.player.Player;
import com.rs2.model.task.TickTask;
import com.rs2.util.GameUtil;
import com.rs2.util.path.PathFinder;

public final class DropPartyLeaderTickTask
extends TickTask {
    private final /* synthetic */ Player leader;

    public DropPartyLeaderTickTask(int n, Player player) {
        super(3);
        this.leader = player;
    }

    @Override
    public final void execute() {
        if (this.leader.isDead() || !this.leader.isRegistered() || !this.leader.dropPartyLeader || this.leader.botTaskState.equals("wait for new task")) {
            this.stop();
            return;
        }
        if (this.leader.botTaskState.equals("do task")) {
            Object object;
            Object object22;
            if (this.getIntervalTicks() != 8) {
                this.leader.queuePublicChatMessage("Dropping now!");
                this.setIntervalTicks(8);
                for (Object participantObject : DropPartyBotManager.dropPartyParticipants) {
                    if (participantObject == this.leader) continue;
                    ((Player)participantObject).botTaskState = "do task";
                    EntityTargetMovement.clearMovementTarget((Entity)participantObject);
                    ((Entity)participantObject).getMovementQueue().clear();
                    object = this.leader.currentBotTask.getRandomTaskAreaPosition();
                    PathFinder.getInstance();
                    PathFinder.findPath((Player)participantObject, ((Position)object).getX(), ((Position)object).getY(), true, 0, 0);
                }
            }
            if (GameUtil.randomInt(2) == 0) {
                object22 = null;
                ItemStack[] itemStackArray = this.leader.getInventoryManager().getContainer().getItems();
                int n = itemStackArray.length;
                int n2 = 0;
                while (n2 < n) {
                    ItemStack itemStack = itemStackArray[n2];
                    if (itemStack != null) {
                        object22 = itemStack;
                        break;
                    }
                    ++n2;
                }
                if (object22 != null) {
                    BotCombatHelper.dropInventoryItem(this.leader, (ItemStack)object22);
                } else {
                    DropPartyBotManager.finishLeaderDrops(this.leader);
                    this.stop();
                    return;
                }
            }
            object22 = this.leader.currentBotTask.getRandomTaskAreaPosition();
            PathFinder.getInstance();
            PathFinder.findPath(this.leader, ((Position)object22).getX(), ((Position)object22).getY(), true, 0, 0);
            int n = GameUtil.randomInt(DropPartyBotManager.dropPartyParticipants.size());
            object = (Player)DropPartyBotManager.dropPartyParticipants.get(n);
            if (!((Player)object).dropPartySentToAssignedDrop && object != this.leader) {
                ((Player)object).botTaskState = "do task";
                ((Player)object).dropPartyAssignedDropPosition = this.leader.getPosition();
            }
            return;
        }
        if (GameUtil.randomInt(3) == 0) {
            this.leader.queuePublicChatMessage(this.leader.botPublicChatMessage, this.leader.botPublicChatColor, this.leader.botPublicChatEffect);
        }
    }
}

