/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.interaction;

import com.rs2.ServerSettings;
import com.rs2.model.Entity;
import com.rs2.model.EntityTargetMovement;
import com.rs2.model.GameplayHelper;
import com.rs2.model.World;
import com.rs2.model.gameplay.abyss.AbyssManager;
import com.rs2.model.gameplay.magetrainingarena.MageTrainingArenaRewardShop;
import com.rs2.model.npc.Npc;
import com.rs2.model.player.Player;
import com.rs2.model.quest.QuestDefinition;
import com.rs2.model.shop.ShopManager;
import com.rs2.model.skill.runecrafting.RunecraftingHandler;
import com.rs2.model.task.TickTask;
import com.rs2.util.GameUtil;

public final class ThirdNpcActionTask
extends TickTask {
    private final /* synthetic */ Player player;
    private final /* synthetic */ int actionSequence;
    private final /* synthetic */ Npc npc;

    public ThirdNpcActionTask(int n, boolean bl, Player player, int n2, Npc npc) {
        super(1, true);
        this.player = player;
        this.actionSequence = n2;
        this.npc = npc;
    }

    @Override
    public final void execute() {
        if (this.player == null || !this.player.isCurrentActionSequence(this.actionSequence) || this.npc.isDead()) {
            this.stop();
            return;
        }
        if (!this.player.isWithinReach(this.npc, 1) || this.player.isOverlapping(this.npc)) {
            return;
        }
        if (this.npc.getNpcId() == 3103) {
            MageTrainingArenaRewardShop.openRewardShop(this.player);
        }
        if (!GameUtil.hasClearPath(this.player.getPosition(), this.npc.getPosition(), true)) {
            return;
        }
        EntityTargetMovement.clearMovementTarget(this.player);
        Object object = World.getNpcs()[this.player.getInteractionTargetIndex()];
        this.player.getUpdateState().setFaceEntity(((Entity)object).getEncodedIndex());
        ((Entity)object).getUpdateState().setFaceEntity(this.player.getEncodedIndex());
        switch (this.player.getInteractionTargetId()) {
            case 553: {
                RunecraftingHandler.startAbyssMageTeleport(this.player, (Npc)object);
                break;
            }
            case 70: 
            case 1596: 
            case 1597: 
            case 1598: 
            case 1599: 
            case 3887: {
                ShopManager.openShop(this.player, GameplayHelper.getNpcShopId(this.player.getInteractionTargetId()));
                break;
            }
            case 2257: {
                if (!this.player.isMember()) {
                    this.player.packetSender.sendGameMessage("You need a members account to access members content.");
                    break;
                }
                if (ServerSettings.freeToPlayWorld) {
                    this.player.packetSender.sendGameMessage("You need to be in members world to access members content.");
                    break;
                }
                if (this.player.getQuestState(14) != 1) {
                    object = QuestDefinition.forId(14);
                    object = ((QuestDefinition)object).getName();
                    this.player.getDialogueManager().showOneLineStatement("You need to complete " + (String)object + " to do this.");
                    this.player.getDialogueManager().finishDialogue();
                    break;
                }
                if (this.player.enterTheAbyssMiniquestState == 1) {
                    AbyssManager.startAbyssMageTeleport(this.player, (Npc)object);
                    break;
                }
                this.player.getDialogueManager().showOneLineStatement("You need to complete Enter the Abyss miniquest to do this.");
                this.player.getDialogueManager().finishDialogue();
            }
        }
        this.stop();
    }
}

