package team.tnt.collectorsalbum.common.init;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import team.tnt.collectorsalbum.CollectorsAlbum;
import team.tnt.collectorsalbum.common.block.CardTradingStationBlock;
import team.tnt.collectorsalbum.platform.registration.PlatformRegistry;

public final class BlockRegistry {

    public static final PlatformRegistry<Block> REGISTRY = PlatformRegistry.create(BuiltInRegistries.BLOCK, CollectorsAlbum.MOD_ID);

    public static final PlatformRegistry.Reference<CardTradingStationBlock> TRADING_STATION = REGISTRY.register("trading_station", () -> new CardTradingStationBlock(BlockBehaviour.Properties.of().strength(0.3F).sound(SoundType.WOOD)));
}
