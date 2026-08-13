package com.liy.level10enchantments;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;

public final class TooltipEnchantmentLinePolicyTest {
    public static void main(String[] args) {
        String sharpness = "enchantment.minecraft.sharpness";
        String smite = "enchantment.minecraft.smite";

        require(TooltipEnchantmentLinePolicy.isDescriptionKeyFor(sharpness, sharpness + ".desc"),
                "primary description key belongs to its enchantment");
        require(TooltipEnchantmentLinePolicy.isDescriptionKeyFor(sharpness, sharpness + ".description"),
                "fallback description key belongs to its enchantment");
        require(!TooltipEnchantmentLinePolicy.isDescriptionKeyFor(sharpness, smite + ".desc"),
                "adjacent enchantments keep description ownership separate");
        require(!TooltipEnchantmentLinePolicy.isDescriptionKeyFor(sharpness, "thirdparty.tooltip.desc"),
                "a third-party description-looking key is never enough");
        require(TooltipEnchantmentLinePolicy.colourDescriptionLine(true, true),
                "a coloured enchantment may tint its exact explanation component");
        require(!TooltipEnchantmentLinePolicy.colourDescriptionLine(false, true),
                "an uncoloured enchantment preserves the description style");
        require(!TooltipEnchantmentLinePolicy.colourDescriptionLine(true, false),
                "no exact key means lore and attributes cannot be tinted");

        Component sharpnessDescription = Component.translatable(sharpness + ".desc");
        Component smiteDescription = Component.translatable(smite + ".description");
        Component lore = Component.literal("Custom lore");
        List<Component> realSequence = List.of(
                Component.literal("Sharpness X"), sharpnessDescription,
                Component.literal("Smite X"), smiteDescription,
                Component.literal("Unbreaking X"), lore,
                Component.translatable("thirdparty.tooltip.desc")
        );
        require(TooltipDescriptionComponents.containsDescriptionKey(realSequence.get(1), sharpness),
                "first enchantment owns only its exact description row");
        require(TooltipDescriptionComponents.containsDescriptionKey(realSequence.get(3), smite),
                "second enchantment uses its own fallback description row");
        require(!TooltipDescriptionComponents.containsDescriptionKey(
                        realSequence.get(5), "enchantment.minecraft.unbreaking"),
                "an enchantment with no description cannot capture following lore");
        require(!TooltipDescriptionComponents.containsDescriptionKey(realSequence.get(6), sharpness),
                "a third-party description key never belongs to sharpness");

        Component indentedDescription = Component.literal("  ").append(
                Component.translatable(sharpness + ".desc").withStyle(ChatFormatting.DARK_GRAY)
        );
        TextColor preservedColour = TextColor.fromRgb(0x33AAFF);
        require(TooltipDescriptionComponents.containsDescriptionKey(indentedDescription, sharpness),
                "an indented literal root finds its translated sibling recursively");
        Component recoloured = TooltipDescriptionComponents.recolourDescriptionTree(
                indentedDescription, preservedColour
        );
        require(preservedColour.equals(recoloured.getStyle().getColor()),
                "the literal indent root receives the preserved static colour");
        require(preservedColour.equals(recoloured.getSiblings().get(0).getStyle().getColor()),
                "the dark-grey translated child is recursively recoloured too");
        System.out.println("PASS: tooltip description colour boundary policy");
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
