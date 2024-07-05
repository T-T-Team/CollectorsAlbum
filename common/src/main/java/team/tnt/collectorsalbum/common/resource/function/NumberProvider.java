package team.tnt.collectorsalbum.common.resource.function;

import java.util.function.IntSupplier;

public interface NumberProvider extends IntSupplier {

    NumberProviderType<?> getType();
}
