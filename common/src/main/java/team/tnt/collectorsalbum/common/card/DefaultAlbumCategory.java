package team.tnt.collectorsalbum.common.card;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.Identifier;
import team.tnt.collectorsalbum.common.AlbumCategory;
import team.tnt.collectorsalbum.common.AlbumCategoryType;
import team.tnt.collectorsalbum.common.AlbumCategoryUiTemplate;
import team.tnt.collectorsalbum.common.init.CategoryRegistry;
import team.tnt.collectorsalbum.common.resource.AlbumCardManager;
import team.tnt.collectorsalbum.platform.Codecs;

import java.util.Objects;
import java.util.Set;

public class DefaultAlbumCategory implements AlbumCategory {

    public static final MapCodec<DefaultAlbumCategory> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Identifier.CODEC.fieldOf("id").forGetter(DefaultAlbumCategory::identifier),
            Codecs.setCodec(AlbumCardManager.BY_NAME_CODEC).fieldOf("cards").forGetter(t -> t.cards),
            DisplayAttributes.CODEC.fieldOf("display").forGetter(t -> t.attributes),
            AlbumCategoryUiTemplate.CODEC.optionalFieldOf("template", AlbumCategoryUiTemplate.DEFAULT_TEMPLATE).forGetter(t -> t.template)
    ).apply(instance, DefaultAlbumCategory::new));

    private final Identifier identifier;
    private final Set<AlbumCard> cards;
    private final DisplayAttributes attributes;
    private final Component displayText;
    private final AlbumCategoryUiTemplate template;
    private final int[] uniqueCardNumbers;

    private DefaultAlbumCategory(Identifier identifier, Set<AlbumCard> cards, DisplayAttributes attributes, AlbumCategoryUiTemplate template) {
        this.identifier = identifier;
        this.cards = cards;
        this.attributes = attributes;
        this.displayText = attributes.translated()
                ? Component.translatable(attributes.displayString()).withColor(attributes.color)
                : Component.literal(attributes.displayString()).withColor(attributes.color);
        this.template = template;
        this.uniqueCardNumbers = this.cards.stream().mapToInt(AlbumCard::cardNumber).distinct().sorted().toArray();
    }

    @Override
    public Identifier identifier() {
        return this.identifier;
    }

    @Override
    public Component getDisplayText() {
        return this.displayText;
    }

    @Override
    public AlbumCategoryUiTemplate visualTemplate() {
        return template;
    }

    @Override
    public int getPageOrder() {
        return this.attributes.pageOrder();
    }

    @Override
    public int[] getCardNumbers() {
        return uniqueCardNumbers;
    }

    @Override
    public AlbumCategoryType<?> getType() {
        return CategoryRegistry.CATEGORY.get();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DefaultAlbumCategory that = (DefaultAlbumCategory) o;
        return Objects.equals(identifier, that.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(identifier);
    }

    public record DisplayAttributes(String displayString, boolean translated, TextColor color, int pageOrder) {

        public static final Codec<DisplayAttributes> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.fieldOf("name").forGetter(DisplayAttributes::displayString),
                Codec.BOOL.optionalFieldOf("translate", false).forGetter(DisplayAttributes::translated),
                TextColor.CODEC.optionalFieldOf("color", TextColor.WHITE).forGetter(DisplayAttributes::color),
                Codec.INT.optionalFieldOf("pageOrder", Integer.MAX_VALUE).forGetter(DisplayAttributes::pageOrder)
        ).apply(instance, DisplayAttributes::new));
    }
}
