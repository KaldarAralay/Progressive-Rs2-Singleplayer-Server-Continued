/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.combat;

import com.rs2.ServerSettings;
import com.rs2.model.Entity;
import com.rs2.model.EntityUpdateState;
import com.rs2.model.Position;
import com.rs2.model.animation.GraphicEffect;
import com.rs2.model.combat.AmmunitionDefinition;
import com.rs2.model.combat.AttackStyleDefinition;
import com.rs2.model.combat.AttackValidationResult;
import com.rs2.model.combat.AttackXpMode;
import com.rs2.model.combat.CombatCycleEvent;
import com.rs2.model.combat.CombatManager;
import com.rs2.model.combat.CombatType;
import com.rs2.model.combat.effect.CombatEffect;
import com.rs2.model.combat.effect.StatDrainEffect;
import com.rs2.model.combat.effect.WallBeastStunEffect;
import com.rs2.model.combat.hit.DamageContribution;
import com.rs2.model.combat.hit.HitDefinition;
import com.rs2.model.combat.hit.HitType;
import com.rs2.model.combat.special.SpecialAttackDefinition;
import com.rs2.model.ground.GroundItem;
import com.rs2.model.ground.GroundItemManager;
import com.rs2.model.item.DegradableEquipmentHandler;
import com.rs2.model.item.ItemStack;
import com.rs2.model.npc.Npc;
import com.rs2.model.player.Player;
import com.rs2.model.skill.SkillActionHelper;
import com.rs2.model.skill.magic.SpellDefinition;
import com.rs2.model.skill.magic.TeleportManager;
import com.rs2.model.skill.prayer.PrayerManager;
import com.rs2.model.skill.thieving.PickpocketDefinition;
import com.rs2.model.skill.thieving.PickpocketTask;
import com.rs2.model.skill.woodcutting.WoodcuttingHandler;
import com.rs2.model.task.CycleEventHandler;
import com.rs2.util.GameUtil;
import com.rs2.util.GameplayTrace;

public class CombatAction {
    private HitDefinition hitDefinition;
    private int delay;
    private int damage;
    private Entity attacker;
    private Entity target;
    private boolean hitSuccessful;
    private Entity projectileSource;

    public static boolean handlePickpocketAttempt(Player player, Npc npc) {
        if (player == null || player.isStunned() || !player.getSkillManager().tryStartActionDelay(2200)) {
            return true;
        }
        String npcName = npc.getDefinition().getName().toLowerCase();
        PickpocketDefinition definition = null;
        PickpocketDefinition[] definitions = PickpocketDefinition.class.getEnumConstants();
        if (definitions != null) {
            for (PickpocketDefinition candidate : definitions) {
                for (String name : candidate.getNpcNames()) {
                    if (npcName.equalsIgnoreCase(name)) {
                        definition = candidate;
                        break;
                    }
                }
                if (definition != null) {
                    break;
                }
            }
        }
        if (definition == null) {
            return false;
        }
        if (!ServerSettings.thievingEnabled) {
            player.packetSender.sendGameMessage("This skill is currently disabled.");
            return true;
        }
        if (!player.isMember()) {
            player.packetSender.sendGameMessage("You need a members account to access members content.");
            return true;
        }
        if (ServerSettings.freeToPlayWorld) {
            player.packetSender.sendGameMessage("You need to be in members world to access members content.");
            return true;
        }
        if (!SkillActionHelper.checkSkillRequirement(player, 17, definition.getRequiredLevel(), "pickpocket this npc")) {
            return true;
        }
        int lowChance = definition.getSuccessChanceLow();
        int highChance = definition.getSuccessChanceHigh();
        boolean successful = GameUtil.rollLevelScaledChance(lowChance, highChance, player.getSkillManager().getCurrentLevels()[17]);
        ItemStack reward = definition.getRareRewards() != null && GameUtil.randomInclusive(30) == 0 ? definition.getRareRewards()[GameUtil.randomExclusive(definition.getRareRewards().length)] : definition.getCommonRewards()[GameUtil.randomExclusive(definition.getCommonRewards().length)];
        reward = new ItemStack(reward.getId(), reward.getAmount());
        int damage = GameUtil.randomBetweenInclusive(definition.getMinDamage(), definition.getMaxDamage());
        player.setActionLocked(true);
        player.getUpdateState().setAnimation(881);
        player.packetSender.sendGameMessage("You attempt to pick the " + npcName + "'s pocket.");
        CycleEventHandler.getInstance().schedule(player, new PickpocketTask(successful, player, npc, reward, definition, npcName, damage), 2);
        return true;
    }

    public CombatAction(Entity entity, Entity entity2, HitDefinition hitDefinition) {
        this.attacker = entity;
        this.target = entity2;
        this.hitDefinition = hitDefinition;
        this.damage = hitDefinition.getMaxDamage();
    }

    public CombatAction(Entity entity, Entity entity2, HitDefinition hitDefinition, Entity entity3) {
        this.attacker = entity;
        this.target = entity2;
        this.hitDefinition = hitDefinition;
        this.damage = hitDefinition.getMaxDamage();
        this.projectileSource = entity3;
    }

    public void tickDelay() {
        --this.delay;
    }

    public void queue() {
        this.queue(true);
    }

    public void queue(boolean n) {
        Entity entity;
        Entity entity2;
        if (this.hitDefinition.isRandomDamageEnabled()) {
            if (this.attacker.isNpc() && this.hitDefinition.getAttackStyle().getCombatType() == CombatType.MELEE) {
                this.damage = CombatManager.calculateMeleeMaxHit(this.getAttacker(), null);
            }
            this.damage = GameUtil.randomInclusive(this.damage);
        }
        if (this.hitDefinition.getMinimumDamage() != -1 && this.damage < this.hitDefinition.getMinimumDamage()) {
            this.damage = this.hitDefinition.getMinimumDamage();
        }
        if (this.attacker != null && !this.hitDefinition.isAlwaysHit() && this.hitDefinition.isAccuracyCheckEnabled()) {
            double d;
            double d2;
            double d3;
            CombatAction combatAction = this;
            this.hitSuccessful = true;
            if (combatAction.attacker != null && GameUtil.randomInclusive(4) == 0) {
                if (combatAction.attacker.isPlayer() && ((Player)combatAction.attacker).isGuthanSetEffectActive() || combatAction.attacker.isNpc() && ((Npc)combatAction.attacker).getDefinition().getId() == 2027) {
                    combatAction.hitDefinition.setSpecialEffectId(7);
                } else if (combatAction.attacker.isPlayer() && ((Player)combatAction.attacker).isToragSetEffectActive() || combatAction.attacker.isNpc() && ((Npc)combatAction.attacker).getDefinition().getId() == 2029) {
                    combatAction.hitDefinition.setSpecialEffectId(8);
                } else if (combatAction.attacker.isPlayer() && ((Player)combatAction.attacker).isAhrimSetEffectActive() || combatAction.attacker.isNpc() && ((Npc)combatAction.attacker).getDefinition().getId() == 2025) {
                    combatAction.hitDefinition.setSpecialEffectId(9);
                } else if (combatAction.attacker.isPlayer() && ((Player)combatAction.attacker).isKarilSetEffectActive() || combatAction.attacker.isNpc() && ((Npc)combatAction.attacker).getDefinition().getId() == 2028) {
                    combatAction.hitDefinition.setSpecialEffectId(10);
                } else if (combatAction.attacker.isPlayer() && ((Player)combatAction.attacker).isVeracSetEffectActive() || combatAction.attacker.isNpc() && ((Npc)combatAction.attacker).getDefinition().getId() == 2030) {
                    combatAction.hitDefinition.setSpecialEffectId(11);
                }
            }
            if (combatAction.attacker != null && (combatAction.attacker.isPlayer() && ((Player)combatAction.attacker).isDharokSetEffectActive() || combatAction.attacker.isNpc() && ((Npc)combatAction.attacker).getDefinition().getId() == 2026)) {
                d3 = combatAction.attacker.getMaxHitpoints();
                d2 = combatAction.attacker.getCurrentHitpoints();
                d = 1.0 + (d3 - d2) / 100.0 * (d3 / 100.0);
                double d4 = combatAction.damage;
                combatAction.damage = (int)(d4 *= d);
            }
            if (combatAction.getAttacker().isNpc() && combatAction.getTarget().isPlayer() && !((Player)combatAction.getTarget()).getSlayerManager().canAttackSlayerMonster((Npc)combatAction.getAttacker())) {
                String string = ((Npc)combatAction.getAttacker()).getDefinition().getName().toLowerCase();
                if (string.equalsIgnoreCase("banshee")) {
                    combatAction.damage = 8;
                    combatAction.hitDefinition.addEffects(new CombatEffect[]{new StatDrainEffect(0, 1), new StatDrainEffect(2, 1), new StatDrainEffect(1, 1), new StatDrainEffect(4, 1), new StatDrainEffect(6, 1)});
                } else if (string.equalsIgnoreCase("cockatrice")) {
                    combatAction.damage = 11;
                    combatAction.hitDefinition.addEffects(new CombatEffect[]{new StatDrainEffect(0, 3), new StatDrainEffect(2, 3), new StatDrainEffect(1, 3), new StatDrainEffect(4, 3), new StatDrainEffect(6, 3), new StatDrainEffect(16, 3)});
                } else if (string.equalsIgnoreCase("basilisk")) {
                    combatAction.damage = 12;
                    combatAction.hitDefinition.addEffects(new CombatEffect[]{new StatDrainEffect(0, 3), new StatDrainEffect(2, 3), new StatDrainEffect(1, 3), new StatDrainEffect(4, 3), new StatDrainEffect(6, 3)});
                } else if (string.equalsIgnoreCase("wall beast")) {
                    combatAction.damage = 18;
                    combatAction.hitDefinition.addEffect(new WallBeastStunEffect(5)).setBlockAnimationId(734);
                } else if (string.equalsIgnoreCase("aberrant specter")) {
                    combatAction.damage = 14;
                    combatAction.hitDefinition.addEffects(new CombatEffect[]{new StatDrainEffect(0, 5), new StatDrainEffect(2, 5), new StatDrainEffect(1, 5), new StatDrainEffect(4, 5), new StatDrainEffect(6, 5)});
                } else if (string.equalsIgnoreCase("dust devil")) {
                    combatAction.damage = 14;
                }
            }
            if (combatAction.getTarget().isPlayer() && ((Player)combatAction.getTarget()).getQuestState(0) != 1 && ((Player)combatAction.getTarget()).getSkillManager().getCurrentLevels()[3] == 1) {
                combatAction.hitSuccessful = false;
            }
            if (combatAction.getAttacker().isPlayer() && combatAction.getTarget().isNpc() && !((Player)combatAction.getAttacker()).getSlayerManager().canAttackSlayerMonster((Npc)combatAction.getTarget())) {
                combatAction.hitSuccessful = false;
            }
            if (combatAction.attacker.isPlayer() && ((Player)combatAction.attacker).getQuestState(0) == 65) {
                ((Player)combatAction.attacker).ea();
                combatAction.hitSuccessful = false;
            }
            if (combatAction.hitSuccessful) {
                d3 = CombatManager.calculateDefenceRoll(combatAction.target, combatAction.hitDefinition);
                d2 = CombatManager.calculateAttackRoll(combatAction.attacker, combatAction.hitDefinition);
                if (!ServerSettings.modernCombatSystemEnabled) {
                    Player player;
                    if (combatAction.hitDefinition.getAttackStyle().getCombatType() == CombatType.MELEE && combatAction.attacker.isPlayer()) {
                        Player player2 = (Player)combatAction.attacker;
                        if (player2.hasFullVoidMeleeSet()) {
                            combatAction.damage = (int)((double)combatAction.damage * 1.1);
                        }
                        if (player2.getEquipmentManager().getItemIdAtSlot(2) == 11128 && (player2.getEquipmentManager().getItemIdAtSlot(3) == 6523 || player2.getEquipmentManager().getItemIdAtSlot(3) == 6528 || player2.getEquipmentManager().getItemIdAtSlot(3) == 6527 || player2.getEquipmentManager().getItemIdAtSlot(3) == 6525)) {
                            combatAction.damage = (int)((double)combatAction.damage * 1.2);
                        }
                    }
                    if (combatAction.hitDefinition.getAttackStyle().getCombatType() == CombatType.RANGED && combatAction.attacker.isPlayer() && (player = (Player)combatAction.attacker).hasFullVoidRangedSet()) {
                        combatAction.damage = (int)((double)combatAction.damage * 1.2);
                    }
                }
                d = CombatManager.calculateHitChance(d2, d3);
                boolean bl = CombatManager.rollAccuracy(d);
                if (combatAction.getAttacker().isPlayer()) {
                    entity2 = (Player)combatAction.getAttacker();
                }
                if (combatAction.getTarget().isPlayer()) {
                    entity2 = (Player)combatAction.getTarget();
                }
                combatAction.hitSuccessful = bl;
            }
            if ((combatAction.hitDefinition.getAttackStyle().getXpMode() == AttackXpMode.ICY_BREATH || combatAction.hitDefinition.getAttackStyle().getXpMode() == AttackXpMode.DRAGONFIRE || combatAction.hitDefinition.getAttackStyle().getXpMode() == AttackXpMode.DRAGONFIRE_FAR || combatAction.hitDefinition.getAttackStyle().getXpMode() == AttackXpMode.KBD_SPECIAL) && combatAction.target.isPlayer()) {
                int n2 = -1;
                if (combatAction.hitDefinition.getAttackStyle().getXpMode() == AttackXpMode.DRAGONFIRE || combatAction.hitDefinition.getAttackStyle().getXpMode() == AttackXpMode.DRAGONFIRE_FAR || combatAction.hitDefinition.getAttackStyle().getXpMode() == AttackXpMode.KBD_SPECIAL) {
                    int n3;
                    HitDefinition hitDefinition = combatAction.hitDefinition;
                    boolean bl = combatAction.hitSuccessful;
                    entity2 = combatAction.target;
                    entity = combatAction.attacker;
                    int n4 = 0;
                    int n5 = 0;
                    if (entity == null || entity2 == null) {
                        n3 = -1;
                    } else if (!entity.isNpc() || !entity2.isPlayer()) {
                        n3 = -1;
                    } else {
                        int n6;
                        entity2 = (Player)entity2;
                        Entity entity3 = entity = (Npc)entity;
                        if (((Npc)entity).getNpcId() == 53 || ((Npc)entity3).getNpcId() == 54 || ((Npc)entity3).getNpcId() == 55 || ((Npc)entity3).getNpcId() == 941 || ((Npc)entity3).getNpcId() == 3885 || ((Npc)entity3).getNpcId() == 5362) {
                            switch (((Player)entity2).getDragonfireProtectionState()) {
                                case 0: {
                                    if (bl) {
                                        n4 = 30;
                                        break;
                                    }
                                    n4 = 50;
                                    break;
                                }
                                case 1: {
                                    n4 = bl ? 30 : 50;
                                    n5 = 15;
                                    break;
                                }
                                case 2: {
                                    n4 = 10;
                                    break;
                                }
                                case 3: {
                                    n4 = 5;
                                    break;
                                }
                                case 4: {
                                    n4 = 10;
                                    n5 = 15;
                                    break;
                                }
                                case 5: {
                                    n4 = 5;
                                    n5 = 15;
                                    break;
                                }
                                case 6: {
                                    n4 = 5;
                                    break;
                                }
                                case 7: {
                                    n4 = 5;
                                    n5 = 15;
                                }
                            }
                        } else {
                            entity3 = entity;
                            if (((Npc)entity3).getNpcId() == 1590 || ((Npc)entity3).getNpcId() == 1591 || ((Npc)entity3).getNpcId() == 1592 || ((Npc)entity3).getNpcId() == 5363) {
                                switch (((Player)entity2).getDragonfireProtectionState()) {
                                    case 0: {
                                        if (bl) {
                                            n4 = 30;
                                            break;
                                        }
                                        n4 = 50;
                                        break;
                                    }
                                    case 1: {
                                        n4 = bl ? 30 : 50;
                                        n5 = 15;
                                        break;
                                    }
                                    case 2: {
                                        if (bl) {
                                            n4 = 30;
                                            break;
                                        }
                                        n4 = 50;
                                        break;
                                    }
                                    case 3: {
                                        n4 = 5;
                                        break;
                                    }
                                    case 4: {
                                        n4 = bl ? 30 : 50;
                                        n5 = 15;
                                        break;
                                    }
                                    case 5: {
                                        n4 = 5;
                                        n5 = 15;
                                        break;
                                    }
                                    case 6: {
                                        n4 = 5;
                                        break;
                                    }
                                    case 7: {
                                        n4 = 5;
                                        n5 = 15;
                                    }
                                }
                            } else if (((Npc)entity).getNpcId() == 50) {
                                switch (((Player)entity2).getDragonfireProtectionState()) {
                                    case 0: {
                                        if (hitDefinition.getAttackStyle().getXpMode() == AttackXpMode.KBD_SPECIAL) {
                                            n4 = 50;
                                            break;
                                        }
                                        n4 = 65;
                                        break;
                                    }
                                    case 1: {
                                        if (hitDefinition.getAttackStyle().getXpMode() == AttackXpMode.KBD_SPECIAL) {
                                            n4 = 50;
                                            break;
                                        }
                                        n4 = 65;
                                        n5 = 15;
                                        break;
                                    }
                                    case 2: {
                                        if (hitDefinition.getAttackStyle().getXpMode() == AttackXpMode.KBD_SPECIAL) {
                                            n4 = 15;
                                            break;
                                        }
                                        n4 = 20;
                                        break;
                                    }
                                    case 3: {
                                        if (hitDefinition.getAttackStyle().getXpMode() == AttackXpMode.KBD_SPECIAL) {
                                            n4 = 10;
                                            break;
                                        }
                                        n4 = 15;
                                        break;
                                    }
                                    case 4: {
                                        if (hitDefinition.getAttackStyle().getXpMode() == AttackXpMode.KBD_SPECIAL) {
                                            n4 = 15;
                                            break;
                                        }
                                        n4 = 20;
                                        n5 = 15;
                                        break;
                                    }
                                    case 5: {
                                        if (hitDefinition.getAttackStyle().getXpMode() == AttackXpMode.KBD_SPECIAL) {
                                            n4 = 10;
                                            break;
                                        }
                                        n4 = 15;
                                        n5 = 15;
                                        break;
                                    }
                                    case 6: {
                                        if (hitDefinition.getAttackStyle().getXpMode() == AttackXpMode.KBD_SPECIAL) {
                                            n4 = 10;
                                            break;
                                        }
                                        n4 = 15;
                                        break;
                                    }
                                    case 7: {
                                        if (hitDefinition.getAttackStyle().getXpMode() == AttackXpMode.KBD_SPECIAL) {
                                            n4 = 10;
                                            break;
                                        }
                                        n4 = 15;
                                        n5 = 15;
                                    }
                                }
                            } else if (((Npc)entity).getNpcId() == 742) {
                                switch (((Player)entity2).getDragonfireProtectionState()) {
                                    case 0: {
                                        n4 = 70;
                                        break;
                                    }
                                    case 1: {
                                        n4 = 70;
                                        n5 = 15;
                                        break;
                                    }
                                    case 2: {
                                        n4 = 55;
                                        break;
                                    }
                                    case 3: {
                                        n4 = 10;
                                        break;
                                    }
                                    case 4: {
                                        n4 = 55;
                                        n5 = 15;
                                        break;
                                    }
                                    case 5: {
                                        n4 = 10;
                                        n5 = 3;
                                        break;
                                    }
                                    case 6: {
                                        n4 = 7;
                                        break;
                                    }
                                    case 7: {
                                        n4 = 7;
                                        n5 = 3;
                                    }
                                }
                            }
                        }
                        if ((n6 = GameUtil.randomInt(n4 + 1) - n5) < 0) {
                            n6 = 0;
                        }
                        n3 = n6;
                    }
                    n2 = n3;
                } else {
                    Player player = (Player)combatAction.target;
                    if (player.getActivePrayers()[12]) {
                        n2 = GameUtil.randomInt(21);
                    }
                }
                if (n2 != -1) {
                    combatAction.damage = n2;
                }
            } else {
                Entity cfr_ignored_0 = combatAction.attacker;
                if (combatAction.target.isProtectedFrom(combatAction.hitDefinition.getAttackStyle().getCombatType()) && combatAction.hitDefinition.getSpecialEffectId() != 11 && !combatAction.hitDefinition.isProtectionPrayerReductionDeferred() && combatAction.damage > 0) {
                    combatAction.damage = combatAction.attacker != null && combatAction.attacker.isPlayer() ? (int)Math.ceil((double)combatAction.damage * 0.6) : 0;
                }
            }
            if (ServerSettings.modernCombatSystemEnabled && combatAction.hitDefinition.getSpecialEffectId() == 11) {
                combatAction.hitSuccessful = true;
                ++combatAction.damage;
            }
            if (!combatAction.hitSuccessful && !combatAction.hitDefinition.isAlwaysHit()) {
                if (combatAction.hitDefinition.getAttackStyle() != null && combatAction.hitDefinition.getAttackStyle().getCombatType() == CombatType.MAGIC && combatAction.hitDefinition.getAttackStyle().getXpMode() != AttackXpMode.ICY_BREATH && combatAction.hitDefinition.getAttackStyle().getXpMode() != AttackXpMode.DRAGONFIRE && combatAction.hitDefinition.getAttackStyle().getXpMode() != AttackXpMode.DRAGONFIRE_FAR && combatAction.hitDefinition.getAttackStyle().getXpMode() != AttackXpMode.KBD_SPECIAL) {
                    combatAction.hitDefinition.setGraphic(new GraphicEffect(85, 100));
                }
                combatAction.hitDefinition.clearEffects();
            }
        }
        if (this.hitDefinition.getProjectile() != null) {
            if (this.projectileSource != null) {
                new WoodcuttingHandler(this.projectileSource, this.target, this.hitDefinition.getProjectile()).sendProjectileToNearbyPlayers();
            } else {
                new WoodcuttingHandler(this.attacker, this.target, this.hitDefinition.getProjectile()).sendProjectileToNearbyPlayers();
            }
        }
        this.delay = this.hitDefinition.calculateDelay(this.attacker != null ? this.attacker.getPosition() : null, this.target.getPosition());
        if (GameplayTrace.enabled()) {
            String style = this.hitDefinition.getAttackStyle() == null ? "null" : this.hitDefinition.getAttackStyle().getXpMode() + "/" + this.hitDefinition.getAttackStyle().getCombatType();
            GameplayTrace.log("combat action queued attacker=" + GameplayTrace.describe(this.attacker) + " target=" + GameplayTrace.describe(this.target) + " style=" + style + " maxDamage=" + this.hitDefinition.getMaxDamage() + " damage=" + this.damage + " hitSuccessful=" + this.hitSuccessful + " random=" + this.hitDefinition.isRandomDamageEnabled() + " accuracy=" + this.hitDefinition.isAccuracyCheckEnabled() + " always=" + this.hitDefinition.isAlwaysHit() + " delay=" + this.delay + " canTakeDamage=" + this.target.getAttributes().get("canTakeDamage"));
        }
        CombatManager.getInstance().queueAction(this);
        if (this.target != null && this.attacker != null) {
            int questDamageOverride = this.attacker.isPlayer() ? ((Player)this.attacker).getQuestManager().getQuestDamageOverride(this.attacker, this.target) : ((Player)this.target).getQuestManager().getQuestDamageOverride(this.attacker, this.target);
            if (this.attacker.isInMageArena() && this.hitDefinition.getAttackStyle().getCombatType() != CombatType.MAGIC) {
                questDamageOverride = 0;
            }
            if (questDamageOverride != -1) {
                this.damage = questDamageOverride;
            }
        }
        if (this.hitDefinition.getSpell() != null && (this.hitDefinition.getSpell() == SpellDefinition.FLAMES_OF_ZAMORAK || this.hitDefinition.getSpell() == SpellDefinition.SARADOMIN_STRIKE || this.hitDefinition.getSpell() == SpellDefinition.CLAWS_OF_GUTHIX)) {
            int godSpellSoundId = 0;
            if (this.hitDefinition.getSpell() == SpellDefinition.FLAMES_OF_ZAMORAK) {
                godSpellSoundId = this.hitSuccessful ? 290 : 293;
            }
            if (this.hitDefinition.getSpell() == SpellDefinition.SARADOMIN_STRIKE) {
                godSpellSoundId = this.hitSuccessful ? 297 : 299;
            }
            if (this.hitDefinition.getSpell() == SpellDefinition.CLAWS_OF_GUTHIX) {
                godSpellSoundId = this.hitSuccessful ? 291 : 296;
            }
            if (this.attacker.isPlayer()) {
                entity = (Player)this.getAttacker();
                if (this.hitDefinition.getSpell() == SpellDefinition.SARADOMIN_STRIKE && ((Player)entity).mageArenaSaradominStrikeCastsRemaining > 0) {
                    --((Player)entity).mageArenaSaradominStrikeCastsRemaining;
                    if (((Player)entity).mageArenaSaradominStrikeCastsRemaining == 0) {
                        entity2 = entity;
                        ((Player)entity2).packetSender.sendGameMessage("You can now cast Saradomin Strike outside the Arena.");
                    }
                }
                if (this.hitDefinition.getSpell() == SpellDefinition.FLAMES_OF_ZAMORAK && ((Player)entity).mageArenaFlamesOfZamorakCastsRemaining > 0) {
                    --((Player)entity).mageArenaFlamesOfZamorakCastsRemaining;
                    if (((Player)entity).mageArenaFlamesOfZamorakCastsRemaining == 0) {
                        entity2 = entity;
                        ((Player)entity2).packetSender.sendGameMessage("You can now cast Flames of Zamorak outside the Arena.");
                    }
                }
                if (this.hitDefinition.getSpell() == SpellDefinition.CLAWS_OF_GUTHIX && ((Player)entity).mageArenaClawsOfGuthixCastsRemaining > 0) {
                    --((Player)entity).mageArenaClawsOfGuthixCastsRemaining;
                    if (((Player)entity).mageArenaClawsOfGuthixCastsRemaining == 0) {
                        entity2 = entity;
                        ((Player)entity2).packetSender.sendGameMessage("You can now cast Claws of Guthix outside the Arena.");
                    }
                }
                entity2 = entity;
                ((Player)entity2).packetSender.sendSoundEffect(godSpellSoundId, 1, 0);
            }
            if (this.target.isPlayer()) {
                entity2 = entity = (Player)this.getTarget();
                ((Player)entity).packetSender.sendSoundEffect(godSpellSoundId, 1, 0);
            }
        }
        if (this.hitSuccessful && this.target.isPlayer()) {
            int n10 = this.damage;
            Player player = (Player)this.target;
            Entity entity4 = this.attacker;
            ItemStack itemStack = player.getEquipmentManager().getContainer().getItemAt(12);
            if (itemStack != null && itemStack.getId() == 2550 && player != null && n10 > 0) {
                int n11 = (int)Math.ceil((double)n10 * 0.1);
                entity4.applyDirectHit(n11, HitType.NORMAL);
                if (entity4 != null) {
                    DamageContribution damageContribution = entity4.getDamageContribution(player);
                    if (damageContribution == null) {
                        damageContribution = new DamageContribution(player);
                    } else {
                        entity4.getDamageContributions().remove(damageContribution);
                    }
                    damageContribution.addDamage(n10);
                    entity4.getDamageContributions().add(damageContribution);
                }
                player.setRingOfRecoilLife(player.getRingOfRecoilLife() - n11);
                if (player.getRingOfRecoilLife() <= 0) {
                    entity2 = player;
                    ((Player)entity2).packetSender.sendGameMessage("Your ring shatters!");
                    player.getEquipmentManager().consumeSlotItemAmount(12, 1);
                    player.setRingOfRecoilLife(40);
                }
            }
        }
        if (this.attacker != null && this.attacker.isPlayer()) {
            DegradableEquipmentHandler.degradeEquipmentAfterCombat((Player)this.attacker);
        }
    }

    public void applyHitUpdate() {
        Object object;
        if (GameplayTrace.enabled()) {
            GameplayTrace.log("combat hit-update attacker=" + GameplayTrace.describe(this.attacker) + " target=" + GameplayTrace.describe(this.target) + " damage=" + this.damage + " canTakeDamage=" + this.target.getAttributes().get("canTakeDamage"));
        }
        if (!this.canTargetTakeDamage() || this.damage < 0) {
            if (GameplayTrace.enabled()) {
                GameplayTrace.log("combat hit-update blocked attacker=" + GameplayTrace.describe(this.attacker) + " target=" + GameplayTrace.describe(this.target) + " damage=" + this.damage);
            }
            return;
        }
        Object object2 = this.target.getUpdateState();
        Object object3 = object = this.damage == 0 ? HitType.BLOCKED : this.hitDefinition.getHitType();
        if (!((EntityUpdateState)object2).isPrimaryHitDamageOverridden()) {
            Object object4;
            ((EntityUpdateState)object2).setPrimaryHitUpdateRequired(true);
            ((EntityUpdateState)object2).setPrimaryHitDamage(this.damage);
            if (this.attacker != null && this.hitDefinition.getAttackStyle() != null && this.hitDefinition.getAttackStyle().getCombatType() != CombatType.MELEE && this.hitDefinition.getAttackStyle().getCombatType() != CombatType.RANGED) {
                this.hitDefinition.getAttackStyle().getCombatType();
            }
            ((EntityUpdateState)object2).setPrimaryHitType(((HitType)((Object)object)).getClientId());
            if (this.target.isPlayer()) {
                object2 = (Player)this.getTarget();
                if (this.damage > 0) {
                    object4 = object2;
                    ((Player)object4).packetSender.sendSoundEffect(69, 1, 0);
                } else {
                    object4 = object2;
                    ((Player)object4).packetSender.sendSoundEffect(((Player)object2).getBlockSoundId(), 1, 0);
                }
                if (this.hitDefinition.getAttackStyle() != null && this.hitDefinition.getAttackStyle().getCombatType() == CombatType.MAGIC) {
                    object4 = object2;
                    ((Player)object4).packetSender.sendSoundEffect(this.hitDefinition.getImpactSoundId(), 1, 0);
                }
                if (this.attacker != null && this.attacker.isPlayer()) {
                    object4 = object = (Player)this.getAttacker();
                    ((Player)object).packetSender.sendSoundEffect(((Player)object2).getBlockSoundId(), 1, 0);
                    if (this.hitDefinition.getAttackStyle() != null && this.hitDefinition.getAttackStyle().getCombatType() == CombatType.MAGIC) {
                        object4 = object;
                        ((Player)object4).packetSender.sendSoundEffect(this.hitDefinition.getImpactSoundId(), 1, 0);
                    }
                }
            }
            if (this.target != null && this.attacker != null && this.target.isNpc() && this.attacker.isPlayer()) {
                object2 = (Player)this.getAttacker();
                object = (Npc)this.getTarget();
                object4 = object2;
                ((Player)object4).packetSender.sendSoundEffect(((Npc)object).getHitSoundId(), 1, 0);
                if (this.hitDefinition.getAttackStyle() != null && this.hitDefinition.getAttackStyle().getCombatType() == CombatType.MAGIC) {
                    object4 = object2;
                    ((Player)object4).packetSender.sendSoundEffect(this.hitDefinition.getImpactSoundId(), 1, 0);
                    if (((Npc)object).getNpcId() == 667 && this.hitDefinition.getSpell() != null && this.damage > 0) {
                        if (this.hitDefinition.getSpell() == SpellDefinition.WIND_BLAST) {
                            ((Npc)object).c = true;
                            object4 = object2;
                            ((Player)object4).packetSender.sendGameMessage("Chronozon weakens...");
                        }
                        if (this.hitDefinition.getSpell() == SpellDefinition.WATER_BLAST) {
                            ((Npc)object).d = true;
                            object4 = object2;
                            ((Player)object4).packetSender.sendGameMessage("Chronozon weakens...");
                        }
                        if (this.hitDefinition.getSpell() == SpellDefinition.EARTH_BLAST) {
                            ((Npc)object).e = true;
                            object4 = object2;
                            ((Player)object4).packetSender.sendGameMessage("Chronozon weakens...");
                        }
                        if (this.hitDefinition.getSpell() == SpellDefinition.FIRE_BLAST) {
                            ((Npc)object).f = true;
                            object4 = object2;
                            ((Player)object4).packetSender.sendGameMessage("Chronozon weakens...");
                        }
                    }
                    if (this.damage > 0 && ((Player)object2).getQuestState(0) == 66) {
                        ((Player)object2).ea();
                        return;
                    }
                }
            }
        } else {
            if (!((EntityUpdateState)object2).isSecondaryHitDamageOverridden()) {
                ((EntityUpdateState)object2).setSecondaryHitUpdateRequired(true);
                ((EntityUpdateState)object2).setSecondaryHitDamage(this.damage);
                if (this.attacker != null && this.hitDefinition.getAttackStyle() != null && this.hitDefinition.getAttackStyle().getCombatType() != CombatType.MELEE && this.hitDefinition.getAttackStyle().getCombatType() != CombatType.RANGED) {
                    this.hitDefinition.getAttackStyle().getCombatType();
                }
                ((EntityUpdateState)object2).setSecondaryHitType(((HitType)((Object)object)).getClientId());
                return;
            }
            ((EntityUpdateState)object2).queueHit(this.damage, ((HitType)((Object)object)).getClientId());
        }
    }

    public void applyHit() {
        Object object;
        Object object2;
        Object object32;
        if (GameplayTrace.enabled()) {
            String style = this.hitDefinition.getAttackStyle() == null ? "null" : this.hitDefinition.getAttackStyle().getXpMode() + "/" + this.hitDefinition.getAttackStyle().getCombatType();
            GameplayTrace.log("combat apply-hit start attacker=" + GameplayTrace.describe(this.attacker) + " target=" + GameplayTrace.describe(this.target) + " style=" + style + " damage=" + this.damage + " hitSuccessful=" + this.hitSuccessful + " always=" + this.hitDefinition.isAlwaysHit() + " canTakeDamage=" + this.target.getAttributes().get("canTakeDamage"));
        }
        if (this.hitDefinition.getDroppedAmmunition() != null && this.attacker != null && this.attacker.isPlayer() && this.hitDefinition.getChainedTargets().isEmpty() && ((Player)(object32 = (Player)this.attacker)).isAmmunitionDropsEnabled() && GameUtil.randomInt(5) > 0) {
            object2 = new GroundItem(new ItemStack(this.hitDefinition.getDroppedAmmunition().getId(), this.hitDefinition.getDroppedAmmunition().getAmount()), (Entity)object32, this.target.getPosition().copy());
            GroundItemManager.getInstance().spawn((GroundItem)object2);
        }
        if (!this.canTargetTakeDamage()) {
            if (GameplayTrace.enabled()) {
                GameplayTrace.log("combat apply-hit blocked canTakeDamage=false attacker=" + GameplayTrace.describe(this.attacker) + " target=" + GameplayTrace.describe(this.target) + " damage=" + this.damage);
            }
            return;
        }
        if (!this.hitDefinition.getChainedTargets().isEmpty()) {
            object32 = (Entity)this.hitDefinition.getChainedTargets().get(0);
            object2 = CombatCycleEvent.validateAttack(this.getAttacker(), (Entity)object32);
            boolean bl = false;
            if (((Entity)object32).isNpc()) {
                object = (Npc)object32;
                bl = ((Entity)object).isDoorSupportNpc();
            }
            object = new CombatAction(this.hitDefinition.getChainedSource(), (Entity)object32, this.hitDefinition, this.getTarget());
            this.hitDefinition.getChainedTargets().remove(0);
            this.hitDefinition.setChainedSource(this.getAttacker());
            this.hitDefinition.setChainedTargets(this.hitDefinition.getChainedTargets());
            if (object2 == AttackValidationResult.VALID || bl) {
                ((CombatAction)object).queue();
            }
        }
        if (this.attacker != null && !this.hitDefinition.isAlwaysHit() && this.hitDefinition.getHitType() != HitType.POISON && this.hitDefinition.getHitType() != HitType.DISEASE) {
            if (this.target.isNpc() && !this.target.isAutoRetaliateDisabled()) {
                object32 = (Npc)this.target;
                if (((Npc)object32).getNpcId() != 1472) {
                    CombatManager.startCombat(this.target, this.attacker);
                }
            } else if (this.target.isPlayer() && !this.target.isMoving()) {
                object32 = (Player)this.target;
                boolean bl = false;
                if (((Entity)object32).getCombatTarget() != null && !((Entity)object32).getCombatTarget().isDead()) {
                    bl = true;
                }
                if (((Player)object32).isAutoRetaliate() && !bl) {
                    CombatManager.startCombat(this.target, this.attacker);
                }
            }
        }
        if (this.hitDefinition.getSpell() != null && this.hitDefinition.getSpell() == SpellDefinition.MELZAR_CABBAGE_SPELL) {
            int n = 2927 + GameUtil.randomInt(3);
            int n2 = 9648 + GameUtil.randomInt(3);
            GroundItemManager.getInstance().spawn(new GroundItem(new ItemStack(1965, 1), this.attacker, this.target, new Position(n, n2, 0)));
            if (this.target.isPlayer()) {
                Player player = (Player)this.target;
                object = player;
                player.packetSender.sendStillGraphicToNearbyPlayers(86, n, n2, 0, 0);
            }
            return;
        }
        if (this.hitDefinition.isBlockAnimationEnabled() && !this.target.getUpdateState().isAnimationUpdateRequired()) {
            if (this.hitDefinition.getBlockAnimationId() > 0) {
                this.target.getUpdateState().setAnimation(this.hitDefinition.getBlockAnimationId());
            } else if (this.target.isPlayer()) {
                object32 = (Player)this.target;
                if (((Player)object32).npcTransformationId > 1) {
                    this.target.getUpdateState().setAnimation(new Npc(((Player)object32).npcTransformationId).getDefinition().getBlockAnimationId());
                } else {
                    this.target.getUpdateState().setAnimation(this.target.getBlockAnimationId());
                }
            } else {
                this.target.getUpdateState().setAnimation(this.target.getBlockAnimationId());
            }
        }
        if (this.target.isPlayer()) {
            object = object32 = (Player)this.target;
            ((Player)object32).packetSender.closeInterfaces();
        }
        if (this.hitDefinition.getGraphic() != null) {
            this.target.getUpdateState().setGraphic(this.hitDefinition.getGraphic());
        }
        if (this.attacker != null && this.attacker.isPlayer() && this.target.isNpc()) {
            object32 = (Player)this.attacker;
            Npc npc = (Npc)this.target;
            if (npc.getNpcId() >= 1024 && npc.getNpcId() <= 1029 && (this.hitDefinition.getAttackStyle().getCombatType() != CombatType.MELEE || ((Player)object32).getEquipmentManager().getItemIdAtSlot(3) != 2952)) {
                npc.getUpdateState().setGraphic(86, 25);
                npc.transformToNpcIdWithAnimation(npc.getNpcId() + 6, 100000, 38);
                object = object32;
                ((Player)object).packetSender.sendSoundEffect(267, 1, 0);
            }
        }
        if (this.hitDefinition.getAmmunition() != null && this.hitDefinition.getAmmunition() == AmmunitionDefinition.DRAGON_BOLTS_E) {
            this.hitDefinition.setSpecialEffectId(14);
        }
        if (this.hitDefinition.getSpell() != null && (this.hitDefinition.getSpell() == SpellDefinition.CHAOS_ELEMENTAL_RANDOM_TELEPORT || this.hitDefinition.getSpell() == SpellDefinition.CHAOS_ELEMENTAL_DISARM)) {
            if (this.hitDefinition.getSpell().getPostHitEffect() != null) {
                this.hitDefinition.addEffect(this.hitDefinition.getSpell().getPostHitEffect());
            }
            if (this.hitDefinition.getEffects() != null && this.hitDefinition.getEffects().size() > 0) {
                for (Object effectObject : this.hitDefinition.getEffects()) {
                    if (!this.target.canApplyCombatEffect((CombatEffect)effectObject)) continue;
                    ((CombatEffect)effectObject).apply(this);
                }
            }
            if (this.hitDefinition.getEffects() != null && this.hitDefinition.getEffects().size() > 0) {
                for (Object effectObject : this.hitDefinition.getEffects()) {
                    if (effectObject == null) continue;
                    ((CombatEffect)effectObject).afterApply(this);
                }
            }
            return;
        }
        int n = 0;
        if (!(this.hitDefinition.getAttackStyle() == null || this.hitDefinition.getAttackStyle().getXpMode() != AttackXpMode.DRAGONFIRE && this.hitDefinition.getAttackStyle().getXpMode() != AttackXpMode.DRAGONFIRE_FAR && this.hitDefinition.getAttackStyle().getXpMode() != AttackXpMode.KBD_SPECIAL || this.hitSuccessful)) {
            this.hitSuccessful = true;
            n = 1;
        }
        if (!this.hitSuccessful && !this.hitDefinition.isAlwaysHit()) {
            if (this.damage > 0) {
                this.damage = 0;
            }
            if (GameplayTrace.enabled()) {
                GameplayTrace.log("combat apply-hit miss attacker=" + GameplayTrace.describe(this.attacker) + " target=" + GameplayTrace.describe(this.target) + " damage=" + this.damage);
            }
            if (this.hitDefinition.getAttackStyle() != null && this.hitDefinition.getAttackStyle().getCombatType() != CombatType.MAGIC) {
                this.applyHitUpdate();
                return;
            }
            if (this.hitDefinition.getAttackStyle().getCombatType() == CombatType.MAGIC) {
                if (this.hitDefinition.getSpell() != null && (this.hitDefinition.getSpell() == SpellDefinition.FLAMES_OF_ZAMORAK || this.hitDefinition.getSpell() == SpellDefinition.SARADOMIN_STRIKE || this.hitDefinition.getSpell() == SpellDefinition.CLAWS_OF_GUTHIX)) {
                    return;
                }
                if (this.attacker.isPlayer()) {
                    Player player = (Player)this.getAttacker();
                    object = player;
                    player.packetSender.sendSoundEffect(940, 1, 0);
                }
                if (this.target.isPlayer()) {
                    Player player = (Player)this.getTarget();
                    object = player;
                    player.packetSender.sendSoundEffect(940, 1, 0);
                }
            }
            return;
        }
        if (this.attacker != null && this.attacker.isPlayer() && this.target.isNpc()) {
            Player player = (Player)this.attacker;
            if (player.getSlayerManager().requiresFinishingItem((Npc)this.target, true) && this.damage >= this.target.getCurrentHitpoints()) {
                this.damage = this.target.getCurrentHitpoints() - 1;
            }
        }
        if (this.hitDefinition.getAttackStyle() != null) {
            if (this.hitDefinition.getAttackStyle().getXpMode() == AttackXpMode.ICY_BREATH && this.target.isPlayer()) {
                Player player;
                Player player2 = player = (Player)this.target;
                if (player.getEquipmentManager().getItemIdAtSlot(5) == 11283 || player2.getEquipmentManager().getItemIdAtSlot(5) == 11284 || player2.getEquipmentManager().getItemIdAtSlot(5) == 2890) {
                    object = player;
                    ((Player)object).packetSender.sendGameMessage("Your shield absorbs most of the wyvern's icy breath!");
                    this.damage = GameUtil.randomInt(11);
                }
            }
            if ((this.hitDefinition.getAttackStyle().getXpMode() == AttackXpMode.DRAGONFIRE || this.hitDefinition.getAttackStyle().getXpMode() == AttackXpMode.DRAGONFIRE_FAR || this.hitDefinition.getAttackStyle().getXpMode() == AttackXpMode.KBD_SPECIAL) && this.target.isNpc()) {
                Npc npc = (Npc)this.target;
                if (npc.getNpcId() == 50 || npc.getNpcId() == 51 || npc.getNpcId() == 52 || npc.getNpcId() == 53 || npc.getNpcId() == 54 || npc.getNpcId() == 55 || npc.getNpcId() == 941 || npc.getNpcId() == 1589 || npc.getNpcId() == 3068 || npc.getNpcId() == 3069 || npc.getNpcId() == 3070 || npc.getNpcId() == 3071) {
                    this.damage = 0;
                }
                if (this.hitDefinition.getEffects() != null) {
                    for (Object effectObject : this.hitDefinition.getEffects()) {
                        CombatEffect combatEffect = (CombatEffect)effectObject;
                        if (!this.target.canApplyCombatEffect(combatEffect)) continue;
                        combatEffect.apply(this);
                    }
                }
            }
            if ((this.hitDefinition.getAttackStyle().getXpMode() == AttackXpMode.DRAGONFIRE || this.hitDefinition.getAttackStyle().getXpMode() == AttackXpMode.DRAGONFIRE_FAR || this.hitDefinition.getAttackStyle().getXpMode() == AttackXpMode.KBD_SPECIAL) && this.target.isPlayer()) {
                Player player = (Player)this.target;
                if (n != 0) {
                    object = player;
                    ((Player)object).packetSender.sendGameMessage("You manage to resist some of the dragon fire!");
                } else {
                    object = player;
                    ((Player)object).packetSender.sendGameMessage("You're horribly burnt by the dragon fire!");
                }
                if (!this.attacker.isPlayer() && player.getEquipmentManager().getContainer().getItemAt(5) != null) {
                    int n3 = player.getEquipmentManager().getContainer().getItemAt(5).getMetadata();
                    if (player.getEquipmentManager().getItemIdAtSlot(5) == 11284) {
                        player.getEquipmentManager().getContainer().setItem(5, new ItemStack(11283));
                        player.getEquipmentManager().getContainer().getItemAt(5).setMetadata(1);
                        object = player;
                        ((Player)object).packetSender.sendGameMessage("Your dragonfire shield is recharging.");
                        player.getUpdateState().setAnimation(6695, 0);
                        player.getUpdateState().setGraphic(1164, 25);
                        player.getEquipmentManager().refresh();
                    } else if (player.getEquipmentManager().getItemIdAtSlot(5) == 11283 && n3 < 50) {
                        player.getEquipmentManager().getContainer().getItemAt(5).setMetadata(n3 + 1);
                        object = player;
                        ((Player)object).packetSender.sendGameMessage("Your dragonfire shield is recharging.");
                        player.getUpdateState().setAnimation(6695, 0);
                        player.getUpdateState().setGraphic(1164, 25);
                        player.getEquipmentManager().refresh();
                    }
                }
                if (this.hitDefinition.getEffects() != null) {
                    for (Object effectObject : this.hitDefinition.getEffects()) {
                        CombatEffect combatEffect = (CombatEffect)effectObject;
                        if (!this.target.canApplyCombatEffect(combatEffect)) continue;
                        combatEffect.apply(this);
                    }
                }
            } else if (this.target.isProtectedFrom(this.hitDefinition.getAttackStyle().getCombatType()) && this.hitDefinition.getSpecialEffectId() != 11 && this.hitDefinition.isProtectionPrayerReductionDeferred() && this.damage > 0) {
                this.damage = this.attacker != null && this.attacker.isPlayer() ? (int)Math.ceil((double)this.damage * 0.6) : 0;
            }
        }
        SpecialAttackDefinition.applyHitSpecialEffect(this.attacker, this.target, this.hitDefinition, this.damage);
        int n4 = this.target.getCurrentHitpoints();
        int hpBefore = n4;
        if (this.damage > n4) {
            this.damage = n4;
        }
        if ((this.damage > 0 || this.hitDefinition.getMaxDamage() <= 0) && this.hitDefinition.getEffects() != null && this.hitDefinition.getEffects().size() > 0) {
            for (Object effectObject : this.hitDefinition.getEffects()) {
                CombatEffect combatEffect = (CombatEffect)effectObject;
                if (combatEffect == null || !this.target.canApplyCombatEffect(combatEffect)) continue;
                combatEffect.apply(this);
            }
        }
        if (this.attacker != null && this.hitDefinition != null && this.attacker.isPlayer() && this.attacker.isPlayer()) {
            Player player = (Player)this.attacker;
            if (player.gameMode == 0 || !this.target.isPlayer()) {
                object = this.hitDefinition.getAttackStyle();
                n = this.damage;
                if (object != null && n > 0) {
                    double d = (double)n * 4.0;
                    double d2 = d / (double)((AttackStyleDefinition)object).getXpMode().getSkillIds().length;
                    int[] nArray = ((AttackStyleDefinition)object).getXpMode().getSkillIds();
                    int n5 = nArray.length;
                    int n6 = 0;
                    while (n6 < n5) {
                        n = nArray[n6];
                        player.getSkillManager().addExperience(n, d2);
                        ++n6;
                    }
                    player.getSkillManager().addExperience(3, d / 3.0);
                }
            }
        }
        if (this.damage > 0) {
            n4 -= this.damage;
        }
        if (this.hitDefinition.p() != -1) {
            this.target.getUpdateState().setAnimation(this.hitDefinition.p());
        }
        if (this.attacker != null && this.damage > 0) {
            Player player;
            DamageContribution damageContribution = this.target.getDamageContribution(this.attacker);
            if (damageContribution == null) {
                damageContribution = new DamageContribution(this.attacker);
            } else {
                this.target.getDamageContributions().remove(damageContribution);
            }
            damageContribution.addDamage(this.damage);
            this.target.getDamageContributions().add(damageContribution);
            if (this.attacker.isPlayer() && (player = (Player)this.attacker).getActivePrayers()[17] && this.target.isPlayer()) {
                PrayerManager.drainPrayerForSmite((Player)this.target, this.damage);
            }
        }
        if (GameplayTrace.enabled()) {
            GameplayTrace.log("combat apply-hit final attacker=" + GameplayTrace.describe(this.attacker) + " target=" + GameplayTrace.describe(this.target) + " damage=" + this.damage + " hpBefore=" + hpBefore + " hpAfter=" + n4 + " hitSuccessful=" + this.hitSuccessful);
        }
        this.target.setCurrentHitpoints(n4);
        if (this.target.isPlayer()) {
            int n7;
            Player player = (Player)this.target;
            if (n4 > 0 && n4 < (n7 = (int)Math.ceil((double)this.target.getMaxHitpoints() * 0.1))) {
                ItemStack itemStack;
                if (player.getActivePrayers()[16]) {
                    PrayerManager.triggerRedemption(player, this.target, n4);
                }
                if ((itemStack = player.getEquipmentManager().getContainer().getItemAt(12)) != null && itemStack.getId() == 2570 && player.getTeleportManager().castItemTeleport(TeleportManager.RESPAWN_TELEPORT_POSITION)) {
                    Player player3 = player;
                    player3.packetSender.sendGameMessage("Your ring shatters!");
                    player.getEquipmentManager().consumeSlotItemAmount(12, 1);
                }
            }
        }
        if (this.hitDefinition.getSpecialEffectId() != 5) {
            this.applyHitUpdate();
        }
    }

    public boolean isReady() {
        return this.delay <= 0;
    }

    public boolean canTargetTakeDamage() {
        return (Boolean)this.target.getAttributes().get("canTakeDamage");
    }

    public Entity getTarget() {
        return this.target;
    }

    public Entity getAttacker() {
        return this.attacker;
    }

    public int getDamage() {
        return this.damage;
    }
}

