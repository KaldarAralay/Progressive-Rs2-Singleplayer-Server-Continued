/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model;

import com.rs2.ServerSettings;
import com.rs2.model.Entity;
import com.rs2.model.Position;
import com.rs2.model.combat.CombatManager;
import com.rs2.model.npc.Npc;
import com.rs2.model.player.PetManager;
import com.rs2.model.player.Player;
import com.rs2.model.skill.GatheringToolDefinition;
import com.rs2.model.skill.ItemCombinationHandler;
import com.rs2.model.skill.mining.RuneEssenceMiningTask;
import com.rs2.model.skill.runecrafting.RunecraftingHandler;
import com.rs2.model.task.CycleEventHandler;
import com.rs2.util.GameUtil;
import com.rs2.util.path.PathFinder;

public class EntityTargetMovement {
    private Entity entity;

    public static void startRuneEssenceMining(Player player) {
        GatheringToolDefinition gatheringToolDefinition = ItemCombinationHandler.findUsableGatheringTool(player, 14);
        if (gatheringToolDefinition == null) {
            Player player2 = player;
            player2.packetSender.sendGameMessage("You do not have a pickaxe that you can use.");
            if (player.botEnabled) {
                player.currentBotTask.startWalkToBank(player);
            }
            return;
        }
        if (player.getInventoryManager().getContainer().getFreeSlots() <= 0) {
            Player player3 = player;
            player3.packetSender.sendGameMessage("Not enough space in your inventory.");
            player3 = player;
            player3.packetSender.sendSoundEffect(1878, 1, 0);
            if (player.botEnabled) {
                player.currentBotTask.startWalkToBank(player);
            }
            return;
        }
        int n = player.nextActionSequence();
        int n2 = player.getSkillManager().getBaseLevel(14) >= 30 && player.isMember() && !ServerSettings.freeToPlayWorld ? RunecraftingHandler.PURE_ESSENCE_ITEM_ID : 1436;
        int n3 = gatheringToolDefinition.getGatherAnimationId();
        gatheringToolDefinition.getGraphicId();
        int n4 = (int)gatheringToolDefinition.getToolSpeed();
        Player player4 = player;
        player4.packetSender.sendGameMessage("You swing your pick at the rock.");
        player.getUpdateState().setAnimation(n3);
        player.setActiveCycleEvent(new RuneEssenceMiningTask(player, n, n3, n2));
        CycleEventHandler.getInstance().schedule(player, player.getActiveCycleEvent(), n4);
    }

    public EntityTargetMovement(Entity entity) {
        int[][] nArrayArray = new int[8][];
        nArrayArray[0] = new int[]{-1, 1};
        int[] nArray = new int[2];
        nArray[1] = 1;
        nArrayArray[1] = nArray;
        nArrayArray[2] = new int[]{1, 1};
        int[] nArray2 = new int[2];
        nArray2[0] = -1;
        nArrayArray[3] = nArray2;
        int[] nArray3 = new int[2];
        nArray3[0] = 1;
        nArrayArray[4] = nArray3;
        nArrayArray[5] = new int[]{-1, -1};
        int[] nArray4 = new int[2];
        nArray4[1] = -1;
        nArrayArray[6] = nArray4;
        nArrayArray[7] = new int[]{1, -1};
        int[][][] nArrayArray2 = new int[][][]{new int[][]{new int[2], {-1, 2}, new int[2], {-2, 1}, new int[2], new int[2], new int[2], new int[2]}, new int[][]{new int[2], new int[2], new int[2], new int[2], new int[2], new int[2], new int[2], new int[2]}, new int[][]{new int[2], {1, 2}, new int[2], new int[2], {2, 1}, new int[2], new int[2], new int[2]}, new int[][]{new int[2], new int[2], new int[2], new int[2], new int[2], new int[2], new int[2], new int[2]}, new int[][]{new int[2], new int[2], new int[2], new int[2], new int[2], new int[2], new int[2], new int[2]}, new int[][]{new int[2], new int[2], new int[2], {-2, -1}, new int[2], new int[2], {-1, -2}, new int[2]}, new int[][]{new int[2], new int[2], new int[2], new int[2], new int[2], new int[2], new int[2], new int[2]}, new int[][]{new int[2], new int[2], new int[2], new int[2], {2, -1}, new int[2], {1, -2}, new int[2]}};
        this.entity = entity;
    }

    public void process() {
        Npc npc;
        if (this.entity.isNpc() && (npc = (Npc)this.entity).getMovementTarget() == null && npc.getForcedCombatTarget() != null) {
            npc.setMovementTarget(npc.getForcedCombatTarget());
            CombatManager.startCombat(npc, npc.getForcedCombatTarget());
            npc.getUpdateState().setFacePosition(npc.getForcedCombatTarget().getPosition());
        }
        if (this.entity.getMovementTarget() != null) {
            this.processTargetMovement();
        }
    }

    public void processTargetMovement() {
        Object object;
        Entity entity = this.entity.getMovementTarget();
        if (entity == null || entity.isDead() || this.entity.isDead() || !entity.getPosition().isWithinDistance(this.entity.getPosition(), 20)) {
            boolean bl = false;
            if (this.entity.isNpc() && ((Npc)(object = (Npc)this.entity)).getForcedCombatTarget() != null) {
                bl = true;
            }
            if (!bl) {
                EntityTargetMovement.clearMovementTarget(this.entity);
                CombatManager.stopCombat(this.entity);
                return;
            }
        }
        if (this.entity.isTargetMovementDisabled()) {
            return;
        }
        this.entity.getUpdateState().setFaceEntity(entity.getEncodedIndex());
        if (this.entity.isMovementLocked() || this.entity.isStunned()) {
            return;
        }
        if (this.entity.isPlayer() && entity.isNpc() && (((Npc)entity).isBanker() || ((Npc)entity).getNpcId() == 736 || ((Npc)entity).getNpcId() == 745 || ((Npc)entity).getNpcId() == 3859 || ((Npc)entity).getNpcId() == 482)) {
            Player player = (Player)this.entity;
            object = ((Npc)entity).getFacingInteractionPosition(2);
            PathFinder.getInstance();
            PathFinder.findPath(player, ((Position)object).getX(), ((Position)object).getY(), true, 0, 0);
            return;
        }
        if (this.entity.isPlayer() && this.entity.getCombatTarget() == null) {
            Player player = (Player)this.entity;
            if (entity.isPlayer() && this.entity.getInteractionTarget() == null) {
                int n = entity.getPosition().getPreviousX();
                int n2 = entity.getPosition().getPreviousY();
                PathFinder.getInstance();
                PathFinder.findPath(player, n, n2, true, 0, 0);
                return;
            }
            EntityTargetMovement.pathPlayerAdjacentToTarget(player, entity);
            return;
        }
        if (this.entity.isPlayer()) {
            Player player = (Player)this.entity;
            if (this.entity.getAttackRange() < 2 && entity.getSize() < 2) {
                EntityTargetMovement.pathPlayerAdjacentToTarget(player, entity);
                return;
            }
            if (this.entity.isOverlapping(entity)) {
                this.moveAwayFromOverlap();
                return;
            }
            if (EntityTargetMovement.canReachTarget(this.entity, entity)) {
                return;
            }
            PathFinder.getInstance();
            PathFinder.findPath(player, entity.getPosition().getX(), entity.getPosition().getY(), true, 0, 0);
            return;
        }
        if (this.entity.isNpc()) {
            Npc npc = (Npc)this.entity;
            if (this.entity.isOverlapping(entity)) {
                this.moveAwayFromOverlap();
                return;
            }
            if (EntityTargetMovement.canReachTarget(this.entity, entity)) {
                return;
            }
            Entity entity2 = entity;
            entity = npc;
            int n = entity2.getPosition().getX();
            int n3 = entity2.getPosition().getY();
            int n4 = 0;
            int n5 = 0;
            if (n != entity.getPosition().getX() && n3 != entity.getPosition().getY()) {
                if (n > entity.getPosition().getX() && n3 > entity.getPosition().getY()) {
                    if (((Npc)entity).canTraverseStep(1, 1) && !entity.isWithinReach(entity2, 1)) {
                        n4 = 1;
                        n5 = 1;
                    } else if (((Npc)entity).canTraverseStep(1, 0)) {
                        n4 = 1;
                        n5 = 0;
                    } else if (((Npc)entity).canTraverseStep(0, 1)) {
                        n4 = 0;
                        n5 = 1;
                    }
                } else if (n > entity.getPosition().getX() && n3 < entity.getPosition().getY()) {
                    if (((Npc)entity).canTraverseStep(1, -1) && !entity.isWithinReach(entity2, 1)) {
                        n4 = 1;
                        n5 = -1;
                    } else if (((Npc)entity).canTraverseStep(0, -1)) {
                        n4 = 0;
                        n5 = -1;
                    } else if (((Npc)entity).canTraverseStep(1, 0)) {
                        n4 = 1;
                        n5 = 0;
                    }
                } else if (n < entity.getPosition().getX() && n3 > entity.getPosition().getY()) {
                    if (((Npc)entity).canTraverseStep(-1, 1) && !entity.isWithinReach(entity2, 1)) {
                        n4 = -1;
                        n5 = 1;
                    } else if (((Npc)entity).canTraverseStep(-1, 0)) {
                        n4 = -1;
                        n5 = 0;
                    } else if (((Npc)entity).canTraverseStep(0, 1)) {
                        n4 = 0;
                        n5 = 1;
                    }
                } else if (n < entity.getPosition().getX() && n3 < entity.getPosition().getY()) {
                    if (((Npc)entity).canTraverseStep(-1, -1) && !entity.isWithinReach(entity2, 1)) {
                        n4 = -1;
                        n5 = -1;
                    } else if (((Npc)entity).canTraverseStep(0, -1)) {
                        n4 = 0;
                        n5 = -1;
                    } else if (((Npc)entity).canTraverseStep(-1, 0)) {
                        n4 = -1;
                        n5 = 0;
                    }
                }
            } else if (n != entity.getPosition().getX()) {
                if (n > entity.getPosition().getX()) {
                    if (((Npc)entity).canTraverseStep(1, 0)) {
                        n4 = 1;
                        n5 = 0;
                    }
                } else if (n < entity.getPosition().getX() && ((Npc)entity).canTraverseStep(-1, 0)) {
                    n4 = -1;
                    n5 = 0;
                }
            } else if (n3 != entity.getPosition().getY()) {
                if (n3 > entity.getPosition().getY()) {
                    if (((Npc)entity).canTraverseStep(0, 1)) {
                        n4 = 0;
                        n5 = 1;
                    }
                } else if (n3 < entity.getPosition().getY() && ((Npc)entity).canTraverseStep(0, -1)) {
                    n4 = 0;
                    n5 = -1;
                }
            }
            if (n4 != 0 || n5 != 0) {
                boolean bl = false;
                int[][] nArray = PetManager.petItemNpcPairs;
                n3 = 0;
                while (n3 < 6) {
                    int[] nArray2 = nArray[n3];
                    if (((Npc)entity).getNpcId() == nArray2[1]) {
                        bl = true;
                    }
                    ++n3;
                }
                if (!GameUtil.isWithinDistance(entity.getPosition(), ((Npc)entity).getSpawnPosition(), ((Npc)entity).getDefinition().getChaseRadius()) && !bl && ((Npc)entity).getForcedCombatTarget() == null) {
                    CombatManager.stopCombat(entity);
                    Entity entity3 = entity;
                    entity3.nextActionSequence();
                    entity3.setInteractionTarget(null);
                    entity3.setCombatTarget(null);
                    entity3.setActiveCycleEvent(null);
                    entity3.getUpdateState().setFaceEntity(-1);
                    EntityTargetMovement.clearMovementTarget(entity3);
                    entity.queuePathTo(((Npc)entity).getSpawnPosition().getX(), ((Npc)entity).getSpawnPosition().getY(), true);
                    return;
                }
                entity.queuePathTo(entity.getPosition().getX() + n4, entity.getPosition().getY() + n5, true);
            }
        }
    }

    public static void pathPlayerAdjacentToTarget(Player player, Entity entity) {
        int n = player.getPosition().getX();
        int n2 = player.getPosition().getY();
        int n3 = entity.getPosition().getX();
        int n4 = entity.getPosition().getY();
        if (n > n3 && entity.canStepToOffset(1, 0)) {
            PathFinder.getInstance();
            PathFinder.findPath(player, n3 + 1, n4, true, 0, 0);
            return;
        }
        if (n < n3 && entity.canStepToOffset(-1, 0)) {
            PathFinder.getInstance();
            PathFinder.findPath(player, n3 - 1, n4, true, 0, 0);
            return;
        }
        if (n2 < n4 && entity.canStepToOffset(0, -1)) {
            PathFinder.getInstance();
            PathFinder.findPath(player, n3, n4 - 1, true, 0, 0);
            return;
        }
        if (entity.canStepToOffset(1, 0)) {
            PathFinder.getInstance();
            PathFinder.findPath(player, n3 + 1, n4, true, 0, 0);
            return;
        }
        if (entity.canStepToOffset(-1, 0)) {
            PathFinder.getInstance();
            PathFinder.findPath(player, n3 - 1, n4, true, 0, 0);
            return;
        }
        if (entity.canStepToOffset(0, -1)) {
            PathFinder.getInstance();
            PathFinder.findPath(player, n3, n4 - 1, true, 0, 0);
            return;
        }
        if (entity.canStepToOffset(0, 1)) {
            PathFinder.getInstance();
            PathFinder.findPath(player, n3, n4 + 1, true, 0, 0);
        }
    }

    public static void clearMovementTarget(Entity entity) {
        entity.getUpdateState().setFaceEntity(65535);
        entity.setMovementTarget(null);
    }

    public static boolean canReachTarget(Entity entity, Entity entity2) {
        return EntityTargetMovement.canReachTarget(entity, entity2, entity.getAttackRange());
    }

    public static boolean canReachTarget(Entity entity, Entity entity2, int n) {
        if (entity.isOverlapping(entity2)) {
            return false;
        }
        if (entity.getCombatTarget() != null && entity.isPlayer() && n == 1 && entity2.getSize() < 2 && !entity2.isMoving() && entity.getPosition().getX() != entity2.getPosition().getX() && entity.getPosition().getY() != entity2.getPosition().getY()) {
            return false;
        }
        if (!entity.isWithinReach(entity2, n)) {
            return false;
        }
        if (entity2.isDoorSupportNpc()) {
            return true;
        }
        if (entity2.isNpc()) {
            Npc npc = (Npc)entity2;
            if (npc.getNpcId() == 221) {
                return true;
            }
            if (npc.getNpcId() == 2892) {
                return true;
            }
        }
        return GameUtil.hasClearPath(entity.getPosition(), entity2.getPosition(), n < 2);
    }

    public void moveAwayFromOverlap() {
        Npc npc;
        this.entity.getMovementQueue().clear();
        int n = this.entity.getPosition().getX();
        int n2 = this.entity.getPosition().getY();
        Npc npc2 = npc = this.entity.isNpc() ? (Npc)this.entity : null;
        if (this.entity.canStepToOffset(-1, 0) && (npc == null || !npc.wouldCollideWithNpc(-1, 0))) {
            this.entity.queuePathTo(n - 1, n2, true);
            return;
        }
        if (this.entity.canStepToOffset(1, 0) && (npc == null || !npc.wouldCollideWithNpc(1, 0))) {
            this.entity.queuePathTo(n + 1, n2, true);
            return;
        }
        if (this.entity.canStepToOffset(0, -1) && (npc == null || !npc.wouldCollideWithNpc(0, -1))) {
            this.entity.queuePathTo(n, n2 - 1, true);
            return;
        }
        if (this.entity.canStepToOffset(0, 1) && (npc == null || !npc.wouldCollideWithNpc(0, 1))) {
            this.entity.queuePathTo(n, n2 + 1, true);
        }
    }

    public static boolean isDiagonalTo(Position position, Position position2) {
        return position.getX() != position2.getX() && position.getY() != position2.getY();
    }
}

