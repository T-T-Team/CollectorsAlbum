package team.tnt.collectorsalbum.common.card;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import team.tnt.collectorsalbum.common.AlbumCard;
import team.tnt.collectorsalbum.common.AlbumCardType;
import team.tnt.collectorsalbum.common.AlbumCategory;
import team.tnt.collectorsalbum.common.init.CardTypeRegistry;
import team.tnt.collectorsalbum.common.resource.AlbumCategoryManager;
import team.tnt.collectorsalbum.platform.Codecs;

import java.util.Locale;

public class SimpleAlbumCard implements AlbumCard {

    public static final MapCodec<SimpleAlbumCard> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(SimpleAlbumCard::identifier),
            Codecs.simpleEnumCodec(CardRarity.class, text -> text.toUpperCase(Locale.ROOT)).fieldOf("rarity").forGetter(SimpleAlbumCard::rarity),
            ItemStack.SIMPLE_ITEM_CODEC.fieldOf("item").forGetter(SimpleAlbumCard::asItem),
            ResourceLocation.CODEC.fieldOf("category").forGetter(SimpleAlbumCard::category),
            ExtraCodecs.POSITIVE_INT.fieldOf("number").forGetter(SimpleAlbumCard::cardNumber)
    ).apply(instance, SimpleAlbumCard::new));

    private final ResourceLocation cardIdentifier;
    private final CardRarity rarity;
    private final ItemStack itemStack;
    private final ResourceLocation categoryIdentifier;
    private final int cardNumber;

    private AlbumCategory cachedCategory;

    public SimpleAlbumCard(ResourceLocation cardIdentifier, CardRarity rarity, ItemStack itemStack, ResourceLocation categoryIdentifier, int cardNumber) {
        this.cardIdentifier = cardIdentifier;
        this.rarity = rarity;
        this.itemStack = itemStack;
        this.categoryIdentifier = categoryIdentifier;
        this.cardNumber = cardNumber;
    }

    @Override
    public ResourceLocation identifier() {
        return this.cardIdentifier;
    }

    @Override
    public CardRarity rarity() {
        return this.rarity;
    }

    @Override
    public ItemStack asItem() {
        return this.itemStack;
    }

    @Override
    public ResourceLocation category() {
        return this.categoryIdentifier;
    }

    @Override
    public int cardNumber() {
        return this.cardNumber;
    }

    @Override
    public AlbumCardType<?> getType() {
        return CardTypeRegistry.CARD.get();
    }

    @Override
    public AlbumCategory getLinkedCategory() {
        if (this.cachedCategory == null) {
            this.cachedCategory = AlbumCategoryManager.getInstance().findById(this.categoryIdentifier).orElseThrow();
        }
        return this.cachedCategory;
    }
}
