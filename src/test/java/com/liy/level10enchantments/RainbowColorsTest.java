package com.liy.level10enchantments;

public final class RainbowColorsTest {
    public static void main(String[] args) {
        require(RainbowColors.rgbForIndex(0, 0L) == 0xFF3333, "gradient starts red");
        require(RainbowColors.rgbForIndex(0, 1_000L) != RainbowColors.rgbForIndex(0, 0L),
                "color changes over time");
        require(RainbowColors.rgbForIndex(1, 0L) != RainbowColors.rgbForIndex(0, 0L),
                "neighboring characters have a gradient");
        require(RainbowColors.rgbForIndex(0, 6_000L) == RainbowColors.rgbForIndex(0, 0L),
                "color animation loops cleanly");
        require(!RainbowColors.isBold(0, 0L) && RainbowColors.isBold(0, 600L),
                "font weight pulses over time");
        require(RainbowColors.isBold(4, 0L) != RainbowColors.isBold(0, 0L),
                "font weight wave moves across characters");
        boolean rejected = false;
        try {
            RainbowColors.rgbForIndex(-1);
        } catch (IllegalArgumentException expected) {
            rejected = true;
        }
        require(rejected, "negative index rejected");
        System.out.println("PASS: animated level X rainbow and font-weight wave");
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
