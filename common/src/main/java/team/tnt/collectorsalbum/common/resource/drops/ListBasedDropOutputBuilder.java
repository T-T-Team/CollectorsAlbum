package team.tnt.collectorsalbum.common.resource.drops;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ListBasedDropOutputBuilder<T> implements DropOutputBuilder<T> {

    private final List<T> list;

    public ListBasedDropOutputBuilder(List<T> list) {
        this.list = list;
    }

    public static <T> ListBasedDropOutputBuilder<T> createArrayListBased() {
        return new ListBasedDropOutputBuilder<>(new ArrayList<>());
    }

    public static <T> ListBasedDropOutputBuilder<T> createLinkedListBased() {
        return new ListBasedDropOutputBuilder<>(new LinkedList<>());
    }

    @Override
    public void accept(T item) {
        this.list.add(item);
    }

    @Override
    public void acceptAll(List<T> items) {
        this.list.addAll(items);
    }

    public List<T> getItems() {
        return this.list;
    }
}
