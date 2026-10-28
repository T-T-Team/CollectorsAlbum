package team.tnt.collectorsalbum.platform.registration;

import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@SuppressWarnings("unchecked")
public final class FabricRegistrationManager<T> implements RegistrationManager<T> {

    private final Registry<T> registry;
    private final String namespace;
    private List<Reference<?>> entries = new ArrayList<>();

    public FabricRegistrationManager(Registry<T> registry, String namespace) {
        this.registry = registry;
        this.namespace = namespace;
    }

    @Override
    public <V extends T> Reference<V> register(String name, Function<Identifier, V> constructor) {
        Identifier identifier = Identifier.fromNamespaceAndPath(this.namespace, name);
        Reference<V> reference = Reference.of(identifier, () -> constructor.apply(identifier));
        this.entries.add(reference);
        return reference;
    }

    @Override
    public String getNamespace() {
        return this.namespace;
    }

    public <V extends T> void bindRegistryElements() {
        this.entries.forEach(reference -> {
            Identifier identifier = reference.getKey();
            Registry.register(this.registry, identifier, (V) reference.get());
        });
        this.entries = null;
    }
}
