package team.tnt.collectorsalbum.common;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import team.tnt.collectorsalbum.common.card.AlbumCard;
import team.tnt.collectorsalbum.common.card.CardRarity;
import team.tnt.collectorsalbum.common.card.RarityHolder;
import team.tnt.collectorsalbum.common.resource.AlbumBonusManager;
import team.tnt.collectorsalbum.common.resource.AlbumCardManager;
import team.tnt.collectorsalbum.common.resource.AlbumCategoryManager;
import team.tnt.collectorsalbum.common.resource.util.ActionContext;
import team.tnt.collectorsalbum.platform.Codecs;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public final class Album implements Predicate<Album> {

    public static final Codec<ItemStack> NULLABLE_ITEMSTACK_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BuiltInRegistries.ITEM.holderByNameCodec().fieldOf("id").forGetter(ItemStack::getItemHolder),
            Codec.INT.optionalFieldOf("count", 1).forGetter(ItemStack::getCount),
            DataComponentPatch.CODEC.optionalFieldOf("components", DataComponentPatch.EMPTY).forGetter(ItemStack::getComponentsPatch)
    ).apply(instance, ItemStack::new));

    public static final Codec<Album> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            UUIDUtil.CODEC.fieldOf("albumId").forGetter(t -> t.albumId),
            Codec.unboundedMap(
                    ResourceLocation.CODEC,
                    Codecs.setCodec(AlbumCardManager.BY_NAME_CODEC)
            ).xmap(HashMap::new, map -> map).fieldOf("cardsByCategory").forGetter(t -> (HashMap<ResourceLocation, Set<AlbumCard>>) t.cardsByCategory),
            Codec.unboundedMap(
                    ResourceLocation.CODEC,
                    Codecs.nonNullListCodec(NULLABLE_ITEMSTACK_CODEC, ItemStack.EMPTY)
            ).fieldOf("categoryInventories").forGetter(t -> t.categoryInventories)
    ).apply(instance, Album::new));

    private final UUID albumId;
    private final Map<ResourceLocation, Set<AlbumCard>> cardsByCategory;
    private final Map<ResourceLocation, NonNullList<ItemStack>> categoryInventories;
    private final int points;

    private Album(Mutable mutable) {
        this.albumId = UUID.randomUUID();
        this.cardsByCategory = new HashMap<>();
        this.categoryInventories = new HashMap<>();
        int pointCounter = 0;
        AlbumCardManager manager = AlbumCardManager.getInstance();
        for (Map.Entry<ResourceLocation, NonNullList<ItemStack>> entry : mutable.inventories.entrySet()) {
            ResourceLocation key = entry.getKey();
            NonNullList<ItemStack> inventory = entry.getValue();
            for (int i = 0; i < inventory.size(); i++) {
                ItemStack itemStack = inventory.get(i);
                if (!itemStack.isEmpty()) {
                    AlbumCard card = manager.getCardInfo(itemStack.getItem())
                            .orElseThrow(() -> new IllegalArgumentException(String.format("Attempting to save undefined card for item %s into album", itemStack)));
                    this.cardsByCategory.computeIfAbsent(key, t -> new HashSet<>()).add(card);
                    this.categoryInventories.computeIfAbsent(key, t -> NonNullList.withSize(inventory.size(), ItemStack.EMPTY)).set(i, itemStack.copy());
                    pointCounter += card.getPoints();
                }
            }
        }
        this.points = pointCounter;
    }

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

    public int countCards() {
        return this.cardsByCategory.values().stream().mapToInt(Set::size).sum();
    }

    public Map<CardRarity, CardRarityStatistics> calculateRarityRatios() {
        List<RarityHolder> rarityCards = this.cardsByCategory.values().stream()
                .flatMap(Collection::stream).filter(card -> card instanceof RarityHolder).map(card -> (RarityHolder) card)
                .toList();
        Map<CardRarity, CardRarityStatistics> map = new EnumMap<>(CardRarity.class);
        Map<CardRarity, List<RarityHolder>> byRarity = rarityCards.stream().collect(Collectors.groupingBy(RarityHolder::rarity));
        int totalCards = Math.max(rarityCards.size(), 1);
        for (CardRarity rarity : CardRarity.values()) {
            List<RarityHolder> list = byRarity.getOrDefault(rarity, Collections.emptyList());
            CardRarityStatistics statistics = new CardRarityStatistics(list.size(), totalCards);
            map.put(rarity, statistics);
        }
        return map;
    }

    public List<AlbumCategoryStatistics> calculateStatistics() {
        AlbumCategoryManager manager = AlbumCategoryManager.getInstance();
        Collection<AlbumCategory> categories = manager.listCategories();
        return categories.stream()
                .map(cat -> {
                    Set<AlbumCard> cardsInCategory = this.cardsByCategory.getOrDefault(cat.identifier(), Collections.emptySet());
                    int categoryTotal = cat.getCardNumbers().length;
                    int categoryCollected = cardsInCategory.size();
                    return new AlbumCategoryStatistics(cat, categoryCollected, categoryTotal);
                })
                .filter(stat -> stat.allCards() > 0)
                .sorted(Comparator.comparingDouble(AlbumCategoryStatistics::getCollectedProgress).reversed())
                .toList();
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

    public record CardRarityStatistics(int collected, int total) {

        public float getRatio() {
            return total > 0 ? collected / (float) total : 0.0F;
        }
    }

    public record AlbumCategoryStatistics(AlbumCategory category, int collectedCards, int allCards) {

        public float getCollectedProgress() {
            return allCards > 0 ? collectedCards / (float) allCards : 0.0F;
        }
    }

    public static final class Mutable {

        private final Map<ResourceLocation, NonNullList<ItemStack>> inventories;

        public Mutable(Album album) {
            Map<ResourceLocation, NonNullList<ItemStack>> inventories = new HashMap<>();
            for (Map.Entry<ResourceLocation, NonNullList<ItemStack>> entry : album.categoryInventories.entrySet()) {
                ResourceLocation key = entry.getKey();
                NonNullList<ItemStack> categoryInventory = entry.getValue();
                AlbumCategoryManager.getInstance().findById(key).ifPresent(category -> {
                    NonNullList<ItemStack> inventory = NonNullList.withSize(category.getCardNumbers().length, ItemStack.EMPTY);
                    for (int i = 0; i < categoryInventory.size(); i++) {
                        ItemStack itemStack = categoryInventory.get(i);
                        if (!itemStack.isEmpty()) {
                            inventory.set(i, itemStack.copy());
                        }
                    }
                    inventories.put(key, inventory);
                });
            }
            this.inventories = inventories;
        }

        public void set(ResourceLocation category, int index, ItemStack itemStack) {
            NonNullList<ItemStack> inventory = inventories.get(category);
            if (inventory == null) {
                AlbumCategoryManager manager = AlbumCategoryManager.getInstance();
                AlbumCategory albumCategory = manager.findById(category)
                        .orElseThrow(() -> new IllegalArgumentException(String.format("Attempting to insert item %s into unknown category %s", itemStack, category)));
                inventory = NonNullList.withSize(albumCategory.getCardNumbers().length, ItemStack.EMPTY);
                inventories.put(category, inventory);
            }
            inventory.set(index, itemStack.copy());
        }

        public Album toImmutable() {
            return new Album(this);
        }
    }
}
