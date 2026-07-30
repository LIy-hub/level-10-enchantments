package com.liy.level10enchantments;

public final class AnvilLevelMergePolicy {
    private AnvilLevelMergePolicy() {
    }

    public static int merge(int vanillaMaximum, int leftLevel, int rightLevel) {
        if (vanillaMaximum < 1) {
            throw new IllegalArgumentException("vanillaMaximum must be positive");
        }
        if (leftLevel < 0 || rightLevel < 0) {
            throw new IllegalArgumentException("enchantment levels must not be negative");
        }
        if (leftLevel == rightLevel && leftLevel > 0) {
            return leftLevel < vanillaMaximum ? leftLevel + 1 : leftLevel;
        }
        return Math.max(leftLevel, rightLevel);
    }

    public static boolean preventsIncrement(int vanillaMaximum, int leftLevel, int rightLevel) {
        return leftLevel > 0 && leftLevel == rightLevel && leftLevel >= vanillaMaximum;
    }
}
