package com.liy.level10enchantments;

public final class AnvilCostPolicy {
    public static final int MAX_COST = 100;

    private AnvilCostPolicy() {
    }

    public static int cap(int calculatedCost) {
        return calculatedCost > MAX_COST ? MAX_COST : calculatedCost;
    }

    public static int addAndCap(int calculatedCost, int surcharge) {
        long sum = (long) calculatedCost + surcharge;
        int saturated = sum > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) sum;
        return cap(saturated);
    }
}
