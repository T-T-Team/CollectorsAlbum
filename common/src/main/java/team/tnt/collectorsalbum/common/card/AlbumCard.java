package team.tnt.collectorsalbum.common.card;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;
import team.tnt.collectorsalbum.common.AlbumCategory;
import team.tnt.collectorsalbum.common.resource.AlbumCategoryManager;

import java.util.List;
import java.util.function.Predicate;

public interface AlbumCard extends Comparable<AlbumCard>, Predicate<CardFilter> {

    ResourceLocation identifier();

    ResourceLocation category();

    CardUiTemplate template();

    ItemStack asItem();

    int getPoints();

    int cardNumber();

    void appendItemStackHoverTooltip(ItemStack itemStack, List<Component> tooltips, TooltipFlag flag);

    @Override
    default int compareTo(@NotNull AlbumCard o) {
        return this.getPoints() - o.getPoints();
    }

    AlbumCardType<?> getType();

    default AlbumCategory getLinkedCategory() {
        AlbumCategoryManager manager = AlbumCategoryManager.getInstance();
        return manager.findById(this.category()).orElseThrow();
    }
}
