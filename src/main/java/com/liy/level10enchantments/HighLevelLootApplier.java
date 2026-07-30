package com.liy.level10enchantments;

import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.storage.loot.LootContext;

public final class HighLevelLootApplier {
    private HighLevelLootApplier() {
    }

    public static void apply(
            LootBalancePolicy.Profile profile,
            List<ItemStack> generatedItems,
            LootContext context
    ) {
        RandomSource random = context.getRandom();
        for (ItemStack stack : generatedItems) {
            promoteExistingEnchantments(profile, stack, random);
        }

        int extraBookLevel = LootBalancePolicy.selectHighLevel(profile, random.nextDouble());
        if (extraBookLevel == 0) {
            return;
        }
        ItemStack extraBook = createExtraBook(profile, extraBookLevel, context, random);
        if (!extraBook.isEmpty()) {
            generatedItems.add(extraBook);
        }
    }

    private static void promoteExistingEnchantments(
            LootBalancePolicy.Profile profile,
            ItemStack stack,
            RandomSource random
    ) {
        if (stack.isEmpty()) {
            return;
        }
        Map<Enchantment, Integer> enchantments =
                new LinkedHashMap<>(EnchantmentHelper.getEnchantments(stack));
        if (enchantments.isEmpty()) {
            return;
        }
        for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
            String id = enchantmentId(entry.getKey());
            EnchantmentRules.Rule rule = EnchantmentRules.find(id).orElse(null);
            if (rule == null) {
                continue;
            }

            int level = Math.min(entry.getValue(), rule.vanillaMax());
            if (LootBalancePolicy.allowsHighLevel(profile, id)) {
                int selectedLevel = LootBalancePolicy.selectHighLevel(profile, random.nextDouble());
                if (selectedLevel != 0) {
                    level = selectedLevel;
                }
            }
            entry.setValue(level);
        }
        EnchantmentHelper.setEnchantments(enchantments, stack);
    }

    private static ItemStack createExtraBook(
            LootBalancePolicy.Profile profile,
            int level,
            LootContext context,
            RandomSource random
    ) {
        List<Enchantment> candidates = BuiltInRegistries.ENCHANTMENT.stream()
                .filter(enchantment -> LootBalancePolicy.allowsHighLevel(
                        profile,
                        enchantmentId(enchantment)
                ))
                .toList();
        if (candidates.isEmpty()) {
            return ItemStack.EMPTY;
        }
        Enchantment enchantment = candidates.get(random.nextInt(candidates.size()));
        return EnchantedBookItem.createForEnchantment(new EnchantmentInstance(enchantment, level));
    }

    private static String enchantmentId(Enchantment enchantment) {
        return BuiltInRegistries.ENCHANTMENT.getKey(enchantment).toString();
    }
}
