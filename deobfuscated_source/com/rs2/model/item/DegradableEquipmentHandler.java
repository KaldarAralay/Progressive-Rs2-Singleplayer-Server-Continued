/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.item;

import com.rs2.model.item.ItemContainer;
import com.rs2.model.item.ItemStack;
import com.rs2.model.player.Player;

public final class DegradableEquipmentHandler {
    private static String[] barrowsSetNameTokens = new String[]{"ahrims", "dharoks", "torags", "guthans", "karils", "veracs"};

    static {
        (new String[1])[0] = "crystal";
    }

    public static void degradeEquipmentAfterCombat(Player player) {
        int n = 0;
        while (n < 14) {
            Object object = player.getEquipmentManager().getContainer().getItemAt(n);
            int n2 = n;
            Player player2 = player;
            if (object != null) {
                Object object2;
                ItemStack itemStack = (ItemStack)object;
                int n3 = n2;
                object = player2;
                String[] stringArray = barrowsSetNameTokens;
                int n4 = 0;
                while (n4 < 6) {
                    object2 = stringArray[n4];
                    if (itemStack.getDefinition().getName().toLowerCase().contains((CharSequence)object2) && (itemStack.getId() < 4856 || itemStack.getId() > 4999)) {
                        int n5;
                        ItemContainer itemContainer = ((Player)object).getEquipmentManager().getContainer();
                        int n6 = itemStack.getId();
                        switch (n6) {
                            case 4708: {
                                n5 = 4856;
                                break;
                            }
                            case 4710: {
                                n5 = 4862;
                                break;
                            }
                            case 4712: {
                                n5 = 4868;
                                break;
                            }
                            case 4714: {
                                n5 = 4874;
                                break;
                            }
                            case 4716: {
                                n5 = 4880;
                                break;
                            }
                            case 4718: {
                                n5 = 4886;
                                break;
                            }
                            case 4720: {
                                n5 = 4892;
                                break;
                            }
                            case 4722: {
                                n5 = 4898;
                                break;
                            }
                            case 4724: {
                                n5 = 4904;
                                break;
                            }
                            case 4726: {
                                n5 = 4910;
                                break;
                            }
                            case 4728: {
                                n5 = 4916;
                                break;
                            }
                            case 4730: {
                                n5 = 4922;
                                break;
                            }
                            case 4732: {
                                n5 = 4928;
                                break;
                            }
                            case 4734: {
                                n5 = 4934;
                                break;
                            }
                            case 4736: {
                                n5 = 4940;
                                break;
                            }
                            case 4738: {
                                n5 = 4946;
                                break;
                            }
                            case 4745: {
                                n5 = 4952;
                                break;
                            }
                            case 4747: {
                                n5 = 4958;
                                break;
                            }
                            case 4749: {
                                n5 = 4964;
                                break;
                            }
                            case 4751: {
                                n5 = 4970;
                                break;
                            }
                            case 4753: {
                                n5 = 4976;
                                break;
                            }
                            case 4755: {
                                n5 = 4982;
                                break;
                            }
                            case 4757: {
                                n5 = 4988;
                                break;
                            }
                            case 4759: {
                                n5 = 4994;
                                break;
                            }
                            default: {
                                n5 = n6;
                            }
                        }
                        itemContainer.setItem(n3, new ItemStack(n5));
                        ((Player)object).getEquipmentManager().getContainer().getItemAt(n3).setMetadata(4500);
                    }
                    ++n4;
                }
                if (itemStack.getId() >= 4856 && itemStack.getId() <= 4999 && !itemStack.getDefinition().getName().toLowerCase().contains(" 0") && ((Player)object).getEquipmentManager().getContainer().getItemAt(n3).getMetadata() == 0) {
                    ((Player)object).getEquipmentManager().getContainer().setItem(n3, new ItemStack(itemStack.getId() + 1));
                    if (!itemStack.getDefinition().getName().toLowerCase().contains(" 25")) {
                        ((Player)object).getEquipmentManager().getContainer().getItemAt(n3).setMetadata(4500);
                    } else {
                        object2 = object;
                        ((Player)object2).packetSender.sendGameMessage("Your barrow equipment has broke.");
                    }
                }
                if (itemStack.getDefinition().getName().toLowerCase().contains("crystal")) {
                    if (itemStack.getDefinition().getId() == 4212 || itemStack.getDefinition().getId() == 4224) {
                        ((Player)object).getEquipmentManager().getContainer().setItem(n3, new ItemStack(itemStack.getDefinition().getId() == 4212 ? 4214 : 4225));
                        ((Player)object).getEquipmentManager().getContainer().getItemAt(n3).setMetadata(250);
                    } else if (((Player)object).getEquipmentManager().getContainer().getItemAt(n3).getMetadata() == 0) {
                        if (itemStack.getDefinition().getId() == 4223 || itemStack.getDefinition().getId() == 4234) {
                            ((Player)object).getEquipmentManager().getContainer().remove(itemStack);
                        } else {
                            ((Player)object).getEquipmentManager().getContainer().setItem(n3, new ItemStack(itemStack.getId() + 1));
                            ((Player)object).getEquipmentManager().getContainer().getItemAt(n3).setMetadata(250);
                        }
                    }
                }
                if (player2.getEquipmentManager().getContainer().getItemAt(n2).getMetadata() >= 0 && player2.getEquipmentManager().getItemIdAtSlot(n2) != 11283 && player2.getEquipmentManager().getItemIdAtSlot(n2) != 11284) {
                    player2.getEquipmentManager().getContainer().getItemAt(n2).setMetadata(player2.getEquipmentManager().getContainer().getItemAt(n2).getMetadata() - 1);
                }
            }
            ++n;
        }
        player.getEquipmentManager().refresh();
    }
}

