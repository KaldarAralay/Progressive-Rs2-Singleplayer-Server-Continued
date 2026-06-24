/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.gameplay.fightcave;

import com.rs2.model.Entity;
import com.rs2.model.GameplayHelper;
import com.rs2.model.Position;
import com.rs2.model.World;
import com.rs2.model.combat.CombatManager;
import com.rs2.model.gameplay.PositionRange;
import com.rs2.model.gameplay.fightcave.FightCaveNpcLevelComparator;
import com.rs2.model.gameplay.fightcave.FightCaveSpawnTable;
import com.rs2.model.npc.Npc;
import com.rs2.model.npc.NpcMovementMode;
import com.rs2.model.player.Player;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

public final class FightCaveWaveSpawner {
    private static int[][] waveNpcIds = new int[][]{{2627}, {2627, 2627}, {2630}, {2630, 2627}, {2630, 2627, 2627}, {2630, 2630}, {2631}, {2631, 2627}, {2631, 2627, 2627}, {2631, 2630}, {2631, 2630, 2627}, {2631, 2630, 2627, 2627}, {2631, 2630, 2630}, {2631, 2631}, {2741}, {2741, 2627}, {2741, 2627, 2627}, {2741, 2630}, {2741, 2630, 2627}, {2741, 2630, 2627, 2627}, {2741, 2630, 2630}, {2741, 2631}, {2741, 2631, 2627}, {2741, 2631, 2627, 2627}, {2741, 2631, 2630}, {2741, 2631, 2630, 2627}, {2741, 2631, 2630, 2627, 2627}, {2741, 2631, 2630, 2630}, {2741, 2631, 2631}, {2741, 2741}, {2743}, {2743, 2627}, {2743, 2627, 2627}, {2743, 2630}, {2743, 2630, 2627}, {2743, 2630, 2627, 2627}, {2743, 2630, 2630}, {2743, 2631}, {2743, 2631, 2627}, {2743, 2631, 2627, 2627}, {2743, 2631, 2630}, {2743, 2631, 2630, 2627}, {2743, 2631, 2630, 2627, 2627}, {2743, 2631, 2630, 2630}, {2743, 2631, 2631}, {2743, 2741}, {2743, 2741, 2627}, {2743, 2741, 2627, 2627}, {2743, 2741, 2630}, {2743, 2741, 2630, 2627}, {2743, 2741, 2630, 2627, 2627}, {2743, 2741, 2630, 2630}, {2743, 2741, 2631}, {2743, 2741, 2631, 2627}, {2743, 2741, 2631, 2627, 2627}, {2743, 2741, 2631, 2630}, {2743, 2741, 2631, 2630, 2627}, {2743, 2741, 2631, 2630, 2627, 2627}, {2743, 2741, 2631, 2630, 2630}, {2743, 2741, 2631, 2631}, {2743, 2741, 2741}, {2743, 2744}, {2745}};

    public static void spawnWave(Player player, int n) {
        Object object;
        int n2 = n + 1;
        Object object2 = player;
        ((Player)object2).packetSender.sendGameMessage("Wave " + n2 + "/" + 63);
        player.clearFightCaveNpcs();
        object2 = new ArrayList();
        int[] nArray = waveNpcIds[n];
        int n3 = nArray.length;
        int n4 = 0;
        while (n4 < n3) {
            n = nArray[n4];
            object = new Npc(n);
            ((ArrayList)object2).add(object);
            player.addFightCaveNpc((Npc)object);
            ++n4;
        }
        Collections.sort((ArrayList)object2, new FightCaveNpcLevelComparator());
        n = 0;
        Iterator iterator = ((ArrayList)object2).iterator();
        while (iterator.hasNext()) {
            Npc npc = (Npc)iterator.next();
            int n5 = player.fightCaveSpawnRotation + n;
            object = FightCaveSpawnTable.spawnAreaRotation[n5];
            int n6 = player.getPosition().getPlane();
            ((PositionRange)object).setPlane(n6);
            Object var3_5 = null;
            boolean bl = false;
            Position position = GameplayHelper.randomUnblockedPositionInRange((PositionRange)object);
            object2 = player;
            npc.setForcedCombatTarget((Entity)object2);
            npc.moveTo(position);
            npc.setMovementMode(NpcMovementMode.STATIONARY);
            npc.setSpawnX(position.getX());
            npc.setSpawnY(position.getY());
            npc.setRespawnEnabled(false);
            World.registerNpc(npc);
            if (object2 != null) {
                npc.setMovementTarget((Entity)object2);
                CombatManager.startCombat(npc, (Entity)object2);
                npc.getUpdateState().setFacePosition(((Entity)object2).getPosition());
            }
            ((Entity)object2).isPlayer();
            if (npc.getPosition().getPlane() != ((Entity)object2).getPosition().getPlane()) {
                System.out.println("FIGHT CAVE! " + npc.getPosition().getPlane() + " " + ((Entity)object2).getPosition().getPlane());
            }
            if (n2 == 63) {
                player.getDialogueManager().setDialogueNpcId(2617);
                player.getDialogueManager().showNpcOneLineDialogue("Look out, here comes TzTok-Jad!", 591);
                player.getDialogueManager().finishDialogue();
            }
            int n7 = player.fightCaveSpawnRotation + ++n;
            if (n7 < 15) continue;
            n -= 15;
        }
        ++player.fightCaveSpawnRotation;
        if (player.fightCaveSpawnRotation >= 15) {
            player.fightCaveSpawnRotation = 0;
        }
        object2 = player;
        ((Player)object2).packetSender.sendGameMessage("Enemies to kill: " + player.getFightCaveNpcs().size());
    }
}

