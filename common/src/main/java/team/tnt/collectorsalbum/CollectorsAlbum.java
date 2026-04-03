package team.tnt.collectorsalbum;

import dev.toma.configuration.Configuration;
import dev.toma.configuration.config.format.ConfigFormats;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import team.tnt.collectorsalbum.common.tracking.PlayerAlbumTracker;
import team.tnt.collectorsalbum.common.init.ItemDataComponentRegistry;
import team.tnt.collectorsalbum.common.item.PackContents;
import team.tnt.collectorsalbum.common.resource.AlbumCardManager;
import team.tnt.collectorsalbum.common.resource.CardPackDropManager;
import team.tnt.collectorsalbum.common.resource.drops.ItemDropProvider;
import team.tnt.collectorsalbum.common.resource.util.ActionContext;
import team.tnt.collectorsalbum.common.resource.util.ListBasedOutputBuilder;
import team.tnt.collectorsalbum.config.CollectorsAlbumConfig;
import team.tnt.collectorsalbum.integrations.PlatformIntegrations;
import team.tnt.collectorsalbum.network.NetworkManager;
import team.tnt.collectorsalbum.network.S2C_OpenCardPackScreen;
import team.tnt.collectorsalbum.network.S2C_SendDatapackResources;
import team.tnt.collectorsalbum.platform.network.PlatformNetworkManager;

import java.util.Collections;
import java.util.List;

public class CollectorsAlbum {

    public static final String MOD_ID = "collectorsalbum";
    public static final Logger LOGGER = LogManager.getLogger("CollectorsAlbum");
    public static final PlatformNetworkManager NETWORK_MANAGER = PlatformNetworkManager.create(CollectorsAlbum.MOD_ID);
    private static CollectorsAlbumConfig config;

    public static final Component WARN_NO_DROPS = Component.translatable("collectorsalbum.label.pack_empty").withStyle(ChatFormatting.GOLD);
    public static final Component USAGE = Component.translatable("collectorsalbum.label.use_open").withStyle(ChatFormatting.GRAY);
    public static final Component LABEL_UNSET = Component.translatable("collectorsalbum.label.not_set").withStyle(ChatFormatting.RED);

    public static void init() {
        config = Configuration.registerConfig(CollectorsAlbumConfig.class, ConfigFormats.YAML).getConfigInstance();
        registerPackets();
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
