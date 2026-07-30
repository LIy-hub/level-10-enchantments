package com.liy.level10enchantments.mixin;

import com.liy.level10enchantments.EnchantmentRules;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.enchantment.ArrowDamageEnchantment;
import net.minecraft.world.item.enchantment.ArrowKnockbackEnchantment;
import net.minecraft.world.item.enchantment.ArrowPiercingEnchantment;
import net.minecraft.world.item.enchantment.DamageEnchantment;
import net.minecraft.world.item.enchantment.DigDurabilityEnchantment;
import net.minecraft.world.item.enchantment.DiggingEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.FireAspectEnchantment;
import net.minecraft.world.item.enchantment.FrostWalkerEnchantment;
import net.minecraft.world.item.enchantment.KnockbackEnchantment;
import net.minecraft.world.item.enchantment.LootBonusEnchantment;
import net.minecraft.world.item.enchantment.OxygenEnchantment;
import net.minecraft.world.item.enchantment.ProtectionEnchantment;
import net.minecraft.world.item.enchantment.SoulSpeedEnchantment;
import net.minecraft.world.item.enchantment.SweepingEdgeEnchantment;
import net.minecraft.world.item.enchantment.ThornsEnchantment;
import net.minecraft.world.item.enchantment.TridentImpalerEnchantment;
import net.minecraft.world.item.enchantment.TridentLoyaltyEnchantment;
import net.minecraft.world.item.enchantment.TridentRiptideEnchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({
        ArrowDamageEnchantment.class,
        ArrowKnockbackEnchantment.class,
        ArrowPiercingEnchantment.class,
        DamageEnchantment.class,
        DigDurabilityEnchantment.class,
        DiggingEnchantment.class,
        FireAspectEnchantment.class,
        FrostWalkerEnchantment.class,
        KnockbackEnchantment.class,
        LootBonusEnchantment.class,
        OxygenEnchantment.class,
        ProtectionEnchantment.class,
        SoulSpeedEnchantment.class,
        SweepingEdgeEnchantment.class,
        ThornsEnchantment.class,
        TridentImpalerEnchantment.class,
        TridentLoyaltyEnchantment.class,
        TridentRiptideEnchantment.class
})
public abstract class ManagedMaxLevelMixin {
    @Inject(method = "getMaxLevel", at = @At("HEAD"), cancellable = true)
    private void level10$raiseManagedMaximum(CallbackInfoReturnable<Integer> callback) {
        Enchantment enchantment = (Enchantment) (Object) this;
        String id = BuiltInRegistries.ENCHANTMENT.getKey(enchantment).toString();
        if (EnchantmentRules.find(id).isPresent()) {
            callback.setReturnValue(10);
        }
    }
}
