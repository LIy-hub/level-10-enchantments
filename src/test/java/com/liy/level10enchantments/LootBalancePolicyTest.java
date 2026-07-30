package com.liy.level10enchantments;

import com.liy.level10enchantments.LootBalancePolicy.Profile;

public final class LootBalancePolicyTest {
    public static void main(String[] args) {
        assertProfile(Profile.STRONGHOLD_LIBRARY, 500, 250, 120, 50, 20, 940);
        assertProfile(Profile.WOODLAND_MANSION, 600, 300, 150, 70, 30, 1150);
        assertProfile(Profile.BASTION_ORDINARY, 700, 400, 200, 100, 40, 1440);
        assertProfile(Profile.ANCIENT_CITY, 700, 450, 300, 150, 80, 1680);
        assertProfile(Profile.TRIAL_VAULT, 800, 500, 300, 150, 70, 1820);
        assertProfile(Profile.BASTION_TREASURE, 800, 500, 350, 200, 100, 1950);
        assertProfile(Profile.END_CITY_TREASURE, 900, 600, 400, 250, 150, 2300);
        assertProfile(Profile.OMINOUS_TRIAL_VAULT, 1000, 700, 500, 300, 200, 2700);

        require(profile("minecraft:chests/stronghold_library") == Profile.STRONGHOLD_LIBRARY,
                "stronghold mapping");
        require(profile("minecraft:chests/bastion_bridge") == Profile.BASTION_ORDINARY,
                "ordinary bastion bridge mapping");
        require(profile("minecraft:chests/trial_chambers/reward_ominous") == Profile.OMINOUS_TRIAL_VAULT,
                "ominous vault mapping");
        require(LootBalancePolicy.profileFor("minecraft:chests/simple_dungeon").isEmpty(),
                "unselected source excluded");

        require(select(Profile.STRONGHOLD_LIBRARY, 0.0000) == 6, "VI lower boundary");
        require(select(Profile.STRONGHOLD_LIBRARY, 0.0499) == 6, "VI upper boundary");
        require(select(Profile.STRONGHOLD_LIBRARY, 0.0500) == 7, "VII lower boundary");
        require(select(Profile.STRONGHOLD_LIBRARY, 0.0749) == 7, "VII upper boundary");
        require(select(Profile.STRONGHOLD_LIBRARY, 0.0750) == 8, "VIII lower boundary");
        require(select(Profile.STRONGHOLD_LIBRARY, 0.0869) == 8, "VIII upper boundary");
        require(select(Profile.STRONGHOLD_LIBRARY, 0.0870) == 9, "IX lower boundary");
        require(select(Profile.STRONGHOLD_LIBRARY, 0.0919) == 9, "IX upper boundary");
        require(select(Profile.STRONGHOLD_LIBRARY, 0.0920) == 10, "X lower boundary");
        require(select(Profile.STRONGHOLD_LIBRARY, 0.0939) == 10, "X upper boundary");
        require(select(Profile.STRONGHOLD_LIBRARY, 0.0940) == 0, "no high level after total chance");

        require(LootBalancePolicy.allowsHighLevel(Profile.BASTION_ORDINARY, "minecraft:soul_speed"),
                "soul speed allowed in ordinary bastion");
        require(LootBalancePolicy.allowsHighLevel(Profile.BASTION_TREASURE, "minecraft:soul_speed"),
                "soul speed allowed in treasure bastion");
        require(!LootBalancePolicy.allowsHighLevel(Profile.END_CITY_TREASURE, "minecraft:soul_speed"),
                "soul speed excluded outside bastions");
        require(LootBalancePolicy.allowsHighLevel(Profile.OMINOUS_TRIAL_VAULT, "minecraft:wind_burst"),
                "wind burst allowed in ominous vault");
        require(!LootBalancePolicy.allowsHighLevel(Profile.TRIAL_VAULT, "minecraft:wind_burst"),
                "wind burst excluded from normal vault");
        require(LootBalancePolicy.allowsHighLevel(Profile.STRONGHOLD_LIBRARY, "minecraft:mending"),
                "mending allowed in general selected loot");
        require(LootBalancePolicy.allowsHighLevel(Profile.STRONGHOLD_LIBRARY, "minecraft:frost_walker"),
                "frost walker allowed in general selected loot");

        require(LootBalancePolicy.allowsHighLevelInDimension(
                        Profile.END_CITY_TREASURE, "minecraft:the_end"),
                "end city high levels allowed in vanilla End");
        require(!LootBalancePolicy.allowsHighLevelInDimension(
                        Profile.END_CITY_TREASURE, "replica_dimensions:the_end"),
                "end city high levels excluded from replica End");
        require(!LootBalancePolicy.allowsHighLevelInDimension(
                        Profile.END_CITY_TREASURE, "minecraft:overworld"),
                "end city table reuse outside vanilla End excluded");
        require(LootBalancePolicy.allowsHighLevelInDimension(
                        Profile.STRONGHOLD_LIBRARY, "minecraft:overworld"),
                "non-End loot keeps its normal dimension");
        require(LootBalancePolicy.allowsHighLevelInDimension(
                        Profile.BASTION_TREASURE, "replica_dimensions:the_nether"),
                "dimension restriction applies only to End City loot");

        require(!LootBalancePolicy.allowsLibrarianTrade("minecraft:soul_speed"),
                "librarian excludes soul speed");
        require(!LootBalancePolicy.allowsLibrarianTrade("minecraft:wind_burst"),
                "librarian excludes wind burst");
        require(LootBalancePolicy.allowsLibrarianTrade("minecraft:mending"),
                "librarian includes mending");
        System.out.println("PASS: high-level loot source probabilities, identities, and dimension gates");
    }

    private static void assertProfile(
            Profile profile,
            int level6,
            int level7,
            int level8,
            int level9,
            int level10,
            int total
    ) {
        require(profile.basisPointsFor(6) == level6, profile + " VI");
        require(profile.basisPointsFor(7) == level7, profile + " VII");
        require(profile.basisPointsFor(8) == level8, profile + " VIII");
        require(profile.basisPointsFor(9) == level9, profile + " IX");
        require(profile.basisPointsFor(10) == level10, profile + " X");
        require(profile.totalBasisPoints() == total, profile + " total");
    }

    private static Profile profile(String id) {
        return LootBalancePolicy.profileFor(id).orElseThrow();
    }

    private static int select(Profile profile, double roll) {
        return LootBalancePolicy.selectHighLevel(profile, roll);
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
