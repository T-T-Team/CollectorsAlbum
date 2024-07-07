package team.tnt.collectorsalbum.common.resource.util;

import java.util.ArrayList;
import java.util.List;

public interface OutputBuilder<T> {

    void accept(T item);

    void acceptAll(List<T> items);

    class OutputBuilderImpl<T> implements OutputBuilder<T> {

        private final List<T> drops;

        public OutputBuilderImpl() {
            this.drops = new ArrayList<>();
        }

        @Override
        public void accept(T item) {
            this.drops.add(item);
        }

        @Override
        public void acceptAll(List<T> items) {
            this.drops.addAll(items);
        }

        public List<T> getDrops() {
            return drops;
        }
    }
}
