package team.tnt.collectorsalbum.common.registry;

import java.util.function.Supplier;

public class RegistryValue<A, T extends A> implements Supplier<T> {

    private final Supplier<T> supplier;
    private T value;

    public RegistryValue(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    @Override
    public T get() {
        if (this.value == null) {
            this.value = this.supplier.get();
        }
        return this.value;
    }
}
