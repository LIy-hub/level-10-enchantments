package com.liy.level10enchantments;

import java.util.Map;

public final class CompatibilitySurchargeTest {
    public static void main(String[] args) {
        require(cost(Map.of(), Map.of(), Map.of("minecraft:sharpness", 10)) == 0,
                "one enchantment has no surcharge");
        require(cost(
                Map.of("minecraft:protection", 4),
                Map.of("minecraft:fire_protection", 4),
                Map.of("minecraft:protection", 4, "minecraft:fire_protection", 4)
        ) == 18, "protection IV pair");
        require(cost(
                Map.of("minecraft:protection", 10),
                Map.of("minecraft:fire_protection", 10),
                Map.of("minecraft:protection", 10, "minecraft:fire_protection", 10)
        ) == 30, "protection X pair");
        require(cost(
                Map.of("minecraft:protection", 10, "minecraft:fire_protection", 10),
                Map.of("minecraft:blast_protection", 10, "minecraft:projectile_protection", 10),
                Map.of(
                        "minecraft:protection", 10,
                        "minecraft:fire_protection", 10,
                        "minecraft:blast_protection", 10,
                        "minecraft:projectile_protection", 10
                )
        ) == 120, "four cross protection pairs");
        require(cost(
                Map.of("minecraft:sharpness", 10),
                Map.of("minecraft:smite", 10),
                Map.of("minecraft:sharpness", 10, "minecraft:smite", 10)
        ) == 60, "damage X pair");
        require(cost(
                Map.of("minecraft:sharpness", 10, "minecraft:smite", 10),
                Map.of("minecraft:bane_of_arthropods", 10),
                Map.of(
                        "minecraft:sharpness", 10,
                        "minecraft:smite", 10,
                        "minecraft:bane_of_arthropods", 10
                )
        ) == 120, "two new damage pairs");
        require(cost(
                Map.of("minecraft:sharpness", 10, "minecraft:smite", 10),
                Map.of("minecraft:unbreaking", 10),
                Map.of("minecraft:sharpness", 10, "minecraft:smite", 10, "minecraft:unbreaking", 10)
        ) == 0, "existing pair is not charged again");
        System.out.println("PASS: compatibility surcharge is progressive and non-repeating");
    }

    private static int cost(Map<String, Integer> left, Map<String, Integer> right, Map<String, Integer> result) {
        return CompatibilitySurcharge.calculate(left, right, result);
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
