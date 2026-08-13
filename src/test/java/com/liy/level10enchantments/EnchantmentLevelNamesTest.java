package com.liy.level10enchantments;

public final class EnchantmentLevelNamesTest {
    public static void main(String[] args) {
        require(EnchantmentLevelNames.displayLevel(14).equals("XIV"), "XIV fallback");
        require(EnchantmentLevelNames.displayLevel(49).equals("XLIX"), "subtractive fallback");
        require(EnchantmentLevelNames.displayLevel(3999).equals("MMMCMXCIX"), "roman upper bound");
        require(EnchantmentLevelNames.displayLevel(4000).equals("4000"), "safe decimal fallback");
        require(EnchantmentLevelNames.displayLevel(14).equals(EnchantmentLevelNames.displayLevel(14)),
                "fallback stays stable for Enchantment Descriptions equality");
        System.out.println("PASS: stable high-level Roman numeral fallback");
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
