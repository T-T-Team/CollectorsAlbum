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
import team.tnt.collectorsalbum.common.init.ItemDataComponentRegistry;
import team.tnt.collectorsalbum.common.init.RegistryTags;
import team.tnt.collectorsalbum.common.menu.AlbumCategoryMenu;
import team.tnt.collectorsalbum.common.resource.AlbumCategoryManager;
import team.tnt.collectorsalbum.platform.Platform;
import team.tnt.collectorsalbum.platform.network.PlatformNetworkManager;

public record C2S_RequestAlbumCategoryInventory(Identifier category) implements CustomPacketPayload {

    private static final Identifier IDENTIFIER = PlatformNetworkManager.generatePacketIdentifier(CollectorsAlbum.MOD_ID, C2S_RequestAlbumCategoryInventory.class);
    public static final Type<C2S_RequestAlbumCategoryInventory> TYPE = new Type<>(IDENTIFIER);
    public static final StreamCodec<FriendlyByteBuf, C2S_RequestAlbumCategoryInventory> CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC, C2S_RequestAlbumCategoryInventory::category,
            C2S_RequestAlbumCategoryInventory::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void onPacket(Player player) {
        ItemStack itemStack = player.getMainHandItem();
        if (!itemStack.is(RegistryTags.Items.ALBUM))
            return;
        Album album = Album.fromItem(itemStack);
        if (album == null)
            return;
        AlbumCategoryManager manager = AlbumCategoryManager.getInstance();
        AlbumCategory albumCategory = manager.findById(category).orElse(null);
        if (albumCategory == null)
            return;
        Platform.INSTANCE.openMenu((ServerPlayer) player, Identifier.STREAM_CODEC, new Platform.PlatformMenuProvider<>() {
            @Override
            public Identifier getMenuData(ServerPlayer player) {
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
