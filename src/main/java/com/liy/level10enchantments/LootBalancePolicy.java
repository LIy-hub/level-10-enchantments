package com.liy.level10enchantments;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public final class LootBalancePolicy {
    public enum Profile {
        STRONGHOLD_LIBRARY(500, 250, 120, 50, 20),
        WOODLAND_MANSION(600, 300, 150, 70, 30),
        BASTION_ORDINARY(700, 400, 200, 100, 40),
        ANCIENT_CITY(700, 450, 300, 150, 80),
        TRIAL_VAULT(800, 500, 300, 150, 70),
        BASTION_TREASURE(800, 500, 350, 200, 100),
        END_CITY_TREASURE(900, 600, 400, 250, 150),
        OMINOUS_TRIAL_VAULT(1000, 700, 500, 300, 200);

        private final int[] basisPoints;

        Profile(int level6, int level7, int level8, int level9, int level10) {
            this.basisPoints = new int[]{level6, level7, level8, level9, level10};
        }

        public int basisPointsFor(int level) {
            if (level < 6 || level > 10) {
                throw new IllegalArgumentException("level must be in [6, 10]");
            }
            return basisPoints[level - 6];
        }

        public int totalBasisPoints() {
            int total = 0;
            for (int probability : basisPoints) {
                total += probability;
            }
            return total;
        }

        public boolean isBastion() {
            return this == BASTION_ORDINARY || this == BASTION_TREASURE;
        }
    }

    private static final Map<String, Profile> PROFILES = profiles();

    private LootBalancePolicy() {
    }

    public static Optional<Profile> profileFor(String randomSequenceId) {
        return Optional.ofNullable(PROFILES.get(randomSequenceId));
    }

    public static int selectHighLevel(Profile profile, double roll) {
        validateRoll(roll);
        int threshold = 0;
        for (int level = 6; level <= 10; level++) {
            threshold += profile.basisPointsFor(level);
            if (roll < threshold / 10_000.0) {
                return level;
            }
        }
        return 0;
    }

    public static boolean allowsHighLevel(Profile profile, String enchantmentId) {
        if (!EnchantmentRules.find(enchantmentId).isPresent()) {
            return false;
        }
        return switch (enchantmentId) {
            case "minecraft:soul_speed" -> profile.isBastion();
            case "minecraft:wind_burst" -> profile == Profile.OMINOUS_TRIAL_VAULT;
            default -> true;
        };
    }

    public static boolean allowsHighLevelInDimension(Profile profile, String dimensionId) {
        return profile != Profile.END_CITY_TREASURE || dimensionId.equals("minecraft:the_end");
    }

    public static boolean allowsLibrarianTrade(String enchantmentId) {
        return EnchantmentRules.find(enchantmentId).isPresent()
                && !enchantmentId.equals("minecraft:soul_speed")
                && !enchantmentId.equals("minecraft:wind_burst");
    }

    private static void validateRoll(double roll) {
        if (Double.isNaN(roll) || roll < 0.0 || roll >= 1.0) {
            throw new IllegalArgumentException("roll must be in [0, 1)");
        }
    }

    private static Map<String, Profile> profiles() {
        LinkedHashMap<String, Profile> profiles = new LinkedHashMap<>();
        profiles.put("minecraft:chests/stronghold_library", Profile.STRONGHOLD_LIBRARY);
        profiles.put("minecraft:chests/woodland_mansion", Profile.WOODLAND_MANSION);
        profiles.put("minecraft:chests/bastion_bridge", Profile.BASTION_ORDINARY);
        profiles.put("minecraft:chests/bastion_hoglin_stable", Profile.BASTION_ORDINARY);
        profiles.put("minecraft:chests/bastion_other", Profile.BASTION_ORDINARY);
        profiles.put("minecraft:chests/ancient_city", Profile.ANCIENT_CITY);
        profiles.put("minecraft:chests/trial_chambers/reward", Profile.TRIAL_VAULT);
        profiles.put("minecraft:chests/bastion_treasure", Profile.BASTION_TREASURE);
        profiles.put("minecraft:chests/end_city_treasure", Profile.END_CITY_TREASURE);
        profiles.put("minecraft:chests/trial_chambers/reward_ominous", Profile.OMINOUS_TRIAL_VAULT);
        return Map.copyOf(profiles);
    }
}
