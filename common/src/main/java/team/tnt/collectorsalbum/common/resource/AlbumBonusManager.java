package team.tnt.collectorsalbum.common.resource;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import team.tnt.collectorsalbum.CollectorsAlbum;
import team.tnt.collectorsalbum.common.resource.bonus.AlbumBonusType;
import team.tnt.collectorsalbum.common.resource.bonus.BonusHolder;
import team.tnt.collectorsalbum.common.resource.util.ActionContext;
import team.tnt.collectorsalbum.platform.resource.PlatformGsonCodecReloadListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class AlbumBonusManager extends PlatformGsonCodecReloadListener<BonusHolder> implements SynchronizedResource<BonusHolder> {

    public static final Codec<BonusHolder> CODEC = Codec.withAlternative(
            BonusHolder.CODEC,
            AlbumBonusType.INSTANCE_CODEC,
            BonusHolder::unnamed
    );

    public static final Identifier IDENTIFIER = Identifier.fromNamespaceAndPath(CollectorsAlbum.MOD_ID, "album_bonus_manager");
    private static final AlbumBonusManager INSTANCE = new AlbumBonusManager();
    private final Map<Identifier, BonusHolder> registeredBonuses = new HashMap<>();
    private final List<BonusHolder> bonusList = new ArrayList<>();

    private AlbumBonusManager() {
        super("album/bonus", CODEC);
    }

    public static AlbumBonusManager getInstance() {
        return INSTANCE;
    }

    public void applyBonuses(ActionContext context) {
        this.bonusList.forEach(bonus -> bonus.value().apply(context));
    }

    public void removeBonuses(ActionContext context) {
        this.bonusList.forEach(bonus -> bonus.value().removed(context));
    }

    public boolean hasBonuses() {
        return !this.bonusList.isEmpty();
    }

    public List<BonusHolder> listAllBonuses() {
        return this.bonusList;
    }

    @Override
    public Identifier identifier() {
        return IDENTIFIER;
    }

    @Override
    public List<BonusHolder> getDataForSync() {
        return new ArrayList<>(this.registeredBonuses.values());
    }

    @Override
    public synchronized void receiveNetworkData(List<BonusHolder> data) {
        this.bonusList.clear();
        data.stream().filter(BonusHolder::isEnabled).forEach(bonusList::add);
    }

    @Override
    protected void preApply(Map<Identifier, JsonElement> resources, ResourceManager manager, ProfilerFiller profiler) {
        this.registeredBonuses.clear();
        this.bonusList.clear();
    }

    @Override
    protected void resolve(Identifier path, BonusHolder element) {
        this.registeredBonuses.put(path, element);
        this.bonusList.add(element);
    }
}
