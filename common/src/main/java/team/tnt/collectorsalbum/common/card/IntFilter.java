package team.tnt.collectorsalbum.common.card;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import team.tnt.collectorsalbum.common.resource.function.NumberProvider;

public record IntFilter(int min, int max) {

    public static final MapCodec<IntFilter> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            NumberProvider.INT_CODEC.optionalFieldOf("min", Integer.MIN_VALUE).forGetter(IntFilter::min),
            NumberProvider.INT_CODEC.optionalFieldOf("max", Integer.MAX_VALUE).forGetter(IntFilter::max)
    ).apply(instance, IntFilter::new));
    public static final Codec<IntFilter> CODEC = MAP_CODEC.codec();
    public static final IntFilter NO_FILTER = new IntFilter(Integer.MIN_VALUE, Integer.MAX_VALUE);

    public boolean test(int value) {
        return value >= this.min && value <= this.max;
    }

    public Component getDisplayComponent() {
        if (this.min > Integer.MIN_VALUE && this.max < Integer.MAX_VALUE) {
            return Component.literal("[" + this.min + "," + this.max + "]");
        } else if (this.min == this.max) {
            return Component.literal("{" + this.min + "}");
        } else if (this.min > Integer.MIN_VALUE) {
            return Component.literal("[" + this.min + ",...)");
        } else if (this.max < Integer.MAX_VALUE) {
            return Component.literal("(...," + this.max + "]");
        }
        return null;
    }
}
