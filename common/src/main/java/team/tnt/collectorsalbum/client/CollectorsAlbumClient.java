package team.tnt.collectorsalbum.client;

import dev.toma.configuration.Configuration;
import dev.toma.configuration.config.format.ConfigFormats;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import team.tnt.collectorsalbum.client.screen.AlbumCategoryScreen;
import team.tnt.collectorsalbum.client.screen.CardPackOpeningScreen;
import team.tnt.collectorsalbum.common.init.MenuRegistry;
import team.tnt.collectorsalbum.common.menu.AlbumCategoryMenu;
import team.tnt.collectorsalbum.network.C2S_CompleteOpeningCardPack;
import team.tnt.collectorsalbum.platform.network.PlatformNetworkManager;
import team.tnt.collectorsalbum.platform.resource.MenuScreenRegistration;

import java.util.List;

public final class CollectorsAlbumClient {

    public static CollectorsAlbumClientConfig config;

    public static void construct() {
        config = Configuration.registerConfig(CollectorsAlbumClientConfig.class, ConfigFormats.yaml()).getConfigInstance();
        MenuScreenRegistration.registerScreenFactory(MenuRegistry.ALBUM_CATEGORY, (AlbumCategoryMenu menu, Inventory inv, Component title) -> new AlbumCategoryScreen(menu, inv, title, menu.getCategory()));
    }

    public static void init() {
    }

    public static void handlePackOpening(List<ItemStack> items) {
        if (config.packOpenAnimation) {
            // open card pack opening animation screen
            Minecraft minecraft = Minecraft.getInstance();
            minecraft.setScreen(new CardPackOpeningScreen(items));
        } else {
            // send finish opening request immediately to server
            PlatformNetworkManager.NETWORK.sendServerMessage(new C2S_CompleteOpeningCardPack());
        }
    }
}
