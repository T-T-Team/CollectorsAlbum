package team.tnt.collectorsalbum;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import team.tnt.collectorsalbum.client.CollectorsAlbumClient;
import team.tnt.collectorsalbum.platform.resource.MenuScreenRegistration;

@Mod(value = CollectorsAlbum.MOD_ID, dist = Dist.CLIENT)
public final class CollectorsAlbumClientNeoforge {

    public CollectorsAlbumClientNeoforge(IEventBus eventBus) {
        CollectorsAlbumClient.construct();
        eventBus.addListener(this::registerScreens);
    }

    private void registerScreens(RegisterMenuScreensEvent event) {
        MenuScreenRegistration.bindRefs(event::register);
    }
}
