package team.tnt.collectorsalbum.common.resource;

import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import team.tnt.collectorsalbum.CollectorsAlbum;
import team.tnt.collectorsalbum.common.resource.drops.ItemDropProvider;
import team.tnt.collectorsalbum.common.resource.drops.ItemDropProviderType;
import team.tnt.collectorsalbum.common.resource.drops.NoItemDropProvider;
import team.tnt.collectorsalbum.platform.resource.PlatformGsonCodecReloadListener;

import java.util.HashMap;
import java.util.Map;

public class CardPackDropManager extends PlatformGsonCodecReloadListener<ItemDropProvider> {

    private static final CardPackDropManager INSTANCE = new CardPackDropManager();
    private static final ResourceLocation IDENTIFIER = ResourceLocation.fromNamespaceAndPath(CollectorsAlbum.MOD_ID, "card_pack_drops");
    private final Map<ResourceLocation, ItemDropProvider> providerMap = new HashMap<>();

    private CardPackDropManager() {
        super("album/packs", ItemDropProviderType.INSTANCE_CODEC);
    }

    public static CardPackDropManager getInstance() {
        return INSTANCE;
    }

    public ItemDropProvider getProvider(ResourceLocation id) {
        return this.providerMap.getOrDefault(id, NoItemDropProvider.INSTANCE);
    }

    @Override
    public ResourceLocation identifier() {
        return IDENTIFIER;
    }

    @Override
    protected void preApply(Map<ResourceLocation, JsonElement> resources, ResourceManager manager, ProfilerFiller profiler) {
        this.providerMap.clear();
    }

    @Override
    protected void resolve(ResourceLocation path, ItemDropProvider element) {
        this.providerMap.put(path, element);
    }
}
