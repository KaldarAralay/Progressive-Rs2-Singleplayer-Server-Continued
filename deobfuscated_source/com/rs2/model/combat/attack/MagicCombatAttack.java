/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.combat.attack;

import com.rs2.ServerSettings;
import com.rs2.bot.combat.BotCombatEscapeHandler;
import com.rs2.model.Entity;
import com.rs2.model.World;
import com.rs2.model.combat.AttackValidationResult;
import com.rs2.model.combat.CombatAction;
import com.rs2.model.combat.CombatCycleEvent;
import com.rs2.model.combat.CombatManager;
import com.rs2.model.combat.CombatType;
import com.rs2.model.combat.attack.BaseCombatAttack;
import com.rs2.model.combat.attack.CombatAttackState;
import com.rs2.model.combat.effect.CombatEffect;
import com.rs2.model.combat.effect.PoisonEffect;
import com.rs2.model.combat.effect.StatDrainEffect;
import com.rs2.model.combat.hit.HitDefinition;
import com.rs2.model.combat.requirement.CombatRequirement;
import com.rs2.model.combat.requirement.GodStaffRequirement;
import com.rs2.model.combat.requirement.MagicCombatLevelRequirement;
import com.rs2.model.combat.requirement.MagicCombatMembersRequirement;
import com.rs2.model.combat.requirement.MagicCombatRuneRequirement;
import com.rs2.model.gameplay.duel.DuelRule;
import com.rs2.model.npc.Npc;
import com.rs2.model.player.Player;
import com.rs2.model.skill.magic.SpellDefinition;
import com.rs2.model.task.CycleEventContainer;
import com.rs2.util.GameUtil;

public final class MagicCombatAttack
extends BaseCombatAttack {
    private SpellDefinition spell;

    public MagicCombatAttack(Entity entity, Entity entity2, SpellDefinition spellDefinition) {
        super(entity, entity2);
        this.spell = spellDefinition;
    }

    @Override
    public final CombatAttackState getState() {
        if (!this.spell.isCombatSpell() || this.spell.getHitDefinition() == null) {
            return CombatAttackState.a;
        }
        if (ServerSettings.freeToPlayWorld && this.spell.isMembersOnly()) {
            if (this.getAttacker().isPlayer()) {
                Player player = (Player)this.getAttacker();
                player.packetSender.sendGameMessage("You need to be in members world to access members content.");
                ((Player)this.getAttacker()).setAutocastSpell(null);
                CombatManager.stopCombat(this.getAttacker());
            }
            return CombatAttackState.a;
        }
        if (this.getAttacker().isPlayer() && DuelRule.NO_MAGIC.isEnabledFor((Player)this.getAttacker())) {
            if (this.getAttacker().isPlayer()) {
                Player player = (Player)this.getAttacker();
                player.packetSender.sendGameMessage("Magic attacks have been disabled during this fight!");
                ((Player)this.getAttacker()).setAutocastSpell(null);
                CombatManager.stopCombat(this.getAttacker());
            }
            return CombatAttackState.a;
        }
        if (this.spell.equals((Object)SpellDefinition.CRUMBLE_UNDEAD) && !Npc.isUndead(this.getTarget())) {
            Player player = (Player)this.getAttacker();
            player.packetSender.sendGameMessage("This spell only affects skeletons, zombies, ghosts and shades.");
            CombatManager.stopCombat(this.getAttacker());
            return CombatAttackState.a;
        }
        if (this.spell.getPrimaryEffect() != null) {
            if (this.spell.getPrimaryEffect() instanceof StatDrainEffect) {
                StatDrainEffect statDrainEffect = (StatDrainEffect)this.spell.getPrimaryEffect();
                int n = statDrainEffect.getSkillId();
                if (this.getTarget().isPlayer()) {
                    Player player = (Player)this.getTarget();
                    int n2 = player.getSkillManager().getBaseLevel(n);
                    int n3 = player.getSkillManager().getCurrentLevels()[n];
                    if (n3 < n2) {
                        if (this.getAttacker().isPlayer()) {
                            Player player2 = (Player)this.getAttacker();
                            player2.packetSender.sendGameMessage("The target is immune to that spell right now!");
                            CombatManager.stopCombat(this.getAttacker());
                        }
                        return CombatAttackState.a;
                    }
                } else {
                    Npc npc = (Npc)this.getTarget();
                    int n4 = 0;
                    int n5 = 0;
                    if (n == 0) {
                        n4 = npc.getBaseAttackLevel();
                        n5 = npc.getCurrentAttackLevel();
                    } else if (n == 2) {
                        n4 = npc.getBaseStrengthLevel();
                        n5 = npc.getCurrentStrengthLevel();
                    } else if (n == 1) {
                        n4 = npc.getBaseDefenceLevel();
                        n5 = npc.getCurrentDefenceLevel();
                    } else if (n == 6) {
                        n4 = npc.getBaseMagicLevel();
                        n5 = npc.getCurrentMagicLevel();
                    } else if (n == 4) {
                        n4 = npc.getBaseRangedLevel();
                        n5 = npc.getCurrentRangedLevel();
                    }
                    if (n5 < n4) {
                        if (this.getAttacker().isPlayer()) {
                            Player player = (Player)this.getAttacker();
                            player.packetSender.sendGameMessage("The target is immune to that spell right now!");
                            CombatManager.stopCombat(this.getAttacker());
                        }
                        return CombatAttackState.a;
                    }
                }
            } else if (!this.getTarget().canApplyCombatEffect(this.spell.getPrimaryEffect())) {
                if (this.getAttacker().isPlayer()) {
                    Player player = (Player)this.getAttacker();
                    player.packetSender.sendGameMessage("The target is immune to that spell right now!");
                    CombatManager.stopCombat(this.getAttacker());
                }
                return CombatAttackState.a;
            }
        }
        return super.getState();
    }

    @Override
    public final CombatType getCombatType() {
        if (!this.spell.isCombatSpell() || this.spell.getHitDefinition() == null) {
            return null;
        }
        return this.spell.getHitDefinition().getAttackStyle().getCombatType();
    }

    @Override
    public final int getAttackRange() {
        return 10;
    }

    @Override
    public final void prepare() {
        if (!this.spell.isCombatSpell() || this.spell.getHitDefinition() == null) {
            return;
        }
        Object object = this.spell.getRuneCosts();
        int n = -1;
        if (this.spell == SpellDefinition.FLAMES_OF_ZAMORAK) {
            n = 2417;
        } else if (this.spell == SpellDefinition.CLAWS_OF_GUTHIX) {
            n = 2416;
        } else if (this.spell == SpellDefinition.SARADOMIN_STRIKE) {
            n = 2415;
        }
        if (this.spell == SpellDefinition.IBAN_BLAST) {
            n = 1409;
        }
        if (this.spell == SpellDefinition.MAGIC_DART) {
            n = 4170;
        }
        int n2 = (n != -1 ? 1 : 0) + (object != null ? 1 : 0) + 1 + (this.spell.isMembersOnly() ? 1 : 0);
        CombatRequirement[] combatRequirementArray = new CombatRequirement[n2];
        int n3 = 0;
        if (this.spell.isMembersOnly()) {
            ++n3;
            combatRequirementArray[0] = new MagicCombatMembersRequirement(this);
        }
        if (object != null) {
            combatRequirementArray[n3] = new MagicCombatRuneRequirement(this, this.spell);
        }
        int n4 = ++n3;
        ++n3;
        combatRequirementArray[n4] = new MagicCombatLevelRequirement(this, 6, this.spell.getRequiredLevel());
        if (n != -1) {
            combatRequirementArray[n3] = new GodStaffRequirement(this, 3, n, 1, false);
        }
        if (this.spell.getAnimationId() != -1) {
            this.setAnimationId(this.spell.getAnimationId());
            this.setAttackSoundId(this.spell.getCastSoundId());
        }
        object = this.spell.getHitDefinition().copy();
        ((HitDefinition)object).setSpell(this.spell);
        if (ServerSettings.modernCombatSystemEnabled) {
            ((HitDefinition)object).setMaxDamage(CombatManager.calculateSpellMaxHit(this.getAttacker(), this.spell));
        } else {
            if (this.getAttacker().isChargeSpellActive() && (this.spell == SpellDefinition.FLAMES_OF_ZAMORAK || this.spell == SpellDefinition.CLAWS_OF_GUTHIX || this.spell == SpellDefinition.SARADOMIN_STRIKE)) {
                ((HitDefinition)object).setMaxDamage(((HitDefinition)object).getMaxDamage() + 10);
            }
            if (this.getAttacker().isPlayer()) {
                Player player = (Player)this.getAttacker();
                if (player.getEquipmentManager().getItemIdAtSlot(9) == 777 && (this.spell == SpellDefinition.WIND_BOLT || this.spell == SpellDefinition.WATER_BOLT || this.spell == SpellDefinition.EARTH_BOLT || this.spell == SpellDefinition.FIRE_BOLT)) {
                    ((HitDefinition)object).setMaxDamage(((HitDefinition)object).getMaxDamage() + 3);
                }
                if (player.getEquipmentManager().getItemIdAtSlot(3) == 4675 || player.getEquipmentManager().getItemIdAtSlot(3) == 4710 || player.getEquipmentManager().getItemIdAtSlot(3) == 6914) {
                    ((HitDefinition)object).setMaxDamage((int)((double)((HitDefinition)object).getMaxDamage() * 1.1));
                }
                if (this.spell == SpellDefinition.MAGIC_DART) {
                    ((HitDefinition)object).setMaxDamage(((HitDefinition)object).getMaxDamage() + player.getSkillManager().getBaseLevel(6) / 10);
                }
            }
        }
        boolean bl = this.spell == SpellDefinition.SUMMON_ZOMBIE || this.spell == SpellDefinition.CHAOS_ELEMENTAL_DISARM || this.spell == SpellDefinition.CHAOS_ELEMENTAL_RANDOM_TELEPORT;
        CombatEffect combatEffect = this.spell.getSecondaryEffect();
        if (this.spell.getSecondaryEffect() != null && this.spell.getSecondaryEffect() instanceof PoisonEffect && !GameUtil.rollChance(0.125)) {
            combatEffect = null;
        }
        this.setHitDefinitions(new HitDefinition[]{((HitDefinition)object).enableRandomDamage().enableAccuracyCheck().addEffects(new CombatEffect[]{this.spell.getPrimaryEffect(), combatEffect}).setAlwaysHits(bl)});
        this.setAttackerGraphic(this.spell.getCastGraphic());
        this.setAttackDelay(5);
        this.setRequirements(combatRequirementArray);
    }

    @Override
    public final int execute(CycleEventContainer cycleEventContainer) {
        Object object;
        Object object2;
        if (this.getAttacker().isPlayer()) {
            object2 = (Player)this.getAttacker();
            if (!((Player)object2).isAutocastEnabled()) {
                this.getAttacker().getMovementQueue().clear();
            }
            if (((Player)object2).getQueuedCombatSpell() == this.spell) {
                ((Player)object2).setQueuedCombatSpell(null);
                cycleEventContainer.stop();
            }
        }
        object2 = this.spell.getHitDefinition().copy();
        ((HitDefinition)object2).setSpell(this.spell);
        if (((HitDefinition)object2).isMultiTargetSpreadEnabled() && ((object = this.spell) == SpellDefinition.SMOKE_BURST || object == SpellDefinition.SHADOW_BURST || object == SpellDefinition.BLOOD_BURST || object == SpellDefinition.ICE_BURST || object == SpellDefinition.SMOKE_BARRAGE || object == SpellDefinition.SHADOW_BARRAGE || object == SpellDefinition.BLOOD_BARRAGE || object == SpellDefinition.ICE_BARRAGE) && this.getAttacker().isInMultiCombatArea()) {
            AttackValidationResult attackValidationResult;
            Entity entity;
            int n = 0;
            Entity[] entityArray = World.getPlayers();
            int n2 = entityArray.length;
            int n3 = 0;
            while (n3 < n2) {
                entity = entityArray[n3];
                if (entity != null && entity != this.getAttacker() && entity != this.getTarget() && GameUtil.isWithinDistance(this.getTarget().getPosition(), entity.getPosition(), 1) && (attackValidationResult = CombatCycleEvent.validateAttack(this.getAttacker(), entity)) == AttackValidationResult.VALID) {
                    if (n > 13) break;
                    ((HitDefinition)object2).setMultiTargetSpreadEnabled(false);
                    ((HitDefinition)object2).enableAccuracyCheck(true);
                    ((HitDefinition)object2).b(0.75);
                    ((HitDefinition)object2).setDelay(-1);
                    new CombatAction(this.getAttacker(), entity, (HitDefinition)object2).queue();
                    ++n;
                }
                ++n3;
            }
            entityArray = World.getNpcs();
            n2 = entityArray.length;
            n3 = 0;
            while (n3 < n2) {
                entity = entityArray[n3];
                if (entity != null && entity != this.getTarget() && this.getTarget().isWithinReach(entity, 1) && (attackValidationResult = CombatCycleEvent.validateAttack(this.getAttacker(), entity)) == AttackValidationResult.VALID) {
                    if (n > 13) break;
                    ((HitDefinition)object2).setMultiTargetSpreadEnabled(false);
                    ((HitDefinition)object2).enableAccuracyCheck(true);
                    ((HitDefinition)object2).b(0.75);
                    ((HitDefinition)object2).setDelay(-1);
                    new CombatAction(this.getAttacker(), entity, (HitDefinition)object2).queue();
                    ++n;
                }
                ++n3;
            }
        }
        if (this.getAttacker().isPlayer()) {
            object = (Player)this.getAttacker();
            ((Player)object).getSkillManager().addExperience(6, this.spell.getExperience());
        }
        int n = super.execute(cycleEventContainer);
        return n;
    }

    @Override
    public final void onRequirementFailed() {
        if (this.getAttacker().isPlayer()) {
            Player player = (Player)this.getAttacker();
            if (player.botEnabled) {
                if (player.isInWilderness()) {
                    BotCombatEscapeHandler.tryStartBotCombatEscape(player);
                } else if (player.currentBotTask != null) {
                    player.botCombatState = "escape";
                }
            }
            if (player.getQueuedCombatSpell() == this.spell) {
                player.setQueuedCombatSpell(null);
                return;
            }
            if (player.getAutocastSpell() == this.spell) {
                player.setAutocastSpell(null);
            }
        }
    }
}

