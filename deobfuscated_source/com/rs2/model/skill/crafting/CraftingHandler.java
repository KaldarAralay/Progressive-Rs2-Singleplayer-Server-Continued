/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.crafting;

import com.rs2.ServerSettings;
import com.rs2.model.item.ItemDefinition;
import com.rs2.model.item.ItemStack;
import com.rs2.model.player.Player;
import com.rs2.model.skill.crafting.DramenStaffCarvingTask;
import com.rs2.model.skill.crafting.DramenStaffRecipe;
import com.rs2.model.skill.crafting.GlassblowingRecipe;
import com.rs2.model.skill.crafting.GlassblowingTask;
import com.rs2.model.skill.crafting.PotteryOvenTask;
import com.rs2.model.skill.crafting.PotteryRecipe;
import com.rs2.model.skill.crafting.PotteryWheelTask;
import com.rs2.model.skill.crafting.SilverCraftingRecipe;
import com.rs2.model.skill.crafting.SilverCraftingTask;
import com.rs2.model.skill.crafting.SpinningRecipe;
import com.rs2.model.skill.crafting.SpinningTask;
import com.rs2.model.skill.crafting.WeavingRecipe;
import com.rs2.model.skill.crafting.WeavingTask;
import com.rs2.model.task.CycleEventHandler;
import com.rs2.util.ByteArrayReader;
import com.rs2.util.FileUtil;
import java.util.Map;

public class CraftingHandler {
    private int mapIndexCapacity = 6000;
    public int mapIndexEntryCount;
    public int[] regionIds = new int[this.mapIndexCapacity];
    public int[] mapArchiveIds = new int[this.mapIndexCapacity];
    public int[] landscapeArchiveIds = new int[this.mapIndexCapacity];

    public static boolean handleDramenStaffButton(Player player, int n, int n2) {
        DramenStaffRecipe dramenStaffRecipe = DramenStaffRecipe.forButtonId(n);
        if (dramenStaffRecipe == null || dramenStaffRecipe.getQuantity() == 0 && n2 == 0) {
            return false;
        }
        Player player2 = player;
        if (player2.interfaceAction == "dramenBranch") {
            if (!ServerSettings.craftingEnabled) {
                player2 = player;
                player2.packetSender.sendGameMessage("This skill is currently disabled.");
                return true;
            }
            if (player.getInventoryManager().getItemAmount(dramenStaffRecipe.getIngredientItemId()) < dramenStaffRecipe.getIngredientAmount()) {
                player.getDialogueManager().showOneLineStatement("You need " + dramenStaffRecipe.getIngredientAmount() + " " + new ItemStack(dramenStaffRecipe.getIngredientItemId()).getDefinition().getName().toLowerCase() + "s to do this.");
                return true;
            }
            if (player.getSkillManager().getCurrentLevels()[12] < dramenStaffRecipe.getRequiredLevel()) {
                player.getDialogueManager().showOneLineStatement("You need a crafting level of " + dramenStaffRecipe.getRequiredLevel() + " to make this.");
                return true;
            }
            player2 = player;
            player2.packetSender.closeInterfaces();
            player.getUpdateState().setAnimation(1248);
            int n3 = player.nextActionSequence();
            player.setActiveCycleEvent(new DramenStaffCarvingTask(dramenStaffRecipe, n2, player, n3));
            CycleEventHandler.getInstance().schedule(player, player.getActiveCycleEvent(), 3);
            return true;
        }
        return false;
    }

    public void loadMapIndex() {
        Object object = FileUtil.readBytes("./data/launcher/map_index.dat");
        object = new ByteArrayReader((byte[])object);
        this.mapIndexEntryCount = ((ByteArrayReader)object).readUnsignedShort();
        int n = 0;
        while (n < this.mapIndexEntryCount) {
            this.regionIds[n] = ((ByteArrayReader)object).readUnsignedShort();
            this.mapArchiveIds[n] = ((ByteArrayReader)object).readUnsignedShort();
            this.landscapeArchiveIds[n] = ((ByteArrayReader)object).readUnsignedShort();
            ++n;
        }
    }

    public static boolean handleGlassblowingButton(Player player, int n, int n2) {
        GlassblowingRecipe glassblowingRecipe = GlassblowingRecipe.forButtonId(n);
        if (glassblowingRecipe == null || glassblowingRecipe.getQuantity() == 0 && n2 == 0) {
            return false;
        }
        Player player2 = player;
        if (player2.interfaceAction == "glassMaking") {
            if (!ServerSettings.craftingEnabled) {
                player2 = player;
                player2.packetSender.sendGameMessage("This skill is currently disabled.");
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
            if (!player.getInventoryManager().getContainer().containsItem(1785)) {
                player.getDialogueManager().showOneLineStatement("You need a glassblowing pipe to do this.");
                return true;
            }
            if (!player.getInventoryManager().getContainer().containsItem(1775)) {
                player.getDialogueManager().showOneLineStatement("You need a molten glass to do this.");
                return true;
            }
            if (player.getSkillManager().getCurrentLevels()[12] < glassblowingRecipe.getRequiredLevel()) {
                player.getDialogueManager().showOneLineStatement("You need a crafting level of " + glassblowingRecipe.getRequiredLevel() + " to make this.");
                return true;
            }
            player2 = player;
            player2.packetSender.closeInterfaces();
            player.getUpdateState().setAnimation(884);
            int n3 = player.nextActionSequence();
            player.setActiveCycleEvent(new GlassblowingTask(glassblowingRecipe, n2, player, n3));
            CycleEventHandler.getInstance().schedule(player, player.getActiveCycleEvent(), 1);
            return true;
        }
        return false;
    }

    public static boolean startPotteryFiring(Player player, int n) {
        PotteryRecipe object2 = null;
        for (Object entryObject : PotteryRecipe.definitionsByButtonId.entrySet()) {
            Map.Entry entry = (Map.Entry)entryObject;
            PotteryRecipe object3 = (PotteryRecipe)((Object)entry.getValue());
            int n2 = object3.getUnfiredItemId();
            if (n2 != n) continue;
            object2 = object3;
            break;
        }
        if (object2 == null) {
            return false;
        }
        String string = "potteryFired";
        Player player2 = player;
        player.interfaceAction = string;
        return CraftingHandler.handlePotteryButton(player, object2.getButtonId(), 1);
    }

    public static boolean handlePotteryButton(Player player, int n, int n2) {
        Player player2;
        PotteryRecipe potteryRecipe = PotteryRecipe.forButtonId(n);
        if (potteryRecipe == null || potteryRecipe.getQuantity() == 0 && n2 == 0) {
            return false;
        }
        if (potteryRecipe.getSoftClayItemId() == 1761) {
            player2 = player;
            if (player2.interfaceAction == "potteryUnfired") {
                if (!ServerSettings.craftingEnabled) {
                    player2 = player;
                    player2.packetSender.sendGameMessage("This skill is currently disabled.");
                    return true;
                }
                ItemStack itemStack = new ItemStack(potteryRecipe.getUnfiredItemId());
                if (itemStack.getDefinition().isMembersOnly()) {
                    if (!player.isMember()) {
                        player.packetSender.sendGameMessage("You need a members account to access members content.");
                        return true;
                    }
                    if (ServerSettings.freeToPlayWorld) {
                        player.packetSender.sendGameMessage("You need to be in members world to access members content.");
                        return true;
                    }
                }
                if (!player.getInventoryManager().getContainer().containsItem(1761)) {
                    player.getDialogueManager().showOneLineStatement("You need soft clay to do this.");
                    return true;
                }
                if (player.getSkillManager().getCurrentLevels()[12] < potteryRecipe.getRequiredLevel()) {
                    player.getDialogueManager().showOneLineStatement("You need a level of " + potteryRecipe.getRequiredLevel() + " to make this.");
                    return true;
                }
                player2 = player;
                player2.packetSender.closeInterfaces();
                player.getUpdateState().setAnimation(894);
                int n3 = player.nextActionSequence();
                player.setActiveCycleEvent(new PotteryWheelTask(potteryRecipe, n2, player, n3, itemStack));
                CycleEventHandler.getInstance().schedule(player, player.getActiveCycleEvent(), 1);
                return true;
            }
        }
        player2 = player;
        if (player2.interfaceAction == "potteryFired") {
            ItemStack itemStack = new ItemStack(potteryRecipe.getFiredItemId());
            if (itemStack.getDefinition().isMembersOnly()) {
                if (!player.isMember()) {
                    player.packetSender.sendGameMessage("You need a members account to access members content.");
                    return true;
                }
                if (ServerSettings.freeToPlayWorld) {
                    player.packetSender.sendGameMessage("You need to be in members world to access members content.");
                    return true;
                }
            }
            if (!player.getInventoryManager().getContainer().containsItem(potteryRecipe.getUnfiredItemId())) {
                player.getDialogueManager().showOneLineStatement("You need an " + new ItemStack(potteryRecipe.getUnfiredItemId()).getDefinition().getName().toLowerCase() + " to do this.");
                return true;
            }
            if (player.getSkillManager().getCurrentLevels()[12] < potteryRecipe.getRequiredLevel()) {
                player2 = player;
                player2.packetSender.sendGameMessage("You need a crafting level of " + potteryRecipe.getRequiredLevel() + " to make this.");
                return true;
            }
            player2 = player;
            player2.packetSender.closeInterfaces();
            player.getUpdateState().setAnimation(896);
            int n4 = player.nextActionSequence();
            player.setActiveCycleEvent(new PotteryOvenTask(potteryRecipe, n2, player, n4, itemStack));
            CycleEventHandler.getInstance().schedule(player, player.getActiveCycleEvent(), 3);
            return true;
        }
        return false;
    }

    public static boolean handleSilverCraftingButton(Player player, int n, int n2) {
        SilverCraftingRecipe silverCraftingRecipe = SilverCraftingRecipe.forButtonId(n);
        if (silverCraftingRecipe == null || silverCraftingRecipe.getQuantity() == 0 && n2 == 0) {
            return false;
        }
        if (silverCraftingRecipe.getBarItemId() == 2355) {
            Player player2 = player;
            if (player2.interfaceAction == "silverCrafting") {
                if (!ServerSettings.craftingEnabled) {
                    player2 = player;
                    player2.packetSender.sendGameMessage("This skill is currently disabled.");
                    return true;
                }
                ItemStack itemStack = new ItemStack(silverCraftingRecipe.getProductItemId());
                if (itemStack.getDefinition().isMembersOnly()) {
                    if (!player.isMember()) {
                        player.packetSender.sendGameMessage("You need a members account to access members content.");
                        return true;
                    }
                    if (ServerSettings.freeToPlayWorld) {
                        player.packetSender.sendGameMessage("You need to be in members world to access members content.");
                        return true;
                    }
                }
                if (!player.getInventoryManager().getContainer().containsItem(2355)) {
                    player.getDialogueManager().showOneLineStatement("You need a silver bar to do this.");
                    return true;
                }
                int n3 = -1;
                if (silverCraftingRecipe.getProductItemId() == 1714) {
                    n3 = 1599;
                }
                if (silverCraftingRecipe.getProductItemId() == 2961) {
                    n3 = 2976;
                }
                if (silverCraftingRecipe.getProductItemId() == 5525) {
                    n3 = 5523;
                }
                if (!player.getInventoryManager().getContainer().containsItem(n3)) {
                    player.getDialogueManager().showOneLineStatement("You don't have the reguired mould to do this.");
                    return true;
                }
                if (player.getSkillManager().getCurrentLevels()[12] < silverCraftingRecipe.getRequiredLevel()) {
                    player.getDialogueManager().showOneLineStatement("You need a crafting level of " + silverCraftingRecipe.getRequiredLevel() + " to make this.");
                    return true;
                }
                player.getUpdateState().setAnimation(899);
                Player player3 = player;
                player3.packetSender.sendSoundEffect(469, 1, 0);
                player3 = player;
                player3.packetSender.closeInterfaces();
                int n4 = player.nextActionSequence();
                player.setActiveCycleEvent(new SilverCraftingTask(silverCraftingRecipe, n2, player, n4, itemStack));
                CycleEventHandler.getInstance().schedule(player, player.getActiveCycleEvent(), 1);
                return true;
            }
        }
        return false;
    }

    public static boolean startBotSpinningTask(Player player) {
        int n = 0;
        if (player.botTaskItemId == 1737) {
            int n2 = n = ItemDefinition.isDefined(6051) ? 8886 : 8871;
        }
        if (player.botTaskItemId == 1779) {
            int n3 = n = ItemDefinition.isDefined(6051) ? 8890 : 8875;
        }
        if (player.botTaskItemId == 6051) {
            n = 8894;
        }
        return CraftingHandler.handleSpinningButton(player, n, 28);
    }

    public static boolean startSpinningAtWheel(Player player, int n) {
        String string = "spinning";
        Player player2 = player;
        player.interfaceAction = string;
        int n2 = 0;
        if (n == 1737) {
            n2 = ItemDefinition.isDefined(6051) ? 8886 : 8871;
        } else if (n == 1779) {
            n2 = ItemDefinition.isDefined(6051) ? 8890 : 8875;
        }
        return CraftingHandler.handleSpinningButton(player, n2, 1);
    }

    public static boolean handleSpinningButton(Player player, int n, int n2) {
        SpinningRecipe spinningRecipe = SpinningRecipe.forButtonId(n);
        if (spinningRecipe == null || spinningRecipe.getQuantity() == 0 && n2 == 0) {
            return false;
        }
        Player player2 = player;
        if (player2.interfaceAction == "spinning") {
            if (!ServerSettings.craftingEnabled) {
                player2 = player;
                player2.packetSender.sendGameMessage("This skill is currently disabled.");
                return true;
            }
            if (!player.getInventoryManager().getContainer().containsItem(spinningRecipe.getIngredientItemId())) {
                player.getDialogueManager().showOneLineStatement("You need " + new ItemStack(spinningRecipe.getIngredientItemId()).getDefinition().getName().toLowerCase() + " to do this.");
                if (player.botEnabled) {
                    player.currentBotTask.startWalkToBank(player);
                }
                return true;
            }
            if (player.getSkillManager().getCurrentLevels()[12] < spinningRecipe.getRequiredLevel()) {
                player.getDialogueManager().showOneLineStatement("You need a crafting level of " + spinningRecipe.getRequiredLevel() + " to make this.");
                return true;
            }
            player2 = player;
            player2.packetSender.closeInterfaces();
            player.getUpdateState().setAnimation(896);
            int n3 = player.nextActionSequence();
            player.setActiveCycleEvent(new SpinningTask(spinningRecipe, n2, player, n3));
            CycleEventHandler.getInstance().schedule(player, player.getActiveCycleEvent(), 3);
            return true;
        }
        return false;
    }

    public static boolean handleWeavingButton(Player player, int n, int n2) {
        WeavingRecipe weavingRecipe = WeavingRecipe.forButtonId(n);
        if (weavingRecipe == null || weavingRecipe.getQuantity() == 0 && n2 == 0) {
            return false;
        }
        Player player2 = player;
        if (player2.interfaceAction == "weaving") {
            if (!ServerSettings.craftingEnabled) {
                player2 = player;
                player2.packetSender.sendGameMessage("This skill is currently disabled.");
                return true;
            }
            if (player.getInventoryManager().getItemAmount(weavingRecipe.getIngredientItemId()) < weavingRecipe.getIngredientAmount()) {
                player.getDialogueManager().showOneLineStatement("You need " + weavingRecipe.getIngredientAmount() + " " + new ItemStack(weavingRecipe.getIngredientItemId()).getDefinition().getName().toLowerCase() + "s to do this.");
                return true;
            }
            if (player.getSkillManager().getCurrentLevels()[12] < weavingRecipe.getRequiredLevel()) {
                player.getDialogueManager().showOneLineStatement("You need a crafting level of " + weavingRecipe.getRequiredLevel() + " to make this.");
                return true;
            }
            player2 = player;
            player2.packetSender.closeInterfaces();
            player.getUpdateState().setAnimation(895);
            int n3 = player.nextActionSequence();
            player.setActiveCycleEvent(new WeavingTask(weavingRecipe, n2, player, n3));
            CycleEventHandler.getInstance().schedule(player, player.getActiveCycleEvent(), 3);
            return true;
        }
        return false;
    }
}

