package team.tnt.collectorsalbum.platform;

import net.minecraft.server.MinecraftServer;

public interface Platform {

    Platform INSTANCE = JavaServiceLoader.loadService(Platform.class);

    Side getSide();

    boolean isModLoaded(String namespace);

    MinecraftServer getServerInstance();
}
