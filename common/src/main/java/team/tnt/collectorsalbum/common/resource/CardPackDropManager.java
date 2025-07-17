package team.tnt.collectorsalbum.common.resource;

import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import team.tnt.collectorsalbum.CollectorsAlbum;
import team.tnt.collectorsalbum.common.resource.drops.ItemDropProvider;
import team.tnt.collectorsalbum.common.resource.drops.ItemDropProviderType;
import team.tnt.collectorsalbum.common.resource.drops.ItemDropResourceManager;
import team.tnt.collectorsalbum.common.resource.drops.NoItemDropProvider;
import team.tnt.collectorsalbum.platform.resource.PlatformGsonCodecReloadListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CardPackDropManager extends PlatformGsonCodecReloadListener<ItemDropProvider> implements ItemDropResourceManager, SynchronizedResource<CardPackDropManager.DropEntry> {

    private static final CardPackDropManager INSTANCE = new CardPackDropManager();
    private static final ResourceLocation IDENTIFIER = ResourceLocation.fromNamespaceAndPath(CollectorsAlbum.MOD_ID, "card_pack_drops");
    private final Map<ResourceLocation, ItemDropProvider> providerMap = new HashMap<>();

    private CardPackDropManager() {
        super("album/packs", ItemDropProviderType.INSTANCE_CODEC);
    }

    public static CardPackDropManager getInstance() {
        return INSTANCE;
    }

    @Override
    public ItemDropProvider getProvider(ResourceLocation id) {
        return this.providerMap.getOrDefault(id, NoItemDropProvider.INSTANCE);
    }

    public ItemDropProvider getEitherProvider(ResourceLocation main, ResourceLocation secondary) {
        return this.providerMap.getOrDefault(main, this.getProvider(secondary));
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

    @Override
    public List<DropEntry> getDataForSync() {
        return this.providerMap.entrySet().stream()
                .map(entry -> new DropEntry(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    @Override
    public synchronized void receiveNetworkData(List<DropEntry> data) {
        this.providerMap.clear();
        data.forEach(entry -> this.providerMap.put(entry.id, entry.provider));
    }

    public record DropEntry(ResourceLocation id, ItemDropProvider provider) {

        public static final Codec<DropEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("id").forGetter(DropEntry::id),
                ItemDropProviderType.INSTANCE_CODEC.fieldOf("provider").forGetter(DropEntry::provider)
        ).apply(instance, DropEntry::new));
    }
}
