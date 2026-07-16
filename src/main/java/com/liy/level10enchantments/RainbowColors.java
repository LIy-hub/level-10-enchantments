package com.liy.level10enchantments;

public final class RainbowColors {
    private static final long COLOR_CYCLE_MILLIS = 6_000L;
    private static final long WEIGHT_CYCLE_MILLIS = 2_400L;
    private static final double CHARACTER_HUE_STEP = 0.075;
    private static final double CHARACTER_WEIGHT_STEP = 0.9;

    private RainbowColors() {
    }

    public static int rgbForIndex(int index) {
        return rgbForIndex(index, 0L);
    }

    public static int rgbForIndex(int index, long timeMillis) {
        requireIndex(index);
        double timeHue = Math.floorMod(timeMillis, COLOR_CYCLE_MILLIS)
                / (double) COLOR_CYCLE_MILLIS;
        double hue = (timeHue + index * CHARACTER_HUE_STEP) % 1.0;
        return hsvToRgb(hue, 0.8, 1.0);
    }

    public static boolean isBold(int index, long timeMillis) {
        requireIndex(index);
        double timePhase = Math.floorMod(timeMillis, WEIGHT_CYCLE_MILLIS)
                / (double) WEIGHT_CYCLE_MILLIS * Math.PI * 2.0;
        return Math.sin(timePhase - index * CHARACTER_WEIGHT_STEP) > 0.0;
    }

    private static int hsvToRgb(double hue, double saturation, double value) {
        double scaled = hue * 6.0;
        int sector = (int) Math.floor(scaled) % 6;
        double fraction = scaled - Math.floor(scaled);
        double low = value * (1.0 - saturation);
        double falling = value * (1.0 - saturation * fraction);
        double rising = value * (1.0 - saturation * (1.0 - fraction));

        double red;
        double green;
        double blue;
        switch (sector) {
            case 0 -> { red = value; green = rising; blue = low; }
            case 1 -> { red = falling; green = value; blue = low; }
            case 2 -> { red = low; green = value; blue = rising; }
            case 3 -> { red = low; green = falling; blue = value; }
            case 4 -> { red = rising; green = low; blue = value; }
            default -> { red = value; green = low; blue = falling; }
        }
        return channel(red) << 16 | channel(green) << 8 | channel(blue);
    }

    private static int channel(double value) {
        return (int) Math.round(value * 255.0);
    }

    private static void requireIndex(int index) {
        if (index < 0) {
            throw new IllegalArgumentException("index must be non-negative");
        }
    }
}
