package team.tnt.collectorsalbum.util;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.function.Function;

public record PageInfo<T>(T element, int pageIndex) {

    public static <B extends ByteBuf, T> StreamCodec<B, PageInfo<T>> streamCodec(StreamCodec<B, T> elementCodec) {
        return StreamCodec.composite(
                elementCodec, PageInfo::element,
                ByteBufCodecs.INT, PageInfo::pageIndex,
                PageInfo::new
        );
    }

    private static final PageInfo<Object> EMPTY = new PageInfo<>(null, -1);

    @SuppressWarnings("unchecked")
    public static <T> PageInfo<T> empty() {
        return (PageInfo<T>) EMPTY;
    }

    public static <T> PageInfo<T> firstPage(T element) {
        return new PageInfo<>(element, 0);
    }

    public static <T, V> PageInfo<T> firstPage(V elementHolder, Function<V, T> mapper) {
        return firstPage(mapper.apply(elementHolder));
    }

    public boolean isEmpty() {
        return this == EMPTY || this.element == null || this.pageIndex < 0;
    }

    public boolean isFirstPage() {
        return this.pageIndex == 0;
    }

    public <R> PageInfo<R> map(Function<T, R> mapper) {
        R converted = mapper.apply(this.element);
        return new PageInfo<>(converted, this.pageIndex);
    }

    public <R> R mapElement(Function<T, R> mapper) {
        return mapper.apply(this.element);
    }

    public int getElementIndex(int pageSize) {
        return this.pageIndex * pageSize;
    }
}
