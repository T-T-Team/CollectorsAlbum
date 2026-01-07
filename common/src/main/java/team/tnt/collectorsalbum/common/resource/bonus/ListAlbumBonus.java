package team.tnt.collectorsalbum.common.resource.bonus;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import team.tnt.collectorsalbum.common.init.AlbumBonusRegistry;
import team.tnt.collectorsalbum.common.resource.util.ActionContext;

import java.util.List;

public record ListAlbumBonus(List<AlbumBonus> items) implements IntermediateAlbumBonus {

    public static final Component LABEL = Component.translatable("collectorsalbum.label.bonus.list");
    public static final MapCodec<ListAlbumBonus> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            AlbumBonusType.INSTANCE_CODEC.listOf().fieldOf("items").forGetter(t -> t.items)
    ).apply(instance, ListAlbumBonus::new));

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
    public SectionOutput appendItemDetailsWithModifiers(SectionOutput output, ActionContext context, AlbumBonus child) {
        output.withTitle(LABEL);
        return new DelegatingSectionOutput(
                output,
                title -> output.withDescription(Component.literal("- ").append(title)),
                description -> {
                }
        );
    }

    @Override
    public AlbumBonusType<?> getType() {
        return AlbumBonusRegistry.LIST.get();
    }
}
