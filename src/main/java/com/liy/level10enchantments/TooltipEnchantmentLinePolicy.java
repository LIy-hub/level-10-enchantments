package com.liy.level10enchantments;

/** Small pure policy used by the final tooltip transform to avoid colour bleed. */
public final class TooltipEnchantmentLinePolicy {
    private TooltipEnchantmentLinePolicy() {
    }

    public static boolean isDescriptionKeyFor(String enchantmentDescriptionId, String translationKey) {
        if (enchantmentDescriptionId == null || translationKey == null) {
            return false;
        }
        return translationKey.equals(enchantmentDescriptionId + ".desc")
                || translationKey.equals(enchantmentDescriptionId + ".description");
    }

    public static boolean colourDescriptionLine(
            boolean hasRecognisableOriginalColour,
            boolean containsExactDescriptionKey
    ) {
        return hasRecognisableOriginalColour && containsExactDescriptionKey;
    }
}
