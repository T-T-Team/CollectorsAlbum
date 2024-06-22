package team.tnt.collectorsalbum.platform;

import java.util.Optional;
import java.util.ServiceLoader;

public final class JavaServiceLoader {

    public static <S> S loadService(final Class<S> type) {
        return tryLoadService(type)
                .orElseThrow(() -> new RuntimeException("No service found for type " + type));
    }

    public static <S> Optional<S> tryLoadService(final Class<S> type) {
        return ServiceLoader.load(type)
                .findFirst();
    }
}
