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
                    Codecs.setCodec(AlbumCardManager.BY_NAME_CODEC)
            ).xmap(HashMap::new, map -> map).fieldOf("cardsByCategory").forGetter(t -> (HashMap<ResourceLocation, Set<AlbumCard>>) t.cardsByCategory),
            Codec.unboundedMap(
                    ResourceLocation.CODEC,
                    Codecs.nonNullListCodec(ItemStack.OPTIONAL_CODEC, ItemStack.EMPTY)
            ).fieldOf("categoryInventories").forGetter(t -> t.categoryInventories)
    ).apply(instance, Album::new));

    private final UUID albumId;
    private final Map<ResourceLocation, Set<AlbumCard>> cardsByCategory;
    private final Map<ResourceLocation, NonNullList<ItemStack>> categoryInventories;
    private final int points;

    private Album(UUID albumId) {
        this(albumId, new HashMap<>(), new HashMap<>());
    }

    private Album(UUID albumId, Map<ResourceLocation, Set<AlbumCard>> cardsByCategory, Map<ResourceLocation, NonNullList<ItemStack>> categoryInventories) {
        this.albumId = albumId;
        this.cardsByCategory = cardsByCategory;
        this.categoryInventories = categoryInventories;
        this.points = cardsByCategory.values().stream()
                .flatMap(Collection::stream).mapToInt(AlbumCard::getPoints).sum();
    }

    public static Album emptyAlbum() {
        return new Album(UUID.randomUUID());
    }

    public Album update(ResourceLocation category, NonNullList<ItemStack> items) {
        Map<ResourceLocation, Set<AlbumCard>> categoryMap = new HashMap<>(this.cardsByCategory);
        Set<AlbumCard> modified = categoryMap.computeIfAbsent(category, k -> new HashSet<>());
        AlbumCardManager manager = AlbumCardManager.getInstance();
        modified.addAll(items.stream().map(itemStack -> {
            if (itemStack.isEmpty())
                return null;
            return manager.getCardInfo(itemStack.getItem()).orElse(null);
        }).filter(Objects::nonNull).toList());
        Map<ResourceLocation, NonNullList<ItemStack>> newCategoryMap = new HashMap<>(this.categoryInventories);
        newCategoryMap.put(category, items);
        return new Album(UUID.randomUUID(), categoryMap, newCategoryMap);
    }

    @Override
    public boolean test(Album album) {
        return album != null && this.albumId.equals(album.albumId);
    }

    public int getPoints() {
        return points;
    }

    public NonNullList<ItemStack> getInventory(ResourceLocation category) {
        return categoryInventories.getOrDefault(category, NonNullList.withSize(1, ItemStack.EMPTY));
    }

    public Collection<AlbumCard> getCardsForCategory(ResourceLocation category) {
        Set<AlbumCard> cards = cardsByCategory.get(category);
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
