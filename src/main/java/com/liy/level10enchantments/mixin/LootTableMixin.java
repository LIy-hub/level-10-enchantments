package com.liy.level10enchantments.mixin;

import com.liy.level10enchantments.HighLevelLootApplier;
import com.liy.level10enchantments.LootBalancePolicy;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Optional;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LootTable.class)
public abstract class LootTableMixin {
    @Shadow
    @Final
    private Optional<Identifier> randomSequence;

    @Inject(
            method = "getRandomItems(Lnet/minecraft/world/level/storage/loot/LootContext;)Lit/unimi/dsi/fastutil/objects/ObjectArrayList;",
            at = @At("RETURN")
    )
    private void level10$applyHighLevelLoot(
            LootContext context,
            CallbackInfoReturnable<ObjectArrayList<ItemStack>> callback
    ) {
        LootBalancePolicy.Profile profile = randomSequence
                .flatMap(identifier -> LootBalancePolicy.profileFor(identifier.toString()))
                .orElse(null);
        String dimensionId = context.getLevel().dimension().identifier().toString();
        if (profile != null && LootBalancePolicy.allowsHighLevelInDimension(profile, dimensionId)) {
            HighLevelLootApplier.apply(profile, callback.getReturnValue(), context);
        }
    }
}
