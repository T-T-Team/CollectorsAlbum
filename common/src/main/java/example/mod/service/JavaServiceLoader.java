package example.mod.service;

import java.util.ServiceLoader;

public final class JavaServiceLoader {

    /**
     * Loads platform specific implementations for given type. Implementation has to be defined within META-INF for
     * each submodule
     */
    public static <T> T load(Class<T> type) {
        return ServiceLoader.load(type).findFirst()
                .orElseThrow(() -> new IllegalStateException("No implementation found for " + type));
    }

    private JavaServiceLoader() {}
}
