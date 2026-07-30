package com.liy.level10enchantments.mixin;

import com.liy.level10enchantments.EnchantmentLevelNormalizer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.EnchantRandomlyFunction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantRandomlyFunction.class)
public abstract class EnchantRandomlyFunctionMixin {
    @Inject(method = "run", at = @At("RETURN"))
    private void level10$capRandomlyGeneratedEnchantments(
            ItemStack stack,
            LootContext context,
            CallbackInfoReturnable<ItemStack> callback
    ) {
        EnchantmentLevelNormalizer.capToVanillaMaximum(callback.getReturnValue());
    }
}
