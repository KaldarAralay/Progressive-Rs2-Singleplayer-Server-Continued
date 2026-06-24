/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.bot;

import com.rs2.ServerSettings;
import com.rs2.bot.ClanWarsBotCombatTickTask;
import com.rs2.bot.combat.BotCombatEscapeHandler;
import com.rs2.bot.combat.BotCombatHelper;
import com.rs2.bot.combat.BotCombatLoadoutManager;
import com.rs2.bot.combat.BotCombatLoadoutTables;
import com.rs2.model.Entity;
import com.rs2.model.GameplayHelper;
import com.rs2.model.Position;
import com.rs2.model.World;
import com.rs2.model.item.ItemDefinition;
import com.rs2.model.item.ItemStack;
import com.rs2.model.path.PathReachability;
import com.rs2.model.player.Player;
import com.rs2.model.skill.SkillManager;
import com.rs2.model.task.TickTask;
import com.rs2.util.GameUtil;
import com.rs2.util.RectangularArea;
import com.rs2.util.path.WalkingCollisionMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class ClanWarsBotManager {
    public static boolean clanWarsEventActive = false;
    public static boolean clanWarsRetreatActive = false;
    public static int clanWarsBaseCombatLevel = 35;
    public static int clanWarsSupplyMultiplier = 5;
    private static int clanWarsTeamOneCapeId = -1;
    private static int clanWarsTeamTwoCapeId = -1;
    public static String clanWarsTeamOneTag = "";
    public static String clanWarsTeamTwoTag = "";
    private static List clanWarsTeamTagPool = Arrays.asList("RoT", "LiT");
    private static RectangularArea[] clanWarsTeamSpawnAreas = new RectangularArea[]{new RectangularArea(3198, 3885, 3208, 3895), new RectangularArea(3302, 3891, 3312, 3901)};
    public static Position clanWarsRallyPosition = new Position(3255, 3893, 0);
    public static ArrayList<Player> clanWarsTeamOneBots = new ArrayList<Player>();
    public static ArrayList<Player> clanWarsTeamTwoBots = new ArrayList<Player>();

    private static void prepareClanWarsCombatant(Player player) {
        int n;
        int n2;
        int n3;
        int n4;
        Player player2;
        GameplayHelper.resetBotTaskState(player);
        if (ServerSettings.wildyBotsUseNewGeneration) {
            player2 = player;
            GameplayHelper.resetBotSkillsToBase(player2);
            n4 = ServerSettings.wildyBotsBaseCombatLevel - ServerSettings.wildyBotsCombatLevelSpread;
            n3 = n4 + GameUtil.randomInt((ServerSettings.wildyBotsCombatLevelSpread << 1) + 1);
            if (n3 < 3) {
                n3 = 3;
            }
            if (n3 > SkillManager.maxCombatLevel) {
                n3 = SkillManager.maxCombatLevel;
            }
            while (player2.getCombatLevel() < n3) {
                int[] nArray = new int[6];
                nArray[1] = 2;
                nArray[2] = 1;
                nArray[3] = 4;
                nArray[4] = 6;
                nArray[5] = 5;
                int[] nArray2 = nArray;
                n2 = nArray[GameUtil.randomInt(6)];
                player2.getSkillManager();
                n = SkillManager.getLevelForExperience(player2.getSkillManager().getExperience()[n2]);
                BotCombatHelper.setBotSkillLevel(player2, n2, n + 1);
                player2.getSkillManager().getExperience()[3] = BotCombatHelper.calculateBotHitpointsExperience(player2);
                int[] nArray3 = player2.getSkillManager().getCurrentLevels();
                player2.getSkillManager();
                nArray3[3] = SkillManager.getLevelForExperience(player2.getSkillManager().getExperience()[3]);
            }
            player2.getSkillManager().refreshAllSkills();
            BotCombatLoadoutManager.selectCombatStyleFromStats(player2, false);
        } else {
            player2 = player;
            GameplayHelper.resetBotSkillsToBase(player2);
            n4 = clanWarsBaseCombatLevel + GameUtil.randomInt(6);
            n3 = n4 / 5 << 1;
            if (n3 == 0) {
                n3 = 2;
            }
            BotCombatHelper.setBotSkillLevel(player2, 0, n4 - n4 / 5 + GameUtil.randomInt(n3));
            n3 = n4 / 5 << 1;
            if (n3 == 0) {
                n3 = 2;
            }
            BotCombatHelper.setBotSkillLevel(player2, 2, n4 - n4 / 5 + GameUtil.randomInt(n3));
            n3 = n4 / 5 << 1;
            if (n3 == 0) {
                n3 = 2;
            }
            BotCombatHelper.setBotSkillLevel(player2, 1, n4 - n4 / 5 + GameUtil.randomInt(n3));
            n3 = n4 / 5 << 1;
            if (n3 == 0) {
                n3 = 2;
            }
            BotCombatHelper.setBotSkillLevel(player2, 4, n4 - n4 / 5 + GameUtil.randomInt(n3));
            n3 = n4 / 5 << 1;
            if (n3 == 0) {
                n3 = 2;
            }
            BotCombatHelper.setBotSkillLevel(player2, 6, n4 - n4 / 5 + GameUtil.randomInt(n3));
            n3 = n4 / 5 << 1;
            if (n3 == 0) {
                n3 = 2;
            }
            BotCombatHelper.setBotSkillLevel(player2, 5, n4 - (n4 / 5 << 1) + GameUtil.randomInt(n3));
            player2.getSkillManager().getExperience()[3] = BotCombatHelper.calculateBotHitpointsExperience(player2);
            int[] nArray = player2.getSkillManager().getCurrentLevels();
            player2.getSkillManager();
            nArray[3] = SkillManager.getLevelForExperience(player2.getSkillManager().getExperience()[3]);
            player2.getSkillManager().refreshAllSkills();
            BotCombatLoadoutManager.selectCombatStyleFromStats(player2, false);
        }
        player2 = player;
        BotCombatLoadoutManager.prepareCombatLoadout(player2, true);
        if (player2.clanWarsTeamId == 1) {
            player2.getEquipmentManager().getContainer().setItem(1, new ItemStack(clanWarsTeamOneCapeId));
        } else {
            player2.getEquipmentManager().getContainer().setItem(1, new ItemStack(clanWarsTeamTwoCapeId));
        }
        player2 = player;
        RectangularArea object = clanWarsTeamOneBots.contains(player2) ? clanWarsTeamSpawnAreas[0] : clanWarsTeamSpawnAreas[1];
        int n5 = 0;
        n2 = 0;
        n = 1;
        while (n != 0) {
            RectangularArea rectangularArea = object;
            int n6 = rectangularArea.getMinX();
            n5 = rectangularArea.getMinY();
            n2 = rectangularArea.getMaxX();
            int n7 = rectangularArea.getMaxY();
            n2 -= n6;
            n7 -= n5;
            Position position = new Position(n6 += GameUtil.randomInt(n2), n7 = n5 + GameUtil.randomInt(n7));
            n5 = position.getX();
            n = WalkingCollisionMap.getTileFlags(n5, n2 = position.getY(), 0) != 0 ? 1 : 0;
        }
        player.moveTo(new Position(n5, n2, 0));
        Player player3 = player;
        World.getTaskScheduler().schedule(new ClanWarsBotCombatTickTask(3, player3));
    }

    public static void processClanWarsBotEvent() {
        boolean bl;
        if (!clanWarsRetreatActive) {
            bl = false;
            for (Player player : clanWarsTeamOneBots) {
                int n = GameUtil.getDistance(player.getPosition(), clanWarsRallyPosition);
                if ((!player.isInWilderness() || player.botCombatEscapeActive) && n >= 20) continue;
                bl = true;
                break;
            }
            boolean bl2 = false;
            for (Object object : clanWarsTeamTwoBots) {
                int n = GameUtil.getDistance(((Entity)object).getPosition(), clanWarsRallyPosition);
                if ((!((Entity)object).isInWilderness() || ((Player)object).botCombatEscapeActive) && n >= 20) continue;
                bl2 = true;
                break;
            }
            if (!bl || !bl2) {
                if (bl) {
                    for (Object object : clanWarsTeamOneBots) {
                        if (!((Entity)object).isInWilderness()) continue;
                        ((Player)object).queuePublicChatMessage("#" + clanWarsTeamOneTag);
                        BotCombatEscapeHandler.tryStartBotCombatEscape((Player)object);
                    }
                } else {
                    for (Object object : clanWarsTeamTwoBots) {
                        if (!((Entity)object).isInWilderness()) continue;
                        ((Player)object).queuePublicChatMessage("#" + clanWarsTeamTwoTag);
                        BotCombatEscapeHandler.tryStartBotCombatEscape((Player)object);
                    }
                }
                clanWarsRetreatActive = true;
            }
        }
        bl = true;
        for (Player player : clanWarsTeamOneBots) {
            if (!player.isInWilderness()) continue;
            bl = false;
            break;
        }
        if (bl) {
            for (Player player : clanWarsTeamTwoBots) {
                if (!player.isInWilderness()) continue;
                bl = false;
                break;
            }
        }
        if (bl) {
            clanWarsEventActive = false;
            clanWarsRetreatActive = false;
        }
    }

    public static void hideClanWarsBot(Player player) {
        player.moveTo(new Position(9999, 9999, 0));
    }

    public static Player findClanWarsOpponent(Player player) {
        Player player2;
        ArrayList<Player> arrayList = new ArrayList<Player>();
        int n = clanWarsTeamOneBots.contains(player) ? clanWarsTeamTwoCapeId : clanWarsTeamOneCapeId;
        Player[] playerArray = World.players;
        int n2 = World.players.length;
        int n3 = 0;
        while (n3 < n2) {
            player2 = playerArray[n3];
            if (player2 != null && player2 != player && player2.getPosition().isWithinViewport(player.getPosition())) {
                int n4;
                if (PathReachability.isReachable(player, player2.getPosition().getX(), player2.getPosition().getY(), true, 0, 0) && (n4 = player2.getEquipmentManager().getItemIdAtSlot(1)) == n) {
                    arrayList.add(player2);
                }
            }
            ++n3;
        }
        if (arrayList.size() > 0) {
            Collections.shuffle(arrayList);
            player2 = (Player)arrayList.get(0);
            return player2;
        }
        return null;
    }

    public static void startClanWarsCombatants() {
        for (Player player : clanWarsTeamOneBots) {
            ClanWarsBotManager.prepareClanWarsCombatant(player);
        }
        for (Player player : clanWarsTeamTwoBots) {
            ClanWarsBotManager.prepareClanWarsCombatant(player);
        }
    }

    public static void chooseClanWarsTeamCapes() {
        ArrayList<Integer> arrayList = new ArrayList<Integer>();
        if (!BotCombatHelper.isFreeToPlayWorld() && ItemDefinition.isDefined(4413)) {
            int[] nArray = BotCombatLoadoutTables.castleWarsCapeIds;
            int n = 0;
            while (n < 55) {
                int n2 = nArray[n];
                arrayList.add(n2);
                ++n;
            }
        } else {
            int[] nArray = BotCombatLoadoutTables.capeIds;
            int n = 0;
            while (n < 7) {
                int n3 = nArray[n];
                arrayList.add(n3);
                ++n;
            }
        }
        Collections.shuffle(arrayList);
        clanWarsTeamOneCapeId = (Integer)arrayList.get(0);
        clanWarsTeamTwoCapeId = (Integer)arrayList.get(1);
    }

    public static void chooseClanWarsTeamTags() {
        Collections.shuffle(clanWarsTeamTagPool);
        clanWarsTeamOneTag = (String)clanWarsTeamTagPool.get(0);
        clanWarsTeamTwoTag = (String)clanWarsTeamTagPool.get(1);
    }
}

