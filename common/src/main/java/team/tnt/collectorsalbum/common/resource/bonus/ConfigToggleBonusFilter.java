package team.tnt.collectorsalbum.common.resource.bonus;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.toma.configuration.Configuration;
import dev.toma.configuration.config.ConfigHolder;
import dev.toma.configuration.config.value.IConfigValue;
import dev.toma.configuration.config.value.IConfigValueReadable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import team.tnt.collectorsalbum.common.init.AlbumBonusRegistry;
import team.tnt.collectorsalbum.common.resource.util.ActionContext;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public record ConfigToggleBonusFilter(ConfigHolder<?> config, String path, AlbumBonus enabled,
                                      AlbumBonus disabled) implements IntermediateAlbumBonus {

    private static final Component UNKNOWN_CONFIG_OPTION = Component.translatable("collectorsalbum.label.bonus.config_toggle.unknown_field").withStyle(ChatFormatting.RED);
    public static final MapCodec<ConfigToggleBonusFilter> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Configuration.BY_ID_CODEC.fieldOf("config").forGetter(t -> t.config),
            Codec.STRING.fieldOf("path").forGetter(t -> t.path),
            AlbumBonusType.INSTANCE_CODEC.fieldOf("enabled").forGetter(t -> t.enabled),
            AlbumBonusType.INSTANCE_CODEC.optionalFieldOf("disabled", NoBonus.INSTANCE).forGetter(t -> t.disabled)
    ).apply(instance, ConfigToggleBonusFilter::new));

    @Override
    public SectionOutput appendItemDetailsWithModifiers(SectionOutput output, ActionContext context, AlbumBonus child) {
        boolean enabled = this.canApply(context);
        Optional<IConfigValue<Boolean>> configValue = this.config.getConfigValue(this.path, Boolean.class);
        Component configLabel = configValue.map(IConfigValueReadable::getTitle).orElse(UNKNOWN_CONFIG_OPTION);
        if (child == this.enabled) {
            output.condition(enabled, Component.translatable("collectorsalbum.label.bonus.config_toggle.enabled", configLabel));
        } else if (child == this.disabled) {
            output.condition(!enabled, Component.translatable("collectorsalbum.label.bonus.config_toggle.disabled", configLabel));
        }
        return output;
    }

    @Override
    public void apply(ActionContext context) {
        if (this.canApply(context)) {
            this.enabled.apply(context);
        } else {
            this.disabled.apply(context);
        }
    }

    @Override
    public void removed(ActionContext context) {
        this.enabled.removed(context);
        this.disabled.removed(context);
    }

    @Override
    public List<AlbumBonus> children() {
        return Arrays.asList(enabled, disabled);
    }

    @Override
    public AlbumBonusType<?> getType() {
        return AlbumBonusRegistry.CONFIG_TOGGLE.get();
    }

    @Override
    public boolean canApply(ActionContext context) {
        return this.config.getValue(this.path, Boolean.class)
                .orElse(false);
    }
}
