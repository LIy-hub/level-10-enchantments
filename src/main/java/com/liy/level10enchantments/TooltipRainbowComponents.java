package com.liy.level10enchantments;

import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

/** High-level name colouring that changes only text colour, never its effective style. */
public final class TooltipRainbowComponents {
    private TooltipRainbowComponents() {
    }

    public static Component colourName(Component original, String salt, int level, long now) {
        String renderedText = original.getString();
        MutableComponent coloured = Component.empty();
        int glyphCount = renderedText.codePointCount(0, renderedText.length());
        int[] glyphIndex = {0};
        original.visit((FormattedText.StyledContentConsumer<Void>) (effectiveStyle, text) -> {
            for (int offset = 0; offset < text.length(); glyphIndex[0]++) {
                int codePoint = text.codePointAt(offset);
                int rgb = RainbowColors.rgbForIndex(
                        glyphIndex[0], glyphCount, now, salt, level
                );
                coloured.append(Component.literal(new String(Character.toChars(codePoint)))
                        .withStyle(effectiveStyle.withColor(rgb)));
                offset += Character.charCount(codePoint);
            }
            return Optional.empty();
        }, Style.EMPTY);
        return coloured;
    }
}
