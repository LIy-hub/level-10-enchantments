package com.liy.level10enchantments.mixin;

import com.liy.level10enchantments.EnchantmentLevelNormalizer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentHelper.class)
public abstract class EnchantmentHelperMixin {
    @ModifyConstant(method = "getEnchantmentCost", constant = @Constant(intValue = 15))
    private static int level10$raiseBookshelfCap(int originalCap) {
        return 25;
    }

    @Inject(
            method = "enchantItem(Lnet/minecraft/world/flag/FeatureFlagSet;Lnet/minecraft/util/RandomSource;Lnet/minecraft/world/item/ItemStack;IZ)Lnet/minecraft/world/item/ItemStack;",
            at = @At("RETURN")
    )
    private static void level10$capGeneratedEnchantments(
            FeatureFlagSet enabledFeatures,
            RandomSource random,
            ItemStack stack,
            int level,
            boolean allowTreasure,
            CallbackInfoReturnable<ItemStack> callback
    ) {
        EnchantmentLevelNormalizer.capToVanillaMaximum(callback.getReturnValue());
    }

}
