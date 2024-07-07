package team.tnt.collectorsalbum.platform;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.impl.launch.server.FabricServerLauncher;
import net.minecraft.server.MinecraftServer;

public class FabricPlatform implements Platform {

    public static MinecraftServer server;

    @Override
    public Side getSide() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT ? Side.CLIENT : Side.SERVER;
    }

    @Override
    public boolean isModLoaded(String namespace) {
        return FabricLoader.getInstance().isModLoaded(namespace);
    }

    @Override
    public MinecraftServer getServerInstance() {
        return server;
    }
}
