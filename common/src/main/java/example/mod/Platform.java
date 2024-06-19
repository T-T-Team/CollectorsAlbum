package example.mod;

import example.mod.service.JavaServiceLoader;

public interface Platform {

    Platform PLATFORM = JavaServiceLoader.load(Platform.class);

    static Platform get() {
        return PLATFORM;
    }

    String getModLoaderName();
}
