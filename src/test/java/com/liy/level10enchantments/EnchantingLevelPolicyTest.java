package com.liy.level10enchantments;

public final class EnchantingLevelPolicyTest {
    public static void main(String[] args) {
        require(EnchantmentRules.all().size() == 29, "manifest size");
        require(EnchantmentRules.find("minecraft:lunge").isPresent(), "lunge is included from 1.21.11");
        require(cap("minecraft:sharpness", 4) == 4, "below vanilla maximum unchanged");
        require(cap("minecraft:sharpness", 5) == 5, "vanilla maximum unchanged");
        require(cap("minecraft:sharpness", 6) == 5, "VI capped to vanilla maximum");
        require(cap("minecraft:sharpness", 10) == 5, "X capped to vanilla maximum");
        require(cap("minecraft:thorns", 10) == 3, "thorns capped to III");
        require(cap("minecraft:mending", 10) == 1, "mending capped to I");
        require(cap("minecraft:lunge", 10) == 3, "lunge capped to III");
        require(cap("minecraft:breach", 4) == 4, "unmanaged enchantment untouched");
        System.out.println("PASS: enchanting table levels are capped to vanilla maxima");
    }

    private static int cap(String id, int level) {
        return EnchantingLevelPolicy.capToVanillaMaximum(id, level);
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
