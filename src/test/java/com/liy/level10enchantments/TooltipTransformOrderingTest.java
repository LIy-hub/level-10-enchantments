package com.liy.level10enchantments;

import net.minecraft.network.chat.Component;

/** Regression coverage for Enchantment Descriptions' Component.equals lookup. */
public final class TooltipTransformOrderingTest {
    public static void main(String[] args) {
        Component stableName = Component.literal("Sharpness X");
        Component rainbowName = TooltipRainbowComponents.colourName(
                stableName, "minecraft:sharpness", 10, 1_234L
        );
        require(TooltipTransformOrdering.descriptionsCanMatchStableName(
                        false, stableName.equals(stableName)),
                "the default callback phase matches the untouched stable name");
        require(!stableName.equals(rainbowName),
                "the rainbow result is intentionally not equals-compatible with the stable name");
        require(!TooltipTransformOrdering.descriptionsCanMatchStableName(
                        true, rainbowName.equals(stableName)),
                "transforming in or before default would reproduce the missing-description bug");
        System.out.println("PASS: after-tooltip phase preserves stable-name description matching");
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
