package team.tnt.collectorsalbum.common.resource.bonus;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.toma.configuration.Configuration;
import dev.toma.configuration.config.ConfigHolder;
import team.tnt.collectorsalbum.common.init.AlbumBonusRegistry;
import team.tnt.collectorsalbum.common.resource.util.ActionContext;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ConfigToggleBonusFilter implements IntermediateAlbumBonus {

    public static final MapCodec<ConfigToggleBonusFilter> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.STRING.fieldOf("config").forGetter(t -> t.config),
            Codec.STRING.fieldOf("path").forGetter(t -> t.path),
            AlbumBonusType.INSTANCE_CODEC.fieldOf("enabled").forGetter(t -> t.enabled),
            AlbumBonusType.INSTANCE_CODEC.optionalFieldOf("disabled", NoBonus.INSTANCE).forGetter(t -> t.disabled)
    ).apply(instance, ConfigToggleBonusFilter::new));

    private final String config;
    private final String path;
    private final AlbumBonus enabled;
    private final AlbumBonus disabled;

    public ConfigToggleBonusFilter(String config, String path, AlbumBonus enabled, AlbumBonus disabled) {
        this.config = config;
        this.path = path;
        this.enabled = enabled;
        this.disabled = disabled;
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
        Optional<ConfigHolder<Object>> configHolder = Configuration.getConfig(this.config);
        return configHolder.flatMap(holder -> holder.getValue(this.path, Boolean.class)).orElse(false);
    }
}
