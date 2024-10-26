package team.tnt.collectorsalbum.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import team.tnt.collectorsalbum.CollectorsAlbum;
import team.tnt.collectorsalbum.common.Album;
import team.tnt.collectorsalbum.common.AlbumCategory;
import team.tnt.collectorsalbum.common.init.RegistryTags;
import team.tnt.collectorsalbum.common.menu.AlbumCategoryMenu;
import team.tnt.collectorsalbum.common.resource.AlbumCategoryManager;
import team.tnt.collectorsalbum.platform.Platform;
import team.tnt.collectorsalbum.platform.network.NetworkCodec;
import team.tnt.collectorsalbum.platform.network.NetworkMessage;

public record C2S_RequestAlbumCategoryInventory(ResourceLocation category) implements NetworkMessage {

    public static final ResourceLocation IDENTIFIER = new ResourceLocation(CollectorsAlbum.MOD_ID, "msg_req_album_inv");

    @Override
    public ResourceLocation getPacketId() {
        return IDENTIFIER;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.writeResourceLocation(this.category);
    }

    public static C2S_RequestAlbumCategoryInventory read(FriendlyByteBuf buf) {
        ResourceLocation category = buf.readResourceLocation();
        return new C2S_RequestAlbumCategoryInventory(category);
    }

    @Override
    public void handle(Player player) {
        ItemStack itemStack = player.getMainHandItem();
        if (!itemStack.is(RegistryTags.Items.ALBUM))
            return;
        Album album = Album.get(itemStack);
        if (album == null)
            return;
        AlbumCategoryManager manager = AlbumCategoryManager.getInstance();
        AlbumCategory albumCategory = manager.findById(category).orElse(null);
        if (albumCategory == null)
            return;
        Platform.INSTANCE.openMenu((ServerPlayer) player, NetworkCodec.RESOURCE_LOCATION, new Platform.PlatformMenuProvider<>() {
            @Override
            public ResourceLocation getMenuData(ServerPlayer player) {
                return category;
            }

            @Override
            public Component getTitle() {
                return albumCategory.getDisplayText();
            }

            @Override
            public AbstractContainerMenu createMenu(int menuId, Inventory inventory, Player player) {
                return new AlbumCategoryMenu(menuId, inventory, category);
            }
        });
    }
}
