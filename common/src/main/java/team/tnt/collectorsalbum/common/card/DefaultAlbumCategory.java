package team.tnt.collectorsalbum.common.card;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import team.tnt.collectorsalbum.common.AlbumCard;
import team.tnt.collectorsalbum.common.AlbumCategory;
import team.tnt.collectorsalbum.common.AlbumCategoryType;
import team.tnt.collectorsalbum.common.AlbumCategoryUiTemplate;
import team.tnt.collectorsalbum.common.init.CategoryRegistry;
import team.tnt.collectorsalbum.common.resource.AlbumCardManager;
import team.tnt.collectorsalbum.platform.Codecs;
import team.tnt.collectorsalbum.util.MathUtil;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class DefaultAlbumCategory implements AlbumCategory {

    public static final int MAX_SLOTS = 30;
    public static final MapCodec<DefaultAlbumCategory> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(DefaultAlbumCategory::identifier),
            Codecs.setCodec(AlbumCardManager.BY_NAME_CODEC).validate(set -> {
                int expectedSum = MathUtil.seqSum(1, MAX_SLOTS) * CardRarity.values().length;
                int sum = set.stream().mapToInt(AlbumCard::cardNumber).sum();
                return expectedSum == sum ? DataResult.success(set) : DataResult.error(() -> "Category must contain exactly 30 cards for each rarity!");
            }).fieldOf("cards").forGetter(t -> t.cards),
            DisplayAttributes.CODEC.fieldOf("display").forGetter(t -> t.attributes),
            AlbumCategoryUiTemplate.CODEC.optionalFieldOf("template", AlbumCategoryUiTemplate.DEFAULT_TEMPLATE).forGetter(t -> t.template)
    ).apply(instance, DefaultAlbumCategory::new));

    private final ResourceLocation identifier;
    private final Set<AlbumCard> cards;
    private final DisplayAttributes attributes;
    private final Component displayText;
    private final AlbumCategoryUiTemplate template;

    private DefaultAlbumCategory(ResourceLocation identifier, Set<AlbumCard> cards, DisplayAttributes attributes, AlbumCategoryUiTemplate template) {
        this.identifier = identifier;
        this.cards = cards;
        this.attributes = attributes;
        ChatFormatting[] styles = attributes.styleList.toArray(new ChatFormatting[0]);
        this.displayText = attributes.translated()
                ? Component.translatable(attributes.displayString()).withStyle(styles)
                : Component.literal(attributes.displayString()).withStyle(styles);
        this.template = template;
    }

    @Override
    public ResourceLocation identifier() {
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
    public int getSlots() {
        return MAX_SLOTS;
    }

    @Override
    public boolean accepts(AlbumCard card) {
        return this.cards.contains(card);
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

    public record DisplayAttributes(String displayString, boolean translated, List<ChatFormatting> styleList, int pageOrder) {

        public static final Codec<DisplayAttributes> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.fieldOf("name").forGetter(DisplayAttributes::displayString),
                Codec.BOOL.optionalFieldOf("translate", false).forGetter(DisplayAttributes::translated),
                ChatFormatting.CODEC.listOf().optionalFieldOf("styles", Collections.emptyList()).forGetter(DisplayAttributes::styleList),
                Codec.INT.optionalFieldOf("pageOrder", Integer.MAX_VALUE).forGetter(DisplayAttributes::pageOrder)
        ).apply(instance, DisplayAttributes::new));
    }
}
