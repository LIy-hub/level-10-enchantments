package com.liy.level10enchantments.mixin;

import com.liy.level10enchantments.EnchantingLevelPolicy;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentMenu.class)
public abstract class EnchantmentMenuMixin {
    @Inject(method = "getEnchantmentList", at = @At("RETURN"), cancellable = true)
    private void level10$capTableEnchantments(
            ItemStack stack,
            int optionIndex,
            int displayedCost,
            CallbackInfoReturnable<List<EnchantmentInstance>> callback
    ) {
        List<EnchantmentInstance> transformed = new ArrayList<>();
        for (EnchantmentInstance instance : callback.getReturnValue()) {
            String id = BuiltInRegistries.ENCHANTMENT.getKey(
                    instance.enchantment
            ).toString();
            int level = EnchantingLevelPolicy.capToVanillaMaximum(id, instance.level);
            transformed.add(new EnchantmentInstance(instance.enchantment, level));
        }
        callback.setReturnValue(List.copyOf(transformed));
    }
}
