package com.liy.level10enchantments;

import java.util.List;
import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

/** Final, client-only tooltip operation, invoked after description callbacks. */
public final class HighLevelTooltipTransformer {
    private HighLevelTooltipTransformer() {
    }

    public static void transform(ItemStack stack, List<Component> tooltip) {
        transform(stack, tooltip, System.nanoTime() / 1_000_000L);
    }

    static void transform(ItemStack stack, List<Component> tooltip, long now) {
        Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
        if (enchantments.isEmpty()) {
            return;
        }

        for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
            int level = entry.getValue();
            if (level < 10) {
                continue;
            }
            Enchantment enchantment = entry.getKey();
            Component stableName = enchantment.getFullname(level);
            ResourceLocation registryId = BuiltInRegistries.ENCHANTMENT.getKey(enchantment);
            String salt = registryId == null ? enchantment.getDescriptionId() : registryId.toString();
            for (int lineIndex = 0; lineIndex < tooltip.size(); lineIndex++) {
                Component line = tooltip.get(lineIndex);
                if (!line.equals(stableName)) {
                    continue;
                }
                TextColor originalColour = line.getStyle().getColor();
                tooltip.set(lineIndex, TooltipRainbowComponents.colourName(line, salt, level, now));
                preserveStaticColourForDescription(
                        tooltip, lineIndex, originalColour, enchantment.getDescriptionId()
                );
                break;
            }
        }
    }

    private static void preserveStaticColourForDescription(
            List<Component> tooltip,
            int enchantmentLine,
            TextColor originalColour,
            String enchantmentDescriptionId
    ) {
        for (int descriptionLine = enchantmentLine + 1;
             descriptionLine < tooltip.size();
             descriptionLine++) {
            Component candidate = tooltip.get(descriptionLine);
            if (!TooltipEnchantmentLinePolicy.colourDescriptionLine(
                    originalColour != null,
                    TooltipDescriptionComponents.containsDescriptionKey(
                            candidate, enchantmentDescriptionId
                    )
            )) {
                return;
            }
            tooltip.set(descriptionLine, TooltipDescriptionComponents.recolourDescriptionTree(
                    candidate, originalColour
            ));
        }
    }
}
