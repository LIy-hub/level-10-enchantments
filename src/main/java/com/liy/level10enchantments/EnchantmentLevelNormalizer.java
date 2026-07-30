package com.liy.level10enchantments;

import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public final class EnchantmentLevelNormalizer {
    private EnchantmentLevelNormalizer() {
    }

    public static void capToVanillaMaximum(ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }
        Map<Enchantment, Integer> enchantments =
                new LinkedHashMap<>(EnchantmentHelper.getEnchantments(stack));
        boolean changed = false;
        for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
            String id = EnchantmentHelper.getEnchantmentId(entry.getKey()).toString();
            EnchantmentRules.Rule rule = EnchantmentRules.find(id).orElse(null);
            if (rule != null && entry.getValue() > rule.vanillaMax()) {
                entry.setValue(rule.vanillaMax());
                changed = true;
            }
        }
        if (changed) {
            EnchantmentHelper.setEnchantments(enchantments, stack);
        }
    }
}
