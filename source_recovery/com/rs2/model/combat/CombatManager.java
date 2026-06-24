/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.combat;

import com.rs2.ServerSettings;
import com.rs2.bot.ClanWarsBotManager;
import com.rs2.bot.combat.BotCombatHelper;
import com.rs2.model.Entity;
import com.rs2.model.EntityTargetMovement;
import com.rs2.model.GameplayHelper;
import com.rs2.model.World;
import com.rs2.model.clue.TreasureTrailManager;
import com.rs2.model.combat.AttackBonusType;
import com.rs2.model.combat.AttackStyleDefinition;
import com.rs2.model.combat.AttackXpMode;
import com.rs2.model.combat.CombatAction;
import com.rs2.model.combat.CombatCycleEvent;
import com.rs2.model.combat.CombatType;
import com.rs2.model.combat.DeathAnimationTask;
import com.rs2.model.combat.DeathCleanupTask;
import com.rs2.model.combat.KalphiteQueenRespawnCombatEvent;
import com.rs2.model.combat.NpcRespawnCombatEvent;
import com.rs2.model.combat.attack.CombatAttackState;
import com.rs2.model.combat.attack.WeaponCombatAttack;
import com.rs2.model.combat.hit.HitDefinition;
import com.rs2.model.gameplay.barrows.BarrowsManager;
import com.rs2.model.gameplay.godwars.GodWarsDungeonManager;
import com.rs2.model.npc.Npc;
import com.rs2.model.npc.NpcDefinition;
import com.rs2.model.objects.DynamicObject;
import com.rs2.model.player.Player;
import com.rs2.model.randomevent.RandomEventManager;
import com.rs2.model.skill.fishing.FishingSpotManager;
import com.rs2.model.skill.magic.SpellDefinition;
import com.rs2.model.skill.magic.TeleportManager;
import com.rs2.model.skill.prayer.PrayerManager;
import com.rs2.model.skill.smithing.SmithingHandler;
import com.rs2.model.task.CycleEvent;
import com.rs2.model.task.CycleEventHandler;
import com.rs2.model.task.TickTask;
import com.rs2.util.GameUtil;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public final class CombatManager
extends TickTask {
    private static CombatManager instance;
    private final List pendingActions = new LinkedList();
    static Npc kalphiteQueenFirstForm;
    private static Random random;

    static {
        random = new Random(System.currentTimeMillis());
    }

    public CombatManager() {
        super(1);
        World.getTaskScheduler().schedule(this);
    }

    public static void startCombat(Entity entity, Entity entity2) {
        block23: {
            Object object;
            block22: {
                Player player;
                if (entity2.isNpc() && !((Npc)entity2).isActive() || entity2.isDead()) {
                    if (entity.isPlayer()) {
                        Player player2 = (Player)entity;
                        if (player2.currentBotTask != null) {
                            player2.interactWithBotNpcTargets(player2.botInteractionTargetIds);
                        }
                    }
                    return;
                }
                if (entity2.getMaxHitpoints() <= 0 || entity2.isNpc() && (((Npc)entity2).getNpcId() == 411 || RandomEventManager.isRandomEventCombatExcludedNpcId(((Npc)entity2).getNpcId()))) {
                    if (entity.isPlayer()) {
                        Player player3 = (Player)entity;
                        player3.packetSender.sendGameMessage("You cannot attack this npc.");
                    }
                    CombatManager.stopCombat(entity);
                    return;
                }
                if (entity2.isPlayer() && entity.isNpc()) {
                    object = (Npc)entity;
                    player = (Player)entity2;
                    if (((Npc)object).getNpcId() == 1456 && player.ai) {
                        CombatManager.stopCombat(entity);
                        return;
                    }
                }
                if (entity2.isPlayer() && entity.isNpc() && ((Npc)(object = (Npc)entity)).getNpcId() == 745) {
                    CombatManager.stopCombat(entity);
                    return;
                }
                if (entity.isPlayer() && entity2.isNpc() && !((Player)entity).getQuestManager().canAttackNpc(((Npc)entity2).getNpcId())) {
                    player = (Player)entity;
                    player.packetSender.sendGameMessage("You cannot attack this npc.");
                    CombatManager.stopCombat(entity);
                    return;
                }
                if (entity.isPlayer() && entity.isInDuelArena() && !((Player)entity).getDuelSession().isActiveDuelStarted()) {
                    CombatManager.stopCombat(entity);
                    return;
                }
                object = new LinkedList();
                int n = GameUtil.getDistance(entity.getPosition(), entity2.getPosition());
                entity.collectCombatAttackOptions((List)object, entity2, n);
                Object object2 = null;
                Object object3 = object.iterator();
                while (object3.hasNext()) {
                    object = (SmithingHandler)object3.next();
                    if (((SmithingHandler)object).getState() == CombatAttackState.a || object2 != null && ((SmithingHandler)object).getAttack().getAttackRange() <= ((SmithingHandler)object2).getAttack().getAttackRange()) continue;
                    object2 = object;
                }
                if (entity.isPlayer() && entity2.isNpc() && object2 != null) {
                    object = (Npc)entity2;
                    object3 = (Player)entity;
                    if (((SmithingHandler)object2).getAttack().getCombatType() == CombatType.MELEE && GodWarsDungeonManager.armadylNpcIds.contains(((Npc)object).getNpcId())) {
                        object2 = object3;
                        ((Player)object2).packetSender.sendGameMessage("The Aviansie is flying too high for you to attack using melee.");
                        return;
                    }
                }
                int n2 = object2 == null ? 1 : ((SmithingHandler)object2).getAttack().getAttackRange();
                entity.setAttackRange(n2);
                if (entity.isPlayer() && ((Player)(object = (Player)entity)).getUsername().toLowerCase().equals("mod test")) {
                    System.out.println("check attack");
                }
                object = entity2;
                entity2 = entity;
                if (!entity2.isPlayer()) break block22;
                Player player4 = (Player)entity2;
                if (player4.botCombatEscapeActive || player4.currentBotTask != null && !player4.botTaskState.equals("do task")) break block23;
            }
            Object object4 = new CombatCycleEvent(entity2, (Entity)object);
            entity2.setCombatTarget((Entity)object);
            entity2.getUpdateState().setFaceEntity(((Entity)object).getEncodedIndex());
            entity2.getUpdateState().setFacePosition(((Entity)object).getPosition());
            entity2.setMovementTarget((Entity)object);
            entity2.setActiveCycleEvent((CycleEvent)object4);
            if (entity2.isPlayer() && ((Player)(object = (Player)entity2)).getUsername().toLowerCase().equals("mod test")) {
                System.out.println("check start combat");
            }
            if (entity2.isNpc() && ((Npc)(object = (Npc)entity2)).getTransformTicksRemaining() <= 0 && ((Entity)object).getCombatTransformNpcId() > 0) {
                ((Npc)object).transformToNpcId(((Entity)object).getCombatTransformNpcId(), 999999);
            }
            if (entity2.isPlayer()) {
                object = (Player)entity2;
                if (((Player)object).npcTransformationId == 2626 || ((Player)object).npcTransformationId >= 3689 && ((Player)object).npcTransformationId <= 3694) {
                    ((Player)object).npcTransformationId = -1;
                    object4 = object;
                    ((Player)object4).packetSender.refreshSidebarInterfaces();
                    ((Player)object).setAppearanceUpdateRequired(true);
                }
            }
            CycleEventHandler.getInstance().schedule(entity2, entity2.getActiveCycleEvent(), 1);
        }
        if (entity.isPlayer() && ((Player)entity).getQuestState(0) == 47) {
            ((Player)entity).getDialogueManager().showTutorialInstructionOverlay("Sit back and watch.", "While you are fighting you will see a bar over your head. The", "bar shows how much health you have left. Your opponent will", "have one too. You will continue to attack the rat until it's dead", "or you do something else.", true);
        }
    }

    private static boolean b(Entity entity, Entity entity2) {
        if (entity2.isNpc()) {
            entity = (Player)entity;
            if (((Npc)(entity2 = (Npc)entity2)).getNpcId() == 911) {
                entity2.setDead(true);
                CombatManager.handleDeath(entity2);
                ((Player)entity).getTeleportManager().startStandardTeleport(2541, 4717, 0, null);
                ((Player)entity).mageArenaProgressStage = 5;
                return true;
            }
        }
        return false;
    }

    @Override
    public final void execute() {
        try {
            Object object2 = new LinkedList();
            object2.addAll(this.pendingActions);
            this.pendingActions.clear();
            LinkedList linkedList = new LinkedList();
            Iterator iterator = object2.iterator();
            while (iterator.hasNext()) {
                object2 = (CombatAction)iterator.next();
                ((CombatAction)object2).tickDelay();
                if (((CombatAction)object2).isReady()) {
                    if (!((CombatAction)object2).getTarget().isDead()) {
                        ((CombatAction)object2).applyHit();
                    }
                    if (((CombatAction)object2).getTarget().getCurrentHitpoints() > 0 || ((CombatAction)object2).getTarget().isDead()) continue;
                    if (((CombatAction)object2).getAttacker() != null && ((CombatAction)object2).getTarget() != null && (((CombatAction)object2).getAttacker().isPlayer() && ((CombatAction)object2).getTarget().isNpc() || ((CombatAction)object2).getAttacker().isNpc() && ((CombatAction)object2).getTarget().isPlayer())) {
                        boolean bl = ((CombatAction)object2).getAttacker().isPlayer() ? ((Player)((CombatAction)object2).getAttacker()).getQuestManager().handleCombatDeath(((CombatAction)object2).getAttacker(), ((CombatAction)object2).getTarget()) : ((Player)((CombatAction)object2).getTarget()).getQuestManager().handleCombatDeath(((CombatAction)object2).getAttacker(), ((CombatAction)object2).getTarget());
                        if (!bl) {
                            bl = CombatManager.b(((CombatAction)object2).getAttacker(), ((CombatAction)object2).getTarget());
                        }
                        if (bl) {
                            return;
                        }
                    }
                    if (((CombatAction)object2).getAttacker() != null && ((CombatAction)object2).getAttacker().isNpc()) {
                        boolean bl;
                        Entity entity = (Npc)((CombatAction)object2).getAttacker();
                        if (entity.a != null && (bl = ((Player)(entity = entity.a)).getQuestManager().handleCombatDeath(((CombatAction)object2).getAttacker(), ((CombatAction)object2).getTarget()))) {
                            return;
                        }
                    }
                    if (((CombatAction)object2).getTarget() != null && ((CombatAction)object2).getTarget().isNpc()) {
                        boolean bl;
                        Entity entity = (Npc)((CombatAction)object2).getTarget();
                        if (entity.a != null && (bl = ((Player)(entity = entity.a)).getQuestManager().handleCombatDeath(((CombatAction)object2).getAttacker(), ((CombatAction)object2).getTarget()))) {
                            return;
                        }
                    }
                    if (((CombatAction)object2).getTarget() != null && ((CombatAction)object2).getTarget().isPlayer()) {
                        Player player = (Player)((CombatAction)object2).getTarget();
                        if (player.godModeEnabled) {
                            player.setCurrentHitpoints(player.getMaxHitpoints());
                            return;
                        }
                    }
                    ((CombatAction)object2).getTarget().setDead(true);
                    CombatManager.handleDeath(((CombatAction)object2).getTarget());
                    if (!((CombatAction)object2).getTarget().isPlayer() || !((CombatAction)object2).getTarget().isInDuelArena()) continue;
                    ((Player)((CombatAction)object2).getTarget()).getDuelSession().getOpponent().getAttributes().put("canTakeDamage", false);
                    return;
                }
                if (((CombatAction)object2).getTarget().isDead()) continue;
                this.pendingActions.add(object2);
            }
            for (Object object2 : linkedList) {
                ((CombatAction)object2).applyHit();
            }
            linkedList.clear();
            return;
        }
        catch (Exception exception) {
            Exception exception2 = exception;
            exception.printStackTrace();
            return;
        }
    }

    public static void handleDeath(Entity entity) {
        int n;
        Object object;
        Entity entity2 = entity.getTopDamageContributor();
        Object object2 = entity.getDamageContributorList();
        if (((ArrayList)object2).size() > 0) {
            object = ((ArrayList)object2).iterator();
            while (object.hasNext()) {
                object2 = (Entity)object.next();
                if (entity2 == object2 || ((Entity)object2).getCombatTarget() != entity || !((Entity)object2).isPlayer()) continue;
                Player player = (Player)object2;
                if (!player.botEnabled || player.currentBotTask == null) continue;
                player.interactWithBotNpcTargets(player.botInteractionTargetIds);
            }
        }
        if (entity != null && entity2 != null && entity.isNpc() && entity2.isPlayer()) {
            object2 = (Npc)entity;
            Player[] playerArray = World.getPlayers();
            int n2 = playerArray.length;
            int n3 = 0;
            while (n3 < n2) {
                object = playerArray[n3];
                if (object != null) {
                    String[] stringArray;
                    if (object.bP == ((Entity)object2).getIndex() && ((Npc)object2).getOwnerPlayer() == null) {
                        stringArray = object;
                        object.packetSender.sendEntityHintIcon(1, Npc.findByDefinitionId(((Npc)object2).getNpcId()).getIndex());
                    }
                    if (object.bP == ((Entity)object2).getIndex() && ((Npc)object2).getOwnerPlayer() == object) {
                        stringArray = object;
                        object.packetSender.sendEntityHintIcon(1, -1);
                    }
                }
                ++n3;
            }
        }
        entity.nextActionSequence();
        entity.setDeathPosition(entity.getPosition().copy());
        if (entity2 != null && entity2.isPlayer()) {
            object2 = (Player)entity2;
            ((Entity)object2).getSingleCombatTimer().setDelayTicks(0);
            ((Entity)object2).getSingleCombatTimer().reset();
            if (((Player)object2).getQuestState(0) == 47 || ((Player)object2).getQuestState(0) == 49) {
                ((Player)object2).ea();
            }
        }
        if (entity.isPlayer()) {
            object2 = (Player)entity;
            ((Player)object2).setActionLocked(true);
            if (((Player)object2).botEnabled && ((Player)object2).currentBotTask == null && !((Player)object2).clanWarsBot) {
                object = new String[]{"gf", "fuck", "shit", "damn"};
                ((Player)object2).queuePublicChatMessage(object[GameUtil.randomInt(4)]);
            }
        }
        if (entity2 != null && entity2.isPlayer()) {
            object2 = (Player)entity2;
            if (((Player)object2).botEnabled) {
                if (((Entity)object2).getCombatTarget() == entity) {
                    ((Player)object2).botCombatState = "wait for loot";
                }
                if (entity.isPlayer() && ((Player)object2).currentGroup == null) {
                    if (((Player)object2).clanWarsBot) {
                        if (((Player)object2).clanWarsTeamId == 1) {
                            ((Player)object2).queuePublicChatMessage("#" + ClanWarsBotManager.clanWarsTeamOneTag);
                        } else {
                            ((Player)object2).queuePublicChatMessage("#" + ClanWarsBotManager.clanWarsTeamTwoTag);
                        }
                    } else {
                        ((Player)object2).queuePublicChatMessage("gf");
                    }
                }
            }
        }
        if ((n = entity.getDeathAnimationId()) != -1) {
            object = new DeathAnimationTask(2, n, entity, entity2);
            World.getTaskScheduler().schedule((TickTask)object);
        }
        object = new DeathCleanupTask(entity.getDeathDelayTicks(), entity, entity2);
        World.getTaskScheduler().schedule((TickTask)object);
        entity.nextActionSequence();
        entity.clearCombatEffectTasks();
        entity.getRecentCombatTimer().setDelayTicks(entity.getDeathDelayTicks() + 2);
        entity.getRecentCombatTimer().reset();
        entity.getSingleCombatTimer().setDelayTicks(entity.getDeathDelayTicks() + 2);
        entity.getSingleCombatTimer().reset();
        if (entity2 != null && entity.isPlayer() && ((Player)entity).getActivePrayers()[15]) {
            PrayerManager.triggerRetribution(entity, entity2);
        }
    }

    public static void finishDeath(Entity entity, Entity killer, boolean dropItems) {
        if (dropItems) {
            entity.dropDeathItems(killer);
            if (entity.getPoisonDamage() > 0.0) {
                entity.setPoisonDamage(0.0);
            }
            entity.setDeathPosition(null);
            if (killer != null && killer.isPlayer() && entity.isNpc()) {
                Npc killedNpc = (Npc)entity;
                Player killerPlayer = (Player)killer;
                int bossPetFlags = killerPlayer.getBossPetUnlockFlags();
                ++killerPlayer.npcKillCount;
                if (killedNpc.getNpcId() == 50 && (bossPetFlags & 1) == 0) {
                    killerPlayer.setBossPetUnlockFlags(bossPetFlags + 1);
                }
                if (killedNpc.getNpcId() == 1160 && (bossPetFlags & 2) == 0) {
                    killerPlayer.setBossPetUnlockFlags(bossPetFlags + 2);
                }
                if (killedNpc.getNpcId() == 3200 && (bossPetFlags & 4) == 0) {
                    killerPlayer.setBossPetUnlockFlags(bossPetFlags + 4);
                }
                if (killedNpc.getNpcId() == 2745 && (bossPetFlags & 8) == 0) {
                    killerPlayer.setBossPetUnlockFlags(bossPetFlags + 8);
                }
                TreasureTrailManager.recordClueWizardKill(killerPlayer, killedNpc);
                killerPlayer.getSlayerManager().recordKill(killedNpc);
                killerPlayer.getPrayerManager().awardNpcPrayerExperience(killedNpc);
                BarrowsManager.recordNpcKill(killerPlayer, killedNpc);
                if (ServerSettings.content2007Enabled) {
                    GodWarsDungeonManager.recordKillCount(killerPlayer, killedNpc.getNpcId());
                }
            }
        }
        if (entity.isNpc()) {
            Npc npc = (Npc)entity;
            if (killer != null && killer.isPlayer() && entity.isNpc()) {
                if (entity.isDoorSupportNpc()) {
                    new DynamicObject(ServerSettings.placeholderObjectId, entity.getPosition().getX(), entity.getPosition().getY(), entity.getPosition().getPlane(), 0, 10, 8967, 60);
                }
                ((Player)killer).getQuestManager().handleNpcKill(npc.getNpcId());
                Player player = (Player)killer;
                if (npc.isInFightCave() && player.isInFightCave()) {
                    boolean removedFightCaveNpc = false;
                    boolean killedJad = false;
                    if (npc.getNpcId() == 2745) {
                        killedJad = true;
                    }
                    for (Object fightCaveNpcObject : player.getFightCaveNpcs()) {
                        Npc fightCaveNpc = (Npc)fightCaveNpcObject;
                        if (npc != fightCaveNpc) continue;
                        player.removeFightCaveNpc(npc);
                        removedFightCaveNpc = true;
                        break;
                    }
                    if (removedFightCaveNpc && player.getFightCaveNpcs().isEmpty()) {
                        if (!killedJad) {
                            player.getFightCaveController().startNextWave();
                        } else {
                            player.getFightCaveController().completeFightCave();
                        }
                    }
                } else {
                    for (Object fightCaveNpcObject : player.getFightCaveNpcs()) {
                        Npc fightCaveNpc = (Npc)fightCaveNpcObject;
                        if (npc != fightCaveNpc) continue;
                        System.out.println("FIGHT CAVE! Not in area?");
                        break;
                    }
                }
                if (npc.isNpc()) {
                    if (npc.getNpcId() == 907) {
                        npc.setDead(true);
                        Npc nextNpc = new Npc(908);
                        GameplayHelper.spawnOwnedNpcAtPosition(player, npc.getPosition(), nextNpc, true, false);
                        nextNpc.getUpdateState().setForcedText("This is only the beginning; you can't beat me!");
                        player.mageArenaProgressStage = 1;
                    } else if (npc.getNpcId() == 908) {
                        npc.setDead(true);
                        Npc nextNpc = new Npc(909);
                        GameplayHelper.spawnOwnedNpcAtPosition(player, npc.getPosition(), nextNpc, true, false);
                        nextNpc.getUpdateState().setForcedText("Foolish mortal; I am unstoppable.");
                        player.mageArenaProgressStage = 2;
                    } else if (npc.getNpcId() == 909) {
                        npc.setDead(true);
                        Npc nextNpc = new Npc(910);
                        GameplayHelper.spawnOwnedNpcAtPosition(player, npc.getPosition(), nextNpc, true, false);
                        nextNpc.getUpdateState().setForcedText("Now you feel it... The dark energy.");
                        player.mageArenaProgressStage = 3;
                    } else if (npc.getNpcId() == 910) {
                        npc.setDead(true);
                        Npc nextNpc = new Npc(911);
                        GameplayHelper.spawnOwnedNpcAtPosition(player, npc.getPosition(), nextNpc, true, false);
                        nextNpc.getUpdateState().setForcedText("Aaaaaaaarrgghhhh! The power!");
                        player.mageArenaProgressStage = 4;
                    }
                }
            }
            if (!npc.isRespawnEnabled()) {
                if (npc.getNpcId() == 1160 && kalphiteQueenFirstForm != null) {
                    CycleEventHandler.getInstance().schedule(kalphiteQueenFirstForm, new KalphiteQueenRespawnCombatEvent(killer), kalphiteQueenFirstForm.getRespawnDelayTicks());
                }
                npc.setActive(false);
                World.unregisterNpc(npc);
            } else {
                if (npc.isActive()) {
                    npc.setActive(false);
                    npc.setPosition(npc.getSpawnPosition().copy());
                    npc.getMovementQueue().clear();
                    npc.transformToNpcId(npc.getOriginalNpcId(), 0);
                    CombatManager.stopCombat(npc);
                    if (npc.getNpcId() != 1158) {
                        CycleEventHandler.getInstance().schedule(npc, new NpcRespawnCombatEvent(npc, killer), npc.getRespawnDelayTicks());
                        return;
                    }
                    kalphiteQueenFirstForm = npc;
                    Npc firstForm = kalphiteQueenFirstForm;
                    Npc secondForm = new Npc(1160);
                    secondForm.setPosition(firstForm.getPosition());
                    secondForm.setSpawnPosition(firstForm.getSpawnPosition());
                    secondForm.setSpawnMinPosition(firstForm.getSpawnMinPosition());
                    secondForm.setSpawnMaxPosition(firstForm.getSpawnMaxPosition());
                    secondForm.setMovementMode(firstForm.getMovementMode());
                    secondForm.setFacingDirection(firstForm.getFacingDirection());
                    secondForm.setSpawnX(firstForm.getSpawnX());
                    secondForm.setSpawnY(firstForm.getSpawnY());
                    secondForm.setRespawnEnabled(false);
                    World.registerNpc(secondForm);
                    secondForm.getUpdateState().setAnimation(1181);
                    return;
                }
                npc.setActive(true);
                if (npc.getDefinition().getName().toLowerCase().equals("fishing spot") && (Npc)FishingSpotManager.activeSpotsByPosition.get(npc.getPosition()) == null) {
                    FishingSpotManager.activeSpotsByPosition.put(npc.getPosition(), npc);
                }
            }
        }
        entity.getUpdateState().setFaceEntity(-1);
        entity.setDead(false);
        if (entity.isNpc() && ((Npc)entity).getOwnerPlayer() != null) {
            entity.setDead(true);
        }
        entity.setCurrentHitpoints(entity.getMaxHitpoints());
        if (entity.isNpc()) {
            Npc npc = (Npc)entity;
            npc.resetCombatLevels();
        }
        entity.getUpdateState().setAnimation(65535, 0);
        entity.clearCombatEffectTasks();
        if (killer != null && killer.isPlayer() && entity.isPlayer() && killer != entity) {
            Player killerPlayer = (Player)killer;
            Player deadPlayer = (Player)entity;
            if (!entity.isInDuelArena()) {
                ++killerPlayer.playerKillCount;
            }
            killerPlayer.packetSender.sendGameMessage("You have defeated " + GameUtil.formatDisplayName(deadPlayer.getUsername()) + ".");
            if (killerPlayer.botEnabled) {
                BotCombatHelper.stopBotCombatTick(killerPlayer);
            }
        }
        if (entity.isPlayer()) {
            Player player = (Player)entity;
            player.setActionLocked(false);
            player.setHideHeldItemsInAppearance(false);
            player.setAutocastSpell(null);
            player.eq();
            player.getSkillManager().refreshAllSkills();
        }
        entity.getDamageContributions().clear();
        if (entity.isPlayer() && entity.isInDuelArena()) {
            ((Player)entity).getDuelSession().finishDuelLoss(false);
            return;
        }
        if (entity.isPlayer() && entity.isInFightCave()) {
            ((Player)entity).getFightCaveController().handleDeath();
            return;
        }
        if (entity.isPlayer() && ((Player)entity).getCreatureGraveyardController().isInsideGraveyard()) {
            ((Player)entity).getCreatureGraveyardController().handleGraveyardDeath();
            return;
        }
        if (entity.isPlayer()) {
            Player player = (Player)entity;
            if (player.isInTenthSquadSigilInstance()) {
                player.clearTemporaryCutsceneNpcs();
            }
            player.moveTo(TeleportManager.RESPAWN_TELEPORT_POSITION);
            player.packetSender.sendGameMessage("Oh dear, you are dead!");
            ++player.deathCount;
            player.packetSender.sendMusicJingle(203, 256);
            player.getRecentCombatTimer().setDelayTicks(0);
            player.getRecentCombatTimer().reset();
            player.getSingleCombatTimer().setDelayTicks(0);
            player.getSingleCombatTimer().reset();
            if (player.botEnabled) {
                player.botLootSellItems.clear();
                player.botCombatState = "died";
            }
        }
    }

    public static void initialize() {
        instance = new CombatManager();
    }

    public static CombatManager getInstance() {
        return instance;
    }

    public final void queueAction(CombatAction combatAction) {
        this.pendingActions.add(combatAction);
    }

    public static double calculateWeaponMaxHit(Player player, WeaponCombatAttack object) {
        double d;
        block15: {
            double d2;
            block14: {
                Object object2;
                block13: {
                    object2 = ((WeaponCombatAttack)object).getAttackStyle();
                    d = 0.0;
                    if (((AttackStyleDefinition)object2).getCombatType() != CombatType.MELEE) break block13;
                    d2 = CombatManager.calculateMeleeMaxHit(player, (WeaponCombatAttack)object);
                    break block14;
                }
                if (((AttackStyleDefinition)object2).getCombatType() != CombatType.RANGED || ((WeaponCombatAttack)object).getAmmunition() == null) break block15;
                if (ServerSettings.modernCombatSystemEnabled) {
                    int n;
                    Entity entity;
                    object2 = object;
                    object = player;
                    if (player.isPlayer()) {
                        entity = (Player)object;
                        double d3 = ((Player)entity).getSkillManager().getCurrentLevels()[4];
                        double d4 = d3 * CombatManager.getPrayerMultiplier((Player)entity, 4);
                        n = (int)Math.floor(d4);
                        object2 = ((WeaponCombatAttack)object2).getAttackStyle();
                        int n2 = 0;
                        if (((AttackStyleDefinition)object2).getXpMode() == AttackXpMode.RANGED_ACCURATE) {
                            n2 = 3;
                        }
                        n += n2;
                        n += 8;
                        if (((Player)entity).hasFullVoidRangedSet()) {
                            n = (int)((double)n * 1.1);
                        }
                        n = (int)Math.floor(n);
                    } else {
                        entity = (Npc)object;
                        double d5 = ((Npc)entity).getCurrentRangedLevel();
                        n = (int)d5;
                        n += 8;
                        n = (int)Math.floor(n);
                    }
                    double d6 = n;
                    n = 0;
                    if (player.isPlayer()) {
                        double d7 = player.getCombatBonus(12);
                        double d8 = 0.5 + d6 * (d7 + 64.0) / 640.0;
                        double d9 = d8;
                        d9 = d8;
                        n = (int)Math.floor(d8);
                    }
                    d2 = n;
                } else {
                    int n = player.getSkillManager().getCurrentLevels()[4];
                    double d10 = 0.0;
                    object2 = ((WeaponCombatAttack)object).getAttackStyle();
                    if (((AttackStyleDefinition)object2).getXpMode() == AttackXpMode.RANGED_ACCURATE) {
                        d10 = 3.0;
                    } else if (((AttackStyleDefinition)object2).getXpMode() == AttackXpMode.LONGRANGE) {
                        d10 = 1.0;
                    }
                    n = (int)((double)n + d10);
                    double d11 = ((WeaponCombatAttack)object).getAmmunition().getRangedStrength();
                    double d12 = ((double)n + d11 / 8.0 + (double)n * d11 * Math.pow(64.0, -1.0) + 14.0) / 10.0;
                    d2 = (int)Math.floor(d12);
                }
            }
            d = d2;
        }
        return d;
    }

    public static double calculateMeleeMaxHit(Player player, WeaponCombatAttack object) {
        if (ServerSettings.modernCombatSystemEnabled) {
            return CombatManager.calculateMeleeMaxHit((Entity)player, (WeaponCombatAttack)object);
        }
        double d = player.getSkillManager().getCurrentLevels()[2];
        if (player.getActivePrayers()[1]) {
            d *= 1.05;
        } else if (player.getActivePrayers()[4]) {
            d *= 1.1;
        } else if (player.getActivePrayers()[10]) {
            d *= 1.15;
        }
        object = ((WeaponCombatAttack)object).getAttackStyle();
        int n = 0;
        if (((AttackStyleDefinition)object).getXpMode() == AttackXpMode.AGGRESSIVE) {
            n = 3;
        } else if (((AttackStyleDefinition)object).getXpMode() == AttackXpMode.CONTROLLED) {
            n = 1;
        }
        int n2 = (int)(d + (double)n);
        double d2 = 5 + (n2 + 8) * (player.getCombatBonus(10) + 64) / 64;
        int n3 = (int)Math.floor(d2);
        return (int)Math.floor(n3 / 10);
    }

    public static double calculateHitChance(double d, double d2) {
        double d3;
        if (ServerSettings.modernCombatSystemEnabled) {
            double d4;
            double d5 = d2;
            double d6 = d;
            double d7 = d4 > d5 ? 1.0 - (d5 + 2.0) / (2.0 * (d6 + 1.0)) : d6 / (2.0 * (d5 + 1.0));
            return d7;
        }
        double d8 = Math.floor(d);
        double d9 = d8 < (d3 = Math.floor(d2)) ? (d8 - 1.0) / (d3 * 2.0) : 1.0 - (d3 + 1.0) / (d8 * 2.0);
        d9 = d9 > 0.9999 ? 0.9999 : (d9 < 1.0E-4 ? 1.0E-4 : d9);
        return d9;
    }

    public static boolean rollAccuracy(double d) {
        return random.nextDouble() <= d;
    }

    public static double calculateDefenceRoll(Entity entity, HitDefinition object) {
        AttackStyleDefinition attackStyleDefinition = ((HitDefinition)object).getAttackStyle();
        if (ServerSettings.modernCombatSystemEnabled) {
            AttackStyleDefinition attackStyleDefinition2 = attackStyleDefinition;
            Entity entity2 = entity;
            double d = 0.0;
            if (attackStyleDefinition2.getCombatType() == CombatType.MELEE) {
                int n;
                int n2;
                Entity entity3;
                AttackStyleDefinition attackStyleDefinition3 = attackStyleDefinition2;
                entity = entity3 = entity2;
                if (entity3.isPlayer()) {
                    object = (Player)entity;
                    double d2 = ((Player)object).getSkillManager().getCurrentLevels()[1];
                    double d3 = d2 * CombatManager.getPrayerMultiplier((Player)object, 1);
                    n2 = (int)Math.floor(d3);
                    int n3 = 0;
                    if (((AttackStyleDefinition)(object = ((Player)object).getWeaponProfile().getInterfaceDefinition().getAttackStyles()[((Player)object).getFightMode()])).getXpMode() == AttackXpMode.DEFENSIVE) {
                        n3 += 3;
                    } else if (((AttackStyleDefinition)object).getXpMode() == AttackXpMode.CONTROLLED) {
                        ++n3;
                    }
                    n2 += n3;
                    n2 += 8;
                    n2 = (int)Math.floor(n2);
                } else {
                    object = (Npc)entity;
                    double d4 = ((Npc)object).getCurrentDefenceLevel();
                    n2 = (int)d4;
                    n2 += 8;
                    n2 = (int)Math.floor(n2);
                }
                int n4 = n2;
                if (entity3.isPlayer()) {
                    Player player = (Player)entity3;
                    n = player.getCombatBonus(attackStyleDefinition3.getDefenseBonusIndex());
                    double d5 = n4 * (n + 64);
                    double d6 = d5;
                    d6 = d5;
                    n = (int)Math.floor(d5);
                } else {
                    Npc npc = (Npc)entity3;
                    int n5 = npc.getCurrentDefenceLevel();
                    n = npc.getDefenceBonus(attackStyleDefinition3.getDefenseBonusIndex());
                    n = (n5 + 9) * (n + 64);
                }
                d = n;
            } else if (attackStyleDefinition2.getCombatType() == CombatType.RANGED) {
                int n;
                int n6;
                AttackStyleDefinition attackStyleDefinition4 = attackStyleDefinition2;
                Entity entity4 = entity2;
                if (entity4.isPlayer()) {
                    Player player = (Player)entity4;
                    n6 = player.getSkillManager().getCurrentLevels()[1];
                    n = player.getCombatBonus(9);
                } else {
                    Npc npc = (Npc)entity4;
                    n6 = npc.getCurrentDefenceLevel();
                    n = npc.getDefenceBonus(attackStyleDefinition4.getDefenseBonusIndex());
                }
                n = (n6 + 9) * (n + 64);
                d = n;
            } else if (attackStyleDefinition2.getCombatType() == CombatType.MAGIC) {
                int n;
                int n7;
                AttackStyleDefinition attackStyleDefinition5 = attackStyleDefinition2;
                Entity entity5 = entity2;
                if (entity5.isPlayer()) {
                    Player player = (Player)entity5;
                    n7 = player.getSkillManager().getCurrentLevels()[6];
                    n = player.getCombatBonus(8);
                } else {
                    Npc npc = (Npc)entity5;
                    n7 = npc.getCurrentMagicLevel();
                    n = npc.getDefenceBonus(attackStyleDefinition5.getDefenseBonusIndex());
                }
                n = (n7 + 9) * (n + 64);
                d = n;
            }
            double d7 = d;
            return (int)d7;
        }
        AttackStyleDefinition attackStyleDefinition6 = attackStyleDefinition;
        Entity entity6 = entity;
        double d = entity6.getCombatBonus(attackStyleDefinition6.getAttackBonusType().getIndex() + AttackBonusType.values().length);
        if (attackStyleDefinition6.getXpMode() == AttackXpMode.SARADOMIN_SWORD) {
            d = entity6.getCombatBonus(AttackBonusType.MAGIC.getIndex() + AttackBonusType.values().length);
        }
        double d8 = entity6.getDefenceLevelFor(attackStyleDefinition6.getCombatType());
        if (d < 0.0) {
            d8 /= 2.0;
            d *= 3.0;
        }
        if (attackStyleDefinition6.getCombatType() == CombatType.MELEE && entity6.isPlayer()) {
            if (((Player)(entity6 = (Player)entity6)).getActivePrayers()[0]) {
                d8 *= 1.05;
            } else if (((Player)entity6).getActivePrayers()[3]) {
                d8 *= 1.1;
            } else if (((Player)entity6).getActivePrayers()[9]) {
                d8 *= 1.15;
            }
        }
        double d9 = Math.floor(d8 + d) + 8.0;
        if (entity.isPlayer()) {
            Object object2 = (Player)entity;
            if (attackStyleDefinition.getCombatType() == CombatType.MAGIC) {
                int n = ((Player)object2).getSkillManager().getCurrentLevels()[6];
                d9 = (int)(Math.floor((double)n * 0.7) + Math.floor(d9 * 0.3));
            } else if (((AttackStyleDefinition)(object2 = ((Player)object2).getWeaponProfile().getInterfaceDefinition().getAttackStyles()[((Player)object2).getFightMode()])).getXpMode() != AttackXpMode.DEFENSIVE && ((AttackStyleDefinition)object2).getXpMode() != AttackXpMode.LONGRANGE) {
                ((AttackStyleDefinition)object2).getXpMode();
            }
        }
        if (((HitDefinition)object).getSpecialEffectId() == 11) {
            d9 *= 0.75;
        }
        if (entity.isNpc()) {
            if (attackStyleDefinition.getCombatType() == CombatType.MAGIC) {
                d9 *= 0.7;
            }
            if (attackStyleDefinition.getCombatType() == CombatType.MELEE) {
                d9 *= 0.6;
            }
        }
        return d9;
    }

    public static double calculateAttackRoll(Entity entity, HitDefinition object) {
        Player player;
        AttackStyleDefinition attackStyleDefinition = ((HitDefinition)object).getAttackStyle();
        double d = ((HitDefinition)object).getAccuracyMultiplier();
        if (ServerSettings.modernCombatSystemEnabled) {
            object = attackStyleDefinition;
            double d2 = 0.0;
            if (((AttackStyleDefinition)object).getCombatType() == CombatType.MELEE) {
                int n;
                Object object2 = object;
                Entity entity2 = entity;
                Object object3 = object2;
                Entity entity3 = entity2;
                if (entity2.isPlayer()) {
                    object = (Player)entity3;
                    double d3 = ((Player)object).getSkillManager().getCurrentLevels()[0];
                    double d4 = d3 * CombatManager.getPrayerMultiplier((Player)object, 0);
                    n = (int)Math.floor(d4);
                    int n2 = 0;
                    if (((AttackStyleDefinition)object3).getXpMode() == AttackXpMode.MELEE_ACCURATE) {
                        n2 = 3;
                    } else if (((AttackStyleDefinition)object3).getXpMode() == AttackXpMode.CONTROLLED) {
                        n2 = 1;
                    }
                    n += n2;
                    n += 8;
                    if (((Player)object).hasFullVoidMeleeSet()) {
                        n = (int)((double)n * 1.1);
                    }
                    n = (int)Math.floor(n);
                } else {
                    object = (Npc)entity3;
                    double d5 = ((Npc)object).getCurrentAttackLevel();
                    n = (int)d5;
                    n += 8;
                    n = (int)Math.floor(n);
                }
                int n3 = n;
                if (entity2.isPlayer()) {
                    Player player2 = (Player)entity2;
                    n = player2.getCombatBonus(((AttackStyleDefinition)object2).getAttackBonusIndex());
                    double d6 = n3 * (n + 64);
                    double d7 = d6;
                    d7 = d6;
                    n = (int)Math.floor(d6);
                } else {
                    Npc npc = (Npc)entity2;
                    n = npc.getMeleeAttackBonus();
                    double d8 = n3 * (n + 64);
                    n = (int)Math.floor(d8);
                }
                d2 = n;
            } else if (((AttackStyleDefinition)object).getCombatType() == CombatType.RANGED) {
                int n;
                Object object4 = object;
                Entity entity4 = entity;
                Object object5 = object4;
                Entity entity5 = entity4;
                if (entity4.isPlayer()) {
                    object = (Player)entity5;
                    double d9 = ((Player)object).getSkillManager().getCurrentLevels()[4];
                    double d10 = d9 * CombatManager.getPrayerMultiplier((Player)object, 4);
                    n = (int)Math.floor(d10);
                    int n4 = 0;
                    if (((AttackStyleDefinition)object5).getXpMode() == AttackXpMode.RANGED_ACCURATE) {
                        n4 = 3;
                    }
                    n += n4;
                    n += 8;
                    if (((Player)object).hasFullVoidRangedSet()) {
                        n = (int)((double)n * 1.1);
                    }
                    n = (int)Math.floor(n);
                } else {
                    object = (Npc)entity5;
                    double d11 = ((Npc)object).getCurrentRangedLevel();
                    n = (int)d11;
                    n += 8;
                    n = (int)Math.floor(n);
                }
                int n5 = n;
                if (entity4.isPlayer()) {
                    Player player3 = (Player)entity4;
                    n = player3.getCombatBonus(4);
                    double d12 = n5 * (n + 64);
                    double d13 = d12;
                    d13 = d12;
                    n = (int)Math.floor(d12);
                } else {
                    Npc npc = (Npc)entity4;
                    n = npc.getRangedAttackBonus();
                    double d14 = n5 * (n + 64);
                    n = (int)Math.floor(d14);
                }
                d2 = n;
            } else if (((AttackStyleDefinition)object).getCombatType() == CombatType.MAGIC) {
                int n;
                int n6;
                double d15;
                Entity entity6;
                Object object6 = object;
                entity = entity6 = entity;
                if (entity6.isPlayer()) {
                    Player player4 = (Player)entity;
                    d15 = player4.getSkillManager().getCurrentLevels()[6];
                    double d16 = d15 * CombatManager.getPrayerMultiplier(player4, 6);
                    n6 = (int)Math.floor(d16);
                    if (player4.hasFullVoidMagicSet()) {
                        n6 = (int)((double)n6 * 1.45);
                    }
                    n6 += 9;
                    n6 = (int)Math.floor(n6);
                } else {
                    Npc npc = (Npc)entity;
                    d15 = npc.getCurrentMagicLevel();
                    n6 = (int)d15;
                    n6 += 9;
                    n6 = (int)Math.floor(n6);
                }
                int n7 = n6;
                entity = entity6;
                if (entity.isPlayer()) {
                    Player player5 = (Player)entity;
                    n = player5.getCombatBonus(3);
                } else {
                    Npc npc = (Npc)entity;
                    n = npc.getMagicAttackBonus();
                }
                int n8 = n;
                entity6.isPlayer();
                double d17 = n7 * (n8 + 64);
                n8 = (int)Math.floor(d17);
                d2 = n8;
            }
            double d18 = d2;
            return (int)(d18 * d);
        }
        object = attackStyleDefinition;
        double d19 = entity.getCombatBonus(((AttackStyleDefinition)object).getAttackBonusType().getIndex());
        double d20 = entity.getAttackLevelFor(((AttackStyleDefinition)object).getCombatType());
        if (d19 < 0.0) {
            d20 /= 2.0;
            d19 *= 3.0;
        }
        if (((AttackStyleDefinition)object).getCombatType() == CombatType.MELEE && entity.isPlayer()) {
            player = (Player)entity;
            if (player.getActivePrayers()[2]) {
                d20 *= 1.05;
            } else if (player.getActivePrayers()[5]) {
                d20 *= 1.1;
            } else if (player.getActivePrayers()[11]) {
                d20 *= 1.15;
            }
            if (player.hasFullVoidMeleeSet()) {
                d20 *= 1.1;
            }
        }
        if (((AttackStyleDefinition)object).getCombatType() == CombatType.RANGED && entity.isPlayer() && (player = (Player)entity).hasFullVoidRangedSet()) {
            d20 *= 1.1;
        }
        if (((AttackStyleDefinition)object).getCombatType() == CombatType.MAGIC && entity.isPlayer() && (player = (Player)entity).hasFullVoidMagicSet()) {
            d20 *= 1.45;
        }
        double d21 = Math.floor(d20 + d19) + 8.0;
        if (attackStyleDefinition.getXpMode() != AttackXpMode.MELEE_ACCURATE && attackStyleDefinition.getXpMode() != AttackXpMode.RANGED_ACCURATE) {
            attackStyleDefinition.getXpMode();
        }
        return (int)(d21 * d);
    }

    public static void stopCombat(Entity entity) {
        if (entity.isPlayer()) {
            ((Player)entity).setQueuedCombatSpell(null);
            if (((Player)entity).botEnabled) {
                BotCombatHelper.stopBotCombatTick((Player)entity);
            }
        }
        entity.setCombatTarget(null);
        entity.setActiveCycleEvent(null);
        entity.getUpdateState().setFaceEntity(-1);
        EntityTargetMovement.clearMovementTarget(entity);
    }

    private static double getPrayerMultiplier(Player player, int n) {
        double d = 1.0;
        if (n == 2) {
            if (player.getActivePrayers()[1]) {
                d = 1.05;
            } else if (player.getActivePrayers()[4]) {
                d = 1.1;
            } else if (player.getActivePrayers()[10]) {
                d = 1.15;
            }
        } else if (n == 0) {
            if (player.getActivePrayers()[2]) {
                d = 1.05;
            } else if (player.getActivePrayers()[5]) {
                d = 1.1;
            } else if (player.getActivePrayers()[11]) {
                d = 1.15;
            }
        } else if (n == 1) {
            if (player.getActivePrayers()[0]) {
                d = 1.05;
            } else if (player.getActivePrayers()[3]) {
                d = 1.1;
            } else if (player.getActivePrayers()[9]) {
                d = 1.15;
            }
        }
        return d;
    }

    public static int calculateMeleeMaxHit(Entity entity, WeaponCombatAttack weaponCombatAttack) {
        int n;
        int n2;
        Entity entity2;
        WeaponCombatAttack weaponCombatAttack2 = weaponCombatAttack;
        Entity entity3 = entity;
        if (entity3.isPlayer()) {
            entity2 = (Player)entity3;
            double d = ((Player)entity2).getSkillManager().getCurrentLevels()[2];
            double d2 = d * CombatManager.getPrayerMultiplier((Player)entity2, 2);
            n2 = (int)Math.floor(d2);
            AttackStyleDefinition attackStyleDefinition = weaponCombatAttack2.getAttackStyle();
            int n3 = 0;
            if (attackStyleDefinition.getXpMode() == AttackXpMode.AGGRESSIVE) {
                n3 = 3;
            } else if (attackStyleDefinition.getXpMode() == AttackXpMode.CONTROLLED) {
                n3 = 1;
            }
            n2 += n3;
            n2 += 8;
            if (((Player)entity2).hasFullVoidMeleeSet()) {
                n2 = (int)((double)n2 * 1.1);
            }
        } else {
            entity2 = (Npc)entity3;
            double d = ((Npc)entity2).getCurrentStrengthLevel();
            n2 = (int)d;
            ++n2;
            n2 += 8;
        }
        double d = n2;
        if (entity.isPlayer()) {
            entity = (Player)entity;
            double d3 = entity.getCombatBonus(10);
            double d4 = 0.5 + d * ((d3 + 64.0) / 640.0);
            double d5 = d4;
            d5 = d4;
            n = (int)Math.floor(d4);
        } else {
            double d6;
            NpcDefinition npcDefinition;
            entity = (Npc)entity;
            NpcDefinition npcDefinition2 = npcDefinition = ((Npc)entity).getDefinition();
            double d7 = d6 = (double)npcDefinition2.getStrengthLevel();
            d7 = d6 + 1.0;
            double d8 = npcDefinition2.getMeleeStrengthBonus();
            double d9 = 0.5 + (d7 += 8.0) * ((d8 + 64.0) / 640.0);
            int n4 = (int)Math.floor(d9);
            if (npcDefinition.getMaxHit() != n4) {
                n = npcDefinition.getMaxHit();
            } else {
                n = ((Npc)entity).getCurrentStrengthLevel();
                double d10 = n + 8;
                double d11 = npcDefinition.getMeleeStrengthBonus();
                double d12 = 0.5 + d10 * ((d11 + 64.0) / 640.0);
                n = (int)Math.floor(d12);
            }
        }
        return n;
    }

    public static int calculateSpellMaxHit(Entity entity, SpellDefinition spellDefinition) {
        if (!spellDefinition.isCombatSpell() || spellDefinition.getHitDefinition() == null) {
            return 0;
        }
        HitDefinition hitDefinition = spellDefinition.getHitDefinition().copy();
        int n = hitDefinition.getMaxDamage();
        if (entity.isPlayer()) {
            if (entity.isChargeSpellActive() && (spellDefinition == SpellDefinition.FLAMES_OF_ZAMORAK || spellDefinition == SpellDefinition.CLAWS_OF_GUTHIX || spellDefinition == SpellDefinition.SARADOMIN_STRIKE)) {
                n += 10;
            }
            if (((Player)(entity = (Player)entity)).getEquipmentManager().getItemIdAtSlot(9) == 777 && (spellDefinition == SpellDefinition.WIND_BOLT || spellDefinition == SpellDefinition.WATER_BOLT || spellDefinition == SpellDefinition.EARTH_BOLT || spellDefinition == SpellDefinition.FIRE_BOLT)) {
                n += 3;
            }
            if (((Player)entity).getEquipmentManager().getItemIdAtSlot(3) == 4675 || ((Player)entity).getEquipmentManager().getItemIdAtSlot(3) == 4710 || ((Player)entity).getEquipmentManager().getItemIdAtSlot(3) == 6914) {
                n = (int)((double)n * 1.1);
            }
            if (spellDefinition == SpellDefinition.MAGIC_DART) {
                n += ((Player)entity).getSkillManager().getBaseLevel(6) / 10;
            }
        }
        return n;
    }
}
