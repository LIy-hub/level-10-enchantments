package com.liy.level10enchantments.mixin;

import com.liy.level10enchantments.BreakthroughSelector;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.core.RegistryAccess;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.EnchantmentMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentMenu.class)
public abstract class EnchantmentMenuMixin {
    @Shadow
    @Final
    private RandomSource random;

    @Inject(method = "getEnchantmentList", at = @At("RETURN"), cancellable = true)
    private void level10$applyBreakthroughs(
            RegistryAccess access,
            ItemStack stack,
            int optionIndex,
            int displayedCost,
            CallbackInfoReturnable<List<EnchantmentInstance>> callback
    ) {
        List<EnchantmentInstance> transformed = new ArrayList<>();
        for (EnchantmentInstance instance : callback.getReturnValue()) {
            String id = instance.enchantment().unwrapKey()
                    .map(key -> key.identifier().toString())
                    .orElse("");
            int level = BreakthroughSelector.selectLevel(
                    id,
                    instance.level(),
                    optionIndex,
                    displayedCost,
                    random::nextDouble
            );
            transformed.add(new EnchantmentInstance(instance.enchantment(), level));
        }
        callback.setReturnValue(List.copyOf(transformed));
    }
}
