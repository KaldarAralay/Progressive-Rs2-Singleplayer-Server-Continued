/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.interaction;

import com.rs2.model.EntityTargetMovement;
import com.rs2.model.GameplayHelper;
import com.rs2.model.Position;
import com.rs2.model.World;
import com.rs2.model.dialogue.DialogueManager;
import com.rs2.model.interaction.FirstNpcActionTask;
import com.rs2.model.interaction.FirstObjectActionTask;
import com.rs2.model.interaction.FourthNpcActionTask;
import com.rs2.model.interaction.FourthObjectActionTask;
import com.rs2.model.interaction.InteractionType;
import com.rs2.model.interaction.ItemOnNpcTask;
import com.rs2.model.interaction.ItemOnObjectTask;
import com.rs2.model.interaction.SecondNpcActionTask;
import com.rs2.model.interaction.SecondObjectActionTask;
import com.rs2.model.interaction.SpellOnObjectTask;
import com.rs2.model.interaction.ThirdNpcActionTask;
import com.rs2.model.interaction.ThirdObjectActionTask;
import com.rs2.model.npc.Npc;
import com.rs2.model.objects.LoadedWorldObject;
import com.rs2.model.objects.ObjectDefinition;
import com.rs2.model.objects.ObjectManager;
import com.rs2.model.objects.WorldObject;
import com.rs2.model.objects.WorldObjectLookup;
import com.rs2.model.player.PetManager;
import com.rs2.model.player.Player;
import com.rs2.model.skill.magic.SpellDefinition;
import com.rs2.util.GameUtil;
import com.rs2.util.GameplayTrace;
import com.rs2.util.path.ProjectileCollisionMap;

public final class InteractionDispatcher {
    private static InteractionType currentInteractionType = InteractionType.FIRST_OBJECT;

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static void dispatchCurrentInteraction(Player player) {
        switch (currentInteractionType) {
            case FIRST_OBJECT: {
                String string;
                Object object;
                int n = player.getInteractionTargetId();
                int n2 = player.getInteractionTargetX();
                int n3 = player.getInteractionTargetY();
                int n4 = player.getInteractionTargetPlane() % 4;
                if (ObjectDefinition.forId(n) != null) {
                    object = ObjectDefinition.forId(n);
                    string = ((ObjectDefinition)object).name.toLowerCase();
                } else {
                    string = "";
                }
                object = string;
                int n5 = player.nextActionSequence();
                if (GameplayTrace.enabled()) {
                    GameplayTrace.log("schedule first-object task player=" + GameplayTrace.describe(player) + " seq=" + n5 + " objectId=" + n + " name=" + object + " x=" + n2 + " y=" + n3 + " plane=" + n4);
                }
                World.scheduleTickTask(new FirstObjectActionTask(1, true, player, n5, n, n2, n3, n4, (String)object));
                return;
            }
            case SECOND_OBJECT: {
                int n = player.getInteractionTargetId();
                int n6 = player.getInteractionTargetX();
                int n7 = player.getInteractionTargetY();
                int n8 = player.getInteractionTargetPlane();
                int n9 = player.nextActionSequence();
                World.scheduleTickTask(new SecondObjectActionTask(1, true, player, n9, n, n6, n7, n8));
                return;
            }
            case THIRD_OBJECT: {
                int n = player.getInteractionTargetId();
                int n10 = player.getInteractionTargetX();
                int n11 = player.getInteractionTargetY();
                int n12 = player.getInteractionTargetPlane();
                int n13 = player.nextActionSequence();
                World.scheduleTickTask(new ThirdObjectActionTask(1, true, player, n13, n, n10, n11, n12));
                return;
            }
            case FOURTH_OBJECT: {
                int n = player.getInteractionTargetId();
                int n14 = player.getInteractionTargetX();
                int n15 = player.getInteractionTargetY();
                int n16 = player.getInteractionTargetPlane();
                int n17 = player.nextActionSequence();
                World.scheduleTickTask(new FourthObjectActionTask(1, true, player, n17, n, n14, n15, n16));
                return;
            }
            case FIRST_NPC: {
                int n;
                Npc npc = World.getNpcs()[player.getInteractionTargetIndex()];
                int n18 = player.nextActionSequence();
                if (npc == null || !npc.isInteractable()) return;
                int[][] nArray = PetManager.petItemNpcPairs;
                int n19 = 0;
                while (n19 < 6) {
                    int[] nArray2 = nArray[n19];
                    if (player.getInteractionTargetId() == nArray2[1]) {
                        player.getPetManager().pickupPet();
                        return;
                    }
                    ++n19;
                }
                if (npc.getOwnerPlayer() != null && (npc.getOwnerPlayer() != player || npc.getCombatTarget() != null)) {
                    Player player2 = player;
                    player2.packetSender.sendGameMessage("This npc is not interested in talking with you right now.");
                    return;
                }
                if (npc.isInApeAtoll() && (n = GameplayHelper.getNpcShopId(player.getInteractionTargetId())) >= 0 && !player.ez()) {
                    Player player3 = player;
                    player3.packetSender.sendGameMessage("This npc is not interested in talking with you right now.");
                    return;
                }
                if (npc.getNpcId() == 1469 && player.es()) {
                    npc.getUpdateState().setFaceEntity(player.getEncodedIndex());
                    player.setInteractionTarget(npc);
                    player.getUpdateState().setFaceEntity(npc.getEncodedIndex());
                    DialogueManager.startDialogue(player, player.getInteractionTargetId());
                    EntityTargetMovement.clearMovementTarget(player);
                    return;
                }
                World.scheduleTickTask(new FirstNpcActionTask(1, true, player, n18, npc));
                return;
            }
            case SECOND_NPC: {
                Npc npc = World.getNpcs()[player.getInteractionTargetIndex()];
                int n = player.nextActionSequence();
                if (npc == null || !npc.isInteractable()) return;
                World.scheduleTickTask(new SecondNpcActionTask(1, true, player, n, npc));
                return;
            }
            case THIRD_NPC: {
                Npc npc = World.getNpcs()[player.getInteractionTargetIndex()];
                int n = player.nextActionSequence();
                if (npc == null || !npc.isInteractable()) return;
                World.scheduleTickTask(new ThirdNpcActionTask(1, true, player, n, npc));
                return;
            }
            case FOURTH_NPC: {
                Npc npc = World.getNpcs()[player.getInteractionTargetIndex()];
                int n = player.nextActionSequence();
                if (npc == null || !npc.isInteractable()) return;
                World.scheduleTickTask(new FourthNpcActionTask(1, true, player, n, npc));
                return;
            }
            case ITEM_ON_OBJECT: {
                int n = player.getInteractionTargetX();
                int n20 = player.getInteractionTargetY();
                int n21 = player.getInteractionTargetPlane();
                int n22 = player.getInteractionTargetId();
                int n23 = player.getSelectedItemId();
                int n24 = player.nextActionSequence();
                LoadedWorldObject loadedWorldObject = WorldObjectLookup.findObjectByIdAt(n22, n, n20, n21);
                if (loadedWorldObject == null) {
                    ObjectManager.getInstance();
                    if (ObjectManager.findDynamicObjectAt(n, n20, n21) == null) {
                        if (GameplayTrace.enabled()) {
                            GameplayTrace.log("item-on-object missing-object player=" + GameplayTrace.describe(player) + " itemId=" + n23 + " objectId=" + n22 + " x=" + n + " y=" + n20 + " plane=" + n21);
                        }
                        return;
                    }
                }
                if (GameplayTrace.enabled()) {
                    GameplayTrace.log("schedule item-on-object task player=" + GameplayTrace.describe(player) + " seq=" + n24 + " itemId=" + n23 + " objectId=" + n22 + " x=" + n + " y=" + n20 + " plane=" + n21);
                }
                World.scheduleTickTask(new ItemOnObjectTask(1, true, player, n24, n22, n, n20, n21, n23));
                return;
            }
            case ITEM_ON_NPC: {
                int n = player.getSelectedItemId();
                Npc npc = World.getNpcs()[player.getInteractionTargetIndex()];
                int n25 = player.nextActionSequence();
                int n26 = player.getSelectedItemSlot();
                if (npc == null || !npc.isInteractable()) return;
                World.scheduleTickTask(new ItemOnNpcTask(1, true, player, n25, npc, n, n26));
                return;
            }
            case SPELL_ON_OBJECT: {
                SpellDefinition spellDefinition;
                int n = player.getInteractionTargetX();
                int n27 = player.getInteractionTargetY();
                int n28 = player.getInteractionTargetPlane();
                int n29 = player.getInteractionTargetId();
                int n30 = player.nextActionSequence();
                int n31 = player.getInteractionSpellButtonId();
                LoadedWorldObject loadedWorldObject = WorldObjectLookup.findObjectByIdAt(n29, n, n27, n28);
                if (loadedWorldObject == null) {
                    ObjectManager.getInstance();
                    if (ObjectManager.findDynamicObjectAt(n, n27, n28) == null) return;
                }
                if ((spellDefinition = (SpellDefinition)((Object)player.getSpellbook().getSpellByButtonId().get(n31))) == null) return;
                World.scheduleTickTask(new SpellOnObjectTask(1, true, player, n30, n29, n, n27, n28, spellDefinition));
            }
        }
    }

    public static boolean canReachObjectInteraction(Position position, Position position2, WorldObject worldObject) {
        if (worldObject.getObjectId() == 2638) {
            return true;
        }
        ProjectileCollisionMap.removeObjectCollisionForReachability(worldObject.getObjectId(), worldObject.getPosition().getX(), worldObject.getPosition().getY(), worldObject.getPosition().getPlane(), worldObject.getOrientation(), worldObject.getType());
        boolean bl = GameUtil.hasClearPath(position, position2, false);
        ProjectileCollisionMap.addObjectCollision(worldObject.getObjectId(), worldObject.getPosition().getX(), worldObject.getPosition().getY(), worldObject.getPosition().getPlane(), worldObject.getOrientation(), worldObject.getType(), true);
        return bl;
    }

    public static void setCurrentInteractionType(InteractionType interactionType) {
        currentInteractionType = interactionType;
    }

    static /* synthetic */ boolean canReachObjectInteraction(Player player, Position position, WorldObject worldObject) {
        if (worldObject.getObjectId() == 2638) {
            return true;
        }
        ProjectileCollisionMap.removeObjectCollisionForReachability(worldObject.getObjectId(), worldObject.getPosition().getX(), worldObject.getPosition().getY(), worldObject.getPosition().getPlane(), worldObject.getOrientation(), worldObject.getType());
        boolean bl = GameUtil.hasClearPath(player.getPosition(), position, false);
        ProjectileCollisionMap.addObjectCollision(worldObject.getObjectId(), worldObject.getPosition().getX(), worldObject.getPosition().getY(), worldObject.getPosition().getPlane(), worldObject.getOrientation(), worldObject.getType(), true);
        return bl;
    }
}
