/*
 * Decompiled with CFR 0.152.
 */
package com.rs2.util;

import com.rs2.Server;
import com.rs2.model.Position;
import com.rs2.model.item.ItemStack;
import com.rs2.model.npc.Npc;
import com.rs2.model.player.Player;
import com.rs2.util.WeightedChanceEntry;
import com.rs2.util.WeightedChanceEntryThresholdComparator;
import com.rs2.util.path.ProjectileCollisionMap;
import com.rs2.util.path.WalkingCollisionMap;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;
import java.util.Random;

public final class GameUtil {
    private static Random random;

    static {
        char[] cArray = new char[]{' ', 'e', 't', 'a', 'o', 'i', 'h', 'n', 's', 'r', 'd', 'l', 'u', 'm', 'w', 'c', 'y', 'f', 'g', 'p', 'b', 'v', 'k', 'x', 'j', 'q', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', ' ', '!', '?', '.', ',', ':', ';', '(', ')', '-', '&', '*', '\\', '\'', '@', '#', '+', '=', '\u00a3', '$', '%', '\"', '[', ']'};
        random = new Random();
    }

    public static int getRegionId(int n, int n2) {
        n >>= 6;
        n = (n2 >>= 6) + (n << 8);
        return n;
    }

    public static Position getRegionBasePosition(int n) {
        int n2 = n >> 8 << 6;
        n = (n & 0xFF) << 6;
        return new Position(n2, n);
    }

    public static int bitFlag(int n) {
        n = (int)Math.pow(2.0, n);
        return n;
    }

    public static int randomBetweenInclusive(int n, int n2) {
        n2 -= n;
        n2 = GameUtil.randomInt(n2 + 1);
        return n + n2;
    }

    public static int clampRunWeightForEnergyDrain(int n, int n2, int n3) {
        if (n3 < 0) {
            return 0;
        }
        if (n3 > 64) {
            return 64;
        }
        return n3;
    }

    public static int rollLevelScaledChanceIndex(int[] nArray, int[] nArray2, int[] nArray3, int n) {
        ArrayList entries = new ArrayList();
        ArrayList sortedEntries = new ArrayList();
        int n2 = 0;
        while (n2 < nArray.length) {
            WeightedChanceEntry entry = new WeightedChanceEntry(nArray[n2], nArray2[n2], nArray3[n2]);
            sortedEntries.add(entry);
            entries.add(entry);
            ++n2;
        }
        Collections.sort(sortedEntries, new WeightedChanceEntryThresholdComparator());
        double[] sortedProbabilities = GameUtil.calculateLevelScaledProbabilities(sortedEntries, n);
        double[] probabilities = new double[sortedProbabilities.length];
        int n3 = 0;
        while (n3 < sortedProbabilities.length) {
            probabilities[entries.indexOf(sortedEntries.get(n3))] = sortedProbabilities[n3];
            ++n3;
        }
        n3 = GameUtil.rollProbabilityIndex(probabilities);
        return n3;
    }

    public static boolean rollLevelScaledChance(int n, int n2, int n3) {
        return GameUtil.rollLevelScaledChance(n, n2, n3, 1.0);
    }

    public static boolean rollLevelScaledChance(int n, int n2, int n3, double d) {
        double d2;
        double d3 = n;
        double d4 = n2;
        n = (int)(d3 *= d);
        n2 = (int)(d4 *= d);
        double d5 = GameUtil.calculateLevelScaledChance(n, n2, n3);
        return d5 >= (d2 = Math.random());
    }

    public static boolean rollChance(double d) {
        double d2 = Math.random();
        return d >= d2;
    }

    private static double calculateLevelScaledChance(int n, int n2, int n3) {
        double d = Math.floor((double)(n * (99 - n3)) / 98.0) + Math.floor((double)(n2 * (n3 - 1)) / 98.0) + 1.0;
        return Math.min(Math.max(d / 256.0, 0.0), 1.0);
    }

    private static double[] calculateLevelScaledProbabilities(ArrayList arrayList, int n) {
        double[] probabilities = new double[arrayList.size()];
        int n2 = 0;
        while (n2 < arrayList.size()) {
            WeightedChanceEntry entry = (WeightedChanceEntry)arrayList.get(n2);
            if (n < entry.requiredLevel) {
                probabilities[n2] = 0.0;
            } else {
                double probability = 1.0;
                int n3 = 0;
                while (n3 < arrayList.size()) {
                    WeightedChanceEntry weightedChanceEntry = (WeightedChanceEntry)arrayList.get(n3);
                    if (n3 == n2) {
                        probability *= GameUtil.calculateLevelScaledChance(weightedChanceEntry.lowChance, weightedChanceEntry.highChance, n);
                        probabilities[n2] = probability;
                        break;
                    }
                    if (n >= weightedChanceEntry.requiredLevel) {
                        probability *= 1.0 - GameUtil.calculateLevelScaledChance(weightedChanceEntry.lowChance, weightedChanceEntry.highChance, n);
                    }
                    ++n3;
                }
                if (n3 >= arrayList.size()) {
                    throw new IllegalStateException("Index out of bounds");
                }
            }
            ++n2;
        }
        return probabilities;
    }

    public static int rollProbabilityIndex(double[] dArray) {
        double d;
        double d2 = d = Math.random();
        double d3 = 0.0;
        int n = 0;
        while (n < dArray.length) {
            double d4;
            d3 += dArray[n];
            if (d3 >= d2) {
                return n;
            }
            ++n;
        }
        return -1;
    }

    public static int rollFractionWeightIndex(String[] stringArray) {
        int[] nArray = new int[stringArray.length];
        int[] nArray2 = new int[stringArray.length];
        int n = 0;
        while (n < stringArray.length) {
            String[] stringArray2 = stringArray[n].split("/");
            nArray[n] = Integer.parseInt(stringArray2[0]);
            nArray2[n] = Integer.parseInt(stringArray2[1]);
            ++n;
        }
        double d = 0.0;
        int n2 = 0;
        while (n2 < nArray.length) {
            double d2 = nArray[n2];
            double d3 = nArray2[n2];
            double d4 = d2 / d3;
            d += d4;
            ++n2;
        }
        double d5 = Math.random();
        double d6 = d5 * d;
        double d7 = 0.0;
        int n3 = 0;
        while (n3 < nArray.length) {
            double d8;
            double d9 = nArray[n3];
            double d10 = nArray2[n3];
            double d11 = d9 / d10;
            d7 += d11;
            if (d7 >= d6) {
                return n3;
            }
            ++n3;
        }
        return -1;
    }

    public static void addTrackedRareItemAmount(ItemStack itemStack) {
        int n = 0;
        for (Object trackedRareItemObject : Server.trackedRareItems) {
            ItemStack itemStack2 = (ItemStack)trackedRareItemObject;
            if (itemStack.getId() == itemStack2.getId()) {
                int n2 = itemStack2.getAmount() + itemStack.getAmount();
                itemStack.setAmount(n2);
                Server.trackedRareItems.set(n, itemStack);
                return;
            }
            ++n;
        }
    }

    public static String formatNumber(long l) {
        NumberFormat numberFormat = NumberFormat.getIntegerInstance(Locale.ENGLISH);
        return numberFormat.format(l);
    }

    public static String formatCompactAmountHighThreshold(int n) {
        int n2 = Math.abs(n);
        String string = "";
        if (n2 >= 100000 && n2 < 10000000) {
            string = "K";
            n /= 1000;
        }
        if (n2 >= 10000000) {
            string = "M";
            n /= 1000000;
        }
        return String.valueOf(n) + string;
    }

    public static String formatCompactAmount(int n) {
        int n2 = Math.abs(n);
        String string = "";
        if (n2 >= 1000 && n2 < 10000000) {
            string = "K";
            n /= 1000;
        }
        if (n2 >= 10000000) {
            string = "M";
            n /= 1000000;
        }
        return String.valueOf(n) + string;
    }

    public static String formatCompactAmountDetailed(int n) {
        int n2 = Math.abs(n);
        String string = "";
        int n3 = 1;
        if (n2 >= 1000 && n2 < 1000000) {
            string = "K";
            n /= 1000;
            n3 = 1000;
        }
        if (n2 >= 1000000) {
            string = "M";
            n /= 1000000;
            n3 = 1000000;
        }
        String string2 = "";
        if ((n2 -= n * n3) != 0) {
            string2 = String.valueOf(n2);
        }
        if (!string2.equals("")) {
            String string3 = String.valueOf(n) + "," + n2;
            string3 = string3.indexOf(",") < 0 ? string3 : string3.replaceAll("0*$", "").replaceAll("\\,$", "");
            return String.valueOf(string3) + string;
        }
        return String.valueOf(n) + string;
    }

    public static String capitalizeLowercaseFirst(String string) {
        if ((string = string.toLowerCase()).length() <= 1) {
            return string.toUpperCase();
        }
        string = String.valueOf(string.substring(0, 1).toUpperCase()) + string.substring(1);
        return string;
    }

    public static String capitalizeWords(String string) {
        int n = 0;
        while (n < string.length()) {
            if (n == 0) {
                string = String.format("%s%s", Character.valueOf(Character.toUpperCase(string.charAt(0))), string.substring(1));
            }
            if (!Character.isLetterOrDigit(string.charAt(n)) && n + 1 < string.length()) {
                string = String.format("%s%s%s", string.subSequence(0, n + 1), Character.valueOf(Character.toUpperCase(string.charAt(n + 1))), string.substring(n + 2));
            }
            ++n;
        }
        return string;
    }

    public static String formatDisplayName(String string) {
        string = GameUtil.capitalizeWords(string);
        string.replace("_", " ");
        return string;
    }

    public static int randomExclusive(int n) {
        n = (int)(Math.random() * (double)n);
        if (n < 0) {
            return 0;
        }
        return n;
    }

    public static int randomInclusive(int n) {
        n = (int)(Math.random() * (double)(n + 1));
        if (n < 0) {
            return 0;
        }
        return n;
    }

    public static int randomInt(int n) {
        n = random.nextInt(n);
        return n;
    }

    public static int randomOneToInclusive(int n, int n2) {
        n = n2 - 1;
        n = 1 + GameUtil.randomInt(n + 1);
        return n;
    }

    public static int rollPriceFluctuationPercent(int n) {
        n = random.nextInt(66);
        int n2 = 0;
        int n3 = 11;
        while (n >= 0) {
            n -= n3;
            ++n2;
            --n3;
        }
        return n2 - 1;
    }

    public static int[] shuffleIntArray(int[] nArray) {
        Random random = new Random();
        int n = nArray.length - 1;
        while (n > 0) {
            int n2 = random.nextInt(n + 1);
            int n3 = nArray[n2];
            nArray[n2] = nArray[n];
            nArray[n] = n3;
            --n;
        }
        return nArray;
    }

    public static String formatNumber(int n) {
        NumberFormat numberFormat = NumberFormat.getIntegerInstance(Locale.ENGLISH);
        return numberFormat.format(n);
    }

    public static boolean hasClearPath(Position position, Position position2, boolean bl) {
        return GameUtil.hasClearPath(position.getX(), position.getY(), position2.getX(), position2.getY(), position.getPlane(), bl);
    }

    public static boolean isNpcLastStepFacingPlayer(Player player, Npc npc) {
        Position position;
        if (player == null) {
            return false;
        }
        if (npc == null) {
            return false;
        }
        Position position2 = player.getPosition();
        if (position2.isWithinDistance(position = npc.getPosition(), 6) && (player.getPosition().getX() > npc.getPosition().getX() && npc.getLastStepFacingDirection() == 4 || player.getPosition().getX() <= npc.getPosition().getX() && npc.getLastStepFacingDirection() == 5 || player.getPosition().getY() > npc.getPosition().getY() && npc.getLastStepFacingDirection() == 2 || player.getPosition().getY() <= npc.getPosition().getY() && npc.getLastStepFacingDirection() == 0)) {
            return GameUtil.hasClearPath(position2.getX(), position2.getY(), position.getX(), position.getY(), position2.getPlane(), false);
        }
        return false;
    }

    public static boolean isNpcWaypointFacingPlayer(Player player, Npc npc) {
        Position position;
        Position position2 = player.getPosition();
        if (position2.isWithinDistance(position = npc.getPosition(), 6) && (player.getPosition().getX() > npc.getPosition().getX() && npc.getWaypointFacingDirection() == 4 || player.getPosition().getX() <= npc.getPosition().getX() && npc.getWaypointFacingDirection() == 5 || player.getPosition().getY() > npc.getPosition().getY() && npc.getWaypointFacingDirection() == 2 || player.getPosition().getY() <= npc.getPosition().getY() && npc.getWaypointFacingDirection() == 0)) {
            return GameUtil.hasClearPath(position2.getX(), position2.getY(), position.getX(), position.getY(), position2.getPlane(), false);
        }
        return false;
    }

    private static boolean hasClearPath(int n, int n2, int n3, int n4, int n5, boolean bl) {
        if (bl) {
            return WalkingCollisionMap.canTravelBetween(n, n2, n3, n4, n5, 1, 1);
        }
        int n7 = n;
        n = n3 - n7;
        n2 = n4 - n2;
        int n6 = Math.max(Math.abs(n), Math.abs(n2));
        int n8 = 0;
        while (n8 < n6) {
            int n9 = n3 - n;
            int n10 = n4 - n2;
            int n11 = 0;
            while (n11 <= 0) {
                int n12 = 0;
                while (n12 <= 0) {
                    if (n < 0 && n2 < 0 ? (ProjectileCollisionMap.getTileFlags(n9 + n11 - 1, n10 + n12 - 1, n5) & 0x128010E) != 0 || (ProjectileCollisionMap.getTileFlags(n9 + n11 - 1, n10 + n12, n5) & 0x1280108) != 0 || (ProjectileCollisionMap.getTileFlags(n9 + n11, n10 + n12 - 1, n5) & 0x1280102) != 0 : (n > 0 && n2 > 0 ? (ProjectileCollisionMap.getTileFlags(n9 + n11 + 1, n10 + n12 + 1, n5) & 0x12801E0) != 0 || (ProjectileCollisionMap.getTileFlags(n9 + n11 + 1, n10 + n12, n5) & 0x1280180) != 0 || (ProjectileCollisionMap.getTileFlags(n9 + n11, n10 + n12 + 1, n5) & 0x1280120) != 0 : (n < 0 && n2 > 0 ? (ProjectileCollisionMap.getTileFlags(n9 + n11 - 1, n10 + n12 + 1, n5) & 0x1280138) != 0 || (ProjectileCollisionMap.getTileFlags(n9 + n11 - 1, n10 + n12, n5) & 0x1280108) != 0 || (ProjectileCollisionMap.getTileFlags(n9 + n11, n10 + n12 + 1, n5) & 0x1280120) != 0 : (n > 0 && n2 < 0 ? (ProjectileCollisionMap.getTileFlags(n9 + n11 + 1, n10 + n12 - 1, n5) & 0x1280183) != 0 || (ProjectileCollisionMap.getTileFlags(n9 + n11 + 1, n10 + n12, n5) & 0x1280180) != 0 || (ProjectileCollisionMap.getTileFlags(n9 + n11, n10 + n12 - 1, n5) & 0x1280102) != 0 : (n > 0 && n2 == 0 ? (ProjectileCollisionMap.getTileFlags(n9 + n11 + 1, n10 + n12, n5) & 0x1280180) != 0 : (n < 0 && n2 == 0 ? (ProjectileCollisionMap.getTileFlags(n9 + n11 - 1, n10 + n12, n5) & 0x1280108) != 0 : (n == 0 && n2 > 0 ? (ProjectileCollisionMap.getTileFlags(n9 + n11, n10 + n12 + 1, n5) & 0x1280120) != 0 : n == 0 && n2 < 0 && (ProjectileCollisionMap.getTileFlags(n9 + n11, n10 + n12 - 1, n5) & 0x1280102) != 0))))))) {
                        return false;
                    }
                    ++n12;
                }
                ++n11;
            }
            if (n < 0) {
                ++n;
            } else if (n > 0) {
                --n;
            }
            if (n2 < 0) {
                ++n2;
            } else if (n2 > 0) {
                --n2;
            }
            ++n8;
        }
        return true;
    }

    public static final boolean isWithinDistance(Position position, Position position2, int n) {
        if (position == null || position2 == null) {
            return false;
        }
        return GameUtil.isWithinDistance(position.getX(), position.getY(), position2.getX(), position2.getY(), n) && position.getPlane() == position2.getPlane();
    }

    public static final boolean isWithinDistance(int n, int n2, int n3, int n4, int n5) {
        n -= n3;
        return (n = (int)Math.sqrt(Math.pow(n, 2.0) + Math.pow(n2 -= n4, 2.0))) <= n5;
    }

    public static Position findReachableInteractionPosition(int n, int n2, int n3, int n4, int n5, int n6, int n7) {
        if (n5 <= 0 && n6 <= 0) {
            if (n3 == n && n4 == n2) {
                return new Position(n, n2, n7);
            }
            return null;
        }
        if (n5 == 1 && n6 == 1) {
            if (GameUtil.isWithinDistance(n3, n4, n, n2, 1)) {
                return new Position(n, n2, n7);
            }
            return null;
        }
        n5 = n + n5 - 1;
        n6 = n2 + n6 - 1;
        Position position = new Position(n3, n4, n7);
        while (n <= n5) {
            n4 = n2;
            while (n4 <= n6) {
                Position position2 = new Position(n, n4, n7);
                if (GameUtil.isWithinDistance(position2, position, 1) && position2.isOrthogonallyAlignedWith(position)) {
                    return position2;
                }
                ++n4;
            }
            ++n;
        }
        return null;
    }

    public static Position getDelta(Position position, Position position2) {
        return new Position(position2.getX() - position.getX(), position2.getY() - position.getY());
    }

    public static int getDistance(Position position, Position position2) {
        int n = position2.getX() - position.getX();
        int n2 = position2.getY() - position.getY();
        return (int)Math.sqrt(Math.pow(n, 2.0) + Math.pow(n2, 2.0));
    }

    public static int getDirectionForDelta(int n, int n2) {
        if (n < 0) {
            if (n2 < 0) {
                return 5;
            }
            if (n2 > 0) {
                return 0;
            }
            return 3;
        }
        if (n > 0) {
            if (n2 < 0) {
                return 7;
            }
            if (n2 > 0) {
                return 2;
            }
            return 4;
        }
        if (n2 < 0) {
            return 6;
        }
        if (n2 > 0) {
            return 1;
        }
        return -1;
    }

    public static String formatNumber(double d) {
        NumberFormat numberFormat = NumberFormat.getIntegerInstance(Locale.ENGLISH);
        return numberFormat.format(d);
    }

    public static Random getRandom() {
        return random;
    }

    public static String getOrdinalWord(int n) {
        if (n == 1) {
            return "first";
        }
        if (n == 2) {
            return "second";
        }
        if (n == 3) {
            return "third";
        }
        if (n == 4) {
            return "fourth";
        }
        return "first";
    }

    public static int getDayOfYear() {
        Calendar calendar = Calendar.getInstance();
        int n = calendar.get(1);
        int n2 = calendar.get(2);
        int[] nArray = new int[]{31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        if (n % 4 == 0 && n % 100 != 0 || n % 400 == 0) {
            nArray[1] = 29;
        }
        int n3 = 0 + calendar.get(5);
        n = 0;
        while (n < 12) {
            if (n < n2) {
                n3 += nArray[n];
            }
            ++n;
        }
        return n3;
    }

    public static int getCurrentYear() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(1);
    }

    public static long secondsToTicks(long l) {
        return (long)Math.ceil((double)l * 1000.0 / 600.0);
    }

    public static int minutesToMillis(int n) {
        return n * 60 * 1000;
    }
}

