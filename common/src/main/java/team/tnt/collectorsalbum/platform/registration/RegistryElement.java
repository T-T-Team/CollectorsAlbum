package team.tnt.collectorsalbum.platform.registration;

import java.util.Objects;
import java.util.function.Supplier;

public final class RegistryElement<B, R extends B> implements Supplier<R> {

    private final Supplier<R> provider;
    private R cachedValue;

    RegistryElement(final Supplier<R> provider) {
        this.provider = provider;
    }

    @Override
    public R get() {
        if (this.cachedValue == null) {
            this.cachedValue = Objects.requireNonNull(this.provider.get());
        }
        return this.cachedValue;
    }
}
