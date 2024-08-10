package team.tnt.collectorsalbum.network;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import team.tnt.collectorsalbum.CollectorsAlbum;
import team.tnt.collectorsalbum.common.AlbumCategory;
import team.tnt.collectorsalbum.common.AlbumCategoryType;
import team.tnt.collectorsalbum.common.card.AlbumCard;
import team.tnt.collectorsalbum.common.card.AlbumCardType;
import team.tnt.collectorsalbum.common.resource.AlbumBonusManager;
import team.tnt.collectorsalbum.common.resource.AlbumCardManager;
import team.tnt.collectorsalbum.common.resource.AlbumCategoryManager;
import team.tnt.collectorsalbum.common.resource.bonus.AlbumBonus;
import team.tnt.collectorsalbum.common.resource.bonus.AlbumBonusType;
import team.tnt.collectorsalbum.platform.network.PlatformNetworkManager;
import team.tnt.collectorsalbum.platform.resource.PlatformGsonCodecReloadListener;

import java.util.List;

public record S2C_SendDatapackResources(List<AlbumCard> cards, List<AlbumCategory> categories, List<AlbumBonus> bonuses) implements CustomPacketPayload {

    private static final ResourceLocation IDENTIFIER = PlatformNetworkManager.generatePacketIdentifier(CollectorsAlbum.MOD_ID, S2C_SendDatapackResources.class);
    public static final Type<S2C_SendDatapackResources> TYPE = new Type<>(IDENTIFIER);
    public static final StreamCodec<FriendlyByteBuf, S2C_SendDatapackResources> CODEC = StreamCodec.of(
            (buf, payload) -> payload.encode(buf),
            S2C_SendDatapackResources::decode
    );

    public S2C_SendDatapackResources() {
        this(
                AlbumCardManager.getInstance().getNetworkData(),
                AlbumCategoryManager.getInstance().getNetworkData(),
                AlbumBonusManager.getInstance().getNetworkData()
        );
    }

    private S2C_SendDatapackResources(ValueHolder<AlbumCard> cardHolder, ValueHolder<AlbumCategory> categoryHolder, ValueHolder<AlbumBonus> bonusHolder) {
        this(cardHolder.values, categoryHolder.values, bonusHolder.values);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    private void encode(FriendlyByteBuf buffer) {
        this.encodeWithCodec(buffer, AlbumCardType.INSTANCE_CODEC, this.cards());
        this.encodeWithCodec(buffer, AlbumCategoryType.INSTANCE_CODEC, this.categories());
        this.encodeWithCodec(buffer, AlbumBonusType.INSTANCE_CODEC, this.bonuses());
    }

    private <T> void encodeWithCodec(FriendlyByteBuf buf, Codec<T> codec, List<T> list) {
        ValueHolder<T> holder = new ValueHolder<>(list);
        Codec<ValueHolder<T>> valueCodec = ValueHolder.codec(codec);
        DataResult<Tag> result = valueCodec.encodeStart(NbtOps.INSTANCE, holder);
        Tag tag = result.getOrThrow();
        buf.writeNbt(tag);
    }

    private static S2C_SendDatapackResources decode(FriendlyByteBuf buffer) {
        return new S2C_SendDatapackResources(
                decodeWithCodec(buffer, AlbumCardType.INSTANCE_CODEC, AlbumCardManager.getInstance()),
                decodeWithCodec(buffer, AlbumCategoryType.INSTANCE_CODEC, AlbumCategoryManager.getInstance()),
                decodeWithCodec(buffer, AlbumBonusType.INSTANCE_CODEC, AlbumBonusManager.getInstance())
        );
    }

    private static <T> ValueHolder<T> decodeWithCodec(FriendlyByteBuf buffer, Codec<T> codec, PlatformGsonCodecReloadListener<T> listener) {
        Codec<ValueHolder<T>> valueCodec = ValueHolder.codec(codec);
        Tag tag = buffer.readNbt();
        DataResult<ValueHolder<T>> result = valueCodec.parse(NbtOps.INSTANCE, tag);
        ValueHolder<T> holder = result.getOrThrow();
        listener.onNetworkDataReceived(holder.values()); // has to be done immediately due to bad design for categories
        return holder;
    }

    public void onDataReceived(Player player) {
        // Just do nothing, data should be already resolved
    }

    private record ValueHolder<T>(List<T> values) {

        static <T> Codec<ValueHolder<T>> codec(Codec<T> element) {
                return RecordCodecBuilder.create(instance -> instance.group(
                        element.listOf().fieldOf("valueList").forGetter(t -> t.values)
                ).apply(instance, ValueHolder::new));
            }
        }
}
