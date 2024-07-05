package team.tnt.collectorsalbum.platform;

public interface Platform {

    Platform INSTANCE = JavaServiceLoader.loadService(Platform.class);

    Side getSide();

    boolean isModLoaded(String namespace);
}
