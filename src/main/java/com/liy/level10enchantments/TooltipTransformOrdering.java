package com.liy.level10enchantments;

/** Pure ordering invariant for the explicit Fabric tooltip event phase. */
public final class TooltipTransformOrdering {
    private TooltipTransformOrdering() {
    }

    public static boolean descriptionsCanMatchStableName(boolean transformHasRun, boolean equalToStableName) {
        return !transformHasRun && equalToStableName;
    }
}
