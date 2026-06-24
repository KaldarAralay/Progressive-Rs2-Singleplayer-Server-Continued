/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.bot.tasks;

import com.rs2.bot.BotRoute;
import com.rs2.bot.BotTaskDefinition;
import com.rs2.bot.combat.BotCombatHelper;
import com.rs2.model.GameplayHelper;
import com.rs2.model.Position;
import com.rs2.model.item.ItemStack;
import com.rs2.model.player.Player;
import com.rs2.model.skill.ItemCombinationHandler;
import com.rs2.model.skill.SkillManager;
import com.rs2.model.skill.mining.MineableRockDefinition;
import com.rs2.util.GameUtil;
import java.util.ArrayList;

public final class VarrockEastMineBotTask
extends BotTaskDefinition {
    private static Position routeStartPosition = new Position(3254, 3420, 0);
    private static BotRoute taskRoute = new BotRoute(new Position[]{new Position(3254, 3428, 0), new Position(3274, 3426, 0), new Position(3288, 3411, 0), new Position(3289, 3391, 0), new Position(3289, 3374, 0)});

    public VarrockEastMineBotTask(int n) {
        super(routeStartPosition, taskRoute, 0, false, 8);
    }

    @Override
    public final ArrayList getRequiredItems(Player player) {
        ArrayList<ItemStack> arrayList = new ArrayList<ItemStack>();
        arrayList.add(new ItemStack(ItemCombinationHandler.getOwnedOrFallbackGatheringTool(player, 14).getToolItemId(), 1));
        return arrayList;
    }

    @Override
    public final boolean isWithinProgressionRange(Player player) {
        return player.getSkillManager().getCurrentLevels()[13] < 30;
    }

    @Override
    public final void configureTaskInteractionTargets(Player player) {
        int n;
        int n2;
        int n3;
        int[] nArray;
        player.botInteractionTargetIds.clear();
        if (player.botMode != 4) {
            int n4;
            int n5;
            int n6;
            int[] nArray2;
            if (player.getSkillManager().getCurrentLevels()[14] >= 15 && GameUtil.randomInt(4) == 0) {
                int[] nArray3 = MineableRockDefinition.collectObjectIds(new MineableRockDefinition[]{MineableRockDefinition.IRON});
                int n7 = nArray3.length;
                int n8 = 0;
                while (n8 < n7) {
                    int n9 = nArray3[n8];
                    player.botInteractionTargetIds.add(n9);
                    ++n8;
                }
                return;
            }
            if (player.getSkillManager().getCurrentLevels()[14] >= 15 && GameUtil.randomInt(3) == 0) {
                nArray2 = MineableRockDefinition.collectObjectIds(new MineableRockDefinition[]{MineableRockDefinition.IRON});
                n6 = nArray2.length;
                n5 = 0;
                while (n5 < n6) {
                    n4 = nArray2[n5];
                    player.botInteractionTargetIds.add(n4);
                    ++n5;
                }
            }
            nArray2 = MineableRockDefinition.collectObjectIds(new MineableRockDefinition[]{MineableRockDefinition.COPPER, MineableRockDefinition.TIN});
            n6 = nArray2.length;
            n5 = 0;
            while (n5 < n6) {
                n4 = nArray2[n5];
                player.botInteractionTargetIds.add(n4);
                ++n5;
            }
            return;
        }
        if (player.getSkillManager().getCurrentLevels()[14] >= 15 && player.getSkillManager().getCurrentLevels()[13] >= 15) {
            int[] nArray4 = MineableRockDefinition.collectObjectIds(new MineableRockDefinition[]{MineableRockDefinition.IRON});
            int n10 = nArray4.length;
            int n11 = 0;
            while (n11 < n10) {
                int n12 = nArray4[n11];
                player.botInteractionTargetIds.add(n12);
                ++n11;
            }
            return;
        }
        if (player.getSkillManager().getCurrentLevels()[14] >= 15) {
            nArray = MineableRockDefinition.collectObjectIds(new MineableRockDefinition[]{MineableRockDefinition.IRON});
            n3 = nArray.length;
            n2 = 0;
            while (n2 < n3) {
                n = nArray[n2];
                player.botInteractionTargetIds.add(n);
                ++n2;
            }
        }
        nArray = MineableRockDefinition.collectObjectIds(new MineableRockDefinition[]{MineableRockDefinition.COPPER, MineableRockDefinition.TIN});
        n3 = nArray.length;
        n2 = 0;
        while (n2 < n3) {
            n = nArray[n2];
            player.botInteractionTargetIds.add(n);
            ++n2;
        }
    }

    @Override
    public final void prepareTaskInventory(Player player) {
        player.getInventoryManager().getContainer().clear();
        player.getEquipmentManager().getContainer().clear();
        if (player.getSkillManager().getCurrentLevels()[14] >= 41 && player.getSkillManager().getCurrentLevels()[0] >= 40) {
            player.getEquipmentManager().getContainer().setItem(3, new ItemStack(1275));
        } else if (player.getSkillManager().getCurrentLevels()[14] >= 31 && player.getSkillManager().getCurrentLevels()[0] >= 30) {
            player.getEquipmentManager().getContainer().setItem(3, new ItemStack(1271));
        } else if (player.getSkillManager().getCurrentLevels()[14] >= 21 && player.getSkillManager().getCurrentLevels()[0] >= 20) {
            player.getEquipmentManager().getContainer().setItem(3, new ItemStack(1273));
        } else if (player.getSkillManager().getCurrentLevels()[14] >= 6 && player.getSkillManager().getCurrentLevels()[0] >= 5) {
            player.getEquipmentManager().getContainer().setItem(3, new ItemStack(1269));
        } else {
            player.getEquipmentManager().getContainer().setItem(3, new ItemStack(GameUtil.randomInt(2) == 0 ? 1267 : 1265));
        }
        player.getInventoryManager().refresh();
        player.getEquipmentManager().refresh();
    }

    @Override
    public final void prepareTaskCombatLoadout(Player player) {
        GameplayHelper.resetBotSkillsToBase(player);
        int n = 1 + GameUtil.randomInt(99);
        int n2 = n / 5 << 1;
        if (n2 == 0) {
            n2 = 2;
        }
        BotCombatHelper.setBotSkillLevel(player, 0, n - n / 5 + GameUtil.randomInt(n2));
        n2 = n / 5 << 1;
        if (n2 == 0) {
            n2 = 2;
        }
        BotCombatHelper.setBotSkillLevel(player, 2, n - n / 5 + GameUtil.randomInt(n2));
        n2 = n / 5 << 1;
        if (n2 == 0) {
            n2 = 2;
        }
        BotCombatHelper.setBotSkillLevel(player, 1, n - n / 5 + GameUtil.randomInt(n2));
        n2 = n / 5 << 1;
        if (n2 == 0) {
            n2 = 2;
        }
        BotCombatHelper.setBotSkillLevel(player, 4, n - n / 5 + GameUtil.randomInt(n2));
        n2 = n / 5 << 1;
        if (n2 == 0) {
            n2 = 2;
        }
        BotCombatHelper.setBotSkillLevel(player, 6, n - n / 5 + GameUtil.randomInt(n2));
        n2 = n / 5 << 1;
        if (n2 == 0) {
            n2 = 2;
        }
        BotCombatHelper.setBotSkillLevel(player, 5, n - (n / 5 << 1) + GameUtil.randomInt(n2));
        n2 = n / 5 << 1;
        if (n2 == 0) {
            n2 = 2;
        }
        BotCombatHelper.setBotSkillLevel(player, 14, n - n / 5 + GameUtil.randomInt(n2));
        player.getSkillManager().getExperience()[3] = BotCombatHelper.calculateBotHitpointsExperience(player);
        int[] nArray = player.getSkillManager().getCurrentLevels();
        player.getSkillManager();
        nArray[3] = SkillManager.getLevelForExperience(player.getSkillManager().getExperience()[3]);
        player.getSkillManager().refreshAllSkills();
    }
}

