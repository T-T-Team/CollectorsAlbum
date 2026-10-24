package team.tnt.collectorsalbum.common.resource.bonus;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import team.tnt.collectorsalbum.common.resource.function.NumberProvider;
import team.tnt.collectorsalbum.common.resource.util.ActionContext;

public record AlbumMobEffectBonus(boolean rewriteExisting, boolean forceRemove, Holder<MobEffect> effect,
                                  int duration, int amplifier, boolean ambient, boolean visible,
                                  boolean showIcon) implements AlbumBonus {

    public static final MapCodec<AlbumMobEffectBonus> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("rewriteExisting", false).forGetter(t -> t.rewriteExisting),
            Codec.BOOL.optionalFieldOf("forceRemove", false).forGetter(t -> t.forceRemove),
            MobEffect.CODEC.fieldOf("effect").forGetter(t -> t.effect),
            NumberProvider.POSITIVE_INT_CODEC.optionalFieldOf("duration", 120).forGetter(t -> t.duration),
            NumberProvider.rangedCodec(Codec.INT, 0, 255).optionalFieldOf("amplifier", 0).forGetter(t -> t.amplifier),
            Codec.BOOL.optionalFieldOf("ambient", true).forGetter(t -> t.ambient),
            Codec.BOOL.optionalFieldOf("visible", false).forGetter(t -> t.visible),
            Codec.BOOL.optionalFieldOf("showIcon", true).forGetter(t -> t.showIcon)
    ).apply(instance, AlbumMobEffectBonus::new));

    @Override
    public void appendDetails(SectionOutput writer, ActionContext ctx) {
        MobEffect mobEffect = this.effect.value();
        Component amplifier = this.amplifier >= 1 && this.amplifier <= 9 ? Component.literal(" ").append(Component.translatable("enchantment.level." + (this.amplifier + 1))) : CommonComponents.EMPTY;
        Component title = Component.translatable("collectorsalbum.label.bonus.mob_effect.effect", mobEffect.getDisplayName(), amplifier);
        writer.withTitle(title);
    }

    @Override
    public void apply(ActionContext context) {
        Player player = context.getOrThrow(ActionContext.PLAYER, Player.class);
        MobEffectInstance instance = this.createEffectInstance();
        if (!player.addEffect(instance) && rewriteExisting) {
            player.removeEffect(effect);
            player.addEffect(instance);
        }
    }

    @Override
    public void removed(ActionContext context) {
        if (forceRemove) {
            Player player = context.getOrThrow(ActionContext.PLAYER, Player.class);
            player.removeEffect(this.effect);
        }
    }

    @Override
    public MapCodec<? extends AlbumBonus> codec() {
        return CODEC;
    }

    private MobEffectInstance createEffectInstance() {
        return new MobEffectInstance(effect, duration, amplifier, ambient, visible, showIcon);
    }
}
