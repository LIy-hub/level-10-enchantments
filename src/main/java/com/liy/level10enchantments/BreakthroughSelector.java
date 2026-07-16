package com.liy.level10enchantments;

import java.util.function.DoubleSupplier;

public final class BreakthroughSelector {
    private BreakthroughSelector() {
    }

    public static int selectLevel(
            String id,
            int generatedLevel,
            int optionIndex,
            int displayedCost,
            DoubleSupplier randomRoll
    ) {
        EnchantmentRules.Rule rule = EnchantmentRules.find(id).orElse(null);
        if (rule == null) {
            return generatedLevel;
        }

        int normalized = Math.min(generatedLevel, rule.vanillaMax());
        if (!rule.tableEligible()
                || optionIndex != 2
                || displayedCost < 30
                || generatedLevel < rule.vanillaMax()) {
            return normalized;
        }

        double roll = randomRoll.getAsDouble();
        if (roll < 0.0 || roll >= 1.0) {
            throw new IllegalArgumentException("roll must be in [0, 1)");
        }
        if (roll < 0.15) {
            return 6;
        }
        if (roll < 0.23) {
            return 7;
        }
        if (displayedCost < 50) {
            return normalized;
        }
        if (roll < 0.27) {
            return 8;
        }
        if (roll < 0.29) {
            return 9;
        }
        if (roll < 0.30) {
            return 10;
        }
        return normalized;
    }
}
