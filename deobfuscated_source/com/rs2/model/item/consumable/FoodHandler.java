/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.item.consumable;

import com.rs2.model.gameplay.duel.DuelRule;
import com.rs2.model.item.ItemService;
import com.rs2.model.item.ItemStack;
import com.rs2.model.item.consumable.FoodDefinition;
import com.rs2.model.item.consumable.FoodHealMessageEvent;
import com.rs2.model.item.consumable.PotionDefinition;
import com.rs2.model.item.consumable.PotionEffectMode;
import com.rs2.model.item.consumable.PotionHandler;
import com.rs2.model.player.Player;
import com.rs2.model.task.CycleEventHandler;
import com.rs2.util.ByteArrayReader;
import com.rs2.util.FileUtil;
import com.rs2.util.GameplayTrace;
import com.rs2.util.GameUtil;

public class FoodHandler {
    Player player;

    public FoodHandler(Player player) {
        this.player = player;
    }

    public final boolean eatFood(int n, int n2) {
        int n3;
        if (this.player.isDead()) {
            if (GameplayTrace.enabled()) {
                GameplayTrace.log("food eat ignored-dead player=" + GameplayTrace.describe(this.player) + " itemId=" + n + " slot=" + n2);
            }
            return false;
        }
        if (DuelRule.NO_FOOD.isEnabledFor(this.player)) {
            Player player = this.player;
            player.packetSender.sendGameMessage("Usage of foods have been disabled during this fight!");
            if (GameplayTrace.enabled()) {
                GameplayTrace.log("food eat blocked-duel player=" + GameplayTrace.describe(this.player) + " itemId=" + n + " slot=" + n2);
            }
            return true;
        }
        FoodDefinition foodDefinition = FoodDefinition.forItemId(n);
        if (foodDefinition == null) {
            if (GameplayTrace.enabled()) {
                GameplayTrace.log("food eat not-food player=" + GameplayTrace.describe(this.player) + " itemId=" + n + " slot=" + n2);
            }
            return false;
        }
        int n4 = n3 = foodDefinition.getReplacementItemId() != -1 ? 600 : 1800;
        if (this.player.getSkillManager().tryStartActionDelay(n3) && this.player.getSkillManager().getCurrentLevels()[3] > 0) {
            int beforeHp = this.player.getSkillManager().getCurrentLevels()[3];
            int baseHp = this.player.getSkillManager().getBaseLevel(3);
            this.player.getUpdateState().setAnimation(829);
            if (!this.player.getInventoryManager().removeItemFromSlot(new ItemStack(n, 1), n2)) {
                this.player.getInventoryManager().removeItem(new ItemStack(n, 1));
            }
            ItemService.getInstance();
            String string = ItemService.getItemName(n);
            Player player = this.player;
            player.packetSender.sendGameMessage("You eat the " + string.toLowerCase() + ".");
            player = this.player;
            player.packetSender.sendSoundEffect(317, 1, 0);
            int n5 = foodDefinition.getHealAmount();
            if (n == 1971) {
                int n6;
                FoodHandler foodHandler = this;
                if (foodHandler.player.getSkillManager().getCurrentLevels()[3] >= foodHandler.player.getSkillManager().getBaseLevel(3)) {
                    n6 = 0;
                } else {
                    int n7 = GameUtil.randomInclusive(9);
                    if (n7 == 0) {
                        player = foodHandler.player;
                        player.packetSender.sendGameMessage("Wow, that was an amazing kebab! You feel really invigorated.");
                        n6 = 30;
                    } else if (n7 == 1) {
                        player = foodHandler.player;
                        player.packetSender.sendGameMessage("That kebab didn't seem to do a lot.");
                        n6 = 0;
                    } else if (n7 < 5) {
                        player = foodHandler.player;
                        player.packetSender.sendGameMessage("That was a good kebab. You feel a lot better.");
                        n6 = GameUtil.randomInclusive(10) + 10;
                    } else {
                        player = foodHandler.player;
                        player.packetSender.sendGameMessage("It restores some life points.");
                        n6 = n5 = foodHandler.player.getSkillManager().getBaseLevel(3) / 10;
                    }
                }
            }
            if (foodDefinition.getReplacementItemId() != -1) {
                this.player.getInventoryManager().addItem(new ItemStack(foodDefinition.getReplacementItemId()));
            }
            this.player.heal(n5);
            if (GameplayTrace.enabled()) {
                GameplayTrace.log("food eat healed player=" + GameplayTrace.describe(this.player) + " itemId=" + n + " item=" + string + " slot=" + n2 + " beforeHp=" + beforeHp + " afterHp=" + this.player.getSkillManager().getCurrentLevels()[3] + " baseHp=" + baseHp + " healAmount=" + n5 + " replacementItemId=" + foodDefinition.getReplacementItemId());
            }
            this.player.nextActionSequence();
            this.player.getAttackDelayTimer().setDelayTicks(this.player.getAttackDelayTimer().getDelayTicks() + 2);
            if (n != 10476 && n != 1971) {
                if (GameplayTrace.enabled()) {
                    GameplayTrace.log("food heal-message scheduled player=" + GameplayTrace.describe(this.player) + " itemId=" + n + " delayTicks=2");
                }
                CycleEventHandler.getInstance().schedule(this.player, new FoodHealMessageEvent(this), 2);
            }
        } else if (GameplayTrace.enabled()) {
            GameplayTrace.log("food eat action-delay-blocked player=" + GameplayTrace.describe(this.player) + " itemId=" + n + " slot=" + n2 + " currentHp=" + this.player.getSkillManager().getCurrentLevels()[3] + " delayMillis=" + n3);
        }
        return true;
    }

    public static void loadPotionDefinitions() {
        try {
            byte[] byArray = FileUtil.readBytes("./data/content/combat/potiondef.dat");
            ByteArrayReader byteArrayReader = new ByteArrayReader(byArray);
            int n = byteArrayReader.readUnsignedShort();
            int n2 = 0;
            while (n2 < n) {
                PotionDefinition potionDefinition = new PotionDefinition();
                int n3 = byteArrayReader.readUnsignedByte();
                PotionDefinition.setEffectMode(potionDefinition, PotionEffectMode.values()[n3 - 1]);
                n3 = byteArrayReader.readUnsignedByte();
                PotionDefinition.setDoseItemIds(potionDefinition, new int[n3]);
                PotionDefinition.setAntipoison(potionDefinition, false);
                int n4 = 0;
                while (n4 < n3) {
                    int n5 = byteArrayReader.readUnsignedShort();
                    if (n5 == 2446 || n5 == 2448 || n5 == 5943 || n5 == 5952) {
                        PotionDefinition.setAntipoison(potionDefinition, true);
                    }
                    PotionDefinition.getMutableDoseItemIds((PotionDefinition)potionDefinition)[n4] = n5;
                    ++n4;
                }
                n4 = PotionDefinition.getMutableDoseItemIds(potionDefinition)[0];
                ItemStack itemStack = new ItemStack(n4);
                String potionName = itemStack.getDefinition().getName();
                if (potionName.contains("(")) {
                    String[] potionNameParts = potionName.split("\\(");
                    potionName = potionNameParts[0];
                }
                PotionDefinition.setName(potionDefinition, potionName);
                n3 = byteArrayReader.readUnsignedByte();
                PotionDefinition.setSkillIds(potionDefinition, new int[n3]);
                PotionDefinition.setFlatBoosts(potionDefinition, new int[n3]);
                PotionDefinition.setPercentBoosts(potionDefinition, new double[n3]);
                n4 = 0;
                while (n4 < n3) {
                    int n6 = byteArrayReader.readUnsignedByte();
                    int n7 = byteArrayReader.readUnsignedByte();
                    double d = byteArrayReader.readUnsignedShort();
                    PotionDefinition.getMutableSkillIds((PotionDefinition)potionDefinition)[n4] = n6;
                    PotionDefinition.getMutableFlatBoosts((PotionDefinition)potionDefinition)[n4] = n7;
                    PotionDefinition.getMutablePercentBoosts((PotionDefinition)potionDefinition)[n4] = d / 100.0;
                    ++n4;
                }
                PotionHandler.getDefinitions()[n2] = potionDefinition;
                ++PotionHandler.definitionCount;
                ++n2;
            }
            return;
        }
        catch (Exception exception) {
            Exception exception2 = exception;
            exception.printStackTrace();
            return;
        }
    }
}

