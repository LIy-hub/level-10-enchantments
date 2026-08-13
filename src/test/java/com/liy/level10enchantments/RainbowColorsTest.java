package com.liy.level10enchantments;

public final class RainbowColorsTest {
    public static void main(String[] args) {
        int start = RainbowColors.rgbForIndex(0, 12, 0L);
        require(start == RainbowColors.rgbForIndex(0, 12, RainbowColors.cycleMillisForLevel(10)),
                "level X cycle loops cleanly");
        require(RainbowColors.rgbForIndex(0, 12, 400L) != start,
                "colour visibly advances within 400ms");
        require(RainbowColors.rgbForIndex(1, 12, 0L) != start,
                "neighboring characters have a stable hue offset");
        require(close(RainbowColors.phaseForIndex(0, 12, 0L), 0.0D),
                "monotone phase starts at zero");
        require(close(RainbowColors.phaseForIndex(
                        0, 12, RainbowColors.cycleMillisForLevel(10)
                ), 0.0D),
                "monotone phase wraps at the cycle boundary");
        require(RainbowColors.phaseForIndex(1, 12, 0L)
                        > RainbowColors.phaseForIndex(0, 12, 0L),
                "per-glyph phase ordering is deterministic");
        double sharpnessOffset = RainbowColors.entryPhaseOffset("minecraft:sharpness");
        require(close(sharpnessOffset, RainbowColors.entryPhaseOffset("minecraft:sharpness")),
                "same registry salt has a deterministic entry phase");
        require(!close(sharpnessOffset, RainbowColors.entryPhaseOffset("minecraft:smite")),
                "different registry salts use different entry phases");
        int saltedStart = RainbowColors.rgbForIndex(0, 12, 0L, "minecraft:sharpness");
        require(saltedStart == RainbowColors.rgbForIndex(
                        0, 12, RainbowColors.cycleMillisForLevel(10), "minecraft:sharpness"
                ), "salted entry phase still closes at a full cycle");
        require(saltedStart != RainbowColors.rgbForIndex(
                        0, 12, 0L, "minecraft:smite"
                ), "different enchantments are visibly de-synchronised");
        require(RainbowColors.cycleMillisForLevel(11) < RainbowColors.cycleMillisForLevel(10),
                "XI animation is faster than X");
        require(RainbowColors.cycleMillisForLevel(13) < RainbowColors.cycleMillisForLevel(11),
                "XIII animation is faster than XI");
        require(RainbowColors.cycleMillisForLevel(100) == 3_000L,
                "very high levels respect the minimum cycle bound");
        require(RainbowColors.glyphHueSpanForLevel(13) > RainbowColors.glyphHueSpanForLevel(10),
                "higher levels have richer hue span");
        require(RainbowColors.glyphHueSpanForLevel(100) == RainbowColors.glyphHueSpanForLevel(101),
                "high-level hue richness is capped");
        int nearbyFrame = RainbowColors.rgbForIndex(0, 12, 16L);
        require(colorDistance(start, nearbyFrame) <= 8,
                "fast animation remains smooth between adjacent frames");
        boolean rejected = false;
        try {
            RainbowColors.rgbForIndex(-1);
        } catch (IllegalArgumentException expected) {
            rejected = true;
        }
        require(rejected, "negative index rejected");
        System.out.println("PASS: fast stable high-level HSB gradient with deterministic entry phases");
    }

    private static int colorDistance(int first, int second) {
        int red = Math.abs((first >> 16 & 0xFF) - (second >> 16 & 0xFF));
        int green = Math.abs((first >> 8 & 0xFF) - (second >> 8 & 0xFF));
        int blue = Math.abs((first & 0xFF) - (second & 0xFF));
        return red + green + blue;
    }

    private static boolean close(double first, double second) {
        return Math.abs(first - second) < 0.0000001D;
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
