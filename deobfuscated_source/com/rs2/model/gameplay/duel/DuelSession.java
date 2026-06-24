/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.gameplay.duel;

import com.rs2.ServerSettings;
import com.rs2.model.Position;
import com.rs2.model.combat.CombatType;
import com.rs2.model.combat.WeaponProfile;
import com.rs2.model.combat.attack.WeaponCombatAttack;
import com.rs2.model.gameplay.duel.DuelArenaLocationManager;
import com.rs2.model.gameplay.duel.DuelCountdownTask;
import com.rs2.model.gameplay.duel.DuelHistory;
import com.rs2.model.gameplay.duel.DuelRule;
import com.rs2.model.gameplay.duel.DuelVictoryTask;
import com.rs2.model.item.ItemStack;
import com.rs2.model.player.Player;
import com.rs2.model.task.CycleEventHandler;
import com.rs2.util.GameUtil;
import java.util.ArrayList;

public final class DuelSession {
    private Player player;
    private Player opponent;
    private boolean started = false;
    private ArrayList d = new ArrayList();
    private ArrayList equipmentToRemove = new ArrayList();
    private ArrayList ruleDescriptions = new ArrayList();
    private boolean[] enabledRules = new boolean[22];
    private ArrayList stakedItems = new ArrayList();

    public DuelSession(Player player) {
        this.player = player;
    }

    public final void clearDuelState() {
        this.stakedItems.clear();
        this.ruleDescriptions.clear();
        this.equipmentToRemove.clear();
        this.d.clear();
    }

    public static ItemStack[] toItemArray(ArrayList arrayList) {
        ItemStack[] itemStackArray = new ItemStack[arrayList.size()];
        int n = 0;
        while (n < arrayList.size()) {
            itemStackArray[n] = (ItemStack)arrayList.get(n);
            ++n;
        }
        return itemStackArray;
    }

    public final CombatType getCurrentCombatType() {
        return new WeaponCombatAttack(this.player, this.player.getDuelSession().opponent, WeaponProfile.forItem(new ItemStack(this.player.getEquipmentManager().getItemIdAtSlot(3)))).getAttackStyle().getCombatType();
    }

    public final boolean handleButtonClick(int n) {
        DuelSession duelSession;
        DuelRule duelRule;
        block7: {
            block6: {
                if (n == 6674 || n == 6520) {
                    if (!this.player.getDuelSession().started) {
                        this.player.getDuelController().acceptCurrentDuelScreen();
                    }
                    return true;
                }
                duelRule = DuelRule.forButtonId(n);
                if (duelRule == null) {
                    return false;
                }
                duelSession = this.player.getDuelSession();
                if (duelSession.opponent == null) break block6;
                duelSession = this.player.getDuelSession();
                if (duelSession.opponent.isRegistered()) break block7;
            }
            this.player.getDuelController().resetDuel(true);
            return true;
        }
        duelRule.toggleForPlayer(this.player, true);
        duelSession = this.player.getDuelSession();
        duelRule.toggleForPlayer(duelSession.opponent, false);
        return true;
    }

    public static void finishDuelVictory(Player player, Player player2) {
        if (player == null || player2 == null) {
            return;
        }
        DuelHistory.recordDuelResult(player, player2);
        String string = player2.getUsername();
        String string2 = "" + player2.getCombatLevel();
        ItemStack[] itemStackArray = DuelSession.toItemArray(player2.getDuelSession().stakedItems);
        player.getDuelController().resetDuel(true);
        player.getDuelSession().clearDuelState();
        player2.getDuelSession().clearDuelState();
        player.setActionLocked(true);
        ++player.duelWins;
        ++player2.duelLosses;
        CycleEventHandler.getInstance().schedule(player, new DuelVictoryTask(player, string, string2, itemStackArray), 2);
    }

    public final void finishDuelLoss(boolean bl) {
        Player player;
        if (this.opponent != null && this.opponent.getDuelSession() != null) {
            this.opponent.getAttributes().put("canTakeDamage", true);
            DuelSession.finishDuelVictory(this.opponent, this.player);
        }
        if (bl) {
            player = this.player;
            player.packetSender.closeInterfaces();
            player = this.player;
            player.packetSender.sendGameMessage("You forfeited the duel.");
        } else {
            player = this.player;
            player.packetSender.sendGameMessage("You have been defeated!");
        }
        this.player.eq();
        player = this.player;
        player.packetSender.sendEntityHintIcon(10, -1);
        this.player.getDuelController().resetDuel(false);
        this.player.getDuelArenaLocationManager();
        this.player.moveTo(DuelArenaLocationManager.randomExitPosition());
    }

    public final void moveToDuelArenaExit() {
        if (this.player.isInDuelArena()) {
            this.player.getDuelArenaLocationManager();
            this.player.moveTo(DuelArenaLocationManager.randomExitPosition());
        }
    }

    /*
     * Handled impossible loop by duplicating code
     * Enabled aggressive block sorting
     */
    public final void startDuel() {
        Position position;
        int n;
        block6: {
            int n2;
            Object object;
            block5: {
                n = GameUtil.randomInclusive(2);
                this.player.eq();
                this.opponent.eq();
                object = this.player;
                ((Player)object).packetSender.closeInterfaces();
                object = this.opponent;
                ((Player)object).packetSender.closeInterfaces();
                object = this.player;
                ((Player)object).packetSender.sendEntityHintIcon(10, this.opponent.getIndex());
                object = this.opponent;
                ((Player)object).packetSender.sendEntityHintIcon(10, this.player.getIndex());
                n2 = 0;
                while (n2 < this.equipmentToRemove.size()) {
                    this.player.getEquipmentManager().unequipSlot(this.player.getEquipmentManager().getContainer().indexOfItem(((ItemStack)this.equipmentToRemove.get(n2)).getId()));
                    ++n2;
                }
                n2 = 0;
                if (!true) break block5;
                object = this.opponent.getDuelSession();
                if (n2 >= ((DuelSession)object).equipmentToRemove.size()) break block6;
            }
            do {
                object = this.opponent.getDuelSession();
                this.opponent.getEquipmentManager().unequipSlot(this.opponent.getEquipmentManager().getContainer().indexOfItem(((ItemStack)((DuelSession)object).equipmentToRemove.get(n2)).getId()));
                ++n2;
                object = this.opponent.getDuelSession();
            } while (n2 < ((DuelSession)object).equipmentToRemove.size());
        }
        Position position2 = this.player.getDuelArenaLocationManager().randomStartPosition(DuelRule.OBSTACLES.isEnabledFor(this.player), n);
        this.player.moveTo(position2);
        if (DuelRule.NO_MOVEMENT.isEnabledFor(this.opponent)) {
            this.player.getDuelArenaLocationManager();
            position = DuelArenaLocationManager.findAdjacentOpenPosition(position2);
        } else {
            position = this.player.getDuelArenaLocationManager().randomStartPosition(DuelRule.OBSTACLES.isEnabledFor(this.opponent), n);
        }
        this.opponent.moveTo(position);
        this.startCountdown();
        this.opponent.getDuelSession().startCountdown();
        this.player.getDuelController().setAccepted(false);
    }

    private void startCountdown() {
        CycleEventHandler.getInstance().schedule(this.player, new DuelCountdownTask(this), 2);
    }

    public final void addStakeItem(ItemStack itemStack, int n) {
        Player player = this.player;
        if (player.interfaceAction != "duel" || !this.player.getInventoryManager().getContainer().containsItem(itemStack.getId()) || this.opponent == null) {
            return;
        }
        if (itemStack.getDefinition().isUntradeable()) {
            player = this.player;
            player.packetSender.sendGameMessage("You can't stake this item.");
            return;
        }
        if (!(this.stakedItems.size() < this.opponent.getInventoryManager().getContainer().getFreeSlots() || itemStack.getDefinition().isStackable() && this.hasStakedItemAmount(itemStack))) {
            player = this.player;
            player.packetSender.sendGameMessage("The opponent has no free spaces left for that.");
            return;
        }
        if (!ServerSettings.adminInteractionsAllowed && this.player.getPlayerRights() >= 2) {
            player = this.player;
            player.packetSender.sendGameMessage("This action is not allowed.");
            return;
        }
        if (!this.player.getInventoryManager().containsItemStack(itemStack)) {
            return;
        }
        int n2 = this.player.getInventoryManager().getItemAmount(itemStack.getId());
        if (!this.player.getInventoryManager().removeItemFromSlot(itemStack, n)) {
            return;
        }
        if (!itemStack.getDefinition().isStackable() && !itemStack.getDefinition().isNote()) {
            n = 0;
            while (n < itemStack.getAmount()) {
                if (n2 > 0) {
                    this.stakedItems.add(new ItemStack(itemStack.getId(), 1));
                    --n2;
                }
                ++n;
            }
        } else {
            n = 0;
            int n3 = 0;
            while (n3 < this.stakedItems.size()) {
                if (((ItemStack)this.stakedItems.get(n3)).getId() == itemStack.getId()) {
                    ((ItemStack)this.stakedItems.get(n3)).setAmount(((ItemStack)this.stakedItems.get(n3)).getAmount() + itemStack.getAmount());
                    n = 1;
                }
                ++n3;
            }
            if (n == 0) {
                this.stakedItems.add(new ItemStack(itemStack.getId(), itemStack.getAmount() > n2 ? n2 : itemStack.getAmount()));
            }
        }
        this.player.getDuelController().setAccepted(false);
        this.opponent.getDuelController().setAccepted(false);
        this.player.getDuelInterfaceManager().refreshAcceptStatus();
        this.opponent.getDuelInterfaceManager().refreshAcceptStatus();
        this.player.getDuelInterfaceManager().refreshStakeContainers();
        this.opponent.getDuelInterfaceManager().refreshStakeContainers();
    }

    public final void removeStakeItem(ItemStack itemStack) {
        Player player = this.player;
        if (player.interfaceAction != "duel" || this.stakedItems.size() <= 0 || !this.hasStakedItemAmount(itemStack) || this.opponent == null) {
            return;
        }
        if (!itemStack.getDefinition().isNote() && !itemStack.getDefinition().isStackable()) {
            int n = 0;
            while (n < itemStack.getAmount()) {
                boolean bl = false;
                int n2 = 0;
                while (n2 < this.stakedItems.size()) {
                    if (((ItemStack)this.stakedItems.get(n2)).getId() == itemStack.getId() && !bl) {
                        this.stakedItems.remove(n2);
                        this.player.getInventoryManager().addItem(new ItemStack(itemStack.getId()));
                        bl = true;
                    }
                    ++n2;
                }
                ++n;
            }
        } else {
            int n = 0;
            while (n < this.stakedItems.size()) {
                if (((ItemStack)this.stakedItems.get(n)).getId() == itemStack.getId()) {
                    if (itemStack.getAmount() >= ((ItemStack)this.stakedItems.get(n)).getAmount()) {
                        int n3 = ((ItemStack)this.stakedItems.get(n)).getAmount();
                        this.stakedItems.remove(n);
                        this.player.getInventoryManager().addItem(new ItemStack(itemStack.getId(), n3));
                    } else {
                        ((ItemStack)this.stakedItems.get(n)).setAmount(((ItemStack)this.stakedItems.get(n)).getAmount() - itemStack.getAmount());
                        this.player.getInventoryManager().addItem(new ItemStack(itemStack.getId(), itemStack.getAmount()));
                    }
                }
                ++n;
            }
        }
        this.player.getDuelController().setAccepted(false);
        this.opponent.getDuelController().setAccepted(false);
        this.player.getDuelInterfaceManager().refreshAcceptStatus();
        this.opponent.getDuelInterfaceManager().refreshAcceptStatus();
        this.player.getDuelInterfaceManager().refreshStakeContainers();
        this.opponent.getDuelInterfaceManager().refreshStakeContainers();
    }

    private boolean hasStakedItemAmount(ItemStack itemStack) {
        int n = 0;
        int n2 = 0;
        while (n2 < this.stakedItems.size()) {
            if (((ItemStack)this.stakedItems.get(n2)).getId() == itemStack.getId()) {
                ++n;
            }
            ++n2;
        }
        return n >= itemStack.getAmount();
    }

    public final boolean isStarted() {
        return this.started;
    }

    public final boolean isActiveDuelStarted() {
        Object object;
        block6: {
            block5: {
                if (!this.player.isInDuelArena()) break block5;
                object = this;
                if (((DuelSession)object).started) break block6;
            }
            object = this.player;
            ((Player)object).packetSender.sendGameMessage("The duel hasn't started yet!");
        }
        if (this.player.isInDuelArena()) {
            object = this;
            if (((DuelSession)object).started) {
                return true;
            }
        }
        return false;
    }

    public final void restoreHitpoints() {
        if (this.player.getSkillManager().getCurrentLevels()[3] < this.player.getSkillManager().getBaseLevel(3)) {
            this.player.getUpdateState().setGraphic(84);
            this.player.getUpdateState().setAnimation(866);
            Player player = this.player;
            player.packetSender.sendGameMessage("You have been healed.");
            this.player.getSkillManager().setCurrentLevel(3, this.player.getSkillManager().getBaseLevel(3));
            this.player.getSkillManager().refreshSkill(3);
            return;
        }
        Player player = this.player;
        player.packetSender.sendGameMessage("You are already very healthy.");
    }

    public final ArrayList getStakedItems() {
        return this.stakedItems;
    }

    public final Player getOpponent() {
        return this.opponent;
    }

    public final void setOpponent(Player player) {
        this.opponent = player;
    }

    public final ArrayList getEquipmentToRemove() {
        return this.equipmentToRemove;
    }

    public final boolean[] getEnabledRules() {
        return this.enabledRules;
    }

    public final ArrayList getRuleDescriptions() {
        return this.ruleDescriptions;
    }

    public final void setStarted(boolean bl) {
        this.started = bl;
    }

    static /* synthetic */ Player getPlayer(DuelSession duelSession) {
        return duelSession.player;
    }
}

