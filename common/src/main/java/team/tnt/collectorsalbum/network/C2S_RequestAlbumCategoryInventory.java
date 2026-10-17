package team.tnt.collectorsalbum.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import team.tnt.collectorsalbum.CollectorsAlbum;
import team.tnt.collectorsalbum.common.Album;
import team.tnt.collectorsalbum.common.AlbumCategory;
import team.tnt.collectorsalbum.common.menu.AlbumCategoryMenu;
import team.tnt.collectorsalbum.common.resource.AlbumCategoryManager;
import team.tnt.collectorsalbum.platform.Platform;
import team.tnt.collectorsalbum.platform.network.PlatformNetworkManager;
import team.tnt.collectorsalbum.util.PageInfo;

public record C2S_RequestAlbumCategoryInventory(PageInfo<Identifier> pageInfo) implements CustomPacketPayload {

    private static final Identifier IDENTIFIER = PlatformNetworkManager.generatePacketIdentifier(CollectorsAlbum.MOD_ID, C2S_RequestAlbumCategoryInventory.class);
    public static final Type<C2S_RequestAlbumCategoryInventory> TYPE = new Type<>(IDENTIFIER);
    public static final StreamCodec<FriendlyByteBuf, C2S_RequestAlbumCategoryInventory> CODEC = StreamCodec.composite(
            CollectorsAlbum.ALBUM_PAGE_STREAM_CODEC, C2S_RequestAlbumCategoryInventory::pageInfo,
            C2S_RequestAlbumCategoryInventory::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void onPacket(Player player) {
        ItemStack itemStack = player.getMainHandItem();
        Album album = Album.fromItem(itemStack);
        if (album == null)
            return;
        AlbumCategoryManager manager = AlbumCategoryManager.getInstance();
        AlbumCategory albumCategory = manager.find(this.pageInfo).orElse(null);
        if (albumCategory == null)
            return;
        Platform.INSTANCE.openMenu((ServerPlayer) player, CollectorsAlbum.ALBUM_PAGE_STREAM_CODEC, new PagedAlbumCategoryMenuProvider(this.pageInfo, albumCategory));
    }

    private record PagedAlbumCategoryMenuProvider(PageInfo<Identifier> pageInfo, AlbumCategory category) implements Platform.PlatformMenuProvider<PageInfo<Identifier>> {

        @Override
        public PageInfo<Identifier> getMenuData(ServerPlayer player) {
            return this.pageInfo;
        }

        @Override
        public Component getTitle() {
            return this.category.getDisplayText();
        }

        @Override
        public AbstractContainerMenu createMenu(int menuId, Inventory inventory, Player player) {
            return new AlbumCategoryMenu(menuId, inventory, this.pageInfo);
        }
    }
}
