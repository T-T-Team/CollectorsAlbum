package team.tnt.collectorsalbum.platform.resource;

import com.mojang.serialization.Codec;

public interface NetworkSynchronizable<T> {

    Codec<T> codec();
}
