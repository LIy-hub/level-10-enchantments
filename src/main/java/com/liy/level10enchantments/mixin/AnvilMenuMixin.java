package com.liy.level10enchantments.mixin;

import com.liy.level10enchantments.AnvilCostPolicy;
import com.liy.level10enchantments.AnvilLevelMergePolicy;
import com.liy.level10enchantments.CompatibilitySurcharge;
import com.liy.level10enchantments.EnchantmentRules;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
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
        LinkedHashMap<String, Integer> levels = new LinkedHashMap<>();
        for (Map.Entry<Enchantment, Integer> entry :
                EnchantmentHelper.getEnchantments(stack).entrySet()) {
            levels.put(enchantmentId(entry.getKey()), entry.getValue());
        }
        return levels;
    }

    private static int balanceResultLevels(ItemStack left, ItemStack right, ItemStack result) {
        Map<Enchantment, Integer> leftEnchantments = EnchantmentHelper.getEnchantments(left);
        Map<Enchantment, Integer> rightEnchantments = EnchantmentHelper.getEnchantments(right);
        Map<Enchantment, Integer> resultEnchantments =
                new LinkedHashMap<>(EnchantmentHelper.getEnchantments(result));
        boolean rightIsBook = right.is(Items.ENCHANTED_BOOK);
        int preventedCost = 0;

        for (Map.Entry<Enchantment, Integer> entry : resultEnchantments.entrySet()) {
            Enchantment enchantment = entry.getKey();
            String id = enchantmentId(enchantment);
            EnchantmentRules.Rule rule = EnchantmentRules.find(id).orElse(null);
            if (rule == null) {
                continue;
            }

            int leftLevel = leftEnchantments.getOrDefault(enchantment, 0);
            int rightLevel = rightEnchantments.getOrDefault(enchantment, 0);
            int mergedLevel = AnvilLevelMergePolicy.merge(
                    rule.vanillaMax(),
                    leftLevel,
                    rightLevel
            );
            entry.setValue(mergedLevel);

            if (AnvilLevelMergePolicy.preventsIncrement(
                    rule.vanillaMax(),
                    leftLevel,
                    rightLevel
            )) {
                int unitCost = enchantment.getRarity().getWeight();
                if (rightIsBook) {
                    unitCost = Math.max(1, unitCost / 2);
                }
                preventedCost += unitCost;
            }
        }
        EnchantmentHelper.setEnchantments(resultEnchantments, result);
        return preventedCost;
    }

    private static String enchantmentId(Enchantment enchantment) {
        return BuiltInRegistries.ENCHANTMENT.getKey(enchantment).toString();
    }
}
