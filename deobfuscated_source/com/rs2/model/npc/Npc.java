/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.npc;

import com.rs2.ServerSettings;
import com.rs2.bot.combat.BotCombatHelper;
import com.rs2.model.Entity;
import com.rs2.model.EntityUpdateState;
import com.rs2.model.GameplayHelper;
import com.rs2.model.MovementStep;
import com.rs2.model.Position;
import com.rs2.model.World;
import com.rs2.model.clue.ClueKeyHandler;
import com.rs2.model.combat.CombatType;
import com.rs2.model.gameplay.abyss.AbyssManager;
import com.rs2.model.gameplay.godwars.GodWarsDungeonManager;
import com.rs2.model.gameplay.partyroom.PartyRoomManager;
import com.rs2.model.ground.GroundItem;
import com.rs2.model.ground.GroundItemManager;
import com.rs2.model.item.ItemDefinition;
import com.rs2.model.item.ItemStack;
import com.rs2.model.npc.AbyssMageTeleportEvent;
import com.rs2.model.npc.MageArenaChallengeStartTask;
import com.rs2.model.npc.NpcDefinition;
import com.rs2.model.npc.NpcDialogueTeleportEvent;
import com.rs2.model.npc.NpcMovementMode;
import com.rs2.model.npc.NpcRelocationEvent;
import com.rs2.model.npc.NpcSequenceAdvanceTask;
import com.rs2.model.npc.NpcStageAdvanceTask;
import com.rs2.model.npc.NpcStatRestoreTask;
import com.rs2.model.npc.combat.NpcCombatDefinition;
import com.rs2.model.npc.drop.NpcDropManager;
import com.rs2.model.player.Player;
import com.rs2.model.task.CycleEventHandler;
import com.rs2.model.task.TickTask;
import com.rs2.util.GameUtil;
import com.rs2.util.RectangularArea;
import com.rs2.util.path.WalkingCollisionMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class Npc
extends Entity {
    private int npcId;
    private int originalNpcId;
    private NpcDefinition definition;
    private Position spawnMinPosition = new Position(0, 0);
    private Position spawnMaxPosition = new Position(0, 0);
    private Position spawnPosition;
    private NpcMovementMode movementMode = NpcMovementMode.STATIONARY;
    private int spawnX;
    private int spawnY;
    private int currentHitpoints;
    private int currentAttackLevel = 0;
    private int currentStrengthLevel = 0;
    private int currentDefenceLevel = 0;
    private int currentMagicLevel = 0;
    private int currentRangedLevel = 0;
    private TickTask[] statRestoreTasks = new TickTask[6];
    private int transformedNpcId;
    private int transformTicksRemaining;
    private int facingDirection;
    private boolean active = true;
    private boolean transformed;
    private boolean respawnEnabled;
    private boolean interactable;
    private int ownerPlayerIndex;
    private boolean faceEntityUpdateDisabled = false;
    private Entity forcedCombatTarget = null;
    private int removalDelayTicks = -1;
    public Player a = null;
    public boolean b = false;
    public boolean c = false;
    public boolean d = false;
    public boolean e = false;
    public boolean f = false;
    private NpcCombatDefinition combatDefinition;
    private static int nextReferenceId;
    public static int[] combatTransformNpcIds;
    public static int[] scriptedMovementNpcIds;
    public static int[] targetMovementDisabledNpcIds;
    public static int[] autoRetaliateDisabledNpcIds;
    public static int[] faceEntityUpdateDisabledNpcIds;
    public static int scriptedStageCursor;
    public static List scriptedStageNpcs;
    private int scriptedPathTargetX = -1;
    private int scriptedPathTargetY = -1;
    private Position[] waypointLoop;
    private int waypointLoopIndex = -1;
    public int scriptedSequenceLoopCount = 0;
    public int scriptedPathStage = -1;
    private boolean skipNextPathAdvance = false;
    public int lastStepDeltaX = 0;
    public int lastStepDeltaY = 0;

    static {
        new Random();
        nextReferenceId = 0;
        combatTransformNpcIds = new int[]{1266, 1268, 2453, 2886, 2890};
        scriptedMovementNpcIds = new int[]{1266, 1268, 2453, 2886, 2890, 1827, 2892, 2894, 2896};
        targetMovementDisabledNpcIds = new int[]{1827, 2892, 2894, 2896};
        autoRetaliateDisabledNpcIds = new int[]{2440, 2443, 2446};
        faceEntityUpdateDisabledNpcIds = new int[]{767, 1274, 1275, 1276, 1277, 1826, 1827, 1740, 1746, 1719, 1720, 1721, 1721, 1721, 1722, 1722, 1722, 1723, 1727, 1727, 1728, 1729, 1741, 1742, 1743, 1743, 1744, 1744, 1744, 1732, 1725, 1727, 1745, 1721, 1719, 1721, 1722, 1726, 1747, 1747, 1747, 1722, 1727, 1727, 1739, 1736, 1737, 1737, 1738, 2535, 1735, 1749, 1750, 2534, 1734};
        scriptedStageCursor = -1;
        scriptedStageNpcs = new ArrayList();
    }

    public final boolean isFaceEntityUpdateDisabled() {
        return this.faceEntityUpdateDisabled;
    }

    public final void setFaceEntityUpdateDisabled(boolean bl) {
        this.faceEntityUpdateDisabled = true;
    }

    public final Entity getForcedCombatTarget() {
        return this.forcedCombatTarget;
    }

    public final void setForcedCombatTarget(Entity entity) {
        this.forcedCombatTarget = entity;
    }

    public Npc(int n) {
        NpcDefinition npcDefinition = World.getNpcDefinitions()[n];
        int n2 = n;
        Npc npc = this;
        this.npcId = n2;
        int n3 = 1;
        Npc npc2 = this;
        this.interactable = true;
        n3 = n;
        npc2 = this;
        this.originalNpcId = n3;
        this.getUpdateState().setUpdateRequired(true);
        this.definition = npcDefinition == null ? NpcDefinition.createFallback(n) : npcDefinition;
        npc2 = this;
        npc2.getAttributes().put("doDamage", Boolean.FALSE);
        npc2.getAttributes().put("canTakeDamage", Boolean.TRUE);
        this.setReferenceId(nextReferenceId++);
        this.combatDefinition = NpcCombatDefinition.forNpcId(n);
        Npc npc3 = this;
        this.currentHitpoints = npc3.definition.getHitpoints();
        this.resetCombatLevels();
        npc3 = npc2 = this;
        if (npc2.npcId == 896) {
            npc2.startWaypointLoop(new Position[]{new Position(2904, 3463, 0), new Position(2930, 3463, 0)});
        }
    }

    public final void process() {
        Object object;
        block52: {
            Npc npc;
            block53: {
                block46: {
                    double d;
                    Object object2;
                    block51: {
                        block50: {
                            Npc npc2;
                            block49: {
                                block48: {
                                    Position position;
                                    int n;
                                    int n2;
                                    block47: {
                                        npc2 = this;
                                        object = npc2;
                                        if (npc2.transformTicksRemaining > 0) {
                                            object = npc2;
                                            if (((Npc)object).transformTicksRemaining < 999999) {
                                                object = npc2;
                                                int n3 = ((Npc)object).transformTicksRemaining - 1;
                                                object = npc2;
                                                npc2.transformTicksRemaining = n3;
                                                object = npc2;
                                                if (((Npc)object).transformTicksRemaining <= 0) {
                                                    object = npc2;
                                                    npc2.transformToNpcId(((Npc)object).originalNpcId, 0);
                                                }
                                            }
                                        }
                                        this.pruneExpiredDamageContributions();
                                        npc2 = this;
                                        if (npc2 == null || npc2.isDead()) break block46;
                                        object2 = npc2;
                                        if (!((Npc)object2).b) {
                                            object = object2;
                                            if (((Npc)object).npcId == 708 && GameUtil.randomInt(30) == 0 && WalkingCollisionMap.getTileFlags(n2 = ((Npc)object2).spawnX + (GameUtil.randomInt(2) == 0 ? -3 - GameUtil.randomInt(4) : 3 + GameUtil.randomInt(4)), n = ((Npc)object2).spawnY + (GameUtil.randomInt(2) == 0 ? -3 - GameUtil.randomInt(4) : 3 + GameUtil.randomInt(4)), ((Entity)object2).getPosition().getPlane()) == 0) {
                                                ((Entity)object2).getUpdateState().setGraphic(86, 25);
                                                ((Npc)object2).moveTo(new Position(n2, n));
                                            }
                                        }
                                        if (npc2.hasCombatTarget() || npc2.getMovementTarget() != null || npc2.getInteractionTarget() != null || npc2.getCombatTarget() != null) break block46;
                                        if (!npc2.isScriptedMovementEnabled()) break block47;
                                        if (npc2.waypointLoop != null && ((Entity)(object2 = npc2)).getPosition().getX() == ((Npc)object2).waypointLoop[((Npc)object2).waypointLoopIndex].getX() && ((Entity)object2).getPosition().getY() == ((Npc)object2).waypointLoop[((Npc)object2).waypointLoopIndex].getY()) {
                                            ((Npc)object2).waypointLoopIndex = ((Npc)object2).waypointLoop.length > ((Npc)object2).waypointLoopIndex + 1 ? ++((Npc)object2).waypointLoopIndex : 0;
                                            ((Entity)object2).getMovementQueue().clear();
                                            ((Entity)object2).getMovementQueue().addStep(((Npc)object2).waypointLoop[((Npc)object2).waypointLoopIndex]);
                                            ((Entity)object2).getMovementQueue().removeFirstStep();
                                        }
                                        if (npc2.scriptedPathTargetX != -1 && npc2.scriptedPathTargetY != -1 || !npc2.getMovementQueue().getSteps().isEmpty()) {
                                            object2 = npc2;
                                            if (((Npc)object2).waypointLoop != null) {
                                                ((Entity)object2).getMovementQueue().clear();
                                                ((Entity)object2).getMovementQueue().addStep(((Npc)object2).waypointLoop[((Npc)object2).waypointLoopIndex]);
                                                ((Entity)object2).getMovementQueue().removeFirstStep();
                                            } else if (((Entity)object2).getPosition().getX() == ((Npc)object2).scriptedPathTargetX && ((Entity)object2).getPosition().getY() == ((Npc)object2).scriptedPathTargetY || ((Entity)object2).getPosition().getX() == ((MovementStep)((Entity)object2).getMovementQueue().getSteps().getLast()).getX() && ((Entity)object2).getPosition().getY() == ((MovementStep)((Entity)object2).getMovementQueue().getSteps().getLast()).getY()) {
                                                ((Npc)object2).scriptedPathTargetX = -1;
                                                ((Npc)object2).scriptedPathTargetY = -1;
                                                if (((Npc)object2).skipNextPathAdvance) {
                                                    ((Npc)object2).skipNextPathAdvance = false;
                                                } else if (((Npc)object2).npcId == 1454) {
                                                    World.getTaskScheduler().schedule(new NpcSequenceAdvanceTask((Npc)object2, 10));
                                                } else if (((Npc)object2).npcId == 1431 || ((Npc)object2).npcId == 1432) {
                                                    if (((Npc)object2).scriptedPathStage == 5) {
                                                        int n4 = scriptedStageCursor;
                                                        if (++n4 + 1 > scriptedStageNpcs.size()) {
                                                            n4 = 0;
                                                        }
                                                        Npc npc3 = (Npc)scriptedStageNpcs.get(n4);
                                                        ((Npc)scriptedStageNpcs.get(n4)).skipNextPathAdvance = true;
                                                        npc3.queueScriptedPath(new Position[]{new Position(2768, 2803, 0)});
                                                    }
                                                    if (((Npc)object2).scriptedPathStage == 6) {
                                                        if (++scriptedStageCursor + 1 > scriptedStageNpcs.size()) {
                                                            scriptedStageCursor = 0;
                                                        }
                                                        object = (Npc)scriptedStageNpcs.get(scriptedStageCursor);
                                                        ((Npc)scriptedStageNpcs.get(scriptedStageCursor)).scriptedPathStage = 0;
                                                        ((Npc)object).queueStageAdvancePath(((Npc)object).scriptedPathStage);
                                                    } else {
                                                        World.getTaskScheduler().schedule(new NpcStageAdvanceTask((Npc)object2, 10));
                                                    }
                                                } else {
                                                    ((Entity)object2).setScriptedMovementEnabled(false);
                                                }
                                            }
                                        }
                                        break block46;
                                    }
                                    object = npc2;
                                    if (((Npc)object).movementMode != NpcMovementMode.STATIONARY) break block48;
                                    EntityUpdateState entityUpdateState = npc2.getUpdateState();
                                    object = npc2;
                                    int n5 = ((Npc)object).facingDirection;
                                    object2 = npc2.getPosition();
                                    n2 = ((Position)object2).getX();
                                    n = ((Position)object2).getY();
                                    switch (n5) {
                                        case 2: {
                                            position = new Position(n2, n + 1);
                                            break;
                                        }
                                        case 3: {
                                            position = new Position(n2, n - 1);
                                            break;
                                        }
                                        case 4: {
                                            position = new Position(n2 + 1, n);
                                            break;
                                        }
                                        case 5: {
                                            position = new Position(n2 - 1, n);
                                            break;
                                        }
                                        default: {
                                            position = new Position(n2, n - 1);
                                        }
                                    }
                                    entityUpdateState.setFacePosition(position);
                                    break block49;
                                }
                                if (npc2.isMovementLocked() || npc2.isStunned() || !GameUtil.rollChance(0.26)) break block49;
                                if (!World.hasNearbyNonBotPlayer(npc2)) break block46;
                                if (Player.eu != null && Player.eu.getMovementTarget() == npc2) {
                                    System.out.println("npc moved");
                                }
                                int n = npc2.spawnMinPosition.getX();
                                int n6 = npc2.spawnMinPosition.getY();
                                int n7 = npc2.spawnMaxPosition.getX() - npc2.spawnMinPosition.getX();
                                int n8 = npc2.spawnMaxPosition.getY() - npc2.spawnMinPosition.getY();
                                n7 = GameUtil.getRandom().nextInt(n7);
                                n8 = GameUtil.getRandom().nextInt(n8);
                                object2 = new Position(n + n7, n6 + n8, npc2.getPosition().getPlane());
                                npc2.queuePathTo((Position)object2, true);
                            }
                            object = object2 = npc2;
                            if (((Npc)object2).npcId == 1765) break block50;
                            object = object2;
                            if (((Npc)object).npcId != 43) break block51;
                        }
                        if (GameUtil.randomInt(75) == 0) {
                            ((Entity)object2).getUpdateState().setForcedText("Baa!");
                        }
                    }
                    object = object2;
                    if (((Npc)object).npcId == 81 && GameUtil.randomInt(75) == 0) {
                        ((Entity)object2).getUpdateState().setForcedText("Moo!");
                    }
                    object = object2;
                    if (((Npc)object).npcId == 767 && GameUtil.randomInt(75) == 0) {
                        ((Entity)object2).getUpdateState().setForcedText("Mew!");
                    }
                    if (((Npc)object2).isBanker() && PartyRoomManager.partyChestValue >= 50000 && PartyRoomManager.balloonDropPending && GameUtil.randomInt(10) == 0) {
                        double d2 = PartyRoomManager.balloonDropTask.getRemainingTicks();
                        d = d2 * 0.6;
                        int n = (int)d;
                        object = String.valueOf(n) + " seconds";
                        if (n > 60) {
                            object = String.valueOf(n / 60 + 1) + " mins";
                        }
                        ((Entity)object2).getUpdateState().setForcedText("Drop party in Party Room in " + (String)object + "! Value: " + GameUtil.formatCompactAmount(PartyRoomManager.partyChestValue));
                    }
                    object = object2;
                    if (((Npc)object).npcId == 659 && PartyRoomManager.balloonDropPending && GameUtil.randomInt(5) == 0) {
                        double d3 = PartyRoomManager.balloonDropTask.getRemainingTicks();
                        d = d3 * 0.6;
                        int n = (int)d;
                        object = String.valueOf(n) + " seconds!";
                        if (n > 60) {
                            object = String.valueOf(n / 60 + 1) + " mins!";
                        }
                        ((Entity)object2).getUpdateState().setForcedText("Balloons dropping in: " + (String)object);
                    }
                }
                if (ServerSettings.content2007Enabled) {
                    GodWarsDungeonManager.handleBossBattleCry(this);
                }
                this.processStatRestoration();
                this.getTargetMovement().process();
                Npc npc4 = npc = this;
                if (npc.ownerPlayerIndex <= 0 || npc.isDead()) break block52;
                if (npc.getOwnerPlayer() == null) break block53;
                if (GameUtil.isWithinDistance(npc.getPosition(), npc.getOwnerPlayer().getPosition(), 15)) break block52;
                object = npc;
                if (((Npc)object).npcId == 3098) break block52;
            }
            GameplayHelper.unregisterTemporaryNpc(npc);
        }
        if (this.removalDelayTicks == 0) {
            boolean bl = false;
            object = this;
            this.active = bl;
            World.unregisterNpc(this);
        }
        if (this.removalDelayTicks > 0) {
            --this.removalDelayTicks;
        }
    }

    public final void setRemovalDelayTicks(int n) {
        this.removalDelayTicks = n;
    }

    private void processStatRestoration() {
        int n = 0;
        while (n < 6) {
            int n2 = n;
            Npc npc = this;
            int n3 = n2;
            n3 = n3 == 0 ? 0 : (n3 == 1 ? 2 : (n3 == 2 ? 1 : (n3 == 3 ? 6 : (n3 == 4 ? 4 : (n3 == 5 ? 3 : -1)))));
            Object object = npc.statRestoreTasks[n2];
            if (npc.getCurrentLevelForSkill(n3) != npc.getBaseLevelForSkill(n3)) {
                int n4 = n3;
                Npc npc2 = npc;
                object = npc2;
                if (npc2.statRestoreTasks[Npc.getStatRestoreTaskSlot(n4)] == null || !npc2.statRestoreTasks[Npc.getStatRestoreTaskSlot(n4)].isActive()) {
                    npc2.statRestoreTasks[Npc.getStatRestoreTaskSlot((int)n4)] = new NpcStatRestoreTask(npc2, 100, (Npc)object, n4);
                    World.getTaskScheduler().schedule(npc2.statRestoreTasks[Npc.getStatRestoreTaskSlot(n4)]);
                }
            } else if (object != null) {
                ((TickTask)object).stop();
                ((TickTask)object).setIntervalTicks(100);
                ((TickTask)object).setRemainingTicks(100);
            }
            ++n;
        }
    }

    public final boolean isStatModified(int n) {
        return this.getCurrentLevelForSkill(n) != this.getBaseLevelForSkill(n);
    }

    private static int getStatRestoreTaskSlot(int n) {
        if (n == 0) {
            return 0;
        }
        if (n == 2) {
            return 1;
        }
        if (n == 1) {
            return 2;
        }
        if (n == 6) {
            return 3;
        }
        if (n == 4) {
            return 4;
        }
        if (n == 3) {
            return 5;
        }
        return -1;
    }

    public final void adjustCurrentLevel(int n, int n2) {
        if (n == 0) {
            this.currentAttackLevel += n2;
        }
        if (n == 2) {
            this.currentStrengthLevel += n2;
        }
        if (n == 1) {
            this.currentDefenceLevel += n2;
        }
        if (n == 6) {
            this.currentMagicLevel += n2;
        }
        if (n == 4) {
            this.currentRangedLevel += n2;
        }
        if (n == 3) {
            this.currentHitpoints += n2;
        }
    }

    public final int getCurrentLevelForSkill(int n) {
        if (n == 0) {
            return this.currentAttackLevel;
        }
        if (n == 2) {
            return this.currentStrengthLevel;
        }
        if (n == 1) {
            return this.currentDefenceLevel;
        }
        if (n == 6) {
            return this.currentMagicLevel;
        }
        if (n == 4) {
            return this.currentRangedLevel;
        }
        if (n == 3) {
            return this.currentHitpoints;
        }
        return -1;
    }

    public final int getBaseLevelForSkill(int n) {
        if (n == 0) {
            Npc npc = this;
            return npc.definition.getAttackLevel();
        }
        if (n == 2) {
            Npc npc = this;
            return npc.definition.getStrengthLevel();
        }
        if (n == 1) {
            Npc npc = this;
            return npc.definition.getDefenceLevel();
        }
        if (n == 6) {
            Npc npc = this;
            return npc.definition.getMagicLevel();
        }
        if (n == 4) {
            Npc npc = this;
            return npc.definition.getRangedLevel();
        }
        if (n == 3) {
            Npc npc = this;
            return npc.definition.getHitpoints();
        }
        return -1;
    }

    public final void startDialogueTeleport(Player player, int n, int n2, int n3, String string) {
        player.getDialogueManager().finishDialogue();
        Player player2 = player;
        player2.packetSender.closeInterfaces();
        this.getUpdateState().setAnimation(1818);
        this.getUpdateState().setGraphic(343);
        this.getUpdateState().setForcedText(string);
        player.setActionLocked(true);
        CycleEventHandler.getInstance().schedule(player, new NpcDialogueTeleportEvent(this, player, n, n2, 0), 4);
    }

    public final void startMageArenaChallenge(Player object, int n, int n2, int n3, String string) {
        Npc npc;
        ((Player)object).getDialogueManager().finishDialogue();
        Player player = object;
        player.packetSender.closeInterfaces();
        Npc npc2 = npc = new Npc(907 + ((Player)object).mageArenaProgressStage);
        player = object;
        ((Player)object).H = npc2;
        this.getUpdateState().setAnimation(717);
        ((Player)object).getTeleportManager().startStandardTeleport(3105, 3934, 0, null);
        World.getTaskScheduler().schedule(new MageArenaChallengeStartTask(this, 12, (Player)object, npc));
    }

    public final void startAbyssMageTeleport(Player player, int n, int n2, int n3, String string) {
        player.getDialogueManager().finishDialogue();
        player.packetSender.closeInterfaces();
        int n4 = this.definition.getId();
        n4 = n4 == 171 ? 200 : 717;
        this.getUpdateState().setAnimation(n4);
        this.getUpdateState().setGraphic(108);
        this.getUpdateState().setForcedText(string);
        player.setActionLocked(true);
        player.getAttributes().put("canTakeDamage", Boolean.FALSE);
        CycleEventHandler.getInstance().schedule(player, new AbyssMageTeleportEvent(this, player, 2911, 4832, 0), 4);
    }

    public final void startNpcRelocation(Player player, int n, int n2, int n3, int n4, int n5, String string, boolean bl) {
        player.getDialogueManager().finishDialogue();
        player.packetSender.closeInterfaces();
        this.getUpdateState().setAnimation(402);
        player.getUpdateState().setAnimation(2304);
        player.packetSender.showInterface(8677);
        if (string != null) {
            this.getUpdateState().setForcedText(string);
        }
        player.setActionLocked(true);
        CycleEventHandler.getInstance().schedule(player, new NpcRelocationEvent(this, player, n3, n4, n5, bl, this), 4);
    }

    public final void resetAfterUpdate() {
        this.getUpdateState().setAnimation(-1);
        this.getUpdateState().reset();
        this.transformed = false;
        this.setWalkDirection(-1);
        this.getUpdateState().setFaceEntity(-1);
    }

    @Override
    public final void heal(int n) {
        Npc npc = this;
        Npc npc2 = npc;
        npc2 = this;
        if (npc.currentHitpoints + n >= npc2.definition.getHitpoints()) {
            npc2 = this;
            n = npc2.definition.getHitpoints();
            npc2 = this;
            this.currentHitpoints = n;
            return;
        }
        npc2 = this;
        n = npc2.currentHitpoints + n;
        npc2 = this;
        this.currentHitpoints = n;
    }

    public final void resetCombatLevels() {
        Npc npc = this;
        this.currentAttackLevel = npc.definition.getAttackLevel();
        npc = this;
        this.currentStrengthLevel = npc.definition.getStrengthLevel();
        npc = this;
        this.currentDefenceLevel = npc.definition.getDefenceLevel();
        npc = this;
        this.currentMagicLevel = npc.definition.getMagicLevel();
        npc = this;
        this.currentRangedLevel = npc.definition.getRangedLevel();
    }

    private void startWaypointLoop(Position[] positionArray) {
        this.setScriptedMovementEnabled(true);
        this.waypointLoop = positionArray;
        this.waypointLoopIndex = 0;
        try {
            this.getMovementQueue().clear();
            this.getMovementQueue().addStep(this.waypointLoop[this.waypointLoopIndex]);
            this.getMovementQueue().removeFirstStep();
            return;
        }
        catch (Exception exception) {
            return;
        }
    }

    public final void queueScriptedPath(Position[] positions) {
        this.setScriptedMovementEnabled(true);
        this.getMovementQueue().clear();
        for (Position position : positions) {
            this.getMovementQueue().addStep(position);
        }
        this.scriptedPathTargetX = ((MovementStep)this.getMovementQueue().getSteps().getLast()).getX();
        this.scriptedPathTargetY = ((MovementStep)this.getMovementQueue().getSteps().getLast()).getY();
        this.getMovementQueue().removeFirstStep();
    }

    public final void queueStageAdvancePath(int n) {
        if (n == 0) {
            this.queueScriptedPath(new Position[]{new Position(2768, 2798, 0), new Position(2770, 2798, 0), new Position(2770, 2801, 0), new Position(2772, 2801, 0)});
            return;
        }
        if (n == 1) {
            this.queueScriptedPath(new Position[]{new Position(2772, 2796, 0)});
            return;
        }
        if (n == 2) {
            this.queueScriptedPath(new Position[]{new Position(2771, 2796, 0)});
            return;
        }
        if (n == 3) {
            this.queueScriptedPath(new Position[]{new Position(2772, 2796, 0)});
            return;
        }
        if (n == 4) {
            this.queueScriptedPath(new Position[]{new Position(2772, 2801, 0)});
            return;
        }
        if (n == 5) {
            this.queueScriptedPath(new Position[]{new Position(2770, 2801, 0), new Position(2770, 2798, 0), new Position(2768, 2798, 0), new Position(2767, 2799, 0)});
            return;
        }
        if (n == 6) {
            this.queueScriptedPath(new Position[]{new Position(2767, 2801, 0), new Position(2764, 2803, 0)});
        }
    }

    public final void queueSequenceAdvancePath(int n) {
        if (n == 0) {
            this.queueScriptedPath(new Position[]{new Position(2743, 2787, 0)});
            return;
        }
        if (n == 1) {
            this.queueScriptedPath(new Position[]{new Position(2740, 2786, 0)});
            return;
        }
        if (n == 2) {
            this.queueScriptedPath(new Position[]{new Position(2736, 2786, 0)});
            return;
        }
        if (n == 3) {
            this.queueScriptedPath(new Position[]{new Position(2736, 2792, 0)});
            return;
        }
        if (n == 4) {
            this.queueScriptedPath(new Position[]{new Position(2733, 2792, 0)});
            return;
        }
        if (n == 5) {
            this.queueScriptedPath(new Position[]{new Position(2734, 2794, 0), new Position(2737, 2794, 0)});
            return;
        }
        if (n == 6) {
            this.queueScriptedPath(new Position[]{new Position(2743, 2794, 0)});
            return;
        }
        if (n == 7) {
            this.queueScriptedPath(new Position[]{new Position(2743, 2792, 0)});
        }
    }

    public final int getLastStepFacingDirection() {
        if (Math.abs(this.lastStepDeltaX) > Math.abs(this.lastStepDeltaY)) {
            if (this.lastStepDeltaX > 0) {
                return 4;
            }
            return 5;
        }
        if (this.lastStepDeltaY > 0) {
            return 2;
        }
        return 0;
    }

    public final int getWaypointFacingDirection() {
        int n = this.waypointLoop[this.waypointLoopIndex].getX();
        int n2 = this.waypointLoop[this.waypointLoopIndex].getY();
        n = this.getPosition().getX() - n;
        n2 = this.getPosition().getY() - n2;
        if (Math.abs(n) > Math.abs(n2)) {
            if (n < 0) {
                return 4;
            }
            return 5;
        }
        if (n2 < 0) {
            return 2;
        }
        return 0;
    }

    public final void transformToNpcId(int n, int n2) {
        this.transformedNpcId = n;
        int n3 = n2;
        Npc npc = this;
        this.transformTicksRemaining = n3;
        this.transformed = true;
        n3 = n;
        npc = this;
        this.npcId = n3;
        this.getUpdateState().setUpdateRequired(true);
        this.definition = World.getNpcDefinitions()[n];
        this.combatDefinition = NpcCombatDefinition.forNpcId(n);
        npc = this;
        this.currentHitpoints = npc.definition.getHitpoints();
        this.resetCombatLevels();
    }

    public final void transformToNpcIdWithAnimation(int n, int n2, int n3) {
        this.transformedNpcId = n;
        n3 = 100000;
        Npc npc = this;
        this.transformTicksRemaining = n3;
        this.transformed = true;
        n3 = n;
        npc = this;
        this.npcId = n3;
        this.getUpdateState().setUpdateRequired(true);
        this.definition = World.getNpcDefinitions()[n];
        this.combatDefinition = NpcCombatDefinition.forNpcId(n);
        this.heal(38);
        this.resetCombatLevels();
    }

    public final int getNpcId() {
        return this.npcId;
    }

    public final int getOriginalNpcId() {
        return this.originalNpcId;
    }

    public final void setActive(boolean bl) {
        this.active = bl;
    }

    public final boolean isActive() {
        return this.active;
    }

    public final Player getOwnerPlayer() {
        if (this.ownerPlayerIndex == -1) {
            boolean bl = false;
            Npc npc = this;
            this.active = bl;
            World.unregisterNpc(this);
            return null;
        }
        return World.getPlayers()[this.ownerPlayerIndex];
    }

    public final void setOwnerPlayerIndex(int n) {
        this.ownerPlayerIndex = n;
    }

    public final void setSpawnMinPosition(Position position) {
        this.spawnMinPosition = position;
    }

    public final Position getSpawnMinPosition() {
        return this.spawnMinPosition;
    }

    public final void setSpawnMaxPosition(Position position) {
        this.spawnMaxPosition = position;
    }

    public final Position getSpawnMaxPosition() {
        return this.spawnMaxPosition;
    }

    public final void setMovementMode(NpcMovementMode npcMovementMode) {
        this.movementMode = npcMovementMode;
    }

    public final NpcMovementMode getMovementMode() {
        return this.movementMode;
    }

    public final boolean isTransformed() {
        return this.transformed;
    }

    public final int getTransformedNpcId() {
        return this.transformedNpcId;
    }

    public final int getRespawnDelayTicks() {
        return this.combatDefinition.getRespawnDelayTicks();
    }

    public final void setRespawnEnabled(boolean bl) {
        this.respawnEnabled = bl;
    }

    public final boolean isRespawnEnabled() {
        return this.respawnEnabled;
    }

    public final void setSpawnPosition(Position position) {
        this.spawnPosition = position;
    }

    public final Position getSpawnPosition() {
        return this.spawnPosition;
    }

    public final void setSpawnX(int n) {
        this.spawnX = n;
    }

    public final int getSpawnX() {
        return this.spawnX;
    }

    public final void setSpawnY(int n) {
        this.spawnY = n;
    }

    public final int getSpawnY() {
        return this.spawnY;
    }

    @Override
    public final int getCurrentHitpoints() {
        return this.currentHitpoints;
    }

    @Override
    public final int getMaxHitpoints() {
        return this.definition.getHitpoints();
    }

    @Override
    public final int getDeathAnimationId() {
        return this.definition.getDeathAnimationId();
    }

    public final int getAttackSoundId() {
        return this.definition.getAttackSoundId();
    }

    public final int getHitSoundId() {
        return this.definition.getHitSoundId();
    }

    public final int getDeathSoundId() {
        return this.definition.getDeathSoundId();
    }

    @Override
    public final int getBlockAnimationId() {
        return this.definition.getBlockAnimationId();
    }

    @Override
    public final int getDeathDelayTicks() {
        return this.combatDefinition.getDeathDelayTicks();
    }

    @Override
    public final int getAttackLevelFor(CombatType combatType) {
        return this.definition.getCombatLevel() / 2;
    }

    @Override
    public final int getDefenceLevelFor(CombatType combatType) {
        return this.definition.getCombatLevel() / 2;
    }

    public final int getCurrentDefenceLevel() {
        return this.currentDefenceLevel;
    }

    public final int getCurrentMagicLevel() {
        return this.currentMagicLevel;
    }

    public final int getCurrentRangedLevel() {
        return this.currentRangedLevel;
    }

    public final int getCurrentAttackLevel() {
        return this.currentAttackLevel;
    }

    public final int getCurrentStrengthLevel() {
        return this.currentStrengthLevel;
    }

    public final int getBaseDefenceLevel() {
        return this.definition.getDefenceLevel();
    }

    public final int getBaseMagicLevel() {
        return this.definition.getMagicLevel();
    }

    public final int getBaseRangedLevel() {
        return this.definition.getRangedLevel();
    }

    public final int getBaseAttackLevel() {
        return this.definition.getAttackLevel();
    }

    public final int getBaseStrengthLevel() {
        return this.definition.getStrengthLevel();
    }

    public final int getDefenceBonus(int n) {
        return this.definition.getDefenceBonus(n);
    }

    public final int getMeleeAttackBonus() {
        return this.definition.getMeleeAttackBonus();
    }

    public final int getMagicAttackBonus() {
        return this.definition.getMagicAttackBonus();
    }

    public final int getRangedAttackBonus() {
        return this.definition.getRangedAttackBonus();
    }

    @Override
    public final boolean isProtectedFrom(CombatType combatType) {
        if (combatType == CombatType.MELEE) {
            return this.definition.isProtectedFromMelee();
        }
        if (combatType == CombatType.RANGED) {
            return this.definition.isProtectedFromRanged();
        }
        if (combatType == CombatType.MAGIC) {
            return this.definition.isProtectedFromMagic();
        }
        return false;
    }

    @Override
    public final void moveTo(Position position) {
        this.active = false;
        this.b = true;
        this.setPosition(position);
        this.getMovementQueue().clear();
        this.active = true;
    }

    @Override
    public final void setCurrentHitpoints(int n) {
        this.currentHitpoints = n;
    }

    public final void setCurrentAttackLevel(int n) {
        this.currentAttackLevel = n;
    }

    public final void setCurrentStrengthLevel(int n) {
        this.currentStrengthLevel = n;
    }

    public final void setCurrentDefenceLevel(int n) {
        this.currentDefenceLevel = n;
    }

    public final void setCurrentMagicLevel(int n) {
        this.currentMagicLevel = n;
    }

    public final void setCurrentRangedLevel(int n) {
        this.currentRangedLevel = n;
    }

    private void dropLootForKiller(Entity entity) {
        GroundItem groundItem;
        Object object;
        if (entity.isPlayer()) {
            object = (Player)entity;
            if (((Player)object).botEnabled) {
                ((Player)object).botLootGroundItems.clear();
            }
        }
        Npc npc = this;
        ItemStack[] itemStackArray = NpcDropManager.rollDrops(entity, npc.npcId, false);
        int n = itemStackArray.length;
        int n2 = 0;
        while (n2 < n) {
            object = itemStackArray[n2];
            if (object != null && ((ItemStack)object).getId() != -1 && ((ItemStack)object).getAmount() > 0) {
                groundItem = null;
                if (((ItemStack)object).getAmount() > 1 && !((ItemStack)object).getDefinition().isNote() && !((ItemStack)object).getDefinition().isStackable()) {
                    int n3 = 0;
                    while (n3 < ((ItemStack)object).getAmount()) {
                        groundItem = new GroundItem(new ItemStack(((ItemStack)object).getId(), 1), this, entity, this.getDeathPosition());
                        GroundItemManager.getInstance().spawn(groundItem);
                        ++n3;
                    }
                } else {
                    groundItem = new GroundItem(new ItemStack(((ItemStack)object).getId(), ((ItemStack)object).getAmount() <= 0 ? 1 : ((ItemStack)object).getAmount()), this, entity, this.getDeathPosition());
                    GroundItemManager.getInstance().spawn(groundItem);
                }
                if (entity.isPlayer()) {
                    Player player = (Player)entity;
                    if (player.botEnabled && player.botCombatState != null && player.botCombatState.equals("wait for loot")) {
                        player.botLootGroundItems.add(groundItem);
                    }
                }
            }
            ++n2;
        }
        if (entity.isPlayer()) {
            object = (Player)entity;
            if (((Player)object).cd) {
                Object object2 = object;
                ((Player)object2).packetSender.sendGameMessage("Your ring of wealth shines more brightly!");
                ((Player)object).cd = false;
            }
            if (((Player)object).botEnabled) {
                if (((Player)object).botCombatStyle == 1) {
                    int n4 = ((Player)object).getEquipmentManager().getItemIdAtSlot(3);
                    n = 0;
                    if (n4 > 0) {
                        n = ItemDefinition.forId(n4).isStackable() ? 1 : 0;
                    }
                    if (((Player)object).getEquipmentManager().getItemIdAtSlot(13) != 0 || n != 0) {
                        int n5 = n != 0 ? 3 : 13;
                        GroundItemManager.getInstance();
                        groundItem = GroundItemManager.findVisibleItem((Player)object, ((Player)object).getEquipmentManager().getItemIdAtSlot(n5), this.getPosition());
                        if (groundItem != null) {
                            ((Player)object).botLootGroundItems.add(groundItem);
                        }
                    }
                }
                if (((Player)object).botLootGroundItems.size() > 0) {
                    ((Player)object).botCombatState = "loot items";
                    BotCombatHelper.processBotLootQueue((Player)object);
                    return;
                }
                if (((Player)object).currentBotTask != null) {
                    ((Player)object).interactWithBotNpcTargets(((Player)object).botInteractionTargetIds);
                }
            }
        }
    }

    @Override
    public final void dropDeathItems(Entity entity) {
        if (entity != null) {
            Object object;
            Player player;
            if (entity.isPlayer()) {
                player = (Player)entity;
                if (player.currentGroup != null) {
                    entity = player.currentGroup.selectLootRecipient(player, this.getDeathPosition());
                }
            }
            this.dropLootForKiller(entity);
            if (entity.isPlayer()) {
                player = (Player)entity;
                if (player.getSlayerManager().slayerMasterId == 3887 && !player.getSlayerManager().slayerTaskName.equalsIgnoreCase("") && player.isInWilderness()) {
                    int n;
                    object = this;
                    object = ((Npc)object).definition.getName().toLowerCase();
                    if (((String)object).contains(player.getSlayerManager().slayerTaskName) && GameUtil.randomInt(n = player.skulled ? 1 : 2) == 0) {
                        this.dropLootForKiller(entity);
                    }
                }
            }
            if (entity.isPlayer()) {
                object = this;
                ((Player)entity).getQuestManager().handleNpcDeathDrop(((Npc)object).npcId, (Player)entity, this.getDeathPosition());
                AbyssManager.rollAbyssPouchDrop((Player)entity, this);
                ClueKeyHandler.dropRequiredKeyFromNpc((Player)entity, this);
                Player.rollActionReward();
            }
        }
    }

    public final NpcCombatDefinition getCombatDefinition() {
        return this.combatDefinition;
    }

    public final NpcDefinition getDefinition() {
        return this.definition;
    }

    public final int getFacingDirection() {
        return this.facingDirection;
    }

    public final void setFacingDirection(int n) {
        this.facingDirection = n;
    }

    public static Npc findByDefinitionId(int n) {
        Npc npc = null;
        Npc[] npcArray = World.getNpcs();
        int n2 = npcArray.length;
        int n3 = 0;
        while (n3 < n2) {
            Npc npc2 = npcArray[n3];
            if (npc2 != null) {
                Npc npc3;
                if (npc2.isDead()) {
                    npc3 = npc2;
                    if (npc3.definition.getId() == n) {
                        npc = npc2;
                    }
                } else {
                    npc3 = npc2;
                    if (npc3.definition.getId() == n) {
                        return npc2;
                    }
                }
            }
            ++n3;
        }
        return npc;
    }

    public static Npc[] findActiveInArea(RectangularArea rectangularArea) {
        ArrayList<Npc> arrayList = new ArrayList<Npc>();
        Npc[] npcArray = World.getNpcs();
        int n = npcArray.length;
        int n2 = 0;
        while (n2 < n) {
            Npc npc = npcArray[n2];
            if (npc != null && !npc.isDead() && rectangularArea.contains(npc.getPosition())) {
                arrayList.add(npc);
            }
            ++n2;
        }
        return arrayList.toArray(new Npc[arrayList.size()]);
    }

    public static Npc findByDefinitionIdAtPosition(int n, Position position) {
        Npc[] npcArray = World.getNpcs();
        int n2 = npcArray.length;
        int n3 = 0;
        while (n3 < n2) {
            Npc npc = npcArray[n3];
            if (npc != null) {
                Npc npc2 = npc;
                if (npc2.definition.getId() == n && npc.getPosition().equals(position)) {
                    return npc;
                }
            }
            ++n3;
        }
        return null;
    }

    public static void refreshNearbyTransformedNpcs(Player player) {
        Npc[] npcArray = World.getNpcs();
        int n = npcArray.length;
        int n2 = 0;
        while (n2 < n) {
            Npc npc = npcArray[n2];
            if (npc != null && GameUtil.isWithinDistance(player.getPosition(), npc.getPosition(), 25) && player.getPosition().getPlane() == npc.getPosition().getPlane()) {
                Npc npc2 = npc;
                if (npc2.transformTicksRemaining > 0) {
                    npc.getUpdateState().setUpdateRequired(true);
                }
            }
            ++n2;
        }
    }

    public final int getTransformTicksRemaining() {
        return this.transformTicksRemaining;
    }

    public final boolean isInteractable() {
        return this.interactable;
    }

    public final boolean canTraverseStep(int n, int n2) {
        return this.canStepToOffset(n, n2);
    }

    public final boolean wouldCollideWithNpc(int n, int n2) {
        Npc[] npcArray = World.getNpcs();
        int n3 = npcArray.length;
        int n4 = 0;
        while (n4 < n3) {
            Npc npc = npcArray[n4];
            if (npc != null && npc != this && !this.isDead() && !npc.isDead() && npc.getPosition().getPlane() == this.getPosition().getPlane() && GameUtil.isWithinDistance(this.getPosition().getX(), this.getPosition().getY(), npc.getPosition().getX(), npc.getPosition().getY(), npc.getSize() + this.getSize())) {
                boolean bl;
                block8: {
                    int n5 = n2;
                    int n6 = n;
                    Npc npc2 = npc;
                    npc = this;
                    int n7 = npc.getPosition().getX();
                    while (n7 < npc.getPosition().getX() + npc.getSize()) {
                        int n8 = npc.getPosition().getY();
                        while (n8 < npc.getPosition().getY() + npc.getSize()) {
                            int n9 = npc2.getPosition().getX();
                            while (n9 < npc2.getPosition().getX() + npc2.getSize()) {
                                int n10 = npc2.getPosition().getY();
                                while (n10 < npc2.getPosition().getY() + npc2.getSize()) {
                                    if (n9 == n7 + n6 && n10 == n8 + n5) {
                                        bl = true;
                                        break block8;
                                    }
                                    ++n10;
                                }
                                ++n9;
                            }
                            ++n8;
                        }
                        ++n7;
                    }
                    bl = false;
                }
                if (bl) {
                    return true;
                }
            }
            ++n4;
        }
        return false;
    }

    public static boolean wouldCollideWithPlayer(Npc npc, Player player, int n, int n2) {
        int n3 = npc.getPosition().getX();
        while (n3 < npc.getPosition().getX() + npc.getSize()) {
            int n4 = npc.getPosition().getY();
            while (n4 < npc.getPosition().getY() + npc.getSize()) {
                int n5 = player.getPosition().getX();
                while (n5 < player.getPosition().getX() + player.getSize()) {
                    int n6 = player.getPosition().getY();
                    while (n6 < player.getPosition().getY() + player.getSize()) {
                        if (n5 == n3 + n && n6 == n4 + n2) {
                            return true;
                        }
                        ++n6;
                    }
                    ++n5;
                }
                ++n4;
            }
            ++n3;
        }
        return false;
    }

    public final boolean isFacingInteractionPosition(Position position, int n) {
        n = this.getPosition().getX();
        int n2 = this.getPosition().getY();
        int n3 = this.getPosition().getPlane();
        switch (this.facingDirection) {
            case 0: {
                return position.equals(new Position(n, n2 - 2, n3));
            }
            case 2: {
                return position.equals(new Position(n, n2 + 2, n3));
            }
            case 3: {
                return position.equals(new Position(n, n2 - 2, n3));
            }
            case 4: {
                return position.equals(new Position(n + 2, n2, n3));
            }
            case 5: {
                return position.equals(new Position(n - 2, n2, n3));
            }
        }
        return GameUtil.isWithinDistance(this.getPosition(), position, 1);
    }

    public final boolean isWithinInteractionDistance(Position position, int n) {
        return GameUtil.isWithinDistance(this.getPosition(), position, n);
    }

    public final Position getFacingInteractionPosition(int n) {
        n = this.getPosition().getX();
        int n2 = this.getPosition().getY();
        int n3 = this.getPosition().getPlane();
        switch (this.facingDirection) {
            case 0: {
                return new Position(n, n2 - 2, n3);
            }
            case 2: {
                return new Position(n, n2 + 2, n3);
            }
            case 3: {
                return new Position(n, n2 - 2, n3);
            }
            case 4: {
                return new Position(n + 2, n2, n3);
            }
            case 5: {
                return new Position(n - 2, n2, n3);
            }
        }
        return new Position(n, n2 + 2, n3);
    }

    public final boolean isBanker() {
        if (this.facingDirection == 1) {
            return false;
        }
        Npc npc = this;
        if (npc.npcId != 166) {
            npc = this;
            if (npc.npcId != 494) {
                npc = this;
                if (npc.npcId != 495) {
                    npc = this;
                    if (npc.npcId != 496) {
                        npc = this;
                        if (npc.npcId != 499) {
                            npc = this;
                            if (npc.npcId != 2619) {
                                return false;
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    public static boolean isUndead(Entity entity) {
        if (entity.isPlayer()) {
            return false;
        }
        String npcName = ((Npc)entity).definition.getName().toLowerCase();
        return npcName.contains("spectre") || npcName.contains("banshee") || npcName.contains("shade") || npcName.contains("zombie") || npcName.contains("skeleton") || npcName.contains("ghost") || npcName.contains("crawling hand") || npcName.contains("skeletal hand") || npcName.contains("zombie hand") || npcName.contains("zogre") || npcName.contains("skorge") || npcName.contains("ankous");
    }

}

