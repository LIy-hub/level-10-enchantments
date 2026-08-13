package com.liy.level10enchantments;

import net.minecraft.network.chat.Component;

public final class TooltipRainbowComponentsTest {
    public static void main(String[] args) {
        Component original = Component.literal("A").withStyle(style -> style
                .withBold(true)
                .withItalic(true)
        ).append(Component.literal("B").withStyle(style -> style
                .withUnderlined(true)
                .withInsertion("preserve-me")
        ));

        Component coloured = TooltipRainbowComponents.colourName(
                original, "minecraft:sharpness", 13, 1_234L
        );
        require("AB".equals(coloured.getString()), "visitor keeps all text in source order");
        require(coloured.getSiblings().size() == 2, "each styled code point stays independently coloured");
        require(coloured.getSiblings().get(0).getStyle().isBold(), "effective bold survives");
        require(coloured.getSiblings().get(0).getStyle().isItalic(), "effective italic survives");
        require(coloured.getSiblings().get(1).getStyle().isUnderlined(), "sibling underline survives");
        require("preserve-me".equals(coloured.getSiblings().get(1).getStyle().getInsertion()),
                "sibling insertion survives");
        require(coloured.getSiblings().get(0).getStyle().getColor() != null,
                "animation overrides only the colour field");
        System.out.println("PASS: styled tooltip rainbow retains effective formatting");
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
