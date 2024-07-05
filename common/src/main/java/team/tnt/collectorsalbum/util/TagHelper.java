package team.tnt.collectorsalbum.util;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class TagHelper {

    public static <T> List<T> getTagValues(TagKey<T> tagKey, Registry<T> registry) {
        Iterator<Holder<T>> iterator = registry.getTagOrEmpty(tagKey).iterator();
        List<T> output = new ArrayList<>();
        while (iterator.hasNext()) {
            Holder<T> holder = iterator.next();
            output.add(holder.value());
        }
        return output;
    }

    public static <T> T getRandomTagValue(TagKey<T> tagKey, Registry<T> registry, RandomSource randomSource) {
        List<T> values = getTagValues(tagKey, registry);
        if (values.isEmpty())
            return null;
        return values.get(randomSource.nextInt(values.size()));
    }
}
