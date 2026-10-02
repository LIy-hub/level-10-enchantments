package com.liy.level10enchantments;

import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/** Applies the common mixins with Fabric Loader without opening a server or world. */
public final class MixinBootstrapTest {
    @Test
    void commonMixinTargetsLoadWithMinecraft263() {
        assertDoesNotThrow(() -> {
            SharedConstants.tryDetectVersion();
            Bootstrap.bootStrap();
            for (String target : new String[] {
                    "net.minecraft.world.entity.npc.villager.AbstractVillager",
                    "net.minecraft.world.inventory.AnvilMenu",
                    "net.minecraft.world.inventory.EnchantmentMenu",
                    "net.minecraft.world.item.enchantment.EnchantmentHelper",
                    "net.minecraft.world.level.storage.loot.LootTable",
                    "net.minecraft.world.level.storage.loot.functions.EnchantRandomlyFunction"}) {
                Class.forName(target);
            }
        });
    }
}
