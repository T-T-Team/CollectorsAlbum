package team.tnt.collectorsalbum.common.resource.drops;

import java.util.ArrayList;
import java.util.List;

public interface DropOutputBuilder<T> {

    void accept(T item);

    void acceptAll(List<T> items);

    class DropOutputBuilderImpl<T> implements DropOutputBuilder<T> {

        private final List<T> drops;

        public DropOutputBuilderImpl() {
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
