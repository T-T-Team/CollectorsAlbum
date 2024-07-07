package team.tnt.collectorsalbum.common.resource.function;

import java.util.function.Function;

public interface NumberProvider {

    <N extends Number> N getNumber(Function<Number, N> mapper);

    NumberProviderType<?> getType();

    default int intValue() {
        return getNumber(Number::intValue);
    }

    default float floatValue() {
        return getNumber(Number::floatValue);
    }

    default double doubleValue() {
        return getNumber(Number::doubleValue);
    }
}
