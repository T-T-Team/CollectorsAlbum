package team.tnt.collectorsalbum.platform.registration;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import net.minecraft.resources.Identifier;

import java.util.Objects;

public final class Reference<T> implements Supplier<T> {

    private final Identifier identifier;
    private final Supplier<T> provider;

    public Reference(Identifier identifier, Supplier<T> provider) {
        this.identifier = identifier;
        this.provider = provider;
    }

    public static <T> Reference<T> of(Identifier identifier, Supplier<T> provider) {
        return new Reference<>(identifier, Suppliers.memoize(provider));
    }

    @Override
    public T get() {
        return this.provider.get();
    }

    public Identifier getKey() {
        return this.identifier;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof Reference<?> reference)) return false;
        return Objects.equals(identifier, reference.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(identifier);
    }

    @Override
    public String toString() {
        return this.identifier.toString();
    }
}
