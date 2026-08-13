package com.liy.level10enchantments.mixin;

import com.liy.level10enchantments.EnchantmentLevelNames;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Stable fallback only. Dynamic styling belongs to the final tooltip phase. */
@Mixin(Enchantment.class)
public abstract class EnchantmentNameMixin {
    @Inject(method = "getFullname", at = @At("RETURN"), cancellable = true)
    private void level10$stableHighLevelFallback(int level, CallbackInfoReturnable<Component> callback) {
        if (level <= 13) {
            return;
        }
        Enchantment enchantment = (Enchantment) (Object) this;
        callback.setReturnValue(Component.translatable(enchantment.getDescriptionId())
                .append(Component.literal(" " + EnchantmentLevelNames.displayLevel(level)))
                .withStyle(callback.getReturnValue().getStyle()));
    }
}
