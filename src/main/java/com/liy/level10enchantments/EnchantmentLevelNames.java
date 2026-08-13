package com.liy.level10enchantments;

/** Stable level text for tooltip equality checks; intentionally contains no clock state. */
public final class EnchantmentLevelNames {
    private EnchantmentLevelNames() {
    }

    public static String displayLevel(int level) {
        if (level < 1 || level > 3_999) {
            return Integer.toString(level);
        }
        int[] values = {1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1};
        String[] tokens = {"M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I"};
        StringBuilder result = new StringBuilder();
        int remaining = level;
        for (int index = 0; index < values.length; index++) {
            while (remaining >= values[index]) {
                result.append(tokens[index]);
                remaining -= values[index];
            }
        }
        return result.toString();
    }
}
