package team.tnt.collectorsalbum.common;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import team.tnt.collectorsalbum.common.card.CardRarity;
import team.tnt.collectorsalbum.common.resource.AlbumCategoryManager;

public interface AlbumCard {

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
