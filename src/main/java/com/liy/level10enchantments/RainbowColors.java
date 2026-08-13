package com.liy.level10enchantments;

public final class RainbowColors {
    private static final long LEVEL_TEN_CYCLE_MILLIS = 6_000L;
    private static final long MINIMUM_CYCLE_MILLIS = 3_000L;
    private static final long CYCLE_REDUCTION_PER_LEVEL = 500L;
    private static final double LEVEL_TEN_GLYPH_HUE_SPAN = 0.11D;
    private static final double MAXIMUM_GLYPH_HUE_SPAN = 0.24D;
    private static final double LEVEL_TEN_SATURATION = 0.47D;
    private static final double MAXIMUM_SATURATION = 0.72D;
    private static final double BASE_BRIGHTNESS = 0.87D;
    private static final double LEVEL_TEN_SHIMMER_AMPLITUDE = 0.04D;
    private static final double MAXIMUM_SHIMMER_AMPLITUDE = 0.12D;

    private RainbowColors() {
    }

    public static int rgbForIndex(int index) {
        return rgbForIndex(index, 12, 0L);
    }

    public static int rgbForIndex(int index, long timeMillis) {
        return rgbForIndex(index, 12, timeMillis);
    }

    public static int rgbForIndex(int index, int glyphCount, long timeMillis) {
        return rgbForPhase(phaseForIndex(index, glyphCount, timeMillis), 10);
    }

    public static int rgbForIndex(int index, int glyphCount, long timeMillis, String entrySalt) {
        return rgbForIndex(index, glyphCount, timeMillis, entrySalt, 10);
    }

    public static int rgbForIndex(
            int index, int glyphCount, long timeMillis, String entrySalt, int level
    ) {
        return rgbForPhase(phaseForIndex(index, glyphCount, timeMillis, entrySalt, level), level);
    }

    /**
     * A stable phase driven by a monotonic timestamp. Tooltip code supplies a
     * nanoTime-derived value so wall-clock corrections cannot make colours jump.
     */
    public static double phaseForIndex(int index, int glyphCount, long monotoneMillis) {
        return phaseForIndex(index, glyphCount, monotoneMillis, "");
    }

    /**
     * Adds a deterministic per-enchantment phase from a stable registry or
     * description identifier. It never uses object identity or random state.
     */
    public static double phaseForIndex(
            int index,
            int glyphCount,
            long monotoneMillis,
            String entrySalt
    ) {
        return phaseForIndex(index, glyphCount, monotoneMillis, entrySalt, 10);
    }

    public static double phaseForIndex(
            int index,
            int glyphCount,
            long monotoneMillis,
            String entrySalt,
            int level
    ) {
        requireIndex(index);
        if (glyphCount < 1 || index >= glyphCount) {
            throw new IllegalArgumentException("glyphCount must be greater than index");
        }
        if (entrySalt == null) {
            throw new IllegalArgumentException("entrySalt must not be null");
        }

        long cycleMillis = cycleMillisForLevel(level);
        double timeProgress = Math.floorMod(monotoneMillis, cycleMillis) / (double) cycleMillis;
        double textProgress = glyphCount == 1 ? 0.0 : index / (double) (glyphCount - 1);
        return wrapUnit(timeProgress
                + textProgress * glyphHueSpanForLevel(level)
                + entryPhaseOffset(entrySalt));
    }

    public static long cycleMillisForLevel(int level) {
        long reduction = Math.max(0L, (long) level - 10L) * CYCLE_REDUCTION_PER_LEVEL;
        return Math.max(MINIMUM_CYCLE_MILLIS, LEVEL_TEN_CYCLE_MILLIS - reduction);
    }

    public static double glyphHueSpanForLevel(int level) {
        return cappedRamp(level, LEVEL_TEN_GLYPH_HUE_SPAN, 0.025D, MAXIMUM_GLYPH_HUE_SPAN);
    }

    public static double entryPhaseOffset(String entrySalt) {
        if (entrySalt == null) {
            throw new IllegalArgumentException("entrySalt must not be null");
        }
        if (entrySalt.isEmpty()) {
            return 0.0D;
        }
        long hash = 0xCBF29CE484222325L;
        for (int index = 0; index < entrySalt.length(); index++) {
            hash ^= entrySalt.charAt(index);
            hash *= 0x100000001B3L;
        }
        return (hash >>> 11) / (double) (1L << 53);
    }

    public static boolean isBold(int index, long timeMillis) {
        requireIndex(index);
        return false;
    }

    private static int rgbForPhase(double phase, int level) {
        double hue = wrapUnit(phase);
        double sector = hue * 6.0D;
        int sectorIndex = (int) sector;
        double fraction = sector - sectorIndex;
        double saturation = cappedRamp(level, LEVEL_TEN_SATURATION, 0.05D, MAXIMUM_SATURATION);
        double shimmer = cappedRamp(
                level, LEVEL_TEN_SHIMMER_AMPLITUDE, 0.016D, MAXIMUM_SHIMMER_AMPLITUDE
        );
        double brightness = BASE_BRIGHTNESS + shimmer * (
                0.5D + 0.5D * Math.sin(Math.PI * 2.0D * hue)
        );
        double p = brightness * (1.0D - saturation);
        double q = brightness * (1.0D - saturation * fraction);
        double t = brightness * (1.0D - saturation * (1.0D - fraction));
        double red;
        double green;
        double blue;
        switch (sectorIndex) {
            case 0 -> { red = brightness; green = t; blue = p; }
            case 1 -> { red = q; green = brightness; blue = p; }
            case 2 -> { red = p; green = brightness; blue = t; }
            case 3 -> { red = p; green = q; blue = brightness; }
            case 4 -> { red = t; green = p; blue = brightness; }
            default -> { red = brightness; green = p; blue = q; }
        }
        return clampChannel((int) Math.round(red * 255.0D)) << 16
                | clampChannel((int) Math.round(green * 255.0D)) << 8
                | clampChannel((int) Math.round(blue * 255.0D));
    }

    private static double wrapUnit(double value) {
        return value - Math.floor(value);
    }

    private static double cappedRamp(int level, double start, double perLevel, double cap) {
        return Math.min(cap, start + Math.max(0, level - 10) * perLevel);
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
