package com.liy.level10enchantments.mixin;

import com.liy.level10enchantments.RainbowColors;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantment.class)
public abstract class EnchantmentNameMixin {
    @Inject(method = "getFullname", at = @At("RETURN"), cancellable = true)
    private static void level10$rainbowLevelTenName(
            Holder<Enchantment> enchantment,
            int level,
            CallbackInfoReturnable<Component> callback
    ) {
        if (level != 10) {
            return;
        }

        String text = callback.getReturnValue().getString();
        MutableComponent rainbow = Component.empty();
        long now = System.currentTimeMillis();
        int glyphCount = text.codePointCount(0, text.length());
        int colorIndex = 0;
        for (int offset = 0; offset < text.length(); colorIndex++) {
            int codePoint = text.codePointAt(offset);
            String character = new String(Character.toChars(codePoint));
            int rgb = RainbowColors.rgbForIndex(colorIndex, glyphCount, now);
            rainbow.append(Component.literal(character).withStyle(style ->
                    style.withColor(rgb).withBold(false)
            ));
            offset += Character.charCount(codePoint);
        }
        callback.setReturnValue(rainbow);
    }
}
