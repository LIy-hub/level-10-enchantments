package com.liy.level10enchantments;

import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public final class EnchantmentLevelNormalizer {
    private EnchantmentLevelNormalizer() {
    }

    public static void capToVanillaMaximum(ItemStack stack) {
        if (stack.isEmpty() || !EnchantmentHelper.hasAnyEnchantments(stack)) {
            return;
        }
        EnchantmentHelper.updateEnchantments(stack, mutable -> {
            for (Holder<Enchantment> holder : List.copyOf(mutable.keySet())) {
                String id = holder.unwrapKey()
                        .map(key -> key.identifier().toString())
                        .orElse("");
                EnchantmentRules.Rule rule = EnchantmentRules.find(id).orElse(null);
                if (rule != null && mutable.getLevel(holder) > rule.vanillaMax()) {
                    mutable.set(holder, rule.vanillaMax());
                }
            }
        });
    }
}
