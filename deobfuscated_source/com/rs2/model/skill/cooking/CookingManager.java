/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.skill.cooking;

import com.rs2.ServerSettings;
import com.rs2.model.Position;
import com.rs2.model.item.ItemDefinition;
import com.rs2.model.item.ItemStack;
import com.rs2.model.objects.LoadedWorldObject;
import com.rs2.model.objects.ObjectDefinition;
import com.rs2.model.objects.WorldObject;
import com.rs2.model.objects.WorldObjectLookup;
import com.rs2.model.player.Player;
import com.rs2.model.skill.SkillActionHelper;
import com.rs2.model.skill.cooking.CookableFoodDefinition;
import com.rs2.model.skill.cooking.CookingTask;
import com.rs2.model.task.CycleEventHandler;
import com.rs2.util.GameplayTrace;
import com.rs2.util.GameUtil;

public final class CookingManager {
    private Player player;
    public Position firePosition;
    private static int[] waterSourceObjectIds;

    static {
        int[] nArray = new int[]{3039, 114, 2727, 2728, 2729, 2730, 2731};
        int[] nArray2 = new int[]{2732, 2724, 2725, 2726};
        waterSourceObjectIds = new int[]{153, 879, 880, 2654, 2864, 6232, 10436, 10437, 11007, 11759, 13478, 13479, 13480, 21764, 24161, 24214, 24265, 884, 11793, 43, 873, 874, 4063, 6151, 8699, 9143, 9684, 10175, 12279, 12974, 13563, 13564, 14868, 14917, 15678, 16704, 16705, 20358, 22715, 24112, 24314, 11661, 4176, 4285, 4482, 6827};
    }

    public CookingManager(Player player) {
        this.player = player;
    }

    public final boolean handleItemOnCookingObject(int n, int n2, int n3, int n4) {
        Object object = CookableFoodDefinition.forRawItemId(n);
        if (object == null) {
            if (GameplayTrace.enabled()) {
                GameplayTrace.log("cooking item-on-object not-cookable player=" + GameplayTrace.describe(this.player) + " itemId=" + n + " objectId=" + n2 + " x=" + n3 + " y=" + n4 + " plane=" + this.player.getPosition().getPlane());
            }
            return false;
        }
        object = this.player;
        ((Player)object).packetSender.closeInterfaces();
        if (!ServerSettings.cookingEnabled) {
            object = this.player;
            ((Player)object).packetSender.sendGameMessage("This skill is currently disabled.");
            return true;
        }
        this.player.setCookingObjectId(n2);
        object = WorldObjectLookup.findObjectByIdAt(n2, n3, n4, this.player.getPosition().getPlane());
        Object object2 = SkillActionHelper.findWorldObjectById(n2, n3, n4, this.player.getPosition().getPlane());
        if (object != null || object2 != null) {
            object = ObjectDefinition.forId(object != null ? ((LoadedWorldObject)object).getWorldObject().getObjectId() : ((WorldObject)object2).getObjectId());
            object2 = ((ObjectDefinition)object).name.toLowerCase();
            if (((String)object2).equalsIgnoreCase("fire") || ((String)object2).equalsIgnoreCase("fireplace")) {
                if (GameplayTrace.enabled()) {
                    GameplayTrace.log("cooking item-on-object accepted player=" + GameplayTrace.describe(this.player) + " rawItemId=" + n + " raw=" + ItemDefinition.forId(n).getName() + " objectId=" + n2 + " objectName=" + ((ObjectDefinition)object).name + " action=cookFire x=" + n3 + " y=" + n4 + " plane=" + this.player.getPosition().getPlane());
                }
                this.player.beginInterruptibleAction();
                object2 = "cookFire";
                object = this.player;
                this.player.interfaceAction = (String)object2;
                this.firePosition = new Position(n3, n4, this.player.getPosition().getPlane());
                this.player.setSelectedSkillItemId(n);
                if (this.player.getQuestState(0) != 1 || this.player.getInventoryManager().getItemAmount(n) == 1 || ServerSettings.cacheVersion < 334) {
                    CookingManager.startCookingTask(this.player, 1);
                    return true;
                }
                object2 = new ItemStack(n);
                object = this.player;
                ((Player)object).packetSender.sendInterfaceModel(13716, 200, n);
                object = this.player;
                ((Player)object).packetSender.sendInterfaceText(((ItemStack)object2).getDefinition().getName(), 13717);
                object = this.player;
                ((Player)object).packetSender.showChatboxInterface(1743);
                return true;
            }
            if (((String)object2).equalsIgnoreCase("stove") || ((String)object2).equalsIgnoreCase("range") || ((String)object2).equalsIgnoreCase("cooking range") || ((String)object2).equalsIgnoreCase("cooking pot")) {
                if (GameplayTrace.enabled()) {
                    GameplayTrace.log("cooking item-on-object accepted player=" + GameplayTrace.describe(this.player) + " rawItemId=" + n + " raw=" + ItemDefinition.forId(n).getName() + " objectId=" + n2 + " objectName=" + ((ObjectDefinition)object).name + " action=cookRange x=" + n3 + " y=" + n4 + " plane=" + this.player.getPosition().getPlane());
                }
                this.player.beginInterruptibleAction();
                object2 = "cookRange";
                object = this.player;
                this.player.interfaceAction = (String)object2;
                this.player.setSelectedSkillItemId(n);
                if (this.player.botEnabled) {
                    CookingManager.startCookingTask(this.player, 28);
                    return true;
                }
                if (this.player.getQuestState(0) != 1 || this.player.getInventoryManager().getItemAmount(n) == 1 || ServerSettings.cacheVersion < 334) {
                    CookingManager.startCookingTask(this.player, 1);
                    this.player.getUpdateState().setFacePosition(new Position(this.player.getPosition().getX(), this.player.getPosition().getY() - 1));
                    return true;
                }
                object2 = new ItemStack(n);
                object = this.player;
                ((Player)object).packetSender.sendInterfaceModel(13716, 200, n);
                object = this.player;
                ((Player)object).packetSender.sendInterfaceText(((ItemStack)object2).getDefinition().getName(), 13717);
                object = this.player;
                ((Player)object).packetSender.showChatboxInterface(1743);
                return true;
            }
            if (GameplayTrace.enabled()) {
                GameplayTrace.log("cooking item-on-object object-not-cooking-surface player=" + GameplayTrace.describe(this.player) + " rawItemId=" + n + " objectId=" + n2 + " objectName=" + ((ObjectDefinition)object).name + " x=" + n3 + " y=" + n4 + " plane=" + this.player.getPosition().getPlane());
            }
        }
        if (GameplayTrace.enabled()) {
            GameplayTrace.log("cooking item-on-object no-object player=" + GameplayTrace.describe(this.player) + " rawItemId=" + n + " objectId=" + n2 + " x=" + n3 + " y=" + n4 + " plane=" + this.player.getPosition().getPlane());
        }
        return true;
    }

    public static void startCookingTask(Player player, int n) {
        int n2 = player.nextActionSequence();
        player.getMovementQueue().clear();
        Player player2 = player;
        player2.packetSender.closeInterfaces();
        player.setActiveCycleEvent(new CookingTask(n, player, n2));
        CycleEventHandler.getInstance().schedule(player, player.getActiveCycleEvent(), 1);
    }

    public static void cookSelectedItem(Player player) {
        CookableFoodDefinition cookableFoodDefinition = CookableFoodDefinition.forRawItemId(player.getSelectedSkillItemId());
        if (cookableFoodDefinition == null) {
            return;
        }
        if (player.getSkillManager().getCurrentLevels()[7] < cookableFoodDefinition.getRequiredLevel()) {
            player.getDialogueManager().showOneLineStatement("You need a cooking level of " + cookableFoodDefinition.getRequiredLevel() + " to cook this.");
            if (player.botEnabled) {
                player.currentBotTask.startWalkToBank(player);
            }
            return;
        }
        Player player2 = player;
        player2.packetSender.closeInterfaces();
        player.getInventoryManager().removeItem(new ItemStack(player.getSelectedSkillItemId()));
        player2 = player;
        if (player2.interfaceAction == "cookFire") {
            player.getUpdateState().setAnimation(897);
        } else {
            player2 = player;
            if (player2.interfaceAction == "cookRange") {
                player.getUpdateState().setAnimation(883);
            }
        }
        player2 = player;
        player2.packetSender.sendSoundEffect(357, 1, 0);
        if (player.getQuestState(0) != 1) {
            if (player.getQuestState(0) == 13) {
                CookingManager.processCookingResult(player, player.getSelectedSkillItemId(), true);
                player.ea();
            } else if (player.getQuestState(0) == 14) {
                CookingManager.processCookingResult(player, player.getSelectedSkillItemId(), false);
                player.ea();
            } else if (player.getQuestState(0) == 19 && player.getSelectedSkillItemId() == 2307) {
                CookingManager.processCookingResult(player, player.getSelectedSkillItemId(), false);
                player2 = player;
                player2.packetSender.sendEntityHintIcon(1, -1);
                player.ea();
            } else {
                CookingManager.processCookingResult(player, player.getSelectedSkillItemId(), false);
            }
            player.getQuestManager().refreshQuestJournal();
            return;
        }
        if (!cookableFoodDefinition.canCookOnFire()) {
            player2 = player;
            if (player2.interfaceAction == "cookFire") {
                CookingManager.processCookingResult(player, player.getSelectedSkillItemId(), true);
                return;
            }
        }
        CookingManager.processCookingResult(player, player.getSelectedSkillItemId(), false);
    }

    private static void processCookingResult(Player player, int n, boolean bl) {
        CookableFoodDefinition cookableFoodDefinition = CookableFoodDefinition.forRawItemId(n);
        int rawItemId = n;
        int n2 = cookableFoodDefinition.getSuccessChanceLow();
        int n3 = cookableFoodDefinition.getSuccessChanceHigh();
        if (player.getEquipmentManager().getContainer().getItemAt(9) != null && player.getEquipmentManager().getContainer().getItemAt(9).getId() == 775 && (n == 377 || n == 371 || n == 383)) {
            n2 += 14;
            n3 += 31;
        }
        boolean bl2 = GameUtil.rollLevelScaledChance(n2, n3, player.getSkillManager().getCurrentLevels()[7]);
        n = bl2 ? 1 : 0;
        if (bl2 && !bl || player.getQuestState(0) == 14 || player.getQuestState(0) == 19) {
            player.getInventoryManager().addItem(new ItemStack(cookableFoodDefinition.getCookedItemId()));
            player.getSkillManager().addExperience(7, cookableFoodDefinition.getExperience());
            if (GameplayTrace.enabled()) {
                GameplayTrace.log("cooking result player=" + GameplayTrace.describe(player) + " rawItemId=" + rawItemId + " cookedItemId=" + cookableFoodDefinition.getCookedItemId() + " result=success xp=" + cookableFoodDefinition.getExperience() + " action=" + player.interfaceAction);
            }
            if (cookableFoodDefinition.getCookedItemId() != 2146) {
                player.packetSender.sendGameMessage("You successfully cook a " + ItemDefinition.forId(cookableFoodDefinition.getCookedItemId()).getName().toLowerCase() + ".");
                return;
            }
            player.packetSender.sendGameMessage("You deliberately burn the perfectly good piece of meat.");
            return;
        }
        player.getInventoryManager().addItem(new ItemStack(cookableFoodDefinition.getBurntItemId()));
        if (GameplayTrace.enabled()) {
            GameplayTrace.log("cooking result player=" + GameplayTrace.describe(player) + " rawItemId=" + rawItemId + " burntItemId=" + cookableFoodDefinition.getBurntItemId() + " result=burn action=" + player.interfaceAction);
        }
        if (cookableFoodDefinition.getCookedItemId() != 2146) {
            player.packetSender.sendGameMessage("You accidentally burn the " + ItemDefinition.forId(cookableFoodDefinition.getCookedItemId()).getName().toLowerCase() + ".");
            return;
        }
        player.packetSender.sendGameMessage("You deliberately burn the perfectly good piece of meat.");
    }

    public static boolean handleCookingButton(Player player, int n) {
        switch (n) {
            case 13720: {
                CookingManager.startCookingTask(player, 1);
                return true;
            }
            case 13719: {
                CookingManager.startCookingTask(player, 5);
                return true;
            }
            case 13717: {
                CookingManager.startCookingTask(player, 28);
                return true;
            }
        }
        return false;
    }

    /*
     * Enabled aggressive block sorting
     */
    public final boolean handleWaterSourceItem(int n, int n2) {
        int n3 = 0;
        while (n3 < 46) {
            if (n2 == waterSourceObjectIds[n3]) {
                if (n >= 5331 && n < 5340) {
                    this.player.getInventoryManager().getContainer().replaceItemId(n, 5340);
                    this.player.getUpdateState().setAnimation(832);
                    Player player = this.player;
                    Object object = player;
                    object = ObjectDefinition.forId(n2);
                    player.packetSender.sendGameMessage("You fill the " + ItemDefinition.forId(5331).getName().toLowerCase() + " from the " + ((ObjectDefinition)object).name.toLowerCase() + ".");
                    object = this.player;
                    ((Player)object).packetSender.sendSoundEffect(1039, 1, 0);
                    return true;
                }
                switch (n) {
                    case 1825: 
                    case 1827: 
                    case 1829: 
                    case 1831: {
                        this.player.getInventoryManager().getContainer().replaceItemId(n, 1823);
                        this.player.getUpdateState().setAnimation(832);
                        Player player = this.player;
                        Object object = player;
                        object = ObjectDefinition.forId(n2);
                        player.packetSender.sendGameMessage("You fill the " + ItemDefinition.forId(n).getName().toLowerCase() + " from the " + ((ObjectDefinition)object).name.toLowerCase() + ".");
                        object = this.player;
                        ((Player)object).packetSender.sendSoundEffect(1039, 1, 0);
                        return true;
                    }
                    case 229: {
                        this.player.getInventoryManager().getContainer().replaceItemId(229, 227);
                        this.player.getUpdateState().setAnimation(832);
                        Player player = this.player;
                        Object object = player;
                        object = ObjectDefinition.forId(n2);
                        player.packetSender.sendGameMessage("You fill the " + ItemDefinition.forId(n).getName().toLowerCase() + " from the " + ((ObjectDefinition)object).name.toLowerCase() + ".");
                        object = this.player;
                        ((Player)object).packetSender.sendSoundEffect(1039, 1, 0);
                        return true;
                    }
                    case 1925: {
                        this.player.getInventoryManager().getContainer().replaceItemId(1925, 1929);
                        this.player.getUpdateState().setAnimation(832);
                        Player player = this.player;
                        Object object = player;
                        object = ObjectDefinition.forId(n2);
                        player.packetSender.sendGameMessage("You fill the " + ItemDefinition.forId(n).getName().toLowerCase() + " from the " + ((ObjectDefinition)object).name.toLowerCase() + ".");
                        object = this.player;
                        ((Player)object).packetSender.sendSoundEffect(1039, 1, 0);
                        return true;
                    }
                    case 1923: {
                        this.player.getInventoryManager().getContainer().replaceItemId(1923, 1921);
                        this.player.getUpdateState().setAnimation(832);
                        Player player = this.player;
                        Object object = player;
                        object = ObjectDefinition.forId(n2);
                        player.packetSender.sendGameMessage("You fill the " + ItemDefinition.forId(n).getName().toLowerCase() + " from the " + ((ObjectDefinition)object).name.toLowerCase() + ".");
                        object = this.player;
                        ((Player)object).packetSender.sendSoundEffect(1039, 1, 0);
                        return true;
                    }
                    case 1935: {
                        this.player.getInventoryManager().getContainer().replaceItemId(1935, 1937);
                        this.player.getUpdateState().setAnimation(832);
                        Player player = this.player;
                        Object object = player;
                        object = ObjectDefinition.forId(n2);
                        player.packetSender.sendGameMessage("You fill the " + ItemDefinition.forId(n).getName().toLowerCase() + " from the " + ((ObjectDefinition)object).name.toLowerCase() + ".");
                        object = this.player;
                        ((Player)object).packetSender.sendSoundEffect(1039, 1, 0);
                        return true;
                    }
                    case 1980: {
                        this.player.getInventoryManager().getContainer().replaceItemId(1980, 4458);
                        this.player.getUpdateState().setAnimation(832);
                        Player player = this.player;
                        Object object = player;
                        object = ObjectDefinition.forId(n2);
                        player.packetSender.sendGameMessage("You fill the " + ItemDefinition.forId(n).getName().toLowerCase() + " from the " + ((ObjectDefinition)object).name.toLowerCase() + ".");
                        object = this.player;
                        ((Player)object).packetSender.sendSoundEffect(1039, 1, 0);
                        return true;
                    }
                }
            }
            ++n3;
        }
        return false;
    }
}

