/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.bot.combat;

import com.rs2.ServerSettings;
import com.rs2.bot.ClanWarsBotManager;
import com.rs2.bot.combat.BotCombatHelper;
import com.rs2.bot.combat.BotCombatLoadoutTables;
import com.rs2.bot.combat.BotGroupCombatTickTask;
import com.rs2.bot.combat.BotPvpCombatHandler;
import com.rs2.bot.combat.BotPvpTargetSearchTickTask;
import com.rs2.cache.InterfaceDefinition;
import com.rs2.model.GameplayHelper;
import com.rs2.model.World;
import com.rs2.model.combat.special.SpecialAttackDefinition;
import com.rs2.model.item.ItemDefinition;
import com.rs2.model.item.ItemStack;
import com.rs2.model.item.consumable.FoodDefinition;
import com.rs2.model.item.consumable.PotionHandler;
import com.rs2.model.path.PathReachability;
import com.rs2.model.player.Player;
import com.rs2.model.skill.SkillManager;
import com.rs2.model.skill.magic.SpellDefinition;
import com.rs2.model.skill.magic.Spellbook;
import com.rs2.model.task.TickTask;
import com.rs2.util.GameUtil;
import java.util.ArrayList;

public final class BotCombatLoadoutManager {
    public static PathReachability a = new PathReachability();

    public static void initializeCombatLoadoutTypes() {
        ArrayList<Integer> arrayList = new ArrayList<Integer>();
        arrayList.add(0);
        arrayList.add(1);
        arrayList.add(2);
        arrayList.add(7);
        arrayList.add(4);
        arrayList.add(6);
        arrayList.add(5);
        if (ItemDefinition.isDefined(6528)) {
            arrayList.add(8);
        }
        if (ItemDefinition.isDefined(6111)) {
            arrayList.add(9);
        }
        if (ItemDefinition.isDefined(4411)) {
            arrayList.add(10);
        }
        int[] nArray = new int[arrayList.size()];
        int n = 0;
        while (n < arrayList.size()) {
            nArray[n] = (Integer)arrayList.get(n);
            ++n;
        }
        BotCombatLoadoutTables.enabledCombatLoadoutTypes = nArray;
    }

    public static void startCombatLoadoutBot(Player player) {
        Player player2;
        Player player3 = player;
        boolean bl = false;
        player3.botCombatStyle = BotCombatLoadoutTables.enabledCombatLoadoutTypes[GameUtil.randomInt(BotCombatLoadoutTables.enabledCombatLoadoutTypes.length)];
        if (player3.botCombatStyle == 8 || player3.botCombatStyle == 9 || player3.botCombatStyle == 10 || player3.botCombatStyle == 11) {
            bl = true;
        }
        while (BotCombatHelper.isFreeToPlayWorld() && bl) {
            player3.botCombatStyle = BotCombatLoadoutTables.enabledCombatLoadoutTypes[GameUtil.randomInt(BotCombatLoadoutTables.enabledCombatLoadoutTypes.length)];
            bl = player3.botCombatStyle == 8 || player3.botCombatStyle == 9 || player3.botCombatStyle == 10 || player3.botCombatStyle == 11;
        }
        GameplayHelper.resetBotTaskState(player3);
        if (ServerSettings.wildyBotsUseNewGeneration) {
            player2 = player3;
            GameplayHelper.resetBotSkillsToBase(player2);
            int n = ServerSettings.wildyBotsBaseCombatLevel - ServerSettings.wildyBotsCombatLevelSpread;
            if ((n += GameUtil.randomInt((ServerSettings.wildyBotsCombatLevelSpread << 1) + 1)) < 3) {
                n = 3;
            }
            if (n > SkillManager.maxCombatLevel) {
                n = SkillManager.maxCombatLevel;
            }
            while (player2.getCombatLevel() < n) {
                int[] nArray = new int[6];
                nArray[1] = 2;
                nArray[2] = 1;
                nArray[3] = 4;
                nArray[4] = 6;
                nArray[5] = 5;
                int[] nArray2 = nArray;
                int n2 = nArray[GameUtil.randomInt(6)];
                player2.getSkillManager();
                int n3 = SkillManager.getLevelForExperience(player2.getSkillManager().getExperience()[n2]);
                BotCombatHelper.setBotSkillLevel(player2, n2, n3 + 1);
                player2.getSkillManager().getExperience()[3] = BotCombatHelper.calculateBotHitpointsExperience(player2);
                int[] nArray3 = player2.getSkillManager().getCurrentLevels();
                player2.getSkillManager();
                nArray3[3] = SkillManager.getLevelForExperience(player2.getSkillManager().getExperience()[3]);
            }
            player2.getSkillManager().refreshAllSkills();
            BotCombatLoadoutManager.selectCombatStyleFromStats(player2, true);
            player2 = player3;
            player2.getInventoryManager().getContainer().clear();
            player2.getEquipmentManager().getContainer().clear();
            if (player2.botCombatStyle == 0) {
                player3 = player2;
                BotCombatLoadoutManager.prepareMeleeLoadout(player3, false);
                BotCombatLoadoutManager.equipGlovesAndBoots(player2);
            } else if (player2.botCombatStyle == 2) {
                BotCombatLoadoutManager.prepareMagicLoadout(player2);
            } else if (player2.botCombatStyle == 1) {
                BotCombatLoadoutManager.prepareRangedLoadout(player2);
                int n4 = player2.getSkillManager().getCurrentLevels()[4] >= 40 ? 1731 : (GameUtil.randomInt(3) == 0 ? 1478 : 1729);
                if (!BotCombatHelper.isFreeToPlayWorld() && player2.getCombatLevel() >= 60 && GameUtil.randomInt(2) == 0) {
                    n4 = 1712;
                }
                player2.getEquipmentManager().getContainer().setItem(2, new ItemStack(n4));
            }
            BotCombatLoadoutManager.equipRandomCape(player2);
            BotCombatLoadoutManager.addCombatSupplies(player2);
            player2.getInventoryManager().refresh();
            player2.getEquipmentManager().refresh();
        } else {
            player2 = player3;
            int n = player2.botCombatStyle;
            GameplayHelper.resetBotSkillsToBase(player2);
            switch (n) {
                case 4: {
                    BotCombatHelper.setBotSkillLevel(player2, 0, 40);
                    BotCombatHelper.setBotSkillLevel(player2, 2, 20 + GameUtil.randomInt(79) + 1);
                    if (GameUtil.randomInt(3) == 0) {
                        BotCombatHelper.setBotSkillLevel(player2, 1, 10);
                    }
                    if (GameUtil.randomInt(4) == 0) break;
                    BotCombatHelper.setBotSkillLevel(player2, 5, GameUtil.randomInt(2) == 0 ? 25 : 31);
                    break;
                }
                case 9: {
                    BotCombatHelper.setBotSkillLevel(player2, 0, 60);
                    BotCombatHelper.setBotSkillLevel(player2, 2, 60 + GameUtil.randomInt(20));
                    if (GameUtil.randomInt(4) != 0) {
                        BotCombatHelper.setBotSkillLevel(player2, 5, GameUtil.randomInt(2) == 0 ? 25 : 25 + GameUtil.randomInt(30));
                    }
                    BotCombatHelper.setBotSkillLevel(player2, 6, GameUtil.randomInt(2) == 0 ? 1 : 25);
                    break;
                }
                case 10: {
                    BotCombatHelper.setBotSkillLevel(player2, 0, 70);
                    BotCombatHelper.setBotSkillLevel(player2, 2, 70 + GameUtil.randomInt(20));
                    BotCombatHelper.setBotSkillLevel(player2, 5, 52);
                    BotCombatHelper.setBotSkillLevel(player2, 6, GameUtil.randomInt(2) == 0 ? 1 : 25);
                    break;
                }
                case 6: {
                    BotCombatHelper.setBotSkillLevel(player2, 6, 1 + (BotCombatHelper.isFreeToPlayWorld() ? GameUtil.randomInt(59) : GameUtil.randomInt(99)));
                    break;
                }
                case 5: {
                    BotCombatHelper.setBotSkillLevel(player2, 4, 1 + GameUtil.randomInt(99));
                    break;
                }
                case 8: {
                    BotCombatHelper.setBotSkillLevel(player2, 0, GameUtil.randomInt(2) == 0 ? 1 : 50);
                    BotCombatHelper.setBotSkillLevel(player2, 2, 60 + GameUtil.randomInt(30));
                    BotCombatHelper.setBotSkillLevel(player2, 4, 50 + GameUtil.randomInt(20));
                    BotCombatHelper.setBotSkillLevel(player2, 5, GameUtil.randomInt(2) == 0 ? 25 : 31);
                    BotCombatHelper.setBotSkillLevel(player2, 6, GameUtil.randomInt(2) == 0 ? 1 : 25);
                    break;
                }
                case 0: 
                case 1: 
                case 2: 
                case 3: 
                case 7: {
                    int n5;
                    int n6 = 1 + GameUtil.randomInt(99);
                    int n7 = n6 / 5 << 1;
                    if (n7 == 0) {
                        n7 = 2;
                    }
                    if ((n5 = n6 - n6 / 5 + GameUtil.randomInt(n7)) < 40) {
                        n5 = 40;
                    }
                    BotCombatHelper.setBotSkillLevel(player2, 0, n5);
                    n7 = n6 / 5 << 1;
                    if (n7 == 0) {
                        n7 = 2;
                    }
                    if ((n5 = n6 - n6 / 5 + GameUtil.randomInt(n7)) < 40) {
                        n5 = 40;
                    }
                    BotCombatHelper.setBotSkillLevel(player2, 2, n5);
                    n7 = n6 / 5 << 1;
                    if (n7 == 0) {
                        n7 = 2;
                    }
                    if ((n5 = n6 - n6 / 5 + GameUtil.randomInt(n7)) < 40) {
                        n5 = 40;
                    }
                    BotCombatHelper.setBotSkillLevel(player2, 1, n5);
                    n7 = n6 / 5 << 1;
                    if (n7 == 0) {
                        n7 = 2;
                    }
                    if ((n5 = n6 - n6 / 5 + GameUtil.randomInt(n7)) < 40) {
                        n5 = 40;
                    }
                    BotCombatHelper.setBotSkillLevel(player2, 4, n5);
                    n7 = n6 / 5 << 1;
                    if (n7 == 0) {
                        n7 = 2;
                    }
                    if ((n5 = n6 - n6 / 5 + GameUtil.randomInt(n7)) < 40) {
                        n5 = 40;
                    }
                    BotCombatHelper.setBotSkillLevel(player2, 6, n5);
                    n7 = n6 / 5 << 1;
                    if (n7 == 0) {
                        n7 = 2;
                    }
                    if ((n5 = n6 - (n6 / 5 << 1) + GameUtil.randomInt(n7)) < 40) {
                        n5 = 40;
                    }
                    BotCombatHelper.setBotSkillLevel(player2, 5, n5);
                    if (!BotCombatHelper.isFreeToPlayWorld()) {
                        n7 = n6 / 5 << 1;
                        if (n7 == 0) {
                            n7 = 2;
                        }
                        if ((n5 = n6 - (n6 / 5 << 1) + GameUtil.randomInt(n7)) < 40) {
                            n5 = 40;
                        }
                        BotCombatHelper.setBotSkillLevel(player2, 16, n5);
                    }
                    BotCombatLoadoutManager.selectCombatStyleFromStats(player2, false);
                }
            }
            player2.getSkillManager().getExperience()[3] = BotCombatHelper.calculateBotHitpointsExperience(player2);
            int[] nArray = player2.getSkillManager().getCurrentLevels();
            player2.getSkillManager();
            nArray[3] = SkillManager.getLevelForExperience(player2.getSkillManager().getExperience()[3]);
            player2.getSkillManager().refreshAllSkills();
            BotCombatLoadoutManager.prepareCombatLoadout(player3, false);
        }
        player.botEnabled = true;
        BotCombatHelper.prepareBotPvpSearchPosition(player);
        player3 = player;
        boolean bl2 = player3.botCombatStyle == 4 || player3.botCombatStyle == 6 || player3.botCombatStyle == 5;
        BotPvpTargetSearchTickTask botPvpTargetSearchTickTask = new BotPvpTargetSearchTickTask(10, player3, bl2);
        World.getTaskScheduler().schedule(botPvpTargetSearchTickTask);
    }

    public static void startGroupCombatBot(Player player) {
        Object object;
        Object object2;
        Player player2 = player;
        player.botEnabled = true;
        player2.botEscapeStuckTicks = 0;
        player2.botCombatEscapeActive = false;
        player2.botAntipoisonAvailable = false;
        player2.setAutoRetaliate(true);
        player2.botMagicPenaltyGearUnequipped = false;
        player2.botCombatState = null;
        player2.botCombatSpell = null;
        player2.botFoodDepleted = false;
        player2.botStrengthPotionDepleted = false;
        player2.botSpecialAttackEnergyCost = 0;
        player2.botSpecialCombatStyle = 0;
        player2.botWeaponItemId = player2.getEquipmentManager().getItemIdAtSlot(3);
        player2.botPrimaryCombatStyle = 0;
        player2.botCombatStyle = 0;
        if (player2.botWeaponItemId > 0) {
            object2 = ItemDefinition.forId(player2.botWeaponItemId).getName().toLowerCase();
            if (((String)object2).contains("bow") || ((String)object2).contains("knife") || ((String)object2).contains("dart") || ((String)object2).contains("javelin") || ((String)object2).contains("thrownaxe")) {
                player2.botPrimaryCombatStyle = BotPvpCombatHandler.RANGED_COMBAT_STYLE;
                player2.botCombatStyle = 1;
            } else if (((String)object2).contains("staff")) {
                player2.botPrimaryCombatStyle = BotPvpCombatHandler.MAGIC_COMBAT_STYLE;
                player2.botCombatStyle = 2;
            }
        }
        player2.botActiveCombatStyle = player2.botPrimaryCombatStyle;
        player2.botShieldItemId = player2.getEquipmentManager().getItemIdAtSlot(5);
        ItemStack[] itemStackArray = player2.getInventoryManager().getContainer().getItems();
        int n = itemStackArray.length;
        int n2 = 0;
        while (n2 < n) {
            object2 = itemStackArray[n2];
            if (object2 != null && ((ItemStack)object2).getDefinition().getEquipmentSlot() == 3) {
                player2.botSpecialWeaponItemId = ((ItemStack)object2).getId();
                player2.botSpecialCombatStyle = 0;
                break;
            }
            ++n2;
        }
        itemStackArray = player2.getInventoryManager().getContainer().getItems();
        n = itemStackArray.length;
        n2 = 0;
        while (n2 < n) {
            object2 = itemStackArray[n2];
            if (object2 != null && (object = FoodDefinition.forItemId(((ItemStack)object2).getId())) != null) {
                player2.botFoodItemId = ((ItemStack)object2).getId();
                break;
            }
            ++n2;
        }
        itemStackArray = player2.getInventoryManager().getContainer().getItems();
        n = itemStackArray.length;
        n2 = 0;
        while (n2 < n) {
            object2 = itemStackArray[n2];
            if (object2 != null && player2.getPotionHandler().selectPotionForItemId(((ItemStack)object2).getId()) && PotionHandler.definitions[player2.getPotionHandler().selectedDefinitionIndex].isAntipoison()) {
                player2.botAntipoisonAvailable = true;
                break;
            }
            ++n2;
        }
        if (player2.botPrimaryCombatStyle == BotPvpCombatHandler.MAGIC_COMBAT_STYLE) {
            Player magicPlayer = player2;
            ArrayList<SpellDefinition> arrayList = new ArrayList<SpellDefinition>();
            SpellDefinition[] spellDefinitions = BotCombatLoadoutTables.standardCombatSpellProgression;
            int n3 = 0;
            while (n3 < 12) {
                SpellDefinition spellDefinition = spellDefinitions[n3];
                arrayList.add(spellDefinition);
                ++n3;
            }
            if (!ServerSettings.freeToPlayWorld) {
                spellDefinitions = BotCombatLoadoutTables.standardWaveSpellProgression;
                n3 = 0;
                while (n3 < 4) {
                    SpellDefinition spellDefinition = spellDefinitions[n3];
                    arrayList.add(spellDefinition);
                    ++n3;
                }
                if (magicPlayer.botWeaponItemId == 4675) {
                    arrayList = new ArrayList<SpellDefinition>();
                    spellDefinitions = BotCombatLoadoutTables.ancientCombatSpellProgression;
                    n3 = 0;
                    while (n3 < 16) {
                        SpellDefinition spellDefinition = spellDefinitions[n3];
                        arrayList.add(spellDefinition);
                        ++n3;
                    }
                }
            }
            int n4 = -1;
            n3 = 0;
            while (n3 < arrayList.size()) {
                SpellDefinition spellDefinition = arrayList.get(n3);
                if (spellDefinition.getRequiredLevel() > magicPlayer.getSkillManager().getCurrentLevels()[6]) break;
                if (magicPlayer.getSkillManager().getCurrentLevels()[6] >= spellDefinition.getRequiredLevel() && BotCombatHelper.hasRunesForSpell(magicPlayer, spellDefinition)) {
                    n4 = n3;
                }
                ++n3;
            }
            if (n4 != -1) {
                magicPlayer.setAutocastSpell(arrayList.get(n4));
            }
            if (!ServerSettings.freeToPlayWorld) {
                if (SpellDefinition.ENTANGLE.getRequiredLevel() <= player2.getSkillManager().getCurrentLevels()[6] && BotCombatHelper.hasRunesForSpell(player2, SpellDefinition.ENTANGLE)) {
                    player2.botCombatSpell = SpellDefinition.ENTANGLE;
                } else if (SpellDefinition.SNARE.getRequiredLevel() <= player2.getSkillManager().getCurrentLevels()[6] && BotCombatHelper.hasRunesForSpell(player2, SpellDefinition.SNARE)) {
                    player2.botCombatSpell = SpellDefinition.SNARE;
                } else if (SpellDefinition.BIND.getRequiredLevel() <= player2.getSkillManager().getCurrentLevels()[6] && BotCombatHelper.hasRunesForSpell(player2, SpellDefinition.BIND)) {
                    player2.botCombatSpell = SpellDefinition.BIND;
                }
            } else if (SpellDefinition.BIND.getRequiredLevel() <= player2.getSkillManager().getCurrentLevels()[6] && BotCombatHelper.hasRunesForSpell(player2, SpellDefinition.BIND)) {
                player2.botCombatSpell = SpellDefinition.BIND;
            }
            if (player2.getAutocastSpell() == null) {
                System.out.print("Warning! playerBot detected as mage, but no autospell set!");
                player2.packetSender.sendGameMessage("Warning! playerBot detected as mage, but no autospell set!");
            }
        }
        player2 = player;
        object2 = new BotGroupCombatTickTask(1, player2);
        World.getTaskScheduler().schedule((TickTask)object2);
    }

    public static void selectCombatStyleFromStats(Player player, boolean n) {
        int n2 = (player.getSkillManager().getCurrentLevels()[0] + player.getSkillManager().getCurrentLevels()[2]) / 2;
        int n3 = player.getSkillManager().getCurrentLevels()[6];
        int n4 = player.getSkillManager().getCurrentLevels()[4];
        if (n4 > n2 && n4 > n3) {
            player.botCombatStyle = 1;
            return;
        }
        if (n3 > n2 && n3 > n4) {
            player.botCombatStyle = 2;
            return;
        }
        if (n4 == n2 && n3 == n2) {
            int n5 = GameUtil.randomInt(3);
            if (n5 == 0) {
                player.botCombatStyle = 1;
                return;
            }
            if (n5 == 1) {
                player.botCombatStyle = 2;
                return;
            }
            if (n5 == 2) {
                player.botCombatStyle = 0;
                return;
            }
        } else {
            player.botCombatStyle = 0;
            if (!n && GameUtil.randomInt(5) == 0 && player.getSkillManager().getCurrentLevels()[0] >= 40 && player.getSkillManager().getCurrentLevels()[1] >= 40 && n4 >= 40) {
                player.botCombatStyle = 7;
            }
        }
    }

    public static void equipRandomCape(Player player) {
        int n = !BotCombatHelper.isFreeToPlayWorld() && GameUtil.randomInt(2) == 0 && ItemDefinition.isDefined(4413) ? BotCombatLoadoutTables.castleWarsCapeIds[GameUtil.randomInt(55)] : BotCombatHelper.selectBotLoadoutItemId(player, BotCombatLoadoutTables.capeIds, BotCombatLoadoutTables.teamCapeIds, true);
        player.getEquipmentManager().getContainer().setItem(1, new ItemStack(n));
    }

    public static void equipGlovesAndBoots(Player player) {
        int n = 1061;
        int n2 = 1059;
        if (!BotCombatHelper.isFreeToPlayWorld()) {
            int[] nArray = BotCombatHelper.filterEquippableMemberLoadoutItems(player, null, BotCombatLoadoutTables.coloredMageBootIds);
            int[] nArray2 = BotCombatHelper.filterEquippableMemberLoadoutItems(player, null, BotCombatLoadoutTables.coloredMageGloveIds);
            if (nArray2.length > 0) {
                n2 = GameUtil.randomInt(nArray2.length);
                n = nArray[n2];
                n2 = nArray2[n2];
            }
        }
        player.getEquipmentManager().getContainer().setItem(9, new ItemStack(n2));
        player.getEquipmentManager().getContainer().setItem(10, new ItemStack(n));
    }

    private static void addCombatSupplies(Player player) {
        int n;
        if (player.getSkillManager().getCurrentLevels()[3] < 25) {
            n = GameUtil.randomInt(2) == 0 ? 333 : 329;
        } else if (player.getSkillManager().getCurrentLevels()[3] >= 25 && player.getSkillManager().getCurrentLevels()[3] < 40) {
            n = GameUtil.randomInt(2) == 0 ? 361 : 379;
        } else {
            int n2 = n = GameUtil.randomInt(2) == 0 ? 379 : 373;
        }
        if (!BotCombatHelper.isFreeToPlayWorld()) {
            if (GameUtil.randomInt(2) == 0) {
                if (player.getSkillManager().getCurrentLevels()[3] >= 60) {
                    n = 385;
                }
            } else if (GameUtil.randomInt(3) == 0 && player.getSkillManager().getCurrentLevels()[3] >= 80) {
                n = 391;
            }
        }
        player.getInventoryManager().addItem(new ItemStack(n, 15));
        player.botFoodItemId = n;
        if (GameUtil.randomInt(100) != 0) {
            if (player.getSpellbook() == Spellbook.MODERN) {
                if (SpellDefinition.VARROCK_TELEPORT.getRequiredLevel() <= player.getSkillManager().getCurrentLevels()[6]) {
                    BotCombatHelper.grantBotSpellRunes(player, SpellDefinition.VARROCK_TELEPORT, 1);
                }
            } else if (player.getSpellbook() == Spellbook.ANCIENT && SpellDefinition.PADDEWWA_TELEPORT.getRequiredLevel() <= player.getSkillManager().getCurrentLevels()[6]) {
                BotCombatHelper.grantBotSpellRunes(player, SpellDefinition.PADDEWWA_TELEPORT, 1);
            }
        }
        if (!BotCombatHelper.isFreeToPlayWorld() && player.getCombatLevel() >= 25 && GameUtil.randomInt(2) == 0) {
            player.getInventoryManager().addItem(new ItemStack(2446, 1));
            player.botAntipoisonAvailable = true;
        }
    }

    private static int selectSpecialAttackWeapon(Player player) {
        int n;
        if (player.botCombatStyle == 9 || player.botCombatStyle == 10) {
            n = 1231;
        } else if (player.botCombatStyle == 8) {
            n = player.getEquipmentManager().canEquipItem(4153) ? 4153 : 6528;
        } else {
            int n2;
            n = BotCombatHelper.selectBestBotLoadoutItemId(player, BotCombatLoadoutTables.twoHandedSwordIds, null);
            if (!BotCombatHelper.isFreeToPlayWorld() && GameUtil.randomInt(2) == 0 && (n2 = BotCombatHelper.selectBotLoadoutItemId(player, null, BotCombatLoadoutTables.specialMeleeWeaponIds, true)) != -1) {
                n = n2;
            }
        }
        player.botSpecialWeaponItemId = n;
        player.botSpecialCombatStyle = 0;
        SpecialAttackDefinition specialAttackDefinition = SpecialAttackDefinition.forItem(new ItemStack(player.botSpecialWeaponItemId));
        if (specialAttackDefinition != null) {
            player.botSpecialAttackEnergyCost = specialAttackDefinition.getEnergyCost();
        }
        return player.botSpecialWeaponItemId;
    }

    public static void prepareMeleeLoadout(Player player) {
        BotCombatLoadoutManager.prepareMeleeLoadout(player, false);
    }

    public static void prepareMeleeLoadout(Player player, boolean bl) {
        int n;
        int n2;
        int n3;
        int n4;
        boolean bl2;
        int n5;
        block40: {
            int n6 = GameUtil.randomInt(bl ? 3 : 4);
            n5 = 0;
            bl2 = false;
            if (n6 == 0) {
                n5 = BotCombatHelper.selectBestBotLoadoutItemId(player, BotCombatLoadoutTables.longswordIds, BotCombatLoadoutTables.dragonLongswordIds);
            } else if (n6 == 1) {
                n5 = BotCombatHelper.selectBestBotLoadoutItemId(player, BotCombatLoadoutTables.scimitarIds, !ItemDefinition.isDefined(4587) ? null : BotCombatLoadoutTables.dragonScimitarIds);
            } else if (n6 == 2) {
                n5 = BotCombatHelper.selectBestBotLoadoutItemId(player, BotCombatLoadoutTables.battleaxeIds, null);
            } else if (n6 == 3) {
                n5 = BotCombatHelper.selectBestBotLoadoutItemId(player, BotCombatLoadoutTables.twoHandedSwordIds, null);
                bl2 = true;
            }
            player.botWeaponItemId = n5;
            player.getEquipmentManager().getContainer().setItem(3, new ItemStack(player.botWeaponItemId));
            if (player.currentBotTask == null) {
                n6 = BotCombatLoadoutManager.selectSpecialAttackWeapon(player);
                if (GameUtil.randomInt(3) == 0 && player.botWeaponItemId != n6) {
                    player.botSpecialWeaponItemId = n6;
                    player.getInventoryManager().addItem(new ItemStack(player.botSpecialWeaponItemId, 1));
                }
            }
            n6 = 0;
            n5 = 0;
            while (n5 < 7) {
                n4 = new ItemStack(BotCombatLoadoutTables.platebodyIds[n5]).getDefinition().getRequiredLevel(1);
                if (n4 > player.getSkillManager().getCurrentLevels()[1]) {
                    n6 = n5 - 1;
                    break;
                }
                n6 = n5++;
            }
            int n7 = n5 = GameUtil.randomInt(2) == 0 ? BotCombatLoadoutTables.mediumHelmetIds[n6] : BotCombatLoadoutTables.fullHelmetIds[n6];
            if (!BotCombatHelper.isFreeToPlayWorld() && GameUtil.randomInt(2) == 0 && (n4 = BotCombatHelper.selectBotLoadoutItemId(player, null, BotCombatLoadoutTables.rareMeleeHelmetIds, true)) != -1) {
                n5 = n4;
            }
            n4 = GameUtil.randomInt(2) == 0 ? BotCombatLoadoutTables.chainbodyIds[n6] : BotCombatLoadoutTables.platebodyIds[n6];
            n3 = GameUtil.randomInt(2) == 0 ? BotCombatLoadoutTables.plateskirtIds[n6] : BotCombatLoadoutTables.platelegIds[n6];
            int n8 = n2 = GameUtil.randomInt(2) == 0 ? BotCombatLoadoutTables.squareShieldIds[n6] : BotCombatLoadoutTables.kiteshieldIds[n6];
            if (player.currentBotTask == null) {
                if (GameUtil.randomInt(4) == 0) {
                    n2 = 1540;
                }
            } else if (bl) {
                n2 = 1540;
            }
            if (n6 != 2 && n6 != 4 && n6 != 5) break block40;
            int n9 = 0;
            int n10 = GameUtil.randomInt(3);
            if (n6 == 4) {
                n9 = 1;
            } else if (n6 == 5) {
                n9 = 2;
            }
            int n11 = 0;
            while (n11 < 4) {
                int n12 = n6 == 5 ? 1500 : 1000;
                n12 = GameUtil.randomInt(n12);
                if (n12 == 0) {
                    if (n11 == 0) {
                        n5 = BotCombatLoadoutTables.trimmedFullHelmetIds[n9];
                    } else if (n11 == 1) {
                        n4 = BotCombatLoadoutTables.trimmedPlatebodyIds[n9];
                    } else if (n11 == 2) {
                        n3 = GameUtil.randomInt(2) == 0 ? BotCombatLoadoutTables.trimmedPlateskirtIds[n9] : BotCombatLoadoutTables.trimmedPlatelegIds[n9];
                    } else if (n11 == 3) {
                        n2 = BotCombatLoadoutTables.trimmedKiteshieldIds[n9];
                    }
                } else if (n12 == 1) {
                    if (n11 == 0) {
                        n5 = BotCombatLoadoutTables.goldTrimmedFullHelmetIds[n9];
                    } else if (n11 == 1) {
                        n4 = BotCombatLoadoutTables.goldTrimmedPlatebodyIds[n9];
                    } else if (n11 == 2) {
                        n3 = GameUtil.randomInt(2) == 0 ? BotCombatLoadoutTables.goldTrimmedPlateskirtIds[n9] : BotCombatLoadoutTables.goldTrimmedPlatelegIds[n9];
                    } else if (n11 == 3) {
                        n2 = BotCombatLoadoutTables.goldTrimmedKiteshieldIds[n9];
                    }
                } else if (n12 == 3 && n6 == 5) {
                    if (n11 == 0) {
                        n5 = BotCombatLoadoutTables.godFullHelmetIds[n10];
                    } else if (n11 == 1) {
                        n4 = BotCombatLoadoutTables.godPlatebodyIds[n10];
                    } else if (n11 == 2) {
                        n3 = GameUtil.randomInt(2) == 0 ? BotCombatLoadoutTables.godPlateskirtIds[n10] : BotCombatLoadoutTables.godPlatelegIds[n10];
                    } else if (n11 == 3) {
                        n2 = BotCombatLoadoutTables.godKiteshieldIds[n10];
                    }
                }
                ++n11;
            }
        }
        player.getEquipmentManager().getContainer().setItem(0, new ItemStack(n5));
        player.getEquipmentManager().getContainer().setItem(4, new ItemStack(n4));
        player.getEquipmentManager().getContainer().setItem(7, new ItemStack(n3));
        if (!bl2) {
            player.botShieldItemId = n2;
            player.getEquipmentManager().getContainer().setItem(5, new ItemStack(player.botShieldItemId));
        }
        if (player.getCombatLevel() <= 15) {
            n = 1478;
        } else if (player.getCombatLevel() <= 40 && player.getCombatLevel() > 15) {
            n = GameUtil.randomInt(2) == 0 ? 1725 : 1729;
        } else {
            n = 1731;
        }
        if (!BotCombatHelper.isFreeToPlayWorld() && player.getCombatLevel() >= 60 && GameUtil.randomInt(2) == 0) {
            n = 1712;
        }
        player.getEquipmentManager().getContainer().setItem(2, new ItemStack(n));
    }

    public static void prepareRangedLoadout(Player player) {
        int n;
        int n2;
        int n3 = BotCombatHelper.selectBestBotLoadoutItemId(player, BotCombatLoadoutTables.basicRangedHeadIds, (int[])(ItemDefinition.isDefined(3749) ? BotCombatLoadoutTables.rareRangedHeadIds : null));
        int n4 = BotCombatHelper.selectBestBotLoadoutItemId(player, BotCombatLoadoutTables.basicRangedBodyIds, BotCombatLoadoutTables.dragonhideBodyIds);
        int n5 = BotCombatHelper.selectBestBotLoadoutItemId(player, BotCombatLoadoutTables.basicRangedLegIds, BotCombatLoadoutTables.dragonhideChapsIds);
        int n6 = BotCombatHelper.selectBestBotLoadoutItemId(player, BotCombatLoadoutTables.basicRangedVambraceIds, BotCombatLoadoutTables.dragonhideVambraceIds);
        if (ServerSettings.cacheVersion >= 306) {
            n2 = BotCombatHelper.selectBestBotLoadoutItemId(player, BotCombatLoadoutTables.post306BowIds, BotCombatLoadoutTables.post306HighTierBowIds);
            n = BotCombatHelper.selectBestBotLoadoutItemId(player, BotCombatLoadoutTables.post306ArrowIds, BotCombatLoadoutTables.post306HighTierArrowIds);
        } else {
            n2 = BotCombatHelper.selectBestBotLoadoutItemId(player, BotCombatLoadoutTables.legacyBowIds, BotCombatLoadoutTables.legacyHighTierBowIds);
            n = BotCombatHelper.selectBestBotLoadoutItemId(player, BotCombatLoadoutTables.legacyArrowIds, BotCombatLoadoutTables.legacyHighTierArrowIds);
        }
        int n7 = 40 + GameUtil.randomInt(20);
        if (player.clanWarsBot) {
            n7 *= ClanWarsBotManager.clanWarsSupplyMultiplier;
        }
        if (player.currentBotTask != null) {
            n7 = 1000;
        }
        player.botWeaponItemId = n2;
        player.getEquipmentManager().getContainer().setItem(0, new ItemStack(n3));
        player.getEquipmentManager().getContainer().setItem(7, new ItemStack(n5));
        player.getEquipmentManager().getContainer().setItem(4, new ItemStack(n4));
        player.getEquipmentManager().getContainer().setItem(9, new ItemStack(n6));
        player.getEquipmentManager().getContainer().setItem(3, new ItemStack(player.botWeaponItemId));
        player.getEquipmentManager().getContainer().setItem(13, new ItemStack(n, n7));
        player.getEquipmentManager().getContainer().setItem(10, new ItemStack(1061));
        if (player.currentBotTask != null) {
            ItemStack[] itemStackArray;
            if (player.botTaskRequiredItems != null) {
                itemStackArray = new ItemStack[player.botTaskRequiredItems.length + 1];
                n4 = 0;
                while (n4 < itemStackArray.length - 1) {
                    itemStackArray[n4] = player.botTaskRequiredItems[n4];
                    ++n4;
                }
                itemStackArray[player.botTaskRequiredItems.length] = new ItemStack(n, n7);
            } else {
                ItemStack[] itemStackArray2 = new ItemStack[1];
                itemStackArray = itemStackArray2;
                itemStackArray2[0] = new ItemStack(n, n7);
            }
            player.getBankContainer().addToTab(new ItemStack(n, n7 * 10), 0);
            player.botTaskRequiredItems = itemStackArray;
        }
    }

    public static void prepareMagicLoadout(Player player) {
        int[] basicHeadIds = new int[]{579, 1017};
        int[] basicBodyIds = new int[]{577, 546};
        int[] basicLegIds = new int[]{1011, 548};
        int styleRollLimit = BotCombatHelper.isFreeToPlayWorld() ? 2 : 3;
        int styleRoll = GameUtil.randomInt(styleRollLimit);
        int headId;
        int bodyId;
        int legId;
        int bootsId;
        int glovesId;
        if (styleRoll == 2) {
            int[] coloredHeadIds = BotCombatHelper.filterEquippableMemberLoadoutItems(player, null, BotCombatLoadoutTables.coloredMageHeadIds);
            int[] coloredBodyIds = BotCombatHelper.filterEquippableMemberLoadoutItems(player, null, BotCombatLoadoutTables.coloredMageBodyIds);
            int[] coloredLegIds = BotCombatHelper.filterEquippableMemberLoadoutItems(player, null, BotCombatLoadoutTables.coloredMageLegIds);
            int[] coloredBootIds = BotCombatHelper.filterEquippableMemberLoadoutItems(player, null, BotCombatLoadoutTables.coloredMageBootIds);
            int[] coloredGloveIds = BotCombatHelper.filterEquippableMemberLoadoutItems(player, null, BotCombatLoadoutTables.coloredMageGloveIds);
            if (coloredGloveIds.length > 0) {
                int colorIndex = GameUtil.randomInt(coloredGloveIds.length);
                headId = coloredHeadIds[colorIndex];
                bodyId = coloredBodyIds[colorIndex];
                legId = coloredLegIds[colorIndex];
                bootsId = coloredBootIds[colorIndex];
                glovesId = coloredGloveIds[colorIndex];
            } else {
                int basicIndex = GameUtil.randomInt(2);
                headId = basicHeadIds[basicIndex];
                bodyId = basicBodyIds[basicIndex];
                legId = basicLegIds[basicIndex];
                bootsId = 1061;
                glovesId = 1059;
            }
        } else {
            headId = basicHeadIds[styleRoll];
            bodyId = basicBodyIds[styleRoll];
            legId = basicLegIds[styleRoll];
            bootsId = 1061;
            glovesId = 1059;
        }
        boolean skipRuneGrant = player.tradeAdvertMode != -1 || player.dropPartyLeader || player.dropPartyFollower;
        int weaponId = 1381;
        boolean ancientSpellbook = false;
        ArrayList<SpellDefinition> spellProgression = new ArrayList<SpellDefinition>();
        SpellDefinition[] standardSpells = BotCombatLoadoutTables.standardCombatSpellProgression;
        int i = 0;
        while (i < 12) {
            spellProgression.add(standardSpells[i]);
            ++i;
        }
        if (!BotCombatHelper.isFreeToPlayWorld()) {
            SpellDefinition[] waveSpells = BotCombatLoadoutTables.standardWaveSpellProgression;
            i = 0;
            while (i < 4) {
                spellProgression.add(waveSpells[i]);
                ++i;
            }
            if (player.getSkillManager().getCurrentLevels()[6] >= 50 && ItemDefinition.isDefined(4675) && GameUtil.randomInt(3) == 0) {
                ancientSpellbook = true;
                player.packetSender.setSidebarInterface(6, 12855);
                player.setSpellbook(Spellbook.ANCIENT);
                spellProgression = new ArrayList<SpellDefinition>();
                SpellDefinition[] ancientSpells = BotCombatLoadoutTables.ancientCombatSpellProgression;
                i = 0;
                while (i < 16) {
                    spellProgression.add(ancientSpells[i]);
                    ++i;
                }
            }
        }
        int selectedSpellIndex = 0;
        i = 0;
        while (i < spellProgression.size()) {
            SpellDefinition candidateSpell = spellProgression.get(i);
            if (candidateSpell.getRequiredLevel() > player.getSkillManager().getCurrentLevels()[6]) {
                selectedSpellIndex = i - 1;
                break;
            }
            selectedSpellIndex = i;
            ++i;
        }
        SpellDefinition selectedSpell = spellProgression.get(selectedSpellIndex);
        if (ancientSpellbook) {
            weaponId = 4675;
        } else if (selectedSpell == SpellDefinition.WIND_STRIKE || selectedSpell == SpellDefinition.WIND_BOLT || selectedSpell == SpellDefinition.WIND_BLAST || selectedSpell == SpellDefinition.WIND_WAVE) {
            weaponId = 1381;
        } else if (selectedSpell == SpellDefinition.WATER_STRIKE || selectedSpell == SpellDefinition.WATER_BOLT || selectedSpell == SpellDefinition.WATER_BLAST || selectedSpell == SpellDefinition.WATER_WAVE) {
            weaponId = 1383;
        } else if (selectedSpell == SpellDefinition.EARTH_STRIKE || selectedSpell == SpellDefinition.EARTH_BOLT || selectedSpell == SpellDefinition.EARTH_BLAST || selectedSpell == SpellDefinition.EARTH_WAVE) {
            weaponId = 1385;
        } else if (selectedSpell == SpellDefinition.FIRE_STRIKE || selectedSpell == SpellDefinition.FIRE_BOLT || selectedSpell == SpellDefinition.FIRE_BLAST || selectedSpell == SpellDefinition.FIRE_WAVE) {
            weaponId = 1387;
        }
        player.botWeaponItemId = weaponId;
        player.getEquipmentManager().getContainer().setItem(3, new ItemStack(player.botWeaponItemId));
        if (!skipRuneGrant) {
            int runeAmount = 30 + GameUtil.randomInt(30);
            if (player.clanWarsBot) {
                runeAmount *= ClanWarsBotManager.clanWarsSupplyMultiplier;
            }
            if (player.currentBotTask != null) {
                runeAmount = 1000;
            }
            BotCombatHelper.grantBotSpellRunes(player, selectedSpell, runeAmount);
            if (!ancientSpellbook && player.currentBotTask == null) {
                if (!BotCombatHelper.isFreeToPlayWorld() && GameUtil.randomInt(3) == 0 && 12445 < InterfaceDefinition.interfaceCount && SpellDefinition.TELE_BLOCK.getRequiredLevel() <= player.getSkillManager().getCurrentLevels()[6]) {
                    runeAmount = GameUtil.randomInt(11);
                    BotCombatHelper.grantBotSpellRunes(player, SpellDefinition.TELE_BLOCK, runeAmount);
                }
                if (GameUtil.randomInt(3) == 0) {
                    runeAmount = GameUtil.randomInt(11);
                    if (!BotCombatHelper.isFreeToPlayWorld()) {
                        if (SpellDefinition.ENTANGLE.getRequiredLevel() <= player.getSkillManager().getCurrentLevels()[6]) {
                            BotCombatHelper.grantBotSpellRunes(player, SpellDefinition.ENTANGLE, runeAmount);
                            player.botCombatSpell = SpellDefinition.ENTANGLE;
                        } else if (SpellDefinition.SNARE.getRequiredLevel() <= player.getSkillManager().getCurrentLevels()[6]) {
                            BotCombatHelper.grantBotSpellRunes(player, SpellDefinition.SNARE, runeAmount);
                            player.botCombatSpell = SpellDefinition.SNARE;
                        } else if (SpellDefinition.BIND.getRequiredLevel() <= player.getSkillManager().getCurrentLevels()[6]) {
                            BotCombatHelper.grantBotSpellRunes(player, SpellDefinition.BIND, runeAmount);
                            player.botCombatSpell = SpellDefinition.BIND;
                        }
                    } else if (SpellDefinition.BIND.getRequiredLevel() <= player.getSkillManager().getCurrentLevels()[6]) {
                        BotCombatHelper.grantBotSpellRunes(player, SpellDefinition.BIND, runeAmount);
                        player.botCombatSpell = SpellDefinition.BIND;
                    }
                }
            }
            player.setAutocastSpell(selectedSpell);
        }
        if (!BotCombatHelper.isFreeToPlayWorld() && player.getSkillManager().getCurrentLevels()[6] >= 20 && player.getSkillManager().getCurrentLevels()[1] >= 20 && ItemDefinition.isDefined(3755)) {
            if (player.getSkillManager().getCurrentLevels()[1] >= 45) {
                headId = 3755;
            }
            if (ItemDefinition.isDefined(4097)) {
                headId = BotCombatHelper.selectBestBotLoadoutItemId(player, null, BotCombatLoadoutTables.mageHeadIds);
                bodyId = BotCombatHelper.selectBestBotLoadoutItemId(player, null, BotCombatLoadoutTables.mageBodyIds);
                legId = BotCombatHelper.selectBestBotLoadoutItemId(player, null, BotCombatLoadoutTables.mageLegIds);
                glovesId = BotCombatHelper.selectBestBotLoadoutItemId(player, null, BotCombatLoadoutTables.mageGloveIds);
                bootsId = BotCombatHelper.selectBestBotLoadoutItemId(player, null, BotCombatLoadoutTables.mageBootIds);
            }
        }
        player.getEquipmentManager().getContainer().setItem(0, new ItemStack(headId));
        player.getEquipmentManager().getContainer().setItem(4, new ItemStack(bodyId));
        player.getEquipmentManager().getContainer().setItem(7, new ItemStack(legId));
        int amuletId = 1727;
        player.botShieldItemId = 1540;
        if (player.botCombatStyle == 2 && GameUtil.randomInt(2) == 0) {
            amuletId = 1731;
        }
        if (!BotCombatHelper.isFreeToPlayWorld()) {
            player.botShieldItemId = GameUtil.randomInt(3) == 0 ? 2890 : 1540;
            if (player.getCombatLevel() >= 60 && GameUtil.randomInt(2) == 0) {
                amuletId = 1712;
            }
        }
        player.getEquipmentManager().getContainer().setItem(5, new ItemStack(player.botShieldItemId));
        player.getEquipmentManager().getContainer().setItem(2, new ItemStack(amuletId));
        player.getEquipmentManager().getContainer().setItem(9, new ItemStack(glovesId));
        player.getEquipmentManager().getContainer().setItem(10, new ItemStack(bootsId));
    }

    public static void prepareCombatLoadout(Player player, boolean bl) {
        int n = player.botCombatStyle;
        player.getInventoryManager().getContainer().clear();
        player.getEquipmentManager().getContainer().clear();
        boolean bl2 = true;
        if (bl) {
            bl2 = false;
        }
        switch (n) {
            case 4: {
                int amuletId = 1725;
                player.botActiveCombatStyle = player.botPrimaryCombatStyle = 0;
                int[] nArray = new int[]{1303, 1333, 1373};
                player.botWeaponItemId = nArray[GameUtil.randomInt(3)];
                player.getEquipmentManager().getContainer().setItem(3, new ItemStack(player.botWeaponItemId));
                if (player.getSkillManager().getCurrentLevels()[1] >= 10) {
                    player.getEquipmentManager().getContainer().setItem(0, new ItemStack(1165));
                    player.getEquipmentManager().getContainer().setItem(4, new ItemStack(1125));
                    player.getEquipmentManager().getContainer().setItem(7, GameUtil.randomInt(4) == 0 ? new ItemStack(1089) : new ItemStack(1077));
                    player.botShieldItemId = GameUtil.randomInt(2) == 0 ? 1195 : 1540;
                } else {
                    player.getEquipmentManager().getContainer().setItem(0, new ItemStack(1153));
                    player.getEquipmentManager().getContainer().setItem(4, new ItemStack(1115));
                    player.getEquipmentManager().getContainer().setItem(7, GameUtil.randomInt(4) == 0 ? new ItemStack(1081) : new ItemStack(1067));
                    player.botShieldItemId = GameUtil.randomInt(2) == 0 ? 1191 : 1540;
                }
                player.getEquipmentManager().getContainer().setItem(5, new ItemStack(player.botShieldItemId));
                if (player.botCombatStyle == 2 && GameUtil.randomInt(2) == 0) {
                    amuletId = 1731;
                }
                if (!BotCombatHelper.isFreeToPlayWorld() && player.getCombatLevel() >= 60 && GameUtil.randomInt(2) == 0) {
                    amuletId = 1712;
                }
                player.getEquipmentManager().getContainer().setItem(2, new ItemStack(amuletId));
                BotCombatLoadoutManager.equipGlovesAndBoots(player);
                player.getInventoryManager().addItem(new ItemStack(113, 1));
                if (GameUtil.randomInt(3) != 0) break;
                player.getInventoryManager().addItem(new ItemStack(BotCombatLoadoutManager.selectSpecialAttackWeapon(player), 1));
                break;
            }
            case 9: {
                player.botActiveCombatStyle = player.botPrimaryCombatStyle = 0;
                int[] nArray = new int[]{1231, 4587};
                player.botWeaponItemId = nArray[GameUtil.randomInt(2)];
                player.getEquipmentManager().getContainer().setItem(3, new ItemStack(player.botWeaponItemId));
                player.getEquipmentManager().getContainer().setItem(0, new ItemStack(6109));
                player.getEquipmentManager().getContainer().setItem(4, new ItemStack(6107));
                player.getEquipmentManager().getContainer().setItem(7, new ItemStack(6108));
                player.botShieldItemId = 3842;
                player.getEquipmentManager().getContainer().setItem(5, new ItemStack(player.botShieldItemId));
                player.getEquipmentManager().getContainer().setItem(2, new ItemStack(1725));
                player.getEquipmentManager().getContainer().setItem(10, new ItemStack(3105));
                player.getEquipmentManager().getContainer().setItem(1, new ItemStack(6111));
                player.getEquipmentManager().getContainer().setItem(12, new ItemStack(2550));
                player.getInventoryManager().addItem(new ItemStack(113, 1));
                if (GameUtil.randomInt(3) == 0 && player.botWeaponItemId != 1231) {
                    player.getInventoryManager().addItem(new ItemStack(BotCombatLoadoutManager.selectSpecialAttackWeapon(player), 1));
                }
                bl2 = false;
                break;
            }
            case 10: {
                player.botActiveCombatStyle = player.botPrimaryCombatStyle = 0;
                player.botWeaponItemId = 4151;
                player.getEquipmentManager().getContainer().setItem(3, new ItemStack(player.botWeaponItemId));
                player.getEquipmentManager().getContainer().setItem(4, new ItemStack(544));
                player.getEquipmentManager().getContainer().setItem(7, new ItemStack(542));
                player.getEquipmentManager().getContainer().setItem(2, new ItemStack(1731));
                player.getEquipmentManager().getContainer().setItem(10, new ItemStack(3105));
                player.getEquipmentManager().getContainer().setItem(1, new ItemStack(4411));
                player.getEquipmentManager().getContainer().setItem(12, new ItemStack(2550));
                player.getEquipmentManager().getContainer().setItem(9, new ItemStack(2912));
                player.getInventoryManager().addItem(new ItemStack(113, 1));
                if (GameUtil.randomInt(3) == 0) {
                    player.getInventoryManager().addItem(new ItemStack(BotCombatLoadoutManager.selectSpecialAttackWeapon(player), 1));
                }
                bl2 = false;
                break;
            }
            case 11: {
                player.botActiveCombatStyle = player.botPrimaryCombatStyle = 0;
                player.botWeaponItemId = 1231;
                player.getEquipmentManager().getContainer().setItem(3, new ItemStack(player.botWeaponItemId));
                player.getEquipmentManager().getContainer().setItem(4, new ItemStack(577));
                player.getEquipmentManager().getContainer().setItem(7, new ItemStack(1033));
                player.getEquipmentManager().getContainer().setItem(2, new ItemStack(1731));
                player.getEquipmentManager().getContainer().setItem(10, new ItemStack(3105));
                player.getEquipmentManager().getContainer().setItem(1, new ItemStack(4365));
                player.botShieldItemId = 3842;
                player.getEquipmentManager().getContainer().setItem(5, new ItemStack(player.botShieldItemId));
                player.getInventoryManager().addItem(new ItemStack(113, 1));
                bl2 = false;
                break;
            }
            case 0: {
                player.botActiveCombatStyle = player.botPrimaryCombatStyle = 0;
                Player n6 = player;
                BotCombatLoadoutManager.prepareMeleeLoadout(n6, false);
                BotCombatLoadoutManager.equipGlovesAndBoots(player);
                player.getInventoryManager().addItem(new ItemStack(113, 1));
                break;
            }
            case 7: {
                int n2 = 1731;
                player.botActiveCombatStyle = player.botPrimaryCombatStyle = 0;
                int[] player3 = new int[]{1303, 1333, 1373};
                player.botWeaponItemId = player3[GameUtil.randomInt(3)];
                n = 1135;
                int n3 = 1099;
                player.botShieldItemId = 1540;
                player.getEquipmentManager().getContainer().setItem(5, new ItemStack(player.botShieldItemId));
                int n4 = 1065;
                int n5 = 1061;
                if (!BotCombatHelper.isFreeToPlayWorld()) {
                    if (player.getCombatLevel() >= 60 && GameUtil.randomInt(2) == 0) {
                        n2 = 1712;
                    }
                    if (player.getEquipmentManager().canEquipItem(4587) && ItemDefinition.isDefined(4587)) {
                        player.botWeaponItemId = 4587;
                    }
                    if (player.getEquipmentManager().canEquipItem(2503)) {
                        n = 2503;
                        n3 = 2497;
                        n4 = 2491;
                    }
                    if (player.getEquipmentManager().canEquipItem(3751)) {
                        player.getEquipmentManager().getContainer().setItem(0, new ItemStack(3751));
                    }
                    player.getEquipmentManager().getContainer().setItem(12, new ItemStack(2550));
                    player.getEquipmentManager().getContainer().setItem(1, new ItemStack(2414));
                    n5 = 3105;
                    bl2 = false;
                }
                player.getEquipmentManager().getContainer().setItem(3, new ItemStack(player.botWeaponItemId));
                player.getEquipmentManager().getContainer().setItem(2, new ItemStack(n2));
                player.getEquipmentManager().getContainer().setItem(9, new ItemStack(n4));
                player.getEquipmentManager().getContainer().setItem(10, new ItemStack(n5));
                player.getEquipmentManager().getContainer().setItem(4, new ItemStack(n));
                player.getEquipmentManager().getContainer().setItem(7, new ItemStack(n3));
                player.getInventoryManager().addItem(new ItemStack(113, 1));
                if (GameUtil.randomInt(3) != 0) break;
                player.getInventoryManager().addItem(new ItemStack(BotCombatLoadoutManager.selectSpecialAttackWeapon(player), 1));
                break;
            }
            case 2: 
            case 6: {
                player.botActiveCombatStyle = player.botPrimaryCombatStyle = BotPvpCombatHandler.MAGIC_COMBAT_STYLE;
                BotCombatLoadoutManager.prepareMagicLoadout(player);
                break;
            }
            case 8: {
                player.botActiveCombatStyle = player.botPrimaryCombatStyle = BotPvpCombatHandler.RANGED_COMBAT_STYLE;
                Player player2 = player;
                n = GameUtil.randomInt(2) == 0 ? 811 : 868;
                int n7 = BotCombatHelper.selectBestBotLoadoutItemId(player2, BotCombatLoadoutTables.basicRangedLegIds, BotCombatLoadoutTables.dragonhideChapsIds);
                int n8 = BotCombatHelper.selectBestBotLoadoutItemId(player2, BotCombatLoadoutTables.basicRangedVambraceIds, BotCombatLoadoutTables.dragonhideVambraceIds);
                int n9 = 40 + GameUtil.randomInt(20);
                player2.botWeaponItemId = n;
                player2.getEquipmentManager().getContainer().setItem(0, new ItemStack(2910));
                player2.getEquipmentManager().getContainer().setItem(7, new ItemStack(n7));
                player2.getEquipmentManager().getContainer().setItem(4, new ItemStack(1129));
                player2.getEquipmentManager().getContainer().setItem(9, new ItemStack(n8));
                player2.getEquipmentManager().getContainer().setItem(3, new ItemStack(player2.botWeaponItemId, n9));
                player2.getEquipmentManager().getContainer().setItem(10, new ItemStack(1061));
                player.getEquipmentManager().getContainer().setItem(2, new ItemStack(1725));
                if (GameUtil.randomInt(3) != 0) break;
                player.getInventoryManager().addItem(new ItemStack(BotCombatLoadoutManager.selectSpecialAttackWeapon(player), 1));
                break;
            }
            case 1: 
            case 5: {
                player.botActiveCombatStyle = player.botPrimaryCombatStyle = BotPvpCombatHandler.RANGED_COMBAT_STYLE;
                BotCombatLoadoutManager.prepareRangedLoadout(player);
                int n10 = player.getSkillManager().getCurrentLevels()[4] >= 40 ? 1731 : (GameUtil.randomInt(3) == 0 ? 1478 : 1729);
                if (!BotCombatHelper.isFreeToPlayWorld() && player.getCombatLevel() >= 60 && GameUtil.randomInt(2) == 0) {
                    n10 = 1712;
                }
                player.getEquipmentManager().getContainer().setItem(2, new ItemStack(n10));
            }
        }
        if (bl2) {
            BotCombatLoadoutManager.equipRandomCape(player);
        }
        BotCombatLoadoutManager.addCombatSupplies(player);
        player.getInventoryManager().refresh();
        player.getEquipmentManager().refresh();
    }
}
