package com.liy.level10enchantments.mixin;

import com.liy.level10enchantments.AnvilCostPolicy;
import com.liy.level10enchantments.AnvilLevelMergePolicy;
import com.liy.level10enchantments.CompatibilitySurcharge;
import com.liy.level10enchantments.EnchantmentRules;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilMenu.class)
public abstract class AnvilMenuMixin {
    @Shadow
    @Final
    private DataSlot cost;

    @Inject(
            method = "createResult",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/inventory/DataSlot;get()I",
                    ordinal = 1,
                    shift = At.Shift.BEFORE
            ),
            require = 1
    )
    private void level10$capBeforeTooExpensiveCheck(CallbackInfo callback) {
        cost.set(AnvilCostPolicy.cap(cost.get()));
    }

    @ModifyConstant(
            method = "createResult",
            constant = @Constant(intValue = 40, ordinal = 2),
            require = 1
    )
    private int level10$raiseTooExpensiveThreshold(int originalThreshold) {
        return AnvilCostPolicy.MAX_COST + 1;
    }

    @Inject(method = "createResult", at = @At("RETURN"))
    private void level10$finalizeAnvilCost(CallbackInfo callback) {
        AnvilMenu menu = (AnvilMenu) (Object) this;
        ItemStack result = menu.getSlot(AnvilMenu.RESULT_SLOT).getItem();
        if (result.isEmpty()) {
            cost.set(AnvilCostPolicy.cap(cost.get()));
            return;
        }

        int preventedIncrementCost = balanceResultLevels(
                menu.getSlot(AnvilMenu.INPUT_SLOT).getItem(),
                menu.getSlot(AnvilMenu.ADDITIONAL_SLOT).getItem(),
                result
        );
        cost.set(Math.max(0, cost.get() - preventedIncrementCost));

        int surcharge = CompatibilitySurcharge.calculate(
                enchantmentLevels(menu.getSlot(AnvilMenu.INPUT_SLOT).getItem()),
                enchantmentLevels(menu.getSlot(AnvilMenu.ADDITIONAL_SLOT).getItem()),
                enchantmentLevels(result)
        );
        cost.set(AnvilCostPolicy.addAndCap(cost.get(), surcharge));
    }

    private static Map<String, Integer> enchantmentLevels(ItemStack stack) {
        ItemEnchantments enchantments = EnchantmentHelper.getEnchantmentsForCrafting(stack);
        LinkedHashMap<String, Integer> levels = new LinkedHashMap<>();
        for (Holder<Enchantment> holder : enchantments.keySet()) {
            holder.unwrapKey().ifPresent(key -> levels.put(
                    key.location().toString(),
                    enchantments.getLevel(holder.value())
            ));
        }
        return levels;
    }

    private static int balanceResultLevels(ItemStack left, ItemStack right, ItemStack result) {
        ItemEnchantments leftEnchantments = EnchantmentHelper.getEnchantmentsForCrafting(left);
        ItemEnchantments rightEnchantments = EnchantmentHelper.getEnchantmentsForCrafting(right);
        boolean rightIsBook = right.has(DataComponents.STORED_ENCHANTMENTS);
        int[] preventedCost = {0};

        EnchantmentHelper.updateEnchantments(result, mutable -> {
            for (Holder<Enchantment> holder : List.copyOf(mutable.keySet())) {
                Enchantment enchantment = holder.value();
                String id = holder.unwrapKey()
                        .map(key -> key.location().toString())
                        .orElse("");
                EnchantmentRules.Rule rule = EnchantmentRules.find(id).orElse(null);
                if (rule == null) {
                    continue;
                }

                int leftLevel = leftEnchantments.getLevel(enchantment);
                int rightLevel = rightEnchantments.getLevel(enchantment);
                int mergedLevel = AnvilLevelMergePolicy.merge(
                        rule.vanillaMax(),
                        leftLevel,
                        rightLevel
                );
                mutable.set(enchantment, mergedLevel);

                if (AnvilLevelMergePolicy.preventsIncrement(
                        rule.vanillaMax(),
                        leftLevel,
                        rightLevel
                )) {
                    int unitCost = enchantment.getAnvilCost();
                    if (rightIsBook) {
                        unitCost = Math.max(1, unitCost / 2);
                    }
                    preventedCost[0] += unitCost;
                }
            }
        });
        return preventedCost[0];
    }
}
