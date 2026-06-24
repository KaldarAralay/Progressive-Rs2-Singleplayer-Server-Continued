/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.bot;

import com.rs2.ServerSettings;
import com.rs2.model.Position;
import com.rs2.model.item.ItemStack;
import com.rs2.model.player.Player;
import com.rs2.model.quest.QuestDefinition;
import com.rs2.model.skill.herblore.PoisonedWeaponDefinition;
import com.rs2.model.skill.herblore.WeaponPoisonTask;
import com.rs2.model.task.CycleEventHandler;
import com.rs2.util.GameUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class BotRoute {
    public Position[] waypoints;

    public static boolean handleWeaponPoisoning(Player player, ItemStack itemStack, ItemStack itemStack2) {
        if (itemStack.getId() == 187 || itemStack2.getId() == 187) {
            BotRoute.startWeaponPoisonTask(player, 1, itemStack2.getId() == 187 ? itemStack.getId() : itemStack2.getId());
            return true;
        }
        if (itemStack.getId() == 5937 || itemStack2.getId() == 5937) {
            BotRoute.startWeaponPoisonTask(player, 2, itemStack2.getId() == 5937 ? itemStack.getId() : itemStack2.getId());
            return true;
        }
        if (itemStack.getId() == 5940 || itemStack2.getId() == 5940) {
            BotRoute.startWeaponPoisonTask(player, 3, itemStack2.getId() == 5940 ? itemStack.getId() : itemStack2.getId());
            return true;
        }
        return false;
    }

    private static void startWeaponPoisonTask(Player player, int n, int n2) {
        PoisonedWeaponDefinition poisonedWeaponDefinition = PoisonedWeaponDefinition.forUnpoisonedItemId(n2);
        if (poisonedWeaponDefinition == null) {
            return;
        }
        if (!ServerSettings.herbloreEnabled) {
            player.packetSender.sendGameMessage("This skill is currently disabled.");
            return;
        }
        if (player.getQuestState(29) != 1) {
            Object object = QuestDefinition.forId(29);
            object = ((QuestDefinition)object).getName();
            player.packetSender.sendGameMessage("You need to complete " + (String)object + " to do this.");
            return;
        }
        player.getUpdateState().setAnimation(1652);
        CycleEventHandler.getInstance().schedule(player, new WeaponPoisonTask(n, poisonedWeaponDefinition, player, n2), 2);
    }

    public BotRoute(Position[] positionArray) {
        this.waypoints = positionArray;
    }

    public BotRoute reversed() {
        ArrayList positionList = new ArrayList(Arrays.asList(this.waypoints));
        Collections.reverse(positionList);
        Position[] positionArray = (Position[])positionList.toArray(new Position[positionList.size()]);
        return new BotRoute(positionArray);
    }

    public int getDistance() {
        int n = 0;
        int n2 = 0;
        while (n2 < this.waypoints.length - 1) {
            Position position = this.waypoints[n2];
            Position position2 = this.waypoints[n2 + 1];
            n += GameUtil.getDistance(position, position2);
            ++n2;
        }
        return n;
    }

    public Position getStartPosition() {
        return this.waypoints[0];
    }

    public Position getEndPosition() {
        return this.waypoints[this.waypoints.length - 1];
    }
}

