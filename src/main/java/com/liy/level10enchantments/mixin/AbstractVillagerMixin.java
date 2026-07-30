package com.liy.level10enchantments.mixin;

import com.liy.level10enchantments.EnchantmentLevelNormalizer;
import com.liy.level10enchantments.LootBalancePolicy;
import com.liy.level10enchantments.MasterLibrarianTradePolicy;
import java.util.Iterator;
import java.util.List;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractVillager.class)
public abstract class AbstractVillagerMixin {
    private static final String TRADE_MARKER = "level10enchantments:master_librarian_trade";

    @Inject(method = "getOffers", at = @At("RETURN"))
    private void level10$ensureMasterLibrarianTrade(
            CallbackInfoReturnable<MerchantOffers> callback
    ) {
        if (!((Object) this instanceof Villager villager)
                || villager.getVillagerData().getLevel() < 5
                || villager.getVillagerData().getProfession() != VillagerProfession.LIBRARIAN) {
            return;
        }

        MerchantOffers offers = callback.getReturnValue();
        boolean foundBalancedTrade = false;
        Iterator<MerchantOffer> iterator = offers.iterator();
        while (iterator.hasNext()) {
            MerchantOffer offer = iterator.next();
            if (isBalancedTrade(offer.getResult())) {
                if (foundBalancedTrade) {
                    iterator.remove();
                } else {
                    foundBalancedTrade = true;
                }
            } else {
                EnchantmentLevelNormalizer.capToVanillaMaximum(offer.getResult());
            }
        }

        if (!foundBalancedTrade) {
            MerchantOffer balancedTrade = createBalancedTrade(villager);
            if (balancedTrade != null) {
                offers.add(balancedTrade);
            }
        }
    }

    private static MerchantOffer createBalancedTrade(Villager villager) {
        RandomSource random = villager.getRandom();
        MasterLibrarianTradePolicy.Trade trade =
                MasterLibrarianTradePolicy.select(random.nextDouble());
        List<Enchantment> candidates = BuiltInRegistries.ENCHANTMENT.stream()
                .filter(enchantment -> LootBalancePolicy.allowsLibrarianTrade(
                        enchantmentId(enchantment)
                ))
                .toList();
        if (candidates.isEmpty()) {
            return null;
        }

        Enchantment enchantment = candidates.get(random.nextInt(candidates.size()));
        ItemStack result = EnchantedBookItem.createForEnchantment(
                new EnchantmentInstance(enchantment, trade.level())
        );
        result.getOrCreateTag().putBoolean(TRADE_MARKER, true);

        Item catalyst = switch (trade.catalyst()) {
            case BOOK -> Items.BOOK;
            case DIAMOND -> Items.DIAMOND;
            case ECHO_SHARD -> Items.ECHO_SHARD;
            case NETHERITE_INGOT -> Items.NETHERITE_INGOT;
        };
        return new MerchantOffer(
                new ItemStack(Items.EMERALD, trade.emeraldCost()),
                new ItemStack(catalyst),
                result,
                trade.maxUses(),
                30,
                0.2F
        );
    }

    private static boolean isBalancedTrade(ItemStack stack) {
        return stack.hasTag() && stack.getTag().getBoolean(TRADE_MARKER);
    }

    private static String enchantmentId(Enchantment enchantment) {
        return BuiltInRegistries.ENCHANTMENT.getKey(enchantment).toString();
    }
}
