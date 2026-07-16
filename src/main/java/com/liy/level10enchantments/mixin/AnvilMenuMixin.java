package com.liy.level10enchantments.mixin;

import com.liy.level10enchantments.AnvilCostPolicy;
import com.liy.level10enchantments.CompatibilitySurcharge;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.core.Holder;
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
                    key.identifier().toString(),
                    enchantments.getLevel(holder)
            ));
        }
        return levels;
    }

}
