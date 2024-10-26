package team.tnt.collectorsalbum.common.card;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.sounds.SoundEvent;
import team.tnt.collectorsalbum.common.AlbumCategory;
import team.tnt.collectorsalbum.common.init.SoundRegistry;
import team.tnt.collectorsalbum.platform.Codecs;

import java.util.function.Supplier;

public record CardUiTemplate(Integer[] effectColors, Integer[] effectDurations, Supplier<SoundEvent> flipSoundRef) {

    public static final CardUiTemplate TEMPLATE = new CardUiTemplate(new Integer[] { 0x00FF00 }, new Integer[] { 5 }, SoundRegistry.FLIP_COMMON);
    public static final Codec<CardUiTemplate> CODEC = Codecs.validate(RecordCodecBuilder.create(instance -> instance.group(
            Codecs.array(Codec.INT, Integer[]::new).optionalFieldOf("effectColors", TEMPLATE.effectColors()).forGetter(t -> t.effectColors),
            Codecs.array(Codec.INT, Integer[]::new).optionalFieldOf("effectDurations", TEMPLATE.effectDurations()).forGetter(t -> t.effectDurations),
            Codecs.supplier(BuiltInRegistries.SOUND_EVENT.byNameCodec()).optionalFieldOf("flipSound", TEMPLATE.flipSoundRef()).forGetter(t -> t.flipSoundRef)
    ).apply(instance, CardUiTemplate::new)), template -> template.effectColors().length == template.effectDurations().length
            ? DataResult.success(template)
            : DataResult.error(() -> "Effect duration count has to be the same as effect colors")
    );

    public static final Component DEFAULT_CARD_INFO_HEADER = Component.translatable("collectorsalbum.tooltip.card.header").withStyle(ChatFormatting.GRAY);
    public static final String ITEM_TOOLTIP_NUMBER_KEY = "collectorsalbum.tooltip.card.number";
    public static final String ITEM_TOOLTIP_CATEGORY_KEY = "collectorsalbum.tooltip.card.category";
    public static final String ITEM_TOOLTIP_RARITY_KEY = "collectorsalbum.tooltip.card.rarity";
    public static final String ITEM_TOOLTIP_VALUE_KEY = "collectorsalbum.tooltip.card.value";

    public static MutableComponent getCardNumberTooltip(Component numberLabel) {
        return Component.translatable(ITEM_TOOLTIP_NUMBER_KEY, numberLabel).withStyle(ChatFormatting.GRAY);
    }

    public static MutableComponent getCardNumberTooltip(int number) {
        Component label = Component.literal("#" + number).withStyle(ChatFormatting.YELLOW);
        return getCardNumberTooltip(label);
    }

    public static MutableComponent getCardCategoryTooltip(AlbumCard card) {
        return getCardCategoryTooltip(card.getLinkedCategory());
    }

    public static MutableComponent getCardCategoryTooltip(AlbumCategory category) {
        return getCardCategoryTooltip(category.getDisplayText());
    }

    public static MutableComponent getCardCategoryTooltip(Component categoryLabel) {
        return Component.translatable(ITEM_TOOLTIP_CATEGORY_KEY, categoryLabel).withStyle(ChatFormatting.GRAY);
    }

    public static MutableComponent getCardRarityTooltip(CardRarity rarity) {
        return getCardRarityTooltip(rarity.getDisplayText());
    }

    public static MutableComponent getCardRarityTooltip(Component rarityLabel) {
        return Component.translatable(ITEM_TOOLTIP_RARITY_KEY, rarityLabel).withStyle(ChatFormatting.GRAY);
    }

    public static MutableComponent getCardPointsTooltip(AlbumCard card) {
        return getCardPointsTooltip(card.getPoints());
    }

    public static MutableComponent getCardPointsTooltip(int points) {
        Component label = Component.translatable("collectorsalbum.text.points.value", points).withStyle(ChatFormatting.YELLOW);
        return getCardPointsTooltip(label);
    }

    public static MutableComponent getCardPointsTooltip(Component pointsLabel) {
        return Component.translatable(ITEM_TOOLTIP_VALUE_KEY, pointsLabel).withStyle(ChatFormatting.GRAY);
    }

}
