package team.tnt.collectoralbum.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkHooks;
import team.tnt.collectoralbum.common.ICardCategory;
import team.tnt.collectoralbum.common.container.AlbumContainer;
import team.tnt.collectoralbum.common.init.CardCategoryRegistry;
import team.tnt.collectoralbum.common.init.ItemRegistry;
import team.tnt.collectoralbum.common.menu.AlbumMenu;

public class RequestAlbumPagePacket extends AbstractNetworkPacket<RequestAlbumPagePacket> {

    private final ICardCategory category;

    public RequestAlbumPagePacket() {
        this(null);
    }

    public RequestAlbumPagePacket(ICardCategory category) {
        this.category = category;
    }

    @Override
    public void encode(FriendlyByteBuf buffer) {
        buffer.writeBoolean(category != null);
        if (category != null) {
            buffer.writeResourceLocation(category.getId());
        }
    }

    @Override
    public RequestAlbumPagePacket decode(FriendlyByteBuf buffer) {
        boolean flag = buffer.readBoolean();
        ICardCategory category = null;
        if (flag) {
            category = CardCategoryRegistry.getByKey(buffer.readResourceLocation());
        }
        return new RequestAlbumPagePacket(category);
    }

    @Override
    protected void handlePacket(NetworkEvent.Context context) {
        ItemStack stack = context.getSender().getMainHandItem();
        if (stack.getItem() != ItemRegistry.ALBUM.get()) {
            return;
        }
        AlbumContainer container = new AlbumContainer(stack);
        ICardCategory category = this.category;
        NetworkHooks.openScreen(
                context.getSender(),
                new SimpleMenuProvider((id, inv, player) -> new AlbumMenu(container, inv, id, category), CommonComponents.EMPTY),
                buffer -> {
                    buffer.writeItem(stack);
                    buffer.writeBoolean(category != null);
                    if (category != null) {
                        buffer.writeResourceLocation(category.getId());
                    }
                }
        );
    }
}
