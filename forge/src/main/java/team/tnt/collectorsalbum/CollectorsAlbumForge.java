package team.tnt.collectorsalbum;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(CollectorsAlbum.MOD_ID)
public class CollectorsAlbumForge {

    public CollectorsAlbumForge() {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
    }
}
