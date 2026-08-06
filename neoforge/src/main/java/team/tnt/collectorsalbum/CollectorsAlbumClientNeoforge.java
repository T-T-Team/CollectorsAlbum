package team.tnt.collectorsalbum;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterPictureInPictureRenderersEvent;
import net.neoforged.neoforge.common.NeoForge;
import team.tnt.collectorsalbum.client.CollectorsAlbumClient;
import team.tnt.collectorsalbum.client.screen.pip.ScalableItemPip;
import team.tnt.collectorsalbum.client.screen.pip.ScalableItemRenderState;
import team.tnt.collectorsalbum.platform.resource.MenuScreenRegistration;

@Mod(value = CollectorsAlbum.MOD_ID, dist = Dist.CLIENT)
public final class CollectorsAlbumClientNeoforge {

    public CollectorsAlbumClientNeoforge(IEventBus eventBus) {
        CollectorsAlbumClient.construct();
        eventBus.addListener(this::registerScreens);
        eventBus.addListener(this::registerPictureInPictureRenderers);

        NeoForge.EVENT_BUS.addListener(this::onClientTick);
    }

    private void registerScreens(RegisterMenuScreensEvent event) {
        MenuScreenRegistration.bindRefs(event::register);
    }

    private void onClientTick(ClientTickEvent.Post event) {
        CollectorsAlbumClient.clientTick();
    }

    private void registerPictureInPictureRenderers(RegisterPictureInPictureRenderersEvent event) {
        event.register(ScalableItemRenderState.class, ScalableItemPip::new);
    }
}
