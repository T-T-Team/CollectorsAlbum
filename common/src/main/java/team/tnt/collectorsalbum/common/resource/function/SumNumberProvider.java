package team.tnt.collectorsalbum.common.resource.function;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import team.tnt.collectorsalbum.common.init.NumberProviderRegistry;

import java.util.function.Function;

public class SumNumberProvider implements NumberProvider {

    public static final MapCodec<SumNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.either(Codec.INT, NumberProviderType.INSTANCE_CODEC).fieldOf("left").forGetter(t -> Either.right(t.left)),
            Codec.either(Codec.INT, NumberProviderType.INSTANCE_CODEC).fieldOf("right").forGetter(t -> Either.right(t.right))
    ).apply(instance, SumNumberProvider::new));

    private final NumberProvider left;
    private final NumberProvider right;

    public SumNumberProvider(Either<Integer, NumberProvider> left, Either<Integer, NumberProvider> right) {
        this.left = left.map(ConstantNumberProvider::new, Function.identity());
        this.right = right.map(ConstantNumberProvider::new, Function.identity());
    }

    @Override
    public int getAsInt() {
        return left.getAsInt() + right.getAsInt();
    }

    @Override
    public NumberProviderType<?> getType() {
        return NumberProviderRegistry.SUM.get();
    }
}
