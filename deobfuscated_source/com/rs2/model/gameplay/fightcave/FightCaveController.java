/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.gameplay.fightcave;

import com.rs2.ServerSettings;
import com.rs2.model.Position;
import com.rs2.model.World;
import com.rs2.model.gameplay.fightcave.FightCaveCompletionTask;
import com.rs2.model.gameplay.fightcave.FightCaveWaveSpawner;
import com.rs2.model.item.ItemStack;
import com.rs2.model.npc.Npc;
import com.rs2.model.player.Player;
import com.rs2.util.GameUtil;

public final class FightCaveController {
    private Player player;
    private Position exitPosition = new Position(2438, 5168, 0);

    public FightCaveController(Player player) {
        this.player = player;
    }

    public final void handleDeath() {
        this.player.eq();
        this.leaveFightCave();
    }

    public final void cleanupIfInFightCave() {
        if (this.player.isInFightCave()) {
            this.leaveFightCave();
        }
    }

    public final void leaveFightCave() {
        this.player.moveTo(this.exitPosition);
        Object object2 = this;
        boolean bl = ((FightCaveController)object2).player.fightCaveWaveIndex == 63;
        if (bl) {
            --((FightCaveController)object2).player.fightCaveWaveIndex;
        }
        double d = ((FightCaveController)object2).player.fightCaveWaveIndex + 1;
        int n = (int)(2.0 * (d / 2.0 * (d + 1.0)));
        if (bl) {
            ((FightCaveController)object2).player.getInventoryManager().addOrDropItem(new ItemStack(6570, 1));
            n += 4000;
        }
        if (n > 0) {
            ((FightCaveController)object2).player.getInventoryManager().addOrDropItem(new ItemStack(6529, n));
        }
        for (Object fightCaveNpcObject : this.player.getFightCaveNpcs()) {
            Npc npc = (Npc)fightCaveNpcObject;
            npc.setActive(false);
            World.unregisterNpc(npc);
        }
    }

    public final void startNextWave() {
        ++this.player.fightCaveWaveIndex;
        FightCaveWaveSpawner.spawnWave(this.player, this.player.fightCaveWaveIndex);
    }

    public final void completeFightCave() {
        this.player.getUpdateState().setAnimation(862);
        FightCaveCompletionTask fightCaveCompletionTask = new FightCaveCompletionTask(this, 3);
        World.getTaskScheduler().schedule(fightCaveCompletionTask);
    }

    public final void startFightCave() {
        this.player.fightCaveWaveIndex = ServerSettings.fightCaveStartWaveZeroBased;
        this.player.fightCaveSpawnRotation = GameUtil.randomInt(15);
        this.player.moveToInstancedPosition(new Position(2411, 5114, 0), true);
        this.player.getDialogueManager().setDialogueNpcId(2617);
        this.player.getDialogueManager().showNpcTwoLineDialogue("You're on your own now JalYt, prepare to fight for", "your life!", 591);
        this.player.getDialogueManager().finishDialogue();
        FightCaveWaveSpawner.spawnWave(this.player, this.player.fightCaveWaveIndex);
    }

    static /* synthetic */ Player getPlayer(FightCaveController fightCaveController) {
        return fightCaveController.player;
    }
}

