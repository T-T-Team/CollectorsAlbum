package team.tnt.collectorsalbum.common.tracking;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import team.tnt.collectorsalbum.common.Album;
import team.tnt.collectorsalbum.common.init.ItemDataComponentRegistry;

import java.util.UUID;

public record CachedAlbum(InventoryKey key, Album value) {

    public boolean is(ItemStack stack) {
        UUID key = this.value.getAlbumId();
        Album album = Album.fromItem(stack);
        if (album == null)
            return false;
        return album.getAlbumId().equals(key);
    }

    @Override
    public @NotNull String toString() {
        return this.value.getAlbumId().toString();
    }

    public boolean matches(UUID identifier) {
        Album album = this.value();
        return album.getAlbumId().equals(identifier);
    }
}
