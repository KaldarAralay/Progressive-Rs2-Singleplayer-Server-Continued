/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.magic;

import com.rs2.bot.combat.BotCombatEscapeHandler;
import com.rs2.model.Position;
import com.rs2.model.combat.hit.HitDefinition;
import com.rs2.model.player.Player;
import com.rs2.model.quest.QuestDefinition;
import com.rs2.model.skill.magic.MagicSpellAction;
import com.rs2.model.skill.magic.SpellDefinition;
import com.rs2.util.GameUtil;

public final class SelfCastSpellAction
extends MagicSpellAction {
    private final /* synthetic */ SpellDefinition selfSpell;
    private final /* synthetic */ Player caster;

    public SelfCastSpellAction(Player player, SpellDefinition spellDefinition, SpellDefinition spellDefinition2, Player player2) {
        super(player, spellDefinition, (byte)0);
        this.selfSpell = spellDefinition2;
        this.caster = player2;
    }

    @Override
    public final boolean prepareCast() {
        switch (this.selfSpell) {
            case BONES_TO_PEACHES: {
                return this.castBonesToFruit(true);
            }
            case BONES_TO_BANANAS: {
                return this.castBonesToFruit(false);
            }
            case CHARGE: {
                if (this.caster.getChargeCooldownTimer().hasElapsed()) {
                    this.caster.activateChargeSpell();
                    this.caster.getAttackDelayTimer().setDelayTicks(this.caster.getAttackDelayTimer().getDelayTicks() + 2);
                    break;
                }
                Player player = this.caster;
                player.packetSender.sendGameMessage("You cannot use this spell yet!");
                return false;
            }
            case aR: {
                return this.caster.getTeleportManager().castSpellbookTeleport(new Position(3241 + GameUtil.randomInclusive(1), 3195 + GameUtil.randomInclusive(1), 0));
            }
            case aS: {
                return this.caster.getTeleportManager().castSpellbookTeleport(new Position(3109 + GameUtil.randomInclusive(1), 3352 + GameUtil.randomInclusive(1), 0));
            }
            case aT: {
                return this.caster.getTeleportManager().castSpellbookTeleport(new Position(2978 + GameUtil.randomInclusive(1), 3506 + GameUtil.randomInclusive(1), 0));
            }
            case aU: {
                if (this.caster.getQuestState(72) != 1) {
                    Object object = QuestDefinition.forId(72);
                    object = ((QuestDefinition)object).getName();
                    Player player = this.caster;
                    player.packetSender.sendGameMessage("You need to complete " + (String)object + " to do this.");
                    return false;
                }
                return this.caster.getTeleportManager().castSpellbookTeleport(new Position(3431 + GameUtil.randomInclusive(1), 3460 + GameUtil.randomInclusive(1), 0));
            }
            case aV: {
                if (this.caster.getQuestState(72) != 1) {
                    Object object = QuestDefinition.forId(72);
                    object = ((QuestDefinition)object).getName();
                    Player player = this.caster;
                    player.packetSender.sendGameMessage("You need to complete " + (String)object + " to do this.");
                    return false;
                }
                return this.caster.getTeleportManager().castSpellbookTeleport(new Position(3545 + GameUtil.randomInclusive(1), 3528 + GameUtil.randomInclusive(1), 0));
            }
            case aW: {
                if (this.caster.getPlayerRights() < 2) {
                    Player player = this.caster;
                    player.packetSender.sendGameMessage("This spell has been temporarily disabled!");
                    return false;
                }
                return this.caster.getTeleportManager().castSpellbookTeleport(new Position(2501 + GameUtil.randomInclusive(1), 3291 + GameUtil.randomInclusive(1), 0));
            }
            case aX: {
                return this.caster.getTeleportManager().castSpellbookTeleport(new Position(2980, 3762 + GameUtil.randomInclusive(1), 0));
            }
            case aY: {
                if (this.caster.getQuestState(72) != 1) {
                    Object object = QuestDefinition.forId(72);
                    object = ((QuestDefinition)object).getName();
                    Player player = this.caster;
                    player.packetSender.sendGameMessage("You need to complete " + (String)object + " to do this.");
                    return false;
                }
                return this.caster.getTeleportManager().castSpellbookTeleport(new Position(3565 + GameUtil.randomInclusive(1), 3314 + GameUtil.randomInclusive(1), 0));
            }
            case aZ: {
                if (this.caster.getQuestState(62) != 1) {
                    Object object = QuestDefinition.forId(62);
                    object = ((QuestDefinition)object).getName();
                    Player player = this.caster;
                    player.packetSender.sendGameMessage("You need to complete " + (String)object + " to do this.");
                    return false;
                }
                return this.caster.getTeleportManager().castSpellbookTeleport(new Position(2799 + GameUtil.randomInclusive(1), 9212 + GameUtil.randomInclusive(1), 0));
            }
            case VARROCK_TELEPORT: {
                boolean bl = this.caster.getTeleportManager().castSpellbookTeleport(new Position(3213 + GameUtil.randomInclusive(1), 3423 + GameUtil.randomInclusive(1), 0));
                if (this.caster.botEnabled && !bl && this.caster.botCombatState.startsWith("escape")) {
                    this.caster.botCombatState = "tele";
                    BotCombatEscapeHandler.startBotCombatWalkingEscape(this.caster);
                } else if (this.caster.botEnabled && !bl && this.caster.botCombatState.equals("tele")) {
                    this.caster.botCombatState = "run";
                }
                return bl;
            }
            case LUMBRIDGE_TELEPORT: {
                return this.caster.getTeleportManager().castSpellbookTeleport(new Position(3222 + GameUtil.randomInclusive(1), 3218 + GameUtil.randomInclusive(1), 0));
            }
            case HOME_TELEPORT: {
                return this.caster.getTeleportManager().castSpellbookTeleport(new Position(3222 + GameUtil.randomInclusive(1), 3218 + GameUtil.randomInclusive(1), 0));
            }
            case FALADOR_TELEPORT: {
                return this.caster.getTeleportManager().castSpellbookTeleport(new Position(2964 + GameUtil.randomInclusive(1), 3378 + GameUtil.randomInclusive(1), 0));
            }
            case CAMELOT_TELEPORT: {
                return this.caster.getTeleportManager().castSpellbookTeleport(new Position(2757 + GameUtil.randomInclusive(1), 3479 + GameUtil.randomInclusive(1), 0));
            }
            case ARDOUGNE_TELEPORT: {
                if (this.caster.getQuestState(71) != 1) {
                    Object object = QuestDefinition.forId(71);
                    object = ((QuestDefinition)object).getName();
                    Player player = this.caster;
                    player.packetSender.sendGameMessage("You need to complete " + (String)object + " to do this.");
                    return false;
                }
                return this.caster.getTeleportManager().castSpellbookTeleport(new Position(2662 + GameUtil.randomInclusive(1), 3305 + GameUtil.randomInclusive(1), 0));
            }
            case WATCHTOWER_TELEPORT: {
                if (this.caster.getQuestState(101) != 1) {
                    Object object = QuestDefinition.forId(101);
                    object = ((QuestDefinition)object).getName();
                    Player player = this.caster;
                    player.packetSender.sendGameMessage("You need to complete " + (String)object + " to do this.");
                    return false;
                }
                return this.caster.getTeleportManager().castSpellbookTeleport(new Position(2546 + GameUtil.randomInclusive(1), 3112 + GameUtil.randomInclusive(1), 2));
            }
            case TROLLHEIM_TELEPORT: {
                if (this.caster.getPlayerRights() < 2) {
                    Player player = this.caster;
                    player.packetSender.sendGameMessage("This spell has been temporarily disabled!");
                    return false;
                }
                if (this.caster.getQuestState(31) != 1) {
                    Object object = QuestDefinition.forId(31);
                    object = ((QuestDefinition)object).getName();
                    Player player = this.caster;
                    player.packetSender.sendGameMessage("You need to complete " + (String)object + " to do this.");
                    return false;
                }
                return this.caster.getTeleportManager().castSpellbookTeleport(new Position(2910 + GameUtil.randomInclusive(1), 3612 + GameUtil.randomInclusive(1), 0));
            }
            case APE_ATOLL_TELEPORT: {
                if (this.caster.getQuestState(62) != 1) {
                    Object object = QuestDefinition.forId(62);
                    object = ((QuestDefinition)object).getName();
                    Player player = this.caster;
                    player.packetSender.sendGameMessage("You need to complete " + (String)object + " to do this.");
                    return false;
                }
                return this.caster.getTeleportManager().castSpellbookTeleport(new Position(2798 + GameUtil.randomInclusive(1), 2798 + GameUtil.randomInclusive(1), 1));
            }
            case PADDEWWA_TELEPORT: {
                return this.caster.getTeleportManager().castSpellbookTeleport(new Position(3098 + GameUtil.randomInclusive(1), 9884 + GameUtil.randomInclusive(1), 0));
            }
            case SENNTISTEN_TELEPORT: {
                return this.caster.getTeleportManager().castSpellbookTeleport(new Position(3321 + GameUtil.randomInclusive(1), 3335 + GameUtil.randomInclusive(1), 0));
            }
            case CARRALLANGAR_TELEPORT: {
                return this.caster.getTeleportManager().castSpellbookTeleport(new Position(3156 + GameUtil.randomInclusive(1), 3666 + GameUtil.randomInclusive(1), 0));
            }
            case KHARYRLL_TELEPORT: {
                if (this.caster.getQuestState(72) != 1) {
                    Object object = QuestDefinition.forId(72);
                    object = ((QuestDefinition)object).getName();
                    Player player = this.caster;
                    player.packetSender.sendGameMessage("You need to complete " + (String)object + " to do this.");
                    return false;
                }
                return this.caster.getTeleportManager().castSpellbookTeleport(new Position(3492 + GameUtil.randomInclusive(1), 3471 + GameUtil.randomInclusive(1), 0));
            }
            case LASSAR_TELEPORT: {
                return this.caster.getTeleportManager().castSpellbookTeleport(new Position(3001 + GameUtil.randomInclusive(1), 3470 + GameUtil.randomInclusive(1), 0));
            }
            case DAREEYAK_TELEPORT: {
                return this.caster.getTeleportManager().castSpellbookTeleport(new Position(2970 + GameUtil.randomInclusive(1), 3697 + GameUtil.randomInclusive(1), 0));
            }
            case ANNAKARL_TELEPORT: {
                return this.caster.getTeleportManager().castSpellbookTeleport(new Position(3287 + GameUtil.randomInclusive(1), 3886 + GameUtil.randomInclusive(1), 0));
            }
            case GHORROCK_TELEPORT: {
                return this.caster.getTeleportManager().castSpellbookTeleport(new Position(2977 + GameUtil.randomInclusive(1), 3873 + GameUtil.randomInclusive(1), 0));
            }
        }
        return true;
    }

    @Override
    public final void applyImpact(HitDefinition hitDefinition) {
    }
}

