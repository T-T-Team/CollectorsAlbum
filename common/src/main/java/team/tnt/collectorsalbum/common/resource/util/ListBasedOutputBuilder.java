package team.tnt.collectorsalbum.common.resource.util;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ListBasedOutputBuilder<T> implements OutputBuilder<T> {

    private final List<T> list;

    public ListBasedOutputBuilder(List<T> list) {
        this.list = list;
    }

    public static <T> ListBasedOutputBuilder<T> createArrayListBased() {
        return new ListBasedOutputBuilder<>(new ArrayList<>());
    }

    public static <T> ListBasedOutputBuilder<T> createLinkedListBased() {
        return new ListBasedOutputBuilder<>(new LinkedList<>());
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
