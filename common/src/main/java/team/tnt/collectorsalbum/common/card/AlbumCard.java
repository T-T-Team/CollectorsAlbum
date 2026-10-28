package team.tnt.collectorsalbum.common.card;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import team.tnt.collectorsalbum.CollectorsAlbum;
import team.tnt.collectorsalbum.common.AlbumCategory;
import team.tnt.collectorsalbum.common.resource.AlbumCategoryManager;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public interface AlbumCard extends Comparable<AlbumCard>, Predicate<CardFilter> {

    Codec<AlbumCard> CODEC = CollectorsAlbum.CARD_TYPE.byNameCodec().dispatch(AlbumCard::codec, Function.identity());

    Identifier identifier();

    Identifier category();

    CardUiTemplate template();

    Item asItem();

    int getPoints();

    int cardNumber();

    void appendItemStackHoverTooltip(ItemStack itemStack, Item.TooltipContext context, List<Component> tooltips, TooltipFlag flag);

    MapCodec<? extends AlbumCard> codec();

    default boolean enabled() {
        return true;
    }

    @Nullable
    default AlbumCategory getLinkedCategory() {
        AlbumCategoryManager manager = AlbumCategoryManager.getInstance();
        return manager.findById(this.category()).orElse(null);
    }

    @Override
    default int compareTo(@NotNull AlbumCard o) {
        return this.getPoints() - o.getPoints();
    }
}
