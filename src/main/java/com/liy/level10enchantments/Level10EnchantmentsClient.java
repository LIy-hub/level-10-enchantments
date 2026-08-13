package com.liy.level10enchantments;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.event.Event;
import net.minecraft.resources.ResourceLocation;

/** Registers the final tooltip colour pass in an explicit Fabric event phase. */
public final class Level10EnchantmentsClient implements ClientModInitializer {
    private static final ResourceLocation AFTER_ENCHANTMENT_DESCRIPTIONS = new ResourceLocation(
            "level10enchantments", "after_enchantment_descriptions"
    );

    @Override
    public void onInitializeClient() {
        // Enchantment Descriptions registers in DEFAULT_PHASE and compares the
        // stable getFullname() component before it inserts explanation text.
        ItemTooltipCallback.EVENT.addPhaseOrdering(
                Event.DEFAULT_PHASE, AFTER_ENCHANTMENT_DESCRIPTIONS
        );
        ItemTooltipCallback.EVENT.register(
                AFTER_ENCHANTMENT_DESCRIPTIONS, (stack, flag, tooltip) ->
                        HighLevelTooltipTransformer.transform(stack, tooltip)
        );
    }
}
