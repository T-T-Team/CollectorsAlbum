package team.tnt.collectorsalbum.config;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import dev.toma.configuration.Configuration;
import dev.toma.configuration.config.Config;
import dev.toma.configuration.config.ConfigHolder;
import dev.toma.configuration.config.Configurable;
import team.tnt.collectorsalbum.CollectorsAlbum;

@Config(id = CollectorsAlbum.MOD_ID)
public final class CollectorsAlbumConfig {

    // TODO remove once the codec is provided by configuration library
    @Deprecated
    public static final Codec<ConfigHolder<Object>> CONFIG_CODEC = Codec.STRING.comapFlatMap(
            cfgId -> Configuration.getConfig(cfgId).map(DataResult::success).orElse(DataResult.error(() -> "Unknown config: " + cfgId)),
            ConfigHolder::getConfigId
    );

    @Configurable
    @Configurable.Comment({"Configuration related to card package drop distributions",
            "Keep in mind to actually change contents of the pack you need to make custom datapack",
            "See wiki for that"})
    public DefaultPackDropsConfig packDrops = new DefaultPackDropsConfig();

    @Configurable
    @Configurable.Comment({"Configuration related to mob drops",
            "Keep in mind to actually change contents of the pack you need to make custom datapack",
            "See wiki for that"
    })
    public MobDropConfig mobDrops = new MobDropConfig();

    @Configurable
    @Configurable.Comment({"Configuration related to album bonuses",
            "Keep in mind to actually change the bonuses to something you need to make custom datapack",
            "See wiki for that"
    })
    public AlbumBonusConfig bonusConfig = new AlbumBonusConfig();
}
