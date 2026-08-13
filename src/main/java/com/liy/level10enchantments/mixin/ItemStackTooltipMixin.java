package com.liy.level10enchantments.mixin;

import com.liy.level10enchantments.TooltipDescriptionComponents;
import com.liy.level10enchantments.TooltipEnchantmentLinePolicy;
import com.liy.level10enchantments.TooltipRainbowComponents;
import java.util.List;
import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Fabric invokes ItemTooltipCallback from its default-priority RETURN injection.
 * Priority 500 deliberately runs afterward, once description rows are present.
 */
@Mixin(value = ItemStack.class, priority = 500)
public abstract class ItemStackTooltipMixin {
    @Inject(method = "getTooltipLines", at = @At("RETURN"))
    private void level10$colourCompletedTooltip(
            Player player,
            TooltipFlag tooltipFlag,
            CallbackInfoReturnable<List<Component>> callback
    ) {
        ItemStack stack = (ItemStack) (Object) this;
        Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
        if (enchantments.isEmpty()) {
            return;
        }

        List<Component> tooltip = callback.getReturnValue();
        long now = System.nanoTime() / 1_000_000L;
        for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
            int level = entry.getValue();
            if (level < 10) {
                continue;
            }
            Enchantment enchantment = entry.getKey();
            Component stableName = enchantment.getFullname(level);
            ResourceLocation registryId = BuiltInRegistries.ENCHANTMENT.getKey(enchantment);
            String salt = registryId == null
                    ? enchantment.getDescriptionId()
                    : registryId.toString();
            for (int lineIndex = 0; lineIndex < tooltip.size(); lineIndex++) {
                Component line = tooltip.get(lineIndex);
                if (line.equals(stableName)) {
                    TextColor originalColour = line.getStyle().getColor();
                    tooltip.set(lineIndex, TooltipRainbowComponents.colourName(line, salt, level, now));
                    level10$preserveStaticColourForDescription(
                            tooltip, lineIndex, originalColour, enchantment.getDescriptionId()
                    );
                    break;
                }
            }
        }
    }

    private static void level10$preserveStaticColourForDescription(
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
