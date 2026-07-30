package com.liy.level10enchantments;

public final class MasterLibrarianTradePolicy {
    public record Trade(int level, int emeraldCost, Catalyst catalyst, int maxUses) {
    }

    public enum Catalyst {
        BOOK,
        DIAMOND,
        ECHO_SHARD,
        NETHERITE_INGOT
    }

    private MasterLibrarianTradePolicy() {
    }

    public static Trade select(double roll) {
        if (Double.isNaN(roll) || roll < 0.0 || roll >= 1.0) {
            throw new IllegalArgumentException("roll must be in [0, 1)");
        }
        if (roll < 0.35) {
            return new Trade(6, 32, Catalyst.BOOK, 4);
        }
        if (roll < 0.60) {
            return new Trade(7, 40, Catalyst.BOOK, 3);
        }
        if (roll < 0.78) {
            return new Trade(8, 48, Catalyst.DIAMOND, 2);
        }
        if (roll < 0.90) {
            return new Trade(9, 56, Catalyst.ECHO_SHARD, 1);
        }
        return new Trade(10, 64, Catalyst.NETHERITE_INGOT, 1);
    }
}
