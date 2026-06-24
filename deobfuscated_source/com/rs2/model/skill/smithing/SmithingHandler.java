/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.smithing;

import com.rs2.ServerSettings;
import com.rs2.model.combat.attack.CombatAttack;
import com.rs2.model.combat.attack.CombatAttackState;
import com.rs2.model.item.ItemDefinition;
import com.rs2.model.item.ItemStack;
import com.rs2.model.player.Player;
import com.rs2.model.skill.SkillActionHelper;
import com.rs2.model.skill.smithing.SmithableItemDefinition;
import com.rs2.model.skill.smithing.SmithingBarDefinition;
import com.rs2.model.skill.smithing.SmithingTask;
import com.rs2.model.task.CycleEventHandler;

public class SmithingHandler {
    private CombatAttack attack;
    private CombatAttackState attackState;

    public static void openSmithingInterface(Player player, int n) {
        Object object;
        if (!ServerSettings.smithingEnabled) {
            Player player2 = player;
            player2.packetSender.sendGameMessage("This skill is currently disabled.");
            return;
        }
        if (!player.getInventoryManager().containsItem(2347)) {
            player.getDialogueManager().showOneLineStatement("You need a hammer to start smithing.");
            if (player.botEnabled) {
                player.currentBotTask.startWalkToBank(player);
            }
            return;
        }
        Object object2 = SmithingBarDefinition.forBarItemId(n);
        if (object2 == null) {
            return;
        }
        player.setSelectedSmithingBarDefinition((SmithingBarDefinition)object2);
        if (!SkillActionHelper.checkSkillRequirement(player, 13, ((SmithingBarDefinition)object2).getRequiredLevel(), "smith this bar")) {
            player.nextActionSequence();
            player.resetAnimation();
            if (player.botEnabled) {
                player.currentBotTask.startWalkToBank(player);
            }
            return;
        }
        player.setSelectedSmithingBarItemId(n);
        if (player.botEnabled) {
            int n2;
            Player player3 = player;
            SmithingBarDefinition smithingBarDefinition = player3.getSelectedSmithingBarDefinition();
            if (smithingBarDefinition == null) {
                n2 = -1;
            } else {
                int n3 = -1;
                int n4 = -1;
                int n5 = 0;
                while (n5 < smithingBarDefinition.getSmithableItems().length) {
                    SmithableItemDefinition smithableItemDefinition = smithingBarDefinition.getSmithableItems()[n5];
                    if (smithableItemDefinition != null) {
                        ItemStack itemStack = new ItemStack(smithableItemDefinition.getProductItemId());
                        if (ItemDefinition.isDefined(smithableItemDefinition.getProductItemId()) && (!itemStack.getDefinition().isMembersOnly() || player3.isMember() && !ServerSettings.freeToPlayWorld)) {
                            int n6 = smithableItemDefinition.getRequiredLevel();
                            if (player3.getSkillManager().getCurrentLevels()[13] >= n6) {
                                object2 = new ItemStack(player3.getSelectedSmithingBarItemId(), smithableItemDefinition.getBarCount());
                                if (player3.getInventoryManager().containsItemStack((ItemStack)object2) && n6 > n3) {
                                    n3 = n6;
                                    n4 = smithableItemDefinition.getProductItemId();
                                }
                            }
                        }
                    }
                    ++n5;
                }
                n2 = player.botSmithingProductItemId = n4;
            }
            if (player.botSmithingProductItemId != -1) {
                SmithingHandler.startSmithingTask(player, player.botSmithingProductItemId, 27);
                return;
            }
            player.botTaskReturnToBankRequested = true;
            player.currentBotTask.startWalkToBank(player);
            return;
        }
        ItemStack[] itemStackArray = new ItemStack[((SmithingBarDefinition)object2).getSmithableItems().length];
        int n7 = 0;
        while (n7 < ((SmithingBarDefinition)object2).getSmithableItems().length) {
            int n8;
            object = ((SmithingBarDefinition)object2).getSmithableItems()[n7];
            String string = "";
            Object object3 = "";
            int n9 = 1;
            if (object != null && ItemDefinition.isDefined(n8 = ((SmithableItemDefinition)((Object)object)).getProductItemId())) {
                Object object4 = object;
                n9 = n;
                object3 = player;
                string = "";
                int n10 = ((SmithableItemDefinition)((Object)object4)).getBarCount();
                if (((Player)object3).getInventoryManager().containsItemAmount(n9, n10)) {
                    string = "@gre@";
                }
                String string2 = String.valueOf(string) + n10 + "bar";
                if (n10 > 1) {
                    string2 = String.valueOf(string2) + "s";
                }
                string = string2;
                Object object5 = object;
                object3 = player;
                int n11 = ((SmithableItemDefinition)((Object)object5)).getRequiredLevel();
                string2 = ((SmithableItemDefinition)((Object)object5)).getDisplayName();
                object3 = ((Player)object3).getQuestState(0) != 1 && !string2.equals("Dagger") ? string2 : (((Player)object3).getSkillManager().getCurrentLevels()[13] >= n11 ? "@whi@" + string2 : string2);
                n9 = ((SmithableItemDefinition)((Object)object)).getOutputAmount();
                itemStackArray[n7] = new ItemStack(n8, n9);
            }
            object = player;
            ((Player)object).packetSender.sendInterfaceText(string, SmithingBarDefinition.barRequirementTextIds[n7]);
            object = player;
            ((Player)object).packetSender.sendInterfaceText((String)object3, SmithingBarDefinition.productNameTextIds[n7]);
            object = player;
            ((Player)object).packetSender.sendInterfaceSlotItem(itemStackArray[n7], SmithingBarDefinition.productItemSlots[n7], SmithingBarDefinition.productItemInterfaceIds[n7], n9);
            ++n7;
        }
        object = player;
        ((Player)object).packetSender.showInterface(994);
    }

    public static void startSmithingTask(Player player, int n, int n2) {
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

    public SmithingHandler(CombatAttack combatAttack, CombatAttackState combatAttackState) {
        this.attack = combatAttack;
        this.attackState = combatAttackState;
    }

    public CombatAttack getAttack() {
        return this.attack;
    }

    public CombatAttackState getState() {
        return this.attackState;
    }
}

