/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.model.item.action;

import com.rs2.model.dialogue.DialogueManager;
import com.rs2.model.item.ItemStack;
import com.rs2.model.item.action.GodBookRecitationEvent;
import com.rs2.model.player.Player;
import com.rs2.model.task.CycleEventHandler;
import com.rs2.util.GameUtil;
import java.util.ArrayList;

public final class GodBookHandler {
    public static int damagedSaradominBookId = 3839;
    public static int holyBookId = 3840;
    public static int damagedZamorakBookId = 3841;
    public static int unholyBookId = 3842;
    public static int damagedGuthixBookId = 3843;
    public static int bookOfBalanceId = 3844;
    private static int saradominPage1Id = 3827;
    private static int saradominPage2Id = 3828;
    private static int saradominPage3Id = 3829;
    private static int saradominPage4Id = 3830;
    private static int zamorakPage1Id = 3831;
    private static int zamorakPage2Id = 3832;
    private static int zamorakPage3Id = 3833;
    private static int zamorakPage4Id = 3834;
    private static int guthixPage1Id = 3835;
    private static int guthixPage2Id = 3836;
    private static int guthixPage3Id = 3837;
    private static int guthixPage4Id = 3838;
    private static int saradominPage1Bit = 1;
    private static int saradominPage2Bit = 2;
    private static int saradominPage3Bit = 3;
    private static int saradominPage4Bit = 4;
    private static int zamorakPage1Bit = 5;
    private static int zamorakPage2Bit = 6;
    private static int zamorakPage3Bit = 7;
    private static int zamorakPage4Bit = 8;
    private static int guthixPage1Bit = 9;
    private static int guthixPage2Bit = 10;
    private static int guthixPage3Bit = 11;
    private static int guthixPage4Bit = 12;
    private static int[] saradominPageBits = new int[]{saradominPage1Bit, saradominPage2Bit, saradominPage3Bit, saradominPage4Bit};
    private static int[] zamorakPageBits = new int[]{zamorakPage1Bit, zamorakPage2Bit, zamorakPage3Bit, zamorakPage4Bit};
    private static int[] guthixPageBits = new int[]{guthixPage1Bit, guthixPage2Bit, guthixPage3Bit, guthixPage4Bit};
    private static int saradominRecitationAnimationId = 1335;
    private static int zamorakRecitationAnimationId = 1336;
    private static int guthixRecitationAnimationId = 1337;
    private static int weddingCeremonyOptionId = 1;
    private static int lastRitesOptionId = 2;
    private static int blessingsOptionId = 3;
    private static int preachOptionId = 4;
    private static String[] saradominWeddingCeremonyLines = new String[]{"In the name of Saradomin,", "Protector of us all,", "I now join you in the eyes of Saradomin."};
    private static String[] saradominLastRitesLines = new String[]{"Thy cause was false, thy skills did lack;", "See you in Lumbridge when you get back."};
    private static String[] saradominBlessingLines = new String[]{"Go in peace in the name of Saradomin;", "May his glory shine upon you like the sun."};
    private static String[] saradominPreachLines = new String[]{"Walk proud, and show mercy.", "For you carry my name in your heart.", "This is Saradomin's wisdom."};
    private static String[] zamorakWeddingCeremonyLines = new String[]{"Two great warriors, joined by hand,", "to spread destruct, on across the land.", "In Zamorak's name, now two are one."};
    private static String[] zamorakLastRitesLines = new String[]{"The weak deserve to die,", "So that the strong may flourish.", "This is the creed of Zamorak."};
    private static String[] zamorakBlessingLines = new String[]{"May your bloodthirst be never sated.", "and may all your battles be glorious.", "Zamorak bring you strength."};
    private static String[] zamorakPreachLines = new String[]{"There is no opinion that cannot be proven true,", "by crushing those who choose to disagree with it.", "Zamorak give me strength!"};
    private static String[] guthixWeddingCeremonyLines = new String[]{"Light and dark, day and night,", "Balance arises from contrast.", "I unify thee in the name of Guthix."};
    private static String[] guthixLastRitesLines = new String[]{"Thy death was not in vain,", "for it brought some balance to the world.", "May Guthix bring you rest."};
    private static String[] guthixBlessingLines = new String[]{"May you walk the path, and never fall,", "For Guthix walks beside thee on thy journey.", "May Guthix bring you peace."};
    private static String[] guthixPreachLines = new String[]{"A journey of a single step,", "May take thee over a thousand miles.", "May Guthix bring you balance."};

    public static boolean showMissingPages(Player player, int n) {
        if (n == damagedSaradominBookId || n == damagedZamorakBookId || n == damagedGuthixBookId) {
            ArrayList arrayList = GodBookHandler.getMissingPageNumbers(player, n);
            String string = "";
            int n2 = 0;
            while (n2 < arrayList.size()) {
                if (n2 > 0) {
                    string = String.valueOf(string) + ", ";
                }
                string = String.valueOf(string) + arrayList.get(n2);
                ++n2;
            }
            player.packetSender.sendGameMessage("The book is missing the following pages: " + string);
            return true;
        }
        return false;
    }

    private static ArrayList getMissingPageNumbers(Player player, int n) {
        ArrayList<Integer> arrayList;
        block7: {
            block8: {
                block6: {
                    arrayList = new ArrayList<Integer>();
                    if (n != damagedSaradominBookId) break block6;
                    n = 0;
                    while (n < saradominPageBits.length) {
                        if ((player.godBookPageFlags & GameUtil.bitFlag(saradominPageBits[n])) == 0) {
                            arrayList.add(n + 1);
                        }
                        ++n;
                    }
                    break block7;
                }
                if (n != damagedZamorakBookId) break block8;
                n = 0;
                while (n < zamorakPageBits.length) {
                    if ((player.godBookPageFlags & GameUtil.bitFlag(zamorakPageBits[n])) == 0) {
                        arrayList.add(n + 1);
                    }
                    ++n;
                }
                break block7;
            }
            if (n != damagedGuthixBookId) break block7;
            n = 0;
            while (n < guthixPageBits.length) {
                if ((player.godBookPageFlags & GameUtil.bitFlag(guthixPageBits[n])) == 0) {
                    arrayList.add(n + 1);
                }
                ++n;
            }
        }
        return arrayList;
    }

    public static boolean handlePageOnBook(Player player, int n, int n2) {
        if (n == damagedSaradominBookId || n2 == damagedSaradominBookId) {
            if (n == saradominPage1Id || n2 == saradominPage1Id) {
                int n3 = saradominPageBits[0];
                if ((player.godBookPageFlags & GameUtil.bitFlag(n3)) == 0) {
                    player.getInventoryManager().removeItem(new ItemStack(saradominPage1Id));
                    Player player2 = player;
                    player2.packetSender.sendGameMessage("You add the page to the book...");
                    player.godBookPageFlags += GameUtil.bitFlag(n3);
                    GodBookHandler.completeBookIfFilled(player, n, n2);
                } else {
                    Player player3 = player;
                    player3.packetSender.sendGameMessage("The book already has that page.");
                }
                return true;
            }
            if (n == saradominPage2Id || n2 == saradominPage2Id) {
                int n4 = saradominPageBits[1];
                if ((player.godBookPageFlags & GameUtil.bitFlag(n4)) == 0) {
                    player.getInventoryManager().removeItem(new ItemStack(saradominPage2Id));
                    Player player4 = player;
                    player4.packetSender.sendGameMessage("You add the page to the book...");
                    player.godBookPageFlags += GameUtil.bitFlag(n4);
                    GodBookHandler.completeBookIfFilled(player, n, n2);
                } else {
                    Player player5 = player;
                    player5.packetSender.sendGameMessage("The book already has that page.");
                }
                return true;
            }
            if (n == saradominPage3Id || n2 == saradominPage3Id) {
                int n5 = saradominPageBits[2];
                if ((player.godBookPageFlags & GameUtil.bitFlag(n5)) == 0) {
                    player.getInventoryManager().removeItem(new ItemStack(saradominPage3Id));
                    Player player6 = player;
                    player6.packetSender.sendGameMessage("You add the page to the book...");
                    player.godBookPageFlags += GameUtil.bitFlag(n5);
                    GodBookHandler.completeBookIfFilled(player, n, n2);
                } else {
                    Player player7 = player;
                    player7.packetSender.sendGameMessage("The book already has that page.");
                }
                return true;
            }
            if (n == saradominPage4Id || n2 == saradominPage4Id) {
                int n6 = saradominPageBits[3];
                if ((player.godBookPageFlags & GameUtil.bitFlag(n6)) == 0) {
                    player.getInventoryManager().removeItem(new ItemStack(saradominPage4Id));
                    Player player8 = player;
                    player8.packetSender.sendGameMessage("You add the page to the book...");
                    player.godBookPageFlags += GameUtil.bitFlag(n6);
                    GodBookHandler.completeBookIfFilled(player, n, n2);
                } else {
                    Player player9 = player;
                    player9.packetSender.sendGameMessage("The book already has that page.");
                }
                return true;
            }
        }
        if (n == damagedZamorakBookId || n2 == damagedZamorakBookId) {
            if (n == zamorakPage1Id || n2 == zamorakPage1Id) {
                int n7 = zamorakPageBits[0];
                if ((player.godBookPageFlags & GameUtil.bitFlag(n7)) == 0) {
                    player.getInventoryManager().removeItem(new ItemStack(zamorakPage1Id));
                    Player player10 = player;
                    player10.packetSender.sendGameMessage("You add the page to the book...");
                    player.godBookPageFlags += GameUtil.bitFlag(n7);
                    GodBookHandler.completeBookIfFilled(player, n, n2);
                } else {
                    Player player11 = player;
                    player11.packetSender.sendGameMessage("The book already has that page.");
                }
                return true;
            }
            if (n == zamorakPage2Id || n2 == zamorakPage2Id) {
                int n8 = zamorakPageBits[1];
                if ((player.godBookPageFlags & GameUtil.bitFlag(n8)) == 0) {
                    player.getInventoryManager().removeItem(new ItemStack(zamorakPage2Id));
                    Player player12 = player;
                    player12.packetSender.sendGameMessage("You add the page to the book...");
                    player.godBookPageFlags += GameUtil.bitFlag(n8);
                    GodBookHandler.completeBookIfFilled(player, n, n2);
                } else {
                    Player player13 = player;
                    player13.packetSender.sendGameMessage("The book already has that page.");
                }
                return true;
            }
            if (n == zamorakPage3Id || n2 == zamorakPage3Id) {
                int n9 = zamorakPageBits[2];
                if ((player.godBookPageFlags & GameUtil.bitFlag(n9)) == 0) {
                    player.getInventoryManager().removeItem(new ItemStack(zamorakPage3Id));
                    Player player14 = player;
                    player14.packetSender.sendGameMessage("You add the page to the book...");
                    player.godBookPageFlags += GameUtil.bitFlag(n9);
                    GodBookHandler.completeBookIfFilled(player, n, n2);
                } else {
                    Player player15 = player;
                    player15.packetSender.sendGameMessage("The book already has that page.");
                }
                return true;
            }
            if (n == zamorakPage4Id || n2 == zamorakPage4Id) {
                int n10 = zamorakPageBits[3];
                if ((player.godBookPageFlags & GameUtil.bitFlag(n10)) == 0) {
                    player.getInventoryManager().removeItem(new ItemStack(zamorakPage4Id));
                    Player player16 = player;
                    player16.packetSender.sendGameMessage("You add the page to the book...");
                    player.godBookPageFlags += GameUtil.bitFlag(n10);
                    GodBookHandler.completeBookIfFilled(player, n, n2);
                } else {
                    Player player17 = player;
                    player17.packetSender.sendGameMessage("The book already has that page.");
                }
                return true;
            }
        }
        if (n == damagedGuthixBookId || n2 == damagedGuthixBookId) {
            if (n == guthixPage1Id || n2 == guthixPage1Id) {
                int n11 = guthixPageBits[0];
                if ((player.godBookPageFlags & GameUtil.bitFlag(n11)) == 0) {
                    player.getInventoryManager().removeItem(new ItemStack(guthixPage1Id));
                    Player player18 = player;
                    player18.packetSender.sendGameMessage("You add the page to the book...");
                    player.godBookPageFlags += GameUtil.bitFlag(n11);
                    GodBookHandler.completeBookIfFilled(player, n, n2);
                } else {
                    Player player19 = player;
                    player19.packetSender.sendGameMessage("The book already has that page.");
                }
                return true;
            }
            if (n == guthixPage2Id || n2 == guthixPage2Id) {
                int n12 = guthixPageBits[1];
                if ((player.godBookPageFlags & GameUtil.bitFlag(n12)) == 0) {
                    player.getInventoryManager().removeItem(new ItemStack(guthixPage2Id));
                    Player player20 = player;
                    player20.packetSender.sendGameMessage("You add the page to the book...");
                    player.godBookPageFlags += GameUtil.bitFlag(n12);
                    GodBookHandler.completeBookIfFilled(player, n, n2);
                } else {
                    Player player21 = player;
                    player21.packetSender.sendGameMessage("The book already has that page.");
                }
                return true;
            }
            if (n == guthixPage3Id || n2 == guthixPage3Id) {
                int n13 = guthixPageBits[2];
                if ((player.godBookPageFlags & GameUtil.bitFlag(n13)) == 0) {
                    player.getInventoryManager().removeItem(new ItemStack(guthixPage3Id));
                    Player player22 = player;
                    player22.packetSender.sendGameMessage("You add the page to the book...");
                    player.godBookPageFlags += GameUtil.bitFlag(n13);
                    GodBookHandler.completeBookIfFilled(player, n, n2);
                } else {
                    Player player23 = player;
                    player23.packetSender.sendGameMessage("The book already has that page.");
                }
                return true;
            }
            if (n == guthixPage4Id || n2 == guthixPage4Id) {
                int n14 = guthixPageBits[3];
                if ((player.godBookPageFlags & GameUtil.bitFlag(n14)) == 0) {
                    player.getInventoryManager().removeItem(new ItemStack(guthixPage4Id));
                    Player player24 = player;
                    player24.packetSender.sendGameMessage("You add the page to the book...");
                    player.godBookPageFlags += GameUtil.bitFlag(n14);
                    GodBookHandler.completeBookIfFilled(player, n, n2);
                } else {
                    Player player25 = player;
                    player25.packetSender.sendGameMessage("The book already has that page.");
                }
                return true;
            }
        }
        return false;
    }

    private static void completeBookIfFilled(Player player, int n, int n2) {
        ArrayList arrayList;
        if (n == damagedSaradominBookId || n2 == damagedSaradominBookId) {
            ArrayList arrayList2 = GodBookHandler.getMissingPageNumbers(player, damagedSaradominBookId);
            if (arrayList2.size() == 0) {
                player.getInventoryManager().removeItem(new ItemStack(damagedSaradominBookId));
                player.getInventoryManager().addItem(new ItemStack(holyBookId));
                player.packetSender.sendGameMessage("The book is now complete!");
                return;
            }
        } else if (n == damagedZamorakBookId || n2 == damagedZamorakBookId) {
            ArrayList arrayList3 = GodBookHandler.getMissingPageNumbers(player, damagedZamorakBookId);
            if (arrayList3.size() == 0) {
                player.getInventoryManager().removeItem(new ItemStack(damagedZamorakBookId));
                player.getInventoryManager().addItem(new ItemStack(unholyBookId));
                player.packetSender.sendGameMessage("The book is now complete!");
                return;
            }
        } else if ((n == damagedGuthixBookId || n2 == damagedGuthixBookId) && (arrayList = GodBookHandler.getMissingPageNumbers(player, damagedGuthixBookId)).size() == 0) {
            player.getInventoryManager().removeItem(new ItemStack(damagedGuthixBookId));
            player.getInventoryManager().addItem(new ItemStack(bookOfBalanceId));
            player.packetSender.sendGameMessage("The book is now complete!");
        }
    }

    public static void giveReplacementBook(Player player, int n) {
        ArrayList arrayList = GodBookHandler.getMissingPageNumbers(player, n);
        if (arrayList.size() == 0) {
            if (n == damagedSaradominBookId) {
                n = holyBookId;
            } else if (n == damagedZamorakBookId) {
                n = unholyBookId;
            } else if (n == damagedGuthixBookId) {
                n = bookOfBalanceId;
            }
        }
        player.getInventoryManager().addOrDropItem(new ItemStack(n, 1));
    }

    public static boolean openRecitationDialogue(Player player, int n) {
        if (n == holyBookId || n == unholyBookId || n == bookOfBalanceId) {
            player.temporaryActionValue = n;
            DialogueManager.startDialogue(player, 13001);
            return true;
        }
        return false;
    }

    public static void startRecitation(Player player, int n) {
        int n2 = player.temporaryActionValue;
        int n3 = -1;
        player.O = 0;
        if (n2 == holyBookId) {
            n3 = saradominRecitationAnimationId;
        } else if (n2 == unholyBookId) {
            n3 = zamorakRecitationAnimationId;
        } else if (n2 == bookOfBalanceId) {
            n3 = guthixRecitationAnimationId;
        }
        String[] stringArray = null;
        if (n == weddingCeremonyOptionId) {
            if (n2 == holyBookId) {
                stringArray = saradominWeddingCeremonyLines;
            } else if (n2 == unholyBookId) {
                stringArray = zamorakWeddingCeremonyLines;
            } else if (n2 == bookOfBalanceId) {
                stringArray = guthixWeddingCeremonyLines;
            }
        } else if (n == lastRitesOptionId) {
            if (n2 == holyBookId) {
                stringArray = saradominLastRitesLines;
            } else if (n2 == unholyBookId) {
                stringArray = zamorakLastRitesLines;
            } else if (n2 == bookOfBalanceId) {
                stringArray = guthixLastRitesLines;
            }
        } else if (n == blessingsOptionId) {
            if (n2 == holyBookId) {
                stringArray = saradominBlessingLines;
            } else if (n2 == unholyBookId) {
                stringArray = zamorakBlessingLines;
            } else if (n2 == bookOfBalanceId) {
                stringArray = guthixBlessingLines;
            }
        } else if (n == preachOptionId) {
            if (n2 == holyBookId) {
                stringArray = saradominPreachLines;
            } else if (n2 == unholyBookId) {
                stringArray = zamorakPreachLines;
            } else if (n2 == bookOfBalanceId) {
                stringArray = guthixPreachLines;
            }
        }
        String[] stringArray2 = stringArray;
        player.setActionLocked(true);
        player.getUpdateState().setAnimation(n3, 0);
        CycleEventHandler.getInstance().schedule(player, new GodBookRecitationEvent(player, stringArray2), 2);
    }
}

