package team.tnt.collectorsalbum.common.resource;

import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;
import team.tnt.collectorsalbum.CollectorsAlbum;
import team.tnt.collectorsalbum.common.resource.drops.ItemDropProvider;
import team.tnt.collectorsalbum.common.resource.drops.ItemDropProviderType;
import team.tnt.collectorsalbum.common.resource.drops.ItemDropResourceManager;
import team.tnt.collectorsalbum.common.resource.drops.NoItemDropProvider;
import team.tnt.collectorsalbum.platform.resource.PlatformGsonCodecReloadListener;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class MobAdditionalDropManager extends PlatformGsonCodecReloadListener<ItemDropProvider> implements Iterable<ItemDropProvider>, ItemDropResourceManager {

    private static final MobAdditionalDropManager INSTANCE = new MobAdditionalDropManager();
    private static final ResourceLocation IDENTIFIER = ResourceLocation.fromNamespaceAndPath(CollectorsAlbum.MOD_ID, "mob_additional_drops");
    private final Map<ResourceLocation, ItemDropProvider> drops = new LinkedHashMap<>();

    private MobAdditionalDropManager() {
        super("album/mobs", ItemDropProviderType.INSTANCE_CODEC);
    }

    public static MobAdditionalDropManager getInstance() {
        return INSTANCE;
    }

    @Override
    public ResourceLocation identifier() {
        return IDENTIFIER;
    }

    @NotNull
    @Override
    public Iterator<ItemDropProvider> iterator() {
        Collection<ItemDropProvider> values = this.drops.values();
        return values.iterator();
    }

    @Override
    public ItemDropProvider getProvider(ResourceLocation path) {
        return this.drops.getOrDefault(path, NoItemDropProvider.INSTANCE);
    }

    @Override
    protected void preApply(Map<ResourceLocation, JsonElement> resources, ResourceManager manager, ProfilerFiller profiler) {
        this.drops.clear();
    }

    @Override
    protected void resolve(ResourceLocation path, ItemDropProvider element) {
        this.drops.put(path, element);
    }
}
