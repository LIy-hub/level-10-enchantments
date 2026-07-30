package com.liy.level10enchantments.mixin;

import com.liy.level10enchantments.LegacyEffectPolicy;
import java.util.Map;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ExperienceOrb.class)
public abstract class ExperienceOrbMixin {
    @Inject(method = "repairPlayerItems", at = @At("HEAD"), cancellable = true)
    private void level10$scaleMendingByLevel(
            Player player,
            int experience,
            CallbackInfoReturnable<Integer> callback
    ) {
        int remaining = experience;
        while (remaining > 0) {
            Map.Entry<EquipmentSlot, ItemStack> selected =
                    EnchantmentHelper.getRandomItemWith(
                            Enchantments.MENDING,
                            player,
                            ItemStack::isDamaged
                    );
            if (selected == null) {
                callback.setReturnValue(remaining);
                return;
            }

            ItemStack stack = selected.getValue();
            int level = EnchantmentHelper.getItemEnchantmentLevel(
                    Enchantments.MENDING,
                    stack
            );
            int capacity = LegacyEffectPolicy.mendingRepairCapacity(remaining, level);
            int repaired = Math.min(capacity, stack.getDamageValue());
            stack.setDamageValue(stack.getDamageValue() - repaired);
            if (repaired <= 0) {
                callback.setReturnValue(remaining);
                return;
            }
            remaining = LegacyEffectPolicy.remainingExperience(
                    remaining,
                    repaired,
                    capacity
            );
        }
        callback.setReturnValue(0);
    }
}
