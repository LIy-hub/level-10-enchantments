package com.liy.level10enchantments.mixin;

import com.liy.level10enchantments.LegacyEffectPolicy;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.ThornsEnchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ThornsEnchantment.class)
public abstract class ThornsEnchantmentMixin {
    @Inject(method = "doPostHurt", at = @At("HEAD"), cancellable = true)
    private void level10$applyBalancedRetaliation(
            LivingEntity wearer,
            Entity attacker,
            int level,
            CallbackInfo callback
    ) {
        RandomSource random = wearer.getRandom();
        if (attacker != null && random.nextFloat() < LegacyEffectPolicy.thornsChance(level)) {
            float minimum = LegacyEffectPolicy.thornsMinimumDamage(level);
            float maximum = LegacyEffectPolicy.thornsMaximumDamage(level);
            float damage = minimum + random.nextFloat() * (maximum - minimum);
            attacker.hurt(wearer.damageSources().thorns(wearer), damage);
        }
        callback.cancel();
    }

    @Inject(method = "shouldHit", at = @At("HEAD"), cancellable = true)
    private static void level10$useBalancedChance(
            int level,
            RandomSource random,
            CallbackInfoReturnable<Boolean> callback
    ) {
        callback.setReturnValue(random.nextFloat() < LegacyEffectPolicy.thornsChance(level));
    }

    @Inject(method = "getDamage", at = @At("HEAD"), cancellable = true)
    private static void level10$useBalancedDamage(
            int level,
            RandomSource random,
            CallbackInfoReturnable<Integer> callback
    ) {
        float minimum = LegacyEffectPolicy.thornsMinimumDamage(level);
        float maximum = LegacyEffectPolicy.thornsMaximumDamage(level);
        callback.setReturnValue((int) (minimum + random.nextFloat() * (maximum - minimum)));
    }
}
