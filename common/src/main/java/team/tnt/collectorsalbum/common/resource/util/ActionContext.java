package team.tnt.collectorsalbum.common.resource.util;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Supplier;

public interface ActionContext {

    String PLAYER = "player";
    String ITEMSTACK = "itemstack";
    String ENTITY = "entity";
    String DAMAGE_SOURCE = "damageSource";
    String RANDOM = "random";
    String ALBUM = "album";

    <T> Optional<T> get(String key, Class<T> clazz);

    default <T> T getOrDefault(String key, Class<T> clazz, T defaultValue) {
        return get(key, clazz).orElse(defaultValue);
    }

    default <T> T getOrDefault(String key, Class<T> clazz, Supplier<T> defaultValue) {
        return get(key, clazz).orElseGet(defaultValue);
    }

    default <T> T getNullable(String key, Class<T> clazz) {
        return getOrDefault(key, clazz, (T) null);
    }

    default <T> T getOrThrow(String key, Class<T> clazz, Supplier<? extends RuntimeException> exceptionSupplier) {
        return get(key, clazz).orElseThrow(exceptionSupplier);
    }

    default <T> T getOrThrow(String key, Class<T> clazz) {
        return getOrThrow(key, clazz, () -> new NoSuchElementException("No such key exists in context for value: " + key));
    }

    static ActionContext empty() {
        return new ActionContextImpl();
    }

    static ActionContext of(String k1, Object v1) {
        ActionContextImpl context = new ActionContextImpl();
        context.values.put(k1, v1);
        return context;
    }

    static ActionContext of(String k1, Object v1, String k2, Object v2) {
        ActionContextImpl context = new ActionContextImpl();
        context.values.put(k1, v1);
        context.values.put(k2, v2);
        return context;
    }

    static ActionContext of(String k1, Object v1, String k2, Object v2, String k3, Object v3) {
        ActionContextImpl context = new ActionContextImpl();
        context.values.put(k1, v1);
        context.values.put(k2, v2);
        context.values.put(k3, v3);
        return context;
    }

    static ActionContext of(String k1, Object v1, String k2, Object v2, String k3, Object v3, String k4, Object v4) {
        ActionContextImpl context = new ActionContextImpl();
        context.values.put(k1, v1);
        context.values.put(k2, v2);
        context.values.put(k3, v3);
        context.values.put(k4, v4);
        return context;
    }

    static ActionContext of(String k1, Object v1, String k2, Object v2, String k3, Object v3, String k4, Object v4, String k5, Object v5) {
        ActionContextImpl context = new ActionContextImpl();
        context.values.put(k1, v1);
        context.values.put(k2, v2);
        context.values.put(k3, v3);
        context.values.put(k4, v4);
        context.values.put(k5, v5);
        return context;
    }

    class ActionContextImpl implements ActionContext {

        private final Map<String, Object> values = new HashMap<>();

        @Override
        public <T> Optional<T> get(String key, Class<T> clazz) {
            Object value = this.values.get(key);
            if (value == null)
                return Optional.empty();
            try {
                return Optional.of(clazz.cast(value));
            } catch (ClassCastException e) {
                return Optional.empty();
            }
        }
    }
}
