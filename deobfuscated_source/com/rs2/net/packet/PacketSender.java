/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.net.packet;

import com.rs2.CacheRevisionInfo;
import com.rs2.Server;
import com.rs2.ServerSettings;
import com.rs2.cache.InterfaceDefinition;
import com.rs2.model.GameplayHelper;
import com.rs2.model.Position;
import com.rs2.model.World;
import com.rs2.model.ground.GroundItem;
import com.rs2.model.item.ItemStack;
import com.rs2.model.message.MessageOfTheWeek;
import com.rs2.model.music.MusicTrackDefinition;
import com.rs2.model.objects.DynamicObject;
import com.rs2.model.objects.LoadedWorldObject;
import com.rs2.model.objects.WorldObjectLookup;
import com.rs2.model.objects.functions.DoubleDoorHandler;
import com.rs2.model.player.BankRearrangeMode;
import com.rs2.model.player.Player;
import com.rs2.model.quest.QuestDefinition;
import com.rs2.model.quest.QuestHook;
import com.rs2.model.quest.QuestScript;
import com.rs2.model.skill.magic.Spellbook;
import com.rs2.model.task.CycleEventHandler;
import com.rs2.net.packet.AgilityMovementCompletionEvent;
import com.rs2.net.packet.ByteOrder;
import com.rs2.net.packet.ByteTransform;
import com.rs2.net.packet.DelayedAnimationEvent;
import com.rs2.net.packet.DelayedUnlockEvent;
import com.rs2.net.packet.PacketBuffer;
import com.rs2.net.packet.PacketWriter;
import com.rs2.net.packet.QueuedPositionUnlockEvent;
import com.rs2.net.packet.RelativePositionUnlockEvent;
import com.rs2.net.packet.YAxisPositionUnlockEvent;
import com.rs2.util.GameUtil;
import java.awt.Color;
import java.util.ArrayList;
import java.util.regex.Pattern;

public final class PacketSender {
    private Player player;

    public PacketSender(Player player) {
        int[] nArray = new int[]{17511, 15819, 15812, 15801, 15791, 15774, 15767};
        this.player = player;
    }

    public final PacketSender sendMinimapState(int n) {
        if (this.player.isBot) {
            return this;
        }
        PacketWriter packetWriter = PacketBuffer.allocateWriter(2);
        packetWriter.writeOpcode(this.player.getOutboundCipher(), 99);
        packetWriter.writeByte(n);
        this.player.writePacketBuffer(packetWriter.getBuffer());
        return this;
    }

    public final void sendMusicTrack(MusicTrackDefinition musicTrackDefinition) {
        this.sendInterfaceText(musicTrackDefinition.getName(), 4439);
        int n = musicTrackDefinition.getTrackId();
        if (!this.player.isBot && this.player.ax != n) {
            this.player.ax = n;
            if (n != -1) {
                PacketWriter packetWriter = PacketBuffer.allocateWriter(3);
                packetWriter.writeOpcode(this.player.getOutboundCipher(), 74);
                packetWriter.writeShort(n, ByteOrder.LITTLE);
                this.player.writePacketBuffer(packetWriter.getBuffer());
            }
        }
    }

    public final void lockPlayerForTicks(int n) {
        this.player.setActionLocked(true);
        CycleEventHandler.getInstance().schedule(this.player, new DelayedUnlockEvent(this), n);
    }

    public final PacketSender sendSystemUpdateTimer(int n) {
        if (this.player.isBot) {
            return this;
        }
        PacketWriter packetWriter = PacketBuffer.allocateWriter(4);
        packetWriter.writeOpcode(this.player.getOutboundCipher(), 114);
        packetWriter.writeShort(n, ByteOrder.LITTLE);
        this.player.writePacketBuffer(packetWriter.getBuffer());
        return this;
    }

    public final void refreshSidebarInterfaces() {
        int[] nArray = new int[14];
        nArray[0] = 2423;
        nArray[1] = 3917;
        nArray[2] = 638;
        nArray[3] = 3213;
        nArray[4] = 1644;
        nArray[5] = 5608;
        nArray[7] = -1;
        nArray[8] = 5065;
        nArray[9] = 5715;
        nArray[10] = 2449;
        nArray[11] = 904;
        nArray[12] = 147;
        nArray[13] = 962;
        int[] nArray2 = nArray;
        int n = 0;
        while (n < 14) {
            this.setSidebarInterface(n, nArray2[n]);
            if (n == 6) {
                if (this.player.getSpellbook() == Spellbook.MODERN) {
                    this.setSidebarInterface(n, 1151);
                }
                if (this.player.getSpellbook() == Spellbook.ANCIENT) {
                    this.setSidebarInterface(n, 12855);
                }
                if (this.player.getSpellbook() == Spellbook.NECROMANCY) {
                    this.setSidebarInterface(n, 19104);
                }
            }
            ++n;
        }
        this.player.getEquipmentManager().refreshWeaponInterface();
    }

    public final boolean updateMissingQuestCompletionStates() {
        boolean bl;
        block7: {
            int n;
            ArrayList<Integer> arrayList;
            block8: {
                bl = true;
                if (ServerSettings.completeMissingQuestsMode <= 0) break block7;
                ArrayList<Integer> arrayList2 = new ArrayList<Integer>();
                arrayList = new ArrayList<Integer>();
                n = 1;
                while (n < QuestDefinition.questCount) {
                    QuestScript questScript = QuestDefinition.getQuestScript(n);
                    if (questScript.getQuestId() == -1) {
                        arrayList.add(n);
                    } else {
                        arrayList2.add(n);
                    }
                    ++n;
                }
                n = 0;
                while (n < arrayList2.size()) {
                    int n2 = (Integer)arrayList2.get(n);
                    if (this.player.getQuestState(n2) != 1) {
                        bl = false;
                    }
                    ++n;
                }
                if (ServerSettings.completeMissingQuestsMode != 1 && (ServerSettings.completeMissingQuestsMode != 2 || !bl)) break block8;
                n = 0;
                while (n < arrayList.size()) {
                    int n3 = (Integer)arrayList.get(n);
                    this.player.setQuestState(n3, 1);
                    ++n;
                }
                break block7;
            }
            if (ServerSettings.completeMissingQuestsMode != 2 || bl) break block7;
            n = 0;
            while (n < arrayList.size()) {
                int n4 = (Integer)arrayList.get(n);
                this.player.setQuestState(n4, 0);
                ++n;
            }
        }
        return bl;
    }

    public final PacketSender sendPostLoginState() {
        PacketSender packetSender;
        int n;
        Object object;
        Object object2;
        PacketSender packetSender2;
        if (!this.player.getHostAddress().equals("127.0.0.1")) {
            Server.getInstance().backupPending = true;
        }
        this.sendPlayerIndex();
        Object object3 = this;
        if (((PacketSender)object3).player.isBot) {
            packetSender2 = (PacketSender)object3;
        } else {
            PacketWriter packetWriter = PacketBuffer.allocateWriter(1);
            packetWriter.writeOpcode(((PacketSender)object3).player.getOutboundCipher(), 107);
            ((PacketSender)object3).player.writePacketBuffer(packetWriter.getBuffer());
            packetSender2 = (PacketSender)object3;
        }
        this.sendMapRegion();
        this.sendRunEnergy();
        object3 = this;
        ((PacketSender)object3).updateMissingQuestCompletionStates();
        int n2 = 1;
        while (n2 < QuestDefinition.questCount) {
            Player player;
            object2 = QuestDefinition.forId(n2);
            object = QuestDefinition.getQuestScript(n2);
            n = ((QuestDefinition)object2).getJournalButtonId();
            if (ServerSettings.recolorMissingQuests && ((QuestHook)object).getQuestId() == -1 && ((PacketSender)object3).player.getQuestState(n2, true) != 1) {
                player = ((PacketSender)object3).player;
                player.packetSender.sendInterfaceTextColor(n, new Color(102, 102, 102));
            } else if (((PacketSender)object3).player.getQuestState(n2, true) == 0) {
                player = ((PacketSender)object3).player;
                player.packetSender.sendInterfaceTextColor(n, Color.RED);
            } else if (((PacketSender)object3).player.getQuestState(n2, true) == 1) {
                player = ((PacketSender)object3).player;
                player.packetSender.sendInterfaceTextColor(n, Color.GREEN);
            } else if (((PacketSender)object3).player.getQuestState(n2, true) >= 2) {
                player = ((PacketSender)object3).player;
                player.packetSender.sendInterfaceTextColor(n, Color.YELLOW);
            }
            ++n2;
        }
        object3 = this;
        if (((PacketSender)object3).player.isBot) {
            packetSender = (PacketSender)object3;
        } else {
            PacketWriter packetWriter = PacketBuffer.allocateWriter(4);
            packetWriter.writeOpcode(((PacketSender)object3).player.getOutboundCipher(), 206);
            packetWriter.writeByte(((PacketSender)object3).player.getPublicChatMode());
            packetWriter.writeByte(((PacketSender)object3).player.getPrivateChatMode());
            packetWriter.writeByte(((PacketSender)object3).player.getTradeMode());
            ((PacketSender)object3).player.writePacketBuffer(packetWriter.getBuffer());
            packetSender = (PacketSender)object3;
        }
        this.player.getEquipmentManager().refreshCarriedValue();
        this.player.getCompostBinManager().processRotting();
        this.player.getAllotmentPatchManager().processGrowth();
        this.player.getFlowerPatchManager().processGrowth();
        this.player.getHerbPatchManager().processGrowth();
        this.player.getHopsPatchManager().processGrowth();
        this.player.getBushPatchManager().processGrowth();
        this.player.getAllotmentPatchManager().processGrowth();
        this.player.getTreePatchManager().processGrowth();
        this.player.getFruitTreePatchManager().processGrowth();
        this.player.getSpecialTreePatchManager().processGrowth();
        this.player.getSpecialCropPatchManager().processGrowth();
        int n3 = 0;
        while (n3 < this.player.getQueuedLoginItemIds().length) {
            this.player.getInventoryManager().addItem(new ItemStack(this.player.getQueuedLoginItemIds()[n3], this.player.getQueuedLoginItemAmounts()[n3]));
            ++n3;
        }
        n3 = 0;
        while (n3 < 4) {
            this.player.getTreePatchManager().scheduleStumpRegrowth(n3);
            ++n3;
        }
        this.player.getSocialManager().initializePrivateMessaging();
        if (this.player.getQuestState(0) == 1) {
            object3 = this;
            if (!((PacketSender)object3).player.isBot) {
                PacketSender packetSender3;
                int n4 = GameplayHelper.getDaysBetweenMidnights(((PacketSender)object3).player.lastSavedMillis, System.currentTimeMillis());
                object2 = MessageOfTheWeek.getMessageForIndex(Server.messageOfTheWeekIndex);
                int n5 = ((MessageOfTheWeek)object2).getInterfaceId();
                n = ((PacketSender)object3).player.getMembershipDaysRemaining();
                int n6 = n4;
                n4 = PacketSender.packLastLoginAddress(((PacketSender)object3).player.lastLoginHostAddress);
                n3 = 0;
                boolean bl = ((PacketSender)object3).player.isMember();
                n3 = 200;
                PacketSender packetSender4 = (PacketSender)object3;
                if (packetSender4.player.isBot) {
                    packetSender3 = packetSender4;
                } else {
                    PacketWriter packetWriter = PacketBuffer.allocateWriter(18);
                    packetWriter.writeOpcode(packetSender4.player.getOutboundCipher(), 176);
                    packetWriter.writeShort(200);
                    packetWriter.writeShort(0, ByteTransform.ADD);
                    packetWriter.writeByte(bl ? 1 : 0);
                    packetWriter.writeByte(ServerSettings.freeToPlayWorld ? 0 : 1);
                    packetWriter.writeByte(ServerSettings.maxLevel);
                    packetWriter.writeInt(n4);
                    packetWriter.writeShort(n6);
                    packetWriter.writeShort(n);
                    packetWriter.writeShort(n5);
                    packetSender4.player.writePacketBuffer(packetWriter.getBuffer());
                    packetSender3 = packetSender4;
                }
                object = "\\nYou do not have a Bank PIN.\\nPlease visit a bank if you would like one.";
                if (((PacketSender)object3).player.getBankPinManager().hasPin()) {
                    object = "\\nYou have set a Bank PIN.";
                }
                ((PacketSender)object3).sendInterfaceText((String)object, 15270);
                n = 0;
                if (((MessageOfTheWeek)object2).getInterfaceId() == 5993) {
                    n = 1;
                }
                ((PacketSender)object3).sendInterfaceText(((MessageOfTheWeek)object2).getTitle(), n != 0 ? 6002 : ((MessageOfTheWeek)object2).getInterfaceId() + 4);
                ((PacketSender)object3).sendInterfaceText(((MessageOfTheWeek)object2).getLines()[0], n != 0 ? 15491 : ((MessageOfTheWeek)object2).getInterfaceId() + 2);
                ((PacketSender)object3).sendInterfaceText(((MessageOfTheWeek)object2).getLines()[1], n != 0 ? 15492 : ((MessageOfTheWeek)object2).getInterfaceId() + 3);
            }
            this.sendGameMessage("Welcome to " + ServerSettings.serverName + "." + (ServerSettings.showServerEmulatorInWelcome ? " (Emulation run by: " + ServerSettings.serverEmulatorName + ")" : ""));
            String string = "";
            CacheRevisionInfo cacheRevisionInfo = CacheRevisionInfo.forRevision(ServerSettings.cacheVersion);
            if (cacheRevisionInfo != null) {
                if (cacheRevisionInfo.releaseDate != null) {
                    string = " (From: " + cacheRevisionInfo.releaseDate + ")";
                }
                if (cacheRevisionInfo.updateNotes != null) {
                    string = String.valueOf(string) + " - Updates:";
                }
            }
            this.sendGameMessage("Running RS2 Build #" + ServerSettings.cacheVersion + string);
            if (cacheRevisionInfo != null && cacheRevisionInfo.updateNotes != null) {
                int n7 = 0;
                while (n7 < cacheRevisionInfo.updateNotes.length) {
                    boolean bl = false;
                    int n8 = n7++;
                    object3 = cacheRevisionInfo;
                    n = n8;
                    object = object3;
                    this.sendGameMessage(String.valueOf(((CacheRevisionInfo)object).updateNotes[n]) + "#url#");
                }
            }
            if (this.player.dQ) {
                this.sendGameMessage("Your account file was somehow corrupted and had to be loaded from backup.");
            }
            this.player.el = true;
        }
        return this;
    }

    private static int packLastLoginAddress(String address) {
        int n = 0;
        int n2 = 0;
        String[] stringArray2 = new String[4];
        String[] stringArray3 = address.split(Pattern.quote("."));
        int n3 = stringArray3.length;
        int n4 = 0;
        while (n4 < n3) {
            String part = stringArray3[n4];
            stringArray2[n2] = part;
            ++n2;
            ++n4;
        }
        String[] reorderedParts = (String.valueOf(stringArray2[1]) + "." + stringArray2[0] + "." + stringArray2[3] + "." + stringArray2[2]).split(Pattern.quote("."));
        int n5 = reorderedParts.length;
        n3 = 0;
        while (n3 < n5) {
            String string = reorderedParts[n3];
            n <<= 8;
            n |= Integer.parseInt(string);
            ++n3;
        }
        return n;
    }

    public final PacketSender syncPlayerConfigs() {
        this.refreshAutocastConfig();
        this.sendConfig(166, this.player.getBrightness());
        this.sendConfig(168, this.player.getMusicVolume());
        this.sendConfig(169, this.player.getEffectVolume());
        this.sendConfig(170, this.player.getMouseButtons());
        this.sendConfig(171, this.player.getPublicChatEffects());
        this.sendConfig(172, this.player.isAutoRetaliate() ? 0 : 1);
        this.sendConfig(173, this.player.getMovementQueue().isRunning() ? 1 : 0);
        this.sendConfig(287, this.player.getSplitPrivateChat());
        this.sendConfig(427, this.player.isAcceptAidEnabled() ? 1 : 0);
        this.sendConfig(115, this.player.isBankWithdrawNoteMode() ? 1 : 0);
        this.sendConfig(304, this.player.getBankRearrangeMode().equals((Object)BankRearrangeMode.SWAP) ? 0 : 1);
        return this;
    }

    public final PacketSender sendEnterInputPrompt(int n) {
        this.player.setSelectedInterfaceId(n);
        if (this.player.isBot) {
            return this;
        }
        PacketWriter packetWriter = PacketBuffer.allocateWriter(1);
        packetWriter.writeOpcode(this.player.getOutboundCipher(), 27);
        this.player.writePacketBuffer(packetWriter.getBuffer());
        return this;
    }

    public final PacketSender modifySkillLevel(int n, int n2, boolean bl) {
        int n3 = this.player.getSkillManager().getBaseLevel(n) + n2;
        if (this.player.getPlayerRights() > 1 && ServerSettings.debugModeEnabled) {
            System.out.println("current: " + this.player.getSkillManager().getBaseLevel(n) + " max: " + n3);
        }
        if (!bl) {
            int[] nArray = this.player.getSkillManager().getCurrentLevels();
            int n4 = n;
            nArray[n4] = nArray[n4] + n2;
            if (this.player.getSkillManager().getCurrentLevels()[n] < 0) {
                this.player.getSkillManager().getCurrentLevels()[n] = 0;
            }
            this.player.getSkillManager().refreshSkill(n);
            return this;
        }
        if (n2 < 0) {
            if (this.player.getSkillManager().getCurrentLevels()[n] < n3) {
                return this;
            }
            if (this.player.getSkillManager().getCurrentLevels()[n] + n2 < n3) {
                this.player.getSkillManager().getCurrentLevels()[n] = n3;
            } else {
                int[] nArray = this.player.getSkillManager().getCurrentLevels();
                int n5 = n;
                nArray[n5] = nArray[n5] + n2;
            }
        } else {
            if (this.player.getSkillManager().getCurrentLevels()[n] > n3) {
                return this;
            }
            if (this.player.getSkillManager().getCurrentLevels()[n] + n2 > n3) {
                this.player.getSkillManager().getCurrentLevels()[n] = n3;
            } else {
                int[] nArray = this.player.getSkillManager().getCurrentLevels();
                int n6 = n;
                nArray[n6] = nArray[n6] + n2;
            }
        }
        this.player.getSkillManager().refreshSkill(n);
        return this;
    }

    public final int modifySkillLevelReturningRemainder(int n, int n2, boolean bl) {
        int remainder = 0;
        int n3 = this.player.getSkillManager().getBaseLevel(n);
        int n4 = this.player.getSkillManager().getCurrentLevels()[n];
        if (n2 < 0) {
            if (n4 + n2 < 0) {
                remainder = n4 + n2;
                this.player.getSkillManager().getCurrentLevels()[n] = 0;
            } else {
                int[] nArray = this.player.getSkillManager().getCurrentLevels();
                int n6 = n;
                nArray[n6] = nArray[n6] + n2;
            }
        } else if (n4 + n2 > n3) {
            remainder = n2 - (n3 - n4);
            this.player.getSkillManager().getCurrentLevels()[n] = n3;
        } else {
            int[] nArray = this.player.getSkillManager().getCurrentLevels();
            int n8 = n;
            nArray[n8] = nArray[n8] + n2;
        }
        this.player.getSkillManager().refreshSkill(n);
        return Math.abs(remainder);
    }

    public final PacketSender queueAgilityMovement(int n, int n2, boolean bl, int n3, int n4, int n5, double d, boolean bl2, String string) {
        if (this.player.isStunned() || this.player.isMovementLocked()) {
            return this;
        }
        if (bl2) {
            this.player.getMovementQueue().setRunning(false);
        }
        this.player.setActionLocked(true);
        this.player.forcedMovementActive = true;
        if (n3 > 0) {
            this.player.setWalkAnimationOverride(n3);
            this.player.setAppearanceUpdateRequired(true);
        }
        this.player.getMovementQueue().clear();
        this.player.getMovementQueue().addStep(new Position(this.player.getPosition().getX() + n, this.player.getPosition().getY() + n2));
        this.player.getMovementQueue().removeFirstStep();
        CycleEventHandler.getInstance().schedule(this.player, new AgilityMovementCompletionEvent(this, string, d, bl2, true), n4 > 0 ? n5 - 3 : n5);
        if (n4 > 0) {
            CycleEventHandler.getInstance().schedule(this.player, new DelayedAnimationEvent(this, n4), n5);
        }
        return this;
    }

    public final PacketSender queueTwoStepMovement(Position[] object, int n, boolean bl) {
        if (this.player.isStunned() || this.player.isMovementLocked()) {
            return this;
        }
        this.player.setActionLocked(true);
        this.player.setInteractionTarget(null);
        this.player.getUpdateState().setFaceEntity(65535);
        this.player.forcedMovementActive = true;
        this.player.getMovementQueue().clear();
        Position[] positionArray = object;
        n = 0;
        while (n < 2) {
            Position position = positionArray[n];
            this.player.getMovementQueue().addStep(position);
            ++n;
        }
        this.player.getMovementQueue().removeFirstStep();
        CycleEventHandler.getInstance().schedule(this.player, new QueuedPositionUnlockEvent(this, true), 2);
        return this;
    }

    public final PacketSender queueRelativeMovementStep(int n, int n2, boolean bl) {
        if (this.player.isStunned() || this.player.isMovementLocked()) {
            return this;
        }
        this.player.setActionLocked(true);
        if (bl) {
            this.player.forcedMovementActive = true;
        }
        this.player.getMovementQueue().clear();
        this.player.getMovementQueue().addStep(new Position(this.player.getPosition().getX() + n, this.player.getPosition().getY() + n2));
        this.player.getMovementQueue().removeFirstStep();
        CycleEventHandler.getInstance().schedule(this.player, new RelativePositionUnlockEvent(this, bl), 1);
        return this;
    }

    public final PacketSender queueYAxisMovementStep(int n, int n2, int n3, boolean bl) {
        if (this.player.isStunned() || this.player.isMovementLocked()) {
            return this;
        }
        this.player.setActionLocked(true);
        this.player.forcedMovementActive = true;
        this.player.getMovementQueue().clear();
        this.player.getMovementQueue().addStep(new Position(this.player.getPosition().getX(), this.player.getPosition().getY() + n2));
        this.player.getMovementQueue().removeFirstStep();
        CycleEventHandler.getInstance().schedule(this.player, new YAxisPositionUnlockEvent(this, true), 2);
        return this;
    }

    public final PacketSender queueAbsoluteMovementStep(int n, int n2) {
        if (this.player.isStunned() || this.player.isMovementLocked()) {
            return this;
        }
        this.player.getMovementQueue().clear();
        this.player.getMovementQueue().addStep(new Position(n, n2));
        this.player.getMovementQueue().removeFirstStep();
        return this;
    }

    public final PacketSender sendStillGraphicToNearbyPlayers(int n, int n2, int n3, int n4, int n5) {
        Player[] playerArray = World.getPlayers();
        int n6 = playerArray.length;
        int n7 = 0;
        while (n7 < n6) {
            Player player = playerArray[n7];
            if (player != null && player.getPosition().getPlane() == n4 && GameUtil.isWithinDistance(n2, n3, player.getPosition().getX(), player.getPosition().getY(), 25)) {
                player.packetSender.sendStillGraphic(n, new Position(n2, n3, n4), n5);
            }
            ++n7;
        }
        return this;
    }

    public final PacketSender sendSkillUpdate(int n, int n2, double d) {
        if (this.player.isBot) {
            return this;
        }
        PacketWriter packetWriter = PacketBuffer.allocateWriter(7);
        packetWriter.writeOpcode(this.player.getOutboundCipher(), 134);
        packetWriter.writeByte(n);
        packetWriter.writeInt((int)d, ByteOrder.MIDDLE);
        packetWriter.writeByte(n2);
        this.player.writePacketBuffer(packetWriter.getBuffer());
        return this;
    }

    public final PacketSender sendInterfaceModelRotation(int n, int n2, int n3, int n4) {
        if (this.player.isBot) {
            return this;
        }
        PacketWriter packetWriter = PacketBuffer.allocateWriter(10);
        packetWriter.writeOpcode(this.player.getOutboundCipher(), 230);
        packetWriter.writeShort(n4, ByteTransform.ADD);
        packetWriter.writeShort(n);
        packetWriter.writeShort(513);
        packetWriter.writeShort(n3, ByteTransform.ADD, ByteOrder.LITTLE);
        this.player.writePacketBuffer(packetWriter.getBuffer());
        return this;
    }

    public final PacketSender selectMagicSidebarTab(int n) {
        if (this.player.isBot) {
            return this;
        }
        PacketWriter packetWriter = PacketBuffer.allocateWriter(2);
        packetWriter.writeOpcode(this.player.getOutboundCipher(), 106);
        packetWriter.writeByte(6, ByteTransform.NEGATE);
        this.player.writePacketBuffer(packetWriter.getBuffer());
        return this;
    }

    public final PacketSender sendInterfaceOffset(int n, int n2, int n3) {
        if (this.player.isBot) {
            return this;
        }
        PacketWriter packetWriter = PacketBuffer.allocateWriter(7);
        packetWriter.writeOpcode(this.player.getOutboundCipher(), 70);
        packetWriter.writeShort(n);
        packetWriter.writeShort(n2, ByteOrder.LITTLE);
        packetWriter.writeShort(n3, ByteOrder.LITTLE);
        this.player.writePacketBuffer(packetWriter.getBuffer());
        return this;
    }

    public final PacketSender sendEntityHintIcon(int n, int n2) {
        this.player.bP = n2;
        if (this.player.isBot) {
            return this;
        }
        PacketWriter packetWriter = PacketBuffer.allocateWriter(7);
        packetWriter.writeOpcode(this.player.getOutboundCipher(), 254);
        packetWriter.writeByte(n);
        packetWriter.writeShort(n2);
        packetWriter.writeByte(0);
        packetWriter.writeByte(0);
        packetWriter.writeByte(0);
        this.player.writePacketBuffer(packetWriter.getBuffer());
        return this;
    }

    public final PacketSender sendPositionHintIcon(int n, int n2, int n3, int n4) {
        if (this.player.isBot) {
            return this;
        }
        PacketWriter packetWriter = PacketBuffer.allocateWriter(7);
        packetWriter.writeOpcode(this.player.getOutboundCipher(), 254);
        packetWriter.writeByte(n4);
        packetWriter.writeShort(n);
        packetWriter.writeShort(n2);
        packetWriter.writeByte(n3);
        this.player.writePacketBuffer(packetWriter.getBuffer());
        return this;
    }

    public final PacketSender sendInterfaceSlotItem(ItemStack itemStack, int n, int n2, int n3) {
        if (this.player.isBot) {
            return this;
        }
        PacketWriter packetWriter = PacketBuffer.allocateWriter(32);
        packetWriter.startVariableShortPacket(this.player.getOutboundCipher(), 34);
        packetWriter.writeShort(n2);
        packetWriter.writeByte(n);
        if (itemStack == null) {
            packetWriter.writeShort(0);
            packetWriter.writeByte(0);
        } else {
            packetWriter.writeShort(itemStack.getId() + 1);
            packetWriter.writeByte(n3);
        }
        packetWriter.finishVariableShortPacket();
        this.player.writePacketBuffer(packetWriter.getBuffer());
        return this;
    }

    public final PacketSender sendInterfaceSlotItem(int n, int n2, ItemStack itemStack) {
        if (this.player.isBot) {
            return this;
        }
        PacketWriter packetWriter = PacketBuffer.allocateWriter(32);
        packetWriter.startVariableShortPacket(this.player.getOutboundCipher(), 34);
        packetWriter.writeShort(n2);
        packetWriter.writeByte(n);
        if (itemStack.getId() == 0) {
            packetWriter.writeShort(0);
            packetWriter.writeByte(0);
        } else {
            packetWriter.writeShort(itemStack.getId() + 1);
            if (itemStack.getAmount() > 254) {
                packetWriter.writeByte(255);
                packetWriter.writeShort(itemStack.getAmount());
            } else {
                packetWriter.writeByte(itemStack.getAmount());
            }
        }
        packetWriter.finishVariableShortPacket();
        this.player.writePacketBuffer(packetWriter.getBuffer());
        return this;
    }

    public final PacketSender sendSingleItemContainer(int n, int n2, int n3) {
        if (this.player.isBot) {
            return this;
        }
        if (n >= InterfaceDefinition.interfaceCount) {
            return this;
        }
        PacketWriter packetWriter = PacketBuffer.allocateWriter(8192);
        packetWriter.startVariableShortPacket(this.player.getOutboundCipher(), 53);
        packetWriter.writeShort(n);
        packetWriter.writeShort(1);
        if (n2 > 0) {
            if (n3 > 254) {
                packetWriter.writeByte(255);
                packetWriter.writeInt(n3, ByteOrder.INVERSE_MIDDLE);
            } else {
                packetWriter.writeByte(n3);
            }
            packetWriter.writeShort(n2 + 1, ByteTransform.ADD, ByteOrder.LITTLE);
        } else {
            packetWriter.writeByte(0);
            packetWriter.writeShort(0, ByteTransform.ADD, ByteOrder.LITTLE);
        }
        packetWriter.finishVariableShortPacket();
        this.player.writePacketBuffer(packetWriter.getBuffer());
        return this;
    }

    public final PacketSender sendItemContainer(int n, ItemStack[] itemStackArray) {
        if (this.player.isBot) {
            return this;
        }
        if (n >= InterfaceDefinition.interfaceCount) {
            return this;
        }
        PacketWriter packetWriter = PacketBuffer.allocateWriter(8192);
        packetWriter.startVariableShortPacket(this.player.getOutboundCipher(), 53);
        packetWriter.writeShort(n);
        packetWriter.writeShort(itemStackArray.length);
        ItemStack[] itemStackArray2 = itemStackArray;
        int n2 = itemStackArray.length;
        int n3 = 0;
        while (n3 < n2) {
            ItemStack itemStack = itemStackArray2[n3];
            if (itemStack != null) {
                if (itemStack.getAmount() > 254) {
                    packetWriter.writeByte(255);
                    packetWriter.writeInt(itemStack.getAmount(), ByteOrder.INVERSE_MIDDLE);
                } else {
                    packetWriter.writeByte(itemStack.getAmount());
                }
                packetWriter.writeShort(itemStack.getId() + 1, ByteTransform.ADD, ByteOrder.LITTLE);
            } else {
                packetWriter.writeByte(0);
                packetWriter.writeShort(0, ByteTransform.ADD, ByteOrder.LITTLE);
            }
            ++n3;
        }
        packetWriter.finishVariableShortPacket();
        this.player.writePacketBuffer(packetWriter.getBuffer());
        return this;
    }

    public final PacketSender sendObjectCreate(int n, int n2, int n3, int n4, int n5, int n6) {
        if (this.player.isBot) {
            return this;
        }
        this.sendLocalScenePosition(new Position(n2, n3, n4));
        PacketWriter packetWriter = PacketBuffer.allocateWriter(5);
        packetWriter.writeOpcode(this.player.getOutboundCipher(), 151);
        packetWriter.writeByte(0, ByteTransform.SUBTRACT);
        packetWriter.writeShort(n, ByteOrder.LITTLE);
        packetWriter.writeByte((n6 << 2) + (n5 & 3), ByteTransform.SUBTRACT);
        this.player.writePacketBuffer(packetWriter.getBuffer());
        return this;
    }

    public final PacketSender closeInterface(int n) {
        if (this.player.isBot) {
            return this;
        }
        if (n >= InterfaceDefinition.interfaceCount) {
            return this;
        }
        PacketWriter packetWriter = PacketBuffer.allocateWriter(4);
        packetWriter.writeOpcode(this.player.getOutboundCipher(), 218);
        packetWriter.writeShort(n, ByteTransform.ADD, ByteOrder.LITTLE);
        this.player.writePacketBuffer(packetWriter.getBuffer());
        return this;
    }

    public final PacketSender sendGameMessage(String string) {
        if (this.player.isBot) {
            return this;
        }
        if (string == null) {
            return this;
        }
        if (this.player.getQuestState(0) != 1) {
            return this;
        }
        PacketWriter packetWriter = PacketBuffer.allocateWriter(string.length() + 3);
        packetWriter.startVariableBytePacket(this.player.getOutboundCipher(), 253);
        packetWriter.writeString(string);
        packetWriter.finishVariableBytePacket();
        this.player.writePacketBuffer(packetWriter.getBuffer());
        return this;
    }

    public final PacketSender sendAccountStatus() {
        if (this.player.isBot) {
            return this;
        }
        PacketWriter packetWriter = PacketBuffer.allocateWriter(4);
        packetWriter.writeOpcode(this.player.getOutboundCipher(), 250);
        packetWriter.writeByte(this.player.getPlayerRights());
        packetWriter.writeByte(0);
        packetWriter.writeByte(this.player.gameMode);
        this.player.writePacketBuffer(packetWriter.getBuffer());
        this.player.setAppearanceUpdateRequired(true);
        return this;
    }

    public final void clearSidebarInterfaces() {
        int n = 0;
        while (n < 14) {
            this.setSidebarInterface(n, -1);
            ++n;
        }
    }

    public final PacketSender setSidebarInterface(int n, int n2) {
        this.player.setSidebarInterfaceId(n, n2);
        if (this.player.isBot) {
            return this;
        }
        PacketWriter packetWriter = PacketBuffer.allocateWriter(4);
        packetWriter.writeOpcode(this.player.getOutboundCipher(), 71);
        packetWriter.writeShort(n2);
        packetWriter.writeByte(n, ByteTransform.ADD);
        this.player.writePacketBuffer(packetWriter.getBuffer());
        return this;
    }

    public final PacketSender sendInterfaceItemModel(int n, int n2) {
        if (this.player.isBot) {
            return this;
        }
        PacketWriter packetWriter = PacketBuffer.allocateWriter(5);
        packetWriter.writeOpcode(this.player.getOutboundCipher(), 16);
        packetWriter.writeShort(n);
        packetWriter.writeShort(n2);
        this.player.writePacketBuffer(packetWriter.getBuffer());
        return this;
    }

    public final PacketSender sendInterfaceProgress(int n, int n2) {
        if (this.player.isBot) {
            return this;
        }
        PacketWriter packetWriter = PacketBuffer.allocateWriter(4);
        if (n2 < 0) {
            n2 = 0;
        }
        packetWriter.writeOpcode(this.player.getOutboundCipher(), 18);
        packetWriter.writeShort(n);
        packetWriter.writeByte(n2);
        this.player.writePacketBuffer(packetWriter.getBuffer());
        return this;
    }

    public final PacketSender sendProjectile(Position object, int n, int n2, byte by, byte by2, int n3, int n4, int n5, int n6, int n7, int n8) {
        if (this.player.isBot) {
            return this;
        }
        if (n > 1) {
            this.sendLocalPosition(new Position(((Position)object).getX() + n / 2, ((Position)object).getY() + n / 2));
        } else {
            this.sendLocalPosition((Position)object);
        }
        PacketWriter packetWriter = PacketBuffer.allocateWriter(16);
        packetWriter.writeOpcode(this.player.getOutboundCipher(), 117);
        packetWriter.writeByte(50);
        packetWriter.writeByte(by);
        packetWriter.writeByte(by2);
        packetWriter.writeShort(n2);
        packetWriter.writeShort(n3);
        packetWriter.writeByte(n6);
        packetWriter.writeByte(n7);
        packetWriter.writeShort(n4);
        packetWriter.writeShort(n5);
        packetWriter.writeByte(n8);
        packetWriter.writeByte(64);
        this.player.writePacketBuffer(packetWriter.getBuffer());
        return this;
    }

    private PacketSender sendLocalPosition(Position position) {
        if (this.player.isBot) {
            return this;
        }
        PacketWriter packetWriter = PacketBuffer.allocateWriter(3);
        packetWriter.writeOpcode(this.player.getOutboundCipher(), 85);
        int n = position.getY() - (this.player.getLastKnownRegionPosition().getRegionY() << 3) - 2;
        int n2 = position.getX() - (this.player.getLastKnownRegionPosition().getRegionX() << 3) - 3;
        packetWriter.writeByte(n, ByteTransform.NEGATE);
        packetWriter.writeByte(n2, ByteTransform.NEGATE);
        this.player.writePacketBuffer(packetWriter.getBuffer());
        return this;
    }

    public final PacketSender sendMapRegion() {
        this.player.getLastKnownRegionPosition().set(this.player.getPosition());
        this.player.refreshLocalViewArea();
        int n = this.player.getLastKnownRegionPosition().getRegionX() << 3;
        int n2 = this.player.getLastKnownRegionPosition().getRegionY() << 3;
        this.player.localX = this.player.getPosition().getX() - n;
        this.player.localY = this.player.getPosition().getY() - n2;
        if (this.player.isBot) {
            return this;
        }
        PacketWriter packetWriter = PacketBuffer.allocateWriter(5);
        packetWriter.writeOpcode(this.player.getOutboundCipher(), 73);
        packetWriter.writeShort(this.player.getPosition().getRegionX() + 6, ByteTransform.ADD);
        packetWriter.writeShort(this.player.getPosition().getRegionY() + 6);
        this.player.writePacketBuffer(packetWriter.getBuffer());
        return this;
    }

    public final PacketSender sendLogout() {
        this.player.cn = true;
        if (this.player.isBot) {
            return this;
        }
        PacketWriter packetWriter = PacketBuffer.allocateWriter(1);
        packetWriter.writeOpcode(this.player.getOutboundCipher(), 109);
        this.player.writePacketBuffer(packetWriter.getBuffer());
        return this;
    }

    public final PacketSender showInterface(int n) {
        this.player.setOpenInterfaceId(n);
        if (this.player.isBot) {
            return this;
        }
        if (n >= InterfaceDefinition.interfaceCount && n == 12140) {
            n = 8680;
        }
        if (n >= InterfaceDefinition.interfaceCount) {
            return this;
        }
        PacketWriter packetWriter = PacketBuffer.allocateWriter(3);
        packetWriter.writeOpcode(this.player.getOutboundCipher(), 97);
        packetWriter.writeShort(n);
        this.player.writePacketBuffer(packetWriter.getBuffer());
        return this;
    }

    public final PacketSender showWalkableInterface(int n) {
        if (this.player.isBot) {
            return this;
        }
        if (n >= InterfaceDefinition.interfaceCount) {
            return this;
        }
        PacketWriter packetWriter = PacketBuffer.allocateWriter(3);
        packetWriter.writeOpcode(this.player.getOutboundCipher(), 208);
        packetWriter.writeShort(n, ByteOrder.LITTLE);
        this.player.writePacketBuffer(packetWriter.getBuffer());
        return this;
    }

    public final PacketSender sendInterfaceScrollPosition(int n, int n2) {
        if (this.player.isBot) {
            return this;
        }
        PacketWriter packetWriter = PacketBuffer.allocateWriter(6);
        packetWriter.writeOpcode(this.player.getOutboundCipher(), 79);
        packetWriter.writeShort(8717, ByteOrder.LITTLE);
        packetWriter.writeShort(0, ByteTransform.ADD);
        this.player.writePacketBuffer(packetWriter.getBuffer());
        return this;
    }

    public final PacketSender sendMultiwayAreaState(boolean bl) {
        if (bl == this.player.cf) {
            return this;
        }
        this.player.cf = bl;
        if (this.player.isBot) {
            return this;
        }
        PacketWriter packetWriter = PacketBuffer.allocateWriter(5);
        packetWriter.writeOpcode(this.player.getOutboundCipher(), 61);
        packetWriter.writeByte(bl ? 1 : 0);
        this.player.writePacketBuffer(packetWriter.getBuffer());
        return this;
    }

    public final PacketSender showInterfaceWithInventory(int n, int n2) {
        this.player.setOpenInterfaceId(n);
        this.player.setInventoryOverlayInterfaceId(n2);
        if (this.player.isBot) {
            return this;
        }
        if (n2 >= InterfaceDefinition.interfaceCount) {
            return this;
        }
        PacketWriter packetWriter = PacketBuffer.allocateWriter(5);
        packetWriter.writeOpcode(this.player.getOutboundCipher(), 248);
        packetWriter.writeShort(n, ByteTransform.ADD);
        packetWriter.writeShort(n2);
        this.player.writePacketBuffer(packetWriter.getBuffer());
        return this;
    }

    public final PacketSender flashSidebarIcon(int n) {
        if (this.player.isBot) {
            return this;
        }
        PacketWriter packetWriter = PacketBuffer.allocateWriter(3);
        packetWriter.writeOpcode(this.player.getOutboundCipher(), 24);
        packetWriter.writeByte(-n, ByteTransform.ADD);
        this.player.writePacketBuffer(packetWriter.getBuffer());
        return this;
    }

    public final PacketSender closeInterfaces() {
        this.player.setOpenInterfaceId(0);
        this.player.setInventoryOverlayInterfaceId(0);
        this.player.activeBookItemId = 0;
        this.player.activeBookPageIndex = 0;
        if (this.player.isBot) {
            return this;
        }
        PacketWriter packetWriter = PacketBuffer.allocateWriter(2);
        packetWriter.writeOpcode(this.player.getOutboundCipher(), 219);
        this.player.writePacketBuffer(packetWriter.getBuffer());
        return this;
    }

    private PacketSender sendLocalScenePosition(Position position) {
        if (this.player.isBot) {
            return this;
        }
        PacketWriter packetWriter = PacketBuffer.allocateWriter(3);
        packetWriter.writeOpcode(this.player.getOutboundCipher(), 85);
        int n = position.getY() - 8 * this.player.getLastKnownRegionPosition().getRegionY();
        int n2 = position.getX() - 8 * this.player.getLastKnownRegionPosition().getRegionX();
        packetWriter.writeByte(n, ByteTransform.NEGATE);
        packetWriter.writeByte(n2, ByteTransform.NEGATE);
        this.player.writePacketBuffer(packetWriter.getBuffer());
        return this;
    }

    public final PacketSender sendGroundItemCreate(GroundItem groundItem) {
        if (this.player.isBot) {
            return this;
        }
        this.sendLocalScenePosition(groundItem.getPosition());
        PacketWriter packetWriter = PacketBuffer.allocateWriter(8);
        packetWriter.writeOpcode(this.player.getOutboundCipher(), 44);
        packetWriter.writeShort(groundItem.getItem().getId(), ByteTransform.ADD, ByteOrder.LITTLE);
        packetWriter.writeInt(groundItem.getItem().getAmount());
        packetWriter.writeByte(0);
        this.player.writePacketBuffer(packetWriter.getBuffer());
        return this;
    }

    public final PacketSender sendGroundItemRemove(GroundItem groundItem) {
        if (this.player.isBot) {
            return this;
        }
        this.sendLocalScenePosition(groundItem.getPosition());
        PacketWriter packetWriter = PacketBuffer.allocateWriter(4);
        packetWriter.writeOpcode(this.player.getOutboundCipher(), 156);
        packetWriter.writeByte(0, ByteTransform.SUBTRACT);
        packetWriter.writeShort(groundItem.getItem().getId());
        this.player.writePacketBuffer(packetWriter.getBuffer());
        return this;
    }

    public final PacketSender sendConfig(int n, int n2) {
        if (this.player.isBot) {
            return this;
        }
        if (n2 < 128 && -128 <= n2) {
            PacketWriter packetWriter = PacketBuffer.allocateWriter(4);
            packetWriter.writeOpcode(this.player.getOutboundCipher(), 36);
            packetWriter.writeShort(n, ByteOrder.LITTLE);
            packetWriter.writeByte(n2);
            this.player.writePacketBuffer(packetWriter.getBuffer());
        } else {
            PacketWriter packetWriter = PacketBuffer.allocateWriter(7);
            packetWriter.writeOpcode(this.player.getOutboundCipher(), 87);
            packetWriter.writeShort(n, ByteOrder.LITTLE);
            packetWriter.writeInt(n2, ByteOrder.MIDDLE);
            this.player.writePacketBuffer(packetWriter.getBuffer());
        }
        return this;
    }

    public final PacketSender sendInterfaceTextColor(int n, Color color) {
        if (this.player.isBot) {
            return this;
        }
        if (n >= InterfaceDefinition.interfaceCount) {
            return this;
        }
        PacketWriter packetWriter = PacketBuffer.allocateWriter(5);
        packetWriter.writeOpcode(this.player.getOutboundCipher(), 122);
        packetWriter.writeShort(n, ByteTransform.ADD, ByteOrder.LITTLE);
        n = color.getRed() >> 3 & 0x1F;
        int n2 = color.getGreen() >> 3 & 0x1F;
        int n3 = color.getBlue() >> 3 & 0x1F;
        packetWriter.writeShort(n << 10 | n2 << 5 | n3, ByteTransform.ADD, ByteOrder.LITTLE);
        this.player.writePacketBuffer(packetWriter.getBuffer());
        return this;
    }

    public final PacketSender sendInterfaceText(String string, int n) {
        if (this.player.isBot) {
            return this;
        }
        if (n >= InterfaceDefinition.interfaceCount) {
            if (n == 12144) {
                n = 8684;
            } else if (n == 12147) {
                n = 8925;
            } else if (n == 12150) {
                n = 8929;
                this.sendInterfaceText("", 8928);
            } else if (n == 12151) {
                n = 8930;
            } else if (n == 12152) {
                n = 8931;
            } else if (n == 12153) {
                n = 8932;
            } else if (n == 12154) {
                n = 8933;
            }
        }
        if (n >= InterfaceDefinition.interfaceCount) {
            return this;
        }
        PacketWriter packetWriter = PacketBuffer.allocateWriter(string.length() + 6);
        packetWriter.startVariableShortPacket(this.player.getOutboundCipher(), 126);
        packetWriter.writeString(string);
        packetWriter.writeShort(n, ByteTransform.ADD);
        packetWriter.finishVariableShortPacket();
        this.player.writePacketBuffer(packetWriter.getBuffer());
        return this;
    }

    public final PacketSender sendFriendStatus(long l, int n) {
        if (this.player.isBot) {
            return this;
        }
        PacketWriter packetWriter = PacketBuffer.allocateWriter(10);
        packetWriter.writeOpcode(this.player.getOutboundCipher(), 50);
        if (n != 0) {
            n += 9;
        }
        packetWriter.writeLong(l);
        packetWriter.writeByte(n);
        this.player.writePacketBuffer(packetWriter.getBuffer());
        return this;
    }

    public final PacketSender sendPrivateMessagingStatus(int n) {
        if (this.player.isBot) {
            return this;
        }
        PacketWriter packetWriter = PacketBuffer.allocateWriter(2);
        packetWriter.writeOpcode(this.player.getOutboundCipher(), 221);
        packetWriter.writeByte(2);
        this.player.writePacketBuffer(packetWriter.getBuffer());
        return this;
    }

    public final PacketSender sendPrivateMessage(long l, int n, int n2, int n3, byte[] byArray, int n4) {
        if (this.player.isBot) {
            return this;
        }
        PacketWriter packetWriter = PacketBuffer.allocateWriter(2048);
        packetWriter.startVariableBytePacket(this.player.getOutboundCipher(), 196);
        packetWriter.writeLong(l);
        packetWriter.writeInt(this.player.getSocialManager().nextPrivateMessageId());
        packetWriter.writeByte(n);
        packetWriter.writeByte(0);
        packetWriter.writeByte(n3);
        packetWriter.writeBytes(byArray, n4);
        packetWriter.finishVariableBytePacket();
        this.player.writePacketBuffer(packetWriter.getBuffer());
        return this;
    }

    public final PacketSender sendInterfaceModel(int n, int n2, int n3) {
        if (this.player.isBot) {
            return this;
        }
        if (n >= InterfaceDefinition.interfaceCount && n == 12145) {
            n = 8923;
        }
        if (n >= InterfaceDefinition.interfaceCount) {
            return this;
        }
        PacketWriter packetWriter = PacketBuffer.allocateWriter(7);
        packetWriter.writeOpcode(this.player.getOutboundCipher(), 246);
        packetWriter.writeShort(n == 0 ? -1 : n, ByteOrder.LITTLE);
        packetWriter.writeShort(n2);
        packetWriter.writeShort(n3);
        this.player.writePacketBuffer(packetWriter.getBuffer());
        return this;
    }

    public final PacketSender sendInterfaceModelId(int n, int n2) {
        if (this.player.isBot) {
            return this;
        }
        PacketWriter packetWriter = PacketBuffer.allocateWriter(5);
        packetWriter.writeOpcode(this.player.getOutboundCipher(), 8);
        packetWriter.writeShort(n, ByteTransform.ADD, ByteOrder.LITTLE);
        packetWriter.writeShort(n2);
        this.player.writePacketBuffer(packetWriter.getBuffer());
        return this;
    }

    public final PacketSender sendStillGraphic(int n, Position position, int n2) {
        if (this.player.isBot) {
            return this;
        }
        this.sendLocalScenePosition(position);
        PacketWriter packetWriter = PacketBuffer.allocateWriter(7);
        packetWriter.writeOpcode(this.player.getOutboundCipher(), 4);
        packetWriter.writeByte(0);
        packetWriter.writeShort(n);
        packetWriter.writeByte(position.getPlane());
        packetWriter.writeShort(n2);
        this.player.writePacketBuffer(packetWriter.getBuffer());
        return this;
    }

    public final PacketSender showChatboxInterface(int n) {
        this.player.setOpenInterfaceId(n);
        if (this.player.isBot) {
            return this;
        }
        if (n >= InterfaceDefinition.interfaceCount) {
            return this;
        }
        PacketWriter packetWriter = PacketBuffer.allocateWriter(3);
        packetWriter.writeOpcode(this.player.getOutboundCipher(), 164);
        packetWriter.writeShort(n, ByteOrder.LITTLE);
        this.player.writePacketBuffer(packetWriter.getBuffer());
        return this;
    }

    public final PacketSender sendInterfaceAnimation(int n, int n2) {
        if (this.player.isBot) {
            return this;
        }
        if (n >= InterfaceDefinition.interfaceCount) {
            return this;
        }
        PacketWriter packetWriter = PacketBuffer.allocateWriter(5);
        packetWriter.writeOpcode(this.player.getOutboundCipher(), 200);
        packetWriter.writeShort(n);
        packetWriter.writeShort(n2);
        this.player.writePacketBuffer(packetWriter.getBuffer());
        return this;
    }

    public final PacketSender sendObjectAnimation(int n, int n2, int n3, int n4) {
        if (this.player.isBot) {
            return this;
        }
        LoadedWorldObject loadedWorldObject = WorldObjectLookup.findObjectAt(n, n2, n3);
        if (loadedWorldObject == null) {
            return this;
        }
        this.sendLocalScenePosition(new Position(n, n2, n3));
        PacketWriter packetWriter = PacketBuffer.allocateWriter(5);
        packetWriter.writeOpcode(this.player.getOutboundCipher(), 160);
        packetWriter.writeByte(0, ByteTransform.SUBTRACT);
        packetWriter.writeByte((loadedWorldObject.getType() << 2) + (loadedWorldObject.getOrientation() & 3), ByteTransform.SUBTRACT);
        packetWriter.writeShort(127, ByteTransform.ADD);
        this.player.writePacketBuffer(packetWriter.getBuffer());
        return this;
    }

    public final PacketSender sendPlayerHeadOnInterface(int n) {
        if (this.player.isBot) {
            return this;
        }
        PacketWriter packetWriter = PacketBuffer.allocateWriter(3);
        packetWriter.writeOpcode(this.player.getOutboundCipher(), 185);
        packetWriter.writeShort(n, ByteTransform.ADD, ByteOrder.LITTLE);
        this.player.writePacketBuffer(packetWriter.getBuffer());
        return this;
    }

    public final PacketSender sendNpcHeadOnInterface(int n, int n2) {
        if (this.player.isBot) {
            return this;
        }
        if (n2 >= InterfaceDefinition.interfaceCount) {
            return this;
        }
        PacketWriter packetWriter = PacketBuffer.allocateWriter(5);
        packetWriter.writeOpcode(this.player.getOutboundCipher(), 75);
        packetWriter.writeShort(n, ByteTransform.ADD, ByteOrder.LITTLE);
        packetWriter.writeShort(n2, ByteTransform.ADD, ByteOrder.LITTLE);
        this.player.writePacketBuffer(packetWriter.getBuffer());
        return this;
    }

    public final PacketSender sendSoundEffect(int n, int n2, int n3) {
        if (this.player.isBot) {
            return this;
        }
        if (n < 0) {
            return this;
        }
        PacketWriter packetWriter = PacketBuffer.allocateWriter(7);
        packetWriter.writeOpcode(this.player.getOutboundCipher(), 174);
        packetWriter.writeShort(n);
        packetWriter.writeByte(1);
        packetWriter.writeShort(n3);
        this.player.writePacketBuffer(packetWriter.getBuffer());
        return this;
    }

    public final PacketSender sendMusicJingle(int n, int n2) {
        if (this.player.isBot) {
            return this;
        }
        PacketWriter packetWriter = PacketBuffer.allocateWriter(5);
        packetWriter.writeOpcode(this.player.getOutboundCipher(), 121);
        packetWriter.writeShort(n, ByteTransform.ADD, ByteOrder.LITTLE);
        packetWriter.writeShort(n2, ByteTransform.ADD, ByteOrder.BIG);
        this.player.writePacketBuffer(packetWriter.getBuffer());
        return this;
    }

    public final PacketSender sendPlayerOption(String string, int n, boolean bl) {
        if (this.player.isBot) {
            return this;
        }
        if (n > 0 && n <= 5) {
            if (this.player.ce[n - 1] != null && this.player.ce[n - 1].equals(string)) {
                return this;
            }
            this.player.ce[n - 1] = string;
        }
        PacketWriter packetWriter = PacketBuffer.allocateWriter(string.length() + 5);
        packetWriter.startVariableBytePacket(this.player.getOutboundCipher(), 104);
        packetWriter.writeByte(n, ByteTransform.NEGATE);
        packetWriter.writeByte(0, ByteTransform.ADD);
        packetWriter.writeString(string);
        packetWriter.finishVariableBytePacket();
        this.player.writePacketBuffer(packetWriter.getBuffer());
        return this;
    }

    public final PacketSender sendPlayerIndex() {
        if (this.player.isBot) {
            return this;
        }
        PacketWriter packetWriter = PacketBuffer.allocateWriter(4);
        packetWriter.writeOpcode(this.player.getOutboundCipher(), 249);
        packetWriter.writeByte(1, ByteTransform.ADD);
        packetWriter.writeShort(this.player.getIndex(), ByteTransform.ADD, ByteOrder.LITTLE);
        this.player.writePacketBuffer(packetWriter.getBuffer());
        return this;
    }

    public final PacketSender sendRunEnergy() {
        if (this.player.isBot) {
            return this;
        }
        PacketWriter packetWriter = PacketBuffer.allocateWriter(2);
        packetWriter.writeOpcode(this.player.getOutboundCipher(), 110);
        packetWriter.writeByte(this.player.getRunEnergyPercent());
        this.player.writePacketBuffer(packetWriter.getBuffer());
        return this;
    }

    public final PacketSender sendWeight() {
        if (this.player.isBot) {
            return this;
        }
        PacketWriter packetWriter = PacketBuffer.allocateWriter(3);
        packetWriter.writeOpcode(this.player.getOutboundCipher(), 240);
        Player player = this.player;
        packetWriter.writeShort((int)Math.floor(player.carriedWeight));
        this.player.writePacketBuffer(packetWriter.getBuffer());
        return this;
    }

    public final void refreshAutocastConfig() {
        if (this.player.getAutocastSpell() == null) {
            this.sendConfig(108, 0);
            this.sendConfig(43, this.player.getFightMode());
            this.sendInterfaceText("", 352);
            return;
        }
        this.sendConfig(43, this.player.getFightMode());
        this.sendConfig(108, 2);
    }

    public final PacketSender setInterfaceHiddenFlag(int n, int n2) {
        if (this.player.isBot) {
            return this;
        }
        if (n2 >= InterfaceDefinition.interfaceCount) {
            return this;
        }
        PacketWriter packetWriter = PacketBuffer.allocateWriter(4);
        packetWriter.writeOpcode(this.player.getOutboundCipher(), 171);
        packetWriter.writeByte(n);
        packetWriter.writeShort(n2);
        this.player.writePacketBuffer(packetWriter.getBuffer());
        return this;
    }

    public final PacketSender sendCameraShake(int n, int n2, int n3, int n4) {
        if (this.player.isBot) {
            return this;
        }
        PacketWriter packetWriter = PacketBuffer.allocateWriter(5);
        packetWriter.writeOpcode(this.player.getOutboundCipher(), 35);
        packetWriter.writeByte(2);
        packetWriter.writeByte(3);
        packetWriter.writeByte(2);
        packetWriter.writeByte(3);
        this.player.writePacketBuffer(packetWriter.getBuffer());
        return this;
    }

    public final PacketSender setInterfaceVisible(int n, boolean bl) {
        if (this.player.isBot) {
            return this;
        }
        if (n >= InterfaceDefinition.interfaceCount) {
            return this;
        }
        PacketWriter packetWriter = PacketBuffer.allocateWriter(4);
        packetWriter.writeOpcode(this.player.getOutboundCipher(), 171);
        packetWriter.writeByte(bl ? 0 : 1);
        packetWriter.writeShort(n);
        this.player.writePacketBuffer(packetWriter.getBuffer());
        return this;
    }

    public final PacketSender sendInterfacePosition(int n, int n2, int n3) {
        if (this.player.isBot) {
            return this;
        }
        if (n >= InterfaceDefinition.interfaceCount && n == 12145) {
            n = 8923;
        }
        if (n >= InterfaceDefinition.interfaceCount) {
            return this;
        }
        PacketWriter packetWriter = PacketBuffer.allocateWriter(7);
        packetWriter.writeOpcode(this.player.getOutboundCipher(), 70);
        packetWriter.writeShort(n2);
        packetWriter.writeShort(n3, ByteOrder.LITTLE);
        packetWriter.writeShort(n, ByteOrder.LITTLE);
        this.player.writePacketBuffer(packetWriter.getBuffer());
        return this;
    }

    public final void refreshSpecialAttackConfig() {
        if (this.player.isSpecialAttackEnabled()) {
            this.sendConfig(301, 1);
            return;
        }
        this.sendConfig(301, 0);
    }

    public final void refreshSpecialEnergyBar(int n) {
        int n2 = 10;
        int n3 = this.player.getSpecialEnergy() / 10;
        int n4 = 0;
        while (n4 < 10) {
            PacketSender packetSender;
            int n5 = --n;
            int n6 = n3 >= n2 ? 500 : 0;
            PacketSender packetSender2 = this;
            if (packetSender2.player.isBot) {
                packetSender = packetSender2;
            } else {
                PacketWriter packetWriter = PacketBuffer.allocateWriter(7);
                packetWriter.writeOpcode(packetSender2.player.getOutboundCipher(), 70);
                packetWriter.writeShort(n6);
                packetWriter.writeShort(0, ByteOrder.LITTLE);
                packetWriter.writeShort(n5, ByteOrder.LITTLE);
                packetSender2.player.writePacketBuffer(packetWriter.getBuffer());
                packetSender = packetSender2;
            }
            --n2;
            ++n4;
        }
    }

    public final void sendCameraPosition(int n, int n2, int n3, int n4, int n5) {
        if (this.player.isBot) {
            return;
        }
        PacketWriter packetWriter = PacketBuffer.allocateWriter(7);
        packetWriter.writeOpcode(this.player.getOutboundCipher(), 177);
        packetWriter.writeByte(n / 64);
        packetWriter.writeByte(n2 / 64);
        packetWriter.writeShort(n3);
        packetWriter.writeByte(0);
        packetWriter.writeByte(n5);
        this.player.writePacketBuffer(packetWriter.getBuffer());
    }

    public final void sendCameraLookAt(int n, int n2, int n3, int n4, int n5) {
        if (this.player.isBot) {
            return;
        }
        PacketWriter packetWriter = PacketBuffer.allocateWriter(7);
        packetWriter.writeOpcode(this.player.getOutboundCipher(), 166);
        packetWriter.writeByte(n / 64);
        packetWriter.writeByte(n2 / 64);
        packetWriter.writeShort(n3);
        packetWriter.writeByte(0);
        packetWriter.writeByte(n5);
        this.player.writePacketBuffer(packetWriter.getBuffer());
    }

    public final void resetCamera() {
        if (this.player.isBot) {
            return;
        }
        PacketWriter packetWriter = PacketBuffer.allocateWriter(1);
        packetWriter.writeOpcode(this.player.getOutboundCipher(), 107);
        this.player.writePacketBuffer(packetWriter.getBuffer());
        this.player.getUpdateState().setUpdateRequired(true);
    }

    public final void openDoubleDoorPair(int n, int n2, int n3, int n4, int n5, int n6) {
        int bl = this.player.getPosition().getPlane();
        LoadedWorldObject loadedWorldObject = WorldObjectLookup.findObjectByIdAt(n, n2, n3, bl);
        LoadedWorldObject loadedWorldObject2 = WorldObjectLookup.findObjectByIdAt(n4, n5, n6, bl);
        boolean bl2 = DoubleDoorHandler.usesWideDoubleDoorOffset(n);
        if (loadedWorldObject.getOrientation() == 0) {
            LoadedWorldObject loadedWorldObject3;
            LoadedWorldObject loadedWorldObject4;
            if (n3 < n6) {
                loadedWorldObject4 = loadedWorldObject;
                loadedWorldObject3 = loadedWorldObject2;
            } else {
                loadedWorldObject4 = loadedWorldObject2;
                loadedWorldObject3 = loadedWorldObject;
            }
            PacketSender.a(loadedWorldObject4, true, bl2);
            PacketSender.a(loadedWorldObject3, false, bl2);
            return;
        }
        if (loadedWorldObject.getOrientation() == 1) {
            LoadedWorldObject loadedWorldObject5;
            LoadedWorldObject loadedWorldObject6;
            if (n2 < n5) {
                loadedWorldObject6 = loadedWorldObject;
                loadedWorldObject5 = loadedWorldObject2;
            } else {
                loadedWorldObject6 = loadedWorldObject2;
                loadedWorldObject5 = loadedWorldObject;
            }
            PacketSender.a(loadedWorldObject6, true, bl2);
            PacketSender.a(loadedWorldObject5, false, bl2);
            return;
        }
        if (loadedWorldObject.getOrientation() == 2) {
            LoadedWorldObject loadedWorldObject7;
            LoadedWorldObject loadedWorldObject8;
            if (n3 < n6) {
                loadedWorldObject8 = loadedWorldObject2;
                loadedWorldObject7 = loadedWorldObject;
            } else {
                loadedWorldObject8 = loadedWorldObject;
                loadedWorldObject7 = loadedWorldObject2;
            }
            PacketSender.a(loadedWorldObject8, true, bl2);
            PacketSender.a(loadedWorldObject7, false, bl2);
            return;
        }
        if (loadedWorldObject.getOrientation() == 3) {
            LoadedWorldObject loadedWorldObject9;
            LoadedWorldObject loadedWorldObject10;
            if (n2 < n5) {
                loadedWorldObject10 = loadedWorldObject;
                loadedWorldObject9 = loadedWorldObject2;
            } else {
                loadedWorldObject10 = loadedWorldObject2;
                loadedWorldObject9 = loadedWorldObject;
            }
            PacketSender.a(loadedWorldObject10, true, bl2);
            PacketSender.a(loadedWorldObject9, false, bl2);
        }
    }

    private static void a(LoadedWorldObject loadedWorldObject, boolean firstDoor, boolean wideOffset) {
        int xOffset = 0;
        int yOffset = 0;
        int orientation = loadedWorldObject.getOrientation();
        if (orientation == 0) {
            xOffset = !firstDoor && wideOffset ? -2 : -1;
        } else if (orientation == 1) {
            if (!firstDoor && wideOffset) {
                xOffset = -1;
                yOffset = 2;
            } else {
                yOffset = 1;
            }
        } else if (orientation == 2) {
            if (!firstDoor && wideOffset) {
                xOffset = 2;
                yOffset = 1;
            } else {
                xOffset = 1;
            }
        } else if (orientation == 3) {
            if (!firstDoor && wideOffset) {
                xOffset = -1;
                yOffset = -2;
            } else {
                yOffset = -1;
            }
        }
        new DynamicObject(ServerSettings.placeholderObjectId, loadedWorldObject.getPosition().getX(), loadedWorldObject.getPosition().getY(), loadedWorldObject.getPosition().getPlane(), orientation, loadedWorldObject.getType(), loadedWorldObject.getWorldObject().getObjectId(), 1, false);
        int openedOrientation = 0;
        if (firstDoor) {
            if (orientation == 0) {
                openedOrientation = 3;
            } else if (orientation == 2) {
                openedOrientation = 1;
            }
        } else if (orientation == 0) {
            openedOrientation = wideOffset ? 3 : 1;
        } else if (orientation == 1) {
            openedOrientation = wideOffset ? 0 : 2;
        } else if (orientation == 2) {
            openedOrientation = wideOffset ? 1 : 3;
        } else if (orientation == 3) {
            openedOrientation = wideOffset ? 0 : 2;
        }
        new DynamicObject(loadedWorldObject.getWorldObject().getObjectId(), loadedWorldObject.getPosition().getX() + xOffset, loadedWorldObject.getPosition().getY() + yOffset, loadedWorldObject.getPosition().getPlane(), openedOrientation, loadedWorldObject.getType(), ServerSettings.placeholderObjectId, 1, false);
    }

    public final void openWestShiftedDoubleDoorPair(int n, int n2, int n3, int n4, int n5, int n6, int n7) {
        LoadedWorldObject loadedWorldObject = WorldObjectLookup.findObjectByIdAt(n, n3, n4, 0);
        LoadedWorldObject loadedWorldObject2 = WorldObjectLookup.findObjectByIdAt(n2, n5, n6, 0);
        int n8 = this.player.getPosition().getPlane();
        new DynamicObject(ServerSettings.placeholderObjectId, n3, n4, n8, loadedWorldObject.getOrientation(), 0, n, 3, false);
        new DynamicObject(ServerSettings.placeholderObjectId, n5, n6, n8, loadedWorldObject2.getOrientation(), 0, n2, 3, false);
        new DynamicObject(n, n3 - 1, n4, n8, loadedWorldObject.getOrientation() + 3, 0, ServerSettings.placeholderObjectId, 3, false);
        new DynamicObject(n2, n5 - 1, n6, n8, loadedWorldObject2.getOrientation() + 1, 0, ServerSettings.placeholderObjectId, 3, false);
        this.sendSoundEffect(318, 1, 0);
    }

    public final void openSingleDoor(int n, int n2, int n3, int n4) {
        LoadedWorldObject loadedWorldObject = WorldObjectLookup.findObjectByIdAt(n, n2, n3, n4);
        new DynamicObject(n, n2, n3, n4, loadedWorldObject.getOrientation() - 1, 0, n, 2, loadedWorldObject.getOrientation(), n2, n3, false);
        this.sendSoundEffect(318, 1, 0);
    }

    public final void openSouthShiftedSingleDoor(int n, int n2, int n3, int n4) {
        LoadedWorldObject loadedWorldObject = WorldObjectLookup.findObjectByIdAt(n, n2, n3, n4);
        new DynamicObject(ServerSettings.placeholderObjectId, n2, n3, n4, loadedWorldObject.getOrientation() - 1, loadedWorldObject.getType(), n, 1, loadedWorldObject.getOrientation(), n2, n3, false);
        new DynamicObject(n, n2, n3 - 1, n4, loadedWorldObject.getOrientation() - 1, loadedWorldObject.getType(), ServerSettings.placeholderObjectId, 1, loadedWorldObject.getOrientation(), n2, n3, false);
        this.sendSoundEffect(318, 1, 0);
    }

    public final void openDoubleDoorPair(int n, int n2, int n3, int n4, int n5, int n6, int n7) {
        LoadedWorldObject loadedWorldObject = WorldObjectLookup.findObjectByIdAt(n, n3, n4, 0);
        LoadedWorldObject loadedWorldObject2 = WorldObjectLookup.findObjectByIdAt(n2, n5, n6, 0);
        new DynamicObject(n, n3, n4, 0, loadedWorldObject.getOrientation() - 1, 0, n, 1, loadedWorldObject.getOrientation(), n3, n4, false);
        new DynamicObject(n2, n5, n6, 0, loadedWorldObject2.getOrientation() + 1, 0, n2, 1, loadedWorldObject2.getOrientation(), n5, n6, false);
        this.sendSoundEffect(318, 1, 0);
    }

    public final void openNorthShiftedDoubleDoorPair(int n, int n2, int n3, int n4, int n5, int n6, int n7) {
        LoadedWorldObject loadedWorldObject = WorldObjectLookup.findObjectByIdAt(n, n3, n4, 0);
        LoadedWorldObject loadedWorldObject2 = WorldObjectLookup.findObjectByIdAt(n2, n5, n6, 0);
        new DynamicObject(ServerSettings.placeholderObjectId, n3, n4, 0, loadedWorldObject.getOrientation() - 1, 0, n, 1, loadedWorldObject.getOrientation(), n3, n4, false);
        new DynamicObject(ServerSettings.placeholderObjectId, n5, n6, 0, loadedWorldObject2.getOrientation() + 1, 0, n2, 1, loadedWorldObject2.getOrientation(), n5, n6, false);
        new DynamicObject(n, n3, n4 + 1, 0, loadedWorldObject.getOrientation() - 1, 0, ServerSettings.placeholderObjectId, 1, loadedWorldObject.getOrientation(), n3, n4, false);
        new DynamicObject(n2, n5, n6 + 1, 0, loadedWorldObject2.getOrientation() + 1, 0, ServerSettings.placeholderObjectId, 1, loadedWorldObject2.getOrientation(), n5, n6, false);
        this.sendSoundEffect(318, 1, 0);
    }

    static /* synthetic */ Player getPlayer(PacketSender packetSender) {
        return packetSender.player;
    }
}
