package team.tnt.collectorsalbum.common;

import net.minecraft.world.item.ItemStack;

public class AlbumLocatorResult {

    private static final AlbumLocatorResult NOT_FOUND = new AlbumLocatorResult(AlbumLocateState.NOT_FOUND, ItemStack.EMPTY, null, 0);

    private final AlbumLocateState state;
    private final ItemStack albumItemStack;
    private final Album album;
    private final int slot;

    private AlbumLocatorResult(final AlbumLocateState state, final ItemStack albumItemStack, final Album album, final int slot) {
        this.state = state;
        this.albumItemStack = albumItemStack;
        this.album = album;
        this.slot = slot;
    }

    public static AlbumLocatorResult notFound() {
        return NOT_FOUND;
    }

    public static AlbumLocatorResult found(ItemStack itemStack, Album album, int slot) {
        return new AlbumLocatorResult(AlbumLocateState.FOUND, itemStack, album, slot);
    }

    public boolean exists() {
        return state.exists();
    }

    public ItemStack getAlbumItemStack() {
        return albumItemStack;
    }

    public Album getAlbum() {
        return album;
    }

    public int getSlot() {
        return slot;
    }
}
