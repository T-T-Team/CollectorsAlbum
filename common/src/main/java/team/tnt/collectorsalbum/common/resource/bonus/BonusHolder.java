package team.tnt.collectorsalbum.common.resource.bonus;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;

public record BonusHolder(Component title, Component description, AlbumBonus value) {

    public static final Component UNNAMED_TITLE = Component.translatable("collectorsalbum.label.unnamed_bonus");
    public static final Component NO_DESCRIPTION = Component.translatable("collectorsalbum.label.missing_description");
    public static final BonusHolder EMPTY = unnamed(NoBonus.INSTANCE);
    public static final Codec<BonusHolder> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ComponentSerialization.CODEC.fieldOf("title").forGetter(BonusHolder::title),
            ComponentSerialization.CODEC.optionalFieldOf("description", NO_DESCRIPTION).forGetter(BonusHolder::description),
            AlbumBonusType.INSTANCE_CODEC.fieldOf("value").forGetter(BonusHolder::value)
    ).apply(instance, BonusHolder::new));

    public static BonusHolder unnamed(AlbumBonus value) {
        return new BonusHolder(UNNAMED_TITLE, NO_DESCRIPTION, value);
    }

    public boolean isEnabled() {
        return this.value != NoBonus.INSTANCE;
    }
}
