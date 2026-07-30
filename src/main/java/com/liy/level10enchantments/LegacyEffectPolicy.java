package com.liy.level10enchantments;

public final class LegacyEffectPolicy {
    private LegacyEffectPolicy() {
    }

    public static float mendingRepairPerXp(int level) {
        requirePositiveLevel(level);
        return 2.0F + (level - 1) * (2.0F / 3.0F);
    }

    public static int mendingRepairCapacity(int experience, int level) {
        if (experience < 0) {
            throw new IllegalArgumentException("experience must not be negative");
        }
        return (int) (experience * mendingRepairPerXp(level));
    }

    public static int remainingExperience(
            int experience,
            int repairedDurability,
            int repairCapacity
    ) {
        if (experience < 0 || repairedDurability < 0 || repairCapacity < 1
                || repairedDurability > repairCapacity) {
            throw new IllegalArgumentException("invalid repair accounting");
        }
        return experience - repairedDurability * experience / repairCapacity;
    }

    public static float thornsChance(int level) {
        requirePositiveLevel(level);
        return Math.min(1.0F, level * 0.1F);
    }

    public static float thornsMinimumDamage(int level) {
        requirePositiveLevel(level);
        return level;
    }

    public static float thornsMaximumDamage(int level) {
        requirePositiveLevel(level);
        return 5.0F + (level - 1) * (10.0F / 9.0F);
    }

    private static void requirePositiveLevel(int level) {
        if (level < 1) {
            throw new IllegalArgumentException("level must be positive");
        }
    }
}
