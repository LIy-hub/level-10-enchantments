package com.liy.level10enchantments;

public final class AnvilCostPolicyTest {
    public static void main(String[] args) {
        require(AnvilCostPolicy.cap(-1) == -1, "negative sentinel");
        require(AnvilCostPolicy.cap(0) == 0, "zero");
        require(AnvilCostPolicy.cap(39) == 39, "39");
        require(AnvilCostPolicy.cap(40) == 40, "40");
        require(AnvilCostPolicy.cap(99) == 99, "99");
        require(AnvilCostPolicy.cap(100) == 100, "100");
        require(AnvilCostPolicy.cap(101) == 100, "101");
        require(AnvilCostPolicy.cap(Integer.MAX_VALUE) == 100, "integer max");
        require(AnvilCostPolicy.addAndCap(90, 5) == 95, "surcharge below cap");
        require(AnvilCostPolicy.addAndCap(90, 15) == 100, "surcharge reaches cap");
        require(AnvilCostPolicy.addAndCap(Integer.MAX_VALUE, Integer.MAX_VALUE) == 100,
                "saturating addition reaches cap");
        System.out.println("PASS: anvil cost policy caps all final costs at 100");
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
