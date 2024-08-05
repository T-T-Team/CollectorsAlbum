package team.tnt.collectorsalbum.common.resource.bonus;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.entity.player.Player;
import team.tnt.collectorsalbum.common.AlbumBonusDescriptionOutput;
import team.tnt.collectorsalbum.common.init.AlbumBonusRegistry;
import team.tnt.collectorsalbum.common.resource.function.ConstantNumberProvider;
import team.tnt.collectorsalbum.common.resource.function.NumberProvider;
import team.tnt.collectorsalbum.common.resource.function.NumberProviderType;
import team.tnt.collectorsalbum.common.resource.util.ActionContext;

import java.util.function.Function;

public class AlbumMobEffectBonus implements AlbumBonus {

    public static final MapCodec<AlbumMobEffectBonus> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("rewriteExisting", false).forGetter(t -> t.rewriteExisting),
            Codec.BOOL.optionalFieldOf("forceRemove", false).forGetter(t -> t.forceRemove),
            MobEffect.CODEC.fieldOf("effect").forGetter(t -> t.effect),
            Codec.either(ExtraCodecs.POSITIVE_INT, NumberProviderType.INSTANCE_CODEC).optionalFieldOf("duration", Either.left(120)).forGetter(t -> Either.right(t.duration)),
            Codec.either(ExtraCodecs.intRange(0, 255), NumberProviderType.INSTANCE_CODEC).optionalFieldOf("amplifier", Either.right(ConstantNumberProvider.ZERO)).forGetter(t -> Either.right(t.amplifier)),
            Codec.BOOL.optionalFieldOf("ambient", true).forGetter(t -> t.ambient),
            Codec.BOOL.optionalFieldOf("visible", false).forGetter(t -> t.visible),
            Codec.BOOL.optionalFieldOf("showIcon", true).forGetter(t -> t.showIcon)
    ).apply(instance, AlbumMobEffectBonus::new));

    private final boolean rewriteExisting;
    private final boolean forceRemove;
    private final Holder<MobEffect> effect;
    private final NumberProvider duration;
    private final NumberProvider amplifier;
    private final boolean ambient;
    private final boolean visible;
    private final boolean showIcon;

    public AlbumMobEffectBonus(boolean rewriteExisting, boolean forceRemove, Holder<MobEffect> effect, Either<Integer, NumberProvider> duration, Either<Integer, NumberProvider> amplifier, boolean ambient, boolean visible, boolean showIcon) {
        this.rewriteExisting = rewriteExisting;
        this.forceRemove = forceRemove;
        this.effect = effect;
        this.duration = duration.map(ConstantNumberProvider::new, Function.identity());
        this.amplifier = amplifier.map(ConstantNumberProvider::new, Function.identity());
        this.ambient = ambient;
        this.visible = visible;
        this.showIcon = showIcon;
    }

    @Override
    public void addDescription(AlbumBonusDescriptionOutput description) {
        MobEffect mobEffect = this.effect.value();
        int amplifierValue = this.amplifier.intValue();
        ActionContext context = description.getContext();
        Player player = context.getOrThrow(ActionContext.PLAYER, Player.class);
        Component amplifier = amplifierValue >= 1 && amplifierValue <= 9 ? Component.literal(" ").append(Component.translatable("enchantment.level." + (amplifierValue + 1))) : CommonComponents.EMPTY;
        Component title = Component.translatable("collectorsalbum.label.bonus.mob_effect.effect", mobEffect.getDisplayName(), amplifier).withStyle(ChatFormatting.BLUE);
        Component tooltip = Component.translatable("collectorsalbum.label.bonus.mob_effect.duration", MobEffectUtil.formatDuration(this.createEffectInstance(), 1.0F, player.level().tickRateManager().tickrate()));
        description.text(title, tooltip);
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
    public AlbumBonusType<?> getType() {
        return AlbumBonusRegistry.MOB_EFFECT.get();
    }

    private MobEffectInstance createEffectInstance() {
        return new MobEffectInstance(effect, duration.intValue(), amplifier.intValue(), ambient, visible, showIcon);
    }
}
