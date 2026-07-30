package com.liy.level10enchantments;

import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.RandomSource;
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
        if (stack.isEmpty() || !EnchantmentHelper.hasAnyEnchantments(stack)) {
            return;
        }
        EnchantmentHelper.updateEnchantments(stack, mutable -> {
            for (Holder<Enchantment> holder : List.copyOf(mutable.keySet())) {
                String id = enchantmentId(holder);
                EnchantmentRules.Rule rule = EnchantmentRules.find(id).orElse(null);
                if (rule == null) {
                    continue;
                }

                int level = Math.min(mutable.getLevel(holder), rule.vanillaMax());
                if (LootBalancePolicy.allowsHighLevel(profile, id)) {
                    int selectedLevel = LootBalancePolicy.selectHighLevel(profile, random.nextDouble());
                    if (selectedLevel != 0) {
                        level = selectedLevel;
                    }
                }
                mutable.set(holder, level);
            }
        });
    }

    private static ItemStack createExtraBook(
            LootBalancePolicy.Profile profile,
            int level,
            LootContext context,
            RandomSource random
    ) {
        HolderLookup.RegistryLookup<Enchantment> registry = context.getLevel()
                .registryAccess()
                .lookupOrThrow(Registries.ENCHANTMENT);
        List<Holder.Reference<Enchantment>> candidates = registry.listElements()
                .filter(holder -> LootBalancePolicy.allowsHighLevel(profile, enchantmentId(holder)))
                .toList();
        if (candidates.isEmpty()) {
            return ItemStack.EMPTY;
        }
        Holder<Enchantment> enchantment = candidates.get(random.nextInt(candidates.size()));
        return EnchantmentHelper.createBook(new EnchantmentInstance(enchantment, level));
    }

    private static String enchantmentId(Holder<Enchantment> holder) {
        return holder.unwrapKey()
                .map(key -> key.identifier().toString())
                .orElse("");
    }
}
