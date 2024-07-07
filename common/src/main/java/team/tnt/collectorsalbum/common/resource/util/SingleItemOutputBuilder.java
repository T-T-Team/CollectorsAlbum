package team.tnt.collectorsalbum.common.resource.util;

import java.util.List;

public class SingleItemOutputBuilder<T> implements OutputBuilder<T> {

    private T item;
    private boolean allowsOverwrite;

    private SingleItemOutputBuilder(boolean allowsOverwrite) {
        this.allowsOverwrite = allowsOverwrite;
    }

    public static <T> SingleItemOutputBuilder<T> acceptsFirst() {
        return new SingleItemOutputBuilder<>(false);
    }

    public static <T> SingleItemOutputBuilder<T> acceptsLast() {
        return new SingleItemOutputBuilder<>(true);
    }

    @Override
    public void accept(T item) {
        if (this.allowsOverwrite || this.item == null) {
            this.item = item;
        }
    }

    @Override
    public void acceptAll(List<T> items) {
        items.forEach(this::accept);
    }

    public T getItemOrDefault(T defaultItem) {
        return item != null ? item : defaultItem;
    }

    public boolean isLocked() {
        return !this.allowsOverwrite && this.item != null;
    }
}
