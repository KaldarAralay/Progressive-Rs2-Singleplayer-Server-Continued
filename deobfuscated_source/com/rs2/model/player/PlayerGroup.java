/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.player;

import com.rs2.ServerSettings;
import com.rs2.bot.combat.BotCombatHelper;
import com.rs2.model.Entity;
import com.rs2.model.EntityTargetMovement;
import com.rs2.model.Position;
import com.rs2.model.combat.CombatManager;
import com.rs2.model.item.ItemStack;
import com.rs2.model.player.Player;
import com.rs2.model.skill.fletching.BowStringingDefinition;
import com.rs2.model.skill.fletching.BowStringingTask;
import com.rs2.model.task.CycleEventHandler;
import com.rs2.util.GameUtil;
import com.rs2.util.TextUtil;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

public class PlayerGroup {
    public Player leader;
    public CopyOnWriteArrayList members = new CopyOnWriteArrayList();
    public CopyOnWriteArrayList deferredRemovedMembers = new CopyOnWriteArrayList();
    public Boolean rollLootEnabled = true;

    public static boolean handleBowStringing(Player player, int n, int n2) {
        BowStringingDefinition bowStringingDefinition = BowStringingDefinition.forComponents(n, n2);
        if (bowStringingDefinition == null) {
            return false;
        }
        if (!ServerSettings.fletchingEnabled) {
            player.packetSender.sendGameMessage("This skill is currently disabled.");
            return false;
        }
        if (player.getSkillManager().getCurrentLevels()[9] < bowStringingDefinition.getRequiredLevel()) {
            player.getDialogueManager().showOneLineStatement("You need a fletching level of " + bowStringingDefinition.getRequiredLevel() + " to do this");
            return true;
        }
        if (!player.getInventoryManager().getContainer().containsItem(bowStringingDefinition.getUnstrungBowItemId()) || !player.getInventoryManager().getContainer().containsItem(bowStringingDefinition.getBowStringItemId())) {
            player.getDialogueManager().showOneLineStatement("You need a " + new ItemStack(bowStringingDefinition.getUnstrungBowItemId()).getDefinition().getName().toLowerCase() + " and a " + new ItemStack(bowStringingDefinition.getBowStringItemId()).getDefinition().getName().toLowerCase() + " to make this");
            return true;
        }
        n2 = player.nextActionSequence();
        player.setActiveCycleEvent(new BowStringingTask(player, n2, bowStringingDefinition));
        CycleEventHandler.getInstance().schedule(player, player.getActiveCycleEvent(), 1);
        return true;
    }

    public PlayerGroup(Player player, Player player2) {
        if (player.currentGroup != null || player2.currentGroup != null) {
            return;
        }
        this.leader = player;
        this.members.add(player);
        this.members.add(player2);
        player.currentGroup = this;
        player2.currentGroup = this;
        Player player3 = player;
        player3.packetSender.sendGameMessage(String.valueOf(player2.getUsername()) + " joined your group.");
        player3 = player2;
        player3.packetSender.sendGameMessage("You joined a group lead by: " + player.getUsername());
        this.showMemberList(player2);
    }

    public boolean isFull() {
        return this.members.size() >= 5;
    }

    public void refreshGroupFollowChain() {
        Player player = this.leader;
        Iterator iterator = this.members.iterator();
        while (iterator.hasNext()) {
            Player player2 = (Player)iterator.next();
            if (this.deferredRemovedMembers.contains(player2)) {
                iterator.remove();
                continue;
            }
            CombatManager.stopCombat(player2);
            if (player2 == this.leader || BotCombatHelper.hasExternalCombatTarget(player2)) continue;
            player2.getUpdateState().setFaceEntity(player.getEncodedIndex());
            player2.setAttackRange(1);
            player2.setMovementTarget(player);
            player = player2;
        }
    }

    public boolean containsMember(Player player) {
        Iterator iterator = this.members.iterator();
        while (iterator.hasNext()) {
            Player player2 = (Player)iterator.next();
            if (this.deferredRemovedMembers.contains(player2)) {
                iterator.remove();
                continue;
            }
            if (player2 != player) continue;
            return true;
        }
        return false;
    }

    public void attackTarget(Entity entity) {
        Iterator iterator = this.members.iterator();
        while (iterator.hasNext()) {
            Player player = (Player)iterator.next();
            if (this.deferredRemovedMembers.contains(player)) {
                iterator.remove();
                continue;
            }
            if (entity == null || entity.isDead() || player == null || player.isDead()) continue;
            CombatManager.startCombat(player, entity);
        }
    }

    public void showMemberList(Player player) {
        player.packetSender.sendGameMessage("Group members: (size:" + this.members.size() + ", roll loot:" + this.rollLootEnabled + ")");
        Iterator iterator = this.members.iterator();
        while (iterator.hasNext()) {
            Player player2 = (Player)iterator.next();
            if (this.deferredRemovedMembers.contains(player2)) {
                iterator.remove();
                continue;
            }
            String string = "";
            if (player2 == this.leader) {
                string = " (leader)";
            }
            player.packetSender.sendGameMessage(String.valueOf(player2.getUsername()) + string);
        }
    }

    public int getHighestCombatLevel() {
        int n = 0;
        Iterator iterator = this.members.iterator();
        while (iterator.hasNext()) {
            Player player = (Player)iterator.next();
            if (this.deferredRemovedMembers.contains(player)) {
                iterator.remove();
                continue;
            }
            if (player.getCombatLevel() <= n) continue;
            n = player.getCombatLevel();
        }
        return n;
    }

    public int getTotalCombatLevel() {
        int n = 0;
        Iterator iterator = this.members.iterator();
        while (iterator.hasNext()) {
            Player player = (Player)iterator.next();
            if (this.deferredRemovedMembers.contains(player)) {
                iterator.remove();
                continue;
            }
            n += player.getCombatLevel();
        }
        return n;
    }

    public void addMember(Player player) {
        Player player2;
        if (this.members.size() >= 5) {
            return;
        }
        if (player.currentGroup != null) {
            return;
        }
        Iterator iterator = this.members.iterator();
        while (iterator.hasNext()) {
            player2 = (Player)iterator.next();
            if (this.deferredRemovedMembers.contains(player2)) {
                iterator.remove();
                continue;
            }
            player2.packetSender.sendGameMessage(String.valueOf(player.getUsername()) + " joined the group.");
        }
        this.members.add(player);
        player.currentGroup = this;
        player2 = player;
        player2.packetSender.sendGameMessage("You joined a group lead by: " + this.leader.getUsername());
        this.showMemberList(player);
    }

    public void finishDeferredRemoval(Player player) {
        player.pendingGroupCleanup = null;
        this.deferredRemovedMembers.remove(player);
        int n = this.members.size() + this.deferredRemovedMembers.size();
        if (n == 1) {
            if (this.members.size() == 1) {
                ((Player)this.members.get((int)0)).currentGroup = null;
                ((Player)this.members.get((int)0)).pendingGroupCleanup = null;
                this.members.clear();
                return;
            }
            if (this.deferredRemovedMembers.size() == 1) {
                this.deferredRemovedMembers.clear();
            }
            return;
        }
    }

    public void removeMember(Player object) {
        boolean bl = false;
        if (((Player)object).isBot && !this.leader.isBot) {
            this.leader.botPvpTeamRequesters.remove(object);
            EntityTargetMovement.clearMovementTarget((Entity)object);
        }
        if (!((Player)object).isBot) {
            ((Player)object).botPvpTeamRequesters.clear();
            bl = true;
        }
        Player player = object;
        player.packetSender.sendGameMessage("You are no longer in the group.");
        boolean bl2 = false;
        if (this.leader == object) {
            bl2 = true;
        }
        this.members.remove(object);
        ((Player)object).currentGroup = null;
        boolean bl3 = true;
        Iterator iterator = this.members.iterator();
        while (iterator.hasNext()) {
            Player player2 = (Player)iterator.next();
            if (this.deferredRemovedMembers.contains(player2)) {
                iterator.remove();
                continue;
            }
            player = player2;
            player.packetSender.sendGameMessage(String.valueOf(((Player)object).getUsername()) + " left the group.");
            if (!player2.botEnabled) {
                bl3 = false;
                continue;
            }
            if (!bl || player2.botPvpRejectedTeamTargets.contains(object)) continue;
            player2.botPvpRejectedTeamTargets.add(object);
        }
        int n = this.members.size() + this.deferredRemovedMembers.size();
        if (n == 1) {
            if (this.members.size() == 1) {
                ((Player)this.members.get((int)0)).currentGroup = null;
                ((Player)this.members.get((int)0)).pendingGroupCleanup = null;
                this.members.clear();
                return;
            }
            if (this.deferredRemovedMembers.size() == 1) {
                this.deferredRemovedMembers.clear();
            }
            return;
        }
        if (bl2 && this.members.size() > 0) {
            this.leader = (Player)this.members.get(0);
            Iterator memberIterator = this.members.iterator();
            while (memberIterator.hasNext()) {
                Player player3 = (Player)memberIterator.next();
                if (this.deferredRemovedMembers.contains(player3)) {
                    memberIterator.remove();
                    continue;
                }
                player = player3;
                player.packetSender.sendGameMessage(String.valueOf(this.leader.getUsername()) + " is the new group leader.");
            }
        }
        if (bl3) {
            EntityTargetMovement.clearMovementTarget(this.leader);
            this.refreshGroupFollowChain();
        }
    }

    public static void handleGroupInvite(Player player, Player player2) {
        if (player.getQuestState(0) != 1) {
            return;
        }
        Player player3 = player2;
        if (player3.pendingGroupInviteTarget != player) {
            player3 = player;
            player3.packetSender.sendGameMessage("Sending group invite...");
            player3 = player2;
            player3.packetSender.sendGameMessage(TextUtil.capitalizeFirst(player.getUsername()) + ":grpinv:");
            Player player4 = player;
            player = player2;
            player3 = player4;
            player4.pendingGroupInviteTarget = player;
            return;
        }
        if (player2.currentGroup == null) {
            if (player2.isBot) {
                new PlayerGroup(player, player2);
            } else {
                new PlayerGroup(player2, player);
            }
        } else {
            player2.currentGroup.addMember(player);
        }
        Player player5 = player;
        player = null;
        player3 = player5;
        player5.pendingGroupInviteTarget = player;
        player = null;
        player3 = player2;
        player2.pendingGroupInviteTarget = player;
    }

    public Entity selectLootRecipient(Player player, Position object5) {
        if (this.rollLootEnabled.booleanValue()) {
            Object object;
            Object object2;
            ArrayList<Player> arrayList = new ArrayList<Player>();
            ArrayList<Player> arrayList2 = new ArrayList<Player>();
            ArrayList<Player> arrayList3 = new ArrayList<Player>();
            Iterator iterator = this.members.iterator();
            while (iterator.hasNext()) {
                Player player2 = (Player)iterator.next();
                if (this.deferredRemovedMembers.contains(player2)) {
                    iterator.remove();
                    continue;
                }
                if (GameUtil.getDistance(player2.getPosition(), (Position)object5) <= 20) {
                    arrayList.add(player2);
                    continue;
                }
                object2 = player2;
                ((Player)object2).packetSender.sendGameMessage("You were too far away from target to roll for the loot.");
                arrayList3.add(player2);
            }
            for (Player player2 : arrayList) {
                Iterator distantIterator = arrayList3.iterator();
                while (distantIterator.hasNext()) {
                    Player player3 = (Player)distantIterator.next();
                    object2 = player2;
                    ((Player)object2).packetSender.sendGameMessage(String.valueOf(player3.getUsername()) + " was too far away to roll for the loot.");
                }
            }
            while (arrayList.size() > 1) {
                arrayList2.clear();
                int n = 0;
                for (Player player4 : arrayList) {
                    int n2;
                    player4.groupLootRoll = n2 = GameUtil.randomInclusive(100);
                    if (n2 <= n) continue;
                    n = n2;
                }
                Iterator iterator2 = this.members.iterator();
                while (iterator2.hasNext()) {
                    Player player5 = (Player)iterator2.next();
                    if (this.deferredRemovedMembers.contains(player5)) {
                        iterator2.remove();
                        continue;
                    }
                    for (Player player6 : arrayList) {
                        object2 = player5;
                        ((Player)object2).packetSender.sendGameMessage(String.valueOf(player6.getUsername()) + " rolled: " + player6.groupLootRoll);
                    }
                }
                for (Player player7 : arrayList) {
                    if (player7.groupLootRoll >= n) continue;
                    arrayList2.add(player7);
                }
                for (Player player8 : arrayList2) {
                    arrayList.remove(player8);
                }
                if (arrayList.size() == 1) continue;
                Iterator iterator3 = this.members.iterator();
                while (iterator3.hasNext()) {
                    object = (Player)iterator3.next();
                    if (this.deferredRemovedMembers.contains(object)) {
                        iterator3.remove();
                        continue;
                    }
                    object2 = object;
                    ((Player)object2).packetSender.sendGameMessage("---");
                }
            }
            if (arrayList.size() == 1) {
                Iterator iterator4 = this.members.iterator();
                while (iterator4.hasNext()) {
                    Player player9 = (Player)iterator4.next();
                    if (this.deferredRemovedMembers.contains(player9)) {
                        iterator4.remove();
                        continue;
                    }
                    object2 = player9;
                    ((Player)object2).packetSender.sendGameMessage(String.valueOf(((Player)arrayList.get(0)).getUsername()) + " won the loot.");
                }
                return (Entity)arrayList.get(0);
            }
        }
        return player;
    }
}

