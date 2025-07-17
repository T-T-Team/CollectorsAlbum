package team.tnt.collectorsalbum.common.resource.bonus;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.player.Player;
import team.tnt.collectorsalbum.common.AlbumBonusDescriptionOutput;
import team.tnt.collectorsalbum.common.init.AlbumBonusRegistry;
import team.tnt.collectorsalbum.common.resource.function.ConstantNumberProvider;
import team.tnt.collectorsalbum.common.resource.function.NumberProvider;
import team.tnt.collectorsalbum.common.resource.function.NumberProviderType;
import team.tnt.collectorsalbum.common.resource.util.ActionContext;

import java.util.Locale;
import java.util.function.Function;

public class PlayerHealthBonus implements AlbumBonus {

    public static final MapCodec<PlayerHealthBonus> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.either(Codec.FLOAT, NumberProviderType.INSTANCE_CODEC).fieldOf("amount").forGetter(t -> Either.right(t.amount)),
            Codec.either(ExtraCodecs.POSITIVE_INT, NumberProviderType.INSTANCE_CODEC).fieldOf("interval").forGetter(t -> Either.right(t.interval))
    ).apply(instance, PlayerHealthBonus::new));

    private final NumberProvider amount;
    private final NumberProvider interval;

    public PlayerHealthBonus(Either<Float, NumberProvider> amount, Either<Integer, NumberProvider> interval) {
        this.amount = amount.map(ConstantNumberProvider::new, Function.identity());
        this.interval = interval.map(ConstantNumberProvider::new, Function.identity());
    }

    @Override
    public void apply(ActionContext context) {
        Player player = context.getOrThrow(ActionContext.PLAYER, Player.class);
        int interval = Math.max(5, this.interval.intValue());
        if (player.level().getGameTime() % interval == 0L) {
            float healthAmount = this.amount.floatValue();
            if (healthAmount != 0.0F) {
                if (healthAmount > 0.0F) {
                    player.heal(healthAmount);
                } else {
                    DamageSources sources = player.damageSources();
                    player.hurt(sources.magic(), healthAmount);
                }
            }
        }
    }

    @Override
    public void removed(ActionContext context) {
    }

    @Override
    public void addDescription(AlbumBonusDescriptionOutput description) {
        float amount = this.amount.floatValue();
        if (amount == 0.0F)
            return;
        String seconds = String.format(Locale.ROOT, "%.2f", this.interval.intValue() / 20.0);
        String formattedAmount = String.format(Locale.ROOT, "%.2f", amount);
        String i18nKey = amount > 0.0F ? "collectorsalbum.label.bonus.player_health.heal" : "collectorsalbum.label.bonus.player_health.hurt";
        description.text(Component.translatable(i18nKey, formattedAmount, seconds));
    }

    @Override
    public AlbumBonusType<?> getType() {
        return AlbumBonusRegistry.PLAYER_HEALTH.get();
    }
}
