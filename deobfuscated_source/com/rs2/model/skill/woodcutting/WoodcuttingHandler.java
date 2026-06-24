/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.woodcutting;

import com.rs2.ServerSettings;
import com.rs2.model.Entity;
import com.rs2.model.GameplayHelper;
import com.rs2.model.Position;
import com.rs2.model.World;
import com.rs2.model.c.ProjectileDefinition;
import com.rs2.model.combat.ProjectileTiming;
import com.rs2.model.item.ItemStack;
import com.rs2.model.npc.Npc;
import com.rs2.model.npc.NpcDefinition;
import com.rs2.model.objects.DynamicObject;
import com.rs2.model.objects.ObjectManager;
import com.rs2.model.objects.WorldObject;
import com.rs2.model.player.Player;
import com.rs2.model.skill.GatheringToolDefinition;
import com.rs2.model.skill.ItemCombinationHandler;
import com.rs2.model.skill.SkillActionHelper;
import com.rs2.model.skill.woodcutting.TreeDefinition;
import com.rs2.model.skill.woodcutting.WoodcuttingTask;
import com.rs2.model.task.CycleEventHandler;
import com.rs2.util.GameUtil;
import com.rs2.util.GameplayTrace;

public class WoodcuttingHandler {
    private int a;
    private int b;
    private byte c;
    private byte d;
    private ProjectileDefinition e;
    private Position f;
    private int g;

    public static void startWoodcutting(Player player, int n, int n2, int n3, boolean bl) {
        Object object;
        Object object2;
        Object object3;
        if (GameplayTrace.enabled()) {
            GameplayTrace.log("woodcutting enter player=" + GameplayTrace.describe(player) + " targetId=" + n + " x=" + n2 + " y=" + n3 + " npcTree=" + bl);
        }
        if (!bl) {
            ObjectManager.getInstance();
            object3 = ObjectManager.findDynamicObjectAt(n2, n3, player.getPosition().getPlane());
            if (object3 != null && ((DynamicObject)object3).getWorldObject().getObjectId() != n) {
                if (GameplayTrace.enabled()) {
                    GameplayTrace.log("woodcutting blocked dynamic-object-mismatch player=" + GameplayTrace.describe(player) + " targetId=" + n + " dynamicId=" + ((DynamicObject)object3).getWorldObject().getObjectId() + " x=" + n2 + " y=" + n3);
                }
                if (player.botEnabled) {
                    player.interactWithBotObjectTargets(player.botInteractionTargetIds);
                }
                return;
            }
        } else {
            object3 = Npc.findByDefinitionIdAtPosition(n, new Position(n2, n3, player.getPosition().getPlane()));
            if (object3 == null) {
                if (GameplayTrace.enabled()) {
                    GameplayTrace.log("woodcutting blocked missing-npc-tree player=" + GameplayTrace.describe(player) + " targetId=" + n + " x=" + n2 + " y=" + n3);
                }
                return;
            }
        }
        if ((object3 = TreeDefinition.forTargetId(n, bl)) == null) {
            if (GameplayTrace.enabled()) {
                GameplayTrace.log("woodcutting blocked missing-tree-definition player=" + GameplayTrace.describe(player) + " targetId=" + n + " x=" + n2 + " y=" + n3 + " npcTree=" + bl);
            }
            return;
        }
        if (!ServerSettings.woodcuttingEnabled) {
            if (GameplayTrace.enabled()) {
                GameplayTrace.log("woodcutting blocked disabled player=" + GameplayTrace.describe(player) + " tree=" + object3);
            }
            Player player2 = player;
            player2.packetSender.sendGameMessage("This skill is currently disabled.");
            return;
        }
        GatheringToolDefinition gatheringToolDefinition = ItemCombinationHandler.findUsableGatheringTool(player, 8);
        if (gatheringToolDefinition == null) {
            if (GameplayTrace.enabled()) {
                GameplayTrace.log("woodcutting blocked no-axe player=" + GameplayTrace.describe(player) + " tree=" + object3);
            }
            Player player3 = player;
            player3.packetSender.sendGameMessage("You do not have an axe which you have the woodcutting level to use.");
            if (player.botEnabled) {
                player.currentBotTask.startWalkToBank(player);
            }
            return;
        }
        if (player.getSkillManager().getCurrentLevels()[8] < ((TreeDefinition)((Object)object3)).getRequiredLevel()) {
            if (GameplayTrace.enabled()) {
                GameplayTrace.log("woodcutting blocked level player=" + GameplayTrace.describe(player) + " current=" + player.getSkillManager().getCurrentLevels()[8] + " required=" + ((TreeDefinition)((Object)object3)).getRequiredLevel() + " tree=" + object3);
            }
            Player player4 = player;
            player4.packetSender.sendGameMessage("You need a Woodcutting level of " + ((TreeDefinition)((Object)object3)).getRequiredLevel() + " to cut this tree.");
            return;
        }
        if (((TreeDefinition)((Object)object3)).getLogItemId() != -1) {
            object2 = new ItemStack(((TreeDefinition)((Object)object3)).getLogItemId(), 1);
            if (player.getInventoryManager().getContainer().getFirstFreeSlot() == -1) {
                if (GameplayTrace.enabled()) {
                    GameplayTrace.log("woodcutting blocked full-inventory player=" + GameplayTrace.describe(player) + " tree=" + object3);
                }
                Player player5 = player;
                player5.packetSender.sendGameMessage("Your inventory is too full to hold any more " + ((ItemStack)object2).getDefinition().getName().toLowerCase() + ".");
                player5 = player;
                player5.packetSender.sendSoundEffect(1878, 1, 0);
                if (player.botEnabled) {
                    player.currentBotTask.startWalkToBank(player);
                }
                return;
            }
        }
        if (player.getQuestState(0) != 1) {
            player.getDialogueManager().showTutorialInstructionOverlay("Please wait.", "", "Your character is now attempting to cut down the tree. Sit back", "for a moment while he does all the hard work.", "", true);
        } else {
            object = player;
            ((Player)object).packetSender.sendGameMessage("You swing your axe at the " + (object3 == TreeDefinition.VINES ? "vines" : "tree") + ".");
        }
        if (object3 == TreeDefinition.VINES && player.botEnabled) {
            player.botRouteActionPending = true;
        }
        player.getUpdateState().setAnimation(gatheringToolDefinition.getGatherAnimationId(), 0);
        player.gatheringHazardCounter = 0;
        if (player.getQuestState(0) == 1 && ServerSettings.randomEventsMode == 0 && GameUtil.randomInt(800) == 0 && ((TreeDefinition)((Object)object3)).getEntNpcIds() != null && !player.botEnabled && !player.isInTutorialIsland()) {
            int n4 = TreeDefinition.getObjectIdIndex(n, (TreeDefinition)((Object)object3)) >= ((TreeDefinition)((Object)object3)).getEntNpcIds().length ? 0 : TreeDefinition.getObjectIdIndex(n, (TreeDefinition)((Object)object3));
            n4 = ((TreeDefinition)((Object)object3)).getEntNpcIds()[n4];
            if (NpcDefinition.isDefined(n4)) {
                object2 = new Npc(n4);
                object = SkillActionHelper.findWorldObjectById(n, n2, n3, player.getPosition().getPlane());
                ObjectManager.getInstance().addDynamicObject(new DynamicObject(ServerSettings.placeholderObjectId, n2, n3, player.getPosition().getPlane(), ((WorldObject)object).getOrientation(), ((WorldObject)object).getType(), n, 15), true);
                GameplayHelper.spawnNpcWithRemovalDelay((Npc)object2, n2, n3, player.getPosition().getPlane(), 15);
            }
        }
        int n5 = player.nextActionSequence();
        if (GameplayTrace.enabled()) {
            GameplayTrace.log("woodcutting scheduled player=" + GameplayTrace.describe(player) + " seq=" + n5 + " tree=" + object3 + " objectId=" + n + " x=" + n2 + " y=" + n3 + " animation=" + gatheringToolDefinition.getGatherAnimationId());
        }
        player.setActiveCycleEvent(new WoodcuttingTask(player, n5, (TreeDefinition)((Object)object3), n2, n3, gatheringToolDefinition, n));
        CycleEventHandler.getInstance().schedule(player, player.getActiveCycleEvent(), 4);
    }

    public WoodcuttingHandler(Position position, int n, Position position2, int n2, ProjectileDefinition projectileDefinition) {
        this.f = position;
        this.g = n;
        this.a = n2;
        this.e = projectileDefinition;
        n = GameUtil.getDistance(position, position2);
        n2 = projectileDefinition.getTiming().getSpeed();
        this.b = projectileDefinition.getTiming().getStartDelay() + n2 + n * 5;
        this.c = (byte)(position2.getX() - position.getX());
        this.d = (byte)(position2.getY() - position.getY());
    }

    public WoodcuttingHandler(Entity entity, Entity entity2, Position position, ProjectileDefinition projectileDefinition) {
        this(entity.getPosition(), entity.getSize(), position, entity2.isPlayer() ? -entity2.getIndex() - 1 : entity2.getIndex() + 1, projectileDefinition);
    }

    public WoodcuttingHandler(Entity entity, Entity entity2, ProjectileDefinition projectileDefinition) {
        this(entity, entity2, entity2.getPosition(), projectileDefinition);
    }

    public void sendProjectileToNearbyPlayers() {
        if (this.e.getProjectileId() == -1) {
            return;
        }
        ProjectileTiming projectileTiming = this.e.getTiming();
        Player[] playerArray = World.getPlayers();
        int n = playerArray.length;
        int n2 = 0;
        while (n2 < n) {
            Player player = playerArray[n2];
            if (player != null && this.f.isWithinViewport(player.getPosition())) {
                player.packetSender.sendProjectile(this.f, this.g, this.a, this.c, this.d, this.e.getProjectileId(), projectileTiming.getStartDelay(), this.b, projectileTiming.getStartHeight(), projectileTiming.getEndHeight(), projectileTiming.getSlope());
            }
            ++n2;
        }
    }
}
