package team.tnt.collectorsalbum.client;

import dev.toma.configuration.Configuration;
import dev.toma.configuration.config.format.ConfigFormats;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import team.tnt.collectorsalbum.client.screen.CardPackOpeningScreen;
import team.tnt.collectorsalbum.network.C2S_CompleteOpeningCardPack;
import team.tnt.collectorsalbum.platform.network.PlatformNetworkManager;

import java.util.List;

public final class CollectorsAlbumClient {

    public static CollectorsAlbumClientConfig config;

    public static void construct() {
        config = Configuration.registerConfig(CollectorsAlbumClientConfig.class, ConfigFormats.yaml()).getConfigInstance();
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
