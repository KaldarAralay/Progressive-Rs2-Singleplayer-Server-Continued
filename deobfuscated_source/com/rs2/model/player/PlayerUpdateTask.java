/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.player;

import com.rs2.bot.BotTaskDefinition;
import com.rs2.bot.combat.BotPvpCombatHandler;
import com.rs2.model.Entity;
import com.rs2.model.EntityUpdateState;
import com.rs2.model.Position;
import com.rs2.model.World;
import com.rs2.model.combat.WeaponProfile;
import com.rs2.model.ground.GroundItemManager;
import com.rs2.model.item.ItemDefinition;
import com.rs2.model.item.ItemStack;
import com.rs2.model.objects.ObjectManager;
import com.rs2.model.player.DelayedBotLevelReplyTask;
import com.rs2.model.player.DelayedBotTradeRequestTask;
import com.rs2.model.player.Player;
import com.rs2.model.player.PlayerConnectionState;
import com.rs2.model.skill.SkillManager;
import com.rs2.model.task.TickTask;
import com.rs2.net.packet.AccessMode;
import com.rs2.net.packet.ByteOrder;
import com.rs2.net.packet.ByteTransform;
import com.rs2.net.packet.PacketBuffer;
import com.rs2.net.packet.PacketWriter;
import com.rs2.util.GameUtil;
import java.util.Iterator;

public class PlayerUpdateTask {
    int itemContainerInterfaceId;
    private int bankTabButtonId;

    private PlayerUpdateTask(int n, int n2, int n3) {
        this.itemContainerInterfaceId = n;
        this.bankTabButtonId = n2;
    }

    public final int getBankTabButtonId() {
        return this.bankTabButtonId;
    }

    /* synthetic */ PlayerUpdateTask(int n, int n2, int n3, byte by) {
        this(n, 19509, 19508);
    }

    PlayerUpdateTask(int n, int n2, int n3, int n4) {
        this(n, 19509, 19508);
    }

    public static void a(Player player) {
        Object object;
        int n;
        int n2 = player.getPosition().getX() - (player.getLastKnownRegionPosition().getRegionX() << 3);
        int n3 = player.getPosition().getY() - (player.getLastKnownRegionPosition().getRegionY() << 3);
        player.I = n2;
        player.J = n3;
        if (n2 < 16 || n2 >= 88 || n3 < 16 || n3 > 88) {
            Player player2 = player;
            player2.packetSender.sendMapRegion();
            player.co = System.currentTimeMillis();
        }
        if (player.isBot) {
            return;
        }
        PacketWriter packetWriter = PacketBuffer.allocateWriter(8192);
        PacketWriter packetWriter2 = PacketBuffer.allocateWriter(4096);
        packetWriter.startVariableShortPacket(player.getOutboundCipher(), 81);
        packetWriter.setAccessMode(AccessMode.BIT_ACCESS);
        PacketWriter packetWriter3 = packetWriter;
        Object object2 = player;
        int n4 = 0;
        boolean updateRequired = ((Entity)object2).getUpdateState().isUpdateRequired();
        if (((Player)object2).isTeleporting()) {
            packetWriter3.writeBoolean(true);
            ((Entity)object2).getPosition();
            n = Position.updateLocalX((Player)object2);
            ((Entity)object2).getPosition();
            int localY = Position.updateLocalY((Player)object2);
            boolean teleportPlacementUpdateRequired = ((Player)object2).isTeleportPlacementUpdateRequired();
            int plane = ((Entity)object2).getPosition().getPlane();
            PacketWriter movementWriter = packetWriter3;
            movementWriter.writeBits(2, 3);
            movementWriter.writeBits(2, plane);
            movementWriter.writeBoolean(teleportPlacementUpdateRequired);
            movementWriter.writeBoolean(updateRequired);
            movementWriter.writeBits(7, localY);
            movementWriter.writeBits(7, n);
        } else {
            n = ((Entity)object2).getWalkDirection();
            int n10 = ((Entity)object2).getRunDirection();
            if (n != -1) {
                packetWriter3.writeBoolean(true);
                if (n10 != -1) {
                    PlayerUpdateTask.a(packetWriter3, n, n10, updateRequired);
                } else {
                    PlayerUpdateTask.a(packetWriter3, n, updateRequired);
                }
            } else if (updateRequired) {
                packetWriter3.writeBoolean(true);
                PacketWriter packetWriter4 = packetWriter3;
                packetWriter4.writeBits(2, 0);
            } else {
                packetWriter3.writeBoolean(false);
            }
        }
        if (player.getUpdateState().isUpdateRequired()) {
            PlayerUpdateTask.a(player, packetWriter2, false, !player.ay, null);
            player.ay = false;
        }
        packetWriter.writeBits(8, player.getLocalPlayers().size());
        Iterator iterator = player.getLocalPlayers().iterator();
        while (iterator.hasNext()) {
            block53: {
                Player player3;
                block54: {
                    Object object3;
                    Object object4;
                    block55: {
                        player3 = (Player)iterator.next();
                        if (!player3.getPosition().isWithinViewport(player.getPosition()) || !player3.eu() || player3.getConnectionState() == PlayerConnectionState.DISCONNECTED || player3.isTeleporting()) break block53;
                        Object object5 = packetWriter;
                        object2 = player3;
                        boolean localUpdateRequired = ((Entity)object2).getUpdateState().isUpdateRequired();
                        int n11 = ((Entity)object2).getWalkDirection();
                        int n12 = ((Entity)object2).getRunDirection();
                        if (n11 != -1) {
                            ((PacketWriter)object5).writeBoolean(true);
                            if (n12 != -1) {
                                PlayerUpdateTask.a((PacketWriter)object5, n11, n12, localUpdateRequired);
                            } else {
                                PlayerUpdateTask.a((PacketWriter)object5, n11, localUpdateRequired);
                            }
                        } else if (localUpdateRequired) {
                            ((PacketWriter)object5).writeBoolean(true);
                            PacketWriter packetWriter5 = (PacketWriter)object5;
                            packetWriter5.writeBits(2, 0);
                        } else {
                            ((PacketWriter)object5).writeBoolean(false);
                        }
                        if (!player3.isBot || player.ey.equals("")) break block54;
                        object5 = player;
                        object2 = player3;
                        n4 = 3 + GameUtil.randomInt(3);
                        object4 = object2;
                        object3 = ((Player)object5).ey;
                        object = "";
                        if (((Player)object2).botMode != 2) break block55;
                        if (!((String)object3).toLowerCase().startsWith("buy") && !((String)object3).toLowerCase().startsWith("sell") || ((Player)object2).botAdvertItemId <= 0) break block54;
                        ItemDefinition itemDefinition = ItemDefinition.forId(((Player)object2).botAdvertItemId);
                        if (itemDefinition.isNote()) {
                            itemDefinition = ItemDefinition.forId(itemDefinition.getUnnotedId());
                        }
                        String string = itemDefinition.getName().toLowerCase();
                        String string2 = itemDefinition.getShortName();
                        string2 = string2 != null ? string2.toLowerCase() : string;
                        if (!((String)object3).contains(string) && !((String)object3).contains(string2)) break block54;
                        if (GameUtil.randomInt(3) == 0) {
                            World.getTaskScheduler().schedule(new DelayedBotTradeRequestTask((Player)object2, n4, (Player)object4, (String)object3, (Player)object5));
                        }
                    }
                    if ((((Player)object2).botMode == 0 || ((Player)object2).botMode == 4) && ((Player)object2).botTaskState.equals("do task") && (((String)object3).toLowerCase().equals("lvls?") || ((String)object3).toLowerCase().equals("lvls") || ((String)object3).toLowerCase().equals("lvl?") || ((String)object3).toLowerCase().equals("lvl") || ((String)object3).toLowerCase().equals("levels?") || ((String)object3).toLowerCase().equals("levels") || ((String)object3).toLowerCase().equals("level?") || ((String)object3).toLowerCase().equals("level"))) {
                        int n13;
                        int n14 = -1;
                        int n15 = -1;
                        object3 = ((Player)object2).currentBotTask;
                        int n16 = BotTaskDefinition.brassKeyTasks.contains(object3) ? 0 : (BotTaskDefinition.shopTasks.contains(object3) ? 1 : (BotTaskDefinition.fishingTasks.contains(object3) ? 2 : (BotTaskDefinition.cookingTasks.contains(object3) ? 3 : (BotTaskDefinition.miningTasks.contains(object3) ? 4 : (BotTaskDefinition.smeltingTasks.contains(object3) ? 5 : (BotTaskDefinition.smithingTasks.contains(object3) ? 6 : (BotTaskDefinition.woodcuttingTasks.contains(object3) ? 7 : (BotTaskDefinition.runecraftingTasks.contains(object3) ? 8 : (BotTaskDefinition.moneyMakingTasks.contains(object3) ? 9 : (BotTaskDefinition.combatTasks.contains(object3) ? 10 : (BotTaskDefinition.sheepShearingTasks.contains(object3) ? 11 : (BotTaskDefinition.spinningTasks.contains(object3) ? 12 : (BotTaskDefinition.tanningTasks.contains(object3) ? 13 : (((Player)object2).currentBotTaskTypeId = BotTaskDefinition.leatherCraftingTasks.contains(object3) ? 14 : -1))))))))))))));
                        if (((Player)object2).currentBotTaskTypeId == 2) {
                            n14 = 10;
                        } else if (((Player)object2).currentBotTaskTypeId == 3) {
                            n14 = 7;
                        } else if (((Player)object2).currentBotTaskTypeId == 4) {
                            n14 = 14;
                        } else if (((Player)object2).currentBotTaskTypeId == 5 || ((Player)object2).currentBotTaskTypeId == 6) {
                            n14 = 13;
                        } else if (((Player)object2).currentBotTaskTypeId == 7) {
                            n14 = 8;
                        } else if (((Player)object2).currentBotTaskTypeId == 8) {
                            n14 = 20;
                        } else if (((Player)object2).currentBotTaskTypeId == 11 || ((Player)object2).currentBotTaskTypeId == 12 || ((Player)object2).currentBotTaskTypeId == 13 || ((Player)object2).currentBotTaskTypeId == 14) {
                            n14 = 12;
                        }
                        if (((Player)object2).currentBotTaskTypeId == 8 && (n13 = ((Player)object2).currentBotTask.getTaskIndexForType(((Player)object2).currentBotTaskTypeId)) == 0) {
                            n14 = 14;
                        }
                        if (n14 != -1) {
                            n15 = ((Player)object2).getSkillManager().getBaseLevel(n14);
                        } else if (((Player)object2).currentBotTaskTypeId == 10) {
                            if (((Player)object2).botActiveCombatStyle == BotPvpCombatHandler.MAGIC_COMBAT_STYLE) {
                                n15 = ((Player)object2).getSkillManager().getBaseLevel(6);
                            } else if (((Player)object2).botActiveCombatStyle == BotPvpCombatHandler.RANGED_COMBAT_STYLE) {
                                n15 = ((Player)object2).getSkillManager().getBaseLevel(4);
                            } else if (((Player)object2).botActiveCombatStyle == 0) {
                                int n17 = ((Player)object2).getSkillManager().getBaseLevel(0);
                                object = String.valueOf(object) + "atk: " + n17 + " ";
                                int n18 = ((Player)object2).getSkillManager().getBaseLevel(1);
                                object = String.valueOf(object) + "def: " + n18 + " ";
                                int n19 = ((Player)object2).getSkillManager().getBaseLevel(2);
                                object = String.valueOf(object) + "str: " + n19 + " ";
                            }
                        }
                        if (n15 != -1) {
                            object = GameUtil.randomInt(10) == 0 ? String.valueOf(n15) + " " + (GameUtil.randomInt(2) == 0 ? "u" : "u?") : String.valueOf(n15);
                        }
                        if (!((String)object).equals("") && GameUtil.randomInt(10) != 0) {
                            Object object6 = object;
                            DelayedBotLevelReplyTask delayedBotLevelReplyTask = new DelayedBotLevelReplyTask((Player)object2, n4, (Player)object4, (String)object6);
                            World.getTaskScheduler().schedule(delayedBotLevelReplyTask);
                        }
                    }
                }
                if (!player3.getUpdateState().isUpdateRequired()) continue;
                PlayerUpdateTask.a(player3, packetWriter2, false, false, player);
                continue;
            }
            packetWriter.writeBoolean(true);
            packetWriter.writeBits(2, 3);
            iterator.remove();
        }
        player.ey = "";
        int n20 = 0;
        int n21 = 0;
        while (n21 < World.getPlayers().length) {
            if (n20 > 15 || player.getLocalPlayers().size() >= 255) break;
            Player player4 = World.getPlayers()[n21];
            if (player4 != null && player4 != player && player4.getConnectionState() != PlayerConnectionState.DISCONNECTED && !player.getLocalPlayers().contains(player4) && player4.getPosition().isWithinViewport(player.getPosition()) && player4.eu()) {
                ++n20;
                player.getLocalPlayers().add(player4);
                Player player5 = player4;
                Player player6 = player;
                object2 = packetWriter;
                ((PacketWriter)object2).writeBits(11, player5.getIndex());
                ((PacketWriter)object2).writeBoolean(true);
                ((PacketWriter)object2).writeBoolean(true);
                Position position = GameUtil.getDelta(player6.getPosition(), player5.getPosition());
                ((PacketWriter)object2).writeBits(5, position.getY());
                ((PacketWriter)object2).writeBits(5, position.getX());
                PlayerUpdateTask.a(player4, packetWriter2, true, false, player);
            }
            ++n21;
        }
        if (packetWriter2.getBuffer().position() > 0) {
            packetWriter.writeBits(11, 2047);
            packetWriter.setAccessMode(AccessMode.BYTE_ACCESS);
            packetWriter.writeBuffer(packetWriter2.getBuffer());
        } else {
            packetWriter.setAccessMode(AccessMode.BYTE_ACCESS);
        }
        packetWriter.finishVariableShortPacket();
        player.writePacketBuffer(packetWriter.getBuffer());
        if (player.planeChangeRefreshPending) {
            ObjectManager.getInstance().refreshDynamicObjectsForPlayer(player);
            GroundItemManager.getInstance().refreshForPlayer(player);
            player.planeChangeRefreshPending = false;
        }
    }

    public static void a(Player player, PacketWriter packetWriter, boolean bl, boolean bl2, Player object) {
        int n;
        int n2 = 0;
        if (player.getUpdateState().isForcedMovementUpdateRequired()) {
            n2 = 1024;
        }
        if (player.getUpdateState().isGraphicUpdateRequired()) {
            n2 |= 0x100;
        }
        if (player.getUpdateState().isAnimationUpdateRequired()) {
            n2 |= 8;
        }
        if (player.getUpdateState().isForcedTextUpdateRequired()) {
            n2 |= 4;
        }
        if (player.getUpdateState().isAppearanceUpdateRequired() && !bl2) {
            n2 |= 0x80;
        }
        if (player.getUpdateState().isFaceEntityUpdateRequired()) {
            n2 |= 1;
        }
        if (player.isAppearanceUpdateRequired() || bl) {
            n2 |= 0x10;
        }
        if (player.getUpdateState().isFacePositionUpdateRequired()) {
            n2 |= 2;
        }
        if (player.getUpdateState().isPrimaryHitUpdateRequired()) {
            n2 |= 0x20;
        }
        if (player.getUpdateState().isSecondaryHitUpdateRequired()) {
            n2 |= 0x200;
        }
        if (n2 >= 256) {
            packetWriter.writeShort(n2 |= 0x40, ByteOrder.LITTLE);
        } else {
            packetWriter.writeByte(n2);
        }
        if (player.getUpdateState().isForcedMovementUpdateRequired()) {
            if (object == null) {
                player.getUpdateState();
                packetWriter.writeByte(EntityUpdateState.getLocalXForUpdate(player), ByteTransform.SUBTRACT);
                player.getUpdateState();
                packetWriter.writeByte(EntityUpdateState.getLocalYForUpdate(player), ByteTransform.SUBTRACT);
                player.getUpdateState();
                packetWriter.writeByte(EntityUpdateState.getLocalXForUpdate(player) + player.getUpdateState().getForcedMovementEndXOffset(), ByteTransform.SUBTRACT);
                player.getUpdateState();
                packetWriter.writeByte(EntityUpdateState.getLocalYForUpdate(player) + player.getUpdateState().getForcedMovementEndYOffset(), ByteTransform.SUBTRACT);
            } else {
                n2 = player.getPosition().getX() - ((Entity)object).getPosition().getX();
                n = player.getPosition().getY() - ((Entity)object).getPosition().getY();
                player.getUpdateState();
                packetWriter.writeByte(EntityUpdateState.getLocalXForUpdate((Player)object) + n2, ByteTransform.SUBTRACT);
                player.getUpdateState();
                packetWriter.writeByte(EntityUpdateState.getLocalYForUpdate((Player)object) + n, ByteTransform.SUBTRACT);
                player.getUpdateState();
                packetWriter.writeByte(EntityUpdateState.getLocalXForUpdate((Player)object) + n2 + player.getUpdateState().getForcedMovementEndXOffset(), ByteTransform.SUBTRACT);
                player.getUpdateState();
                packetWriter.writeByte(EntityUpdateState.getLocalYForUpdate((Player)object) + n + player.getUpdateState().getForcedMovementEndYOffset(), ByteTransform.SUBTRACT);
            }
            packetWriter.writeShort(player.getUpdateState().getForcedMovementStartDelay(), ByteTransform.ADD, ByteOrder.LITTLE);
            packetWriter.writeShort(player.getUpdateState().getForcedMovementEndDelay(), ByteTransform.ADD);
            packetWriter.writeByte(player.getUpdateState().getForcedMovementDirection(), ByteTransform.SUBTRACT);
        }
        if (player.getUpdateState().isGraphicUpdateRequired()) {
            packetWriter.writeShort(player.getUpdateState().getGraphicId(), ByteOrder.LITTLE);
            packetWriter.writeInt(player.getUpdateState().getGraphicDelay());
        }
        if (player.getUpdateState().isAnimationUpdateRequired()) {
            packetWriter.writeShort(player.getUpdateState().getAnimationId(), ByteOrder.LITTLE);
            packetWriter.writeByte(player.getUpdateState().getAnimationDelay(), ByteTransform.NEGATE);
        }
        if (player.getUpdateState().isForcedTextUpdateRequired()) {
            packetWriter.writeString(player.getUpdateState().getForcedText());
        }
        if (player.getUpdateState().isAppearanceUpdateRequired() && !bl2) {
            PacketWriter chatWriter = packetWriter;
            Player player2 = player;
            chatWriter.writeShort(((player2.getPublicChatColor() & 0xFF) << 8) + (player2.getPublicChatEffects() & 0xFF), ByteOrder.LITTLE);
            chatWriter.writeByte(player2.getPlayerRights());
            chatWriter.writeByte(player2.getPublicChatPayload().length, ByteTransform.NEGATE);
            byte[] byArray = player2.getPublicChatPayload();
            int n3 = byArray.length - 1;
            while (n3 >= 0) {
                chatWriter.writeByte(byArray[n3]);
                --n3;
            }
        }
        if (player.getUpdateState().isFaceEntityUpdateRequired()) {
            packetWriter.writeShort(player.getUpdateState().getFaceEntityId(), ByteOrder.LITTLE);
        }
        if (player.isAppearanceUpdateRequired() || bl) {
            PacketWriter appearanceWriter = packetWriter;
            Player player3 = player;
            PacketWriter packetWriter2 = PacketBuffer.allocateWriter(128);
            packetWriter2.writeByte(player3.getGender());
            packetWriter2.writeByte(player3.getPrayerHeadIcon());
            packetWriter2.writeByte(player3.getSkullIcon());
            if (player3.npcTransformationId <= 0) {
                if (player3.getEquipmentManager().getContainer().hasItemAtSlot(0) && player3.getEquipmentManager().getContainer().getItemAt(0).isEquippable()) {
                    packetWriter2.writeShort(512 + player3.getEquipmentManager().getContainer().getItemAt(0).getId());
                } else {
                    packetWriter2.writeByte(0);
                }
                if (player3.getEquipmentManager().getContainer().hasItemAtSlot(1) && player3.getEquipmentManager().getContainer().getItemAt(1).isEquippable()) {
                    packetWriter2.writeShort(512 + player3.getEquipmentManager().getContainer().getItemAt(1).getId());
                } else {
                    packetWriter2.writeByte(0);
                }
                if (player3.getEquipmentManager().getContainer().hasItemAtSlot(2) && player3.getEquipmentManager().getContainer().getItemAt(2).isEquippable()) {
                    packetWriter2.writeShort(512 + player3.getEquipmentManager().getContainer().getItemAt(2).getId());
                } else {
                    packetWriter2.writeByte(0);
                }
                if (player3.getEquipmentManager().getContainer().hasItemAtSlot(3) && !player3.shouldHideHeldItemsInAppearance() && player3.getEquipmentManager().getContainer().getItemAt(3).isEquippable()) {
                    packetWriter2.writeShort(512 + player3.getEquipmentManager().getContainer().getItemAt(3).getId());
                } else {
                    packetWriter2.writeByte(0);
                }
                if (player3.getEquipmentManager().getContainer().hasItemAtSlot(4) && player3.getEquipmentManager().getContainer().getItemAt(4).isEquippable()) {
                    packetWriter2.writeShort(512 + player3.getEquipmentManager().getContainer().getItemAt(4).getId());
                } else {
                    packetWriter2.writeShort(256 + player3.getAppearanceParts()[0]);
                }
                if (player3.getEquipmentManager().getContainer().hasItemAtSlot(5) && !player3.shouldHideHeldItemsInAppearance() && player3.getEquipmentManager().getContainer().getItemAt(5).isEquippable()) {
                    packetWriter2.writeShort(512 + player3.getEquipmentManager().getContainer().getItemAt(5).getId());
                } else {
                    packetWriter2.writeByte(0);
                }
                n = 0;
                ItemStack itemStack = player3.getEquipmentManager().getContainer().getItemAt(4);
                if (itemStack != null) {
                    n = itemStack.getDefinition().getEquipmentAppearanceType();
                }
                if (itemStack == null || itemStack != null && n != 1 && itemStack.isEquippable()) {
                    packetWriter2.writeShort(256 + player3.getAppearanceParts()[1]);
                } else {
                    packetWriter2.writeShort(512 + itemStack.getId());
                }
                if (player3.getEquipmentManager().getContainer().hasItemAtSlot(7) && player3.getEquipmentManager().getContainer().getItemAt(7).isEquippable()) {
                    packetWriter2.writeShort(512 + player3.getEquipmentManager().getContainer().getItemAt(7).getId());
                } else {
                    packetWriter2.writeShort(256 + player3.getAppearanceParts()[2]);
                }
                ItemStack itemStack2 = player3.getEquipmentManager().getContainer().getItemAt(0);
                n = 0;
                if (itemStack2 != null) {
                    n = itemStack2.getDefinition().getEquipmentAppearanceType();
                }
                if (itemStack2 == null || itemStack2 != null && n != 3 && n != 2 && itemStack2.isEquippable()) {
                    packetWriter2.writeShort(256 + player3.getAppearanceParts()[3]);
                } else {
                    packetWriter2.writeByte(0);
                }
                if (player3.getEquipmentManager().getContainer().hasItemAtSlot(9) && player3.getEquipmentManager().getContainer().getItemAt(9).isEquippable()) {
                    packetWriter2.writeShort(512 + player3.getEquipmentManager().getContainer().getItemAt(9).getId());
                } else {
                    packetWriter2.writeShort(256 + player3.getAppearanceParts()[4]);
                }
                if (player3.getEquipmentManager().getContainer().hasItemAtSlot(10) && player3.getEquipmentManager().getContainer().getItemAt(10).isEquippable()) {
                    packetWriter2.writeShort(512 + player3.getEquipmentManager().getContainer().getItemAt(10).getId());
                } else {
                    packetWriter2.writeShort(256 + player3.getAppearanceParts()[5]);
                }
                if (player3.getGender() == 0 && (itemStack2 == null || itemStack2 != null && n != 4 && n != 2)) {
                    packetWriter2.writeShort(256 + player3.getAppearanceParts()[6]);
                } else {
                    packetWriter2.writeByte(0);
                }
            } else {
                packetWriter2.writeShort(-1);
                packetWriter2.writeShort(player3.npcTransformationId);
            }
            packetWriter2.writeByte(player3.getAppearanceColors()[0]);
            packetWriter2.writeByte(player3.getAppearanceColors()[1]);
            packetWriter2.writeByte(player3.getAppearanceColors()[2]);
            packetWriter2.writeByte(player3.getAppearanceColors()[3]);
            packetWriter2.writeByte(player3.getAppearanceColors()[4]);
            packetWriter2.writeShort(player3.getStandAnimation());
            Player player4 = player3;
            int n4 = player4.getWalkAnimation();
            packetWriter2.writeShort(n4 != WeaponProfile.FISTS.getMovementAnimations()[1] ? n4 : 823);
            packetWriter2.writeShort(player3.getWalkAnimation());
            Player player5 = player3;
            int n5 = player5.getWalkAnimation();
            packetWriter2.writeShort(n5 != WeaponProfile.FISTS.getMovementAnimations()[1] ? n5 : 820);
            Player player6 = player3;
            int n6 = player6.getWalkAnimation();
            packetWriter2.writeShort(n6 != WeaponProfile.FISTS.getMovementAnimations()[1] ? n6 : 821);
            Player player7 = player3;
            int n7 = player7.getWalkAnimation();
            packetWriter2.writeShort(n7 != WeaponProfile.FISTS.getMovementAnimations()[1] ? n7 : 822);
            packetWriter2.writeShort(player3.getRunAnimation());
            packetWriter2.writeLong(player3.getNameHash());
            packetWriter2.writeByte(player3.getCombatLevel());
            packetWriter2.writeShort(0);
            appearanceWriter.writeByte(packetWriter2.getBuffer().position(), ByteTransform.NEGATE);
            appearanceWriter.writeBuffer(packetWriter2.getBuffer());
        }
        if (player.getUpdateState().isFacePositionUpdateRequired()) {
            packetWriter.writeShort((player.getUpdateState().getFacePosition().getX() << 1) + 1, ByteTransform.ADD, ByteOrder.LITTLE);
            packetWriter.writeShort((player.getUpdateState().getFacePosition().getY() << 1) + 1, ByteOrder.LITTLE);
        }
        if (player.getUpdateState().isPrimaryHitUpdateRequired()) {
            packetWriter.writeByte(player.getUpdateState().getPrimaryHitDamage());
            packetWriter.writeByte(player.getUpdateState().getPrimaryHitType(), ByteTransform.ADD);
            packetWriter.writeByte(player.getSkillManager().getCurrentLevels()[3], ByteTransform.NEGATE);
            player.getSkillManager();
            packetWriter.writeByte(SkillManager.getLevelForExperience((int)player.getSkillManager().getExperience()[3]));
        }
        if (player.getUpdateState().isSecondaryHitUpdateRequired()) {
            packetWriter.writeByte(player.getUpdateState().getSecondaryHitDamage());
            packetWriter.writeByte(player.getUpdateState().getSecondaryHitType(), ByteTransform.SUBTRACT);
            packetWriter.writeByte(player.getSkillManager().getCurrentLevels()[3]);
            player.getSkillManager();
            packetWriter.writeByte(SkillManager.getLevelForExperience((int)player.getSkillManager().getExperience()[3]), ByteTransform.NEGATE);
        }
    }

    public static void a(PacketWriter packetWriter, int n, boolean bl) {
        packetWriter.writeBits(2, 1);
        packetWriter.writeBits(3, n);
        packetWriter.writeBoolean(bl);
    }

    public static void a(PacketWriter packetWriter, int n, int n2, boolean bl) {
        packetWriter.writeBits(2, 2);
        packetWriter.writeBits(3, n);
        packetWriter.writeBits(3, n2);
        packetWriter.writeBoolean(bl);
    }
}

