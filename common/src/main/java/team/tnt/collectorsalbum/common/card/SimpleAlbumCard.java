package team.tnt.collectorsalbum.common.card;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import team.tnt.collectorsalbum.common.AlbumCard;
import team.tnt.collectorsalbum.common.AlbumCardType;
import team.tnt.collectorsalbum.common.AlbumCategory;

public record SimpleAlbumCard(ResourceLocation identifier, ItemStack itemStack, AlbumCategory category, int cardNumber) implements AlbumCard {

    @Override
    public ItemStack asItem() {
        return this.itemStack;
    }

    @Override
    public AlbumCardType<?> getType() {
        return null;
    }
}
