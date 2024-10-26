package team.tnt.collectorsalbum.common.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import team.tnt.collectorsalbum.CollectorsAlbum;

public final class RegistryTags {

    public static final class Items {

        public static final TagKey<Item> ALBUM = TagKey.create(Registries.ITEM, new ResourceLocation(CollectorsAlbum.MOD_ID, "album"));

        private Items() {}
    }

    private RegistryTags() {}
}
