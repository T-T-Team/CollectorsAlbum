package team.tnt.collectorsalbum.common.command;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import team.tnt.collectorsalbum.common.Album;
import team.tnt.collectorsalbum.common.AlbumCategory;
import team.tnt.collectorsalbum.common.card.AlbumCard;
import team.tnt.collectorsalbum.common.init.ItemDataComponentRegistry;
import team.tnt.collectorsalbum.common.init.ItemRegistry;
import team.tnt.collectorsalbum.common.resource.AlbumCardManager;
import team.tnt.collectorsalbum.common.resource.AlbumCategoryManager;

import java.util.*;

public final class CollectorsAlbumCommand {

    private static final SimpleCommandExceptionType NOT_PLAYER = new SimpleCommandExceptionType(Component.literal("Command must be executed by player"));

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("album")
                        .requires(source -> source.hasPermission(2))
                        .then(
                                Commands.literal("giveCompleted")
                                        .executes(CollectorsAlbumCommand::givePlayerCompletedAlbum)
                        )
        );
    }

    private static int givePlayerCompletedAlbum(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        ServerPlayer player = source.getPlayer();
        if (player == null) {
            throw NOT_PLAYER.create();
        }

        UUID albumUUID = UUID.randomUUID();
        AlbumCategoryManager categoryManager = AlbumCategoryManager.getInstance();
        AlbumCardManager cardManager = AlbumCardManager.getInstance();
        Collection<AlbumCategory> registeredCategories = categoryManager.listCategories();

        Map<ResourceLocation, Set<AlbumCard>> byCategory = new HashMap<>();
        Map<ResourceLocation, NonNullList<ItemStack>> items = new HashMap<>();

        Multimap<ResourceLocation, CardItem> cardItemMap = ArrayListMultimap.create();
        for (Map.Entry<Item, AlbumCard> entry : cardManager.getByItemMap().entrySet()) {
            ResourceLocation key = entry.getValue().category();
            cardItemMap.put(key, new CardItem(entry.getValue(), entry.getKey()));
        }

        for (AlbumCategory category : registeredCategories) {
            Set<AlbumCard> cardSet = new HashSet<>();
            NonNullList<ItemStack> inventory = NonNullList.withSize(category.getCardNumbers().length, ItemStack.EMPTY);
            Collection<CardItem> validCards = cardItemMap.get(category.identifier());
            int slotIndex = 0;
            for (int number : category.getCardNumbers()) {
                CardItem cardHolder = validCards.stream()
                        .filter(holder -> holder.card.cardNumber() == number)
                        .max(Comparator.comparingInt(holder -> holder.card.getPoints()))
                        .orElse(null);
                if (cardHolder != null) {
                    inventory.set(slotIndex, new ItemStack(cardHolder.item));
                    cardSet.add(cardHolder.card);
                }
                ++slotIndex;
            }
            byCategory.put(category.identifier(), cardSet);
            items.put(category.identifier(), inventory);
        }
        Album album = new Album(albumUUID, byCategory, items);
        ItemStack albumItemStack = new ItemStack(ItemRegistry.ALBUM.get());
        albumItemStack.set(ItemDataComponentRegistry.ALBUM.get(), album);

        player.addItem(albumItemStack);
        player.sendSystemMessage(Component.literal("Album generated"));
        return 0;
    }

    private record CardItem(AlbumCard card, Item item) {

    }
}
