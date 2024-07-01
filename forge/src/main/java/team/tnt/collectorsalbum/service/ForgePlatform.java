package team.tnt.collectorsalbum.service;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import team.tnt.collectorsalbum.platform.Platform;
import team.tnt.collectorsalbum.platform.Side;

public class ForgePlatform implements Platform {

    @Override
    public Side getSide() {
        return FMLEnvironment.dist == Dist.CLIENT ? Side.CLIENT : Side.SERVER;
    }
}
