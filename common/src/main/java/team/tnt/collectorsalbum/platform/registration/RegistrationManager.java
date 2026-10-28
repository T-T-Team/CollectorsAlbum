package team.tnt.collectorsalbum.platform.registration;

import net.minecraft.resources.Identifier;

import java.util.function.Function;

public interface RegistrationManager<T> {

    <V extends T> Reference<V> register(String name, Function<Identifier, V> constructor);

    default <V extends T> Reference<V> registerInstance(String name, V value) {
        return register(name, _ -> value);
    }

    String getNamespace();
}
