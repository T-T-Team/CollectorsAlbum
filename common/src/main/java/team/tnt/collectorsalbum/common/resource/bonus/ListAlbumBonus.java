package team.tnt.collectorsalbum.common.resource.bonus;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import team.tnt.collectorsalbum.common.AlbumBonusDescriptionOutput;
import team.tnt.collectorsalbum.common.init.AlbumBonusRegistry;
import team.tnt.collectorsalbum.common.resource.util.ActionContext;

import java.util.List;

public class ListAlbumBonus implements IntermediateAlbumBonus {

    public static final Component LABEL = Component.translatable("collectorsalbum.label.bonus.list");
    public static final Component TOOLTIP = Component.translatable("collectorsalbum.tooltip.bonus.list");
    public static final MapCodec<ListAlbumBonus> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            AlbumBonusType.INSTANCE_CODEC.listOf().fieldOf("items").forGetter(t -> t.items)
    ).apply(instance, ListAlbumBonus::new));

    private final List<AlbumBonus> items;

    public ListAlbumBonus(List<AlbumBonus> items) {
        this.items = items;
    }

    @Override
    public List<AlbumBonus> children() {
        return this.items;
    }

    @Override
    public void apply(ActionContext context) {
        this.items.forEach(item -> item.apply(context));
    }

    @Override
    public void removed(ActionContext context) {
        this.items.forEach(item -> item.removed(context));
    }

    @Override
    public boolean canApply(ActionContext context) {
        return true;
    }

    @Override
    public void addDescription(AlbumBonusDescriptionOutput description) {
        description.list(LABEL, TOOLTIP, this);
    }

    @Override
    public AlbumBonusType<?> getType() {
        return AlbumBonusRegistry.LIST.get();
    }
}
