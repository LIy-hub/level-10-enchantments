package com.liy.level10enchantments;

public final class EnchantingLevelPolicy {
    private EnchantingLevelPolicy() {
    }

    public static int capToVanillaMaximum(String id, int generatedLevel) {
        EnchantmentRules.Rule rule = EnchantmentRules.find(id).orElse(null);
        if (rule == null) {
            return generatedLevel;
        }
        return Math.min(generatedLevel, rule.vanillaMax());
    }
}
