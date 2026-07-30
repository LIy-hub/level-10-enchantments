package com.liy.level10enchantments.mixin;

import com.liy.level10enchantments.EnchantmentLevelNormalizer;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.providers.EnchantmentProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentHelper.class)
public abstract class EnchantmentHelperMixin {
    @ModifyConstant(method = "getEnchantmentCost", constant = @Constant(intValue = 15))
    private static int level10$raiseBookshelfCap(int originalCap) {
        return 25;
    }

    @Inject(
            method = "enchantItem(Lnet/minecraft/util/RandomSource;Lnet/minecraft/world/item/ItemStack;ILnet/minecraft/core/RegistryAccess;Ljava/util/Optional;)Lnet/minecraft/world/item/ItemStack;",
            at = @At("RETURN")
    )
    private static void level10$capRegistryGeneratedEnchantments(
            RandomSource random,
            ItemStack stack,
            int level,
            RegistryAccess access,
            Optional<? extends HolderSet<Enchantment>> options,
            CallbackInfoReturnable<ItemStack> callback
    ) {
        EnchantmentLevelNormalizer.capToVanillaMaximum(callback.getReturnValue());
    }

    @Inject(
            method = "enchantItem(Lnet/minecraft/util/RandomSource;Lnet/minecraft/world/item/ItemStack;ILjava/util/stream/Stream;)Lnet/minecraft/world/item/ItemStack;",
            at = @At("RETURN")
    )
    private static void level10$capStreamGeneratedEnchantments(
            RandomSource random,
            ItemStack stack,
            int level,
            Stream<Holder<Enchantment>> options,
            CallbackInfoReturnable<ItemStack> callback
    ) {
        EnchantmentLevelNormalizer.capToVanillaMaximum(callback.getReturnValue());
    }

    @Inject(method = "enchantItemFromProvider", at = @At("RETURN"))
    private static void level10$capProviderGeneratedEnchantments(
            ItemStack stack,
            RegistryAccess access,
            ResourceKey<EnchantmentProvider> provider,
            DifficultyInstance difficulty,
            RandomSource random,
            CallbackInfo callback
    ) {
        EnchantmentLevelNormalizer.capToVanillaMaximum(stack);
    }
}
