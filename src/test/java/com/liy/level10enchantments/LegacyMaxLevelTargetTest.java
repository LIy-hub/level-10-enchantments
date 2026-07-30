package com.liy.level10enchantments;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import net.minecraft.SharedConstants;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.enchantment.Enchantment;

public final class LegacyMaxLevelTargetTest {
    private static final Set<String> MIXED_OVERRIDE_CLASSES = Set.of(
            "ArrowDamageEnchantment",
            "ArrowKnockbackEnchantment",
            "ArrowPiercingEnchantment",
            "DamageEnchantment",
            "DigDurabilityEnchantment",
            "DiggingEnchantment",
            "FireAspectEnchantment",
            "FrostWalkerEnchantment",
            "KnockbackEnchantment",
            "LootBonusEnchantment",
            "OxygenEnchantment",
            "ProtectionEnchantment",
            "SoulSpeedEnchantment",
            "SweepingEdgeEnchantment",
            "ThornsEnchantment",
            "TridentImpalerEnchantment",
            "TridentLoyaltyEnchantment",
            "TridentRiptideEnchantment"
    );

    private LegacyMaxLevelTargetTest() {
    }

    public static void main(String[] args) throws ReflectiveOperationException {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        for (Map.Entry<String, EnchantmentRules.Rule> rule :
                EnchantmentRules.all().entrySet()) {
            String id = rule.getKey();
            Enchantment enchantment = BuiltInRegistries.ENCHANTMENT.get(
                    new ResourceLocation(id)
            );
            require(enchantment != null, "registered enchantment " + id);
            Method maximum = enchantment.getClass().getMethod("getMaxLevel");
            Class<?> declaringClass = maximum.getDeclaringClass();
            require(
                    declaringClass == Enchantment.class
                            || MIXED_OVERRIDE_CLASSES.contains(declaringClass.getSimpleName()),
                    "Mixin target covers " + id + " via " + declaringClass.getName()
            );
        }
        System.out.println("PASS: every legacy rule is covered by a max-level Mixin target");
        System.exit(0);
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
