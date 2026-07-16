package com.liy.level10enchantments;

import java.util.concurrent.atomic.AtomicInteger;

public final class BreakthroughSelectorTest {
    public static void main(String[] args) {
        require(EnchantmentRules.all().size() == 29, "manifest size");
        require(EnchantmentRules.find("minecraft:mending").orElseThrow().vanillaMax() == 1, "mending cap");
        require(!EnchantmentRules.find("minecraft:mending").orElseThrow().tableEligible(), "mending treasure");
        require(EnchantmentRules.find("minecraft:sharpness").orElseThrow().tableEligible(), "sharpness table");
        require(EnchantmentRules.find("minecraft:thorns").orElseThrow().vanillaMax() == 3, "thorns cap");
        require(EnchantmentRules.find("minecraft:thorns").orElseThrow().tableEligible(), "thorns table");

        require(select(30, 0.0000) == 6, "level 30 VI lower boundary");
        require(select(30, 0.149999) == 6, "level 30 VI upper boundary");
        require(select(30, 0.1500) == 7, "level 30 VII lower boundary");
        require(select(30, 0.229999) == 7, "level 30 VII upper boundary");
        require(select(30, 0.2300) == 5, "level 30 excludes VIII");
        require(select(49, 0.299999) == 5, "level 49 excludes VIII through X");
        require(select(50, 0.2300) == 8, "level 50 VIII lower boundary");
        require(select(50, 0.269999) == 8, "level 50 VIII upper boundary");
        require(select(50, 0.2700) == 9, "level 50 IX lower boundary");
        require(select(50, 0.289999) == 9, "level 50 IX upper boundary");
        require(select(50, 0.2900) == 10, "level 50 X lower boundary");
        require(select(50, 0.299999) == 10, "level 50 X upper boundary");
        require(select(50, 0.3000) == 5, "level 50 unchanged lower boundary");
        require(select(50, 0.999999) == 5, "level 50 unchanged upper boundary");

        AtomicInteger calls = new AtomicInteger();
        int lowSlot = BreakthroughSelector.selectLevel("minecraft:sharpness", 10, 1, 30,
                () -> { calls.incrementAndGet(); return 0.0; });
        require(lowSlot == 5 && calls.get() == 0, "normalize without consuming RNG");
        require(BreakthroughSelector.selectLevel("minecraft:sharpness", 4, 2, 30, () -> 0.0) == 4,
                "base result below vanilla max");
        require(BreakthroughSelector.selectLevel("minecraft:sharpness", 5, 2, 29, () -> 0.0) == 5,
                "cost below 30");
        require(BreakthroughSelector.selectLevel("minecraft:mending", 10, 2, 30, () -> 0.0) == 1,
                "treasure never table-breaks");
        require(BreakthroughSelector.selectLevel("minecraft:breach", 4, 2, 30, () -> 0.0) == 4,
                "excluded enchantment untouched");
        require(BreakthroughSelector.selectLevel("minecraft:thorns", 3, 2, 50, () -> 0.2900) == 10,
                "thorns can break through to X at level 50");
        System.out.println("PASS: rule manifest and breakthrough selector");
    }

    private static int select(int displayedCost, double roll) {
        return BreakthroughSelector.selectLevel("minecraft:sharpness", 5, 2, displayedCost, () -> roll);
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
