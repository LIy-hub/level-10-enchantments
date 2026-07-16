package com.liy.level10enchantments;

import java.util.List;
import java.util.Map;

public final class CompatibilitySurcharge {
    private static final List<String> PROTECTION_GROUP = List.of(
            "minecraft:protection",
            "minecraft:fire_protection",
            "minecraft:blast_protection",
            "minecraft:projectile_protection"
    );
    private static final List<String> DAMAGE_GROUP = List.of(
            "minecraft:sharpness",
            "minecraft:smite",
            "minecraft:bane_of_arthropods"
    );

    private CompatibilitySurcharge() {
    }

    public static int calculate(
            Map<String, Integer> left,
            Map<String, Integer> right,
            Map<String, Integer> result
    ) {
        return groupCost(PROTECTION_GROUP, left, right, result, 10, 1)
                + groupCost(DAMAGE_GROUP, left, right, result, 20, 2);
    }

    private static int groupCost(
            List<String> group,
            Map<String, Integer> left,
            Map<String, Integer> right,
            Map<String, Integer> result,
            int baseCost,
            int levelMultiplier
    ) {
        int total = 0;
        for (int firstIndex = 0; firstIndex < group.size(); firstIndex++) {
            String first = group.get(firstIndex);
            int firstLevel = result.getOrDefault(first, 0);
            if (firstLevel <= 0) {
                continue;
            }
            for (int secondIndex = firstIndex + 1; secondIndex < group.size(); secondIndex++) {
                String second = group.get(secondIndex);
                int secondLevel = result.getOrDefault(second, 0);
                if (secondLevel <= 0 || containsPair(left, first, second) || containsPair(right, first, second)) {
                    continue;
                }
                total = Math.addExact(total, baseCost + levelMultiplier * (firstLevel + secondLevel));
            }
        }
        return total;
    }

    private static boolean containsPair(Map<String, Integer> enchantments, String first, String second) {
        return enchantments.getOrDefault(first, 0) > 0 && enchantments.getOrDefault(second, 0) > 0;
    }
}
