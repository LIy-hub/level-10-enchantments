package com.liy.level10enchantments.mixin;

import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(EnchantmentHelper.class)
public abstract class EnchantmentHelperMixin {
    @ModifyConstant(method = "getEnchantmentCost", constant = @Constant(intValue = 15))
    private static int level10$raiseBookshelfCap(int originalCap) {
        return 25;
    }
}
