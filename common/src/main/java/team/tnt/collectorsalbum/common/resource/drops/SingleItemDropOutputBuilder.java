package team.tnt.collectorsalbum.common.resource.drops;

import java.util.List;

public class SingleItemDropOutputBuilder<T> implements DropOutputBuilder<T> {

    private T item;
    private boolean allowsOverwrite;

    private SingleItemDropOutputBuilder(boolean allowsOverwrite) {
        this.allowsOverwrite = allowsOverwrite;
    }

    public static <T> SingleItemDropOutputBuilder<T> acceptsFirst() {
        return new SingleItemDropOutputBuilder<>(false);
    }

    public static <T> SingleItemDropOutputBuilder<T> acceptsLast() {
        return new SingleItemDropOutputBuilder<>(true);
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
