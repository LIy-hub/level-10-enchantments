package com.liy.level10enchantments;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.contents.TranslatableContents;

/** Structural component operations used by the final client tooltip pass. */
public final class TooltipDescriptionComponents {
    private TooltipDescriptionComponents() {
    }

    public static boolean containsDescriptionKey(Component candidate, String enchantmentDescriptionId) {
        if (isDescriptionKeyFor(candidate, enchantmentDescriptionId)) {
            return true;
        }
        for (Component sibling : candidate.getSiblings()) {
            if (containsDescriptionKey(sibling, enchantmentDescriptionId)) {
                return true;
            }
        }
        return false;
    }

    public static MutableComponent recolourDescriptionTree(Component original, TextColor colour) {
        MutableComponent recoloured = MutableComponent.create(original.getContents())
                .withStyle(original.getStyle().withColor(colour));
        for (Component sibling : original.getSiblings()) {
            recoloured.append(recolourDescriptionTree(sibling, colour));
        }
        return recoloured;
    }

    private static boolean isDescriptionKeyFor(Component component, String enchantmentDescriptionId) {
        if (!(component.getContents() instanceof TranslatableContents translated)) {
            return false;
        }
        return TooltipEnchantmentLinePolicy.isDescriptionKeyFor(
                enchantmentDescriptionId, translated.getKey()
        );
    }
}
