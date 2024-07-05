package team.tnt.collectorsalbum.common.resource.function;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import team.tnt.collectorsalbum.common.init.NumberProviderRegistry;

public class ConstantNumberProvider implements NumberProvider {

    public static final MapCodec<ConstantNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("value").forGetter(t -> t.value)
    ).apply(instance, ConstantNumberProvider::new));

    public static final ConstantNumberProvider ZERO = new ConstantNumberProvider(0);
    public static final ConstantNumberProvider ONE = new ConstantNumberProvider(1);

    private final int value;

    public ConstantNumberProvider(int value) {
        this.value = value;
    }

    @Override
    public int getAsInt() {
        return this.value;
    }

    @Override
    public NumberProviderType<?> getType() {
        return NumberProviderRegistry.CONSTANT.get();
    }
}
