package com.liy.level10enchantments;

public final class AnvilLevelMergePolicyTest {
    public static void main(String[] args) {
        require(merge(5, 4, 4) == 5, "IV plus IV becomes V");
        require(merge(5, 5, 5) == 5, "V plus V remains V");
        require(merge(5, 6, 6) == 6, "VI plus VI remains VI");
        require(merge(5, 6, 7) == 7, "higher level covers lower level");
        require(merge(5, 7, 6) == 7, "left higher level is preserved");
        require(merge(3, 2, 2) == 3, "vanilla upgrade remains available");
        require(merge(3, 3, 3) == 3, "vanilla cap cannot be crossed");
        require(merge(1, 1, 1) == 1, "mending I plus I remains I");
        require(AnvilLevelMergePolicy.preventsIncrement(5, 5, 5), "detect capped increment");
        require(AnvilLevelMergePolicy.preventsIncrement(5, 8, 8), "detect high-level increment");
        require(!AnvilLevelMergePolicy.preventsIncrement(5, 4, 4), "allow vanilla increment");
        require(!AnvilLevelMergePolicy.preventsIncrement(5, 6, 7), "different levels do not increment");
        System.out.println("PASS: anvil merge policy");
    }

    private static int merge(int vanillaMaximum, int leftLevel, int rightLevel) {
        return AnvilLevelMergePolicy.merge(vanillaMaximum, leftLevel, rightLevel);
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
