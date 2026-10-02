package com.liy.level10enchantments;

import net.minecraft.SharedConstants;
import net.fabricmc.loader.api.FabricLoader;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.assertTrue;
import net.minecraft.server.Bootstrap;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/** Applies the common mixins with Fabric Loader without opening a server or world. */
public final class MixinBootstrapTest {
    @Test
    void commonMixinTargetsLoadWithMinecraft263() {
        assertTrue(FabricLoader.getInstance().isModLoaded("level10enchantments"));
        assertDoesNotThrow(() -> {
            SharedConstants.tryDetectVersion();
            Bootstrap.bootStrap();
            for (String target : new String[] {
                    "net.minecraft.world.entity.npc.villager.AbstractVillager",
                    "net.minecraft.world.inventory.AnvilMenu",
                    "net.minecraft.world.inventory.EnchantmentMenu",
                    "net.minecraft.world.item.enchantment.EnchantmentHelper",
                    "net.minecraft.world.item.enchantment.Enchantment",
                    "net.minecraft.world.level.storage.loot.LootTable",
                    "net.minecraft.world.level.storage.loot.functions.EnchantRandomlyFunction"}) {
                Class<?> transformed = Class.forName(target);
                assertTrue(Arrays.stream(transformed.getDeclaredMethods())
                        .anyMatch(method -> method.getName().contains("level10$")),
                        () -> "Level 10 mixin was not applied to " + target);
            }
        });
    }
}
