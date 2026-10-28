package team.tnt.collectorsalbum.common.init;

import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import team.tnt.collectorsalbum.common.Album;
import team.tnt.collectorsalbum.common.item.PackContents;
import team.tnt.collectorsalbum.platform.registration.Reference;
import team.tnt.collectorsalbum.platform.registration.RegistrationManager;

public final class ItemDataComponentRegistry {

    public static Reference<DataComponentType<PackContents>> PACK_CONTENTS;
    public static Reference<DataComponentType<Identifier>> PACK_DROPS_TABLE;
    public static Reference<DataComponentType<Album>> ALBUM;

    public static void init(RegistrationManager<DataComponentType<?>> registry) {
        PACK_CONTENTS = register(registry, "pack_contents", PackContents.CODEC, null);
        PACK_DROPS_TABLE = register(registry, "pack_drops_table", Identifier.CODEC, Identifier.STREAM_CODEC);
        ALBUM = register(registry, "album", Album.CODEC, null);
    }

    private static <T> Reference<DataComponentType<T>> register(RegistrationManager<DataComponentType<?>> registry, String name, Codec<T> codec, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec) {
        return registry.register(name, _ -> DataComponentType.<T>builder()
                .persistent(codec)
                .networkSynchronized(streamCodec)
                .build());
    }
}
