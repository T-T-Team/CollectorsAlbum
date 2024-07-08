package team.tnt.collectorsalbum.common;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import team.tnt.collectorsalbum.CollectorsAlbum;
import team.tnt.collectorsalbum.common.resource.AlbumBonusManager;
import team.tnt.collectorsalbum.common.resource.AlbumCardManager;
import team.tnt.collectorsalbum.common.resource.util.ActionContext;
import team.tnt.collectorsalbum.platform.Codecs;

import java.util.*;
import java.util.function.Predicate;

public final class Album implements Predicate<Album> {

    public static final Codec<Album> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            UUIDUtil.CODEC.fieldOf("albumId").forGetter(t -> t.albumId),
            Codec.unboundedMap(
                    ResourceLocation.CODEC,
                    AlbumCardManager.BY_NAME_CODEC.listOf()
            ).xmap(HashMap::new, map -> map).fieldOf("cardsByCategory").forGetter(t -> (HashMap<ResourceLocation, List<AlbumCard>>) t.cardsByCategory),
            Codec.unboundedMap(
                    ResourceLocation.CODEC,
                    Codecs.nonNullListCodec(ItemStack.OPTIONAL_CODEC, ItemStack.EMPTY)
            ).fieldOf("categoryInventories").forGetter(t -> t.categoryInventories)
    ).apply(instance, Album::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, Album> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, card -> card.albumId,
            ByteBufCodecs.map(HashMap::new, ResourceLocation.STREAM_CODEC, ResourceLocation.STREAM_CODEC.apply(ByteBufCodecs.list())).map(map -> {
                Map<ResourceLocation, List<AlbumCard>> remapped = new HashMap<>();
                AlbumCardManager cardManager = AlbumCardManager.getInstance();
                for (Map.Entry<ResourceLocation, List<ResourceLocation>> entry : map.entrySet()) {
                    List<AlbumCard> cards = new ArrayList<>();
                    for (ResourceLocation key : entry.getValue()) {
                        AlbumCard card = cardManager.getCardById(key);
                        if (card != null) {
                            cards.add(card);
                        }
                    }
                    remapped.put(entry.getKey(), cards);
                }
                return remapped;
            }, map -> {
                HashMap<ResourceLocation, List<ResourceLocation>> remapped = new HashMap<>();
                for (Map.Entry<ResourceLocation, List<AlbumCard>> entry : map.entrySet()) {
                    List<ResourceLocation> identifiers = new ArrayList<>();
                    for (AlbumCard card : entry.getValue()) {
                        identifiers.add(card.identifier());
                    }
                    remapped.put(entry.getKey(), identifiers);
                }
                return remapped;
            }), album -> album.cardsByCategory,
            ByteBufCodecs.map(HashMap::new, ResourceLocation.STREAM_CODEC, Codecs.nonNullListStreamCodec(ItemStack.OPTIONAL_STREAM_CODEC, ItemStack::isEmpty, ItemStack.EMPTY)), t -> t.categoryInventories,
            Album::new
    );

    private final UUID albumId;
    private final Map<ResourceLocation, List<AlbumCard>> cardsByCategory;
    private final Map<ResourceLocation, NonNullList<ItemStack>> categoryInventories;
    private final int points;

    private Album(UUID albumId) {
        this(albumId, new HashMap<>(), new HashMap<>());
    }

    private Album(UUID albumId, Map<ResourceLocation, List<AlbumCard>> cardsByCategory, Map<ResourceLocation, NonNullList<ItemStack>> categoryInventories) {
        this.albumId = albumId;
        this.cardsByCategory = cardsByCategory;
        this.categoryInventories = categoryInventories;
        this.points = cardsByCategory.values().stream()
                .flatMap(Collection::stream).mapToInt(AlbumCard::getPoints).sum();
    }

    public static Album emptyAlbum() {
        return new Album(UUID.randomUUID());
    }

    public static Album basedOn(Album other) {
        if (other == null) {
            return emptyAlbum();
        }
        return new Album(UUID.randomUUID(), new HashMap<>(other.cardsByCategory), new HashMap<>(other.categoryInventories));
    }

    @Override
    public boolean test(Album album) {
        return album != null && this.albumId.equals(album.albumId);
    }

    public int getPoints() {
        return points;
    }

    public ItemStack getItem(ResourceLocation categoryId, int index) {
        List<ItemStack> list = categoryInventories.get(categoryId);
        if (list == null) {
            return ItemStack.EMPTY;
        }
        return list.get(index);
    }

    public ItemStack getItem(AlbumCategory category, int index) {
        return getItem(category.identifier(), index);
    }

    public ItemStack setItem(AlbumCategory category, int index, ItemStack itemStack) {
        ItemStack prevItemStack = this.getItem(category, index);
        if (prevItemStack.isEmpty() && itemStack.isEmpty())
            return ItemStack.EMPTY;
        List<ItemStack> list = categoryInventories.computeIfAbsent(category.identifier(), key -> NonNullList.withSize(category.getSlots(), ItemStack.EMPTY));
        list.set(index, itemStack);
        return prevItemStack;
    }

    public List<AlbumCard> getCardsForCategory(ResourceLocation category) {
        List<AlbumCard> cards = cardsByCategory.get(category);
        return cards == null ? Collections.emptyList() : cards;
    }

    public void tick(Player player) {
        ActionContext context = ActionContext.of(ActionContext.PLAYER, player, ActionContext.ALBUM, this);
        AlbumBonusManager manager = AlbumBonusManager.getInstance();
        manager.applyBonuses(context);
    }

    public void removed(Player player) {
        ActionContext context = ActionContext.of(ActionContext.PLAYER, player, ActionContext.ALBUM, this);
        AlbumBonusManager manager = AlbumBonusManager.getInstance();
        manager.removeBonuses(context);
    }

    public UUID getUUID() {
        return this.albumId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Album album = (Album) o;
        return Objects.equals(albumId, album.albumId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(albumId);
    }
}
