package com.liy.level10enchantments;

import com.liy.level10enchantments.MasterLibrarianTradePolicy.Catalyst;
import com.liy.level10enchantments.MasterLibrarianTradePolicy.Trade;

public final class MasterLibrarianTradePolicyTest {
    public static void main(String[] args) {
        assertTrade(0.0000, 6, 32, Catalyst.BOOK, 4);
        assertTrade(0.3499, 6, 32, Catalyst.BOOK, 4);
        assertTrade(0.3500, 7, 40, Catalyst.BOOK, 3);
        assertTrade(0.5999, 7, 40, Catalyst.BOOK, 3);
        assertTrade(0.6000, 8, 48, Catalyst.DIAMOND, 2);
        assertTrade(0.7799, 8, 48, Catalyst.DIAMOND, 2);
        assertTrade(0.7800, 9, 56, Catalyst.ECHO_SHARD, 1);
        assertTrade(0.8999, 9, 56, Catalyst.ECHO_SHARD, 1);
        assertTrade(0.9000, 10, 64, Catalyst.NETHERITE_INGOT, 1);
        assertTrade(0.9999, 10, 64, Catalyst.NETHERITE_INGOT, 1);
        System.out.println("PASS: B2 master librarian trade probabilities and costs");
    }

    private static void assertTrade(
            double roll,
            int level,
            int emeraldCost,
            Catalyst catalyst,
            int maxUses
    ) {
        Trade trade = MasterLibrarianTradePolicy.select(roll);
        require(trade.level() == level, "level at " + roll);
        require(trade.emeraldCost() == emeraldCost, "emerald cost at " + roll);
        require(trade.catalyst() == catalyst, "catalyst at " + roll);
        require(trade.maxUses() == maxUses, "max uses at " + roll);
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
