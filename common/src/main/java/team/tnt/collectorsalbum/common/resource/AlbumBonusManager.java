package team.tnt.collectorsalbum.common.resource;

import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import team.tnt.collectorsalbum.CollectorsAlbum;
import team.tnt.collectorsalbum.common.resource.bonus.AlbumBonus;
import team.tnt.collectorsalbum.common.resource.bonus.AlbumBonusType;
import team.tnt.collectorsalbum.common.resource.bonus.NoBonus;
import team.tnt.collectorsalbum.common.resource.util.ActionContext;
import team.tnt.collectorsalbum.network.S2C_SendDatapackResources;
import team.tnt.collectorsalbum.platform.network.PlatformNetworkManager;
import team.tnt.collectorsalbum.platform.resource.PlatformGsonCodecReloadListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class AlbumBonusManager extends PlatformGsonCodecReloadListener<AlbumBonus> {

    private static final ResourceLocation IDENTIFIER = ResourceLocation.fromNamespaceAndPath(CollectorsAlbum.MOD_ID, "album_bonus_manager");
    private static final AlbumBonusManager INSTANCE = new AlbumBonusManager();
    private final Map<ResourceLocation, AlbumBonus> registeredBonuses = new HashMap<>();
    private final List<AlbumBonus> bonusList = new ArrayList<>();

    private AlbumBonusManager() {
        super("album/bonus", AlbumBonusType.INSTANCE_CODEC);
        S2C_SendDatapackResources.registerType(this);
    }

    public static AlbumBonusManager getInstance() {
        return INSTANCE;
    }

    public void applyBonuses(ActionContext context) {
        this.bonusList.forEach(bonus -> bonus.apply(context));
    }

    public void removeBonuses(ActionContext context) {
        this.bonusList.forEach(bonus -> bonus.removed(context));
    }

    public Pair<AlbumBonus, AlbumBonus> getBonusesForPage(int page) {
        int leftIdx = page * 2;
        int rightIdx = leftIdx + 1;
        AlbumBonus left = this.getBonusAtIndex(leftIdx);
        AlbumBonus right = this.getBonusAtIndex(rightIdx);
        return Pair.of(left, right);
    }

    public boolean hasNextPage(int currentPage) {
        int lastDisplayed = currentPage * 2 + 1;
        return lastDisplayed >= 0 && lastDisplayed < this.bonusList.size();
    }

    @Override
    public ResourceLocation identifier() {
        return IDENTIFIER;
    }

    @Override
    public List<AlbumBonus> getNetworkData() {
        return new ArrayList<>(this.registeredBonuses.values());
    }

    @Override
    public void onNetworkDataReceived(List<AlbumBonus> collection) {
        this.bonusList.clear();
        this.bonusList.addAll(collection);
    }

    @Override
    protected void preApply(Map<ResourceLocation, JsonElement> resources, ResourceManager manager, ProfilerFiller profiler) {
        this.registeredBonuses.clear();
        this.bonusList.clear();
    }

    @Override
    protected void resolve(ResourceLocation path, AlbumBonus element) {
        this.registeredBonuses.put(path, element);
        this.bonusList.add(element);
    }

    @Override
    protected void onReloadComplete(ResourceManager manager, ProfilerFiller profiler) {
        super.onReloadComplete(manager, profiler);
        PlatformNetworkManager.NETWORK.sendAllClientMessage(new S2C_SendDatapackResources<>(this));
    }

    private AlbumBonus getBonusAtIndex(int index) {
        if (index < 0 || index >= this.bonusList.size()) {
            return NoBonus.INSTANCE;
        }
        return this.bonusList.get(index);
    }
}
