package com.liy.level10enchantments.mixin;

import com.liy.level10enchantments.EnchantmentRules;
import java.util.Set;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantment.class)
public abstract class EnchantmentMixin {
    private static final Set<String> ELYTRA_CHEST_ENCHANTMENTS = Set.of(
            "minecraft:protection",
            "minecraft:fire_protection",
            "minecraft:blast_protection",
            "minecraft:projectile_protection",
            "minecraft:thorns"
    );
    private static final Set<String> PROTECTION_GROUP = Set.of(
            "minecraft:protection",
            "minecraft:fire_protection",
            "minecraft:blast_protection",
            "minecraft:projectile_protection"
    );
    private static final Set<String> DAMAGE_GROUP = Set.of(
            "minecraft:sharpness",
            "minecraft:smite",
            "minecraft:bane_of_arthropods"
    );

    @Inject(method = "getMaxLevel", at = @At("HEAD"), cancellable = true)
    private void level10$raiseBaseMaximum(CallbackInfoReturnable<Integer> callback) {
        if (EnchantmentRules.find(level10$id((Enchantment) (Object) this)).isPresent()) {
            callback.setReturnValue(10);
        }
    }

    @Inject(method = "canEnchant", at = @At("HEAD"), cancellable = true)
    private void level10$allowSelectedChestEnchantmentsOnElytra(
            ItemStack stack,
            CallbackInfoReturnable<Boolean> callback
    ) {
        if (stack.is(Items.ELYTRA)
                && ELYTRA_CHEST_ENCHANTMENTS.contains(
                        level10$id((Enchantment) (Object) this)
                )) {
            callback.setReturnValue(true);
        }
    }

    @Inject(method = "isCompatibleWith", at = @At("HEAD"), cancellable = true)
    private void level10$allowSelectedCompatibility(
            Enchantment other,
            CallbackInfoReturnable<Boolean> callback
    ) {
        Enchantment self = (Enchantment) (Object) this;
        if (self == other) {
            return;
        }
        String selfId = level10$id(self);
        String otherId = level10$id(other);
        if ((PROTECTION_GROUP.contains(selfId) && PROTECTION_GROUP.contains(otherId))
                || (DAMAGE_GROUP.contains(selfId) && DAMAGE_GROUP.contains(otherId))) {
            callback.setReturnValue(true);
        }
    }

    private static String level10$id(Enchantment enchantment) {
        return BuiltInRegistries.ENCHANTMENT.getKey(enchantment).toString();
    }
}
