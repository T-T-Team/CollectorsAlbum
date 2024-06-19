package team.tnt.collectorsalbum.util;

import com.google.gson.JsonElement;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import team.tnt.collectorsalbum.common.resource.MultiloaderJsonReloadListener;

import java.util.Map;
import java.util.Objects;

public final class FabricJsonReloadListenerWrapper<T extends MultiloaderJsonReloadListener> extends MultiloaderJsonReloadListener implements IdentifiableResourceReloadListener {

    private final ResourceLocation identifier;
    private final T data;

    private FabricJsonReloadListenerWrapper(ResourceLocation identifier, T data) {
        super(data.getGson(), data.getDir());
        this.identifier = identifier;
        this.data = data;
    }

    public static <T extends MultiloaderJsonReloadListener> FabricJsonReloadListenerWrapper<T> wrap(ResourceLocation identifier, T reloadListener) {
        return new FabricJsonReloadListenerWrapper<>(
                Objects.requireNonNull(identifier), Objects.requireNonNull(reloadListener)
        );
    }

    @Override
    public ResourceLocation getFabricId() {
        return identifier;
    }

    @Override
    public void loadData(Map<ResourceLocation, JsonElement> resources, ResourceManager manager, ProfilerFiller profiler) {
        this.data.loadData(resources, manager, profiler);
    }
}
