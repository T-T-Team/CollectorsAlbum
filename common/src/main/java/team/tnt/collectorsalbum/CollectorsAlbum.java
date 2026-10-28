package team.tnt.collectorsalbum;

import com.mojang.serialization.MapCodec;
import dev.toma.configuration.Configuration;
import dev.toma.configuration.config.format.ConfigFormats;
import io.netty.buffer.ByteBuf;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import team.tnt.collectorsalbum.common.AlbumCategory;
import team.tnt.collectorsalbum.common.card.AlbumCard;
import team.tnt.collectorsalbum.common.init.*;
import team.tnt.collectorsalbum.common.item.PackContents;
import team.tnt.collectorsalbum.common.resource.AlbumCardManager;
import team.tnt.collectorsalbum.common.resource.CardPackDropManager;
import team.tnt.collectorsalbum.common.resource.bonus.AlbumBonus;
import team.tnt.collectorsalbum.common.resource.drops.ItemDropProvider;
import team.tnt.collectorsalbum.common.resource.function.NumberProvider;
import team.tnt.collectorsalbum.common.resource.util.ActionContext;
import team.tnt.collectorsalbum.common.resource.util.ListBasedOutputBuilder;
import team.tnt.collectorsalbum.common.tracking.PlayerAlbumTracker;
import team.tnt.collectorsalbum.config.CollectorsAlbumConfig;
import team.tnt.collectorsalbum.integrations.PlatformIntegrations;
import team.tnt.collectorsalbum.integrations.trinkets_updated.TrinketsUpdatedPlugin;
import team.tnt.collectorsalbum.network.NetworkManager;
import team.tnt.collectorsalbum.network.S2C_OpenCardPackScreen;
import team.tnt.collectorsalbum.network.S2C_SendDatapackResources;
import team.tnt.collectorsalbum.platform.Platform;
import team.tnt.collectorsalbum.platform.network.PlatformNetworkManager;
import team.tnt.collectorsalbum.platform.registration.RegistrationManager;
import team.tnt.collectorsalbum.util.PageInfo;

import java.util.Collections;
import java.util.List;

public class CollectorsAlbum {

    public static final String MOD_ID = "collectorsalbum";
    public static final Logger LOGGER = LogManager.getLogger("CollectorsAlbum");
    public static final PlatformNetworkManager NETWORK_MANAGER = PlatformNetworkManager.create(CollectorsAlbum.MOD_ID);
    public static final StreamCodec<ByteBuf, PageInfo<Identifier>> ALBUM_PAGE_STREAM_CODEC = PageInfo.streamCodec(Identifier.STREAM_CODEC);
    private static CollectorsAlbumConfig config;

    // Custom registries
    @Deprecated public static final Registry<MapCodec<? extends AlbumCard>> CARD_TYPE = Platform.INSTANCE.createRegistry(MOD_ID, "card_type");
    @Deprecated public static final Registry<MapCodec<? extends AlbumCategory>> CATEGORY = Platform.INSTANCE.createRegistry(MOD_ID, "category");
    public static final Registry<MapCodec<? extends NumberProvider>> NUMBER_PROVIDER = Platform.INSTANCE.createRegistry(MOD_ID, "number_provider");
    public static final Registry<MapCodec<? extends ItemDropProvider>> ITEM_DROP_PROVIDER = Platform.INSTANCE.createRegistry(MOD_ID, "item_drop_provider");
    public static final Registry<MapCodec<? extends AlbumBonus>> ALBUM_BONUS = Platform.INSTANCE.createRegistry(MOD_ID, "album_bonus");

    // Registry managers
    public static final RegistrationManager<DataComponentType<?>> DATA_COMPONENT_REGISTRY = Platform.INSTANCE.createRegistryManager(BuiltInRegistries.DATA_COMPONENT_TYPE);
    public static final RegistrationManager<Item> ITEM_REGISTRY = Platform.INSTANCE.createRegistryManager(BuiltInRegistries.ITEM);
    public static final RegistrationManager<CreativeModeTab> ITEM_GROUP_REGISTRY = Platform.INSTANCE.createRegistryManager(BuiltInRegistries.CREATIVE_MODE_TAB);
    public static final RegistrationManager<SoundEvent> SOUND_REGISTRY = Platform.INSTANCE.createRegistryManager(BuiltInRegistries.SOUND_EVENT);
    public static final RegistrationManager<MenuType<?>> MENU_REGISTRY = Platform.INSTANCE.createRegistryManager(BuiltInRegistries.MENU);
    @Deprecated public static final RegistrationManager<MapCodec<? extends AlbumCard>> CARD_TYPE_REGISTRY = Platform.INSTANCE.createRegistryManager(CARD_TYPE);
    @Deprecated public static final RegistrationManager<MapCodec<? extends AlbumCategory>> CATEGORY_REGISTRY = Platform.INSTANCE.createRegistryManager(CATEGORY);
    public static final RegistrationManager<MapCodec<? extends NumberProvider>> NUMBER_PROVIDER_REGISTRY = Platform.INSTANCE.createRegistryManager(NUMBER_PROVIDER);
    public static final RegistrationManager<MapCodec<? extends ItemDropProvider>> ITEM_DROP_PROVIDER_REGISTRY = Platform.INSTANCE.createRegistryManager(ITEM_DROP_PROVIDER);
    public static final RegistrationManager<MapCodec<? extends AlbumBonus>> ALBUM_BONUS_REGISTRY = Platform.INSTANCE.createRegistryManager(ALBUM_BONUS);

    public static final Component WARN_NO_DROPS = Component.translatable("collectorsalbum.label.pack_empty").withStyle(ChatFormatting.GOLD);
    public static final Component USAGE = Component.translatable("collectorsalbum.label.use_open").withStyle(ChatFormatting.GRAY);
    public static final Component LABEL_UNSET = Component.translatable("collectorsalbum.label.not_set").withStyle(ChatFormatting.RED);

    public static void init() {
        config = Configuration.registerConfig(CollectorsAlbumConfig.class, ConfigFormats.YAML).getConfigInstance();

        ItemDataComponentRegistry.init(DATA_COMPONENT_REGISTRY);
        ItemRegistry.init(ITEM_REGISTRY);
        ItemGroupRegistry.init(ITEM_GROUP_REGISTRY);
        SoundRegistry.init(SOUND_REGISTRY);
        MenuRegistry.init(MENU_REGISTRY);
        CardTypeRegistry.init(CARD_TYPE_REGISTRY);
        CategoryRegistry.init(CATEGORY_REGISTRY);
        NumberProviderRegistry.init(NUMBER_PROVIDER_REGISTRY);
        ItemDropProviderRegistry.init(ITEM_DROP_PROVIDER_REGISTRY);
        AlbumBonusRegistry.init(ALBUM_BONUS_REGISTRY);

        registerPackets();
        PlatformIntegrations.registerStartupPlugin("trinkets_updated", TrinketsUpdatedPlugin::instance);
        PlatformIntegrations.onStartup();
    }

    public static CollectorsAlbumConfig getConfig() {
        return config;
    }

    public static void tickPlayer(Player player) {
        Level level = player.level();
        long time = level.getGameTime();
        if (level.isClientSide() || time % 100L != 0L) {
            return;
        }
        PlayerAlbumTracker.get().update(player);
    }

    public static void forceAlbumReload(Player player) {
        if (!player.level().isClientSide()) {
            PlayerAlbumTracker tracker = PlayerAlbumTracker.get();
            tracker.deleteCachedAlbum(player);
            tracker.update(player);
        }
    }

    public static void sendPlayerDatapacks(ServerPlayer player) {
        if (player != null) {
            LOGGER.debug("Sending server resources to client {}", player.getUUID());
            PlatformNetworkManager.NETWORK.sendClientMessage(player, new S2C_SendDatapackResources());
            forceAlbumReload(player);
        }
    }

    public static void playerLoggedOut(Player player) {
        PlayerAlbumTracker tracker = PlayerAlbumTracker.get();
        tracker.deleteCachedAlbum(player);
    }

    public static void serverStopped() {
        PlayerAlbumTracker tracker = PlayerAlbumTracker.get();
        tracker.clearCache();
    }

    public static void openPack(ServerPlayer player) {
        ItemStack itemStack = player.getItemInHand(InteractionHand.MAIN_HAND);
        if (!itemStack.has(ItemDataComponentRegistry.PACK_DROPS_TABLE.get())) {
            LOGGER.warn("No pack drops path set for item {} on player {}", itemStack, player);
            return;
        }
        ListBasedOutputBuilder<ItemStack> outputBuilder = ListBasedOutputBuilder.createArrayListBased();
        CardPackDropManager dropManager = CardPackDropManager.getInstance();
        Identifier lootTablePath = itemStack.get(ItemDataComponentRegistry.PACK_DROPS_TABLE.get());
        ItemDropProvider provider = dropManager.getProvider(lootTablePath);
        ActionContext context = ActionContext.of(ActionContext.PLAYER, player, ActionContext.ITEMSTACK, itemStack, ActionContext.RANDOM, player.getRandom());
        provider.generateDrops(context, outputBuilder);
        AlbumCardManager cardManager = AlbumCardManager.getInstance();
        List<ItemStack> generatedDrops = outputBuilder.getItems();
        List<ItemStack> drops = cardManager.processDrops(generatedDrops);
        if (generatedDrops.size() != drops.size()) {
            LOGGER.warn("Some invalid drops have been generated for player {} on item {}, filtering invalid drops...", player, itemStack);
            LOGGER.debug("Generated drops: {}, Filtered drops: {}", generatedDrops, drops);
        }
        player.getCooldowns().addCooldown(itemStack, 20);
        if (drops.isEmpty()) {
            player.sendOverlayMessage(WARN_NO_DROPS);
            return;
        }
        Collections.shuffle(drops);
        itemStack.set(ItemDataComponentRegistry.PACK_CONTENTS.get(), new PackContents(drops));
        LOGGER.debug("Player {} is opening card pack {} with generated drops: {}", player, itemStack, itemStack.get(ItemDataComponentRegistry.PACK_CONTENTS.get()));
        PlatformNetworkManager.NETWORK.sendClientMessage(player, new S2C_OpenCardPackScreen(drops));
    }

    public static void addCardPackTooltip(ItemStack itemStack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(USAGE);
        if (flag.isAdvanced()) {
            Identifier dropsPath = itemStack.get(ItemDataComponentRegistry.PACK_DROPS_TABLE.get());
            Component description = dropsPath != null ? Component.literal(dropsPath.toString()) : LABEL_UNSET;
            tooltip.add(Component.translatable("collectorsalbum.label.custom_drop_table", description));
        }
    }

    private static void registerPackets() {
        NetworkManager.init();
    }
}
