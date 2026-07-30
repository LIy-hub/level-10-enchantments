package com.liy.level10enchantments;

public final class RainbowColorsTest {
    public static void main(String[] args) {
        int start = RainbowColors.rgbForIndex(0, 12, 0L);
        require(start == 0xFB89D5, "gradient starts with softened rose");
        require(RainbowColors.rgbForIndex(0, 12, 1_000L) != start,
                "color changes over time");
        require(RainbowColors.rgbForIndex(1, 12, 0L) != start,
                "neighboring characters have a gradient");
        require(RainbowColors.rgbForIndex(0, 12, 12_000L) == start,
                "color animation loops cleanly");
        int nearbyFrame = RainbowColors.rgbForIndex(0, 12, 16L);
        require(colorDistance(start, nearbyFrame) <= 3,
                "animation changes smoothly between adjacent frames");
        require(!RainbowColors.isBold(0, 0L) && !RainbowColors.isBold(0, 6_000L),
                "binary font-weight pulse removed");
        boolean rejected = false;
        try {
            RainbowColors.rgbForIndex(-1);
        } catch (IllegalArgumentException expected) {
            rejected = true;
        }
        require(rejected, "negative index rejected");
        System.out.println("PASS: smooth level X aurora gradient without weight flicker");
    }

    private static int colorDistance(int first, int second) {
        int red = Math.abs((first >> 16 & 0xFF) - (second >> 16 & 0xFF));
        int green = Math.abs((first >> 8 & 0xFF) - (second >> 8 & 0xFF));
        int blue = Math.abs((first & 0xFF) - (second & 0xFF));
        return red + green + blue;
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
