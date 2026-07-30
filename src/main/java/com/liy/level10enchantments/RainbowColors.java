package com.liy.level10enchantments;

public final class RainbowColors {
    private static final long COLOR_CYCLE_MILLIS = 12_000L;
    private static final double TEXT_GRADIENT_SPAN = 0.42;
    private static final int[] AURORA_PALETTE = {
            0xFF8BD8,
            0xB69CFF,
            0x7BCBFF,
            0x82E6C4,
            0xF8D486
    };

    private RainbowColors() {
    }

    public static int rgbForIndex(int index) {
        return rgbForIndex(index, 12, 0L);
    }

    public static int rgbForIndex(int index, long timeMillis) {
        return rgbForIndex(index, 12, timeMillis);
    }

    public static int rgbForIndex(int index, int glyphCount, long timeMillis) {
        requireIndex(index);
        if (glyphCount < 1 || index >= glyphCount) {
            throw new IllegalArgumentException("glyphCount must be greater than index");
        }

        double timeProgress = Math.floorMod(timeMillis, COLOR_CYCLE_MILLIS)
                / (double) COLOR_CYCLE_MILLIS;
        double textProgress = glyphCount == 1 ? 0.0 : index / (double) (glyphCount - 1);
        double palettePosition = (timeProgress + textProgress * TEXT_GRADIENT_SPAN)
                * AURORA_PALETTE.length;
        int firstIndex = Math.floorMod((int) Math.floor(palettePosition), AURORA_PALETTE.length);
        int secondIndex = (firstIndex + 1) % AURORA_PALETTE.length;
        double fraction = palettePosition - Math.floor(palettePosition);
        double easedFraction = fraction * fraction * (3.0 - 2.0 * fraction);

        int rgb = interpolateLinearRgb(
                AURORA_PALETTE[firstIndex],
                AURORA_PALETTE[secondIndex],
                easedFraction
        );
        double shimmer = 0.97 + 0.03 * (
                0.5 + 0.5 * Math.sin(
                        Math.PI * 2.0 * (timeProgress * 1.5 - textProgress * 0.8)
                )
        );
        return scaleBrightness(rgb, shimmer);
    }

    public static boolean isBold(int index, long timeMillis) {
        requireIndex(index);
        return false;
    }

    private static int interpolateLinearRgb(int first, int second, double fraction) {
        int red = interpolateChannel(first >> 16 & 0xFF, second >> 16 & 0xFF, fraction);
        int green = interpolateChannel(first >> 8 & 0xFF, second >> 8 & 0xFF, fraction);
        int blue = interpolateChannel(first & 0xFF, second & 0xFF, fraction);
        return red << 16 | green << 8 | blue;
    }

    private static int interpolateChannel(int first, int second, double fraction) {
        double firstLinear = Math.pow(first / 255.0, 2.2);
        double secondLinear = Math.pow(second / 255.0, 2.2);
        double interpolated = firstLinear + (secondLinear - firstLinear) * fraction;
        return clampChannel((int) Math.round(Math.pow(interpolated, 1.0 / 2.2) * 255.0));
    }

    private static int scaleBrightness(int rgb, double scale) {
        int red = clampChannel((int) Math.round((rgb >> 16 & 0xFF) * scale));
        int green = clampChannel((int) Math.round((rgb >> 8 & 0xFF) * scale));
        int blue = clampChannel((int) Math.round((rgb & 0xFF) * scale));
        return red << 16 | green << 8 | blue;
    }

    private static int clampChannel(int value) {
        return Math.max(0, Math.min(255, value));
    }

    private static void requireIndex(int index) {
        if (index < 0) {
            throw new IllegalArgumentException("index must be non-negative");
        }
    }
}
