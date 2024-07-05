package team.tnt.collectorsalbum.common;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import team.tnt.collectorsalbum.common.card.CardRarity;
import team.tnt.collectorsalbum.common.resource.AlbumCategoryManager;

public interface AlbumCard {

    Component ITEM_TOOLTIP_HEADER = Component.translatable("collectorsalbum.tooltip.card.header").withStyle(ChatFormatting.GRAY);
    String ITEM_TOOLTIP_NUMBER_KEY = "collectorsalbum.tooltip.card.number";
    String ITEM_TOOLTIP_CATEGORY_KEY = "collectorsalbum.tooltip.card.category";
    String ITEM_TOOLTIP_RARITY_KEY = "collectorsalbum.tooltip.card.rarity";
    String ITEM_TOOLTIP_VALUE_KEY = "collectorsalbum.tooltip.card.value";

    ResourceLocation identifier();

    ResourceLocation category();

    CardRarity rarity();

    ItemStack asItem();

    int cardNumber();

    AlbumCardType<?> getType();

    default AlbumCategory getLinkedCategory() {
        AlbumCategoryManager manager = AlbumCategoryManager.getInstance();
        return manager.findById(this.category()).orElseThrow();
    }
}
