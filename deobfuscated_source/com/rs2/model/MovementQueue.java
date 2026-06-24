/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model;

import com.rs2.ServerSettings;
import com.rs2.model.Entity;
import com.rs2.model.EntityTargetMovement;
import com.rs2.model.GameplayHelper;
import com.rs2.model.MovementStep;
import com.rs2.model.Position;
import com.rs2.model.World;
import com.rs2.model.bankpin.BankPinManager;
import com.rs2.model.gameplay.CaveLightManager;
import com.rs2.model.gameplay.DesertHeatManager;
import com.rs2.model.gameplay.partyroom.PartyRoomManager;
import com.rs2.model.music.MusicManager;
import com.rs2.model.npc.Npc;
import com.rs2.model.player.Player;
import com.rs2.net.packet.PacketSender;
import com.rs2.util.GameUtil;
import java.util.Deque;
import java.util.LinkedList;

public final class MovementQueue {
    private final Entity entity;
    private Deque steps = new LinkedList();
    private Deque stepHistory = new LinkedList();
    private boolean running = false;
    private boolean runPath = false;
    private static byte[] directionDeltaX;
    private static byte[] directionDeltaY;
    private static int[][] collisionBypassTiles;

    static {
        byte[] byArray = new byte[8];
        byArray[0] = -1;
        byArray[2] = 1;
        byArray[3] = -1;
        byArray[4] = 1;
        byArray[5] = -1;
        byArray[7] = 1;
        directionDeltaX = byArray;
        byte[] byArray2 = new byte[8];
        byArray2[0] = 1;
        byArray2[1] = 1;
        byArray2[2] = 1;
        byArray2[5] = -1;
        byArray2[6] = -1;
        byArray2[7] = -1;
        directionDeltaY = byArray2;
        collisionBypassTiles = new int[][]{{2491, 10146}, {2491, 10147}, {2491, 10148}, {2491, 10162}, {2491, 10163}, {2491, 10164}, {2491, 10130}, {2491, 10131}, {2491, 10132}, {2809, 3437}, {2543, 10143}, {2545, 10141}, {2545, 10145}, {3225, 3238}};
    }

    public final Deque getSteps() {
        return this.steps;
    }

    public MovementQueue(Entity entity) {
        this.entity = entity;
    }

    public final void reset() {
        this.clear();
    }

    public final void process() {
        Object object;
        block26: {
            int n;
            int n2;
            MovementStep movementStep;
            block27: {
                Entity entity;
                Object object2;
                block28: {
                    boolean bl;
                    block24: {
                        Player player;
                        block25: {
                            if (this.entity.isDead() || !this.entity.getMovementDelayTimer().hasElapsed()) {
                                return;
                            }
                            movementStep = null;
                            if (this.stepHistory.size() >= 25) {
                                java.util.Iterator stepIterator = this.stepHistory.iterator();
                                while (stepIterator.hasNext() && this.stepHistory.size() >= 10) {
                                    stepIterator.next();
                                    stepIterator.remove();
                                }
                            }
                            if ((object2 = (MovementStep)this.steps.poll()) == null || ((MovementStep)object2).getDirection() == -1) {
                                object = this;
                            }
                            if (!this.entity.isPlayer()) break block24;
                            player = (Player)this.entity;
                            object = this;
                            if (((MovementQueue)object).running) break block25;
                            object = this;
                            if (!((MovementQueue)object).runPath) break block24;
                        }
                        if (player.getRunEnergyPercent() > 0) {
                            movementStep = (MovementStep)this.steps.poll();
                        }
                    }
                    boolean bl2 = bl = this.entity.isPlayer() && ((Player)this.entity).forcedMovementActive;
                    if (object2 != null && ((MovementStep)object2).getDirection() != -1) {
                        n2 = directionDeltaX[((MovementStep)object2).getDirection()];
                        n = directionDeltaY[((MovementStep)object2).getDirection()];
                        if (!bl && !this.canStep(n2, n)) {
                            this.clear();
                            return;
                        }
                        if (this.entity.isNpc()) {
                            entity = (Npc)this.entity;
                            ((Npc)this.entity).lastStepDeltaX = n2;
                            ((Npc)entity).lastStepDeltaY = n;
                        }
                        this.entity.getPosition().translate(n2, n);
                        this.entity.setWalkDirection(((MovementStep)object2).getDirection());
                        this.stepHistory.add(object2);
                    }
                    if (movementStep == null || movementStep.getDirection() == -1) break block26;
                    n2 = directionDeltaX[movementStep.getDirection()];
                    n = directionDeltaY[movementStep.getDirection()];
                    if (!bl && !this.canStep(n2, n)) {
                        this.clear();
                        return;
                    }
                    if (!this.entity.isPlayer()) break block27;
                    entity = (Player)this.entity;
                    object = this;
                    if (((MovementQueue)object).running) break block28;
                    object = this;
                    if (!((MovementQueue)object).runPath) break block27;
                }
                if (((Player)entity).getRunEnergyPercent() > 0) {
                    if (!ServerSettings.debugModeEnabled) {
                        if (((Player)entity).godModeEnabled || ((Player)entity).infiniteRunEnabled) {
                            ((Player)entity).setRunEnergyPercent(100);
                        } else {
                            object = object2 = entity;
                            int n3 = GameUtil.clampRunWeightForEnergyDrain(0, 64, (int)((Player)object).carriedWeight);
                            n3 = 67 + n3 * 67 / 64;
                            ((Player)entity).addRunEnergyRaw(-n3);
                        }
                    }
                    if (((Player)entity).getRunEnergyPercent() <= 0) {
                        this.setRunning(false);
                    }
                    object = entity;
                    ((Player)object).packetSender.sendRunEnergy();
                }
            }
            this.entity.getPosition().translate(n2, n);
            this.entity.setRunDirection(movementStep.getDirection());
            this.stepHistory.add(movementStep);
        }
        if (this.entity.isPlayer()) {
            Player player = (Player)this.entity;
            if (player.isInWilderness() && !player.aj) {
                player.aj = true;
            }
            if (this.entity.isWildernessCoordinate(this.entity.getPosition().getX(), this.entity.getPosition().getY()) && !player.aj) {
                player.aj = true;
                object = player;
                ((Player)object).packetSender.showInterface(1908);
                EntityTargetMovement.clearMovementTarget(player);
                this.clear();
            }
            GameplayHelper.refreshPlayerAreaOverlay(player);
            player.getQuestManager().handleMovementStep();
            DesertHeatManager.updateDesertHeatHazard(player);
            CaveLightManager.updateCaveLightHazards(player);
            Player player2 = player;
            if (player.currentBotRoute != null && !player.botRouteActionPending && !player.botRouteTravelPending) {
                player.continueBotRoute();
            }
            BankPinManager.a(player);
            new MusicManager().updateForPlayerPosition(player);
        }
    }

    public final void clear() {
        boolean bl = false;
        Object object = this;
        this.runPath = bl;
        this.steps.clear();
        object = this.entity.getPosition();
        this.steps.add(new MovementStep(this, ((Position)object).getX(), ((Position)object).getY(), -1));
    }

    public final void removeFirstStep() {
        if (this.steps.size() > 0) {
            this.steps.removeFirst();
        }
    }

    public final void addStep(Position position) {
        if (this.steps.size() == 0) {
            this.clear();
        }
        MovementStep movementStep = (MovementStep)this.steps.peekLast();
        int n = position.getX() - movementStep.getX();
        int n2 = position.getY() - movementStep.getY();
        int n3 = Math.max(Math.abs(n), Math.abs(n2));
        int n4 = 0;
        while (n4 < n3) {
            Object object;
            if (n < 0) {
                ++n;
            } else if (n > 0) {
                --n;
            }
            if (n2 < 0) {
                ++n2;
            } else if (n2 > 0) {
                --n2;
            }
            int n5 = position.getY() - n2;
            int n6 = position.getX() - n;
            MovementQueue movementQueue = this;
            int n7 = 100;
            if (movementQueue.entity.isPlayer()) {
                object = (Player)movementQueue.entity;
                if (((Player)object).botEnabled) {
                    n7 = 2000;
                }
            }
            if (movementQueue.steps.size() < n7) {
                object = (MovementStep)movementQueue.steps.peekLast();
                n7 = n6 - ((Position)object).getX();
                int n8 = n5 - ((Position)object).getY();
                if ((n7 = GameUtil.getDirectionForDelta(n7, n8)) >= 0) {
                    movementQueue.steps.add(new MovementStep(movementQueue, n6, n5, n7));
                }
            }
            ++n4;
        }
    }

    public final void setRunning(boolean bl) {
        this.running = bl;
        if (this.entity.isPlayer()) {
            Player player;
            Player player2 = player = (Player)this.entity;
            PacketSender cfr_ignored_0 = player.packetSender;
            player2 = player;
            player2.packetSender.sendConfig(173, this.running ? 1 : 0);
        }
    }

    public final boolean isRunning() {
        return this.running;
    }

    public final void setRunPath(boolean bl) {
        this.runPath = bl;
    }

    public final boolean isRunPath() {
        return this.runPath;
    }

    public final void clearMovementActions() {
        if (this.entity.isPlayer()) {
            Player player = (Player)this.entity;
            player.getAttributes().put("isBanking", Boolean.FALSE);
            player.getAttributes().put("isShopping", Boolean.FALSE);
            GameplayHelper.declineTrade(player);
            PartyRoomManager.returnStagedChestItems(player);
            if (player.getQuestState(0) == 1) {
                Player player2 = player;
                player2.packetSender.closeInterfaces();
            }
            player.queuedLevelUpSkillIds.clear();
            player.currentLevelUpSkillId = -1;
        }
        this.entity.getUpdateState().setFaceEntity(65535);
    }

    private boolean canStep(int n, int n2) {
        Entity entity;
        int n3;
        int n4;
        Entity[] entityArray;
        Object object;
        boolean bl = false;
        int[][] nArray = collisionBypassTiles;
        int n5 = 0;
        while (n5 < 14) {
            int[] bypassTile = nArray[n5];
            if (this.entity.getPosition().getX() + n == bypassTile[0] && this.entity.getPosition().getY() + n2 == bypassTile[1]) {
                bl = true;
                break;
            }
            ++n5;
        }
        if (!bl && !this.entity.canTravelBetween(this.entity.getPosition().getX(), this.entity.getPosition().getY(), this.entity.getPosition().getX() + n, this.entity.getPosition().getY() + n2, this.entity.getPosition().getPlane(), this.entity.getSize(), this.entity.getSize())) {
            return false;
        }
        if (this.entity.isNpc()) {
            boolean bl2;
            block14: {
                Npc npc = (Npc)this.entity;
                object = npc;
                int n6 = n2;
                n5 = n;
                Npc npc2 = npc;
                entityArray = World.getPlayers();
                n4 = entityArray.length;
                n3 = 0;
                while (n3 < n4) {
                    entity = entityArray[n3];
                    if (entity != null && !npc2.isDead() && !entity.isDead() && entity.getPosition().getPlane() == npc2.getPosition().getPlane() && GameUtil.isWithinDistance(npc2.getPosition().getX(), npc2.getPosition().getY(), entity.getPosition().getX(), entity.getPosition().getY(), entity.getSize() + npc2.getSize()) && Npc.wouldCollideWithPlayer(npc2, (Player)entity, n5, n6)) {
                        bl2 = true;
                        break block14;
                    }
                    ++n3;
                }
                bl2 = false;
            }
            if (bl2) {
                return false;
            }
            if (((Npc)object).wouldCollideWithNpc(n, n2)) {
                return false;
            }
        }
        if (this.entity.isPlayer()) {
            Player player = (Player)this.entity;
            object = player;
            if (!player.forcedMovementActive) {
                boolean bl3;
                block15: {
                    int n7 = n2;
                    n5 = n;
                    Object object2 = object;
                    entityArray = World.getNpcs();
                    n4 = entityArray.length;
                    n3 = 0;
                    while (n3 < n4) {
                        entity = entityArray[n3];
                        if (entity != null && !((Entity)object2).isDead() && !entity.isDead() && entity.getPosition().getPlane() == ((Entity)object2).getPosition().getPlane() && (((Npc)entity).getNpcId() == 1459 || ((Npc)entity).getNpcId() == 1461 || ((Npc)entity).getNpcId() == 1462) && GameUtil.isWithinDistance(((Entity)object2).getPosition().getX(), ((Entity)object2).getPosition().getY(), entity.getPosition().getX(), entity.getPosition().getY(), entity.getSize() + ((Entity)object2).getSize()) && Player.a((Player)object2, (Npc)entity, n5, n7)) {
                            bl3 = true;
                            break block15;
                        }
                        ++n3;
                    }
                    bl3 = false;
                }
                if (bl3) {
                    return false;
                }
            }
        }
        if (this.entity.getCombatTarget() != null) {
            return !EntityTargetMovement.canReachTarget(this.entity, this.entity.getCombatTarget());
        }
        return true;
    }
}

