package team.tnt.collectorsalbum.network;

import com.mojang.serialization.DataResult;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import team.tnt.collectorsalbum.CollectorsAlbum;
import team.tnt.collectorsalbum.client.CollectorsAlbumClient;
import team.tnt.collectorsalbum.platform.network.NetworkMessage;

import java.util.List;

public record S2C_OpenCardPackScreen(List<ItemStack> items) implements NetworkMessage {

    public static final ResourceLocation IDENTIFIER = new ResourceLocation(CollectorsAlbum.MOD_ID, "msg_open_card_pack_screen");

    @Override
    public ResourceLocation getPacketId() {
        return IDENTIFIER;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        DataResult<Tag> result = ItemStack.CODEC.listOf().encodeStart(NbtOps.INSTANCE, items);
        ListTag listTag = (ListTag) result.result().orElseThrow();
        CompoundTag wrapper = new CompoundTag();
        wrapper.put("data", listTag);
        buf.writeNbt(wrapper);
    }

    public static S2C_OpenCardPackScreen read(FriendlyByteBuf buf) {
        CompoundTag wrapper = buf.readNbt();
        ListTag listTag = wrapper.getList("data", Tag.TAG_COMPOUND);
        DataResult<List<ItemStack>> dataResult = ItemStack.CODEC.listOf().parse(NbtOps.INSTANCE, listTag);
        List<ItemStack> content = dataResult.result().orElseThrow();
        return new S2C_OpenCardPackScreen(content);
    }

    @Override
    public void handle(Player player) {
        CollectorsAlbumClient.handlePackOpening(items);
    }
}
