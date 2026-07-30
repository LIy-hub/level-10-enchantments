package com.liy.level10enchantments;

public final class LegacyEffectPolicyTest {
    public static void main(String[] args) {
        require(close(LegacyEffectPolicy.mendingRepairPerXp(1), 2.0F),
                "mending I repairs 2 durability per XP");
        require(close(LegacyEffectPolicy.mendingRepairPerXp(10), 8.0F),
                "mending X repairs 8 durability per XP");
        require(LegacyEffectPolicy.mendingRepairCapacity(3, 10) == 24,
                "three XP can repair 24 durability at mending X");
        require(LegacyEffectPolicy.remainingExperience(3, 8, 24) == 2,
                "repair accounting consumes one of three XP");

        require(close(LegacyEffectPolicy.thornsChance(1), 0.1F),
                "thorns I chance is 10 percent");
        require(close(LegacyEffectPolicy.thornsChance(10), 1.0F),
                "thorns X chance is 100 percent");
        require(close(LegacyEffectPolicy.thornsMinimumDamage(1), 1.0F),
                "thorns I minimum damage is 1");
        require(close(LegacyEffectPolicy.thornsMaximumDamage(1), 5.0F),
                "thorns I maximum damage is 5");
        require(close(LegacyEffectPolicy.thornsMinimumDamage(10), 10.0F),
                "thorns X minimum damage is 10");
        require(close(LegacyEffectPolicy.thornsMaximumDamage(10), 15.0F),
                "thorns X maximum damage is 15");
        System.out.println("PASS: legacy Mending and Thorns effects match the porting contract");
    }

    private static boolean close(float left, float right) {
        return Math.abs(left - right) < 0.00001F;
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
